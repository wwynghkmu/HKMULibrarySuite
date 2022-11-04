package hk.edu.hkmu.lib;

import java.sql.*;

import hk.edu.hkmu.lib.acq.*;

public class AcqEmailTodayEBookPlatformCheckResultsForJSP {

	public AcqEmailTodayEBookPlatformCheckResultsForJSP() {
		try {
			String today = StringHandling.getTodayDateOnly();
			Connection conn = null;
			Statement stmt = null;
			Class.forName(Config.JDBC_DRIVER);
			conn = DriverManager.getConnection(Config.DB_URL, Config.USER, Config.PASS);
			String sql;
			sql = "SELECT MAX(batchID) FROM ebkplatformcheckingreport where timestamp LIKE '" + today
					+ "%' order by timestamp";
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			rs.next();
			int batchID = rs.getInt(1);
			rs.close();
			stmt.close();
			conn.close();
			EmailEBKPlatformReport e = new EmailEBKPlatformReport(batchID);
		} catch (Exception e) {

		}
	}

}
