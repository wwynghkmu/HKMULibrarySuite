package hk.edu.hkmu.lib.acq;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.*;

import hk.edu.hkmu.lib.StringHandling;
import okhttp3.internal.platform.Platform;

import java.sql.*;

/**
 * Reading the configuration files in the package "hk.edu.ouhk.lib.acq".
 * 
 * @author Wai-yan NG
 * @version 1.0
 * @since 6 Feb, 2021
 */
public final class Config extends hk.edu.hkmu.lib.Config {

	public static Map<String, String> VALUES = new HashMap<String, String>();
	public static Map<String, String> VALUESDES = new HashMap<String, String>();
	public static Map<String, Object[]> BUDGET_CODES = new HashMap<String, Object[]>();
	public static Map<String, String[]> PLATFORMS;
	public static Map<String, ArrayList<String>> SEARCHITEMS;

	public static void init() {
		BufferedReader br = null;
		Connection conn = null;
		Statement stmt = null;
		SEARCHITEMS = new HashMap<String, ArrayList<String>>();
		PLATFORMS = new HashMap<String, String[]>();

		try {
			hk.edu.hkmu.lib.Config.init();
			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL, USER, PASS);

			String sql;
			sql = "SELECT * FROM configuration where session = 'acq' order by name, subName Desc";
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

				if (module.toLowerCase().equals("ebkplatformchecking")) {
					if (PLATFORMS.get(name) == null) {
						String vendorArry[] = new String[5];
						for (int i = 0; i < vendorArry.length; i++)
							vendorArry[i] = "NONE";
						PLATFORMS.put(name, vendorArry);
					}

					switch (subName.toLowerCase()) {

					case "searchtype":
						PLATFORMS.get(name)[0] = value;
						break;
					case "platformname":
						PLATFORMS.get(name)[1] = value;
						break;
					case "primokeyword":
						PLATFORMS.get(name)[2] = value;
						break;
					case "primolink":
						PLATFORMS.get(name)[3] = value;
						break;
					case "primocreatorfacet":
						PLATFORMS.get(name)[4] = value;
						break;
					}
				} else if (module.toLowerCase().equals("root")) {
					VALUES.put(name, value);
					VALUESDES.put(name, des);
				}

			}

			for (Map.Entry<String, String[]> entry : Config.PLATFORMS.entrySet()) {

				String platformName = "";

				String searchType = "";
				platformName = entry.getValue()[1];
				searchType = entry.getValue()[0];
				if (!searchType.toLowerCase().equals("platform")) {
					if (SEARCHITEMS.get(platformName) == null) {
						SEARCHITEMS.put(platformName, new ArrayList<String>());
					}
					sql = "SELECT * FROM ebkplatformsearchitem where platformName = '" + platformName + "'";
					stmt = conn.createStatement();
					rs = stmt.executeQuery(sql);
					while (rs.next()) {
						String searchItem = StringHandling.trimNewLineChar(rs.getString("searchItem".trim()));
						if(searchItem!=null)
							SEARCHITEMS.get(platformName).add(searchItem);
					}
				}
			}

			rs.close();
			stmt.close();
			conn.close();

			URL url = Config.class.getResource("addListConfig.txt");
			br = new BufferedReader(new FileReader(url.getPath()));
			String line;
			while ((line = br.readLine()) != null) {
				String[] para = line.split("~");
				VALUES.put(para[0], para[1]);
			} // end while

			url = Config.class.getResource("budgetCodes.txt");
			br = new BufferedReader(new FileReader(url.getPath()));
			while ((line = br.readLine()) != null) {
				String[] para = line.split("~");
				String[] sch = para[0].split("@");
				String[] codes = para[1].split(",");
				Object[] values = { sch[1], codes, para[2] };
				BUDGET_CODES.put(sch[0], values);
			} // end while

			url = Config.class.getResource("addListConfig.txt");
			br = new BufferedReader(new FileReader(url.getPath()));
			while ((line = br.readLine()) != null) {
				String[] para = line.split("~");
				VALUES.put(para[0], para[1]);
			}

			url = Config.class.getResource("config.txt");
			br = new BufferedReader(new FileReader(url.getPath()));
			while ((line = br.readLine()) != null) {
				String[] para = line.split("~");
				VALUES.put(para[0], para[1]);
			}

			url = Config.class.getResource("platformCheckingConfig.txt");
			br = new BufferedReader(new FileReader(url.getPath()));
			while ((line = br.readLine()) != null) {
				String[] para = line.split("~");
				VALUES.put(para[0], para[1]);
			}

		} // end try

		catch (Exception e) {
			e.printStackTrace();
		} // end catch
	} // end updateConfig()

} // end class Config