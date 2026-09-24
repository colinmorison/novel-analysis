package com.colin.novelanalysis.infrastructure.graph.repository;

import com.colin.novelanalysis.infrastructure.graph.entity.CharacterNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import java.util.List;
import java.util.Optional;

/**
 * 角色节点仓储
 */
public interface CharacterNodeRepository extends Neo4jRepository<CharacterNode, String> {

    Optional<CharacterNode> findByNovelIdAndName(Long novelId, String name);

    List<CharacterNode> findByNovelId(Long novelId);

    @Query("MATCH (c:Character)-[:PARTICIPATES_IN]->(e:Event) " +
            "WHERE c.novelId = $novelId AND c.name = $name " +
            "RETURN e ORDER BY e.chapterNo, e.orderInChapter")
    List<com.colin.novelanalysis.infrastructure.graph.entity.EventNode> findEventsByCharacter(Long novelId, String name);

    @Query("MATCH (c:Character)-[:RELATES_TO]->(t:Character) " +
            "WHERE c.novelId = $novelId AND c.name = $name " +
            "RETURN t.name")
    List<String> findRelatedCharacterNames(Long novelId, String name);
}
