<%@ page import="java.io.*,java.util.*, javax.servlet.*,org.apache.commons.io.comparator.*" %>
<%@ page import="javax.servlet.http.*" %>
<%@include file="menu.jsp"%>
</html>
</head>
<%
String filedir = request.getServletContext().getRealPath("/") + "/logs";
%>
<br>
<br>
<br>
<br>
<br>
<h3> <b>TS Services Tools System Logs</b> </h3>
<%
hk.edu.hkmu.lib.Config.init();
        String fileKey = (String) hk.edu.hkmu.lib.Config.VALUES.get("FILEPROTECTIONKEY");
	if(authen!=null && !authen.isAuthenticated())
        	response.sendRedirect("/index.jsp?logout=yes");
	File directoryLog = new File(filedir);
	File[] listLog = directoryLog.listFiles();
	Arrays.sort(listLog, LastModifiedFileComparator.LASTMODIFIED_REVERSE);
	int lenght = listLog.length; 
	out.println("<table border=1 class='table table-hover'>");
	out.println("<thead><tr><td> <b> System Logs of LIB TSServices Tools</b> </td> <td> <b> </b></td></tr></thead><tbody>");
	for(int i=0;i <lenght; i++){
		out.println("<tr>");
		out.println("<td align=left>");
		out.println("<a href='getFile.jsp?loc=LOGPATH&file=" + listLog[i].getName().replaceAll(fileKey + "-", "") + "' target='_blank'>" + listLog[i].getName().replaceAll(fileKey + "-", "") + "</a>");
		out.println("</tr>");
	} //end for
	out.println("<tbody></table>");
%>
</html>
