package com.teaching.competition.controller;

import com.teaching.competition.common.Result;
import com.teaching.competition.dto.TodoDTO;
import com.teaching.competition.entity.Todo;
import com.teaching.competition.entity.User;
import com.teaching.competition.service.TodoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/todo")
@RequiredArgsConstructor
public class TodoController {

    private final TodoService todoService;

    private User getCurrentUser(Authentication authentication) {
        return (User) authentication.getPrincipal();
    }

    @PostMapping
    public Result<Todo> createTodo(@RequestBody @Valid TodoDTO dto, Authentication authentication) {
        User user = getCurrentUser(authentication);
        Todo todo = todoService.createTodo(dto, user.getId());
        return Result.success(todo);
    }

    @PutMapping("/{id}")
    public Result<Void> updateTodo(@PathVariable Long id, @RequestBody @Valid TodoDTO dto, Authentication authentication) {
        User user = getCurrentUser(authentication);
        todoService.updateTodo(id, dto, user.getId());
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteTodo(@PathVariable Long id, Authentication authentication) {
        User user = getCurrentUser(authentication);
        todoService.deleteTodo(id, user.getId());
        return Result.success();
    }

    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam String status, Authentication authentication) {
        User user = getCurrentUser(authentication);
        todoService.updateTodoStatus(id, status, user.getId());
        return Result.success();
    }

    @GetMapping("/my")
    public Result<List<Todo>> getMyTodos(@RequestParam(required = false) String status, Authentication authentication) {
        User user = getCurrentUser(authentication);
        List<Todo> todos = todoService.getMyTodos(user.getId(), status);
        return Result.success(todos);
    }
}
