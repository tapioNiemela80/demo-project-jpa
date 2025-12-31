package tn.demo.jpa.common.infra.optimistic.locking;

import org.hibernate.LockMode;
import org.hibernate.event.spi.PostUpdateEvent;
import org.hibernate.event.spi.PostUpdateEventListener;
import org.hibernate.persister.entity.EntityPersister;
import tn.demo.jpa.common.domain.RootAware;

public class RootAwareUpdateEventListener implements PostUpdateEventListener {

    public static final RootAwareUpdateEventListener INSTANCE = new RootAwareUpdateEventListener();

    @Override
    public void onPostUpdate(PostUpdateEvent event) {
        if (event.getEntity() instanceof RootAware<?> rootAware) {
            Object root = rootAware.root();
            if (root != null) {
                event.getSession().lock(root, LockMode.OPTIMISTIC_FORCE_INCREMENT);
            }
        }
    }

    @Override
    public boolean requiresPostCommitHandling(EntityPersister persister) {
        return false;
    }
}