package com.teaching.competition.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.teaching.competition.entity.TeamMember;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface TeamMemberMapper extends BaseMapper<TeamMember> {

    @Select("SELECT tm.*, u.name as user_name, u.avatar as user_avatar " +
            "FROM team_member tm " +
            "LEFT JOIN sys_user u ON tm.user_id = u.id " +
            "WHERE tm.team_id = #{teamId} AND tm.deleted = 0 " +
            "ORDER BY tm.join_time ASC")
    List<TeamMember> selectByTeamId(Long teamId);
}
