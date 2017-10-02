package com.github.dambaron.jgiven.tags;

import com.tngtech.jgiven.annotation.IsTag;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Feature
@IsTag(name = "History",
        description = "<strong>In order to</strong> check my operations</br>" +
                "<strong>As a</strong> bank client</br>" +
                "<strong>I want to</strong> see the history (operation, date, amount, balance) of my operations</br>",
        color = "rgb(44, 55, 76)")
@Retention(RetentionPolicy.RUNTIME)
public @interface FeatureHistory {

}
