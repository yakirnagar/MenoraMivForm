package pkg1;

import java.time.Duration;
import java.util.Set;

import org.testng.Assert;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchSessionException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class SearchBar extends Base {

	public SearchBar(WebDriver driver, WebDriverWait wait) {
		super(driver, wait);
	}

	By searchBar = By.cssSelector("#headerSearchInput");
	By numOfResults = By.cssSelector("div > h1");
	By noResultsFound = By.cssSelector(
			".NotFoundResults__NoResultsWrapper-sc-1mukwur-2.ddrbIK > div:nth-of-type(2) > div:nth-of-type(1) > span:nth-of-type(1)");

	private String expectedTextIfFound = "נמצאו עבורך";
	private String expectedTextIfNotFound = "לא נמצאו תוצאות עבור";
	private String actualText = "";

//	POSITIVE TESTS: 

//    חיפוש מילה בעברית
	public void validTextInSearchBar() {
		findElement(searchBar);
		type("פנסיה" + Keys.ENTER, searchBar);
		assertIfContains(getText(numOfResults), expectedTextIfFound);
	}

//	חיפוש מילה באנגלית
	public void validSearchBarTextInEnglish() {
		findElement(searchBar);
		type("pension" + Keys.ENTER, searchBar);
		assertIfContains(getText(numOfResults), expectedTextIfFound);
	}

//	חיפוש מילה בערבית
	public void validSearchBarTextInArabic() {
		try {

			// Make sure we're on the right window and wait for element
			WebDriverWait extendedWait = new WebDriverWait(driver, Duration.ofSeconds(10));
			extendedWait.until(ExpectedConditions.presenceOfElementLocated(searchBar));

			// Switch to the active window if needed
			String currentHandle = driver.getWindowHandle(); // מקבל כתובת של טאב ראשון של הדרייבר שנפתח
			Set<String> handles = driver.getWindowHandles(); // אוסף את כל הכתובות של הטאבים שנפתחו ושם אותם בסט (שזה כמו ליסט, רק פחות מסודר)
			if (handles.size() > 1) { // אם יש מעל טאב/חלון אחד 
				for (String handle : handles) { // רוץ על כל כתובת של טאב/חלון
					if (!handle.equals(currentHandle)) { // אם (כתובת) החלון שונה מ(כתובת) החלון הנוכחי
						driver.switchTo().window(handle); // תעביר את המיקוד לחלון הרלוונטי (שעליו אמורה להתבצע הפקודה)
						break;
					}
				}
			}

			WebElement searchElement = extendedWait.until(ExpectedConditions.elementToBeClickable(searchBar));
			searchElement.sendKeys("التوفير" + Keys.ENTER);

			// Your existing navigation code...
			try {
				getDriver().navigate().back();
			} catch (NoSuchSessionException e) {
				System.out.println("Session crashed. Reinitializing...");
				restartSession("https://www.menoramivt.co.il/");
			}

		} catch (Exception e) {
			System.out.println("Error during test execution: " + e.getMessage());
			restartSession("https://www.menoramivt.co.il/");
			throw e;
		}
	}

//==================================================================
//	חיפוש מספר
	public void validSearchBarOfNumbers() {
		findElement(searchBar);
		type("30" + Keys.ENTER, searchBar);
		assertIfContains(getText(numOfResults), expectedTextIfFound);
	}

//	חיפוש טקסט בעברית עם מספר
	public void textWithNumbers() {
		findElement(searchBar);
		type("השקעה 30" + Keys.ENTER, searchBar);
		assertIfContains(getText(numOfResults), expectedTextIfFound);
	}

//	חיפוש ביטוי בעברית
	public void validSearchBarOfExpressions() {
		findElement(searchBar);
		type("השקעת מחשבה" + Keys.ENTER, searchBar);
		assertIfContains(getText(numOfResults), expectedTextIfFound);
	}

//	חיפוש ביטוי שבין מילותיו יש רווח כפול
	public void expressionWithTwoSpaceBetween() {
		findElement(searchBar);
		type("השקעת  מחשבה" + Keys.ENTER, searchBar);
		assertIfContains(getText(numOfResults), expectedTextIfFound);
	}

//	NEGATIVE TESTS:

//	חיפוש ביטוי שאין רווח כלל בין מילותיו
	public void expressionsWithoutSpace() {
		findElement(searchBar);
		type("השקעתמחשבה" + Keys.ENTER, searchBar);
		assertIfContains(getText(noResultsFound), expectedTextIfNotFound);
	}

//	חיפוש טקסט שמכיל גם תווים
	public void textWithSymbol() {
		findElement(searchBar);
		type("השקעה," + Keys.ENTER, searchBar);
		assertIfContains(getText(noResultsFound), expectedTextIfNotFound);
	}

//	חיפוש מילה עם מספר ללא רווח
	public void textIncludingNumbersWithoutSpace() {
		findElement(searchBar);
		type("השקעה30" + Keys.ENTER, searchBar);
		assertIfContains(getText(noResultsFound), expectedTextIfNotFound);
	}

//	חיפוש תווים
	public void sympbols() {
		findElement(searchBar);
		type("%" + Keys.ENTER, searchBar);
		assertIfContains(getText(noResultsFound), expectedTextIfNotFound);
	}
}
