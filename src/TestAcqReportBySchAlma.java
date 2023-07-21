
import java.io.PrintWriter;

import hk.edu.hkmu.lib.acq.*;

public class TestAcqReportBySchAlma {

	public static void main(String argvs[]) {
		Config.init();
		/*
		 * for(String schCode : Config.BUDGET_CODES.keySet()) {
		 * FetchReportBySchoolAndDates acqRpt = new FetchReportBySchoolAndDates(schCode,
		 * "20180821", "20180822", "D:\\"); acqRpt.fetchReport(); }
		 */
		PrintWriter out = new PrintWriter(System.out, true);
		FetchLastMonthArrivalListAlma acqRpt = new FetchLastMonthArrivalListAlma("EL", "D:\\", null);

	}
}
