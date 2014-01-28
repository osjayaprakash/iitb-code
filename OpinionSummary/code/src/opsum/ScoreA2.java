package opsum;

import java.util.HashMap;
import java.util.Map;

public class ScoreA2 implements ScoreA {
	Summarize summ = null;
	
	public ScoreA2( Summarize sl ){
		summ = sl;
	}
	
	public Double getAScore(){
		Double res = 0.0;
		Map<Aspect, Double> aspScore = new HashMap<Aspect, Double>();
		
		for(Integer iSentID : summ.summSentences){
			Sentence sent =  summ.sentList[iSentID];
			Aspect asp = sent.aspect;
			Double w = sent.aspect.getWeight();
			Double sScore = summ.sentList[iSentID].dSentiScore;
			
			Double score = aspScore.get(asp);
			if(score == null)
				score = 0.0;
			aspScore.put(asp, score+sScore);
		}
		
		for(Aspect asp: aspScore.keySet()){
			Double w = asp.getWeight();
			Double tScore = aspScore.get(asp); 
			res += Math.min(tScore, asp.getBudget()) * w ;
		}
		
		return res;
	}
}
