package tn.demo.jpa.team.domain;

import org.junit.jupiter.api.Test;
import tn.demo.jpa.common.domain.ActualSpentTime;
import tn.demo.jpa.project.domain.ProjectTaskId;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static tn.demo.jpa.team.domain.TeamTaskStatus.*;

class TeamTest {
    @Test
    void addsMember() {
        Team team = Team.createNew(getTeamId(), "project team");
        TeamMemberId teamMemberId = getMemberId();
        assertFalse(team.containsMember(teamMemberId, "John doe", "tester"));
        team.addMember(teamMemberId, "John doe", "tester");
        assertTrue(team.containsMember(teamMemberId, "John doe", "tester"));
    }

    @Test
    void removesMember() {
        Team team = Team.createNew(getTeamId(), "project team");
        TeamMemberId teamMemberId = getMemberId();
        team.addMember(teamMemberId, "John doe", "tester");
        assertTrue(team.containsMember(teamMemberId, "John doe", "tester"));
        team.removeMember(teamMemberId);
        assertFalse(team.containsMember(teamMemberId, "John doe", "tester"));
    }

    @Test
    void throwsExceptionWhenTryingToRemoveUnknownMember() {
        Team team = Team.createNew(getTeamId(), "project team");
        TeamMemberId teamMemberId = getMemberId();
        assertThrows(UnknownTeamMemberIdException.class, () -> team.removeMember(teamMemberId));
    }

    @Test
    void addsTask() {
        Team team = Team.createNew(getTeamId(), "project team");
        TeamTaskId taskId = getTaskId();
        ProjectTaskId projectTaskId = getProjectTaskId();
        assertFalse(team.containsUncompletedTask(taskId, projectTaskId, "test", "robot framework", null, NOT_ASSIGNED));
        team.addTask(taskId, projectTaskId, "test", "robot framework");
        assertTrue(team.containsUncompletedTask(taskId, projectTaskId, "test", "robot framework", null, NOT_ASSIGNED));

    }

    @Test
    void givesOriginalProjectTaskId() {
        Team team = Team.createNew(getTeamId(), "project team");
        TeamTaskId taskId = getTaskId();
        ProjectTaskId projectTaskId = getProjectTaskId();
        assertTrue(team.getOriginalTaskId(taskId).isEmpty());
        team.addTask(taskId, projectTaskId, "test", "robot framework");
        assertEquals(projectTaskId, team.getOriginalTaskId(taskId).get());
    }

    @Test
    void removesTask() {
        Team team = Team.createNew(getTeamId(), "project team");
        TeamTaskId taskId = getTaskId();
        TeamTaskId taskId2 = getTaskId();
        ProjectTaskId projectTaskId = getProjectTaskId();
        ProjectTaskId projectTaskId2 = getProjectTaskId();
        team.addTask(taskId, projectTaskId, "test", "robot framework");
        team.addTask(taskId2, projectTaskId2, "test", "unit test");
        assertTrue(team.containsUncompletedTask(taskId, projectTaskId, "test", "robot framework", null, NOT_ASSIGNED));
        assertTrue(team.containsUncompletedTask(taskId2, projectTaskId2, "test", "unit test", null, NOT_ASSIGNED));
        team.removeTask(taskId);
        assertFalse(team.containsUncompletedTask(taskId, projectTaskId, "test", "robot framework", null, NOT_ASSIGNED));
        assertTrue(team.containsUncompletedTask(taskId2, projectTaskId2, "test", "unit test", null, NOT_ASSIGNED));
    }

    @Test
    void throwsExceptionWhenTryingToRemoveNonExistingTask() {
        Team team = Team.createNew(getTeamId(), "project team");
        TeamTaskId taskId = getTaskId();
        assertThrows(UnknownTeamTaskIdException.class, () -> team.removeTask(taskId));
    }

    @Test
    void throwsExceptionWhenTryingToRemoveTaskWhichCannotBeDeleted() {
        Team team = Team.createNew(getTeamId(), "project team");
        TeamTaskId taskId = getTaskId();
        TeamTaskId taskId2 = getTaskId();
        TeamMemberId memberId = getMemberId();
        ProjectTaskId projectTaskId = getProjectTaskId();
        ProjectTaskId projectTaskId2 = getProjectTaskId();
        team.addTask(taskId, projectTaskId, "test", "robot framework");
        team.addMember(memberId, "john doe", "tester");
        team.addTask(taskId2, projectTaskId2, "test", "unit test");
        assertTrue(team.containsUncompletedTask(taskId, projectTaskId, "test", "robot framework", null, NOT_ASSIGNED));
        assertTrue(team.containsUncompletedTask(taskId2, projectTaskId2, "test", "unit test", null, NOT_ASSIGNED));
        team.assignTask(taskId, memberId);
        assertThrows(TaskCannotBeDeletedException.class, () -> team.removeTask(taskId));

        assertTrue(team.containsUncompletedTask(taskId, projectTaskId, "test", "robot framework", memberId, ASSIGNED));
        assertTrue(team.containsUncompletedTask(taskId2, projectTaskId2, "test", "unit test", null, NOT_ASSIGNED));
    }

    @Test
    void assignsTask() {
        Team team = Team.createNew(getTeamId(), "project team");
        TeamTaskId taskId = getTaskId();
        TeamMemberId memberId = getMemberId();
        ProjectTaskId projectTaskId = getProjectTaskId();
        team.addTask(taskId, projectTaskId, "test", "robot framework");
        team.addMember(memberId, "john doe", "tester");
        team.assignTask(taskId, memberId);
        assertTrue(team.containsUncompletedTask(taskId, projectTaskId, "test", "robot framework", memberId, ASSIGNED));
    }

    @Test
    void throwsExceptionWhenTryingToRemoveMemberWhoHasAssignedTask() {
        Team team = Team.createNew(getTeamId(), "project team");
        TeamTaskId taskId = getTaskId();
        TeamTaskId taskId2 = getTaskId();
        TeamMemberId memberId = getMemberId();
        ProjectTaskId projectTaskId = getProjectTaskId();
        ProjectTaskId projectTaskId2 = getProjectTaskId();
        team.addTask(taskId, projectTaskId, "test", "robot framework");
        team.addTask(taskId2, projectTaskId2, "write code", "java code");
        team.addMember(memberId, "john doe", "tester");
        team.assignTask(taskId, memberId);
        assertTrue(team.containsUncompletedTask(taskId, projectTaskId, "test", "robot framework", memberId, ASSIGNED));
        assertThrows(TeamMemberHasAssignedTasksException.class, () -> team.removeMember(memberId));
    }

    @Test
    void throwsExceptionWhileAssigningTaskBecauseUnknownMember() {
        Team team = Team.createNew(getTeamId(), "project team");
        TeamTaskId taskId = getTaskId();
        TeamMemberId memberId = getMemberId();
        ProjectTaskId projectTaskId = getProjectTaskId();
        team.addTask(taskId, projectTaskId, "test", "robot framework");
        assertThrows(UnknownTeamMemberIdException.class, () -> team.assignTask(taskId, memberId));
    }

    @Test
    void throwsExceptionWhileAssigningTaskBecauseUnknownTask() {
        Team team = Team.createNew(getTeamId(), "project team");
        TeamTaskId taskId = getTaskId();
        TeamMemberId memberId = getMemberId();
        assertThrows(UnknownTeamTaskIdException.class, () -> team.assignTask(taskId, memberId));
    }

    @Test
    void throwsExceptionWhileAssigningTaskBecauseTaskAlreadyAssigned() {
        Team team = Team.createNew(getTeamId(), "project team");
        TeamTaskId taskId = getTaskId();
        TeamMemberId memberId = getMemberId();
        ProjectTaskId projectTaskId = getProjectTaskId();
        team.addTask(taskId, projectTaskId, "test", "robot framework");
        team.addMember(memberId, "john doe", "tester");
        team.assignTask(taskId, memberId);
        assertTrue(team.containsUncompletedTask(taskId, projectTaskId, "test", "robot framework", memberId, ASSIGNED));
        assertThrows(TaskTransitionNotAllowedException.class, () -> team.assignTask(taskId, memberId));
    }

    @Test
    void marksTaskInProgress() {
        Team team = Team.createNew(getTeamId(), "project team");
        TeamTaskId taskId = getTaskId();
        TeamMemberId memberId = getMemberId();
        ProjectTaskId projectTaskId = getProjectTaskId();
        team.addTask(taskId, projectTaskId, "test", "robot framework");
        team.addMember(memberId, "john doe", "tester");
        team.assignTask(taskId, memberId);
        assertTrue(team.containsUncompletedTask(taskId, projectTaskId, "test", "robot framework", memberId, ASSIGNED));
        team.markTaskInProgress(taskId);
        assertTrue(team.containsUncompletedTask(taskId, projectTaskId, "test", "robot framework", memberId, IN_PROGRESS));
    }

    @Test
    void throwsExceptionWhenTryingToMarkNotAssignedTaskToInProgress() {
        Team team = Team.createNew(getTeamId(), "project team");
        TeamTaskId taskId = getTaskId();
        TeamMemberId memberId = getMemberId();
        ProjectTaskId projectTaskId = getProjectTaskId();
        team.addTask(taskId, projectTaskId, "test", "robot framework");
        team.addMember(memberId, "john doe", "tester");
        assertThrows(TaskTransitionNotAllowedException.class, () -> team.markTaskInProgress(taskId));
    }

    @Test
    void throwsExceptionWhenTryingToMarkNonExistingTaskToInProgress() {
        Team team = Team.createNew(getTeamId(), "project team");
        TeamTaskId taskId = getTaskId();
        assertThrows(UnknownTeamTaskIdException.class, () -> team.markTaskInProgress(taskId));
    }

    @Test
    void marksTaskCompleted() {
        Team team = Team.createNew(getTeamId(), "project team");
        TeamTaskId taskId = getTaskId();
        TeamMemberId memberId = getMemberId();
        ProjectTaskId projectTaskId = getProjectTaskId();
        team.addTask(taskId, projectTaskId, "test", "robot framework");
        team.addMember(memberId, "john doe", "tester");
        team.assignTask(taskId, memberId);
        team.markTaskInProgress(taskId);
        team.markTaskCompleted(taskId, ActualSpentTime.fromMinutes(100));
        assertTrue(team.containsCompletedTask(taskId, projectTaskId, "test", "robot framework", ActualSpentTime.fromMinutes(100)));
    }

    @Test
    void throwsExceptionWhenTryingToMarkNotAssignedTaskCompleted() {
        Team team = Team.createNew(getTeamId(), "project team");
        TeamTaskId taskId = getTaskId();
        TeamMemberId memberId = getMemberId();
        ProjectTaskId projectTaskId = getProjectTaskId();
        team.addTask(taskId, projectTaskId, "test", "robot framework");
        team.addMember(memberId, "john doe", "tester");
        assertThrows(TaskTransitionNotAllowedException.class, () -> team.markTaskCompleted(taskId, ActualSpentTime.fromMinutes(10)));

        assertTrue(team.containsUncompletedTask(taskId, projectTaskId, "test", "robot framework", null, NOT_ASSIGNED));
    }

    @Test
    void throwsExceptionWhenTryingToMarkNonExistingTaskCompleted() {
        Team team = Team.createNew(getTeamId(), "project team");
        TeamTaskId taskId = getTaskId();
        assertThrows(UnknownTeamTaskIdException.class, () -> team.markTaskCompleted(taskId, ActualSpentTime.fromMinutes(10)));
    }

    @Test
    void unassignsTask() {
        Team team = Team.createNew(getTeamId(), "project team");
        TeamTaskId taskId = getTaskId();
        TeamMemberId memberId = getMemberId();
        ProjectTaskId projectTaskId = getProjectTaskId();
        team.addTask(taskId, projectTaskId, "test", "robot framework");
        team.addMember(memberId, "john doe", "tester");
        team.assignTask(taskId, memberId);
        team.markTaskUnassigned(taskId);
        assertTrue(team.containsUncompletedTask(taskId, projectTaskId, "test", "robot framework", null, NOT_ASSIGNED));
    }

    @Test
    void throwsExceptionWhenTryingToUnassignUnassignableTask() {
        Team team = Team.createNew(getTeamId(), "project team");
        TeamTaskId taskId = getTaskId();
        TeamMemberId memberId = getMemberId();
        ProjectTaskId projectTaskId = getProjectTaskId();
        team.addTask(taskId, projectTaskId, "test", "robot framework");
        team.addMember(memberId, "john doe", "tester");
        team.assignTask(taskId, memberId);
        team.markTaskUnassigned(taskId);
        assertTrue(team.containsUncompletedTask(taskId, projectTaskId, "test", "robot framework", null, NOT_ASSIGNED));
        assertThrows(TaskTransitionNotAllowedException.class, () -> team.markTaskUnassigned(taskId));
    }

    @Test
    void throwsExceptionWhenTryingToMarkNonExistingTaskUnAssigned() {
        Team team = Team.createNew(getTeamId(), "project team");
        TeamTaskId taskId = getTaskId();
        assertThrows(UnknownTeamTaskIdException.class, () -> team.markTaskUnassigned(taskId));
    }

    private TeamId getTeamId() {
        return new TeamId(UUID.randomUUID());
    }

    private TeamTaskId getTaskId() {
        return new TeamTaskId(UUID.randomUUID());
    }

    private ProjectTaskId getProjectTaskId() {
        return new ProjectTaskId(UUID.randomUUID());
    }

    private TeamMemberId getMemberId() {
        return new TeamMemberId(UUID.randomUUID());
    }
}