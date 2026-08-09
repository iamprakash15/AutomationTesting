package io.github.iamprakash15.automationtesting;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

/** Reads tabular data from an Excel worksheet without leaking file handles. */
public final class ExcelReader {

    private ExcelReader() {
    }

    public static List<List<String>> read(Path file, String sheetName) throws IOException {
        Objects.requireNonNull(file, "file must not be null");
        String normalizedSheetName = requireNonBlank(sheetName, "sheetName");
        if (!Files.isRegularFile(file)) {
            throw new IOException("Excel file does not exist or is not a regular file: " + file.toAbsolutePath());
        }

        try (InputStream input = Files.newInputStream(file); Workbook workbook = WorkbookFactory.create(input)) {
            Sheet sheet = workbook.getSheet(normalizedSheetName);
            if (sheet == null) {
                throw new IllegalArgumentException("Worksheet not found: " + normalizedSheetName);
            }

            DataFormatter formatter = new DataFormatter(Locale.ROOT);
            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
            List<List<String>> rows = new ArrayList<>();

            for (Row row : sheet) {
                int lastColumn = Math.max(row.getLastCellNum(), 0);
                List<String> values = new ArrayList<>(lastColumn);
                for (int column = 0; column < lastColumn; column++) {
                    values.add(formatter.formatCellValue(
                            row.getCell(column, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK), evaluator));
                }
                rows.add(List.copyOf(values));
            }
            return List.copyOf(rows);
        }
    }

    private static String requireNonBlank(String value, String name) {
        String normalized = Objects.requireNonNull(value, name + " must not be null").trim();
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException(name + " must not be blank");
        }
        return normalized;
    }
}
