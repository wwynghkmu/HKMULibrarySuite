package hk.edu.hkmu.lib.sys.misctools;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.*;
import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.apache.commons.validator.routines.UrlValidator;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import hk.edu.hkmu.lib.*;

/**
 * HKMU In-house Library Tool
 * 
 * @author Wai-yan NG
 * @author wwyng@hkmu.edu.hk
 * @since 14 Mar 2023
 * 
 *        This class is used by Alma Implementation Project to migrate Aleph
 *        course reading list into Alma Reading List / Citations
 */

public class AlephExtractCourseCodeNotInCourseList {

	public static void main(String[] args) {

		try {

			Class.forName("oracle.jdbc.driver.OracleDriver");
			Set<String> set = new HashSet<>();
			BufferedWriter writer = new BufferedWriter(new FileWriter("D:\\exportedCourseCode.txt"));
			writer.write("series\n");

			HashMap<String, String[]> exceltable = new HashMap<String, String[]>();

			FileInputStream file = new FileInputStream(new File("d:\\Z108_course_202301017.xls"));

			// Create Workbook instance holding reference to .xlsx file
			HSSFWorkbook workbook = new HSSFWorkbook(file);

			// Get first/desired sheet from the workbook
			HSSFSheet sheet = workbook.getSheetAt(0);

			// Iterate through each rows one by one
			Iterator<Row> rowIterator = sheet.iterator();
			while (rowIterator.hasNext()) {
				Row row = rowIterator.next();
				String str = "";
				String key = "";
				double f = 0;

				// For each row, iterate through all the columns
				Iterator<Cell> cellIterator = row.cellIterator();

				while (cellIterator.hasNext()) {
					Cell cell = cellIterator.next();

					// Check the cell type and format accordingly
					switch (cell.getCellType().name()) {
					case "STRING":

						str = cell.getStringCellValue();
						break;
					case "NUMERIC":

						f = cell.getNumericCellValue();
						break;
					}

					if (cell.getColumnIndex() == 0) {

						exceltable.put(str, new String[20]);

						exceltable.get(str)[cell.getColumnIndex()] = str.replaceAll("....$", "").trim();
						exceltable.get(str)[row.getLastCellNum() + 1] = str
								.replaceAll(exceltable.get(str)[cell.getColumnIndex()], "").trim();
						key = str;

					} else {
						exceltable.get(key)[cell.getColumnIndex()] = str;

					}

				}

			}
			file.close();

			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			LocalDate now = LocalDate.now();

			Connection conAleph = DriverManager.getConnection("jdbc:oracle:thin:@aleph.lib.ouhk.edu.hk:1521:aleph22",
					"oul01", "oul01");
			Statement stmt = conAleph.createStatement();
			String sql = "select Z13U_USER_DEFINED_7 from oul50.z13u";
			ResultSet rs = stmt.executeQuery(sql);

			String rec_key;
			String title;
			int count = 0;
			while (rs.next()) {
				count++;
				String series = rs.getString("Z13U_USER_DEFINED_7");
				if (series != null)
					series = series.trim();
				boolean inCourse = false;
				for (String name : exceltable.keySet()) {
					String key = name.toString();
					String[] value = exceltable.get(name);

					String course_id = value[0].trim();
					course_id = course_id.replaceAll("\t", "");
					if (series != null && series.contains(course_id)) {
						inCourse = true;
					}
				}

				if (series != null && !inCourse
						&& (series.contains("(Exam papers)") || series.contains("(Set textbooks)")
								|| series.contains("(Course materials)")
								|| series.contains("(Other reserve materials)"))) {
					if (set.add(series))
						System.out.println(series);
				}

			}

			System.out.println("Total count: " + count);
			for (String str : set) {
				writer.write(str + "\n");
			}

			rs.close();
			stmt.close();
			conAleph.close();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
