
import java.io.File;

import hk.edu.hkmu.lib.cat.*;

public class TestCopyCat {

	static public void main(String args[]){
		File file = new File(
				"d:/list.xlsx");
		
		String writePath = "D:/";
		CopyCatExcel cc;
		cc = new CopyCatExcel(file, writePath);
	} //end main()
}