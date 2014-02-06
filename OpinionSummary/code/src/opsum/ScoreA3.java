package opsum;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class ScoreA3 implements ScoreA {
	Summarize summ = null;

	public ScoreA3(Summarize sl) {
		summ = sl;
	}

	public Double getAScore() {
		Double res = 0.0;
		/* negative sum and posive sum */
		Map<Aspect, Double> aspScorePos = new HashMap<Aspect, Double>();
		Map<Aspect, Double> aspScoreNeg = new HashMap<Aspect, Double>();

		for (Integer iSentID : summ.summSentences) {
			Sentence sent = summ.sentList[iSentID];
			Aspect asp = sent.aspect;
			Double w = sent.aspect.getWeight();
			Double sScore = summ.sentList[iSentID].dSentiScore;

			if (sScore < 0.0) {
				Double score = aspScoreNeg.get(asp);
				if (score == null)
					score = 0.0;
				score += Math.abs(sScore);
				aspScoreNeg.put(asp, score);
			} else {
				Double score = aspScorePos.get(asp);
				if (score == null)
					score = 0.0;
				score += Math.abs(sScore);
				aspScorePos.put(asp, score);
			}

		}

		Set <Aspect> ts = new TreeSet<Aspect>();
		ts.addAll( aspScorePos.keySet() );
		ts.addAll( aspScoreNeg.keySet() );
		
		for (Aspect asp : aspScorePos.keySet()) {
			Double w = asp.getWeight();

			// take positive sum
			Double tScorePos = 0.0;
			if( aspScorePos.containsKey(asp) )
				tScorePos = aspScorePos.get(asp);
			
			// take negative sum
			Double tScoreNeg = 0.0;
			if( aspScoreNeg.containsKey(asp) )
				tScoreNeg = aspScoreNeg.get(asp);
			
			Double tScore = tScorePos+tScoreNeg;
			// add budgeted score to result
			res += Math.min(tScore, asp.getBudget() * w);

			asp.d_Budget = asp.getBudget() * w;
			asp.d_Score = tScore;
			asp.d_MXScore = Math.max(tScore, asp.d_MXScore);
			
		}


		return res;
	}
}
