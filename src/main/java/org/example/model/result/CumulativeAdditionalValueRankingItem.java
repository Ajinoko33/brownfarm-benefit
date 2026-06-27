package org.example.model.result;

import java.util.Objects;

public record CumulativeAdditionalValueRankingItem(
        int rank,
        String itemName,
        int cumulativeAdditionalValue
) {
    public CumulativeAdditionalValueRankingItem {
        Objects.requireNonNull(itemName);
    }
}
