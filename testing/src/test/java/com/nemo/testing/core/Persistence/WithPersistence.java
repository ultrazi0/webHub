package com.nemo.testing.core.Persistence;

import com.nemo.testing.core.Persistence.UniqueAttributes.AbstractUniqueAttributes;
import org.jooq.Record;

import java.util.Collection;

/**
 * Common interface for interactions with the database
 *
 * @param <T> entity type which repository/service handles
 * */
public interface WithPersistence<T> {

    T getEntityWith(AbstractUniqueAttributes uniqueAttributes);

    T createEntityFrom(Record record);

    T getFrom(Record record);

    void delete(Object entity);

    void deleteAll(Collection<T> entities);

}
