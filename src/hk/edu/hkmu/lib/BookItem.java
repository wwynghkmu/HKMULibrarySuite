package hk.edu.hkmu.lib;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;
import java.util.regex.*;
import org.apache.commons.lang3.StringEscapeUtils;

/** OUHK In-house Library Tool - Represent a Book Item Information, including BIB and Item (Holding) records.
 * @author Wai-yan NG
 * @author wwyng@ouhk.edu.hk
 * @version 1.0
 * @since 1.0
*/

public class BookItem {
	public ISBN isbn;
	public String issn;
	public MARC marc;

	public ArrayList<String> fulltextUrls;
	
	/*
	 * holdingInfo is a 2 dimensional array that each row holds a holding of an item
	 * Col 1 represents "Institution" Col 2 represents "Location" Col 3 represents
	 * "Call No." Col 4 represents "Status"
	 */
	public List<List<String>> holdingInfo;

	private String title;
	private ArrayList<String> altTitles;
	private String creator;
	private ArrayList<String> creators;
	private String contributor;
	private ArrayList<String> contributors;
	private String publisher;
	private String publishYear;
	private String translator;
	private String format;
	private String bookType;
	private ArrayList<String> translators;
	private String subject;
	private ArrayList<String> subjects;
	private String edition;
	private String volume;
	private String ilsid;
	private String[] ilsids;
	private String primoLink;
	private String callNo;

	private StringHandling strHandle = new StringHandling();
	private int totalHoldings;

	public BookItem() {
		clearBookItemInfo();
		clearLibHoldInfo();
		marc = new MARC();
		isbn = new ISBN();
	} // end BookItem

	public BookItem(String isbn) {
		clearLibHoldInfo();
		clearBookItemInfo();
		this.isbn = new ISBN(isbn);
		marc = new MARC();
	} // end BookItem

	public BookItem(String titile, String creator) {
		clearLibHoldInfo();
		clearBookItemInfo();
		setTitle(title);
		setCreator(creator);
		isbn = new ISBN();
		marc = new MARC();
	} // end BookItem

	public BookItem(String title, String creator, String publisher) {
		clearLibHoldInfo();
		clearBookItemInfo();
		setTitle(title);
		setCreator(creator);
		setPublisher(publisher);
	} // end BookItem

	public void clearBookItemInfo() {
		title = null;
		altTitles = new ArrayList<String>();
		primoLink = null;
		creator = null;
		creators = new ArrayList<String>();
		contributor = null;
		contributors = new ArrayList<String>();
		translator = null;
		translators = new ArrayList<String>();
		bookType = null;
		fulltextUrls = new ArrayList<String>();
		publisher = null;
		publishYear = null;
		subject = null;
		subjects = null;
		edition = null;
		ilsid = null;
		;
		isbn = new ISBN();
		marc = null;
		clearLibHoldInfo();
	} // end clearBookItemInfo()

	public void clearLibHoldInfo() {
		totalHoldings = 0;
		primoLink = null;
		holdingInfo = new ArrayList<List<String>>();
	} // end clearLibHoldInfo()

	public void setBookType(String str) {
		bookType = str;
	} // end setBookType()

	public void setSubject(String str) {
		setSubjects(str);
		subject = str;
	} // end setSubjects()

	
	/** Formalize a call no (removing head-tail spaces and standardize a space between the 1st and the 2nd cutter); validation feature disabled.
	 * @param callno A string represent an intended Call No.
	 * @return A string of the formized call no.
	*/
	public String validateCallNo(String callno) {
		callno = callno.trim();
		String str2 = new String(callno);
		/*
		if (str2.matches("^[0-9]") || !str2.matches(".*[0-9].*") || str2.matches("^\\..*")
				|| (!str2.matches("[a-zA-Z].*[\\s|\\t].*") && !str2.matches("[a-zA-Z].*\\..*")) || str2.contains("microfilm"))
			str = null;
		*/

		if (callno != null && !callno.matches("^[a-zA-Z].*\\..*")) {
			callno = callno.replaceAll("^([a-zA-Z].*?)[\\s|\\t](.*)", "$1 .$2");
		} //end if
		return callno;
	}

	public void setCallNo(String str) {
		callNo = validateCallNo(str);
	} // end setCallNo();

	private void setSubjects(String subject) {
		String[] strs = subject.replaceAll("; ", ";").split(";");
		subjects = new ArrayList<String>();
		for (int i = 0; i < strs.length; i++) {
			subjects.add(strs[i]);
		} // end for
	} // end setSubjects()

	public void setTitle(String str) {
		if (!strHandle.hasSomething(str)) {
			str = "";
		} else {
			str = str.replaceAll("Vol.:.*", "");
			str = StringEscapeUtils.unescapeHtml4(str);
			str = StringEscapeUtils.unescapeXml(str);
			str = str.replaceAll("=.*", "");
			str = strHandle.trimSpecialChars(str);

			title = str;
		} // end if
	} // end setTitle()

	public void setIlsId(String str) {
		ilsid = str;
	} // end setIlsID

	public void setIlsIds(String[] str) {
		ilsids = str;
	} // end setIlsID

	public void setHoldingInfo(List<List<String>> al) {
		holdingInfo = al;
	} // end setHoldingInfo()

	public void setAltTitles(ArrayList<String> at) {
		if (strHandle.hasSomething(at)) {
			altTitles.add("");
		} // end if
		altTitles = at;
	} // end setHoldingInfo()

	public void setFulltextUrls(ArrayList<String> urls) {
		if (strHandle.hasSomething(urls)) {
			fulltextUrls = new ArrayList<String>();
			fulltextUrls.add("");
		} else {
			fulltextUrls = urls;
		} // end if
	} // end setFulltexturls()

	public void setTotalHoldings(int i) {
		totalHoldings = i;
	} // setTotalHoldings()

	public void setPrimoLink(String str) {
		primoLink = str;
	} // end setPrimoLink()

	public void setCreator(String str) {
		if (!strHandle.hasSomething(str)) {
			str = "";
		} else {
			str = StringEscapeUtils.unescapeHtml4(str);
			str = StringEscapeUtils.unescapeXml(str);
			str = strHandle.trimSpecialChars(str);
			setCreators(str);
			creator = str;
		} // end if
	} // end setCreator()

	private void setCreators(String str) {
		String[] strs = str.replaceAll("; ", ";").split(";");
		for (int i = 0; i < strs.length; i++) {
			creators.add(strs[i]);
		} // end for
	} // end setCreators()

	public void setContributor(String str) {
		if (strHandle.hasSomething(str)) {
			setContributors(str);
			contributor = str;
		} else {
			contributor = "";
		} // end if
	} // end setContributor()

	private void setContributors(String str) {
		if (strHandle.hasSomething(str)) {
			String[] strs = str.replaceAll("; ", ";").split(";");
			for (int i = 0; i < strs.length; i++) {
				contributors.add(strs[i]);
			} // end for
		} // end if
	} // end setContributors()

	public void setTranslator(String str) {
		setTranslators(str);
		translator = str;
	} // end setTranslator()

	private void setTranslators(String str) {
		String[] strs = str.replaceAll("; ", ";").split(";");
		for (int i = 0; i < strs.length; i++) {
			translators.add(strs[i]);
		} // end for
	} // end setTranslators()

	public void setIsbn(String str) {
		isbn.setIsbn(str);
	} // end setIsbn10()
	
	public void setIssn(String str) {
		issn=str;
	} // end setIsbn10()

	public void setPublisher(String str) {
		if (!strHandle.hasSomething(str)) {
			str = "";
		} else {
			str = StringEscapeUtils.unescapeHtml4(str);
			str = StringEscapeUtils.unescapeXml(str);
			str = strHandle.trimSpecialChars(str);
			publisher = str;
		} // end if
	} // end setPublisher()

	public void setVolume(String str) {
		if (!strHandle.hasSomething(str)) {
			volume = "-1";
		} else {
			volume = "v." + str;
		} // end if

	} // end setPublisher()

	public void setPublishYear(String str) {
		if (!strHandle.hasSomething(str)) {
			str = "";
		} else {
			str = StringEscapeUtils.unescapeHtml4(str);
			str = StringEscapeUtils.unescapeXml(str);
			str = strHandle.trimSpecialChars(str);
			Pattern pat = Pattern.compile("(\\d\\d\\d\\d)");
			Matcher mat = pat.matcher(str);
			if (mat.find()) {
				publishYear = mat.group(1);
			} else {
				publishYear = str;
			} // end if
		} // end if
	} // end setPublishYear()

	public void setEdition(String str) {
		if (str == null || (str.matches("臺.版") && strHandle.hasSomething(getPublishYear()))) {
			edition = "";
		} else {
			edition = str;
		} // end if
	} // end seteEdition()

	public void setFormat(String str) {
		if (str == null) {
			format = "";
		} else {
			format = str;
		} // end if
	} // end setFormat()

	public String getTitle() {
		return title;
	} // end getTitle
	
	public String getIssn() {
		return issn;
	}

	public String getCallNo() {
		return callNo;
	} // end getCallNo()

	public String getIlsId() {
		return ilsid;
	} // end getIlsId()

	public String[] getIlsIds() {
		return ilsids;
	} // end getIlsId()

	public List<List<String>> getHoldingInfo() {
		return holdingInfo;
	} // end getHoldingInfo

	public int getTotalHoldings() {
		return totalHoldings;
	} // end getTotalHoldings()

	public String getCreator() {

		if (creator == null)
			return "";

		return creator;
	}// end getAuthor

	public ArrayList<String> getCreators() {
		return creators;
	} // end getAuthors

	public String getContributor() {
		if (contributor == null)
			return "";

		return contributor;
	}// end getAuthor

	public String getFormat() {
		if (format == null)
			return "";
		return format;
	} // end getFormat()

	public ArrayList<String> getContributors() {
		return contributors;
	} // end getAuthors

	public ArrayList<String> getFulltextUrls() {
		return fulltextUrls;
	} // end getFulltextUrls()

	public String getFulltextUrlsHtml() {
		String str = new String("");
		for (int i = 0; i < fulltextUrls.size(); i++) {
			if (i != 0) {
				str += "<br>";
			} // end if
			str += "<a href='" + fulltextUrls.get(i) + "'>" + fulltextUrls.get(i) + "</a>";
		} // end for
		return str;
	} // end getFulltextUrls()

	public String getTranslator() {
		if (translator == null)
			return "";

		return translator;
	}// end getTranslator

	public String getVolume() {
		if (volume == null)
			return "";

		return volume;
	}// end getVolume

	public ArrayList<String> getTranslators() {
		return translators;
	} // end getTranslators

	public String getPublisher() {
		if (publisher == null)
			return "";

		return publisher;
	} // end getPublisher()

	public String getPublishYear() {
		if (publishYear == null)
			return "";

		return publishYear;
	} // end getPublishYear()

	public String getEdition() {
		if (edition == null)
			return "";

		return edition;
	} // end GetEdition()

	public String getSubject() {
		if (subject == null)
			return "";

		return subject;
	} // end getSubject()

	public ArrayList<String> getSubjects() {
		return subjects;
	} // end getSubjects

	public ArrayList<String> getAltTitles() {
		return altTitles;
	} // end getSubjects

	public String getPrimoLink() {
		if (primoLink == null)
			return "";

		return primoLink;
	} // end getPrimoLink()

	public String getBookType() {
		if (bookType == null)
			return "";

		return bookType;
	} // end getBookType()

	/*
	 * getHoldingText() retrieve library holdings of an item and present in a human
	 * readable way.
	 */
	public String getHoldingText() {

		if (holdingInfo.size() == 0) {
			return "";
		} // end if

		String str = new String("");

		for (int i = 0; i < holdingInfo.size(); i++) {
			str += "Holding Record " + (i + 1) + ": ";
			str += " [Institution: " + holdingInfo.get(i).get(0);
			str += " Location: " + holdingInfo.get(i).get(1);
			str += " Call No.: " + holdingInfo.get(i).get(2);
			str += " Status: " + holdingInfo.get(i).get(3);
			str += ".]";
			str += "\n";
		} // end for

		return str;
	} // end getHoldingText()

	public int parseVolume() {
		return parseVolume(getVolume());
	} // end parseVolume

	public int parseVolume(String str) {

		boolean isVol = false;

		if (str.contains("v.") || str.contains("pt.") || str.contains("vol") || str.contains("vd."))
			isVol = true;

		if (!isVol)
			return -1;

		if (str.contains("yearvol")) {
			Pattern pattern = Pattern.compile(" (\\d{4})");
			Matcher matcher = pattern.matcher(str);
			if (matcher.find()) {
				str = matcher.group(0);
			} // end if
		} // end if

		str = str.replaceAll("^.*vd.|^.*v\\.|^.*pt\\.|^.*vol|c\\..*$", "");

		str = strHandle.extractNumeric(str);
		if (str == null || str.equals("") || str.length() > 4)
			return -1;

		try {
			return Integer.parseInt(str);
		} // end try

		catch (Exception e) {
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			String errStr = "BookItem:parseVolume()" + errors.toString();
			System.out.println(errStr);
			return -1;
		}
	} // end parseVolume

	public double parseEdition() {
		return parseEdition(edition);
	} // end parseEdition

	public double parseEdition(String edition) {
		edition = edition.toLowerCase();
		if (edition.equals("-1") || edition.equals("") || edition == null) {
			return -1;
		} // end if

		if (CJKStringHandling.isCJKString(edition)) {
			edition = strHandle.normalizeString(edition);
			edition = edition.toLowerCase();
			edition = edition.replaceAll("tai.*", "");
			edition = edition.replaceAll("di.*", "");
			edition = edition.replaceAll("第", "");
			edition = edition.replaceAll("初", "一");
			edition = edition.replaceAll("再版", "一");
			edition = edition.replaceAll("二十", "廿");
			edition = edition.replaceAll("三十", "卅");
			String[] chiEd = { "一", "二", "三", "四", "五", "六", "七", "八", "九", "十", "十一", "十二", "十三", "十四", "十五", "十六",
					"十七", "十八", "十九", "廿", "廿一", "廿二", "廿三", "廿四", "廿五", "廿六", "廿七", "廿八", "廿九", "卅" };
			for (int i = chiEd.length - 1; i >= 0; i--) {
				edition = edition.replaceAll(chiEd[i], (i + 1) + "");
			} // end for
			if (edition.matches(".*修訂.*|.*增訂.*|.*校訂.*")) {
				edition = edition.replaceAll(".*xiu.*", "");
				edition = edition.replaceAll(".*zeng.*", "");
				String minor_ed_str = strHandle.extractNumeric(edition);
				if (minor_ed_str == null || minor_ed_str.equals("")) {
					minor_ed_str = "1";
				} // end if
				int minor_ed = Integer.parseInt(minor_ed_str);
				return Double.parseDouble("1." + minor_ed);
			} // end if
		} else {
			edition = edition.toLowerCase();
			edition = edition.replaceAll("zai ban", "first");
			if (edition.contains("rev.") || edition.contains("revise") || edition.contains("expand")
					|| edition.contains("new ed")) {
				return 2.1;
			} // end if

			String[] engEd = { "first", "second", "third", "fourth", "fifth", "sixth", "seventh", "eighth", "ninth",
					"tenth", "eleventh", "twelfth", "thirteenth", "fifteenth", "sixteenth", "seventeenth", "nineteenth",
					"twentieth" };

			for (int i = engEd.length - 1; i >= 0; i--) {
				edition = strHandle.normalizeString(strHandle.tidyString(edition));
				edition = edition.replaceAll(strHandle.normalizeString(engEd[i]), (i + 1) + "");
			} // end for
		} // end if
		edition = edition.replaceAll("[^0-9]", "");

		if (edition.equals("1") || edition.equals(""))
			edition = "1";

		if (!edition.equals("")) {
			return Double.parseDouble(edition);
		} // end if
		return -1;
	} // end parseEdition

	public String standizePublisherWording() {
		return stardizePublisherWording(publisher);
	} // removeCommonPublisherWording()

	public String stardizePublisherWording(String str) {
		if (!strHandle.hasSomething(str))
			return "";
		str = str.replace("明窗", "明報");
		// The array is for trimming off common publishing company keywords
		// before matching.
		String[] dkeys = { " limited", " publications", " corporation", " publishing", " company", " books", " book",
				" row", " colophon", "ltd$", " co$", " & co$", " pub$", " Inc$", " group$", " press$", " distributor$",
				" $", "有限公司", "企管顧問", "企管顾问", "股份", "出版社", "出版", "資訊科技", "资讯科技", "出版企業", "出版企业", "書局", "书局", "大學", "大学",
				"師範", "师范", "股彬", "文化企業", "書店", "發行", "印书馆", "印書館", "公司", "事業", "事业" };

		str = strHandle.tidyString(str);

		/*
		 * Trim off the publishing place Note: "Lazy" quantifier of regular expression
		 * is used.
		 */
		str = str.replaceAll("^.*?:", "");

		for (int i = 0; i < dkeys.length; i++) {
			str = str.replaceAll(dkeys[i], "");
		} // end for

		return str;
	} // end stardizePublisherWording()

	public String convMarcRaw(String str) {

		return "";
	} // end convMarcRaw

	public String toString() {
		String out = "";
		if (creator != null)
			out = "Creator:" + getCreator() + "\n";

		if (title != null)
			out += "Title: " + getTitle() + "\n";

		if (isbn.getIsbn() != null && !isbn.getIsbn().equals(""))
			out += "ISBN:f" + isbn.getIsbn() + "\n";

		if (isbn.getIsbns() != null)
			out += "ISBNS:" + isbn.getIsbns() + "\n";

		if (publisher != null)
			out += "Publisher:" + getPublisher() + "\n";

		if (publishYear != null)
			out += "Publish Year:" + getPublishYear() + "\n";

		if (callNo != null)
			out += "Call No:" + getCallNo() + "\n";

		return out;
	} // end toString()

	public boolean isMultiVolume() {
		String format = getFormat();
		format = format.toLowerCase();
		format = strHandle.trimSpace(format);
		if (format.contains("全一冊") || format.matches("^1V\\..*") || format.matches("^1冊.*") || format.matches("^一冊.*"))
			return false;

		if (format.contains("v.") || format.contains("vol") || format.contains("pt.") || format.matches(".*冊.*"))
			return true;

		return false;
	} // end isMultiVolume()

} // end class ChkBkByISBN()