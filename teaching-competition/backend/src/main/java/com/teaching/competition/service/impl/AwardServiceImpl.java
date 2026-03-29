package com.teaching.competition.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.teaching.competition.dto.AwardDTO;
import com.teaching.competition.entity.Award;
import com.teaching.competition.entity.Competition;
import com.teaching.competition.entity.Work;
import com.teaching.competition.exception.BusinessException;
import com.teaching.competition.mapper.AwardMapper;
import com.teaching.competition.service.AwardService;
import com.teaching.competition.service.CompetitionService;
import com.teaching.competition.service.WorkService;
import com.teaching.competition.vo.ScoreVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AwardServiceImpl extends ServiceImpl<AwardMapper, Award> implements AwardService {

    private final WorkService workService;
    private final CompetitionService competitionService;

    @Override
    @Transactional
    public void publishAwards(AwardDTO dto) {
        List<AwardDTO.AwardItemDTO> items = dto.getItems();
        if (items == null || items.isEmpty()) {
            throw new BusinessException("获奖信息不能为空");
        }
        
        Long competitionId = dto.getCompetitionId();
        Competition competition = competitionService.getById(competitionId);
        if (competition == null) {
            throw new BusinessException("竞赛不存在");
        }
        
        List<Award> awards = new ArrayList<>();
        for (AwardDTO.AwardItemDTO item : items) {
            Work work = workService.getById(item.getWorkId());
            if (work != null) {
                Award award = new Award();
                award.setCompetitionId(competitionId);
                award.setUserId(work.getUserId());
                award.setWorkId(item.getWorkId());
                award.setAwardLevel(item.getAwardLevel());
                awards.add(award);
            }
        }
        
        saveBatch(awards);
    }

    @Override
    @Transactional
    public void publishAnnouncement(Long competitionId) {
        Competition competition = competitionService.getById(competitionId);
        if (competition == null) {
            throw new BusinessException("竞赛不存在");
        }
        
        competition.setStatus("ANNOUNCED");
        competitionService.updateById(competition);
    }

    @Override
    public List<Award> getAwardsByCompetition(Long competitionId) {
        LambdaQueryWrapper<Award> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Award::getCompetitionId, competitionId);
        return list(wrapper);
    }

    @Override
    public List<ScoreVO> getAwardResults(Long competitionId, Long userId) {
        LambdaQueryWrapper<Work> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Work::getCompetitionId, competitionId)
                .isNotNull(Work::getAvgScore)
                .orderByDesc(Work::getAvgScore);
        
        List<Work> works = workService.list(wrapper);
        
        return works.stream()
                .map(work -> {
                    ScoreVO vo = new ScoreVO();
                    vo.setWorkId(work.getId());
                    vo.setWorkTitle(work.getTitle());
                    vo.setAvgScore(work.getAvgScore());
                    return vo;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<Award> getFinishedAwards(Long competitionId) {
        LambdaQueryWrapper<Award> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Award::getCompetitionId, competitionId);
        return list(wrapper);
    }
}
