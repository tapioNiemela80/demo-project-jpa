package tn.demo.jpa.team.domain;

public class TaskTransitionNotAllowedException extends RuntimeException {
    public TaskTransitionNotAllowedException(String message) {
        super(message);
    }
}
