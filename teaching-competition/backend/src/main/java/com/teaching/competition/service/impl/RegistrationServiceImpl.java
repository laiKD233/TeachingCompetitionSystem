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
        // 检查是否重复报名
        LambdaQueryWrapper<Registration> checkWrapper = new LambdaQueryWrapper<>();
        checkWrapper.eq(Registration::getUserId, userId)
                .eq(Registration::getCompetitionId, dto.getCompetitionId());
        if (count(checkWrapper) > 0) {
            throw new BusinessException("您已报名该竞赛，请勿重复报名");
        }

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
        // 搜索支持：项目名称、描述（ Registration 表字段）
        // 姓名/学号需要在 enrich 后过滤，因为 Registration 表没有这些字段
        wrapper.orderByDesc(Registration::getCreatedAt);

        Page<Registration> result = page(registrationPage, wrapper);
        List<RegistrationVO> voList = enrichWithDetails(result.getRecords());

        // 如果有关键词且包含姓名/学号搜索，在已关联用户信息的结果中进行二次过滤
        if (StrUtil.isNotBlank(keyword)) {
            String lowerKeyword = keyword.toLowerCase();
            voList = voList.stream()
                    .filter(vo -> {
                        // Registration 表字段匹配
                        if (StrUtil.isNotBlank(vo.getProjectName()) && vo.getProjectName().toLowerCase().contains(lowerKeyword)) return true;
                        if (StrUtil.isNotBlank(vo.getDescription()) && vo.getDescription().toLowerCase().contains(lowerKeyword)) return true;
                        // 用户信息字段匹配
                        if (StrUtil.isNotBlank(vo.getParticipantName()) && vo.getParticipantName().toLowerCase().contains(lowerKeyword)) return true;
                        if (StrUtil.isNotBlank(vo.getStudentId()) && vo.getStudentId().toLowerCase().contains(lowerKeyword)) return true;
                        return false;
                    })
                    .collect(Collectors.toList());
        }

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
