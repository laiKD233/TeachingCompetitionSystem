package com.teaching.competition.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.teaching.competition.common.PageResult;
import com.teaching.competition.dto.CompetitionDTO;
import com.teaching.competition.entity.Competition;

public interface CompetitionService extends IService<Competition> {
    PageResult<Competition> getPublicList(String keyword, String status, String type, int page, int size);
    Competition getPublicDetail(Long id);
    PageResult<Competition> getAdminList(String keyword, String status, int page, int size, Long adminId, String role);
    void createCompetition(CompetitionDTO dto, Long userId, String role);
    void updateCompetition(Long id, CompetitionDTO dto, Long adminId, String role);
    void deleteCompetition(Long id, Long adminId, String role);
    Competition getAdminDetail(Long id, Long adminId, String role);
    PageResult<Competition> getFinishedCompetitions(int page, int size, Long adminId, String role);
    java.util.List<Competition> getAllCompetitions();
}
