package com.nemo.testing.core.Tags;

import com.tngtech.jgiven.annotation.IsTag;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@IsTag(name = "Custom Commands Odyssey", style = "background-color: darkmagenta; color: white")
@Retention(RetentionPolicy.RUNTIME)
public @interface CustomCommandsOdyssey {
}
