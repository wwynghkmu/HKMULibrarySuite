package hk.edu.hkmu.lib.bookquery;

import java.io.PrintWriter;
import java.io.StringWriter;

import hk.edu.hkmu.lib.BookItem;

/**
 * 
 * Z39.50 remote query by ISBN. An institute code must be specified the
 * connection details of which is defined in config.txt
 * 
 * @author Wai-yan NG
 * @author wwyng@ouhk.edu.hk
 * @version 1.0
 * @since Jan 29, 2019
 */
public class Z3950QueryByISBN extends Z3950Query {

	public Z3950QueryByISBN(String inst) {
		super(inst);
	} // end Z3950QueryByISBN()

	public Z3950QueryByISBN(String isbn, String inst) {
		super(inst);
		queryBk = new BookItem(isbn);
		queryBk.isbn.setIsbn(isbn);
		query();
	} // end Z3950QueryByISBN()

	public Z3950QueryByISBN(String isbn, String inst, String vol) {
		super(inst);
		queryBk = new BookItem(isbn);
		queryBk.isbn.setIsbn(isbn);
		queryBk.setVolume(vol);
		query();
	} // end Z3950QueryByISBN()

	public Z3950QueryByISBN() {
		super();
	} // end Z3950QueryByISBN()

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
			queryStr = "@attr 1=7 @attr 2=3 " + queryBk.isbn.getOriginalIsbn();
			if (remoteQuery(queryStr)) {
				System.out.println(queryStr);
				match = true;
				setBookInfo();

				while (!checkAva(queryBk.parseVolume()) && nextRecord()) {
					copyNextRecToCurrentRec();
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

} // end class Z3950QueryByISBN
