package com.teaching.competition.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.teaching.competition.dto.StudentDTO;
import com.teaching.competition.entity.Student;
import com.teaching.competition.mapper.StudentMapper;
import com.teaching.competition.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl extends ServiceImpl<StudentMapper, Student> implements StudentService {

    @Override
    public Student getStudentByUserId(Long userId) {
        LambdaQueryWrapper<Student> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Student::getUserId, userId);
        return getOne(wrapper);
    }

    @Override
    public Student saveOrUpdateStudent(Long userId, StudentDTO dto) {
        LambdaQueryWrapper<Student> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Student::getUserId, userId);
        Student existing = getOne(wrapper);

        if (existing != null) {
            existing.setStudentNo(dto.getStudentNo());
            existing.setRealName(dto.getRealName());
            existing.setIdCard(dto.getIdCard());
            existing.setEntryYear(dto.getEntryYear());
            existing.setClassName(dto.getClassName());
            existing.setMajor(dto.getMajor());
            existing.setCollegeName(dto.getCollegeName());
            existing.setBio(dto.getBio());
            existing.setIsExternal(dto.getIsExternal());
            existing.setSchool(dto.getSchool());
            updateById(existing);
            return existing;
        } else {
            Student student = new Student();
            student.setUserId(userId);
            student.setStudentNo(dto.getStudentNo());
            student.setRealName(dto.getRealName());
            student.setIdCard(dto.getIdCard());
            student.setEntryYear(dto.getEntryYear());
            student.setClassName(dto.getClassName());
            student.setMajor(dto.getMajor());
            student.setCollegeName(dto.getCollegeName());
            student.setBio(dto.getBio());
            student.setIsExternal(dto.getIsExternal());
            student.setSchool(dto.getSchool());
            save(student);
            return student;
        }
    }
}