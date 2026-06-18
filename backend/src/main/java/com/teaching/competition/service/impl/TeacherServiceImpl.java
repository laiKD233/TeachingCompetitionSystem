package com.teaching.competition.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.teaching.competition.dto.TeacherDTO;
import com.teaching.competition.entity.Teacher;
import com.teaching.competition.mapper.TeacherMapper;
import com.teaching.competition.service.TeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TeacherServiceImpl extends ServiceImpl<TeacherMapper, Teacher> implements TeacherService {

    @Override
    public Teacher getTeacherByUserId(Long userId) {
        LambdaQueryWrapper<Teacher> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Teacher::getUserId, userId);
        return getOne(wrapper);
    }

    @Override
    public Teacher saveOrUpdateTeacher(Long userId, TeacherDTO dto) {
        LambdaQueryWrapper<Teacher> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Teacher::getUserId, userId);
        Teacher existing = getOne(wrapper);

        if (existing != null) {
            existing.setTeacherNo(dto.getTeacherNo());
            existing.setRealName(dto.getRealName());
            existing.setIdCard(dto.getIdCard());
            existing.setEducation(dto.getEducation());
            existing.setDegree(dto.getDegree());
            existing.setBio(dto.getBio());
            existing.setMajor(dto.getMajor());
            existing.setIsExternal(dto.getIsExternal());
            existing.setCollegeName(dto.getCollegeName());
            existing.setSchool(dto.getSchool());
            updateById(existing);
            return existing;
        } else {
            Teacher teacher = new Teacher();
            teacher.setUserId(userId);
            teacher.setTeacherNo(dto.getTeacherNo());
            teacher.setRealName(dto.getRealName());
            teacher.setIdCard(dto.getIdCard());
            teacher.setEducation(dto.getEducation());
            teacher.setDegree(dto.getDegree());
            teacher.setBio(dto.getBio());
            teacher.setMajor(dto.getMajor());
            teacher.setIsExternal(dto.getIsExternal());
            teacher.setCollegeName(dto.getCollegeName());
            teacher.setSchool(dto.getSchool());
            save(teacher);
            return teacher;
        }
    }
}