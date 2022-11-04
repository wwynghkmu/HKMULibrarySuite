<%@ page import="java.io.*,java.util.*, javax.servlet.*,org.apache.commons.io.comparator.*" %>
<%@ page import="javax.servlet.http.*" %>
<%@include file="menu.jsp"%>
<script lanuage="javascript">

	function restartQuery(filehead){
                var xhttp = new XMLHttpRequest();
                xhttp.onreadystatechange = function() {
                        if (xhttp.readyState == 4 && xhttp.status == 200) {
                                alert(xhttp.responseText);
                        } //end if
                } //end function();
                xhttp.open("GET", "getsfxenrich.jsp?cmd=restart&file=" + filehead, true);
		xhttp.send();
		alert("Query restarted " + filehead);
	}
</script>
</html>
</head>
<%
        if(authen!=null && !authen.isAuthenticated())
                response.sendRedirect("/acq/index.jsp?logout=yes");
	String basedir = request.getServletContext().getRealPath("/") + "cat/";
	String logFilePath = basedir + "logs/reportLog.txt";
	String cmd = request.getParameter("cmd");
	String type = request.getParameter("type");
	if(type==null)
		type="";
	String file = request.getParameter("file");
	String filedircopycat = request.getServletContext().getRealPath("/") + "cat/reports/copycat";
	String filedirsfx = request.getServletContext().getRealPath("/") + "cat/reports/sfxenrich";
	File f = new File(filedircopycat + "/" + file); 
	if(cmd!=null && cmd.contains("delete")){

		if(type.equals("cat"))
			f = new File(filedircopycat + "/" + file);
		if(type.equals("sfx"))
			f = new File(filedirsfx + "/" + file);

		String completedFile = f.getName();
		completedFile = completedFile.replaceAll("-s.*$", "");
		completedFile = completedFile.replaceAll("-o.*$", "");
		completedFile = completedFile.replaceAll("-S.*$", "");
		completedFile = completedFile.trim();
		completedFile += "-Completed.txt";
		f.delete();

		if(type.equals("cat"))
			f = new File(filedircopycat + "/" + completedFile);
		if(type.equals("sfx"))
			f = new File(filedirsfx + "/" + completedFile);
		if(f.exists())
			f.delete();

		out.println("File '" + file + "' deleted <br>");
		BufferedWriter writer = new BufferedWriter(new FileWriter(logFilePath, true));
		writer.write( new java.util.Date()  + "\t" + request.getRemoteAddr() + "\t" + authen.getUserid() + "\tReport deleted:" + file + "\n");
		writer.close();
	} //end 
%>
<br>
<br>
<br>
<br>
<br>
<h3> <b> Reports</b> </h3>
<%
	File directorycat = new File(filedircopycat);
	File[] listcat = directorycat.listFiles();
	Arrays.sort(listcat, Collections.reverseOrder());
	File directorysfx = new File(filedirsfx);
	File[] listsfx = directorysfx.listFiles();
	Arrays.sort(listsfx, Collections.reverseOrder());
	String lastFileHeadercat = "";
	String lastFileHeadersfx = "";
	int lenght = (listcat.length > listsfx.length) ? listcat.length : listsfx.length; 
	out.println("<table border=1 class='table table-hover'>");
	out.println("<thead><tr><td> <b> CopyCAT Reports </b> </td> <td> <b> SFX Enrichment Reports </b></td></tr></thead><tbody>");
	for(int i=0;i <lenght; i++){
		out.println("<tr>");
		if(i<listcat.length && !listcat[i].getName().contains("Completed") && !listcat[i].getName().equals("copycat")){
			String nowFileHeader = listcat[i].getName();
			String status = "";
			nowFileHeader = nowFileHeader.replaceAll("-s.*$", "");
			nowFileHeader = nowFileHeader.replaceAll("-o.*$", "");
			nowFileHeader = nowFileHeader.trim();
			String fileForm = nowFileHeader;
			out.println("<td align=right>");
                        try{
                                File completedFile = new File(filedircopycat + "/" + fileForm + "-Completed.txt");
                                FileInputStream fileinputstream = new FileInputStream(completedFile);
                                int numberBytes = fileinputstream.available();
                                byte bytearray[] = new byte[numberBytes];
                                fileinputstream.read(bytearray);
				String isCompleted = new String(bytearray);
                                if(isCompleted.contains("YES"))
                                        status = "Completed";
				else
					status = "In progress";
                                fileinputstream.close();
                        }   
                        catch(Exception e){}
			if(!lastFileHeadercat.equals(nowFileHeader)){
				out.println(status + " Report: " + nowFileHeader + ":");
			}
			out.println("<a href='reports/copycat/" + listcat[i].getName() + "' target='_blank'>" + listcat[i].getName() + "</a>");
			out.println(" &nbsp; <b> <a href='?type=cat&cmd=delete&file=" + listcat[i].getName() + "'> [DELETE]</a> </b> <br>");
			lastFileHeadercat = listcat[i].getName();
			lastFileHeadercat = lastFileHeadercat.replaceAll("-s.*$", "");
			lastFileHeadercat = lastFileHeadercat.replaceAll("-o.*$", "");
			lastFileHeadercat = lastFileHeadercat.trim();
			out.println("</td>");
		} //end if
		if(i<listcat.length && listcat[i].getName().contains("Completed")){
			out.println("<td></td>");
		} //end if
		if(i<listsfx.length && !listsfx[i].getName().contains("Completed") && !listsfx[i].getName().equals("sfxenrich")){
			if(i>=listcat.length)
				out.println("<td></td>");
			String nowFileHeader = listsfx[i].getName();
			String status = "";
			nowFileHeader = nowFileHeader.replaceAll("-S.*$", "");
			nowFileHeader = nowFileHeader.trim();
			String fileForm = nowFileHeader;
			out.println("<td align=right> ");
                        try{
                                File completedFile = new File(filedirsfx + "/" + fileForm + "-Completed.txt");
                                FileInputStream fileinputstream = new FileInputStream(completedFile);
                                int numberBytes = fileinputstream.available();
                                byte bytearray[] = new byte[numberBytes];
                                fileinputstream.read(bytearray);
				String isCompleted = new String(bytearray);
                                if(isCompleted.contains("YES"))
                                        status = "Completed";
				else
					status = "In progress";
                                fileinputstream.close();
                        }   
                        catch(Exception e){}
			if(!lastFileHeadersfx.equals(nowFileHeader)){
				out.println(status + " Report: " + nowFileHeader + ":");
			}
			out.println("<a href='reports/sfxenrich/" + listsfx[i].getName() + "' target='_blank'>" + listsfx[i].getName()   + "</a>");
			out.println(" &nbsp; <b> <a href='?type=sfx&cmd=delete&file=" + listsfx[i].getName() + "'> [DELETE]</a> </b>");
			if(status.equals("In progress") && listsfx[i].getName().contains("SFXout.txt") )
				out.println("<b> <a href='javascript:restartQuery(\"" + nowFileHeader + "\")'> [RESTART]</a> </b> <br>");
			lastFileHeadersfx = listsfx[i].getName();
			lastFileHeadersfx = lastFileHeadersfx.replaceAll("-S.*$", "");
			lastFileHeadersfx = lastFileHeadersfx.trim();
			out.println("</td>");
		} //end if
		out.println("</tr>");
	} //end for
	out.println("<tbody></table>");

%>
</html>
