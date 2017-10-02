package com.github.dambaron.jgiven.tags;

import com.tngtech.jgiven.annotation.IsTag;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Feature
@IsTag(name = "Deposit",
        description = "<strong>In order to</strong> save money</br>" +
                "<strong>As a</strong> bank client</br>" +
                "<strong>I want to</strong> make a deposit in my account</br>",
        color = "rgb(108, 29, 95)")
@Retention(RetentionPolicy.RUNTIME)
public @interface FeatureDeposit {

}
