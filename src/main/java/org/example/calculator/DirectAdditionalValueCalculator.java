package org.example.calculator;

import org.example.model.Item;
import org.example.model.ItemName;
import org.example.model.ItemQuantity;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

class DirectAdditionalValueCalculator {
    public Map<ItemName, BigDecimal> calculate(
            final Map<ItemName, Item> itemMap
    ) {
        Map<ItemName, BigDecimal> directAdditionalValue = new HashMap<>();
        itemMap.values().forEach(
                item ->
                        directAdditionalValue.put(
                                item.name(),
                                calculate(item, itemMap)
                        )
        );

        return directAdditionalValue;
    }

    private static BigDecimal calculate(
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
}
