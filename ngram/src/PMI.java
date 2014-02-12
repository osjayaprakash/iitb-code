import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class PMI {
	// 12,32,635
	// 5,15,73,873
	public final static int noBigrams = 51573873;
	public final static int noWords = 1232635;
	Set<String> setUnigram = new HashSet<String>();
	Set<String> setBigram = new HashSet<String>();
	StringIntBijection sibBigram = null;
	StringIntBijection sibUnigram = null;
	int totalNoLines = 0;
	Double pAB[] = null;
	Double pA[] = null;
	Double pB[] = null;

	public PMI() {
		pAB = new Double[noBigrams];
		pA = new Double[noWords];
		pB = new Double[noWords];
	}

	public void updateLinePass1(String line) {
		String[] arr = line.split("[ \t]+");
		if (arr.length != 3) {
			System.err.println(line);
			return;
		}
		setUnigram.add(arr[0]);
		setUnigram.add(arr[1]);
	}
	
	public void updateLinePass2(String line) {
		String[] arr = line.split("[ \t]+");
		if (arr.length != 3) {
			System.err.println(line);
			return;
		}
		int iA = sibUnigram.getInt(arr[0]);
		int iB = sibUnigram.getInt(arr[1]);
		setBigram.add(""+iA+"#"+iB);
	}

	public void updateLinePass3(String line) {
		String[] arr = line.split("[ \t]+");
		if (arr.length != 3) {
			System.err.println(line);
			return;
		}
		Double val = Double.parseDouble(arr[3]);
		int iA = sibUnigram.getInt(arr[0]);
		int iB = sibUnigram.getInt(arr[1]);
		int iAB = sibUnigram.getInt(""+ iA + "#" + iB);
		pA[iA] += val;
		pB[iB] += val;
		pAB[iAB] += val;
		// System.exit(-1);
	}

	public void readDocPass1(String fileName) {
		String text = "";
		int read, N = 1024 * 1024 * 50;
		char[] buffer = new char[N];

		try {
			FileReader fr = new FileReader(new File(fileName));
			BufferedReader br = new BufferedReader(fr);

			while (true) {
				read = br.read(buffer, 0, N);
				text += new String(buffer, 0, read) + "\n";
				String[] lines = text.split("[\r\n]");
				int noLines = lines.length;
				for (int i = 0; i < noLines - 1; i++) {
					updateLinePass1(lines[i]);
				}
				text = lines[noLines - 1];
				totalNoLines += (noLines - 1);
				System.err.println(totalNoLines);
				if (read < N) {
					break;
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return;
	}

	public void readDocPass2(String fileName) {
		String text = "";
		int read, N = 1024 * 1024 * 50;
		char[] buffer = new char[N];

		try {
			FileReader fr = new FileReader(new File(fileName));
			BufferedReader br = new BufferedReader(fr);

			while (true) {
				read = br.read(buffer, 0, N);
				text += new String(buffer, 0, read) + "\n";
				String[] lines = text.split("[\r\n]");
				int noLines = lines.length;
				for (int i = 0; i < noLines - 1; i++) {
					updateLinePass2(lines[i]);
				}
				text = lines[noLines - 1];
				totalNoLines += (noLines - 1);
				System.err.println(totalNoLines);
				if (read < N) {
					break;
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return;
	}

	public void readDocPass3(String fileName) {
		String text = "";
		int read, N = 1024 * 1024 * 50;
		char[] buffer = new char[N];

		try {
			FileReader fr = new FileReader(new File(fileName));
			BufferedReader br = new BufferedReader(fr);

			while (true) {
				read = br.read(buffer, 0, N);
				text += new String(buffer, 0, read) + "\n";
				String[] lines = text.split("[\r\n]");
				int noLines = lines.length;
				for (int i = 0; i < noLines - 1; i++) {
					updateLinePass3(lines[i]);
				}
				text = lines[noLines - 1];
				totalNoLines += (noLines - 1);
				System.err.println(totalNoLines);
				if (read < N) {
					break;
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return;
	}

	public static void main(String args[]) {
		try {
			PMI pmi = new PMI();
			Long startTime = System.currentTimeMillis();
			Long endTime = System.currentTimeMillis();
			
			pmi.readDocPass1("/home/zerone/2gramstuff.final.sorted");
			endTime = System.currentTimeMillis();
			System.out.println((endTime - startTime) / 1000);
			pmi.sibUnigram = new StringIntBijection(pmi.setUnigram);
			
			pmi.readDocPass2("/home/zerone/2gramstuff.final.sorted");
			endTime = System.currentTimeMillis();
			System.out.println((endTime - startTime) / 1000);
			pmi.sibBigram = new StringIntBijection(pmi.setBigram);
			
			pmi.readDocPass3("/home/zerone/2gramstuff.final.sorted");
			endTime = System.currentTimeMillis();			
			System.out.println((endTime - startTime) / 1000);
			
			System.out.println(pmi.setUnigram.size());
			System.out.println(pmi.setBigram.size());
			System.out.println(pmi.totalNoLines);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
