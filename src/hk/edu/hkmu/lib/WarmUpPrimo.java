package hk.edu.hkmu.lib;

import hk.edu.hkmu.lib.acq.CheckeBookPlatform;

/*
 * This is the class to warm up Primo by making a search. It is used during Primo reboot which always fails 1st time search
 */
public class WarmUpPrimo {
	public static void main(String[] argvs) {
		try {
			String[] strArry = { "ABC", "DEF", "HIJ", "KLM", "MNP", "RST", "TUV", "XYZ", "TEST SEARCH", "TEST SEARCH 2",
					"TEST SEARCH 3", "TEST SEARCH 4", "TEST SEARCH 5" };
			PrimoDriver pdriver = new PrimoDriver();
			pdriver.driver.get(hk.edu.hkmu.lib.Config.VALUES.get("PRIMOURL"));
			for (int i = 0; i < 25; i++) {
				Thread.sleep(5000);
				pdriver.initSearchKeywordComponent(strArry[i % 8]);
				Thread.sleep(15000);
				pdriver.driver.navigate().refresh();
			}
			Runtime.getRuntime().exec("taskkill /im chromepdriver.driver.exe /f");
			Runtime.getRuntime().exec("taskkill /im chrome.exe /f");
			Runtime.getRuntime().exec("taskkill /im javaw.exe /f");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
