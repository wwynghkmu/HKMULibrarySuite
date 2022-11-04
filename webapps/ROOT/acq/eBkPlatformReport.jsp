<%@ page import="java.io.*,java.util.*, javax.servlet.*,org.apache.commons.io.comparator.*" %>
<%@ page import="javax.servlet.http.*,java.sql.*" %>
<%@include file="menu.jsp"%>
</html>
</head>
<%
if(authen!=null && !authen.isAuthenticated())
		response.sendRedirect("/acq/index.jsp?logout=yes");


	String cmd = request.getParameter("cmd");
	if(cmd==null)
		cmd="";
%>
<br>
<br>
<br>
<br>
<br>
<h3> <b> ACQ Past Reports</b> </h3>
<br>
<div align=center>
<form>
	Report by Date Range: <br>
	Form: <input id="from" type="text" name=from />
	To: <input id="to" type="text" name=to /> <br> <br>
	<input type=hidden name=cmd value=reportByDate>
	<input type=submit>
</form>

<script languaeg=javascript>
	const myDatePicker = MCDatepicker.create({ 
	      el: '#from',
	      dateFormat: 'YYYY-MM-DD',
	})
	const myDatePicker2 = MCDatepicker.create({ 
	      el: '#to',
	      dateFormat: 'YYYY-MM-DD',
	})
</script>
</div>
<br>
<br>
<%
Connection conn = null;
	Statement stmt = null;
	Class.forName(hk.edu.hkmu.lib.acq.Config.JDBC_DRIVER);
	conn = DriverManager.getConnection(hk.edu.hkmu.lib.acq.Config.DB_URL, hk.edu.hkmu.lib.acq.Config.USER, hk.edu.hkmu.lib.acq.Config.PASS);
        stmt = conn.createStatement();
	if(!cmd.equals("reportByDate")){
		Map<Integer, String> eBKPReports = new HashMap<Integer, String>();
		String sql = "SELECT DISTINCT batchID, SUBSTRING(timestamp, 1, 10) AS date from ebkplatformcheckingreport GROUP BY batchID, date ORDER BY batchID DESC, date DESC";
	       	ResultSet rs = stmt.executeQuery(sql);
        	while (rs.next()) {
	       	        String date = StringHandling.trimNewLineChar((rs.getString("date").trim()));
       		        int batchID = rs.getInt("batchID");
	eBKPReports.put(batchID, date);
	       	}
		int eBKPReportsSize = eBKPReports.size();
		int count = 0;
		ArrayList<Integer> eBKPReportsKeyList = new ArrayList<Integer>();
		eBKPReportsKeyList.addAll(eBKPReports.keySet());
	
		out.println("<table border=1 class='table table-hover'>");
		out.println("<thead><tr><td> <b> <div align=center> EBook Platform Checking Daily Reports </div> </b></td></tr></thead><tbody>");
		for(int i=eBKPReportsSize-1;i >= 0; i--){
	out.println("<tr>");
	out.println( "<td> <div align=center> <a target=_blank href='getEBKPlatformReport.jsp?batchID=" + eBKPReportsKeyList.get(i) + "'>");
	out.println(eBKPReportsKeyList.get(i));
	out.println(". ");
	out.println(  eBKPReports.get(eBKPReportsKeyList.get(i)));
	out.println("</a>");
	out.println("</div></td>");
	out.println("</tr>");
		} //end for
	} else {
		String from = request.getParameter("from");
		String to = request.getParameter("to");
		String sql = "SELECT * from ebkplatformcheckingreport where timestamp > '" + from + "' and timestamp < '" + to + "' order by timestamp DESC";
	       	ResultSet rs = stmt.executeQuery(sql);
		out.println("<div align=center> <b> Report Period From: </b> " + from);
		out.println("<b> To: </b>" + to);
		int success = 0;
		int fail = 0;
        	while (rs.next()) {
	       	        String date = StringHandling.trimNewLineChar((rs.getString("timestamp").trim()));
	       	        String platform = StringHandling.trimNewLineChar((rs.getString("platform").trim()));
	       	        String searchKeyword = StringHandling.trimNewLineChar((rs.getString("searchKeyword").trim()));
	       	        String checkStatus = StringHandling.trimNewLineChar((rs.getString("checkStatus").trim()));
	if(checkStatus.contains("SUCCESS"))
		success++;
	else
		fail++;

		}	
		out.println("<br>");
		out.println("<b> Success: </b>" + success);
		out.println("<b> <font color=red> Failed: </b>" + fail + " </font> <br>");
		out.println(" </div> ");
	}
	out.println("<tbody></table>");
        stmt.close();
        conn.close();
%>
</html>
