
import hk.edu.hkmu.lib.bookquery.*;

public class TestPrimoSearchByNonISBN {
	public static void main(String[] args) {
		
		String inst = "HKSYU";
		String author = "村上春樹";
		String title = " 沒有色彩的多崎作和他的巡禮之年";
		String publisher = "";
		String year = "2013.";
		String edition = "";
		String vol = "";
				
		if(args.length != 0)
			inst = args[0];
		try{
		PrimoQueryByNonISBN q = new PrimoQueryByNonISBN(author, title, publisher, year, edition, vol, inst);
		
		if (q.match()) {
			System.out.println("-- MATCHED --");
			System.out.println("TITLE: " + q.bk.getTitle());
			System.out.println("ALTTITLE: " + q.bk.getAltTitles());
			System.out.println("CREATOR: " + q.bk.getCreator());
			System.out.println("Contributor: " + q.bk.getContributor());
			System.out.println("PUBLISHER: " + q.bk.getPublisher());
			System.out.println("Edition: " + q.bk.getEdition());
			System.out.println("TITLE: " + q.bk.getTitle());
			System.out.println("BOOKTYPE: " + q.bk.getBookType());
			System.out.println("Fulltext Urls: " + q.bk.getFulltextUrls());
			System.out.println("PUBLISH YEAR: " + q.bk.getPublishYear());
			System.out.println("Subject: " + q.bk.getSubject());
			System.out.println("ORIISBN " + q.bk.isbn.getOriginalIsbn());
			System.out.println("ISBN13: " + q.bk.isbn.getIsbn13());
			System.out.println("VOL: " + q.queryBk.getVolume());
			System.out.println("Primo Link: " + q.bk.getPrimoLink());
			System.out.println("Holding Info: " + q.bk.getHoldingText());
			System.out.println("BIB No: " + q.getBib_no());
			System.out.println("AVA: " + q.isAva() );
			System.out.println("Item count: " + q.getExt_itm_no() );
			System.out.println("AVA Item count: " + q.getAva_itm_no() );
			for (int i = 0; i < q.bk.getSubjects().size(); i++) {
				System.out.println("Subject " + i + ": "
						+ q.bk.getSubjects().get(i));
			}// endfor
			for (int i = 0; i < q.bk.getCreators().size(); i++) {
				System.out.println("Authors " + i + ": "
						+ q.bk.getCreators().get(i));
			}// endfor
			System.out.println("-- MATCHED --");
		} else {
			System.out.println("NOT MATCHED. ISBN:" + q.bk.isbn.getOriginalIsbn() + ".\n"
					+ q.getErrMsg());
			System.out.println("Primo X-service Base: "
					+ hk.edu.hkmu.lib.bookquery.Config.PRIMO_X_BASE);
			System.out.println("Author: " + q.queryBk.getCreator());
			System.out.println("Title: " + q.queryBk.getTitle());
			System.out.println("Publisher: " + q.queryBk.getPublisher());
			System.out.println("Year: " + q.queryBk.getPublishYear());
			System.out.println("Edition: " + q.queryBk.getEdition());
			System.out.println("VOL: " + q.queryBk.getVolume());
		} // end if
		
		} 
		catch(Exception e){e.printStackTrace();}
		
	} // end main
} // end class Test