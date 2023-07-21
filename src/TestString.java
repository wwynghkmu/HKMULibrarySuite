import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Arrays;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.apache.commons.validator.routines.UrlValidator;

import hk.edu.hkmu.lib.CJKStringHandling;
import hk.edu.hkmu.lib.StringHandling;

public class TestString {
	public static void main(String[] args) {

		File file = new File("d:\\excludeList.txt");

		try {
			try (BufferedReader br = new BufferedReader(new FileReader(file))) {
				String line;
				while ((line = br.readLine()) != null) {
					line = line.trim();
					line = line.replaceAll("\s", "");
					System.out.println(line);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("IS CJK: " + (CJKStringHandling.isCJKString("The Brothers Karamazov = 卡拉馬助夫兄弟們")));

	}

}
