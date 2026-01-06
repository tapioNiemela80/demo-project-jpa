package tn.demo.jpa.project.view;

import java.util.UUID;

public record ProjectsView(UUID id, String name, String description) {
}