<%@include file="../preload.jsp"%>
<body>

<%
hk.edu.hkmu.lib.acq.Config.init();
	AuthenticateLibLDAP authen = (AuthenticateLibLDAP) session.getAttribute("authen");
	String username = "";
	if(authen!=null && hk.edu.hkmu.lib.acq.Config.VALUES.get("ALLOWUSERS").toLowerCase().contains(authen.getUserid().toLowerCase()) )
		username = authen.getSurname() + ", " + authen.getGivenName() + " (" + authen.getPatronType().trim() + ")";
	else
		response.sendRedirect("/acq/index.jsp?logout=yes&source=" + javax.servlet.http.HttpUtils.getRequestURL(request));
%>

<div style="background-color: lightblue;" id="cssmenu">
<img src="/img/acq.jpg" height=50px>
<font color=lightblue size=5> <b>  OUHK LIB ACQ's Tools (Testing Site) - <%=username%> </b> </font>
<ul>

<li class='has-sub'> <a href='#'> Tool</a>
<ul>
	<li class='has-sub'> <a href='#'> New Addition List Tool</a>
		<ul>
			<li class='has-sub'> <a href="additionListBySchoolMenu.jsp"> New Addition List by School</a></li>
			<li class='has-sub'> <a href="emailAdditionListBySchool.jsp"> Email "New Addition List by School" Report (Last Month)</a></li>
		</ul>
	</li>
</ul>
</li>

<li>
   <li class='has-sub'><a href='#'><span>Report</span></a>
      <ul>
   	<li class='has-sub'><a href='newArrivialReport.jsp'><span>New Arrivial List</span></a> </li>
   	<li class='has-sub'><a href='eBkPlatformReport.jsp'><span>eBook Platform Checking</span></a> </li>
   	<li class='has-sub'><a href='systemLog.jsp'><span>System Log</span></a> </li>
      </ul>
   </li>
<li>

<li>
   <li class='has-sub'><a href='#'><span>Configuration</span></a>
      <ul>
	         <li class='has-sub'><a href='config.jsp'><span>General</span></a> </li>
   		<li class='has-sub'><a href='#'><span>New Addition List</span></a>
		      <ul>
		         <li class='has-sub'><a href='addListReportConfig.jsp'><span>Report & Email</span></a> </li>
		         <li class='has-sub'><a href='addListConfig.jsp'><span>Budget</span></a> </li>
		      </ul>
		</li>
		<li class='has-sub'><a href='#'><span>EBook Platform Checking</span></a>
		      <ul>
		         <li class='has-sub'><a href='eBkPlatformCheckingGeneralConfig.jsp'><span>General Configuration</span></a> </li>
		         <li class='has-sub'><a href='eBkPlatformCheckingConfig.jsp'><span>Platform Configuration</span></a> </li>
		      </ul>
		</li>
      </ul>
   </li>
<li>
<a href="help.jsp"> Help</a>
</li>
<li>
<a href="index.jsp?logout=yes"> Logout </a>
</li>
</ul>
</div>
