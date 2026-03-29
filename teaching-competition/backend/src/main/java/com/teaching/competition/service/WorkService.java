package com.teaching.competition.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.teaching.competition.common.PageResult;
import com.teaching.competition.dto.WorkDTO;
import com.teaching.competition.entity.Work;
import org.springframework.web.multipart.MultipartFile;

public interface WorkService extends IService<Work> {
    void uploadWork(WorkDTO dto, MultipartFile file, Long userId);
    void updateWork(Long id, WorkDTO dto, MultipartFile file, Long userId);
    PageResult<Work> getMyWorks(Long userId, int page, int size);
    Work getWorkDetail(Long id, Long userId);
    void deleteWork(Long id, Long userId);
    PageResult<Work> getAdminWorks(Long competitionId, int page, int size);
}
