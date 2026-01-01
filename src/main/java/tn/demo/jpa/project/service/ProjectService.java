package tn.demo.jpa.project.service;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.demo.jpa.common.IDService;
import tn.demo.jpa.project.controller.ContactPersonInput;
import tn.demo.jpa.project.controller.TimeEstimation;
import tn.demo.jpa.project.domain.Project;
import tn.demo.jpa.project.domain.ProjectId;
import tn.demo.jpa.project.domain.ProjectTaskId;
import tn.demo.jpa.project.domain.UnknownProjectIdException;
import tn.demo.jpa.project.events.TaskAddedToProjectEvent;
import tn.demo.jpa.project.repository.ProjectRepository;

import java.time.LocalDate;

@Service
public class ProjectService {
    private final ProjectRepository projects;
    private final IDService IDService;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final ProjectFactory projectFactory;

    public ProjectService(ProjectRepository projects, IDService IDService, ApplicationEventPublisher applicationEventPublisher, ProjectFactory projectFactory) {
        this.projects = projects;
        this.IDService = IDService;
        this.applicationEventPublisher = applicationEventPublisher;
        this.projectFactory = projectFactory;
    }

    @Transactional
    public ProjectId createProject(String name, String description, LocalDate estimatedEndDate, TimeEstimation estimation, ContactPersonInput contactPerson) {
        Project project = projectFactory.createNew(name, description, estimatedEndDate, estimation, contactPerson);
        return new ProjectId(projects.add(project).getId());
    }

    @Transactional
    public ProjectTaskId addTaskTo(ProjectId projectId, String taskName, String description, TimeEstimation estimation) {
        ProjectTaskId taskId = IDService.newProjectTaskId();

        Project project = projects.findById(projectId.value())
                .orElseThrow(() -> new UnknownProjectIdException(projectId));

        project.addTask(taskId, taskName, description, toDomain(estimation));
        applicationEventPublisher.publishEvent(new TaskAddedToProjectEvent(projectId, taskId));
        return taskId;
    }

    private tn.demo.jpa.project.domain.TimeEstimation toDomain(TimeEstimation estimation) {
        return new tn.demo.jpa.project.domain.TimeEstimation(estimation.hours(), estimation.minutes());
    }

}