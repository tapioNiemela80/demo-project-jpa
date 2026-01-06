package tn.demo.jpa.common.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.Tuple;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

@Component
public class EntityManagerUtils {
    private final EntityManager entityManager;

    public EntityManagerUtils(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public <T> List<T> find(String sql, Map<String, Object> params, Function<EntityRecord, ? extends T> mapper) {
        Query nativeQuery = entityManager.createNativeQuery(sql, Tuple.class);
        params.forEach(setParam(nativeQuery));
        List<Tuple> result = nativeQuery.getResultList();
        List<EntityRecord> records = result.stream()
                .map(EntityRecord::new)
                .toList();
        return (List<T>) records.stream().map(mapper).toList();
    }

    private BiConsumer<String, Object> setParam(Query nativeQuery) {
        return (s, o) -> nativeQuery.setParameter(s, o);
    }
}