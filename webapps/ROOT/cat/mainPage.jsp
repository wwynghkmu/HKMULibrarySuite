<%@ page import="hk.edu.hkmu.lib.cat.*,hk.edu.hkmu.lib.*,hk.edu.hkmu.lib.bookquery.*"%>
<%@ page import="java.io.*,java.util.*,javax.servlet.*,javax.servlet.http.*"%>
<%@ page import="org.yaz4j.*"%>
<%@ page import="org.apache.commons.fileupload.*"%>
<%@ page import="org.apache.commons.fileupload.disk.*"%>
<%@ page import="org.apache.commons.fileupload.servlet.*"%>
<%@ page import="org.apache.commons.io.output.*"%>
<%@include file="menu.jsp"%>
<br>
<br>
<br>
<br>
<br>
<h3> <b> CAT Tools</b> </h3>
<hr>
<b> 1. CopyCat Get MARCs </b>
<br> <br>
<form enctype="multipart/form-data" method="post">
	<input type="hidden" name="cmd" id="cmd" value="cc">
	<input type="file" name="srcFile" id="srcFile"> <br> <br>
	<input type="submit" value="Get MARCs">
</form>
<hr>
<b>
2. SFX Enrichment
</b>
<br> <br>
<form enctype="multipart/form-data" method="post">
	<input type="hidden" name="cmd" id="cmd" value="sfx">
	<input type="file" name="srcFileSFX" id="srcFileSFX"> <br> <br>
	<input type="submit" value="Get SFX Enrichment">
</form>
<hr>
<%

        if(authen!=null && !authen.isAuthenticated())
                response.sendRedirect("/cat/index.jsp?logout=yes");
	String contentType = request.getContentType();
	String savedFile = "";
	String cmd = "";
	if ((contentType != null && contentType.indexOf("multipart/form-data") >= 0)) {
		String basedir = request.getServletContext().getRealPath("/") + "cat/";
		String filePath = "";
		String reportsFilePath = "";
		DiskFileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload(factory);
		try {
			List fileItems = upload.parseRequest(request);
			Iterator i = fileItems.iterator();
			while (i.hasNext()) {
				FileItem fi = (FileItem) i.next();

				if (fi.isFormField()){
					if(fi.getFieldName().equals("cmd"))
						cmd = fi.getString();
					if(cmd.equals("cc")){
						filePath = basedir + "requests/copycat";
						reportsFilePath = basedir + "reports/copycat";
					} else if(cmd.equals("sfx")){
						filePath = basedir + "requests/sfxenrich";
						reportsFilePath = basedir + "reports/sfxenrich";
					} //end if
					factory.setRepository(new File(filePath));
				} //end if

				if (!fi.isFormField()) {
					// Get the uploaded file parameters
					String now = StringHandling.getToday();
					String fieldName = fi.getFieldName();
					String fileName = fi.getName();
					String fileExt = fileName.replaceAll("^.*\\.", "");
					savedFile = now + "." + fileExt;
					boolean isInMemory = fi.isInMemory();
					long sizeInBytes = fi.getSize();
					if (!fileExt.equals("xlsx") && cmd.equals("cc")) {
						out.println("<font color=red> <b> Wrong file format; must be .xlsx (CopyCat) </b></font>");
					} else if (!fileExt.equals("xml-marc") && cmd.equals("sfx")) {
						out.println("<font color=red> <b> Wrong file format; must be .xml-marc (SFX Enrichment) </b></font>");
					} else {
						out.println("Start time: " + now + "<br>");
						File file = new File(filePath + "/" + savedFile);
						fi.write(file);
						out.println("Saved file: " + savedFile + "<br>");
						out.println("Report generation would take a quite long time.<br>");
						out.println("You may close this tab and check report later on the report page.<br>");
						out.println("<font color='red'> <div id='outMsg'> </div> </font>");
					} //end if
				} //end if
			} //end while
		} //end try

		catch (Exception e) {
	                out.println("<br>");
        	        out.println("<br>");
                	e.printStackTrace(new java.io.PrintWriter(out));
		}
	} //end if
%>

<script lanuage="javascript">
		var savedFile = "<%out.print(savedFile);%>";
		var cmd = "<%out.print(cmd);%>";
                var xhttp${resultStatus.index} = new XMLHttpRequest();
                xhttp${resultStatus.index}.onreadystatechange = function() {
                        if (xhttp${resultStatus.index}.readyState == 4 && xhttp${resultStatus.index}.status == 200) {
				var outMsg = document.getElementById("outMsg");
				outMsg.innerHTML = xhttp${resultStatus.index}.responseText; 
                        } //end if
                } //end function();
		if(savedFile!=""){
			if(cmd=="cc")
		                xhttp${resultStatus.index}.open("GET", "getmarcs.jsp?file=" + savedFile, true);
			if(cmd=="sfx")
		                xhttp${resultStatus.index}.open("GET", "getsfxenrich.jsp?file=" + savedFile, true);
        	        xhttp${resultStatus.index}.send();
		} //end if
		xhttp${resultStatus.index}.addEventListener("progress", function (evt) {
			if (evt.lengthComputable) {
				var percentComplete = evt.loaded / evt.total;
				console.log(Math.round(percentComplete * 100) + "%");
			}
		}, false);
        </script>


</html>
