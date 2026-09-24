package com.colin.novelanalysis.infrastructure.graph.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

/**
 * 角色关系边
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RelationshipProperties
public class CharacterRelation {

    @Id
    private String id;

    private String relationType;

    @TargetNode
    private CharacterNode target;
}
