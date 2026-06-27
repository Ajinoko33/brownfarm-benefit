package org.example.model;

import java.util.Objects;

public record ShopName(String value) {
    public ShopName {
        Objects.requireNonNull(value);
        if (value.isEmpty()) {
            throw new IllegalArgumentException("[ERROR] value is empty");
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        ShopName other = (ShopName) obj;
        return value.equals(other.value);
    }
}
