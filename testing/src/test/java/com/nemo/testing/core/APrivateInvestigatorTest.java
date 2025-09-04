package com.nemo.testing.core;

import com.tngtech.jgiven.annotation.IsTag;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.lang.annotation.*;

/**
 * Annotation to indicate classes with API tests
 * */
@IsTag(name = "API", description = "API tests", style = "background-color: green; color: white")
@SpringBootTest(classes = PrivateInvestigatorConfig.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableAutoConfiguration
@ActiveProfiles("PrivateInvestigator")
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Inherited
public @interface APrivateInvestigatorTest {
}
