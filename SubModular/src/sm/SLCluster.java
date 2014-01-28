package sm;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.Semaphore;


import sm.Sentence;


public class SLCluster implements Cluster  {
	Integer K, currClusterSize;
	Integer sentListSize;
	Sentence sentList[] = {null};
	Integer clusterList[] = {null};
	
	
	public SLCluster(int K, Sentence[] sentList){
		this.K = K;
		this.sentList = sentList;
		this.sentListSize = this.sentList.length;
		clusterList = new Integer[sentList.length];
		for(Integer i=0; i<sentListSize; i++)
			clusterList[i] = i;
		currClusterSize = this.sentListSize;
	}
	
	public void doCluster(){
		Integer cluster1=0, cluster2=0;
		Double maxScore;
		Comparator<SLAux> comparator = new SLAuxComparator();
        PriorityQueue<SLAux> queue = 
            new PriorityQueue<SLAux>(sentListSize*sentListSize, comparator);
        
		for(int i=0;i<sentListSize;i++){
			Sentence s = this.sentList[i];
			for(int j=0;j<sentListSize;j++){
				SLAux sl = new SLAux(s.scoreCosine( sentList[j] ), i, j);
				queue.add(sl);
			}
		}
		
		while(K < currClusterSize){
			SLAux s = queue.poll();
			if(s == null)
				break;
			System.err.println(s.score + " " + s.sent1ID + " " +s.sent2ID);
			if(s.sent1ID == s.sent2ID)
				continue;
			int c1 = clusterList[s.sent1ID];
			int c2 = clusterList[s.sent2ID];
			if( c1 == c2 )
				continue;
			
			mergeClusters(c1, c2);
			
			currClusterSize --;			
		}
		/*
		while( K < currClusterSize){
			maxScore = Double.MIN_VALUE;
			for(Integer i=0; i<this.sentListSize;i++){
				Sentence s1 = sentList.get(i);
				for(Integer j=0;j<this.sentListSize;j++){
					Integer ci = clusterList.get(i); 
					Integer cj = clusterList.get(j); 
					if(i==j ||  ci == cj )
						continue;
					Sentence s2 = sentList.get(j);
					Double score = s1.score(s2);
					if(score > maxScore){
						maxScore = score;
						cluster1 = ci;
						cluster2 = cj;
					}
				}
			}
		
			mergeClusters(cluster1, cluster2);
			
			currClusterSize --;
			
		} //end while
		*/
		assignClusterIDs();
	}
	
	/***
	 * Merge Cluster1 and Cluster2  ( Cluster1 => Cluster2 )SLCluster
	 * 
	 * @param cluster1
	 * @param cluster2
	 */
	
	public void mergeClusters(Integer cluster1, Integer cluster2){
		System.err.println("merge " + cluster1 + " " + cluster2);
		for(Integer i=0; i<this.sentListSize; i++){
			Integer ci = clusterList[i];
			if(ci==cluster1){
				clusterList[i] = cluster2;
				System.err.println("change cluster ("+i+") " + cluster1 + " --> " + cluster2);
			}
		}
	}
	
	public Integer getClusterID(Integer sentID){
		return clusterList[sentID];
	}
	
	private void assignClusterIDs(){
		for(int i = 0; i<sentList.length;i++){
			Sentence sent = sentList[i];
			sent.setClusterID( getClusterID(sent.getSentID() ) );
			System.err.println("cluster id " + sent.getSentID() +" = " + getClusterID(sent.getSentID()) );
		}
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
