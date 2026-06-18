package com.teaching.competition.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 赛道数据传输对象
 */
@Data
public class TrackDTO {

    /**
     * 赛道ID（更新时使用）
     */
    private Long id;

    /**
     * 竞赛ID
     */
    @NotNull(message = "竞赛ID不能为空")
    private Long competitionId;

    /**
     * 赛道名称
     */
    @NotBlank(message = "赛道名称不能为空")
    private String name;

    /**
     * 赛道描述
     */
    private String description;

    /**
     * 赛道状态：0-禁用，1-启用
     */
    private Integer status;
}