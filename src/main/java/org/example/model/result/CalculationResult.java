package org.example.model.result;

import java.util.Objects;

public record CalculationResult(AdditionalValueResult additionalValueResult,
                                CumulativeAdditionalValueRankingResult cumulativeAdditionalValueRankingResult) {
    public CalculationResult {
        Objects.requireNonNull(additionalValueResult);
        Objects.requireNonNull(cumulativeAdditionalValueRankingResult);
    }
}
