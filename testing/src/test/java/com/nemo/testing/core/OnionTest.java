package com.nemo.testing.core;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.lang.annotation.*;

@SpringBootTest(classes = OnionTestConfig.class)
@ActiveProfiles("Onion")
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface OnionTest {
}
