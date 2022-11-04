package hk.edu.hkmu.lib;
import hk.edu.hkmu.lib.adm.*;

public class AdmStaffLeaveConsolidation {

	/**
	 * @param args Arguments from the console. Not used at all
	 * @throws Exception if there is anything wrong
	 * Download and consoldiate the Staff Leave Excel file from OUHK SharePoint.
	 */
	public static void main(String[] args) throws Exception {
		FetchAndConsolidateStaffLeave fl = new FetchAndConsolidateStaffLeave("wwyng", "98731486Abcd");
	}
}