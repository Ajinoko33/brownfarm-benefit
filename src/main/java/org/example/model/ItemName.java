package org.example.model;

import java.util.Objects;

public record ItemName(String value) {
    public ItemName {
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
        ItemName other = (ItemName) obj;
        return value.equals(other.value);
    }
}
