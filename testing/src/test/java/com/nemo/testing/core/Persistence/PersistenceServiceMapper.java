package com.nemo.testing.core.Persistence;

import com.nemo.webHub.Decibel.RobotEntity;
import com.nemo.webHub.Decibel.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PersistenceServiceMapper {

    private final RobotService robotService;
    private final UserService userService;

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
