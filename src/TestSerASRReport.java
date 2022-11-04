
import java.io.PrintWriter;

import hk.edu.hkmu.lib.ser.*;

public class TestSerASRReport {

	public static void main(String args[]) {
		PrintWriter out = new PrintWriter(System.out, true);
		FetchASRReport rpt = new FetchASRReport("D:/", null);
		rpt.fetchReport();
	}
}
