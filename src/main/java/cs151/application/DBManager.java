import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

class DBManager {
	private static final File root = new File(System.getProperty("user.home")+"/RateMyStudent/");
	
	/**
	 * Returns the root file of the database.
	 * @return The root file your database for the app is stored in.
	 */
	static File getRoot() {
		return root;
	}
	
	/**
	 * Stores a new Programming Language in the database.
	 * You store the programming language in a csv file.
	 * @param lang The name of the programming language.
	 * @return Returns true if the language was stored successfully. Returns false when the language is not stored(duplicate or the file is not found).
	 */
	static boolean storeNewProgramingLanguage(String lang) {
		try {
			String[] strings = getProgrammingLanguages();
			if(strings==null) {return false;}
			for (String s: strings) {
				if(lang.equals(s)) {
					return false;
				}
			}
			File f = new File(root.getAbsolutePath()+"/ProgrammingLangs/");
			f.mkdirs();
			int n = 1;
			do {
				f = new File(root.getAbsolutePath()+"/ProgrammingLangs/"+n+".csv");
				n++;
			} while (f.exists());
			f.createNewFile();
			FileWriter out = new FileWriter(f);
			out.append(stringToCSV(lang));
			out.flush();
			out.close();
			return true;
		} catch(IOException e) {
			return false;
		}
	}
	
	/**
	 * Gets a list of the stored Programming Languages.
	 * @return Returns the list of stored Programming Languages. Returns null if the file is not found.
	 */
	static String[] getProgrammingLanguages() {
		try {
			File f = new File(root.getAbsolutePath()+"/ProgrammingLangs/");
			f.mkdirs();
			File[] languages = f.listFiles();
			String[] stringLanguages = new String[languages.length];
			for (int i = 0; i < languages.length; i++) {
				BufferedReader br = new BufferedReader(new FileReader(languages[i]));
				stringLanguages[i]=csvToString(br.readLine());
				br.close();
			}
			return stringLanguages;
		} catch(IOException e) {
			return null;
		}
	}
	
	/**
	 * Deletes a Programming Language from your database.
	 * @param lang The name of the Programming Language.
	 * @return Returns true if programming language was successfully deleted. Returns false if the file is not found.
	 */
	static boolean deleteProgrammingLanguage(String lang) {
		try {
			File f = new File(root.getAbsolutePath()+"/ProgrammingLangs/");
			f.mkdirs();
			File[] languages = f.listFiles();
			for (int i = 0; i < languages.length; i++) {
				BufferedReader br = new BufferedReader(new FileReader(languages[i]));
				if(lang.equals(csvToString(br.readLine()))) {
					br.close();
					languages[i].delete();
					return true;
				}
				br.close();
			}
			return false;
		} catch(IOException e) {
			return false;
		}
	}
	
	/**
	 * Checks the database to see if a programming language is stored there(case sensitive).
	 * @param lang The name of the programming language you are checking for.
	 * @return Returns true if the programming language was found or false otherwise.
	 */
	static boolean hasProgrammingLanguage(String lang) {
		try {
			File f = new File(root.getAbsolutePath()+"/ProgrammingLangs/");
			f.mkdirs();
			File[] languages = f.listFiles();
			for (int i = 0; i < languages.length; i++) {
				BufferedReader br = new BufferedReader(new FileReader(languages[i]));
				if(lang.equals(csvToString(br.readLine()))) {
					br.close();
					return true;
				}
				br.close();
			}
			return false;
		} catch(IOException e) {
			return false;
		}
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
