package com.colin.novelanalysis.infrastructure.graph.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.neo4j.core.schema.Relationship;

/**
 * Neo4j 事件节点
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Node("Event")
public class EventNode {

    @Id
    private String id;

    @Property
    private Long novelId;

    @Property
    private String title;

    @Property
    private String summary;

    @Property
    private Integer chapterNo;

    @Property
    private Integer orderInChapter;

    @Relationship(type = "IN_NOVEL", direction = Relationship.Direction.OUTGOING)
    private NovelNode novel;
}
