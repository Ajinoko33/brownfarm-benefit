package org.example.model.result;

import org.example.calculator.AdditionalValueItem;

import java.util.List;
import java.util.Objects;

// 品物ごとの付加価値：品物名、(素材からの)累積付加価値, (固有の)直接付加価値
public record AdditionalValueResult(List<AdditionalValueResultItem> items) {
    public AdditionalValueResult {
        Objects.requireNonNull(items);
    }

    public static AdditionalValueResult from(final List<AdditionalValueItem> additionalValueItems) {
        return new AdditionalValueResult(
                additionalValueItems.stream()
                        .map(AdditionalValueResultItem::from)
                        .toList()
        );
    }
}
