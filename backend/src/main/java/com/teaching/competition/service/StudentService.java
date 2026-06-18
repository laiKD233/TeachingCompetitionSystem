package com.teaching.competition.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.teaching.competition.dto.StudentDTO;
import com.teaching.competition.entity.Student;

public interface StudentService extends IService<Student> {

    Student getStudentByUserId(Long userId);

    Student saveOrUpdateStudent(Long userId, StudentDTO dto);
}