package com.nemo.testing.core.Persistence;

import com.nemo.testing.core.Persistence.UniqueAttributes.AbstractUniqueAttributes;

import java.util.Collection;

public interface WithPersistence<T> {

    T getEntityWith(AbstractUniqueAttributes uniqueAttributes);

    void delete(Object entity);

    void deleteAll(Collection<T> entities);

}
