package com.teaching.competition.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.teaching.competition.entity.TeamTask;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface TeamTaskMapper extends BaseMapper<TeamTask> {

    @Select("SELECT tt.*, u.name as assignee_name, uc.name as creator_name " +
            "FROM team_task tt " +
            "LEFT JOIN sys_user u ON tt.assignee_id = u.id " +
            "LEFT JOIN sys_user uc ON tt.created_by = uc.id " +
            "WHERE tt.team_id = #{teamId} AND tt.deleted = 0 " +
            "ORDER BY tt.created_at DESC")
    List<TeamTask> selectByTeamId(Long teamId);
}
