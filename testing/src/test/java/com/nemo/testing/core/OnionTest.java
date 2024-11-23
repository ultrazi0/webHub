package com.nemo.testing.core;

import com.tngtech.jgiven.annotation.IsTag;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.lang.annotation.*;

@IsTag(name = "Onion", description = "UI tests", style = "background-color: darkviolet; color: white")
@SpringBootTest(classes = OnionTestConfig.class)
@ActiveProfiles("Onion")
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface OnionTest {
}
