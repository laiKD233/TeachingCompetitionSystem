package com.teaching.competition.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.teaching.competition.common.PageResult;
import com.teaching.competition.entity.OperationLog;
import com.teaching.competition.mapper.OperationLogMapper;
import com.teaching.competition.service.OperationLogService;
import org.springframework.stereotype.Service;

@Service
public class OperationLogServiceImpl extends ServiceImpl<OperationLogMapper, OperationLog> implements OperationLogService {

    @Override
    public void log(Long userId, String username, String role, String module, String operation, String content, String ip, String result) {
        OperationLog log = new OperationLog();
        log.setUserId(userId);
        log.setUsername(username);
        log.setOperation(operation);
        log.setModule(module);
        log.setIp(ip);
        save(log);
    }

    @Override
    public PageResult<OperationLog> getLogs(String module, String username, String startDate, String endDate, int page, int size) {
        Page<OperationLog> logPage = new Page<>(page, size);
        
        LambdaQueryWrapper<OperationLog> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(module)) {
            wrapper.eq(OperationLog::getModule, module);
        }
        if (StrUtil.isNotBlank(username)) {
            wrapper.eq(OperationLog::getUsername, username);
        }
        if (StrUtil.isNotBlank(startDate)) {
            wrapper.ge(OperationLog::getCreatedAt, startDate);
        }
        if (StrUtil.isNotBlank(endDate)) {
            wrapper.le(OperationLog::getCreatedAt, endDate);
        }
        wrapper.orderByDesc(OperationLog::getCreatedAt);
        
        Page<OperationLog> result = page(logPage, wrapper);
        
        return new PageResult<>(result.getRecords(), result.getTotal(), size, page);
    }
}
