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

    public WithCleanup getCleanupService(Class<?> entityClass) {
        if (entityClass.equals(RobotEntity.class)) {
            return robotService;
        } else if (entityClass.equals(UserEntity.class)) {
            return userService;
        }
        throw new IllegalArgumentException("Unsupported entity class: " + entityClass);
    }

}
