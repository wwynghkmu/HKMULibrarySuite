import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.apache.commons.validator.routines.UrlValidator;

import hk.edu.hkmu.lib.StringHandling;

public class TestString {
	public static void main(String[] args) {

		String str = "TOLOGY–CRYPTO2019SPRINGER";
		str = StringHandling.trimSpecialChars(str);
		System.out.println(str);

	}

}
