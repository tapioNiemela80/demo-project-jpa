package tn.demo.jpa.project.domain;

import jakarta.persistence.*;
import tn.demo.jpa.common.domain.ActualSpentTime;
import tn.demo.jpa.common.domain.AggregateRoot;
import tn.demo.jpa.common.domain.EmailAddress;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@AggregateRoot
@Entity
@Table(name = "projects")
public class Project {
    @Id
    private UUID id;
    private String name;
    private String description;
    private LocalDateTime createdAt;
    private LocalDate plannedEndDate;
    @Enumerated(value = EnumType.STRING)
    private ProjectStatus status;
    @Version
    private int version;
    @Column(name = "initial_estimated_time_hours")
    private int initialEstimationHours;
    @Column(name = "initial_estimated_time_minutes")
    private int initialEstimationMinutes;
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ProjectTask> tasks = new HashSet<>();

    private String contactPersonName;
    private String contactPersonEmail;

    public static Project createNew(ProjectId projectId, String name, String description, LocalDateTime now,
                                    LocalDate plannedEndDate, TimeEstimation timeEstimation, ContactPerson contactPerson) {
        return new Project(projectId.value(), name, description, now, plannedEndDate, timeEstimation, contactPerson);
    }

    private Project(UUID id, String name, String description, LocalDateTime createdAt, LocalDate plannedEndDate,
                    TimeEstimation timeEstimation, ContactPerson contactPerson) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.createdAt = createdAt;
        this.plannedEndDate = plannedEndDate;
        this.status = ProjectStatus.PLANNED;
        this.version = 0;
        this.tasks = new HashSet<>();
        this.initialEstimationHours = timeEstimation.getHours();
        this.initialEstimationMinutes = timeEstimation.getMinutes();
        this.contactPersonName = contactPerson.name();
        this.contactPersonEmail = contactPerson.email().value();
    }

    protected Project() {
        //for jpa
    }

    public UUID getId() {
        return id;
    }

    public void addTask(ProjectTaskId taskId, String title, String description, TimeEstimation estimation) {
        if (isCompleted()) {
            throw new ProjectAlreadyCompletedException(new ProjectId(id));
        }
        var currentTotalEstimation = getEstimationOfAllTasks();
        var newEstimation = currentTotalEstimation.add(estimation);
        if (newEstimation.exceedsOther(getInitialEstimation())) {
            throw new ProjectTimeEstimationWouldBeExceededException("Cannot add any more tasks, project estimation would be exceeded");
        }

        tasks.add(ProjectTask.newInstance(this, taskId, title, description, estimation));
    }

    TimeEstimation getInitialEstimation() {
        return new TimeEstimation(initialEstimationHours, initialEstimationMinutes);
    }

    public void completeTask(ProjectTaskId taskId, ActualSpentTime actualSpentTime) {
        verifyContainsTask(taskId);

        tasks.stream()
                .filter(task -> task.hasId(taskId))
                .forEach(task -> task.complete(actualSpentTime));
    }

    private void verifyContainsTask(ProjectTaskId projectTaskId) {
        tasks.stream()
                .filter(task -> task.hasId(projectTaskId))
                .findFirst()
                .orElseThrow(() -> new UnknownProjectTaskIdException(projectTaskId));
    }

    public boolean isCompleted() {
        return status == ProjectStatus.COMPLETED;
    }

    public Optional<ProjectTaskSnapshot> getTask(ProjectTaskId projectTaskId) {
        return tasks.stream()
                .filter(task -> task.hasId(projectTaskId))
                .map(task -> task.toSnapshot(new ProjectId(this.getId())))
                .findFirst();
    }

    public TimeEstimation getEstimationOfAllTasks() {
        return tasks.stream()
                .map(ProjectTask::getEstimation)
                .reduce(TimeEstimation::add)
                .orElseGet(TimeEstimation::zeroEstimation);
    }

    public Optional<EmailAddress> validContactEmail() {
        return EmailAddress.rehydrate(contactPersonEmail).isValid() ?
                Optional.of(EmailAddress.rehydrate(contactPersonEmail))
                : Optional.empty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Project other = (Project) o;
        return Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

}