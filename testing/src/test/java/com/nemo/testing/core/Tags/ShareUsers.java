package com.nemo.testing.core.Tags;

import com.tngtech.jgiven.annotation.IsTag;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@IsTag(name = "Share Users", style = "background-color: lightgreen; color: black")
@Retention(RetentionPolicy.RUNTIME)
public @interface ShareUsers {
}
