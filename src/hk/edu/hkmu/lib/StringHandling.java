package hk.edu.hkmu.lib;

import java.text.*;
import java.text.Normalizer.Form;
import java.util.*;
import java.util.regex.*;

/**
 * 
 * String conversion.
 * 
 * @author Wai-yan NG
 * @author wwyng@ouhk.edu.hk
 * @version 1.0
 * @since Jan 29, 2019
 */
public class StringHandling {

	public static String extractUrl(String string) {

		String regexString = "(?:^|[\\W])((ht|f)tp(s?):\\/\\/|www\\.)" + "(([\\w\\-]+\\.){1,}?([\\w\\-.~]+\\/?)*"
				+ "[\\p{Alnum}.,%_=?&#\\-+()\\[\\]\\*$~@!:/{};']*)";
		Pattern pattern = Pattern.compile(regexString, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(string);
		while (matcher.find()) {
			return string.substring(matcher.start(0) + 1, matcher.end(0) - 1);
		}

		return "";

	}

	public static String removeAccents(String text) {
		return text == null ? null
				: Normalizer.normalize(text, Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
	} // removeAccents()

	/**
	 * Remove the occurrence of an article of various the languages English, German,
	 * and French from a string.
	 * 
	 * @param str input string
	 * @return converted string
	 */
	public static String removeFirstArticles(String str) {
		str = str.replaceAll("^a ", "");
		str = str.replaceAll("^an ", "");
		str = str.replaceAll("^the ", "");
		str = str.replaceAll("^la ", "");
		str = str.replaceAll("^les ", "");
		str = str.replaceAll("^die ", "");
		str = str.replaceAll("^der ", "");
		str = str.replaceAll("^das ", "");
		str = str.replaceAll("^eine ", "");
		return str;
	} // end removeFirstArticles();

	/**
	 * Remove all articles of various the languages English, German, and French from
	 * a string.
	 * 
	 * @param str input string
	 * @return converted string
	 */
	public static String removeArticles(String str) {
		str = str.replaceAll("^a ", "");
		str = str.replaceAll("^an ", "");
		str = str.replaceAll("^the ", "");
		str = str.replaceAll("^la ", "");
		str = str.replaceAll("^les ", "");
		str = str.replaceAll("^die ", "");
		str = str.replaceAll("^der ", "");
		str = str.replaceAll("^das ", "");
		str = str.replaceAll("^eine ", "");
		str = str.replaceAll(" a$", "");
		str = str.replaceAll(" an$", "");
		str = str.replaceAll(" the$", "");
		str = str.replaceAll(" la$", "");
		str = str.replaceAll(" les$", "");
		str = str.replaceAll(" die$", "");
		str = str.replaceAll(" die$", "");
		str = str.replaceAll(" der$", "");
		str = str.replaceAll(" das$", "");
		str = str.replaceAll(" a ", " ");
		str = str.replaceAll(" an ", " ");
		str = str.replaceAll(" the ", " ");
		str = str.replaceAll(" la ", " ");
		str = str.replaceAll(" les ", " ");
		str = str.replaceAll(" die ", " ");
		str = str.replaceAll(" der ", " ");
		str = str.replaceAll(" das ", " ");
		str = str.replaceAll(" eine ", " ");
		return str;
	} // end removeArticles

	/**
	 * Remove all special characters (characters other than alphabets)
	 * 
	 * @param str input string
	 * @return converted string
	 */
	public static String trimSpecialChars(String str) {
		if (str == null) {
			return "";
		} // end if
		str = str.replace('–', '?');
		String[] specChars = { "\\=", "\\-", "\\^", "#", "/", "@", "%", "&", "~", "[\\s|\t]or[\\s|\t]" };

		String[] specChars2 = { "\\-", "《", "》", "〈", "〉", ",", "「", "」", "\"", "？", "‘", "’", "‚", "“", "”", "†", "‡",
				"‰", "‹", "›", "♠", "♣", "♥", "♥", "♦", "‾", "←", "↑", "→", "↓", "™", "\\+", "\\*", "'", "\\.", "\\\\",
				"\\+", "\\?", "\\[", "\\]", "\\$", "\\(", "\\)", "\\{", "\\}", "\\!", "\\<", "\\>", "\\|", "。", "、",
				":", "・" };

		for (int i = 0; i < specChars.length; i++) {
			str = str.replaceAll(specChars[i], " ");
		} // end for

		for (int i = 0; i < specChars2.length; i++) {
			str = str.replaceAll(specChars2[i], "");
		} // end for

		str = str.replaceAll("^\\s{1,}|\\s{1,}$|^\t{1,}|\t${1,}", "");
		str = str.replaceAll("\\s{2,}|\t{2,}", " ");
		str = str.trim();
		return str;
	} // end trimSpecialChars()

	public static String trimNewLineChar(String str) {
		str = str.replaceAll("\\r|\\n|\\t", "");
		return str;
	} // end NewLineChar()

	public static String trimSpace(String str) {
		str = str.replaceAll(" |\\s", "");
		return str;
	} // end trimSpace()

	/**
	 * Extract the last 5 words in English or Chinese from a string.
	 * 
	 * @param str input string
	 * @return extracted string
	 */
	public static String extractLast5Words(String str) {

		if (!hasSomething(str))
			return "";

		if (CJKStringHandling.isCJKString(str)) {
			str = CJKStringHandling.removeNonCJKChars(str);
			if (str.length() < 5)
				return str;
			String s = "";
			for (int i = str.length() - 5; i < str.length(); i++) {
				s += str.charAt(i);
			} // end for
			return s;
		} else {
			String[] arry = str.split("\\s|\\t");
			if (arry.length > 5) {
				str = "";
				for (int i = arry.length - 6; i < arry.length; i++) {
					str += arry[i] + " ";
				} // end for
				str = str.trim();
				return str;

			} // end if
		} // end if

		return str;
	} // end extractLast5Chars()

	/**
	 * Standardize a string by removing articles, special characters; and making the
	 * string in lower cases.
	 * 
	 * @param str input string
	 * @return output string
	 */
	public static String tidyString(String str) {
		if (str == null) {
			return "";
		} // end if
		str = str.toLowerCase();
		str = trimSpecialChars(str);
		str = removeAccents(str);
		// remove articles
		str = str.replaceAll("^a |^the |^an |^le |^la |^ |^die |^der |^das " + "|^el |\"|&quot;|quot|&apos|apos|;", "");
		str = str.replaceAll("&| & | and | but |  |\\/|\\-", " ");
		return str;
	}

	public static String normalizeString(String str) {
		if (str == null) {
			return "";
		} // end if
		str = str.toUpperCase();
		str = str.replaceAll(" |\\s|\\t", "");
		str = str.replace("\"", "");
		str = str.replace("/", "");
		str = str.replace("-", "");
		return str;
	} // end normalizeString()

	public static String trimAll(String str) {
		str = normalizeString(str);
		str = tidyString(str);
		str = trimSpecialChars(str);
		str = trimSpace(str);
		return str;
	}

	/**
	 * Extract 3 longest words in a sentence.
	 * 
	 * @param str input string
	 * @return output string
	 */
	public static String extract3LongestAdjententKeywords(String str) {

		String result = "";
		str = str.trim();
		String[] strs = str.split(" |\\s|\\t");

		if (strs.length < 4 || strs.length == 4)
			return str;

		int longestIndex = -1;
		for (int i = 0; i < strs.length; i++) {
			if (longestIndex < strs[i].length())
				longestIndex = i;
		} // end for

		if (longestIndex == strs.length - 1) {
			result = strs[longestIndex - 2] + " " + strs[longestIndex - 1] + " " + strs[longestIndex];
		} else {
			result = strs[longestIndex] + " " + strs[longestIndex + 1] + " " + strs[longestIndex + 2];
		} // end if

		return result;
	} // end extract3LongestAdjententKeywords

	/**
	 * Check if the 1st input sentence contains all the words of the 2nd input
	 * sentence.
	 * 
	 * @param str1 1st input sentence
	 * @param str2 2nd input sentence
	 * @return the checking result
	 */
	public static boolean containWords(String str1, String str2) {
		str1 = str1.trim();
		str2 = str2.trim();

		String[] str2s = str2.split(" |\\t|\\s");
		int matchCount = 0;
		for (int i = 0; i < str2s.length; i++) {
			if (str1.contains(str2s[i]))
				matchCount++;
		} // end for

		if (str2s.length == matchCount)
			return true;

		return false;
	} // end containWords()

	/**
	 * Extract numeral from a string.
	 * 
	 * @param str input string
	 * @return output string with numeral only.
	 */
	public static String extractNumeric(String str) {
		if (str.contains("-")) {
			str = str.replaceAll("[^0-9]", "");
			str = "-" + str;
		} else {
			str = str.replaceAll("[^0-9]", "");
		} // end if
		return str;
	} // extractNumeric

	/**
	 * Remove numeral.
	 * 
	 * @param str input string
	 * @return converted string
	 */
	public static String trimNumeric(String str) {
		if (str.contains("-")) {
			str = str.replaceAll("[0-9]", "");
			str = "-" + str;
		} else {
			str = str.replaceAll("[0-9]", "");
		} // end if
		return str;
	} // trimNumeric

	public static String trimTrailingPeriod(String str) {
		return str.replaceAll("$\\.", "");
	} // trimTrailingPeriod

	/**
	 * Check if an object is null or an empty string.
	 * 
	 * @param o any object
	 * @return the checking result
	 */
	public static boolean hasSomething(Object o) {
		if (o == null || o.toString().trim().equals("")) {
			return false;
		} // end if
		return true;
	} // end hasSomething();

	/**
	 * Escape reserved characters of regular expression from a string.
	 * 
	 * @param str input string
	 * @return converted string
	 */
	public static String escapeRegExpReservedChars(String str) {
		char[] espChars = { '.', '\'', '^', '$', '*', '+', '?', '(', ')', '[', '{', '}', '|' };
		for (int j = 0; j < espChars.length; j++) {
			if (espChars[j] != '\\') {
				str = str.replaceAll("\\" + espChars[j], "" + '\\' + '\\' + espChars[j]);
			} // end if
		} // end for
		return str;
	} // end escapeRegExpReservedChars()

	/**
	 * Get today in the format yyyy-MM-dd-HH-mm-ss.
	 * 
	 * @return the date in form yyyy-MM-dd-HH-mm-ss
	 */
	public static String getToday() {
		Date today = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
		return formatter.format(today);
	} // end getToday()

	public static int getTodayDateOfWeek() {
		Date today = new Date();
		Calendar c = Calendar.getInstance();
		c.setTime(today);
		return c.get(Calendar.DAY_OF_WEEK);
	}

	public static String getTodayDateOnly() {
		Date today = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		return formatter.format(today);
	} // end getToday()

	public static String getTodayDBFormat() {
		Date today = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return formatter.format(today);
	} // end getToday()

	/**
	 * Convert an input number to the relevent month in English.
	 * 
	 * @param num input string
	 * @return converted string
	 */
	public static String getMonthByNum(String num) {
		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put("01", "Jan");
		hm.put("1", "Jan");
		hm.put("02", "Feb");
		hm.put("2", "Feb");
		hm.put("03", "Mar");
		hm.put("3", "Mar");
		hm.put("04", "Apr");
		hm.put("4", "Apr");
		hm.put("05", "May");
		hm.put("5", "May");
		hm.put("06", "Jun");
		hm.put("6", "Jun");
		hm.put("07", "Jul");
		hm.put("7", "Jul");
		hm.put("08", "Aug");
		hm.put("8", "Aug");
		hm.put("09", "Sep");
		hm.put("9", "Sep");
		hm.put("10", "Oct");
		hm.put("11", "Nov");
		hm.put("12", "Dec");
		String out = hm.get(num);
		if (out == null)
			out = "";
		return out;
	}

	public static String getMonthByNum(int num) {
		HashMap<Integer, String> hm = new HashMap<Integer, String>();

		hm.put(1, "Jan");
		hm.put(2, "Feb");
		hm.put(3, "Mar");
		hm.put(4, "Apr");
		hm.put(5, "May");
		hm.put(6, "Jun");
		hm.put(7, "Jul");
		hm.put(8, "Aug");
		hm.put(9, "Sep");
		hm.put(10, "Oct");
		hm.put(11, "Nov");
		hm.put(12, "Dec");
		String out = hm.get(num);
		if (out == null)
			out = "";
		return out;
	}

	public static int getMonthNumByName(String mon) {
		HashMap<String, Integer> hm = new HashMap<String, Integer>();

		hm.put("Jan", 1);
		hm.put("Feb", 2);
		hm.put("Mar", 3);
		hm.put("Apr", 4);
		hm.put("May", 5);
		hm.put("Jun", 6);
		hm.put("Jul", 7);
		hm.put("Aug", 8);
		hm.put("Sep", 9);
		hm.put("Oct", 10);
		hm.put("Nov", 11);
		hm.put("Dec", 12);
		int out = hm.get(mon);
		return out;
	}

	public static int parseInteger(String i) {
		int out = 0;
		try {
			out = Integer.parseInt(i);
		} catch (Exception e) {
			return 0;
		}

		return out;
	}

	public static String getCurrentMonth() {
		String today = getToday();
		String month = "";
		Pattern pattern = Pattern.compile("(....)-(..)-(.*)");
		Matcher matcher = pattern.matcher(today);
		if (matcher.matches()) {
			month = matcher.group(2);
		}
		return month;
	}

	public static String getCurrentYear() {
		String today = getToday();
		String year = "";
		Pattern pattern = Pattern.compile("(....)-(..)-(.*)");
		Matcher matcher = pattern.matcher(today);
		if (matcher.matches()) {
			year = matcher.group(1);
		}
		return year;
	}

	public static int getCurrentYearNum() {
		String today = getToday();
		String year = "";
		Pattern pattern = Pattern.compile("(....)-(..)-(.*)");
		Matcher matcher = pattern.matcher(today);
		if (matcher.matches()) {
			year = matcher.group(1);
		}
		return Integer.parseInt(year);
	}

} // end class