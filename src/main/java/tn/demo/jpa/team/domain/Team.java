package tn.demo.jpa.team.domain;

import jakarta.persistence.*;
import tn.demo.jpa.common.domain.ActualSpentTime;
import tn.demo.jpa.common.domain.AggregateRoot;
import tn.demo.jpa.project.domain.ProjectTaskId;

import java.util.*;

@AggregateRoot
@Entity
@Table(name = "teams")
public class Team {
    @Id
    private UUID id;
    private String name;
    @Version
    private int version;
    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<TeamMember> members;

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<TeamTask> tasks;

    protected Team() {
        //for jpa
    }

    private Team(UUID id, String name) {
        this.id = id;
        this.name = name;
        this.version = 0;
        this.members = new HashSet<>();
        this.tasks = new HashSet<>();
    }

    public static Team createNew(TeamId id, String name) {
        return new Team(id.value(), name);
    }

    public void addMember(TeamMemberId memberId, String name, String profession) {
        members.add(TeamMember.createNew(memberId, name, profession, this));
    }

    public boolean containsMember(TeamMemberId memberId, String name, String profession) {
        return members.stream().anyMatch(member -> member.hasDetails(memberId, name, profession));
    }

    public boolean containsCompletedTask(TeamTaskId taskId, ProjectTaskId projectTaskId, String name, String description, ActualSpentTime actualSpentTime) {
        return tasks.stream()
                .anyMatch(task -> task.hasDetails(taskId, projectTaskId, name, description, null, actualSpentTime, TeamTaskStatus.COMPLETED));
    }

    public boolean containsUncompletedTask(TeamTaskId taskId, ProjectTaskId projectTaskId, String name, String description, TeamMemberId assignee, TeamTaskStatus expectedStatus) {
        return tasks.stream()
                .anyMatch(task -> task.hasDetails(taskId, projectTaskId, name, description, assignee, null, expectedStatus));
    }

    public void addTask(TeamTaskId taskId, ProjectTaskId projectTaskId, String name, String description) {
        tasks.add(TeamTask.createNew(taskId, projectTaskId, name, description, this));
    }

    public void removeTask(TeamTaskId taskId) {
        verifyContainsTask(taskId);
        verifyTaskCanBeRemoved(taskId);
        TeamTask foundTask = tasks.stream()
                .filter(task -> task.hasId(taskId))
                .findAny().orElseThrow();
        removeTask(foundTask);
    }

    private void removeTask(TeamTask task) {
        this.tasks.remove(task);
        task.markTeamRemoved();
    }

    public void removeMember(TeamMemberId memberId) {
        Objects.requireNonNull(memberId);
        verifyContainsMember(memberId);
        verifyMemberCanBeRemoved(memberId);
        TeamMember foundMember = members.stream()
                .filter(member -> member.hasId(memberId))
                .findFirst().orElseThrow();
        removeMember(foundMember);
    }

    private void removeMember(TeamMember member) {
        this.members.remove(member);
        member.markTeamRemoved();
    }

    private void verifyMemberCanBeRemoved(TeamMemberId memberId) {
        if (tasks.stream().anyMatch(task -> task.isAssignedTo(memberId))) {
            throw new TeamMemberHasAssignedTasksException(memberId);
        }
    }

    private void verifyContainsMember(TeamMemberId memberId) {
        if (members.stream().noneMatch(member -> member.hasId(memberId))) {
            throw new UnknownTeamMemberIdException(memberId);
        }
    }

    private void verifyTaskCanBeRemoved(TeamTaskId taskId) {
        var canBeDeleted = tasks.stream()
                .filter(task -> task.hasId(taskId))
                .map(TeamTask::canBeDeleted)
                .findFirst()
                .orElse(true);
        if (!canBeDeleted) {
            throw new TaskCannotBeDeletedException(taskId);
        }
    }

    public Optional<ProjectTaskId> getOriginalTaskId(TeamTaskId taskId) {
        return tasks.stream()
                .filter(task -> task.hasId(taskId))
                .map(TeamTask::getOriginalTaskId)
                .findFirst();
    }

    private void verifyContainsTask(TeamTaskId taskId) {
        if (tasks.stream().noneMatch(task -> task.hasId(taskId))) {
            throw new UnknownTeamTaskIdException(taskId);
        }
    }

    public void assignTask(TeamTaskId taskId, TeamMemberId memberId) {
        verifyContainsTask(taskId);
        if (members.stream().noneMatch(member -> member.hasId(memberId))) {
            throw new UnknownTeamMemberIdException(memberId);
        }
        tasks.stream()
                .filter(task -> task.hasId(taskId))
                .forEach(task -> task.assignTo(memberId));
    }

    public void markTaskInProgress(TeamTaskId taskId) {
        verifyContainsTask(taskId);
        tasks.stream()
                .filter(task -> task.hasId(taskId))
                .forEach(TeamTask::markInProgress);
    }

    public void markTaskCompleted(TeamTaskId taskId, ActualSpentTime actualSpentTime) {
        verifyContainsTask(taskId);
        tasks.stream()
                .filter(task -> task.hasId(taskId))
                .forEach(task -> task.complete(actualSpentTime));
    }

    public void markTaskUnassigned(TeamTaskId taskId) {
        verifyContainsTask(taskId);
        tasks.stream()
                .filter(task -> task.hasId(taskId))
                .forEach(TeamTask::unassign);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Team other = (Team) o;
        return Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

}