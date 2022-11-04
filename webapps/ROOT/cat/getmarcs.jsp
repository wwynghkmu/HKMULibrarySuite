<%@ page import="hk.edu.hkmu.lib.cat.*,hk.edu.hkmu.lib.*,hk.edu.hkmu.lib.bookquery.*"%>
<%@ page import="java.io.*,java.util.*"%>
CopyCAT Program for Library
<%
	try {
		String file = request.getParameter("file");
		String basedir = request.getServletContext().getRealPath("/") + "cat/";
		String requestFilePath = basedir + "requests/copycat";
		String reportFilePath = basedir + "reports/copycat";
		AuthenticateLibLDAP authen = (AuthenticateLibLDAP) session.getAttribute("authen");
		String logFilePath = basedir + "logs/reportLog.txt";
                BufferedWriter writer = new BufferedWriter(new FileWriter(logFilePath, true));
		if (file != null && file.contains(".xlsx")) {
                        writer.write( new java.util.Date()  + "\t" + request.getRemoteAddr() + "\t" + authen.getUserid() + "\tRequest started:" + file + "\n");
			File f = new File(requestFilePath + "/" + file);
			CopyCatExcel cc = new CopyCatExcel(f, reportFilePath);
			String now = StringHandling.getToday();
			out.println("<b> Report generation completed <br>");
			out.println("Completed file: " + file);
			out.println("End time: " + now + "</b>");
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
