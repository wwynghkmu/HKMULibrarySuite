<%@ page import="java.io.*,java.util.*, javax.servlet.*" %>
<%@ page import="javax.servlet.http.*" %>
<%
String cmd = request.getParameter("cmd");
	String type = request.getParameter("type");
	if(type==null)
		type="";
	String file = request.getParameter("file");
	String filedirsubjectSearchPolyU = request.getServletContext().getRealPath("/") + "acq/reports/subjectSearchPolyU";
	File f = new File(filedirsubjectSearchPolyU + "/" + file); 
	if(cmd!=null && cmd.contains("delete")){
		f = new File(filedirsubjectSearchPolyU + "/" + file);
		String completedFile = f.getName();
		completedFile = completedFile.replaceAll("-s.*$", "");
		completedFile = completedFile.replaceAll("-o.*$", "");
		completedFile = completedFile.replaceAll("-S.*$", "");
		completedFile = completedFile.trim();
		completedFile += "-Completed.txt";
		f.delete();

		f = new File(filedirsubjectSearchPolyU + "/" + completedFile);
		if(f.exists())
	f.delete();

		out.println("File '" + file + "' deleted <br>");
	} //end
%>
<html>
<head>
<title> Reports </title>
</head>
<%@include file="menu.jsp"%>
<br>
<br>
<br>
<br>
<br>
<h3> <b> ACQ Report</b> </h3>
<br>
<%
	File directorysubjectSearchPolyU = new File(filedirsubjectSearchPolyU);
	File[] listcat = directorysubjectSearchPolyU.listFiles();
	Arrays.sort(listcat, Collections.reverseOrder());
	String lastFileHeadercat = "";
	int lenght = listcat.length;
	out.println("<table border=1>");
	out.println("<tr><td> <b> ACQ search report PolyU by subject  </b> </td> </tr>");
	for(int i=0;i <lenght; i++){
		out.println("<tr>");
		if(i<listcat.length && !listcat[i].getName().contains("Completed") && !listcat[i].getName().equals("copycat")){
			String nowFileHeader = listcat[i].getName();
			String status = "";
			nowFileHeader = nowFileHeader.replaceAll("-O.*$", "");
			nowFileHeader = nowFileHeader.trim();
			String fileForm = nowFileHeader;
			out.println("<td align=right>");
                        try{
                                File completedFile = new File(filedirsubjectSearchPolyU + "/" + fileForm + "-Completed.txt");
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
			out.println("<a href='reports/subjectSearchPolyU/" + listcat[i].getName() + "' target='_blank'>" + listcat[i].getName() + "</a>");
			out.println(" <a href='?type=cat&cmd=delete&file=" + listcat[i].getName() + "'> [delete] </a> <br>");
			lastFileHeadercat = listcat[i].getName();
			lastFileHeadercat = lastFileHeadercat.replaceAll("-s.*$", "");
			lastFileHeadercat = lastFileHeadercat.replaceAll("-o.*$", "");
			lastFileHeadercat = lastFileHeadercat.trim();
			out.println("</td>");
		} //end if
		out.println("</tr>");
	} //end for
	out.println("</table>");

%>
</html>
