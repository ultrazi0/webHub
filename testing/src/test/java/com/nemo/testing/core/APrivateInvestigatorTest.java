package com.nemo.testing.core;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.lang.annotation.*;

@SpringBootTest(classes = PrivateInvestigatorConfig.class)
@ActiveProfiles("PrivateInvestigator")
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface APrivateInvestigatorTest {
}
