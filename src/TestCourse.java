
import hk.edu.hkmu.lib.cir.*;

public class TestCourse {

	public static void main(String[] argvs) {
		CourseTest c = new CourseTest(
				"https://api-ap.hosted.exlibrisgroup.com/almaws/v1/courses?apikey=l8xxeb2a38bf0cb840e59f4ecf7a8f1c01f0&status=ACTIVE&order_by=code&limit=100&offset=0",
				"d:\\");
		for (int i = 0; i < c.courseCodeArry.size(); i++) {
			System.out.println(c.courseCodeArry.get(i).toString());
			System.out.println(c.courseNameArry.get(i).toString());
		}
	}

}
