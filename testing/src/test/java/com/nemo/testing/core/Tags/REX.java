package com.nemo.testing.core.Tags;

import com.tngtech.jgiven.annotation.IsTag;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@IsTag(prependType = true, description = "REX story", style = "background-color: deeppink; color: white")
@Retention(RetentionPolicy.RUNTIME)
public @interface REX {
    String value();
}
