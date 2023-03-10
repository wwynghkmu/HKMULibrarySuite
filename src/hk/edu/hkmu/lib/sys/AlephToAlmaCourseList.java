package hk.edu.hkmu.lib.sys;

import java.io.*;
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
import hk.edu.hkmu.lib.sys.*;

public class AlephToAlmaCourseList {
	public static void main(String[] args) {

		try {

			HashMap<String, String[]> exceltable = new HashMap<String, String[]>();

			FileInputStream file = new FileInputStream(new File("d:\\Z108_course_202301017.xls"));

			// Create Workbook instance holding reference to .xlsx file
			HSSFWorkbook workbook = new HSSFWorkbook(file);

			// Get first/desired sheet from the workbook
			HSSFSheet sheet = workbook.getSheetAt(0);

			// Iterate through each rows one by one
			Iterator<Row> rowIterator = sheet.iterator();
			int row_no = 0;
			while (rowIterator.hasNext()) {
				row_no++;
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
						cell.setCellStyle(null);
						/*
						 * if(cell.getColumnIndex() == 10) System.out.println("NUMMM10----" + (long) f
						 * ); if(cell.getColumnIndex() == 11) System.out.println("NUMMM11----" + (long)
						 * f );
						 */
						break;

					case "FORMULA":
						str = cell.getStringCellValue();
						break;
					}

					
					if (cell.getColumnIndex() == 0) {
						exceltable.put(str, new String[20]);

						exceltable.get(str)[cell.getColumnIndex()] = str.replaceAll("....$", "").trim();
						exceltable.get(str)[row.getLastCellNum() + 1] = str
								.replaceAll(exceltable.get(str)[cell.getColumnIndex()], "").trim();
						key = str;
					} else {
						if (cell.getCellType().name().equals("NUMERIC")) {
							if (cell.getColumnIndex() == 10)
								exceltable.get(key)[10] = (long) f + "";
							if (cell.getColumnIndex() == 11)
								exceltable.get(key)[11] = (long) f + "";
						} else {
							exceltable.get(key)[cell.getColumnIndex()] = str;
						}

					}

				}

			}
			file.close();

			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			LocalDate now = LocalDate.now();

			for (String name : exceltable.keySet()) {
				String key = name.toString();
				String[] value = exceltable.get(name);

				String course_id = value[0].trim();
				
				String course_name = value[2].trim();

				if (value[4] == null)
					value[4] = "";

				String instructor = value[4].trim();

				if (value[6] == null)
					value[6] = "";

				String school = value[6].trim();

				String session = value[18];
				if(session == null)
					session = "        ";
				session = session.substring(2,4);

				String start_date = value[10];

				String end_date = value[11];

				String year = value[16];

				if (value[12] == null)
					value[12] = "";
				String term = value[12].trim();
				AlmaCreateCourse ac = new AlmaCreateCourse();
				
					ac.CreateCourse(course_id, course_name, instructor, school, start_date, end_date, term, year,
							session);

				/*
				 * LocalDate sd =
				 * sdf.parse(start_date).toInstant().atZone(ZoneId.systemDefault()).toLocalDate(
				 * ); LocalDate ed =
				 * sdf.parse(end_date).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
				 * 
				 * if(!now.isAfter(sd) && now.isBefore(sd)) { exceltable.remove(key); }
				 */

			}

			System.out.println(dtf.format(now));

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
