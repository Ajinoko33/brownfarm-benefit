package org.example.calculator;

import org.example.model.Item;
import org.example.model.ItemName;
import org.example.model.ItemQuantity;
import org.example.model.ShopName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class AdditionalValueCalculatorTest {
    private AdditionalValueCalculator additionalValueCalculator = new AdditionalValueCalculator();

    @Nested
    class Calculate {
        @Nested
        class 基本品物 {
            private static final Item FUNDAMENTAL_ITEM = new Item(
                    new ItemName("品物名"),
                    100,
                    new ShopName("店舗名"),
                    Map.of()
            );

            @Test
            void 直接付加価値が売価と同じ() {
                /* Act */
                final var actual = additionalValueCalculator.calculate(List.of(FUNDAMENTAL_ITEM));

                /* Assert */
                final AdditionalValueItem additionalValueItem = extract(FUNDAMENTAL_ITEM.name(), actual);
                assertEquals(new BigDecimal(100), additionalValueItem.directAdditionalValue());
            }

            @Test
            void 累積付加価値がゼロ() {
                /* Act */
                final var actual = additionalValueCalculator.calculate(List.of(FUNDAMENTAL_ITEM));

                /* Assert */
                final AdditionalValueItem additionalValueItem = extract(FUNDAMENTAL_ITEM.name(), actual);
                assertEquals(BigDecimal.ZERO, additionalValueItem.cumulativeAdditionalValue());
            }
        }

        @Nested
        class 基本品物1つを素材とする合成品物 {
            private static final Item FUNDAMENTAL_ITEM = new Item(
                    new ItemName("基本品物"),
                    100,
                    new ShopName("店舗名1"),
                    Map.of());
            private static final Item COMPOUND_ITEM = new Item(
                    new ItemName("合成品物"),
                    500,
                    new ShopName("店舗名2"),
                    Map.of(FUNDAMENTAL_ITEM.name(), new ItemQuantity(new BigDecimal(2)))
            );
            private static final List<Item> ITEMS = List.of(FUNDAMENTAL_ITEM, COMPOUND_ITEM);

            @Test
            void 直接付加価値() {
                /* Act */
                final var actual = additionalValueCalculator.calculate(ITEMS);

                /* Assert */
                final AdditionalValueItem additionalValueItem = extract(COMPOUND_ITEM.name(), actual);
                assertEquals(new BigDecimal(300), additionalValueItem.directAdditionalValue());
            }

            @Test
            void 累積付加価値() {
                /* Act */
                final var actual = additionalValueCalculator.calculate(ITEMS);

                /* Assert */
                final AdditionalValueItem additionalValueItem = extract(COMPOUND_ITEM.name(), actual);
                assertEquals(new BigDecimal(300), additionalValueItem.directAdditionalValue());
            }
        }

        @Nested
        class 基本品物複数を素材とする合成品物 {
            private static final Item FUNDAMENTAL_ITEM_1 = new Item(
                    new ItemName("基本品物1"),
                    1,
                    new ShopName("店舗名1"),
                    Map.of());
            private static final Item FUNDAMENTAL_ITEM_2 = new Item(
                    new ItemName("基本品物2"),
                    10,
                    new ShopName("店舗名2"),
                    Map.of());
            private static final Item FUNDAMENTAL_ITEM_3 = new Item(
                    new ItemName("基本品物3"),
                    100,
                    new ShopName("店舗名3"),
                    Map.of());
            private static final Item COMPOUND_ITEM = new Item(
                    new ItemName("合成品物"),
                    999,
                    new ShopName("店舗名4"),
                    Map.of(
                            FUNDAMENTAL_ITEM_1.name(),
                            new ItemQuantity(new BigDecimal(1)),
                            FUNDAMENTAL_ITEM_2.name(),
                            new ItemQuantity(new BigDecimal(2)),
                            FUNDAMENTAL_ITEM_3.name(),
                            new ItemQuantity(new BigDecimal(3))
                    )
            );
            private static final List<Item> ITEMS = List.of(
                    FUNDAMENTAL_ITEM_1,
                    FUNDAMENTAL_ITEM_2,
                    FUNDAMENTAL_ITEM_3,
                    COMPOUND_ITEM
            );

            @Test
            void 直接付加価値() {
                /* Act */
                final var actual = additionalValueCalculator.calculate(ITEMS);

                /* Assert */
                final AdditionalValueItem additionalValueItem = extract(COMPOUND_ITEM.name(), actual);
                assertEquals(new BigDecimal(678), additionalValueItem.directAdditionalValue());
            }

            @Test
            void 累積付加価値() {
                /* Act */
                final var actual = additionalValueCalculator.calculate(ITEMS);

                /* Assert */
                final AdditionalValueItem additionalValueItem = extract(COMPOUND_ITEM.name(), actual);
                assertEquals(new BigDecimal(678), additionalValueItem.directAdditionalValue());
            }
        }

        /**
         * 上位合成品物 (999)
         * ├── 基本品物2 (10)
         * ├── 中間合成品物 (100)
         * │   └── 基本品物1 (1)
         * └── 中間合成品物 (100)
         *     └── 基本品物1 (1)
         */
        @Nested
        class 合成品物を素材とする合成品物 {
            private static final Item FUNDAMENTAL_ITEM_1 = new Item(
                    new ItemName("基本品物1"),
                    1,
                    new ShopName("店舗名1"),
                    Map.of());
            private static final Item FUNDAMENTAL_ITEM_2 = new Item(
                    new ItemName("基本品物2"),
                    10,
                    new ShopName("店舗名2"),
                    Map.of());
            private static final Item COMPOUND_ITEM_1 = new Item(
                    new ItemName("中間合成品物"),
                    100,
                    new ShopName("店舗名3"),
                    Map.of(
                            FUNDAMENTAL_ITEM_1.name(),
                            new ItemQuantity(new BigDecimal(1))
                    )
            );
            private static final Item COMPOUND_ITEM_2 = new Item(
                    new ItemName("上位合成品物"),
                    999,
                    new ShopName("店舗名4"),
                    Map.of(
                            FUNDAMENTAL_ITEM_2.name(),
                            new ItemQuantity(new BigDecimal(1)),
                            COMPOUND_ITEM_1.name(),
                            new ItemQuantity(new BigDecimal(2))
                    )
            );
            private static final List<Item> ITEMS = List.of(
                    FUNDAMENTAL_ITEM_1,
                    FUNDAMENTAL_ITEM_2,
                    COMPOUND_ITEM_1,
                    COMPOUND_ITEM_2
            );

            @Test
            void 直接付加価値() {
                /* Act */
                final var actual = additionalValueCalculator.calculate(ITEMS);

                /* Assert */
                final AdditionalValueItem additionalValueItem = extract(COMPOUND_ITEM_2.name(), actual);
                assertEquals(new BigDecimal(789), additionalValueItem.directAdditionalValue());
            }

            @Test
            void 累積付加価値() {
                /* Act */
                final var actual = additionalValueCalculator.calculate(ITEMS);

                /* Assert */
                final AdditionalValueItem additionalValueItem = extract(COMPOUND_ITEM_2.name(), actual);
                assertEquals(new BigDecimal(987), additionalValueItem.cumulativeAdditionalValue());
            }
        }

        /**
         * 合成品物1 (999)
         * ├── 基本品物1 (1)
         * ├── 基本品物1 (1)
         * └── 基本品物1 (1)
         * 合成品物3 (999)
         * ├── 基本品物2 (10)
         * ├── 基本品物3 (100)
         * └── 合成品物2 (99)
         *     ├── 基本品物1 (1)
         *     ├── 基本品物2 (10)
         *     └── 基本品物2 (10)
         */
        @Nested
        class 合成品物複数 {
            private static final Item FUNDAMENTAL_ITEM_1 = new Item(
                    new ItemName("基本品物1"),
                    1,
                    new ShopName("店舗名1"),
                    Map.of());
            private static final Item FUNDAMENTAL_ITEM_2 = new Item(
                    new ItemName("基本品物2"),
                    10,
                    new ShopName("店舗名1"),
                    Map.of());
            private static final Item FUNDAMENTAL_ITEM_3 = new Item(
                    new ItemName("基本品物3"),
                    100,
                    new ShopName("店舗名1"),
                    Map.of());
            private static final Item COMPOUND_ITEM_1 = new Item(
                    new ItemName("合成品物1"),
                    999,
                    new ShopName("店舗名2"),
                    Map.of(
                            FUNDAMENTAL_ITEM_1.name(),
                            new ItemQuantity(new BigDecimal(3))
                    )
            );
            private static final Item COMPOUND_ITEM_2 = new Item(
                    new ItemName("合成品物2"),
                    99,
                    new ShopName("店舗名2"),
                    Map.of(
                            FUNDAMENTAL_ITEM_1.name(),
                            new ItemQuantity(new BigDecimal(1)),
                            FUNDAMENTAL_ITEM_2.name(),
                            new ItemQuantity(new BigDecimal(2))
                    )
            );
            private static final Item COMPOUND_ITEM_3 = new Item(
                    new ItemName("合成品物3"),
                    999,
                    new ShopName("店舗名2"),
                    Map.of(
                            FUNDAMENTAL_ITEM_2.name(),
                            new ItemQuantity(new BigDecimal(1)),
                            FUNDAMENTAL_ITEM_3.name(),
                            new ItemQuantity(new BigDecimal(1)),
                            COMPOUND_ITEM_2.name(),
                            new ItemQuantity(new BigDecimal(1))
                    )
            );
            private static final List<Item> ITEMS = List.of(
                    FUNDAMENTAL_ITEM_1,
                    FUNDAMENTAL_ITEM_2,
                    FUNDAMENTAL_ITEM_3,
                    COMPOUND_ITEM_1,
                    COMPOUND_ITEM_2,
                    COMPOUND_ITEM_3
            );

            private static final List<AdditionalValueItem> expected = List.of(
                    new AdditionalValueItem(
                            FUNDAMENTAL_ITEM_1,
                            new BigDecimal(1),
                            new BigDecimal(0)
                    ),
                    new AdditionalValueItem(
                            FUNDAMENTAL_ITEM_2,
                            new BigDecimal(10),
                            new BigDecimal(0)
                    ),
                    new AdditionalValueItem(
                            FUNDAMENTAL_ITEM_3,
                            new BigDecimal(100),
                            new BigDecimal(0)
                    ),
                    new AdditionalValueItem(
                            COMPOUND_ITEM_1,
                            new BigDecimal(996),
                            new BigDecimal(996)
                    ),
                    new AdditionalValueItem(
                            COMPOUND_ITEM_2,
                            new BigDecimal(78),
                            new BigDecimal(78)
                    ),
                    new AdditionalValueItem(
                            COMPOUND_ITEM_3,
                            new BigDecimal(790),
                            new BigDecimal(868)
                    )
            );

            @Test
            void test() {
                /* Act */
                final var actual = additionalValueCalculator.calculate(ITEMS);

                /* Assert */
                assertThat(actual).usingRecursiveComparison().ignoringCollectionOrder().isEqualTo(expected);
            }
        }

        @Nested
        class 数量が小数 {
            private static final Item FUNDAMENTAL_ITEM = new Item(
                    new ItemName("基本品物"),
                    1,
                    new ShopName("店舗名1"),
                    Map.of());
            private static final Item COMPOUND_ITEM = new Item(
                    new ItemName("合成品物"),
                    100,
                    new ShopName("店舗名2"),
                    Map.of(FUNDAMENTAL_ITEM.name(), new ItemQuantity(new BigDecimal("2.8")))
            );
            private static final List<Item> ITEMS = List.of(FUNDAMENTAL_ITEM, COMPOUND_ITEM);

            @Test
            void 直接付加価値() {
                /* Act */
                final var actual = additionalValueCalculator.calculate(ITEMS);

                /* Assert */
                final AdditionalValueItem additionalValueItem = extract(COMPOUND_ITEM.name(), actual);
                assertEquals(new BigDecimal("97.2"), additionalValueItem.directAdditionalValue());
            }

            @Test
            void 累積付加価値() {
                /* Act */
                final var actual = additionalValueCalculator.calculate(ITEMS);

                /* Assert */
                final AdditionalValueItem additionalValueItem = extract(COMPOUND_ITEM.name(), actual);
                assertEquals(new BigDecimal("97.2"), additionalValueItem.directAdditionalValue());
            }
        }

        private static AdditionalValueItem extract(final ItemName itemName, final List<AdditionalValueItem> items) {
            return items.stream().filter(item -> item.item().name().equals(itemName)).findFirst().orElse(null);
        }
    }
}
