
import hk.edu.hkmu.lib.acq.*;

public class TestACQConfig {

	public static void main(String[] argvs) {
		hk.edu.hkmu.lib.acq.Config.init();	
		System.out.println(hk.edu.hkmu.lib.Config.DB_URL);
		System.out.println(hk.edu.hkmu.lib.Config.USER);
		System.out.println(hk.edu.hkmu.lib.Config.PASS);
	}

}
