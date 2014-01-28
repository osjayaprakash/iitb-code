package sm.api;

import edu.cmu.lti.lexical_db.ILexicalDatabase;
import edu.cmu.lti.lexical_db.NictWordNet;
import edu.cmu.lti.ws4j.RelatednessCalculator;
import edu.cmu.lti.ws4j.impl.Lesk;
import edu.cmu.lti.ws4j.impl.Lin;
import edu.cmu.lti.ws4j.impl.WuPalmer;


public class WSWrapper {
	public static ILexicalDatabase db;
	public static RelatednessCalculator lesk, lin, wu;
	
	public static void init(){
		db = new NictWordNet();
		lesk = new Lesk(db);		
		lin = new Lin(db);		
		wu = new WuPalmer(db);		
	}
	
	public static Double getLesk(String w1, String w2){
		return lesk.calcRelatednessOfWords(w1, w2);		
	}

	public static Double getLin(String w1, String w2){
		return lin.calcRelatednessOfWords(w1, w2);		
	}

	public static Double getWu(String w1, String w2){
		return wu.calcRelatednessOfWords(w1, w2);		
	}

	public static void main(String a[]){
		ILexicalDatabase db = new NictWordNet();
		RelatednessCalculator rc = new Lin(db);
		System.out.println( rc.calcRelatednessOfWords("run", "walk") );
	}
}
