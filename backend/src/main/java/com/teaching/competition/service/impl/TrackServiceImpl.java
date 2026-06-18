package com.teaching.competition.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.teaching.competition.dto.TrackDTO;
import com.teaching.competition.entity.Track;
import com.teaching.competition.mapper.TrackMapper;
import com.teaching.competition.service.TrackService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 赛道Service实现类
 */
@Service
@RequiredArgsConstructor
public class TrackServiceImpl extends ServiceImpl<TrackMapper, Track> implements TrackService {

    @Override
    @Transactional
    public Track createTrack(TrackDTO dto) {
        Track track = new Track();
        track.setCompetitionId(dto.getCompetitionId());
        track.setName(dto.getName());
        track.setDescription(dto.getDescription());
        track.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);
        track.setCreatedAt(LocalDateTime.now());
        track.setUpdatedAt(LocalDateTime.now());
        
        save(track);
        return track;
    }

    @Override
    @Transactional
    public Track updateTrack(TrackDTO dto) {
        Track track = getById(dto.getId());
        if (track == null) {
            throw new RuntimeException("赛道不存在");
        }
        
        track.setName(dto.getName());
        track.setDescription(dto.getDescription());
        if (dto.getStatus() != null) {
            track.setStatus(dto.getStatus());
        }
        track.setUpdatedAt(LocalDateTime.now());
        
        updateById(track);
        return track;
    }

    @Override
    public List<Track> getTracksByCompetitionId(Long competitionId) {
        LambdaQueryWrapper<Track> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Track::getCompetitionId, competitionId)
               .orderByAsc(Track::getId);
        return list(wrapper);
    }

    @Override
    @Transactional
    public void deleteTrack(Long id) {
        Track track = getById(id);
        if (track == null) {
            throw new RuntimeException("赛道不存在");
        }
        
        // 软删除或硬删除，根据业务需求选择
        removeById(id);
    }
}