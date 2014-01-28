package opsum;

public class Clustering {
	Summarize summ = null;
	
	public Clustering(Summarize sum){
		summ	=	sum; 
	}
	
	public void run(){
		for(int i=0;i<summ.sentList.length;i++)
			clusterSentenceRandom(i);
	}

	public void clusterSentenceRandom(int sentID){
		Sentence sent = summ.sentList[sentID];
		sent.dAspRatio = new Double[Summarize.iAspectCnt];
		Double sumW = 0.0;
		for(int i=0;i<sent.dAspRatio.length;i++){
			Double w = Math.random();
			sent.dAspRatio[i] = w;
			sumW += w;
		}
		for(int i=0;i<sent.dAspRatio.length;i++){
			sent.dAspRatio[i] = sent.dAspRatio[i] / sumW;
		}		
	}
}
