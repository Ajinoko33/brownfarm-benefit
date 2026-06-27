package org.example.model.result;

import org.example.calculator.AdditionalValueItem;

import java.util.Objects;

// 品物ごとの付加価値：品物名、(基本品物からの)累積付加価値, (固有の)直接付加価値
public record AdditionalValueResultItem(
        String itemName,
        int cumulativeAdditionalValue,
        int directAdditionalValue
) {
    public AdditionalValueResultItem {
        Objects.requireNonNull(itemName);
    }

    public static AdditionalValueResultItem from(final AdditionalValueItem item) {
        return new AdditionalValueResultItem(
                item.item().name().value(),
                item.cumulativeAdditionalValue().intValue(),
                item.directAdditionalValue().intValue()
        );
    }
}
