package hk.edu.hkmu.lib.ser;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.*;
import hk.edu.hkmu.lib.*;
import org.apache.commons.validator.routines.UrlValidator;
import org.apache.poi.common.usermodel.HyperlinkType;
import java.io.*;
import java.sql.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * OUHK In-house Library Tool - Fetch URLs with specified material types (the
 * material types are defined in config.txt) from Aleph Oracle tables.
 * 
 * @author Wai-yan NG
 * @author wwyng@ouhk.edu.hk
 *
 * @since May 21, 2019
 */

public class FetchURLReport extends Report {
	private String outputHTML = "";
	private String now = StringHandling.getToday();
	private String materialStr = "";

	private void init() {
		Config.init();
		materialStr = StringHandling.trimSpace(Config.VALUES.get("URLITEMTYPE"));
		materialStr = materialStr.replaceAll(",$", "");
		materialStr = materialStr.replace(",,", ",");
		reportFile = "SER-URL-REPORT-" + now + "-" + materialStr.replace(",", "-") + ".xlsx";
	}

	/**
	 * Constructor of the class.
	 * 
	 * @param reportPath The place the Excel report is saved, must have appropriate
	 *                   permission to write into the folder.
	 */
	public FetchURLReport(String reportPath) {
		super(reportPath, null);
		init();
	}

	/*
	 * Constructor of the class.
	 * 
	 * @param reportPath The place the Excel report is saved, must have appropriate
	 * permission to write into the folder.
	 * 
	 * @param wr The Writer object for writing out the HTML report. Give it a null
	 * value if no need to write out HTML report.
	 */
	public FetchURLReport(String reportPath, Writer wr) {
		super(reportPath, wr);
		init();
	}

	/**
	 * Fetching the report in Excel and HTML format. The Excel report is saved in
	 * 'reprotPath' which is set by creating the class instance; while HTML report
	 * can be get using the 'getOutputHTML()' method. The report file name, which is
	 * generated with the time stamp invoking report generation, can be get by the
	 * method 'getReportFile()'.
	 */
	public void fetchReport() {

		Connection conAleph = null;
		Statement stmt = null;
		Statement stmt2 = null;
		ResultSet rs2 = null;

		System.setProperty("org.apache.poi.util.POILogger", "org.apache.poi.util.NullLogger");

		try {

			String z30_material_query = "(";

			// Get the material types defined in "config.txt" via the class Config
			String[] materialType = materialStr.split(",");
			for (int i = 0; i < materialType.length; i++) {
				z30_material_query += "Z30_MATERIAL = '" + materialType[i] + "'";
				if (i != materialType.length - 1) {
					z30_material_query += " or ";
				}
			}
			z30_material_query += ")";

			Class.forName("oracle.jdbc.driver.OracleDriver");
			conAleph = DriverManager.getConnection("jdbc:oracle:thin:@aleph.lib.ouhk.edu.hk:1521:aleph22", "oul01",
					"oul01");

			// The 1st SQL statement for fetching necessary title information with the
			// material types specified.
			String sql = "select UNIQUE(z13_rec_key), z13_title from oul50.z30, oul50.z13 where Z30_ITEM_PROCESS_STATUS = 'WB' and "
					+ z30_material_query + " and z13_rec_key = SUBSTR(z30_rec_key,1,9) order by z13_rec_key";

			stmt = conAleph.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			int count = 1;

			SXSSFWorkbook workbook = new SXSSFWorkbook(100);
			SXSSFSheet sheet = workbook.createSheet(materialStr + " URL RPT " + now);
			sheet.createFreezePane(0, 2);

			Row headerRow = sheet.createRow(0);
			Cell cell = headerRow.createCell(0);
			Font headerFont = workbook.createFont();
			headerFont.setBold(true);
			headerFont.setFontHeightInPoints((short) 12);
			CellStyle headerCellStyle = workbook.createCellStyle();
			headerCellStyle.setFont(headerFont);
			cell.setCellStyle(headerCellStyle);
			cell.setCellValue(materialStr + " URL Report. Generated time: " + now);

			headerRow = sheet.createRow(1);
			cell = headerRow.createCell(0);
			cell.setCellValue("No.");
			cell.setCellStyle(headerCellStyle);

			cell = headerRow.createCell(1);
			cell.setCellValue("BIB#");
			cell.setCellStyle(headerCellStyle);

			cell = headerRow.createCell(2);

			cell.setCellValue("Title");
			cell.setCellStyle(headerCellStyle);

			cell = headerRow.createCell(3);
			cell.setCellValue("URL");
			cell.setCellStyle(headerCellStyle);

			cell = headerRow.createCell(4);
			cell.setCellValue("YOC");
			cell.setCellStyle(headerCellStyle);

			outputHTML = "Report generated time: " + now + "<br>";
			outputHTML += "<h3> <div align=center> SER URL Report " + now + " </h3> <br>";
			outputHTML += "<table id='reportTable' class='table table-hover sortable'> <thead> <tr> <td> No. </td> <td> BIB # </td> <td> Title </td> <td>"
					+ " URL </td> <td> YOC </td> </tr> </thead>\n";
			outputHTML += "<tbody>";

			if (wr != null) {
				wr.write(outputHTML);
				wr.flush();
			}
			outputHTML = "";
			CellStyle hrefStyle = null;
			hrefStyle = workbook.createCellStyle();
			Font hrefFont = workbook.createFont();
			hrefFont.setUnderline(Font.U_SINGLE);
			hrefFont.setColor(IndexedColors.BLUE.getIndex());
			hrefStyle.setFont(hrefFont);
			Hyperlink href = workbook.getCreationHelper().createHyperlink(HyperlinkType.URL);
			String data = "";
			Pattern pattern = Pattern.compile("856..L(\\$\\$3.*)\\$\\$u");
			Matcher matcher;
			String subfield3 = "";
			String reportLine = "";
			stmt2 = conAleph.createStatement();
			BufferedWriter fileout = null;
			String tmpFile = StringHandling.getToday();
			tmpFile = Config.VALUES.get("TEMPFOLDER") + tmpFile + ".csv";
			try {
				FileWriter fstream = new FileWriter(tmpFile, false);
				fileout = new BufferedWriter(fstream);
				while (rs.next()) {
					String rec_key = rs.getString("Z13_REC_KEY");

					String title = rs.getString("Z13_TITLE");
					fileout.write(rec_key + "~" + title + "\n");

				}

			}

			catch (IOException e) {
				System.err.println("Error: " + e.getMessage());
			}

			finally {
				if (fileout != null) {
					fileout.close();
				}
			}
			rs.close();
			stmt.close();

			try (BufferedReader br = new BufferedReader(new FileReader(tmpFile))) {
				String line;
				while ((line = br.readLine()) != null) {
					String[] lineValues = line.split("~");
					String rec_key = lineValues[0];

					String title = "";
					if (lineValues.length > 1)
						title = lineValues[1];

					// The 2nd SQL statement, for fetching the URLs from the raw data (Z00_DATA) of
					// a title from Aleph Oracle.
					String sql2 = "select * from z00 where z00_doc_number = '" + rec_key + "'";

					try {
						data = "";
						rs2 = stmt2.executeQuery(sql2);
						if (rs2.next()) {
							data = rs2.getString("Z00_DATA");
							matcher = pattern.matcher(data);
							if (matcher.find()) {
								subfield3 = matcher.group(1);
							}

							data = data.replace(subfield3, "");
						}
						rs2.close();
					} catch (Exception e2) {
						System.out.println(StringHandling.getToday());
						System.out.println("rec_key " + rec_key);
						System.out.println(sql2);
						conAleph.close();
						conAleph = DriverManager.getConnection("jdbc:oracle:thin:@aleph.lib.ouhk.edu.hk:1521:aleph22",
								"oul01", "oul01");
						stmt2.close();
						stmt2 = conAleph.createStatement();
						e2.printStackTrace();
					}
					subfield3 = subfield3.replace("$$3", "");
					subfield3 = subfield3 + " ";

					String urls[] = data.split("\\d\\d\\d\\d856..L\\$\\$u");
					String yocs[] = new String[urls.length];
					for (int i = 0; i < urls.length; i++) {
						urls[i] = urls[i].trim();
						yocs[i] = urls[i].replaceAll("^.*http.*?\\$\\$z", "");
						if (!urls[i].contains("$z"))
							yocs[i] = "";
						urls[i] = urls[i].replaceAll("^.*85640L\\$u", "");
						urls[i] = urls[i].replaceAll("^.*\\$\\$u", "");
						urls[i] = urls[i].replaceAll("CAT  L.*", "");
						urls[i] = urls[i].replaceAll("004\\d\\d\\d\\d .*", "");
						urls[i] = urls[i].replaceAll("\\$\\$z.*", "");
						yocs[i] = yocs[i].replaceAll("\\d\\d\\d\\d\\d\\d\\d..L.*", "");
						yocs[i] = yocs[i].replaceAll("\\d\\d\\d\\dCAT  L.*", "");
						yocs[i] = yocs[i].replaceAll("\\d\\d\\dCAT  L.*", "");
						yocs[i] = yocs[i].replaceAll("^.*\\$\\$y", "");
						yocs[i] = yocs[i].replaceAll("\\$\\$a", "");
						yocs[i] = yocs[i].replaceAll("\\$\\$b", "");
						yocs[i] = yocs[i].replaceAll("\\$\\$c", "");
						yocs[i] = yocs[i].replaceAll("\\d\\d\\d949.*", "");
						yocs[i] = yocs[i].replaceAll("\\)\\d+", ")");
						yocs[i] = yocs[i].replaceAll("\\.0$", ".");
						yocs[i] = yocs[i].replaceAll("\\-0$", "-");
					}

					Row row = sheet.createRow(count + 1);
					row.createCell(0).setCellValue(count);
					row.createCell(1).setCellValue(rec_key);
					row.createCell(2).setCellValue(title);

					if (yocs.length > 1)
						row.createCell(4).setCellValue(yocs[1]);
					else
						row.createCell(4).setCellValue("");
					if (urls.length > 1) {
						row.createCell(3).setCellValue(urls[1]);
						System.out.println(urls[1]);
					} else
						row.createCell(3).setCellValue("");

					if (urls.length > 1) {
						urls[1] = urls[1].replaceAll(" ", "%20");
						row.getCell(3).setCellStyle(hrefStyle);
					}
					count++;

					reportLine = "<tr> <td>#</td><td>" + rec_key + "</td><td>" + title + "</td>";

					if (yocs.length > 1 && urls.length > 1)
						reportLine += "<td> <a href='" + urls[1] + "' target='_blank'> " + urls[1] + "</a></td><td>"
								+ yocs[1] + "</td>\n";
					else
						reportLine += "<td> <a href='" + "" + "' target='_blank'> " + "" + "</a></td><td>" + ""
								+ "</td>\n";

					if (wr != null) {
						wr.write(reportLine);
						wr.flush();
					}

					reportLine = "";

					for (int j = 2; j < urls.length; j++) {

						if (!urls[j].contains("aleph.lib.ouhk.edu.hk")) {

							row = sheet.createRow(count + 1);
							row.createCell(0).setCellValue(count);
							row.createCell(1).setCellValue(rec_key);
							row.createCell(2).setCellValue(title);
							if (yocs.length > 1)
								row.createCell(4).setCellValue(yocs[j]);
							else
								row.createCell(4).setCellValue("");
							if (urls.length > 1) {

								row.createCell(3).setCellValue(urls[j]);
								/*
								 * href.setAddress(urls[j]); row.getCell(3).setHyperlink(href);
								 */
								row.getCell(3).setCellStyle(hrefStyle);

							} else {
								row.createCell(3).setCellValue("");
							}
							count++;

							reportLine += "<tr> <td>#</td><td>" + rec_key + "</td><td>" + title + "</td>";

							if (urls.length > 1 && yocs.length > 1)
								reportLine += "<td> <a href='" + urls[j] + "' target='_blank'> " + urls[j]
										+ "</a></td> <td>" + yocs[j] + "</td>\n";
							else
								reportLine += "<td> <a href='" + "" + "' target='_blank'> " + "" + "</a></td> <td>" + ""
										+ "</td>\n";
							if (wr != null) {
								wr.write(reportLine);
								wr.flush();
							}

							reportLine = "";
						}
					}
				}

			}

			outputHTML = "</tr></tbody></table>";

			if (wr != null) {
				wr.write(outputHTML);
				wr.flush();
			}
			outputHTML = "";

			sheet.trackAllColumnsForAutoSizing();
			sheet.autoSizeColumn(1);
			sheet.autoSizeColumn(2);
			sheet.autoSizeColumn(4);
			sheet.autoSizeColumn(3);

			FileOutputStream fileOut = new FileOutputStream(reportPath + reportFile);
			workbook.write(fileOut);
			workbook.dispose();
			workbook.close();
			fileOut.close();

			rs.close();
			stmt2.close();
			conAleph.close();
		}

		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
