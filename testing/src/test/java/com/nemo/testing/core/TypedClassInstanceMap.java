package com.nemo.testing.core;

import java.util.*;
import java.util.function.BiConsumer;

public class TypedClassInstanceMap {

    private final Map<Class<?>, Set<?>> map = new HashMap<>();

    @SuppressWarnings("unchecked")
    public <T> void addInstance(final Class<T> cls, final T instance) {
        Set<T> instances = (Set<T>) map.computeIfAbsent(cls, k -> new HashSet<>());
        instances.add(instance);
    }

    @SuppressWarnings("unchecked")
    public <T> void addInstance(final T instance) {
        Class<T> cls = (Class<T>) instance.getClass();
        addInstance(cls, instance);
    }

    @SuppressWarnings("unchecked")
    public <T> void addInstances(final Class<T> cls, final Collection<T> instances) {
        Set<T> instancesInMap = (Set<T>) map.computeIfAbsent(cls, k -> new HashSet<>(instances.size()));
        instancesInMap.addAll(instances);
    }

    @SuppressWarnings("unchecked")
    public <T> Set<T> getInstances(final Class<T> cls) {
        return (Set<T>) map.getOrDefault(cls, Collections.emptySet());
    }

    public void forEach(final BiConsumer<? super Class<?>, ? super Set<?>> action) {
        map.forEach(action);
    }
}
