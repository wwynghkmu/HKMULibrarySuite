
import java.io.File;

import hk.edu.hkmu.lib.cat.*;

public class TestCopyCatSFX {

	static public void main(String args[]){
		File file = new File(
				"d:/list.xml");
		
		String writePath = "D:\\";
		try {
		CopyCatSFX cc;
		cc = new CopyCatSFX(file, writePath);
		} catch (Exception e) {
			e.printStackTrace();
		}
	} //end main()
}