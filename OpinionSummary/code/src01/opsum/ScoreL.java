package opsum;

public class ScoreL {
	
	Summarize sum = null;
	
	public ScoreL( Summarize sl ){
		sum = sl;
	}
	
	/*** C1 ***/
	public Double getCiScore1(int sentID){
		Double res = 0.0;
		for(int i: sum.summSentences)
				res += sum.simWrapper.getScore(i, sentID);
		return res;
	}
	
	public Double getCScore1(){
		Double res = 0.0;
		for(int j=0;j<sum.sentList.length;j++)
			res += Math.min( sum.sentList[j].getBudgetScore() , getCiScore1(j) );
		return res;
	}
	
	/*** C2 ***/
	public Double getCScore2(){
		Double res = 0.0;
		for(int j=0;j<sum.sentList.length;j++){
			Double maxMatch = 0.0;
				for(int i: sum.summSentences)
					maxMatch = Math.max(maxMatch, sum.simWrapper.getScore(i, j));
			res += maxMatch ;
		}
		return res;
	}
	
	public Double getCScore(){
		return getCScore1();
		//return getCScore2();
	}

}
