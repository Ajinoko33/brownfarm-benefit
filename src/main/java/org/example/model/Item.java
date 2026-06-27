package org.example.model;

import java.util.Map;
import java.util.Objects;

public record Item(
        ItemName name,
        int price,
        ShopName shop,
        Map<ItemName, ItemQuantity> material
) {
    public Item {
        Objects.requireNonNull(name);
        Objects.requireNonNull(shop);
        Objects.requireNonNull(material);
    }

    public boolean isFundamentalItem() {
        return material.isEmpty();
    }
}
