<%@ page import="hk.edu.hkmu.lib.adm.*,hk.edu.hkmu.lib.*"%>
<%@ page import="java.io.*,java.util.*"%>
<%
try {
		String basedir = request.getServletContext().getRealPath("/") + "adm/";
	        hk.edu.hkmu.lib.adm.Config.init();
	        String fileKey = (String) hk.edu.hkmu.lib.adm.Config.VALUES.get("FILEPROTECTIONKEY");
		String reportFilePath = basedir + "reports/leave";
		AuthenticateLibLDAP authen = (AuthenticateLibLDAP) session.getAttribute("authen");
		String logFilePath = basedir + "logs/reportLog.txt";
                BufferedWriter writer = new BufferedWriter(new FileWriter(logFilePath, true));
                writer.write( new java.util.Date()  + "\t" + request.getRemoteAddr() + "\t" + authen.getUserid() + "\tStaff Leave Report Request started\n");
		FetchAndConsolidateStaffLeave fl = new FetchAndConsolidateStaffLeave(session.getAttribute("username").toString(), session.getAttribute("password").toString());
		String now = StringHandling.getToday();
		String year = StringHandling.getCurrentYear(); 
                writer.write( new java.util.Date()  + "\t" + request.getRemoteAddr() + "\t" + authen.getUserid() + "\tStaff Leave Report Report generated\n");
		writer.close();
		out.println("<br> <b> <font color=red>  Report Generated: ");
		out.println("<br>[ <a href='getFile.jsp?loc=REPORTFOLDER&file=" + year + "-"  + hk.edu.hkmu.lib.adm.Config.VALUES.get("REPORTFILE") + "'> Download report file </a> ]");
		out.println("&nbsp; &nbsp [ <a href='/adm/pastReport.jsp'> View past reports </a> <b> </font>]");
		
	} //end try

	catch (Exception e) {
                out.println("<br>");
                out.println("<br>");
                out.println("<b> Something wrong, please contact system administrator.</b> </br> </br>");
                e.printStackTrace(new java.io.PrintWriter(out));
	}
%>
