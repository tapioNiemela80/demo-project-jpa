package tn.demo.jpa.project.domain;

import jakarta.persistence.*;
import tn.demo.jpa.common.domain.ActualSpentTime;
import tn.demo.jpa.common.domain.RootAware;

import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "project_tasks")
class ProjectTask implements RootAware<Project> {
    @Id
    private UUID id;

    private String title;

    private String description;

    @Column(name = "estimated_time_hours")
    private int estimatedTimeHours;

    @Column(name = "estimated_time_minutes")
    private int estimatedTimeMinutes;

    @Enumerated(value = EnumType.STRING)
    private TaskStatus taskStatus;

    @Column(name = "actual_time_spent_hours")
    private Integer actualTimeSpentHours;

    @Column(name = "actual_time_spent_minutes")
    private Integer actualTimeSpentMinutes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    protected ProjectTask() {
        //for jpa
    }

    private ProjectTask(UUID id, String title, String description, int estimatedTimeHours, int estimatedTimeMinutes, TaskStatus taskStatus, Project project) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.estimatedTimeHours = estimatedTimeHours;
        this.estimatedTimeMinutes = estimatedTimeMinutes;
        this.taskStatus = taskStatus;
        this.project = project;
    }

    static ProjectTask newInstance(Project project, ProjectTaskId id, String title, String description, TimeEstimation estimation) {
        return new ProjectTask(id.value(), title, description, estimation.getHours(), estimation.getMinutes(), TaskStatus.INCOMPLETE, project);
    }

    void complete(ActualSpentTime actualSpentTime) {
        this.taskStatus = TaskStatus.COMPLETE;
        this.actualTimeSpentHours = actualSpentTime.getHours();
        this.actualTimeSpentMinutes = actualSpentTime.getMinutes();
    }

    boolean hasId(ProjectTaskId expected) {
        return expected.equals(new ProjectTaskId(id));
    }

    TimeEstimation getEstimation() {
        return new TimeEstimation(estimatedTimeHours, estimatedTimeMinutes);
    }

    ProjectTaskSnapshot toSnapshot(ProjectId projectId) {
        return new ProjectTaskSnapshot(new ProjectTaskId(id), projectId, title, description, getEstimation());
    }

    public boolean isCompleted() {
        return taskStatus == TaskStatus.COMPLETE;
    }

    @Override
    public Project root() {
        return project;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProjectTask other = (ProjectTask) o;
        return Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
