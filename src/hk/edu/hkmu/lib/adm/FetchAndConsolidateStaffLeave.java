package hk.edu.hkmu.lib.adm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.NTCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import hk.edu.hkmu.lib.ExcelCellStyle;
import hk.edu.hkmu.lib.StringHandling;

/**
 * 
 * Fetch staff leave records in Excel from OUHK SharePoint
 * (sharepoint.ouhk.edu.hk); and consolidate the records into a master staff
 * leave file in yearly basis.
 * 
 * @author Wai-yan NG
 * @author wwyng@ouhk.edu.hk
 * @version 1.0
 * @since 26 Aug, 2020
 */

public class FetchAndConsolidateStaffLeave {

	private String tempFile;
	int maxDay;
	SXSSFSheet masterSheet;
	SXSSFWorkbook masterWorkbook;

	private void init() {
		tempFile = "";
		masterSheet = null;
		masterWorkbook = null;
		maxDay = 0;
		Config.init();
		hk.edu.hkmu.lib.Config.init();
	}

	/*
	 * Constructor for fetching and consolidating the Staff Leave Record file in
	 * Excel from the SharePoint. The relevant configurations are in config.txt.
	 * 
	 * @param The user name The user name for logging onto the SharePoint
	 * 
	 * @param The password of the a/c for logging onto the SharePoint
	 */
	public FetchAndConsolidateStaffLeave(String username, String password) throws Exception {
		init();
		try {
			if (fetExcelFromSharePoint(username, password)) {
				consolidateExcelReport();
			} else {

			}
		} catch (Exception e) {
			throw e;
		}
	}

	/*
	 * Copy the downloaded Excel sheets to the reporting Excel file
	 */
	private void consolidateExcelReport() throws Exception {

		try {
			List<Integer> holidayIndexes = null;
			List<Integer> sessionFirstRowIndexes = null;
			ArrayList<String> nameList = new ArrayList<String>();

			boolean holidayColIndex[];
			int lastStaffRowIndex = 0;
			ZipSecureFile.setMinInflateRatio(-1.0d);
			XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(tempFile));
			String currentMonth = StringHandling.getMonthByNum(StringHandling.getCurrentMonth());

			// Getting the reporting year defined in config.txt as "CALENDARYEAR" which will
			// be included in the report file name.
			int year = StringHandling.parseInteger(Config.VALUES.get("CALENDARYEAR"));

			XSSFSheet sheet = null;
			XSSFWorkbook masterWB = null;

			// Prepare to write the file into disk, "FILEPROTECTIONKEY" of the Config class
			// was prefixed, for avoiding people guessing the report file URL.
			String reportFile = Config.VALUES.get("REPORTFOLDER") + "/"
					+ hk.edu.hkmu.lib.Config.VALUES.get("FILEPROTECTIONKEY") + "-" + year + "-"
					+ Config.VALUES.get("REPORTFILE");

			File masterFile = new File(reportFile);

			if (masterFile.exists()) {
				masterWB = new XSSFWorkbook(new FileInputStream(reportFile));
				masterWorkbook = new SXSSFWorkbook(masterWB, 100, true, true);

			} else {
				masterWorkbook = new SXSSFWorkbook(100);

			}

			ExcelCellStyle.init(masterWorkbook);

			for (int monInd = 0; monInd <= 12; monInd++) {

				// Change to the financial year range.
				int mon = monInd;
				mon += 8;
				if (mon > 12)
					mon -= 12;

				String month = StringHandling.getMonthByNum(mon);

				// Getting the reporting year defined in config.txt as "CALENDARYEAR"
				year = StringHandling.parseInteger(Config.VALUES.get("CALENDARYEAR"));

				if (mon < 9)
					year += 1;

				sessionFirstRowIndexes = new ArrayList<Integer>();
				holidayIndexes = new ArrayList<Integer>();
				holidayColIndex = new boolean[72];
				for (int i = 0; i < 72; i++) {
					holidayColIndex[i] = false;
				}

				sheet = workbook.getSheet(month + " " + year);
				if (sheet != null) {
					if (masterWorkbook.getSheet(month + " " + year) != null) {
						masterSheet = masterWorkbook.getSheet(month + " " + year);
						masterWorkbook.removeSheetAt(masterWorkbook.getSheetIndex(masterSheet));
						masterSheet = masterWorkbook.createSheet(month + " " + year);
					} else {
						masterSheet = masterWorkbook.createSheet(month + " " + year);
					}

					int sheetCol = 0;
					maxDay = 0;

					for (int rowIndex = 0; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
						Row row = sheet.getRow(rowIndex);
						if (row != null) {
							
							Row masterRow = masterSheet.createRow(rowIndex);
							masterRow.setZeroHeight(row.getZeroHeight());
							masterRow.setHeight(row.getHeight());
							/*
							 * if (row.getLastCellNum() > sheetCol) sheetCol = row.getLastCellNum();
							 */
							// Stick to the column number to 32 for preventing from enter spaces mistakenly
							// on more than 32 sheet columns.
							sheetCol = 63;
							// masterRow.setRowStyle(row.getRowStyle());

							for (int colIndex = 0; colIndex < row.getLastCellNum(); colIndex++) {
								Cell masterCell = masterRow.createCell(colIndex, CellType.STRING);
								Cell cell2 = row.getCell(colIndex);
								if (cell2 != null) {

									if (mon == 8 && colIndex == 0 && rowIndex <= sheet.getLastRowNum()
											&& !cell2.getStringCellValue().toLowerCase().contains("legend")
											&& !cell2.getStringCellValue().toLowerCase().contains("annual")
											&& !cell2.getStringCellValue().toLowerCase().contains("leave record")) {
										String name = cell2.getStringCellValue();
										nameList.add(name);
									}

									masterCell.setCellStyle(ExcelCellStyle.defaultFontStyleCenter);

									// TODO Set cell value
									String tmpStr = "";

									switch (cell2.getCellType().name()) {
									case "STRING":
										tmpStr = cell2.getStringCellValue();
										if (tmpStr.toLowerCase().contains("legends")) {
											lastStaffRowIndex = rowIndex - 1;
										}
										if ((tmpStr.toLowerCase().trim().equals("ph")
												|| tmpStr.toLowerCase().trim().equals("sat")
												|| tmpStr.toLowerCase().trim().equals("sun")
												|| tmpStr.toLowerCase().trim().equals("o")) && rowIndex == 7) {
											holidayColIndex[colIndex] = true;
											holidayColIndex[colIndex + 1] = true;
										}
										masterCell.setCellValue(tmpStr);

										break;
									case "NUMERIC":
										masterCell.setCellType(CellType.NUMERIC);
										masterCell.setCellValue(cell2.getNumericCellValue());
										if (rowIndex == 6) {
											maxDay = (int) masterCell.getNumericCellValue();
										}
										break;
									default:
										tmpStr = cell2.getStringCellValue();
										masterCell.setCellValue(tmpStr);
										break;
									}

									switch (rowIndex) {
									case 0:
										masterCell.setCellStyle(ExcelCellStyle.titleFontStyleCenterWithoutBorder);
										break;
									case 2:
										masterCell.setCellStyle(null);
										break;
									case 3:
										masterCell.setCellStyle(ExcelCellStyle.titleFontStyleLeftAlign);
										break;
									case 5:
										masterCell.setCellStyle(ExcelCellStyle.defaultFontStyleCenterBold);
										break;
									case 6:
										masterCell.setCellStyle(ExcelCellStyle.defaultFontStyleCenterBold);
										break;

									default:

										// If in normal leave cell, remove the middle line between AM & PM cells.
										if (colIndex > 0 && colIndex < 63 && (colIndex + colIndex + 1) % 4 == 3) {
											masterCell.setCellStyle(ExcelCellStyle.defaultFontStyleCenterLeftGrid);
										} else if (colIndex > 0 && colIndex < 63) {
											masterCell.setCellStyle(ExcelCellStyle.defaultFontStyleCenterRightGrid);

										} else {
											masterCell.setCellStyle(ExcelCellStyle.defaultFontStyleCenter);
										}

										break;
									}

									// Set style of the "Remark" and "Legend"
									if (rowIndex > sheet.getLastRowNum() - 3)
										masterCell.setCellStyle(null);

									// Left align the staff name & session style
									if (colIndex == 0 && rowIndex <= sheet.getLastRowNum() - 5 && rowIndex > 1) {
										switch (masterCell.getStringCellValue().toUpperCase()) {
										case "PS":
										case "CDM & MBS":
										case "OTHERS":
										case "SYS & DI":
											sessionFirstRowIndexes.add(rowIndex);
											masterCell.setCellStyle(
													ExcelCellStyle.defaultFontStyleLeftAlignLIBSessionStyle);
											break;
										default:
											masterCell.setCellStyle(ExcelCellStyle.defaultFontStyleLeftAlign);
											break;
										} // end case

									} // end if

									// Set row height to zero if the name column is empty.
									if (rowIndex > 9 && lastStaffRowIndex > rowIndex) {
										if (masterRow.getCell(0).getStringCellValue().trim().equals(""))
											masterRow.setHeight((short) 0);
									}

									// Remove all style for cells larger than the last staff row
									if (rowIndex > lastStaffRowIndex) {
										masterRow.getCell(0)
												.setCellStyle(ExcelCellStyle.defaultFontStyleBoldWithoutBorder);
									}
								} else {
									masterCell.setCellValue("");
								}
							} // end for
						} // end if
					} // end for

					// Mend the missing styles of last staff row
					for (int j = 0; j < sheetCol; j++) {

						if (j > 0 && j < 63 && (j + j + 1) % 4 == 3) {
							masterSheet.getRow(lastStaffRowIndex - 1).getCell(j)
									.setCellStyle(ExcelCellStyle.defaultFontStyleCenterLeftGrid);
						} else if (j > 0 && j < 63) {
							masterSheet.getRow(lastStaffRowIndex - 1).getCell(j)
									.setCellStyle(ExcelCellStyle.defaultFontStyleCenterRightGrid);
						} else {
							masterSheet.getRow(lastStaffRowIndex - 1).getCell(j)
									.setCellStyle(ExcelCellStyle.defaultFontStyleCenter);
						}
					}

					// Add holiday style
					for (int i = 9; i < lastStaffRowIndex - 2; i++) {
						for (int j = 0; j < 72; j++) {
							if (holidayColIndex[j]) {

								Row row = masterSheet.getRow(i);
								Cell cell = row.getCell(j);

								// cell.setCellStyle(ExcelCellStyle.defaultFontStyleSessionFirstLine);
								// If in normal leave cell, remove the middle line between AM & PM cells.
								if (j > 0 && j < 63 && (j + j + 1) % 4 == 3) {
									cell.setCellStyle(ExcelCellStyle.defaultFontStyleGreyFillLeftGrid);
								} else if (j > 0 && j < 63) {
									cell.setCellStyle(ExcelCellStyle.defaultFontStyleGreyFillRightGrid);
								} else {
									cell.setCellStyle(ExcelCellStyle.defaultFontStyleGreyFill);
								}
							}
						}
					}

					// Remove last rows's style.
					for (int i = lastStaffRowIndex - 3; i < lastStaffRowIndex + 2; i++) {
						for (int j = 0; j < sheetCol; j++) {
							masterSheet.getRow(i).getCell(j)
									.setCellStyle(ExcelCellStyle.defaultFontStyleBoldWithoutBorder);
						}
					}

					addSummaryCols(sheetCol, lastStaffRowIndex, sessionFirstRowIndexes);

					// Add session first row style
					for (Integer i : sessionFirstRowIndexes) {
						Row row = masterSheet.getRow(i.intValue());

						for (int j = 1; j < row.getLastCellNum(); j++) {
							Cell cell = row.getCell(j);
							if (!holidayColIndex[j] && cell != null) {
								// cell.setCellStyle(ExcelCellStyle.defaultFontStyleSessionFirstLine);
								// If in normal leave cell, remove the middle line between AM & PM cells.
								if (j > 0 && j < 63 && (j + j + 1) % 4 == 3) {
									cell.setCellStyle(ExcelCellStyle.defaultFontStyleSessionFirstLineLeftGrid);
								} else if (j > 0 && j < 63) {
									cell.setCellStyle(ExcelCellStyle.defaultFontStyleSessionFirstLineRightGrid);
								} else {
									cell.setCellStyle(ExcelCellStyle.defaultFontStyleSessionFirstLine);
								}

							} else if (cell != null) {
								// cell.setCellStyle(ExcelCellStyle.defaultFontStyleSessionFirstLine);
								// If in normal leave cell, remove the middle line between AM & PM cells.
								if (j > 0 && j < 64 && (j + j + 1) % 4 == 3) {
									cell.setCellStyle(ExcelCellStyle.defaultFontStyleGreyFillSessionFirstLineLeftGrid);
								} else if (j > 0 && j < 64) {
									cell.setCellStyle(ExcelCellStyle.defaultFontStyleGreyFillSessionFirstLineRightGrid);
								} else {
									cell.setCellStyle(ExcelCellStyle.defaultFontStyleGreyFillSessionFirstLine);
								}

							}

						}
					}

					for (int i = 0; i < 72; i++) {
						holidayColIndex[i] = false;
					}

					// Copy column width from the source to the master Excel report

					for (int i = 0; i < sheetCol; i++) {
						masterSheet.setColumnWidth(i, sheet.getColumnWidth(i));
					}
					masterSheet.setColumnWidth(0, 5000);

					// Copy merged cells from the source to the master Excel report
					List<CellRangeAddress> cellRangeAddrs = sheet.getMergedRegions();
					for (CellRangeAddress cra : cellRangeAddrs) {
						if (cra.getFirstRow() != 0)
							masterSheet.addMergedRegion(cra);
					}

					masterSheet.addMergedRegion(new CellRangeAddress(0, 2, 0, 69));


					//masterSheet.getRow(0).getCell(0).setCellStyle(ExcelCellStyle.titleFontStyleCenterWithoutBorder);
					masterSheet.getRow(1).getCell(0).setCellStyle(ExcelCellStyle.titleFontStyleCenterWithoutBorder);
					masterSheet.getRow(2).getCell(0).setCellStyle(ExcelCellStyle.titleFontStyleCenterWithoutBorder);
					masterSheet.createFreezePane(0, 9, 0, 9);
					masterSheet.createFreezePane(1, 9, 1, 9);

					switch (maxDay) {
					case 28:
						masterSheet.setColumnWidth(29 * 2, 0);
						masterSheet.setColumnWidth((29 * 2) - 1, 0);
					case 29:
						masterSheet.setColumnWidth(30 * 2, 0);
						masterSheet.setColumnWidth((30 * 2) - 1, 0);
					case 30:
						masterSheet.setColumnWidth(31 * 2, 0);
						masterSheet.setColumnWidth((31 * 2) - 1, 0);
						break;
					}

					// Removed unneeded merged 20200618
					// masterSheet
					// .addMergedRegion(new CellRangeAddress(lastStaffRowIndex - 1,
					// lastStaffRowIndex - 1, 0, 62));
					masterSheet.getRow(1).getCell(62).setCellStyle(ExcelCellStyle.titleFontStyleCenterWithoutBorder);

				}

			}

			// Creating an summary sheet to summarize all financial year months per a staff
			// member.
			SXSSFSheet totalSheet;
			if (masterWorkbook.getSheet("Total") != null) {
				totalSheet = masterWorkbook.getSheet("Total");
				masterWorkbook.removeSheetAt(masterWorkbook.getSheetIndex(totalSheet));
				totalSheet = masterWorkbook.createSheet("Total");
			} else {
				totalSheet = masterWorkbook.createSheet("Total");
			}
			int cnt = 1;
			Row row;
			Cell cell;

			FormulaEvaluator evaluator = masterWorkbook.getCreationHelper().createFormulaEvaluator();
			for (String name : nameList) {
				if (cnt <= lastStaffRowIndex - 1) {
					name = name.trim();
					row = totalSheet.createRow(cnt);
					cell = row.createCell(0);
					cell.setCellValue(name);

					// Highlight the LIB Session wording
					switch (name.toUpperCase().trim()) {
					case "PS":
					case "CDM & MBS":
					case "OTHERS":
					case "SYS & DI":
						cell.setCellStyle(ExcelCellStyle.defaultFontStyleLeftAlignLIBSessionStyle);
						break;
					default:
						cell.setCellStyle(ExcelCellStyle.defaultFontStyle);
						break;
					}

					// Minimize the height for empty name
					if (name.equals("") && cnt > 7 && cnt < lastStaffRowIndex - 1) {
						row.setHeight((short) 0);
					}

					// Writing summary formula
					char exlCol = 'K';
					if (cnt > 7 && cnt < lastStaffRowIndex - 2 && !cell.getStringCellValue().equals("")
							&& !cell.getStringCellValue().toUpperCase().equals("PS")
							&& !cell.getStringCellValue().toUpperCase().equals("SYS & DI")
							&& !cell.getStringCellValue().toUpperCase().equals("OTHERS")
							&& !cell.getStringCellValue().toUpperCase().equals("CDM & MBS")) {

						for (int i = 1; i < 8; i++) {
							cell.setCellStyle(ExcelCellStyle.defaultFontStyle);
							cell = row.createCell(i);
							cell.setCellStyle(ExcelCellStyle.defaultFontStyle);
							String formular = "SUM('Sep " + (year - 1) + "'!B" + (char) ((int) exlCol + i) + (cnt + 1)
									+ "+'Oct " + (year - 1) + "'!B" + (char) ((int) exlCol + i) + (cnt + 1) + "+'Nov "
									+ (year - 1) + "'!B" + (char) ((int) exlCol + i) + (cnt + 1) + "+'Dec " + (year - 1)
									+ "'!B" + (char) ((int) exlCol + i) + (cnt + 1) + "+'Jan " + year + "'!B"
									+ (char) ((int) exlCol + i) + (cnt + 1) + "+'Feb " + year + "'!B"
									+ (char) ((int) exlCol + i) + (cnt + 1) + "+'Mar " + year + "'!B"
									+ (char) ((int) exlCol + i) + (cnt + 1) + "+'Apr " + year + "'!B"
									+ (char) ((int) exlCol + i) + (cnt + 1) + "+'May " + year + "'!B"
									+ (char) ((int) exlCol + i) + (cnt + 1) + "+'Jun " + year + "'!B"
									+ (char) ((int) exlCol + i) + (cnt + 1) + "+'Jul " + year + "'!B"
									+ (char) ((int) exlCol + i) + (cnt + 1) + "+'Aug " + year + "'!B"
									+ (char) ((int) exlCol + i) + (cnt + 1) + ")";
							cell.setCellFormula(formular);
							evaluator.clearAllCachedResultValues();
							evaluator.evaluateFormulaCell(cell);

						}
					} else {
						for (int i = 1; i < 8; i++) {
							cell = row.createCell(i);
							cell.setCellValue("");
							cell.setCellStyle(ExcelCellStyle.defaultFontStyle);
						}
					} // end if

					// Add the summation formula of each column.
					exlCol = 'A';
					if (lastStaffRowIndex - 1 == cnt) {
						cell = row.createCell(0);
						cell.setCellValue("Total: ");
						cell.setCellStyle(ExcelCellStyle.defaultFontStyleBold);
						for (int i = 1; i < 8; i++) {
							cell = row.createCell(i);
							cell.setCellFormula("SUM(" + (char) ((int) exlCol + i) + "11:" + (char) ((int) exlCol + i)
									+ (lastStaffRowIndex - 3) + ")");
							evaluator.clearAllCachedResultValues();
							evaluator.evaluateFormulaCell(cell);
							cell.setCellStyle(ExcelCellStyle.defaultFontStyleBold);
						}
					}

					cnt++;
				} // end if
			} // end for

			// TODO Add session first row style
			for (Integer i : sessionFirstRowIndexes) {
				row = totalSheet.getRow(i.intValue());
				for (int j = 1; j < row.getLastCellNum(); j++) {
					cell = row.getCell(j);
					cell.setCellStyle(ExcelCellStyle.defaultFontStyleSessionFirstLine);
				}
			}

			row = totalSheet.createRow(0);
			cell = row.createCell(0);
			cell.setCellValue("LEAVE RECORD FOR LIBRARY STAFF");
			row = totalSheet.createRow(1);
			cell = row.createCell(0);
			cell = row.createCell(7);
			row = totalSheet.createRow(2);
			cell = row.createCell(0);
			cell = row.createCell(7);
			row = totalSheet.createRow(3);
			cell = row.createCell(0);
			cell = row.createCell(7);
			row = totalSheet.createRow(4);
			cell = row.createCell(0);
			cell.setCellValue("Sep " + (year - 1) + " to " + "Aug " + year);

			row = totalSheet.createRow(6);
			cell = row.createCell(0);
			cell.setCellValue("");
			cell.setCellStyle(ExcelCellStyle.defaultFontStyleBold);
			cell = row.createCell(1);
			cell.setCellValue("Annual Leave");
			cell.setCellStyle(ExcelCellStyle.defaultFontStyleBold);

			cell = row.createCell(2);
			cell.setCellValue("Comp. Leave");
			cell.setCellStyle(ExcelCellStyle.defaultFontStyleBold);

			cell = row.createCell(3);
			cell.setCellValue("Sick Leave");
			cell.setCellStyle(ExcelCellStyle.defaultFontStyleBold);

			cell = row.createCell(4);
			cell.setCellValue("Special Leave");
			cell.setCellStyle(ExcelCellStyle.defaultFontStyleBold);

			cell = row.createCell(5);
			cell.setCellValue("Maternity Leave");
			cell.setCellStyle(ExcelCellStyle.defaultFontStyleBold);

			cell = row.createCell(6);
			cell.setCellValue("Paternity Leave");
			cell.setCellStyle(ExcelCellStyle.defaultFontStyleBold);

			cell = row.createCell(7);
			cell.setCellValue("No Pay Leave");
			cell.setCellStyle(ExcelCellStyle.defaultFontStyleBold);

			totalSheet.setColumnWidth(0, 5000);
			totalSheet.setColumnWidth(1, 3800);
			totalSheet.setColumnWidth(2, 3800);
			totalSheet.setColumnWidth(3, 3800);
			totalSheet.setColumnWidth(4, 3800);
			totalSheet.setColumnWidth(5, 3800);
			totalSheet.setColumnWidth(6, 3800);
			totalSheet.setColumnWidth(7, 3800);

			totalSheet.addMergedRegion(new CellRangeAddress(0, 3, 0, 7));

			totalSheet.createFreezePane(0, 8, 0, 8);
			totalSheet.createFreezePane(1, 8, 1, 8);
			totalSheet.getRow(0).getCell(0).setCellStyle(ExcelCellStyle.titleFontStyleCenterWithoutBorderBigger);
			totalSheet.getRow(1).getCell(0).setCellStyle(ExcelCellStyle.titleFontStyleCenterWithoutBorderBigger);
			totalSheet.getRow(2).getCell(0).setCellStyle(ExcelCellStyle.titleFontStyleCenterWithoutBorderBigger);
			totalSheet.getRow(3).getCell(0).setCellStyle(ExcelCellStyle.titleFontStyleCenterWithoutBorderBigger);
			totalSheet.getRow(1).getCell(7).setCellStyle(ExcelCellStyle.titleFontStyleCenterWithoutBorderBigger);
			totalSheet.getRow(2).getCell(7).setCellStyle(ExcelCellStyle.titleFontStyleCenterWithoutBorderBigger);
			totalSheet.getRow(3).getCell(7).setCellStyle(ExcelCellStyle.titleFontStyleCenterWithoutBorderBigger);
			totalSheet.getRow(4).createCell(7).setCellValue("");
			totalSheet.getRow(4).getCell(7).setCellStyle(ExcelCellStyle.defaultFontStyleCenterRightGrid);
			totalSheet.getRow(4).getCell(0).setCellStyle(ExcelCellStyle.titleFontStyleLeftAlign);

			// Financial year arrangement

			int sheetTabIndex = StringHandling.getMonthNumByName(currentMonth);

			sheetTabIndex += 3;
			if (sheetTabIndex > 12)
				sheetTabIndex -= 12;
			sheetTabIndex -= 1;

			masterWorkbook.setSelectedTab(sheetTabIndex + 1);
			masterWorkbook.setActiveSheet(sheetTabIndex + 1);

			// Write down the consolidated report file.
			FileOutputStream fileOut = new FileOutputStream(reportFile);
			masterWorkbook.write(fileOut);
			masterWorkbook.close();
			masterWorkbook.dispose();
			if (masterWB != null)
				masterWB.close();
			workbook.close();

		} catch (

		Exception e) {
			e.printStackTrace();
			throw e;
		}

	}

	private boolean fetExcelFromSharePoint(String username, String password) throws Exception {
		CloseableHttpClient httpclient = HttpClients.custom()
				.setRetryHandler(new DefaultHttpRequestRetryHandler(0, false)).build();

		CredentialsProvider credsProvider = new BasicCredentialsProvider();
		credsProvider.setCredentials(AuthScope.ANY, new NTCredentials(username, password, "", ""));

		// You may get 401 if you go through a load-balancer.
		// To fix this, go directly to one of the SharePoint web server or
		// change the configuration. See this article :
		// http://blog.crsw.com/2008/10/14/unauthorized-401-1-exception-calling-web-services-in-sharepoint/
		HttpHost target = new HttpHost(Config.VALUES.get("SHAREPOINTHOST"), 443, "https");

		HttpClientContext context = HttpClientContext.create();
		context.setCredentialsProvider(credsProvider);

		// The authentication is NTLM.
		// To trigger it, we send a minimal http request
		HttpHead request1 = new HttpHead("/");
		CloseableHttpResponse response1 = null;
		try {
			response1 = httpclient.execute(target, request1, context);
			EntityUtils.consume(response1.getEntity());

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (response1 != null)
				try {
					response1.close();
				} catch (Exception e2) {
					e2.printStackTrace();
					return false;
				}
		}

		// The real request, reuse authentication
		String file = Config.VALUES.get("SHAREPOINTFILENAME"); // source
		String targetFolder = Config.VALUES.get("DOWNLOADFOLDER");
		HttpGet request2 = new HttpGet(Config.VALUES.get("SHAREPOINTAPIFUNCTION").replaceAll("FILENAME", file));

		CloseableHttpResponse response2 = null;
		try {
			response2 = httpclient.execute(target, request2, context);
			HttpEntity entity = response2.getEntity();
			int rc = response2.getStatusLine().getStatusCode();
			String reason = response2.getStatusLine().getReasonPhrase();
			if (rc == HttpStatus.SC_OK) {

				File f = new File(file);

				// Prepare to write the file into disk, "FILEPROTECTIONKEY" of the Config class
				// was prefixed, for avoiding people guessing the report file URL.
				File ff = new File(targetFolder, hk.edu.hkmu.lib.Config.VALUES.get("FILEPROTECTIONKEY") + "-"
						+ StringHandling.getToday() + f.getName());
				tempFile = targetFolder + "/" + hk.edu.hkmu.lib.Config.VALUES.get("FILEPROTECTIONKEY") + "-"
						+ StringHandling.getToday() + f.getName();

				// writing the byte array into a file using Apache Commons IO
				FileUtils.writeByteArrayToFile(ff, EntityUtils.toByteArray(entity));
				consolidateExcelReport();

			} else {
				throw new Exception("Problem while receiving " + file + "  reason : " + reason + " httpcode : " + rc);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (response2 != null)
				if (response1 != null)
					try {
						response1.close();
					} catch (Exception e2) {
						e2.printStackTrace();
						return false;
					}
		}
		return true;

	}

	private void addSummaryCols(int sheetCol, int lastStaffRowIndex, List<Integer> sessionFirstRowIndexes) {

		FormulaEvaluator evaluator = masterWorkbook.getCreationHelper().createFormulaEvaluator();

		masterSheet.addMergedRegion(new CellRangeAddress(3, 5, 63, 63));
		Row row = masterSheet.getRow(3);

		Cell cell = row.createCell(sheetCol);
		cell.setCellValue("Annual Leave");
		cell.setCellStyle(ExcelCellStyle.defaultFontStyleCenterBoldBottomAlign);
		row = masterSheet.getRow(4);
		cell = row.createCell(sheetCol);
		cell.setCellStyle(ExcelCellStyle.defaultFontStyleCenter);
		row = masterSheet.getRow(5);
		cell = row.createCell(sheetCol);
		cell.setCellStyle(ExcelCellStyle.defaultFontStyleCenter);
		row = masterSheet.getRow(6);
		cell = row.createCell(sheetCol);
		cell.setCellStyle(ExcelCellStyle.defaultFontStyleCenter);
		row = masterSheet.getRow(7);
		cell = row.createCell(sheetCol);
		cell.setCellStyle(ExcelCellStyle.defaultFontStyleCenter);
		row = masterSheet.getRow(8);
		cell = row.createCell(sheetCol);
		cell.setCellStyle(ExcelCellStyle.defaultFontStyleCenter);

		for (int j = 9; j < lastStaffRowIndex; j++) {
			row = masterSheet.getRow(j);
			cell = row.createCell(sheetCol);
			if (!sessionFirstRowIndexes.contains(Integer.valueOf(j))) {
				cell.setCellStyle(ExcelCellStyle.defaultFontStyleCenter);
				cell.setCellFormula("COUNTIF(B" + (j + 1) + ":BK" + (j + 1) + ", \"A\")/2");
				evaluator.evaluateFormulaCell(cell);
			}
		}
		row = masterSheet.getRow(lastStaffRowIndex);
		cell = row.createCell(sheetCol);
		cell.setCellStyle(ExcelCellStyle.defaultFontStyleCenterBold);
		evaluator.clearAllCachedResultValues();
		cell.setCellFormula("SUM(BL10:BL" + lastStaffRowIndex + ")");

		masterSheet.addMergedRegion(new CellRangeAddress(3, 5, 64, 64));
		row = masterSheet.getRow(3);
		cell = row.createCell(sheetCol + 1);
		cell.setCellValue("Comp. Leave");
		cell.setCellStyle(ExcelCellStyle.defaultFontStyleCenterBoldBottomAlign);
		row = masterSheet.getRow(4);
		cell = row.createCell(sheetCol + 1);
		cell.setCellStyle(ExcelCellStyle.defaultFontStyleCenter);
		row = masterSheet.getRow(5);
		cell = row.createCell(sheetCol + 1);
		cell.setCellStyle(ExcelCellStyle.defaultFontStyleCenter);
		row = masterSheet.getRow(6);
		cell = row.createCell(sheetCol + 1);
		cell.setCellStyle(ExcelCellStyle.defaultFontStyleCenter);
		row = masterSheet.getRow(7);
		cell = row.createCell(sheetCol + 1);
		cell.setCellStyle(ExcelCellStyle.defaultFontStyleCenter);
		row = masterSheet.getRow(8);
		cell = row.createCell(sheetCol + 1);
		cell.setCellStyle(ExcelCellStyle.defaultFontStyleCenter);
		for (int j = 9; j < lastStaffRowIndex; j++) {
			row = masterSheet.getRow(j);
			cell = row.createCell(sheetCol + 1);
			if (!sessionFirstRowIndexes.contains(Integer.valueOf(j))) {
				cell.setCellFormula("COUNTIF(B" + (j + 1) + ":BK" + (j + 1) + ", \"C\")/2");
				cell.setCellStyle(ExcelCellStyle.defaultFontStyleCenter);
			}

		}
		row = masterSheet.getRow(lastStaffRowIndex);
		cell = row.createCell(sheetCol + 1);
		cell.setCellStyle(ExcelCellStyle.defaultFontStyleCenterBold);
		evaluator.clearAllCachedResultValues();
		cell.setCellFormula("SUM(BM10:BM" + lastStaffRowIndex + ")");

		masterSheet.addMergedRegion(new CellRangeAddress(3, 5, 65, 65));
		row = masterSheet.getRow(3);
		cell = row.createCell(sheetCol + 2);
		cell.setCellValue("Sick Leave");
		cell.setCellStyle(ExcelCellStyle.defaultFontStyleCenterBoldBottomAlign);
		row = masterSheet.getRow(4);
		cell = row.createCell(sheetCol + 2);
		cell.setCellStyle(ExcelCellStyle.defaultFontStyleCenter);
		row = masterSheet.getRow(5);
		cell = row.createCell(sheetCol + 2);
		cell.setCellStyle(ExcelCellStyle.defaultFontStyleCenter);
		row = masterSheet.getRow(6);
		cell = row.createCell(sheetCol + 2);
		cell.setCellStyle(ExcelCellStyle.defaultFontStyleCenter);
		row = masterSheet.getRow(7);
		cell = row.createCell(sheetCol + 2);
		cell.setCellStyle(ExcelCellStyle.defaultFontStyleCenter);
		row = masterSheet.getRow(8);
		cell = row.createCell(sheetCol + 2);
		cell.setCellStyle(ExcelCellStyle.defaultFontStyleCenter);
		for (int j = 9; j < lastStaffRowIndex; j++) {
			row = masterSheet.getRow(j);
			cell = row.createCell(sheetCol + 2);
			if (!sessionFirstRowIndexes.contains(Integer.valueOf(j))) {
				cell.setCellFormula("COUNTIF(B" + (j + 1) + ":BK" + (j + 1) + ", \"S\")/2");
				cell.setCellStyle(ExcelCellStyle.defaultFontStyleCenter);
				evaluator.evaluateFormulaCell(cell);
			}
		}
		row = masterSheet.getRow(lastStaffRowIndex);
		cell = row.createCell(sheetCol + 2);
		cell.setCellStyle(ExcelCellStyle.defaultFontStyleCenterBold);
		evaluator.clearAllCachedResultValues();
		cell.setCellFormula("SUM(BN10:BN" + lastStaffRowIndex + ")");

		masterSheet.addMergedRegion(new CellRangeAddress(3, 5, 66, 66));
		row = masterSheet.getRow(3);
		cell = row.createCell(sheetCol + 3);
		cell.setCellValue("Special Leave");
		cell.setCellStyle(ExcelCellStyle.defaultFontStyleCenterBoldBottomAlign);
		row = masterSheet.getRow(4);
		cell = row.createCell(sheetCol + 3);
		cell.setCellStyle(ExcelCellStyle.defaultFontStyleCenter);
		row = masterSheet.getRow(5);
		cell = row.createCell(sheetCol + 3);
		cell.setCellStyle(ExcelCellStyle.defaultFontStyleCenter);
		row = masterSheet.getRow(6);
		cell = row.createCell(sheetCol + 3);
		cell.setCellStyle(ExcelCellStyle.defaultFontStyleCenter);
		row = masterSheet.getRow(7);
		cell = row.createCell(sheetCol + 3);
		cell.setCellStyle(ExcelCellStyle.defaultFontStyleCenter);
		row = masterSheet.getRow(8);
		cell = row.createCell(sheetCol + 3);
		cell.setCellStyle(ExcelCellStyle.defaultFontStyleCenter);
		for (int j = 9; j < lastStaffRowIndex; j++) {
			row = masterSheet.getRow(j);
			cell = row.createCell(sheetCol + 3);
			if (!sessionFirstRowIndexes.contains(Integer.valueOf(j))) {
				cell.setCellFormula("COUNTIF(B" + (j + 1) + ":BK" + (j + 1) + ", \"SP\")/2");
				cell.setCellStyle(ExcelCellStyle.defaultFontStyleCenter);
				evaluator.evaluateFormulaCell(cell);
			}
		}
		row = masterSheet.getRow(lastStaffRowIndex);
		cell = row.createCell(sheetCol + 3);
		cell.setCellStyle(ExcelCellStyle.defaultFontStyleCenterBold);
		evaluator.clearAllCachedResultValues();
		cell.setCellFormula("SUM(BO10:BO" + lastStaffRowIndex + ")");

		masterSheet.addMergedRegion(new CellRangeAddress(3, 5, 67, 67));
		row = masterSheet.getRow(3);
		cell = row.createCell(sheetCol + 4);
		cell.setCellValue("Maternity Leave");
		cell.setCellStyle(ExcelCellStyle.defaultFontStyleCenterBoldBottomAlign);
		row = masterSheet.getRow(4);
		cell = row.createCell(sheetCol + 4);
		cell.setCellStyle(ExcelCellStyle.defaultFontStyleCenter);
		row = masterSheet.getRow(5);
		cell = row.createCell(sheetCol + 4);
		cell.setCellStyle(ExcelCellStyle.defaultFontStyleCenter);
		row = masterSheet.getRow(6);
		cell = row.createCell(sheetCol + 4);
		cell.setCellStyle(ExcelCellStyle.defaultFontStyleCenter);
		row = masterSheet.getRow(7);
		cell = row.createCell(sheetCol + 4);
		cell.setCellStyle(ExcelCellStyle.defaultFontStyleCenter);
		row = masterSheet.getRow(8);
		cell = row.createCell(sheetCol + 4);
		cell.setCellStyle(ExcelCellStyle.defaultFontStyleCenter);
		for (int j = 9; j < lastStaffRowIndex; j++) {
			row = masterSheet.getRow(j);
			cell = row.createCell(sheetCol + 4);
			if (!sessionFirstRowIndexes.contains(Integer.valueOf(j))) {
				cell.setCellFormula("COUNTIF(B" + (j + 1) + ":BK" + (j + 1) + ", \"M\")/2");
				cell.setCellStyle(ExcelCellStyle.defaultFontStyleCenter);
				evaluator.evaluateFormulaCell(cell);
			}
		}
		row = masterSheet.getRow(lastStaffRowIndex);
		cell = row.createCell(sheetCol + 4);
		cell.setCellStyle(ExcelCellStyle.defaultFontStyleCenterBold);
		evaluator.clearAllCachedResultValues();
		cell.setCellFormula("SUM(BP10:BP" + lastStaffRowIndex + ")");

		masterSheet.addMergedRegion(new CellRangeAddress(3, 5, 68, 68));
		row = masterSheet.getRow(3);
		cell = row.createCell(sheetCol + 5);
		cell.setCellValue("Paternity Leave");
		cell.setCellStyle(ExcelCellStyle.defaultFontStyleCenterBoldBottomAlign);
		row = masterSheet.getRow(4);
		cell = row.createCell(sheetCol + 5);
		cell.setCellStyle(ExcelCellStyle.defaultFontStyleCenter);
		row = masterSheet.getRow(5);
		cell = row.createCell(sheetCol + 5);
		cell.setCellStyle(ExcelCellStyle.defaultFontStyleCenter);
		row = masterSheet.getRow(6);
		cell = row.createCell(sheetCol + 5);
		cell.setCellStyle(ExcelCellStyle.defaultFontStyleCenter);
		row = masterSheet.getRow(7);
		cell = row.createCell(sheetCol + 5);
		cell.setCellStyle(ExcelCellStyle.defaultFontStyleCenter);
		row = masterSheet.getRow(8);
		cell = row.createCell(sheetCol + 5);
		cell.setCellStyle(ExcelCellStyle.defaultFontStyleCenter);
		for (int j = 9; j < lastStaffRowIndex; j++) {
			row = masterSheet.getRow(j);
			cell = row.createCell(sheetCol + 5);
			if (!sessionFirstRowIndexes.contains(Integer.valueOf(j))) {
				cell.setCellFormula("COUNTIF(B" + (j + 1) + ":BK" + (j + 1) + ", \"P\")/2");
				cell.setCellStyle(ExcelCellStyle.defaultFontStyleCenter);
				evaluator.evaluateFormulaCell(cell);
			}
		}
		row = masterSheet.getRow(lastStaffRowIndex);
		cell = row.createCell(sheetCol + 5);
		cell.setCellStyle(ExcelCellStyle.defaultFontStyleCenterBold);
		evaluator.clearAllCachedResultValues();
		cell.setCellFormula("SUM(BQ10:BQ" + lastStaffRowIndex + ")");

		masterSheet.addMergedRegion(new CellRangeAddress(3, 5, 69, 69));
		row = masterSheet.getRow(3);
		cell = row.createCell(sheetCol + 6);
		cell.setCellValue("No Pay Leave");
		cell.setCellStyle(ExcelCellStyle.defaultFontStyleCenterBoldBottomAlign);
		row = masterSheet.getRow(4);
		cell = row.createCell(sheetCol + 6);
		cell.setCellStyle(ExcelCellStyle.defaultFontStyleCenter);
		row = masterSheet.getRow(5);
		cell = row.createCell(sheetCol + 6);
		cell.setCellStyle(ExcelCellStyle.defaultFontStyleCenter);
		row = masterSheet.getRow(6);
		cell = row.createCell(sheetCol + 6);
		cell.setCellStyle(ExcelCellStyle.defaultFontStyleCenter);
		row = masterSheet.getRow(7);
		cell = row.createCell(sheetCol + 6);
		cell.setCellStyle(ExcelCellStyle.defaultFontStyleCenter);
		row = masterSheet.getRow(8);
		cell = row.createCell(sheetCol + 6);
		cell.setCellStyle(ExcelCellStyle.defaultFontStyleCenter);
		for (int j = 9; j < lastStaffRowIndex; j++) {
			row = masterSheet.getRow(j);
			cell = row.createCell(sheetCol + 6);
			if (!sessionFirstRowIndexes.contains(Integer.valueOf(j))) {
				cell.setCellFormula("COUNTIF(B" + (j + 1) + ":BK" + (j + 1) + ", \"NP\")/2");
				cell.setCellStyle(ExcelCellStyle.defaultFontStyleCenter);
				evaluator.evaluateFormulaCell(cell);
			}
		}

		row = masterSheet.getRow(lastStaffRowIndex);
		cell = row.createCell(sheetCol + 6);
		cell.setCellStyle(ExcelCellStyle.defaultFontStyleCenterBold);
		cell.setCellFormula("SUM(BR10:BR" + lastStaffRowIndex + ")");
		evaluator.clearAllCachedResultValues();
		evaluator.evaluateFormulaCell(cell);

		masterSheet.setColumnWidth(sheetCol, 2600);
		masterSheet.setColumnWidth(sheetCol + 1, 2600);
		masterSheet.setColumnWidth(sheetCol + 2, 2600);
		masterSheet.setColumnWidth(sheetCol + 3, 2600);
		masterSheet.setColumnWidth(sheetCol + 4, 2600);
		masterSheet.setColumnWidth(sheetCol + 5, 2600);

		row = masterSheet.getRow(lastStaffRowIndex);
		cell = row.createCell((maxDay * 2) - 6);
		cell.setCellValue("GRAND TOTAL (no. of days): ");
		cell.setCellStyle(ExcelCellStyle.defaultFontStyleRightAlignBold);
		masterSheet.addMergedRegion(new CellRangeAddress(lastStaffRowIndex, lastStaffRowIndex, (maxDay * 2) - 6, 62));
		cell = row.createCell((maxDay * 2) - 6 + 1);
		cell.setCellStyle(ExcelCellStyle.defaultFontStyleRightAlignBold);
		cell = row.createCell((maxDay * 2) - 6 + 2);
		cell.setCellStyle(ExcelCellStyle.defaultFontStyleRightAlignBold);
		cell = row.createCell((maxDay * 2) - 6 + 3);
		cell.setCellStyle(ExcelCellStyle.defaultFontStyleRightAlignBold);
		cell = row.createCell((maxDay * 2) - 6 + 4);
		cell.setCellStyle(ExcelCellStyle.defaultFontStyleRightAlignBold);
		cell = row.createCell((maxDay * 2) - 6 + 5);
		cell.setCellStyle(ExcelCellStyle.defaultFontStyleRightAlignBold);
		cell = row.createCell((maxDay * 2) - 6 + 6);
		cell.setCellStyle(ExcelCellStyle.defaultFontStyleRightAlignBold);

	}

}
