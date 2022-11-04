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
 * This class serves as a collection of constants containing system specific
 * settings. This class reads config.txt at the same directory of the package
 * "hk.edu.ouhk.lib"
 * 
 * @author Wai-yan NG
 * @author wwyng@ouhk.edu.hk
 * @version 1.0
 * @since Jan 29, 2019
 */

public class Config {

	public static Map<String, String> VALUES = new HashMap<String, String>();
	public static Map<String, String> VALUESDES = new HashMap<String, String>();
	public static Map<String, String> VALUESMOD = new HashMap<String, String>();

	public static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
	public static final String TSSERVICE_SERVER = "tsservices.lib.hkmu.edu.hk";
	// public static final String TSSERVICE_SERVER_LOGIN = "tomcat";
	// public static final String TSSERVICE_SERVER_PWD = "tom959cat";
	public static final String TSSERVICE_SERVER_LOGIN = "tomdev";
	public static final String TSSERVICE_SERVER_PWD = "tom959dev";
	public static final String DB_URL = "jdbc:mysql://" + TSSERVICE_SERVER + ":3306/tsservices2";
	// public static final String USER = "root";
	// public static final String PASS = "987&ac151p?";
	public static final String USER = "tsservice2";
	public static final String PASS = "123456aB";
	public static String SERVER_LOCAL_ROOT = "";

	public static void init() {
		BufferedReader br = null;
		Connection conn = null;
		Statement stmt = null;

		try {

			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL, USER, PASS);

			String sql;
			sql = "SELECT * FROM configuration where session = 'lib'";
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);

			while (rs.next()) {
				String name = StringHandling.trimNewLineChar((rs.getString("name").trim()));
				String value = StringHandling.trimNewLineChar((rs.getString("value").trim()));
				String module = StringHandling.trimNewLineChar((rs.getString("module").trim()));
				String des = "NONE";
				if (rs.getString("description") != null)
					des = StringHandling.trimNewLineChar(rs.getString("description").trim());
				VALUES.put(name.toUpperCase(), value);
				VALUESDES.put(name.toUpperCase(), name + ": " + des);
				VALUESMOD.put(name.toUpperCase(), module); 
			}

			URL url = Config.class.getResource("config.txt");
			br = new BufferedReader(new FileReader(url.getPath()));
			String line;
			while ((line = br.readLine()) != null) {
				String[] para = line.split("~");
				VALUES.put(para[0], para[1]);

			} // end while

			SERVER_LOCAL_ROOT = VALUES.get("SERVERROOT");

		} // end try

		catch (Exception e) {
			e.printStackTrace();
		} // end catch
	} // end updateConfig()

}
// end class Config