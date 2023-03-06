import java.util.*;
import java.util.regex.*;
import hk.edu.hkmu.lib.*;

public class TestExtractURL {

	public static void main(String args[]) {

		String s = "小說史 理論與實踐http://www.apabi.com.ezproxy.lib.hkmu.edu.hk/hkmu/?pid=book.detail&metaid=isbn7-301-01985-8&cult=TW&username=%E4%BE%86%E8%87%AA%20%E9%A6%99%E6%B8%AF%E9%83%BD%E6%9C%83%E5%A4%A7%E5%AD%B8%20%E7%9A%84%E7%94%A8%E6%88%B6&ug=%E9%A6%99%E6%B8%AF%E9%83%BD%E6%9C%83%E5%A4%A7%E5%AD%B8%E7%84%A1%E5%AF%86%E7%A2%BC%E7%94%A8%E6%88%B6%E7%B5%84)";

		System.out.println(StringHandling.extractUrl(s));

	}

}
