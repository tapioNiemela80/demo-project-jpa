package tn.demo.jpa.project.domain;

import org.junit.jupiter.api.Test;
import tn.demo.jpa.common.domain.ActualSpentTime;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ProjectTest {
    @Test
    void addsTask() {
        LocalDateTime now = LocalDateTime.of(2025, 6, 1, 18, 0, 0);
        LocalDate endDate = LocalDate.of(2026, 12, 31);
        ProjectId id = new ProjectId(UUID.randomUUID());
        ProjectTaskId taskId = new ProjectTaskId(UUID.randomUUID());
        Project project = Project.createNew(id, "test project", "testing", now, endDate, TimeEstimation.fromMinutes(60), contactPerson());
        verifyNoTaskFound(project, taskId);

        project.addTask(taskId, "task name", "some description", TimeEstimation.fromMinutes(60));
        Optional<ProjectTaskSnapshot> task = project.getTask(taskId);
        assertTrue(task.isPresent());

        ProjectTaskSnapshot expected = new ProjectTaskSnapshot(taskId, id, "task name", "some description", TimeEstimation.fromMinutes(60));
        assertEquals(expected, task.get());
    }

    @Test
    void cannotAddTaskAsTimeEstimationWouldBeExceeded() {
        LocalDateTime now = LocalDateTime.of(2025, 6, 1, 18, 0, 0);
        LocalDate endDate = LocalDate.of(2026, 12, 31);
        ProjectId id = new ProjectId(UUID.randomUUID());
        ProjectTaskId taskId = new ProjectTaskId(UUID.randomUUID());
        ProjectTaskId taskId2 = new ProjectTaskId(UUID.randomUUID());
        Project project = Project.createNew(id, "test project", "testing", now, endDate, TimeEstimation.fromMinutes(60), contactPerson());
        project.addTask(taskId, "test", "test", TimeEstimation.fromMinutes(60));
        assertThrows(ProjectTimeEstimationWouldBeExceededException.class, () -> project.addTask(taskId2, "test", "test", TimeEstimation.fromMinutes(1)));
    }

    @Test
    void completesTask() {
        LocalDateTime now = LocalDateTime.of(2025, 6, 1, 18, 0, 0);
        LocalDate endDate = LocalDate.of(2026, 12, 31);
        ProjectId id = new ProjectId(UUID.randomUUID());
        ProjectTaskId taskId = new ProjectTaskId(UUID.randomUUID());
        ProjectTaskId taskId2 = new ProjectTaskId(UUID.randomUUID());
        Project project = Project.createNew(id, "test project", "testing", now, endDate, TimeEstimation.fromMinutes(61), contactPerson());
        project.addTask(taskId, "task name", "some description", TimeEstimation.fromMinutes(60));
        project.addTask(taskId2, "task name", "some desc", TimeEstimation.fromMinutes(1));
        project.completeTask(taskId, ActualSpentTime.fromMinutes(50));
        assertFalse(project.isCompleted());
    }

    @Test
    void completesProjectWhenAllTasksAreCompleted() {
        LocalDateTime now = LocalDateTime.of(2025, 6, 1, 18, 0, 0);
        LocalDate endDate = LocalDate.of(2026, 12, 31);
        ProjectId id = new ProjectId(UUID.randomUUID());
        ProjectTaskId taskId = new ProjectTaskId(UUID.randomUUID());
        Project project = Project.createNew(id, "test project", "testing", now, endDate, TimeEstimation.fromMinutes(61), contactPerson());
        project.addTask(taskId, "task name", "some description", TimeEstimation.fromMinutes(60));
        project.completeTask(taskId, ActualSpentTime.fromMinutes(50));
        assertTrue(project.isCompleted());
    }

    @Test
    void cannotAddTaskToCompletedProject() {
        LocalDateTime now = LocalDateTime.of(2025, 6, 1, 18, 0, 0);
        LocalDate endDate = LocalDate.of(2026, 12, 31);
        ProjectId id = new ProjectId(UUID.randomUUID());
        ProjectTaskId taskId = new ProjectTaskId(UUID.randomUUID());
        ProjectTaskId taskId2 = new ProjectTaskId(UUID.randomUUID());
        Project project = Project.createNew(id, "test project", "testing", now, endDate, TimeEstimation.fromMinutes(61), contactPerson());
        project.addTask(taskId, "task name", "some description", TimeEstimation.fromMinutes(60));
        project.completeTask(taskId, ActualSpentTime.fromMinutes(50));
        assertTrue(project.isCompleted());
        assertThrows(ProjectAlreadyCompletedException.class, () -> project.addTask(taskId2, "task", "desc", TimeEstimation.fromMinutes(1)));
    }

    private void verifyNoTaskFound(Project project, ProjectTaskId taskId) {
        assertTrue(project.getTask(taskId).isEmpty());
    }

    private ContactPerson contactPerson() {
        return ContactPerson.create("name", "some@gmail.com");
    }
}