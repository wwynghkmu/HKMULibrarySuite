package hk.edu.hkmu.lib;

import java.io.*;

import hk.edu.hkmu.lib.acq.*;

/**
 * OUHK In-house Library Tool - run in OS level, for OUHK LIB ACQ auto-report
 * generation using hk.edu.ouhk.lib.acq.FetchReportBySchoolAndDates and
 * e-mailing the report using hk.edu.ouhk.lib.acq.EmailReportBySubject.
 * 
 * @author Wai-yan NG
 * @author wwyng@ouhk.edu.hk
 * @version 1.0
 * @since Mar 07 2019
 */

public class AcqNewArrivalMonthReportBySch {
	public static void main(String argvs[]) {
		Config.init();
		String reportPath = "";
		try {
			reportPath = new File(".").getCanonicalPath();

		} catch (Exception e) {
			;
		}
		reportPath = reportPath.replaceAll("\\\\", "/");
		reportPath = reportPath.replaceAll("\\/WEB-INF\\/classes", "");
		reportPath += "/acq/reports/newArrivalBySch/";
		hk.edu.hkmu.lib.acq.Config.init();
		for (String schCode : hk.edu.hkmu.lib.acq.Config.BUDGET_CODES.keySet()) {
			FetchReportBySchoolAndDates acqRpt = new FetchReportBySchoolAndDates(schCode, "", "", reportPath, null);
			acqRpt.reportLastMonth();
			for (String reportFile : acqRpt.getReportFiles()) {
				EmailReportBySubject em = new EmailReportBySubject(reportPath + reportFile);
			}
		}
	}

}
