package hk.edu.hkmu.lib.sys.misctools;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

//This class is to test Alma API using Post method. Post method is to create recrods in Alma.
//by William NG

public class TestAlmaAPIPostXML {
	public static void main(String args[]) {
		try {
			
			String sandboxAPIKey = "apikey=l8xx41bf077192274811b545c60ba48df465";
			String prodAPIKey 	 = "apikey=l8xxeb2a38bf0cb840e59f4ecf7a8f1c01f0";
			

			URL url = new URL(
					//Create Bib URL: "https://api-ap.hosted.exlibrisgroup.com/almaws/v1/bibs?validate=false&override_warning=true&check_match=false&" + prodAPIKey);
					"https://api-ap.hosted.exlibrisgroup.com/almaws/v1/bibs/990002108300108061/holdings?" + prodAPIKey);
			
			HttpURLConnection http = (HttpURLConnection) url.openConnection();
			http.setRequestMethod("POST");
			http.setDoOutput(true);
			http.setRequestProperty("Content-Type", "application/xml");
			

			// BIB: String data = "<bib> <record> <leader>00260nam a2200109 u 4500</leader> <controlfield tag='001'>991122700000121</controlfield> <controlfield tag='005'>20140120122820.0</controlfield> <controlfield tag='008'>131105s2013 xx r 000 0 gsw d</controlfield> <datafield ind1='1' ind2=' ' tag='100'> <subfield code='a'>Smith, John</subfield> </datafield> <datafield ind1='1' ind2='0' tag='245'> <subfield code='a'>Book of books Wittgenstein</subfield> </datafield> </record></bib>";
			String data = "<holding> <suppress_from_publishing>false</suppress_from_publishing> <record> <leader>nx a22 1i 4500</leader> <controlfield tag='008'>1011252p 8 1001aueng0000000</controlfield> <datafield ind1='0' ind2=' ' tag='852'> <subfield code='b'>HMT</subfield> <subfield code='t'>1</subfield> <subfield code='c'>GEN</subfield> <subfield code='h'>AC123</subfield> </datafield> </record> </holding>";
			
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
