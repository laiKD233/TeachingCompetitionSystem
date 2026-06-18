package com.teaching.competition.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.teaching.competition.entity.Team;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface TeamMapper extends BaseMapper<Team> {

    @Select("SELECT t.*, u.name as leader_name, ua.name as advisor_name, c.name as competition_name, " +
            "(SELECT COUNT(*) FROM team_member tm WHERE tm.team_id = t.id AND tm.deleted = 0) as member_count " +
            "FROM team t " +
            "LEFT JOIN sys_user u ON t.leader_id = u.id " +
            "LEFT JOIN sys_user ua ON t.advisor_id = ua.id " +
            "LEFT JOIN competition c ON t.competition_id = c.id " +
            "WHERE t.deleted = 0 AND t.leader_id = #{userId} " +
            "ORDER BY t.created_at DESC")
    List<Team> selectByLeaderId(Long userId);

    @Select("SELECT t.*, u.name as leader_name, ua.name as advisor_name, c.name as competition_name, " +
            "(SELECT COUNT(*) FROM team_member tm WHERE tm.team_id = t.id AND tm.deleted = 0) as member_count " +
            "FROM team t " +
            "LEFT JOIN sys_user u ON t.leader_id = u.id " +
            "LEFT JOIN sys_user ua ON t.advisor_id = ua.id " +
            "LEFT JOIN competition c ON t.competition_id = c.id " +
            "WHERE t.deleted = 0 AND t.advisor_id = #{advisorId} " +
            "ORDER BY t.created_at DESC")
    List<Team> selectByAdvisorId(Long advisorId);

    @Select("SELECT t.*, u.name as leader_name, ua.name as advisor_name, c.name as competition_name, " +
            "(SELECT COUNT(*) FROM team_member tm WHERE tm.team_id = t.id AND tm.deleted = 0) as member_count " +
            "FROM team t " +
            "LEFT JOIN sys_user u ON t.leader_id = u.id " +
            "LEFT JOIN sys_user ua ON t.advisor_id = ua.id " +
            "LEFT JOIN competition c ON t.competition_id = c.id " +
            "WHERE t.deleted = 0 AND t.id IN (SELECT tm.team_id FROM team_member tm WHERE tm.user_id = #{userId} AND tm.deleted = 0) " +
            "ORDER BY t.created_at DESC")
    List<Team> selectByMemberId(Long userId);
}
