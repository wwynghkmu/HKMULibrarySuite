package hk.edu.hkmu.lib.sys;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class AlmaCreateReadingList {

	private String json;

	public AlmaCreateReadingList(String json) {
		this.json = json;
	}

	public String CreateReadingList() {
		String respondCode = "";
		String respondBody = "";
		try {

			URL url = new URL(
					"https://api-ap.hosted.exlibrisgroup.com/almaws/v1/courses/PHI4360/reading-lists?apikey=l8xx41bf077192274811b545c60ba48df465");
			HttpURLConnection http = (HttpURLConnection) url.openConnection();
			http.setRequestMethod("POST");
			http.setDoOutput(true);
			http.setRequestProperty("Content-Type", "application/json");

			String data = "{\"link\":\"\",\"code\":\"AD142.01-RL-1\",\"name\":\"IntroductiontoOrganizationalBehaviorReadingLi\",\"due_back_date\":\"2020-04-01Z\",\"status\":{\"value\":\"Complete\"},\"visibility\":{\"value\":\"PUBLIC\"},\"publishingStatus\":{\"value\":\"DRAFT\"}}";

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
