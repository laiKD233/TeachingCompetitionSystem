package com.teaching.competition.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.teaching.competition.dto.TodoDTO;
import com.teaching.competition.entity.Todo;
import com.teaching.competition.mapper.TodoMapper;
import com.teaching.competition.service.TodoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TodoServiceImpl extends ServiceImpl<TodoMapper, Todo> implements TodoService {

    @Override
    @Transactional
    public Todo createTodo(TodoDTO dto, Long userId) {
        Todo todo = new Todo();
        todo.setUserId(userId);
        todo.setTitle(dto.getTitle());
        todo.setDescription(dto.getDescription());
        todo.setDueDate(dto.getDueDate());
        todo.setPriority(dto.getPriority() != null ? dto.getPriority() : "MEDIUM");
        todo.setStatus("PENDING");
        todo.setType("PERSONAL");
        save(todo);
        return todo;
    }

    @Override
    @Transactional
    public void updateTodo(Long id, TodoDTO dto, Long userId) {
        Todo todo = getById(id);
        if (todo == null || !todo.getUserId().equals(userId)) {
            throw new RuntimeException("待办不存在或无权限");
        }
        todo.setTitle(dto.getTitle());
        todo.setDescription(dto.getDescription());
        todo.setDueDate(dto.getDueDate());
        todo.setPriority(dto.getPriority());
        updateById(todo);
    }

    @Override
    @Transactional
    public void deleteTodo(Long id, Long userId) {
        Todo todo = getById(id);
        if (todo == null || !todo.getUserId().equals(userId)) {
            throw new RuntimeException("待办不存在或无权限");
        }
        removeById(id);
    }

    @Override
    @Transactional
    public void updateTodoStatus(Long id, String status, Long userId) {
        Todo todo = getById(id);
        if (todo == null || !todo.getUserId().equals(userId)) {
            throw new RuntimeException("待办不存在或无权限");
        }
        todo.setStatus(status);
        updateById(todo);
    }

    @Override
    public List<Todo> getMyTodos(Long userId, String status) {
        LambdaQueryWrapper<Todo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Todo::getUserId, userId);
        if (status != null && !status.isEmpty()) {
            wrapper.eq(Todo::getStatus, status);
        }
        wrapper.orderByDesc(Todo::getCreatedAt);
        return list(wrapper);
    }
}
