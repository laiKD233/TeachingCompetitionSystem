package com.teaching.competition.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 竞赛管理员关联实体
 */
@Data
@TableName("competition_admin")
public class CompetitionAdmin {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 竞赛ID
     */
    @TableField("competition_id")
    private Long competitionId;

    /**
     * 管理员ID
     */
    @TableField("admin_id")
    private Long adminId;

    /**
     * 创建时间
     */
    @TableField("created_at")
    private LocalDateTime createdAt;

    /**
     * 删除标志
     */
    @TableLogic
    @TableField("deleted")
    private Integer deleted;

    /**
     * 竞赛名称（非数据库字段，用于查询结果展示）
     */
    @TableField(exist = false)
    private String competitionName;

    /**
     * 管理员名称（非数据库字段，用于查询结果展示）
     */
    @TableField(exist = false)
    private String adminName;
}