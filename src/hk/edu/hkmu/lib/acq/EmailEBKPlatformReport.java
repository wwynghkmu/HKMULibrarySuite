package hk.edu.hkmu.lib.acq;

import java.sql.*;
import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;

import hk.edu.hkmu.lib.*;

import javax.activation.*;

/**
 * This class is used by the class AcqNewArrivalMonthReportBySch under
 * hk.edu.ouhk.lib to email the fetched reports.
 * 
 * @author Wai-yan NG
 * @version 1.0
 * @since Jan 29, 2019
 */

public class EmailEBKPlatformReport {
	DataSource source;
	private String tos;
	private String toAddresses[];
	private Properties properties;
	private Session sess;
	private MimeMessage message;
	private int batchID;
	private String platforms[][];
	private String checkDate = "";
	private LogWriter logwriter;

	/**
	 * Initialize an object; in order to send an email report; report file must be
	 * set via setReportFileStr() and sendEmail() must be called.
	 */
	public EmailEBKPlatformReport() {
		Config.init();
		logwriter = new LogWriter(hk.edu.hkmu.lib.Config.SERVER_LOCAL_ROOT);
		logwriter.setLogFile("eBkPlatfromChkEmailRPTLog.txt");
		initEmailSystem();
		setTos(Config.VALUES.get("REPORTEMAIL"));
		logwriter.close();
	}

	public EmailEBKPlatformReport(int batchID) {
		Config.init();
		logwriter = new LogWriter(hk.edu.hkmu.lib.Config.SERVER_LOCAL_ROOT);
		logwriter.setLogFile("eBkPlatfromChkEmailRPTLog.txt");
		initEmailSystem();
		setTos(Config.VALUES.get("REPORTEMAIL"));
		this.batchID = batchID;
		fetchReportFromDB();
		sendEmail();
		logwriter.close();
	}

	public void fetchReportFromDB() {

		try {
			if (batchID != 0) {
				Class.forName(Config.JDBC_DRIVER);
				Connection conn = null;
				Statement stmt = null;
				conn = DriverManager.getConnection(Config.DB_URL, Config.USER, Config.PASS);
				String sql = "SELECT * FROM ebkplatformcheckingreport where batchID = " + this.batchID
						+ " Order By timestamp";

				stmt = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
				ResultSet rs = stmt.executeQuery(sql);
				rs.last();
				platforms = new String[rs.getRow()][7];
				int i = 0;
				rs.beforeFirst();

				while (rs.next()) {

					String timestamp = rs.getString("timestamp");
					String platform = rs.getString("platform");
					String searchKeyword = rs.getString("searchKeyword");
					String checkStatus = rs.getString("checkStatus");
					String remark = rs.getString("remark");

					platforms[i][0] = this.batchID + "";
					platforms[i][1] = timestamp;
					platforms[i][2] = platform;
					platforms[i][3] = searchKeyword;
					platforms[i][4] = checkStatus;
					platforms[i][5] = remark;
					i++;

				}
				rs.close();
				stmt.close();
				conn.close();

				checkDate = platforms[0][1];
				checkDate = checkDate.replaceAll(" .*", "");
			}

		} catch (Exception e) {
			logwriter.out(e.getMessage());
		}

	}

	/**
	 * Set the SMTP server (local) and sending email addresses.
	 */
	private void initEmailSystem() {
		try {
			properties = System.getProperties();
			properties.setProperty("mail.smtp.host", "localhost");
			sess = Session.getDefaultInstance(properties);
			message = new MimeMessage(sess);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logwriter.out(e.getMessage());
		}
	}

	/**
	 * sendEmail report, assuming setReportFileStr() is properly called.
	 */
	public void sendEmail() {

		try {
			String sender = Config.VALUES.get("eBkPlatformCheckingEMailReportSender");
			message.setFrom(new InternetAddress(sender));

			String recipient = Config.VALUES.get("eBkPlatformCheckingEMailReportRecipient");
			String[] receipents = recipient.split(",");
			for (int i = 0; i < receipents.length; i++) {
				message.addRecipient(Message.RecipientType.TO, new InternetAddress(receipents[i].trim()));
			}

			message.setSubject("EBook Platform Checking Daily Report");
			String msgText = "";
			String tableText = "";
			tableText = "<table border=1>\n";
			if (batchID != 0) {
				tableText += "<tr> <td> <b> Platform </b> </td> <td> <b> Time Checked </b> </td> <td> <b> Keyword Searched </b> </td> <td> <b> Checking Result </b> </td>\n";
				for (int i = 0; i < platforms.length; i++) {
					if (platforms[i][4].contains("FAILED")) {
						msgText = "NO";

						tableText += "<tr> <td>" + platforms[i][2] + "</td> <td> " + platforms[i][1] + "</td> <td>"
								+ platforms[i][3] + "</td> <td> " + platforms[i][4] + "</td> </tr> \n";
						tableText = tableText.replaceAll("FAILED", "<font color=red> <b> FAILED </b> </font>");
					}
				}
				tableText += "</table>\n";

				if (msgText.equals("")) {
					msgText = "<b> There is no failing eBook Platform today. </b>";
				} else {
					msgText = tableText;
				}

				msgText += "<br> <br> This is only a brief report. If you want to read the full report, please visit <a href='https://"
						+ hk.edu.hkmu.lib.Config.VALUES.get("TSSERVICE_SERVER") + "/acq/eBkPlatformReport.jsp'>";
				msgText += "https://" + hk.edu.hkmu.lib.Config.VALUES.get("TSSERVICE_SERVER")
						+ "/acq/eBkPlatformReport.jsp</a>.";
				
			} else {
				msgText = "There is no report of the date. Something is wrong. Please contact Systems for technical help.";
			}
			msgText += "<br> <br> Please email to <a href='mailto:libsys@hkmu.edu.hk'> libsys@hkmu.edu.hk </a> for any technical difficulty.";
			logwriter.out(msgText);
			message.setContent(msgText, "text/html; charset=UTF-8");
			Transport.send(message);

		} catch (Exception e) {
			e.printStackTrace();
			logwriter.out(e.getMessage());
		}
	}

	/**
	 * Set the recipient email address.'.
	 * 
	 * @param str The string of email address separated by ','.
	 */
	public void setTos(String str) {
		if (str == null)
			str = "";
		tos = str;

		toAddresses = str.split(",");
		for (int i = 0; i < toAddresses.length; i++) {
			toAddresses[i] = toAddresses[i].trim();
		}
	}

	public String getTos() {
		return tos;
	}

}
