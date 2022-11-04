<%@ page import="hk.edu.hkmu.lib.cat.*,hk.edu.hkmu.lib.*,hk.edu.hkmu.lib.bookquery.*"%>
<%@ page import="java.io.*,java.util.*"%>
CopyCAT Program for Library
<%
	try {
		String file = request.getParameter("file");
		String cmd = request.getParameter("cmd");
		String basedir = request.getServletContext().getRealPath("/") + "cat";
		String requestFilePath = basedir + "/requests/sfxenrich/";
		String reportFilePath = basedir + "/reports/sfxenrich/";
		String logFilePath = basedir + "/logs/reportLog.txt";
		BufferedWriter writer = new BufferedWriter(new FileWriter(logFilePath, true));
		AuthenticateLibLDAP authen = (AuthenticateLibLDAP) session.getAttribute("authen");
		if(cmd != null && cmd.equals("restart")){
			CopyCatSFX cc = new CopyCatSFX(null, reportFilePath, file);
        	        writer.write( new java.util.Date()  + "\t" + request.getRemoteAddr() + "\t" + authen.getUserid() + "\tReport restarted:" + file + "\n");
		}
		if (file != null && file.contains(".xml-marc")) {
                        writer.write( new java.util.Date()  + "\t" + request.getRemoteAddr() + "\t" + authen.getUserid() + "\tRequest started:" + file + "\n");
			File f = new File(requestFilePath + file);
			CopyCatSFX cc = new CopyCatSFX(f, reportFilePath);
			String now = StringHandling.getToday();
			out.println("<b> report generation completed");
			out.println("Completed file: " + file);
			out.println("End time: " + now + "</b>");
			out.println(reportFilePath);
        	        writer.write( new java.util.Date()  + "\t" + request.getRemoteAddr() + "\t" + authen.getUserid() + "\tReport generated for:" + file + "\n");
		} else {
			out.println("invalid file.");
		} //end if
               	writer.close();
	} //end try

	catch (Exception e) {
                out.println("<br>");
                out.println("<br>");
                e.printStackTrace(new java.io.PrintWriter(out));
	}
%>
