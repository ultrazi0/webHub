package com.nemo.testing.core;

import com.tngtech.jgiven.integration.spring.EnableJGiven;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@EnableJGiven
@Configuration
@ComponentScan({"com.nemo.testing.Onion", "com.nemo.testing.core"})
public class OnionTestConfig {

    @Bean
    public Logger logger() {
        return LoggerFactory.getLogger(OnionTestConfig.class);
    }
}
