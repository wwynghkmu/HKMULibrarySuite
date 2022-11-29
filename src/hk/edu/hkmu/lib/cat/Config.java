package hk.edu.hkmu.lib.cat;

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
 * 
 * This class serves as a collection of constants containing system specific
 * settings. This class reads configuration files in the package
 * "hk.edu.ouhk.lib.cat".
 * 
 * @author Wai-yan NG
 * @author wwyng@ouhk.edu.hk
 * @version 1.0
 * @since Jan 29, 2019
 */
public final class Config extends hk.edu.hkmu.lib.Config {

	public static void init() {
		BufferedReader br = null;
		Connection conn = null;
		Statement stmt = null;

		try {
			URL url = Config.class.getResource("ccconfig.txt");
			br = new BufferedReader(new FileReader(url.getPath()));
			String line;
			while ((line = br.readLine()) != null) {
				String[] para = line.split("~");
				VALUES.put(para[0], para[1]);
			} // end while
			
			hk.edu.hkmu.lib.Config.init();
			
			url = Config.class.getResource("config.txt");
			br = new BufferedReader(new FileReader(url.getPath()));
			while ((line = br.readLine()) != null) {
				String[] para = line.split("~");
				VALUES.put(para[0], para[1]);
			} // end while


			try {
				Class.forName(JDBC_DRIVER);
				conn = DriverManager.getConnection(DB_URL, USER, PASS);

				String sql;
				sql = "SELECT * FROM configuration where session = 'cat' order by name, subName Desc";
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

					VALUES.put(name, value);
					VALUESDES.put(name, name + ": " + des);
					VALUESMOD.put(name, module);

				}
				stmt.close();
				conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}

		} // end try

		catch (

		IOException e) {
			e.printStackTrace();
		} // end catch
	} // end updateConfig()

} // end class Config