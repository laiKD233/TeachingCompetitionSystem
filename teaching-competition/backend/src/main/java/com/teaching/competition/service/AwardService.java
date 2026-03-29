package com.teaching.competition.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.teaching.competition.dto.AwardDTO;
import com.teaching.competition.entity.Award;
import com.teaching.competition.vo.ScoreVO;

import java.util.List;

public interface AwardService extends IService<Award> {
    void publishAwards(AwardDTO dto);
    void publishAnnouncement(Long competitionId);
    List<Award> getAwardsByCompetition(Long competitionId);
    List<ScoreVO> getAwardResults(Long competitionId, Long userId);
    List<Award> getFinishedAwards(Long competitionId);
}
