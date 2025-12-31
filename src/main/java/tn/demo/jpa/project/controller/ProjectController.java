package tn.demo.jpa.project.controller;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.demo.jpa.project.domain.ProjectId;
import tn.demo.jpa.project.service.ProjectService;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/projects")
public class ProjectController {
    private final ProjectService service;

    public ProjectController(ProjectService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Void> create(@RequestBody ProjectInput projectInput) {
        UUID projectId = service.createProject(projectInput.name(), projectInput.description(), projectInput.estimatedEndDate(), projectInput.estimation(), projectInput.contactPersonInput()).value();
        return createdPath("/projects/" + projectId);
    }

    @PostMapping("/{id}/tasks")
    public ResponseEntity<Void> addTask(@PathVariable UUID id, @RequestBody TaskInput taskInput) {
        UUID taskId = service.addTaskTo(new ProjectId(id), taskInput.name(), taskInput.description(), taskInput.estimation()).value();
        return createdPath("/projects/" + id + "/tasks/" + taskId);
    }

    private ResponseEntity<Void> createdPath(String path) {
        return ResponseEntity.created(URI.create(path)).build();
    }

}