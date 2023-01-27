import hk.edu.hkmu.lib.StringHandling;
import hk.edu.hkmu.lib.*;
import hk.edu.hkmu.lib.cat.*;

public class TestConfig {
	public static void main(String[] args) {
		hk.edu.hkmu.lib.cat.Config.init();
		System.out.println(hk.edu.hkmu.lib.cat.Config.VALUESDES.get("invitationEmailCC"));
		System.out.println(hk.edu.hkmu.lib.cat.Config.VALUES.get("invitationEmailCC"));

	}

}
