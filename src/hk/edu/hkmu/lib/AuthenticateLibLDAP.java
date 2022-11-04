package hk.edu.hkmu.lib;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;
import javax.naming.*;
import javax.naming.directory.*;

import net.bytebuddy.asm.Advice.This;

/**
 * 
 * This class serve to authenticate users against OUHK LIB UAS LDAP server. And,
 * the class can check if users authenticated against UAS are valid users of
 * certain by the access list checking.
 * 
 * @author Wai-yan NG
 * @author wwyng@ouhk.edu.hk
 * @version 1.0
 * @since 10 Mar 2020
 */
public class AuthenticateLibLDAP {
	private String userid;
	private String password;
	private String patronType;
	private String barcode;
	private String logintime;
	private String ip;

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public boolean isAuthenticated() {
		return authenticated;
	}

	public void setAuthenticated(boolean authenticated) {
		this.authenticated = authenticated;
	}

	public String getLogintime() {
		return logintime;
	}

	public void setLogintime(String logintime) {
		this.logintime = logintime;
	}

	public String getBarcode() {
		return barcode;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

	private String givenName;
	private String surname;

	public String getPatronType() {
		return patronType;
	}

	public void setPatronType(String patronType) {
		this.patronType = patronType;
	}

	public String getGivenName() {
		return givenName;
	}

	public void setGivenName(String givenName) {
		this.givenName = givenName;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	private boolean authenticated;

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getBase() {
		return Config.VALUES.get("LDAPBASE");
	}

	public String getDn() {
		return "uid=" + getUserid() + "," + getBase();
	}

	public String getLdapUrl() {
		return Config.VALUES.get("LDAPURL");
	}

	/*
	 * Constructor for the case which authenticates against the LIB UAS (host
	 * defined in config.txt). User name and password must be set before calling
	 * this.
	 * 
	 * @param userid The login account.
	 * 
	 * @param password The password for logging in.
	 * 
	 * 
	 */
	public AuthenticateLibLDAP(String userid, String password) {
		super();
		Config.init();
		hk.edu.hkmu.lib.Config.init();
		setAuthenticated(false);
		setUserid(userid);
		setPassword(password);
		authenticate(null, "");
	}

	/*
	 * Constructor for the case which authenticates against the LIB UAS (host
	 * defined in config.txt). User name and password must be set before calling
	 * this.
	 * 
	 * @param userid The login account.
	 * 
	 * @param password The password for logging in.
	 * 
	 * @param ip The IP address for logging.
	 * 
	 */
	public AuthenticateLibLDAP(String userid, String password, String ip) {
		super();
		Config.init();
		setAuthenticated(false);
		setUserid(userid);
		setPassword(password);
		setIp(ip);
		authenticate(null, "");
	}

	/*
	 * Constructor for the case which authenticates against the LIB UAS (host
	 * defined in config.txt). User name and password must be set before calling
	 * this.
	 * 
	 * @param userid The login account.
	 * 
	 * @param password The password for logging in.
	 * 
	 * @param ip The IP address for logging.
	 * 
	 * @param allowUserList The list of allowed users in addition to authentication
	 * against UAS.F
	 * 
	 * @param from Onto which system users are going to login.
	 * 
	 */
	public AuthenticateLibLDAP(String userid, String password, String ip, String allowUserList, String from) {
		super();
		Config.init();
		setAuthenticated(false);
		setUserid(userid);
		setPassword(password);
		setIp(ip);
		authenticate(allowUserList, from);
	}

	/**
	 * 
	 * Authenticate against the LIB UAS (host defined in config.txt). User name and
	 * password must be set before calling this.
	 * 
	 * @param allowUserList The list of allowed users in addition to authentication
	 *                      against UAS.
	 * @param from          Onto which system users are going to login.
	 * 
	 */
	public void authenticate(String allowUserList, String from) {

		Hashtable<String, String> environment = new Hashtable<String, String>();
		environment.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		environment.put(Context.PROVIDER_URL, getLdapUrl());
		environment.put(Context.SECURITY_AUTHENTICATION, "simple");
		environment.put(Context.SECURITY_PRINCIPAL, getDn());
		environment.put(Context.SECURITY_CREDENTIALS, getPassword());

		String systemFrom = "";
		if (allowUserList != null) {
			systemFrom = "from " + from;
			allowUserList = allowUserList.toLowerCase();
		}

		try {
			DirContext authContext = new InitialDirContext(environment);
			Attributes answer = authContext.getAttributes(getDn());

			setPatronType(answer.get("patrontype").toString().replaceAll("^.*:", ""));
			setGivenName(answer.get("givenname").toString().replaceAll("^.*:", ""));
			setSurname(answer.get("sn").toString().replaceAll("^.*:", ""));
			if (answer.get("barcode") != null) {
				setBarcode(answer.get("barcode").toString().replaceAll("^.*:", ""));
			}

			setLogintime(Instant.now().toString());

			// 'config' is not null means the person is trying to login to the system from a
			// specific module. The module name is in the variable 'systemFrom'.
			if (allowUserList != null) {
				System.out.println("allow list: " + allowUserList + " userid: " + this.getUserid().toLowerCase());
				if (allowUserList.contains(this.getUserid().toLowerCase())) {
					setAuthenticated(true);
					String logFilePath = Config.VALUES.get("LOGPATH");
					BufferedWriter writer = new BufferedWriter(new FileWriter(logFilePath
							+ hk.edu.hkmu.lib.Config.VALUES.get("FILEPROTECTIONKEY") + "-" + "authenticate.txt", true));
					writer.write(new java.util.Date() + "\t" + getUserid() + ":" + getPatronType() + ":" + getBarcode()
							+ ":" + getLogintime() + ":" + getGivenName() + "," + getSurname() + ":" + getIp()
							+ ":success against UAS " + systemFrom + "\n");
					writer.close();

				} else {
					// Authenticated but failed to pass the access list.
					setAuthenticated(false);
					System.out.println("allow list: " + allowUserList + " userid: " + this.getUserid().toLowerCase());
					String logFilePath = Config.VALUES.get("LOGPATH");
					BufferedWriter writer = new BufferedWriter(new FileWriter(logFilePath
							+ hk.edu.hkmu.lib.Config.VALUES.get("FILEPROTECTIONKEY") + "-" + "authenticate.txt", true));
					writer.write(new java.util.Date() + "\t" + getUserid() + ":" + getPatronType() + ":" + getBarcode()
							+ ":" + getLogintime() + ":" + getGivenName() + "," + getSurname() + ":" + getIp()
							+ ":FAILED against UAS " + systemFrom + "\n");
					writer.close();

				}
			} else {
				// 'config' is null means the person is just want to authenicate against UAS.
				setAuthenticated(true);
				String logFilePath = Config.VALUES.get("LOGPATH");
				BufferedWriter writer = new BufferedWriter(new FileWriter(
						logFilePath + hk.edu.hkmu.lib.Config.VALUES.get("FILEPROTECTIONKEY") + "-" + "authenticate.txt",
						true));
				writer.write(new java.util.Date() + "\t" + getUserid() + ":" + getPatronType() + ":" + getBarcode()
						+ ":" + getLogintime() + ":" + getGivenName() + "," + getSurname() + ":" + getIp()
						+ ":success against UAS \n");
				writer.close();

			}
		} catch (Exception e) {
			// Failed to authenticate against UAS.
			setAuthenticated(false);
			try {
				// The log path is prefixed the key "FILEPROTECTIONKEY" from
				// hk.edu.ouhk.lib.Config, for avoding others to guess the log file which
				// contains sensitive information
				String logFilePath = Config.VALUES.get("LOGPATH");
				BufferedWriter writer = new BufferedWriter(new FileWriter(
						logFilePath + hk.edu.hkmu.lib.Config.VALUES.get("FILEPROTECTIONKEY") + "-" + "authenticate.txt",
						true));
				writer.write(new java.util.Date() + "\t" + getUserid() + ":" + getPatronType() + ":" + getBarcode()
						+ ":" + getLogintime() + ":" + getGivenName() + "," + getSurname() + ":" + getIp()
						+ ":FAILED against UAS " + systemFrom + "\n");
				writer.close();
				e.printStackTrace();
			} catch (Exception e2) {
			}
		}
	}

}
