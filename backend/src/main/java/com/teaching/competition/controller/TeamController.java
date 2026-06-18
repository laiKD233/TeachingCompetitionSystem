package com.teaching.competition.controller;

import com.teaching.competition.common.PageResult;
import com.teaching.competition.common.Result;
import com.teaching.competition.dto.TeamDTO;
import com.teaching.competition.dto.TeamMessageDTO;
import com.teaching.competition.dto.TeamTaskDTO;
import com.teaching.competition.entity.Team;
import com.teaching.competition.entity.TeamMember;
import com.teaching.competition.entity.TeamMessage;
import com.teaching.competition.entity.TeamTask;
import com.teaching.competition.entity.User;
import com.teaching.competition.service.TeamService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/team")
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;

    private User getCurrentUser(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (principal instanceof User) {
            return (User) principal;
        }
        return null;
    }

    // ========== 团队管理 ==========

    @PostMapping
    public Result<Team> createTeam(@RequestBody @Valid TeamDTO dto, Authentication authentication) {
        User user = getCurrentUser(authentication);
        if (user == null) {
            return Result.error("用户不存在");
        }
        Team team = teamService.createTeam(dto, user.getId());
        return Result.success(team);
    }

    @PutMapping("/{id}")
    public Result<Void> updateTeam(@PathVariable Long id, @RequestBody @Valid TeamDTO dto, Authentication authentication) {
        User user = getCurrentUser(authentication);
        if (user == null) {
            return Result.error("用户不存在");
        }
        teamService.updateTeam(id, dto, user.getId());
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteTeam(@PathVariable Long id, Authentication authentication) {
        User user = getCurrentUser(authentication);
        if (user == null) {
            return Result.error("用户不存在");
        }
        teamService.deleteTeam(id, user.getId());
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<Team> getTeamDetail(@PathVariable Long id, Authentication authentication) {
        User user = getCurrentUser(authentication);
        if (user == null) {
            return Result.error("用户不存在");
        }
        Team team = teamService.getTeamDetail(id, user.getId());
        return Result.success(team);
    }

    @GetMapping("/my")
    public Result<List<Team>> getMyTeams(Authentication authentication) {
        User user = getCurrentUser(authentication);
        if (user == null) {
            return Result.error("用户不存在");
        }
        if ("ADMIN".equals(user.getRole())) {
            PageResult<Team> allTeams = teamService.getAllTeams(1, 1000);
            return Result.success(allTeams.getRecords());
        }
        List<Team> teams = teamService.getMyTeams(user.getId());
        if ("TEACHER".equals(user.getRole()) || "ADVISOR".equals(user.getRole())) {
            List<Team> advisedTeams = teamService.getAdvisedTeams(user.getId());
            Set<Long> seenIds = new LinkedHashSet<>();
            List<Team> mergedTeams = new ArrayList<>();
            for (Team team : teams) {
                if (seenIds.add(team.getId())) {
                    mergedTeams.add(team);
                }
            }
            for (Team team : advisedTeams) {
                if (seenIds.add(team.getId())) {
                    mergedTeams.add(team);
                }
            }
            return Result.success(mergedTeams);
        }
        return Result.success(teams);
    }

    @GetMapping("/advised")
    public Result<List<Team>> getAdvisedTeams(Authentication authentication) {
        User user = getCurrentUser(authentication);
        if (user == null) {
            return Result.error("用户不存在");
        }
        List<Team> teams = teamService.getAdvisedTeams(user.getId());
        return Result.success(teams);
    }

    @GetMapping("/list")
    public Result<PageResult<Team>> getAllTeams(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResult<Team> result = teamService.getAllTeams(page, size);
        return Result.success(result);
    }

    @GetMapping("/advisors")
    public Result<List<?>> getAvailableAdvisors() {
        List<?> advisors = teamService.getAvailableAdvisors();
        return Result.success(advisors);
    }

    // ========== 成员管理 ==========

    @PostMapping("/{id}/join")
    public Result<Void> joinTeam(@PathVariable Long id, @RequestParam String inviteCode, Authentication authentication) {
        User user = getCurrentUser(authentication);
        if (user == null) {
            return Result.error("用户不存在");
        }
        teamService.joinTeam(id, inviteCode, user.getId());
        return Result.success();
    }

    @PostMapping("/{id}/leave")
    public Result<Void> leaveTeam(@PathVariable Long id, Authentication authentication) {
        User user = getCurrentUser(authentication);
        if (user == null) {
            return Result.error("用户不存在");
        }
        teamService.leaveTeam(id, user.getId());
        return Result.success();
    }

    @DeleteMapping("/{teamId}/member/{memberId}")
    public Result<Void> removeMember(@PathVariable Long teamId, @PathVariable Long memberId, Authentication authentication) {
        User user = getCurrentUser(authentication);
        if (user == null) {
            return Result.error("用户不存在");
        }
        teamService.removeMember(teamId, memberId, user.getId());
        return Result.success();
    }

    @GetMapping("/{id}/members")
    public Result<List<TeamMember>> getTeamMembers(@PathVariable Long id, Authentication authentication) {
        User user = getCurrentUser(authentication);
        if (user == null) {
            return Result.error("用户不存在");
        }
        List<TeamMember> members = teamService.getTeamMembers(id, user.getId());
        return Result.success(members);
    }

    @GetMapping("/{id}/invite-code")
    public Result<String> getInviteCode(@PathVariable Long id, Authentication authentication) {
        User user = getCurrentUser(authentication);
        if (user == null) {
            return Result.error("用户不存在");
        }
        String code = teamService.generateInviteCode(id, user.getId());
        return Result.success(code);
    }

    // ========== 任务管理 ==========

    @PostMapping("/{id}/tasks")
    public Result<TeamTask> createTask(@PathVariable Long id, @RequestBody @Valid TeamTaskDTO dto, Authentication authentication) {
        User user = getCurrentUser(authentication);
        if (user == null) {
            return Result.error("用户不存在");
        }
        TeamTask task = teamService.createTask(id, dto, user.getId());
        return Result.success(task);
    }

    @PutMapping("/tasks/{taskId}")
    public Result<Void> updateTask(@PathVariable Long taskId, @RequestBody @Valid TeamTaskDTO dto, Authentication authentication) {
        User user = getCurrentUser(authentication);
        if (user == null) {
            return Result.error("用户不存在");
        }
        teamService.updateTask(taskId, dto, user.getId());
        return Result.success();
    }

    @DeleteMapping("/tasks/{taskId}")
    public Result<Void> deleteTask(@PathVariable Long taskId, Authentication authentication) {
        User user = getCurrentUser(authentication);
        if (user == null) {
            return Result.error("用户不存在");
        }
        teamService.deleteTask(taskId, user.getId());
        return Result.success();
    }

    @PutMapping("/tasks/{taskId}/status")
    public Result<Void> updateTaskStatus(@PathVariable Long taskId, @RequestParam String status, Authentication authentication) {
        User user = getCurrentUser(authentication);
        if (user == null) {
            return Result.error("用户不存在");
        }
        teamService.updateTaskStatus(taskId, status, user.getId());
        return Result.success();
    }

    @GetMapping("/{id}/tasks")
    public Result<List<TeamTask>> getTeamTasks(@PathVariable Long id, Authentication authentication) {
        User user = getCurrentUser(authentication);
        if (user == null) {
            return Result.error("用户不存在");
        }
        List<TeamTask> tasks = teamService.getTeamTasks(id, user.getId());
        return Result.success(tasks);
    }

    // ========== 消息管理 ==========

    @PostMapping("/{id}/messages")
    public Result<TeamMessage> sendMessage(@PathVariable Long id, @RequestBody @Valid TeamMessageDTO dto, Authentication authentication) {
        User user = getCurrentUser(authentication);
        if (user == null) {
            return Result.error("用户不存在");
        }
        TeamMessage message = teamService.sendMessage(id, dto, user.getId());
        return Result.success(message);
    }

    @GetMapping("/{id}/messages")
    public Result<List<TeamMessage>> getTeamMessages(
            @PathVariable Long id,
            @RequestParam(defaultValue = "100") int limit,
            Authentication authentication) {
        User user = getCurrentUser(authentication);
        if (user == null) {
            return Result.error("用户不存在");
        }
        List<TeamMessage> messages = teamService.getTeamMessages(id, limit, user.getId());
        return Result.success(messages);
    }
}
