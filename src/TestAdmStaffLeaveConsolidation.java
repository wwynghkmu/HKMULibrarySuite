import hk.edu.hkmu.lib.adm.*;

public class TestAdmStaffLeaveConsolidation {

	/**
	 * @param args arguments on the console.
	 * @throws Exception if there is any problem
	 * Download and consolidate the staff leave Excel file from OUHK SharePoint
	 */
	public static void main(String[] args) throws Exception {
		FetchAndConsolidateStaffLeave fl = new FetchAndConsolidateStaffLeave("wwyng", "98731486aBc");
	}
}