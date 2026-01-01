package tn.demo.jpa.project.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import tn.demo.jpa.common.IDService;
import tn.demo.jpa.project.controller.ContactPersonInput;
import tn.demo.jpa.project.domain.*;
import tn.demo.jpa.project.events.TaskAddedToProjectEvent;
import tn.demo.jpa.project.repository.ProjectRepository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {
    @Mock
    private ProjectRepository projects;
    @Mock
    private IDService IDService;
    @Mock
    private ProjectFactory projectFactory;
    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    private ProjectService underTest;

    @BeforeEach
    void setup(){
        underTest = new ProjectService(projects, IDService, applicationEventPublisher, projectFactory);
    }

    @Test
    void createsProject(){
        ProjectId id = new ProjectId(UUID.randomUUID());
        LocalDate endDate = LocalDate.of(2026,12,31);
        tn.demo.jpa.project.controller.TimeEstimation timeEstimation = new tn.demo.jpa.project.controller.TimeEstimation(100,0);
        Project project = mock(Project.class);
        when(projectFactory.createNew("test project", "test description", endDate, timeEstimation, new ContactPersonInput("name", "email"))).thenReturn(project);
        when(project.getId()).thenReturn(id.value());
        when(projects.add(project)).thenReturn(project);

        ProjectId actual = underTest.createProject( "test project", "test description", endDate, timeEstimation, new ContactPersonInput("name", "email"));
        assertEquals(id, actual);
    }
    @Test
    void addsTaskTo(){
        ProjectId id = new ProjectId(UUID.randomUUID());
        Project project = mock(Project.class);
        String taskName = "taskName";
        String description = "task description";
        ProjectTaskId taskId = new ProjectTaskId(UUID.randomUUID());
        when(IDService.newProjectTaskId()).thenReturn(taskId);
        when(projects.findById(id.value())).thenReturn(Optional.of(project));

        ProjectTaskId actualTaskId = underTest.addTaskTo(id, taskName, description, new tn.demo.jpa.project.controller.TimeEstimation(0,5));
        assertEquals(taskId, actualTaskId);

        verify(project).addTask(taskId, taskName, description, TimeEstimation.fromMinutes(5));
        verify(applicationEventPublisher).publishEvent(new TaskAddedToProjectEvent(id, taskId));
    }

    @Test
    void throwsExceptionWhenUnknownProjectIdWhenTryingToAddTaskToProject(){
        ProjectId id = new ProjectId(UUID.randomUUID());
        String taskName = "taskName";
        String description = "task description";
        ProjectTaskId taskId = new ProjectTaskId(UUID.randomUUID());
        when(IDService.newProjectTaskId()).thenReturn(taskId);
        when(projects.findById(id.value())).thenReturn(Optional.empty());

        assertThrows(UnknownProjectIdException.class, () -> underTest.addTaskTo(id, taskName, description, new tn.demo.jpa.project.controller.TimeEstimation(0,5)));
        verifyNoInteractions(applicationEventPublisher);
    }
}