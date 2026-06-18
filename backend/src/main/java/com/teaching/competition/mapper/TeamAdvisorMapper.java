package com.teaching.competition.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.teaching.competition.entity.TeamAdvisor;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface TeamAdvisorMapper extends BaseMapper<TeamAdvisor> {

    @Select("SELECT ta.*, u.name as advisor_name " +
            "FROM team_advisor ta " +
            "LEFT JOIN sys_user u ON ta.advisor_id = u.id " +
            "WHERE ta.team_id = #{teamId} AND ta.deleted = 0")
    List<TeamAdvisor> selectByTeamId(Long teamId);
}
