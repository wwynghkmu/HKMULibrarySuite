<%@ page import="java.io.*,java.util.*, javax.servlet.*,java.sql.*"%>
<%@ page import="hk.edu.hkmu.lib.ser.*,hk.edu.hkmu.lib.*,hk.edu.hkmu.lib.bookquery.*"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@include file="menu.jsp"%>
<br>
<br>
<br>
<br>
<br>
<%
        if(authen!=null && !authen.isAuthenticated())
                response.sendRedirect("/ser/index.jsp?logout=yes");
        String cmd = request.getParameter("cmd");
        String type = request.getParameter("type");
        String basedir = request.getServletContext().getRealPath("/") + "ser/";
%>
<b> Download the ASR report in Excel (it would take long time to load): </b><div id=reportFile><img src=/img/loading.gif width=100></div>
<script language=javascript>
	var xhttp = new XMLHttpRequest();
		 xhttp.onreadystatechange = function() {
			if (this.readyState == 4 && this.status == 200) {
				document.getElementById("reportFile").innerHTML =
				this.responseText;
			}
			if (this.status == 408 || this.status==504) {
				document.getElementById("reportFile").innerHTML = "Report failed. Pls the contact system support (<a href='mailto:libsys@ouhk.edu.hk'> libsys@ouhk.edu.hk </a>.";
			}
		};
	xhttp.open("GET", "asrReportForAJAX.jsp", true);
	xhttp.send();
</script>
<br>
<b> You may also go to <a href="pastReport.jsp">  the past report page </a> later for getting the report. </b>
</body>
</html>
