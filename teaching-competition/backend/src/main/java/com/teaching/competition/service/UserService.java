package com.teaching.competition.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.teaching.competition.common.PageResult;
import com.teaching.competition.dto.*;
import com.teaching.competition.entity.User;
import com.teaching.competition.vo.LoginVO;
import com.teaching.competition.vo.UserVO;

public interface UserService extends IService<User> {
    LoginVO login(LoginDTO dto);
    void register(RegisterDTO dto);
    UserVO getCurrentUser(Long userId);
    void updateUserInfo(Long userId, UserUpdateDTO dto);
    void updatePassword(Long userId, PasswordUpdateDTO dto);
    PageResult<UserVO> getUserList(String role, Integer status, String keyword, int page, int size);
    void addUser(UserManageDTO dto);
    void updateUser(Long id, UserManageDTO dto);
    void deleteUser(Long id);
    void freezeUser(Long id);
    void unfreezeUser(Long id);
    void resetPassword(Long id);
    void updatePermissions(Long id, String permissions);
}
