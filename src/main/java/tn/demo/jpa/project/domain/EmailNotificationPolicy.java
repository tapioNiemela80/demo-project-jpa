package tn.demo.jpa.project.domain;

import tn.demo.jpa.common.domain.EmailAddress;

public interface EmailNotificationPolicy {
    boolean notificationToEmailIsAllowed(EmailAddress emailAddress);
}
