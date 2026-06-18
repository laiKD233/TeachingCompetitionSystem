package com.teaching.competition.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 赛道实体类
 */
@Data
@TableName("track")
public class Track {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 竞赛ID
     */
    private Long competitionId;

    /**
     * 赛道名称
     */
    private String name;

    /**
     * 赛道描述
     */
    private String description;

    /**
     * 赛道状态：0-禁用，1-启用
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}