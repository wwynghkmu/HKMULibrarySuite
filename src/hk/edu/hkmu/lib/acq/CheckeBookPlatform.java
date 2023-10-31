package hk.edu.hkmu.lib.acq;

import java.awt.GraphicsDevice;
import com.jcraft.jsch.*;
import hk.edu.hkmu.lib.*;
import java.awt.GraphicsEnvironment;
import java.io.*;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.*;
import java.util.List;
import java.util.Random;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;
import javax.swing.*;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.font.TextAttribute;
import java.text.AttributedString;

/**
 * This class is is for ACQ's eBook platform checking automatically. The class
 * uses Selenium's webdriver to drive Chrome to Check all eBook platforms. After
 * checking, the class saves captured screens to a SFTP server and records the
 * checking results to MySQL server.
 * 
 * @author Wai-yan NG
 * @author wwyng@ouhk.edu.hk
 * @version 1.0
 * @since 2 Mar 2021
 */

public class CheckeBookPlatform extends JFrame {
	private static final long serialVersionUID = 1L;
	private String[][] platforms;
	String title;
	String savePath;
	String logPath;
	String starttime;
	private HashMap<String, String[]> searchList;
	private JLabel progressJLabel;
	private JLabel checkingPlatformJLabel;
	private JLabel inProcessJLabel;
	WebElement clickEle;
	private String workingdir;
	private int batchID;
	private PrimoDriver pdriver;
	private LogWriter logwriter;
	private LogWriter htmlLogwriter;

	/*
	 * Print text to the console and save it to the log file.
	 */
	private void out(String str) {
		try {
			logwriter.out(str);
			inProcessJLabel.setText(str);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int getBatchID() {
		return batchID;
	}

	public String[][] getPlatforms() {
		return platforms;
	}

	public CheckeBookPlatform() {
		Config.init();
		logwriter = new LogWriter();
		htmlLogwriter = null;
		logwriter.setLogFile("eBookPlatformLog" + StringHandling.getToday() + ".txt");
		starttime = "";
		title = "";
		batchID = 0;
		searchList = null;
		setPath();
		initGUI();
		// initPlatformExcel();
		initPlatformList();
		getNewBatchIDFromDB();
		pdriver = new PrimoDriver();
		checkPlatform();
		saveReportToDB();
		logwriter.close();
		finishing();
	}

	private void getNewBatchIDFromDB() {
		try {
			String sql;
			ResultSet rs = null;
			Connection conn = null;
			Statement stmt = null;
			Class.forName(hk.edu.hkmu.lib.Config.JDBC_DRIVER);
			conn = DriverManager.getConnection(hk.edu.hkmu.lib.Config.DB_URL, hk.edu.hkmu.lib.Config.USER,
					hk.edu.hkmu.lib.Config.PASS);
			sql = "SELECT MAX(batchID) FROM ebkplatformcheckingreport";
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			rs.next();
			batchID = rs.getInt(1) + 1;
		} catch (Exception e) {
			e.printStackTrace();
			out(e.getMessage());
		}
	}

	private void saveReportToDB() {
		try {
			String sql;
			Connection conn = null;
			Statement stmt = null;
			for (int i = 0; i < platforms.length; i++) {
				if (platforms[i][0] != null) {
					if (!platforms[i][1].equals("NONE")) {
						if (platforms[i][6] == null || platforms[i][6].equals(""))
							platforms[i][6] = hk.edu.hkmu.lib.StringHandling.getTodayDBFormat();
						Thread.sleep(2000);
						out("platform " + platforms[i][1] + "Checking time: " + platforms[i][6] + " Checking result: "
								+ platforms[i][5]);
						Class.forName(hk.edu.hkmu.lib.Config.JDBC_DRIVER);

						conn = DriverManager.getConnection(hk.edu.hkmu.lib.Config.DB_URL, hk.edu.hkmu.lib.Config.USER,
								hk.edu.hkmu.lib.Config.PASS);
						stmt = conn.createStatement();
						sql = "INSERT INTO ebkplatformcheckingreport (batchID, timestamp, platform, searchKeyword, checkstatus, remark) VALUES ("
								+ batchID + ", '" + platforms[i][6] + "', '" + platforms[i][1] + "', ' "
								+ platforms[i][2] + "', '" + platforms[i][5] + "'" + ", '" + platforms[i][7] + "')";
						stmt.execute(sql);
					}
				}
			}
			if (stmt != null)
				stmt.close();
			if (conn != null)
				conn.close();
		} catch (Exception e) {
			e.printStackTrace();
			out(e.getMessage());
		}
	}

	// Killing all invoked OS processes.
	private void finishing() {
		try {
			Runtime.getRuntime().exec("taskkill /im chromepdriver.driver.exe /f");
			Runtime.getRuntime().exec("taskkill /im chrome.exe /f");
			Runtime.getRuntime().exec("taskkill /im javaw.exe /f");
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
			out(e.getMessage());
		}
	}

	private void setPath() {
		workingdir = System.getProperty("user.dir");
		File dir = new File(workingdir + "\\savedScreens");
		if (!dir.exists())
			dir.mkdir();
		savePath = workingdir + "\\savedScreens\\";

	}

	/*
	 * Reading eBook platform list into the array.
	 */
	private void initPlatformList() {
		platforms = new String[Config.PLATFORMS.size()][9];
		searchList = new HashMap<String, String[]>();
		int count = 0;
		for (Map.Entry<String, String[]> entry : Config.PLATFORMS.entrySet()) {

			String platformName = "";
			String primoKeyword = "";
			String primoLink = "";
			String searchType = "";
			String primoCreatorFacet = "";

			platformName = entry.getValue()[1];

			// if (platformName.contains("AVON")) {
			primoKeyword = entry.getValue()[2];
			primoLink = entry.getValue()[3];
			searchType = entry.getValue()[0];
			primoCreatorFacet = entry.getValue()[4];

			platforms[count][0] = searchType;
			platforms[count][1] = platformName;
			platforms[count][2] = primoKeyword;
			//
			platforms[count][3] = primoLink;
			platforms[count][4] = primoCreatorFacet;
			platforms[count][5] = "FAILED";
			platforms[count][5] = "";

			// if (platforms[count][1].contains("萬方視頻")) {

			count++;

			// Reader the keyword list
			if (searchType.toLowerCase().equals("keyword")) {
				String[] list = new String[Config.SEARCHITEMS.get(platformName).size()];
				int count2 = 0;
				for (String item : Config.SEARCHITEMS.get(platformName)) {
					list[count2] = item;

					count2++;

				}
				searchList.put(platformName.toUpperCase(), list);

			}
			// }
		}
	}

	private void initGUI() {
		setTitle("ACQ's eBook Platform Checking Tool");
		Date date = new Date();
		long time = date.getTime();
		Timestamp ts = new Timestamp(time);

		setLayout(null);
		setSize(600, 100);
		progressJLabel = new JLabel();
		progressJLabel.setBounds(0, 0, 600, 20);
		progressJLabel.setText("Checking status: in progress (start time: " + ts + ")");
		progressJLabel.setVisible(true);
		starttime = ts + "";

		checkingPlatformJLabel = new JLabel();
		checkingPlatformJLabel.setBounds(0, 20, 700, 20);
		checkingPlatformJLabel.setText("");
		checkingPlatformJLabel.setVisible(true);

		inProcessJLabel = new JLabel();
		inProcessJLabel.setBounds(0, 30, 700, 20);
		inProcessJLabel.setText("");
		inProcessJLabel.setVisible(true);

		add(checkingPlatformJLabel);
		add(progressJLabel);
		add(inProcessJLabel);
		setVisible(true);
		setAlwaysOnTop(true);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice defaultScreen = ge.getDefaultScreenDevice();
		java.awt.Rectangle rect = defaultScreen.getDefaultConfiguration().getBounds();

		int y = (int) rect.getMaxY() - getHeight();
		setLocation(0, y - 30);

		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				try {
					pdriver.driver.quit();
				} catch (Exception e) {
					e.printStackTrace();
					out(e.getMessage());
				}
			}
		});

	}

	private void completeChecking() {
		Date date = new Date();
		long time = date.getTime();
		Timestamp ts = new Timestamp(time);
		progressJLabel.setText("Checking status: complete (start time: " + starttime + " end time: " + ts + ")");
	}

	private void checkPlatform() {
		try {
			for (int i = 0; i < platforms.length; i++) {
				if (platforms[i][0] != null) {
					out("\n\nTarget Platform Information:");
					out("Search Type: " + platforms[i][0]);
					out("Ebk Platform: " + platforms[i][1]);
					checkingPlatformJLabel.setText(platforms[i][1]);
					out("Searching Keyword on Primo: " + platforms[i][2]);
					out("Platform Keyword on Primo's Accessing Link: " + platforms[i][3]);
					out("Creator Keyword on Primo's Facet: " + platforms[i][4]);
					ArrayList<String> tabs = new ArrayList<String>(pdriver.driver.getWindowHandles());
					for (int j = 1; j < tabs.size(); j++) {
						pdriver.driver.switchTo().window(tabs.get(j));
						pdriver.driver.close();
					}
					hk.edu.hkmu.lib.Config.VALUES.put("PRIMOASEARCHURL",
							"https://hkmu.primo.exlibrisgroup.com/discovery/search?&tab=LibraryCatalog&search_scope=MyInstitution&vid=852HKMU_INST:hkmu&mode=advanced&offset=0&query=lds03,contains,");
					System.out.println("PrimoURL:" + hk.edu.hkmu.lib.Config.VALUES.get("PRIMOASEARCHURL"));

					if (platforms[i][6] == null || platforms[i][6].equals(""))
						platforms[i][6] = hk.edu.hkmu.lib.StringHandling.getTodayDBFormat();
					// Performing Platform searching using Advanced Search.
					if (platforms[i][0].equals("platform")) {
						try {
							pdriver.driver.get(hk.edu.hkmu.lib.Config.VALUES.get("PRIMOASEARCHURL") + URLEncoder
									.encode(platforms[i][2], java.nio.charset.StandardCharsets.UTF_8.toString()));

							out("Finding the SearchBar");
							// pdriver.initAdvancedSearchKeywordComponent(platforms[i][2]);
							if (!platforms[i][2].toLowerCase().contains("video")) {
								out("Clicking eBook facet");
								pdriver.filterEBookFacet();
							}
							navigateResultPage(i);
						} catch (Exception e) {
							try {
								logwriter.out(e.getMessage());
							} catch (Exception e2) {
								e2.printStackTrace();
								out(e2.getMessage());
							}

							e.printStackTrace();
							out(e.getMessage());
						}

						// Performing Keyword searching.
					} else if (platforms[i][0].toLowerCase().equals("keyword")) {
						System.out.println("Search by KEYWORD");

						Random r = new Random();
						int isbnIndex = 0;
						String keyword = "";
						if (searchList != null && searchList.get(platforms[i][1].toUpperCase()) != null
								&& searchList.get(platforms[i][1].toUpperCase()).length > 0) {

							isbnIndex = r.nextInt(searchList.get(platforms[i][1].toUpperCase()).length);
							keyword = searchList.get(platforms[i][1].toUpperCase())[isbnIndex];
							hk.edu.hkmu.lib.Config.VALUES.put("PRIMOURL",
									"https://hkmu.primo.exlibrisgroup.com/discovery/search?&tab=LibraryCatalog&search_scope=MyInstitution&vid=852HKMU_INST:hkmu&mode=advanced&offset=0&query=any,contains,"
											+ keyword);
							pdriver.driver.get(hk.edu.hkmu.lib.Config.VALUES.get("PRIMOURL"));

							out("\tSearching by ISBN: " + keyword);
							platforms[i][2] = keyword;
							out("Clicking eBook facet");

							pdriver.filterEBookFacet();
							out("Clicking facet_platform");
							pdriver.filterPlatformFacet(platforms[i][4].toLowerCase());

							if (!navigateResultPage(i)) {
								isbnIndex = r.nextInt(searchList.get(platforms[i][1].toUpperCase()).length);
								keyword = searchList.get(platforms[i][1].toUpperCase())[isbnIndex];
								out("Searching by ISBN: " + keyword);
								platforms[i][2] = keyword;
								out("Finding the SearchBar");
								pdriver.initSearchKeywordComponent(keyword);
								out("Clicking eBook facet");
								pdriver.filterEBookFacet();
								navigateResultPage(i);
							}

						}

						// Performing EBA platform checking
					} else if (platforms[i][0].toLowerCase().equals("eba")) {
						pdriver.driver.get(hk.edu.hkmu.lib.Config.VALUES.get("PRIMOASEARCHURL"));
						Random r = new Random();
						int publisherIndex = r.nextInt(searchList.get(platforms[i][1].toUpperCase()).length);

						String publisher = searchList.get(platforms[i][1].toUpperCase())[publisherIndex];
						out("Searching by publisher (EBA platform): " + publisher);
						platforms[i][2] = publisher;
						out("Finding the SearchBar");
						pdriver.initSearchKeywordComponent(publisher);
						out("Clicking eBook facet");
						pdriver.filterEBookFacet();
						out("Clicking platfrom facet");
						pdriver.filterPlatformFacet(platforms[i][4].toLowerCase());
						if (!navigateResultPage(i)) {
							r = new Random();
							publisherIndex = r.nextInt(searchList.get(platforms[i][1].toUpperCase()).length);
							publisher = searchList.get(platforms[i][1].toUpperCase())[publisherIndex];
							out("Searching by publisher (EBA platform): " + publisher);
							platforms[i][2] = publisher;
							out("Finding the SearchBar");
							pdriver.initSearchKeywordComponent(publisher);
							out("Clicking eBook facet");
							pdriver.filterEBookFacet();
							out("Clicking platfrom facet");
							pdriver.filterPlatformFacet(platforms[i][4].toLowerCase());
							navigateResultPage(i);
						}
					}

				}
			} // end for

			completeChecking();
			pdriver.driver.quit();
			out("Completed Platform Checking.");
		} catch (Exception e) {
			e.printStackTrace();
			out(e.getMessage());
		}

	}

	private boolean navigateResultPage(int i) {
		try {
			List<WebElement> resultMainEles = null;
			try {
				pdriver.wait.until(
						ExpectedConditions.visibilityOfElementLocated(By.cssSelector("prm-brief-result-container")));
				resultMainEles = pdriver.driver.findElements(By.cssSelector("prm-brief-result-container"));
				Thread.sleep(2000);

			} catch (Exception e0) {
				try {
					logwriter.out(e0.getMessage());

				} catch (Exception e2) {
					e2.printStackTrace();
					out(e2.getMessage());
				}
				e0.printStackTrace();
				out(e0.getMessage());
			}
			int breifResultSize = resultMainEles.size();
			for (int k = 0; k < breifResultSize; k++) {
				out("nav: " + k + " of " + breifResultSize);
				try {
					Thread.sleep(2000);

					pdriver.wait
							.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".list-item-wrapper")));
					resultMainEles = pdriver.driver.findElements(By.cssSelector(".list-item-wrapper"));

					// pdriver.wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("searchResultsContainer")));
					// resultMainEles =
					// pdriver.driver.findElements(By.id("searchResultsContainer"));

					out("nav: " + k + " of " + breifResultSize);

				} catch (Exception e0) {
					try {
						logwriter.out(e0.getMessage());
					} catch (Exception e2) {
						e2.printStackTrace();
						out(e2.getMessage());
					}
					e0.printStackTrace();
					out(e0.getMessage());
				}
				Thread.sleep(2000);

				Random random = new Random();
				WebElement mainEle = null;

				try {

					if (breifResultSize > 1)
						mainEle = resultMainEles.get(random.ints(0, breifResultSize - 1).findFirst().getAsInt());
					else
						mainEle = resultMainEles.get(0);

					// out("Main ELe Text: " + mainEle.getText());
					// out("Main ELe CSS: " + mainEle.toString());
					pdriver.wait.until(ExpectedConditions
							.visibilityOfElementLocated(By.cssSelector("prm-brief-result-container")));
					clickEle = mainEle.findElement(By.cssSelector("prm-brief-result-container"));
					pdriver.actions.moveToElement(clickEle).click().build().perform();

				} catch (Exception e0) {
					try {
						logwriter.out(e0.getMessage());
					} catch (Exception e2) {
						e2.printStackTrace();
						out(e2.getMessage());
					}
					e0.printStackTrace();
					out(e0.getMessage());
				}

				Thread.sleep(2000);

				try {
					out("Check whether is a FRBRed record.");
					Thread.sleep(2000);
					if (pdriver.isInFRBR()) {
						out("In FRBR Group. Trying to get the online record.");
						pdriver.driver.navigate().refresh();
						Thread.sleep(2000);
						pdriver.wait.until(ExpectedConditions
								.visibilityOfElementLocated(By.cssSelector("prm-brief-result-container")));
						List<WebElement> resultMainElesFRBR = pdriver.driver
								.findElements(By.cssSelector("prm-brief-result-container"));
						mainEle = resultMainElesFRBR.get(0);

						// Trying to find the record with online access
						for (int j = 0; j < resultMainElesFRBR.size(); j++) {
							mainEle = resultMainElesFRBR.get(j);
							if (mainEle.getText().toLowerCase().contains("online")
									|| mainEle.getText().toLowerCase().contains("線上")
									|| mainEle.getText().toLowerCase().contains("线上"))
								break;
						}

						pdriver.wait.until(ExpectedConditions.visibilityOfElementLocated(
								By.cssSelector("prm-search-result-thumbnail-container:first-child")));
						clickEle = mainEle
								.findElement(By.cssSelector("prm-search-result-thumbnail-container:first-child"));
						Thread.sleep(2000);
						pdriver.wait.until(ExpectedConditions.visibilityOfElementLocated(
								By.cssSelector("prm-search-result-thumbnail-container:first-child")));
						pdriver.actions.moveToElement(clickEle).click().build().perform();
						Thread.sleep(2000);
						pdriver.driver.navigate().refresh();
						Thread.sleep(2000);
						if (pdriver.isInFRBR()) {
							pdriver.wait.until(ExpectedConditions.visibilityOfElementLocated(
									By.cssSelector("prm-search-result-thumbnail-container:first-child")));
							pdriver.actions.moveToElement(clickEle).click().build().perform();
							Thread.sleep(2000);
							pdriver.wait.until(ExpectedConditions.visibilityOfElementLocated(
									By.cssSelector("prm-search-result-thumbnail-container:first-child")));
							pdriver.actions.moveToElement(clickEle).click().build().perform();
							pdriver.driver.navigate().refresh();
						}
					} else {
						out("Not in FRBR");
					}

				} catch (Exception e0) {
					try {
						out(e0.getMessage());
						e0.printStackTrace();
					} catch (Exception e2) {
						e2.printStackTrace();
					}
					e0.printStackTrace();
				}

				Thread.sleep(2000);
				pdriver.driver.navigate().refresh();
				Thread.sleep(8000);

				if (goAndSaveScreen(i)) {
					return true;
				}

				if (k + 1 == resultMainEles.size()) {
					if (platforms[i][6] == null || platforms[i][6].equals(""))
						platforms[i][6] = hk.edu.hkmu.lib.StringHandling.getTodayDBFormat();
					platforms[i][5] = "FAILED - Failed after 10 tries. It seems the platform is down.";
				}

				if (pdriver.driver.findElements(By.cssSelector(hk.edu.hkmu.lib.Config.VALUES.get("BACKTORESULTSET")))
						.size() != 0) {
					out("Going back");
					out("K:" + k);
					out("resultMainEles.size()" + breifResultSize);

					pdriver.driver.navigate().back();
					Thread.sleep(4000);
					if (pdriver.isInFRBR()) {
						out("in FRBR. Going back again");
						pdriver.driver.navigate().back();
					}

					Thread.sleep(2000);
				}

			} // end for
			return false;

		} catch (

		Exception ex) {
			try {
				logwriter.out(ex.getMessage());
			} catch (Exception e2) {
				e2.printStackTrace();
			}
			ex.printStackTrace();
		}
		return false;
	} // end navigateResultPage

	private boolean goAndSaveScreen(int i) {
		String todayDB = hk.edu.hkmu.lib.StringHandling.getTodayDBFormat();
		String today = hk.edu.hkmu.lib.StringHandling.getToday();
		String checkTitle = "";
		try {
			List<WebElement> linksEles = null;
			List<WebElement> titleEles = null;
			try {
				out("Getting all links of the target title.");
				Thread.sleep(4000);
				/*
				 * pdriver.wait.until(ExpectedConditions.visibilityOfElementLocated(
				 * By.cssSelector(hk.edu.hkmu.lib.Config.VALUES.get("VIEWONLINELINK"))));
				 */
				pdriver.wait.until(ExpectedConditions
						.visibilityOfElementLocated(By.cssSelector("md-list-item[ng-repeat*='getServices']")));

				/*
				 * linksEles = pdriver.driver
				 * .findElements(By.cssSelector(hk.edu.hkmu.lib.Config.VALUES.get(
				 * "VIEWONLINELINK"))); titleEles = pdriver.driver
				 * .findElements(By.cssSelector(hk.edu.hkmu.lib.Config.VALUES.get("BRIEFCONTENT"
				 * )));
				 */
				linksEles = pdriver.driver.findElements(By.cssSelector("md-list-item[ng-repeat*='getServices']"));
				titleEles = pdriver.driver.findElements(By.cssSelector("span[data-field-selector*='title']"));

				title = titleEles.get(0).getText();
				System.out.println("Title: " + title + "<<<Title");
				checkTitle = title;
				if (CJKStringHandling.isCJKString(checkTitle)) {
					checkTitle = CJKStringHandling.convertToSimpChinese(checkTitle);
				}
				checkTitle = checkTitle.replaceAll(":.*", "");
				checkTitle = checkTitle.replaceAll("=.*", "");
				checkTitle = StringHandling.trimAll(checkTitle).toLowerCase();
				checkTitle = StringHandling.trimSpecialChars(checkTitle);
				checkTitle = StringHandling.normalizeString(checkTitle);
				checkTitle = checkTitle.replaceAll("VOL.*$", "");
				checkTitle = checkTitle.replaceAll("^THE", "");

				out("No. of links records of the target title:" + linksEles.size());

			} catch (Exception e0) {
				try {
					logwriter.out(e0.getMessage());
				} catch (Exception e2) {
					e2.printStackTrace();
				}
				e0.printStackTrace();
			}

			if (linksEles != null) {
				// Check every links on the page (a title may be provided by more than 1
				// paltform.)
				for (WebElement ele : linksEles) {

					// Check if the eBook is held by the platform on browser.

					String checkPlatform = platforms[i][3];
					checkPlatform = checkPlatform.toLowerCase();
					if (CJKStringHandling.isCJKString(checkPlatform)) {
						checkPlatform = CJKStringHandling.convertToSimpChinese(checkPlatform);
					}
					checkPlatform = StringHandling.trimAll(checkPlatform);

					String checkLink = ele.getText();
					checkLink = checkLink.toLowerCase();
					if (CJKStringHandling.isCJKString(checkLink)) {
						checkLink = CJKStringHandling.convertToSimpChinese(checkLink);
					}
					checkLink = StringHandling.trimAll(checkLink);

					out("Target link on browser: " + checkLink);
					out("Link keyword in config file: " + checkPlatform);

					if (platforms[i][0].toLowerCase().equals("eba") || checkLink.contains(checkPlatform)
							|| checkPlatform.toLowerCase().equals("none")) {
						pdriver.actions.moveToElement(ele).click().build().perform();
						if (platforms[i][0].toLowerCase().equals("eba")) {
							// If this is a EBA title, the eBook is accessed via SFX. This helps to click on
							// the SFX menu.
							out("EBA checking.");
							Thread.sleep(1000);
							ArrayList<String> tabs2 = new ArrayList<String>(pdriver.driver.getWindowHandles());
							pdriver.driver.switchTo().window(tabs2.get(1));

							pdriver.wait.until(ExpectedConditions.visibilityOfElementLocated(
									By.cssSelector(hk.edu.hkmu.lib.Config.VALUES.get("SFXLINK"))));

							Thread.sleep(2000);

							List<WebElement> linksEles2 = pdriver.driver
									.findElements(By.cssSelector(hk.edu.hkmu.lib.Config.VALUES.get("SFXLINK")));

							out("No. of ele.:" + linksEles2.size());

							int index = 0;
							if (linksEles2 != null) {
								for (WebElement ele2 : linksEles2) {
									String providerOnSFX = "";
									providerOnSFX = pdriver.driver
											.findElements(
													By.cssSelector(hk.edu.hkmu.lib.Config.VALUES.get("SFXSERVICE")))
											.get(index).getText().toLowerCase().trim();

									out("Checking SFX Page");
									out("Provider on SFX: " + providerOnSFX);
									out("Platfrom Checking Link: " + platforms[i][3].toLowerCase());
									index++;
									if (providerOnSFX.contains(platforms[i][3].toLowerCase())) {
										out("SFX Link matches.");
										pdriver.actions.moveToElement(ele2).click().build().perform();
										pdriver.driver.close();
										tabs2 = new ArrayList<String>(pdriver.driver.getWindowHandles());
										pdriver.driver.switchTo().window(tabs2.get(1));
										break;
									} else {
										out("SFX Link not match.");
										pdriver.driver.close();
										break;
									}
								}
							}

						}

						Thread.sleep(5000);
						out("Link matched. Going to save.");
						break;
					} else {
						// No appropiate link found in the platform.
						out("Link keyword not match to the target link.");
					}
				}
			}

			ArrayList<String> tabs2 = new ArrayList<String>(pdriver.driver.getWindowHandles());

			String SFTPHOST = hk.edu.hkmu.lib.Config.VALUES.get("TSSERVICE_SERVER");
			int SFTPPORT = 22;
			String SFTPUSER = hk.edu.hkmu.lib.Config.TSSERVICE_SERVER_LOGIN;
			String SFTPPASS = hk.edu.hkmu.lib.Config.TSSERVICE_SERVER_PWD;
			String SFTPWORKINGDIR = hk.edu.hkmu.lib.Config.VALUES.get("SERVERROOT")
					+ "/acq/reports/eBkPlatformChecking";

			Session session = null;
			Channel channel = null;
			ChannelSftp channelSftp = null;
			JSch jsch = new JSch();
			File screenshotFile = null;
			String saveFileName = null;

			if (tabs2.size() > 1 && linksEles != null) {
				String page = "";
				String pageOri = "";
				try {
					pdriver.driver.switchTo().window(tabs2.get(1));
					Thread.sleep(60000);
					screenshotFile = ((TakesScreenshot) pdriver.driver).getScreenshotAs(OutputType.FILE);
					BufferedImage image = ImageIO.read(screenshotFile);
					page = pdriver.driver.getPageSource();
					pageOri = page;
					if (CJKStringHandling.isCJKString(page)) {
						page = CJKStringHandling.convertToSimpChinese(page);
					}
					page = page.replaceAll("&amp;", "");
					page = page.replaceAll("&", "");
					page = StringHandling.trimAll(page).toLowerCase();
					page = StringHandling.trimSpecialChars(page);
					page = StringHandling.normalizeString(page);

					int imageType = BufferedImage.TYPE_INT_RGB;
					BufferedImage watermarked = new BufferedImage(image.getWidth(), image.getHeight(), imageType);
					Graphics2D w = (Graphics2D) watermarked.getGraphics();
					w.drawImage(image, 0, 0, null);
					AlphaComposite alphaChannel = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f);
					w.setComposite(alphaChannel);
					w.setColor(Color.BLACK);
					w.setBackground(Color.WHITE);
					w.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));

					FontMetrics fontMetrics = w.getFontMetrics();

					AttributedString as1 = new AttributedString("URL: " + pdriver.driver.getCurrentUrl());
					as1.addAttribute(TextAttribute.BACKGROUND, Color.GREEN, 0,
							pdriver.driver.getCurrentUrl().length() + 5);
					as1.addAttribute(TextAttribute.SIZE, 16, 0, pdriver.driver.getCurrentUrl().length() + 5);
					as1.addAttribute(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD, 0,
							pdriver.driver.getCurrentUrl().length() + 5);

					AttributedString as2 = new AttributedString("Timestamp: " + today);
					as2.addAttribute(TextAttribute.BACKGROUND, Color.GREEN, 0, today.length() + 11);
					as2.addAttribute(TextAttribute.SIZE, 16, 0, today.length() + 11);
					as2.addAttribute(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD, 0, today.length() + 11);

					AttributedString as3 = new AttributedString("Book Title: " + title);
					as3.addAttribute(TextAttribute.BACKGROUND, Color.GREEN, 0, title.length() + 12);
					as3.addAttribute(TextAttribute.SIZE, 16, 0, title.length() + 12);
					as3.addAttribute(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD, 0, title.length() + 12);

					w.drawString(as1.getIterator(), 0, 40);
					w.drawString(as2.getIterator(), 0, 70);
					w.drawString(as3.getIterator(), 0, 100);

					saveFileName = today + "-" + platforms[i][1] + ".jpg";
					ImageIO.write(watermarked, "jpeg", new File(savePath + saveFileName));
					screenshotFile = new File(savePath + saveFileName);
					w.dispose();
					platforms[i][6] = todayDB;
					platforms[i][7] = "Title: " + title.replaceAll("'", "''") + " (" + pdriver.driver.getCurrentUrl()
							+ ")";

				} catch (Exception e) {
					out(e.getMessage());
				}

				if (page.contains(checkTitle) && !page.contains("PAGEDOESNOTEXISTORTHELINKISOUTOFDATE")
						&& !(page.contains("ERROR_NETWORK_GENERIC")) && !(page.contains("THISPAGEISNTWORKING"))
						&& !(CJKStringHandling.convertToSimpChinese(platforms[i][1]).contains(checkTitle))
						&& !pdriver.driver.getCurrentUrl().contains("www.lib.hkmu.edu.hk")
						&& !pdriver.driver.getCurrentUrl().contains("sfx.lib.hkmu.edu.hk")) {

					platforms[i][5] = "SUCCESS";
					platforms[i][6] = todayDB;
					out("Title on Primo matches title on the eBook Platform.");
					out("HTML on the Target Platform: " + page);
					htmlLogwriter = new LogWriter();
					htmlLogwriter.setLogFile(platforms[i][1] + "-" + StringHandling.getToday() + ".html");
					htmlLogwriter.out(pageOri, false);
					out("Title on Primo: " + checkTitle);

					try {
						session = jsch.getSession(SFTPUSER, SFTPHOST, SFTPPORT);
						session.setPassword(SFTPPASS);
						java.util.Properties config = new java.util.Properties();
						config.put("StrictHostKeyChecking", "no");
						session.setConfig(config);
						session.connect();
						out("Host connected. ");
						channel = session.openChannel("sftp");
						channel.connect();
						out("sftp channel opened and connected. " + SFTPWORKINGDIR);
						channelSftp = (ChannelSftp) channel;
						channelSftp.cd(SFTPWORKINGDIR);
						try {
							channelSftp.mkdir(StringHandling.getTodayDateOnly() + "-Batch-" + batchID);
						} catch (Exception e) {
							out(e.getMessage());
						}
						channelSftp.cd(StringHandling.getTodayDateOnly() + "-Batch-" + batchID);
						channelSftp.put(new FileInputStream(screenshotFile), saveFileName);
						channelSftp.exit();
						out("sftp Channel exited.");
						channel.disconnect();
						out("Channel disconnected.");
						session.disconnect();
						out("Host Session disconnected.");

					} catch (Exception e) {
						out(e.getMessage());
					}

					for (int j = 1; j < tabs2.size(); j++) {
						pdriver.driver.switchTo().window(tabs2.get(j));
						pdriver.driver.close();
					} // end for
					pdriver.driver.switchTo().window(tabs2.get(0));
					return true;
				} else {
					out("Title on Primo does NOT match title on the eBook Platform.");
					out("Title on Primo: " + checkTitle);
					htmlLogwriter = new LogWriter();
					htmlLogwriter.setLogFile(platforms[i][1] + "-" + StringHandling.getToday() + ".html");
					htmlLogwriter.out(pageOri, false);
					platforms[i][5] = "FAILED - Title on Primo does NOT match title on the eBook Platform.";
					if (platforms[i][6] == null || platforms[i][6].equals(""))
						platforms[i][6] = todayDB;
				}
			} // end if

			// cannot reach the target platform.
			out("cannot reach the platform:" + platforms[i][1]);
			try {
				session = jsch.getSession(SFTPUSER, SFTPHOST, SFTPPORT);
				session.setPassword(SFTPPASS);
				java.util.Properties config = new java.util.Properties();
				config.put("StrictHostKeyChecking", "no");
				session.setConfig(config);
				session.connect();
				out("Host connected. ");
				channel = session.openChannel("sftp");
				channel.connect();
				out("sftp channel opened and connected. " + SFTPWORKINGDIR);
				channelSftp = (ChannelSftp) channel;
				channelSftp.cd(SFTPWORKINGDIR);
				try {
					channelSftp.mkdir(StringHandling.getTodayDateOnly() + "-Batch-" + batchID);
				} catch (Exception e) {
				}
				channelSftp.cd(StringHandling.getTodayDateOnly() + "-Batch-" + batchID);
				channelSftp.put(new FileInputStream(screenshotFile), saveFileName);
				channelSftp.exit();
				out("sftp Channel exited.");
				channel.disconnect();
				out("Channel disconnected.");
				session.disconnect();
				out("Host Session disconnected.");
			} catch (Exception e) {
			}

			platforms[i][5] = "FAILED - It seems the platform is down.";
			if (platforms[i][6] == null || platforms[i][6].equals(""))
				platforms[i][6] = todayDB;

			for (int j = 1; j < tabs2.size(); j++) {
				pdriver.driver.switchTo().window(tabs2.get(j));
				pdriver.driver.close();
			} // end for
			pdriver.driver.switchTo().window(tabs2.get(0));
			return false;

		} catch (

		Exception e) {
			try {
				logwriter.out(e.getMessage());
			} catch (Exception e2) {
				e2.printStackTrace();
			}
			e.printStackTrace();
		}
		return false;
	}

}
