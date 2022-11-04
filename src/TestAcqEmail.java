

import hk.edu.hkmu.lib.acq.*;

public class TestAcqEmail {

	public static void main(String args[]) {
		String reportFile = "d:\\A&SS-Monthly Report-Jul 2019";
		EmailReportBySubject em = new EmailReportBySubject(reportFile);
		System.out.println(em.getReportFileStr());


	}
}
