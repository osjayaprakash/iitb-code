package sm.api;

import java.util.List;

public class SentSim {
	MWBMatchingAlgorithm mwb  = null;
	
	public SentSim(){
		mwb = null;
	}
	
	public void constructGraph(List<String> sent1, List<String> sent2){
		int n = sent1.size();
		int m = sent2.size();
		
		mwb = new MWBMatchingAlgorithm(n, m);
		WSWrapper.init();
		int i = 0;
		for(String word1:sent1){
			int j = 0;
			for(String word2:sent2){
				Double edgeWeight = 0.0;
				edgeWeight = WSWrapper.getLin(word1, word2);
				if(word1.equalsIgnoreCase(word2))
					mwb.setWeight(i, j, 1.0);
				else
					mwb.setWeight(i, j, edgeWeight);
				//System.err.println("g"+i+","+j+"="+edgeWeight+" " + word1 + " " + word2);
				j++;
			}
			i++;
		}
		//System.exit(0);
		
	}
	
	public Double getScore(List<String> sent1, List<String> sent2){
		Double res = 0.0;
		constructGraph(sent1, sent2);
		res = mwb.getMaximalMatching();
		// normalize ; to avoid bias towards longer sent
		res = res / Math.max( sent1.size() , sent2.size()); 
		return res;
	}
	/*
	public static void main(String args[]){
		MWBMatchingAlgorithm mwb = new MWBMatchingAlgorithm(2,2);
		mwb.setWeight(0, 1, 2.0);
		mwb.setWeight(0, 0, 3.0);
		mwb.setWeight(1, 1, 3.0);
		Double res = mwb.getMaximalMatching();
		System.out.println( res );
	}
	*/
}
