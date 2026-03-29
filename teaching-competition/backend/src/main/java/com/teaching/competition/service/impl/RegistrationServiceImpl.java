package com.teaching.competition.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.teaching.competition.common.PageResult;
import com.teaching.competition.dto.RegistrationDTO;
import com.teaching.competition.dto.ReviewRejectDTO;
import com.teaching.competition.entity.Competition;
import com.teaching.competition.entity.Registration;
import com.teaching.competition.entity.User;
import com.teaching.competition.exception.BusinessException;
import com.teaching.competition.mapper.RegistrationMapper;
import com.teaching.competition.service.CompetitionService;
import com.teaching.competition.service.RegistrationService;
import com.teaching.competition.service.UserService;
import com.teaching.competition.vo.RegistrationVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RegistrationServiceImpl extends ServiceImpl<RegistrationMapper, Registration> implements RegistrationService {

    private final CompetitionService competitionService;
    private final UserService userService;

    @Override
    @Transactional
    public void applyRegistration(RegistrationDTO dto, Long userId) {
        Registration registration = new Registration();
        BeanUtils.copyProperties(dto, registration);
        registration.setUserId(userId);
        registration.setStatus("PENDING");
        save(registration);
    }

    @Override
    public PageResult<RegistrationVO> getMyRegistrations(Long userId, String status, int page, int size) {
        Page<Registration> registrationPage = new Page<>(page, size);

        LambdaQueryWrapper<Registration> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Registration::getUserId, userId);
        if (StrUtil.isNotBlank(status)) {
            wrapper.eq(Registration::getStatus, status);
        }
        wrapper.orderByDesc(Registration::getCreatedAt);

        Page<Registration> result = page(registrationPage, wrapper);
        List<RegistrationVO> voList = enrichWithCompetitionName(result.getRecords());

        return new PageResult<>(voList, result.getTotal(), size, page);
    }

    @Override
    @Transactional
    public void approveRegistration(Long registrationId, Long adminId) {
        Registration registration = getById(registrationId);
        if (registration == null) {
            throw new BusinessException("报名记录不存在");
        }

        registration.setStatus("APPROVED");
        registration.setReviewedBy(adminId);
        registration.setReviewedAt(LocalDateTime.now());
        updateById(registration);
    }

    @Override
    @Transactional
    public void rejectRegistration(ReviewRejectDTO dto, Long adminId) {
        Registration registration = getById(dto.getRegistrationId());
        if (registration == null) {
            throw new BusinessException("报名记录不存在");
        }

        registration.setStatus("REJECTED");
        registration.setRejectReason(dto.getRejectReason());
        registration.setReviewedBy(adminId);
        registration.setReviewedAt(LocalDateTime.now());
        updateById(registration);
    }

    @Override
    public PageResult<RegistrationVO> getAdminRegistrations(Long competitionId, String status, String keyword, int page, int size) {
        Page<Registration> registrationPage = new Page<>(page, size);

        LambdaQueryWrapper<Registration> wrapper = new LambdaQueryWrapper<>();
        if (competitionId != null) {
            wrapper.eq(Registration::getCompetitionId, competitionId);
        }

        if (StrUtil.isNotBlank(status)) {
            wrapper.eq(Registration::getStatus, status);
        }
        if (StrUtil.isNotBlank(keyword)) {
            wrapper.and(w -> w.like(Registration::getProjectName, keyword)
                    .or().like(Registration::getDescription, keyword));
        }
        wrapper.orderByDesc(Registration::getCreatedAt);

        Page<Registration> result = page(registrationPage, wrapper);
        List<RegistrationVO> voList = enrichWithDetails(result.getRecords());

        return new PageResult<>(voList, result.getTotal(), size, page);
    }

    private List<RegistrationVO> enrichWithCompetitionName(List<Registration> registrations) {
        Set<Long> competitionIds = registrations.stream()
                .map(Registration::getCompetitionId)
                .filter(id -> id != null)
                .collect(Collectors.toSet());

        Map<Long, String> competitionNameMap = competitionIds.isEmpty() ? Map.of() :
                competitionService.listByIds(competitionIds).stream()
                        .collect(Collectors.toMap(Competition::getId, Competition::getName));

        return registrations.stream().map(reg -> {
            RegistrationVO vo = new RegistrationVO();
            BeanUtils.copyProperties(reg, vo);
            vo.setCompetitionName(competitionNameMap.get(reg.getCompetitionId()));
            return vo;
        }).collect(Collectors.toList());
    }

    private List<RegistrationVO> enrichWithDetails(List<Registration> registrations) {
        Set<Long> competitionIds = registrations.stream()
                .map(Registration::getCompetitionId)
                .filter(id -> id != null)
                .collect(Collectors.toSet());

        Set<Long> userIds = registrations.stream()
                .map(Registration::getUserId)
                .filter(id -> id != null)
                .collect(Collectors.toSet());

        Map<Long, String> competitionNameMap = competitionIds.isEmpty() ? Map.of() :
                competitionService.listByIds(competitionIds).stream()
                        .collect(Collectors.toMap(Competition::getId, Competition::getName));

        Map<Long, User> userMap = userIds.isEmpty() ? Map.of() :
                userService.listByIds(userIds).stream()
                        .collect(Collectors.toMap(User::getId, u -> u));

        return registrations.stream().map(reg -> {
            RegistrationVO vo = new RegistrationVO();
            BeanUtils.copyProperties(reg, vo);
            vo.setCompetitionName(competitionNameMap.get(reg.getCompetitionId()));
            User user = userMap.get(reg.getUserId());
            if (user != null) {
                vo.setParticipantName(user.getName());
                vo.setStudentId(user.getStudentId());
                vo.setCollege(user.getCollege());
            }
            return vo;
        }).collect(Collectors.toList());
    }
}
