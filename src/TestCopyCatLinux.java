
import java.io.File;
import hk.edu.hkmu.lib.cat.*;

public class TestCopyCatLinux {

	static public void main(String args[]){
		File file = new File(
				"/data/tomcat/webapps/ROOT/cat/docs/copycat-request-sample.xlsx");
		
		String writePath = "/tmp";
		CopyCatExcel cc;
		cc = new CopyCatExcel(file, writePath);
	} //end main()
}