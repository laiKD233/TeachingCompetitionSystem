package com.teaching.competition.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.teaching.competition.dto.TeacherDTO;
import com.teaching.competition.entity.Teacher;

public interface TeacherService extends IService<Teacher> {

    Teacher getTeacherByUserId(Long userId);

    Teacher saveOrUpdateTeacher(Long userId, TeacherDTO dto);
}