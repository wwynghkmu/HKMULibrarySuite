
import hk.edu.hkmu.lib.*;

public class TestAuthenticateLibLDAP {

	public static void main(String args[]) {
		hk.edu.hkmu.lib.Config.init();

		if (args.length == 0) {
			System.out.println("useage: TestAuthenticateLibLDAP [login] [password]");
		} else if (args.length == 2) {
			AuthenticateLibLDAP authen = new AuthenticateLibLDAP(args[0], args[1]);
			System.out.println("Authenticated success: " + authen.isAuthenticated());
			System.out.println("Surname: " + authen.getSurname());
			System.out.println("Given Name: " + authen.getGivenName());
			System.out.println("Login TIme: " + authen.getLogintime());

		}
		/*
		 * AuthenticateLibLDAP authen = new AuthenticateLibLDAP("testw", "123456aB",
		 * "192.168.0.0", "testw", "TEST SYSTEM");
		 * System.out.println(authen.isAuthenticated());
		 * System.out.println(authen.getSurname());
		 * System.out.println(authen.getGivenName());
		 * System.out.println(authen.getLogintime());
		 */
	}
}
