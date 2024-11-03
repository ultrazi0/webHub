package com.nemo.testing.core;

import com.tngtech.jgiven.integration.spring.EnableJGiven;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@EnableJGiven
@Configuration
@ComponentScan({"com.nemo.testing.Onion", "com.nemo.testing.core"})
@Profile("Onion")
public class OnionTestConfig {
}
