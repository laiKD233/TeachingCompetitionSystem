package com.teaching.competition.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.teaching.competition.entity.TeamAdvisorAudit;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface TeamAdvisorAuditMapper extends BaseMapper<TeamAdvisorAudit> {

    @Select("SELECT taa.*, t.name as team_name, u1.name as advisor_name, u2.name as requester_name " +
            "FROM team_advisor_audit taa " +
            "LEFT JOIN team t ON taa.team_id = t.id " +
            "LEFT JOIN sys_user u1 ON taa.advisor_id = u1.id " +
            "LEFT JOIN sys_user u2 ON taa.requester_id = u2.id " +
            "WHERE taa.advisor_id = #{advisorId} AND taa.deleted = 0 " +
            "ORDER BY taa.created_at DESC")
    List<TeamAdvisorAudit> selectByAdvisorId(Long advisorId);

    @Select("SELECT taa.*, t.name as team_name, u1.name as advisor_name, u2.name as requester_name " +
            "FROM team_advisor_audit taa " +
            "LEFT JOIN team t ON taa.team_id = t.id " +
            "LEFT JOIN sys_user u1 ON taa.advisor_id = u1.id " +
            "LEFT JOIN sys_user u2 ON taa.requester_id = u2.id " +
            "WHERE taa.team_id = #{teamId} AND taa.deleted = 0 " +
            "ORDER BY taa.created_at DESC")
    List<TeamAdvisorAudit> selectByTeamId(Long teamId);

    @Select("SELECT taa.*, t.name as team_name, u1.name as advisor_name, u2.name as requester_name " +
            "FROM team_advisor_audit taa " +
            "LEFT JOIN team t ON taa.team_id = t.id " +
            "LEFT JOIN sys_user u1 ON taa.advisor_id = u1.id " +
            "LEFT JOIN sys_user u2 ON taa.requester_id = u2.id " +
            "WHERE taa.requester_id = #{requesterId} AND taa.deleted = 0 " +
            "ORDER BY taa.created_at DESC")
    List<TeamAdvisorAudit> selectByRequesterId(Long requesterId);
}