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
        String webinfdir = request.getServletContext().getRealPath("/") + "/WEB-INF/classes/hk/edu/ouhk/lib/acq/";
        String configFilePath = basedir + "conf/eBookPlatformCheckingConfig.txt";
        String configWebFilePath = webinfdir + "eBookPlatformCheckingConfig.txt";
	String logFilePath = basedir + "logs/configLog.txt";
	if(cmd!=null){

		Connection conn = null;
		Statement stmt = null;
		Class.forName(hk.edu.hkmu.lib.Config.JDBC_DRIVER);
		conn = DriverManager.getConnection(hk.edu.hkmu.lib.Config.DB_URL, hk.edu.hkmu.lib.Config.USER, hk.edu.hkmu.lib.Config.PASS);	
		stmt = conn.createStatement();

		if(cmd.equals("update")){
	String configUpdate = "";
	int t=Integer.parseInt(tol);
	for(int i=0;i<t;i++){
		String configName = request.getParameter("configname_" + i);
		String configValue = request.getParameter(configName + i);
		String configDes = request.getParameter("des" + configName + i);
		configUpdate += configName + "~" +configValue + "~" + configDes; 
		if(i<=t-1)
	configUpdate += "\n";
	}
                        File file = new File(configFilePath);
                        file.delete();
                        Writer objWriter = new BufferedWriter(new FileWriter(configFilePath));
                        objWriter.write(configUpdate);
                        objWriter.flush();
                        objWriter.close(); 
                        file = new File(configWebFilePath);
                        objWriter = new BufferedWriter(new FileWriter(configWebFilePath));
                        objWriter.write(configUpdate);
                        objWriter.flush();
                        objWriter.close(); 
	out.println("<b> <font color=red> Configuration Updated </font> </b>");
                        BufferedWriter writer = new BufferedWriter(new FileWriter(logFilePath, true));
                        writer.write( new java.util.Date()  + "\t" + request.getRemoteAddr() + "\t" + authen.getUserid() + "\tACQ LIB Tools config updated: \n" + configUpdate + "\n\n");
                        writer.close();

	String ebkCountStr = request.getParameter("eBkPlatformCount");
	int ebkCount = Integer.parseInt(ebkCountStr);
	for(int i=0; i<ebkCount; i++){
		String platform = request.getParameter("eBkPlatformName" + i);
		String type = request.getParameter("eBkPlatformType" + i);
		String keyword = request.getParameter("eBkPlatformKeyword" + i);
		String link = request.getParameter("eBkPlatformLink" + i);
		String facet = request.getParameter("eBkPlatformFacet" + i);
		String sql;
		sql = "UPDATE configuration set value='" + keyword + "' where session='acq' and module='eBkPlatformChecking' and subName='primoKeyword' and name='" + platform + "'";
		stmt.executeUpdate(sql);
		sql = "UPDATE configuration set value='" + link + "' where session='acq' and module='eBkPlatformChecking' and subName='primoLink' and name='" + platform + "'";
		stmt.executeUpdate(sql);
		sql = "UPDATE configuration set value='" + facet + "' where session='acq' and module='eBkPlatformChecking' and subName='primoCreatorFacet' and name='" + platform + "'";
		stmt.executeUpdate(sql);
	}


	String platform = request.getParameter("eBkPlatformName");
	String type = request.getParameter("eBkPlatformType");
	String keyword = request.getParameter("eBkPlatformKeyword");
	String link = request.getParameter("eBkPlatformLink");
	String facet = request.getParameter("eBkPlatformFacet");
	if(!platform.equals("") && !type.equals("") && !keyword.equals("") && !link.equals("") && !facet.equals("")){
		String sql = "INSERT INTO configuration (session, module, name, subName, value) VALUES('acq', 'eBkPlatformChecking', '" 
		+ platform + "', 'platformName', '" + platform + "')";	
		stmt.executeUpdate(sql);

		sql = "INSERT INTO configuration (session, module, name, subName, value) VALUES('acq', 'eBkPlatformChecking', '" 
		+ platform + "', 'searchType', '" + type + "')";
		stmt.executeUpdate(sql);

		sql = "INSERT INTO configuration (session, module, name, subName, value) VALUES('acq', 'eBkPlatformChecking', '" 
		+ platform + "', 'primoLink', '" + link + "')";	
		stmt.executeUpdate(sql);

		sql = "INSERT INTO configuration (session, module, name, subName, value) VALUES('acq', 'eBkPlatformChecking', '" 
		+ platform + "', 'primoKeyword', '" + keyword + "')";	
		stmt.executeUpdate(sql);

		sql = "INSERT INTO configuration (session, module, name, subName, value) VALUES('acq', 'eBkPlatformChecking', '" 
		+ platform + "', 'primoCreatorFacet', '" + facet + "')";	
		stmt.executeUpdate(sql);
	}
		} else if(cmd.equals("delete")){
	String platform = request.getParameter("platform");
	String sql = "DELETE from configuration where session='acq' and module='eBkPlatformChecking' and name='" + platform + "'";
	stmt.executeUpdate(sql);
	out.println("<b> <font color=red> Record Deleted </font> </b>");
		}
		stmt.close();
		conn.close();
	}
%>

<hr>
<form method=post>
<table border=1 class="table table-hover">
<thead>
<tr>
      <td> <b> Platform </b> </td>
      <td> <b> Search Type </b> </td>
      <td> <b> Primo Search Keyword </b> </td>
      <td> <b> Primo Access Link Wording </b> </td>
      <td> <b> Primo Facet Wording </b> </td>

</tr>
</thead>
<%
String[][] platforms;
		HashMap<String, String[]> searchList;
		hk.edu.hkmu.lib.acq.Config.init();
		platforms = new String[hk.edu.hkmu.lib.acq.Config.PLATFORMS.size()][9];
		out.println(hk.edu.hkmu.lib.acq.Config.PLATFORMS.size());
                searchList = new HashMap<String, String[]>();
		int count = 0;
                for (Map.Entry<String, String[]> entry : hk.edu.hkmu.lib.acq.Config.PLATFORMS.entrySet()) {
                        String platformName = "";
                        String primoKeyword = "";
                        String primoLink = "";
                        String searchType = "";
                        String primoCreatorFacet = "";

                        platformName = entry.getValue()[1];
	out.println(platformName);
                        primoKeyword = entry.getValue()[2];
                        primoLink = entry.getValue()[3];
                        searchType = entry.getValue()[0];
                        primoCreatorFacet = entry.getValue()[4];

                        platforms[count][0] = searchType;
                        platforms[count][1] = platformName;
                        platforms[count][2] = primoKeyword;
                        platforms[count][3] = primoLink;
                        platforms[count][4] = primoCreatorFacet;
                        platforms[count][5] = "FAILED";
                        platforms[count][5] = "";

                        count++;

                        if (!searchType.toLowerCase().equals("keyword")) {
                                String[] list = new String[hk.edu.hkmu.lib.acq.Config.SEARCHITEMS.get(platformName).size()];
                                int count2 = 0;
                                for (String item : hk.edu.hkmu.lib.acq.Config.SEARCHITEMS.get(platformName)) {
                                        list[count2] = item;

                                        count2++;

                                }
                                searchList.put(platformName.toUpperCase(), list);

                        }

                }
%>
</table>
<div align=center>
<input type=submit value="Update Configuration">
<br> <br>
<table border=1 class="table table-hover">
<thead>
<tr>
      <td width=10%> <b> Platform </b> </td>
      <td width="8%"> <b> Search Type </b> </td>
      <td> <b> Primo Search Keyword </b> </td>
      <td> <b> Primo Access Link Wording </b> </td>
      <td> <b> Primo Facet Wording </b> </td>

</tr>
</thead>
<tr>
      <td> <b> <input name='eBkPlatformName' type=text> </b> </td>
      <td>
	<select name="eBkPlatformType">
	  <option value="keyword">Keyword</option>
	  <option value="eba">EBA</option>
	  <option value="isbn">isbn</option>
	</select>
      <td> <b> <input name='eBkPlatformKeyword' type=text> </b> </td>
      <td> <b> <input name='eBkPlatformLink' type=text> </b> </td>
      <td> <b> <input name='eBkPlatformFacet' type=text> </b> </td>

</tr>
</table>
<input type=submit value="Add Configuration">
<br> <br>
<table border=1 class="table table-hover">
<thead>
<tr>  <td> <b> Value </b> </td> <td> <b> Meaning </b> </td> </tr>
</thead>
<%



%>
</table>
<input type=hidden value=update name=cmd>
<input type=submit value="Update Configuration">
</form>
</div>
</body>
</html>
