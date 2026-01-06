package tn.demo.jpa.project.view;

import org.springframework.stereotype.Component;
import tn.demo.jpa.common.service.EntityManagerUtils;
import tn.demo.jpa.common.service.EntityRecord;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@Component
class ProjectViewRepository {
    private final EntityManagerUtils entityManagerUtils;

    ProjectViewRepository(EntityManagerUtils entityManagerUtils) {
        this.entityManagerUtils = entityManagerUtils;
    }

    List<ProjectTaskRow> findProjectWithTasks(UUID projectId) {
        String sql = """
                    SELECT
                    p.id AS id,
                    p.name AS name,
                    p.description AS description,
                    p.initial_estimated_time_hours as project_estimate_hours,
                    p.initial_estimated_time_minutes as project_estimate_minutes,
                    p.status as project_status,
                    pt.id AS task_id,
                    pt.title AS task_title,
                    pt.description AS task_description,
                    pt.task_status as task_status,
                    pt.estimated_time_hours as task_estimate_hours,
                    pt.estimated_time_minutes as task_estimate_minutes,
                    pt.actual_time_spent_hours as actual_hours,
                    pt.actual_time_spent_minutes as actual_minutes
                    FROM project_demo_jpa.projects p
                    LEFT JOIN project_demo_jpa.project_tasks pt ON p.id = pt.project_id
                    WHERE p.id = :projectId
                """;
        return entityManagerUtils.find(sql, Map.of("projectId", projectId), singleProjectMapper());
    }

    private Function<EntityRecord, ProjectTaskRow> singleProjectMapper() {
        return r -> {
            UUID id = r.getUUID("id");
            String name = r.getString("name");
            String desc = r.getString("description");
            Integer projectEstHours = r.getInteger("project_estimate_hours");
            Integer projectEstMinutes = r.getInteger("project_estimate_minutes");
            String status = r.getString("project_status");
            UUID taskId = r.getUUID("task_id");
            String taskTitle = r.getString("task_title");
            String taskDescription = r.getString("task_description");
            String taskStatus = r.getString("task_status");
            Integer taskEstHours = r.getInteger("task_estimate_hours");
            Integer taskEstMinutes = r.getInteger("task_estimate_minutes");
            Integer actualHours = r.getInteger("actual_hours");
            Integer actualMinutes = r.getInteger("actual_minutes");
            return new ProjectTaskRow(id, name, desc, projectEstHours,
                    projectEstMinutes, status, taskId,
                    taskTitle, taskDescription, taskStatus,
                    taskEstHours, taskEstMinutes, actualHours, actualMinutes);
        };
    }

    List<ProjectsViewRow> findAll() {
        String sql = """
                SELECT p.id,
                p.name,
                p.description
                FROM project_demo_jpa.projects p
                """;
        return entityManagerUtils.find(sql, Map.of(), projectsMapper());
    }

    private Function<EntityRecord, ProjectsViewRow> projectsMapper() {
        return r -> new ProjectsViewRow(r.getUUID("id"), r.getString("name"), r.getString("description"));
    }
}