package hk.edu.hkmu.lib;

import java.util.List;
import java.util.concurrent.TimeUnit;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

import hk.edu.hkmu.lib.CJKStringHandling;
import hk.edu.hkmu.lib.StringHandling;

import org.openqa.selenium.chrome.*;
import org.openqa.selenium.interactions.Actions;

/**
 * A congregation of functions for Primo driving for automation using
 * ChromeDriver from Selenium.
 * 
 * @author Wai-yan NG
 * @author wwyng@ouhk.edu.hk
 * @version 1.0
 * @since 4 Mar 2021
 */
public class PrimoDriver {

	public WebDriver driver;
	public WebDriverWait wait;
	private WebElement clickEle;
	public Actions actions;
	private String workingdir;

	public PrimoDriver() {
		Config.init();
		initWebDriver();
	}

	private void initWebDriver() {
		try {
			String workingdir = System.getProperty("user.dir");
			System.setProperty("webdriver.chrome.driver", workingdir + "\\webdriver\\chromedriver.exe");
			driver = new ChromeDriver();
		} catch (Exception e) {

		}
		actions = new Actions(driver);
		driver.manage().timeouts().pageLoadTimeout(40, TimeUnit.SECONDS);
		driver.manage().window().maximize();
		wait = new WebDriverWait(driver, 5);
	}

	public void filterPlatformFacet(String platform) {
		try {
			if (!platform.toLowerCase().trim().equals("none")) {
				Thread.sleep(2000);
				clickMore();

				wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(hk.edu.hkmu.lib.Config.VALUES
						.get("PLATFORMFACET").replaceAll("PLATFORM", platform.toLowerCase()))));

				clickEle = driver.findElement(By.cssSelector(hk.edu.hkmu.lib.Config.VALUES.get("PLATFORMFACET")
						.replaceAll("PLATFORM", platform.toLowerCase())));

				actions.moveToElement(clickEle).click().build().perform();
				Thread.sleep(2000);
			}
		} catch (

		Exception e0) {
			try {

			} catch (Exception e2) {
				e2.printStackTrace();

			}
			e0.printStackTrace();

		}
	} // end filterPlatformFacet()

	// Clicking the facet creator.
	public void filterCreatorFacet(String creator) {
		try {

			// Get out of the function if no value is set.
			if (creator == null || creator.equals("") || creator.toLowerCase().equals("none"))
				return;

			Thread.sleep(2000);
			clickMore();
			wait.until(ExpectedConditions.visibilityOfElementLocated(
					By.cssSelector(hk.edu.hkmu.lib.Config.VALUES.get("CREATORFACET").replaceAll("CREATOR", creator))));

			clickEle = driver.findElement(
					By.cssSelector(hk.edu.hkmu.lib.Config.VALUES.get("CREATORFACET").replaceAll("CREATOR", creator)));

			actions.moveToElement(clickEle).click().build().perform();
			Thread.sleep(2000);

		} catch (Exception e0) {
			e0.printStackTrace();
		}
	} // end filterCreatorFacet()

	public void initSearchKeywordComponent(String keyword) {
		try {
			WebElement searchBar;
			Thread.sleep(2000);
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("searchBar")));
			searchBar = driver.findElement(By.id("searchBar"));
			searchBar.click();
			searchBar.clear();
			searchBar.sendKeys(keyword);
			searchBar.sendKeys(Keys.RETURN);
			searchBar.sendKeys(Keys.TAB);
			if (StringHandling.getTodayDateOfWeek() == 4) {
				driver.navigate().refresh();
				Thread.sleep(5000);
			}
			Thread.sleep(2000);
		} catch (Exception e0) {
			try {
			} catch (Exception e2) {
				e2.printStackTrace();
			}

			e0.printStackTrace();
		}

	} // end initSearchKeywordComponent()

	public boolean isInFRBR() {
		if (driver.findElements(By.cssSelector(hk.edu.hkmu.lib.Config.VALUES.get("ISFRBR"))).size() != 0)
			return true;

		return false;
	}

	// Clicking the facet eBook
	public void filterEBookFacet() {
		try {

			Thread.sleep(2000);
			clickMore();
			wait.until(ExpectedConditions
					.visibilityOfElementLocated(By.cssSelector(hk.edu.hkmu.lib.Config.VALUES.get("EBOOKFACET"))));
			clickEle = driver.findElement(By.cssSelector(hk.edu.hkmu.lib.Config.VALUES.get("EBOOKFACET")));
			actions.moveToElement(clickEle).click().build().perform();
			Thread.sleep(2000);
		} catch (Exception e0) {
			try {
			} catch (Exception e2) {
				e2.printStackTrace();
			}
			e0.printStackTrace();
		}
	} // end filterEBookFacet()

	// Clicking Library Holding facet
	public void filterLibHoldingFacet() {
		try {
			Thread.sleep(2000);
			clickMore();
			wait.until(ExpectedConditions
					.visibilityOfElementLocated(By.cssSelector(hk.edu.hkmu.lib.Config.VALUES.get("LIBHOLDFACET"))));
			clickEle = driver.findElement(By.cssSelector(hk.edu.hkmu.lib.Config.VALUES.get("LIBHOLDFACET")));
			actions.moveToElement(clickEle).click().build().perform();
			Thread.sleep(2000);
		} catch (Exception e0) {
			try {
			} catch (Exception e2) {
				e2.printStackTrace();
			}

			e0.printStackTrace();
		}
	} // end filterLibHoldingFacet(){

	// Clicking all the facets' "more", showing all the facets values before
	// selecting.
	public void clickMore() {
		try {

			Thread.sleep(8000);
			wait.until(ExpectedConditions
					.visibilityOfElementLocated(By.cssSelector("span[translate*='showmore']:first-child")));
			List<WebElement> clickEles = driver.findElements(By.cssSelector("span[translate*='showmore']"));
			for (WebElement ele : clickEles) {

				if (ele.getText().toLowerCase().contains("more"))
					Thread.sleep(1000);
				actions.moveToElement(ele).click().build().perform();
			}

			clickEles = driver.findElements(By.cssSelector("span[translate*='showmore']"));
			for (WebElement ele : clickEles) {

				if (ele.getText().toLowerCase().contains("more"))
					Thread.sleep(1000);
				actions.moveToElement(ele).click().build().perform();
			}

			Thread.sleep(2000);
		} catch (Exception e0) {
			try {

			} catch (Exception e2) {
				e2.printStackTrace();

			}

			e0.printStackTrace();

		}

	}

}
