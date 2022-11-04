<%@ page import="hk.edu.hkmu.lib.cat.*,hk.edu.hkmu.lib.*,hk.edu.hkmu.lib.bookquery.*"%>
<%@ page import="java.sql.*"%>
<%@ page import="java.util.*"%>
<%@ page import="java.text.*"%>
<%@ page import="java.io.*,java.util.*,javax.servlet.*"%>
<%@include file="menu.jsp"%>
<br>
<br>
<br>
<br>
<br>
<br>
<h3> <b> New Additional List (by School) </b> </h3>
<table border=1 width=70% class='table table-hover'>
<thead> <tr>  <td> School</td> <td> </td> <td> </td> <td></td></tr> </thead>
<%
	        if(authen!=null && !authen.isAuthenticated())
	                response.sendRedirect("/acq/index.jsp?logout=yes");

	        String basedir = request.getServletContext().getRealPath("/") + "acq/";
		String budgetCodeFilePath = basedir + "conf/budgetCodes.txt";
                BufferedReader br = new BufferedReader(new FileReader(budgetCodeFilePath));
                String line = "";
                String sch[] = null;
                int count=0;
                while((line = br.readLine()) != null){
                        String arry[] = line.split("~");
                        String schStr = arry[0];
                        String codeStr = arry[1];
                        sch = schStr.split("@");
			String currMLink = "additionListBySchoolFromARC.jsp?quick=current&sch=" + sch[0].replace("&", "%26");
			String lastMLink = "additionListBySchoolFromARC.jsp?quick=last&sch=" + sch[0].replace("&", "%26");
                        String html = "<tr> <td>" + sch[0] + " (" + sch[1] + ")";
                        html += "<td> <a target=_blank  href=" + currMLink + ">  Current Month </a> </td>";
                        html += "<td> <a target=_blank  href=" + lastMLink + ">  Last Month </a> </td> ";
			html += "<td> Specific Date Range. <br> <form target=_blank action=additionListBySchoolFromARC.jsp> Start: <input type=text name=start value=yyyymmdd size=6> ";
			html += "End: <input type=text name=end  value=yyyymmdd size=6> <input name=sch type=hidden value=" + sch[0] + "> <input type=submit> </form> </td></tr>";
                        out.println(html);
                        count++;
                }

%>
</table>
