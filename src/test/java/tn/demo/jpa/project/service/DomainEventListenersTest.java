package tn.demo.jpa.project.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.demo.jpa.common.EmailClientService;
import tn.demo.jpa.common.EmailMessage;
import tn.demo.jpa.common.domain.ActualSpentTime;
import tn.demo.jpa.common.domain.EmailAddress;
import tn.demo.jpa.project.domain.*;
import tn.demo.jpa.project.events.TaskAddedToProjectEvent;
import tn.demo.jpa.project.repository.ProjectRepository;
import tn.demo.jpa.team.domain.TeamTaskId;
import tn.demo.jpa.team.events.TeamTaskCompletedEvent;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DomainEventListenersTest {
    @Mock
    private ProjectRepository projects;
    @Mock
    private EmailClientService emailClientService;
    @Mock
    private EmailNotificationPolicy emailNotificationPolicy;

    private DomainEventListeners underTest;
    private String sender ="example.project@demo.org";

    @BeforeEach
    void setup() {
        underTest = new DomainEventListeners(projects, emailClientService, emailNotificationPolicy, sender);
    }

    @Test
    void marksProjectTaskCompletedOnTeamTaskCompletedEvent(){
        TeamTaskId taskID = new TeamTaskId(UUID.randomUUID());
        ProjectTaskId projectTaskId = new ProjectTaskId(UUID.randomUUID());

        TeamTaskCompletedEvent teamTaskCompletedEvent = new TeamTaskCompletedEvent(taskID, projectTaskId, ActualSpentTime.fromMinutes(50));
        Project project = mock(Project.class);
        when(projects.findByTaskId(projectTaskId.value())).thenReturn(Optional.of(project));

        underTest.on(teamTaskCompletedEvent);
        verify(project).completeTask(projectTaskId, ActualSpentTime.fromMinutes(50));
    }

    @Test
    void sendsEmailOnTaskAddedToProjectEvent(){
        Project project = mock(Project.class);
        EmailAddress emailAddress = mock(EmailAddress.class);
        when(project.validContactEmail()).thenReturn(Optional.of(emailAddress));
        ProjectId projectId = new ProjectId(UUID.randomUUID());
        ProjectTaskId taskId = new ProjectTaskId(UUID.randomUUID());
        when(projects.findById(projectId.value())).thenReturn(Optional.of(project));
        when(emailNotificationPolicy.notificationToEmailIsAllowed(emailAddress)).thenReturn(true);
        when(project.getTask(taskId)).thenReturn(Optional.of(new ProjectTaskSnapshot(taskId, projectId, "title", "desc", TimeEstimation.fromMinutes(1))));
        TaskAddedToProjectEvent taskAddedToProjectEvent = new TaskAddedToProjectEvent(projectId, taskId);

        underTest.on(taskAddedToProjectEvent);
        verify(emailClientService).send(any(EmailMessage.class));
    }

    @Test
    void doesNotSendEmailOnTaskAddedToProjectEventWhenContactEmailIsIncorrect(){
        Project project = mock(Project.class);
        when(project.validContactEmail()).thenReturn(Optional.empty());
        ProjectId projectId = new ProjectId(UUID.randomUUID());
        ProjectTaskId taskId = new ProjectTaskId(UUID.randomUUID());
        when(projects.findById(projectId.value())).thenReturn(Optional.of(project));
        when(project.getTask(taskId)).thenReturn(Optional.of(new ProjectTaskSnapshot(taskId, projectId, "title", "desc", TimeEstimation.fromMinutes(1))));
        TaskAddedToProjectEvent taskAddedToProjectEvent = new TaskAddedToProjectEvent(projectId, taskId);

        underTest.on(taskAddedToProjectEvent);
        verify(emailClientService, never()).send(any(EmailMessage.class));
    }

    @Test
    void doesNotSendEmailOnTaskAddedToProjectEventWhenPolicyDisallows(){
        Project project = mock(Project.class);
        EmailAddress emailAddress = mock(EmailAddress.class);
        when(project.validContactEmail()).thenReturn(Optional.of(emailAddress));
        ProjectId projectId = new ProjectId(UUID.randomUUID());
        ProjectTaskId taskId = new ProjectTaskId(UUID.randomUUID());
        when(projects.findById(projectId.value())).thenReturn(Optional.of(project));
        when(project.getTask(taskId)).thenReturn(Optional.of(new ProjectTaskSnapshot(taskId, projectId, "title", "desc", TimeEstimation.fromMinutes(1))));
        TaskAddedToProjectEvent taskAddedToProjectEvent = new TaskAddedToProjectEvent(projectId, taskId);

        underTest.on(taskAddedToProjectEvent);
        verify(emailClientService, never()).send(any(EmailMessage.class));
    }
}