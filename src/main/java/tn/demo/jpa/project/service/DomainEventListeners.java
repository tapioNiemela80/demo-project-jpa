package tn.demo.jpa.project.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import tn.demo.jpa.common.EmailClientService;
import tn.demo.jpa.common.EmailMessage;
import tn.demo.jpa.common.domain.ActualSpentTime;
import tn.demo.jpa.common.domain.EmailAddress;
import tn.demo.jpa.project.domain.EmailNotificationPolicy;
import tn.demo.jpa.project.domain.Project;
import tn.demo.jpa.project.domain.ProjectTaskId;
import tn.demo.jpa.project.domain.ProjectTaskSnapshot;
import tn.demo.jpa.project.events.TaskAddedToProjectEvent;
import tn.demo.jpa.project.repository.ProjectRepository;
import tn.demo.jpa.team.events.TeamTaskCompletedEvent;

import java.util.Optional;

@Component
public class DomainEventListeners {
    private final ProjectRepository projects;
    private final EmailClientService emailClientService;
    private final EmailNotificationPolicy emailNotificationPolicy;

    private final EmailAddress sender;

    private static final Logger log = LoggerFactory.getLogger(DomainEventListeners.class);

    public DomainEventListeners(ProjectRepository projects, EmailClientService emailClientService,
                                EmailNotificationPolicy emailNotificationPolicy,
                                @Value("${email.sender}") String sender) {
        this.projects = projects;
        this.emailClientService = emailClientService;
        this.emailNotificationPolicy = emailNotificationPolicy;
        this.sender = EmailAddress.of(sender);
    }

    @Retryable(
            recover = "recoverOptimisticLockingFailureExceptionOnTeamTaskCompletedEvent",
            retryFor = OptimisticLockingFailureException.class,
            maxAttempts = 5,
            backoff = @Backoff(delay = 200)
    )
    @TransactionalEventListener
    public void on(TeamTaskCompletedEvent teamTaskCompletedEvent) {
        log.atDebug().log(() -> "teamTaskCompletedEvent %s".formatted(teamTaskCompletedEvent));

        Optional<Project> projectByTaskId = projects.findByTaskId(teamTaskCompletedEvent.getProjectTaskId().value());
        projectByTaskId.ifPresentOrElse(project -> markProjectTaskCompleted(teamTaskCompletedEvent, project),
                () -> log.warn("Project by task id not found {}", teamTaskCompletedEvent.getProjectTaskId()));
    }

    @Recover
    public void recoverOptimisticLockingFailureExceptionOnTeamTaskCompletedEvent(OptimisticLockingFailureException ex, TeamTaskCompletedEvent event) {
        log.error("failed to complete task after 5 retries for event: {}", event, ex);
    }

    @TransactionalEventListener
    public void on(TaskAddedToProjectEvent taskAddedToProjectEvent) {
        log.atDebug().log(() -> "taskAddedToProjectEvent %s".formatted(taskAddedToProjectEvent));
        projects.findById(taskAddedToProjectEvent.getToProject().value())
                .ifPresentOrElse(
                        project -> notifyContactPerson(project, taskAddedToProjectEvent.getTaskId()),
                        () -> log.warn("Project not found {}", taskAddedToProjectEvent.getToProject())
                );
    }

    private void notifyContactPerson(Project project, ProjectTaskId taskId) {
        project.getTask(taskId)
                .ifPresentOrElse(
                        task -> attemptToSendEmail(project, taskId, task),
                        () -> log.warn("Task {} not found in project {}", taskId, project.getId())
                );
    }

    private void attemptToSendEmail(Project project, ProjectTaskId taskId, ProjectTaskSnapshot task) {
        project.validContactEmail()
                .ifPresentOrElse(
                        emailRecipient -> sendMailIfPolicyAllows(emailRecipient, project, taskId, task),
                        warnInvalidRecipientAddress(project, taskId));
    }

    private void sendMailIfPolicyAllows(EmailAddress emailRecipient, Project project, ProjectTaskId taskId, ProjectTaskSnapshot task) {
        if (!emailNotificationPolicy.notificationToEmailIsAllowed(emailRecipient)) {
            log.info("Skipping sending email about new task {} to project {}, policy denied", taskId, project.getId());
            return;
        }
        emailClientService.send(
                new EmailMessage(sender, emailRecipient, "Task added", "Task %s was added".formatted(task), false));
    }

    private Runnable warnInvalidRecipientAddress(Project project, ProjectTaskId taskId) {
        return () -> log.warn("Skipping sending email notification about new task {} for project {}: invalid contact email",
                taskId, project.getId());
    }

    private void markProjectTaskCompleted(TeamTaskCompletedEvent teamTaskCompletedEvent, Project project) {
        project.completeTask((teamTaskCompletedEvent.getProjectTaskId()), teamTaskCompletedEvent.getActualSpentTime());
    }

}
