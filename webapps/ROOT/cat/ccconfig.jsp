<%@ page import="hk.edu.csids.cat.*,hk.edu.csids.*"%>
<%@ page import="hk.edu.csids.bookquery.*"%>
<%@ page import="java.sql.*"%>
<%@ page import="java.util.*"%>
<%@ page import="java.text.*"%>
<%@ page import="java.io.*,java.util.*, javax.servlet.*"%>
<%@ page import = "org.apache.commons.fileupload.*" %>
<%@ page import = "org.apache.commons.fileupload.disk.*" %>
<%@ page import = "org.apache.commons.fileupload.servlet.*" %>
<%@ page import = "org.apache.commons.io.output.*" %>
<%@ page import = "org.apache.commons.io.*" %>

<%@include file="menu.jsp"%>
<br>
<br>
<br>
<br>
<br>
<h3> <b> CopyCat Configuration</b> </h3>
<%
        if(authen!=null && !authen.isAuthenticated())
                response.sendRedirect("/acq/index.jsp?logout=yes");
	String cmd = request.getParameter("cmd");
	String tol = request.getParameter("tol");
        String basedir = request.getServletContext().getRealPath("/") + "cat/";
        String webinfdir = request.getServletContext().getRealPath("/") + "WEB-INF/classes/hk/edu/ouhk/lib/cat/";
        String chineseAIwebinfdir = request.getServletContext().getRealPath("/") + "WEB-INF/classes/hk/edu/ouhk/lib/";
        String configFilePath = basedir + "conf/ccconfig.txt";
        String chineseAIFilePath = basedir + "conf/chineseAI.txt";
        String chineseConvFilePath = basedir + "conf/TtoSChineseConversionTable.xlsx";
        String configWebFilePath = webinfdir + "ccconfig.txt";
        String chineseAIWebFilePath = chineseAIwebinfdir + "chineseAI.txt";
        String chineseConvWebFilePath = chineseAIwebinfdir + "TtoSChineseConversionTable.xlsx";
	String logFilePath = basedir + "logs/configLog.txt";
	String contentType = request.getContentType();
	if(cmd!=null){

		if(cmd.equals("update") && contentType==null ){
			String configUpdate = "";
			int t=Integer.parseInt(tol);
			for(int i=0;i<t;i++){
				String configName = request.getParameter("configname_" + i);
				String configValue = request.getParameter(configName + i);
				String configDes = request.getParameter("des" + configName + i);
				configUpdate += configName + "~" +configValue + "~" + configDes; 
				if(i<=t-1)
					configUpdate += "\n";
			}
                        File file = new File(configFilePath);
                        file.delete();
                        Writer objWriter = new BufferedWriter(new FileWriter(configFilePath));
                        objWriter.write(configUpdate);
                        objWriter.flush();
                        objWriter.close(); 
                        file = new File(configWebFilePath);
                        objWriter = new BufferedWriter(new FileWriter(configWebFilePath));
                        objWriter.write(configUpdate);
                        objWriter.flush();
                        objWriter.close();
                        BufferedWriter writer = new BufferedWriter(new FileWriter(logFilePath, true));
                        writer.write( new java.util.Date()  + "\t" + request.getRemoteAddr() + "\t" + authen.getUserid() + "\tCAT LIB Tools config updated: \n" + configUpdate + "\n\n");
                        writer.close();
			out.println("<b> <font color=red> Configuration Updated </font> </b>");
		}
	}

	File file;
	if ((contentType!=null && contentType.indexOf("multipart/form-data") >= 0)) {
		try{
		DiskFileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload(factory);
		List fileItems = upload.parseRequest(request);
		Iterator i = fileItems.iterator();
		FileItem fi = (FileItem)i.next();
		String fieldName = fi.getFieldName();
		String fileName = fi.getName();
		long sizeInBytes = fi.getSize();
		if(fileName.contains("chineseAI.txt")){
			file = new File(chineseAIFilePath); 
			fi.write( file ) ;
			FileUtils.copyFile(file, new FileOutputStream(new File(chineseAIWebFilePath)) );
			out.println("<font color=red> <b> chineseAI.txt file updated. </b> </font>");
                        BufferedWriter writer = new BufferedWriter(new FileWriter(logFilePath, true));
                        writer.write( new java.util.Date()  + "\t" + request.getRemoteAddr() + "\t" + authen.getUserid() + "\tCAT LIB Tools config updated: chineseAI.txt\n\n");
                        writer.close();
		} else if(fileName.contains("TtoSChineseConversionTable.xlsx")){
			file = new File(chineseConvFilePath); 
			fi.write( file ) ;
			FileUtils.copyFile(file, new FileOutputStream(new File(chineseConvWebFilePath)) );
			out.println("<font color=red> <b> TtoSChineseConversionTable.xlsx file updated. </b> </font>");
                        BufferedWriter writer = new BufferedWriter(new FileWriter(logFilePath, true));
                        writer.write( new java.util.Date()  + "\t" + request.getRemoteAddr() + "\t" + authen.getUserid() + "\tCAT LIB Tools config updated: TtoSChineseConversionTable.xlsx \n\n");
                        writer.close();
		
		} else {
			out.println("<font color=red> <b> Wrong file. The filname name must be 'chineseAI.txt' or 'TtoSChineseConversionTable.xlsx'.</b> </font>");
		}
		}
		catch(Exception e){
			out.println("<font color=red> <b> File failed to upload; make sure you chose the correct file. </b> </font>");
			out.println(e.getMessage());
		}

	}
	
%>

<hr>
<form>
<table border=1 class="table table-hover">
<thead>
<tr>  <td> <b> Value </b> </td> <td> <b> Remark </b> </td> </tr>
</thead>
<%
                BufferedReader br = new BufferedReader(new FileReader(configFilePath));
                String line = ""; 
                String sch[] = null;
		int count=0;
                while((line = br.readLine()) != null){
                        String arry[] = line.split("~");
			String html = "";
			if(!arry[0].contains("DEFAULT_")){
				html = "<tr> <td>";
				html += "<input name=" + arry[0] + count + " size=50% type=text value='" + arry[1] + "'>";
				html += "<input type=hidden name=configname_" + count + " value='" + arry[0] + "'> </td> <td> ";
				html += "<input type=hidden name=des" + arry[0] + count + " value='" + arry[2] + "'>";
				html += arry[2] + "</td> </tr>";
			}

			out.println(html);
			count++;
                }

%>
</table>
<input type=hidden value=update name=cmd>
<input type=hidden name=tol value="<%=count%>">
<input type=submit value="Update Configuration">
</form>

<br>
<hr>
<b> Update Traditional Chinese Converstion File ("TtoSChineseConversionTable.xlsx'): </b>
<br> &nbsp;&nbsp;&nbsp;&nbsp;
        "TtoSChineseConversionTable.xlsx" is a mapping table for converting simplifed Chinese to traditional Chinese, or vice versa. The Excel file must in the formal as follow: the 1st column contains the unitcode of tradictional Chinese characters while the 2nd column contains traditional Chinese characters. The 3th and 4th columns are siimplifed Chinese counterpart.
<br>
<br>
Current File: <a href="/cat/conf/TtoSChineseConversionTable.xlsx" target="_blank"> TtoSChineseConversionTable.xlsx </a> <br>
Default File: <a href="/cat/conf/TtoSChineseConversionTable_default.xlsx" target="_blank"> TtoSChineseConversionTable_default.xlsx </a> 
<form enctype="multipart/form-data" method="post">
<input type="file" name="TtoSChineseConversionTableFile"><br>
<br>
<b> <font color=red> Warning: update with cautions, wrong format and/or content of the file would corrupt the whole program.</font> </b> 
<br>
<input type=submit value="Update the TtoSChineseConversionTable file">
</form>

<br>
<hr>
<b> Update Correcting Traditional Chinese Converstion File ("chineseAI.txt'): </b>
<br> &nbsp;&nbsp;&nbsp;&nbsp;
        "chineseAI.txt" is used for the feature of converting simplified Chinese to traditioinal Chinese to correct mis-converted Chinese characters. Each line contains a correction instruction. For example the line
        "&#39740;&#31296;&#23376;_&#35895;2" will instruct the program to correct the 2nd character of "&#39740;&#31296;&#23376;" to "&#35895;".
<br>
<br>
Current File: <a href="/cat/conf/chineseAI.txt" target="_blank"> chineseAI.txt </a> <br>
Default File: <a href="/cat/conf/chineseAI_default.txt" target="_blank"> chineseAI_default.txt </a>
<form enctype="multipart/form-data" method="post">
<input type="file" name="chineseAIFile"><br>
<b> <font color=red> Warning: update with cautions, wrong format and/or content of the file would corrupt the whole program.</font> </b> 
<br>
<input type=submit value="Update the ChineseAI file">
</form>
</body>
</html>
