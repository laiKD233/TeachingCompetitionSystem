package com.teaching.competition.controller;

import com.teaching.competition.common.Result;
import com.teaching.competition.dto.TrackDTO;
import com.teaching.competition.entity.Track;
import com.teaching.competition.service.TrackService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 赛道控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/track")
@RequiredArgsConstructor
public class TrackController {

    private final TrackService trackService;

    /**
     * 创建赛道
     */
    @PostMapping
    public Result<Track> create(@Valid @RequestBody TrackDTO dto) {
        log.info("创建赛道: {}", dto.getName());
        Track track = trackService.createTrack(dto);
        return Result.success(track);
    }

    /**
     * 更新赛道
     */
    @PutMapping
    public Result<Track> update(@Valid @RequestBody TrackDTO dto) {
        log.info("更新赛道: {}", dto.getId());
        Track track = trackService.updateTrack(dto);
        return Result.success(track);
    }

    /**
     * 根据竞赛ID查询赛道列表
     */
    @GetMapping("/competition/{competitionId}")
    public Result<List<Track>> getByCompetitionId(@PathVariable Long competitionId) {
        log.info("查询竞赛{}的赛道列表", competitionId);
        List<Track> tracks = trackService.getTracksByCompetitionId(competitionId);
        return Result.success(tracks);
    }

    /**
     * 根据ID查询赛道详情
     */
    @GetMapping("/{id}")
    public Result<Track> getById(@PathVariable Long id) {
        Track track = trackService.getById(id);
        if (track == null) {
            return Result.error("赛道不存在");
        }
        return Result.success(track);
    }

    /**
     * 删除赛道
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        log.info("删除赛道: {}", id);
        trackService.deleteTrack(id);
        return Result.success();
    }
}