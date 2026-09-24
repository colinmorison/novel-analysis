package com.colin.novelanalysis.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;

/**
 * Neo4j 配置
 */
@Configuration
@EnableNeo4jRepositories(basePackages = "com.colin.novelanalysis.infrastructure.graph.repository")
public class Neo4jConfig {
}
