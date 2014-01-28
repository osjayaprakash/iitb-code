package sm.api;

import java.io.File;
import java.io.File;
import java.io.FilenameFilter;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import edu.mit.jwi.Dictionary;
import edu.mit.jwi.morph.WordnetStemmer;

import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import sm.collection.Tokenizer;

public class Util {
	public static Map<String, Double> idf = null;
	public static Map<String, Double> tf = null;
	public static Double numDocs = 0.0;
	private static Dictionary dict = null;
	private static WordnetStemmer stemmer = null;
	public static String modelSentence =  "/home/zerone/code/SubModular/lib/en-sent.bin";
	public static String modelToken =  "/home/zerone/code/SubModular/lib/en-token.bin";
	public static boolean usePorterStem = false;
	public static boolean useWordnetStem = false;
	
	public static String[] SentenceDetector (String paragraph)  {
		InputStream is;
		SentenceModel model;
		String sentences[] = {null};
		try {
			is = new FileInputStream(modelSentence);
			model = new SentenceModel(is);
			SentenceDetectorME sdetector = new SentenceDetectorME(model);
			sentences = sdetector.sentDetect(paragraph);
			is.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 		
		return sentences;
	}

	
	public static String getFileContents(String fileName){
	   String content = null;
	   File file = new File(fileName);
	   try {
	       FileReader reader = new FileReader(file);
	       char[] chars = new char[(int) file.length()];
	       reader.read(chars);
	       content = new String(chars);
	       reader.close();
	   } catch (IOException e) {
	       e.printStackTrace();
	   }
	   return content;
	}
	
		
	public static String getWordNetStem(String term){
		try{
			if( dict == null){
				dict = new Dictionary(new File("/home/zerone/code/SubModular/lib/res/dict/"));
				dict.open();
				stemmer = new WordnetStemmer(dict);
			}
			List<String> lStrings =  stemmer.findStems(term, null);
			System.err.println( lStrings );
			if( lStrings.size() > 0 ){
				String res = lStrings.get(0);
				for(String s:lStrings){
					if(s.length() < res.length())
						res = s;
				}
				return res;
			}
		}catch(IOException e){
			e.printStackTrace();
		}
		return null;
	}
	
	public static int[] get_k_rand(int N, int K){
		int[] ans = new int[K];
        Random rand = new Random();
		for (int k = 0; k < K; k++)
                ans[k]=k;
        
        for(int k = K ; k < N; k++) {
        	int v = rand.nextInt(k+1);
        	if(v < K) {
        		ans[v] = k;
        	}
        }
        return ans;
	}
	
	public static File[] FindTextFiles( String dirName){
    	File dir = new File(dirName);

    	return dir.listFiles(new FilenameFilter() { 
    	         public boolean accept(File dir, String filename)
    	              { return filename.endsWith(".txt"); }
    	} );

    }

	
	public static void loadIDF(String dir){
		idf = new HashMap<String, Double>();
		int dCnt = 0;
		for(File d: FindTextFiles(dir)){
			try{
				System.err.println(d.getAbsolutePath());
				updateIDF(d);
			}catch(Exception e){
				e.printStackTrace();
			}
			dCnt ++;
		}
		
		for(String s:idf.keySet()){
			double cnt = idf.get(s);
			idf.put(s, Math.log(dCnt/cnt));
		}
		
	}

	public static void updateIDF (File file) throws FileNotFoundException, IOException{
		updateIDF(file.getAbsolutePath());
	}
	
	public static void updateIDF(String filename) throws FileNotFoundException, IOException{
		String content = getFileContents(filename);
	    Tokenizer tokenizer=null;
	    tokenizer=new Tokenizer(Util.modelToken);
	    Set<String> set = new HashSet<String>();
	    for(String word: tokenizer.getTokens(content)){
	    	set.add(word);
	    }
	    for(String s:set){
	    	Double cnt = 0.0;
	    	if( idf.containsKey(s) )
	    		cnt = idf.get(s);
	    	cnt+=1.0;
	    	idf.put(s, cnt);
	    }
	}
	
	public static Map<String, Double> loadTF(String filename) throws FileNotFoundException, IOException{
		tf = new HashMap<String, Double>();
		String content = getFileContents(filename);
		Double maxCnt = 0.0;
	    Tokenizer tokenizer=null;
	    tokenizer=new Tokenizer(Util.modelToken);
	    for(String s:tokenizer.getTokens(content)){
	    	Double cnt = 0.0;
	    	if( tf.containsKey(s) )
	    		cnt = tf.get(s);
	    	cnt+=1.0;
	    	tf.put(s, cnt);
	    	if(cnt > maxCnt)
	    		maxCnt = cnt;
	    }
		for(String s:tf.keySet()){
			double cnt = tf.get(s);
			tf.put(s, cnt / maxCnt );
		}
		return tf;
	}

	public static Double getIDF(String word){
		if(idf != null && idf.containsKey(word))
			return idf.get(word);
		return 0.0;
	}
	
	public static Double getTF(String word){
		if(tf != null && tf.containsKey(word))
			return tf.get(word);
		return 0.0;
	}
	
	public static void main(String args[]){
		int randnos[] = get_k_rand(100, 10);
		for(int no:randnos)
			System.out.println(no);
		System.err.println( Util.getWordNetStem("running") );
	}
	
}
