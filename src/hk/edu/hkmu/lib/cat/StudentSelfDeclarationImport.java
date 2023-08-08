package hk.edu.hkmu.lib.cat;

import hk.edu.hkmu.lib.*;
import hk.edu.hkmu.lib.acq.Config;
import hk.edu.hkmu.lib.bookquery.*;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * 
 * Accepts an Excel file containing Aleph BIB no. and ISBN and remote query via
 * Z39.50 from the provides defined in config.txt.
 * 
 * @author Wai-yan NG
 * @author wwyng@ouhk.edu.hk
 * @version 1.0
 * @since Jan 29, 2019
 */
public class StudentSelfDeclarationImport {

	protected StringHandling strHandle;
	protected CJKStringHandling cjkStrHandle;
	protected ArrayList<String> StudentIDArrayList = null;
	protected ArrayList<String> TitleArrayList = null;
	protected ArrayList<String> AcademicYearArrayList = null;
	protected ArrayList<String> StudentNameArrayList = null;
	protected ArrayList<String> SchoolArrayList = null;
	protected ArrayList<String> StudentEmailArrayList = null;
	private LogWriter logwriter;

	protected String summaryTxt;
	public boolean success = false;

	/*
	 * Constructor for recreating object with null properties.
	 */
	public StudentSelfDeclarationImport() {
		clear();
	} // end CopyCat

	/*
	 * Constructor for getting the MARC records from the providers.
	 * 
	 * @param file The Java File object of the source Excel file for query.
	 * 
	 * @param writePath The write path the reports stored which must has writable
	 * premission.
	 */
	public StudentSelfDeclarationImport(File file, String writePath) {
		clear();
		try {
			logwriter = new LogWriter(hk.edu.hkmu.lib.Config.SERVER_LOCAL_ROOT + "/cat/logs/");
			logwriter.setLogFile("importStudentSelfDeclaration.txt");
			FileInputStream is = new FileInputStream(file);
			readSource(is);
			printStudentList();
			success = importSrouceIntoDB();
		} // end try
		catch (Exception e) {
			System.out.println("Student Declearation Form:" + e);
			e.printStackTrace();
		} // end catch
	} // end CopyCat

	/*
	 * Constructor for getting the MARC records from the providers.
	 * 
	 * @param is The Java FileInputStream object of the source Excel file for query.
	 * 
	 * @param writePath The write path the reports stored which must has writable
	 * premission.
	 */
	public StudentSelfDeclarationImport(FileInputStream is, String writePath) {
		clear();
		try {
			logwriter = new LogWriter(hk.edu.hkmu.lib.Config.SERVER_LOCAL_ROOT + "/cat/logs/");
			logwriter.setLogFile("importStudentSelfDeclaration.txt");
			readSource(is);
			importSrouceIntoDB();
		} // end try
		catch (Exception e) {
			System.out.println("Student Self-declaration Import:");
			e.printStackTrace();
		} // catch
	} // end CopyCat()

	public void clear() {
		Config.init();
		summaryTxt = "";
		strHandle = new StringHandling();
		cjkStrHandle = new CJKStringHandling();
	} // end clear()

	/*
	 * Read the query file containing information of student declaration from the
	 * provided Excel
	 * 
	 * param is The Java steam of the query file in Excel.
	 */
	public void readSource(FileInputStream is) {
		try {

			if (StudentIDArrayList == null) {
				StudentIDArrayList = new ArrayList<String>();
				StudentEmailArrayList = new ArrayList<String>();
				AcademicYearArrayList = new ArrayList<String>();
				StudentNameArrayList = new ArrayList<String>();
				TitleArrayList = new ArrayList<String>();
				SchoolArrayList = new ArrayList<String>();
				XSSFWorkbook wb = new XSSFWorkbook(is);
				XSSFSheet sheet = wb.getSheetAt(0);
				Iterator<org.apache.poi.ss.usermodel.Row> rowIterator = sheet.iterator();
				rowIterator.next();

				while (rowIterator.hasNext()) {
					org.apache.poi.ss.usermodel.Row row = rowIterator.next();
					// For each row, iterate through all the columns
					Iterator<Cell> cellIterator = row.cellIterator();

					while (cellIterator.hasNext()) {

						Cell cell = cellIterator.next();
						// Check the cell type and format accordingly

						if (cell.getColumnIndex() == 2) {
							String tempStr = "";
							switch (cell.getCellType()) {
							case NUMERIC:
								Double d = cell.getNumericCellValue();
								tempStr = d.longValue() + "";
								break;
							case STRING:
								tempStr = cell.getStringCellValue();
								break;
							} // switch
							
							StudentIDArrayList.add(tempStr.trim());

						} // end if

						if (cell.getColumnIndex() == 0) {

							String tempStr = "";
							switch (cell.getCellType()) {
							case NUMERIC:
								Double d = cell.getNumericCellValue();
								tempStr = d.longValue() + "";
								break;
							case STRING:
								tempStr = cell.getStringCellValue();
								break;
							} // switch

							AcademicYearArrayList.add(tempStr);

						} // end if

						if (cell.getColumnIndex() == 3) {
							String tempStr = "";
							switch (cell.getCellType()) {
							case NUMERIC:
								Double d = cell.getNumericCellValue();
								tempStr = d.longValue() + "";
								break;
							case STRING:
								tempStr = cell.getStringCellValue();
								break;
							} // switch
							tempStr = tempStr.replaceAll("\n", " ");
							TitleArrayList.add(tempStr);
						} // end if

						if (cell.getColumnIndex() == 1) {
							String tempStr = "";
							switch (cell.getCellType()) {
							case NUMERIC:
								Double d = cell.getNumericCellValue();
								tempStr = d.longValue() + "";
								break;
							case STRING:
								tempStr = cell.getStringCellValue();
								break;
							} // switch
							StudentNameArrayList.add(tempStr);
						} // end if

						if (cell.getColumnIndex() == 4) {
							String tempStr = "";
							switch (cell.getCellType()) {
							case NUMERIC:
								Double d = cell.getNumericCellValue();
								tempStr = d.longValue() + "";
								break;
							case STRING:
								tempStr = cell.getStringCellValue();
								break;
							} // switch
							SchoolArrayList.add(tempStr);
						} // end if

						if (cell.getColumnIndex() == 5) {
							String tempStr = "";
							switch (cell.getCellType()) {
							case NUMERIC:
								Double d = cell.getNumericCellValue();
								tempStr = d.longValue() + "";
								break;
							case STRING:
								tempStr = cell.getStringCellValue();
								break;
							} // switch
							StudentEmailArrayList.add(tempStr);
						} // end if

					} // end while
				} // end whiles
				wb.close();
			} // end if
		} // end try

		catch (Exception e) {
			System.out.println("CopyCat:readISBNandTitleSource():");
			e.printStackTrace();
		} // end catch
	} // end loadIsbnList()

	/*
	 * Get the ISBNs loaded from the source Excel file for query.
	 */
	public void printStudentList() {

		for (int i = 0; i < StudentIDArrayList.size(); i++) {
			String s = AcademicYearArrayList.get(i);
			System.out.print(i + ". " + s + " ");

			s = StudentNameArrayList.get(i);
			System.out.print(s + " ");

			s = StudentIDArrayList.get(i);
			System.out.print(s + " ");

			s = TitleArrayList.get(i);
			System.out.print(s + " ");

			s = SchoolArrayList.get(i);
			System.out.print(s + " ");

			s = StudentEmailArrayList.get(i);
			System.out.print(s + " ");

			System.out.println(" ");
		}
	} // end printIsbnList()

	public boolean importSrouceIntoDB() {
		Connection conn = null;
		Statement stmt = null;

		try {
			Class.forName(Config.JDBC_DRIVER);
			conn = DriverManager.getConnection(Config.DB_URL, Config.USER, Config.PASS);
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {

			conn.setAutoCommit(false);
			String sql = "insert into catStudentSelfDeclaration (academicYear, studentName, studentEmail, studentID, title, school)"
					+ " values (?, ?, ?, ?, ?, ?)";
			PreparedStatement preparedStmt = conn.prepareStatement(sql);

			for (int i = 0; i < StudentIDArrayList.size(); i++) {
				String s = AcademicYearArrayList.get(i);
				preparedStmt.setString(1, s);

				s = StudentNameArrayList.get(i);
				preparedStmt.setString(2, s);

				s = StudentEmailArrayList.get(i);
				preparedStmt.setString(3, s);

				s = StudentIDArrayList.get(i);
				preparedStmt.setString(4, s);

				s = TitleArrayList.get(i);
				preparedStmt.setString(5, s);

				s = SchoolArrayList.get(i);
				preparedStmt.setString(6, s);

				preparedStmt.execute();
			}
			conn.commit();
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
			logwriter.out(e.toString());
			try {
				conn.rollback();
			} catch (Exception e2) {
			}
			return false;
		}

		return true;
	}

}
// end class CopyCat