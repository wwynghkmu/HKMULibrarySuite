<%@ page import="hk.edu.hkmu.lib.*,hk.edu.hkmu.lib.adm.*"%>
<%@ page import="java.io.*,org.apache.commons.io.IOUtils" %>
<html>
<head>
</head>

<%
hk.edu.hkmu.lib.adm.Config.init();
        hk.edu.hkmu.lib.Config.init();
        AuthenticateLibLDAP authen = (AuthenticateLibLDAP) session.getAttribute("authen");
        String username = "";
	String reqFileName = request.getParameter("file");
	String reqFileLoc = request.getParameter("loc");
        if(authen!=null && hk.edu.hkmu.lib.adm.Config.VALUES.get("ALLOWUSERS").toLowerCase().contains(authen.getUserid().toLowerCase()) )
                username = authen.getSurname() + ", " + authen.getGivenName() + " (" + authen.getPatronType().trim() + ")";
        else
                response.sendRedirect("/adm/index.jsp?logout=yes");
	String file = hk.edu.hkmu.lib.adm.Config.VALUES.get(reqFileLoc) + "/" + hk.edu.hkmu.lib.Config.VALUES.get("FILEPROTECTIONKEY") + "-" +  reqFileName; 
	InputStream inFile=new FileInputStream(new File(file));
	byte[] bytesFile=IOUtils.toByteArray(inFile);
	response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition",
                                "attachment; filename=" + request.getParameter("file"));
        response.getOutputStream().write(bytesFile);
        response.getOutputStream().close();
%>
