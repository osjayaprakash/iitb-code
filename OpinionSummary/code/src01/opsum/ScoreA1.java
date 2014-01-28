package opsum;

public class ScoreA1 implements ScoreA {
	Summarize summ = null;
	
	public ScoreA1( Summarize sl ){
		summ = sl;
	}
	
	public Double getAScore(){
		Double res = 0.0;
		for(Integer iSentID : summ.summSentences){
			Integer aspID = summ.sentList[iSentID].iAspectID;
			Double w = Summarize.aspects[aspID].dWeight;
			Double sScore = summ.sentList[iSentID].dSentiScore;
			res += (w* sScore);
		}
		return res;
	}
}
