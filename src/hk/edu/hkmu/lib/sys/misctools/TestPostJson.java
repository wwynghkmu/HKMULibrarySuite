package hk.edu.hkmu.lib.sys.misctools;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

//This class is to test Alma API using Post method. Post method is to create recrods in Alma.
//by William NG

public class TestPostJson {
	public static void main(String args[]) {
		try {

			URL url = new URL(
					"https://dauatup.lib.hkmu.edu.hk/WebVD/Message.aspx?error=0&msgcode=100&url=https%3a%2f%2fdauat.lib.hkmu.edu.hk%2fWebVD%2findex_eng.aspx%3fLANG%3d0fp93p%20onmouseover%3ddocument.location%3d1%20style%3dposition%3aabsolute%3bwidth%3a100%25%3bheight%3a100%25%3btop%3a0%3bleft%3a0%3b%20esiyg&urlmsgcode=101&target=_self");

			HttpURLConnection http = (HttpURLConnection) url.openConnection();
			http.setRequestMethod("POST");
			http.setDoOutput(true);
			http.setRequestProperty("Content-Type", "application/json");

			String data = "__EVENTTARGET=docTypeList&__EVENTARGUMENT=&__LASTFOCUS=&__VIEWSTATE=cXqgLF8JRoBw57BSXF26Ez32no1D%2FKbnmRYUBfQ9y0vFBEvwa03Oylwh93WOOcP8nFD0%2BCzBBKn83Y9f%2FQs7fG4dZlacgGdifH3h99T0ovtwQcAEesDeo9iuVs0JR%";

			byte[] out = data.getBytes(StandardCharsets.UTF_8);

			OutputStream stream = http.getOutputStream();
			stream.write(out);

			BufferedReader br = null;
			if (100 <= http.getResponseCode() && http.getResponseCode() <= 399) {
				br = new BufferedReader(new InputStreamReader(http.getInputStream()));
			} else {
				br = new BufferedReader(new InputStreamReader(http.getErrorStream()));
			}

			System.out.print(http.getURL());
			System.out.println(http.getURL().getQuery());
			
			System.out.println(http.getResponseCode() + " " + http.getResponseMessage());
			String s = null;
			while ((s = br.readLine()) != null) {
				System.out.println(s);
			}

			http.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
