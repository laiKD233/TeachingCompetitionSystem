package com.teaching.competition.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.teaching.competition.entity.CompetitionAdmin;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CompetitionAdminMapper extends BaseMapper<CompetitionAdmin> {

    @Select("SELECT competition_id FROM competition_admin WHERE admin_id = #{adminId} AND deleted = 0")
    List<Long> selectCompetitionIdsByAdminId(@Param("adminId") Long adminId);

    @Select("SELECT * FROM competition_admin WHERE competition_id = #{competitionId} AND deleted = 0")
    List<CompetitionAdmin> selectByCompetitionId(@Param("competitionId") Long competitionId);

    @Select("SELECT COUNT(*) FROM competition_admin WHERE admin_id = #{adminId} AND competition_id = #{competitionId} AND deleted = 0")
    Integer checkAdminCompetition(@Param("adminId") Long adminId, @Param("competitionId") Long competitionId);

    @Select("SELECT COUNT(*) FROM competition_admin WHERE admin_id = #{adminId} AND deleted = 0")
    Integer countByAdminId(@Param("adminId") Long adminId);
}