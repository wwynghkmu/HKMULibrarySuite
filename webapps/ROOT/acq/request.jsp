<%@ page import="java.io.*,java.util.*, javax.servlet.*" %>
<%@ page import="javax.servlet.http.*" %>
<%
String cmd = request.getParameter("cmd");
	String file = request.getParameter("file");
	String type = request.getParameter("type");
	String filedircopycat = request.getServletContext().getRealPath("/") + "copycat/requests/copycat";
	String filedirsfxenrich = request.getServletContext().getRealPath("/") + "copycat/requests/sfxenrich";
	File f = null;


	if(cmd!=null && cmd.contains("delete")){
		if(type.contains("copycat"))
	f = new File(filedircopycat + "/" + file);
		if(type.equals("sfxenrich"))
	f = new File(filedirsfxenrich + "/" + file);
		f.delete();
		out.println("File '" + file + "' deleted <br>");
	} //end
%>
<html>
<head>
<title> CopyCAT Requests </title>
</head>
<%@include file="menu.jsp"%>
<table border=1>
<tr><td><b> CopyCat Requests </b> </td> <td> <b> SFX Enrichment Requests </b> </td></tr>
<%
	File directorycat = new File(filedircopycat);
	File[] listcat = directorycat.listFiles();
        Arrays.sort(listcat, Collections.reverseOrder());

	File directorysfx = new File(filedirsfxenrich);
	File[] listsfx = directorysfx.listFiles();
        Arrays.sort(listsfx, Collections.reverseOrder());

	if(listcat!=null || listsfx!=null){
	int length = (listcat.length > listsfx.length) ? listcat.length : listsfx.length;
	for(int i=0;i <length; i++){
		out.println("<tr>");
		if(i<listcat.length){
			if(!listcat[i].getName().contains("sample")){
				out.println("<td>");
				out.println("<a href='requests/copycat/" + listcat[i].getName() + "' target='_blank'>" + listcat[i].getName() + "</a>");
				out.println(" <a href='?type=copycat&cmd=delete&file=" + listcat[i].getName() + "'> [delete] </a> <br>");
				out.println("</td>");
			}else{
				out.println("<td></td>");
			} //end if
		} //end if
		if(i<listsfx.length){
			if(!listsfx[i].getName().contains("sample")){
				if(i >= listcat.length){
					out.println("<td>");
					out.println("</td>");
				} //end if
				out.println("<td>");
				out.println("<a href='requests/sfxenrich/" + listsfx[i].getName() + "' target='_blank'>" + listsfx[i].getName() + "</a>");
				out.println(" <a href='?type=sfxenrich&cmd=delete&file=" + listsfx[i].getName() + "'> [delete] </a> <br>");
				out.println("</td>");
			}else{
				out.println("<td></td>");
			} //end if
		} //end if
		out.println("</tr>");
	} //end for
	} //end if
%>
</table>
</html>
