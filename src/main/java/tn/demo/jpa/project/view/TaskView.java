package tn.demo.jpa.project.view;

import java.util.UUID;

public record TaskView(UUID id, String title, String description, boolean isCompleted, TimeEstimate timeEstimate,
                       ActualTimeSpent actualTimeSpent) {
}