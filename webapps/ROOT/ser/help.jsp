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
<h3><b> Help Page for the Library Tools for SER </b></h3>
<br>
<b> I. Tools </b>
<br>
  1. <a href="/ser/urlReport.jsp"> SER's URL Tools </a>: Connecting Aleph's Oracle Database for retrieving the URLs in BIB record with Item Process Status = WB by specifying item material types. 
<br>
  2. <a href="/ser/asrReport.jsp"> SER's ASR Tools </a>: Connecting Aleph's Oracle Database for retrieving orders and the related information by schools/units.  This tool is for SER's Annual Serial Review.  

<br>
<br>

<b> II. Past Reports </b>
<br>
<a href="/ser/pastReport.jsp"> The Past Reports </a> show the previous SER's URL/ASR reports requested. 

<br>
<br>

<b> III. Configuration </b>
<br>
<a href="/ser/config.jsp"> The configuration page </a> is to configure the SER's tools. The page is self-explanatory. 


</body>
</html>
