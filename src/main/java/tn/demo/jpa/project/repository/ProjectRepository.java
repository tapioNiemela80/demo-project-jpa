package tn.demo.jpa.project.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import tn.demo.jpa.project.domain.Project;

import java.util.Optional;
import java.util.UUID;

public interface ProjectRepository extends CrudRepository<Project, UUID> {
    @Query("""
                SELECT DISTINCT p
                FROM Project p
                JOIN p.tasks filterTask
                JOIN FETCH p.tasks
                WHERE filterTask.id = :taskId
            """)
    Optional<Project> findByTaskId(@Param("taskId") UUID taskId);

    default Project add(Project project) {
        return save(project);
    }
}