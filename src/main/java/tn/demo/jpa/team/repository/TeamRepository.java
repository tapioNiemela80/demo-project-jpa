package tn.demo.jpa.team.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import tn.demo.jpa.team.domain.Team;

import java.util.Optional;
import java.util.UUID;

public interface TeamRepository extends CrudRepository<Team, UUID> {

    @Query("""
            SELECT DISTINCT t
            FROM Team t
            JOIN t.tasks filterTask
            JOIN fetch t.tasks
            LEFT JOIN fetch t.members
            WHERE filterTask.projectTaskId = :projectTaskId
            """)
    Optional<Team> findByOriginalProjectTaskId(@Param("projectTaskId") UUID projectTaskId);

    @Query("""
            SELECT DISTINCT t
            FROM Team t
            JOIN t.tasks filterTask
            JOIN fetch t.tasks
            LEFT JOIN fetch t.members
            WHERE filterTask.id = :taskId
            """)
    Optional<Team> findByTaskId(@Param("taskId") UUID taskId);

    default Team add(Team team) {
        return save(team);
    }
}