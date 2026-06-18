package com.teaching.competition.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.teaching.competition.entity.Track;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 赛道Mapper接口
 */
@Mapper
public interface TrackMapper extends BaseMapper<Track> {

    /**
     * 根据竞赛ID查询赛道列表
     *
     * @param competitionId 竞赛ID
     * @return 赛道列表
     */
    List<Track> selectByCompetitionId(Long competitionId);
}