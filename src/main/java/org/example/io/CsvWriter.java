package org.example.io;

import org.example.model.result.AdditionalValueResult;
import org.example.model.result.CalculationResult;
import org.example.model.result.CumulativeAdditionalValueRankingResult;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class CsvWriter {
    private static final String ADDITIONAL_VALUE_RESULT_FILE_NAME = "additionalValueResult.csv";
    private static final String CUMULATIVE_ADDITIONAL_VALUE_RANKING_RESULT_FILE_NAME = "cumulativeAdditionalValueRankingResult.csv";

    private final String destinationDirectoryPath;

    public CsvWriter(final String destinationDirectoryPath) {
        this.destinationDirectoryPath = destinationDirectoryPath;
    }

    public void write(final CalculationResult result, final String sourcePath) {
        // outputディレクトリを作成
        try {
            Files.createDirectories(Path.of(destinationDirectoryPath));
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }

        // inputファイルをコピー
        copySourceFile(sourcePath);

        // 品物ごとの付加価値一覧を出力
        exportAdditionalValueResult(result.additionalValueResult());

        // 店ごとの付加価値を出力
        exportCumulativeAdditionalValueRankingResult(result.cumulativeAdditionalValueRankingResult());
    }

    private void copySourceFile(final String sourcePath) {
        try (final InputStream is = getClass().getClassLoader().getResourceAsStream(sourcePath)) {
            if (is == null) {
                throw new RuntimeException("Can't find resource: " + sourcePath);
            }

            final Path path = Path.of(destinationDirectoryPath, sourcePath);
            Files.copy(is, path, StandardCopyOption.REPLACE_EXISTING);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void exportAdditionalValueResult(final AdditionalValueResult result) {
        final Path outputPath = Path.of(destinationDirectoryPath, ADDITIONAL_VALUE_RESULT_FILE_NAME);
        try (final BufferedWriter bw = Files.newBufferedWriter(outputPath, StandardCharsets.UTF_8)) {
            // ヘッダー
            final String header = "\uFEFF品物名,(基本品物からの)累積付加価値,(固有の)直接付加価値";
            bw.write(header);
            bw.newLine();

            // データ
            for (final var item : result.items()) {
                bw.write(String.format(
                        "%s,%d,%d",
                        item.itemName(),
                        item.cumulativeAdditionalValue(),
                        item.directAdditionalValue()
                ));
                bw.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void exportCumulativeAdditionalValueRankingResult(final CumulativeAdditionalValueRankingResult result) {
        final Path outputPath = Path.of(destinationDirectoryPath, CUMULATIVE_ADDITIONAL_VALUE_RANKING_RESULT_FILE_NAME);
        try (final BufferedWriter bw = Files.newBufferedWriter(outputPath, StandardCharsets.UTF_8)) {
            // ヘッダー
            final String header = "\uFEFF店舗名,順位,品物名,(基本品物からの)累積付加価値";
            bw.write(header);
            bw.newLine();

            // データ
            for (final var item : result.items()) {
                final String shopName = item.shopName();
                final var shopItems = item.items();

                for (final var shopItem: shopItems) {
                    bw.write(String.format(
                            "%s,%d,%s,%d",
                            shopName,
                            shopItem.rank(),
                            shopItem.itemName(),
                            shopItem.cumulativeAdditionalValue()
                    ));
                    bw.newLine();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
