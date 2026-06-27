package org.example.validator;

import org.example.model.Item;
import org.example.model.ItemName;

import java.util.*;
import java.util.stream.Collectors;

public class Validator {
    public void validate(final List<Item> items) {
        validateAllMaterialExist(items);
        validateNoSelfMaterial(items);
    }

    /**
     * 素材は全て items に存在するか。
     */
    private void validateAllMaterialExist(final List<Item> items) {
        final Set<ItemName> itemNames = items.stream().map(Item::name).collect(Collectors.toUnmodifiableSet());
        final Map<Integer, List<ItemName>> notExistItemNames = new TreeMap<>();

        for (int i = 0; i < items.size(); i++) {
            for (final ItemName materialName : items.get(i).material().keySet()) {
                if (!itemNames.contains(materialName)) {
                    // items に存在しない
                    notExistItemNames.computeIfAbsent(i, k -> new ArrayList<>()).add(materialName);
                }
            }
        }

        if (notExistItemNames.isEmpty()) {
            return;
        }

        // 存在しない素材があるので例外を投げる
        throw new RuntimeException(createAnyMaterialNotExistError(items, notExistItemNames));
    }

    private String createAnyMaterialNotExistError(final List<Item> items, final Map<Integer, List<ItemName>> notExistItemNames) {
        final StringBuilder sb = new StringBuilder();
        sb.append("[Error] 存在しない素材が指定されています:\n");

        for(final Map.Entry<Integer, List<ItemName>> entry : notExistItemNames.entrySet()) {
            final int index = entry.getKey();
            final List<ItemName> materialNames = entry.getValue();
            sb.append(String.format("[%d] %s: %s\n", index + 1, items.get(index).name().value(), materialNames.stream().map(ItemName::value).collect(Collectors.joining(", "))));
        }

        return sb.toString();
    }

    /**
     * 素材が自身を指していないか。
     */
    private void validateNoSelfMaterial(final List<Item> items) {
        final List<Integer> selfMaterialIndexes = new ArrayList<>();

        for(int i = 0; i < items.size(); i++) {
            final Item item = items.get(i);
            if (item.material().containsKey(item.name())) {
                // 自身を指している素材がある
                selfMaterialIndexes.add(i);
            }
        }

        if (selfMaterialIndexes.isEmpty()) {
            return;
        }

        // 素材が自身を指しているものがあるので例外を投げる
        throw new RuntimeException(createSelfMaterialError(items, selfMaterialIndexes));
    }

    private String createSelfMaterialError(final List<Item> items, final List<Integer> selfMaterialIndexes) {
        final StringBuilder sb = new StringBuilder();
        sb.append("[Error] 素材が自身を指しています:\n");

        for(final int index : selfMaterialIndexes) {
            sb.append(String.format("[%d] %s\n", index + 1, items.get(index).name().value()));
        }

        return sb.toString();
    }
}
