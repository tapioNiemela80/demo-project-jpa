package tn.demo.jpa.common.infra.optimistic.locking;

import org.hibernate.boot.Metadata;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.service.spi.SessionFactoryServiceRegistry;

//inspired by https://vladmihalcea.com/how-to-increment-the-parent-entity-version-whenever-a-child-entity-gets-modified-with-jpa-and-hibernate/
public class RootAwareEventListenerIntegrator implements org.hibernate.integrator.spi.Integrator {

    @Override
    public void integrate(
            Metadata metadata,
            SessionFactoryImplementor sessionFactory,
            SessionFactoryServiceRegistry serviceRegistry) {

        EventListenerRegistry registry = serviceRegistry.getService(EventListenerRegistry.class);

        registry.getEventListenerGroup(EventType.PERSIST)
                .appendListener(RootAwarePersistEventListener.INSTANCE);

        registry.getEventListenerGroup(EventType.PERSIST_ONFLUSH)
                .appendListener(RootAwarePersistEventListener.INSTANCE);

        registry.getEventListenerGroup(EventType.POST_UPDATE)
                .appendListener(RootAwareUpdateEventListener.INSTANCE);

        registry.getEventListenerGroup(EventType.DELETE)
                .appendListener(RootAwareDeleteEventListener.INSTANCE);

    }

    @Override
    public void disintegrate(SessionFactoryImplementor sessionFactory, SessionFactoryServiceRegistry serviceRegistry) {
        //Do nothing
    }
}