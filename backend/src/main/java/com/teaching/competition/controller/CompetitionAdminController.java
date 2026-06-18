package com.teaching.competition.controller;

import com.teaching.competition.common.Result;
import com.teaching.competition.dto.CompetitionAdminDTO;
import com.teaching.competition.entity.Competition;
import com.teaching.competition.mapper.CompetitionMapper;
import com.teaching.competition.service.CompetitionAdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/competition-admin")
@RequiredArgsConstructor
@Slf4j
public class CompetitionAdminController {

    private final CompetitionAdminService competitionAdminService;
    private final CompetitionMapper competitionMapper;

    @PostMapping("/assign")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Transactional
    public Result<Void> assignCompetitions(@RequestBody CompetitionAdminDTO dto) {
        log.info("分配竞赛给管理员: adminId={}, competitionIds={}", dto.getAdminId(), dto.getCompetitionIds());
        
        if (dto.getAdminId() == null) {
            return Result.error("管理员ID不能为空");
        }
        
        competitionAdminService.clearAllCompetitions(dto.getAdminId());

        if (dto.getCompetitionIds() != null && !dto.getCompetitionIds().isEmpty()) {
            for (Long competitionId : dto.getCompetitionIds()) {
                competitionAdminService.assignCompetition(dto.getAdminId(), competitionId);
                log.info("已分配竞赛: adminId={}, competitionId={}", dto.getAdminId(), competitionId);
            }
        }

        log.info("竞赛分配完成: adminId={}, 分配了{}个竞赛", dto.getAdminId(), 
                dto.getCompetitionIds() != null ? dto.getCompetitionIds().size() : 0);
        return Result.success();
    }

    @PostMapping("/remove")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Transactional
    public Result<Void> removeCompetitions(@RequestBody CompetitionAdminDTO dto) {
        log.info("移除管理员的竞赛权限: adminId={}, competitionIds={}", dto.getAdminId(), dto.getCompetitionIds());

        if (dto.getCompetitionIds() != null && !dto.getCompetitionIds().isEmpty()) {
            for (Long competitionId : dto.getCompetitionIds()) {
                competitionAdminService.removeCompetition(dto.getAdminId(), competitionId);
            }
        }

        return Result.success();
    }

    @GetMapping("/list/{adminId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public Result<List<Competition>> getAdminCompetitions(@PathVariable Long adminId) {
        List<Long> competitionIds = competitionAdminService.getManagedCompetitionIds(adminId);

        if (competitionIds == null || competitionIds.isEmpty()) {
            return Result.success(List.of());
        }

        List<Competition> competitions = competitionMapper.selectBatchIds(competitionIds);
        return Result.success(competitions);
    }

    @GetMapping("/managed/{adminId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'COMPETITION_ADMIN')")
    public Result<List<Long>> getManagedCompetitions(@PathVariable Long adminId) {
        List<Long> competitionIds = competitionAdminService.getManagedCompetitionIds(adminId);
        return Result.success(competitionIds);
    }

    @GetMapping("/has-all/{adminId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'COMPETITION_ADMIN')")
    public Result<Boolean> hasAllPermission(@PathVariable Long adminId) {
        boolean hasAll = competitionAdminService.hasAllCompetitionPermission(adminId);
        return Result.success(hasAll);
    }
}