package org.example.calculator;

import org.example.model.Item;
import org.example.model.ItemName;
import org.example.model.ShopName;
import org.example.model.result.CumulativeAdditionalValueRankingItem;
import org.example.model.result.CumulativeAdditionalValueRankingResult;
import org.example.model.result.CumulativeAdditionalValueRankingResultItem;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class CumulativeAdditionalValueRankingCalculatorTest {
    private CumulativeAdditionalValueRankingCalculator calculator = new CumulativeAdditionalValueRankingCalculator();

    @Nested
    class Calculate {
        @Nested
        class 店舗ごとに累積付加価値で降順ソートされていること {
            private static final String SHOP_A = "店舗A";
            private static final String SHOP_B = "店舗B";

            @Test
            void test() {
                /* Arrange */
                final var items = items();

                /* Act */
                final var actual = calculator.calculate(items);

                /* Assert */
                assertThat(actual)
                        .usingRecursiveComparison()
                        .ignoringCollectionOrder()
                        .isEqualTo(expected());
            }

            private static List<AdditionalValueItem> items() {
                return List.of(
                        // 店舗A
                        new AdditionalValueItem(
                                new Item(new ItemName("店舗A-商品1"), 10, new ShopName(SHOP_A), new HashMap<>()),
                                new BigDecimal("10"),
                                new BigDecimal("10")
                        ),
                        new AdditionalValueItem(
                                new Item(new ItemName("店舗A-商品2"), 10, new ShopName(SHOP_A), new HashMap<>()),
                                new BigDecimal("30"),
                                new BigDecimal("20")
                        ),
                        new AdditionalValueItem(
                                new Item(new ItemName("店舗A-商品3"), 10, new ShopName(SHOP_A), new HashMap<>()),
                                new BigDecimal("20"),
                                new BigDecimal("30")
                        ),
                        // 店舗B
                        new AdditionalValueItem(
                                new Item(new ItemName("店舗B-商品1"), 10, new ShopName(SHOP_B), new HashMap<>()),
                                new BigDecimal("200"),
                                new BigDecimal("100")
                        ),
                        new AdditionalValueItem(
                                new Item(new ItemName("店舗B-商品2"), 10, new ShopName(SHOP_B), new HashMap<>()),
                                new BigDecimal("100"),
                                new BigDecimal("200")
                        ),
                        new AdditionalValueItem(
                                new Item(new ItemName("店舗B-商品3"), 10, new ShopName(SHOP_B), new HashMap<>()),
                                new BigDecimal("300"),
                                new BigDecimal("300")
                        )
                );
            }

            private static CumulativeAdditionalValueRankingResult expected() {
                final var items = List.of(
                        new CumulativeAdditionalValueRankingResultItem(
                                SHOP_A,
                                List.of(
                                        new CumulativeAdditionalValueRankingItem(
                                                1,
                                                "店舗A-商品2",
                                                30
                                        ),
                                        new CumulativeAdditionalValueRankingItem(
                                                2,
                                                "店舗A-商品3",
                                                20
                                        ),
                                        new CumulativeAdditionalValueRankingItem(
                                                3,
                                                "店舗A-商品1",
                                                10
                                        )
                                )
                        ),
                        new CumulativeAdditionalValueRankingResultItem(
                                SHOP_B,
                                List.of(
                                        new CumulativeAdditionalValueRankingItem(
                                                1,
                                                "店舗B-商品3",
                                                300
                                        ),
                                        new CumulativeAdditionalValueRankingItem(
                                                2,
                                                "店舗B-商品1",
                                                200
                                        ),
                                        new CumulativeAdditionalValueRankingItem(
                                                3,
                                                "店舗B-商品2",
                                                100
                                        )
                                )
                        )
                );
                return new CumulativeAdditionalValueRankingResult(items);
            }
        }
    }
}
