package hk.edu.hkmu.lib.cat;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import hk.edu.hkmu.lib.*;

/**
 * 
 * Complete various MARC tags conversion requirements which are defined in
 * config.txt.
 * 
 * @author Wai-yan NG
 * @author wwyng@ouhk.edu.hk
 * @version 1.0
 * @since Jan 29, 2019
 */
public class MarcConversion {
	private StringHandling strHandle = new StringHandling();
	private CJKStringHandling cjkStrHandle = new CJKStringHandling();
	private boolean conNRm880;
	private boolean remove0XXexcept008;
	private boolean rm9XX;
	private boolean rm856;
	private boolean con001;
	private boolean simpChiToTradChin;
	private boolean removeLeader;
	private boolean addEqualSign;
	private String pAddTagNo;

	MarcConversion(ArrayList<String> marcList, ArrayList<String> ilsList) throws Exception {
		clear();
		convert(marcList, ilsList);
	} // end MarcConvertion()

	MarcConversion() {
		clear();
	} // end MarcConversion()

	public void setAllConvFalse() {
		conNRm880 = false;
		remove0XXexcept008 = false;
		rm9XX = false;
		rm856 = false;
		con001 = false;
		simpChiToTradChin = false;
		removeLeader = false;
		addEqualSign = false;
	} // end setAllConvFalse()

	public void setConAndRemove880() {
		if (conNRm880) {
			conNRm880 = false;
		} else {
			conNRm880 = true;
		} // end if
	} // end setConAndRemove880()

	public void setConAndRemove880(boolean b) {
		conNRm880 = b;
	} // end setConAndRemove880()

	public void setAddEqualSign() {
		if (addEqualSign) {
			addEqualSign = false;
		} else {
			addEqualSign = true;
		} // end if
	} // end setAddEqualSign()

	public void setAddEqualSign(boolean b) {
		addEqualSign = b;
	} // end setAddEqualSign()

	public void setRemove856() {
		if (rm856) {
			rm856 = false;
		} else {
			rm856 = true;
		} // end if
	} // end setRemove856()

	public void setRemove856(boolean b) {
		rm856 = b;
	} // end setRemove856()

	public void setProgramInsertedTagNumber(String str) {
		if (strHandle.hasSomething(str) && str.matches("^9\\d\\d.*")) {
			pAddTagNo = str;
		} else {
			pAddTagNo = "997";
		} // end if
	} // end setRemove856()

	public void setConvert001() {
		if (con001) {
			con001 = false;
		} else {
			con001 = true;
		} // end if
	} // end setRemove856()

	public void setConvert001(boolean b) {
		con001 = b;
	} // end setRemove856()

	public void setRemove9XX() {
		if (rm9XX) {
			rm9XX = false;
		} else {
			rm9XX = true;
		} // end if
	} // end setRemove9XX()

	public void setRemove9XX(boolean b) {
		rm9XX = b;
	} // end setRemove9XX()

	public void setSimpChiToTradChin() {
		if (simpChiToTradChin) {
			simpChiToTradChin = false;
		} else {
			simpChiToTradChin = true;
		} // end if
	} // end setSimpChiToTradChin()

	public void setSimpChiToTradChin(boolean b) {
		simpChiToTradChin = b;
	} // end setSimpChiToTradChin()

	public void setRemoveLeader() {
		if (removeLeader) {
			removeLeader = false;
		} else {
			removeLeader = true;
		} // end if
	} // end setRemoveLeader()

	public void setRemoveLeader(boolean b) {
		removeLeader = b;
	} // end setRemoveLeader()

	public void setRemove0XXexcept008() {
		if (remove0XXexcept008) {
			remove0XXexcept008 = false;
		} else {
			remove0XXexcept008 = true;
		} // end if
	} // end setRemove0XXexcept008()

	public void setRemove0XXexcept008(boolean b) {
		remove0XXexcept008 = b;
	} // end setRemove0XXexcept008()

	public void clear() {
		addEqualSign = true;
		String value = Config.VALUES.get("conNRm880");
		if (strHandle.hasSomething(value) && value.equals("YES")) {
			conNRm880 = true;
		} else {
			conNRm880 = false;
		} // end if

		value = Config.VALUES.get("rm9XX");
		if (strHandle.hasSomething(value) && value.equals("YES")) {
			rm9XX = true;
		} else {
			rm9XX = false;
		} // end if

		value = Config.VALUES.get("rm856");
		if (strHandle.hasSomething(value) && value.equals("YES")) {
			rm856 = true;
		} else {
			rm856 = false;
		} // end if

		value = Config.VALUES.get("con001");
		if (strHandle.hasSomething(value) && value.equals("YES")) {
			con001 = true;
		} else {
			con001 = false;
		} // end if

		value = Config.VALUES.get("simpChiToTradChin");
		if (strHandle.hasSomething(value) && value.equals("YES")) {
			simpChiToTradChin = true;
		} else {
			simpChiToTradChin = false;
		} // end if

		value = Config.VALUES.get("removeLeader");
		if (strHandle.hasSomething(value) && value.equals("YES")) {
			removeLeader = true;
		} else {
			removeLeader = false;
		} // end if

		value = Config.VALUES.get("remove0XXexcept008");
		if (strHandle.hasSomething(value) && value.equals("YES")) {
			remove0XXexcept008 = true;
		} else {
			remove0XXexcept008 = false;
		} // end if

		pAddTagNo = "997";
	} // end clear()

	public boolean convert(ArrayList<String> marcList, ArrayList<String> ilsList) throws Exception {
		if (strHandle.hasSomething(marcList)) {
			for (int i = 0; i < marcList.size(); i++) {
				String record = marcList.get(i);
				if (strHandle.hasSomething(record)) {
					try {
						BufferedReader bufReader = new BufferedReader(new StringReader(record));
						String line = "";
						String conRecord = "";
						while ((line = bufReader.readLine()) != null) {
							line = line.trim();

							if (remove0XXexcept008)
								line = remove0XXexcept008(line);

							if (simpChiToTradChin)
								line = simpChiToTradChin(line);

							if (line.matches("^LDR.*"))
								line = "LDR  01054nam 2200301 i 4500";

							if (removeLeader)
								line = removeLeader(line);

							if (conNRm880)
								line = convertAndRemove880Line(line);

							if (rm9XX)
								line = remove9XXLine(line);

							if (rm856)
								line = remove856Line(line);

							if (con001 && ilsList != null) {
								line = remove001Line(line);
							} // end if

							if (addEqualSign && ilsList != null) {
								line = addEqualSign(line);
							} // end if

							line = stardarndConvertLine(line);

							if (strHandle.hasSomething(line)) {
								conRecord += line + "\r\n";
							} // end if

							

						} // end while

						if (con001 && ilsList != null)
							conRecord = add001Line(ilsList.get(i)) + "\r\n" + conRecord;

						marcList.set(i, conRecord);
					} // end try

					catch (Exception e) {
						throw e;
					}
				} // end if
			} // end for
			return true;
		} else {
			return false;
		} // end if
	} // end conv880To245()

	private String convertAndRemove880Line(String line) {
		if (line.matches("^880.*")) {
			line = line.replaceAll("^880.", "");
			String controlField = line.substring(0, 2);
			line = line.replaceAll("^.*\\$6", "");
			line = line.replaceAll("\\/\\$\\d", " ");

			String str = "";
			for (int i = 0; i < line.length(); i++) {
				if (i == 3) {
					str += " ";
				} else if (i == 4) {
					str += controlField.charAt(0);
				} else if (i == 5) {
					str += controlField.charAt(1);
				} else if (i != 3) {
					str += line.charAt(i);
				} // end if
			} // end for
			line = str;

		} // end if

		if (line.matches(".*\\$6880\\-\\d\\d.*")) {
			line = "";
		} // end if

		return line;

	} // end convertAndRemove880Line()

	private String remove9XXLine(String line) {
		if (line.matches("^9\\d\\d.*")) {
			return "";
		} else {
			return line;
		} // end if
	} // end remove9XXLine()

	private String remove856Line(String line) {
		if (line.matches("^856.*")) {
			return "";
		} else {
			return line;
		} // end if
	} // end remove856Line()

	private String remove001Line(String line) {
		if (line.matches("^001.*")) {
			return "";
		} else {
			return line;
		} // end if
	} // end remove9XXLine()

	private String add001Line(String ilsID) {
		ilsID = "001  " + ilsID;
		if (addEqualSign)
			ilsID = addEqualSign(ilsID);
		return ilsID;
	} // end add001Line()

	private String addEqualSign(String line) {
		if (strHandle.hasSomething(line))
			return "=" + line;
		return "";
	} // end add001Line()

	private String simpChiToTradChin(String line) {
		if (cjkStrHandle.isCJKString(line)) {
			line = cjkStrHandle.convertToTradChinese(line);
		} // end if
		return line;
	} // end simpChiToTradChin()

	private String removeLeader(String line) {
		if (line.contains("LDR")) {
			line = "";
		} // end if
		return line;
	} // end removeLeader()

	private String stardarndConvertLine(String line) {

		if (line.matches("^Z39.*"))
			line = line.replaceAll("^Z39", pAddTagNo);

		if (line.contains("(Length implementation at offset"))
			line = "";

		return line;
	} // end stardarndConvertLine()

	private String remove0XXexcept008(String line) {
		if (line.matches("^00\\d.*") && !line.matches("^008.*")) {
			line = "";
		} // end if
		return line;
	} // end remove0XXexcept008();
}
