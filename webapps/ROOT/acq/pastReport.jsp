<%@ page import="java.io.*,java.util.*, javax.servlet.*,org.apache.commons.io.comparator.*" %>
<%@ page import="javax.servlet.http.*,java.sql.*" %>
<%@include file="menu.jsp"%>
</html>
</head>
<%
if(authen!=null && !authen.isAuthenticated())
		response.sendRedirect("/acq/index.jsp?logout=yes");

	String basedir = request.getServletContext().getRealPath("/") + "acq";
	String logFilePath = basedir + "/logs/reportLog.txt";
	String fileddir1stReport = basedir + "/reports/newArrivalBySch";
	String filedir2ndReport = basedir + "/reports/dummy";

	String cmd = request.getParameter("cmd");
	String type = request.getParameter("type");
	if(type==null)
		type="";
	String file = request.getParameter("file");
	File f = new File(fileddir1stReport + "/" + file); 
	if(cmd!=null && cmd.contains("delete")){

		if(type.equals("url"))
	f = new File(fileddir1stReport + "/" + file);
		if(type.equals("asr"))
	f = new File(filedir2ndReport + "/" + file);

		String completedFile = f.getName();
		completedFile = completedFile.replaceAll("-s.*$", "");
		completedFile = completedFile.replaceAll("-o.*$", "");
		completedFile = completedFile.replaceAll("-S.*$", "");
		completedFile = completedFile.trim();
		completedFile += "-Completed.txt";
		f.delete();

		if(type.equals("url"))
	f = new File(fileddir1stReport + "/" + completedFile);
		if(type.equals("asr"))
	f = new File(filedir2ndReport + "/" + completedFile);
		if(f.exists())
	f.delete();

		out.println("File '" + file + "' deleted <br>");
		BufferedWriter writer = new BufferedWriter(new FileWriter(logFilePath, true));
  		writer.write( new java.util.Date()  + "\t" + request.getRemoteAddr() + "\t" + authen.getUserid() + "\tACQ LIB Tools report deleted: " + file + "\n");
		writer.close();
	} //end
%>
<br>
<br>
<br>
<br>
<br>
<h3> <b> ACQ Past Reports</b> </h3>
<%
File directory1stReport = new File(fileddir1stReport);
	File[] list1stReport = directory1stReport.listFiles();
	Arrays.sort(list1stReport, LastModifiedFileComparator.LASTMODIFIED_REVERSE);
	File directory2ndReport = new File(filedir2ndReport);
	File[] list2ndReport = directory2ndReport.listFiles();
	Arrays.sort(list2ndReport, LastModifiedFileComparator.LASTMODIFIED_REVERSE);
	String lastFileHeader1stReport = "";
	String lastFileHeader2ndReport = "";
	Connection conn = null;
	Statement stmt = null;
	Class.forName(hk.edu.hkmu.lib.acq.Config.JDBC_DRIVER);
	conn = DriverManager.getConnection(hk.edu.hkmu.lib.acq.Config.DB_URL, hk.edu.hkmu.lib.acq.Config.USER, hk.edu.hkmu.lib.acq.Config.PASS);

	Map<Integer, String> eBKPReports = new HashMap<Integer, String>();
	String sql = "SELECT DISTINCT batchID, SUBSTRING(timestamp, 1, 10) AS date from ebkplatformcheckingreport";
        stmt = conn.createStatement();
       	ResultSet rs = stmt.executeQuery(sql);
        while (rs.next()) {
       	        String date = StringHandling.trimNewLineChar((rs.getString("date").trim()));
       	        int batchID = rs.getInt("batchID");
		eBKPReports.put(batchID, date);
       	}
	int eBKPReportsSize = eBKPReports.size();
	int count = 0;
	ArrayList<Integer> eBKPReportsKeyList = new ArrayList<Integer>();
	eBKPReportsKeyList.addAll(eBKPReports.keySet());

	int lenght = (list1stReport.length > list2ndReport.length) ? list1stReport.length : list2ndReport.length; 
	out.println("<table border=1 class='table table-hover'>");
	out.println("<thead><tr><td width='50%'> <b> New Arrival List for School  Reports </b> </td> <td> <b> EBook Platform Checking Reports</b></td></tr></thead><tbody>");
	for(int i=0;i <lenght; i++){
		if(!list1stReport[i].getName().contains(".empty")){
		out.println("<tr>");
		if(i<list1stReport.length && !list1stReport[i].getName().contains("Completed")){
	String nowFileHeader = list1stReport[i].getName();
	String status = "";
	nowFileHeader = nowFileHeader.replaceAll("-s.*$", "");
	nowFileHeader = nowFileHeader.replaceAll("-o.*$", "");
	nowFileHeader = nowFileHeader.trim();
	String fileForm = nowFileHeader;
	out.println("<td align=left>");
                        try{
                                File completedFile = new File(fileddir1stReport + "/" + fileForm + "-Completed.txt");
                                FileInputStream fileinputstream = new FileInputStream(completedFile);
                                int numberBytes = fileinputstream.available();
                                byte bytearray[] = new byte[numberBytes];
                                fileinputstream.read(bytearray);
		String isCompleted = new String(bytearray);
                                if(isCompleted.contains("YES"))
                                        status = "Completed";
		else
			status = "In progress";
                                fileinputstream.close();
                        }   
                        catch(Exception e){}
		out.println("<a href='reports/newArrivalBySch/" + list1stReport[i].getName() + "' target='_blank'>" + list1stReport[i].getName() + "</a>");
		out.println(" &nbsp; <b> <a href='?type=url&cmd=delete&file=" + list1stReport[i].getName().replace("&", "%26") + "'> [DELETE]</a> </b> <br>");
	lastFileHeader1stReport = list1stReport[i].getName();
	lastFileHeader1stReport = lastFileHeader1stReport.replaceAll("-s.*$", "");
	lastFileHeader1stReport = lastFileHeader1stReport.replaceAll("-o.*$", "");
	lastFileHeader1stReport = lastFileHeader1stReport.trim();
	out.println("</td>");
		} //end if
		if(i<list1stReport.length && list1stReport[i].getName().contains("Completed")){
	out.println("<td></td>");
		} //end if
		if(i<list2ndReport.length && !list2ndReport[i].getName().contains("Completed")){
	if(i>=list1stReport.length)
		out.println("<td></td>");
	String nowFileHeader = list2ndReport[i].getName();
	String status = "";
	nowFileHeader = nowFileHeader.replaceAll("-S.*$", "");
	nowFileHeader = nowFileHeader.trim();
	String fileForm = nowFileHeader;
	out.println("<td align=right> ");
                        try{
                                File completedFile = new File(filedir2ndReport + "/" + fileForm + "-Completed.txt");
                                FileInputStream fileinputstream = new FileInputStream(completedFile);
                                int numberBytes = fileinputstream.available();
                                byte bytearray[] = new byte[numberBytes];
                                fileinputstream.read(bytearray);
		String isCompleted = new String(bytearray);
                                if(isCompleted.contains("YES"))
                                        status = "Completed";
		else
			status = "In progress";
                                fileinputstream.close();
                        }   
                        catch(Exception e){}
	out.println("<a href='reports/asrReports/" + list2ndReport[i].getName() + "' target='_blank'>" + list2ndReport[i].getName()   + "</a>");
	out.println(" &nbsp; <b> <a href='?type=asr&cmd=delete&file=" + list2ndReport[i].getName() + "'> [DELETE]</a> </b>");
	if(status.equals("In progress") && list2ndReport[i].getName().contains("SFXout.txt") )
		out.println("<b> <a href='javascript:restartQuery(\"" + nowFileHeader + "\")'> [RESTART]</a> </b> <br>");
	lastFileHeader2ndReport = list2ndReport[i].getName();
	lastFileHeader2ndReport = lastFileHeader2ndReport.replaceAll("-S.*$", "");
	lastFileHeader2ndReport = lastFileHeader2ndReport.trim();
	out.println("</td>");
		} //end if

		if(count < eBKPReportsSize){
	out.println( "<td> <a target=_blank href='getEBKPlatformReport.jsp?batchID=" + eBKPReportsKeyList.get(count) + "'>");
	out.println(eBKPReportsKeyList.get(count));
	out.println(". ");
	out.println(  eBKPReports.get(eBKPReportsKeyList.get(count)));
	out.println("</a>");
	out.println("</td>");
		}
		count++;

		out.println("</tr>");
		}//end if
	} //end for
	out.println("<tbody></table>");
        stmt.close();
        conn.close();
%>
</html>
