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

public class AlephToAlmaCourseReadingList {

	public static void main(String[] args) {

		try {
			ArrayList<String> excludeList = new ArrayList<String>();

			File file2 = new File("d:\\excludeList.txt");

			try {
				try (BufferedReader br = new BufferedReader(new FileReader(file2))) {
					String line;
					while ((line = br.readLine()) != null) {
						line = line.trim();
						line = line.replaceAll("\s", "");
						excludeList.add(line);
						// System.out.println(line);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			Class.forName("oracle.jdbc.driver.OracleDriver");

			BufferedWriter writer = new BufferedWriter(new FileWriter("D:\\exportedReadList.txt"));
			writer.write(
					"coursecode\tsection_id\tsearchable_id1\tsearchable_id2\tsearchable_id3\treading_list_code\treadin_list_name\treading_list_descripotion\treading_list_subject\treading_list_status\tRLStatus\tvisibility\treading_list_assigned_to\treading_list_library_note\treading_list_instructor_note\towner_user_name\tcreativecomone\tsection_name\tsection_description\tsection_start_date\tsection_end_date\tsection_tags\tcitation_secondary_type\tcitation_status\tcitation_tags\tcitation_mms_id\tcitation_original_system_id\tcitation_title\tcitation_journal_title\tcitation_author\tcitation_publication_date\tcitation_edition\tcitation_isbn\tcitation_issn\tcitation_place_of_publication\tcitation_publisher\tcitation_volume\tcitation_issue\tcitation_pages\tcitation_start_page\tcitation_end_page\tcitation_doi\tcitation_oclc\tcitation_lccn\tcitation_chapter\trlterms_chapter_title\tcitation_chapter_author\teditor\tcitation_source\tcitation_source1\tcitation_source2\tcitation_source3\tcitation_source4\tcitation_source5\tcitation_source6\tcitation_source7\tcitation_source8\tcitation_source9\tcitation_source10\tcitation_note\tadditional_person_name\tfile_name\tcitation_public_note\tlicense_type\tcitation_instructor_note\tcitation_library_note\texternal_system_id");

			HashMap<String, String[]> exceltable = new HashMap<String, String[]>();

			FileInputStream file = new FileInputStream(new File("d:\\Z108_20230628.xls"));

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

						exceltable.put(str, new String[25]);

						exceltable.get(str)[cell.getColumnIndex()] = str.replaceAll("....$", "").trim();
						exceltable.get(str)[20] = str.replaceAll(exceltable.get(str)[cell.getColumnIndex()], "").trim();
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

			for (String name : exceltable.keySet()) {
				String key = name.toString();
				String[] value = exceltable.get(name);

				String course_id = value[0].trim();
				course_id = course_id.replaceAll("\t", "");

				if (!excludeList.contains(course_id)) {

					String course_name = value[2].trim();

					if (value[4] == null)
						value[4] = "";

					String instructor = value[4].trim();

					if (value[6] == null)
						value[6] = "";

					String school = value[6].trim();

					if (value[10].equals("0"))
						value[10] = "20990101";

					if (value[11].equals("0"))
						value[11] = "19960321";

					String session = value[20];
					if (session != null)
						session = session.substring(2, 4);
					else
						session = "";
					System.out.println("Session: " + session);

					String start_date = value[10].trim().replace("'", "");
					String end_date = value[11].trim().replace("'", "");
					if (value[12] == null)
						value[12] = "";
					String term = value[12].trim();
					if (!course_id.equals("Z108_REC")) {
						System.out.println(course_id + " " + course_name);
						Connection conAleph = DriverManager.getConnection(
								"jdbc:oracle:thin:@aleph.lib.ouhk.edu.hk:1521:aleph22", "oul01", "oul01");
						Statement stmt = conAleph.createStatement();
						String sql = "select * from oul50.z13u where z13u_user_defined_7 like '%" + course_id + "%'";
						if (!course_id.equals("Z108_REC"))
							System.out.println("SQL:" + sql);
						ResultSet rs = stmt.executeQuery(sql);
						String rec_key;
						String title;
						while (rs.next()) {
							rec_key = rs.getString("Z13U_REC_KEY").trim();

							title = rs.getString("Z13U_USER_DEFINED_14");
							System.out.println("\t" + course_id + " " + course_name + " " + rec_key + " " + title);

							String mmsid = "";

							File myObj = new File("d:\\852HKMU_INST_001_BIB_IDs.csv");
							Scanner myReader = new Scanner(myObj);
							while (myReader.hasNextLine()) {
								String data = myReader.nextLine();
								if (data.contains(rec_key) && !data.contains("-OUL30"))
									mmsid = data.split(",")[0];
							}
							myReader.close();

							System.out.println("\tMMSID: " + mmsid);
							System.out.println("\tSession: " + session);
							writer.write(course_id + "\t" + session + "\t\t\t\t" + "RL_" + course_id + "_" + session
									+ "\t" + course_name + "\t\t\tComplete\tComplete\tFULL\t\t\t\t\t\t" + session
									+ "\t\t\t\t\tBK\tComplete\t\t" + mmsid + "\t\t" + title
									+ "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\n");
						}
						rs.close();
						stmt.close();
						conAleph.close();

						/*
						 * LocalDate sd =
						 * sdf.parse(start_date).toInstant().atZone(ZoneId.systemDefault()).toLocalDate(
						 * ); LocalDate ed =
						 * sdf.parse(end_date).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
						 * 
						 * if(!now.isAfter(sd) && now.isBefore(sd)) { exceltable.remove(key); }
						 */
					}
				}
			}

			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
