<%@ page import="java.io.*,java.util.*, javax.servlet.*,org.apache.commons.io.comparator.*" %>
<%@ page import="javax.servlet.http.*" %>
<%@include file="menu.jsp"%>
</head>
<body>
<%

        String basedir = request.getServletContext().getRealPath("/") + "ser/";
        String logFilePath = basedir + "logs/reportLog.txt";
	String cmd = request.getParameter("cmd");
	String type = request.getParameter("type");
	String file = request.getParameter("file");
	String fileddir1stReport = basedir + "/reports/urlReports";
	String filedir2ndReport = basedir + "/reports/asrReports";

	File f = new File(fileddir1stReport + "/" + file); 
	if(cmd!=null && cmd.contains("delete")){

		if(type.equals("url"))
			f = new File(fileddir1stReport + "/" + file);
		if(type.equals("asr"))
			f = new File(filedir2ndReport + "/" + file);

		String completedFile = f.getName();
		completedFile = completedFile.replaceAll("-s.*$", "");
		completedFile = completedFile.replaceAll("-o.*$", "");
		completedFile = completedFile.replaceAll("-S.*$", "");
		completedFile = completedFile.trim();
		completedFile += "-Completed.txt";
		f.delete();

		if(type.equals("url"))
			f = new File(fileddir1stReport + "/" + completedFile);
		if(type.equals("asr"))
			f = new File(filedir2ndReport + "/" + completedFile);
		if(f.exists())
			f.delete();

		out.println("File '" + file + "' deleted <br>");

                BufferedWriter writer = new BufferedWriter(new FileWriter(logFilePath, true));
                writer.write( new java.util.Date()  + "\t" + request.getRemoteAddr() + "\t" + authen.getUserid() + "\tReport deleted:" + file + "\n");
                writer.close();
	} //end if 
%>
<br>
<br>
<br>
<br>
<br>
<%

	out.println("<h3> <b> Reports</b> </h3>");
	File directory1stReport = new File(fileddir1stReport);
	File[] list1stReport = directory1stReport.listFiles();
	Arrays.sort(list1stReport, LastModifiedFileComparator.LASTMODIFIED_REVERSE);
	File directory2ndReport = new File(filedir2ndReport);
	File[] list2ndReport = directory2ndReport.listFiles();
	Arrays.sort(list2ndReport, LastModifiedFileComparator.LASTMODIFIED_REVERSE);
	String lastFileHeader1stReport = "";
	String lastFileHeader2ndReport = "";
	int lenght = (list1stReport.length > list2ndReport.length) ? list1stReport.length : list2ndReport.length; 
	out.println("<table border=1 class='table table-hover'>");
	out.println("<thead><tr><td> <b> SER URL Reports </b> </td> <td> <b> SER ASR Reports </b></td></tr></thead><tbody>");
	for(int i=0;i <lenght; i++){
		out.println("<tr>");
		if(i<list1stReport.length && !list1stReport[i].getName().contains("Completed")){
			String nowFileHeader = list1stReport[i].getName();
			String status = "";
			nowFileHeader = nowFileHeader.replaceAll("-s.*$", "");
			nowFileHeader = nowFileHeader.replaceAll("-o.*$", "");
			nowFileHeader = nowFileHeader.trim();
			String fileForm = nowFileHeader;
			out.println("<td align=right>");
                        try{
                                File completedFile = new File(fileddir1stReport + "/" + fileForm + "-Completed.txt");
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
			out.println("<a href='reports/urlReports/" + list1stReport[i].getName() + "' target='_blank'>" + list1stReport[i].getName() + "</a>");
			out.println(" &nbsp; <b> <a href='?type=url&cmd=delete&file=" + list1stReport[i].getName() + "'> [DELETE]</a> </b> <br>");
			lastFileHeader1stReport = list1stReport[i].getName();
			lastFileHeader1stReport = lastFileHeader1stReport.replaceAll("-s.*$", "");
			lastFileHeader1stReport = lastFileHeader1stReport.replaceAll("-o.*$", "");
			lastFileHeader1stReport = lastFileHeader1stReport.trim();
			out.println("</td>");
		} //end if
		if(i<list1stReport.length && list1stReport[i].getName().contains("Completed")){
			out.println("<td></td>");
		} //end if
		if(i<list2ndReport.length && !list2ndReport[i].getName().contains("Completed")){
			if(i>=list1stReport.length)
				out.println("<td></td>");
			String nowFileHeader = list2ndReport[i].getName();
			String status = "";
			nowFileHeader = nowFileHeader.replaceAll("-S.*$", "");
			nowFileHeader = nowFileHeader.trim();
			String fileForm = nowFileHeader;
			out.println("<td align=right> ");
                        try{
                                File completedFile = new File(filedir2ndReport + "/" + fileForm + "-Completed.txt");
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
			out.println("<a href='reports/asrReports/" + list2ndReport[i].getName() + "' target='_blank'>" + list2ndReport[i].getName()   + "</a>");
			out.println(" &nbsp; <b> <a href='?type=asr&cmd=delete&file=" + list2ndReport[i].getName() + "'> [DELETE]</a> </b>");
			if(status.equals("In progress") && list2ndReport[i].getName().contains("SFXout.txt") )
				out.println("<b> <a href='javascript:restartQuery(\"" + nowFileHeader + "\")'> [RESTART]</a> </b> <br>");
			lastFileHeader2ndReport = list2ndReport[i].getName();
			lastFileHeader2ndReport = lastFileHeader2ndReport.replaceAll("-S.*$", "");
			lastFileHeader2ndReport = lastFileHeader2ndReport.trim();
			out.println("</td>");
		} //end if
		out.println("</tr>");
	} //end for
	out.println("<tbody></table>");

%>
</body>
</html>
