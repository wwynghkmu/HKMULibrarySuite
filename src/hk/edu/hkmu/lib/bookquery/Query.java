package hk.edu.hkmu.lib.bookquery;

import java.util.ArrayList;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import hk.edu.hkmu.lib.*;

/**
 * This abstract class defines the common variables and methods used by the
 * classes which search over Primo via X-service or via Z39.50.
 * 
 * @author Wai-yan NG
 * @author wwyng@ouhk.edu.hk
 * @version 1.0
 * @since 2017
 */
public abstract class Query {

	public BookItem bk;
	public BookItem nextBk;
	public BookItem queryBk;
	protected boolean match;
	protected boolean ava;
	protected int bib_no;
	protected int ext_itm_no;
	protected int ava_itm_no;
	protected String errMsg;
	protected String queryStr;
	protected StringHandling strHandle = new StringHandling();
	protected CJKStringHandling cjkStrHandle = new CJKStringHandling();
	public String debug = "";

	/*
	 * "nodesRecord" holds result PNX records each node of which represents a PNX
	 * record. For others, ie. "nodes...", types are to hold the sections tags of
	 * resulted PNX records.
	 */

	protected NodeList nodesRecord;
	protected NodeList nodesFrbrRecord;
	protected NodeList nodesDisplay;
	protected NodeList nodesControl;
	protected NodeList nodesLink;
	protected NodeList nodesSearch;
	protected NodeList nodesDelivery;
	protected NodeList nodesFacet;

	/*
	 * The following nodes are for ILS XML AVA results
	 */
	protected NodeList nodesHolding;
	protected NodeList nodesHoldings;

	abstract boolean query();

	abstract boolean remoteQuery(String qstr);

	public Query() {
		clearQuery();
	} // end PrimoQuery

	public Query(String inst) {
		inst = inst.toUpperCase();
		Config.init(inst);
		clearQuery();
	} // end PrimoQuery()

	public String getQuerySetting() {
		return Config.QUERY_SETTING + "(" + Config.VALUES.get("ILS_AVA_BASE") + ")";
	} // end getQuerySetting()

	public String getErrMsg() {
		if (!strHandle.hasSomething(errMsg)) {
			return "NONE";
		} // end if
		return errMsg;
	} // end getErrMsg()

	protected void clearQuery() {
		errMsg = "";
		match = false;
		ava = false;
		bib_no = 0;
		ext_itm_no = 0;
		ava_itm_no = 0;
		bk = new BookItem();
		nextBk = new BookItem();
		queryBk = new BookItem();
	} // end clearQuery()

	protected String getNodeValue(String t, NodeList n) {
		for (int x = 0; x < n.getLength(); x++) {
			Node node = n.item(x);
			if (node.getNodeName().equalsIgnoreCase(t)) {
				return node.getTextContent();
			} // end if
		} // end for
		return "";
	} // end getNodeValue

	protected ArrayList<String> getNodeValues(String t, NodeList n) {
		ArrayList<String> al = new ArrayList<String>();
		al.clear();
		for (int x = 0; x < n.getLength(); x++) {
			Node node = n.item(x);
			if (node.getNodeName().equalsIgnoreCase(t)) {
				al.add(node.getTextContent());
			} // end if
		} // end for
		return al;
	} // end getNodeValues

	public boolean match() {
		return match;
	} // end match()

	public boolean isAva() {
		return ava;
	} // end isAva()

	public int getBib_no() {
		return bib_no;
	} // getBig_no()

	public int getExt_itm_no() {
		return ext_itm_no;
	} // getExt_itm_no()

	public int getAva_itm_no() {
		return ava_itm_no;
	} // end getAva_itm_no()

	public String getQueryStr() {
		return queryStr;
	} // end getQueryStr()

} // end class PrimoQuery