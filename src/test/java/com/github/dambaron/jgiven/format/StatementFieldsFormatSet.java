package com.github.dambaron.jgiven.format;

import com.tngtech.jgiven.annotation.Format;
import com.tngtech.jgiven.annotation.NamedFormat;
import com.tngtech.jgiven.annotation.NamedFormats;
import com.tngtech.jgiven.format.PrintfFormatter;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@NamedFormats({
        @NamedFormat(name = "instant", format = @Format(value = LocalDateFormatter.class)),
        //@NamedFormat(name = "operation", customFormatAnnotation = Quoted.class),
        @NamedFormat(name = "startBalance", format = @Format(value = PrintfFormatter.class, args = "%s")),
        @NamedFormat(name = "endBalance", format = @Format(value = PrintfFormatter.class, args = "%.2f"))
})
@Retention(RetentionPolicy.RUNTIME)
public @interface StatementFieldsFormatSet {

}
