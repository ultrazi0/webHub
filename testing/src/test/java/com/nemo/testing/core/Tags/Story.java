package com.nemo.testing.core.Tags;

import com.tngtech.jgiven.annotation.IsTag;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@IsTag(description = "This is a story", style = "background-color: purple; color: white")
@Retention(RetentionPolicy.RUNTIME)
public @interface Story {
    String value();
}
