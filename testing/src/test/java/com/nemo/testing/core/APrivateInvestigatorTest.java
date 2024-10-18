package com.nemo.testing.core;

import org.springframework.boot.test.context.SpringBootTest;

import java.lang.annotation.*;

@SpringBootTest(classes = PrivateInvestigatorConfig.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface APrivateInvestigatorTest {
}
