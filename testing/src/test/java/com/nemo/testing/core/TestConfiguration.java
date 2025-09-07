package com.nemo.testing.core;

import com.nemo.rexus.Decibel.RobotRepository;
import com.nemo.rexus.Decibel.UserRepository;
import org.jooq.DSLContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

/// Configuration class that defines beans needed to run tests
@Configuration
public class TestConfiguration {

    @Bean
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection") // It should be created by automatic JOOQ configuration
    public UserRepository userRepository(DSLContext dslContext) {
        return new UserRepository(dslContext, passwordEncoder());
    }

    @Bean
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection") // It should be created by automatic JOOQ configuration
    public RobotRepository robotRepository(DSLContext dslContext) {
        return new RobotRepository(dslContext);
    }

    // Required by UserRepository
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

}
