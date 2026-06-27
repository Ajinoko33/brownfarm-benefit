package org.example.calculator;

import org.example.model.Item;
import org.example.model.ItemName;
import org.example.model.ItemQuantity;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

// 品物ごとの付加価値を計算するクラス
public class AdditionalValueCalculator {
    public List<AdditionalValueItem> calculate(final List<Item> items) {
        final Map<ItemName, Item> itemMap = items.stream().collect(
                Collectors.toMap(
                        Item::name,
                        Function.identity()
                )
        );

        // 商品ごとの直接付加価値を計算
        final Map<ItemName, BigDecimal> directAdditionalValueMap = calculateDirectAdditionalValue(itemMap);

        // 商品ごとの累積付加価値を計算
        final Map<ItemName, BigDecimal> cumulativeAdditionalValueMap = calculateCumulativeAdditionalValue(itemMap, directAdditionalValueMap);

        // 整理
        return organize(items, directAdditionalValueMap, cumulativeAdditionalValueMap);
    }

    private static Map<ItemName, BigDecimal> calculateDirectAdditionalValue(
            final Map<ItemName, Item> itemMap
    ) {
        Map<ItemName, BigDecimal> directAdditionalValue = new HashMap<>();
        itemMap.values().forEach(
                item ->
                    directAdditionalValue.put(
                            item.name(),
                            calculateDirectAdditionalValue(item, itemMap)
                        )
        );

        return directAdditionalValue;
    }

    private static BigDecimal calculateDirectAdditionalValue(
            final Item targetItem,
            final Map<ItemName, Item> itemMap
    ) {
        final BigDecimal price = BigDecimal.valueOf(targetItem.price());
        final BigDecimal materialPriceSum = targetItem.material().entrySet().stream().map(entry -> {
            final ItemName materialItemName = entry.getKey();
            final ItemQuantity materialQuantity = entry.getValue();

            final Item material = itemMap.get(materialItemName);
            return BigDecimal.valueOf(material.price()).multiply(materialQuantity.value());
        }).reduce(BigDecimal.ZERO, BigDecimal::add);

        return price.subtract(materialPriceSum);
    }

    private static Map<ItemName, BigDecimal> calculateCumulativeAdditionalValue(
            final Map<ItemName, Item> itemMap,
            final Map<ItemName, BigDecimal> directAdditionalValueMap
    ) {
        Map<ItemName, BigDecimal> cumulativeAdditionalValueMap = new HashMap<>();
        itemMap.keySet().forEach(itemName -> {
            calculateCumulativeAdditionalValue(
                    itemName,
                    itemMap,
                    directAdditionalValueMap,
                    cumulativeAdditionalValueMap
            );
        });

        return cumulativeAdditionalValueMap;
    }

    private static BigDecimal calculateCumulativeAdditionalValue(
            final ItemName targetItemName,
            final Map<ItemName, Item> itemMap,
            final Map<ItemName, BigDecimal> directAdditionalValueMap,
            Map<ItemName, BigDecimal> cumulativeAdditionalValueMap
    ) {
        if (cumulativeAdditionalValueMap.containsKey(targetItemName)) {
            return cumulativeAdditionalValueMap.get(targetItemName);
        }

        // 計算
        final Item targetItem = itemMap.get(targetItemName);
        BigDecimal result = directAdditionalValueMap.get(targetItemName);
        for (final var materialEntry : targetItem.material().entrySet()) {
            final ItemName materialName = materialEntry.getKey();
            final ItemQuantity materialQuantity = materialEntry.getValue();

            final BigDecimal materialCumulativeAdditionalValue = calculateCumulativeAdditionalValue(
                    materialName,
                    itemMap,
                    directAdditionalValueMap,
                    cumulativeAdditionalValueMap
            ).multiply(materialQuantity.value());
            result = result.add(
                    materialCumulativeAdditionalValue
            );
        }

        // メモ化
        cumulativeAdditionalValueMap.put(targetItemName, result);

        return result;
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
