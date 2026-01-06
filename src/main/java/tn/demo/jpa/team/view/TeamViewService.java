package tn.demo.jpa.team.view;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TeamViewService {
    private final TeamViewRepository repository;

    public TeamViewService(TeamViewRepository repository) {
        this.repository = repository;
    }

    public List<TeamsView> findAll() {
        return repository.findTeams().stream()
                .map(data -> new TeamsView(data.teamId(), data.teamName()))
                .toList();
    }

    public Optional<TeamView> findById(UUID teamId) {
        var rows = repository.findTeamViewByTeamId(teamId);
        if (rows.isEmpty()) {
            return Optional.empty();
        }
        TeamViewRow first = rows.get(0);
        List<MemberView> members = getMembers(rows);
        List<TaskView> tasks = getTasks(rows);
        return Optional.of(new TeamView(
                first.teamId(),
                first.teamName(),
                members,
                tasks
        ));
    }

    private List<TaskView> getTasks(List<TeamViewRow> rows) {
        return rows.stream()
                .filter(row -> row.taskId() != null)
                .collect(Collectors.collectingAndThen(
                        Collectors.toMap(
                                TeamViewRow::taskId,
                                this::getTaskView,
                                (a, b) -> a
                        ),
                        map -> new ArrayList<>(map.values())
                ));
    }

    private TaskView getTaskView(TeamViewRow row) {
        return new TaskView(
                row.taskId(),
                row.taskName(),
                row.taskDescription(),
                row.projectTaskId(),
                row.taskStatus(),
                row.taskAssigneeId(),
                actualTimeSpent(row.actualTimeSpentHours(), row.actualTimeSpentMinutes())
        );
    }

    private List<MemberView> getMembers(List<TeamViewRow> rows) {
        return rows.stream()
                .filter(row -> row.memberId() != null)
                .collect(Collectors.collectingAndThen(
                        Collectors.toMap(
                                TeamViewRow::memberId,
                                this::getMemberView,
                                (a, b) -> a // ignore duplicates
                        ),
                        map -> new ArrayList<>(map.values())
                ));
    }

    private MemberView getMemberView(TeamViewRow row) {
        return new MemberView(
                row.memberId(),
                row.memberName(),
                row.memberProfession()
        );
    }

    private ActualTimeSpent actualTimeSpent(Integer actualTimeSpentHours, Integer actualTimeSpentMinutes) {
        if (actualTimeSpentHours == null) {
            return null;
        }
        return new ActualTimeSpent(actualTimeSpentHours, actualTimeSpentMinutes);
    }
}