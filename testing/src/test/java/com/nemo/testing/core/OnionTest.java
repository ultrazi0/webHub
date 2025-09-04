package com.nemo.testing.core;

import com.tngtech.jgiven.annotation.IsTag;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.lang.annotation.*;

/**
 * Annotation to indicate classes with UI tests
 * */
@IsTag(name = "Onion", description = "UI tests", style = "background-color: #1f0bcb; color: white")
@SpringBootTest(classes = OnionTestConfig.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableAutoConfiguration
@ActiveProfiles("Onion")
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Inherited
public @interface OnionTest {
}
