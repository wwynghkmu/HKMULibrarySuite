package hk.edu.hkmu.lib.sys.misctools;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

//This class is to test Alma API using Post method. Post method is to create recrods in Alma.
//by William NG

public class TestAlmaAPIGet {
	public static void main(String args[]) {
		try {

			String sandboxAPIKey = "apikey=l8xx41bf077192274811b545c60ba48df465";
			hk.edu.hkmu.lib.Config.init();
			String prodAPIKey = "apikey=" + hk.edu.hkmu.lib.Config.VALUES.get("ALMAAPIKEYALL");

			URL url = new URL(
					// Course URL:
					// "https://api-ap.hosted.exlibrisgroup.com/almaws/v1/courses?apikey=l8xx41bf077192274811b545c60ba48df465");
					// "https://api-ap.hosted.exlibrisgroup.com/almaws/v1/acq/po-lines?" +
					// prodAPIKey);
					// Fund URL: "https://api-ap.hosted.exlibrisgroup.com/almaws/v1/acq/funds?" +
					// prodAPIKey);
					// BIB URL "https://api-ap.hosted.exlibrisgroup.com/almaws/v1/bibs?" +
					// prodAPIKey);
					"https://api-ap.hosted.exlibrisgroup.com/almaws/v1/analytics/reports?path=/shared/Hong%20Kong%20Metropolitan%20University%20852HKMU_INST/Reports/SYS/Arrived_Last_Month_ASS_P&limit=999&format=json&col_names=true&"
							+ prodAPIKey);

			System.out.println(
					"https://api-ap.hosted.exlibrisgroup.com/almaws/v1/analytics/reports?path=/shared/Hong%20Kong%20Metropolitan%20University%20852HKMU_INST/Reports/SYS/Arrived_Last_Month_ASS_P&limit=1000&format=json&col_names=true&"
							+ prodAPIKey);

			HttpURLConnection http = (HttpURLConnection) url.openConnection();

			BufferedReader br = null;
			if (100 <= http.getResponseCode() && http.getResponseCode() <= 399) {
				br = new BufferedReader(new InputStreamReader(http.getInputStream()));
			} else {
				br = new BufferedReader(new InputStreamReader(http.getErrorStream()));
			}

			String inputLine;
			StringBuffer content = new StringBuffer();
			while ((inputLine = br.readLine()) != null) {
				content.append(inputLine);
			}

			/*
			 * DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			 * DocumentBuilder builder = factory.newDocumentBuilder(); InputSource is = new
			 * InputSource(new StringReader(content.toString())); Document doc =
			 * builder.parse(is); NodeList nlist = doc.getElementsByTagName("course");
			 */
			//System.out.println(content);

			http.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
