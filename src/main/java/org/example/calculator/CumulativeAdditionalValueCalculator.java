package org.example.calculator;

import org.example.model.Item;
import org.example.model.ItemName;
import org.example.model.ItemQuantity;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

class CumulativeAdditionalValueCalculator {
    public Map<ItemName, BigDecimal> calculate(
            final Map<ItemName, Item> itemMap,
            final Map<ItemName, BigDecimal> directAdditionalValueMap
    ) {
        Map<ItemName, BigDecimal> cumulativeAdditionalValueMap = new HashMap<>();
        itemMap.keySet().forEach(itemName -> {
            calculate(
                    itemName,
                    itemMap,
                    directAdditionalValueMap,
                    cumulativeAdditionalValueMap
            );
        });

        return cumulativeAdditionalValueMap;
    }

    private static BigDecimal calculate(
            final ItemName targetItemName,
            final Map<ItemName, Item> itemMap,
            final Map<ItemName, BigDecimal> directAdditionalValueMap,
            Map<ItemName, BigDecimal> cumulativeAdditionalValueMap
    ) {
        if (cumulativeAdditionalValueMap.containsKey(targetItemName)) {
            return cumulativeAdditionalValueMap.get(targetItemName);
        }

        final Item targetItem = itemMap.get(targetItemName);

        if (targetItem.isFundamentalItem()) {
            // 基本品物は累積ゼロ
            cumulativeAdditionalValueMap.put(targetItemName, BigDecimal.ZERO);
            return BigDecimal.ZERO;
        }

        // 計算
        BigDecimal result = directAdditionalValueMap.get(targetItemName);
        for (final var materialEntry : targetItem.material().entrySet()) {
            final ItemName materialName = materialEntry.getKey();
            final ItemQuantity materialQuantity = materialEntry.getValue();

            final BigDecimal materialCumulativeAdditionalValue = calculate(
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
}
