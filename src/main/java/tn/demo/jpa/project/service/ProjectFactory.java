package tn.demo.jpa.project.service;

import org.springframework.stereotype.Component;
import tn.demo.jpa.common.DateService;
import tn.demo.jpa.common.IDService;
import tn.demo.jpa.project.controller.ContactPersonInput;
import tn.demo.jpa.project.controller.TimeEstimation;
import tn.demo.jpa.project.domain.ContactPerson;
import tn.demo.jpa.project.domain.Project;

import java.time.LocalDate;

@Component
class ProjectFactory {

    private final IDService idService;
    private final DateService dateService;

    ProjectFactory(IDService idService, DateService dateService) {
        this.idService = idService;
        this.dateService = dateService;
    }

    Project createNew(String name, String description, LocalDate estimatedEndDate, TimeEstimation estimation, ContactPersonInput contactPerson){
        return Project.createNew(idService.newProjectId(), name, description, dateService.now(), estimatedEndDate, toDomain(estimation), toDomain(contactPerson));
    }

    private tn.demo.jpa.project.domain.TimeEstimation toDomain(TimeEstimation estimation) {
        return new tn.demo.jpa.project.domain.TimeEstimation(estimation.hours(), estimation.minutes());
    }

    private ContactPerson toDomain(ContactPersonInput contactPersonInput) {
        return ContactPerson.create(contactPersonInput.name(), contactPersonInput.email());
    }
}
