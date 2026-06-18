package com.teaching.competition.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TeamMessageDTO {

    @NotBlank(message = "消息内容不能为空")
    private String content;
}
