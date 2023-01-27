
import java.io.File;

import hk.edu.hkmu.lib.cat.*;

public class TestStudentDeclareForm {

	static public void main(String args[]){
		File file = new File(
				"d:/Online form (name list) TEST.xlsx");
		
		String writePath = "D:/";
		StudentSelfDeclarationImport cc;
		cc = new StudentSelfDeclarationImport(file, writePath);
	} //end main()
}