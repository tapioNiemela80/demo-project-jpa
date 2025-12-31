package tn.demo.jpa.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class DummyEmailClientService implements EmailClientService {

    private static final Logger log = LoggerFactory.getLogger(DummyEmailClientService.class);

    @Override
    public void send(EmailMessage message) {
        log.info(message.toString());
    }
}