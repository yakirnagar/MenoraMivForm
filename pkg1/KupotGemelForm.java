package pkg1;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

public class KupotGemelForm extends Base {

	public KupotGemelForm(WebDriver driver, WebDriverWait wait) {
		super(driver, wait);
	}

	private By buttonToJoin = By.cssSelector("[type=\"button\"] > span > p");
	
	public By getButtonToJoin() { // פונקציה שמחזירה את הסלקטור של כפתור ההצטרפות בעמוד הראשון שנפתח
		return buttonToJoin;
	}

	By firstName = By.cssSelector("[type=\"firstName\"]");
	By lastName = By.cssSelector("[type=\"lastName\"]");
	By email = By.cssSelector("[type=\"customerEmail\"]");
	By phone = By.cssSelector("[type=\"customerPhone\"]");
	By idNumber = By.cssSelector("[type=\"insuredId\"]");
	By birthDate = By.cssSelector("[type=\"text\"]");
//	By checkbox2 = By.cssSelector(".CheckBox__Wrapper-sc-v4fzpu-1.gBAYoC [type=\"checkbox\"]");
	By checkbox2 = By.cssSelector("[aria-describedby=\"confirmation-marketing-checkbox\"]");

	public void FillInForm() {
		waitUntilElementLocated(firstName);
		
		for (int i = 1; i < 4; i++) { // ירוץ על מספר השורות שיש באקסל

			String Pass_Failed;
	        String exception;
	        
	        try {
			type(firstName, readExcelCell(i, 0, "personal_details.xlsx", 0));
			type(lastName, readExcelCell(i, 1, "personal_details.xlsx", 0));
			type(email, readExcelCell(i, 2, "personal_details.xlsx", 0));
			type(phone, readExcelCell(i, 3, "personal_details.xlsx", 0));
			type(idNumber, readExcelCell(i, 4, "personal_details.xlsx", 0));
			type(birthDate, readExcelCell(i, 5, "personal_details.xlsx", 0));
			click(checkbox2); // סימון וי בתיבת הסימון

			 // If everything passes, mark the test as "Pass"
            Pass_Failed = "Pass";
            exception = "";

        } catch (Exception e) {
            // If an exception occurs, mark the test as "Fail" and capture the exception message
            Pass_Failed = "Fail";
            exception = e.getMessage();
        }
			
			delete(firstName);
			delete(lastName);
			delete(email);
			delete(phone);
			delete(idNumber);
			delete(birthDate);
			click(checkbox2); // להסרת סימון וי מתיבת הסימון

//			writeExcelCell(int rowNum, int colNum, String value, String fileName, int sheetNum)
//			writeExcelCell(i, 0, "", "personal_details.xlsx", 1);
//			writeExcelCell(i, 1, "", "personal_details.xlsx", 1);
//			writeExcelCell(i, 2, "", "personal_details.xlsx", 1);
//			writeExcelCell(i, 3, "", "personal_details.xlsx", 1);
//			writeExcelCell(i, 4, "", "personal_details.xlsx", 1);
//			writeExcelCell(i, 5, "", "personal_details.xlsx", 1);
			
			
			String testTitle = readExcelCell(i, 6, "personal_details.xlsx", 0); // gets content of test title from sheet 0
			String[] strArr = {"" + i, testTitle, Pass_Failed, createDate(), createTime(), exception };
//			writeExcelRow(int rowNum, String[] rowData, String fileName, int sheetNum)
			writeExcelRow(i, strArr,"personal_details.xlsx", 1);
		}
	}
} //////////////////////////////////////////////////////
