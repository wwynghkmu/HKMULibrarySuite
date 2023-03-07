import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class TestAlmaAPI {
	public static void main(String args[]) {
		try {

			URL url = new URL(
					"https://api-ap.hosted.exlibrisgroup.com/almaws/v1/courses?apikey=l8xx41bf077192274811b545c60ba48df465");
			HttpURLConnection http = (HttpURLConnection) url.openConnection();
			http.setRequestMethod("POST");
			http.setDoOutput(true);
			http.setRequestProperty("Content-Type", "application/json");

			String data = "{\"link\":\"\",\"code\":\"C_CODE\",\"name\":\"C_NAME\",\"section\":\"01\",\"academic_department\":{\"value\":\"C_DEPT\"},\"processing_department\":{\"value\":\"PROSS_DEPT\"},\"term\":[{\"value\":\"C_TERM\"}],\"status\":\"C_STATUS\",\"start_date\":\"C_START\",\"end_date\":\"C_END\",\"weekly_hours\":\"0\",\"participants\":\"0\",\"year\":\"C_YEAR\",\"instructor\":[{\"primary_id\":\"C_INSTRUCTOR\"}],\"campus\":[{\"campus_code\":{\"value\":\"code\"},\"campus_participants\":\"0\"}],\"submit_by_date\":\"C_SDATE\"}";

			byte[] out = data.getBytes(StandardCharsets.UTF_8);

			OutputStream stream = http.getOutputStream();
			stream.write(out);

			BufferedReader br = null;
			if (100 <= http.getResponseCode() && http.getResponseCode() <= 399) {
				br = new BufferedReader(new InputStreamReader(http.getInputStream()));
			} else {
				br = new BufferedReader(new InputStreamReader(http.getErrorStream()));
			}

			System.out.println(http.getResponseCode() + " " + http.getResponseMessage());
			System.out.println(br.readLine());

			http.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
