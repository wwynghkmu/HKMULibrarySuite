<%@ page import="hk.edu.csids.cat.*,hk.edu.csids.*"%>
<%@ page import="hk.edu.csids.bookquery.*"%>
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
<h3> <b> ACQ New Additional List by School - Email Configuration</b> </h3>
<%
	if(authen!=null && !authen.isAuthenticated())
		response.sendRedirect("/acq/index.jsp?logout=yes");

	String cmd = request.getParameter("cmd");
	String tol = request.getParameter("tol");
        String basedir = request.getServletContext().getRealPath("/") + "acq/";
        String webinfdir = request.getServletContext().getRealPath("/") + "/WEB-INF/classes/hk/edu/ouhk/lib/acq/";
        String configFilePath = basedir + "conf/addListConfig.txt";
        String configWebFilePath = webinfdir + "addListConfig.txt";
	String logFilePath = basedir + "logs/configLog.txt";
	if(cmd!=null){

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
		}
	}

	
%>

<hr>
<div align=center>
<form>
<table border=1 class="table table-hover">
<thead>
<tr>  <td> <b> Value </b> </td> <td> <b> Meaning </b> </td> </tr>
</thead>
<%
                BufferedReader br = new BufferedReader(new FileReader(configFilePath));
                String line = ""; 
                String sch[] = null;
		int count=0;
                while((line = br.readLine()) != null){
                        String arry[] = line.split("~");
			String html = "";
			arry[0] = arry[0].trim();
			arry[1] = arry[1].trim();
			arry[2] = arry[2].trim();
			if(!arry[0].contains("DEFAULT_")){
				html = "<tr>  <td>";
				html += "<input name=" + arry[0] + count + " size=100% type=text value='" + arry[1] + "'>";
				html += "<input type=hidden name=configname_" + count + " value='" + arry[0] + "'> </td> <td> ";
				html += "<input type=hidden name=des" + arry[0] + count + " value='" + arry[2] + "'>";
				html += arry[2] + "</td> </tr>";
			}

			out.println(html);
			count++;
                }

%>
</table>

<input type=hidden value=update name=cmd>
<input type=hidden name=tol value="<%=count%>">
<input type=submit value="Update Configuration">
</form>
</div>
</body>
</html>
