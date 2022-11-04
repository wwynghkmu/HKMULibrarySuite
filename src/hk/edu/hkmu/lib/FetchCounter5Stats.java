package hk.edu.hkmu.lib;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.regex.*;
import java.sql.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.*;
import org.apache.poi.xssf.usermodel.*;
import org.json.*;
import org.json.simple.*;
import org.json.simple.parser.*;
import hk.edu.hkmu.lib.*;

public class FetchCounter5Stats {

	private String startDate;
	private String endDate;
	private String reportName;
	private String vendor;
	private LogWriter logwriter;

	public FetchCounter5Stats(String startDate, String endDate, ArrayList<String> ursStrs) {
		Config.init();
		logwriter = new LogWriter();
		logwriter.setLogFile("c5statLog.txt");
		this.vendor = "";
		Pattern p = Pattern.compile("(^\\d\\d\\d\\d)(\\d\\d)(\\d\\d)");
		Matcher m = p.matcher(startDate);
		if (m.find()) {
			startDate = m.group(1) + "-" + m.group(2) + "-" + m.group(3);
		}

		m = p.matcher(endDate);
		if (m.find()) {
			endDate = m.group(1) + "-" + m.group(2) + "-" + m.group(3);
		}

		this.startDate = startDate;
		this.endDate = endDate;
		this.reportName = "TR";
		for (String vendor : Config.COUNTER5PLATFORMS.keySet()) {
			this.vendor = vendor;
			String url = Config.COUNTER5PLATFORMS.get(vendor)[0];
			url = url.replaceAll("\\[Start Date\\]", startDate);
			url = url.replaceAll("\\[End Date\\]", endDate);
			url = url.replaceAll("\\[Report Name\\]", reportName);
			System.out.println(url);
			FetchPerVendorsStatJR1(url);

		}

	}

	private void FetchPerVendorsStatJR1(String urlS) {
		try {
			SXSSFWorkbook workbook;
			workbook = new SXSSFWorkbook(100);
			SXSSFSheet sheet = workbook.createSheet("JR1");
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

			headerRow = sheet.createRow(1);
			cell = headerRow.createCell(0);
			cell.setCellValue("Journal");
			cell.setCellStyle(headerCellStyle);

			cell = headerRow.createCell(1);
			cell.setCellValue("Publisher");
			cell.setCellStyle(headerCellStyle);

			cell = headerRow.createCell(2);
			cell.setCellValue("Platform");
			cell.setCellStyle(headerCellStyle);

			cell = headerRow.createCell(3);
			cell.setCellValue("Print ISSN");
			cell.setCellStyle(headerCellStyle);

			cell = headerRow.createCell(4);
			cell.setCellValue("Online ISSN");
			cell.setCellStyle(headerCellStyle);

			cell = headerRow.createCell(5);
			cell.setCellValue("Metric_type");
			headerCellStyle.setWrapText(true);
			cell.setCellStyle(headerCellStyle);

			cell = headerRow.createCell(6);
			cell.setCellValue("Reporting Period Total");
			headerCellStyle.setWrapText(true);
			cell.setCellStyle(headerCellStyle);

			String startYear = "";
			String startMonth = "";
			String endYear = "";
			String endMonth = "";
			Pattern p = Pattern.compile("(^\\d\\d\\d\\d)-(\\d\\d)-(\\d\\d)");
			Matcher m = p.matcher(startDate);
			if (m.find()) {
				startYear = m.group(1);
				startMonth = m.group(2);
			}
			m = p.matcher(endDate);
			if (m.find()) {
				endYear = m.group(1);
				endMonth = m.group(2);
			}

			HashMap<String, Integer> periods = new HashMap<String, Integer>();

			for (int i = Integer.parseInt(startYear); i <= Integer.parseInt(endYear); i++) {
				for (int j = Integer.parseInt(startMonth), k = 1; j <= Integer.parseInt(endMonth); j++, k++) {
					int colNo = 6 + k;
					cell = headerRow.createCell(colNo);
					cell.setCellValue(StringHandling.getMonthByNum(j) + " " + i);
					headerCellStyle.setWrapText(true);
					cell.setCellStyle(headerCellStyle);
					String tmp = (j < 10) ? "0" + j : j + "";
					periods.put(i + "-" + tmp, colNo);
				}
			}
			int count = 0;

			// Connecting to the MySQL DB

			Connection conn = DriverManager.getConnection(hk.edu.hkmu.lib.Config.DB_URL, hk.edu.hkmu.lib.Config.USER,
					hk.edu.hkmu.lib.Config.PASS);

			Class.forName("oracle.jdbc.driver.OracleDriver");
			Connection connAleph = DriverManager.getConnection("jdbc:oracle:thin:@aleph.lib.ouhk.edu.hk:1521:aleph22",
					"oul01", "oul01");

			Statement stmt = conn.prepareStatement("select * from serc5statistics", ResultSet.TYPE_SCROLL_SENSITIVE,
					ResultSet.CONCUR_UPDATABLE);

			ResultSet rs = stmt.executeQuery("select * from serc5statistics");
			rs = stmt.getResultSet();
			rs.first();

			String rawFilePath = Config.VALUES.get("RAWCOUNTER5FILES");
			rawFilePath += startDate + "_to_" + endDate + "_" + reportName + "_" + StringHandling.getToday() + ".json";
			File outfile = new File(rawFilePath);
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outfile)));
			URL url = new URL(urlS);
			URLConnection urlcon = url.openConnection();
			urlcon.setConnectTimeout(3000);
			BufferedReader buffread = new BufferedReader(new InputStreamReader(urlcon.getInputStream()));
			String inputLine;
			String outstr = "";
			while ((inputLine = buffread.readLine()) != null) {
				outstr += inputLine;
				bw.write(inputLine);
			}
			bw.close();
			buffread.close();
			JSONParser parser = new JSONParser();
			JSONObject jsonObject = (JSONObject) parser.parse(outstr);

			for (Iterator iterator = jsonObject.keySet().iterator(); iterator.hasNext();) {
				String key = (String) iterator.next();

				if (key.toLowerCase().trim().equals("report_header")) {

				} else if (key.toLowerCase().trim().equals("report_items")) {
					JSONArray jarray = (JSONArray) jsonObject.get(key);
					for (int i = 0; i < jarray.size(); i++) {

						JSONObject jo = (JSONObject) jarray.get(i);
						String title = jo.get("Title") + "";
						String platform = jo.get("Platform") + "";
						String publisher = jo.get("Publisher") + "";
						String doi = "";
						String proprietary = "";
						String onlineIssn = "";
						String printIssn = "";
						JSONArray performJO = (JSONArray) jo.get("Performance");

						JSONArray pubIDJO = (JSONArray) jo.get("Publisher_ID");
						JSONArray itemIDJO = (JSONArray) jo.get("Item_ID");
						for (int j = 0; j < itemIDJO.size(); j++) {
							JSONObject jo2 = (JSONObject) itemIDJO.get(j);
							String type = (String) jo2.get("Type");
							String value = (String) jo2.get("Value");
							switch (type) {
							case "DOI":
								doi = value;
								break;
							case "Proprietary":
								proprietary = value;
								break;
							case "Online_ISSN":
								onlineIssn = value;
								break;
							case "Print_ISSN":
								printIssn = value;
								break;

							}
						}

						Row row = sheet.createRow(count + 3);
						row.createCell(0).setCellValue(title);
						row.createCell(1).setCellValue(publisher);
						row.createCell(2).setCellValue(platform);
						row.createCell(3).setCellValue(printIssn);
						row.createCell(4).setCellValue(onlineIssn);
						count++;
						Row row2 = sheet.createRow(count + 3);
						row2.createCell(0).setCellValue(title);
						row2.createCell(1).setCellValue(publisher);
						row2.createCell(2).setCellValue(platform);
						row2.createCell(3).setCellValue(printIssn);
						row2.createCell(4).setCellValue(onlineIssn);

						// Insert the title into DB, ignore if already exist
						title = title.replace("\"", "\\\"");
						String insSQL = "insert ignore into serc5service (title, publisher, platform, doi, online_issn, print_issn, vendor) values(\""
								+ title + "\",\"" + publisher + "\", '" + platform + "', '" + doi + "', '" + onlineIssn
								+ "', '" + printIssn + "', '" + vendor + "')";

						stmt.executeUpdate(insSQL);

						for (int j = 0; j < performJO.size(); j++) {
							JSONObject jo2 = (JSONObject) performJO.get(j);
							JSONObject joPeriod = (JSONObject) jo2.get("Period");
							JSONArray joInstance = (JSONArray) jo2.get("Instance");

							String bd = "";
							String ed = "";

							bd = (String) joPeriod.get("Begin_Date");
							m = p.matcher(bd);
							int colIndex = 0;
							if (m.find()) {
								colIndex = periods.get(m.group(1) + "-" + m.group(2));
							}

							String uniqueItmReq = "";
							String totalItmReq = "";

							for (int k = 0; k < joInstance.size(); k++) {

								JSONObject jo3 = (JSONObject) joInstance.get(k);
								String metricType = "";
								String cnt = "";
								metricType = (String) jo3.get("Metric_Type");
								cnt = (String) jo3.get("Count").toString();
								switch (metricType.toLowerCase()) {
								case "total_item_requests":
									row.createCell(colIndex).setCellType(CellType.NUMERIC);
									row.createCell(colIndex).setCellValue(Double.parseDouble(cnt));
									row.createCell(5).setCellType(CellType.NUMERIC);
									row.createCell(5).setCellValue(metricType);
									break;
								case "unique_item_requests":
									row2.createCell(colIndex).setCellType(CellType.NUMERIC);
									row2.createCell(colIndex).setCellValue(Double.parseDouble(cnt));
									row2.createCell(5).setCellValue(metricType);

									break;
								} // end switch

							} // end for

							// set the formular to sum the numbers in the period.
							char startStatColIndex = (char) (7 + 97);
							char endStatColIndex = (char) (7 + periods.size() - 1 + 97);

							row.createCell(6).setCellFormula("SUM(" + startStatColIndex + (count + 3 + 1 - 1) + ":"
									+ endStatColIndex + (count + 3 + 1 - 1) + ")");

							row2.createCell(6).setCellFormula("SUM(" + startStatColIndex + (count + 3 + 1) + ":"
									+ endStatColIndex + (count + 3 + 1) + ")");

						}

						count++;
					} // end for
				} // end if
			} // end for
			conn.close();
			connAleph.close();
			sheet.trackAllColumnsForAutoSizing();
			sheet.setColumnWidth(0, 15000);
			sheet.setColumnWidth(1, 5000);
			sheet.setColumnWidth(2, 5000);
			sheet.setColumnWidth(3, 5000);
			sheet.setColumnWidth(4, 5000);
			sheet.setColumnWidth(5, 5000);
			sheet.setColumnWidth(6, 5000);

			FileOutputStream fileOut = new FileOutputStream("D:/Jr1Rpt.xlsx");
			workbook.write(fileOut);
			fileOut.close();
		} catch (

		Exception e) {
			e.printStackTrace();
		}

	}

}
