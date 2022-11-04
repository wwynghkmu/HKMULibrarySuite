<%@ page import="hk.edu.hkmu.lib.*,hk.edu.hkmu.lib.adm.*"%>
<html>
<head>
   <meta charset='utf-8'>
   <meta http-equiv="X-UA-Compatible" content="IE=edge">
   <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
   <link rel="stylesheet" href="/css/menu.css">
   <link rel="stylesheet" href="/css/bootstrap.min.css" integrity="sha384-MCw98/SFnGE8fJT3GXwEOngsV7Zt27NXFoaoApmYm81iuXoPkFOJwJ8ERdknLPMO" crossorigin="anonymous">
   <script src="/js/standardLibrary/jquery-3.3.1.slim.min.js" integrity="sha384-q8i/X+965DzO0rT7abK41JStQIAqVgRVzpbzo5smXKp4YfRvH+8abtTE1Pi6jizo" crossorigin="anonymous"></script>
   <script src="/js/standardLibrary/popper.min.js" integrity="sha384-ZMP7rVo3mIykV+2+9J3UJ46jBk0WLaUAdn689aCwoqbBJiSnjAK/l8WvCWPIPm49" crossorigin="anonymous"></script>
   <script src="/js/standardLibrary/bootstrap.min.js" integrity="sha384-ChfqqxuZUCnJSK3+MXmPNIyE6ZbWh2IMqE241rYiqJxyMiZ6OW/JmZQ5stwEULTy" crossorigin="anonymous"></script>
   <title>OUHK LIB TSServices Tools</title>
</head>

<%
hk.edu.hkmu.lib.adm.Config.init();
        AuthenticateLibLDAP authen = (AuthenticateLibLDAP) session.getAttribute("authen");
        String username = "";
	out.println(session.getAttribute("username"));
        if(authen!=null && hk.edu.hkmu.lib.adm.Config.VALUES.get("ALLOWUSERS").toLowerCase().contains(authen.getUserid().toLowerCase()) )
                username = authen.getSurname() + ", " + authen.getGivenName() + " (" + authen.getPatronType().trim() + ")";
        else
                response.sendRedirect("/adm/index.jsp?logout=yes");
%>
<div style="background-color: lightblue;" id="cssmenu">
<font color=yellow size=5> <b>  OUHK LIB TSServices Admin Office Tools (Production Site) - <%=username%></b> </font>
<ul>
<li>
<a href="index.jsp">Main Page</a>
</li>

<li>
        <li class='has-sub'><a href='#'><span>Report</span></a>
                <ul>
                        <li class='has-sub'><a href='pastReport.jsp'><span>Past Report</span></a></li>
                        <li class='has-sub'><a href='systemLog.jsp'><span>System Log</span></a></li>
                </ul>
        </li>

</li>

<li>
<a href="help.jsp"> Help</a>
</li>
<li>
<a href="index.jsp?logout=yes"> Logout </a>
</li>
</ul>
</div>
