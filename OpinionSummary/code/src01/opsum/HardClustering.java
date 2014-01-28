package opsum;

public class HardClustering {
	Summarize summ = null;
	
	public HardClustering(Summarize sum){
		summ	=	sum; 
	}
	
	public void run(){
		for(int i=0;i<summ.sentList.length;i++)
			clusterSentenceRandom(i);
	}

	public void clusterSentenceRandom(int sentID){
		int total = Summarize.iAspectCnt;
		Double dNo = Math.random();
		Integer iNo = dNo.intValue() % total;
		summ.sentList[sentID].iAspectID = iNo;
	}
	
}
