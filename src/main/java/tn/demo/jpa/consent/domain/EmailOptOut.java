package tn.demo.jpa.consent.domain;

import tn.demo.jpa.common.domain.EmailAddress;

import java.time.LocalDateTime;
import java.util.Objects;
import jakarta.persistence.*;

@Entity
@Table(name = "email_opt_outs")
public class EmailOptOut {
    @Id
    private String email;

    private LocalDateTime optedOutAt;

    protected EmailOptOut() {
        //for jpa
    }

    public EmailOptOut(EmailAddress emailAddress, LocalDateTime optedOutAt) {
        this.email = emailAddress.value();
        this.optedOutAt = optedOutAt;
    }

    public static EmailOptOut optOut(EmailAddress emailAddress, LocalDateTime when) {
        return new EmailOptOut(emailAddress, when);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EmailOptOut other = (EmailOptOut) o;
        return Objects.equals(email, other.email);
    }

    @Override
    public int hashCode() {
        return email.hashCode();
    }
}
