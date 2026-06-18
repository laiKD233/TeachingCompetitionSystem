package com.teaching.competition.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.teaching.competition.common.PageResult;
import com.teaching.competition.dto.WorkDTO;
import com.teaching.competition.entity.Competition;
import com.teaching.competition.entity.Registration;
import com.teaching.competition.entity.ReviewTask;
import com.teaching.competition.entity.TeamMember;
import com.teaching.competition.entity.Work;
import com.teaching.competition.exception.BusinessException;
import com.teaching.competition.mapper.AwardMapper;
import com.teaching.competition.mapper.CompetitionMapper;
import com.teaching.competition.mapper.RegistrationMapper;
import com.teaching.competition.mapper.ReviewTaskMapper;
import com.teaching.competition.mapper.TeamMemberMapper;
import com.teaching.competition.mapper.WorkMapper;
import com.teaching.competition.service.LocalFileService;
import com.teaching.competition.service.CompetitionAdminService;
import com.teaching.competition.service.WorkService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkServiceImpl extends ServiceImpl<WorkMapper, Work> implements WorkService {

    private final LocalFileService localFileService;
    private final RegistrationMapper registrationMapper;
    private final CompetitionMapper competitionMapper;
    private final AwardMapper awardMapper;
    private final ReviewTaskMapper reviewTaskMapper;
    private final TeamMemberMapper teamMemberMapper;
    private final CompetitionAdminService competitionAdminService;

    @Override
    @Transactional
    public void uploadWork(WorkDTO dto, MultipartFile file, Long userId) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("文件不能为空");
        }

        Competition competition = competitionMapper.selectById(dto.getCompetitionId());
        ensureSubmissionOpen(competition);

        Set<Long> teamIds = teamMemberMapper.selectList(
                        new LambdaQueryWrapper<TeamMember>()
                                .eq(TeamMember::getUserId, userId))
                .stream()
                .map(TeamMember::getTeamId)
                .filter(id -> id != null)
                .collect(Collectors.toSet());

        LambdaQueryWrapper<Registration> registrationWrapper = new LambdaQueryWrapper<>();
        registrationWrapper.eq(Registration::getCompetitionId, dto.getCompetitionId())
                .eq(Registration::getStatus, "APPROVED")
                .and(w -> {
                    w.eq(Registration::getUserId, userId);
                    if (!teamIds.isEmpty()) {
                        w.or().in(Registration::getTeamId, teamIds);
                    }
                });
        Registration registration = registrationMapper.selectList(registrationWrapper).stream().findFirst().orElse(null);
        if (registration == null) {
            throw new BusinessException("您未报名该竞赛或报名尚未审核通过");
        }

        Work work = new Work();
        BeanUtils.copyProperties(dto, work);
        work.setUserId(userId);
        work.setRegistrationId(registration.getId());

        String fileUrl = localFileService.upload(file, "works");
        work.setFileUrl(fileUrl);
        work.setFileName(file.getOriginalFilename());
        work.setStatus("SUBMITTED");

        save(work);
    }

    @Override
    @Transactional
    public void updateWork(Long id, WorkDTO dto, MultipartFile file, Long userId) {
        Work work = getById(id);
        if (work == null) {
            throw new BusinessException("作品不存在");
        }
        if (!work.getUserId().equals(userId)) {
            throw new BusinessException("无权限修改该作品");
        }
        ensureWorkMutable(work, "修改");

        BeanUtils.copyProperties(dto, work, "id", "userId", "competitionId", "registrationId",
                "fileUrl", "fileName", "removeFile");

        if (file != null && !file.isEmpty()) {
            deleteWorkFileIfUnused(work.getFileUrl(), work.getId());
            String fileUrl = localFileService.upload(file, "works");
            work.setFileUrl(fileUrl);
            work.setFileName(file.getOriginalFilename());
        } else if (Boolean.TRUE.equals(dto.getRemoveFile())) {
            deleteWorkFileIfUnused(work.getFileUrl(), work.getId());
            // The database columns are NOT NULL, so use blanks to persist "no file".
            work.setFileUrl("");
            work.setFileName("");
        }

        updateById(work);
    }

    @Override
    public PageResult<Work> getMyWorks(Long userId, int page, int size) {
        Page<Work> workPage = new Page<>(page, size);
        Set<Long> teamIds = teamMemberMapper.selectList(
                        new LambdaQueryWrapper<TeamMember>()
                                .eq(TeamMember::getUserId, userId))
                .stream()
                .map(TeamMember::getTeamId)
                .filter(id -> id != null)
                .collect(Collectors.toSet());
        LambdaQueryWrapper<Registration> regWrapper = new LambdaQueryWrapper<>();
        regWrapper.and(w -> {
            w.eq(Registration::getUserId, userId);
            if (!teamIds.isEmpty()) {
                w.or().in(Registration::getTeamId, teamIds);
            }
        });
        Set<Long> visibleRegistrationIds = registrationMapper.selectList(regWrapper).stream()
                .map(Registration::getId)
                .filter(id -> id != null)
                .collect(Collectors.toSet());

        LambdaQueryWrapper<Work> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(w -> {
                    w.eq(Work::getUserId, userId);
                    if (!visibleRegistrationIds.isEmpty()) {
                        w.or().in(Work::getRegistrationId, visibleRegistrationIds);
                    }
                })
                .orderByDesc(Work::getCreatedAt);

        Page<Work> result = page(workPage, wrapper);

        for (Work work : result.getRecords()) {
            if (work.getCompetitionId() != null) {
                Competition competition = competitionMapper.selectById(work.getCompetitionId());
                if (competition != null) {
                    fillCompetitionInfo(work, competition);
                }
            }
        }

        return new PageResult<>(result.getRecords(), result.getTotal(), size, page);
    }

    @Override
    public Work getWorkDetail(Long id, Long userId) {
        Work work = getById(id);
        if (work == null) {
            throw new BusinessException("作品不存在");
        }
        if (work.getCompetitionId() != null) {
            Competition competition = competitionMapper.selectById(work.getCompetitionId());
            if (competition != null) {
                fillCompetitionInfo(work, competition);
            }
        }
        if (work.getUserId().equals(userId)) {
            return work;
        }
        Set<Long> teamIds = teamMemberMapper.selectList(
                        new LambdaQueryWrapper<TeamMember>()
                                .eq(TeamMember::getUserId, userId))
                .stream()
                .map(TeamMember::getTeamId)
                .filter(tid -> tid != null)
                .collect(Collectors.toSet());
        if (work.getRegistrationId() != null && !teamIds.isEmpty()) {
            Registration registration = registrationMapper.selectById(work.getRegistrationId());
            if (registration != null && teamIds.contains(registration.getTeamId())) {
                return work;
            }
        }
        if (!work.getUserId().equals(userId)) {
            throw new BusinessException("无权限查看该作品");
        }
        return work;
    }

    @Override
    @Transactional
    public void deleteWork(Long id, Long userId) {
        Work work = getById(id);
        if (work == null) {
            throw new BusinessException("作品不存在");
        }
        if (!work.getUserId().equals(userId)) {
            throw new BusinessException("无权限删除该作品");
        }
        ensureWorkMutable(work, "删除");
        deleteWorkRelationsAndFile(work);
        removeById(id);
    }

    @Override
    @Transactional
    public void deleteWorkAsAdmin(Long id, Long adminId) {
        Work work = getById(id);
        if (work == null) {
            throw new BusinessException("作品不存在");
        }
        if (!competitionAdminService.hasCompetitionPermission(adminId, work.getCompetitionId())) {
            throw new BusinessException("无权删除该竞赛作品");
        }
        deleteWorkRelationsAndFile(work);
        removeById(id);
    }

    @Override
    public PageResult<Work> getAdminWorks(Long competitionId, int page, int size, Long adminId) {
        if (!competitionAdminService.hasCompetitionPermission(adminId, competitionId)) {
            throw new BusinessException("无权查看该竞赛作品");
        }

        Page<Work> workPage = new Page<>(page, size);

        LambdaQueryWrapper<Work> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Work::getCompetitionId, competitionId)
                .orderByDesc(Work::getCreatedAt);

        Page<Work> result = page(workPage, wrapper);

        for (Work work : result.getRecords()) {
            if (work.getCompetitionId() != null) {
                Competition competition = competitionMapper.selectById(work.getCompetitionId());
                if (competition != null) {
                    fillCompetitionInfo(work, competition);
                }
            }
        }

        return new PageResult<>(result.getRecords(), result.getTotal(), size, page);
    }

    private void deleteWorkFileIfUnused(String fileUrl, Long currentWorkId) {
        if (fileUrl == null || fileUrl.isBlank()) {
            return;
        }
        LambdaQueryWrapper<Work> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Work::getFileUrl, fileUrl);
        if (currentWorkId != null) {
            wrapper.ne(Work::getId, currentWorkId);
        }
        if (count(wrapper) == 0) {
            localFileService.delete(fileUrl);
        }
    }

    private void deleteWorkRelationsAndFile(Work work) {
        deleteWorkFileIfUnused(work.getFileUrl(), work.getId());

        LambdaQueryWrapper<ReviewTask> taskWrapper = new LambdaQueryWrapper<>();
        taskWrapper.eq(ReviewTask::getWorkId, work.getId());
        reviewTaskMapper.delete(taskWrapper);

        LambdaQueryWrapper<com.teaching.competition.entity.Award> awardWrapper = new LambdaQueryWrapper<>();
        awardWrapper.eq(com.teaching.competition.entity.Award::getWorkId, work.getId());
        awardMapper.delete(awardWrapper);
    }

    private void fillCompetitionInfo(Work work, Competition competition) {
        work.setCompetitionName(competition.getName());
        work.setCompetitionStatus(competition.getStatus());
        work.setSubmissionDeadline(competition.getSubmissionDeadline());
    }

    private void ensureWorkMutable(Work work, String action) {
        if (work.getAvgScore() != null) {
            throw new BusinessException("作品已有成绩，不能" + action);
        }
        Long reviewTaskCount = reviewTaskMapper.selectCount(
                new LambdaQueryWrapper<ReviewTask>().eq(ReviewTask::getWorkId, work.getId()));
        if (reviewTaskCount != null && reviewTaskCount > 0) {
            throw new BusinessException("作品已进入评审流程，不能" + action);
        }
        Competition competition = competitionMapper.selectById(work.getCompetitionId());
        ensureSubmissionOpen(competition);
    }

    private void ensureSubmissionOpen(Competition competition) {
        if (competition == null) {
            throw new BusinessException("竞赛不存在");
        }
        String status = competition.getStatus();
        if ("DRAFT".equals(status)) {
            throw new BusinessException("竞赛未发布，不能提交作品");
        }
        if ("REVIEW".equals(status) || "ANNOUNCEMENT".equals(status)
                || "FINISHED".equals(status) || "ENDED".equals(status)) {
            throw new BusinessException("竞赛已进入评审或公示阶段，不能修改作品");
        }
        LocalDateTime deadline = competition.getSubmissionDeadline();
        if (deadline != null && LocalDateTime.now().isAfter(deadline)) {
            throw new BusinessException("作品提交截止时间已结束，不能修改作品");
        }
    }
}
