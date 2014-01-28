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
	}
}
