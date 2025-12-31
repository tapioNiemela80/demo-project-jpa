package tn.demo.jpa.team.domain;

public class TaskAlreadyAssignedException extends RuntimeException{
    public TaskAlreadyAssignedException(String message) {
        super(message);
    }
}
