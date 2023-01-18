import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.apache.commons.validator.routines.UrlValidator;

import hk.edu.hkmu.lib.StringHandling;

public class TestString {
	public static void main(String[] args) {

		String str = "TOLOGY–CRYPTO2019SPRINGER";
		str = StringHandling.trimSpecialChars(str);
		System.out.println(str);
		
	    try {
	    	String[] wordsArray;
	        File myObj = new File("d:\\Z108_course_202301017.csv");
	        Scanner myReader = new Scanner(myObj);
	        while (myReader.hasNextLine()) {
	          String data = myReader.nextLine();
	          wordsArray = data.split("\t");
	          System.out.println(wordsArray[0]);
	          System.out.println(wordsArray[1]);
	          System.out.println(wordsArray[2]);
	          System.out.println(data);
	        }
	        myReader.close();
	      } catch (FileNotFoundException e) {
	        System.out.println("An error occurred.");
	        e.printStackTrace();
	      }

	}

}
