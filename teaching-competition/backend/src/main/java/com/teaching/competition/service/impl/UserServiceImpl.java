package com.teaching.competition.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.teaching.competition.common.PageResult;
import com.teaching.competition.dto.*;
import com.teaching.competition.entity.User;
import com.teaching.competition.exception.BusinessException;
import com.teaching.competition.mapper.UserMapper;
import com.teaching.competition.security.JwtTokenProvider;
import com.teaching.competition.service.UserService;
import com.teaching.competition.utils.RedisUtil;
import com.teaching.competition.vo.LoginVO;
import com.teaching.competition.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisUtil redisUtil;

    @Override
    public LoginVO login(LoginDTO dto) {
        User user = getOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, dto.getUsername()));
        
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        // BCrypt 密码验证
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new BusinessException("密码错误");
        }
        
        if (user.getStatus() == 0) {
            throw new BusinessException("账号已被禁用");
        }
        
        String token = jwtTokenProvider.generateToken(user.getId(), user.getUsername(), user.getRole());

        // 将 Token 存储到 Redis
        redisUtil.setStr("token:" + user.getId(), token, 7, TimeUnit.DAYS);
        redisUtil.set("user:" + user.getId(), user, 7, TimeUnit.DAYS);
        
        LoginVO vo = new LoginVO();
        vo.setToken(token);
        vo.setUserId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setName(user.getName());
        vo.setRole(user.getRole());
        return vo;
    }

    @Override
    @Transactional
    public void register(RegisterDTO dto) {
        long count = count(new LambdaQueryWrapper<User>().eq(User::getUsername, dto.getUsername()));
        if (count > 0) {
            throw new BusinessException("用户名已存在");
        }
        
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword())); // BCrypt 加密
        user.setName(dto.getName());
        user.setStudentId(dto.getStudentId());
        user.setCollege(dto.getCollege());
        user.setPhone(dto.getPhone());
        user.setEmail(dto.getEmail());
        user.setRole("STUDENT");
        user.setStatus(1);
        
        save(user);
    }

    @Override
    public UserVO getCurrentUser(Long userId) {
        User user = getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return toUserVO(user);
    }

    @Override
    @Transactional
    public void updateUserInfo(Long userId, UserUpdateDTO dto) {
        User user = getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        user.setName(dto.getName());
        user.setCollege(dto.getCollege());
        user.setClassName(dto.getClassName());
        user.setPhone(dto.getPhone());
        user.setEmail(dto.getEmail());
        
        updateById(user);
    }

    @Override
    @Transactional
    public void updatePassword(Long userId, PasswordUpdateDTO dto) {
        User user = getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        if (!passwordEncoder.matches(dto.getOldPassword(), user.getPassword())) {
            throw new BusinessException("原密码错误");
        }
        
        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        updateById(user);
        
        // 清除 Redis 中的旧 Token，强制重新登录
        redisUtil.delete("token:" + userId);
        redisUtil.delete("user:" + userId);
    }

    @Override
    public PageResult<UserVO> getUserList(String role, Integer status, String keyword, int page, int size) {
        Page<User> userPage = new Page<>(page, size);
        
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(role)) {
            wrapper.eq(User::getRole, role);
        }
        if (status != null) {
            wrapper.eq(User::getStatus, status);
        }
        if (StrUtil.isNotBlank(keyword)) {
            wrapper.and(w -> w.like(User::getUsername, keyword)
                    .or().like(User::getName, keyword)
                    .or().like(User::getStudentId, keyword));
        }
        wrapper.orderByDesc(User::getCreatedAt);
        
        Page<User> result = page(userPage, wrapper);
        
        List<UserVO> records = result.getRecords().stream()
                .map(this::toUserVO)
                .collect(Collectors.toList());
        
        return new PageResult<>(records, result.getTotal(), size, page);
    }

    @Override
    @Transactional
    public void addUser(UserManageDTO dto) {
        long count = count(new LambdaQueryWrapper<User>().eq(User::getUsername, dto.getUsername()));
        if (count > 0) {
            throw new BusinessException("用户名已存在");
        }
        
        User user = new User();
        BeanUtils.copyProperties(dto, user);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);
        
        save(user);
    }

    @Override
    @Transactional
    public void updateUser(Long id, UserManageDTO dto) {
        User user = getById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        BeanUtils.copyProperties(dto, user, "id", "username", "password", "createdAt");
        updateById(user);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        removeById(id);
    }

    @Override
    @Transactional
    public void freezeUser(Long id) {
        User user = getById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        user.setStatus(0);
        updateById(user);
    }

    @Override
    @Transactional
    public void unfreezeUser(Long id) {
        User user = getById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        user.setStatus(1);
        updateById(user);
    }

    @Override
    @Transactional
    public void resetPassword(Long id) {
        User user = getById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        user.setPassword(passwordEncoder.encode("123456"));
        updateById(user);
    }

    @Override
    @Transactional
    public void updatePermissions(Long id, String permissions) {
        User user = getById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        user.setPermissions(permissions);
        updateById(user);
    }

    private UserVO toUserVO(User user) {
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);
        return vo;
    }
}
