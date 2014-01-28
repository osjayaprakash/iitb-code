package util;

import java.io.File;
import java.io.IOException;
import java.util.List;

import edu.mit.jwi.Dictionary;
import edu.mit.jwi.morph.WordnetStemmer;

public class WordNetStem {
	private static Dictionary dict = null;
	private static WordnetStemmer stemmer = null;

	public static String getWordNetStem(String term){
		try{
			if( dict == null){
				dict = new Dictionary(new File(Constants.WORDNET_DICT_DIR));
				dict.open();
				stemmer = new WordnetStemmer(dict);
			}
			List<String> lStrings =  stemmer.findStems(term, null);

			if( lStrings.size() > 0 ){
				String res = lStrings.get(0);
				return res;/*
				for(String s:lStrings){
					if(s.length() < res.length())
						res = s;
				}
				return res;*/
			}
		}catch(IOException e){
			e.printStackTrace();
		}
		return null;
	}

}
