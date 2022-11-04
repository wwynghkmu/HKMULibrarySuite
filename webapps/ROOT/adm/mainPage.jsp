<%@include file="menu.jsp"%>
<html>
<head>
	<link rel="stylesheet" href="/css/menu.css">
	<link rel="stylesheet" href="/css/bootstrap.min.css" integrity="sha384-MCw98/SFnGE8fJT3GXwEOngsV7Zt27NXFoaoApmYm81iuXoPkFOJwJ8ERdknLPMO" crossorigin="anonymous">
	<script src="/js/standardLibrary/jquery-3.3.1.slim.min.js" integrity="sha384-q8i/X+965DzO0rT7abK41JStQIAqVgRVzpbzo5smXKp4YfRvH+8abtTE1Pi6jizo" crossorigin="anonymous"></script>
	<script src="/js/standardLibrary/popper.min.js" integrity="sha384-ZMP7rVo3mIykV+2+9J3UJ46jBk0WLaUAdn689aCwoqbBJiSnjAK/l8WvCWPIPm49" crossorigin="anonymous"></script>
	<script src="/js/standardLibrary/bootstrap.min.js" integrity="sha384-ChfqqxuZUCnJSK3+MXmPNIyE6ZbWh2IMqE241rYiqJxyMiZ6OW/JmZQ5stwEULTy" crossorigin="anonymous"></script>
	<script type='text/javascript' src='/js/standardista_table_sorting/common.js'></script>
	<script type='text/javascript' src='/js/standardista_table_sorting/css.js'></script>
	<script type='text/javascript' src='/js/standardista_table_sorting/standardista-table-sorting.js'></script>
	<title> OUHK LIB Server - for OUHK Library in-house developed programs </title>
	<meta charset="UTF-8">

<%
        if(authen!=null && !authen.isAuthenticated())
                response.sendRedirect("/adm/index.jsp?logout=yes");
	String cmd = request.getParameter("cmd");
	if(cmd==null)
		cmd="";
%>

<script lanuage="javascript">
		var cmd = "<%out.print(cmd);%>";
                var xhttp${resultStatus.index} = new XMLHttpRequest();
                xhttp${resultStatus.index}.onreadystatechange = function() {
                	if (xhttp${resultStatus.index}.readyState == 4 && xhttp${resultStatus.index}.status == 200) {
				var respondMsg = document.getElementById("respondMsg"); 
				respondMsg.innerHTML = xhttp${resultStatus.index}.responseText;
	                } //end if
                } //end function();


	        if(cmd=="staffLeave"){
                	xhttp${resultStatus.index}.open("GET", "getLeaveExcel.jsp", true);
			xhttp${resultStatus.index}.send();
                } //end if
</script>


</html>

</head>
<html>
<body>
<br> <br>
<br> <br>
<p> <b> Administration Office Tools </b> </p>
1. <a href="mainPage.jsp?cmd=staffLeave"> Staff Leave (in Excel) </a>
<%
	if(cmd.equals("staffLeave")){
		out.println("<br> <br> Report generation would take a quite long time.<br>");
		out.println("You may close this tab and check report later on the report page.<br>");
		out.println("Or you may wait for the complete message appears.");
	}
%>
<div id='respondMsg'>
</body>
</html>

