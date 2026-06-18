package com.teaching.competition.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.teaching.competition.common.PageResult;
import com.teaching.competition.dto.TeamDTO;
import com.teaching.competition.dto.TeamMessageDTO;
import com.teaching.competition.dto.TeamTaskDTO;
import com.teaching.competition.entity.Team;
import com.teaching.competition.entity.TeamMember;
import com.teaching.competition.entity.TeamMessage;
import com.teaching.competition.entity.TeamTask;

import java.util.List;

public interface TeamService extends IService<Team> {

    // 团队管理
    Team createTeam(TeamDTO dto, Long userId);
    void updateTeam(Long id, TeamDTO dto, Long userId);
    void deleteTeam(Long id, Long userId);
    Team getTeamDetail(Long id);
    Team getTeamDetail(Long id, Long userId);
    List<Team> getMyTeams(Long userId);
    List<Team> getAdvisedTeams(Long advisorId);
    PageResult<Team> getAllTeams(int page, int size);

    // 成员管理
    void joinTeam(Long teamId, String inviteCode, Long userId);
    void leaveTeam(Long teamId, Long userId);
    void removeMember(Long teamId, Long memberId, Long operatorId);
    List<TeamMember> getTeamMembers(Long teamId);
    List<TeamMember> getTeamMembers(Long teamId, Long userId);
    String generateInviteCode(Long teamId, Long userId);

    // 任务管理
    TeamTask createTask(Long teamId, TeamTaskDTO dto, Long userId);
    void updateTask(Long taskId, TeamTaskDTO dto, Long userId);
    void deleteTask(Long taskId, Long userId);
    void updateTaskStatus(Long taskId, String status, Long userId);
    List<TeamTask> getTeamTasks(Long teamId);
    List<TeamTask> getTeamTasks(Long teamId, Long userId);

    // 消息管理
    TeamMessage sendMessage(Long teamId, TeamMessageDTO dto, Long userId);
    List<TeamMessage> getTeamMessages(Long teamId, int limit);
    List<TeamMessage> getTeamMessages(Long teamId, int limit, Long userId);

    // 获取可选的指导老师列表
    List<?> getAvailableAdvisors();
}
