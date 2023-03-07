package hk.edu.hkmu.lib.sys;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class AlmaCreateCourse {

	private String json;

	public AlmaCreateCourse() {

	}

	public AlmaCreateCourse(String json) {
		this.json = json;
	}

	public String CreateCourse(String code, String name, String instructor, String school, String sd, String ed,
			String term) {
		String respondCode = "";
		String respondBody = "";
		try {

			URL url = new URL(
					"https://api-ap.hosted.exlibrisgroup.com/almaws/v1/courses?apikey=l8xx41bf077192274811b545c60ba48df465");
			HttpURLConnection http = (HttpURLConnection) url.openConnection();
			http.setRequestMethod("POST");
			http.setDoOutput(true);
			http.setRequestProperty("Content-Type", "application/json");

			// String data =
			// "{\"link\":\"\",\"code\":\"PHI4361\",\"name\":\"Wittgenstein'sPhilosophy
			// 2\",\"section\":\"01\",\"academic_department\":{\"value\":\"A&SS\"},\"processing_department\":{\"value\":\"C_DEP\"},\"term\":[{\"value\":\"AUTUMN\"}],\"status\":\"INACTIVE\",\"start_date\":\"2011-09-10Z\",\"end_date\":\"2013-12-31Z\",\"weekly_hours\":\"0\",\"participants\":\"35\",\"year\":\"2007\",\"instructor\":[{\"primary_id\":\"1234\"}],\"campus\":[{\"campus_code\":{\"value\":\"code\"},\"campus_participants\":\"30\"}],\"submit_by_date\":\"2013-12-01Z\"}";
			
			if(sd.equals(""))
				sd = "2022-09-01";
			
			if(ed.equals(""))
				ed = "2023-07-14";
			
			
			String data = "{\"link\":\"\",\"code\":\"" + code + "\",\"name\":\"" + name
					+ "\",\"section\":\"01\",\"academic_department\":{\"value\":\"" + school
					+ "\"},\"processing_department\":{\"value\":\"CIR-CR\"},\"term\":[{\"value\":\"" + term
					+ "\"}],\"status\":\"ACTIVE\",\"start_date\":\"" + sd + "\",\"end_date\":\"" + ed
					+ "\",\"weekly_hours\":\"0\",\"participants\":\"0\",\"year\":\"2022\",\"instructor\":[{\"primary_id\":\""
					+ instructor + "\"}],\"campus\":[{\"campus_code\":{\"value\":\"code\"},\"campus_participants\":\"0\"}],\"submit_by_date\":\"" + sd + "\"}";
			
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
			System.out.println(respondCode);
			respondBody = br.readLine();
			System.out.println(respondBody);
			http.disconnect();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return respondCode;
	}

}
