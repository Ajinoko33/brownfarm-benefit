package org.example.calculator;

import org.example.model.result.AdditionalValueResult;
import org.example.model.result.CalculationResult;
import org.example.model.Item;
import org.example.model.result.CumulativeAdditionalValueRankingResult;

import java.util.List;
import java.util.Objects;

public class Calculator {
    private static final AdditionalValueCalculator additionalValueCalculator = new AdditionalValueCalculator();
    private static final CumulativeAdditionalValueRankingCalculator cumulativeAdditionalValueRankingCalculator = new CumulativeAdditionalValueRankingCalculator();

    private final List<Item> items;

    public Calculator(final List<Item> items) {
        this.items = Objects.requireNonNull(items);
    }

    public CalculationResult calculate() {
        final List<AdditionalValueItem> additionalValueItems = additionalValueCalculator.calculate(items);
        final CumulativeAdditionalValueRankingResult rankingResult = cumulativeAdditionalValueRankingCalculator.calculate(additionalValueItems);
        return new CalculationResult(
                AdditionalValueResult.from(additionalValueItems),
                rankingResult
        );
    }
}
