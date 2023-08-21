package hk.edu.hkmu.lib.acq;

import java.io.File;
import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;

/**
 * This class is used by the class AcqNewArrivalMonthReportBySch under
 * hk.edu.ouhk.lib to email the fetched reports for Alma.
 * 
 * @author Wai-yan NG
 * @version 1.0
 * @since July 24 29, 2023
 */

public class EmailReportBySubjectAlma {
	private String schCode;
	private String reportFileStr;
	DataSource source;
	private String tos;
	private String toAddresses[];
	private Properties properties;
	private Session sess;
	private MimeMessage message;
	private BodyPart messageBodyPart;
	private Multipart multipart;

	/**
	 * Initialize an object; in order to send an email report; report file must be
	 * set via setReportFileStr() and sendEmail() must be called.
	 */
	public EmailReportBySubjectAlma() {
		initEmailSystem();
		setSchCode("");
		setReportFileStr("");
		setTos("");
		toAddresses = null;

		Config.init();
	}

	/**
	 * Initialize an object and an email report will be send right after this; the
	 * report file must in the form [schCode]-[any string].[extension].
	 * 
	 * @param reportFileStr the FULL path of the pre-recorded file; the file must
	 *                      exist before and in the form [schCode]-[any
	 *                      string].[extension].
	 * 
	 */
	public EmailReportBySubjectAlma(String reportFileStr) {
		Config.init();
		setReportFileStr(reportFileStr);
		setTos(Config.VALUES.get(schCode + "Email").toString());
		initEmailSystem();
		sendEmail();

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
			messageBodyPart = new MimeBodyPart();
			multipart = new MimeMultipart();
			message.setFrom(new InternetAddress(Config.VALUES.get("EMAILSENDADDR")));
			if (toAddresses != null) {
				message.addRecipient(Message.RecipientType.TO, new InternetAddress(toAddresses[0]));
				for (int i = 1; i < toAddresses.length; i++) {
					message.addRecipient(Message.RecipientType.CC, new InternetAddress(toAddresses[i]));
				}
			}
		} catch (AddressException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * sendEmail report, assuming setReportFileStr() is properly called.
	 */
	public void sendEmail() {

		String str = getReportFileStr();
		if (str == null) {
			reportFileStr = "";
			return;
		}
		try {
			String filename = str.replaceAll("^.*[\\\\|/]", "");
			String reportMonth = filename;
			if (reportMonth.matches(".*\\d\\d\\d\\d\\d\\d\\d\\d=\\d\\d\\d\\d\\d\\d\\d\\d.*")) {
				String[] tmp = reportMonth.split("-");
				reportMonth = tmp[1] + "-" + tmp[2];
			} else {
				String[] tmp = reportMonth.split("-");
				reportMonth = tmp[2];
			}
			reportMonth = reportMonth.replaceAll(".xlsx", "");
			String sch = hk.edu.hkmu.lib.acq.Config.VALUES.get(schCode);
			String subject = reportMonth + " " + Config.VALUES.get("NEWARRIVALEMAILSUBJECT") + " for " + sch;
			System.out.println(subject);
			File emptyFile = new File(str + ".empty");
			System.out.println("emp: " + emptyFile);
			
			messageBodyPart = new MimeBodyPart();
			message.setSubject(subject);
			if (!emptyFile.exists()) {
				messageBodyPart.setText(Config.VALUES.get("NEWARRIVALEMAILGREETING") + "\n\n"
						+ Config.VALUES.get("NEWARRIVALEMAILCONTENT1") + "\n\n"
						+ Config.VALUES.get("NEWARRIVALEMAILCONTENT2") + "\n\n"
						+ Config.VALUES.get("NEWARRIVALEMAILCOMPLIMENTARYCLOSE") + "\n"
						+ Config.VALUES.get("NEWARRIVALEMAILCOMPLIMENTARYSIGN"));
			} else {
				messageBodyPart.setText(Config.VALUES.get("NEWARRIVALEMAILGREETING") + "\n\n"
						+ Config.VALUES.get("NEWARRIVALEMAILCONTENTFOREMPTYREPORT") + "\n\n"
						+ Config.VALUES.get("NEWARRIVALEMAILCOMPLIMENTARYCLOSE") + "\n"
						+ Config.VALUES.get("NEWARRIVALEMAILCOMPLIMENTARYSIGN"));
			}

			multipart.addBodyPart(messageBodyPart);

			messageBodyPart = new MimeBodyPart();
			DataSource source = new FileDataSource(str);
			messageBodyPart.setDataHandler(new DataHandler(source));
			messageBodyPart.setFileName(filename);
			multipart.addBodyPart(messageBodyPart);
			message.setContent(multipart, "text/html; charset=UTF-8");
			Transport.send(message);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setSchCode(String str) {
		String filename = str.replaceAll("^.*[\\\\|/]", "");
		String filenameArry[] = filename.split("-");
		if (filenameArry[0] == null)
			str = "";
		else
			str = filenameArry[0];
		schCode = str;
	}

	/**
	 * Set the report file string and the school code.
	 * 
	 * @param reportFileStr The full path of the file and the file name must in the
	 *                      form [schCode]-[any string].[extension] and exist in the
	 *                      file system.
	 */
	public void setReportFileStr(String reportFileStr) {
		setSchCode(reportFileStr);
		if (reportFileStr == null)
			reportFileStr = "";
		this.reportFileStr = reportFileStr;
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

	public String getReportFileStr() {
		return reportFileStr;
	}

	public String getTos() {
		return tos;
	}

	public String getSchCode() {
		return schCode;
	}

}
