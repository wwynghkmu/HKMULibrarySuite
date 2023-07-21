package hk.edu.hkmu.lib.bookquery;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.json.simple.*;
import org.json.simple.parser.JSONParser;

import hk.edu.hkmu.lib.BookItem;

/**
 * This class accepts ISBN and Volume (optional) and search over Primo VE API.
 *  The related configs can be done in Config.java of the same
 * package.
 * 
 * @author Wai-yan NG
 * @author wwyng@hkmu.edu.hk
 * @version 1.0
 * @since July 2023
 */
public class PrimoVEQueryByISBN extends PrimoVEQuery {

	public PrimoVEQueryByISBN() {
		super();
	} // end PrimoQueryByISBN()

	public PrimoVEQueryByISBN(String str) {
		super();
		queryBk = new BookItem(str);
		query();
	} // end PrimoQueryByISBN()

	public PrimoVEQueryByISBN(String str, String inst) {
		super(inst);
		queryBk = new BookItem(str);
		queryBk.setVolume("-1");
		query();
	} // end PrimoQueryByISBN()

	public PrimoVEQueryByISBN(String str, String inst, String vol) {
		super(inst);
		queryBk = new BookItem(str);
		queryBk.setVolume(vol);
		query();
	} // end PrimoQueryByISBN()

	public boolean query(String str) {
		clearQuery();
		queryBk.isbn.setIsbn(str);
		queryBk.setVolume("-1");
		return query();
	} // end if

	@Override
	protected boolean query() {
		if (queryBk.isbn.getOriginalIsbn().equals("N/A")) {
			return false;
		} // end iF
		queryStr = new String("&q=isbn,contains,");
		if (queryBk.isbn.getValidity()) {
			queryStr += queryBk.isbn.getIsbn13();
			if (remoteQuery(queryStr)) {
				checkAVA(queryBk.parseVolume());
				return true;
			} else {
				errMsg = "No record found on Primo." + Config.QUERY_SETTING;
			} // end if
		} else {
			errMsg = "Invalid ISBN: " + queryBk.isbn.getOriginalIsbn();
		} // end if
		return false;
	} // end query()

	protected boolean remoteQuery(String qstr) {
		try {

			System.out.println("API URL: " + Config.PRIMOVE_API_BASE + qstr);
			URL url = new URL(Config.PRIMOVE_API_BASE + qstr);
			URLConnection con = url.openConnection();
			con.setConnectTimeout(3000);
			BufferedReader br = null;
			StringBuilder body = null;

			String line = "";
			try {
				br = new BufferedReader(new InputStreamReader(con.getInputStream()));
				body = new StringBuilder();
				while ((line = br.readLine()) != null)
					body.append(line);
				Object obj = new JSONParser().parse(body.toString());
				JSONObject jo = (JSONObject) obj;
				JSONArray ja = (JSONArray) jo.get("docs");

				JSONObject pnxjo = new JSONObject();
				JSONObject deliveryjo = new JSONObject();

				// iterating phoneNumbers
				Iterator itr2 = ja.iterator();
				Iterator<Map.Entry> itr1;
				while (itr2.hasNext()) {
					itr1 = ((Map) itr2.next()).entrySet().iterator();
					while (itr1.hasNext()) {
						Map.Entry pair = itr1.next();
						if (pair.getKey().equals("pnx")) {
							pnxjo = (JSONObject) new JSONParser().parse(pair.getValue().toString());
						}
						if (pair.getKey().equals("delivery")) {
							deliveryjo = (JSONObject) new JSONParser().parse(pair.getValue().toString());
						}
					}
				}

				JSONObject controljo = (JSONObject) new JSONParser().parse(pnxjo.get("control").toString());
				JSONArray controlja = (JSONArray) controljo.get("recordid");
				String recordid = controlja.get(0).toString().trim();

				JSONObject displayjo = (JSONObject) new JSONParser().parse(pnxjo.get("display").toString());

				JSONArray titleja = (JSONArray) displayjo.get("title");
				bk.setTitle(titleja.get(0).toString().trim());

				JSONArray contributorja = (JSONArray) displayjo.get("contributor");
				String contributor = "";
				if (contributorja != null) {
					for (int i = 0; i < contributorja.size(); i++) {
						contributor += contributorja.get(i).toString().replaceAll("\\$\\$Q.*", "") + "; ";
					}
				}
				bk.setContributor(contributor);

				JSONArray holdingja = (JSONArray) deliveryjo.get("holding");
				if (holdingja != null) {
					for (int l = 0; l < holdingja.size(); l++) {
						ext_itm_no++;

						System.out.println("HOLDING::::" + holdingja.get(l));
						JSONObject holdingjo = (JSONObject) new JSONParser().parse(holdingja.get(l).toString());
						String ava = holdingjo.get("availabilityStatus").toString();
						System.out.println("HOLDING::::AVAAAA:::: " + ava);
						if (ava.equals("available"))
							ava_itm_no++;
					}
				}

				JSONArray creatorja = (JSONArray) displayjo.get("creator");
				String creator = "";
				if (creatorja != null) {
					for (int i = 0; i < creatorja.size(); i++) {
						creator += creatorja.get(i).toString().replaceAll("\\$\\$Q.*", "") + "; ";
					}
				}
				bk.setCreator(creator);

				JSONArray publisherja = (JSONArray) displayjo.get("publisher");
				bk.setPublisher(publisherja.get(0).toString().trim());

				bk.setIsbn(queryBk.isbn.getIsbn());

				JSONArray editionja = (JSONArray) displayjo.get("edition");
				bk.setEdition(editionja.get(0).toString().trim());

				JSONArray creationdateja = (JSONArray) displayjo.get("creationdate");
				bk.setPublishYear(creationdateja.get(0).toString().trim());
				bk.setPrimoLink("https://" + Config.VALUES.get("PRIMOVE_BASE")
						+ Config.VALUES.get("PRIMOVE_RECORD_STRING") + recordid);

			} catch (Exception e) {
				throw new RuntimeException(e);
			}

			match = true;
			return true;

		} // end try

		catch (Exception e) {
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			String errStr = "PrimoQueryByISBN:remoteQuery()" + errors.toString();
			System.out.println(errStr);
			errMsg = errStr;
		} // end catch

		return false;
	} // end remoteQuery()

	private int determineRecordIndex(NodeList nodes) {
		ArrayList<String> al = new ArrayList<String>();
		for (int i = 0; i < nodes.getLength(); i++) {
			al = getNodeValues("sourceid", nodes.item(i).getChildNodes());
			for (int j = 0; j < al.size(); j++) {
				if (al.get(j).matches(".*" + Config.VALUES.get("SOURCE_ID") + ".*")) {
					return i;
				} // end if
			} // end for
		} // end for
		return -1;
	} // end determineRecordIndex()
} // end class PrimoQueryByISBN