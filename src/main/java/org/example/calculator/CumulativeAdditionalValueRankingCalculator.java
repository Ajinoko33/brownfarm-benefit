package org.example.calculator;

import org.example.model.ShopName;
import org.example.model.result.CumulativeAdditionalValueRankingItem;
import org.example.model.result.CumulativeAdditionalValueRankingResult;
import org.example.model.result.CumulativeAdditionalValueRankingResultItem;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CumulativeAdditionalValueRankingCalculator {
    public CumulativeAdditionalValueRankingResult calculate(final List<AdditionalValueItem> items) {
        // shop ごとにグルーピング
        final Map<ShopName, List<AdditionalValueItem>> additionalValueItemMap = items.stream().collect(Collectors.groupingBy(item -> item.item().shop()));

        // shop ごとに処理
        List<CumulativeAdditionalValueRankingResultItem> resultItemList = new ArrayList<>();
        for (final var entry : additionalValueItemMap.entrySet()) {
            final ShopName shopName = entry.getKey();
            final List<AdditionalValueItem> itemList = entry.getValue();

            // shop 内でソート
            final List<AdditionalValueItem> sortedItemList = itemList.stream().sorted(
                    Comparator.comparing(AdditionalValueItem::cumulativeAdditionalValue)
            ).toList();

            // 変換
            final List<CumulativeAdditionalValueRankingItem> thisShopResultItems = createCumulativeAdditionalValueRankingItems(sortedItemList);

            resultItemList.add(
                    new CumulativeAdditionalValueRankingResultItem(
                            shopName.value(),
                            thisShopResultItems
                    )
            );
        }

        return new CumulativeAdditionalValueRankingResult(resultItemList);
    }

    private static List<CumulativeAdditionalValueRankingItem> createCumulativeAdditionalValueRankingItems(final List<AdditionalValueItem> sortedItemList) {
        List<CumulativeAdditionalValueRankingItem> result = new ArrayList<>();
        for (int i = 0; i < sortedItemList.size(); i++) {
            final var item = sortedItemList.get(i);
            result.add(
                    new CumulativeAdditionalValueRankingItem(
                            i + 1,
                            item.item().name().value(),
                            item.cumulativeAdditionalValue().intValue()
                    )
            );
        }
        return result;
    }
}
