package hk.edu.hkmu.lib.cir;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class Course {
	public ArrayList courseCodeArry = new ArrayList();
	public ArrayList courseNameArry = new ArrayList();
	public ArrayList statusArry = new ArrayList();
	public ArrayList departmentArry = new ArrayList();
	public int totalRecord = 0;
	public int index = 0;
	public int offset = 0;
	private String urlStr = "";

	public Course() {
		readCourses();
	}

	public Course(String urlStr) {
		this.urlStr = urlStr;
		readCourses();
	}

	public void readCourses() {
		String outstr = "";
		String urlStr = "https://api-ap.hosted.exlibrisgroup.com/almaws/v1/courses?apikey=l8xx41bf077192274811b545c60ba48df465&status=ACTIVE&order_by=code&limit=100&offset=0";
		if (this.urlStr.equals(""))
			this.urlStr = urlStr;
		readNextCourses();

	}

	public void readNextCourses() {
		int nextOffset = offset + 100;
		String outstr = "";
		urlStr = urlStr.replaceAll("offset=" + offset, "offset=" + nextOffset);
		offset += 100;
		try {
			URL url = new URL(urlStr);
			URLConnection urlcon = url.openConnection();

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
				System.out.print(courseCodeArry.get(i));

				nlist2 = doc.getElementsByTagName("name");
				String courseName = "";
				if (nlist2.item(i).getFirstChild() != null)
					courseName = nlist2.item(i).getFirstChild().getNodeValue();
				courseNameArry.add(i, courseName);
				System.out.print(courseName);

				nlist2 = doc.getElementsByTagName("status");
				String status = "";
				if (nlist2.item(i).getFirstChild() != null)
					status = nlist2.item(i).getFirstChild().getNodeValue();
				statusArry.add(i, status);
				System.out.print(status);

				nlist2 = doc.getElementsByTagName("academic_department");
				String department = "";
				if (nlist2.item(i).getFirstChild() != null)
					department = nlist2.item(i).getFirstChild().getNodeValue();
				departmentArry.add(i, department);
				System.out.println(department);
			}
			if (totalRecord % 100 == 0) {
				System.out.println("TOTL: " + totalRecord);
				System.out.println("Offset: " + offset);
				System.out.println(urlStr);
				readNextCourses();
			}
			

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
