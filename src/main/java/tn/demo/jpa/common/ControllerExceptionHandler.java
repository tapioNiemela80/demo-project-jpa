package tn.demo.jpa.common;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import tn.demo.jpa.common.domain.EmailNotValidException;
import tn.demo.jpa.project.domain.ProjectAlreadyCompletedException;
import tn.demo.jpa.project.domain.ProjectTimeEstimationWouldBeExceededException;
import tn.demo.jpa.project.domain.UnknownProjectIdException;
import tn.demo.jpa.project.domain.UnknownProjectTaskIdException;
import tn.demo.jpa.team.domain.*;

@ControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(UnknownProjectIdException.class)
    public ResponseEntity<String> handleUnknownProjectIdException(UnknownProjectIdException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ex.getMessage());
    }

    @ExceptionHandler(ProjectTimeEstimationWouldBeExceededException.class)
    public ResponseEntity<String> handleProjectTimeEstimationWouldBeExceededException(ProjectTimeEstimationWouldBeExceededException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
    }

    @ExceptionHandler(UnknownProjectTaskIdException.class)
    public ResponseEntity<String> handleUnknownProjectTaskIdException(UnknownProjectTaskIdException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
    }

    @ExceptionHandler(ProjectAlreadyCompletedException.class)
    public ResponseEntity<String> handleProjectAlreadyCompletedException(ProjectAlreadyCompletedException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
    }


    @ExceptionHandler(TaskTransitionNotAllowedException.class)
    public ResponseEntity<String> handleTaskNotInProgressException(TaskTransitionNotAllowedException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
    }

    @ExceptionHandler(TaskCannotBeDeletedException.class)
    public ResponseEntity<String> handleTaskCannotBeDeletedException(TaskCannotBeDeletedException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
    }

    @ExceptionHandler(UnknownTeamIdException.class)
    public ResponseEntity<String> handleUnknownTeamIdException(UnknownTeamIdException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ex.getMessage());
    }

    @ExceptionHandler(UnknownTeamMemberIdException.class)
    public ResponseEntity<String> handleUnknownTeamMemberIdException(UnknownTeamMemberIdException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ex.getMessage());
    }

    @ExceptionHandler(UnknownTeamTaskIdException.class)
    public ResponseEntity<String> handleUnknownTeamTaskIdException(UnknownTeamTaskIdException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ex.getMessage());
    }

    @ExceptionHandler(TaskAlreadyAssignedException.class)
    public ResponseEntity<String> handleTaskAlreadyAssignedException(TaskAlreadyAssignedException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ex.getMessage());
    }

    @ExceptionHandler(TeamMemberHasAssignedTasksException.class)
    public ResponseEntity<String> handleTeamMemberHasAssignedTasksException(TeamMemberHasAssignedTasksException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
    }


    @ExceptionHandler(EmailNotValidException.class)
    public ResponseEntity<String> handleEmailNotValidException(EmailNotValidException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
    }

}
