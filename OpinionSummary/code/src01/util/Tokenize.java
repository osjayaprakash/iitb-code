package util;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;

import opennlp.tools.tokenize.SimpleTokenizer;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;

public class Tokenize {
	public static TokenizerModel model = null;
	public static Tokenizer sdetector = null;

	public static Tokenizer getInstance() {
		if (sdetector == null) {
			InputStream is;
			try {
				is = new FileInputStream(Constants.TOKENIZER_MODEL);
				model = new TokenizerModel(is);
				sdetector = new TokenizerME(model);
				is.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return sdetector;
	}

	public static String[] runME(String paragraph) {
		String sentences[] = { null };
		try {
			getInstance();
			sentences = sdetector.tokenize(paragraph);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sentences;
	}
	
	public static String[] runSimple(String paragraph) {
		String sentences[] = { null };
		try {
			if(sdetector == null)
				sdetector = new SimpleTokenizer();
			sentences = sdetector.tokenize(paragraph);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sentences;
	}
	
	public static String[] doPostProcessing(String[] tokens ){
		List <String> l = new ArrayList<String>();
		for( String token : tokens ){
			String tok = token;
			tok.toLowerCase();
			if(tok.matches("^.*\\p{Punct}$"))
				continue;
			String res = WordNetStem.getWordNetStem(tok);
			if(res != null)
				l.add(res);
			else
				l.add(tok);
		}
		return l.toArray(new String[l.size()]);
	}

	public static String[] run(String paragraph) {
		String tokens[] = runME(paragraph);
		return doPostProcessing(tokens);
	}

}
