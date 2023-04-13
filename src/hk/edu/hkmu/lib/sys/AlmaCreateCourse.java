package hk.edu.hkmu.lib.sys;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

/*by William NG (HKMU LIB TSDI)
 * This class use Alma API to create Alma Courses.
 */
public class AlmaCreateCourse {

	public String CreateCourse(String code, String name, String instructor, String school, String sd, String ed,
			String term, String year, String session) {
		String respondCode = "";
		String respondBody = "";
		try {

			URL url = new URL(
					"https://api-ap.hosted.exlibrisgroup.com/almaws/v1/courses?apikey=l8xxeb2a38bf0cb840e59f4ecf7a8f1c01f0");

			// Alma SandBox API:
			// "https://api-ap.hosted.exlibrisgroup.com/almaws/v1/courses?apikey=l8xx41bf077192274811b545c60ba48df465"
			// Alma Production API:
			// "https://api-ap.hosted.exlibrisgroup.com/almaws/v1/courses?apikey=l8xxeb2a38bf0cb840e59f4ecf7a8f1c01f0"

			HttpURLConnection http = (HttpURLConnection) url.openConnection();
			http.setRequestMethod("POST");
			http.setDoOutput(true);
			http.setRequestProperty("Content-Type", "application/json");

			// String data =
			// "{\"link\":\"\",\"code\":\"PHI4361\",\"name\":\"Wittgenstein'sPhilosophy
			// 2\",\"section\":\"01\",\"academic_department\":{\"value\":\"A&SS\"},\"processing_department\":{\"value\":\"C_DEP\"},\"term\":[{\"value\":\"AUTUMN\"}],\"status\":\"INACTIVE\",\"start_date\":\"2011-09-10Z\",\"end_date\":\"2013-12-31Z\",\"weekly_hours\":\"0\",\"participants\":\"35\",\"year\":\"2007\",\"instructor\":[{\"primary_id\":\"1234\"}],\"campus\":[{\"campus_code\":{\"value\":\"code\"},\"campus_participants\":\"30\"}],\"submit_by_date\":\"2013-12-01Z\"}";

			if (!sd.equals("0")) {
				if (!sd.equals(""))
					sd = sd.substring(0, 4) + "-" + sd.substring(4, 6) + "-" + sd.substring(6, 8);

				if (!ed.equals(""))
					ed = ed.substring(0, 4) + "-" + ed.substring(4, 6) + "-" + ed.substring(6, 8);
			} else {
				sd = "1996-01-01";
				ed = "2099-12-31";
			}

			//using default start date and end date of course if no dates are given.
			if (sd.equals("0"))
				sd = "2022-09-01";

			if (ed.equals("0"))
				ed = "2023-07-13";
			
			//parameter "term" accepts course terms in form of [term1],[term2],... the following split the term string into a JSON strings readable by Alma.
			term = term.trim();
			String terms[] = term.split("\\$\\$\\d");

			if (year.length() > 4) {
				year = year.substring(0, 4);
			}

			term = "";
			for (int j = 1; j < terms.length; j++) {
				switch (terms[j]) {
				case "April":
					terms[j] = "TERM1";
					break;
				case "September":
					terms[j] = "TERM2";
					break;
				case "October":
					terms[j] = "TERM3";
					break;
				default:
					terms[j] = terms[j].toUpperCase();
					break;
				}
			}

			System.out.println("CODE:" + code);
			System.out.println("INST:" + instructor);
			String data = "{\"link\":\"\",\"code\":\"" + code + "\",\"name\":\"" + name + "\",\"section\":\"" + session
					+ "\",\"academic_department\":{\"value\":\"" + school
					+ "\"},\"processing_department\":{\"value\":\"CIR-CR\"},\"term\":[";

			if (terms.length > 1) {
				for (int k = 1; k < terms.length; k++) {
					term += "{\"value\":\"" + terms[k] + "\"}";
					if (k != terms.length - 1)
						term += ", ";
				}
			} else {
				term = "{\"value\":\"" + terms[0] + "\"}";
			}

			data += term;

			data += "],\"status\":\"ACTIVE\",\"start_date\":\"" + sd + "\",\"end_date\":\"" + ed
					+ "\",\"weekly_hours\":\"0\",\"participants\":\"0\",\"year\":\"" + year
					+ "\",\"instructor\":[{\"primary_id\":\"" + instructor
					+ "\"}],\"campus\":[{\"campus_code\":{\"value\":\"code\"},\"campus_participants\":\"0\"}],\"submit_by_date\":\""
					+ sd + "\"}";

			System.out.println(data);
			
			byte[] out = data.getBytes(StandardCharsets.UTF_8);

			OutputStream stream = http.getOutputStream();
			stream.write(out);

			BufferedReader br = null;
			if (100 <= http.getResponseCode() && http.getResponseCode() <= 399) {
				br = new BufferedReader(new InputStreamReader(http.getInputStream()));
			} else {
				br = new BufferedReader(new InputStreamReader(http.getErrorStream()));
			}
			respondCode = http.getResponseCode() + " " + http.getResponseMessage();
			//System.out.println(respondCode);
			respondBody = br.readLine();
			//System.out.println(respondBody);
			http.disconnect();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return respondCode;
	}

}
