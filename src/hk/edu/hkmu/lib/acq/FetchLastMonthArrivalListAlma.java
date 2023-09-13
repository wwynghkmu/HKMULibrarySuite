package hk.edu.hkmu.lib.acq;

import java.util.*;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.*;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import hk.edu.hkmu.lib.StringHandling;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.poi.common.usermodel.HyperlinkType;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * OUHK In-house Library Tool - Fetch last month new arrival title report by
 * School and date range from Alma Analytics.
 * 
 * @author Wai-yan NG
 * @author wwyng@hkmu.edu.hk
 * @version 1.0
 * @since 18 July 2023
 */

public class FetchLastMonthArrivalListAlma {
	private String sql;
	private String startDate;
	private String endDate;
	private String schCode;
	private String school;
	private String[] codes;
	private String writePath;
	private String filename;
	private String outputHTML;
	private Writer wr;
	private ArrayList<String> reportFiles;
	private Set<String> resultSet;
	private HashMap<String, Object[]> result;
	private java.util.Date now;
	private StringHandling strHandle;
	private String URL;
	private Object[] URLs;
	private boolean isEmpty;

	/**
	 * Initiating the class instance
	 * 
	 * @param wr The Writer object which is used to output the HTML report. The
	 *           object is normally the out object from JSP the framework.
	 * 
	 */
	private void init(Writer wr) {
		hk.edu.hkmu.lib.acq.Config.init();
		URL = null;
		URLs = null;
		startDate = null;
		endDate = null;
		codes = null;
		sql = null;
		school = null;
		schCode = null;
		writePath = null;
		outputHTML = "";
		isEmpty = false;
		now = new java.util.Date();
		reportFiles = new ArrayList<String>();
		resultSet = null;
		result = null;
		strHandle = new StringHandling();

		if (wr == null)
			this.wr = null;
		else
			this.wr = wr;
	}

	/**
	 * Preparing for reports; you may run reportThisMonth() or reportLastMonth()
	 * right after this.
	 * 
	 * @param schCode   The School code of a particular school, defined in the file
	 *                  1st column in the form [schCode@School Name] in
	 *                  budgetCode.txt under the class folder hk.edu.ouhk.lib.acq.
	 * 
	 * @param writePath The full folder path to store the generated Excel report; be
	 *                  sure the program has writing privilege of the path.
	 * 
	 * @param wr        For writing the report in HTML format out to an IO, normally
	 *                  used by JSP the istance javax.servlet.jsp.JspWriter.
	 */
	public FetchLastMonthArrivalListAlma(String schCode, String writePath, Writer wr) {
		init(wr);
		setSchCode(schCode);
		setWritePath(writePath);

		fetchReport();

	}

	/**
	 * 
	 * Set the report Writer class; the report is in HTML format.
	 * 
	 * @param wr A Writer class for writing out the HTML report.
	 */
	public void setWriter(Writer wr) {
		this.wr = wr;
	}

	/**
	 * Set the writePath
	 * 
	 * @param writePath writePath is the path to write the Excel report. The running
	 *                  of this program must have relevant writing privilege of the
	 *                  path.
	 */
	public void setWritePath(String writePath) {
		if (writePath == null)
			writePath = "";
		this.writePath = writePath;
	}

	/**
	 * Set the schCode
	 * 
	 * @param schCode schCode is the key relating to the budget codes, that each
	 *                school holds certain budgets, for report generation. The
	 *                schCode is defined in the configuration file "budgetCodes.txt"
	 *                of this program.
	 */
	public void setSchCode(String schCode) {
		if (schCode == null)
			schCode = "";
		this.schCode = schCode;

	}

	/**
	 * Set the startDate
	 * 
	 * @param startDate startDate is the starting date range of the target report.
	 */
	public void setStartDate(String startDate) {
		if (startDate == null)
			startDate = "";
		this.startDate = startDate;
	}

	/**
	 * Set the endDate
	 * 
	 * @param endDate endDate is the ending date range of the target report.
	 */
	public void setEndDate(String endDate) {
		if (endDate == null)
			endDate = "";
		this.endDate = endDate;
	}

	public String getSchCode() {
		return schCode;
	}

	public String getSchool() {
		String str = hk.edu.hkmu.lib.acq.Config.VALUES.get(schCode);

		if (str == null)
			return "";
		school = str;
		return str;
	}

	public String[] getBudgetCodes() {
		return codes;
	}

	public String getStartDate() {
		return startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public String getFileName() {
		return filename;
	}

	/**
	 * Using the current set schCode, startDate, endDate, and report path; and
	 * generate the associated report.
	 */
	public void fetchReport() {

		result = new HashMap<String, Object[]>();
		resultSet = new TreeSet<String>();

		System.setProperty("org.apache.poi.util.POILogger", "org.apache.poi.util.NullLogger");

		try {
			LocalDate currentDate = LocalDate.now();
			LocalDate lastDayOfMonthDate = currentDate
					.withDayOfMonth(currentDate.getMonth().length(currentDate.isLeapYear()));

			Calendar c = Calendar.getInstance();
			Date dt = new Date();
			c.setTime(dt);
			c.add(Calendar.DATE, -1);

			dt = c.getTime();

			String startMonth = c.get(Calendar.MONTH) + "";
			String lastMonth = c.get(Calendar.MONTH) + "";
			String endMonth = c.get(Calendar.MONTH) + "";
			String startYear = c.get(Calendar.YEAR) + "";

			SXSSFWorkbook workbook;

			String filename = writePath + schCode + "-Monthly Report-"
					+ StringHandling.getMonthByNum(Integer.parseInt(lastMonth) - 1) + " " + startYear + ".xlsx";

			File file = new File(filename);
			if (file.exists()) {
				XSSFWorkbook wb = new XSSFWorkbook(new FileInputStream(filename));
				workbook = new SXSSFWorkbook(wb, 100, true, true);

			} else {
				workbook = new SXSSFWorkbook(100);
			}

			int total = 1;

			startMonth = StringHandling.getMonthByNum(startMonth);
			endMonth = StringHandling.getMonthByNum(endMonth);

			SXSSFSheet sheet = workbook.createSheet(startMonth + " " + startYear);
			sheet.setSelected(true);
			sheet.createFreezePane(0, 3);

			CellStyle hrefStyle = workbook.createCellStyle();
			Font hrefFont = workbook.createFont();
			hrefFont.setUnderline(Font.U_SINGLE);
			hrefFont.setColor(IndexedColors.BLUE.getIndex());
			hrefFont.setFontName("Arial Unicode MS");
			hrefStyle.setFont(hrefFont);
			hrefStyle.setBorderBottom(BorderStyle.THIN);
			hrefStyle.setBorderTop(BorderStyle.THIN);
			hrefStyle.setBorderRight(BorderStyle.THIN);
			hrefStyle.setBorderLeft(BorderStyle.THIN);
			hrefStyle.setVerticalAlignment(VerticalAlignment.TOP);
			hrefStyle.setAlignment(HorizontalAlignment.CENTER);
			hrefStyle.setWrapText(true);

			CellStyle defaultFontStyle = workbook.createCellStyle();
			Font defaultFont = workbook.createFont();
			defaultFont.setFontHeightInPoints((short) 11);
			defaultFont.setFontName("Arial Unicode MS");
			defaultFontStyle.setFont(defaultFont);
			defaultFontStyle.setWrapText(true);
			defaultFontStyle.setBorderBottom(BorderStyle.THIN);
			defaultFontStyle.setBorderTop(BorderStyle.THIN);
			defaultFontStyle.setBorderRight(BorderStyle.THIN);
			defaultFontStyle.setBorderLeft(BorderStyle.THIN);
			defaultFontStyle.setVerticalAlignment(VerticalAlignment.TOP);

			CellStyle centerFontStyle = workbook.createCellStyle();
			centerFontStyle.setFont(defaultFont);
			centerFontStyle.setWrapText(true);
			centerFontStyle.setBorderBottom(BorderStyle.THIN);
			centerFontStyle.setBorderTop(BorderStyle.THIN);
			centerFontStyle.setBorderRight(BorderStyle.THIN);
			centerFontStyle.setBorderLeft(BorderStyle.THIN);
			centerFontStyle.setAlignment(HorizontalAlignment.CENTER);
			centerFontStyle.setVerticalAlignment(VerticalAlignment.TOP);
			centerFontStyle.setWrapText(true);

			CellStyle backgroundCellstyle = workbook.createCellStyle();
			backgroundCellstyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());

			Font headerFont = workbook.createFont();
			headerFont.setBold(true);
			headerFont.setFontHeightInPoints((short) 12);
			headerFont.setFontName("Arial Unicode MS");

			CellStyle titleCellStyle = workbook.createCellStyle();
			titleCellStyle.setFont(headerFont);

			CellStyle headerCellStyle = workbook.createCellStyle();

			headerCellStyle.setFont(headerFont);
			headerCellStyle.setAlignment(HorizontalAlignment.CENTER);
			headerCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
			headerCellStyle.setBorderBottom(BorderStyle.THIN);
			headerCellStyle.setBorderTop(BorderStyle.THIN);
			headerCellStyle.setBorderRight(BorderStyle.THIN);
			headerCellStyle.setBorderLeft(BorderStyle.THIN);
			headerCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
			headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

			Row headerRow = sheet.createRow(0);
			Cell cell = headerRow.createCell(0);

			cell.setCellValue(Config.VALUES.get("NEWARRIVALEXCELHEADER") + " " + getSchool());

			cell.setCellStyle(titleCellStyle);

			headerRow = sheet.createRow(1);
			cell = headerRow.createCell(0);
			cell.setCellStyle(titleCellStyle);

			cell.setCellValue(Config.VALUES.get("NEWARRIVALEXCELHEADER2") + " " + startMonth + " " + startYear);
			headerCellStyle.setFont(headerFont);

			headerRow = sheet.createRow(2);
			cell = headerRow.createCell(0);
			cell.setCellValue("No.");
			cell.setCellStyle(headerCellStyle);

			cell = headerRow.createCell(1);
			cell.setCellValue("Title");
			cell.setCellStyle(headerCellStyle);

			cell = headerRow.createCell(6);
			cell.setCellValue("Link to the Item");
			cell.setCellStyle(headerCellStyle);

			cell = headerRow.createCell(2);
			cell.setCellValue("Author / Editor");
			cell.setCellStyle(headerCellStyle);

			cell = headerRow.createCell(3);
			cell.setCellValue("Publisher");
			cell.setCellStyle(headerCellStyle);

			cell = headerRow.createCell(4);
			cell.setCellValue("Subject");
			cell.setCellStyle(headerCellStyle);

			cell = headerRow.createCell(5);
			cell.setCellValue("Material Type");
			headerCellStyle.setWrapText(true);
			cell.setCellStyle(headerCellStyle);

			outputHTML = "";
			outputHTML = now + "<br>";
			outputHTML += "<h3><div align=center> " + startMonth + " " + startYear
					+ " Arrival List for <font color=blue>" + school + "</font> </div></h3> <br>";
			outputHTML += "<table id='reportTable' class='table table-hover sortable'> <thead> <tr> <td> No. </td> <td> Title </td> <td> Author / Editor </td>  <td> Publisher </td> <td> Order Material Type </td> <td> Link to the item </td></tr> </thead>\n";
			outputHTML += "<tbody>";

			if (wr != null) {
				wr.write(outputHTML);
				wr.flush();
			}
			outputHTML = "";

			hk.edu.hkmu.lib.Config.init();
			String prodAPIKey = "apikey=" + hk.edu.hkmu.lib.Config.VALUES.get("ALMAAPIKEYALL");
			String almaAnalyticsAPIPath = hk.edu.hkmu.lib.Config.VALUES.get("ALMAANALYTICSAPIROOTPATH");
			String urlStr = almaAnalyticsAPIPath + "SYS/Arrived_Last_Month_" + schCode + "_P&limit=1000&col_names=true&"
					+ prodAPIKey;
			URL url = new URL(urlStr);
			System.out.println(urlStr);

			HttpURLConnection http = (HttpURLConnection) url.openConnection();

			BufferedReader br = null;
			if (100 <= http.getResponseCode() && http.getResponseCode() <= 399) {
				br = new BufferedReader(new InputStreamReader(http.getInputStream()));
			} else {
				br = new BufferedReader(new InputStreamReader(http.getErrorStream()));
			}

			String inputLine;
			StringBuffer content = new StringBuffer();
			while ((inputLine = br.readLine()) != null) {
				content.append(inputLine);
			}

			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			InputSource is = new InputSource(new StringReader(content.toString()));
			Document doc = builder.parse(is);
			NodeList nlist = doc.getElementsByTagName("Row");

			for (int i = 0; i < nlist.getLength(); i++) {
				if (nlist.item(i).getNodeType() == Node.ELEMENT_NODE) {
					Element el = (Element) nlist.item(i);

					String author = "";
					if (el.getElementsByTagName("Column1").item(0) != null) {
						author += el.getElementsByTagName("Column1").item(0).getTextContent();
					}
					if (el.getElementsByTagName("Column2").item(0) != null) {
						author += el.getElementsByTagName("Column2").item(0).getTextContent();
					}

					String subject = "";
					if (el.getElementsByTagName("Column7").item(0) != null) {
						subject = el.getElementsByTagName("Column7").item(0).getTextContent();
					}

					String title = "";
					if (el.getElementsByTagName("Column8").item(0) != null) {
						title = el.getElementsByTagName("Column8").item(0).getTextContent();
					}

					String callno = "";
					if (el.getElementsByTagName("Column9").item(0) != null) {
						callno = el.getElementsByTagName("Column9").item(0).getTextContent();
					}

					String mmsid = "";
					if (el.getElementsByTagName("Column4").item(0) != null) {
						mmsid = el.getElementsByTagName("Column4").item(0).getTextContent();
					}
					String publisher = "";
					if (el.getElementsByTagName("Column6").item(0) != null) {
						publisher = el.getElementsByTagName("Column6").item(0).getTextContent();
						if (el.getElementsByTagName("Column5").item(0) != null) {
							publisher += ". " + el.getElementsByTagName("Column5").item(0).getTextContent();
						}
					}

					String rstype = "";
					if (el.getElementsByTagName("Column11").item(0) != null) {
						rstype = el.getElementsByTagName("Column11").item(0).getTextContent();
					}

					if (!resultSet.contains(mmsid)) {
						resultSet.add(mmsid);
						Object strArry[] = { title, author, publisher, callno, "Print - " + rstype, mmsid, subject };
						result.put(mmsid, strArry);
					}

				}
			}
			http.disconnect();

			urlStr = almaAnalyticsAPIPath + "SYS/Arrived_Last_Month_" + schCode + "_E&limit=1000&col_names=true&"
					+ prodAPIKey;

			url = new URL(urlStr);
			System.out.println(urlStr);
			http = (HttpURLConnection) url.openConnection();

			br = null;

			if (100 <= http.getResponseCode() && http.getResponseCode() <= 399) {
				br = new BufferedReader(new InputStreamReader(http.getInputStream()));
			} else {
				br = new BufferedReader(new InputStreamReader(http.getErrorStream()));
			}

			inputLine = "";
			content = new StringBuffer();
			while ((inputLine = br.readLine()) != null) {
				content.append(inputLine);
			}

			is = new InputSource(new StringReader(content.toString()));
			doc = builder.parse(is);
			nlist = doc.getElementsByTagName("Row");

			for (int i = 0; i < nlist.getLength(); i++) {
				if (nlist.item(i).getNodeType() == Node.ELEMENT_NODE) {
					Element el = (Element) nlist.item(i);

					String author = "";
					if (el.getElementsByTagName("Column1").item(0) != null) {
						author += el.getElementsByTagName("Column1").item(0).getTextContent();
					}
					if (el.getElementsByTagName("Column2").item(0) != null) {
						author += el.getElementsByTagName("Column2").item(0).getTextContent();
					}

					String title = "";
					if (el.getElementsByTagName("Column9").item(0) != null) {
						title = el.getElementsByTagName("Column9").item(0).getTextContent();
					}

					String callno = "N/A";

					String mmsid = "";
					if (el.getElementsByTagName("Column5").item(0) != null) {
						mmsid = el.getElementsByTagName("Column5").item(0).getTextContent();
					}

					String publisher = "";
					if (el.getElementsByTagName("Column7").item(0) != null) {
						publisher = el.getElementsByTagName("Column7").item(0).getTextContent();
						if (el.getElementsByTagName("Column6").item(0) != null) {
							publisher += ". " + el.getElementsByTagName("Column6").item(0).getTextContent();
						}
					}

					String subject = "";
					if (el.getElementsByTagName("Column8").item(0) != null) {
						subject = el.getElementsByTagName("Column8").item(0).getTextContent();
					}

					String rstype = "";
					if (el.getElementsByTagName("Column4").item(0) != null) {
						rstype = el.getElementsByTagName("Column4").item(0).getTextContent();
					}

					if (!resultSet.contains(mmsid)) {
						resultSet.add(mmsid);
						Object strArry[] = { title, author, publisher, callno, "Electronic - " + rstype, mmsid,
								subject };
						result.put(mmsid, strArry);

					}

				}
			}

			http.disconnect();
			if (resultSet.size() == 0) {
				isEmpty = true;
			}
			String noReportLine = "";
			if (isEmpty) {
				noReportLine = "<tr> <td></td><td colspan=6> <b> There is no new item received for the captioned period. </b> </td> </tr>";
				headerRow = sheet.createRow(4);
				cell = headerRow.createCell(0);
				cell.setCellValue("There is no new item received for the captioned period.");
				cell.setCellStyle(titleCellStyle);

			}

			ArrayList<String> al = new ArrayList<String>();

			for (String key : result.keySet()) {

				String callno = "";

				try {
					callno = result.get(key)[3].toString().toLowerCase();
				} catch (Exception e) {
					callno = "";
				}

				al.add(callno + "^^" + key);
			}

			Collections.sort(al);

			int count = 0;

			for (String str : al) {
				str = str.replaceAll("^.*\\^\\^", "");

				Row row = sheet.createRow(count + 3);
				String tmp = result.get(str)[0].toString();

				row.createCell(0).setCellValue(count + 1);
				row.getCell(0).setCellStyle(centerFontStyle);

				row.createCell(1).setCellValue(tmp);
				row.getCell(1).setCellStyle(defaultFontStyle);

				tmp = result.get(str)[1].toString();
				row.createCell(2).setCellValue(tmp);
				row.getCell(2).setCellStyle(defaultFontStyle);

				tmp = result.get(str)[2].toString();
				row.createCell(3).setCellValue(tmp);
				row.getCell(3).setCellStyle(defaultFontStyle);

				tmp = result.get(str)[6].toString();
				row.createCell(4).setCellValue(tmp);
				row.getCell(4).setCellStyle(defaultFontStyle);

				tmp = result.get(str)[4].toString();
				row.createCell(5).setCellValue(tmp);
				row.getCell(5).setCellStyle(defaultFontStyle);

				tmp = result.get(str)[5].toString();

				URL = Config.VALUES.get("PRIMOFULLVIEWURLFORM").replace("^", "=") + tmp;
				String URLstr = "<a href='" + URL + "' target=_blank> click here </a>";
				Hyperlink href = workbook.getCreationHelper().createHyperlink(HyperlinkType.URL);
				href.setAddress(URL);
				row.createCell(6).setCellValue("Click Here");
				row.getCell(6).setHyperlink(href);
				row.getCell(6).setCellStyle(hrefStyle);

				/*
				 * Remove multiple links because link now points to Primo's permanent link. if
				 * (URLs.length > 1) { for (int i = 2; i < URLs.length; i++) {
				 * 
				 * Hyperlink href2 =
				 * workbook.getCreationHelper().createHyperlink(HyperlinkType.URL);
				 * href2.setAddress(URLs[i].toString()); row.createCell(7 + (i -
				 * 1)).setCellValue("Click Here"); row.getCell(7 + (i - 1)).setHyperlink(href2);
				 * row.getCell(7 + (i - 1)).setCellStyle(hrefStyle); URLstr += "<br>" +
				 * "<a href='" + URLs[i].toString() + "' target=_blank> click here </a>"; } }
				 */

				tmp = null;

				int authorlength = result.get(str)[1].toString().length();
				if (authorlength > 30)
					authorlength = 30;

				int titlelength = result.get(str)[0].toString().length();
				if (titlelength > 40)
					titlelength = 40;

				String reportLine = "<tr> <td>#</td><td>" + result.get(str)[0].toString().substring(0, titlelength)
						+ "</td><td>" + result.get(str)[1].toString().substring(0, authorlength) + "</td><td>"
						+ result.get(str)[2] + "</td> <td>" + result.get(str)[4]
						+ "</td><td><a href='" + URL + "' target=_blank> Click Here" + "</a> </td> </tr>";

				URL = null;
				if (wr != null) {
					wr.write(reportLine);
					wr.flush();
				}

				count++;

				if ((count % 2) == 0) {
					row.setRowStyle(backgroundCellstyle);
				}

			}

			outputHTML = noReportLine + "</tbody></table>";

			if (wr != null) {
				wr.write(outputHTML);
				wr.flush();
			}

			sheet.trackAllColumnsForAutoSizing();
			sheet.setColumnWidth(0, 1300);
			sheet.setColumnWidth(1, 20000);
			sheet.setColumnWidth(2, 10000);
			sheet.setColumnWidth(3, 7500);
			sheet.setColumnWidth(4, 10000);
			sheet.autoSizeColumn(5);
			sheet.autoSizeColumn(6);
			sheet.autoSizeColumn(7);

			for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
				if (i == workbook.getNumberOfSheets() - 1) {
					workbook.setSelectedTab(i);
					workbook.setActiveSheet(i);
				}
			}

			String now = StringHandling.getToday();
			filename = "";

			filename = schCode + "-Monthly Report-" + startMonth + " " + startYear + ".xlsx";

			reportFiles.add(filename);

			FileOutputStream fileOut = new FileOutputStream(writePath + "/" + filename);
			workbook.write(fileOut);
			fileOut.close();

			if (isEmpty) {
				fileOut = new FileOutputStream(writePath + "/" + filename + ".empty");
				fileOut.write(
						(byte[]) (schCode + "-Monthly Report-" + startMonth + " " + startYear + " EMPTY").getBytes());
			}

			this.filename = filename;
			resultSet = null;
			result = null;
			sheet = null;
			workbook.dispose();
			workbook.close();

		}

		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
