package tn.demo.jpa.project.domain;

public class UnknownProjectTaskIdException extends RuntimeException{
    private final ProjectTaskId taskId;

    public UnknownProjectTaskIdException(ProjectTaskId taskId) {
        super("Unknown task %s".formatted(taskId));
        this.taskId = taskId;
    }

    public ProjectTaskId getTaskId() {
        return taskId;
    }
}
