package hk.edu.hkmu.lib.cir;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.commons.lang3.time.DateUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/*This class Course is used for reading course information from Alma's API. 
 * The class is used by the JSP script https://utils.lib.hkmu.edu.hk/alma/courseList.jsp for displaying course information which further be linked to Primo VE.  
 * Written by William NG (7 Mar 2023).
 */

public class Course {

	public HashMap<String, ArrayList> courses = new HashMap<String, ArrayList>();
	
	// For storing the courses: courseArray[i].get(0) is Course Code,
	// courseArray[i].get(1) is Course Name, courseArray[i].get(0) is Status,
	// courseArray[i].get(0) is School
	public ArrayList<ArrayList<?>> courseArray = new ArrayList();
	public int totalRecord = 0;
	public int index = 0;
	public int offset = -100;
	private String urlStr = "";
	String tempDir = "";
	URLConnection urlcon;
	
	
	//urlStr: the whole API (including API Key) URL for fetching courses in Alma.
	//e.g.: https://api-ap.hosted.exlibrisgroup.com/almaws/v1/courses?apikey=someAPIKey&status=ACTIVE&order_by=code&limit=100&offset=0
	//tempDir: the temporary directory to store the object courseArray as cache of the result from Alma
	public Course(String urlStr, String tempDir) {
		this.urlStr = urlStr;
		this.tempDir = tempDir;
		readCourses();
	}

	// After sort the course list and save it after fetch course information from
	// Alma
	void sortAndSaveCourses() {
		SortedSet<String> keys = new TreeSet<>(courses.keySet());
		for (String key : keys) {
			courseArray.add((ArrayList) courses.get(key));
		}

		try {
			FileOutputStream fileOut = new FileOutputStream(tempDir + "CoursesArray.ser");
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(courseArray);
			fileOut.close();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void readCourses() {

		try {
			File f = new File(tempDir + "CoursesArray.ser");
			// Check if course info already fetched from Alma today, if so, read the saved
			// object locally to save time to ask Alma again.
			if (f.exists()) {
				long lmodify = f.lastModified();
				Date lmdate = new Date(lmodify);
				DateUtils du = new DateUtils();

				Date today = new Date();
				if (du.isSameDay(lmdate, today)) {

					FileInputStream fileIn = new FileInputStream(tempDir + "CoursesArray.ser");
					ObjectInputStream in = new ObjectInputStream(fileIn);
					courseArray = (ArrayList) in.readObject();
					in.close();
					fileIn.close();
					totalRecord = courseArray.size();

				} else {
					readNextCourses();
					FileOutputStream fileOut = new FileOutputStream(tempDir + "CoursesArray.ser");
					ObjectOutputStream out = new ObjectOutputStream(fileOut);
					out.writeObject(courseArray);
					fileOut.close();
					out.close();

				}
			} else {
				readNextCourses();

				FileOutputStream fileOut = new FileOutputStream(tempDir + "CoursesArray.ser");
				ObjectOutputStream out = new ObjectOutputStream(fileOut);
				out.writeObject(courseArray);

				fileOut.close();
				out.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void readNextCourses() {
		int nextOffset = offset + 100;
		String outstr = "";
		urlStr = urlStr.replaceAll("offset=" + offset, "offset=" + nextOffset);
		offset += 100;
		try {
			URL url = new URL(urlStr);

			urlcon = url.openConnection();

			urlcon.setConnectTimeout(5000);
			BufferedReader buffread = new BufferedReader(new InputStreamReader(urlcon.getInputStream(), "UTF-8"));
			String inputLine;
			while ((inputLine = buffread.readLine()) != null)
				outstr += inputLine;
			buffread.close();

			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			InputSource is = new InputSource(new StringReader(outstr));
			Document doc = builder.parse(is);
			NodeList nlist = doc.getElementsByTagName("course");
			totalRecord += nlist.getLength();

			for (int i = 0; i < nlist.getLength(); i++) {
				NodeList nlist2 = doc.getElementsByTagName("code");
				String courseCode = "";
				if (nlist2.item(i).getFirstChild() != null)
					courseCode = nlist2.item(i).getFirstChild().getNodeValue();
				courseCode = courseCode.trim();

				if (courses.get(courseCode) == null)
					courses.put(courseCode, new ArrayList());
				courses.get(courseCode).add(courseCode);

				nlist2 = doc.getElementsByTagName("name");
				String courseName = "";
				if (nlist2.item(i).getFirstChild() != null)
					courseName = nlist2.item(i).getFirstChild().getNodeValue();

				courses.get(courseCode).add(courseName);

				nlist2 = doc.getElementsByTagName("status");
				String status = "";
				if (nlist2.item(i).getFirstChild() != null)
					status = nlist2.item(i).getFirstChild().getNodeValue();

				courses.get(courseCode).add(status);

				nlist2 = doc.getElementsByTagName("academic_department");
				String department = "";
				if (nlist2.item(i).getFirstChild() != null)
					department = nlist2.item(i).getFirstChild().getNodeValue();

				courses.get(courseCode).add(department);

			}
			if (totalRecord % 100 == 0 && !(offset > 5000)) {
				readNextCourses();
			} else {
				sortAndSaveCourses();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
