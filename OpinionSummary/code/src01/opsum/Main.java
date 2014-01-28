package opsum;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.List;

import util.Constants;

public class Main {

	public static void main(String[] args) {
		
		try{
			File file = new File(Constants.CONFIG_BASE_DIR+"log.txt");
			FileOutputStream fos = new FileOutputStream(file);
			PrintStream ps = new PrintStream(fos);
			System.setErr(ps);
		}catch (Exception e) {
			e.printStackTrace();
		}

		
		Summarize summarizer = new Summarize();
		String testFile = "/home/zerone/code/OpinionSummary/code/test/001.txt";
		List<Sentence> lSent = summarizer.doSummarizeFromFile(testFile);
		for(Sentence sent : lSent){
			System.out.println ( sent );
		}
	}

}
