package hk.edu.hkmu.lib.bookquery;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import javax.xml.parsers.*;
import org.w3c.dom.*;

import hk.edu.hkmu.lib.BookItem;

/**
 * This class accepts ISBN and Volume (optional) and search over Primo via
 * X-service. The related configs can be done in Config.java of the same
 * package.
 * 
 * @author Wai-yan NG
 * @author wwyng@ouhk.edu.hk
 * @version 1.0
 * @since Jan 29, 2019
 */
public class PrimoQueryByISBN extends PrimoQuery {

	public PrimoQueryByISBN() {
		super();
	} // end PrimoQueryByISBN()

	public PrimoQueryByISBN(String str) {
		super();
		queryBk = new BookItem(str);
		query();
	} // end PrimoQueryByISBN()

	public PrimoQueryByISBN(String str, String inst) {
		super(inst);
		queryBk = new BookItem(str);
		queryBk.setVolume("-1");
		query();
	} // end PrimoQueryByISBN()

	public PrimoQueryByISBN(String str, String inst, String vol) {
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
		queryStr = new String("&query=isbn,contains,");
		if (queryBk.isbn.getValidity()) {
			queryStr += queryBk.isbn.getIsbn13();
			if (remoteQuery(queryStr)) {
				setBookInfo();
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
		DocumentBuilderFactory f;
		DocumentBuilder b;
		Document doc;
		try {

			f = DocumentBuilderFactory.newInstance();
			b = f.newDocumentBuilder();
			URL url = new URL(Config.PRIMO_X_BASE + qstr);
			URLConnection con = url.openConnection();
			con.setConnectTimeout(3000);
			doc = b.parse(con.getInputStream());

			System.out.println(Config.PRIMO_X_BASE + qstr);
			nodesDisplay = doc.getElementsByTagName("display");
			nodesControl = doc.getElementsByTagName("control");
			nodesLink = doc.getElementsByTagName("links");
			nodesSearch = doc.getElementsByTagName("search");
			nodesDelivery = doc.getElementsByTagName("delivery");
			if (nodesDisplay.item(0) == null || nodesControl.item(0) == null) {
				match = false;
				return false;
			} else {
				int indrec = 0;
				indrec = determineRecordIndex(nodesControl);

				// if no record found with an appropriate source ID
				if (indrec == -1) {
					match = false;
					return false;
				} // end if
				nodesDisplay = nodesDisplay.item(indrec).getChildNodes();
				nodesControl = nodesControl.item(indrec).getChildNodes();
				nodesLink = nodesLink.item(indrec).getChildNodes();
				nodesDelivery = nodesDelivery.item(indrec).getChildNodes();
				nodesSearch = nodesSearch.item(indrec).getChildNodes();
				match = true;
				return true;
			} // end if
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