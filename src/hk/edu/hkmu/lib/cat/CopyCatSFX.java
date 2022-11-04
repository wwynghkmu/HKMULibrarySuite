package hk.edu.hkmu.lib.cat;

import javax.xml.parsers.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;
import java.util.*;
import java.io.*;
import org.apache.commons.io.input.*;
import hk.edu.hkmu.lib.*;
import hk.edu.hkmu.lib.bookquery.*;
import hk.edu.hkmu.lib.cat.Config;

/**
 * 
 * OUHK In-house Library Tool - Accepts a SFX generated MARC source file and
 * remotely fetch call numbers from Z39.50 providers which are defined in
 * config.txt
 * 
 * @author Wai-yan NG
 * @author wwyng@ouhk.edu.hk
 * @version 1.0
 * @since Mar 7, 2019
 */

public class CopyCatSFX {

	ArrayList<String> qArryList;
	ArrayList<String> activeZ3950ServerList;
	String requestFile;

	/*
	 * Constructor of creating object with null properties.
	 */
	public CopyCatSFX() {
		clear();
	} // end CopyCatSFX()

	/*
	 * Constructor for getting the enriched SFX contents and reporting in tabbed CSV
	 * format.
	 * 
	 * @param file the file of source query in MARC XML format
	 * 
	 * @param reportFileFullPath The directory for writing the reports; must have a
	 * writable permission.
	 * 
	 * 
	 */
	public CopyCatSFX(File file, String reportFileFullPath) {
		clear();
		Config.init();
		initZ3950Connection();
		requestFile = file.getAbsolutePath();
		sfxEnrichment(file, reportFileFullPath, activeZ3950ServerList, qArryList, "");
	} // end CopyCatSFX()

	/*
	 * Constructor for restarting to get the enriched SFX contents and reporting in
	 * tabbed CSV format.
	 * 
	 * @param file the file of source query in MARC XML format
	 * 
	 * @param reportDirectory The directory for writing the reports; must have a
	 * writable permission.
	 * 
	 * @param restartFilehead The source query file name (without the extension)
	 * that is intended to restart the query.
	 */
	public CopyCatSFX(File file, String reportDirectory, String restartFilehead) {
		clear();
		Config.init();
		initZ3950Connection();
		try {
			if (restartFilehead != null || !restartFilehead.equals("")) {
				FileReader notObtainedFile = new FileReader(reportDirectory + restartFilehead + "-SFXNotObtained.txt");
				BufferedReader reader = new BufferedReader(notObtainedFile);
				String line = "";
				do {
					line = reader.readLine();
				} while (!line.contains("Request File:"));
				String reqFile = line.replaceAll("Request File:", "").trim();
				requestFile = reqFile;
				file = new File(reqFile);
				reader.close();
				notObtainedFile.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		sfxEnrichment(file, reportDirectory, activeZ3950ServerList, qArryList, restartFilehead);
	} // end CopyCatSFX()

	private void clear() {
		qArryList = null;
		activeZ3950ServerList = null;
		requestFile = "";
	} // end clear()

	private void initZ3950Connection() {
		try {
			String queryList = Config.VALUES.get("COPYCAT_QUERY_LIST");
			qArryList = new ArrayList<String>(Arrays.asList(queryList.split(",")));
			activeZ3950ServerList = new ArrayList<String>();
			activeZ3950ServerList = obtainViableZ3950Server(qArryList);
		} // end try
		catch (Exception e) {
			System.out.println("CopyCatSFX:initZ3950Connection():");
			e.printStackTrace();
			try {
				Thread.sleep(3000);
			} catch (Exception e2) {
			}
		} // end catch
	} // end initZ3950Connection();

	private ArrayList<String> obtainViableZ3950Server(ArrayList<String> al) {
		ArrayList<String> os = new ArrayList<String>();
		for (Object s : al) {
			Z3950QueryByISBN zq = new Z3950QueryByISBN();
			boolean viable = zq.testConnection(s.toString());
			zq.closeConnection();
			if (viable)
				os.add(s.toString());
			else
				System.out.println("No Z3950 connection in Z3950ISBN class");
		} // end if

		for (Object s : os) {
			al.remove(s);
		} // end for

		return (ArrayList<String>) os;
	} // end obtainViableZ3950Server()

	private void sfxEnrichment(File file, String reportFileFullPath, ArrayList<String> qList,
			ArrayList<String> qListFail, String restartFilehead) {
		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();
			XMLHandle userhandler = new XMLHandle(qList, qListFail, reportFileFullPath, requestFile, restartFilehead);
			saxParser.parse(file, userhandler);
		} // end try
		catch (Exception e) {
			System.out.println("CopyCatSFX:sfxEnrichment():");
			e.printStackTrace();
		} // end catch
	}

} // end class CopyCatSFX

class XMLHandle extends DefaultHandler {

	ArrayList<String> qList;
	ArrayList<String> qListFail;
	String requestFile;
	String restartFilehead;
	String lastQueriedObjectID;
	boolean restartQueryFlag = false;

	int fetchRec = 0;
	int conseQueryCount = 0;
	int failRec = 0;

	boolean objId = false;
	boolean issn = false;
	boolean isbn = false;
	boolean eissn = false;
	boolean local = false;

	boolean objIdTag = false;
	boolean issnTag = false;
	boolean isbnTag = false;
	boolean eissnTag = false;
	boolean localTag = false;

	int count = 0;
	String objIdStr = "";
	String issnStr = "";
	String isbnStr = "";
	String eissnStr = "";
	String localStr = "";
	String lastQueryInst = "";
	protected Z3950QueryByISSN zqISSN;
	protected Z3950QueryByISBN zqISBN;

	String writePath;
	BufferedWriter writerOutput;
	BufferedWriter writerReport;
	BufferedWriter writerLog;
	BufferedWriter writerComFile;

	XMLHandle(ArrayList<String> l, ArrayList<String> l2, String reportFileFullPath, String requestFile,
			String restartFilehead) {
		qList = l;
		qListFail = l2;

		zqISSN = new Z3950QueryByISSN();
		zqISBN = new Z3950QueryByISBN();
		writePath = reportFileFullPath;
		this.requestFile = requestFile;
		this.restartFilehead = restartFilehead;
		lastQueriedObjectID = "";
	} // end XMLHandle()

	public void startDocument() {
		try {
			String fileHead = "";
			String now = StringHandling.getToday();
			if (restartFilehead == null || restartFilehead.equals("")) {
				fileHead = now;
			} else {
				fileHead = restartFilehead;
				File reportFile = new File(writePath + restartFilehead + "-SFXout.txt");
				ReversedLinesFileReader reader = new ReversedLinesFileReader(reportFile);
				String line = reader.readLine();

				while (line.equals(""))
					line = reader.readLine();

				String[] arry = line.split("\t");
				lastQueriedObjectID = arry[0];
				restartQueryFlag = true;
				reader.close();
			} // end if

			String path = writePath + fileHead;
			File file = new File(path + "-SFXout.txt");
			File comFile = new File(path + "-Completed.txt");
			File file2 = new File(path + "-SFXNotObtained.txt");
			File file3 = new File(path + "-SFXEnrichLog.txt");

			writerOutput = new BufferedWriter(new FileWriter(file, true));
			writerReport = new BufferedWriter(new FileWriter(file2, true));
			writerLog = new BufferedWriter(new FileWriter(file3, true));
			writerComFile = new BufferedWriter(new FileWriter(comFile));

			String liveServerList = "";
			String serverList = "";

			for (String str : qList) {
				liveServerList += str + ",";
			} // end for

			for (String str : qListFail) {
				serverList += str + ",";
			} // end for

			if (!(restartFilehead == null || restartFilehead.equals("")))
				writerReport.write("\n\nRESTART report fetching\n");

			writerReport.write("Request File: " + requestFile + "\n");
			if (!(restartFilehead == null || restartFilehead.equals("")))
				writerReport.write("Writing report RESTARTS at: " + now + "\n");
			else
				writerReport.write("Writing report STARTS at: " + now + "\n");

			writerReport.write("Alive server list: " + liveServerList + "\n");
			writerReport.write("Failed server list: " + serverList + "\n");

		} catch (Exception e) {
		}
	}

	public void endDocument() {
		try {
			writerComFile.write("YES");
			writerComFile.close();
			writerOutput.close();
			writerReport.close();
			writerLog.close();
		} catch (Exception e) {
		}
	}

	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

		if (qName.equalsIgnoreCase("datafield") && attributes.getValue("tag").equalsIgnoreCase("090")) {
			objIdTag = true;
		} else if (qName.equalsIgnoreCase("datafield") && attributes.getValue("tag").equalsIgnoreCase("022")) {
			issnTag = true;
		} else if (qName.equalsIgnoreCase("datafield") && attributes.getValue("tag").equalsIgnoreCase("020")) {
			isbnTag = true;
		} else if (qName.equalsIgnoreCase("datafield") && attributes.getValue("tag").equalsIgnoreCase("776")) {
			eissnTag = true;
		} else if (qName.equalsIgnoreCase("datafield") && attributes.getValue("tag").equalsIgnoreCase("901")) {
			localTag = true;
		} // end if

		if (objIdTag == true && qName.equalsIgnoreCase("subfield")
				&& attributes.getValue("code").equalsIgnoreCase("a")) {
			objId = true;
			objIdTag = false;
		} else if (issnTag == true && qName.equalsIgnoreCase("subfield")
				&& attributes.getValue("code").equalsIgnoreCase("a")) {
			issn = true;
			issnTag = false;
		} else if (eissnTag == true && qName.equalsIgnoreCase("subfield")
				&& attributes.getValue("code").equalsIgnoreCase("x")) {
			eissn = true;
			eissnTag = false;
		} else if (isbnTag == true && qName.equalsIgnoreCase("subfield")
				&& attributes.getValue("code").equalsIgnoreCase("a")) {
			isbn = true;
			isbnTag = false;
		} else if (localTag == true && qName.equalsIgnoreCase("subfield")
				&& attributes.getValue("code").equalsIgnoreCase("1")) {
			local = true;
			localTag = false;
		} // end if
	}

	public void endElement(String uri, String localName, String qName) throws SAXException {
		try {
			String logStr = "";
			logStr = "----------------------------------------------------------------------\n";
			logStr += Calendar.getInstance().getTime().toString() + "\n";

			if (!restartQueryFlag && !lastQueriedObjectID.equals(objIdStr)) {

				if (qName.equalsIgnoreCase("record")) {
					// Correct the wrong call no format in the "local string"
					if (localStr.matches(".*\\^.*")) {
						String callNo = localStr;
						callNo = callNo.replaceAll("(.*\\^)(.*)", "$2");
						callNo = zqISSN.bk.validateCallNo(callNo);
						if (callNo == null || !localStr.contains(callNo)) {
							logStr += "\n++++++++++++++++++++++\n";
							logStr += "Object ID: " + objIdStr + "\n";
							logStr += "Local String Call Number Corrected\n";
							logStr += "Original Local String: " + localStr + "\n";
							logStr += "Call Number is corrected as: " + callNo + "\n";
							localStr = localStr.replaceAll("(.*)(\\^.*)", "$1");
							logStr += "Replaced Local String: " + localStr + "\n";
							logStr += "Going to fetch online this title afresh." + "\n";
							logStr += "++++++++++++++++++++++\n";
						} // end if
					} // end if

					if (!objIdStr.equals("") && (!issnStr.equals("") || !eissnStr.equals("") || !isbnStr.equals(""))
							&& !localStr.matches(".*\\^.*")) {
						int startIndex = 0;
						int endIndex = qList.size();
						boolean loop = false;
						boolean match = false;

						if (!lastQueryInst.equals("")) {
							for (int i = 0; i < qList.size(); i++) {
								if (lastQueryInst.equals(qList.get(i))) {
									conseQueryCount++;
									if (conseQueryCount == 101) {
										logStr = "----------------------------------------------------------------------\n";
										writerLog.write("WAIT 5 MINS...");
										logStr = "----------------------------------------------------------------------\n";
										Thread.sleep(300000);
										conseQueryCount = 0;
									} // end if
									startIndex = 0;
									loop = true;
								} else {
									conseQueryCount = 0;
								} // end if
							} // end for
						}

						if ((fetchRec % 2000) == 0 && fetchRec != 0) {
							logStr = "----------------------------------------------------------------------\n";
							writerLog.write("WAIT 5 MINS...");
							logStr = "----------------------------------------------------------------------\n";
							Thread.sleep(500000);
						} // end if

						for (int i = startIndex; i < endIndex; i++) {
							logStr += "\nProvider: " + qList.get(i) + "\n";
							logStr += "Fetch Number: " + fetchRec + "\n";
							logStr += "Object ID:" + objIdStr + "\n";
							logStr += "Original Local String:" + localStr + "\n";
							if (!issnStr.equals("")) {
								String issns[] = issnStr.split(",");
								for (int j = 0; j < issns.length; j++) {
									zqISSN.query(issns[j], qList.get(i));
									logStr += "ISSN:" + issnStr + "\n";
									logStr += "ISSN Tried: " + issns[j] + "\n";
									if (zqISSN.bk.getTitle() != null && !zqISSN.bk.getTitle().equals("")) {
										logStr += "Title:" + zqISSN.bk.getTitle() + "\n";
										logStr += "Call #:" + zqISSN.bk.getCallNo() + "\n";
									} else {
										logStr += "NO RECORD FETCHED\n";
									} // end if

									if (zqISSN.match() && zqISSN.bk.getCallNo() != null
											&& !zqISSN.bk.getCallNo().equals("")) {
										match = true;
										logStr += "Queried ISSN:" + issns[j] + "\n";
										logStr += "RECORD MATCHED\n";
										logStr += "----------------------------------------------------------------------\n";
										writerLog.write(logStr);
										writerOutput.write(objIdStr + "\t" + localStr + "^" + zqISSN.bk.getCallNo()
												+ "\t" + zqISSN.bk.getTitle() + "\t" + issnStr + "\t" + eissnStr + "\t"
												+ qList.get(i) + "\t" + Calendar.getInstance().getTime().toString()
												+ "\n");
										lastQueryInst = qList.get(i);
										fetchRec++;
									} // end if
									if (match)
										break;
								} // end for
							} else if (!eissnStr.equals("")) {
								String eissns[] = eissnStr.split(",");
								for (int j = 0; j < eissns.length; j++) {
									zqISSN.query(eissns[j], qList.get(i));
									logStr += "EISSN:" + eissnStr + "\n";
									logStr += "EISSN Tried: " + eissns[j] + "\n";
									if (zqISSN.bk.getTitle() != null && !zqISSN.bk.getTitle().equals("")) {
										logStr += "Title:" + zqISSN.bk.getTitle() + "\n";
										logStr += "Call #:" + zqISSN.bk.getCallNo() + "\n";
									} else {
										logStr += "NO RECORD FETCHED\n";
									} // end if
									if (zqISSN.match() && zqISSN.bk.getCallNo() != null
											&& !zqISSN.bk.getCallNo().equals("")) {
										match = true;
										logStr += "Queried EISSN:" + eissns[j] + "\n";
										logStr += "RECORD MATCHED\n";
										logStr += "----------------------------------------------------------------------\n";
										writerLog.write(logStr);
										writerOutput.write(objIdStr + "\t" + localStr + "^" + zqISSN.bk.getCallNo()
												+ "\t" + zqISSN.bk.getTitle() + "\t" + issnStr + "\t" + eissnStr + "\t"
												+ qList.get(i) + "\t" + Calendar.getInstance().getTime().toString()
												+ "\n");
										lastQueryInst = qList.get(i);
										fetchRec++;
									} // end if
									if (match)
										break;
								} // end for
							} else if (!isbnStr.equals("")) {
								String isbns[] = isbnStr.split(",");
								for (int j = 0; j < isbns.length; j++) {
									zqISBN.query(isbns[j], qList.get(i));
									logStr += "ISBN:" + isbnStr + "\n";
									logStr += "ISBN Tried: " + isbns[j] + "\n";
									if (zqISBN.bk.getTitle() != null && !zqISBN.bk.getTitle().equals("")) {
										logStr += "Title:" + zqISBN.bk.getTitle() + "\n";
										logStr += "Call #:" + zqISBN.bk.getCallNo() + "\n";
									} else {
										logStr += "NO RECORD FETCHED\n";
									} // end if

									if (zqISBN.match() && zqISBN.bk.getCallNo() != null) {
										match = true;
										logStr += "Queried ISBN:" + isbns[j] + "\n";
										logStr += "RECORD MATCHED\n";
										logStr += "----------------------------------------------------------------------\n";
										writerLog.write(logStr);
										writerOutput.write(objIdStr + "\t" + localStr + "^" + zqISBN.bk.getCallNo()
												+ "\t" + zqISBN.bk.getTitle() + "\t" + isbnStr + "\t" + isbnStr + "\t"
												+ qList.get(i) + "\t" + Calendar.getInstance().getTime().toString()
												+ "\n");
										lastQueryInst = qList.get(i);
										fetchRec++;
									} // end if
									if (match)
										break;
								} // end for
							} // end if
							count++;
							Thread.sleep(1000);
							if (match) {
								break;
							}
						} // end for

						if (!match) {
							writerReport.write(Calendar.getInstance().getTime().toString() + ":" + objIdStr + ","
									+ localStr + "," + issnStr + "," + eissnStr + isbnStr + "\n");
							failRec++;
							logStr += "\nNOT MATCH\n";
							logStr += "SFX Object ID: " + objIdStr + "\n";
							logStr += "ISSN:" + issnStr + "\n";
							logStr += "EISSN:" + eissnStr + "\n";
							logStr += "ISBN:" + isbnStr + "\n";
							logStr += "----------------------------------------------------------------------\n";
							writerLog.write(logStr);
						} // end if

						objIdStr = "";
						issnStr = "";
						isbnStr = "";
						eissnStr = "";
						localStr = "";
					} else {
						logStr += "Object ID:" + objIdStr + "\n";
						logStr += "Original Local String:" + localStr + "\n";
						logStr += "Call Number EXIST; NOT GOING TO QUERY ONLINE." + "\n";
						logStr += "----------------------------------------------------------------------\n";
						objIdStr = "";
						issnStr = "";
						isbnStr = "";
						eissnStr = "";
						localStr = "";
						writerLog.write(logStr);
					} // end if
				} // end if
			} else {
				if (!lastQueriedObjectID.equals("") && lastQueriedObjectID.equals(objIdStr)) {
					restartQueryFlag = false;
				}
			} // end if

		} // end try
		catch (Exception e) {
			System.out.println("CopyCatSFX:XMLHandle:endElement():");
			e.printStackTrace();
		}

	}

	public void characters(char ch[], int start, int length) throws SAXException {

		if (objId) {
			objIdStr = new String(ch, start, length);
			objIdStr = objIdStr.trim();
			objId = false;
			objIdTag = false;
		} else if (issn) {
			issnStr += new String(ch, start, length) + ",";
			issnStr = issnStr.trim();
			issn = false;
			issnTag = false;
			issnStr = issnStr.replace("-", "");
		} else if (eissn) {
			eissnStr += new String(ch, start, length) + ",";
			eissnStr = eissnStr.trim();
			eissn = false;
			eissnTag = false;
			eissnStr = eissnStr.replace("-", "");
		} else if (isbn) {
			isbnStr += new String(ch, start, length) + ",";
			isbnStr = isbnStr.trim();
			isbnStr = isbnStr.replace("-", "");
			isbn = false;
			isbnTag = false;
		} else if (local) {
			localStr = new String(ch, start, length);
			localStr = localStr.trim();
			local = false;
			localTag = false;
		}
	}

} // end class XMLHandle
