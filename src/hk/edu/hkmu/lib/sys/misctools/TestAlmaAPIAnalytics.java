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
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

//This class is to test Alma API using Post method. Post method is to create recrods in Alma.
//by William NG

public class TestAlmaAPIAnalytics {
	public static void main(String args[]) {
		try {

			String sandboxAPIKey = "apikey=l8xx41bf077192274811b545c60ba48df465";
			hk.edu.hkmu.lib.Config.init();
			String prodAPIKey = "apikey=" + hk.edu.hkmu.lib.Config.VALUES.get("ALMAAPIKEYALL");
			String almaAnalyticsAPIPath = hk.edu.hkmu.lib.Config.VALUES.get("ALMAANALYTICSAPIROOTPATH");
			String urlStr = almaAnalyticsAPIPath + "SYS/Arrived_Last_Month_ASS_P&limit=1000&col_names=true&"
					+ prodAPIKey;
			URL url = new URL(urlStr);

			System.out.println(urlStr);

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

			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			InputSource is = new InputSource(new StringReader(content.toString()));
			Document doc = builder.parse(is);
			NodeList nlist = doc.getElementsByTagName("Row");

			System.out.println("\n\n\n" + nlist.getLength());

			for (int i = 0; i < nlist.getLength(); i++) {
				if (nlist.item(i).getNodeType() == Node.ELEMENT_NODE) {
					Element el = (Element) nlist.item(i);

					String author = "";
					if (el.getElementsByTagName("Column1").item(0) != null) {
						author += el.getElementsByTagName("Column1").item(0).getTextContent();
					}
					if (el.getElementsByTagName("Column2").item(0) != null) {
						author += el.getElementsByTagName("Column2").item(0).getTextContent();
					}

					String title = "";
					if (el.getElementsByTagName("Column5").item(0) != null) {
						title = el.getElementsByTagName("Column5").item(0).getTextContent();
					}

					String callno = "";
					if (el.getElementsByTagName("Column7").item(0) != null) {
						callno = el.getElementsByTagName("Column7").item(0).getTextContent();
					}

					String mmsid = "";
					if (el.getElementsByTagName("Column3").item(0) != null) {
						mmsid = el.getElementsByTagName("Column3").item(0).getTextContent();
					}
					String publisher = "";
					if (el.getElementsByTagName("Column4").item(0) != null) {
						publisher = el.getElementsByTagName("Column4").item(0).getTextContent();
					}

					String rstype = "";
					if (el.getElementsByTagName("Column8").item(0) != null) {
						rstype = el.getElementsByTagName("Column8").item(0).getTextContent();
					}

					System.out.println(author + ", " + title);

				}
			}

			http.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
