import hk.edu.hkmu.lib.sys.*;
import hk.edu.hkmu.lib.sys.misctools.AlmaCreateCitation;
import hk.edu.hkmu.lib.sys.misctools.AlmaCreateCourseViaAPI;
import hk.edu.hkmu.lib.sys.misctools.AlmaCreateReadingList;

public class TestCallAlmaAPI {
	public static void main(String args[]) {
		try {
			AlmaCreateCourseViaAPI acourse = new AlmaCreateCourseViaAPI(
					"{\"link\":\"\",\"code\":\"PHI4360\",\"name\":\"Wittgenstein'sPhilosophy\",\"section\":\"01\",\"academic_department\":{\"value\":\"A&SS\"},\"processing_department\":{\"value\":\"C_DEP\"},\"term\":[{\"value\":\"AUTUMN\"}],\"status\":\"INACTIVE\",\"start_date\":\"2011-09-10Z\",\"end_date\":\"2013-12-31Z\",\"weekly_hours\":\"0\",\"participants\":\"35\",\"year\":\"2007\",\"instructor\":[{\"primary_id\":\"1234\"}],\"campus\":[{\"campus_code\":{\"value\":\"code\"},\"campus_participants\":\"30\"}],\"submit_by_date\":\"2013-12-01Z\"}");

			System.out.println(acourse.CreateCourse());

			AlmaCreateReadingList rl = new AlmaCreateReadingList("");
			System.out.println(rl.CreateReadingList());
			
			AlmaCreateCitation c = new AlmaCreateCitation("");
			System.out.println(c.CreateCitation());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
