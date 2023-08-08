
import java.io.File;

import hk.edu.hkmu.lib.cat.*;

public class TestDIStudentDeclareForm {

	static public void main(String args[]){
		File file = new File(
				"d:/2023BAKevin-22t108s.xlsx");
		
		String writePath = "D:/";
		StudentSelfDeclarationImport cc;
		cc = new StudentSelfDeclarationImport(file, writePath);
	} //end main()
}