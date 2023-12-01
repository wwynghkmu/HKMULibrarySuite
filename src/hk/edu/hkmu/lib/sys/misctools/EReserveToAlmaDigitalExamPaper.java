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
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.apache.commons.validator.routines.UrlValidator;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;

import hk.edu.hkmu.lib.*;

/**
 * HKMU In-house Library Tool
 * 
 * @author Wai-yan NG
 * @author wwyng@hkmu.edu.hk
 * @since 14 Nov 2023
 * 
 *        This class is used by the post Alma Implementation Project task to
 *        migrate EReserve Exam Papers into Alma Digital. The class will match
 *        each record with Alma's exported records wit course code and enrich
 *        the exported record by adding MMISD of Alma and convert the source
 *        into Alma Digital importable format . This also also copy and rename
 *        the EReserve exported exam papers (in PDF formation) into another
 *        directory which will be uploaded with the converted metadata file and
 *        imported into Alma Digital.
 */

public class EReserveToAlmaDigitalExamPaper {

	public static void main(String[] args) {

		try {

			HashMap<String, String> AlmaMMSID = new HashMap<String, String>();

			// Read the Alma exported Bib records which contains only Series, Title, and
			// MMSID.
			File file2 = new File("d:\\AlmaExamPaperMMSIDSB.csv");

			try {
				try (BufferedReader br = new BufferedReader(new FileReader(file2))) {
					String line;
					while ((line = br.readLine()) != null) {
						line = line.trim();
						String[] values = line.split(",");
						AlmaMMSID.put(values[0] + " " + values[1], values[2]);

					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			// Create Alma Digital readable file.
			BufferedWriter writer = new BufferedWriter(new FileWriter("D:\\EReserveToAlmaDigital.csv"));
			writer.write(
					"group_id,mms_id,collection_name,collection_id,collection_external,originating_system_id,contributor,coverage,creator,date,description,format,identifier,isbn,issn,Language,publisher,relation,rights,source,subject,title,type,bib custom field,rep_label,rep_public_note,rep_access_rights,rep_usage_type,rep_library,rep_note,rep_custom field,file_name_1,file_label_1,file_name_2,file_label_2");

			String exceltable[][] = new String[50000][40];

			// Read EReserve exported metadata file.
			FileInputStream file = new FileInputStream(new File("d:\\1st Exported with CIR's Modification.xlsx"));

			// Create Workbook instance holding reference to .xlsx file
			XSSFWorkbook workbook = new XSSFWorkbook(file);

			// Get first/desired sheet from the workbook
			XSSFSheet sheet = workbook.getSheetAt(0);

			// Iterate through each rows one by one
			int index = 0;
			Iterator<Row> rowIterator = sheet.iterator();
			while (rowIterator.hasNext()) {
				Row row = rowIterator.next();
				String str = "";

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
						str = Double.toString(f);
						break;
					}

					exceltable[index][cell.getColumnIndex()] = str;

				}
				index++;

			}
			file.close();

			LocalDate now = LocalDate.now();

			for (int i = 0, j = 0; i < 50000; i++) {

				String[] value = exceltable[i];

				if (value != null) {

					if (value[1] == null)
						value[1] = "N";

					if (value[2] == null)
						value[2] = "";

					String isWithdraw = value[2].trim();

					String isMigrating = value[1].trim();

					if (value[10] == null)
						value[10] = "";

					String type = value[10].trim();

					
					if (!isMigrating.equals("N") && !isWithdraw.contains("WD") && type.toUpperCase().contains("EXAM")) {
						if (value[3] == null)
							value[3] = "";
						String courseCode = value[3].trim();

						if (value[9] == null)
							value[9] = "";

						String title = value[9].trim();

						if (value[14] == null)
							value[14] = "";

						String filename = value[14].trim();

						if (value[32] == null)
							value[32] = "";

						String period = value[32].trim();

						String mmsid = "";

						// if (courseCode.contains("CHINA274C")) {

						Iterator it = AlmaMMSID.entrySet().iterator();
						while (it.hasNext()) {
							Map.Entry pair = (Map.Entry) it.next();

							if (pair.getKey().toString().toUpperCase().contains(courseCode)) {
								mmsid = pair.getValue().toString();
							}
						}

						String alSuffix = "";

						Pattern pattern = Pattern.compile("\\((.*?)\\)");
						Matcher matcher = pattern.matcher(title);
						if (matcher.find()) {
							alSuffix = matcher.group(1);
						}
						if (!alSuffix.equals("")) {
							alSuffix = "(" + alSuffix + ")";
						}

						String destFile = "";
						if (!filename.equals("")) {
							if (period.equals(""))
								destFile = filename;
							else if (!period.equals(""))
								if (type.equals("SPECEXAM"))
									destFile = period + "-" + courseCode + alSuffix.replaceAll(" ", "")
											+ "-Specimen.pdf";
								else if (type.equals("SUPEXAM"))
									destFile = period + "-" + courseCode + alSuffix.replaceAll(" ", "")
											+ "-Supplement.pdf";
								else
									destFile = period + "-" + courseCode + alSuffix.replaceAll(" ", "") + ".pdf";
						}

						String almaLabel = period;
						if (!alSuffix.equals(""))
							almaLabel = almaLabel + alSuffix;

						if (type.equals("SPECEXAM"))
							almaLabel += " - Specimen";
						if (type.equals("SUPEXAM"))
							almaLabel += " - Supplement";

						almaLabel = almaLabel.replaceAll("\\\\.*$", "");
						if (almaLabel.length() == 7)
							almaLabel = almaLabel.substring(0, 4) + "-" + almaLabel.substring(4, 7);

						String outline = "," + mmsid + ",,,8187250000000000,,,,,,,,,,,,,,,,,EP,,," + almaLabel
								+ ",,,Master,,,," + destFile + ",,,\n";

						writer.write(outline);
						System.out.println(j + ". " + outline);
						j++;

						if (filename != null && !filename.equals("")) {
							try {
								File source = new File("d:\\eReserveFiles\\" + filename);
								File dest = new File("d:\\eReserveFilesConv\\" + destFile);
								FileUtils.copyFile(source, dest);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
//						}
					}
				}
			}

			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
