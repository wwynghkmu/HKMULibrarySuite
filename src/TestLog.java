
import hk.edu.hkmu.lib.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TestLog {
	private static final Logger logger = LogManager.getLogger(TestLog.class);

	public static void main(String args[]) {
		Config.init();
		System.out.println(Config.VALUES.get("SERVERROOT"));
		LogWriter lw = new LogWriter();
		lw.setLogFile("test.log");
		lw.out("This is a test 3..");
		lw.close();
		logger.debug("Hello from Log4j 2");
		 
	}
}
