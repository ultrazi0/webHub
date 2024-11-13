package com.nemo.testing.core.Persistence;

import java.util.Collection;

public interface WithCleanup {

    void delete(Object entity);

    <T> void deleteAll(Collection<T> entities);

}
