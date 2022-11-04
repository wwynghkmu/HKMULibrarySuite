<%@ page import="java.io.*,java.util.*, javax.servlet.*,java.sql.*"%>
<%@ page import="hk.edu.hkmu.lib.ser.*,hk.edu.hkmu.lib.*,hk.edu.hkmu.lib.bookquery.*"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@include file="menu.jsp"%>
<%
        if(authen!=null && !authen.isAuthenticated())
                response.sendRedirect("/ser/index.jsp?logout=yes");
%>
<br>
<br>
<br>
<br>
<br>
<br>
<b> Download the URL report in Excel (it would take long time to load): </b><div id=reportFile><img src=/img/loading.gif width=100></div>
<script language=javascript>
	var xhttp = new XMLHttpRequest();
		 xhttp.onreadystatechange = function() {
			if (this.readyState == 4 && this.status == 200) {
				document.getElementById("reportFile").innerHTML =
				this.responseText;
			}
			if (this.status == 408 || this.status==504) {
				document.getElementById("reportFile").innerHTML = "Report failed. Pls the contact system support.";
			}
		};
	xhttp.open("GET", "urlReportForAJAX.jsp", true);
	xhttp.send();
</script>
<b> You may also go to <a href="pastReport.jsp">  the past report page </a> later for getting the report. </b>
</body>
</html>
