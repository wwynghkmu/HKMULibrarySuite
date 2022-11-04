
import java.io.PrintWriter;

import hk.edu.hkmu.lib.ser.*;

public class TestSerURLReport {

	public static void main(String argvs[]) {
		PrintWriter out = new PrintWriter(System.out, true);
		FetchURLReport rpt = new FetchURLReport("d:\\", null);
		rpt.fetchReport();

	}

}
