package org.example.model.result;

import java.util.List;
import java.util.Objects;

// 店ごとの累積付加価値ランキング：店、順位、品物名、累積付加価値
public record CumulativeAdditionalValueRankingResult(
        List<CumulativeAdditionalValueRankingResultItem> items
) {
    public CumulativeAdditionalValueRankingResult {
        Objects.requireNonNull(items);
    }
}
