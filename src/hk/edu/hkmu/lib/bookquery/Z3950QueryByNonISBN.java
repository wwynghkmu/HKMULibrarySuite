package hk.edu.hkmu.lib.bookquery;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;

import hk.edu.hkmu.lib.*;

/**
 * 
 * Z39.50 remove query by Author, Title, Publisher, Publishing Year, Edition,
 * Volume, and Institute. Author and Title are mandatory. For Publisher,
 * Publishing Year, or Edition, at least one must be filled. Volume and Institute
 * are optional. "-1" is the default code for note specifying edition/volume
 * information.
 * 
 * @author Wai-yan NG
 * @author wwyng@ouhk.edu.hk
 * @version 1.0
 * @since Jan 29, 2019
 */
public class Z3950QueryByNonISBN extends Z3950Query {

	public Z3950QueryByNonISBN(String author, String title, String publisher, String year, String edition, String vol,
			String inst) {
		super(inst);
		clearQuery();
		queryBk = new BookItem();
		queryBk.setPublishYear(year);
		queryBk.setVolume(vol);
		queryBk.setEdition(edition);
		queryBk.setTitle(title);
		queryBk.setCreator(author);
		queryBk.setPublisher(publisher);
		if (query()) {
			checkAva(queryBk.parseVolume());

			// Cater the case that there are two BIB records and the first one
			// is not available.
			// In this case 2nd BIB record is tried for availbility.
			while (!ava && nextRecord()) {
				String tempHoldingXML = holdingXML;
				String tempResult = result;
				byte[] tempMarcRaw = bk.marc.getMarcRaw();
				holdingXML = nextHoldingXML;
				result = nextResult;
				if (matched() && checkAva(queryBk.parseVolume())) {
					copyNextRecToCurrentRec();
				} else {
					holdingXML = tempHoldingXML;
					result = tempResult;
					bk.marc.setMarcRaw(tempMarcRaw);
					setBookInfo();
					matched();
					checkAva(queryBk.parseVolume());
				} // end if
			} // end while

			closeConnection();
		} // end if
	} // Z3950QueryByNonISBN()

	public Z3950QueryByNonISBN() {
		super();
	} // Z3950QueryByNonISBN()

	public Z3950QueryByNonISBN(String inst) {
		super(inst);
	} // Z3950QueryByNonISBN()

	public boolean query(String author, String title, String publisher, String year, String edition, String vol,
			String inst) {
		clearQuery();
		Config.init(inst);
		queryBk.setTitle(title);
		queryBk.setPublisher(publisher);
		queryBk.setPublishYear(year);
		queryBk.setEdition(edition);
		queryBk.setVolume(vol);
		queryBk.setCreator(author);
		return query();
	} // end query()

	public boolean query() {
		String qt = queryBk.getTitle();

		qt = qt.trim();

		String qc = queryBk.getCreator();
		qc = qc.trim();
		String qp = queryBk.getPublisher();
		qp = qp.trim();
		String qy = queryBk.getPublishYear();
		qy = qy.trim();

		if (!strHandle.hasSomething(qt) || !strHandle.hasSomething(qc)) {
			return false;
		} // end if

		if (CJKStringHandling.isCJKString(qt)) {
			qt = qt.replaceAll("[\\s| ].*", "");
			qt = CJKStringHandling.removeNonCJKChars(qt);

		} // end if

		String qtc = "";

		String qcc = "";
		String inst = Config.VALUES.get("INST_CODE");
		String eaccSys = Config.VALUES.get("EACC_SYSTEM_" + inst);
		boolean isEACC = false;
		if (strHandle.hasSomething(eaccSys) && eaccSys.contains("YES"))
			isEACC = true;

		if (result != null && CJKStringHandling.isCJKString(qt) && isEACC) {
			int iterateNo = qt.length();
			if (iterateNo > 8) {
				iterateNo = 8;
			} // end if

			for (int i = 0; i < iterateNo; i++) {

				qtc += "{" + CJKStringHandling.UnicodeCharToEACCNumber(qt.charAt(i)) + "}";

			} // end for
			qt = qtc;
		} // end if

		System.out.println(qc);
		if (result != null && CJKStringHandling.isCJKString(qc) && isEACC) {
			for (int i = 0; i < qc.length(); i++) {
				qcc += "{" + CJKStringHandling.UnicodeCharToEACCNumber(qc.charAt(i)) + "}";
			} // end for
			qc = qcc;
		} // end if

		qt = qt.toLowerCase();
		qt = strHandle.removeFirstArticles(qt);

		if (!isEACC) {
			qt = strHandle.trimSpecialChars(qt);
		} // end if

		int noOfCri = 0;
		queryStr = "@attr 1=4 @attr 4=2 \"" + qt + "\" ";

		String queryStrAdditional = queryStr;

		if (strHandle.hasSomething(qy)) {
			queryStrAdditional += " @attr 1=31 " + qy;
			noOfCri++;
		} // end if

		if (strHandle.hasSomething(qc)) {
			queryStrAdditional += " @attr 1=1003 " + "\"" + qc + "\"";
			noOfCri++;
		} // end if

		for (int i = 0; i < noOfCri; i++) {
			queryStrAdditional = "@and " + queryStrAdditional;
		} // end for

		boolean querySuccess = remoteQuery(queryStrAdditional);

		if (!querySuccess) {
			querySuccess = remoteQuery(queryStr);
			queryStrAdditional = queryStr;
		} // end if

		if (querySuccess) {
			debug += "First Query + " + queryStrAdditional + " \n";
			if (matched()) {
				queryStr = queryStrAdditional;
				return true;
			} // end if
		} // end if

		queryStr = "@attr 1=4 @attr 3=3 @attr 4=2 \"" + qt + "\" ";
		if (remoteQuery(queryStr)) {
			debug += "\n\n DEBUG: Remedy query " + queryStr + "\n";
			if (matched())
				return true;

			queryStr = "@attr 1=4 @attr 4=2 \"" + qt + "\" ";
			debug += "\n\n DEBUG: Final query " + queryStr + "\n";

			if (remoteQuery(queryStr) && match())
				return true;

		} // end if

		result = "";
		resultBytes = null;

		return false;
	} // end query()

	private boolean matchYear() {
		String qy = queryBk.getPublishYear();
		if (!strHandle.hasSomething(qy)) {
			return false;
		} // end if

		try {
			BufferedReader bufReader = new BufferedReader(new StringReader(result));
			String line = null;
			while ((line = bufReader.readLine()) != null) {
				if (line.toLowerCase().matches("^264.*") || line.toLowerCase().matches("^260.*")
						|| line.toLowerCase().matches("^008.*")) {
					line = line.toLowerCase();
					line = line.trim();
					if (line.matches("^008.*")) {
						line = line.substring(11, 16);
					} // end if
					line = line.replaceAll("^.*\\$a", "");
					line = strHandle.extractNumeric(line);
					debug += " match year LINE: " + line + "\n";
					debug += " match year qy: " + qy + "\n";
					if (line.contains(qy) || qy.contains(line)) {
						debug += "MATCH YEAR\n";
						return true;
					} // end if
				} // end if
			} // end while
		} // end try
		catch (Exception e) {
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			String errStr = "Z3950QueryByNonISBN:matchEdition()" + errors.toString();
			System.out.println(errStr);
			errMsg = errStr;
		} // end catch()
		debug += "NOT MATCH YEAR\n";
		return false;
	} // end matchYear()

	private boolean matchAuthor() {

		String qc = queryBk.getCreator();
		if (!strHandle.hasSomething(qc)) {
			return false;
		} // end if

		if (CJKStringHandling.isCJKString(qc)) {
			qc = CJKStringHandling.standardizeVariantChinese(qc);
			qc = CJKStringHandling.convertToSimpChinese(qc);
		} // end if

		qc = strHandle.trimSpecialChars(qc);
		qc = strHandle.removeAccents(qc);
		qc = qc.toLowerCase();
		qc = qc.replaceAll("\\d", "");
		while (qc.matches(".* [a-z] .*")) {
			qc = qc.replaceAll(" [a-z] ", " ");
		} // end while

		String[] qcNames = qc.split(" ");

		try {
			BufferedReader bufReader = new BufferedReader(new StringReader(result));
			String line = null;
			while ((line = bufReader.readLine()) != null) {
				if (line.matches("^100.*") || line.matches("^260.*") || line.matches("^264.*") || line.matches("^700.*")
						|| line.contains("6260") || line.contains("6264") || line.contains("6100")
						|| line.contains("6700") || line.matches("^245.*") || line.contains("6245")) {
					if (line.matches("^245.*") || line.contains("6245")) {
						line = line.replaceAll("^.*\\$c", "");
					} // end if
					line = line.toLowerCase();
					line = line.replaceAll("^.*\\$a", "");
					line = line.replaceAll("\\$[a-z]", "");
					line = strHandle.trimSpecialChars(line);
					line = strHandle.removeAccents(line);
					line = line.replaceAll("\\d", "");

					while (line.matches(".* [a-z] .*")) {
						line = line.replaceAll(" [a-z] ", " ");
					} // end while
					if (CJKStringHandling.isCJKString(line)) {
						line = CJKStringHandling.standardizeVariantChinese(line);
						line = CJKStringHandling.convertToSimpChinese(line);
					} // end if

					debug += " match author LINE: " + line + "\n";
					debug += " match author qc: " + qc + "\n";

					for (int i = 0; i < qcNames.length; i++) {
						if (line.contains(qcNames[i])) {
							debug += "MATCH AUTHOR\n";
							return true;
						} // end if
					} // end for
				} // end if
			} // end while
		} // end try
		catch (Exception e) {
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			String errStr = "Z3950QueryByNonISBN:matchAuthor()" + errors.toString();
			System.out.println(errStr);
			errMsg = errStr;
		} // end catch()
		debug += "NOT MATCH AUTHOR\n";
		return false;
	} // matchAuthor

	private boolean matchEdition() {

		String qe = queryBk.getEdition();

		if (!strHandle.hasSomething(qe)) {
			debug += "MATCHEDITION (cos NO Query Edition)\n";
			return true;
		} // end if

		double ed = queryBk.parseEdition(qe);

		if (ed < 0) {
			debug += "MATCHEDITION (cos NO Query Edition < 0)\n";
			return true;
		} // end if

		try {
			BufferedReader bufReader = new BufferedReader(new StringReader(result));
			String line = null;
			boolean noResultEdition = true;
			while ((line = bufReader.readLine()) != null) {
				if (line.matches("^250.*") || line.contains("$6250")) {
					line = line.replaceAll(".*\\$a", "");
					debug += "match edition LINE: " + queryBk.parseEdition(line) + "\n";
					debug += "match edition ed: " + ed + "\n";
					noResultEdition = false;
					if (queryBk.parseEdition(line) == ed) {
						debug += "MATCH EDITION\n";
						return true;
					} // end if
				} // end if
			} // end while
			if (noResultEdition) {
				if (ed == 1) {
					debug += "match edition LINE: NO EDITION\n";
					debug += "match edition ed: " + ed + "\n";
					debug += "MATCH EDITION\n";
					return true;
				} // end if
			} // end if
		} // end try
		catch (Exception e) {
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			String errStr = "Z3950QueryByNonISBN:matchEdition()" + errors.toString();
			System.out.println(errStr);
			errMsg = errStr;
		} // end catch()
		debug += "match edition: qed " + ed + "\n";
		debug += "NOT MATCH EDITION\n";
		return false;
	} // end matchEdition()

	private boolean matchPublisher() {

		String qPub = queryBk.standizePublisherWording().toLowerCase();
		if (!strHandle.hasSomething(qPub)) {
			return false;
		} // end if

		if (CJKStringHandling.isCJKString(qPub)) {
			qPub = CJKStringHandling.removeNonCJKChars(qPub);
			qPub = CJKStringHandling.standardizeVariantChinese(qPub);
			qPub = CJKStringHandling.convertToSimpChinese(qPub);
		} // end if

		try {
			BufferedReader bufReader = new BufferedReader(new StringReader(result));
			String line = null;

			while ((line = bufReader.readLine()) != null) {

				if (line.toLowerCase().matches("^260.*|^264.*|.*6260.*|.*6264.*")) {

					line = line.replaceAll("^.*?\\$a", "");
					line = line.replaceAll("\\$c.*$", "");
					line = line.toLowerCase();
					line = strHandle.trimSpecialChars(line);
					line = strHandle.trimNumeric(line);
					line = queryBk.stardizePublisherWording(line);

					if (CJKStringHandling.isCJKString(line)) {
						line = CJKStringHandling.removeNonCJKChars(line);
						line = CJKStringHandling.standardizeVariantChinese(line);
						line = CJKStringHandling.convertToSimpChinese(line);
					} // end if

					debug += " match pub Line: " + line + "\n";
					debug += " matchpub qpub: " + qPub + "\n";
					if (line.contains(qPub)) {
						debug += "MATCH PUBLISHER\n";
						return true;
					} // end if
				} // end if
			} // end while
		} // end try

		catch (Exception e) {
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			String errStr = "Z3950QueryByNonISBN:matchPublisher()" + errors.toString();
			System.out.println(errStr);
			errMsg = errStr;
		}
		debug += "NOT MATCH PUBLISHER\n";
		return false;
	} // end matchPublisher()

	private boolean matchTitle() {
		String qt = queryBk.getTitle();

		qt = strHandle.removeAccents(qt);
		qt = strHandle.trimNewLineChar(qt);
		qt = qt.toLowerCase();
		qt = strHandle.removeArticles(qt);

		if (!strHandle.hasSomething(qt)) {
			return false;
		} // end if

		if (CJKStringHandling.isCJKString(qt)) {
			qt = qt.replaceAll("年$", "");
			qt = CJKStringHandling.removeNonCJKChars(qt);
			qt = CJKStringHandling.standardizeVariantChinese(qt);
			qt = CJKStringHandling.convertToSimpChinese(qt);
		} // end if

		try {
			BufferedReader bufReader = new BufferedReader(new StringReader(result));
			String line = null;

			while ((line = bufReader.readLine()) != null) {

				if (line.toLowerCase().matches(".*6245.*|^245.*")) {
					line = line.replaceAll("^.*\\$a", "");
					line = line.replaceAll("\\$c.*$", "");
					line = line.replaceAll("\\$b", "");
					line = line.replaceAll("\\$h", "");
					line = line.replaceAll("\\[electronic resource\\]", "");
					line = line.toLowerCase();
					line = strHandle.trimSpecialChars(line);
					line = strHandle.trimNewLineChar(line);
					line = strHandle.removeArticles(line);
					line = strHandle.removeAccents(line);

					if (CJKStringHandling.isCJKString(line)) {
						qt = qt.replaceAll("年$", "");
						line = CJKStringHandling.removeNonCJKChars(line);
						line = CJKStringHandling.standardizeVariantChinese(line);
						line = CJKStringHandling.convertToSimpChinese(line);
					} // end if
					debug += "match title LINE: " + line + "\n";
					debug += "match title qt: " + qt + "\n";
					if (strHandle.containWords(line, qt) || strHandle.containWords(qt, line)) {
						debug += "MATCHTITLE\n";
						return true;
					} // end if
				} // end if
			} // end while
		} // end try

		catch (Exception e) {
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			String errStr = "Z3950QueryByNonISBN:matchTitle()" + errors.toString();
			System.out.println(errStr);
			errMsg = errStr;
		}
		debug += "NOT MATCHTITLE\n";
		return false;
	} // end matchTitle()

	private boolean matched() {

		String qt = queryBk.getTitle();
		qt = qt.trim();
		String qc = queryBk.getCreator();
		qc = qc.trim();
		String qp = queryBk.getPublisher();
		qp = qp.trim();
		String qy = queryBk.getPublishYear();
		qy = qy.trim();
		String qe = queryBk.getEdition();
		qe = qe.trim();

		for (int i = 0; i < resultSet.getHitCount(); i++) {

			// Evaluate 2^3 - 1 = 7 possibilities. Minusing all void inputs, as a result,
			// one of the three inputs (year, publisher, or edition) must appear.
			if (strHandle.hasSomething(qe) && strHandle.hasSomething(qy) && strHandle.hasSomething(qp)) {
				if (matchTitle() && matchPublisher() && matchYear() && matchEdition() && matchAuthor()) {
					match = true;
					setBookInfo();
					return true;
				} // end if

			} else if (!strHandle.hasSomething(qe) && strHandle.hasSomething(qy) && strHandle.hasSomething(qp)) {
				if (matchTitle() && matchAuthor() && matchYear() && matchPublisher()) {
					match = true;
					setBookInfo();
					return true;
				} // end if

			} else if (!strHandle.hasSomething(qe) && !strHandle.hasSomething(qy) && strHandle.hasSomething(qp)) {
				if (matchTitle() && matchAuthor() && matchPublisher()) {
					match = true;
					setBookInfo();
					return true;
				} // end if

			} else if (!strHandle.hasSomething(qe) && strHandle.hasSomething(qy) && !strHandle.hasSomething(qp)) {
				if (matchTitle() && matchAuthor() && matchYear()) {
					match = true;
					setBookInfo();
					return true;
				} // end if

			} else if (strHandle.hasSomething(qe) && !strHandle.hasSomething(qy) && !strHandle.hasSomething(qp)) {
				if (matchTitle() && matchAuthor() && matchEdition()) {
					match = true;
					setBookInfo();
					return true;
				} // end if

			} else if (strHandle.hasSomething(qe) && strHandle.hasSomething(qy) && !strHandle.hasSomething(qp)) {
				if (matchTitle() && matchAuthor() && matchEdition() && matchYear()) {
					match = true;
					setBookInfo();
					return true;
				} // end if

			} else if (strHandle.hasSomething(qe) && !strHandle.hasSomething(qy) && strHandle.hasSomething(qp)) {
				if (matchTitle() && matchAuthor() && matchEdition() && matchPublisher()) {
					match = true;
					setBookInfo();
					return true;
				} // end if

			} // end if

			// If no matched, loop to next record for further matching.
			if (nextRecord()) {
				copyNextRecToCurrentRec();
			} // end if
		} // end for
		match = false;
		return false;

	} // end matched()
} // end class
