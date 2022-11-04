<%@ page import="java.io.*,java.util.*, javax.servlet.*"%>
<%@ page import="hk.edu.hkmu.lib.ser.*,hk.edu.hkmu.lib.*"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%
try {

	        hk.edu.hkmu.lib.ser.Config.init();
	        AuthenticateLibLDAP authen = (AuthenticateLibLDAP) session.getAttribute("authen");
		String basedir = request.getServletContext().getRealPath("/") + "ser/";
		String logFilePath = basedir + "logs/reportLog.txt";
	        String username = "";
	        if(authen!=null && hk.edu.hkmu.lib.ser.Config.VALUES.get("ALLOWUSERS").toLowerCase().contains(authen.getUserid().toLowerCase()) ){
                	username = authen.getSurname() + ", " + authen.getGivenName();
	String reportdir = request.getServletContext().getRealPath("/") + "ser/reports/urlReports/";
	String webReportdir = "/ser/reports/urlReports/";
	FetchURLReport rpt = new FetchURLReport(reportdir, null);
	rpt.fetchReport();
	                out.println("<a href=" + webReportdir + "/" + rpt.getReportFile() + "> " + rpt.getReportFile() + " </a>");
	BufferedWriter writer = new BufferedWriter(new FileWriter(logFilePath, true));
                        writer.write( new java.util.Date()  + "\t" + request.getRemoteAddr() + "\t" + authen.getUserid() + "\tSER URL Report run: " + rpt.getReportFile() + "\n");
	writer.close();
        	} else {
	                response.sendRedirect("/ser/index.jsp?logout=yes");
		}
	} //end try

	catch (Exception e) {
		out.println("<br>");
		out.println("<br>");
		e.printStackTrace(new java.io.PrintWriter(out));
	}
%>
