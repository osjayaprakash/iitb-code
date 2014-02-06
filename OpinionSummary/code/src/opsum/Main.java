package opsum;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import util.Constants;

public class Main {

	public static void println(String out) {
		// System.err.println(out);
	}

	public static void print(String out) {
		// System.err.print(out);
	}

	public static void runSample() {
		try {
			File file = new File(Constants.CONFIG_BASE_DIR + "log.txt");
			FileOutputStream fos = new FileOutputStream(file);
			PrintStream ps = new PrintStream(fos);
			System.setErr(ps);
		} catch (Exception e) {
			e.printStackTrace();
		}

		Summarize summarizer = new Summarize();
		summarizer.scoreL = new ScoreL(summarizer);
		summarizer.scoreA = new ScoreA2(summarizer);

		String testFile = "/home/zerone/code/OpinionSummary/code/test/001.txt";

		List<Sentence> lSent = summarizer.doSummarizeFromFile(testFile);
		for (Sentence sent : lSent) {
			System.err.println(sent);
		}

		/** Format */

		List<Sentence> lSent1 = lSent;
		Collections.sort(lSent1, new Comparator<Sentence>() {
			public int compare(Sentence o1, Sentence o2) {
				if (o1.getSentID() > o2.getSentID()) {
					return 1;
				} else if (o1.getSentID() < o2.getSentID()) {
					return -1;
				}
				return 0;
			}
		});
		
		for (Sentence sent : lSent1) {
			System.out.println(sent.getSentID() + ")\t" + sent.getSentString());
		}
		
		System.out.println("Total number of words :: "
				+ summarizer.currBucketSize);
		
		summarizer.aspectRoot.printOk("");
		
	}

	public static void runTestForFile(String filename, String outPrefix)
			throws Exception {
		Double startAlpha = 0.0;
		Double endAlpha = 5.0;
		Double intAlpha = 0.5;

		Double startR = 0.0;
		Double endR = 10.0;
		Double intR = 1.0;
		File f = new File(outPrefix);
		f.mkdirs();

		for (int i = 1; i <= 4; i++) {
			for (Double Alpha = startAlpha; Alpha <= endAlpha; Alpha += intAlpha) {
				for (Double R = startR; R <= endR; R += intR) {

					Summarize summarizer = new Summarize();

					summarizer.scoreL = new ScoreL(summarizer);
					if (i == 1)
						summarizer.scoreA = new ScoreA1(summarizer);
					else if (i == 2)
						summarizer.scoreA = new ScoreA2(summarizer);
					else if (i == 3)
						summarizer.scoreA = new ScoreA3(summarizer);
					else if (i == 4)
						summarizer.scoreA = new ScoreA4(summarizer);

					summarizer.tradeOff = Alpha;
					summarizer.tradeR = R;

					List<Sentence> lSent = summarizer
							.doSummarizeFromFile(filename);
					List<Sentence> lSent1 = lSent;
					Collections.sort(lSent1, new Comparator<Sentence>() {
						public int compare(Sentence o1, Sentence o2) {
							if (o1.getSentID() > o2.getSentID()) {
								return 1;
							} else if (o1.getSentID() < o2.getSentID()) {
								return -1;
							}
							return 0;
						}
					});

					String outFileName = outPrefix + "A" + i + "_ALPHA" + Alpha
							+ "_R" + R;
					System.err.println("\t\t" + outFileName);
					PrintWriter pw = new PrintWriter(outFileName);
					for (Sentence sent : lSent1) {
						// System.out.println(sent.getSentID() + ")\t" +
						// sent.getSentString());
						pw.print(sent.getSentString() + "\n");
					}
					pw.close();

					/*
					 * if(ccnt>0) System.exit(0); ccnt++;
					 */
				}
			}
		}
	}

	public static void runTest() {

		try {
			File file = new File(Constants.CONFIG_BASE_DIR + "log.txt");
			FileOutputStream fos = new FileOutputStream(file);
			PrintStream ps = new PrintStream(fos);
			System.setErr(ps);
		} catch (Exception e) {
			e.printStackTrace();
		}

		String dir = "/home/zerone/code/OpinionSummary/corpus/polar/txt_sentoken/pos/";
		for (File d : util.IDF.FindTextFiles(dir)) {
			try {
				String sFile = d.getAbsolutePath();
				System.err.println(sFile);
				runTestForFile(sFile, sFile + ".sum/");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	public static void main(String[] args) {
		//runTest();
		runSample();
	}
}
