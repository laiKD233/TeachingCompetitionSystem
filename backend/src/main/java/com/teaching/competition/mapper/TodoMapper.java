package com.teaching.competition.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.teaching.competition.entity.Todo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TodoMapper extends BaseMapper<Todo> {
}
