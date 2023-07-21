package hk.edu.hkmu.lib.sys.misctools;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

//This class is to test Alma API using Delete method. Post method is to delete records in Alma.
//by William NG

public class TestAlmaAPIDelete {
	public static void main(String args[]) {
		try {

			String sandboxAPIKey = "apikey=l8xx41bf077192274811b545c60ba48df465";
			String prodAPIKey 	 = "apikey=l8xxeb2a38bf0cb840e59f4ecf7a8f1c01f0";
			
			URL url = new URL(
					// Course URL: "https://api-ap.hosted.exlibrisgroup.com/almaws/v1/courses?apikey=l8xx41bf077192274811b545c60ba48df465");
					// POL URL: "https://api-ap.hosted.exlibrisgroup.com/almaws/v1/acq/po-lines/POL-164?reason=LIBRARY_CANCELLED" + prodAPIKey);
					// Fund URL: "https://api-ap.hosted.exlibrisgroup.com/almaws/v1/acq/funds/830250760008061?" + prodAPIKey);
					"https://api-ap.hosted.exlibrisgroup.com/almaws/v1/bibs/998167280208061?" + prodAPIKey);
					
			HttpURLConnection http = (HttpURLConnection) url.openConnection();
			http.setRequestMethod("DELETE");
			http.setDoOutput(true);
			http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			
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
