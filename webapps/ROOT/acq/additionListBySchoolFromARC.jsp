<%@ page import="java.io.*,java.util.*, javax.servlet.*,java.sql.*"%>
<%@ page import="hk.edu.hkmu.lib.acq.*,hk.edu.hkmu.lib.*,hk.edu.hkmu.lib.bookquery.*"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@include file="menu.jsp"%>
<br>
<br>
<br>
<br>
<br>
<br>

<%

       if(authen!=null && !authen.isAuthenticated())
		response.sendRedirect("/acq/index.jsp?logout=yes");

	out.println("<b> Download Excel report: </b><div id=reportFile></div>");
	try {

		String schCode = request.getParameter("sch");
		String startDate = request.getParameter("start"); 
		String endDate = request.getParameter("end");
		String quickDate = request.getParameter("quick");
		String reportdir = request.getServletContext().getRealPath("/") + "acq/reports/newArrivalBySch/";
		String webReportdir = "/acq/reports/newArrivalBySch/";
		FetchReportBySchoolAndDates rpt = new FetchReportBySchoolAndDates(schCode,startDate,endDate,reportdir,out);
	        String basedir = request.getServletContext().getRealPath("/") + "acq/";
	        String logFilePath = basedir + "logs/reportLog.txt";
		if(startDate == null || endDate == null){
		switch(quickDate){
			case "current":
				rpt.reportThisMonth();
				break;
			case "last":
				rpt.reportLastMonth();
				break;
			default:
				break;
		}
		}
		else{
			rpt.fetchReport();
		}
		ArrayList<String> reports = rpt.getReportFiles();
		for(String report : reports){
			String str = "<a href='" + webReportdir + report + "' target=_blank> " + report + "</a>";
			out.println("<script language=javascript> var reportFile = document.getElementById('reportFile'); reportFile.innerHTML=\"" + str + "\"; </script>");
			BufferedWriter writer = new BufferedWriter(new FileWriter(logFilePath, true));
			writer.write( new java.util.Date()  + "\t" + request.getRemoteAddr() + "\t" + authen.getUserid() + "\tACQ LIB Tools report generated: " + report + "\n");
			writer.close();
		}
		out.println("<br>");
	} //end try

	catch (Exception e) {
		out.println("<br>");
		out.println("<br>");
		e.printStackTrace(new java.io.PrintWriter(out));
	}
%>
