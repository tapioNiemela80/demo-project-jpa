package tn.demo.jpa.team.view;

import org.springframework.stereotype.Component;
import tn.demo.jpa.common.service.EntityManagerUtils;
import tn.demo.jpa.common.service.EntityRecord;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@Component
class TeamViewRepository {
    private final EntityManagerUtils entityManagerUtils;
    public TeamViewRepository(EntityManagerUtils entityManagerUtils) {
        this.entityManagerUtils = entityManagerUtils;
    }

    List<TeamViewRow> findTeamViewByTeamId(UUID teamId) {
        String sql =     """
        SELECT
            t.id AS team_id,
            t.name AS team_name,
            m.id AS member_id,
            m.name AS member_name,
            m.profession AS member_profession,
            tt.id AS task_id,
            tt.name AS task_name,
            tt.description AS task_description,
            tt.project_task_id AS project_task_id,
            tt.status AS task_status,
            tt.assignee_id AS task_assignee_id,
            tt.actual_time_spent_hours AS actual_time_spent_hours,
            tt.actual_time_spent_minutes AS actual_time_spent_minutes
        FROM project_demo_jpa.teams t
        LEFT JOIN project_demo_jpa.team_members m ON t.id = m.team_id
        LEFT JOIN project_demo_jpa.team_tasks tt ON t.id = tt.team_id
        WHERE t.id = :teamId
    """;
        return entityManagerUtils.find(sql, Map.of("teamId", teamId), teamViewRowMapper());
    }

    private Function<EntityRecord, TeamViewRow> teamViewRowMapper() {
        return r -> {
            UUID id = r.getUUID("team_id");
            String name = r.getString("team_name");
            UUID memberId = r.getUUID("member_id");
            String memberName = r.getString("member_name");
            String profession = r.getString("member_profession");
            UUID taskId = r.getUUID("task_id");
            String taskName = r.getString("task_name");
            String description = r.getString("task_description");
            UUID projectTaskId = r.getUUID("project_task_id");
            String status = r.getString("task_status");
            UUID assignee = r.getUUID("task_assignee_id");
            Integer spentHours = r.getInteger("actual_time_spent_hours");
            Integer spentMinutes = r.getInteger("actual_time_spent_minutes");
            return new TeamViewRow(id, name, memberId,
                    memberName, profession, taskId,
                    taskName, description, projectTaskId,
                    status, assignee, spentHours, spentMinutes);
        };
    }

    List<TeamsViewRow> findTeams(){
        String sql =     """
            SELECT
            t.id AS team_id,
            t.name AS team_name
        FROM project_demo_jpa.teams t
    """;
        return entityManagerUtils.find(sql, Map.of(), teamsViewRowMapper());
    }

    private Function<EntityRecord, TeamsViewRow> teamsViewRowMapper() {
        return r -> new TeamsViewRow(r.getUUID("team_id"), r.getString("team_name"));
    }
}