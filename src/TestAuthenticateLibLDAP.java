
import hk.edu.hkmu.lib.*;

public class TestAuthenticateLibLDAP {

	public static void main(String args[]) {
		hk.edu.hkmu.lib.Config.init();
		AuthenticateLibLDAP authen = new AuthenticateLibLDAP("testw", "9123456aB", "192.168.0.0", "testw", "TEST SYSTEM");
		System.out.println(authen.isAuthenticated());
		System.out.println(authen.getSurname());
		System.out.println(authen.getGivenName());
		System.out.println(authen.getLogintime());

		authen = new AuthenticateLibLDAP("wwyng", "98731486aB");
		System.out.println(authen.isAuthenticated());
		System.out.println(authen.getSurname());
		System.out.println(authen.getGivenName());
		System.out.println(authen.getLogintime());
		
		
		authen = new AuthenticateLibLDAP("wwyng", "98731486aB", "192.168.0.0", "wwyng", "TEST SYSTEM");
		System.out.println(authen.isAuthenticated());
		System.out.println(authen.getSurname());
		System.out.println(authen.getGivenName());
		System.out.println(authen.getLogintime());
		
	}
}
