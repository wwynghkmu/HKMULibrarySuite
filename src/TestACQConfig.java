
import hk.edu.hkmu.lib.acq.*;

public class TestACQConfig {

	public static void main(String[] argvs) {
		hk.edu.hkmu.lib.acq.Config.init();
		for (int i = 0; i < hk.edu.hkmu.lib.acq.Config.UNITS.size(); i++) {
			System.out.println(hk.edu.hkmu.lib.acq.Config.UNITS.get(i));
			System.out
					.println(hk.edu.hkmu.lib.acq.Config.VALUES.get(hk.edu.hkmu.lib.acq.Config.UNITS.get(i) + "Email"));
		}

	}

}
