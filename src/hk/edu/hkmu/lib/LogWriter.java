package hk.edu.hkmu.lib;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.sql.*;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * This class writes logs for debugging. Logs are written on to the
 * sub-directory "logs" of the Java program is running while log path can be set
 * mannually.
 * 
 * @author Wai-yan NG
 * @author wwyng@ouhk.edu.hk
 * @version 1.0
 * @since Jan 29, 2019
 */

public class LogWriter {

	public String LOG_PATH = "";
	private BufferedWriter logWriter;

	public LogWriter() {
		init();
	}

	public LogWriter(String path) {
		init();
		System.out.println("INIT(): " + path);
		if (path != null || path != "")
			setLogPath(path);
	}

	private void init() {
		try {
			String workingdir = System.getProperty("user.dir");
			System.out.println("Working DIR: " + workingdir);
			File dir = new File(workingdir + "\\logs");
			System.out.println("working dir / path " + workingdir + "\\logs");
			if (!dir.exists())
				dir.mkdir();
			LOG_PATH = workingdir + "\\logs\\";
		} // end try

		catch (Exception e) {
			e.printStackTrace();
		} // end catch
	} // end updateConfig()

	private void setLogPath(String path) {
		System.out.println("Set log parth: " + path);
		LOG_PATH = path;
	}

	public String getLogPath() {
		return LOG_PATH;
	}

	public void close() {
		try {
			logWriter.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void setLogFile(String logfile) {
		try {
			System.out.println("log file: " + LOG_PATH + logfile);
			logWriter = new BufferedWriter(new FileWriter(LOG_PATH + logfile, true));

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void out(String str) {
		try {
			logWriter.write(StringHandling.getTodayDBFormat() + ": " + str + "\n");
			System.out.println(str);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void out(String str, boolean noTimeStamp) {
		try {
			if (noTimeStamp) {
				logWriter.write(StringHandling.getTodayDBFormat() + ": " + str + "\n");
			} else {
				logWriter.write(str + "\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
// end class LogWriter