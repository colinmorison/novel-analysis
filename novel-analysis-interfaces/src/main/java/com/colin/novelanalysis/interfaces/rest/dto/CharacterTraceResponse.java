package com.colin.novelanalysis.interfaces.rest.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 角色轨迹响应
 */
@Data
@Builder
public class CharacterTraceResponse {

    private String sessionId;

    private String characterName;

    private String answer;

    private List<EventItem> events;

    private List<String> relatedCharacters;

    @Data
    @Builder
    public static class EventItem {
        private String title;
        private String summary;
        private Integer chapterNo;
    }
}
