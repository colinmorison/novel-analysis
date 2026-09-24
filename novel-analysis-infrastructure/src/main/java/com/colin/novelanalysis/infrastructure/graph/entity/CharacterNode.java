package com.colin.novelanalysis.infrastructure.graph.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.ArrayList;
import java.util.List;

/**
 * Neo4j 角色节点
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Node("Character")
public class CharacterNode {

    @Id
    private String id;

    @Property
    private Long novelId;

    @Property
    private String name;

    @Property
    private List<String> aliases;

    @Property
    private String profile;

    @Relationship(type = "BELONGS_TO", direction = Relationship.Direction.OUTGOING)
    private NovelNode novel;

    @Relationship(type = "PARTICIPATES_IN", direction = Relationship.Direction.OUTGOING)
    private List<EventNode> events = new ArrayList<>();

    @Relationship(type = "RELATES_TO", direction = Relationship.Direction.OUTGOING)
    private List<CharacterRelation> relations = new ArrayList<>();
}
