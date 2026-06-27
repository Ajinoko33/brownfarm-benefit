package org.example.model;

import java.math.BigDecimal;
import java.util.Objects;

public record ItemQuantity(BigDecimal value) {
    public ItemQuantity {
        Objects.requireNonNull(value);
    }
}
