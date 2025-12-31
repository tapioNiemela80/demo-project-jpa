package tn.demo.jpa.project.domain;

public record ProjectTaskSnapshot(ProjectTaskId projectTaskId, ProjectId projectId, String title, String description, TimeEstimation timeEstimation) {
}
