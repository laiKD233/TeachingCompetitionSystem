package com.teaching.competition.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.teaching.competition.dto.TodoDTO;
import com.teaching.competition.entity.Todo;

import java.util.List;

public interface TodoService extends IService<Todo> {
    
    Todo createTodo(TodoDTO dto, Long userId);
    
    void updateTodo(Long id, TodoDTO dto, Long userId);
    
    void deleteTodo(Long id, Long userId);
    
    void updateTodoStatus(Long id, String status, Long userId);
    
    List<Todo> getMyTodos(Long userId, String status);
}
