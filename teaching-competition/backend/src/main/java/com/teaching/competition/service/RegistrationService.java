package com.teaching.competition.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.teaching.competition.common.PageResult;
import com.teaching.competition.dto.RegistrationDTO;
import com.teaching.competition.dto.ReviewRejectDTO;
import com.teaching.competition.entity.Registration;
import com.teaching.competition.vo.RegistrationVO;

public interface RegistrationService extends IService<Registration> {
    void applyRegistration(RegistrationDTO dto, Long userId);
    PageResult<RegistrationVO> getMyRegistrations(Long userId, String status, int page, int size);
    void approveRegistration(Long registrationId, Long adminId);
    void rejectRegistration(ReviewRejectDTO dto, Long adminId);
    PageResult<RegistrationVO> getAdminRegistrations(Long competitionId, String status, String keyword, int page, int size);
}
