package com.nemo.testing.core.Persistence;

import com.nemo.rexus.Decibel.RobotEntity;
import com.nemo.rexus.Decibel.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Maps persistence services based on the entity type
 *
 * @see RobotService
 * @see UserService
 * */
@Service
@RequiredArgsConstructor
public class PersistenceServiceMapper {

    private final RobotService robotService;
    private final UserService userService;

    /**
     * Returns the persistence service for the specified entity type
     *
     * @param entityClass entity type
     * @return persistence service that is an implementation of {@link WithPersistence<T>} for provided entity type
     * @throws IllegalArgumentException if no service can be mapped to the provided type
     * */
    @SuppressWarnings("unchecked")
    public <T> WithPersistence<T> getPersistenceService(Class<T> entityClass) {
        if (RobotEntity.class.equals(entityClass)) {
            return (WithPersistence<T>) robotService;
        } else if (UserEntity.class.equals(entityClass)) {
            return (WithPersistence<T>) userService;
        }
        throw new IllegalArgumentException("Unsupported entity class: " + entityClass);
    }

}
