

import hk.edu.hkmu.lib.acq.*;

public class TestAcqEmail {

	public static void main(String args[]) {
		String reportFile = "d:\\EL-Monthly Report-Jun 2023.xlsx";
		EmailReportBySubjectAlma em = new EmailReportBySubjectAlma(reportFile);
		System.out.println(em.getReportFileStr());


	}
}
