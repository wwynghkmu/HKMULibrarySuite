import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;
import org.openqa.selenium.chrome.*;

public class TestWebDriver {

	public static void main(String[] args) {
		try {
			String title = "";
			WebElement clickEle;
			String workingdir = System.getProperty("user.dir");
			System.setProperty("webdriver.chrome.driver", workingdir + "\\webdriver\\chromedriver.exe");
			WebDriver driver = new ChromeDriver();
			driver.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);
			driver.manage().window().maximize();
			WebDriverWait wait = new WebDriverWait(driver, 30000);
			driver.get("https://www2.lib.ouhk.edu.hk/primo.html");

			wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("searchBar")));
			WebElement searchBar = driver.findElement(By.id("searchBar"));
			searchBar.click();
			searchBar.sendKeys("ebook ebsco");
			searchBar.sendKeys(Keys.RETURN);
			try {
				wait.until(ExpectedConditions.visibilityOfElementLocated(
						By.cssSelector("span[translate='facets.facet.tlevel.lib_holding']")));
				clickEle = driver.findElement(By.cssSelector("span[translate='facets.facet.tlevel.lib_holding']"));
				clickEle.click();
				wait.until(ExpectedConditions
						.visibilityOfElementLocated(By.cssSelector("span[translate='facets.facet.facet_rtype.books")));
				clickEle = driver.findElement(By.cssSelector("span[translate='facets.facet.facet_rtype.books']"));
				clickEle.click();

				wait.until(ExpectedConditions
						.visibilityOfElementLocated(By.cssSelector("prm-search-result-thumbnail-container")));
				clickEle = driver.findElement(By.cssSelector("prm-search-result-thumbnail-container:first-child"));
				clickEle.click();

				Thread.sleep(2000);
				driver.navigate().refresh();

				wait.until(ExpectedConditions
						.visibilityOfElementLocated(By.cssSelector("prm-search-result-thumbnail-container")));

				List<WebElement> linksEles = driver.findElements(By.cssSelector("span[ng-if='link.link.length>0']"));

				List<WebElement> titleEles = driver.findElements(By.cssSelector("div[class=item-details-element]"));
				title = titleEles.get(0).getText();

				for (WebElement ele : linksEles) {
					if (ele.getText().toLowerCase().contains("ebsco")) {
						ele.click();
						break;
					}
				}

				ArrayList<String> tabs2 = new ArrayList<String>(driver.getWindowHandles());
				driver.switchTo().window(tabs2.get(1));

			} catch (NoSuchElementException ex) {
				ex.printStackTrace();
			}

			File screenshotFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
			FileUtils.copyFile(screenshotFile, new File("d:\\" + title + ".jpg"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
