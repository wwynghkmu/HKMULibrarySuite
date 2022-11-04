package hk.edu.hkmu.lib.ser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import hk.edu.hkmu.lib.StringHandling;

/**
 * This class serves as a collection of constants containing system specific
 * settings. This class reads configuration files in the package
 * "hk.edu.ouhk.lib.ser".
 * 
 * @author Wai-yan NG
 * @version 1.0
 * @since Jan 29, 2019
 */
public final class Config extends hk.edu.hkmu.lib.Config {

	public static Map<String, String> VALUES = new HashMap<String, String>();
	public static String SCHOOLS[];
	public static Map<String, String[]> COUNTER5PLATFORMS;

	public static void init() {
		BufferedReader br = null;
		Connection conn = null;
		Statement stmt = null;
		COUNTER5PLATFORMS = new HashMap<String, String[]>();

		try {

			hk.edu.hkmu.lib.Config.init();
			try {
				Class.forName(JDBC_DRIVER);
				conn = DriverManager.getConnection(DB_URL, USER, PASS);

				String sql;
				sql = "SELECT * FROM configuration where session = 'ser' order by name, subName Desc";
				stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql);

				while (rs.next()) {
					String module = StringHandling.trimNewLineChar((rs.getString("module").trim()));
					String name = StringHandling.trimNewLineChar(rs.getString("name".trim()));
					String subName = StringHandling.trimNewLineChar(rs.getString("subName").trim());
					String value = StringHandling.trimNewLineChar(rs.getString("value").trim());
					String des = "NONE";
					if (rs.getString("description") != null)
						des = StringHandling.trimNewLineChar(rs.getString("description").trim());

					if (module.toLowerCase().equals("counter5")) {
						if (COUNTER5PLATFORMS.get(name) == null) {
							COUNTER5PLATFORMS.put(name, new String[2]);
						}
						COUNTER5PLATFORMS.get(name)[0] = value;
						COUNTER5PLATFORMS.get(name)[1] = des;
					}

				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			URL url = Config.class.getResource("config.txt");
			br = new BufferedReader(new FileReader(url.getPath()));
			String line;
			while ((line = br.readLine()) != null) {
				String[] para = line.split("~");
				VALUES.put(para[0], para[1]);
			} // end while

			String schStr = VALUES.get("SCHOOL");
			if (schStr != null)
				SCHOOLS = schStr.split(",");

		} // end try

		catch (IOException e) {
			e.printStackTrace();
		} // end catch
	} // end updateConfig()

} // end class Config