package org.example.io;

import org.example.model.Item;
import org.example.model.ItemName;
import org.example.model.ItemQuantity;
import org.example.model.ShopName;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class CsvReader {
    private static final String[] HEADERS = {
            "name",
            "price",
            "shop",
            "material1",
            "volume1",
            "material2",
            "volume2",
            "material3",
            "volume3",
            "material4",
            "volume4"
    };

    private final String sourcePath;

    public CsvReader(final String sourcePath) {
        this.sourcePath = sourcePath;
    }

    public List<Item> read() {
        // 読み込み
        try (final InputStream is = getClass().getClassLoader().getResourceAsStream(sourcePath);
             final BufferedReader reader = new BufferedReader(
                     new InputStreamReader(is, StandardCharsets.UTF_8)
             )) {
            // CSV ヘッダー
            final String headerLine = reader.readLine();
            validateHeaderLine(headerLine);

            // データ
            return reader.lines().map(CsvReader::convertToItem).toList();
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void validateHeaderLine(final String headerLine) {
        // ヘッダーが想定するものであるか
        final String[] incomingHeaders = headerLine.split(",");

        if (incomingHeaders.length != HEADERS.length) {
            throwInvalidHeaderError(incomingHeaders);
        }

        for (int i = 0; i < HEADERS.length; i++) {
            if (!Objects.equals(incomingHeaders[i].trim(), HEADERS[i])) {
                throwInvalidHeaderError(incomingHeaders);
            }
        }
    }

    private static void throwInvalidHeaderError(final String[] expectedHeaders) {
        final StringBuilder sb = new StringBuilder();
        sb.append("[ERROR] 想定するヘッダーではありません:\n");

        // expected
        sb.append("Expected: ");
        for (int i = 0; i < expectedHeaders.length; i++) {
            if (i > 0) {
                sb.append(", ");
            }

            sb.append("\"");
            sb.append(expectedHeaders[i]);
            sb.append("\"");
        }
        sb.append("\n");

        // actual
        sb.append("Actual: ");
        for (int i = 0; i < CsvReader.HEADERS.length; i++) {
            if (i > 0) {
                sb.append(", ");
            }

            sb.append("\"");
            sb.append(CsvReader.HEADERS[i]);
            sb.append("\"");
        }
        sb.append("\n");

        throw new RuntimeException(sb.toString());
    }

    private static Item convertToItem(final String line) {
        final List<String> columns = toColumnList(line).stream().map(String::trim).toList();

        final String name = columns.get(0);
        final int price = parseInt(columns.get(1), line, HEADERS[1]);
        final String shop = columns.get(2);

        Map<ItemName, ItemQuantity> material = new HashMap<>();
        for (int i = 0; i < 4; i++) {
            final int indexDiff = i * 2;
            final int materialIndex = 3 + indexDiff;
            final int quantityIndex = 4 + indexDiff;

            final String materialName = columns.get(materialIndex);
            final String quantityString = columns.get(quantityIndex);

            if (!materialName.isEmpty() && !quantityString.isEmpty()) {
                final BigDecimal quantity = parseDecimal(quantityString, line, HEADERS[quantityIndex]);

                material.put(new ItemName(materialName), new ItemQuantity(quantity));
            } else if (materialName.isEmpty() ^ quantityString.isEmpty()) {
                // 一方だけが空白
                final String message = String.format(
                        "[WARN] %s 列と %s 列の一方が空白です: %s",
                        HEADERS[materialIndex],
                        HEADERS[quantityIndex],
                        line
                );
                System.out.println(message);
            }
        }

        return new Item(
                new ItemName(name),
                price,
                new ShopName(shop),
                material
        );
    }

    private static List<String> toColumnList(final String line) {
        final String[] columns = line.split(",");
        List<String> list = new ArrayList<>(Arrays.asList(columns));

        while (list.size() < HEADERS.length) {
            list.add("");
        }

        return list;
    }

    private static int parseInt(final String value, final String line, final String header) {
        try {
            return Integer.parseInt(value);
        } catch (final NumberFormatException e) {
            final String message = String.format("[ERROR] %s 列が数値に変換できません: %s\n", header, line);
            throw new RuntimeException(message, e);
        }
    }

    private static BigDecimal parseDecimal(final String value, final String line, final String header) {
        try {
            return new BigDecimal(value);
        } catch (final NumberFormatException e) {
            final String message = String.format("[ERROR] %s 列が数値に変換できません: %s\n", header, line);
            throw new RuntimeException(message, e);
        }
    }
}
