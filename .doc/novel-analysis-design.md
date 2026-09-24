# 小说角色轨迹智能分析系统 — 设计文档

> 版本：v1.0  
> 日期：2026-09-21  
> 编写人：AI 工程架构师  
> 状态：待评审（基于当前脚手架与已确认的中间件、需求编写）

---

## 1. 项目概述

### 1.1 目标

构建一套**小说内容解析 + 角色轨迹问答**系统。用户上传小说文件（txt / epub / pdf / docx）后，系统自动解析、构建知识库；用户通过自然语言提问（如“段誉的发展轨迹是什么样的？”），系统基于小说全文给出该角色在书中的成长历程、关键事件与结局概括。

### 1.2 非功能性目标

- **可扩展**：支持多种大模型、多种文件格式、多本小说同时入库。
- **可维护**：沿用 DDD 四层架构，职责清晰。
- **可观测**：关键流程（解析、向量化、问答）均有日志、指标与异常处理。
- **成本可控**：2 核 4G 服务器，连接池、向量维度、召回数量均需做限制。

---

## 2. 需求确认与关键决策

| 决策项 | 用户确认 | 设计应对 |
|--------|---------|---------|
| 文件格式 | txt / epub / pdf / docx | 引入统一 `NovelParser` 策略接口，按 MIME 类型路由 |
| 问答范围 | 全局库，自动匹配小说 | 向量元数据携带 `novelId` + `title`，召回后不强制过滤，由 LLM 自行判断；如需精确限定，可后续增加 `novelId` 参数 |
| 输出方式 | 流式 + 非流式都要 | `ChatController` 提供 `/api/v1/chat`（JSON）与 `/api/v1/chat/stream`（SSE） |
| 大模型 | 本地可用 Ollama 调试；生产模型未定 | 设计模型可插拔层，默认支持 Ollama / OpenAI / DashScope / DeepSeek，通过配置切换 |

### 2.1 待你最终确认

1. **生产环境大模型**：本地用 Ollama，公网部署建议用哪一家？推荐 **DashScope（通义千问）** 或 **DeepSeek**，二者在国内可用性与 LangChain4j 支持度最好。若你确认，我会在实现阶段把默认 starter 改为对应实现。
2. **向量库**：你已有 PostgreSQL，建议启用 `pgvector` 扩展作为向量库（减少中间件）。如果公网 PostgreSQL 不便装扩展，则改为 **Qdrant** 或 **Milvus** 独立部署。请确认是否允许在 PG 上安装 pgvector。
3. **角色信息是否单独构建图**：Neo4j 已部署，建议把“角色 - 事件 - 角色”关系抽取后写入 Neo4j，用于增强角色轨迹问答；如果只用于缓存/会话，则 Neo4j 利用率低。请确认是否启用图增强。
4. **单文件大小上限**：默认按 50 MB 设计；若小说很大（如武侠长篇 5 MB txt），分块 + 异步解析 + Kafka 削峰。

---

## 3. 总体架构

```text
┌─────────────────────────────────────────────────────────────────────────────┐
│                           接口层 (interfaces)                                │
│  NovelUploadController        ChatController (JSON)     ChatController (SSE) │
│       ↑                                                              ↑       │
└───────┬──────────────────────────────────────────────────────────────┬───────┘
        │                                                              │
┌───────▼──────────────────────────────────────────────────────────────▼───────┐
│                         应用层 (application)                                 │
│   NovelUploadAppService         CharacterQueryAppService      ChatAppService │
│        │                              │                              │       │
│        ▼                              ▼                              ▼       │
│  ParseNovelUseCase ─────►  BuildKnowledgeUseCase  ◄──────  AskNovelUseCase   │
└─────────────────────────────────────────────────────────────────────────────┘
        │                              │                              │
┌───────▼──────────────────────────────▼──────────────────────────────▼───────┐
│                          领域层 (domain)                                     │
│  Novel / Chapter / Character / Event / KnowledgeChunk / ChatSession          │
│  AiChatService(可插拔) / NovelParser(可插拔) / EmbeddingService / GraphService │
│  Repository Interfaces                                                        │
└─────────────────────────────────────────────────────────────────────────────┘
        │                              │                              │
┌───────▼──────────────────────────────▼──────────────────────────────▼───────┐
│                       基础设施层 (infrastructure)                            │
│  PostgreSQL(MyBatis)   Neo4j(SDNN)   Redis   MinIO   Kafka   LLM Client │
│  LangChain4jChatClient / DashScopeChatClient / OllamaChatClient ...          │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 3.1 数据流

1. **上传**：文件 → MinIO 原始存储 → Kafka `novel.parse` 任务 → 异步解析。
2. **解析**：按格式提取文本 → 章节切分 → 语义分块 → Embedding → PG 向量表 + 元数据表。
3. **角色抽取**（可选）：LLM 抽取角色/事件/关系 → Neo4j 图库。
4. **问答**：问题 Embedding → 向量召回 Top-K 片段 → 构造 Prompt（含小说上下文）→ LLM 生成 → 返回 JSON/SSE。

---

## 4. 技术栈

| 层级 | 技术 | 说明 |
|------|------|------|
| 基础框架 | Spring Boot 3.3.5 / JDK 17 | 沿用现有脚手架 |
| DDD 分层 | domain / application / infrastructure / interfaces / starter | 沿用现有模块 |
| AI 框架 | LangChain4j 0.35.0 | 统一抽象 ChatLanguageModel / StreamingChatLanguageModel / EmbeddingModel |
| 向量存储 | PostgreSQL + pgvector（推荐）或 Qdrant | 与业务数据同库，降低运维 |
| 图数据库 | Neo4j 5.x + Spring Data Neo4j | 角色关系网络 |
| 关系数据库 | PostgreSQL 14+ | 替换当前 MySQL |
| 缓存/会话 | Redis | Session、解析状态、热点缓存 |
| 对象存储 | MinIO | 原始小说文件 |
| 消息队列 | Kafka | 异步解析任务削峰 |
| 文件解析 | txt 自实现 / epub 用 epublib / pdf 用 PDFBox / docx 用 Apache POI | 统一抽象 |
| 向量模型 | 与 LLM 配套，如 DashScope 的 text-embedding-v3 | 默认 1024 / 1536 维 |

---

## 5. DDD 分层与模块职责

沿用现有五模块结构，新增/调整如下：

### 5.1 domain（领域层）

新增实体与值对象：

- `Novel`：小说聚合根，包含标题、作者、文件地址、解析状态、章节列表。
- `Chapter`：章节，包含序号、标题、起始/结束位置。
- `Character`：角色，包含姓名、别名、出场章节、简介。
- `Event`：事件，时间线、参与角色、地点、摘要。
- `KnowledgeChunk`：知识片段，包含 `novelId`、`chapterId`、`content`、`embeddingVector`（值对象）。
- `ChatSession`：会话聚合根，包含会话 ID、历史消息列表。

新增领域服务接口：

- `AiChatService`：已存在，扩展 `streamChat` 方法。
- `NovelParser`：文件解析策略接口。
- `EmbeddingService`：文本向量化。
- `KnowledgeRepository`：知识片段仓储。
- `NovelRepository`：小说聚合仓储。
- `CharacterGraphService`：角色图服务接口。

### 5.2 application（应用层）

新增应用服务：

- `NovelUploadAppService`：接收上传、生成分布式 ID、写 MinIO、发 Kafka。
- `NovelParseAppService`：消费 Kafka，编排解析、分块、入库、角色抽取。
- `CharacterQueryAppService`：角色轨迹查询编排。
- `ChatAppService`：普通问答与流式问答编排。

新增 DTO：

- `NovelUploadCommand` / `NovelUploadResult`
- `ParseProgressResult`
- `CharacterTraceQueryCommand` / `CharacterTraceResult`
- `ChatCommand`（已存在，增加 `streaming` 标志）

### 5.3 infrastructure（基础设施层）

新增实现：

- `LangChain4jChatClient` 扩展：支持 `StreamingChatLanguageModel`。
- `OllamaChatClient` / `DashScopeChatClient` / `OpenAIChatClient`：实现 `AiChatService`，按 `ai.provider` 配置切换。
- `DefaultNovelParserRouter`：根据 MIME 类型选择 `TxtParser` / `EpubParser` / `PdfParser` / `DocxParser`。
- `PgVectorKnowledgeRepository`：基于 PostgreSQL + pgvector 的仓储实现。
- `Neo4jCharacterGraphService`：基于 Neo4j 的角色图实现。
- `MinioFileStorage`：MinIO 文件存储。
- `KafkaNovelParseProducer` / `Consumer`。

### 5.4 interfaces（接口层）

新增 Controller：

- `NovelUploadController`
- `CharacterTraceController`
- `ChatController` 增加 `/stream` 端点

### 5.5 starter（启动模块）

- 汇总所有模块依赖。
- `application.properties` 改为 PostgreSQL + Redis + Neo4j + Kafka + MinIO 配置。

---

## 6. 核心领域模型

### 6.1 聚合根：Novel

```java
public class Novel extends AggregateRoot<Long> {
    private Long id;
    private String title;              // 小说标题
    private String author;             // 作者
    private String fileName;           // 原始文件名
    private String minioPath;          // MinIO 存储路径
    private Long fileSize;             // 文件大小
    private ParseStatus parseStatus;   // PENDING / PARSING / CHUNKING / COMPLETED / FAILED
    private String parseError;         // 失败原因
    private LocalDateTime uploadedAt;
    private LocalDateTime parsedAt;
}
```

### 6.2 知识片段：KnowledgeChunk

```java
public class KnowledgeChunk extends Entity<Long> {
    private Long id;
    private Long novelId;
    private Integer chapterNo;         // 章节序号，用于排序
    private String chapterTitle;       // 章节标题
    private Integer chunkNo;           // 分块序号
    private String content;            // 原始文本
    private float[] embedding;         // 向量（值对象）
    private Integer tokenCount;        // 预估 token 数
    private LocalDateTime createdAt;
}
```

### 6.3 角色：Character

```java
public class Character extends Entity<Long> {
    private Long id;
    private Long novelId;
    private String name;               // 角色名，如“段誉”
    private List<String> aliases;      // 别名，如“段公子”、“誉儿”
    private String profile;            // 角色简介
    private List<Long> appearChunkIds; // 出场片段 ID
}
```

### 6.4 事件：Event

```java
public class Event extends Entity<Long> {
    private Long id;
    private Long novelId;
    private String title;              // 事件标题
    private String summary;            // 事件摘要
    private List<String> characters;   // 参与角色
    private Integer chapterNo;         // 发生章节
    private Integer orderInChapter;    // 章节内顺序
}
```

---

## 7. 数据存储设计

### 7.1 PostgreSQL 业务表

```sql
-- 小说表
CREATE TABLE novel (
    id              BIGSERIAL PRIMARY KEY,
    title           VARCHAR(255) NOT NULL,
    author          VARCHAR(100),
    file_name       VARCHAR(255) NOT NULL,
    minio_path      VARCHAR(500) NOT NULL,
    file_size       BIGINT,
    parse_status    VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    parse_error     TEXT,
    uploaded_at     TIMESTAMPTZ DEFAULT NOW(),
    parsed_at       TIMESTAMPTZ
);

-- 章节表
CREATE TABLE novel_chapter (
    id              BIGSERIAL PRIMARY KEY,
    novel_id        BIGINT NOT NULL REFERENCES novel(id),
    chapter_no      INT NOT NULL,
    title           VARCHAR(255),
    start_pos       BIGINT,
    end_pos         BIGINT,
    created_at      TIMESTAMPTZ DEFAULT NOW()
);

-- 知识片段表（pgvector）
CREATE TABLE knowledge_chunk (
    id              BIGSERIAL PRIMARY KEY,
    novel_id        BIGINT NOT NULL REFERENCES novel(id),
    chapter_id      BIGINT REFERENCES novel_chapter(id),
    chapter_no      INT,
    chapter_title   VARCHAR(255),
    chunk_no        INT NOT NULL,
    content         TEXT NOT NULL,
    embedding       VECTOR(1024),      -- 维度按实际模型调整
    token_count     INT,
    created_at      TIMESTAMPTZ DEFAULT NOW()
);
CREATE INDEX idx_knowledge_chunk_novel ON knowledge_chunk(novel_id);
CREATE INDEX idx_knowledge_chunk_embedding ON knowledge_chunk USING ivfflat (embedding vector_cosine_ops);

-- 角色表
CREATE TABLE novel_character (
    id              BIGSERIAL PRIMARY KEY,
    novel_id        BIGINT NOT NULL REFERENCES novel(id),
    name            VARCHAR(100) NOT NULL,
    aliases         TEXT,              -- JSON 数组
    profile         TEXT,
    appear_chunk_ids TEXT,             -- JSON 数组
    created_at      TIMESTAMPTZ DEFAULT NOW()
);

-- 事件表
CREATE TABLE novel_event (
    id              BIGSERIAL PRIMARY KEY,
    novel_id        BIGINT NOT NULL REFERENCES novel(id),
    title           VARCHAR(255) NOT NULL,
    summary         TEXT,
    characters      TEXT,              -- JSON 数组
    chapter_no      INT,
    order_in_chapter INT,
    created_at      TIMESTAMPTZ DEFAULT NOW()
);

-- 会话表（可选，Redis 优先）
CREATE TABLE chat_session (
    session_id      VARCHAR(64) PRIMARY KEY,
    novel_id        BIGINT REFERENCES novel(id),
    messages        JSONB,             -- 历史消息
    updated_at      TIMESTAMPTZ DEFAULT NOW()
);
```

### 7.2 Neo4j 图模型（可选，建议启用）

```cypher
(:Novel {novelId, title})
(:Character {name, aliases, profile}) -[:BELONGS_TO]-> (:Novel)
(:Event {title, summary, chapterNo}) -[:IN_NOVEL]-> (:Novel)
(:Character) -[:PARTICIPATES_IN]-> (:Event)
(:Character) -[:RELATES_TO {relationType}]-> (:Character)
```

### 7.3 Redis 数据结构

| 用途 | Key | 类型 | 说明 |
|------|-----|------|------|
| 会话历史 | `chat:session:{sessionId}` | String/Hash | JSON 存储最近 N 轮对话 |
| 解析状态 | `novel:parse:{novelId}` | String | PENDING / PARSING / COMPLETED / FAILED |
| 解析进度 | `novel:parse:progress:{novelId}` | Hash | 总块数、已处理块数、百分比 |
| 热点小说元数据 | `novel:meta:{novelId}` | String | 缓存标题、作者等 |
| 限流 | `rate:chat:{ip}` | String | 滑动窗口限流 |

### 7.4 MinIO 存储

- Bucket: `my-bucket`（沿用你的配置）
- 路径规则：`novels/{novelId}/{originalFilename}`

---

## 8. 小说解析与知识库构建流程

### 8.1 同步上传（接口层）

```
POST /api/v1/novels/upload
Content-Type: multipart/form-data
file: <小说文件>
```

1. 校验文件类型与大小。
2. 生成 `novelId`（雪花或数据库自增）。
3. 上传 MinIO：`novels/{novelId}/{filename}`。
4. 写入 `novel` 表，状态 `PENDING`。
5. 发送 Kafka 消息 `novel.parse`。
6. 返回 `{novelId, title, parseStatus}`。

### 8.2 异步解析（Kafka Consumer）

```
Kafka topic: novel.parse
Consumer group: novel-parse-group
```

1. 下载文件到本地临时目录。
2. `NovelParserRouter` 按 MIME 类型选择解析器，提取纯文本。
3. 章节切分（按“第 X 章”、“Chapter X”等正则）。
4. 语义分块：
   - 按章节 → 段落 → 窗口（默认 500 tokens / 100 tokens overlap）。
   - 块内保留上下文：在 chunk 前加 `[{novelTitle} - {chapterTitle}]`。
5. 批量 Embedding（调用 EmbeddingModel）。
6. 批量写入 `knowledge_chunk`。
7. 更新 `novel.parseStatus = COMPLETED`。

### 8.3 角色/事件抽取（可选增强）

在解析完成后，针对每本小说调用一次 LLM，抽取：

- 角色列表、别名、简介。
- 关键事件、时间线、参与角色。
- 角色之间的关系（师徒、父子、情侣、敌对）。

结果写入 `novel_character`、`novel_event` 和 Neo4j。

---

## 9. 角色轨迹分析设计

### 9.1 两种实现路径

| 路径 | 优点 | 缺点 | 适用 |
|------|------|------|------|
| **A. 纯 RAG 召回 + LLM 生成** | 实现快、通用性强 | 对超长篇小说可能遗漏早期情节 | MVP / 通用问答 |
| **B. RAG + Neo4j 图增强** | 按时间线/关系网组织，角色轨迹更完整 | 需要额外抽取成本 | 角色轨迹专项问答（推荐） |

### 9.2 推荐方案：RAG + 图增强

1. 用户提问：“段誉的发展轨迹是什么样的？”
2. 识别角色名（可用 LLM 做实体识别，或匹配 `novel_character` 表）。
3. 从 Neo4j 查询：
   - 段誉参与的所有 `Event`，按 `chapterNo` 排序。
   - 段誉相关的重要关系（师父、父亲、爱慕对象等）。
4. 向量召回与“段誉”最相关的 Top-K 文本片段。
5. 构造 Prompt：

```text
你是一位熟读《{novelTitle}》的文学分析助手。请根据以下小说原文片段与事件时间线，概括角色“{characterName}”在小说中的发展轨迹及结局。

【事件时间线】
{eventTimeline}

【相关原文片段】
{chunks}

要求：
1. 按时间顺序叙述；
2. 包含重要转折事件；
3. 最后说明角色结局；
4. 若原文未提及，请明确说明“原文未明确交代”。
```

6. LLM 生成回答，返回 JSON 或 SSE。

---

## 10. 问答接口设计

### 10.1 非流式问答

```http
POST /api/v1/chat
Content-Type: application/json

{
  "sessionId": "s001",
  "question": "段誉的发展轨迹是什么样的？"
}
```

响应：

```json
{
  "code": 200,
  "data": {
    "sessionId": "s001",
    "novelId": 1,
    "novelTitle": "天龙八部",
    "answer": "段誉是大理镇南王段正淳之子...",
    "referencedChunks": [
      {"chapterNo": 1, "chapterTitle": "青衫磊落险峰行", "score": 0.92}
    ]
  }
}
```

### 10.2 流式问答

```http
GET /api/v1/chat/stream?sessionId=s001&question=段誉的发展轨迹是什么样的？
Accept: text/event-stream
```

SSE 事件：

```text
event: start
data: {"sessionId":"s001","novelId":1}

event: delta
data: {"chunk":"段誉"}

event: delta
data: {"chunk":"是大理镇南王段正淳之子"}

event: end
data: {"sessionId":"s001","referencedChunks":[...]}
```

### 10.3 角色轨迹专用接口（可选）

```http
POST /api/v1/characters/trace
Content-Type: application/json

{
  "question": "段誉的发展轨迹是什么样的？",
  "sessionId": "s001"
}
```

与普通问答的区别：内部强制走角色图增强逻辑，返回结构化时间线 + 总结。

---

## 11. 模型可插拔设计

### 11.1 领域接口

```java
public interface AiChatService {
    String chat(String sessionId, String userMessage);
    void streamChat(String sessionId, String userMessage, Consumer<String> onChunk);
}

public interface EmbeddingService {
    float[] embed(String text);
    List<float[]> embed(List<String> texts);
}
```

### 11.2 配置模型

```yaml
novel:
  ai:
    provider: ollama   # ollama | openai | dashscope | deepseek
    chat-model: qwen2.5:7b
    embedding-model: nomic-embed-text
    base-url: http://localhost:11434
```

### 11.3 实现类

| Provider | 实现类 | LangChain4j Starter |
|----------|--------|---------------------|
| Ollama | `OllamaChatClient` | `langchain4j-ollama-spring-boot-starter` |
| OpenAI | `OpenAIChatClient` | `langchain4j-open-ai-spring-boot-starter`（已存在） |
| DashScope | `DashScopeChatClient` | `langchain4j-dashscope-spring-boot-starter` |
| DeepSeek | `DeepSeekChatClient`（OpenAI 兼容） | `langchain4j-open-ai-spring-boot-starter` + 改 base-url |

### 11.4 本地调试建议

- Ollama 拉取：`ollama pull qwen2.5:7b` 和 `ollama pull nomic-embed-text`。
- 本地关闭向量库时可用内存版 H2 + pgvector 测试，或直接用 Qdrant 容器。

---

## 12. 接口清单（V1.0）

| Method | Path | 说明 |
|--------|------|------|
| POST | /api/v1/novels/upload | 上传小说文件 |
| GET | /api/v1/novels/{novelId} | 查询小说解析状态 |
| GET | /api/v1/novels/{novelId}/progress | 查询解析进度 |
| POST | /api/v1/chat | 非流式问答 |
| GET | /api/v1/chat/stream | 流式问答（SSE） |
| POST | /api/v1/characters/trace | 角色轨迹问答（结构化） |
| GET | /api/v1/characters | 查询小说角色列表 |

---

## 13. 中间件配置建议

基于你已部署的中间件，建议在 `application.properties` 中这样配置：

```properties
# PostgreSQL
spring.datasource.url=jdbc:postgresql://123.207.187.42:5432/appdb
spring.datasource.username=admin
spring.datasource.password=ynNAjE07IG
spring.datasource.driver-class-name=org.postgresql.Driver
spring.datasource.hikari.maximum-pool-size=5
spring.datasource.hikari.minimum-idle=2

# JPA / MyBatis 配置
spring.jpa.hibernate.ddl-auto=none
mybatis.configuration.log-impl=org.apache.ibatis.logging.slf4j.Slf4jImpl
mybatis.mapper-locations=classpath*:/mapper/**/*.xml

# Neo4j
spring.neo4j.uri=bolt://123.207.187.42:7687
spring.neo4j.authentication.username=neo4j
spring.neo4j.authentication.password=+mhT@lS~Zg

# Redis
spring.data.redis.host=123.207.187.42
spring.data.redis.port=6379
spring.data.redis.password=XZg%Cq&#NT
spring.data.redis.database=0

# Kafka
spring.kafka.bootstrap-servers=123.207.187.42:9092
spring.kafka.producer.acks=all
spring.kafka.consumer.group-id=novel-analysis-group
spring.kafka.consumer.auto-offset-reset=earliest

# MinIO
minio.endpoint=http://123.207.187.42:9000
minio.access-key=admin
minio.secret-key=2x+Wr8q7@p
minio.bucket=my-bucket

# AI（默认 Ollama，生产切换）
novel.ai.provider=ollama
novel.ai.chat-model=qwen2.5:7b
novel.ai.embedding-model=nomic-embed-text
novel.ai.base-url=http://localhost:11434
```

---

## 14. 演进路线

### Phase 1：MVP（核心问答）

- [ ] 替换 MySQL 为 PostgreSQL。
- [ ] 接入 Ollama 本地调试，模型可插拔。
- [ ] 实现 txt / epub / pdf / docx 解析。
- [ ] 实现上传 + Kafka 异步解析 + 向量入库。
- [ ] 实现非流式/流式问答。

### Phase 2：角色增强

- [ ] 角色/事件抽取。
- [ ] Neo4j 图库存储角色关系。
- [ ] 角色轨迹专用接口。

### Phase 3：体验与运维

- [ ] 解析进度 WebSocket / SSE 推送。
- [ ] 多会话历史管理。
- [ ] 回答引用溯源（原文高亮）。
- [ ] 限流、审计、多租户。

---

## 15. 风险与待确认事项

| 风险 | 影响 | 缓解措施 | 待确认 |
|------|------|---------|--------|
| 公网 PostgreSQL 能否安装 pgvector | 向量库选型 | 如不能装，改用 Qdrant 容器 | 需要你确认 |
| 生产模型未定 | 接口实现/成本 | 设计可插拔，本地先用 Ollama | 需要你确认 DashScope / DeepSeek / 其他 |
| 大文件 PDF 可能是扫描版 | 解析失败 | 扫描版提示用户，二期加 OCR | 是否支持扫描版 PDF？ |
| 长篇章回小说分块后早期情节召回不足 | 角色轨迹不完整 | 加章节元数据 + 图增强 | 是否启用 Neo4j 图增强？ |
| 2 核 4G 跑本地大模型吃力 | 性能/OOM | 本地仅调试，生产用远程模型 | 生产是否用远程 API？ |

---

## 16. 下一步建议

请你确认以下 4 点后，我将按本设计文档开始编码：

1. **生产模型**：本地 Ollama 调试没问题，公网部署默认用 **DashScope（通义千问）** 还是 **DeepSeek**？
2. **向量库**：PostgreSQL 是否允许安装 `pgvector` 扩展？
3. **Neo4j 图增强**：是否启用角色/事件抽取并写入 Neo4j？
4. **文件大小上限**：单本小说上限设为多少 MB？（建议 50 MB）
