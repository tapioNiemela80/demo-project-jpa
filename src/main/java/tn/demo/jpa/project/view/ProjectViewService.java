package tn.demo.jpa.project.view;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProjectViewService {
    private final ProjectViewRepository projectViewRepository;

    public ProjectViewService(ProjectViewRepository projectViewRepository) {
        this.projectViewRepository = projectViewRepository;
    }

    public List<ProjectsView> findAll(){
        return projectViewRepository.findAll()
                .stream()
                .map(data -> new ProjectsView(data.id(), data.name(), data.description()))
                .toList();
    }

    public Optional<ProjectView> findOne(UUID projectId) {
        var rows = projectViewRepository.findProjectWithTasks(projectId);
        if(rows.isEmpty()){
            return Optional.empty();
        }

        ProjectTaskRow first = rows.get(0);
        List<TaskView> tasks = getTasks(rows);

        return Optional.of(new ProjectView(
                first.id(),
                first.name(),
                first.description(),
                first.projectStatus(),
                new TimeEstimate(first.projectEstimateHours(), first.projectEstimateMinutes()),
                tasks
        ));
    }

    private List<TaskView> getTasks(List<ProjectTaskRow> rows) {
        return rows.stream().
                map(this::toTask)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }
    private Optional<TaskView> toTask(ProjectTaskRow row) {
        if (row.taskId() == null){
            return Optional.empty();
        }
        return Optional.of(new TaskView(row.taskId(), row.taskTitle(), row.taskDescription(), row.taskStatus().equals("COMPLETE"), estimation(row), timeSpent(row)));
    }

    private ActualTimeSpent timeSpent(ProjectTaskRow row) {
        if(row.actualHours() == null){
            return null;
        }
        return new ActualTimeSpent(row.actualHours(), row.actualMinutes());
    }
    private TimeEstimate estimation(ProjectTaskRow row) {
        return new TimeEstimate(row.taskEstimateHours(), row.taskEstimateMinutes());
    }

}