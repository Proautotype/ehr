package com.custard.ehr.shared.domain;

import java.math.BigDecimal;
import java.util.Currency;

public record Money(BigDecimal amount, Currency currency) implements ValueObject {

    public Money {
        if (amount == null) {
            throw new IllegalArgumentException("Amount is required");
        }

        if (currency == null) {
            throw new IllegalArgumentException("Currency is required");
        }

        if (amount.signum() < 0) {
            throw new IllegalArgumentException("Amount cannot be negative");
        }
    }

    public static Money zero(String currencyCode) {
        return new Money(BigDecimal.ZERO, Currency.getInstance(currencyCode));
    }
}