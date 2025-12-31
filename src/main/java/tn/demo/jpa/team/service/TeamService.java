package tn.demo.jpa.team.service;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.demo.jpa.project.domain.ProjectTaskId;
import tn.demo.jpa.project.domain.ProjectTaskSnapshot;
import tn.demo.jpa.project.domain.UnknownProjectTaskIdException;
import tn.demo.jpa.project.repository.ProjectRepository;
import tn.demo.jpa.team.controller.ActualSpentTime;
import tn.demo.jpa.team.domain.*;
import tn.demo.jpa.team.events.TeamTaskCompletedEvent;
import tn.demo.jpa.team.repository.TeamRepository;

@Service
public class TeamService {
    private final TeamRepository teams;
    private final ProjectRepository projects;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final tn.demo.jpa.common.IDService IDService;
    private final TeamFactory teamFactory;

    public TeamService(TeamRepository teams, ProjectRepository projects, ApplicationEventPublisher applicationEventPublisher, tn.demo.jpa.common.IDService IDService, TeamFactory teamFactory) {
        this.teams = teams;
        this.projects = projects;
        this.applicationEventPublisher = applicationEventPublisher;
        this.IDService = IDService;
        this.teamFactory = teamFactory;
    }

    @Transactional
    public TeamId createNew(String name) {
        TeamId teamId = IDService.newTeamId();
        teams.add(teamFactory.createNew(teamId, name));
        return teamId;
    }

    @Transactional
    public TeamMemberId addMember(TeamId teamId, String name, String profession) {
        TeamMemberId memberId = IDService.newTeamMemberId();
        Team team = teams.findById(teamId.value())
                .orElseThrow(() -> new UnknownTeamIdException(teamId));
        team.addMember(memberId, name, profession);
        return memberId;
    }

    @Transactional
    public void removeMember(TeamId teamId, TeamMemberId memberId) {
        Team team = teams.findById(teamId.value())
                .orElseThrow(() -> new UnknownTeamIdException(teamId));
        team.removeMember(memberId);
    }

    @Transactional
    public TeamTaskId addTask(TeamId teamId, ProjectTaskId projectTaskId) {
        boolean alreadyBelongsToSomeTeam = checkIfAlreadyBelongsToSomeTeam(projectTaskId);
        if (alreadyBelongsToSomeTeam) {
            throw new TaskAlreadyAssignedException("Task is already assigned to some team");
        }
        ProjectTaskSnapshot projectTaskSnapshot = projects.findByTaskId(projectTaskId.value())
                .flatMap(project -> project.getTask(projectTaskId))
                .orElseThrow(() -> new UnknownProjectTaskIdException(projectTaskId));
        Team team = teams.findById(teamId.value()).orElseThrow(() -> new UnknownTeamIdException(teamId));
        TeamTaskId teamTaskId = IDService.newTeamTaskId();
        team.addTask(teamTaskId, projectTaskSnapshot.projectTaskId(), projectTaskSnapshot.title(), projectTaskSnapshot.description());
        return teamTaskId;
    }

    private boolean checkIfAlreadyBelongsToSomeTeam(ProjectTaskId originalTaskId) {
        return teams.findByOriginalProjectTaskId(originalTaskId.value()).isPresent();
    }

    @Transactional
    public void assignTask(TeamId teamId, TeamTaskId taskID, TeamMemberId toMemberId) {
        Team team = teams.findById(teamId.value())
                .orElseThrow(() -> new UnknownTeamIdException(teamId));
        team.assignTask(taskID, toMemberId);
    }

    @Transactional
    public void markTaskInProgress(TeamId teamId, TeamTaskId taskID) {
        Team team = teams.findById(teamId.value())
                .orElseThrow(() -> new UnknownTeamIdException(teamId));
        team.markTaskInProgress(taskID);
    }

    @Transactional
    public void unassignTask(TeamId teamId, TeamTaskId taskID) {
        Team team = teams.findById(teamId.value())
                .orElseThrow(() -> new UnknownTeamIdException(teamId));
        team.markTaskUnassigned(taskID);
    }

    @Transactional
    public void removeTask(TeamId teamId, TeamTaskId taskID) {
        Team team = teams.findById(teamId.value())
                .orElseThrow(() -> new UnknownTeamIdException(teamId));
        team.removeTask(taskID);
    }

    @Transactional
    public void completeTask(TeamId teamId, TeamTaskId taskID, ActualSpentTime actualSpentTime) {
        var timeSpent = toDomain(actualSpentTime);
        Team team = teams.findById(teamId.value())
                .orElseThrow(() -> new UnknownTeamIdException(teamId));
        team.markTaskCompleted(taskID, timeSpent);
        publishTaskCompletedEvent(taskID, team, timeSpent);
    }

    private tn.demo.jpa.common.domain.ActualSpentTime toDomain(ActualSpentTime actualSpentTime) {
        return new tn.demo.jpa.common.domain.ActualSpentTime(actualSpentTime.hours(), actualSpentTime.minutes());
    }

    private Team publishTaskCompletedEvent(TeamTaskId taskID, Team team, tn.demo.jpa.common.domain.ActualSpentTime actualSpentTime) {
        team.getOriginalTaskId(taskID)
                .ifPresent(projectTaskId -> applicationEventPublisher.publishEvent(new TeamTaskCompletedEvent(taskID, projectTaskId, actualSpentTime)));
        return team;
    }

}
