package com.teaching.competition.controller;

import com.teaching.competition.common.Result;
import com.teaching.competition.dto.StudentDTO;
import com.teaching.competition.dto.TeacherDTO;
import com.teaching.competition.entity.Student;
import com.teaching.competition.entity.Teacher;
import com.teaching.competition.entity.User;
import com.teaching.competition.service.StudentService;
import com.teaching.competition.service.TeacherService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final TeacherService teacherService;
    private final StudentService studentService;

    private User getCurrentUser(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (principal instanceof User) {
            return (User) principal;
        }
        return null;
    }

    @GetMapping("/teacher")
    public Result<Teacher> getTeacherProfile(Authentication authentication) {
        User user = getCurrentUser(authentication);
        if (user == null) {
            return Result.error("用户不存在");
        }
        Teacher teacher = teacherService.getTeacherByUserId(user.getId());
        return Result.success(teacher);
    }

    @PostMapping("/teacher")
    public Result<Teacher> saveTeacherProfile(@RequestBody @Valid TeacherDTO dto, Authentication authentication) {
        User user = getCurrentUser(authentication);
        if (user == null) {
            return Result.error("用户不存在");
        }
        Teacher teacher = teacherService.saveOrUpdateTeacher(user.getId(), dto);
        return Result.success(teacher);
    }

    @GetMapping("/student")
    public Result<Student> getStudentProfile(Authentication authentication) {
        User user = getCurrentUser(authentication);
        if (user == null) {
            return Result.error("用户不存在");
        }
        Student student = studentService.getStudentByUserId(user.getId());
        return Result.success(student);
    }

    @PostMapping("/student")
    public Result<Student> saveStudentProfile(@RequestBody @Valid StudentDTO dto, Authentication authentication) {
        User user = getCurrentUser(authentication);
        if (user == null) {
            return Result.error("用户不存在");
        }
        Student student = studentService.saveOrUpdateStudent(user.getId(), dto);
        return Result.success(student);
    }
}