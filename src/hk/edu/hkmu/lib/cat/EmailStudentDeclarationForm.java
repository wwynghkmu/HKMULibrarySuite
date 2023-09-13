package hk.edu.hkmu.lib.cat;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.sql.*;
import org.apache.commons.lang3.StringUtils;
import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;
import hk.edu.hkmu.lib.*;
import hk.edu.hkmu.lib.acq.Config;

import javax.activation.*;

/**
 * This class is used by the class AcqNewArrivalMonthReportBySch under
 * hk.edu.ouhk.lib to email the fetched reports.
 * 
 * @author Wai-yan NG
 * @version 1.0
 * @since Jan 29, 2019
 */

public class EmailStudentDeclarationForm {
	DataSource source;
	private String tos;
	private String toAddresses[];
	private Properties properties;
	private Session sess;
	private MimeMessage message;
	private int batchID;
	private String invitations[][];
	private String checkDate = "";
	private LogWriter logwriter;
	private BodyPart messageBodyPart;
	private Multipart multipart;
	private boolean reminder = false;

	/**
	 * Initialize an object; in order to send an email report; report file must be
	 * set via setReportFileStr() and sendEmail() must be called.
	 */
	public EmailStudentDeclarationForm() {

		Config.init();
		logwriter = new LogWriter(hk.edu.hkmu.lib.Config.SERVER_LOCAL_ROOT + "/cat/logs/");
		/*
		 * String workingdir = System.getProperty("user.dir"); File dir = new
		 * File(workingdir + "\\logs"); try { logwriter = new
		 * LogWriter(dir.getCanonicalPath()); } catch (Exception e) {
		 * 
		 * }
		 */
		logwriter.setLogFile("emailStudentSelfDeclarationForm.txt");
		fetchReportFromDB();
		initEmailSystem();
		setTos(Config.VALUES.get("REPORTEMAIL"));
		sendEmail();
		logwriter.close();
	}

	public EmailStudentDeclarationForm(String acaYear, String id, String title) {
		Config.init();
		logwriter = new LogWriter(hk.edu.hkmu.lib.Config.SERVER_LOCAL_ROOT + "/cat/logs/");
		/*
		 * String workingdir = System.getProperty("user.dir"); File dir = new
		 * File(workingdir + "\\logs"); try { logwriter = new
		 * LogWriter(dir.getCanonicalPath()); } catch (Exception e) {
		 * 
		 * }
		 */

		logwriter.setLogFile("emailStudentSelfDeclarationForm.txt");
		fetchReportFromDB(acaYear, id, title, false);
		initEmailSystem();
		setTos(Config.VALUES.get("REPORTEMAIL"));
		sendEmail();
		logwriter.close();
	}

	public EmailStudentDeclarationForm(String acaYear, String id, String title, boolean reminder) {
		Config.init();
		logwriter = new LogWriter(hk.edu.hkmu.lib.Config.SERVER_LOCAL_ROOT + "/cat/logs/");
		/*
		 * String workingdir = System.getProperty("user.dir"); File dir = new
		 * File(workingdir + "\\logs"); try { logwriter = new
		 * LogWriter(dir.getCanonicalPath()); } catch (Exception e) {
		 * 
		 * }
		 */
		this.reminder = reminder;
		logwriter.setLogFile("emailStudentSelfDeclarationForm.txt");
		fetchReportFromDB(acaYear, id, title, reminder);
		initEmailSystem();
		setTos(Config.VALUES.get("REPORTEMAIL"));
		sendEmail();
		this.reminder = reminder;
		logwriter.close();
	}

	public void fetchReportFromDB(String acaYear2, String id2, String title2, boolean reminder) {

		try {

			Class.forName(Config.JDBC_DRIVER);
			Connection conn = null;
			Statement stmt = null;
			conn = DriverManager.getConnection(Config.DB_URL, Config.USER, Config.PASS);
			title2 = title2.replaceAll("'", "''");
			String sql = "select * from catStudentSelfDeclaration where (signed IS NULL OR signed = 0) and (sentInvEmail IS NULL or sentInvEmail = 0) and academicYear='"
					+ acaYear2 + "' and studentID='" + id2 + "' and title LIKE '" + title2 + "%'";

			if (reminder) {
				sql = "select * from catStudentSelfDeclaration where (signed IS NULL OR signed = 0) and academicYear='"
						+ acaYear2 + "' and studentID='" + id2 + "' and title LIKE '" + title2 + "%'";

			}

			System.out.println(sql);

			stmt = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			ResultSet rs = stmt.executeQuery(sql);
			rs.last();
			invitations = new String[rs.getRow()][7];
			int i = 0;
			rs.beforeFirst();

			while (rs.next()) {

				String acaYear = rs.getString("academicYear");
				String studentName = rs.getString("studentName");
				String studentID = rs.getString("studentID");
				String title = rs.getString("title");
				String school = rs.getString("school");
				String email = rs.getString("studentEmail");
				int sentInvEmail = rs.getInt("sentInvEmail");

				invitations[i][0] = acaYear;
				invitations[i][1] = studentName;
				invitations[i][2] = studentID;
				invitations[i][3] = title;
				invitations[i][4] = school;
				invitations[i][5] = email;
				invitations[i][6] = sentInvEmail + "";

				i++;
			}
			rs.close();
			stmt.close();
			conn.close();

			checkDate = invitations[0][1];
			checkDate = checkDate.replaceAll(" .*", "");

		} catch (Exception e) {
			logwriter.out(e.getMessage());
		}

	}

	public void fetchReportFromDB() {

		try {

			Class.forName(Config.JDBC_DRIVER);
			Connection conn = null;
			Statement stmt = null;
			conn = DriverManager.getConnection(Config.DB_URL, Config.USER, Config.PASS);
			String sql = "select * from catStudentSelfDeclaration where (signed IS NULL OR signed = 0) and (sentInvEmail IS NULL or sentInvEmail = 0)";
			stmt = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			ResultSet rs = stmt.executeQuery(sql);
			rs.last();
			invitations = new String[rs.getRow()][7];
			int i = 0;
			rs.beforeFirst();

			while (rs.next()) {

				String acaYear = rs.getString("academicYear");
				String studentName = rs.getString("studentName");
				String studentID = rs.getString("studentID");
				String title = rs.getString("title");
				String school = rs.getString("school");
				String email = rs.getString("studentEmail");
				int sentInvEmail = rs.getInt("sentInvEmail");
				System.out.println("sentInvEmail: " + sentInvEmail);

				invitations[i][0] = acaYear;
				invitations[i][1] = studentName;
				invitations[i][2] = studentID;
				invitations[i][3] = title;
				invitations[i][4] = school;
				invitations[i][5] = email;
				invitations[i][6] = sentInvEmail + "";
				i++;

			}
			rs.close();
			stmt.close();
			conn.close();

			checkDate = invitations[0][1];
			checkDate = checkDate.replaceAll(" .*", "");

		} catch (Exception e) {
			logwriter.out(e.getMessage());
			e.printStackTrace();
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

			message.setFrom(
					new InternetAddress(Config.VALUES.get("EMAILSENDADDR"), Config.VALUES.get("EMAILSENDERSDFNAME")));

			if (toAddresses != null) {
				message.addRecipient(Message.RecipientType.TO, new InternetAddress(toAddresses[0]));
				for (int i = 1; i < toAddresses.length; i++) {
					message.addRecipient(Message.RecipientType.BCC, new InternetAddress(toAddresses[i]));
				}
			}
		} catch (AddressException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * sendEmail report, assuming setReportFileStr() is properly called.
	 */
	public void sendEmail() {

		try {
			hk.edu.hkmu.lib.cat.Config.init();
			String sender = hk.edu.hkmu.lib.cat.Config.VALUES.get("emailSenderSdf");
			String senderName = hk.edu.hkmu.lib.cat.Config.VALUES.get("emailSenderSdfName");

			if (sender != null)
				message.setFrom(new InternetAddress(sender, senderName));

			MessageDigest md = MessageDigest.getInstance("SHA-256");
			String digestMsg = "";
			Class.forName(Config.JDBC_DRIVER);
			Connection conn = null;
			Statement stmt = null;
			conn = DriverManager.getConnection(Config.DB_URL, Config.USER, Config.PASS);

			for (int i = 0; i < invitations.length; i++) {
				digestMsg = invitations[i][0] + invitations[i][1] + invitations[i][2]
						+ new Timestamp(System.currentTimeMillis());
				;
				md.update(digestMsg.getBytes());
				byte[] digest = md.digest();
				StringBuffer hexString = new StringBuffer();
				for (int j = 0; j < digest.length; j++) {
					hexString.append(Integer.toHexString(0xFF & digest[j]));
				}
				String recipient = StringUtils.chomp(invitations[i][5]);

				message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipient.trim()));

				String[] receipents = hk.edu.hkmu.lib.cat.Config.VALUES.get("invitationEmailCC").split(",");

				String logMsg = "Recipient: " + recipient.trim() + " ;";

				for (int j = 0; j < receipents.length; j++) {
					message.addRecipient(Message.RecipientType.BCC, new InternetAddress(receipents[j].trim()));
				}

				message.setSubject(hk.edu.hkmu.lib.cat.Config.VALUES.get("emailTitleSdf"));

				if (reminder)
					message.setSubject(hk.edu.hkmu.lib.cat.Config.VALUES.get("emailReminderTitleSdf"));

				String msgText = "";
				String tableText = "";
				tableText = hk.edu.hkmu.lib.cat.Config.VALUES.get("email1stParaSdf");
				tableText += "<br>";
				tableText += "<table border=1>";
				tableText += "<tr> <td> Student ID </td> <td> Student Name </td> <td>Student Work Title</td> <td> School </td> </tr> \n";
				tableText += "<tr> <td>" + invitations[i][2] + "</td> <td> " + invitations[i][1] + "</td> <td>"
						+ invitations[i][3] + "</td> <td> " + invitations[i][4] + "</td> </tr> \n";
				
				logMsg += "Work: " + invitations[i][2] + "-" + invitations[i][1] + "-" + invitations[i][3] + "-"
						+ invitations[i][4] + " ;";
				logwriter.out(logMsg);
				
				tableText += "</table>";
				tableText += "<br>";
				tableText += "<br>";
				tableText += hk.edu.hkmu.lib.cat.Config.VALUES.get("email2ndParaSdf");
				tableText += "<br>";
				tableText += hk.edu.hkmu.lib.cat.Config.VALUES.get("sdfFormServerBase") + hexString;
				tableText += "<br>";
				tableText += hk.edu.hkmu.lib.cat.Config.VALUES.get("email3rdParaSdf");
				tableText += "<br>";
				tableText += "<br>";

				tableText += hk.edu.hkmu.lib.cat.Config.VALUES.get("email4thParaSdf");
				tableText += "<br> <br>";

				tableText += hk.edu.hkmu.lib.cat.Config.VALUES.get("emailSignatureSdf");

				msgText = tableText;

				message.setContent(msgText, "text/html; charset=UTF-8");
				Transport.send(message);
				String sql = "UPDATE catStudentSelfDeclaration SET emailtime=NOW(), sentInvEmail = true, digest = '"
						+ hexString + "' where studentID ='" + invitations[i][2] + "' and academicyear = '"
						+ invitations[i][0] + "' and title='" + invitations[i][3].replaceAll("'", "''") + "'";

				if (reminder)
					sql = "UPDATE catStudentSelfDeclaration SET emailtime=NOW(), sentInvEmail = "
							+ (Integer.parseInt(invitations[i][6]) + 1) + ", digest = '" + hexString
							+ "' where studentID ='" + invitations[i][2] + "' and academicyear = '" + invitations[i][0]
							+ "' and title='" + invitations[i][3].replaceAll("'", "''") + "'";

				stmt = conn.prepareStatement(sql);
				try {

					stmt.executeUpdate(sql);
					if (stmt != null)
						stmt.close();

				} catch (Exception e) {
					e.printStackTrace();
				}

			}
			conn.close();

		} catch (

		Exception e) {
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
