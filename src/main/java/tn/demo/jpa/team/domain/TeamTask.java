package tn.demo.jpa.team.domain;

import jakarta.persistence.*;
import tn.demo.jpa.common.domain.ActualSpentTime;
import tn.demo.jpa.common.domain.RootAware;
import tn.demo.jpa.project.domain.ProjectTaskId;

import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "team_tasks")
class TeamTask implements RootAware<Team> {
    @Id
    private UUID id;
    private UUID projectTaskId;
    private String name;
    private String description;
    @Enumerated(value = EnumType.STRING)
    private TeamTaskStatus status;
    private UUID assigneeId;
    @Column(name = "actual_time_spent_hours")
    private Integer actualTimeSpentHours;
    @Column(name = "actual_time_spent_minutes")
    private Integer actualTimeSpentMinutes;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    protected TeamTask() {
        //for jpa
    }

    private TeamTask(UUID id, UUID projectTaskId, String name, String description, Team team) {
        this.id = id;
        this.projectTaskId = projectTaskId;
        this.name = name;
        this.description = description;
        this.status = TeamTaskStatus.NOT_ASSIGNED;
        this.assigneeId = null;
        this.actualTimeSpentHours = null;
        this.actualTimeSpentMinutes = null;
        this.team = team;
    }

    static TeamTask createNew(TeamTaskId id, ProjectTaskId projectTaskId, String name, String description, Team team) {
        return new TeamTask(id.value(), projectTaskId.value(), name, description, team);
    }

    boolean canBeDeleted() {
        return status == TeamTaskStatus.NOT_ASSIGNED;
    }

    void assignTo(TeamMemberId assigneeId) {
        if (this.status != TeamTaskStatus.NOT_ASSIGNED) {
            throw new TaskTransitionNotAllowedException("Task already assigned or in progress.");
        }
        this.status = TeamTaskStatus.ASSIGNED;
        this.assigneeId = assigneeId.value();
    }

    void markInProgress() {
        if (this.status != TeamTaskStatus.ASSIGNED) {
            throw new TaskTransitionNotAllowedException("Task needs to be assigned before it can be put to in progress.");
        }
        this.status = TeamTaskStatus.IN_PROGRESS;
    }

    void complete(ActualSpentTime actualTimeSpent) {
        if (this.status != TeamTaskStatus.IN_PROGRESS) {
            throw new TaskTransitionNotAllowedException("task not in progress");
        }
        this.status = TeamTaskStatus.COMPLETED;
        this.assigneeId = null;
        this.actualTimeSpentHours = actualTimeSpent.getHours();
        this.actualTimeSpentMinutes = actualTimeSpent.getMinutes();
    }

    void unassign() {
        if (this.status != TeamTaskStatus.ASSIGNED) {
            throw new TaskTransitionNotAllowedException("Task is not assigned");
        }
        this.status = TeamTaskStatus.NOT_ASSIGNED;
        this.assigneeId = null;
    }

    boolean hasId(TeamTaskId expected) {
        return id.equals(expected.value());
    }

    ProjectTaskId getOriginalTaskId() {
        return new ProjectTaskId(projectTaskId);
    }

    void markTeamRemoved(){
        this.team = null;
    }

    boolean hasDetails(TeamTaskId taskId, ProjectTaskId projectTaskId, String name, String description, TeamMemberId assignee, ActualSpentTime actualSpentTime, TeamTaskStatus expectedStatus) {
        return hasId(taskId)
                && Objects.equals(projectTaskId.value(), this.projectTaskId)
                && Objects.equals(name, this.name)
                && Objects.equals(description, this.description)
                && assigneeEquals(assignee)
                && Objects.equals(actualSpentTime, this.getActualSpentTime())
                && expectedStatus == this.status;
    }

    private boolean assigneeEquals(TeamMemberId expected) {
        if (expected == null) {
            return assigneeId == null;
        }
        return new TeamMemberId(assigneeId).equals(expected);
    }

    private ActualSpentTime getActualSpentTime() {
        if (status == TeamTaskStatus.COMPLETED) {
            return new ActualSpentTime(actualTimeSpentHours, actualTimeSpentMinutes);
        }
        return null;
    }

    boolean isAssignedTo(TeamMemberId memberId) {
        Objects.requireNonNull(memberId);
        if (assigneeId == null) {
            return false;
        }
        return assigneeId.equals(memberId.value());
    }

    @Override
    public Team root() {
        return team;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TeamTask other = (TeamTask) o;
        return Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
