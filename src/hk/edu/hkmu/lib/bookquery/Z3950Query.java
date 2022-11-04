package hk.edu.hkmu.lib.bookquery;

import java.util.*;
import java.util.concurrent.*;
import java.util.regex.*;
import javax.xml.parsers.*;
import java.io.*;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.yaz4j.*;

/**
 * An abstract class for Z39.50 query.
 * 
 * @author Wai-yan NG
 * @author wwyng@ouhk.edu.hk
 * @version 1.0
 * @since Jan 29, 2019
 */
public abstract class Z3950Query extends Query {
	protected String result;
	protected String holdingXML;
	protected String nextResult;
	protected String nextHoldingXML;
	protected ResultSet resultSet;
	protected long resultCurrInd;
	protected byte[] resultBytes;
	protected byte[] nextResultBytes;
	protected String resultMarc;
	private String queryBase;
	private String queryLogin = null;
	private String queryPass = null;
	private String lastQueryInst;
	private Connection z39c;
	private org.yaz4j.Query query;
	protected static int queryCount;

	abstract boolean query();

	public Z3950Query(String inst) {
		super(inst);
		clearQuery();
		setQueryBase();
	} // end Z3950QueryByISBN()

	public Z3950Query(String qStr, String inst) {
		super(inst);
		clearQuery();
		setQueryBase();
		queryStr = qStr;
		query();
		checkAva(queryBk.parseVolume());
	} // end Z3950QueryByISBN()

	protected void clearQuery() {
		super.clearQuery();
		result = "";
		nextResult = "";
		resultSet = null;
		resultCurrInd = 0;
		resultBytes = null;
		nextResultBytes = null;
		query = null;
		holdingXML = "";
		nextHoldingXML = "";
	} // end clearZ395Query()

	public Z3950Query() {
		super();
	} // end Z3950QueryByISBN()

	public boolean checkAva(int vol) {

		if (holdingXML == null)
			return false;

		DocumentBuilderFactory f;
		DocumentBuilder b;
		Document doc;
		try {
			int avaT = 0;
			String noLendLocation = Config.VALUES.get("NOLEND_LOCATION_" + Config.VALUES.get("INST_CODE"));
			if (noLendLocation == null)
				noLendLocation = "";
			String[] nolendLocations = noLendLocation.split(",");
			f = DocumentBuilderFactory.newInstance();
			b = f.newDocumentBuilder();
			boolean start = false;
			BufferedReader bufReader = new BufferedReader(new StringReader(holdingXML));
			holdingXML = "";
			String line = "";
			boolean holding = false;

			while ((line = bufReader.readLine()) != null) {
				if (line.toLowerCase().contains("<holdings>")) {
					start = true;
					holding = true;
				} else if (line.toLowerCase().contains("</holdings>")) {
					start = false;
				} // end if
				if (start) {
					holdingXML += line;
				} // end if
			} // end while

			if (holding) {
				holdingXML = holdingXML + "</holdings>";
				holdingXML = holdingXML.replaceAll("</holdings></holdings>", "</holdings>");
				InputSource is = new InputSource(new StringReader(holdingXML));
				doc = b.parse(is);
				nodesHoldings = doc.getElementsByTagName("holding");
				bk.clearLibHoldInfo();

				for (int i = 0; i < nodesHoldings.getLength(); i++) {
					nodesHolding = doc.getElementsByTagName("holding").item(i).getChildNodes();
					String callNo = getNodeValue("callNumber", nodesHolding);
					String volume = callNo;
					String status = getNodeValue("publicNote", nodesHolding);
					String localLocation = getNodeValue("localLocation", nodesHolding);
					bk.holdingInfo.add(new ArrayList<String>());
					bk.holdingInfo.get(i).add(Config.VALUES.get("INST_CODE"));
					bk.holdingInfo.get(i).add(localLocation);
					bk.holdingInfo.get(i).add(callNo);
					bk.holdingInfo.get(i).add(status);

					if (bk.isMultiVolume() && !volume.contains("v.") && !volume.contains("pt."))
						volume = "yearvol" + volume;
					status = strHandle.normalizeString(status);
					status = strHandle.trimSpecialChars(status);
					boolean lendable = true;

					for (int j = 0; j < nolendLocations.length; j++) {
						if (strHandle.hasSomething(nolendLocations[j]) && strHandle.normalizeString(localLocation)
								.contains(strHandle.normalizeString((nolendLocations[j]))))
							lendable = false;
					} // end for

					if ((status.equals("NOTCHCKDOUT") || status.equals("AVAILABLE")) && vol < 1
							&& (queryBk.parseVolume(volume) < 0 || !bk.isMultiVolume())) {
						ava = true;
						bib_no = 1;
						if (lendable)
							avaT++;

					} else if (vol > 0) {
						if (vol == queryBk.parseVolume(volume)) {
							ava = true;
							bib_no = 1;

							if ((status.equals("NOTCHCKDOUT") || status.equals("AVAILABLE")) && lendable)
								avaT++;
						} // end if
					} // end if

					if (avaT > 0) {
						ext_itm_no = avaT;
						ava_itm_no = avaT;
					} // end if
				} // end for
				if (!ava) {
					ava = false;
					bib_no = 1;
					ext_itm_no = 0;
					ava_itm_no = 0;
				} else {
					return true;
				} // end if
			} // end if
		} // end try
		catch (Exception e) {
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			String errStr = "Z3950Query:checkAva()" + errors.toString();
			errMsg = errStr;
			System.out.println(errStr + "\n\n" + holdingXML);
		} // end catch
		return false;
	} // end checkAva

	protected void setBookInfo() {

		if (match && strHandle.hasSomething(result)) {

			try {
				String m = result;
				BufferedReader bufReader = new BufferedReader(new StringReader(result));
				String line = null;
				while ((line = bufReader.readLine()) != null) {

					line = line.trim();
					if (strHandle.hasSomething(line))
						m += line + "\r";

					if (line.matches("^020.*"))
						bk.isbn.addIsbn(parseMARCLineValue(line).trim());

					if (line.matches("^022.*"))
						bk.setIssn(parseMARCLineValue(line).trim());

					if (line.matches("^245.*"))
						bk.setTitle(parseMARCLineValue(line).trim());

					if (line.matches("^100.*"))
						bk.setCreator(parseMARCLineValue(line).trim());

					if (line.matches("^260.*|^264.*"))
						bk.setPublisher(parseMARCLineValue(line).trim());

					if (line.matches("^300.*"))
						bk.setFormat(parseMARCLineValue(line).trim());

					if (line.matches("^300.*"))
						bk.setFormat(parseMARCLineValue(line).trim());

					if (line.matches("^050.*") || line.matches("^098.*")) {
						String callNo = parseMARCLineValue(line).trim().replaceAll("\\$b", " ");
						if (bk.validateCallNo(callNo) != null)
							bk.setCallNo(callNo);
					} // end if

				} // end while
				m = m.trim();
			} // end try
			catch (Exception e) {
				StringWriter errors = new StringWriter();
				e.printStackTrace(new PrintWriter(errors));
				String errStr = "Z3950Query:setbookInfo()" + errors.toString();
				System.out.println(errStr);
				errMsg = errStr;
			}
		} // end if
	} // end setBookInfo()

	protected String parseMARCLineValue(String str) {
		String out = "";
		Pattern pat = Pattern.compile("\\$a(.*)");
		Matcher mat = pat.matcher(str);
		if (mat.find()) {
			out += mat.group(1);
		} // end i

		return out;
	} // end parseMARCLine

	public String getResult() {
		return result;
	} // getResult()

	public byte[] getResultBytes() {
		return resultBytes;
	} // end getResultBytes()

	public void closeConnection() {

		try {
			if (z39c != null)
				z39c.close();
		} // end try
		catch (Exception e) {
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			String errStr = "Z3950Query:closeConnection()" + errors.toString();
			System.out.println(errStr);
			errMsg = errStr;
		} // end catch
	} // end closeConnection()

	public void copyNextRecToCurrentRec() {
		bk.marc.setMarcRaw(nextBk.marc.getMarcRaw());
		resultBytes = nextResultBytes;
		result = nextResult;
		holdingXML = nextHoldingXML;
	} // end copyNextRecToCurrentRec()

	protected boolean nextRecord() {
		debug += "nextrecord gethitcount(): " + resultSet.getHitCount() + "\n";
		debug += "resultCurrInd: " + resultCurrInd + "\n";
		resultCurrInd++;
		if (resultSet.getHitCount() < resultCurrInd + 1 || resultSet == null) {
			return false;
		} else {
			try {
				org.yaz4j.Record rec = resultSet.getRecord(resultCurrInd);
				if (rec != null) {
					nextBk.marc.setMarcRaw(rec.get("raw"));
					nextResultBytes = nextBk.marc.getMarcTagRaw();
					nextResult = nextBk.marc.getMarcTag();
					nextHoldingXML = rec.render();
					return true;
				} else {
					nextResult = "";
				} // end if
			} // end try
			catch (Exception e) {
				StringWriter errors = new StringWriter();
				e.printStackTrace(new PrintWriter(errors));
				String errStr = "Z3950Query:nextRecord()" + errors.toString();
				System.out.println(errStr);
				errMsg = errStr;
			}
		} // end if
		return false;
	} // end nextRecord()

	protected boolean remoteQuery(String queryStr) {

		resultCurrInd = 0;
		if (queryBase == null) {
			debug += "queryBase NULL \n";
			return false;
		} // end if

		if (z39c == null) {
			try {

				z39c = new Connection(queryBase, 0);
				if (queryLogin != null) {
					z39c.setUsername(queryLogin);
					z39c.setPassword(queryPass);
				} // end if
				z39c.connect();
				z39c.setSyntax("opac");
				lastQueryInst = Config.VALUES.get("INST_CODE");

			} // end try

			catch (Exception e) {
				StringWriter errors = new StringWriter();
				e.printStackTrace(new PrintWriter(errors));
				String errStr = "Z3950Query:remoteQuery():new Connection" + queryBase + queryStr + ": "
						+ errors.toString();
				errMsg = errStr;
			} // end catch

		} else if (!Config.VALUES.get("INST_CODE").equals(lastQueryInst) || queryCount % 400 == 0) {
			try {

				queryCount = 0;
				closeConnection();
				Thread.sleep(500);
				z39c = new Connection(queryBase, 0);
				if (queryLogin != null) {
					z39c.setUsername(queryLogin);
					z39c.setPassword(queryPass);
				} // end if
				z39c.connect();
				z39c.setSyntax("opac");
				lastQueryInst = Config.VALUES.get("INST_CODE");
			} // end try

			catch (Exception e) {
				StringWriter errors = new StringWriter();
				e.printStackTrace(new PrintWriter(errors));
				String errStr = "Z3950Query:remoteQuery():next Inst Query" + queryBase + queryStr + ": "
						+ errors.toString();
				System.out.println(errStr);
				errMsg = errStr;
			} // end catch
		} // end if

		org.yaz4j.Record rec = null;
		try {

			query = new PrefixQuery(new String(queryStr));
			z39c.option("format", "usmarc");
			resultSet = z39c.search(query);
			rec = resultSet.getRecord(0);

			if (resultSet.getHitCount() > 40) {
				debug += "result > 40\n";
				return false;
			}
			if (rec != null) {
				resultBytes = rec.get("raw");
				bk.marc.setMarcRaw(resultBytes);
				resultBytes = bk.marc.getMarcTagRaw();
				result = bk.marc.getMarcTag();
				holdingXML = rec.render();
			} else {
				result = "";
			} // end if
		} // end try

		catch (Exception e) {
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			String errStr = "Z3950Query:remoteQuery():Search:\nQuery Base: " + queryBase + "\nQuery String: " + queryStr
					+ "\n Error Msg: " + errors.toString();
			System.out.println(errStr);
			errMsg = errStr;
			debug += errMsg;
			// Probably due to connection error. Try to connect again,
			// the caller of this function would try again.
			try {
				z39c = new Connection(queryBase, 0);
				if (queryLogin != null) {
					z39c.setUsername(queryLogin);
					z39c.setPassword(queryPass);
				} // end if
				z39c.connect();
				z39c.setSyntax("opac");
			} // end try
			catch (Exception e2) {
				errors = new StringWriter();
				e.printStackTrace(new PrintWriter(errors));
				errStr = "Z3950Query:remoteQuery():catchException:" + queryBase + queryStr + ": " + errors.toString();
				System.out.println(errStr);
				errMsg = errStr;
				debug += errMsg;
			} // end catch
			return false;
		} // end catch

		if (strHandle.hasSomething(result)) {
			return true;
		} // end if

		return false;
	} // end remoteQuery()

	void setQueryBase() {
		queryBase = Config.VALUES.get("Z3950_SERVER") + ":" + Config.VALUES.get("Z3950_PORT") + "/"
				+ Config.VALUES.get("Z3950_BASE");
		queryLogin = Config.VALUES.get("Z3950_LOGIN");
		queryPass = Config.VALUES.get("Z3950_PASS");
	} // end setServerAddr()

	public String getQueryBase() {
		return queryBase;
	} // end getQueryBase()

	public boolean testConnection(String inst) {
		Config.init(inst);
		setQueryBase();

		class TestZ3950 implements Runnable {
			private String queryBase;
			private boolean isViable;

			TestZ3950(String str) {
				queryBase = str;
				isViable = true;
			} // end TesetZ3950

			public void run() {
				Connection z39c = new Connection(queryBase, 0);
				System.out.println("QBASE:" + queryBase);
				if (queryLogin != null) {
					z39c.setUsername(queryLogin);
					z39c.setPassword(queryPass);
				} // end if
				try {
					z39c.connect();
					String qStr = "@attr 1=4 philosophy";
					org.yaz4j.Query query = new org.yaz4j.PrefixQuery(new String(qStr));
					ResultSet resultSet = z39c.search(query);

					org.yaz4j.Record rec = resultSet.getRecord(0);

					if (rec == null)
						isViable = false;
					z39c.close();
					System.out.print(Calendar.getInstance().getTime().toString() + ": Able to make connection to: "
							+ queryBase + " ");
					System.out.println("Using: " + queryLogin + "/" + queryPass);
				} // end try
				catch (Exception e) {
					StringWriter errors = new StringWriter();
					e.printStackTrace(new PrintWriter(errors));
					String errStr = "Z3950Query:testConnection()" + errors.toString();
					errMsg = errStr;
					isViable = false;
					System.out.print(e.getMessage());
					System.out.println(" Using: " + queryLogin + "/" + queryPass);
				} // end catch
			} // end run()

			public boolean isViable() {
				return isViable;
			} // end isViable()
		} // end class TestZ3950

		TestZ3950 tz = new TestZ3950(getQueryBase());

		ExecutorService executor = Executors.newFixedThreadPool(1);
		Future<?> future = executor.submit(tz);
		executor.shutdown();
		try {
			future.get(5, TimeUnit.SECONDS);
		} // end try
		catch (Exception e) {
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			String errStr = "Z3950Query:testConnection()" + errors.toString();
			errMsg = errStr;
			return false;
		} // end catch
		try {
			if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
				executor.shutdownNow();
				return false;
			} // end if
		} // end try
		catch (Exception e) {
			return false;
		}
		try {
			Thread.sleep(15000);
		} catch (Exception e) {
		}
		return tz.isViable();
	} // end TestConnection()
} // end class Z3950QueryByISBN
