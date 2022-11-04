<%@ page import="java.io.*,java.util.*, javax.servlet.*,java.sql.*"%>
<%@ page import="javax.naming.*, javax.naming.directory.*"%>
<%@ page import="hk.edu.hkmu.lib.*,hk.edu.hkmu.lib.cat.*"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<h1> OUHK LIB TSServices Admin System </h1>
<br>
<%
session.setMaxInactiveInterval(2400);
	hk.edu.hkmu.lib.Config.init();
	String username = request.getParameter("username");
	String password = request.getParameter("password");
	String logout = request.getParameter("logout");
	String ip = request.getRemoteAddr();
	AuthenticateLibLDAP authen = (AuthenticateLibLDAP) session.getAttribute("authen");
	if(logout!=null && logout.equals("yes")){
		session.setAttribute("authen", null);
		authen = null;
	}
	if(!(username == null && password == null)){
                authen = new AuthenticateLibLDAP(username, password, ip, hk.edu.hkmu.lib.Config.VALUES.get("ALLOWUSERS").toLowerCase(), "TS Services Root System");
		if(hk.edu.hkmu.lib.Config.VALUES.get("ALLOWUSERS").toLowerCase().contains(authen.getUserid().toLowerCase()) && authen.isAuthenticated() ){
	session.setAttribute("authen", authen);
	session.setAttribute("username", username);
	session.setAttribute("password", password);
		}
	}

	if(authen!=null && authen.isAuthenticated() && hk.edu.hkmu.lib.Config.VALUES.get("ALLOWUSERS").toLowerCase().contains(authen.getUserid().toLowerCase()) ){
		response.sendRedirect("/mainPage.jsp");
	} else if(username!=null) {
		out.println("<font color=red> Wrong password  or user doesn't have the right to login</font> <br>");
	}
%>
<form method=post>
User ID: &nbsp;
<input type=text name=username>
<br>
Password:
<input type=password name=password>
<br>
<input type=submit>
</form>
