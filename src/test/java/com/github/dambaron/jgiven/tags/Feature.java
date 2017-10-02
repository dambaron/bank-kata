package com.github.dambaron.jgiven.tags;

import com.tngtech.jgiven.annotation.IsTag;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@IsTag(name = "Features")
@Retention(RetentionPolicy.RUNTIME)
public @interface Feature {

}
