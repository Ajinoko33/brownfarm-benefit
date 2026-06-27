package org.example;

import org.example.calculator.Calculator;
import org.example.io.CsvReader;
import org.example.io.CsvWriter;
import org.example.model.result.CalculationResult;
import org.example.model.Item;
import org.example.validator.Validator;

import java.util.List;

public class Main {
    private static final String sourcePath = "data.csv";
    private static final String destinationPath = "";

    private static final CsvReader reader = new CsvReader(sourcePath);
    private static final CsvWriter writer = new CsvWriter(destinationPath);

    private static final Validator validator = new Validator();

    static void main() {
        // ファイル読み込み
        final List<Item> items = reader.read();

        // バリデーション
        validator.validate(items);

        // 計算
        final Calculator calculator = new Calculator(items);
        final CalculationResult result = calculator.calculate();

        // ファイル出力
        writer.write(result);
    }
}
