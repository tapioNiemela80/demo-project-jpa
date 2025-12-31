package tn.demo.jpa.common.infra.optimistic.locking;

import org.hibernate.HibernateException;
import org.hibernate.event.spi.PersistContext;
import org.hibernate.event.spi.PersistEvent;
import org.hibernate.event.spi.PersistEventListener;
import org.hibernate.LockMode;
import tn.demo.jpa.common.domain.RootAware;

public class RootAwarePersistEventListener implements PersistEventListener {

    public static final RootAwarePersistEventListener INSTANCE = new RootAwarePersistEventListener();

    @Override
    public void onPersist(PersistEvent event) {
        handle(event.getObject(), event);
    }

    @Override
    public void onPersist(PersistEvent persistEvent, PersistContext persistContext) throws HibernateException {
        handle(persistEvent.getObject(), persistEvent);
    }

    private void handle(Object entity, PersistEvent event) {
        if (entity instanceof RootAware<?> rootAware) {
            Object root = rootAware.root();
            if (root != null) {
                event.getSession().lock(root, LockMode.OPTIMISTIC_FORCE_INCREMENT);
            }
        }
    }
}