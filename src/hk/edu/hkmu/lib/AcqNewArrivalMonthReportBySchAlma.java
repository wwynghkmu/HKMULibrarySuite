package hk.edu.hkmu.lib;

import java.io.*;

import hk.edu.hkmu.lib.acq.*;

/**
 * OUHK In-house Library Tool - run in OS level, for OUHK LIB ACQ auto-report
 * generation using hk.edu.ouhk.lib.acq.FetchReportBySchoolAndDates and
 * e-mailing the report using hk.edu.ouhk.lib.acq.EmailReportBySubject for Alma.
 * 
 * @author Wai-yan NG
 * @author wwyng@hkmu.edu.hk
 * @version 1.0
 * @since July 24 2023
 */

public class AcqNewArrivalMonthReportBySchAlma {
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
		for (int i = 0; i < hk.edu.hkmu.lib.acq.Config.UNITS.size(); i++) {
			
			FetchLastMonthArrivalListAlma acqRpt = new FetchLastMonthArrivalListAlma(
					hk.edu.hkmu.lib.acq.Config.UNITS.get(i).toString(), reportPath, null);
			EmailReportBySubjectAlma em = new EmailReportBySubjectAlma(reportPath + acqRpt.getFileName());

		}
	}

}
