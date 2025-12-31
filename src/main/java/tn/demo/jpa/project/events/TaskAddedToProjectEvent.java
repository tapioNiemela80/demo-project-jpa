package tn.demo.jpa.project.events;

import org.springframework.context.ApplicationEvent;
import tn.demo.jpa.project.domain.ProjectId;
import tn.demo.jpa.project.domain.ProjectTaskId;

import java.util.Objects;

public class TaskAddedToProjectEvent extends ApplicationEvent {
    private final ProjectId toProject;
    private final ProjectTaskId taskId;

    public TaskAddedToProjectEvent(ProjectId toProject, ProjectTaskId taskId) {
        super(toProject);
        this.toProject = toProject;
        this.taskId = taskId;
    }

    public ProjectId getToProject() {
        return toProject;
    }

    public ProjectTaskId getTaskId() {
        return taskId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaskAddedToProjectEvent that = (TaskAddedToProjectEvent) o;
        return toProject.equals(that.toProject) && taskId.equals(that.taskId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(toProject, taskId);
    }
}
