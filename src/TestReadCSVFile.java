import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.apache.commons.validator.routines.UrlValidator;

import hk.edu.hkmu.lib.StringHandling;

public class TestReadCSVFile {
	public static void main(String[] args) {

		try {
			File myObj = new File("d:\\852HKMU_INST_001_BIB_IDs.csv");
			Scanner myReader = new Scanner(myObj);
			while (myReader.hasNextLine()) {
				String data = myReader.nextLine();
				System.out.println(data);
			}
			myReader.close();
		} catch (FileNotFoundException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}

	}

}
