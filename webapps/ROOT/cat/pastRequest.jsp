<%@ page import="java.io.*,java.util.*, javax.servlet.*" %>
<%@ page import="javax.servlet.http.*" %>
<%@include file="menu.jsp"%>
<%
        if(authen!=null && !authen.isAuthenticated())
                response.sendRedirect("/acq/index.jsp?logout=yes");
	String cmd = request.getParameter("cmd");
	String file = request.getParameter("file");
	String type = request.getParameter("type");
	String filedircopycat = request.getServletContext().getRealPath("/") + "cat/requests/copycat";
	String filedirsfxenrich = request.getServletContext().getRealPath("/") + "cat/requests/sfxenrich";
        String basedir = request.getServletContext().getRealPath("/") + "cat/";
        String logFilePath = basedir + "logs/reportLog.txt";
	File f = null;


	if(cmd!=null && cmd.contains("delete")){
		if(type.contains("copycat"))
			f = new File(filedircopycat + "/" + file);
		if(type.equals("sfxenrich"))
			f = new File(filedirsfxenrich + "/" + file);
		f.delete();
		out.println("File '" + file + "' deleted <br>");
                BufferedWriter writer = new BufferedWriter(new FileWriter(logFilePath, true));
                writer.write( new java.util.Date()  + "\t" + request.getRemoteAddr() + "\t" + authen.getUserid() + "\tRequest deleted:" + file + "\n");
                writer.close();
	} //end 
%>
<br>
<br>
<br>
<br>
<br>
<h3> <b> Past Requests of CAT Tools </b> </h3>	
<table border=1 class="table table-hover">
<thead><tr><td><b> CopyCat Requests </b> </td> <td> <b> SFX Enrichment Requests </b> </td></tr></thead>
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
				out.println("&nbsp <b> <a href='?type=copycat&cmd=delete&file=" + listcat[i].getName() + "'> [DELETE]</a> </b> <br>");
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
				out.println("&nbsp; <b> <a href='?type=sfxenrich&cmd=delete&file=" + listsfx[i].getName() + "'> [DELETE]</a> </b> <br>");
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
