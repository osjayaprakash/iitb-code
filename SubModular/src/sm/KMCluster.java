package sm;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import sm.Sentence;
import sm.api.Util;


public class KMCluster implements Cluster {
	Integer K;
	Integer sentListSize;
	Sentence[] sentList = {null};
	Integer[] clusterList = {null};
	Sentence[] clusterCenters = {null};
	Integer[] clusterCount={null};
	
	public KMCluster(int K, Sentence[] sentList){
		this.K = K;
		this.sentList = sentList;
		this.sentListSize = this.sentList.length;
		clusterList = new Integer[this.sentListSize];

		clusterCenters = new Sentence[K];
		clusterCount = new Integer[K];
		
		int candSentences[] = Util.get_k_rand(sentList.length, K);
		
		for(int i=0;i<K;i++){
			Sentence s = new Sentence();
			s.wordMap = new HashMap<String, Double>();
			int sentId = candSentences[i];
			if( sentId < this.sentListSize ){
				Sentence sFrom = sentList[ sentId ];
				for( String word : sFrom.wordMap.keySet() )
					s.wordMap.put(word, sFrom.wordMap.get(word) + 0.0);
			}
			clusterCenters[i] = s;
		}
		
	}
	
	public void doCluster(){
		for(int i=0;i<10;i++){
			System.err.println("doCluster " + i);
			assignClusters();
			computeCenteroids();
		}
	}
	
	public void assignClusters(){
		for(int i=0; i< this.sentListSize; i++){
			Sentence s = sentList[i];
			Double score = -1.0;
			for(int j=0;j<K;j++){
				Sentence c = clusterCenters[j];
				Double cscore = s.score(c);
				if( score < cscore ){
					score = cscore;
					s.setClusterID(j);
				}
			}
		}
	}
	
	public void computeCenteroids(){
		
		for(int i=0;i<K;i++){
			clusterCount[i] = 0;
			clusterCenters[i].wordMap.clear();
		}
		
		for(int i=0; i< this.sentListSize; i++){
			Sentence s = sentList[i];
			int clusterID = s.getClusterID();
			
			clusterCount[clusterID] += 1 ;
			Sentence cs = clusterCenters[clusterID];
			for(String word : s.wordMap.keySet()){
				Double value = 0.0;
				if( cs.wordMap.containsKey(word) )
					value = cs.wordMap.get(word);
				cs.wordMap.put(word, value+1.0);
			}
		}

		for(int i=0;i<K;i++){
			int cnt = clusterCount[i];
			if(cnt == 0)
				continue;
			Sentence cs = clusterCenters[i];
			for(String word : cs.wordMap.keySet()){
				Double v = cs.wordMap.get(word);
				cs.wordMap.put(word, v/cnt);
			}
		}
		
	}
	
	public Integer getClusterID(Integer sentID){
		return sentList[sentID].getClusterID();
	}
	
	public void print(){
		
		Sentence ls[] = Arrays.copyOf(sentList, sentListSize);
		
		Arrays.sort(ls, new Comparator<Sentence>() {
	           public int compare(Sentence o1, Sentence o2) {
	        	   	if( o1.getClusterID() != o2.getClusterID() )
	        	   		return o1.getClusterID() - o2.getClusterID();
	        	   	return o1.getSentID() - o2.getSentID();
	           }
	        });
        
        for(Sentence s:ls){
        	System.err.println( s.getClusterID() + "==>" + s.getSentString() );
        }
        
	}
}
