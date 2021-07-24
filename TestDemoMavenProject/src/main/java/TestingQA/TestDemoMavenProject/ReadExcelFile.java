package TestingQA.TestDemoMavenProject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ReadExcelFile {

	public static void main(String[] args) throws IOException {

		fileReader("D:\\JavaMava\\TestingFile.xlsx");

	}

	private static void disableWarning() {
		System.err.close();
		System.setErr(System.out);
	}

	@SuppressWarnings("deprecation")
	static void fileReader(String file) throws IOException {
		try {
			
			FileInputStream fis = new FileInputStream(new File (file)); // obtaining bytes from the file
			disableWarning();
			// creating Workbook instance that refers to .xlsx file
			@SuppressWarnings("resource")
			XSSFWorkbook wb = new XSSFWorkbook(fis);
			XSSFSheet sheet = wb.getSheet("UserDetails"); // creating a Sheet object to retrieve object
			Iterator<Row> itr = sheet.iterator(); // iterating over excel file
			while (itr.hasNext()) {
				Row row = itr.next();
				Iterator<Cell> cellIterator = row.cellIterator(); // iterating over each column
				while (cellIterator.hasNext()) {
					Cell cell = cellIterator.next();
					switch (cell.getCellType()) {
					case Cell.CELL_TYPE_STRING: // field that represents string cell type
						System.out.print(cell.getStringCellValue() + "\t\t\t");
						break;
					case Cell.CELL_TYPE_NUMERIC: // field that represents number cell type
						System.out.print(cell.getNumericCellValue() + "\t\t\t");
						break;
					default:
					}
				}
				System.out.println("");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
