
import hk.edu.hkmu.lib.bookquery.*;

public class TestZ3950earchBySubject {
	public static void main(String[] args) {
		try {
			String inst = "MIT";
			String subject = "abc";
			String writePath = "D:/";

			Z3950QueryBySubject q = new Z3950QueryBySubject(subject, inst, writePath);
			if(q.querySuccess()) {
				System.out.println("SUCCESS");
				System.out.println(q.getResult());
			} else {
				System.out.println(q.getResult());
				System.out.println("NOT SUCCESS");
			}

		} // end try
		catch (Exception e) {
			e.printStackTrace();
		}
	} // end main()
} // end class Test