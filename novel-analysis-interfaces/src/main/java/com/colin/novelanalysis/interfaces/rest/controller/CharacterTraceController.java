package com.colin.novelanalysis.interfaces.rest.controller;

import com.colin.novelanalysis.application.dto.CharacterTraceQueryCommand;
import com.colin.novelanalysis.application.dto.CharacterTraceResult;
import com.colin.novelanalysis.application.service.CharacterQueryAppService;
import com.colin.novelanalysis.interfaces.rest.dto.ApiResult;
import com.colin.novelanalysis.interfaces.rest.dto.CharacterTraceRequest;
import com.colin.novelanalysis.interfaces.rest.dto.CharacterTraceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

/**
 * 角色轨迹接口
 */
@RestController
@RequestMapping("/api/v1/characters")
@RequiredArgsConstructor
@Validated
public class CharacterTraceController {

    private final CharacterQueryAppService characterQueryAppService;

    @PostMapping("/trace")
    public ApiResult<CharacterTraceResponse> trace(@RequestBody @Validated CharacterTraceRequest request) {
        CharacterTraceResult result = characterQueryAppService.trace(CharacterTraceQueryCommand.builder()
                .sessionId(request.getSessionId())
                .question(request.getQuestion())
                .build());
        return ApiResult.ok(CharacterTraceResponse.builder()
                .sessionId(result.getSessionId())
                .characterName(result.getCharacterName())
                .answer(result.getAnswer())
                .events(result.getEvents().stream()
                        .map(e -> CharacterTraceResponse.EventItem.builder()
                                .title(e.getTitle())
                                .summary(e.getSummary())
                                .chapterNo(e.getChapterNo())
                                .build())
                        .collect(Collectors.toList()))
                .relatedCharacters(result.getRelatedCharacters())
                .build());
    }
}
