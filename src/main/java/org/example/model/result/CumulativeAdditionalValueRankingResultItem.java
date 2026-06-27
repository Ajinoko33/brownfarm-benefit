package org.example.model.result;

import java.util.List;
import java.util.Objects;

// 店ごとの累積付加価値ランキング：店、順位、品物名、累積付加価値
public record CumulativeAdditionalValueRankingResultItem(
        String shopName,
        List<CumulativeAdditionalValueRankingItem> items
) {
    public CumulativeAdditionalValueRankingResultItem {
        Objects.requireNonNull(shopName);
        Objects.requireNonNull(items);
    }
}
