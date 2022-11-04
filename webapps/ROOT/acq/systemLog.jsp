<%@ page import="java.io.*,java.util.*, javax.servlet.*,org.apache.commons.io.comparator.*" %>
<%@ page import="javax.servlet.http.*" %>
<%@include file="menu.jsp"%>
</html>
</head>
<%
	String filedir = request.getServletContext().getRealPath("/") + "acq/logs";
%>
<br>
<br>
<br>
<br>
<br>
<h3> <b> ACQ Module System Logs</b> </h3>
<%
	if(authen!=null && !authen.isAuthenticated())
        	response.sendRedirect("/acq/index.jsp?logout=yes");
	File directoryLog = new File(filedir);
	File[] listLog = directoryLog.listFiles();
	Arrays.sort(listLog, LastModifiedFileComparator.LASTMODIFIED_REVERSE);
	int lenght = listLog.length; 
	out.println("<table border=1 class='table table-hover'>");
	out.println("<thead><tr><td> <b> System Logs of ACQ LIB Tools</b> </td> <td> <b> </b></td></tr></thead><tbody>");
	for(int i=0;i <lenght; i++){
		out.println("<tr>");
		out.println("<td align=left>");
		out.println("<a href='logs/" + listLog[i].getName() + "' target='_blank'>" + listLog[i].getName() + "</a>");
		out.println("</tr>");
	} //end for
	out.println("<tbody></table>");

%>
</html>
