package opsum;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Collections;
import java.util.Comparator;
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
			System.err.println ( sent );
		}
		
		
		/** Format */
		
		List<Sentence> lSent1 = lSent;
		Collections.sort(lSent1, new Comparator<Sentence>() {
			  public int compare( Sentence o1,  Sentence o2) {
			    if (o1.getSentID() > o2.getSentID() ) {
			      return 1;
			    } else if (o1.getSentID() < o2.getSentID() ) {
			      return -1;
			    }  
			    return 0;
			  }
			});
		for(Sentence sent: lSent1){
			System.out.println(sent.getSentID()+")\t"+sent.getSentString());
		}
		System.out.println("Total number of words :: " + summarizer.currBucketSize);
		
		
	}

}
