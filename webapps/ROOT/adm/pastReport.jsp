<%@ page import="java.io.*,java.util.*, javax.servlet.*,org.apache.commons.io.comparator.*" %>
<%@ page import="javax.servlet.http.*" %>
<%@include file="menu.jsp"%>
</html>
</head>
<%
hk.edu.hkmu.lib.Config.init();
	hk.edu.hkmu.lib.adm.Config.init();
	String fileKey = (String) hk.edu.hkmu.lib.Config.VALUES.get("FILEPROTECTIONKEY");
        if(authen!=null && !authen.isAuthenticated())
                response.sendRedirect("/adm/index.jsp?logout=yes");
	String basedir = request.getServletContext().getRealPath("/") + "adm/";
	String logFilePath = basedir + "logs/reportLog.txt";
	String cmd = request.getParameter("cmd");
	String file = request.getParameter("file");
	String leftRPTFolder = hk.edu.hkmu.lib.adm.Config.VALUES.get("DOWNLOADFOLDER"); 
	String rightRPTFolder = hk.edu.hkmu.lib.adm.Config.VALUES.get("REPORTFOLDER"); 
	File f = new File(leftRPTFolder + "/" + file); 
	if(cmd!=null && cmd.contains("delete")){

		String completedFile = f.getName();
		completedFile = completedFile.replaceAll("-s.*$", "");
		completedFile = completedFile.replaceAll("-o.*$", "");
		completedFile = completedFile.replaceAll("-S.*$", "");
		completedFile = completedFile.trim();
		completedFile += "-Completed.txt";
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
<h3> <b> Past Reports</b> </h3>
<%
	File[] leftRPTFileList = new File(leftRPTFolder).listFiles(); 
	Arrays.sort(leftRPTFileList, Collections.reverseOrder());
	File[] rightRPTFileList = new File(rightRPTFolder).listFiles();
	Arrays.sort(rightRPTFileList, Collections.reverseOrder());
	String lastFileHeaderLeft = "";
	String lastFileHeaderRight = "";
	int lenght = (leftRPTFileList.length > rightRPTFileList.length) ? leftRPTFileList.length : rightRPTFileList.length; 
	out.println("<table border=1 class='table table-hover'>");
	out.println("<thead><tr><td width='50%'> <b> Sharepoint Downloaded Leave Record </b> </td> <td> <b> Staff Leave Master Reports </b></td></tr></thead><tbody>");
	for(int i=0;i <lenght; i++){
		out.println("<tr>");
		if(i<leftRPTFileList.length && !leftRPTFileList[i].getName().contains("Completed")){
			String nowFileHeader = leftRPTFileList[i].getName();
			String status = "";
			nowFileHeader = nowFileHeader.replaceAll("-s.*$", "");
			nowFileHeader = nowFileHeader.replaceAll("-o.*$", "");
			nowFileHeader = nowFileHeader.trim();
			String fileForm = nowFileHeader;
			out.println("<td align=right>");
                        try{
                                File completedFile = new File(leftRPTFolder + "/" + fileForm + "-Completed.txt");
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
			if(!lastFileHeaderLeft.equals(nowFileHeader)){
				out.println(status + " Report: ");
			}
			out.println("<a href='getFile.jsp?loc=DOWNLOADFOLDER&file=" + leftRPTFileList[i].getName().replaceAll(fileKey + "-", "") + "' target='_blank'>" + leftRPTFileList[i].getName().replaceAll(fileKey + "-", "") + "</a>");
			lastFileHeaderLeft = leftRPTFileList[i].getName();
			lastFileHeaderLeft = lastFileHeaderLeft.replaceAll("-s.*$", "");
			lastFileHeaderLeft = lastFileHeaderLeft.replaceAll("-o.*$", "");
			lastFileHeaderLeft = lastFileHeaderLeft.trim();
			out.println("</td>");
		} //end if
		if(i<leftRPTFileList.length && leftRPTFileList[i].getName().contains("Completed")){
			out.println("<td></td>");
		} //end if
		if(i<rightRPTFileList.length && !rightRPTFileList[i].getName().contains("Completed") ){
			if(i>=leftRPTFileList.length)
				out.println("<td></td>");
			String nowFileHeader = rightRPTFileList[i].getName();
			String status = "";
			nowFileHeader = nowFileHeader.replaceAll("-S.*$", "");
			nowFileHeader = nowFileHeader.trim();
			String fileForm = nowFileHeader;
			out.println("<td align=right> ");
                        try{
                                File completedFile = new File(rightRPTFolder + "/" + fileForm + "-Completed.txt");
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
			if(!lastFileHeaderRight.equals(nowFileHeader)){
				out.println(status + " Report:");
			}
			out.println("<a href='getFile.jsp?loc=REPORTFOLDER&file=" + rightRPTFileList[i].getName().replaceAll(fileKey + "-", "") + "' target='_blank'>" + rightRPTFileList[i].getName().replaceAll(fileKey + "-", "")   + "</a>");
			lastFileHeaderRight = rightRPTFileList[i].getName();
			lastFileHeaderRight = lastFileHeaderRight.replaceAll("-S.*$", "");
			lastFileHeaderRight = lastFileHeaderRight.trim();
			out.println("</td>");
		} //end if
		out.println("</tr>");
	} //end for
	out.println("<tbody></table>");

%>
</html>
