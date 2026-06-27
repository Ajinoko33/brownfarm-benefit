package org.example.calculator;

import org.example.model.Item;
import org.example.model.ItemName;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

// 品物ごとの付加価値を計算するクラス
class AdditionalValueCalculator {
    private final DirectAdditionalValueCalculator directAdditionalValueCalculator = new DirectAdditionalValueCalculator();
    private final CumulativeAdditionalValueCalculator cumulativeAdditionalValueCalculator = new CumulativeAdditionalValueCalculator();

    public List<AdditionalValueItem> calculate(final List<Item> items) {
        final Map<ItemName, Item> itemMap = items.stream().collect(
                Collectors.toMap(
                        Item::name,
                        Function.identity()
                )
        );

        // 商品ごとの直接付加価値を計算
        final Map<ItemName, BigDecimal> directAdditionalValueMap = directAdditionalValueCalculator.calculate(itemMap);

        // 商品ごとの累積付加価値を計算
        final Map<ItemName, BigDecimal> cumulativeAdditionalValueMap = cumulativeAdditionalValueCalculator.calculate(itemMap, directAdditionalValueMap);

        // 整理
        return organize(items, directAdditionalValueMap, cumulativeAdditionalValueMap);
    }

    private static List<AdditionalValueItem> organize(
            final List<Item> items,
            final Map<ItemName, BigDecimal> directAdditionalValue,
            final Map<ItemName, BigDecimal> cumulativeAdditionalValue
    ) {
        return items.stream().map(item -> new AdditionalValueItem(
                item,
                directAdditionalValue.get(item.name()),
                cumulativeAdditionalValue.get(item.name())
        )).toList();
    }
}
