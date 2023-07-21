import hk.edu.hkmu.lib.sys.*;
import hk.edu.hkmu.lib.sys.misctools.CheckSSLCertExpiry;

public class TestCheckCert {

	public static void main(String[] argvs) {
		System.out.println("Java runtime: "  + System.getProperty("java.runtime.version"));
		CheckSSLCertExpiry chkssl = new CheckSSLCertExpiry("www.lib.hkmu.edu.hk");
		System.out.println("Date Left: " + chkssl.daysLeft);
		System.out.println("Report: " + chkssl.report);
		System.out.println(chkssl.daysLeft);
	}
}
