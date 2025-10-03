import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

class DBManager {
	static boolean storeNewProgramingLanguage(String lang) throws IOException {
		String[] strings = getProgrammingLanguages();
		for (String s: strings) {
			if(lang.equals(s)) {
				return false;
			}
		}
		File f = new File(System.getProperty("user.home")+"/RateMyStudent/ProgrammingLangs/");
		f.mkdirs();
		int n = 1;
		do {
			f = new File(System.getProperty("user.home")+"/RateMyStudent/ProgrammingLangs/"+n+".csv");
			n++;
		} while (f.exists());
		f.createNewFile();
		FileWriter out = new FileWriter(f);
		out.append(stringToCSV(lang));
		out.flush();
		out.close();
		return true;
	}
	
	static String[] getProgrammingLanguages() throws IOException {
		File f = new File(System.getProperty("user.home")+"/RateMyStudent/ProgrammingLangs/");
		f.mkdirs();
		File[] languages = f.listFiles();
		String[] stringLanguages = new String[languages.length];
		for (int i = 0; i < languages.length; i++) {
			BufferedReader br = new BufferedReader(new FileReader(languages[i]));
			stringLanguages[i]=csvToString(br.readLine());
			br.close();
		}
		return stringLanguages;
	}
	
	static boolean deleteProgrammingLanguage(String s) throws IOException {
		File f = new File(System.getProperty("user.home")+"/RateMyStudent/ProgrammingLangs/");
		f.mkdirs();
		File[] languages = f.listFiles();
		for (int i = 0; i < languages.length; i++) {
			BufferedReader br = new BufferedReader(new FileReader(languages[i]));
			if(s.equals(csvToString(br.readLine()))) {
				br.close();
				languages[i].delete();
				return true;
			}
			br.close();
		}
		return false;
	}
	
	static boolean hasProgrammingLanguage(String s) throws IOException {
		File f = new File(System.getProperty("user.home")+"/RateMyStudent/ProgrammingLangs/");
		f.mkdirs();
		File[] languages = f.listFiles();
		for (int i = 0; i < languages.length; i++) {
			BufferedReader br = new BufferedReader(new FileReader(languages[i]));
			if(s.equals(csvToString(br.readLine()))) {
				br.close();
				return true;
			}
			br.close();
		}
		return false;
	}
	
	private static String csvToString(String s) {
		StringBuilder sb = new StringBuilder("");
		boolean isInQuotes = false;
		for (int i = 0; i < s.length(); i++) {
			if (!isInQuotes && s.charAt(i) == ',') {
				sb.delete(0, sb.length());
			} else if (isInQuotes && s.charAt(i) == 34) {
				if (i != s.length() - 1 && s.charAt(i + 1) == 34) {
					i++;
					sb.append('"');
				} else {
					isInQuotes = false;
				}
			} else if (s.charAt(i) == 34) {
				isInQuotes = true;
			} else {
				sb.append(s.charAt(i));
			}
		}
		return sb.toString();
	}
	
	private static String stringToCSV(String s) {
		StringBuilder sb = new StringBuilder();
		boolean needsQuotes = false;
		for (int i = 0; i < s.length(); i++) {
			if (!needsQuotes && (s.charAt(i) == '"' || s.charAt(i) == ',')) {
				needsQuotes = true;
				sb.insert(0, '"');
			}
			if (s.charAt(i) == '"') {
				sb.append('"');
				sb.append('"');
			} else {
				sb.append(s.charAt(i));
			}
		}
		if (needsQuotes) {
			sb.append('"');
		}
		return sb.toString();
	}
}
