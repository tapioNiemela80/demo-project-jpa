package tn.demo.jpa.team.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.demo.jpa.project.domain.ProjectTaskId;
import tn.demo.jpa.team.domain.TeamId;
import tn.demo.jpa.team.domain.TeamMemberId;
import tn.demo.jpa.team.domain.TeamTaskId;
import tn.demo.jpa.team.service.TeamService;
import tn.demo.jpa.team.view.TeamView;
import tn.demo.jpa.team.view.TeamViewService;
import tn.demo.jpa.team.view.TeamsView;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/teams")
public class TeamController {
    private final TeamService teamService;
    private final TeamViewService teamViewService;

    public TeamController(TeamService teamService, TeamViewService teamViewService) {
        this.teamService = teamService;
        this.teamViewService = teamViewService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Void> create(@RequestBody TeamInput teamInput) {
        UUID teamId = teamService.createNew(teamInput.name()).value();
        return createdPath("/teams/" + teamId);
    }

    @PostMapping("/{teamId}/members")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Void> addMember(@PathVariable UUID teamId, @RequestBody MemberInput memberInput) {
        UUID memberId = teamService.addMember(new TeamId(teamId), memberInput.name(), memberInput.profession()).value();
        return createdPath("/teams/" + teamId + "/members/" + memberId);
    }

    @PostMapping("/{teamId}/tasks/by-project-id/{projectTaskId}")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Void> addTask(@PathVariable UUID teamId, @PathVariable UUID projectTaskId) {
        UUID taskId = teamService.addTask(new TeamId(teamId), new ProjectTaskId(projectTaskId)).value();
        return createdPath("/teams/" + teamId + "/tasks/" + taskId);
    }

    @PatchMapping("/{teamId}/tasks/{taskId}/assignee")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> assignTask(@PathVariable UUID teamId, @PathVariable UUID taskId, @RequestBody AssignTaskInput assignTaskInput) {
        teamService.assignTask(new TeamId(teamId), new TeamTaskId(taskId), new TeamMemberId(assignTaskInput.assigneeId()));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{teamId}/tasks/{taskId}/mark-in-progress")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> markTaskInProgress(@PathVariable UUID teamId, @PathVariable UUID taskId) {
        teamService.markTaskInProgress(new TeamId(teamId), new TeamTaskId(taskId));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{teamId}/tasks/{taskId}/unassign")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> markTaskUnAssigned(@PathVariable UUID teamId, @PathVariable UUID taskId) {
        teamService.unassignTask(new TeamId(teamId), new TeamTaskId(taskId));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{teamId}/tasks/{taskId}/complete")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> markTaskCompleted(@PathVariable UUID teamId, @PathVariable UUID taskId, @RequestBody ActualSpentTime actualSpentTime) {
        teamService.completeTask(new TeamId(teamId), new TeamTaskId(taskId), actualSpentTime);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{teamId}/tasks/{taskId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> removeTask(@PathVariable UUID teamId, @PathVariable UUID taskId) {
        teamService.removeTask(new TeamId(teamId), new TeamTaskId(taskId));
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{teamId}/members/{memberId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> removeMember(@PathVariable UUID teamId, @PathVariable UUID memberId) {
        teamService.removeMember(new TeamId(teamId), new TeamMemberId(memberId));
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<TeamsView>> findAll() {
        return ResponseEntity.ok(teamViewService.findAll());
    }

    @GetMapping("/{teamId}")
    public ResponseEntity<TeamView> findById(@PathVariable UUID teamId) {
        return teamViewService.findById(teamId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    private ResponseEntity<Void> createdPath(String path) {
        return ResponseEntity.created(URI.create(path)).build();
    }

}
