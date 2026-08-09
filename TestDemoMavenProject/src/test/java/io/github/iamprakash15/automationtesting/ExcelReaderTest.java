package io.github.iamprakash15.automationtesting;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ExcelReaderTest {

    @TempDir
    Path temporaryDirectory;

    @Test
    void readsStringsNumbersBooleansFormulasAndBlankCells() throws IOException {
        Path workbookFile = temporaryDirectory.resolve("test-data.xlsx");
        try (XSSFWorkbook workbook = new XSSFWorkbook(); OutputStream output = Files.newOutputStream(workbookFile)) {
            Sheet sheet = workbook.createSheet("UserDetails");
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Name");
            header.createCell(1).setCellValue("Score");
            header.createCell(2).setCellValue("Active");
            header.createCell(3).setCellValue("Double score");
            header.createCell(4).setCellValue("Optional");

            Row data = sheet.createRow(1);
            data.createCell(0).setCellValue("Prakash");
            data.createCell(1).setCellValue(42);
            data.createCell(2).setCellValue(true);
            data.createCell(3).setCellFormula("B2*2");
            data.createCell(4).setBlank();
            workbook.write(output);
        }

        List<List<String>> rows = ExcelReader.read(workbookFile, "UserDetails");

        assertEquals(List.of("Name", "Score", "Active", "Double score", "Optional"), rows.get(0));
        assertEquals(List.of("Prakash", "42", "TRUE", "84", ""), rows.get(1));
    }

    @Test
    void reportsMissingWorksheet() throws IOException {
        Path workbookFile = temporaryDirectory.resolve("empty.xlsx");
        try (XSSFWorkbook workbook = new XSSFWorkbook(); OutputStream output = Files.newOutputStream(workbookFile)) {
            workbook.createSheet("Existing");
            workbook.write(output);
        }

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> ExcelReader.read(workbookFile, "Missing"));
        assertEquals("Worksheet not found: Missing", exception.getMessage());
    }

    @Test
    void reportsMissingFile() {
        Path missingFile = temporaryDirectory.resolve("missing.xlsx");

        assertThrows(IOException.class, () -> ExcelReader.read(missingFile, "UserDetails"));
    }
}
