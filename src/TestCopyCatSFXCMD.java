
import java.io.File;

import hk.edu.hkmu.lib.cat.*;

public class TestCopyCatSFXCMD {

	static public void main(String args[]) {
		String fileStr = "";
		String writePath = "";
		System.out.println("args length:" + args.length);
		if (args.length <= 0) {
			fileStr = "d:\\list.xml";
			writePath = "D:\\";
		} else {
			fileStr = args[0];
			writePath = args[1];
		}

		System.out.println("xml file: " + fileStr);
		System.out.println("write path: " + writePath);

		File file = new File(fileStr);

		try {
			CopyCatSFX cc;
			cc = new CopyCatSFX(file, writePath);
		} catch (Exception e) {
			e.printStackTrace();
		}
	} // end main()
}