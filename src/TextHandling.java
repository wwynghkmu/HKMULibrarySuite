import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Set;

import hk.edu.hkmu.lib.*;

public class TextHandling
{
	public static Set<String> chiDict = new HashSet<String>();
	public static void main(String[] args) {
		try{
		
		File file = new File(
				"d:/cedict.txt");
		File fileout = new File("d:/cedictout.txt");
		BufferedWriter writer = new BufferedWriter(new FileWriter(fileout));
		FileInputStream is = new FileInputStream(file);
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		String line = "";
		CJKStringHandling ch = new CJKStringHandling();
		StringHandling gh = new StringHandling();
		while((line = reader.readLine())!= null){
			line = line.trim();
			line = line.replaceAll("[a-zA-Z]|[0-9]|[()\\[\\]/,.;:#-*-áíéé?ãó]", "");
			line = line.replaceAll("  ", " ");
			String[] strs = line.split(" ");
			for(int i=0; i<strs.length; i++){
				if(strs[i].length()>1){
					strs[i] = gh.removeAccents(strs[i]);
					strs[i] = gh.normalizeString(strs[i]);
					chiDict.add(strs[i]);
					chiDict.add(ch.convertToSimpChinese(strs[i]));
				}
			}
			
		} //end while
		System.out.println("SSS:" + chiDict.size());
		for(String str : chiDict)
			writer.write(str + " ");
		writer.close();
		reader.close();
		} //end try
		catch(Exception e){System.out.println(e.toString());}
		
	}
}