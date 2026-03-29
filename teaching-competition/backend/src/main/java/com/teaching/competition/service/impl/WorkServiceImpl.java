package com.teaching.competition.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.teaching.competition.common.PageResult;
import com.teaching.competition.dto.WorkDTO;
import com.teaching.competition.entity.Work;
import com.teaching.competition.exception.BusinessException;
import com.teaching.competition.mapper.WorkMapper;
import com.teaching.competition.service.OssService;
import com.teaching.competition.service.WorkService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class WorkServiceImpl extends ServiceImpl<WorkMapper, Work> implements WorkService {

    private final OssService ossService;

    @Override
    @Transactional
    public void uploadWork(WorkDTO dto, MultipartFile file, Long userId) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("文件不能为空");
        }

        Work work = new Work();
        BeanUtils.copyProperties(dto, work);
        work.setUserId(userId);

        String fileUrl = ossService.upload(file, "works");
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

        BeanUtils.copyProperties(dto, work, "id", "userId", "fileUrl", "fileName");

        if (file != null && !file.isEmpty()) {
            // 删除旧文件
            ossService.delete(work.getFileUrl());
            // 上传新文件
            String fileUrl = ossService.upload(file, "works");
            work.setFileUrl(fileUrl);
            work.setFileName(file.getOriginalFilename());
        }

        updateById(work);
    }

    @Override
    public PageResult<Work> getMyWorks(Long userId, int page, int size) {
        Page<Work> workPage = new Page<>(page, size);

        LambdaQueryWrapper<Work> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Work::getUserId, userId)
                .orderByDesc(Work::getCreatedAt);

        Page<Work> result = page(workPage, wrapper);

        return new PageResult<>(result.getRecords(), result.getTotal(), size, page);
    }

    @Override
    public Work getWorkDetail(Long id, Long userId) {
        Work work = getById(id);
        if (work == null) {
            throw new BusinessException("作品不存在");
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
        // 同时删除 OSS 上的文件
        ossService.delete(work.getFileUrl());
        removeById(id);
    }

    @Override
    public PageResult<Work> getAdminWorks(Long competitionId, int page, int size) {
        Page<Work> workPage = new Page<>(page, size);

        LambdaQueryWrapper<Work> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Work::getCompetitionId, competitionId)
                .orderByDesc(Work::getCreatedAt);

        Page<Work> result = page(workPage, wrapper);

        return new PageResult<>(result.getRecords(), result.getTotal(), size, page);
    }
}
