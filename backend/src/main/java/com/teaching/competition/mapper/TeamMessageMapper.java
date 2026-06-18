package com.teaching.competition.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.teaching.competition.entity.TeamMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface TeamMessageMapper extends BaseMapper<TeamMessage> {

    @Select("SELECT tm.*, u.name as user_name, u.avatar as user_avatar " +
            "FROM team_message tm " +
            "LEFT JOIN sys_user u ON tm.user_id = u.id " +
            "WHERE tm.team_id = #{teamId} AND tm.deleted = 0 " +
            "ORDER BY tm.created_at ASC " +
            "LIMIT #{limit}")
    List<TeamMessage> selectByTeamId(Long teamId, int limit);
}
