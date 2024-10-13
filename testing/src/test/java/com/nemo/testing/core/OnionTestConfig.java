package com.nemo.testing.core;

import com.tngtech.jgiven.integration.spring.EnableJGiven;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@EnableJGiven
@Configuration
@ComponentScan("com.nemo.testing.Onion")
public class OnionTestConfig {
}
