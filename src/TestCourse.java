
import hk.edu.hkmu.lib.cir.*;

public class TestCourse {

	public static void main(String[] argvs) {

		Course c = new Course(
				"https://api-ap.hosted.exlibrisgroup.com/almaws/v1/courses?apikey=l8xxeb2a38bf0cb840e59f4ecf7a8f1c01f0&status=ACTIVE&order_by=code&limit=100&offset=0",
				"d:\\");
		// Course c = new
		// Course("https://api-ap.hosted.exlibrisgroup.com/almaws/v1/courses?apikey=l8xx41bf077192274811b545c60ba48df465&status=ACTIVE&limit=100&offset=0","d:\\");
		// Course c = new
		// Course("https://api-ap.hosted.exlibrisgroup.com/almaws/v1/courses?apikey=l8xx41bf077192274811b545c60ba48df465&status=ACTIVE&order_by=code&limit=100&offset=0","d:\\");

		/*
		 * for (int i = 0; i < c.courseCodeArry.size(); i++) { System.out.print(i + 1 +
		 * ". " + c.courseCodeArry.get(i).toString() + ":");
		 * System.out.println(c.courseNameArry.get(i).toString()); }
		 */

		for (int i = 0; i < c.courseArray.size(); i++) {
			System.out.println(i + ". " + c.courseArray.get(i).get(0) + " " + c.courseArray.get(i).get(1)  + " " + c.courseArray.get(i).get(3));
		}

	}

}
