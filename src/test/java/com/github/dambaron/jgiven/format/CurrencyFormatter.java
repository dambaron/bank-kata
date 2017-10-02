package com.github.dambaron.jgiven.format;

import com.tngtech.jgiven.format.ArgumentFormatter;

import java.util.Currency;

public class CurrencyFormatter implements ArgumentFormatter<Currency> {

    @Override
    public String format(Currency currency, String... formatterArguments) {
        if (currency == null) {
            return "";
        }

        return currency.getCurrencyCode();
    }
}
