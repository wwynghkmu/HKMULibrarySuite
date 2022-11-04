<%@ page import="hk.edu.csids.bookquery.*"%>
<%@ page import="java.sql.*"%>
<%@ page import="java.util.*"%>
<%@ page import="java.text.*"%>
<%@ page import="java.io.*,java.util.*, javax.servlet.*"%>
<%@include file="menu.jsp"%>

<script language="javascript">
	function validateNewForm(){
		var schCode = document.forms["newForm"]["schCode"].value;
		var schName = document.forms["newForm"]["schName"].value;
		var codes = document.forms["newForm"]["codes"].value;
		var emails = document.forms["newForm"]["emails"].value;

		if(schName == ""){
			alert("School name cannot be empty!");
			return false;
		}

		if(codes == ""){
			alert("Budget codes cannot be empty!");
			return false;
		}

		if(emails == ""){
			alert("Emails cannot be empty!");
			return false;
		}
/*
		if(!/^[a-zA-Z]+$/.test(schCode)){
			alert("School code allows alphabet Only!");
			return false;
		}
*/
 
		var emailArry = emails.split(",");
		for(var i=0; i< emailArry.length; i++){
			if(!emailArry[i].match(/.*@.*/)){
				alert("Email string '" + emails + "' is not in correct format. Pls check and reenter.");
				return false;
			}
                } 

		return true;
	}

        function validateUpdateForm(){
                var total = document.forms["updateForm"]["budtol"].value;
                for(var i=0;i<total;i++){
                        var schCode = document.forms["updateForm"]["schCode" + i].value;
                        var schName = document.forms["updateForm"]["schName" + i].value;
                        var codes = document.forms["updateForm"]["codes" + i].value;
                        var emails = document.forms["updateForm"]["emails" + i].value;
/*
                        if(!/^[a-zA-Z]+$/.test(schCode)){
                                alert("School code in row " + (i+1) + " allows alphabet Only!");
                                return false;
                        }
*/
                        if(schName == ""){
                                alert("School name in row " + (i+1) + " cannot be empty!");
                                return false;
                        }       
                        if(codes == ""){
                                alert("Budget codes in row " + (i+1) + " cannot be empty!");
                                return false;
                        }
                        var emailArry = emails.split(",");
                        for(var j=0; j< emailArry.length; j++)
                                if(!emailArry[j].match(/.*@.*/)){
                                        alert("Email string '" + emails + "' in row " + (i+1) + " is not in correct format. Pls check and reenter.");
                                        return false;
                                }
                        } 

                return true;
        }

</script>
<br>
<br>
<br>
<br>
<br>
<br>
<h3> <b> Configiguration for ACQ Tools </b> </h3>
<%
	if(authen!=null && !authen.isAuthenticated())
        	response.sendRedirect("/acq/index.jsp?logout=yes");
	String cmd = request.getParameter("cmd");
	String budtol = request.getParameter("budtol");
        String basedir = request.getServletContext().getRealPath("/") + "acq/";
        String webinfdir = request.getServletContext().getRealPath("/") + "/WEB-INF/classes/hk/edu/ouhk/lib/acq/";
	String logdir = basedir + "logs/";
        String budgetCodeFilePath = basedir + "conf/budgetCodes.txt";
        String budgetCodeWebFilePath = webinfdir + "budgetCodes.txt";
        String logFilePath = logdir + "configLog.txt";
	if(cmd!=null){
		if(cmd.equals("update")){
			String action = request.getParameter("action");
			if(action.equals("Update Configuration")){
				String configUpdate = "";
				int t=Integer.parseInt(budtol);
				for(int i=0;i<t;i++){
					String schName = request.getParameter("schName" + i);
					String schCode = request.getParameter("schCode" + i);
					String updateCode = request.getParameter("codes" + i);
					String emails = request.getParameter("emails" + i);
				
					configUpdate += schCode + "@" + schName + "~" + updateCode + "~" + emails;
					if(i<=t-1)
						configUpdate += "\n";
				}
        	                File file = new File(budgetCodeFilePath);
                	        file.delete();
                        	Writer objWriter = new BufferedWriter(new FileWriter(budgetCodeFilePath));
	                        objWriter.write(configUpdate);
        	                objWriter.flush();
                	        objWriter.close(); 
	                        file = new File(budgetCodeWebFilePath);
        	                objWriter = new BufferedWriter(new FileWriter(budgetCodeWebFilePath));
                	        objWriter.write(configUpdate);
                        	objWriter.flush();
	                        objWriter.close(); 
				out.println("<b> <font color=red> CONFIG UPDATED </font> </b>");
				BufferedWriter writer = new BufferedWriter(new FileWriter(logFilePath, true));
				writer.write( new java.util.Date()  + "\t" + request.getRemoteAddr() + "\t" + authen.getUserid() + "\tACQ LIB Tools budget codes config updated: \n" + configUpdate + "\n\n");
				writer.close();
			}
			if(action.equals("Delete Selected Row")){
				String schCode = request.getParameter("deleteSchCode");
				if(schCode == null){
					out.println("<b> <font color=red> NO school is selected. Please try again.</font> </b>");
				} else {
                			BufferedReader br = new BufferedReader(new FileReader(budgetCodeFilePath));
			                String line = ""; 
			                String sch[] = null;
					int count=0;
					String configUpdate = "";
			                while((line = br.readLine()) != null){
						if(!line.matches("^" + schCode + "@.*"))
							configUpdate += line + "\n";
					}
	        	                File file = new File(budgetCodeFilePath);
        	        	        file.delete();
                	        	Writer objWriter = new BufferedWriter(new FileWriter(budgetCodeFilePath));
	                	        objWriter.write(configUpdate);
	        	                objWriter.flush();
        	        	        objWriter.close(); 
	        	                file = new File(budgetCodeWebFilePath);
	        	                objWriter = new BufferedWriter(new FileWriter(budgetCodeWebFilePath));
        	        	        objWriter.write(configUpdate);
                	        	objWriter.flush();
	                	        objWriter.close(); 
					out.println("<b> <font color=red> School " + schCode + " is DELETEED </font> </b>");
					BufferedWriter writer = new BufferedWriter(new FileWriter(logFilePath, true));
					writer.write( new java.util.Date()  + "\t" + request.getRemoteAddr() + "\t" + authen.getUserid() + "\tACQ LIB Tools budget codes config deleted:\n " + configUpdate + "\n\n");
					writer.close();
				}
			}
		}
		if(cmd.equals("add")){
			String configUpdate = "";
			String schName = request.getParameter("schName");
			String schCode = request.getParameter("schCode");
			String updateCode = request.getParameter("codes");
			String emails = request.getParameter("emails");
			if(schName == "" || schCode == "" || updateCode == "" || emails == ""){
				out.println("NO field is allowed to be null, pls reenter");
			} else {
				configUpdate += schCode + "@" + schName + "~" + updateCode + "~" + emails;
        	                Writer objWriter = new BufferedWriter(new FileWriter(budgetCodeFilePath,true));
                	        objWriter.write(configUpdate);
                        	objWriter.flush();
	                        objWriter.close(); 
                        	objWriter = new BufferedWriter(new FileWriter(budgetCodeWebFilePath,true));
	                        objWriter.write(configUpdate);
        	                objWriter.flush();
                	        objWriter.close(); 
				out.println("<b> <font color=red> CONFIG ADDED </font> </b>");
				BufferedWriter writer = new BufferedWriter(new FileWriter(logFilePath, true));
				writer.write( new java.util.Date()  + "\t" + request.getRemoteAddr() + "\t" + authen.getUserid() +  "\tACQ LIB Tools budget codes config added: \n" + configUpdate + "\n\n");
				writer.close();
			}
		}
	}

	
%>


<hr>
<b> Update New Arrival List configuration </b>
<div align=center>
<form id="updateForm" onsubmit="return validateUpdateForm()">
<table border=1 width=70% class='table table-hover'>
<thead> <tr> <td> No </td>  <td> School Code <br> </td> <td> School Name </td> <td> Budget Codes </td> <td> Email RPT addr (',' for delimiter.) <br> The 1st entry is the TO-addr while the 2nd and onwards are CC-addr. </td> <td> Delete? </td></tr></thead>
<%
                BufferedReader br = new BufferedReader(new FileReader(budgetCodeFilePath));
                String line = ""; 
                String sch[] = null;
		int count=0;
                while((line = br.readLine()) != null){
                        String arry[] = line.split("~");
                        String schStr = arry[0];
                        String codeStr = arry[1];
                        sch = schStr.split("@");
			String html = "<tr> <td> " + (count+1) + ". </td> <td>";
			html += "<input name=schCode" + count + " type=text value=" + sch[0] + "> </td> <td> ";
			html += "<input name=schName" + count + " type=text value='" + sch[1] + "' size=35></td> <td>";
			html += "<input name=codes" + count + " type=text value='" + codeStr + "' size=50></td> <td>"; 
			html += "<input size=90px name=emails" + count + " type=text value='" + arry[2] + "' size=50></td> <td>"; 
			html += "<input type='radio' name='deleteSchCode' value='" + sch[0] + "' </td> </tr>"; 
			out.println(html);
			count++;
                }

%>
</table>

<input type=hidden value=update name=cmd>
<input type=hidden name=budtol value="<%=count%>">
<input type=submit name=action value="Update Configuration">
<input type=submit name=action value="Delete Selected Row">
</form>
<br>
<b>
Add New School:
</b>
<form id="newForm" onsubmit="return validateNewForm()">
<table border=1 width=50% class='table table-hover'>
<thead>
<tr> <td> School Code <br> </td> <td> School Name </td> <td> Budget Codes </td> <td> Email RPT addr (',' for delimiter) <br>The 1st entry is the TO-addr while the 2nd and onwards are CC-addr. </td></tr>
</thead>
<tr> <td> <input type=text name=schCode>  </td> <td> <input type=text name=schName> </td> <td> <input type=text name=codes size=40px> </td> <td> <input type=text name=emails size=60px> </td> </tr>
</table>
<input type=hidden value=add name=cmd>
<input type=submit value="Add New School Budget">
</form>
</div>
</body>
</html>
