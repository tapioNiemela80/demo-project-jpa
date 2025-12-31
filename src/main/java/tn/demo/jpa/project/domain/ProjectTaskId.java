package tn.demo.jpa.project.domain;

import tn.demo.jpa.common.domain.ValueObject;

import java.util.Objects;
import java.util.UUID;

@ValueObject(description ="Represents id of project task")
public record ProjectTaskId(UUID value) {
    public ProjectTaskId {
        Objects.requireNonNull(value, "ProjectTaskId value cannot be null");
    }
}
