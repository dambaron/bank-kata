package com.github.dambaron.jgiven.format;

import com.tngtech.jgiven.format.ArgumentFormatter;

import java.time.Instant;
import java.time.format.DateTimeFormatter;

public class LocalDateFormatter implements ArgumentFormatter<Instant> {

    @Override
    public String format(Instant instant, String... formatterArguments) {
        if (instant == null) {
            return "";
        }

        return DateTimeFormatter.ISO_LOCAL_DATE.format(instant);
    }
}
