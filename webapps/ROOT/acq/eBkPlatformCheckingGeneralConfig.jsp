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

	String cmd = request.getParameter("cmd");
	String tol = request.getParameter("tol");
        String basedir = request.getServletContext().getRealPath("/") + "acq/";
	String logFilePath = basedir + "logs/configLog.txt";
	if(cmd!=null){

		Connection conn = null;
		Statement stmt = null;
		Class.forName(hk.edu.hkmu.lib.Config.JDBC_DRIVER);
		conn = DriverManager.getConnection(hk.edu.hkmu.lib.Config.DB_URL, hk.edu.hkmu.lib.Config.USER, hk.edu.hkmu.lib.Config.PASS);	
		stmt = conn.createStatement();
                BufferedWriter writer = new BufferedWriter(new FileWriter(logFilePath, true));

		if(cmd.equals("update")){
	String configUpdate = "";
	int t=Integer.parseInt(tol);
	for(int i=0; i<t; i++){
		String value = "";
		value = request.getParameter("value" + i);
		String key = "";
		key = request.getParameter("key" + i);
		String sql = "";
		sql = "UPDATE configuration set value='" + value + "' where session='acq' and module='root' and name='" + key + "'";
		stmt.executeUpdate(sql);
                	        writer.write( new java.util.Date()  + "\t" + request.getRemoteAddr() + "\t" + authen.getUserid() + "\tACQ LIB Tools config updated: \n" + sql + "\n\n");
		out.println("<b> <font color=red> Configuration Updated </font> </b>");
	}
		}
		stmt.close();
		conn.close();
                writer.close();
	}
%>

<hr>
<form method=post>
<table border=1 class="table table-hover">
<thead>
<tr>
      <td> <b> Value </b> </td>
      <td> <b> Meaning </b> </td>

</tr>
</thead>
<%
String[][] platforms;
		HashMap<String, String[]> searchList;
		hk.edu.hkmu.lib.acq.Config.init();
		platforms = new String[hk.edu.hkmu.lib.acq.Config.PLATFORMS.size()][9];
		searchList = new HashMap<String, String[]>();
		int count = 0;
		String html = "";
		for (Map.Entry entry : hk.edu.hkmu.lib.acq.Config.VALUES.entrySet()) {
	String key = (String) entry.getKey();
	String value = (String) entry.getValue();
	if(key.contains("eBkPlatform")){
		html += "<tr> <td>";
		html += "<input type=text value='" + value + "' size=70 name='value" + count + "' >";
		html += "<input type=hidden value='" + key + "' size=70 name='key" + count + "'>";
		html += "</td> <td>";
		html += hk.edu.hkmu.lib.acq.Config.VALUESDES.get(key);
		html += "</td> <tr>";
		count++;
	}
		}
		out.println(html);
%>
</table>
<div align=center>
<input type=submit value="Update Configuration">
<br> <br>
<input type=hidden name=tol value="<%=count%>">
<input type=hidden name=cmd value="update">
</form>
</div>
</body>
</html>
