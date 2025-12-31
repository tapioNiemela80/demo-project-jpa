package tn.demo.jpa.consent.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tn.demo.jpa.consent.repository.EmailOptOutRepository;
import tn.demo.jpa.consent.service.OptOutNotificationPolicy;
import tn.demo.jpa.project.domain.EmailNotificationPolicy;

@Configuration
public class ConsentConfiguration {
    @Bean
    public EmailNotificationPolicy emailNotificationPolicy(EmailOptOutRepository emailOptOuts){
        return new OptOutNotificationPolicy(emailOptOuts);
    }
}
