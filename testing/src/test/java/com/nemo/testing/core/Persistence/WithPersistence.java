package com.nemo.testing.core.Persistence;

import com.nemo.testing.core.Persistence.UniqueAttributes.AbstractUniqueAttributes;
import org.jooq.Record;

import java.util.Collection;

public interface WithPersistence<T> {

    T getEntityWith(AbstractUniqueAttributes uniqueAttributes);

    T createEntityFrom(Record record);

    T getFrom(Record record);

    void delete(Object entity);

    void deleteAll(Collection<T> entities);

}
