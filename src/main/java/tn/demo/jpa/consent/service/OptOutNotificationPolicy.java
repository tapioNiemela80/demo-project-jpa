package tn.demo.jpa.consent.service;

import tn.demo.jpa.common.domain.EmailAddress;
import tn.demo.jpa.consent.repository.EmailOptOutRepository;
import tn.demo.jpa.project.domain.EmailNotificationPolicy;

public class OptOutNotificationPolicy implements EmailNotificationPolicy {

    private final EmailOptOutRepository optOuts;

    public OptOutNotificationPolicy(EmailOptOutRepository optOuts) {
        this.optOuts = optOuts;
    }

    @Override
    public boolean notificationToEmailIsAllowed(EmailAddress emailAddress) {
        return !optOuts.existsByEmail(emailAddress.value());
    }
}
