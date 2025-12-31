package tn.demo.jpa.team.events;

import org.springframework.context.ApplicationEvent;
import tn.demo.jpa.common.domain.ActualSpentTime;
import tn.demo.jpa.project.domain.ProjectTaskId;
import tn.demo.jpa.team.domain.TeamTaskId;

import java.util.Objects;

public class TeamTaskCompletedEvent extends ApplicationEvent {
    private final TeamTaskId taskID;
    private final ProjectTaskId projectTaskId;
    private final ActualSpentTime actualSpentTime;
    public TeamTaskCompletedEvent(TeamTaskId taskID, ProjectTaskId projectTaskId, ActualSpentTime actualSpentTime) {
        super(taskID);
        this.taskID = taskID;
        this.projectTaskId = projectTaskId;
        this.actualSpentTime = actualSpentTime;
    }
    public ProjectTaskId getProjectTaskId() {
        return projectTaskId;
    }

    public ActualSpentTime getActualSpentTime() {
        return actualSpentTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TeamTaskCompletedEvent that = (TeamTaskCompletedEvent) o;
        return taskID.equals(that.taskID) && projectTaskId.equals(that.projectTaskId) && actualSpentTime.equals(that.actualSpentTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskID, projectTaskId, actualSpentTime);
    }
}
