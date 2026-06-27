package org.example.calculator;

import org.example.model.Item;

import java.math.BigDecimal;
import java.util.Objects;

public record AdditionalValueItem(
        Item item,
        BigDecimal cumulativeAdditionalValue,
        BigDecimal directAdditionalValue
) {
    public AdditionalValueItem {
        Objects.requireNonNull(item);
        Objects.requireNonNull(cumulativeAdditionalValue);
        Objects.requireNonNull(directAdditionalValue);
    }
}
