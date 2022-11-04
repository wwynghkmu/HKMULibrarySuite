package hk.edu.hkmu.lib;

import java.io.BufferedReader;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.ArrayList;

/**
 * 
 * Conversion between MARC machine readable format and human readable format
 * 
 * @author Wai-yan NG
 * @author wwyng@ouhk.edu.hk
 * @version 1.0
 * @since Jan 29, 2019
 */
public class MARC {

	private byte[] marcRaw;
	private String marcRawStr;
	private byte[] marcTagRaw;
	private String marcTag;
	private StringHandling strHandle;

	public MARC() {
		clear();
	} // end MARC()

	public MARC(byte[] marc) {
		clear();
		setMarcRaw(marc);

	} // end MARC()

	public MARC(String str) {
		clear();
		setMarcTag(str);
	} // end MARC()

	private void clear() {
		marcRaw = null;
		marcTag = null;
		strHandle = new StringHandling();
	} // end clear()

	// Accept tagged MARC and convert it into raw MARC
	public void setMarcTag(String str) {
		if (str == null) {
			marcTag = null;
		} else {
			marcTag = str;
			makeMarc(str);
		} // end if
	} // end setMarcTag()

	// Accept raw MARC in byte[] and convert it into tagged MARC
	public void setMarcRaw(byte[] marc) {
		clear();
		if (isWithinXML(marc)) {
			marcRaw = extractMarcRawfromXML(marc);
		} else {
			marcRaw = marc;
		} // end if
		breakMarc(marcRaw);
	} // end setMarcMar

	// Extract MARC raw record within XML tags; assuming the raw record must
	// within <bibliographicRecord> </bibliographicRecord>
	private byte[] extractMarcRawfromXML(byte[] marc) {

		byte[] resultMarc = new byte[marc.length];
		for (int i = 0; i < resultMarc.length; i++) {
			resultMarc[i] = 0x00;
		} // end for

		byte[] startTag = "<bibliographicRecord>".getBytes();
		byte[] endTag = "</bibliographicRecord>".getBytes();
		int startIndex = 0;
		int endIndex = 0;

		int startTagCount = 0;
		int endTagCount = 0;

		for (int i = 0; i < marc.length; i++) {
			if (marc[i] == startTag[0]) {
				startTagCount += 1;
				for (int j = 1, k = i + j; j < startTag.length; j++, k++) {
					if (marc[k] == startTag[j]) {
						startTagCount += 1;
					} else {
						break;
					} // end if
				} // end for
				if (startTag.length == startTagCount) {
					if (marc[i + startTag.length] == 0x0a)
						i += 1;
					startIndex = i;
					break;
				} // end if
				startTagCount = 0;
			} // end if
		} // end for

		for (int i = 0, j = startIndex + startTag.length; j < marc.length; i++, j++) {
			resultMarc[i] = marc[j];
		} // end for

		for (int i = 0; i < resultMarc.length; i++) {
			if (resultMarc[i] == endTag[0]) {
				endTagCount += 1;
				for (int j = 1, k = i + j; j < endTag.length; j++, k++) {

					if (resultMarc[k] == endTag[j]) {
						endTagCount += 1;
					} else {
						break;
					} // end if
				} // end for
				if (endTag.length == endTagCount) {
					endIndex = i;
					break;
				} // end if
				endTagCount = 0;
			} // end if
		} // end for

		for (int i = endIndex; i < resultMarc.length; i++) {
			resultMarc[i] = 0x20;
		} // end for

		marc = new byte[endIndex];

		for (int i = 0; i < marc.length; i++) {
			marc[i] = resultMarc[i];
		} // end for

		return marc;
	} // end exTractMarcRaw()

	// Test if a raw MARC byte[] is within XML tags; use <opacRecord> as an
	// indicator.
	private boolean isWithinXML(byte[] marc) {

		String xmlFeature = "<opacRecord>";
		String marcStr = new String(marc);

		if (marcStr.contains(xmlFeature)) {
			return true;
		} // end if
		return false;
	} // end isWithinXML()

	public String getMarcTag() {
		return marcTag;
	} // end getMarcTag()

	public byte[] getMarcTagRaw() {
		return marcTagRaw;
	} // end getMarcTagRaw()

	// Accept a string (assumed in unicode) and convert it into machine readable
	// format.
	public boolean makeMarc(String str) {
		try {
			if (!strHandle.hasSomething(str)) {
				return false;
			} // end if

			ArrayList<String> tags = new ArrayList<String>();
			ArrayList<String> values = new ArrayList<String>();

			ArrayList<String> valuesLength = new ArrayList<String>();
			ArrayList<String> startIndexes = new ArrayList<String>();

			BufferedReader bufReader = new BufferedReader(new StringReader(str));
			String line = "";
			String tag = "";
			String value = "";
			int startIndex = 0;

			// The most complicated part, read MARC tags, calculate for MARC
			// directory, and break tags and contents for further processing.
			while ((line = bufReader.readLine()) != null) {
				if (line.contains("LDR")) {
					continue;
				} // end if
				if (line.contains("001") || line.contains("008")) {
					line = line.replaceAll("  ", " ");
				} // end if
				String temp = line;
				temp = temp.trim();
				if (strHandle.hasSomething(temp)) {
					tag = line.substring(0, 3);
					tag = tag.trim();
					value = line.substring(3, line.length());
					
					
					byte[] vb = value.getBytes();

					// Remove delimiters from MARC tags, convert subfield symbol
					// (0x1f), and add content delimiter (0x1e)
					for (int i = 0; i < vb.length; i++) {
						if (vb[i] == 0x1f || vb[i] == 0x1e)
							vb[i] = 0x5f;
					} // end for
					vb[0] = 0x5f;
					if (!tag.contains("001") && !tag.contains("LDR") && !tag.contains("008") && !tag.contains("006")
							&& !tag.contains("007") && !tag.contains("003"))
						vb[3] = 0x5f;
					value = new String(vb);
					value = value.replaceAll("_", "");
					value = value.replace('$', (char) 0x1f);
					value = value + (char) 0x1e;

					tags.add(tag);
					values.add(value);
					

					/*
					 * if(tag.equals("020") || tag.equals("505") || tag.equals("264"))
					 * System.out.println(tag + ":::" + value);
					 */

					// Get the index fields (4 digits for length; and 5 digits
					// for the start of content.
					String tmpStr = Integer.toString(value.getBytes().length);
					for (int i = tmpStr.length(); i < 4; i++) {
						tmpStr = "0" + tmpStr;
					} // end for
					valuesLength.add(tmpStr);
					tmpStr = Integer.toString(startIndex);
					for (int i = tmpStr.length(); i < 5; i++) {
						tmpStr = "0" + tmpStr;
					} // end for
					startIndexes.add(tmpStr);
					startIndex += value.getBytes().length;
				} // end if
			} // end while

			String leader = "";

			// Prepare leader.
			String recordLen = Integer.toString(str.getBytes().length);
			for (int i = recordLen.length(); i < 5; i++) {
				recordLen = "0" + recordLen;
			} // end for
			leader = recordLen + "nam 2200301 i 4500 ";

			// Prepare the MARC directory and contents.
			String dir = leader;
			for (int i = 0; i < tags.size(); i++) {
				dir += tags.get(i) + valuesLength.get(i) + startIndexes.get(i);
			} // end for

			// Add delimiter (0x1e) which marks a staring of a record; divide
			// directory section from content session
			dir = dir + (char) 0x1e;

			String content = "";

			for (String v : values) {
				content += v;
			} // end for

			// A raw MARC must end with '0x1d'
			marcRawStr = dir + content + (char) 0x1d;
			

		} // end try

		catch (Exception e) {
			System.out.println("MARC::makeMarc()::");
			e.printStackTrace();
		} // end catch

		return false;
	} // end makeMarc()

	public byte[] getMarcRaw() {
		return marcRaw;
	} // end getmarcRaw()

	// Accept raw Marc
	public boolean breakMarc(byte[] marc) {

		try {
			if (marc == null) {
				return false;
			} // end if

			ArrayList<byte[]> values = new ArrayList<byte[]>();
			ArrayList<byte[]> tags = new ArrayList<byte[]>();
			ArrayList<byte[]> valuesIndex = new ArrayList<byte[]>();
			int lastDelmiter = 0;
			int firstDelmiter = 0;

			byte[] bs = null;
			byte[] bs2 = null;
			
			for (int i = 0; i < marc.length; i++) {
				if (marc[i] == 0x1e) {
					if (lastDelmiter == 0) {
						firstDelmiter = i;
					} // end if
					lastDelmiter = i;
				} // end if
			} // end for

			bs = new byte[25];

			//Prepare for LDR tag line
			for (int i = 1, j = 0; i < 25; i++, j++) {
				bs[i] = marc[j];
			} // end for
			bs[0] = 0x20;
			tags.add("LDR".getBytes());
			values.add(bs);

			bs = new byte[3];
			bs2 = new byte[9];

			for (int i = 24; i < firstDelmiter + 1; i++) {
				if (i % 12 == 0 && i != 24 && i != firstDelmiter) {
					for (int j = 0; j < 3; j++) {
						bs[j] = marc[(i - 12) + j];
					} // end for

					tags.add(bs);

					bs = new byte[3];

					for (int j = 0; j < 9; j++) {
						bs2[j] = marc[(i - 9 + j)];
					} // end for
					valuesIndex.add(bs2);
					bs2 = new byte[9];
				} // end if

				if (i == firstDelmiter) {
					for (int j = 0; j < 3; j++) {
						bs[j] = marc[(i - 12 + j)];
					} // end for

					tags.add(bs);
					
					for (int j = 0; j < 9; j++) {
						bs2[j] = marc[(i - 9 + j)];
					} // end for
					valuesIndex.add(bs2);
				} // end if

			} // end for

			int i = 0;

			for (byte[] b : valuesIndex) {

				String index = new String(b);
				String valueLength = index.substring(0, 4);
				String startIndex = index.substring(4, 9);
				int valueLengthInt = Integer.parseInt(valueLength);
				int startInt = Integer.parseInt(startIndex);
				bs = new byte[valueLengthInt + 1];

				for (int j = 0; j < bs.length; j++) {
					if (firstDelmiter + startInt + j < marc.length) {

						bs[j] = marc[firstDelmiter + startInt + j];
						if (bs[j] == 0x1f)
							bs[j] = 0x24;

					} // end if
				} // end for

				bs[0] = 0x20;

				String tag = new String(tags.get(i + 1));
				

				if (!tag.equals("LDR") && !tag.equals("008") && !tag.equals("006") && !tag.equals("007")
						&& !tag.equals("003") && !tag.equals("001")) {

					for (int j = bs.length - 1; j > 2; j--) {
						bs[j] = bs[j - 1];
					} // end for

					if (bs.length > 3)
						bs[3] = 0x20;

				} else {

					for (int j = bs.length - 1; j > 0; j--) {
						bs[j] = bs[j - 1];
					} // end for

					bs[1] = 0x20;

				} // end if
				
				//Reposition the spaces (two spaces) after tag and the indicator positions
				//Original: XXX XX [subfield]
				//Changed: XXX  XX[subfield]
				String tempStr = new String(bs);
				tempStr = "  " + tempStr.substring(1,3) + tempStr.substring(4);
				bs = tempStr.getBytes();
				
				values.add(bs);
				
				
				
				i++;
			} // end for

			marcTagRaw = new byte[marc.length];
			for (i = 0; i < marcTagRaw.length; i++) {
				marcTagRaw[i] = 0x00;
			} // end for

			i = 0;
			int m = 0;
			for (byte[] tag : tags) {
				byte[] value = values.get(i);
				
				for (int k = 0; k < tag.length; k++) {
					if (m < marcTagRaw.length) {
						marcTagRaw[m] = tag[k];
						m++;
					} // end if
				} // end for

				for (int k = 0; k < value.length; k++) {
					if (m < marcTagRaw.length) {
						marcTagRaw[m] = value[k];
						m++;
					} // end if
				} // end for
				if (m < marcTagRaw.length) {
					marcTagRaw[m] = 0xA;
					m += 1;
					i += 1;
				} // end if
			} // end for

			String result = "";

			boolean cjkCon = false;

			if (CJKStringHandling.isBig5(marcTagRaw)) {
				result = new String(marcTagRaw, Charset.forName("Big5_HKSCS"));
				result = new String(CJKStringHandling.removeControl(result.getBytes()));
				result = result.replaceAll("，", ",");
				marcTagRaw = result.getBytes();
				cjkCon = true;
			}
			/*
			 * if (CJKStringHandling.isEACC(marcTagRaw)) { result = new
			 * String(CJKStringHandling.EACCtoUnicode(marcTagRaw)); result =
			 * result.replace("!ON(B", ""); result = result.replace("'Q[(B", ""); result =
			 * result.replace("!Na(B", ""); result = result.replace("!:g(B", ""); cjkCon =
			 * true; } else if (CJKStringHandling.isCJKString(new String(marcTagRaw))) {
			 * result = new String(marcTagRaw, Charset.forName("UTF-8")); cjkCon = true; }
			 * // end if
			 */

			if (!cjkCon)
				result = new String(marcTagRaw, Charset.forName("UTF-8"));

			result = result.replaceAll("Ã", "©");
			
			setMarcTag(result);

		} // end try
		catch (Exception e) {
			System.out.println("MARC::breakMarc():");
			e.printStackTrace();
			return false;
		}
		return true;
	} // end breakMarc()

	public String getMarcRawStr() {
		return marcRawStr;
	} // end getMarcRawStr()

} // end class
