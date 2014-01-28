package util;

import java.io.FileInputStream;
import java.io.InputStream;

import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;

public class SentenceDetector {
	public static String[] run(String paragraph){
		InputStream is;
		SentenceModel model;
		String sentences[] = {null};
		try {
			is = new FileInputStream(Constants.SENTENCE_MODEL);
			model = new SentenceModel(is);
			SentenceDetectorME sdetector = new SentenceDetectorME(model);
			sentences = sdetector.sentDetect(paragraph);
			is.close();
		} catch (Exception e) {
			e.printStackTrace();
		} 	
		return sentences;
	}
}
