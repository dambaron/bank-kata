package com.github.dambaron.jgiven.tags;

import com.tngtech.jgiven.annotation.IsTag;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Feature
@IsTag(name = "Withdrawal", description = "<strong>In order to</strong> retrieve some or all of my savings<br/>" +
        "<strong>As a</strong> bank client</br>" +
        "<strong>I want to</strong> make a withdrawal from my account</br>",
        color = "rgb(199, 178, 153)")
@Retention(RetentionPolicy.RUNTIME)
public @interface FeatureWithdrawal {

}
