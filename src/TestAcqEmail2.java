

import hk.edu.hkmu.lib.acq.*;

public class TestAcqEmail2 {

	public static void main(String args[]) {
		String reportFile = "d:\\A&SS-Monthly Report-Jul 2019";
		EmailEBKPlatformReport em = new EmailEBKPlatformReport(reportFile);
		System.out.println(em.getReportFileStr());

	}
}
