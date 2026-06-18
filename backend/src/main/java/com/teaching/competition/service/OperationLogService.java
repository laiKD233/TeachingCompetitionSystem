package com.teaching.competition.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.teaching.competition.common.PageResult;
import com.teaching.competition.entity.OperationLog;

public interface OperationLogService extends IService<OperationLog> {
    void log(Long userId, String username, String role, String module, String operation, String content, String ip, String result);
    PageResult<OperationLog> getLogs(String module, String username, String startDate, String endDate, int page, int size);
}
