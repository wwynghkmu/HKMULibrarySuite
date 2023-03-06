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

public class CourseTest {
	HashMap<String, ArrayList> CourseHashMap = new HashMap<String, ArrayList>();
	public ArrayList courseCodeArry = new ArrayList();
	public ArrayList courseNameArry = new ArrayList();
	public ArrayList statusArry = new ArrayList();
	public ArrayList departmentArry = new ArrayList();
	public int totalRecord = 0;
	public int index = 0;
	public int offset = 0;
	private String urlStr = "";
	String tempDir = "";
	URLConnection urlcon;

	public CourseTest(String tempDir) {
		this.tempDir = tempDir;
		readCourses();
	}

	public CourseTest(String urlStr, String tempDir) {
		this.urlStr = urlStr;
		this.tempDir = tempDir;
		readCourses();
	}

	public void readCourses() {
		String outstr = "";
		String urlStr = "https://api-ap.hosted.exlibrisgroup.com/almaws/v1/courses?apikey=l8xx41bf077192274811b545c60ba48df465&status=ACTIVE&order_by=code&limit=100&offset=0";
		if (this.urlStr.equals(""))
			this.urlStr = urlStr;
		try {
			File f = new File(tempDir + "Courses.ser");
			if (f.exists()) {

				long lmodify = f.lastModified();
				Date lmdate = new Date(lmodify);
				DateUtils du = new DateUtils();

				Date today = new Date();
				if (du.isSameDay(lmdate, today)) {
					FileInputStream fileIn = new FileInputStream(tempDir + "Courses.ser");
					ObjectInputStream in = new ObjectInputStream(fileIn);
					CourseHashMap = (HashMap) in.readObject();
					in.close();
					fileIn.close();
					courseCodeArry = CourseHashMap.get("courseCodeArry");
					courseNameArry = CourseHashMap.get("courseNameArry");
					statusArry = CourseHashMap.get("statusArry");
					departmentArry = CourseHashMap.get("departmentArry");
				} else {
					readNextCourses();
					CourseHashMap.put("courseCodeArry", courseCodeArry);
					CourseHashMap.put("courseNameArry", courseNameArry);
					CourseHashMap.put("statusArry", statusArry);
					CourseHashMap.put("departmentArry", departmentArry);
					FileOutputStream fileOut = new FileOutputStream(tempDir + "Courses.ser");
					ObjectOutputStream out = new ObjectOutputStream(fileOut);
					out.writeObject(CourseHashMap);

				}

			} else {
				readNextCourses();
				CourseHashMap.put("courseCodeArry", courseCodeArry);
				CourseHashMap.put("courseNameArry", courseNameArry);
				CourseHashMap.put("statusArry", statusArry);
				CourseHashMap.put("departmentArry", departmentArry);
				FileOutputStream fileOut = new FileOutputStream(tempDir + "Courses.ser");
				ObjectOutputStream out = new ObjectOutputStream(fileOut);
				out.writeObject(CourseHashMap);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("TOTL: " + totalRecord);

	}

	public void readNextCourses() {
		int nextOffset = offset + 100;
		String outstr = "";
		urlStr = urlStr.replaceAll("offset=" + offset, "offset=" + nextOffset);
		offset += 100;
		try {
			URL url = new URL(urlStr);
			System.out.println(urlStr);
			urlcon = url.openConnection();

			urlcon.setConnectTimeout(3000);
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
				courseCodeArry.add(i, courseCode);

				nlist2 = doc.getElementsByTagName("name");
				String courseName = "";
				if (nlist2.item(i).getFirstChild() != null)
					courseName = nlist2.item(i).getFirstChild().getNodeValue();
				courseNameArry.add(i, courseName);

				nlist2 = doc.getElementsByTagName("status");
				String status = "";
				if (nlist2.item(i).getFirstChild() != null)
					status = nlist2.item(i).getFirstChild().getNodeValue();
				statusArry.add(i, status);

				nlist2 = doc.getElementsByTagName("academic_department");
				String department = "";
				if (nlist2.item(i).getFirstChild() != null)
					department = nlist2.item(i).getFirstChild().getNodeValue();
				departmentArry.add(i, department);

			}
			if (totalRecord % 100 == 0) {
				readNextCourses();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
