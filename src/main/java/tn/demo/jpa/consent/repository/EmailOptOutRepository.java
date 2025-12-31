package tn.demo.jpa.consent.repository;

import org.springframework.data.repository.CrudRepository;
import tn.demo.jpa.consent.domain.EmailOptOut;

public interface EmailOptOutRepository extends CrudRepository<EmailOptOut, String> {
    boolean existsByEmail(String email);
}
