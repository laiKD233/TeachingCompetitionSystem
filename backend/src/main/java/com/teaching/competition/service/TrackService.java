package com.teaching.competition.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.teaching.competition.dto.TrackDTO;
import com.teaching.competition.entity.Track;

import java.util.List;

/**
 * 赛道Service接口
 */
public interface TrackService extends IService<Track> {

    /**
     * 创建赛道
     *
     * @param dto 赛道DTO
     * @return 创建的赛道
     */
    Track createTrack(TrackDTO dto);

    /**
     * 更新赛道
     *
     * @param dto 赛道DTO
     * @return 更新后的赛道
     */
    Track updateTrack(TrackDTO dto);

    /**
     * 根据竞赛ID查询赛道列表
     *
     * @param competitionId 竞赛ID
     * @return 赛道列表
     */
    List<Track> getTracksByCompetitionId(Long competitionId);

    /**
     * 删除赛道
     *
     * @param id 赛道ID
     */
    void deleteTrack(Long id);
}