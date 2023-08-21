package hk.edu.hkmu.lib;
import hk.edu.hkmu.lib.adm.*;

public class AdmStaffLeaveConsolidation {

	/**
	 * @param args Arguments from the console. Not used at all
	 * @throws Exception if there is anything wrong
	 * Download and consolidate the Staff Leave Excel file from HKMU SharePoint.
	 */
	public static void main(String[] args) throws Exception {
		//Replace "login" and "password" in FetchAndConsolidateStaffLeave("login", "password") with a valid SharePoint Account while compiling this Java Class. 
		FetchAndConsolidateStaffLeave fl = new FetchAndConsolidateStaffLeave("login", "password");
	}
}