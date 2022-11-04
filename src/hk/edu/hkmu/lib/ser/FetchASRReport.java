package hk.edu.hkmu.lib.ser;

import java.util.*;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.*;

import hk.edu.hkmu.lib.*;

import org.apache.commons.lang3.math.*;
import java.io.*;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * OUHK In-house Library Tool - Fetch the report from Aleph's Oracle for SER's
 * ASR. The report mainly uses Aleph's Oracle table Z68 (orders). But to
 * complete the reports, Z76 (budget, for the budget holder) and Z601 (budget
 * transaction, for current and last year budget spend) are also queried in
 * separate SQLs.
 * 
 * 
 * @author Wai-yan NG
 * @author wwyng@ouhk.edu.hk
 * @version 1.0
 * @since Mar 25, 2019
 */

public class FetchASRReport extends Report {

	private String outputHTML = "";
	private String SVStartDate = "";
	private String SVEndDate = "";
	private String LCStartDate = "";
	private String LCEndDate = "";
	private String now = StringHandling.getToday();
	private String thisBudgetYear = "";
	private String nextBudgetYear = "";
	private String lastBudgetYear = "";
	private String rec_key = "";
	private String order_no = "";
	private String title = "";
	private String adm_doc_no = "";
	private String est_list_price = "";
	private String est_price_curr = "";
	private String format = "";
	private String method_of_acq = "";
	private String open_date = "";
	private String order_date = "";
	private String order_group = "";
	private String order_status = "";
	private String order_change_date = "";
	private String order_type = "";
	private String order_no_1 = "";
	private String order_final_price = "";
	private String order_local_price = "";
	private String price_note = "";
	private String sublibrary = "";
	private String sub_date_from = "";
	private String sub_date_to = "";
	private String sub_renew_date = "";
	private String unit_price = "";
	private String vendor_code = "";
	private String library_note = "";
	private String quantity_text = "";
	private String publisher = "";
	private String vendor_name = "";
	private String last_exp = "";
	private String this_exp = "";
	private String budget = "";
	ArrayList<String> last_exps = null;
	ArrayList<String> this_exps = null;
	ArrayList<String> budgets = null;
	ArrayList<String> schools = null;
	int index = 0;

	private void init() {
		Config.init();
		SVStartDate = Config.VALUES.get("SVStartDate");
		SVEndDate = Config.VALUES.get("SVEndDate");
		LCStartDate = Config.VALUES.get("LCStartDate");
		LCEndDate = Config.VALUES.get("LCEndDate");
		thisBudgetYear = Config.VALUES.get("FINYEAR");
		lastBudgetYear = (Integer.parseInt(thisBudgetYear) - 1) + "";
		nextBudgetYear = (Integer.parseInt(thisBudgetYear) + 1) + "";
		super.setReportFile("SER-ASR-REPORT-" + now + ".xlsx");
	}

	/**
	 * Constructor of the class
	 * 
	 * @param reportPath The place the Excel report is saved, must have appropriate
	 *                   permission to write into the folder.
	 * @param wr         The PrintWriter object for writing out the HTML. Give null
	 *                   if not HTML report is needed.
	 */
	public FetchASRReport(String reportPath, Writer wr) {
		super(reportPath, wr);
		init();
	}

	/**
	 * Constructor of the class.
	 * 
	 * @param SVStartDate The start date of the order status "SV" or "RSV".
	 * @param SVEndDate   The end date of the order status "SV" or "RSV".
	 * @param LCStartDate The start date of the order status "LC".
	 * @param LCEndDate   The end date of the order status "LC".
	 * @param reportPath  The place the Excel report is saved, must have appropriate
	 *                    permission to write into the folder.
	 * @param wr          The PrintWriter object for writing out the HTML. Give null
	 *                    if not HTML report is needed.
	 */
	public FetchASRReport(String SVStartDate, String SVEndDate, String LCStartDate, String LCEndDate, String reportPath,
			Writer wr) {
		super(reportPath, wr);
		init();
		this.SVStartDate = SVStartDate;
		this.SVEndDate = SVEndDate;
		this.LCStartDate = LCStartDate;
		this.LCEndDate = LCEndDate;
		super.setReportPath(reportPath);
	}

	/*
	 * Private method for Creating an Excel header row of the final report.
	 * 
	 * @param sheet the Apache POI Excel sheet the header is written to.
	 * 
	 * @param headerCellStyle The Apache POI CellStyle for the header.
	 */
	private void createHeaderRow(Sheet sheet, CellStyle headerCellStyle) {
		Row headerRow = sheet.createRow(1);
		Cell cell = headerRow.createCell(0);

		cell = headerRow.createCell(0);
		cell.setCellValue("No.");
		cell.setCellStyle(headerCellStyle);

		cell = headerRow.createCell(1);
		cell.setCellValue("Order No.");
		cell.setCellStyle(headerCellStyle);

		cell = headerRow.createCell(2);
		cell.setCellValue("Title");
		cell.setCellStyle(headerCellStyle);

		cell = headerRow.createCell(3);
		cell.setCellValue("ADM doc. no.");
		cell.setCellStyle(headerCellStyle);

		cell = headerRow.createCell(4);
		cell.setCellValue("Budget Number");
		cell.setCellStyle(headerCellStyle);

		cell = headerRow.createCell(5);
		cell.setCellValue("School");
		cell.setCellStyle(headerCellStyle);

		cell = headerRow.createCell(6);
		cell.setCellValue("Est. List Price");
		cell.setCellStyle(headerCellStyle);

		cell = headerRow.createCell(7);
		cell.setCellValue("Est. Price Currency");
		cell.setCellStyle(headerCellStyle);

		cell = headerRow.createCell(8);
		cell.setCellValue("Format");
		cell.setCellStyle(headerCellStyle);

		cell = headerRow.createCell(9);
		cell.setCellValue("Method of Acquisition");
		cell.setCellStyle(headerCellStyle);

		cell = headerRow.createCell(10);
		cell.setCellValue("Open Date");
		cell.setCellStyle(headerCellStyle);

		cell = headerRow.createCell(11);
		cell.setCellValue("Order Date");
		cell.setCellStyle(headerCellStyle);

		cell = headerRow.createCell(12);
		cell.setCellValue("Order Group");
		cell.setCellStyle(headerCellStyle);

		cell = headerRow.createCell(13);
		cell.setCellValue("Order Status");
		cell.setCellStyle(headerCellStyle);

		cell = headerRow.createCell(14);
		cell.setCellValue("Order Status Change Date");
		cell.setCellStyle(headerCellStyle);

		cell = headerRow.createCell(15);
		cell.setCellValue("Order Type");
		cell.setCellStyle(headerCellStyle);

		cell = headerRow.createCell(16);
		cell.setCellValue("Order Number 1");
		cell.setCellStyle(headerCellStyle);

		cell = headerRow.createCell(17);
		cell.setCellValue("Order's Final Price");
		cell.setCellStyle(headerCellStyle);

		cell = headerRow.createCell(18);
		cell.setCellValue("Last Year Expenditure / Annual Fee (HKD)");
		cell.setCellStyle(headerCellStyle);

		cell = headerRow.createCell(19);
		cell.setCellValue("This Year Expenditure / Annual Fee (HKD)");
		cell.setCellStyle(headerCellStyle);

		cell = headerRow.createCell(20);
		cell.setCellValue("Price Note");
		cell.setCellStyle(headerCellStyle);

		cell = headerRow.createCell(21);
		cell.setCellValue("Sublibrary");
		cell.setCellStyle(headerCellStyle);

		cell = headerRow.createCell(22);
		cell.setCellValue("Subscription Date From");
		cell.setCellStyle(headerCellStyle);

		cell = headerRow.createCell(23);
		cell.setCellValue("Subscription Date To");
		cell.setCellStyle(headerCellStyle);

		cell = headerRow.createCell(24);
		cell.setCellValue("Subscription Renew Date");
		cell.setCellStyle(headerCellStyle);

		cell = headerRow.createCell(25);
		cell.setCellValue("Unit Price Description");
		cell.setCellStyle(headerCellStyle);

		cell = headerRow.createCell(26);
		cell.setCellValue("Vendor Code");
		cell.setCellStyle(headerCellStyle);

		cell = headerRow.createCell(27);
		cell.setCellValue("Library Note");
		cell.setCellStyle(headerCellStyle);

		cell = headerRow.createCell(28);
		cell.setCellValue("Quantity Note");
		cell.setCellStyle(headerCellStyle);

		cell = headerRow.createCell(29);
		cell.setCellValue("Publisher");
		cell.setCellStyle(headerCellStyle);

		cell = headerRow.createCell(30);
		cell.setCellValue("Z13 Rec Key");
		cell.setCellStyle(headerCellStyle);

		cell = headerRow.createCell(31);
		cell.setCellValue("Z70 Vendor Name");
		cell.setCellStyle(headerCellStyle);

		cell = headerRow.createCell(32);
		cell.setCellValue("Z68_E_LOCAL_PRICE");
		cell.setCellStyle(headerCellStyle);

		headerRow.setHeight((short) (256 * 5));
	}

	/*
	 * Assumed the various variables are read into by SQLs, write a single row
	 * report on the Apache POI Excel sheet.
	 * 
	 * @param row the Excel row associated with the report Apache POI sheet.
	 * 
	 * @param count the row number
	 * 
	 * @param priceCellStyle Apache POI style of the price.
	 * 
	 * @param centerCellStyle Apache POI style for centering text.
	 * 
	 * @param numericCellSytle Apache POI style for numeric.
	 * 
	 * @param dateCellStyle Apache POI style for date.
	 */
	private void reportOneRow(Row row, int count, CellStyle priceCellStyle, CellStyle centerCellStyle,
			CellStyle numericCellStyle, CellStyle dateCellStyle) throws ParseException {

		if (est_list_price.trim().isEmpty())
			est_list_price = "-";

		if (this_exp.trim().isEmpty())
			this_exp = "-";

		if (order_final_price.trim().isEmpty())
			order_final_price = "-";

		if (order_no_1.trim().isEmpty())
			order_no_1 = "-";

		if (price_note.trim().isEmpty())
			price_note = "-";

		row.createCell(0).setCellValue(count);

		if (NumberUtils.isDigits(order_no)) {
			double dnum = Double.parseDouble(order_no);
			row.createCell(1).setCellValue(dnum);
			row.getCell(1).setCellStyle(numericCellStyle);
		} else {

			row.createCell(1).setCellValue(order_no);
		}

		row.createCell(2).setCellValue(title);

		if (NumberUtils.isDigits(adm_doc_no)) {
			int inum = Integer.parseInt(adm_doc_no);
			row.createCell(3).setCellValue(inum);
			row.getCell(3).setCellStyle(numericCellStyle);
		} else {
			row.createCell(3).setCellValue(adm_doc_no);
		}

		row.createCell(4).setCellValue(budget);
		row.createCell(5).setCellValue(schools.get(index));

		if (NumberUtils.isNumber(est_list_price)) {
			double dnum = Double.parseDouble(est_list_price);
			row.createCell(6).setCellValue(dnum);
			row.getCell(6).setCellStyle(priceCellStyle);
		} else {

			row.createCell(6).setCellValue(est_list_price);
		}

		row.createCell(7).setCellValue(est_price_curr);
		row.createCell(8).setCellValue(format);
		row.createCell(9).setCellValue(method_of_acq);

		if (open_date.equals("0"))
			open_date = "-";

		if (open_date.matches("\\d{8}")) {
			SimpleDateFormat datetemp = new SimpleDateFormat("yyyyMMdd");
			java.util.Date cellValue = datetemp.parse(open_date);
			row.createCell(10).setCellValue(cellValue);
			row.getCell(10).setCellStyle(dateCellStyle);
		} else {
			row.createCell(10).setCellValue(open_date);
			row.getCell(10).setCellStyle(centerCellStyle);
		}

		if (order_date.equals("0"))
			order_date = "-";

		if (order_date.matches("\\d{8}")) {
			SimpleDateFormat datetemp = new SimpleDateFormat("yyyyMMdd");
			java.util.Date cellValue = datetemp.parse(order_date);
			row.createCell(11).setCellValue(cellValue);
			row.getCell(11).setCellStyle(dateCellStyle);
		} else {
			row.createCell(11).setCellValue(order_date);
			row.getCell(11).setCellStyle(centerCellStyle);
		}

		row.createCell(12).setCellValue(order_group);
		row.createCell(13).setCellValue(order_status);

		if (order_change_date.equals("0"))
			order_change_date = "-";

		if (order_change_date.matches("\\d{8}")) {
			SimpleDateFormat datetemp = new SimpleDateFormat("yyyyMMdd");
			java.util.Date cellValue = datetemp.parse(order_change_date);
			row.createCell(14).setCellValue(cellValue);
			row.getCell(14).setCellStyle(dateCellStyle);
		} else {
			row.createCell(14).setCellValue(order_change_date);
			row.getCell(14).setCellStyle(centerCellStyle);
		}

		row.createCell(15).setCellValue(order_type);

		if (!order_no_1.equals("-")) {
			if (NumberUtils.isDigits(order_no_1)) {
				double dnum = Double.parseDouble(order_no_1);
				row.createCell(16).setCellValue(dnum);
				row.getCell(16).setCellStyle(numericCellStyle);
			}
		} else {
			row.createCell(16).setCellValue(order_no_1);
			row.getCell(16).setCellStyle(centerCellStyle);
		}

		if (NumberUtils.isNumber(order_final_price)) {
			double dnum = Double.parseDouble(order_final_price);
			row.createCell(17).setCellValue(dnum);
			row.getCell(17).setCellStyle(priceCellStyle);
		} else {
			row.createCell(17).setCellValue(order_final_price);
			row.getCell(17).setCellStyle(centerCellStyle);
		}

		if (NumberUtils.isNumber(last_exps.get(index))) {
			double dnum = Double.parseDouble(last_exps.get(index));
			row.createCell(18).setCellValue(dnum);
			row.getCell(18).setCellStyle(priceCellStyle);
		} else {

			row.createCell(18).setCellValue(last_exps.get(index));
			row.getCell(18).setCellStyle(centerCellStyle);
		}

		if (NumberUtils.isNumber(this_exps.get(index))) {
			double dnum = Double.parseDouble(this_exps.get(index));
			row.createCell(19).setCellValue(dnum);
			row.getCell(19).setCellStyle(priceCellStyle);
		} else {

			row.createCell(19).setCellValue(this_exps.get(index));
			row.getCell(19).setCellStyle(centerCellStyle);
		}

		if (!price_note.equals("-")) {
			row.createCell(20).setCellValue(price_note);
		} else {
			row.createCell(20).setCellValue(price_note);
			row.getCell(20).setCellStyle(centerCellStyle);
		}

		row.createCell(21).setCellValue(sublibrary);

		if (sub_date_from.equals("0"))
			sub_date_from = "-";
		if (sub_date_from.matches("\\d{8}")) {
			SimpleDateFormat datetemp = new SimpleDateFormat("yyyyMMdd");
			java.util.Date cellValue = datetemp.parse(sub_date_from);
			row.createCell(22).setCellValue(cellValue);
			row.getCell(22).setCellStyle(dateCellStyle);
		} else {
			row.createCell(22).setCellValue(sub_date_from);
		}

		if (sub_date_to.equals("0"))
			sub_date_to = "-";
		if (sub_date_to.matches("\\d{8}")) {
			SimpleDateFormat datetemp = new SimpleDateFormat("yyyyMMdd");
			java.util.Date cellValue = datetemp.parse(sub_date_to);
			row.createCell(23).setCellValue(cellValue);
			row.getCell(23).setCellStyle(dateCellStyle);
		} else {
			row.createCell(23).setCellValue(sub_date_to);
		}
		if (sub_renew_date.equals("0"))
			sub_renew_date = "-";

		if (sub_renew_date.matches("\\d{8}")) {
			SimpleDateFormat datetemp = new SimpleDateFormat("yyyyMMdd");
			java.util.Date cellValue = datetemp.parse(sub_renew_date);
			row.createCell(24).setCellValue(cellValue);
			row.getCell(24).setCellStyle(dateCellStyle);
		} else {
			row.createCell(24).setCellValue(sub_renew_date);
		}

		if (NumberUtils.isNumber(unit_price)) {
			double dnum = Double.parseDouble(unit_price);
			row.createCell(25).setCellValue(dnum);
			row.getCell(25).setCellStyle(priceCellStyle);
		} else {

			row.createCell(25).setCellValue(unit_price);
			row.getCell(25).setCellStyle(centerCellStyle);
		}

		row.createCell(26).setCellValue(vendor_code);
		row.createCell(27).setCellValue(library_note);
		row.createCell(28).setCellValue(quantity_text);
		row.createCell(29).setCellValue(publisher);

		if (NumberUtils.isDigits(rec_key)) {
			int inum = Integer.parseInt(rec_key);
			row.createCell(30).setCellValue(inum);
			row.getCell(30).setCellStyle(numericCellStyle);
		} else {
			row.createCell(30).setCellValue(rec_key);
			row.getCell(30).setCellStyle(centerCellStyle);
		}

		row.createCell(31).setCellValue(vendor_name);

		if (NumberUtils.isNumber(order_local_price)) {
			double dnum = Double.parseDouble(order_local_price);
			row.createCell(32).setCellValue(dnum);
			row.getCell(32).setCellStyle(priceCellStyle);
		} else {

			row.createCell(32).setCellValue(order_local_price);
			row.getCell(32).setCellStyle(centerCellStyle);
		}

	}

	/**
	 * Fetching the report in Excel and HTML format. The Excel report is saved in
	 * 'reprotPath' which is set by creating the class instance; while HTML report
	 * is written out by the Writer 'wr' object if it is set by the constructor. The
	 * report file name, which is generated with the time stamp invoking report
	 * generation, can be get by the method 'getReportFile()'.
	 */
	public void fetchReport() {

		Connection conAleph = null;
		Statement stmt = null;
		Statement stmt2 = null;
		Statement stmt3 = null;
		ResultSet rs2 = null;
		ResultSet rs3 = null;

		int counts[] = new int[Config.SCHOOLS.length];

		for (int i = 0; i < counts.length; i++)
			counts[i] = 1;
		int count = 1;

		System.setProperty("org.apache.poi.util.POILogger", "org.apache.poi.util.NullLogger");

		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			conAleph = DriverManager.getConnection("jdbc:oracle:thin:@aleph.lib.ouhk.edu.hk:1521:aleph22", "oul01",
					"oul01");

			/*
			 * The main SQL statement, fetching the table Z68 with title and publisher
			 * (Z13), and the vendor name (Z70).
			 */
			String sql = "select z68_order_number, z13_title, SUBSTR(z68_rec_key,1,9) as ADM_REC_KEY, z68_e_price /100 as EST_LIST_PRICE, Z68_E_CURRENCY, Z68_MATERIAL_TYPE, Z68_METHOD_OF_AQUISITION,\r\n"
					+ "		Z68_NO_UNITS, Z68_ORDER_DATE as ORDER_DATE, Z68_ORDER_DELIVERY_TYPE, Z68_OPEN_DATE, Z68_ORDER_GROUP, Z68_ORDER_NUMBER_2, z68_order_status,\r\n"
					+ "		Z68_ORDER_STATUS_DATE_X as ORDER_CHANGE_DATE, Z68_ORDER_TYPE, z68_order_number_1, Z68_TOTAL_PRICE as ORDER_FINAL_PRICE, Z68_E_LOCAL_PRICE / 100 as ORDER_LOCAL_PRICE,\r\n"
					+ "		Z68_E_NOTE, Z68_SUB_LIBRARY, z68_subscription_date_from, Z68_SUBSCRIPTION_DATE_TO,\r\n"
					+ "		z68_subscription_renew_date, Z68_UNIT_PRICE, z68_vendor_code,  z68_library_note, z68_quantity_text, z13_imprint, z13_rec_key, z70_vendor_name\r\n"
					+ "		\r\n" + "from oul50.z68 z68, oul50.z13 z13, oul50.z70 z70\r\n" + "\r\n" + "where\r\n"
					+ "	(( (z68.z68_order_status = 'SV' or z68.z68_order_status = 'RSV') and (z68.z68_open_date >= '"
					+ SVStartDate + "' and z68.z68_open_date <= '" + SVEndDate + "') )\r\n"
					+ "	or ( z68.z68_order_status = 'LC' or z68.z68_order_status = 'CLS' and (z68.z68_ORDER_STATUS_DATE_X  >= '"
					+ LCStartDate + "' and z68.z68_ORDER_STATUS_DATE_X  <= '" + LCEndDate + "') ))\r\n"
					+ "	and (z68.Z68_ORDER_TYPE  = 'O' or z68.Z68_ORDER_TYPE  = 'S')\r\n"
					+ "	and TRIM(SUBSTR(z68.z68_rec_key, 1, 9))=TRIM(z13.z13_rec_key)\r\n"
					+ "	and TRIM(z68.z68_vendor_code) = TRIM(z70.z70_rec_key)"
					+ " and (Z68_METHOD_OF_AQUISITION = 'P' or Z68_METHOD_OF_AQUISITION = 'PF')";
			
			
			System.out.println(sql);

			stmt = conAleph.createStatement();
			ResultSet rs = stmt.executeQuery(sql);

			SXSSFWorkbook workbook = new SXSSFWorkbook(100);
			SXSSFSheet sheet = workbook.createSheet("ALL");
			sheet.createFreezePane(0, 2);
			SXSSFSheet sheets[] = new SXSSFSheet[Config.SCHOOLS.length];
			CreationHelper createHelper = workbook.getCreationHelper();

			Font headerFont = workbook.createFont();
			headerFont.setBold(true);
			headerFont.setFontHeightInPoints((short) 12);

			CellStyle boldCellStyle = workbook.createCellStyle();
			boldCellStyle.setFont(headerFont);

			CellStyle headerCellStyle = workbook.createCellStyle();
			headerCellStyle.setFont(headerFont);
			headerCellStyle.setVerticalAlignment(VerticalAlignment.TOP);
			headerCellStyle.setWrapText(true);

			CellStyle centerCellStyle = (CellStyle) workbook.createCellStyle();
			centerCellStyle.setAlignment(HorizontalAlignment.CENTER);

			CellStyle priceCellStyle = workbook.createCellStyle();
			priceCellStyle.setDataFormat((short) 8);

			CellStyle numericCellStyle = workbook.createCellStyle();
			numericCellStyle.setDataFormat((short) 1);

			CellStyle dateCellStyle = workbook.createCellStyle();
			dateCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("yyyymmdd"));

			String firstRowText = "SER ASR Report. Generated time: " + now + ". SV / RSV Date Range: " + SVStartDate
					+ " - " + SVEndDate + ". LC Date Range: " + LCStartDate + " - " + LCEndDate + "."
					+ " Finanical Year: " + thisBudgetYear + " - " + nextBudgetYear + ".";

			// Initialize sheet for schools
			for (int i = 0; i < sheets.length; i++) {
				sheets[i] = workbook.createSheet(Config.SCHOOLS[i]);
				sheets[i].createFreezePane(0, 2);
				Row titleRow = sheets[i].createRow(0);
				Cell cell = titleRow.createCell(0);
				cell.setCellValue(firstRowText + "School Report " + Config.SCHOOLS[i]);
				cell.setCellStyle(boldCellStyle);

				createHeaderRow(sheets[i], headerCellStyle);
				sheets[i].trackAllColumnsForAutoSizing();
				sheets[i].autoSizeColumn(1);
				sheets[i].autoSizeColumn(2);
				sheets[i].autoSizeColumn(4);
				sheets[i].autoSizeColumn(3);

			}

			// Initialize the All report sheet.
			Row titleRow = sheet.createRow(0);
			Cell cell = titleRow.createCell(0);
			cell.setCellValue(firstRowText);
			cell.setCellStyle(boldCellStyle);
			createHeaderRow(sheet, headerCellStyle);

			outputHTML = "Report generated time: " + now + "<br>";
			outputHTML += "<h3><div align=center> SER ASR Report " + now + " </h3> <br>";
			outputHTML += "<table id='reportTable' class='table table-hover sortable'> <thead> <tr> <td> No. </td> <td> Order Number </td> <td> Title </td>"
					+ "<td> ADM doc. no.</td> <td> Budget Number </td> <td> School </td> <td> Estimated Listed Price </td> <td> Est. Price Currency </td>"
					+ "<td> Format </td> <td> method of Acq </td> <td> Number of Units Ordered </td> <td> Open Date </td> <td> Order Date </td> <td> Order Delivery Type </td>"
					+ "<td> Order Group </td> <td> Order Number </td> <td> Order Status </td> <td> Order Status Change Date </td> <td> Order Type </td>"
					+ "<td> Order Number 1 </td> <td> Order's Final Price </td> <td> Last Year Expenditure / Annual Fee (HKD) </td>"
					+ "<td> This Year Expenditure / Annual Fee (HKD) </td> <td> Prce Note </td> <td> subLibrary </td> <td> Subscription Date From </td>"
					+ "<td> Subscription Date To </td> <td> Subscription Renew Date </td> <td> Unit Price Description</td> <td> Vendor Code </td>"
					+ "<td> Library Note </td> <td> Quantity Note </td> <td> Publisher </td> <td> Z13 Rec Key </td>"
					+ "<td> Z70 Vendor Name</td> <td> Z68_E_LOCAL_PRICE </td>" + "</tr> </thead>\n";
			outputHTML += "<tbody>";

			if (wr != null) {
				wr.write(outputHTML);
				wr.flush();
			}

			outputHTML = "";

			while (rs.next()) {

				rec_key = rs.getString("Z13_REC_KEY");
				rec_key = rec_key.trim();
				order_no = rs.getString("Z68_ORDER_NUMBER");
				order_no = order_no.trim();
				title = rs.getString("Z13_TITLE");
				adm_doc_no = rs.getString("ADM_REC_KEY");
				adm_doc_no = adm_doc_no.trim();
				est_list_price = rs.getString("EST_LIST_PRICE");
				est_price_curr = rs.getString("Z68_E_CURRENCY");
				format = rs.getString("Z68_MATERIAL_TYPE");
				method_of_acq = rs.getString("Z68_METHOD_OF_AQUISITION");
				open_date = rs.getString("Z68_OPEN_DATE");
				order_date = rs.getString("ORDER_DATE");
				order_group = rs.getString("Z68_ORDER_GROUP");
				order_status = rs.getString("z68_order_status");
				order_change_date = rs.getString("ORDER_CHANGE_DATE");
				order_type = rs.getString("Z68_ORDER_TYPE");
				order_no_1 = rs.getString("z68_order_number_1");
				if (order_no_1 == null)
					order_no_1 = "";
				order_final_price = rs.getString("ORDER_FINAL_PRICE");
				if (order_final_price == null || order_final_price.trim().equals(""))
					order_final_price = "-";
				order_local_price = rs.getString("ORDER_LOCAL_PRICE");
				if (order_local_price == null || order_local_price.trim().equals(""))
					order_local_price = "-";
				price_note = rs.getString("Z68_E_NOTE");
				if (price_note == null)
					price_note = "";
				sublibrary = rs.getString("Z68_SUB_LIBRARY");
				sub_date_from = rs.getString("z68_subscription_date_from");
				sub_date_to = rs.getString("z68_subscription_date_to");
				sub_renew_date = rs.getString("z68_subscription_renew_date");
				unit_price = rs.getString("Z68_UNIT_PRICE");

				if (unit_price == null)
					unit_price = "----";

				unit_price = unit_price.trim();
				vendor_code = rs.getString("z68_vendor_code");
				library_note = rs.getString("z68_library_note");
				quantity_text = rs.getString("z68_quantity_text");
				publisher = rs.getString("z13_imprint");
				vendor_name = rs.getString("z70_vendor_name");
				vendor_name = vendor_name.trim();
				if (vendor_name == null)
					vendor_name = "";
				last_exp = "";
				this_exp = "";
				last_exps = new ArrayList<String>();
				this_exps = new ArrayList<String>();
				budgets = new ArrayList<String>();
				schools = new ArrayList<String>();

				// Since Aleph may have the problem of the databases OUL50 and OUL01 is not in
				// sync, fetch the title and imprint from OUL01 (the pivot BIB database) if
				// the title and imprint are failed to be get in OUL50.

				if (title == null || title.trim().equals("")) {
					String sql1_1 = "select z13_title, z13_imprint from oul01.z13 z13 where z13.z13_rec_key = '"
							+ rec_key + "'";
					stmt2 = conAleph.createStatement();
					rs2 = stmt2.executeQuery(sql1_1);
					rs2.next();
					title = rs2.getString("z13_title");
					publisher = rs2.getString("z13_imprint");
					rs2.close();
					stmt2.close();
				}

				/*
				 * The 2nd SQL statement. Generate the budget codes (Z76_BUDGET_NUMBER) and the
				 * school name (in Z76_NAME) associated with the order number.
				 */
				String sql2 = " select UNIQUE(z76_budget_number), z76_name\r\n" + " \r\n"
						+ " from  oul50.z76 z76, oul50.z601 z601, oul50.z68 z68\r\n" + " \r\n"
						+ " where z601.z601_rec_key_3=z68.z68_rec_key\n" + " and z68_order_number='" + order_no
						+ "' and z76.z76_budget_number=SUBSTR(z601.z601_rec_key,1,50)\r\n";

				stmt2 = conAleph.createStatement();
				rs2 = stmt2.executeQuery(sql2);

				while (rs2.next()) {
					String budget_no = rs2.getString("z76_budget_number");
					budget_no = budget_no.trim();
					
					String school = rs2.getString("z76_name").trim();
					String trimStr[] = Config.VALUES.get("FORTRIMSCHOOLCODE").split(",");

					for (int j = 0; j < trimStr.length; j++) {
						school = school.replaceAll(trimStr[j].trim(), "");
					}
					school = school.trim();

					if (budget_no.contains("-" + thisBudgetYear)) {

						budgets.add(budget_no);
						schools.add(school);
						
						String budget_no_this = "";
						String budget_no_last = "";

/* 20190430: SER requests, this / last year budget are no longer grouped by the budget form. Now comment out the codes here.
 
						// Obtaining the this and last year's budget code form (replacing all budget
						// number with "_" for grouping before doing SQL). The forms of the budget code
						// are defined in config.txt and are read by the class Config earlier.
						String budgetForms[] = Config.VALUES.get("BUDGETCODEFORM").split(",");
						int matchCount = 0;
						int budget_no_form_index = -1;
						
						for (int j = 0; j < budgetForms.length; j++) {
							matchCount = 0;
							for (int k = 0; k < budgetForms[j].length(); k++) {
								budgetForms[j] = budgetForms[j].trim();
								budget_no = budget_no.trim();

								if (budgetForms[j].length() != budget_no.length())
									continue;

								if ((budget_no.charAt(k) + "").matches("^\\d|[a-zA-Z]$")
										&& (budgetForms[j].charAt(k) + "").matches("^\\d|[a-zA-Z]$")) {
									matchCount++;
								}
								if (budget_no.charAt(k) == '-' && budgetForms[j].charAt(k) == '-') {
									matchCount++;
								}
							}
							if (matchCount == budget_no.length())
								budget_no_form_index = j;

						}

						if (budget_no_form_index > -1) {
							for (int j = 0; j < budgetForms[budget_no_form_index].length(); j++) {
								if (budgetForms[budget_no_form_index].charAt(j) != 'B'
										|| budgetForms[budget_no_form_index].charAt(j) == '-') {
									budget_no_this += budget_no.charAt(j);
								} else {
									budget_no_this += "_";
								}
							}
						}
*/
						
						// Getting the budget codes.
						budget_no_this = budget_no;
						budget_no_last = budget_no_this;
						budget_no_last = budget_no_last.replaceAll("\\-\\d{4}$", "-" + lastBudgetYear);

						/*
						 * The 1st statement of the 3rd set SQL statement. For obtaining the last year
						 * budget expenditure from Z601. And is retrieved by the same budget code of the
						 * last year (budget_no_last).
						 */
						String sql3 = "select  z601_local_sum / 100 as LOCAL_SUM from oul50.z601 z601, oul50.z68 z68 \n"
								+ "where z601.z601_rec_key_3=z68.z68_rec_key and NOT z601_local_sum = 0 and  \n"
								+ "z68.z68_order_number='" + order_no + "' and z601.z601_rec_key like '"
								+ budget_no_last + "%'  and z601.z601_credit_debit='D'\n\n";

						stmt3 = conAleph.createStatement();
						rs3 = stmt3.executeQuery(sql3);
						double last_exp_sum = 0;
						int rowCount = 0;
						while (rs3.next()) {
							rowCount++;
							last_exp = rs3.getString("LOCAL_SUM");
							if (NumberUtils.isNumber(last_exp)) {
								last_exp_sum += Double.parseDouble(last_exp);
							}
						}

						if (rowCount == 0)
							last_exp_sum = -1;

						if (last_exp_sum == -1) {
							last_exp = "-";
						} else {
							last_exp = Double.toString(last_exp_sum);
						}
						last_exps.add(last_exp);

						/*
						 * The 2nd statement of the 3rd set SQL statement. For obtaining the this year
						 * budget expenditure. It is retrieved by the same budget code of the this year
						 * (budget_no_this).
						 */

						sql3 = "select  z601_local_sum / 100 as LOCAL_SUM from oul50.z601 z601, oul50.z68 z68 \n"
								+ "where z601.z601_rec_key_3=z68.z68_rec_key and NOT z601_local_sum = 0 and  \n"
								+ "z68.z68_order_number='" + order_no + "' and z601.z601_rec_key like '"
								+ budget_no_this + "%'  and z601.z601_credit_debit='D'";

						double this_exp_sum = 0;
						rowCount = 0;
						rs3 = stmt3.executeQuery(sql3);
						while (rs3.next()) {
							rowCount++;
							this_exp = rs3.getString("LOCAL_SUM");
							if (NumberUtils.isNumber(this_exp)) {
								this_exp_sum += Double.parseDouble(this_exp);
							}
						}

						if (rowCount == 0)
							this_exp_sum = -1;

						if (this_exp_sum == -1) {
							this_exp = "-";
						} else {
							this_exp = Double.toString(this_exp_sum);
						}

						this_exps.add(this_exp);
						rs3.close();
						stmt3.close();

					}

				}
				rs2.close();
				stmt2.close();

				boolean hit = false;
				index = 0;

				for (String budget : budgets) {
					this.budget = budget;
					for (int i = 0; i < Config.SCHOOLS.length; i++) {
						if (schools.get(index).trim().toUpperCase().contains(Config.SCHOOLS[i].trim().toUpperCase())) {
							hit = true;
							Row row = sheets[i].createRow(counts[i] + 1);
							reportOneRow(row, counts[i], priceCellStyle, centerCellStyle, numericCellStyle,
									dateCellStyle);
							counts[i] = 1 + counts[i];
						} // end if
					} // end for

					if (!hit) {
						Row row = sheets[sheets.length - 1].createRow(counts[sheets.length - 1] + 1);
						row.createCell(0).setCellValue(counts[sheets.length - 1]);
						reportOneRow(row, counts[sheets.length - 1], priceCellStyle, centerCellStyle, numericCellStyle,
								dateCellStyle);
						counts[sheets.length - 1] = 1 + counts[sheets.length - 1];
					}

					Row row = sheet.createRow(count + 1);
					reportOneRow(row, count, priceCellStyle, centerCellStyle, numericCellStyle, dateCellStyle);

					String reportLine = "<tr> <td>#</td><td>" + order_no + "</td><td>" + title + "</td> <td> "
							+ adm_doc_no + "</td> <td>" + budget + "</td> <td> " + schools.get(index) + "</td>"
							+ " </td>" + "<td>" + est_list_price + "</td> <td>" + est_price_curr + "</td> <td>" + format
							+ "</td> <td>" + method_of_acq + "</td> <td> </td> <td>" + open_date + "</td> <td>"
							+ order_date + "</td> <td> </td> <td>" + order_group + "</td> <td> </td> <td>"
							+ order_status + "</td> <td>" + order_change_date + "</td> <td>" + order_type + "</td> <td>"
							+ order_no_1 + "</td> <td>" + order_final_price + "</td> <td>" + last_exp + "</td> <td>"
							+ this_exp + "</td> <td>" + price_note + "</td> <td>" + sublibrary + "</td> <td>"
							+ sub_date_from + "</td> <td>" + sub_date_to + "</td> <td>" + sub_renew_date + "</td> <td> "
							+ unit_price + "</td> <td>" + vendor_code + "</td> <td>" + library_note + "</td> <td> "
							+ quantity_text + "</td> <td>" + publisher + "</td> <td>" + rec_key + "</td> <td>"
							+ vendor_name + "</td> <td> " + order_local_price + " </tr>\n";

					if (wr != null) {
						wr.write(reportLine);
						wr.flush();
					}

					count++;
					index++;
				}

			}

			if (wr != null) {
				wr.write("</tbody></table>");
				wr.flush();
			}

			sheet.trackAllColumnsForAutoSizing();
			sheet.autoSizeColumn(1);
			sheet.autoSizeColumn(2);
			sheet.autoSizeColumn(4);
			sheet.autoSizeColumn(3);
			sheet.setColumnWidth(6, 256 * 12);
			sheet.setColumnWidth(17, 256 * 12);
			sheet.setColumnWidth(18, 256 * 12);
			sheet.setColumnWidth(19, 256 * 12);
			sheet.setColumnWidth(25, 256 * 12);
			sheet.setColumnWidth(32, 256 * 12);

			for (int i = 0; i < sheets.length; i++) {
				sheets[i].trackAllColumnsForAutoSizing();
				sheets[i].autoSizeColumn(1);
				sheets[i].autoSizeColumn(2);
				sheets[i].autoSizeColumn(4);
				sheets[i].autoSizeColumn(3);
				sheets[i].setColumnWidth(6, 256 * 12);
				sheets[i].setColumnWidth(17, 256 * 12);
				sheets[i].setColumnWidth(18, 256 * 12);
				sheets[i].setColumnWidth(19, 256 * 12);
				sheets[i].setColumnWidth(25, 256 * 12);
				sheets[i].setColumnWidth(32, 256 * 12);
			}

			FileOutputStream fileOut = new FileOutputStream(reportPath + reportFile);
			workbook.write(fileOut);
			workbook.dispose();
			workbook.close();
			fileOut.close();

			rs.close();
			stmt.close();
			conAleph.close();
		}

		catch (

		Exception e) {
			StringWriter writer = new StringWriter();
			PrintWriter printWriter = new PrintWriter(writer);
			e.printStackTrace(printWriter);
			System.out.println(writer.toString());
			e.printStackTrace();
		}

	}
}
