import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.*;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.stream.Collectors;

public class TestJava2 {
	public static void main(String args[]) {

		try {
			Scanner scanner = new Scanner(new File("d:\\excludecourselist.txt"));

			while (scanner.hasNextLine()) {
				System.out.println(scanner.nextLine());
			}

			scanner.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		try {
			URL url = new URL(
					"https://api-ap.hosted.exlibrisgroup.com/almaws/v1/courses?apikey=l8xx41bf077192274811b545c60ba48df465");
			HttpURLConnection http = (HttpURLConnection) url.openConnection();
			http.setRequestMethod("POST");
			http.setDoOutput(true);
			http.setRequestProperty("Content-Type", "application/json");

			String data = "{\"link\":\"\",\"code\":\"PHI4360\",\"name\":\"Wittgenstein'sPhilosophy\",\"section\":\"01\",\"academic_department\":{\"value\":\"A&SS\"},\"processing_department\":{\"value\":\"C_DEP\"},\"term\":[{\"value\":\"AUTUMN\"}],\"status\":\"INACTIVE\",\"start_date\":\"2011-09-10Z\",\"end_date\":\"2013-12-31Z\",\"weekly_hours\":\"0\",\"participants\":\"35\",\"year\":\"2007\",\"instructor\":[{\"primary_id\":\"1234\"}],\"campus\":[{\"campus_code\":{\"value\":\"code\"},\"campus_participants\":\"30\"}],\"submit_by_date\":\"2013-12-01Z\"}";

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
			System.out.println(br.lines().collect(Collectors.joining()));
			http.disconnect();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
