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
- langchain4j
- Apache Commons Lang3
- Google Guava
- Lombok + MapStruct
- SLF4J / Logback

## 快速开始

    cd novel-analysis
    mvn clean install
    # 设置环境变量后启动
    cd novel-analysis-starter
    OPENAI_API_KEY=sk-xxx mvn spring-boot:run -Dspring-boot.run.profiles=dev

## 测试接口

    curl -X POST http://localhost:8080/api/v1/chat \
      -H "Content-Type: application/json" \
      -d '{"sessionId":"s001","userMessage":"你好"}'

## 配置说明

在 novel-analysis-starter/src/main/resources/application.properties 中配置 AI Provider API Key。
