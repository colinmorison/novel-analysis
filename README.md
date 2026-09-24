# novel-analysis

企业级 Spring Boot 多模块脚手架，AI 框架使用 **langchain4j**。

## 模块说明

| 模块 | 职责 |
|------|------|
| novel-analysis-domain | 领域层：实体、值对象、领域服务、仓储接口、领域事件 |
| novel-analysis-application | 应用层：用例编排、应用服务、DTO、转换器 |
| novel-analysis-infrastructure | 基础设施层：配置、持久化、外部客户端、仓储实现 |
| novel-analysis-interfaces | 接口层：REST API、DTO、异常处理 |
| novel-analysis-starter | 启动模块：Spring Boot Application、配置、测试 |

## 技术栈

- Spring Boot 3.3.5 / JDK 17
- Spring Web + Validation + Actuator
- LangChain4j 0.35.0
- PostgreSQL + pgvector（向量存储）
- Neo4j（角色关系图）
- Redis（会话缓存）
- Kafka（异步解析任务）
- MinIO（文件存储）
- Ollama（本地大模型调试）
- Apache Commons Lang3 / IO
- Google Guava
- Lombok + MapStruct
- SLF4J / Logback

## 快速开始

### 1. 本地准备中间件

推荐用 Docker Compose 一键启动：

```bash
docker-compose up -d postgres neo4j redis kafka minio ollama
```

或直接在 `application.properties` 中把地址改为你已部署的中间件。

### 2. 拉取 Ollama 模型

```bash
ollama pull qwen2.5:7b
ollama pull nomic-embed-text
```

### 3. 配置敏感信息（不要提交到 Git）

项目已移除所有真实中间件密码和服务器地址，`application.properties` 中只保留本地开发默认值。

本地真实配置放在 `application-local.properties` 中，该文件已加入 `.gitignore`，不会提交：

```bash
cp novel-analysis-starter/src/main/resources/application-local.properties.example \
   novel-analysis-starter/src/main/resources/application-local.properties
# 编辑 application-local.properties 填入真实值
```

### 4. 编译启动

```bash
cd novel-analysis
mvn clean install -DskipTests
cd novel-analysis-starter
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

> 其他环境（测试/生产）部署时，只需在服务器上放置对应的 `application-{profile}.properties`，启动时指定 `-Dspring.profiles.active={profile}` 即可。

## 接口说明

### 上传小说

```bash
curl -X POST http://localhost:8080/api/v1/novels/upload \
  -F "file=@/path/to/天龙八部.txt" \
  -F 'request={"title":"天龙八部","author":"金庸"};type=application/json'
```

### 角色轨迹问答（JSON）

```bash
curl -X POST http://localhost:8080/api/v1/characters/trace \
  -H "Content-Type: application/json" \
  -d '{"sessionId":"s001","question":"段誉的发展轨迹是什么样的？"}'
```

### 通用问答（JSON）

```bash
curl -X POST http://localhost:8080/api/v1/chat \
  -H "Content-Type: application/json" \
  -d '{"sessionId":"s001","userMessage":"段誉的结局是什么？"}'
```

### 通用问答（SSE 流式）

```bash
curl -N 'http://localhost:8080/api/v1/chat/stream?sessionId=s001&question=段誉的结局是什么？'
```

## 配置说明

在 `novel-analysis-starter/src/main/resources/application.properties` 中配置中间件地址与 AI 模型。默认使用 Ollama 本地模型，可切换为 OpenAI / DashScope / DeepSeek。
