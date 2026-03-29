package com.teaching.competition.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.teaching.competition.common.PageResult;
import com.teaching.competition.dto.CompetitionDTO;
import com.teaching.competition.entity.Competition;
import com.teaching.competition.exception.BusinessException;
import com.teaching.competition.mapper.CompetitionMapper;
import com.teaching.competition.service.CompetitionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CompetitionServiceImpl extends ServiceImpl<CompetitionMapper, Competition> implements CompetitionService {

    @Override
    public PageResult<Competition> getPublicList(String keyword, String status, String type, int page, int size) {
        Page<Competition> competitionPage = new Page<>(page, size);
        
        LambdaQueryWrapper<Competition> wrapper = new LambdaQueryWrapper<>();
        wrapper.ne(Competition::getStatus, "DRAFT");
        
        if (StrUtil.isNotBlank(type)) {
            wrapper.eq(Competition::getType, type);
        }
        if (StrUtil.isNotBlank(status)) {
            wrapper.eq(Competition::getStatus, status);
        }
        if (StrUtil.isNotBlank(keyword)) {
            wrapper.and(w -> w.like(Competition::getName, keyword)
                    .or().like(Competition::getTheme, keyword));
        }
        wrapper.orderByDesc(Competition::getCreatedAt);
        
        Page<Competition> result = page(competitionPage, wrapper);
        
        return new PageResult<>(result.getRecords(), result.getTotal(), size, page);
    }

    @Override
    public Competition getPublicDetail(Long id) {
        Competition competition = getById(id);
        if (competition == null) {
            throw new BusinessException("竞赛不存在");
        }
        return competition;
    }

    @Override
    public PageResult<Competition> getAdminList(String keyword, String status, int page, int size) {
        Page<Competition> competitionPage = new Page<>(page, size);
        
        LambdaQueryWrapper<Competition> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(status)) {
            wrapper.eq(Competition::getStatus, status);
        }
        if (StrUtil.isNotBlank(keyword)) {
            wrapper.and(w -> w.like(Competition::getName, keyword)
                    .or().like(Competition::getTheme, keyword));
        }
        wrapper.orderByDesc(Competition::getCreatedAt);
        
        Page<Competition> result = page(competitionPage, wrapper);
        
        return new PageResult<>(result.getRecords(), result.getTotal(), size, page);
    }

    @Override
    public Competition getAdminDetail(Long id) {
        Competition competition = getById(id);
        if (competition == null) {
            throw new BusinessException("竞赛不存在");
        }
        return competition;
    }

    @Override
    public PageResult<Competition> getFinishedCompetitions(int page, int size) {
        Page<Competition> competitionPage = new Page<>(page, size);
        
        LambdaQueryWrapper<Competition> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Competition::getStatus, "ENDED")
                .orderByDesc(Competition::getAnnouncementEnd);
        
        Page<Competition> result = page(competitionPage, wrapper);
        
        return new PageResult<>(result.getRecords(), result.getTotal(), size, page);
    }

    @Override
    @Transactional
    public void createCompetition(CompetitionDTO dto, Long userId) {
        Competition competition = new Competition();
        BeanUtils.copyProperties(dto, competition);
        competition.setCreatedBy(userId);
        competition.setStatus("DRAFT");
        save(competition);
    }

    @Override
    @Transactional
    public void updateCompetition(Long id, CompetitionDTO dto) {
        Competition competition = getById(id);
        if (competition == null) {
            throw new BusinessException("竞赛不存在");
        }
        
        BeanUtils.copyProperties(dto, competition, "id", "createdBy", "createdAt");
        updateById(competition);
    }

    @Override
    @Transactional
    public void deleteCompetition(Long id) {
        removeById(id);
    }
}
