package pkg1;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import org.testng.Assert;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.github.bonigarcia.wdm.WebDriverManager;

public class Base {

	protected static WebDriver driver;
	protected WebDriverWait wait;

	public Base(WebDriver driver, WebDriverWait wait) {
		this.driver = driver;
		this.wait = wait;
	}

	public WebDriver getDriver() {
		return driver;
	}

//	    public WebDriverWait getWebDriverWait() {
//	    	return wait;
//	    }

	public WebDriver chromeDriverConnection() {
		WebDriverManager.chromedriver().setup();
		driver = new ChromeDriver(new ChromeOptions().addArguments("--remote-allow-origins=*"));
		wait = new WebDriverWait(driver, Duration.ofSeconds(3));
		return driver;
	}

	protected void restartSession(String url) {
		if (driver != null) {
			try {
				driver.quit();
			} catch (Exception e) {
				System.out.println("Error while closing driver: " + e.getMessage());
			}
		}
		driver = chromeDriverConnection();
		wait = new WebDriverWait(driver, Duration.ofSeconds(5));
		visit(url);
		
		 // Switch to the active window
	    String currentHandle = driver.getWindowHandle();
	    for (String handle : driver.getWindowHandles()) {
	        if (!handle.equals(currentHandle)) {
	            driver.switchTo().window(handle);
	        }
	    }
	    
	    
	}
//===========================================
//	protected boolean isServerError() {
//		try {
//			String pageSource = driver.getPageSource().toLowerCase();
//			return pageSource.contains("500") && pageSource.contains("server error");
//		} catch (Exception e) {
//			return false;
//		}
//	}
//=========================================
	public void waitUntilElementLocated(By locator) {
		wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
	}

	public WebElement findElement(By locator) {
		return driver.findElement(locator);
	}

	public List<WebElement> findElements(By locator) {
		return driver.findElements(locator);
	}

	public String getText(WebElement element) {
		return element.getText();
	}

	public String getText(By locator) {
		return driver.findElement(locator).getText();
	}

	public WebElement type(String inputText, By locator) {
		findElement(locator).sendKeys(inputText);
		return findElement(locator);
	}

	public void type(By locator, String inputText) {
		findElement(locator).sendKeys(inputText);
	}

	public void click(By locator) {
		driver.findElement(locator).click();
	}

	public boolean isDisplayed(By locator) {
		try {
			return driver.findElement(locator).isDisplayed();
		} catch (NoSuchElementException e) {
			return false;
		}
	}

	public void selectDropDownList(By locator, String value) {
		Select select = new Select(driver.findElement(locator));
		select.selectByValue(value);
	}

	public void visit(String url) {
		driver.get(url);
		driver.manage().window().maximize();
		driver.manage().timeouts().implicitlyWait(Duration.ofMillis(3000));
	}

	public void assertIfContains(String actualText, String ExpectedText) {
		Assert.assertTrue(actualText.contains(ExpectedText), "Text verification failed!");
	}

	public void switchToLastWindow() {
		for (String windowHandle : driver.getWindowHandles()) {
			driver.switchTo().window(windowHandle);
		}
	}

	public void delete(By locator) {
//		findElement(locator).clear();
		WebElement element = findElement(locator);
		element.click(); // Focus on the field
		element.sendKeys(Keys.CONTROL + "a"); // Select all text
		element.sendKeys(Keys.BACK_SPACE);
	}

	public String createDate() {
		Date now = new Date();
		String date = new SimpleDateFormat("dd-MM-yyyy").format(now);
		return date;
	}

	public String createTime() {
		Date now = new Date();
		String time = new SimpleDateFormat("HH:mm:ss").format(now);
		return time;
	}

	public static void quiteDriver() {
		driver.quit();
	} 
	
// =============================================================================
	// "personal_details.xlxs"; sheetNum = 0;
	public static String readExcelCell(int rowNum, int colNum, String fileName, int sheetNum) {
		// Open the file and get the sheet
		try (FileInputStream f = new FileInputStream(fileName); XSSFWorkbook workbook = new XSSFWorkbook(f)) {

			// Get the specified sheet from the workbook
			XSSFSheet sheet = workbook.getSheetAt(sheetNum);

			// Get the specified row from the sheet
			Row row = sheet.getRow(rowNum);

			// If row is null (e.g. empty row), return an empty string
			if (row == null) {
				return "";
			}

			// Get the specified cell from the row
			Cell cell = row.getCell(colNum);

			// If cell is null, return an empty string
			if (cell == null) {
				return "";
			}

			// Return the cell's value based on its type
			switch (cell.getCellType()) {
			case NUMERIC:
				if (DateUtil.isCellDateFormatted(cell)) {
					// If the cell contains a date, format it as a date string
					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy"); // Desired date format
					return sdf.format(cell.getDateCellValue()); // Convert the date to string using the format
				} else {
					// Handle large numbers (e.g., phone numbers, IDs)
					String value = String.valueOf((long) cell.getNumericCellValue());
					if (colNum == 3) { // checking if it's column 4 which contains phone numbers without 0 in the
										// beginning
						return "0" + value; // adding 0 to the phone number
					} else {
						return value;
					}
				}
			case STRING:
				return cell.getStringCellValue();
			case BOOLEAN:
				return Boolean.toString(cell.getBooleanCellValue());
			case FORMULA:
				return cell.getCellFormula(); // In case it's a formula
			default:
				return "";
			}

//			return switch (cell.getCellType()) {
//			case NUMERIC -> Double.toString(cell.getNumericCellValue());
//			case STRING -> cell.getStringCellValue();
//			case BOOLEAN -> Boolean.toString(cell.getBooleanCellValue());
//			case FORMULA -> cell.getCellFormula(); // in case it's a formula
//			default -> "";
//			};

		} catch (IOException e) {
			e.printStackTrace();
			return ""; // return empty string in case of exception
		}
	}

	// =========================================================================================
	public static void readTheExcel(String fileName) {

		// Open the file and get sheet(0)
		try (FileInputStream f = new FileInputStream(fileName); XSSFWorkbook workbook = new XSSFWorkbook(f);) {

			// Get a sheet from the workbook
			XSSFSheet sheet = workbook.getSheetAt(0);

			// for each row create a OneRowData and
			// insert the info of the row into its fields
			// and add it to our List
			// Iterate through each rows one by one
			Iterator<Row> rowIterator = sheet.iterator();
			rowIterator.next(); // דילוג על השורה הראשונה, זו שורת הכותרות של העמודות
			while (rowIterator.hasNext()) {
				Row row = (Row) rowIterator.next();

				// For each row, iterate through all the columns
				Iterator<Cell> cellIterator = row.cellIterator();
				int colNum = 0;
				while (cellIterator.hasNext()) {
					Cell cell = (Cell) cellIterator.next();

					// Print the cell types
					// System.out.print("|type = " + cell.getCellType() +" ");

					String st = switch (cell.getCellType()) {
					case NUMERIC -> Double.toString(cell.getNumericCellValue());
					case STRING -> cell.getStringCellValue();
					case BOOLEAN -> Boolean.toString(cell.getBooleanCellValue());
					default -> "";
					};

					System.out.println(st);
					colNum++;
					System.out.println("moving to colNum: " + colNum);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// =========================================================================================
	// Method to read a single row into an array
	public static String[] readExcelRow(int rowNum, String fileName, int sheetNum) {
		try (FileInputStream f = new FileInputStream(fileName); XSSFWorkbook workbook = new XSSFWorkbook(f)) {

			// Get the specified sheet from the workbook
			XSSFSheet sheet = workbook.getSheetAt(sheetNum);

			// Get the specified row from the sheet
			Row row = sheet.getRow(rowNum);

			// If the row is null, return an empty array
			if (row == null) {
				return new String[0];
			}

			// Get the number of cells in the row (max column index + 1)
			int numCells = row.getPhysicalNumberOfCells();

			// Create an array to store the cell values
			String[] rowData = new String[numCells];

			// Iterate through each cell in the row and store the value in the array
			for (int i = 0; i < numCells; i++) {
				Cell cell = row.getCell(i);

				// Handle the case where the cell might be null
				if (cell == null) {
					rowData[i] = "";
				} else {
					// Read the cell value based on the type
					rowData[i] = switch (cell.getCellType()) {
					case NUMERIC -> Double.toString(cell.getNumericCellValue());
					case STRING -> cell.getStringCellValue();
					case BOOLEAN -> Boolean.toString(cell.getBooleanCellValue());
					case FORMULA -> cell.getCellFormula();
					default -> "";
					};
				}
			}

			return rowData;

		} catch (IOException e) {
			e.printStackTrace();
			return new String[0]; // Return an empty array in case of an error
		}

	}

	// ===================================================================================
	// Method to write a value into a specific cell
	public static void writeExcelCell(int rowNum, int colNum, String value, String fileName, int sheetNum) {
		try (FileInputStream f = new FileInputStream(fileName); XSSFWorkbook workbook = new XSSFWorkbook(f)) {

			// Get the specified sheet from the workbook
			XSSFSheet sheet = workbook.getSheetAt(sheetNum);

//			if (sheetNum < workbook.getNumberOfSheets()) {
//				sheet = workbook.getSheetAt(sheetNum);
//			} else {
//				sheet = workbook.createSheet("Sheet" + (sheetNum + 1));
//			}

			// Get the specified row, if row doesn't exist, create it
			Row row = sheet.getRow(rowNum);
			if (row == null) {
				row = sheet.createRow(rowNum); // Create a new row if it doesn't exist
			}

			// Get the specified cell, if cell doesn't exist, create it
			Cell cell = row.getCell(colNum);
			if (cell == null) {
				cell = row.createCell(colNum); // Create a new cell if it doesn't exist
			}

			// Write the value to the cell
			cell.setCellValue(value);

			// Save the changes to the Excel file
			try (FileOutputStream out = new FileOutputStream(fileName)) {
				workbook.write(out); // Write the updated workbook to the file
			}

			System.out.println("Cell updated successfully!");

		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Error while updating the cell.");
		}

	}

	// ===================================================================================
	// Method to write an entire row into an Excel sheet
	public static void writeExcelRow(int rowNum, String[] rowData, String fileName, int sheetNum) {
		try (FileInputStream f = new FileInputStream(fileName); XSSFWorkbook workbook = new XSSFWorkbook(f)) {

			// Get the specified sheet from the workbook
			XSSFSheet sheet = workbook.getSheetAt(sheetNum);

			// Create styles for Pass and Fail
			XSSFCellStyle passStyle = workbook.createCellStyle();
			passStyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
			passStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

			XSSFCellStyle failStyle = workbook.createCellStyle();
			failStyle.setFillForegroundColor(IndexedColors.RED.getIndex());
			failStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

			// Get the specified row, if row doesn't exist, create it
			Row row = sheet.getRow(rowNum);
			if (row == null) {
				row = sheet.createRow(rowNum); // Create a new row if it doesn't exist
			}

			// Iterate through the provided rowData array and write each value to the cell
			for (int i = 0; i < rowData.length; i++) {
				// Get the cell at the current column index, or create it if it doesn't exist
				Cell cell = row.getCell(i);
				if (cell == null) {
					cell = row.createCell(i); // Create a new cell if it doesn't exist
				}

				// Write the value into the cell
				cell.setCellValue(rowData[i]);

				if (i == 2) {
					if ("Pass".equalsIgnoreCase(rowData[i])) {
						cell.setCellStyle(passStyle);
					} else if ("Fail".equalsIgnoreCase(rowData[i])) {
						cell.setCellStyle(failStyle);
					}
				}
			}

			// Save the changes to the Excel file
			try (FileOutputStream out = new FileOutputStream(fileName)) {
				workbook.write(out); // Write the updated workbook to the file
			}

			System.out.println("Row updated successfully!");

		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Error while updating the row.");
		}
	}

	// ===================================================================================
	// Method to write a value into a specific column for all rows
	public static void writeExcelColumn(int colNum, String value, String fileName, int sheetNum, int startRow,
			int endRow) {
		try (FileInputStream f = new FileInputStream(fileName); XSSFWorkbook workbook = new XSSFWorkbook(f)) {

			// Get the specified sheet from the workbook
			XSSFSheet sheet = workbook.getSheetAt(sheetNum);

			// Iterate through rows from startRow to endRow and write the value to the
			// specified column
			for (int rowNum = startRow; rowNum <= endRow; rowNum++) {
				// Get the row, create it if it doesn't exist
				Row row = sheet.getRow(rowNum);
				if (row == null) {
					row = sheet.createRow(rowNum); // Create a new row if it doesn't exist
				}

				// Get the cell in the specified column, create it if it doesn't exist
				Cell cell = row.getCell(colNum);
				if (cell == null) {
					cell = row.createCell(colNum); // Create a new cell if it doesn't exist
				}

				// Write the value into the cell
				cell.setCellValue(value);
			}

			// Save the changes to the Excel file
			try (FileOutputStream out = new FileOutputStream(fileName)) {
				workbook.write(out); // Write the updated workbook to the file
			}

			System.out.println("Column updated successfully!");

		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Error while updating the column.");
		}
	}
} ////////////////////////////////////////////////////////////////////////////////////////