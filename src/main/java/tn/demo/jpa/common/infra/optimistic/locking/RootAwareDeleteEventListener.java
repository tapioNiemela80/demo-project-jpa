package tn.demo.jpa.common.infra.optimistic.locking;

import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.event.spi.DeleteContext;
import org.hibernate.event.spi.DeleteEvent;
import org.hibernate.event.spi.DeleteEventListener;
import tn.demo.jpa.common.domain.RootAware;

public class RootAwareDeleteEventListener implements DeleteEventListener {

    public static final RootAwareDeleteEventListener INSTANCE = new RootAwareDeleteEventListener();

    @Override
    public void onDelete(DeleteEvent event) {
        handle(event.getObject(), event);
    }

    @Override
    public void onDelete(DeleteEvent deleteEvent, DeleteContext deleteContext) throws HibernateException {
        handle(deleteEvent.getObject(), deleteEvent);
    }

    private void handle(Object entity, DeleteEvent event) {
        if (entity instanceof RootAware<?> rootAware) {
            Object root = rootAware.root();
            if (root != null) {
                event.getSession().lock(root, LockMode.OPTIMISTIC_FORCE_INCREMENT);
            }
        }
    }
}
