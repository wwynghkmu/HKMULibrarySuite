package hk.edu.hkmu.lib.bookquery;

import java.io.BufferedReader;
import org.w3c.dom.*;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.regex.*;
import javax.xml.parsers.*;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.json.simple.*;
import org.json.simple.parser.*;

/**
 * This abstract class defines the common variables and methods used by the
 * classes which search over Primo via X-service or via Z39.50.
 * 
 * @author Wai-yan NG
 * @author wwyng@ouhk.edu.hk
 * @version 1.0
 * @since Jan 29, 2019
 */
public abstract class PrimoQuery extends Query {

	abstract boolean query();

	abstract boolean remoteQuery(String qstr);

	public PrimoQuery() {
		clearQuery();
	} // end PrimoQuery

	/**
	 * Specifying the insitition
	 * 
	 * @param inst The institution code
	 */
	public PrimoQuery(String inst) {
		Config.init(inst);
		clearQuery();
	} // end PrimoQuery()

	/**
	 * Filling a BookItem info while a title is matched.
	 */
	protected void setBookInfo() {
		if (match) {
			bk.setTitle(getNodeValue("title", nodesDisplay));
			bk.setAltTitles(getNodeValues("alttitle", nodesSearch));
			bk.setPublisher(getNodeValue("publisher", nodesDisplay));
			bk.setEdition(getNodeValue("edition", nodesDisplay));
			bk.setBookType(getNodeValue("type", nodesDisplay));
			bk.setCreator(getNodeValue("creator", nodesDisplay));
			bk.isbn.setIsbn(getNodeValue("isbn", nodesSearch));
			bk.setContributor(getNodeValue("contributor", nodesDisplay));
			bk.setPublishYear(getNodeValue("creationdate", nodesDisplay));
			bk.setFormat(getNodeValue("format", nodesDisplay));

			if (getNodeValue("subject", nodesDisplay) != null) {
				bk.setSubject(getNodeValue("subject", nodesDisplay));
			} // end if

			ArrayList<String> al = new ArrayList<String>();
			al = getNodeValues("sourcerecordid", nodesControl);
			Pattern pat;
			Matcher mat;

			String primoIds[] = new String[al.size()];
			int noPrimoIds = 0;
			boolean found = false;
			for (int i = 0, j = 0; i < al.size(); i++) {
				if (al.get(i).matches(".*\\$\\$O" + Config.VALUES.get("SOURCE_ID") + ".*")) {
					pat = Pattern.compile(".*\\$\\$O(.*)");
					mat = pat.matcher(al.get(i));
					if (mat.find()) {
						if (mat.group(1) != null) {
							primoIds[j] = mat.group(1);
							j++;
						} // end if
						bk.setPrimoLink("http://" + Config.VALUES.get("PRIMO_BASE") + "/"
								+ Config.VALUES.get("INST_CODE") + ":" + mat.group(1));

						String temp_ilsid = mat.group(1).replaceAll(Config.VALUES.get("SOURCE_ID").replace(".", ""),
								"");
						temp_ilsid = temp_ilsid.replace(".", "");

						bk.setIlsId(temp_ilsid);
						found = true;
					} // end if
				} else {
					if (!found) {
						primoIds[0] = getNodeValue("recordid", nodesControl);
						bk.setPrimoLink("http://" + Config.VALUES.get("PRIMO_BASE") + "/"
								+ Config.VALUES.get("INST_CODE") + ":" + getNodeValue("recordid", nodesControl));

						String temp_ilsid = getNodeValue("recordid", nodesControl)
								.replaceAll(Config.VALUES.get("SOURCE_ID"), "").replace(".", "");
						temp_ilsid = temp_ilsid.replace(".", "");

						bk.setIlsId(temp_ilsid);
					} // end if
				} // end if
			} // end for

			for (int i = 0; i < primoIds.length; i++) {
				if (primoIds[i] != null) {
					noPrimoIds++;
				} // end if
			} // end if

			String[] ilsids = new String[noPrimoIds];
			for (int i = 0; i < primoIds.length; i++) {
				if (primoIds[i] != null) {
					ilsids[i] = primoIds[i];
					ilsids[i] = ilsids[i].replaceAll(Config.VALUES.get("SOURCE_ID").replace(".", ""), "");
					ilsids[i] = ilsids[i].replace(".", "");
				} // end if
			} // end if
			bk.setIlsIds(ilsids);
			bib_no = noPrimoIds;
			al = new ArrayList<String>();
			al = getNodeValues("availlibrary", nodesDisplay);

			for (int i = 0, j = 0; i < al.size(); i++) {
				if (al.get(i).matches(".*\\$\\$I" + Config.VALUES.get("INST_CODE") + "\\$\\$.*")) {
					bk.holdingInfo.add(new ArrayList<String>());
					pat = Pattern.compile("\\$\\$I(.*?)\\$\\$");
					mat = pat.matcher(al.get(i));
					if (mat.find()) {
						bk.holdingInfo.get(j).add(mat.group(1));
					} // end if
					pat = Pattern.compile("\\$\\$L(.*?)\\$\\$");
					mat = pat.matcher(al.get(i));
					if (mat.find()) {
						bk.holdingInfo.get(j).add(mat.group(1));
					} // end if
					pat = Pattern.compile("\\$\\$2\\((.*?)\\)");
					mat = pat.matcher(al.get(i));
					if (mat.find()) {
						bk.holdingInfo.get(j).add(mat.group(1));
					} else {
						bk.holdingInfo.get(j).add("");
					} // end if
					bk.holdingInfo.get(j).add("NA");
					++j;
				} // end if
			} // end for

			if (al.size() == 0) {
				bk.holdingInfo.add(new ArrayList<String>());
				bk.holdingInfo.get(0).add("");
				bk.holdingInfo.get(0).add("");
				bk.holdingInfo.get(0).add("");
				bk.holdingInfo.get(0).add("");
			} // end if

			boolean hasFulltext = false;
			al = getNodeValues("delcategory", nodesDelivery);
			for (int i = 0; i < al.size(); i++) {
				if (al.get(i).contains("$$VOnline Resource$$O" + Config.VALUES.get("SOURCE_ID"))) {
					hasFulltext = true;
				} else if (al.size() == 1
						&& getNodeValue("institution", nodesDelivery).equals(Config.VALUES.get("INST_CODE"))) {
					hasFulltext = true;
				} // end if
			} // end for

			// Set links to full text URLs
			if (hasFulltext) {
				al = getNodeValues("linktorsrc", nodesLink);
				for (int i = 0; i < al.size(); i++) {

					if (!al.get(i).matches("\\$\\$U.*\\$\\$")) {
						continue;
					} // end if

					pat = Pattern.compile("\\$\\$U(.*?)\\$\\$");
					mat = pat.matcher(al.get(i));
					mat.find();
					if (!al.get(i).contains("$$O")) {
						bk.fulltextUrls.add(mat.group(1));
					} else if (al.get(i).contains(Config.VALUES.get("SOURCE_ID")) || al.size() == 1) {
						bk.fulltextUrls.add(mat.group(1));
					} // end if
				} // end for
			} else {
				bk.fulltextUrls.add("");
			} // end if
		} // end if
	} // end setBookInfo()

	/**
	 * Check ILS item availability.
	 * 
	 * @param vol vol is the volume number of a title. '-1' means no volume number.
	 */
	protected void checkAVA(int vol) {

		String[] ilsids = bk.getIlsIds();
		for (int h = 0; h < ilsids.length; h++) {
			if (ilsids.length > 0) {
				String urlStr = "http://" + Config.VALUES.get("ILS_AVA_BASE");
				String outstr = "";
				urlStr = urlStr + ilsids[h];
				queryStr = urlStr;

				int itemCount = 0;
				String loanStatus = "";
				String loanStatuses[] = null;
				int volumes[] = null;

				String loanDueDate = "";
				String loanDueDates[] = null;
				String subLibraries[] = null;
				try {
					URL url = new URL(urlStr);
					URLConnection urlcon = url.openConnection();

					urlcon.setConnectTimeout(3000);
					BufferedReader buffread = new BufferedReader(new InputStreamReader(urlcon.getInputStream()));
					String inputLine;
					while ((inputLine = buffread.readLine()) != null)
						outstr += inputLine;
					buffread.close();
					// Check HKSYU's III Millennium API
					if (Config.VALUES.get("INST_CODE").contains("HKSYU")) {

						outstr = outstr.replaceAll("^.*\\[", "");
						outstr = outstr.replaceAll("\\].*$", "");
						outstr = "{\"items\": [" + outstr + "] }";
						JSONParser parser = new JSONParser();
						JSONObject json = (JSONObject) parser.parse(outstr);
						JSONArray jarry = (JSONArray) json.get("items");

						volumes = new int[jarry.size()];
						loanStatuses = new String[jarry.size()];
						loanDueDates = new String[jarry.size()];
						for (int i = 0; i < jarry.size(); i++) {
							JSONObject jo = (JSONObject) jarry.get(i);
							String v = jo.get("volume").toString();
							if (bk.isMultiVolume() && !v.contains("v.") && !v.contains("pt.")) {
								v = "yearvolume" + v;
							} // end if
							volumes[i] = bk.parseVolume(v);
							String order = "";
							if (jo.get("order") != null)
								order = jo.get("order").toString();
							loanStatuses[i] = jo.get("status").toString();
							if (strHandle.hasSomething(order)) {
								loanStatuses[i] = order;
							} else {
								loanDueDates[i] = loanStatuses[i];
							} // end for
							itemCount++;
						} // end for

					} else {
						// Check Aleph ILSs (OUHK,TWC,CIHE,and CHCHE)'
						// X-services
						DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
						DocumentBuilder builder = factory.newDocumentBuilder();
						InputSource is = new InputSource(new StringReader(outstr));
						Document doc = builder.parse(is);
						NodeList nlist = doc.getElementsByTagName("loan-status");
						if (nlist != null && nlist.getLength() == 1) {
							loanStatus = nlist.item(0).getFirstChild().getNodeValue();
							nlist = doc.getElementsByTagName("due-date");
							loanDueDate = nlist.item(0).getFirstChild().getNodeValue();
							loanStatuses = new String[1];

							loanStatuses[0] = loanStatus;
							loanDueDates = new String[1];
							loanDueDates[0] = loanDueDate;
							volumes = new int[1];

							String v = "";
							nlist = doc.getElementsByTagName("location");
							if (nlist.item(0).getFirstChild() != null) {
								v = nlist.item(0).getFirstChild().getNodeValue();
								if (bk.isMultiVolume() && !v.contains("v.") && !v.contains("pt.")) {
									v = "yearvolume" + v;
								} // end if
								volumes[0] = bk.parseVolume(v);
							} else {
								nlist = doc.getElementsByTagName("z30-description");
								if (nlist.item(0).getFirstChild() != null) {
									v = nlist.item(0).getFirstChild().getNodeValue();
									volumes[0] = bk.parseVolume(v);
								} // end if
							} // end if
							itemCount++;

						} else if (nlist != null && nlist.getLength() > 1) {

							loanStatuses = new String[nlist.getLength()];
							loanDueDates = new String[nlist.getLength()];
							subLibraries = new String[nlist.getLength()];
							volumes = new int[nlist.getLength()];
							for (int i = 0; i < nlist.getLength(); i++) {
								loanStatuses[i] = nlist.item(i).getFirstChild().getNodeValue();

								itemCount++;
							} // end for

							nlist = doc.getElementsByTagName("due-date");
							for (int i = 0; i < nlist.getLength(); i++) {
								loanDueDates[i] = nlist.item(i).getFirstChild().getNodeValue();
							} // end for

							nlist = doc.getElementsByTagName("sub-library");
							for (int i = 0; i < nlist.getLength(); i++) {
								subLibraries[i] = nlist.item(i).getFirstChild().getNodeValue();
							} // end for

							nlist = doc.getElementsByTagName("location");
							for (int i = 0; i < nlist.getLength(); i++) {
								if (nlist.item(i).getFirstChild() == null) {
									volumes[i] = -1;
								} else {
									String v = nlist.item(i).getFirstChild().getNodeValue();
									volumes[i] = bk.parseVolume(v);
								} // end if

							} // end for

							nlist = doc.getElementsByTagName("z30-description");

							for (int i = 0; i < nlist.getLength(); i++) {
								if (nlist.item(i).getFirstChild() == null) {
									volumes[i] = -1;
								} else {
									volumes[i] = bk.parseVolume(nlist.item(i).getFirstChild().getNodeValue());
								} // end if

							} // end for
						} // end if

						if (vol < 0 && volumes[0] > 0 && bk.isMultiVolume()) {
							ext_itm_no = 0;
						} else {
							ext_itm_no = itemCount;
						} // end if

					} // end if
				} // end try
				catch (Exception e) {
					StringWriter errors = new StringWriter();
					e.printStackTrace(new PrintWriter(errors));
					String errStr = "PrimoQuery:checkAva()" + errors.toString();
					errMsg = errStr;
					System.out.println(errStr);
				} // end catch
				if (loanStatuses != null) {
					boolean illable = false;
					ArrayList<String> al = new ArrayList<String>();
					al = getNodeValues("lds48", nodesDisplay);
					for (int m = 0; m < al.size(); m++) {
						if (al.get(m).contains("ILL-") && al.get(m).contains(Config.VALUES.get("INST_CODE"))) {
							illable = true;
						} // end if
					} // end for
					for (int i = 0; i < itemCount; i++) {
						loanDueDates[i] = strHandle.normalizeString(loanDueDates[i]);
						loanStatuses[i] = strHandle.normalizeString(loanStatuses[i]);

						if ((loanDueDates[i].equals("ONSHELF") || loanDueDates[i].equals("AVAILABLE")
								|| loanDueDates[i].equals("JUSTRETURNED")) && bk.getBookType().contains("book")
								&& !loanStatuses[i].contains("RESERVE") && !loanStatuses[i].contains("USEONLY")
								&& !loanStatuses[i].contains("NOTCIR") && !loanStatuses[i].contains("NOT")
								&& !loanStatuses[i].contains("CONTACT") && !loanStatuses[i].contains("ASK")
								&& !loanStatuses[i].contains("STAFF") && !loanStatuses[i].contains("MISSING")
								&& !loanStatuses[i].contains("ORDER") && !loanStatuses[i].contains("DISPLAY")
								&& !loanStatuses[i].contains("EXHIBITION") && !loanStatuses[i].contains("CATALOGING")
								&& !loanStatuses[i].contains("BINARY") && !loanStatuses[i].contains("DAMAGE")
								&& !loanStatuses[i].contains("WEBACCESS") && !loanStatuses[i].contains("INLIBUSE")
								&& !loanStatuses[i].contains("WITHDRAWN") && !loanStatuses[i].contains("WRITE-OFF")
								&& !loanStatuses[i].contains("CANCELL") && !loanStatuses[i].contains("NEWITEM")
								&& !loanStatuses[i].contains("OTHER") && !loanStatuses[i].contains("LOST")) {

							if (vol == -1 && (volumes[i] < 1 || !bk.isMultiVolume())) {
								ava = true;
								int tmp = 0;
								if (illable) {

									if (bib_no == 1 && ext_itm_no != 0) {
										ava_itm_no++;
									} else if (volumes.length == 1 && volumes[0] == -1) {
										ava_itm_no++;
									} else {
										for (int j = 0; j < volumes.length; j++) {

											if (volumes[j] == -1 || !bk.isMultiVolume()) {
												tmp++;
											} // end if
										} // end for
										if (tmp == volumes.length) {
											ava_itm_no++;
										} // end if
									} // end if
								} else {
									ext_itm_no = 0;
									ava_itm_no = 0;
								} // end if
							} else if (volumes[i] > 0 && vol == volumes[i]) {
								ava = true;
								if (illable) {
									ava_itm_no++;
									ext_itm_no++;
								} else {
									ext_itm_no = 0;
									ava_itm_no = 0;
								} // end if
							} // end if
						} // end if
					} // end for
				} // end if
			} // end for
		} // end if
	} // end checkAva()
} // end class PrimoQuery