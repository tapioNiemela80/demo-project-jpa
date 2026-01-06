package tn.demo.jpa.team.view;

import java.util.UUID;

public record TaskView(
        UUID id,
        String name,
        String description,
        UUID projectTaskId,
        String status,
        UUID assigneeId,
        ActualTimeSpent actualTimeSpent
) {
    public boolean isCompleted() {
        return actualTimeSpent != null;
    }
}