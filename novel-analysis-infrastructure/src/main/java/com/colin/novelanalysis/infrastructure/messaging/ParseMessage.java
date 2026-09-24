package com.colin.novelanalysis.infrastructure.messaging;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 小说解析消息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParseMessage implements Serializable {

    private Long novelId;
}
