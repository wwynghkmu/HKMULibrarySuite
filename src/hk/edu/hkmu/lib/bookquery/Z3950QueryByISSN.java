package hk.edu.hkmu.lib.bookquery;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;

import hk.edu.hkmu.lib.BookItem;

/**
 * 
 * Z39.50 remote query by ISSN. An institute code must be specified the
 * connection details of which is defined in config.txt
 * 
 * @author Wai-yan NG
 * @author wwyng@ouhk.edu.hk
 * @version 1.0
 * @since Jan 29, 2019
 */
public class Z3950QueryByISSN extends Z3950Query {

	public Z3950QueryByISSN(String inst) {
		super(inst);
	} // end Z3950QueryByISSN()

	public Z3950QueryByISSN(String issn, String inst) {
		super(inst);
		queryBk = new BookItem(issn);
		queryBk.isbn.setIsbn(issn);
		query();
	} // end Z3950QueryByISSN()

	public Z3950QueryByISSN(String isbn, String inst, String vol) {
		super(inst);
		queryBk = new BookItem(isbn);
		queryBk.isbn.setIsbn(isbn);
		queryBk.setVolume(vol);
		query();
	} // end Z3950QueryByISSN()

	public Z3950QueryByISSN() {
		super();
	} // end Z3950QueryByISSN()

	public boolean query(String isbn, String inst) {
		clearQuery();
		Config.init(inst);
		setQueryBase();
		queryBk = new BookItem(isbn);
		queryBk.isbn.setIsbn(isbn);
		return query();
	} // end if

	public boolean query() {
		queryCount++;
		if (!strHandle.hasSomething(queryBk.isbn.getOriginalIsbn())) {
			queryStr = "";
			return false;
		} // end if

		try {
			queryStr = "@attr 1=8 @attr 2=3 " + queryBk.isbn.getOriginalIsbn();
			if (remoteQuery(queryStr)) {
				match = true;
				boolean isWrongRecord = false;

				String m = result;
				BufferedReader bufReader = new BufferedReader(new StringReader(result));
				String line = null;
				while ((line = bufReader.readLine()) != null) {

					line = line.trim();
					if (strHandle.hasSomething(line))
						m += line + "\r";

					if (line.matches("^022.*")) {
						line = line.replaceAll("-", "");
						if (line.contains(queryBk.isbn.getOriginalIsbn())) {
							isWrongRecord = false;
							break;
						}
					}

					if (line.matches("^7.*")) {
						line = line.replaceAll("-", "");
						if (line.contains(queryBk.isbn.getOriginalIsbn())) {
							isWrongRecord = true;
						}
					} // end if
				} // end while
				if (!isWrongRecord) {
					setBookInfo();
				}
				while (!checkAva(queryBk.parseVolume()) && nextRecord()) {
					copyNextRecToCurrentRec();
					bufReader = new BufferedReader(new StringReader(result));
					line = null;
					while ((line = bufReader.readLine()) != null) {

						line = line.trim();
						if (strHandle.hasSomething(line))
							m += line + "\r";

						if (line.matches("^022.*")) {
							line = line.replaceAll("-", "");
							if (line.contains(queryBk.isbn.getOriginalIsbn())) {
								isWrongRecord = false;
								break;
							} // end if
						} // end if

						if (line.matches("^7.*")) {
							line = line.replaceAll("-", "");
							if (line.contains(queryBk.isbn.getOriginalIsbn())) {
								isWrongRecord = true;
							}
						} // end if
					} // end while
					if (!isWrongRecord)
						setBookInfo();
				} // end while
				return true;
			} // end if
		} // end try
		catch (Exception e) {
			try {
				if (remoteQuery(queryStr)) {
					setBookInfo();
					match = true;
					checkAva(queryBk.parseVolume());
					return true;
				} // end if
			} // end try
			catch (Exception e2) {
				StringWriter errors = new StringWriter();
				e.printStackTrace(new PrintWriter(errors));
				String errStr = "Z3950Query:query()" + errors.toString();
				errMsg = errStr;
				System.out.println(errStr);
			} // end catch
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			String errStr = "Z3950Query:query()" + errors.toString();
			errMsg = errStr;
			System.out.println(errStr);
		} // end catch
		return false;
	} // end query()

} // end class Z3950QueryByISSN
