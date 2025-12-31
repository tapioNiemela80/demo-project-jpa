CREATE INDEX idx_project_tasks_project_id
    ON project_demo_jpa.project_tasks (project_id);

CREATE INDEX idx_team_tasks_project_task_id
    ON project_demo_jpa.team_tasks (project_task_id);

CREATE INDEX idx_team_tasks_team_id
    ON project_demo_jpa.team_tasks (team_id);

CREATE INDEX idx_team_members_team_id
    ON project_demo_jpa.team_members (team_id);