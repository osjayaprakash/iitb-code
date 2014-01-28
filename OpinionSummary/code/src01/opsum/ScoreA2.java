package opsum;

public class ScoreA2 implements ScoreA {
	Summarize summ = null;
	
	public ScoreA2( Summarize sl ){
		summ = sl;
	}
	
	public Double getAScore(){
		Double res = 0.0;
		Double aspScore[] = new Double[Summarize.iAspectCnt];
		
		for(int i=0;i<aspScore.length;i++)
			aspScore[i] = 0.0;
		
		for(Integer iSentID : summ.summSentences){
			Integer aspID = summ.sentList[iSentID].iAspectID;
			Double sScore = summ.sentList[iSentID].dSentiScore;
			aspScore[aspID] += sScore;
		}
		
		for(int i=0;i<aspScore.length;i++){
			Double w = Summarize.aspects[i].dWeight;
			res += Math.min(aspScore[i], Summarize.aspects[i].dBudget) * w ;
		}
		
		return res;
	}
}
