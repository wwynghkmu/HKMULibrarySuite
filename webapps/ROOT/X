<%@page contentType="text/xml" pageEncoding="UTF-8"%>
<%@ page
	import="java.net.*,javax.xml.parsers.*,org.w3c.dom.*,org.xml.sax.*,java.util.*,java.util.Arrays,java.io.*,java.util.regex.*,java.net.URLEncoder,hk.edu.csids.bookquery.*,hk.edu.csids.*,org.apache.commons.lang3.*"%>
<%
	String server_adr = request.getParameter("server-adr");
	String doc_number = request.getParameter("doc-number");
	String doc_library = request.getParameter("doc-library");
	String inst = request.getParameter("library");
	String locate_base = request.getParameter("locate-base");
	GenStringHandling stringHandle = new GenStringHandling();
	locate_base = stringHandle.trimSpecialChars(locate_base);
	locate_base = stringHandle.normalizeString(locate_base);
	String urlStr = "http://" + server_adr + "/X?op=ill-get-doc&doc_number=" + doc_number + "&library="
			+ doc_library;
	String urlStr2 = "http://" + server_adr + "/X?op=ill-get-doc-short&doc_number=" + doc_number + "&library="
			+ doc_library;
	String outstr = "";

	String errMsg = "00";

	boolean z3950Search = false;
	if (inst.contains("Z3950")) {
		String tmp = inst;
		inst = locate_base;
		locate_base = inst;
		z3950Search = true;
	} //end if

	//Fetch Volume info
	String req_vol = "";

	try {
		URL url = new URL(urlStr2);
		URLConnection urlcon = url.openConnection();
		urlcon.setConnectTimeout(4000);
		BufferedReader buffread = new BufferedReader(new InputStreamReader(urlcon.getInputStream(), "UTF-8"));
		String inputLine;
		while ((inputLine = buffread.readLine()) != null)
			outstr += inputLine;
		buffread.close();

		if (outstr.length() < 300) {
			buffread = new BufferedReader(new InputStreamReader(urlcon.getInputStream(), "UTF-8"));
			while ((inputLine = buffread.readLine()) != null)
				outstr += inputLine;
			buffread.close();
		} //end if
	} //end try
	catch (Exception e) {
		StringWriter errors = new StringWriter();
		e.printStackTrace(new PrintWriter(errors));
		String errStr = "Z3950QueryByNonISBN:matchEdition()" + errors.toString();
		System.out.println(errStr);
		errMsg = errStr;
	} //end catch

	Pattern pattern = Pattern.compile("<z13u-user-defined-1>(.+?)</z13u-user-defined-1>");
	Matcher matcher = pattern.matcher(outstr);
	if (matcher.find()) {
		req_vol = matcher.group(1);
	} //end if

	outstr = "";

	try {
		URL url = new URL(urlStr);
		URLConnection urlcon = url.openConnection();
		urlcon.setConnectTimeout(4000);
		BufferedReader buffread = new BufferedReader(new InputStreamReader(urlcon.getInputStream(), "UTF-8"));
		String inputLine;
		while ((inputLine = buffread.readLine()) != null)
			outstr += inputLine;
		buffread.close();

		if (outstr.length() < 300) {
			buffread = new BufferedReader(new InputStreamReader(urlcon.getInputStream(), "UTF-8"));
			while ((inputLine = buffread.readLine()) != null)
				outstr += inputLine;
			buffread.close();
		} //end if
	} //end try
	catch (Exception e) {
		StringWriter errors = new StringWriter();
		e.printStackTrace(new PrintWriter(errors));
		String errStr = "Z3950QueryByNonISBN:matchEdition()" + errors.toString();
		System.out.println(errStr);
		errMsg = errStr;
	} //end catch

	String session_id = "";
	String req_title = "";
	String req_author = "";
	String req_publisher = "";
	String req_publishingyear = "";
	String req_isbn = "";
	String req_edition = "";
	String temp_str = "";
	String ans_bib_no_isbn = "000000000";
	String ans_bib_no_nonisbn = "000000000";
	String ans_ava_no_isbn = "000000000";
	String ans_ext_no_isbn = "000000000";
	String ans_loa_no_isbn = "000000000";
	String ans_ava_no_nonisbn = "000000000";
	String ans_ext_no_nonisbn = "000000000";
	String ans_loa_no_nonisbn = "000000000";
	outstr = outstr.replace(",", "");
	pattern = Pattern.compile("<session-id>(.+?)</session-id>");
	matcher = pattern.matcher(outstr);
	if (matcher.find()) {
		session_id = matcher.group(1);
	} //end if

	pattern = Pattern.compile("<datafield tag=\"245\".*?><subfield code=\"a\">(.+?)</subfield>");
	matcher = pattern.matcher(outstr);
	if (matcher.find()) {
		req_title = matcher.group(1);
		req_title = req_title.replaceAll("\\(Vol.:.*", "");
	} //end if

	pattern = Pattern.compile("<datafield tag=\"100\".*?><subfield code=\"a\">(.+?)</subfield></datafield>");
	matcher = pattern.matcher(outstr);
	if (matcher.find()) {
		req_author = matcher.group(1);
		req_author = req_author.trim();
	}

	pattern = Pattern.compile("<datafield tag=\"020\".*?><subfield code=\"a\">(.+?)</subfield>");
	matcher = pattern.matcher(outstr);
	if (matcher.find()) {
		if (req_vol == "") {
			req_isbn = matcher.group(1);
		} //end if
	} //end if

	pattern = Pattern.compile("<datafield tag=\"250\".*?><subfield code=\"a\">(.+?)</subfield>");
	matcher = pattern.matcher(outstr);
	if (matcher.find()) {
		req_edition = matcher.group(1);
	} //end if

	if (req_isbn == "") {
		pattern = Pattern.compile("<datafield tag=\"022\".*?><subfield code=\"a\">(.+?)</subfield>");
		matcher = pattern.matcher(outstr);
		if (matcher.find()) {
			if (req_vol == "") {
				req_isbn = matcher.group(1);
			} //end if
		} //end if
	} //end if

	temp_str = "";
	pattern = Pattern.compile("<datafield tag=\"260\".*?>(.+?)</datafield>");
	matcher = pattern.matcher(outstr);
	if (matcher.find()) {
		temp_str = matcher.group(1);
	} //end if
	pattern = Pattern.compile("<subfield code=\"b\">(.+?)</subfield>");
	matcher = pattern.matcher(temp_str);
	if (matcher.find()) {
		req_publisher = matcher.group(1);
		req_publisher = req_publisher.trim();
	} //end if

	temp_str = "";
	pattern = Pattern.compile("<datafield tag=\"260\".*?>(.+?)</datafield>");
	matcher = pattern.matcher(outstr);
	if (matcher.find()) {
		temp_str = matcher.group(1);
	} //end if
	pattern = Pattern.compile("<subfield code=\"c\">(.+?)</subfield>");
	matcher = pattern.matcher(temp_str);
	if (matcher.find()) {
		req_publishingyear = matcher.group(1);
	} //end if

	Query q;
	if (z3950Search) {
		q = new Z3950QueryByISBN(req_isbn, inst, req_vol);
	} else {
		q = new PrimoQueryByISBN(req_isbn, inst, req_vol);
	} //end if

	Query q2;
	if (z3950Search) {
		q2 = new Z3950QueryByNonISBN();
	} else {
		q2 = new PrimoQueryByNonISBN();
	} //end if

	if (q.match()) {
		ans_bib_no_isbn = "000000001";
		if (q.isAva()) {
			ans_ext_no_isbn = "00000000" + q.getAva_itm_no();
			ans_ava_no_isbn = "00000000" + q.getAva_itm_no();
			ans_loa_no_isbn = ans_ava_no_isbn;
		} //end if 
	} else {
		if (z3950Search) {
			q2 = new Z3950QueryByNonISBN(req_author, req_title, req_publisher, req_publishingyear, req_edition,
					req_vol, inst);
		} else {
			q2 = new PrimoQueryByNonISBN(req_author, req_title, req_publisher, req_publishingyear, req_edition,
					req_vol, inst);
		} //end if
		if (q2.match()) {
			ans_bib_no_nonisbn = "000000001";
			if (q2.isAva()) {
				ans_ext_no_nonisbn = "00000000" + q2.getAva_itm_no();
				ans_ava_no_nonisbn = "00000000" + q2.getAva_itm_no();
				ans_loa_no_nonisbn = ans_ava_no_nonisbn;
			} //end if
		} else {
			ans_bib_no_nonisbn = "000000000";
			ans_ava_no_nonisbn = "000000000";
		} //end if
	} //end if

	String outxml = "<?xml version = \"1.0\" encoding = \"UTF-8\"?>" + "<ill-in-loc-preview>";

	if (q.match()) {
		outxml += "<line>" + "<locate-code>" + locate_base + "</locate-code>" + "<locate-request>isxn= ("
				+ req_isbn + ")</locate-request>" + "<no-docs>" + ans_bib_no_isbn + "</no-docs>"
				+ "<exist-items>" + ans_ext_no_isbn + "</exist-items>" + "<avail-items>" + ans_ava_no_isbn
				+ "</avail-items>" + "<loanable-items>" + ans_loa_no_isbn + "</loanable-items>"
				+ "<set-number>000002</set-number>" + "<bib-library></bib-library>"
				+ "<bib-doc-number>000000000</bib-doc-number>" + "<locate-error-code>" + errMsg
				+ "</locate-error-code>" + "</line>";
	} //end if

	if (!q.match()) {
		outxml += "<line>" + "<locate-code>" + locate_base + "-1</locate-code>" + "<locate-request>wti=("
				+ req_title + ") and  wau=(" + req_author + ") and  wpu=(" + req_publisher + ") and  wyr=("
				+ req_publishingyear + ") and  wed=(" + req_edition + ") and  wev=(" + req_vol
				+ ")</locate-request>" + "<no-docs>" + ans_bib_no_nonisbn + "</no-docs>" + "<exist-items>"
				+ ans_ext_no_nonisbn + "</exist-items>" + "<avail-items>" + ans_ava_no_nonisbn
				+ "</avail-items>" + "<loanable-items>" + ans_loa_no_nonisbn + "</loanable-items>"
				+ "<set-number>000001</set-number>" + "<bib-library></bib-library>"
				+ "<bib-doc-number>000000000</bib-doc-number>" + "<locate-error-code>" + errMsg
				+ "</locate-error-code>" + "</line>";
	} //end if

	outxml += "<session-id>" + session_id + "</session-id>" + "</ill-in-loc-preview>";
%><%=outxml%>
