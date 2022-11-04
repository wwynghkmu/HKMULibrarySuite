<%@ page import="java.sql.*"%>
<%@ page import="java.util.*"%>
<%@ page import="java.text.*"%>
<%@ page import="java.io.*,java.util.*, javax.servlet.*"%>

<%@include file="menu.jsp"%>
<br>
<br>
<br>
<br>
<br>
<%
if(authen!=null && !authen.isAuthenticated())
		response.sendRedirect("/acq/index.jsp?logout=yes");

	String batchIDStr = request.getParameter("batchID");
	int batchID = Integer.parseInt(batchIDStr);
	Connection conn = null;
	Statement stmt = null;
	Class.forName(hk.edu.hkmu.lib.Config.JDBC_DRIVER);
	conn = DriverManager.getConnection(hk.edu.hkmu.lib.Config.DB_URL, hk.edu.hkmu.lib.Config.USER, hk.edu.hkmu.lib.Config.PASS);	
	stmt = conn.createStatement();
%>

<h3> <b> EBK Platform Checking Report</b> </h3>
<hr>

<%
	String sql = "SELECT * from ebkplatformcheckingreport where batchID='" + batchID + "' order by checkStatus, platform";
	stmt = conn.createStatement();
	ResultSet rs = stmt.executeQuery(sql);
	String html = "";
	int count=0;
	out.println("<div align=center>");
	while (rs.next()) {
		String timestamp = StringHandling.trimNewLineChar((rs.getString("timestamp").trim()));
		String date = timestamp;
		
		date = timestamp.replaceAll("\\d\\d.\\d\\d.\\d\\d$", "");
		date = date.trim();
		String platform = StringHandling.trimNewLineChar((rs.getString("platform").trim()));
		String keyword = StringHandling.trimNewLineChar((rs.getString("searchKeyword").trim()));
		String status = StringHandling.trimNewLineChar((rs.getString("checkStatus").trim()));
		String screenCaptureTime = StringHandling.trimNewLineChar((rs.getString("timestamp").trim()));
		screenCaptureTime = screenCaptureTime.replaceAll(":", "-");
		screenCaptureTime = screenCaptureTime.replaceAll(" ", "-");
		screenCaptureTime = screenCaptureTime.trim();
		String screenPath ="/acq/reports/eBkPlatformChecking/" + date + "-Batch-" + batchID + "/" + screenCaptureTime + "-" + platform + ".jpg";
		screenPath = screenPath.replaceAll(" ", "%20");
		screenPath = screenPath.replaceAll("&", "%26");
		html += "<b> " +  platform  + "</b>";
		html += "<br>";
		html += status ;
		html += "<br>";
		html += "Search Keyword - " + keyword ;
		html += "<br>";
		html += "Check Time  - " + screenCaptureTime ;
		html += " ";
		html += "<br>";
		if(!status.contains("FAILED"))
			html += "(Click to enlarge the image) <br>  <a target=_blank href=" + screenPath + "> <img height=30% src=" + screenPath + "> </a>";
		else
			html = html.replaceAll("FAILED", "<font color=red> <b> FAILED </b> </font>"); 
		html += "<hr>";
		count++;
	}
	out.println(html);
	out.println("</div>");
	stmt.close();
	conn.close();
%>

</body>
</html>
