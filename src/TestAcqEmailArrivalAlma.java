

import hk.edu.hkmu.lib.acq.*;

public class TestAcqEmailArrivalAlma {

	public static void main(String args[]) {
		String reportFile = "d:\\EL-Monthly Report-Jul 2023.xlsx";
		EmailReportBySubjectAlma em = new EmailReportBySubjectAlma(reportFile);
		System.out.println(em.getReportFileStr());


	}
}
