package com.colin.novelanalysis.infrastructure.graph.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

/**
 * Neo4j 小说节点
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Node("Novel")
public class NovelNode {

    @Id
    private Long novelId;

    private String title;
}
