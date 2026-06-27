package org.example.calculator;

import org.example.model.Item;

import java.math.BigDecimal;
import java.util.Objects;

public record AdditionalValueItem(
        Item item,
        BigDecimal directAdditionalValue,
        BigDecimal cumulativeAdditionalValue
) {
    public AdditionalValueItem {
        Objects.requireNonNull(item);
        Objects.requireNonNull(directAdditionalValue);
        Objects.requireNonNull(cumulativeAdditionalValue);
    }
}
