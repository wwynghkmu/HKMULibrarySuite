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
<h3> <b> EBK Platform Checking Configuration</b> </h3>
<%
if(authen!=null && !authen.isAuthenticated())
		response.sendRedirect("/acq/index.jsp?logout=yes");

	Connection conn = null;
	Statement stmt = null;
	Class.forName(hk.edu.hkmu.lib.Config.JDBC_DRIVER);
	conn = DriverManager.getConnection(hk.edu.hkmu.lib.Config.DB_URL, hk.edu.hkmu.lib.Config.USER, hk.edu.hkmu.lib.Config.PASS);	
	stmt = conn.createStatement();

	String cmd = request.getParameter("cmd");
	String tol = request.getParameter("tol");
        String basedir = request.getServletContext().getRealPath("/") + "acq/";
        String webinfdir = request.getServletContext().getRealPath("/") + "/WEB-INF/classes/hk/edu/ouhk/lib/acq/";
        String configFilePath = basedir + "conf/eBookPlatformCheckingConfig.txt";
        String configWebFilePath = webinfdir + "eBookPlatformCheckingConfig.txt";
	String logFilePath = basedir + "logs/configLog.txt";
	String platform = "";
	platform = request.getParameter("platform");
	if(cmd!=null){

		if(cmd.equals("add")){

	platform = request.getParameter("platform");
	String item = request.getParameter("item");
	String sql = "INSERT INTO ebkplatformsearchitem(platformName, searchItem) VALUES('" + platform + "', '" + item + "' )";
	stmt.executeUpdate(sql);
	out.println("<b> <font color=red> Record Added </font> </b>");
		} else if(cmd.equals("delete")){
	platform = request.getParameter("platform");
	String item = request.getParameter("item");
	String sql = "DELETE from ebkplatformsearchitem  where platformName='" + platform + "' and searchItem='" + item + "'";
	stmt.executeUpdate(sql);
	out.println("<b> <font color=red> Record Deleted </font> </b>");
		}
	}
%>

<hr>
<div align=center>
<form>
<table border=1 class="table table-hover">
<thead>
<tr>
      <td> <b>   <%=platform%>  Search Item </b> </td>

</tr>
</thead>

<%
	String sql = "SELECT * from ebkplatformsearchitem where platformName='" + platform + "'";
	stmt = conn.createStatement();
	ResultSet rs = stmt.executeQuery(sql);
	String html = "";
	int count=0;
	while (rs.next()) {
		String searchItem = StringHandling.trimNewLineChar((rs.getString("searchItem").trim()));
		html += "<tr> <td> <font size=2>";
		html += searchItem ;
		html += "</font> <a href='?cmd=delete&item=" + searchItem + "&platform=" + platform + "'> [Delete] </a> </td> </tr>";
		count++;
	}
	html += "<input type=hidden name=count value=" + count + ">";
	html += "<input type=hidden name=platform value='" + platform + "'>";
	out.println(html);
	stmt.close();
	conn.close();
%>

</table>
<br> <br>
<table border=1 class="table table-hover">
<thead>
<tr>
      <td> <b> Search Item </b> </td>

</tr>
</thead>
<tr>
      <td> <b> <input name='item' type=text> </b> </td>

</tr>
</table>
<input type=submit value="Add Configuration">
<br> <br>
<input type=hidden value=add name=cmd>
</form>
</div>
</body>
</html>
