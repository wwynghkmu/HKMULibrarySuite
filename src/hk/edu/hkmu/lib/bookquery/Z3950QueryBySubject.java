package hk.edu.hkmu.lib.bookquery;

import java.io.BufferedReader;
import java.util.*;

import hk.edu.hkmu.lib.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;

/**
 * 
 * Z39.50 remote query by subject keywords.
 * 
 * @author Wai-yan NG
 * @author wwyng@ouhk.edu.hk
 * @version 1.0
 * @since Jan 29, 2019
 */
public class Z3950QueryBySubject extends Z3950Query {
	private String subject;
	private boolean querySuccess = false;
	private BufferedWriter writerOutput;
	private BufferedWriter writerOutputAll;
	private BufferedWriter writerComFile;
	private String writePath;
	private StringHandling handleText = new StringHandling();
	private ArrayList<String> resultList = new ArrayList<String>();

	public Z3950QueryBySubject() {
		super();
	} // Z3950QueryByNonISBN()

	public Z3950QueryBySubject(String inst) {
		super(inst);
	} // Z3950QueryByNonISBN()

	public Z3950QueryBySubject(String subject, String inst, String wp) {

		super(inst);

		this.subject = subject;
		this.writePath = wp;
		querySuccess = query(subject, inst);
		long hitCount = resultSet.getHitCount();

		String title = "";
		String title880 = "";
		String author = "";
		String author880 = "";
		String publisher = "";
		String publisher880 = "";
		String isbn = "";
		String subjectLines = "";
		int pubyear = 0;
		long lastQueryNo;

		try {
			String now = StringHandling.getToday();
			File file = new File(writePath + now + "-Subject-" + subject + "-Out.txt");
			File comFile = new File(writePath + now + "-Subject-" + subject + "-Completed.txt");
			writerOutput = new BufferedWriter(new FileWriter(file));
			writerComFile = new BufferedWriter(new FileWriter(comFile));

			System.out.println("Total Hit: " + resultSet.getHitCount());
			writerOutput.write("Search Subject: " + subject + "\n\r");
			writerOutput.write("Total Hit: " + resultSet.getHitCount() + "\n\r");
			for (int i = 0; i < hitCount; i++) {
				System.out.println("NO.: " + i);
				querySuccess = query(subject, inst);
				if (resultSet == null || resultSet.getRecord(i) == null) {
					querySuccess = query(subject, inst);
				} // end if
				String record = resultSet.getRecord(i).render();

				// if ((record.contains("HKP") && record.contains("040")) ||
				// (record.contains("pcc") && record.contains("042"))) {
				BufferedReader bufReader = new BufferedReader(new StringReader(record));
				String line = null;
				while ((line = bufReader.readLine()) != null) {
					line = line.trim();
					line = line.replaceAll("\\n\\r", "");

					if (line.matches("^022.*")) {
						isbn += parseMARCLineValue(line).trim() + "; ";
						isbn = isbn.replaceAll("\\$q.*", "");
						isbn = isbn.replaceAll("\\$c.*", "");
						isbn = isbn.replaceAll("\\$z", "");
						isbn = isbn.replaceAll("\\$1.*", "");
					} // end if
					if (line.matches("^020.*")) {
						isbn += parseMARCLineValue(line).trim() + "; ";
						isbn = isbn.replaceAll("\\$q.*", "");
						isbn = isbn.replaceAll("\\$c.*", "");
						isbn = isbn.replaceAll("\\$z", "");
						isbn = isbn.replaceAll("\\$1.*", "");
					} // end if
					if (line.matches("^245.*")) {
						title += parseMARCLineValue(line).trim();
						title = title.replaceAll("\\$h.*", "");
						title = title.replaceAll("\\$b", "");
						title = title.replaceAll("\\/.*\\$c.*", "");
						title = title.replaceAll("$c.*", "");
					} // end if
					if (line.matches("^880.*245.*")) {
						title880 += parseMARCLineValue(line).trim() + "; ";
						title880 = title880.replaceAll("\\$h.*", "");
						title880 = title880.replaceAll("\\$b", "");
						title880 = title880.replaceAll("\\/.*\\$c.*", "");
						title880 = title880.replaceAll("\\$c.*", "");

					} // end if
					if (line.matches("^100.*") || line.matches("^700.*") || line.matches("^710.*")) {
						author += parseMARCLineValue(line).trim() + "; ";
						author = author.replaceAll("\\$q.*", "");
						author = author.replaceAll("\\$d.*", "");
						author = author.replaceAll("\\$e", "");
						author = author.replaceAll("\\$b", "");
						author = author.replaceAll("\\$t.*", "");
						author = author.replaceAll("\\$c.*", "");
						author = author.replaceAll("\\$4.*", "");
					} // end if
					if (line.matches("^880.*100.*") || line.matches("^880.*700.*") || line.matches("^700.*")
							|| line.matches("^880.*710.*")) {
						author880 += parseMARCLineValue(line).trim() + "; ";
						author880 = author880.replaceAll("\\$q.*", "");
						author880 = author880.replaceAll("\\$d.*", "");
						author880 = author880.replaceAll("\\$e", "");
						author880 = author880.replaceAll("\\$b", "");
						author880 = author880.replaceAll("\\$t.*", "");
						author880 = author880.replaceAll("\\$c.*", "");
						author880 = author880.replaceAll("\\$4.*", "");
					} // end if
					if (line.matches("^26[04].*") || line.matches("^502.*")) {
						publisher = publisher.replaceAll("^502", "");
						publisher += parseMARCLineValue(line).trim() + "; ";
						try {
							pubyear = Integer.parseInt(line.replaceAll("^.*\\$c", "").replaceAll("[^\\d]", ""));
						} catch (Exception e) {
							pubyear = 0;
						}
						publisher = publisher.replaceAll(".*\\$b", "");
						publisher = publisher.replaceAll("\\$c", "");
					} // end if
					if (line.matches("^880.*26[04].*") || line.matches("^880.*502.*")) {
						publisher = publisher.replaceAll("^880.*502", "");
						publisher880 += parseMARCLineValue(line).trim() + "; ";
						publisher880 = publisher880.replaceAll(".*\\$b", "");
						publisher880 = publisher880.replaceAll("\\$c", "");
					} // end if
				}

				if (!title880.equals(""))
					title = title880;
				if (!publisher880.equals(""))
					publisher = publisher880;
				if (!author880.equals(""))
					author = author880;
				String reportLineAll = isbn + "\t" + title + "\t" + author + "\t" + publisher + "\n";
				System.out.println("\nTitle: " + title);
				System.out.println("Author: " + author);
				System.out.println("year:" + pubyear);
				if (pubyear >= 2000 && !author.equals("")) {
					String searchAuthor = handleText.trimSpecialChars(author);
					// searchAuthor = handleText.tidyString(searchAuthor);
					searchAuthor = searchAuthor.replaceAll(" .*$", "");
					searchAuthor = searchAuthor.replace(";", "");

					String searchTitle = title.replaceAll("[&|:]", "");
					searchTitle = searchTitle.replaceAll("^.*=", "");
					searchTitle = searchTitle.replaceAll("\"", "");
					searchTitle = searchTitle.replaceAll("!", "");

					String queryStr = "@attr 1=4 \"" + searchTitle + "\"";
					System.out.println("\nSearch title: " + searchTitle);
					System.out.println("Search author: " + searchAuthor);

					if (searchAuthor.equals(""))
						break;

					queryStr = queryStr.replaceAll("\\s$", "");
					System.out.println(queryStr);
					try {
						remoteQuery(queryStr);
					} catch (Exception e) {
						writerOutput.write(e.getMessage());
						e.printStackTrace();
					}
					for (int j = 0; j < resultSet.getHitCount(); j++) {
						try {
							record = resultSet.getRecord(j).render();
							if (record.toLowerCase().contains(subject.toLowerCase())
									&& record.toLowerCase().contains(searchAuthor.toLowerCase()) && pubyear >= 2000
									&& (record.contains("040") && record.contains("HKP"))) {
								// && (record.contains("040") && record.contains("HKP"))
								// || (record.contains("042") && record.contains("pcc"))

								bufReader = new BufferedReader(new StringReader(record));
								line = null;
								title = "";
								title880 = "";
								author = "";
								author880 = "";
								publisher = "";
								publisher880 = "";
								isbn = "";

								while ((line = bufReader.readLine()) != null) {
									line = line.trim();
									line = line.replaceAll("\\n\\r", "");

									if (line.matches("^650.*")) {
										subjectLines += parseMARCLineValue(line).trim() + " - ";
										subjectLines = subjectLines.replaceAll("$x", " - ");
										subjectLines = subjectLines.replaceAll("$z", " < ");

									}

									if (line.matches("^022.*")) {
										isbn += parseMARCLineValue(line).trim() + "; ";
										isbn = isbn.replaceAll("\\$q.*", "");
										isbn = isbn.replaceAll("\\$c.*", "");
										isbn = isbn.replaceAll("\\$z", "");
										isbn = isbn.replaceAll("\\$1.*", "");
									} // end if
									if (line.matches("^020.*")) {
										isbn += parseMARCLineValue(line).trim() + "\t ";
										isbn = isbn.replaceAll("\\$q.*", "");
										isbn = isbn.replaceAll("\\$c.*", "");
										isbn = isbn.replaceAll("\\$z", "");
										isbn = isbn.replaceAll("\\$1.*", "");
									} // end if
									if (line.matches("^245.*")) {
										title += parseMARCLineValue(line).trim() + "; ";
										title = title.replaceAll("\\$h.*:", ":");
										title = title.replaceAll("\\$h.*", "");
										title = title.replaceAll("\\$b", "");
										title = title.replaceAll("\\/.*\\$c.*", "");
										title = title.replaceAll("$c.*", "");
									} // end if
									if (line.matches("^880.*245.*")) {
										title880 += parseMARCLineValue(line).trim() + "; ";
										title880 = title880.replaceAll("\\$h.*", "");
										title880 = title880.replaceAll("\\$b", "");
										title880 = title880.replaceAll("\\/.*\\$c.*", "");
										title880 = title880.replaceAll("\\$c.*", "");

									} // end if
									if (line.matches("^100.*") || line.matches("^700.*") || line.matches("^710.*")) {
										author += parseMARCLineValue(line).trim() + "; ";
										author = author.replaceAll("\\$q.*", "");
										author = author.replaceAll("\\$d.*", "");
										author = author.replaceAll("\\$e", "");
										author = author.replaceAll("\\$b", "");
										author = author.replaceAll("\\$t.*", "");
										author = author.replaceAll("\\$c.*", "");
										author = author.replaceAll("\\$4.*", "");
									} // end if
									if (line.matches("^880.*100.*") || line.matches("^700.*")
											|| line.matches("^880.*700.*") || line.matches("^880.*710.*")) {
										author880 += parseMARCLineValue(line).trim() + "; ";
										author880 = author880.replaceAll("\\$q.*", "");
										author880 = author880.replaceAll("\\$d.*", "");
										author880 = author880.replaceAll("\\$e", "");
										author880 = author880.replaceAll("\\$b", "");
										author880 = author880.replaceAll("\\$t.*", "");
										author880 = author880.replaceAll("\\$c.*", "");
										author880 = author880.replaceAll("\\$4.*", "");
									} // end if
									if (line.matches("^260.*") || line.matches("^264.*") || line.matches("^502.*")) {
										publisher = publisher.replaceAll("^502", "");
										publisher += parseMARCLineValue(line).trim() + "; ";
										try {
											pubyear = Integer
													.parseInt(line.replaceAll("^.*\\$c", "").replaceAll("[^\\d]", ""));
										} catch (Exception e) {
											e.printStackTrace();
											pubyear = 0;
										}

										publisher = publisher.replaceAll(".*\\$b", "");
										publisher = publisher.replaceAll("\\$c", "");
										publisher = publisher.replaceAll("\\d", "");
									} // end if
									if (line.matches("^880.*260.*") || line.matches("^880.*264.*")
											|| line.matches("^880.*502.*")) {
										publisher = publisher.replaceAll("^880.*502", "");
										publisher880 += parseMARCLineValue(line).trim() + "; ";
										publisher880 = publisher880.replaceAll(".*\\$b", "");
										publisher880 = publisher880.replaceAll("\\$c", "");
										publisher880 = publisher880.replaceAll("\\d", "");
									} // end if
									if (!title880.equals(""))
										title = title880;
									if (!publisher880.equals(""))
										publisher = publisher880;
									if (!author880.equals(""))
										author = author880;
								}

								if (pubyear >= 2000 && author.toLowerCase().contains(searchAuthor.toLowerCase())
										&& subjectLines.toLowerCase().contains(subject.toLowerCase())) {
									String reportLine = title + "\t" + author + "\t" + publisher + "\t" + pubyear + "\t"
											+ subjectLines + "\t" + isbn + "\n";
									boolean found = false;
									for (int k = 0; k < resultList.size(); k++) {
										if (resultList.get(k).equals(reportLine)) {
											found = true;
											break;
										}
									}
									System.out.print("REPROTLINE: " + reportLine);
									if (resultList.size() == 0)
										resultList.add(reportLine);
									if (!found) {
										resultList.add(reportLine);
										System.out.print("REPROTLINE: " + reportLine);
									}
								}
							}
						} catch (Exception e2) {
							e2.printStackTrace();
						}

					} // end for
				} // end if
				title = "";
				title880 = "";
				author = "";
				author880 = "";
				publisher = "";
				publisher880 = "";
				isbn = "";
				subjectLines = "";
				// }
			}
			this.closeConnection();
			System.out.println("RESULTE LIST ");
			System.out.println(resultList.size());
			for (int k = 0; k < resultList.size(); k++) {
				writerOutput.write(resultList.get(k));
				writerOutput.write("SOMEHTING");
			}
			writerOutput.close();
			writerComFile.write("YES");
			writerComFile.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	} // Z3950QueryByNonISBN()

	public boolean query(String subject, String inst) {
		clearQuery();
		Config.init(inst);
		setQueryBase();
		return query();
	} // end if

	public boolean query() {
		try {
			String queryStr = "@at" + "tr 1=21 \"" + subject + "\" ";
			System.out.println(queryStr);

			return remoteQuery(queryStr);
		} catch (Exception e) {
			try {
				writerOutput.write(e.getMessage());
			} catch (Exception e2) {
			}
		}
		return false;
	} // end query()

	public boolean querySuccess() {
		return querySuccess;
	}
} // end class
