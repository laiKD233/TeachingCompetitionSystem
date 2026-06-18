package com.teaching.competition.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.teaching.competition.entity.Competition;
import com.teaching.competition.mapper.CompetitionMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CompetitionStatusScheduler {

    private final CompetitionMapper competitionMapper;
    private final CompetitionStatusResolver statusResolver;

    @Scheduled(fixedRate = 60000)
    public void autoTransitionStatus() {
        LocalDateTime now = LocalDateTime.now();

        List<Competition> competitions = competitionMapper.selectList(
                new LambdaQueryWrapper<Competition>()
                        .ne(Competition::getStatus, "FINISHED")
        );

        for (Competition comp : competitions) {
            String oldStatus = comp.getStatus();
            String newStatus = statusResolver.resolveStatus(comp, now);

            if (!newStatus.equals(oldStatus)) {
                comp.setStatus(newStatus);
                competitionMapper.updateById(comp);
                log.info("竞赛[{}]状态自动流转: {} -> {}", comp.getName(), oldStatus, newStatus);
            }
        }
    }
}
