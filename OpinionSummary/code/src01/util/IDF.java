package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import opsum.Sentence;

public class IDF {
	public static Map<String, Double> idf = null;
	public static Map<String, Double> idfCnt = null;

	public static void recompute(Sentence[] sentList) {

		idf = new HashMap<String, Double>();
		idfCnt = new HashMap<String, Double>();

		for (int i = 0; i < sentList.length; i++) {
			for (String s : sentList[i].wordMap.keySet()) {
				Double cnt = 0.0;
				if (idfCnt.containsKey(s))
					cnt = idfCnt.get(s);
				cnt += 1.0;
				idfCnt.put(s, cnt);
			}
		}

		int NDocs = sentList.length;
		for (String s : idfCnt.keySet()) {
			Double cnt = idfCnt.get(s);
			cnt = Math.log(NDocs / cnt);
			idf.put(s, cnt);
		}

	}

	public static Double getIDF(String s) {
		Double res = 0.0;
		if (idf.containsKey(s) == false) {
			System.err.println(s);
			for (String t : idf.keySet()) {
				System.err.println("IDF\t" + t + "->" + idf.get(t));
			}
		}
		res = idf.get(s);
		return res;
	}

	public static Double getIDFCnt(String s) {
		Double res = 0.0;
		if (idfCnt.containsKey(s) == false) {
			System.err.println(s);
			for (String t : idfCnt.keySet()) {
				System.err.println("IDF\t" + t + "->" + idf.get(t));
			}
		}
		res = idfCnt.get(s);
		return res;
	}

	public static File[] FindTextFiles(String dirName) {
		File dir = new File(dirName);

		return dir.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String filename) {
				return filename.endsWith(".txt");
			}
		});

	}

	public static boolean loadSerial() {
		try {
			FileInputStream fileIn = new FileInputStream(
					Constants.CONFIG_BASE_DIR + "idf.ser");
			ObjectInputStream in = new ObjectInputStream(fileIn);
			idf = (Map<String, Double>) in.readObject();
			idfCnt = (Map<String, Double>) in.readObject();
			in.close();
			fileIn.close();
		} catch (IOException i) {
			i.printStackTrace();
			return false;
		} catch (ClassNotFoundException c) {
			System.out.println("Employee class not found");
			c.printStackTrace();
			return false;
		}
		return true;
	}

	public static void saveSerial() {
		try {
			FileOutputStream fileOut = new FileOutputStream(
					Constants.CONFIG_BASE_DIR + "idf.ser");
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(idf);
			out.writeObject(idfCnt);
			out.close();
			fileOut.close();
		} catch (IOException i) {
			i.printStackTrace();
		}

	}

	public static void loadIDF(String dir) {
		idf = new HashMap<String, Double>();
		idfCnt = new HashMap<String, Double>();
		
		if(loadSerial())
			return;
		
		int dCnt = 0;
		for (File d : FindTextFiles(dir)) {
			try {
				System.err.println(d.getAbsolutePath());
				updateIDF(d);
			} catch (Exception e) {
				e.printStackTrace();
			}
			dCnt++;
		}

		for (String s : idfCnt.keySet()) {
			double cnt = idfCnt.get(s);
			idf.put(s, Math.log(dCnt / cnt));
		}
		
		saveSerial();

	}

	public static void updateIDF(File file) throws FileNotFoundException,
			IOException {
		updateIDF(file.getAbsolutePath());
	}

	public static void updateIDF(String filename) throws FileNotFoundException,
			IOException {
		String content = FileW.getFileContents(filename);
		Set<String> set = new HashSet<String>();
		for (String word : Tokenize.run(content)) {
			set.add(word);
		}
		for (String s : set) {
			Double cnt = 0.0;
			if (idfCnt.containsKey(s))
				cnt = idfCnt.get(s);
			cnt += 1.0;
			idfCnt.put(s, cnt);
		}
	}

}
