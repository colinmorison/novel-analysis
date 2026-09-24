package com.colin.novelanalysis.application.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

/**
 * 角色轨迹查询命令
 */
@Data
@Builder
public class CharacterTraceQueryCommand {

    @NotBlank(message = "会话 ID 不能为空")
    private String sessionId;

    @NotBlank(message = "问题不能为空")
    private String question;
}
