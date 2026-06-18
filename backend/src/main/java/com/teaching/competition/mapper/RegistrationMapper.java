package com.teaching.competition.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.teaching.competition.entity.Registration;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RegistrationMapper extends BaseMapper<Registration> {
}
