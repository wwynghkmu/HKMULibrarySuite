package hk.edu.hkmu.lib.bookquery;

import java.util.*;
import java.util.regex.*;

import javax.xml.parsers.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.w3c.dom.*;

import hk.edu.hkmu.lib.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * * This class accepts parameters Author, Title, Publisher, Publishing Year,
 * Edition, Volume, and Institute. Author and Title are mandatory. For
 * Publisher, Publishing Year, or Edition, at least one must be filled. Volume
 * and Institute are optional. "-1" is the default code for note specifying
 * edition/volume information. The "latest" edition is coded '0', no remote
 * query will occur and will result false. Primo VE APIs are consulted.
 * "config.txt" controls network paths for query
 * 
 * @author Wai-yan NG
 * @author wwyng@hkmu.edu.hk
 * @version 1.0
 * @since July, 2023
 */
public class PrimoVEQueryByNonISBN extends PrimoQuery {

	private boolean queryStrProccessed = false;

	public PrimoVEQueryByNonISBN() {
		super();
	} // end PrimoQueryByISBN()

	/**
	 * 
	 * Constructor with institute code. Default global variables, which originally
	 * are defined in "config.txt", are changed accordingly. Default global
	 * variables include: INST_CODE, SOURCE_ID, and LOCAL_SCOPE.
	 * 
	 * @param author    Author string
	 * @param title     Title string
	 * @param publisher Publisher string
	 * @param year      Publishing year
	 * @param edition   The edition
	 * @param vol       The volume of the book
	 * @param inst      The institute
	 */
	public PrimoVEQueryByNonISBN(String author, String title, String publisher, String year, String edition, String vol,
			String inst) {
		super(inst);
		queryBk = new BookItem();
		queryBk.setPublishYear(year);
		queryBk.setVolume(vol);
		queryBk.setEdition(edition);
		queryBk.setTitle(title);
		queryBk.setCreator(author);
		queryBk.setPublisher(publisher);
		query();
	} // end PrimoQueryByISBN()

	// Constructor without institute code.
	// Using the default global variables defined in "config.txt".
	// Default gloal variables include: INST_CODE, SOURCE_ID, and LOCAL_SCOPE.
	public PrimoVEQueryByNonISBN(String author, String title, String publisher, String year, String edition,
			String vol) {
		super();
		queryBk.setPublishYear(year);
		queryBk.setVolume(vol);
		queryBk.setEdition(edition);
		queryBk.setTitle(title);
		queryBk.setCreator(author);
		queryBk.setPublisher(publisher);
		queryBk.getPublisher();
		query();
	} // end PrimoQueryByISBN()

	// Making multiple queries until reaches a match result.
	protected boolean query() {
		queryStr = "";
		queryStr = new String(processQueryTitle(queryBk.getTitle()));
		queryStrProccessed = true;
		queryStr += processQueryAuthor(queryBk.getCreator()) + processQueryPublisher(queryBk.getPublisher());
		System.out.println("query(): query string" + queryStr);
		queryStrProccessed = false;
		// if edition is 'lastest' (coded '0'), no query is performed; and
		// return false.
		if (queryBk.parseEdition() == 0) {
			return false;
		} // end if

		/*
		 * The following section adds edition info to the query string if edition no. is
		 * greater than one. By cataloging practice, for the first edition, probably
		 * there is NO input on the associated MARC field. Considering this, edition
		 * query string to Primo is NOT added if querying for the first edition or no
		 * edition is specified.
		 */
		if (queryBk.parseEdition() > 1) {
			queryStr += processQueryEdition(queryBk.parseEdition());
		} // end if

		/*
		 * Querying the Primo X-service; and invoking the matching processes (all done
		 * by remoteQuery()).
		 */
		if (strHandle.hasSomething(queryBk.getPublisher())) {
			if (remoteQuery(queryStr)) {
				match = true;
				// setBookInfo();
				// checkAVA(queryBk.parseVolume());
				return true;
			} else {
				match = false;
			} // end if
		} // end if

		/*
		 * For various reasons, there are possibilities that the first query fails while
		 * a match should be found. The follow work as remedy queries to ensure the
		 * accuracy.
		 */

		if (!match && strHandle.hasSomething(queryBk.getPublisher())) {
			queryStr = "";
			queryStr = new String(processQueryPublisher(queryBk.getPublisher()));
			queryStrProccessed = true;
			queryStr += processQueryAuthor(queryBk.getCreator());
			queryStrProccessed = false;
			if (remoteQuery(queryStr)) {
				match = true;
				// setBookInfo();
				// checkAVA(queryBk.parseVolume());
				return true;
			} else {
				match = false;
			} // end if
		} // end if

		if (!match) {
			queryStr = "";
			queryStr = new String(processQueryTitle(queryBk.getTitle()));
			queryStrProccessed = true;
			queryStr += processQueryAuthor(queryBk.getCreator());
			queryStrProccessed = false;
			if (remoteQuery(queryStr)) {
				match = true;
				// setBookInfo();
				// checkAVA(queryBk.parseVolume());
				return true;
			} else {
				match = false;
			} // end if
		} // end if

		queryStr = "";

		if (!match) {
			queryStr = "";
			queryStr = new String(processQueryTitle(queryBk.getTitle()));
			if (remoteQuery(queryStr)) {
				match = true;
				// setBookInfo();
				// checkAVA(queryBk.parseVolume());
				return true;
			} else {
				match = false;
			} // end if
		} // end if

		queryStr = "";

		/*
		 * if (!match && strHandle.hasSomething(queryBk.getPublishYear())) { queryStr =
		 * new String( processQueryAuthor(queryBk.getCreator()) +
		 * processQueryYear(queryBk.getPublishYear()));
		 * 
		 * if (remoteQuery(queryStr)) { match = true; // setBookInfo(); //
		 * checkAVA(queryBk.parseVolume()); return true; } else { match = false; } //
		 * end if } // end if
		 */

		queryStr = "";

		if (!match) {
			queryStr = new String(processQueryTitleShort(queryBk.getTitle()));
			queryStrProccessed = true;
			queryStr += processQueryAuthor(queryBk.getCreator());
			queryStrProccessed = false;
			if (remoteQuery(queryStr)) {
				match = true;
				// setBookInfo();
				// checkAVA(queryBk.parseVolume());
				return true;
			} else {
				match = false;
			} // end if
		} // end if

		queryStr = "";
		// Additional query for Chinese titles

		if (!match && (CJKStringHandling.isCJKString(queryBk.getTitle())
				|| CJKStringHandling.isCJKString(queryBk.getCreator())
				|| CJKStringHandling.isCJKString(queryBk.getPublisher()))) {

			queryStr = new String(processQueryTitle(queryBk.getTitle()));
			if (remoteQuery(queryStr)) {
				match = true;
				// setBookInfo();
				// checkAVA(queryBk.parseVolume());
				return true;
			} else {
				match = false;
			} // end if

			queryStr = "";

			if (!match && queryBk.parseEdition() != -1) {
				queryStr = new String(
						processQueryTitle(queryBk.getTitle()) + processQueryEdition2(queryBk.parseEdition()));
				if (remoteQuery(queryStr)) {
					match = true;
					// setBookInfo();
					// checkAVA(queryBk.parseVolume());
					return true;
				} else {
					match = false;
				} // end if

				queryStr = "";

				if (!match && queryBk.parseEdition() != -1) {
					queryStr = new String(
							processQueryTitle(queryBk.getTitle()) + processQueryEdition3(queryBk.parseEdition()));
					if (remoteQuery(queryStr)) {
						match = true;
						// setBookInfo();
						// checkAVA(queryBk.parseVolume());
						return true;
					} else {
						match = false;
					} // end if
				} // end if
			} // end if
		} // end if

		queryStr = "";

		// Additional check for ISO Document number in <search> <genernal> PNX
		// tag
		if (!match && !CJKStringHandling.isCJKString(queryBk.getTitle())) {
			queryStr = new String(processQueryTitleISODoc(queryBk.getTitle()));
			if (remoteQuery(queryStr)) {
				match = true;
				// setBookInfo();
				// checkAVA(queryBk.parseVolume());
				return true;
			} else {
				match = false;
				errMsg += "Query: No record found on Primo." + Config.QUERY_SETTING;
			} // end if
		} // end if

		queryStr = "";
		return false;

	} // end query()

	// Called by query(), to fetch remote Primo PNX records.
	@Override
	protected boolean remoteQuery(String qstr) {
		if (queryBk.getPublishYear() == null || queryBk.getPublishYear().equals("")) {
			if (queryBk.getPublisher() == null || queryBk.getPublisher().equals("")) {
				if (queryBk.parseEdition() == -1 && queryBk.parseVolume() == -1) {
					return false;
				} // end if
			} // end if
		} // end if

		BufferedReader br = null;
		StringBuilder body = null;

		try {

			queryStr = Config.PRIMOVE_API_BASE + qstr;
			System.out.println("Query String: " + queryStr);

			URL url = new URL(Config.PRIMOVE_API_BASE + qstr);

			URLConnection con = url.openConnection();

			con.setConnectTimeout(3000);

			br = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
			body = new StringBuilder();
			String line = "";

			while ((line = br.readLine()) != null)
				body.append(line);

			// System.out.println(body.toString());

			Object obj = new JSONParser().parse(body.toString());
			JSONObject jo = (JSONObject) obj;
			JSONArray ja = (JSONArray) jo.get("docs");

			debug += queryStr + "\n";

			/*
			 * After fetched a XML doc, store necessary tags from the XMLs for further
			 * matching.
			 */
			System.out.println("Size: " + ja.size());

			for (int i = 0; i < ja.size(); i++) {
				// Return true if the query item is of ISO doc no. and of
				// published by ISO
				JSONObject jo2 = (JSONObject) ja.get(i);
				JSONObject pnxjo = (JSONObject) new JSONParser().parse(jo2.get("pnx").toString());
				JSONObject controljo = (JSONObject) new JSONParser().parse(pnxjo.get("control").toString());
				JSONObject displayjo = (JSONObject) new JSONParser().parse(pnxjo.get("display").toString());
				JSONObject addatajo = (JSONObject) new JSONParser().parse(pnxjo.get("addata").toString());
				JSONObject deliveryjo = (JSONObject) new JSONParser().parse(jo2.get("delivery").toString());
				JSONObject facetjo = (JSONObject) new JSONParser().parse(pnxjo.get("facets").toString());

				JSONArray deliveryCategoryja = (JSONArray) deliveryjo.get("deliveryCategory");
				if (deliveryCategoryja != null) {
					String deliveryCategory = deliveryCategoryja.get(0).toString().trim();
					if (deliveryCategory.equals("Alma-E") || deliveryCategory.equals("Alma-P")) {
						ava = true;
					}
				}

				JSONArray holdingja = (JSONArray) deliveryjo.get("holding");
				if (holdingja != null) {
					for (int l = 0; l < holdingja.size(); l++) {
						ext_itm_no++;

						
						JSONObject holdingjo = (JSONObject) new JSONParser().parse(holdingja.get(l).toString());
						String ava = holdingjo.get("availabilityStatus").toString();
						
						if (ava.equals("available"))
							ava_itm_no++;
					}
				}

				JSONArray frbrtypeja = (JSONArray) facetjo.get("frbrtype");
				String frbrtype = "";
				if (frbrtypeja != null) {
					frbrtype = frbrtypeja.get(0).toString().trim();
				}

				JSONArray frbrtypeidja = (JSONArray) facetjo.get("frbrgroupid");
				String frbrtypeid = "";
				if (frbrtypeidja != null) {
					frbrtypeid = frbrtypeidja.get(0).toString().trim();
				}

				JSONArray isbnja = (JSONArray) addatajo.get("isbn");
				if (isbnja != null)
					bk.setIsbn(isbnja.get(0).toString().trim());

				JSONArray recordidja = (JSONArray) controljo.get("recordid");
				String recordid = "";
				if (recordidja != null)
					recordid = recordidja.get(0).toString().trim();

				bk.setPrimoLink("https://" + Config.VALUES.get("PRIMOVE_BASE")
						+ Config.VALUES.get("PRIMOVE_RECORD_STRING") + recordid);

				JSONArray titleja = (JSONArray) displayjo.get("title");
				bk.setTitle(titleja.get(0).toString().trim());

				JSONArray unititleja = (JSONArray) displayjo.get("unititle");
				if (unititleja != null) {
					ArrayList al = new ArrayList();
					al.add(unititleja.get(0));
					bk.setAltTitles(al);
				}

				JSONArray subjectja = (JSONArray) displayjo.get("subject");
				if (subjectja != null) {

					String sub = "";
					for (int j = 0; j < subjectja.size(); j++) {
						sub += subjectja.get(j) + " ;";
					}
					bk.setSubject(sub);
				}

				JSONArray contributorja = (JSONArray) displayjo.get("contributor");
				String contributor = "";
				if (contributorja != null) {
					for (int j = 0; j < contributorja.size(); j++) {
						contributor += contributorja.get(j).toString().replaceAll("\\$\\$Q.*", "") + "; ";
					}
				}
				bk.setContributor(contributor);

				JSONArray creatorja = (JSONArray) displayjo.get("creator");
				String creator = "";
				if (creatorja != null) {
					for (int j = 0; j < creatorja.size(); j++) {
						creator += creatorja.get(j).toString().replaceAll("\\$\\$Q.*", "") + "; ";
					}
				}
				bk.setCreator(creator);

				JSONArray publisherja = (JSONArray) displayjo.get("publisher");
				if (publisherja != null)
					bk.setPublisher(publisherja.get(0).toString().trim());

				JSONArray editionja = (JSONArray) displayjo.get("edition");
				if (editionja != null)
					bk.setEdition(editionja.get(0).toString().trim());

				JSONArray creationdateja = (JSONArray) displayjo.get("creationdate");
				if (creationdateja != null)
					bk.setPublishYear(creationdateja.get(0).toString().trim());

				if (matchIsoPublisher() && matchIsoDocNo()) {
					return true;
				} // end if

				if (!strHandle.hasSomething(queryBk.getCreator())) {
					if (matchTitle() && matchPublisher() && matchYear()) {
						return true;
					} // end if
				} // end if

				if (matchTitle() && matchAuthor()) {

					if ((matchEdition() && matchPublisher() && matchYear())) {
						return true;
					} else if (!strHandle.hasSomething(queryBk.getPublisher()) && matchYear()
							&& strHandle.hasSomething(queryBk.getPublishYear())) {
						return true;
					} else if (matchEdition() && queryBk.parseEdition() > 1) {
						return true;
					} else {
						// if frbr type = 5, it is in a FRBR group, then fetch again the other pnx for
						// checking.
						if (frbrtype.equals("5")) {
							System.out.println("FRBR TYPE: " + frbrtype);
							System.out.println("FRBR ID: " + frbrtypeid);
							System.out.println("IN FRBR 5: ");
							queryStr += "&qInclude=facet_frbrgroupid,exact," + frbrtypeid;
							System.out.println("QSTR::: " + queryStr);
							url = new URL(queryStr);
							con = url.openConnection();
							con.setConnectTimeout(3000);

							br = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
							body = new StringBuilder();
							line = "";

							while ((line = br.readLine()) != null)
								body.append(line);

							// System.out.println(body.toString());

							Object obj2 = new JSONParser().parse(body.toString());
							JSONObject jo22 = (JSONObject) obj2;
							JSONArray ja2 = (JSONArray) jo22.get("docs");

							for (int k = 0; k < ja2.size(); k++) {
								JSONObject jo3 = (JSONObject) ja2.get(i);
								JSONObject pnxjo2 = (JSONObject) new JSONParser().parse(jo3.get("pnx").toString());
								JSONObject displayjo2 = (JSONObject) new JSONParser()
										.parse(pnxjo2.get("display").toString());
								JSONObject deliveryjo2 = (JSONObject) new JSONParser()
										.parse(jo3.get("delivery").toString());

								JSONArray deliveryCategoryja2 = (JSONArray) deliveryjo2.get("deliveryCategory");
								if (deliveryCategoryja2 != null) {
									String deliveryCategory = deliveryCategoryja.get(0).toString().trim();
									if (deliveryCategory.equals("Alma-E") || deliveryCategory.equals("Alma-P")) {
										ava = true;
									}

									JSONArray contributorja2 = (JSONArray) displayjo2.get("contributor");
									String contributor2 = "";
									if (contributorja2 != null) {
										for (int j = 0; j < contributorja2.size(); j++) {
											contributor2 += contributorja2.get(j).toString().replaceAll("\\$\\$Q.*", "")
													+ "; ";
										}
									}
									bk.setContributor(contributor2);
									JSONObject addatajo2 = (JSONObject) new JSONParser()
											.parse(pnxjo2.get("addata").toString());
									JSONArray isbnja2 = (JSONArray) addatajo2.get("isbn");
									if (isbnja2 != null)
										bk.setIsbn(isbnja2.get(0).toString().trim());

									JSONArray holdingja2 = (JSONArray) deliveryjo2.get("holding");
									ext_itm_no = 0;
									ava_itm_no = 0;
									if (holdingja2 != null) {
										for (int l = 0; l < holdingja2.size(); l++) {
											ext_itm_no++;

											System.out.println("HOLDING::::" + holdingja2.get(l));
											JSONObject holdingjo2 = (JSONObject) new JSONParser()
													.parse(holdingja2.get(l).toString());
											String ava = holdingjo2.get("availabilityStatus").toString();
											System.out.println("HOLDING::::AVAAAA:::: " + ava);
											if (ava.equals("available"))
												ava_itm_no++;
										}
									}

									JSONArray creatorja2 = (JSONArray) displayjo2.get("creator");
									String creator2 = "";
									if (creatorja2 != null) {
										for (int j = 0; j < creatorja2.size(); j++) {
											creator2 += creatorja2.get(j).toString().replaceAll("\\$\\$Q.*", "") + "; ";
										}
									}
									bk.setCreator(creator2);
									JSONObject controljo2 = (JSONObject) new JSONParser()
											.parse(pnxjo2.get("control").toString());
									JSONArray recordidja2 = (JSONArray) controljo2.get("recordid");
									String recordid2 = "";
									if (recordidja2 != null)
										recordid2 = recordidja2.get(0).toString().trim();

									bk.setPrimoLink("https://" + Config.VALUES.get("PRIMOVE_BASE")
											+ Config.VALUES.get("PRIMOVE_RECORD_STRING") + recordid);

									JSONArray publisherja2 = (JSONArray) displayjo2.get("publisher");
									if (publisherja2 != null)
										bk.setPublisher(publisherja2.get(0).toString().trim());

									JSONArray editionja2 = (JSONArray) displayjo2.get("edition");
									if (editionja2 != null)
										bk.setEdition(editionja2.get(0).toString().trim());

									JSONArray creationdateja2 = (JSONArray) displayjo2.get("creationdate");
									if (creationdateja2 != null)
										bk.setPublishYear(creationdateja2.get(0).toString().trim());

									if ((matchEdition() && matchPublisher() && matchYear())) {
										return true;
									}

								}
							}

						}
						/*
						 * As Primo X-service only shows the first FRBRed record, check the rest records
						 * involving the same frbrgroupid.
						 */
						/*
						 * String frbrid = ""; frbrid = getNodeValue("frbrgroupid", nodesFacet); String
						 * frbr_qstr = qstr + "&q=facet_frbrgroupid,exact," + frbrid; //
						 * System.out.println("FRBR:" + Config.PRIMO_X_BASE + // frbr_qstr); // Document
						 * doc2 = b.parse(Config.PRIMO_X_BASE + frbr_qstr); // nodesFrbrRecord =
						 * doc2.getElementsByTagName("record");
						 * 
						 * for (int j = 0; j < nodesFrbrRecord.getLength(); j++) {
						 * 
						 * if (!strHandle.hasSomething(queryBk.getPublishYear()) &&
						 * queryBk.parseEdition() == -1 &&
						 * !strHandle.hasSomething(queryBk.getPublisher())) { return false; } // end if
						 * 
						 * if (matchEdition() && matchTitle() && matchAuthor() && matchPublisher() &&
						 * matchYear()) { return true; } else if (matchEdition() && matchTitle() &&
						 * matchAuthor() && matchYear() &&
						 * !strHandle.hasSomething(queryBk.getPublisher())) { return true; } // end if
						 * 
						 * } // end for
						 */
					} // end if
				} // end if
			} // end for
		} // end try

		catch (

		Exception e) {
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			String errStr = "PrimoQueryByNonISBN:remoteQuery()" + errors.toString();
			System.out.println(errStr);
			errMsg = errStr;
		} // end catch
		return false;
	} // end remoteQuery()

	private String processAuthor(String str) {
		str = strHandle.tidyString(str);
		return str;
	} // end processAuthor()

	private String processTitle(String str) {
		str = strHandle.tidyString(str);
		// Triming off any non-character symbols
		str = str.replaceAll("\\s|:|：|、|,|:|\"", "");

		// remove any space if the title is in Chinese
		if (CJKStringHandling.isCJKString(str)) {
			str = str.replaceAll("\\s", "");
			str = CJKStringHandling.standardizeVariantChineseFast(str);
			str = CJKStringHandling.standardizeVariantChineseForLooseMaching(str);
			str = CJKStringHandling.convertToSimpChinese(str);
		} // end if
			// System.out.println("ProcessTitle(): " + str);
		return str;
	} // end processTitle

	private boolean matchAuthor() {

		String a = strHandle.removeAccents(queryBk.getCreator());

		if (!strHandle.hasSomething(a))
			return false;

		String cPnx = bk.getCreator() + " " + bk.getContributor();

		cPnx = processAuthor(cPnx).replaceAll("\\s", "");

		if (CJKStringHandling.isCJKString(a)) {
			a = CJKStringHandling.standardizeVariantChineseFast(a);
			a = CJKStringHandling.standardizeVariantChineseForLooseMaching(a);
			a = CJKStringHandling.convertToSimpChinese(a);
			a = a.replaceAll("\\d", "");
		} // end if

		if (CJKStringHandling.isCJKString(cPnx)) {
			cPnx = CJKStringHandling.standardizeVariantChineseFast(cPnx);
			cPnx = CJKStringHandling.standardizeVariantChineseForLooseMaching(cPnx);
			cPnx = CJKStringHandling.convertToSimpChinese(cPnx);
			cPnx = cPnx.replaceAll("\\d", "");
		} // end if

		String[] auKeys = a.split(" ");

		for (int i = 0; i < auKeys.length; i++) {
			String key = auKeys[i].toLowerCase();
			if (cPnx.contains(key) && key.length() > 1) {
				debug += "MATCH AUTHOR: cPnx: " + cPnx + " key: " + key + "\n";
				System.out.println(debug);
				return true;
			} // end if
		} // end for
		debug += "NO MATCH AUTHOR: cPnx: " + cPnx + " a: " + a + "\n";
		System.out.println(debug);
		return false;
	} // end matchAuthor()

	private boolean matchIsoPublisher() {
		String p = queryBk.getPublisher().toLowerCase();
		p = strHandle.trimSpecialChars(p);
		if (p.equals("iso") || p.contains("international organization for standardization")) {
			return true;
		} // end if

		return false;
	} // end matchIsoPublisher()

	private boolean matchPublisher() {
		String p = strHandle.tidyString(queryBk.standizePublisherWording());
		String pPnx = strHandle.tidyString(queryBk.stardizePublisherWording(bk.getPublisher()));
		String[] pubKeys;
		if (CJKStringHandling.isCJKString(p) || CJKStringHandling.isCJKString(pPnx)) {
			p = CJKStringHandling.convertToSimpChinese(p);
			pPnx = CJKStringHandling.convertToSimpChinese(pPnx);
		} // end if

		int len = 0;
		if (CJKStringHandling.isCJKString(pPnx)) {
			len -= 1;
			pubKeys = new String[pPnx.length()];
			for (int i = 0; i < pPnx.length(); i++) {
				pubKeys[i] = String.valueOf(pPnx.charAt(i));
				if (i < pPnx.length() - 1) {
					pubKeys[i] += String.valueOf(pPnx.charAt(i + 1));
				} // end if
			} // end for
		} else {
			pubKeys = pPnx.split(" ");
		} // end if

		len += pubKeys.length;

		for (int i = 0; i < len; i++) {
			String key = pubKeys[i].toLowerCase();
			if (p.toLowerCase().contains(key)) {
				debug = "PUB MATCH: PNX - " + pPnx + " p:" + p + "\n";
				System.out.println(debug);
				return true;
			} // end if
		} // end for
		debug += "NO PUB MATCH: PNX - " + pPnx + " p:" + p + "\n";
		System.out.println(debug);
		return false;
	} // end matchPublisher()

	private boolean matchTitle() {
		String t = processTitle(queryBk.getTitle());

		String tPnx = processTitle(bk.getTitle());

		if (CJKStringHandling.isCJKString(t)) {

			t = CJKStringHandling.standardizeVariantChineseFast(t);
			t = CJKStringHandling.standardizeVariantChineseForLooseMaching(t);
			t = CJKStringHandling.convertToSimpChinese(t);
			t = CJKStringHandling.removeNonCJKChars(t);
		} // end if

		if (CJKStringHandling.isCJKString(tPnx)) {

			tPnx = CJKStringHandling.standardizeVariantChineseFast(tPnx);
			tPnx = CJKStringHandling.standardizeVariantChineseForLooseMaching(tPnx);
			tPnx = CJKStringHandling.convertToSimpChinese(tPnx);
			tPnx = CJKStringHandling.removeNonCJKChars(tPnx);
		} // end if
		ArrayList<String> atl = bk.getAltTitles();

		if (tPnx.contains(t) || t.contains(tPnx)) {
			debug += "MATCH TITLE: tPnx: " + tPnx + " t:" + t + ":" + "\n";
			System.out.println(debug);
			return true;
		} // end if

		for (int i = 0; i < atl.size(); i++) {
			String t3 = processTitle(atl.get(i));

			if (CJKStringHandling.isCJKString(t3)) {
				t3 = CJKStringHandling.standardizeVariantChineseFast(t3);
				t3 = CJKStringHandling.standardizeVariantChineseForLooseMaching(t3);
				t3 = CJKStringHandling.convertToSimpChinese(t3);
				t3 = CJKStringHandling.removeNonCJKChars(t3);
			} // end if

			if (t3.contains(t)) {
				debug += "MATCH TITLE: t: " + t + " t3:" + t3 + "\n";
				System.out.println(debug);
				return true;
			} // end if
		} // end for
		debug += "NO MATCH TITLE: tPnx: " + tPnx + " t:" + t + "\n";
		System.out.println(debug);
		return false;
	} // matchTitle()

	private boolean matchIsoDocNo() {
		String t = queryBk.getTitle();
		String t2;
		t = t.replace(" ", "");
		t.toLowerCase();
		ArrayList<String> atl = getNodeValues("general", nodesSearch);
		for (int i = 0; i < atl.size(); i++) {
			t2 = atl.get(i).replace(" ", "").toLowerCase();
			if (t2.contains(t2)) {
				return true;
			} // end if
		} // end for
		return false;
	} // end matchIsoDocNo()

	private boolean matchEdition() {
		String e = bk.getEdition();
		if (queryBk.parseEdition() == -1) {
			return true;
		} else {
			if (e == null || e.equals("")) {
				e = "1";
			} // end if
			if (queryBk.parseEdition() == queryBk.parseEdition(e)) {
				debug += " MATCH EDITION edition: " + queryBk.parseEdition() + " e: " + e + "\n";
				System.out.println(debug);
				return true;
			} // end if
		} // end if
		debug += " NOT MATCH EDITION edition: " + queryBk.parseEdition() + " e: " + e + "\n";
		System.out.println(debug);
		return false;
	} // end matchEdition()

	private boolean matchYear() {
		String cPnx = bk.getPublishYear();

		cPnx = cPnx.replace("-", "0");
		if (queryBk.getPublishYear() == null) {
			debug += "MATCH YEAR: PNX -" + cPnx + " year - " + queryBk.getPublishYear() + "\n";
			return true;
		} // end if
		if (cPnx.contains(queryBk.getPublishYear())) {
			debug += "MATCH YEAR: PNX -" + cPnx + " year - " + queryBk.getPublishYear() + "\n";
			System.out.println(debug);
			return true;
		} // end if
		debug += "NO MATCH YEAR: PNX -" + cPnx + " year - " + queryBk.getPublishYear() + "\n";
		System.out.println(debug);
		return false;
	} // end matchYear()

	private String processQueryEdition(double i) {
		String str = "";
		String noStr = "";
		int i2 = (int) i;
		switch (i2) {
		case 2:
			noStr = "nd";
			break;
		case 3:
			noStr = "rd";
			break;
		default:
			noStr = "th";
		} // end switch
		str += "&q=" + Config.VALUES.get("PRIMO_SEARCHFIELD_EDITION") + "general,contains," + i2 + noStr + "%20ed";

		if (CJKStringHandling.isCJKString(queryBk.getTitle())) {
			str = "&q=" + Config.VALUES.get("PRIMO_SEARCHFIELD_EDITION") + ",contains," + i2 + "版";
		} // end if
		return str;
	} // end processQueryEdition()

	private String processQueryEdition2(double i) {
		if (i == 1) {
			return "";
		} // end if

		String str = "&q=" + Config.VALUES.get("PRIMO_SEARCHFIELD_EDITION") + ",contains,第" + (int) i;

		String[] chiEd = { "一", "二", "三", "四", "五", "六", "七", "八", "九", "十", "十一", "十二", "十三", "十四", "十五", "十六", "十七",
				"十八", "十九", "廿", "廿一", "廿二", "廿三", "廿四", "廿五", "廿六", "廿七", "廿八", "廿九", "卅" };

		for (int j = chiEd.length; 1 < j; j--) {
			str = str.replaceAll(Integer.toString(j), chiEd[j - 1]);
		} // end for

		str += "版";

		return str;
	} // end processQueryEdition2()

	private String processQueryEdition3(double i) {
		String str = "&q=" + Config.VALUES.get("PRIMO_SEARCHFIELD_EDITION") + ",contains," + (int) i;

		String[] chiEd = { "一", "二", "三", "四", "五", "六", "七", "八", "九", "十", "十一", "十二", "十三", "十四", "十五", "十六", "十七",
				"十八", "十九", "廿", "廿一", "廿二", "廿三", "廿四", "廿五", "廿六", "廿七", "廿八", "廿九", "卅" };

		for (int j = chiEd.length; 1 < j; j--) {
			str = str.replaceAll(Integer.toString(j), chiEd[j - 1]);
		} // end for

		str += "版";

		return str;
	} // end processQueryEdition2()

	private String processQueryPublisher(String str) {
		if (!strHandle.hasSomething(str)) {
			return "";
		} // end if
		String queryPublisher = "";
		str = queryBk.stardizePublisherWording(str);
		String[] sArry = str.split(" ");

		/*
		 * "lsr02" is the local search field defined by OUHK as publisher This may need
		 * a change for various reason
		 */
		try {
			/*
			 * queryPublisher += ",AND;" + Config.VALUES.get("PRIMO_SEARCHFIELD_PUBLISHER")
			 * + ",contains," + URLEncoder.encode(sArry[0],
			 * StandardCharsets.UTF_8.toString());
			 */
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return queryPublisher;
	} // end processQueryPublisher()

	private String processQueryAuthor(String str) {
		if (!strHandle.hasSomething(str)) {
			return "";
		} // end if
		String[] sArry;
		String queryAuthor = "";
		str = strHandle.tidyString(str);
		sArry = str.split(" ");
		try {
			if (queryStr == null || !queryStr.contains("&q=")) {
				queryAuthor = "&q=" + Config.VALUES.get("PRIMO_SEARCHFIELD_AUTHOR") + ",contains,"
						+ URLEncoder.encode(sArry[0], StandardCharsets.UTF_8.toString());
			} else if (queryStr.contains("&q=")) {

				queryAuthor = ",AND;" + Config.VALUES.get("PRIMO_SEARCHFIELD_AUTHOR") + ",contains,"
						+ URLEncoder.encode(sArry[0], StandardCharsets.UTF_8.toString());
				
			}

		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return queryAuthor;
	} // end processQueryAuthor()

	private String processQueryTitleISODoc(String t) {

		if (!strHandle.hasSomething(t)) {
			return "";
		} // end if

		t = t.toLowerCase();
		t = t.replace("/", "%2F");
		t = t.replace(" ", "%20");
		t = t.replace(":", "%3A");

		try {
			return "&q=general,contains,reference,AND;general,contains,"
					+ URLEncoder.encode(t, StandardCharsets.UTF_8.toString());
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return t;
	} // end processQueryTitleISODoc()

	private String processQueryTitle(String t) {

		if (!strHandle.hasSomething(t)) {
			return "";
		} // end if

		t = strHandle.tidyString(t);
		t = t.replaceAll("\\d+dst|\\d+nd|\\d+rd|\\d+th|" + "|[0-9]s$|[0-9]s |[0-9]th |[0-9]st|[0-9]nd |[0-9]rd"
				+ "|\\.|^\\s+|,|\\?|:|'s|\\+|\\#|\\!|\\*|\\\"", "");
		t = t.replaceAll("\\w*\\d\\w*", " ");
		t = t.replaceAll("\\s\\s", " ");
		t = t.trim();

		if (CJKStringHandling.isCJKString(t)) {
			t = t.replaceAll("[a-z]|[A-Z]|\\s|[0-9]", "");
			t = t.replaceAll("\\s", "");
			t = CJKStringHandling.standardizeVariantChineseFast(t);
			t = CJKStringHandling.standardizeVariantChineseForLooseMaching(t);
		} // end if

		String[] tArr = t.split(" ");
		String query = "";
		if (tArr.length == 1) {
			try {
				
				if (!queryStrProccessed)
					query = "&q=title,contains," + URLEncoder.encode(t, StandardCharsets.UTF_8.toString());
				else
					query = ",AND;title,contains," + URLEncoder.encode(t, StandardCharsets.UTF_8.toString());
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return query;
		} // end if

		for (int i = 0; i < tArr.length; i++) {
			try {
				if (i == 0) {
					if (!queryStrProccessed)
						query += "&q=title,contains," + URLEncoder.encode(tArr[i], StandardCharsets.UTF_8.toString())
								+ "*";
					else
						query += ",AND;title,contains," + URLEncoder.encode(tArr[i], StandardCharsets.UTF_8.toString())
								+ "*";

				}
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} // end for

		// System.out.println("processQueryTitle(): " + query);
		return query;
	} // end processQueryTitle()

	private String processQueryTitleShort(String t) {
		if (!strHandle.hasSomething(t)) {
			return "";
		} // end if
		String[] tArr = t.split(" ");
		String t2 = "";
		if (tArr.length == 1) {
			try {
				if (queryStr == null || !queryStr.contains("&=q"))
					return "&q=title,contains," + URLEncoder.encode(t, StandardCharsets.UTF_8.toString());
				else
					return ",AND;title,contains," + URLEncoder.encode(t, StandardCharsets.UTF_8.toString());
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} // end if

		if (tArr.length >= 2) {
			for (int i = 0; i < 2; i++) {
				t2 += ",AND;title,contains," + tArr[i] + "*";
			} // end for
		} else {
			t2 += ",AND;title,contains," + tArr[0] + "*";
		} // end if
		return t2;
	} // end processQueryTitleShort()

	private String processQueryYear(String str) {
		if (!strHandle.hasSomething(str)) {
			return "";
		} // end if
		if (str == null || str == "") {
			return "";
		} // end if
		Pattern pat = Pattern.compile("(\\d\\d\\d\\d)");
		Matcher mat = pat.matcher(str);
		if (mat.find()) {
			return ",AND;cdate,contains," + mat.group(1);
		} // end if
		return "";
	} // end processQueryYear

} // end class PrimoQueryByISBN