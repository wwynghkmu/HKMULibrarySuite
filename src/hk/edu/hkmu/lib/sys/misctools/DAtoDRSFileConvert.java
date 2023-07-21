package hk.edu.hkmu.lib.sys.misctools;

import java.io.*;
import java.util.*;
import org.apache.commons.io.FileUtils;

//This class is for organize exported files from LIB's DA to DRS importable folder structure in the form [id]\[filename]
// By William NG 20230413

public class DAtoDRSFileConvert {

	public static void main(String args[]) throws FileNotFoundException, IOException {
		List<List<String>> records = new ArrayList<>();
		try (BufferedReader br = new BufferedReader(new FileReader("d:\\DAtoDRS\\data.csv"))) {
			String line;
			while ((line = br.readLine()) != null) {
				String[] values = line.split(",");
				records.add(Arrays.asList(values));
			}
		}
		
		for(int i=1; i<records.size(); i++) {
			String folder = records.get(i).get(0);
			String filename = records.get(i).get(160);
			System.out.println("Folder: " + folder + " Filename: " + filename);
			new File("d:\\DAtoDRS\\output\\" + folder).mkdirs();
			File source = new File("d:\\DAtoDRS\\" + filename);
			File dest = new File("d:\\DAtoDRS\\output\\" + folder + "\\" + filename);
			FileUtils.copyFile(source, dest);
		}
		File source = new File("d:\\DAtoDRS\\data.csv");
		File dest = new File("d:\\DAtoDRS\\output\\data.csv");
		FileUtils.copyFile(source, dest);

	}

}
