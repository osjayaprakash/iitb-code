package sm;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import sm.api.HttpGet;
import sm.api.Util;

public class Summarize {
	Sentence sentList[] = {null};
	List<Integer> summSentences = null;
	List<Integer> summClusters = null;
	Cluster cluster = null;
	public simWrapper simw = null;
	
	/*parameters*/
	Integer reqSize;
	Integer noClusters;
	String clusterName;
	Boolean sim;
	int isim;
	Double tradeOff, tradeR;
	Integer bucketSize;
	Integer currBucketSize;
	Integer availableSize;
	Double bucketCoverage, tradeOffB;
	String inFile;
	
	
	public void setbucketSize(int a) { bucketSize =a; availableSize = a; }
	public void setreqSize(int a) { reqSize =a; }
	public void setnoClusters(int a) { noClusters =a; }
	public void setclusterName(String a) { clusterName =a; }
	public void setSimilarity(String a) { if(a.equals("sem")) sim = true; else sim=false; }
	public void setSimilarity(int a) { isim = a; }
	public void settradeOff(Double a) { tradeOff =a; }
	public void settradeOffB(Double a) { tradeOffB =a; }
	public void settradeR(Double a) { tradeR =a; }
	public Sentence[] getSentList(){ return sentList; }
	
	public Summarize(){
		sim = false;
		bucketSize = -1;
		currBucketSize = 0;
		reqSize = -1;
		availableSize = 1000000000;
		tradeOff = 1.0;
		tradeR = 1.0;
		tradeOffB = 0.1;
		
		summSentences = new ArrayList<Integer>();
		summClusters = new ArrayList<Integer>();
	}
	
	private boolean isSummSentence(int sentID){
		return sentList[sentID].isSummSentence;		
	}

	private boolean isSummCluster(int clusterID){
		for(Integer i=0;i<summClusters.size();i++)
			if( summClusters.get(i) == clusterID )
				return true;
		return false;
	}
	
	private Double getCoverageScore(Integer sentID) throws IOException{
		Double res = 0.0;
		Sentence s1 = sentList[sentID];
		for(Integer i=0;i<sentList.length;i++){
			if( isSummSentence(i) || i == sentID)
				continue;
			Sentence s2 = sentList[i];
			if(sim){
				HttpGet hg = new HttpGet();
				res += hg.getscore(s1.getSentString(), s2.getSentString());
			}else
				res += s1.score(s2);
		}
		return res;
	}
	

	private Double getDiversityScore(Integer sentID) throws IOException{
		Double res = 0.0;
		Sentence s1 = sentList[sentID];
		for(Integer i=0;i<sentList.length;i++){
			if( isSummSentence(i)  || i == sentID)
				continue;
			Sentence s2 = sentList[i];
			if(sim){
				HttpGet hg = new HttpGet();
				res += hg.getscore(s1.getSentString(), s2.getSentString());
			}else
				res += s1.score(s2);
		}
		
		Double clusterScore[]  = new Double[sentList.length];
		
		for(int i=0;i<clusterScore.length;i++)
			clusterScore[i]=0.0;
		
		for(int i=0;i<sentList.length;i++){
			if(i==sentID) 
				continue;
			Sentence s2 = sentList[i];
			Integer clusterID = cluster.getClusterID(i);
			Double currScore  = s1.score(s2);
			clusterScore[clusterID] = clusterScore[clusterID] + currScore;
		}
		
		for(Double score : clusterScore){
			res += Math.sqrt(score);
		}
		
		return res;		
	}

	
	private Double getScore (Integer sentID) throws IOException{
		Double covScore = getCoverageScore(sentID);
		Double divScore = getDiversityScore(sentID);
		sentList[sentID].setCoverageScore(covScore);
		sentList[sentID].setDiversityScore(divScore);
		return covScore + tradeOff* divScore;
	}
	
	private Integer getMaxSentence() throws IOException{
		Integer res = -1;
		Double maxScore = -1.0;
		for(Integer i=0;i<sentList.length;i++){
			if( isSummSentence(i) )
				continue;
			
			Sentence sent = sentList[i];
			if( sent.wordList.size() > availableSize ){
				sent.isSummSentence = true; // mark for not available
				continue;
			}
				
			Double score = getScore(i);
			System.err.println("sent "+i+" => " + score);
			if(maxScore < score){
				maxScore = score;
				res = i;
			}
		}
				
		return res;
	}

	public List<Sentence> doSummarizeFromFile(String fileName){
		String fileContent = Util.getFileContents(fileName);
		return doSummarizeFromText(fileContent);
	}
	
	public Double getCScore(){
		Double res = 0.0;
		for(int i: summSentences){
			for(int j=0;j<sentList.length;j++){
				res += simw.getScore(i, j);
			}
		}
		return res;
	}

	public Double getAllCScore(){
		Double res = 0.0;
		for(int i=0;i<sentList.length;i++){
			for(int j=0;j<sentList.length;j++){
				res += simw.getScore(i, j);
			}
		}
		return res;
	}

	
	public Double getDScore(){
		Double res = 0.0;

		Double clusterScore[]  = new Double[sentList.length];
		
		for(int i=0;i<clusterScore.length;i++)
			clusterScore[i]=0.0;
		
		for(int i=0;i<sentList.length;i++){
			Sentence si = sentList[i];
			if(si.isSummSentence == false) 
				continue;
			Integer clusterID = cluster.getClusterID(i);
			Double currScore  = 0.0;
			for( int j=0;j<sentList.length;j++ ){
				currScore += simw.getScore(i, j);
			}
			clusterScore[clusterID] = clusterScore[clusterID] + currScore;
		}
		
		for(Double score : clusterScore){
			res += Math.sqrt(score);
		}
		
		if(res > 0.0)
			res = res / sentList.length;
		
		return res;		
	}

	public Double getDDScore(){
		Double res = 0.0;
		
		Set<String> set = new HashSet<String>();
		
		for(int i=0;i<sentList.length;i++){
			Sentence si = sentList[i];
			if(si.isSummSentence == false) 
				continue;
			for(String w: si.wordMap.keySet()){
				set.add(w);
			}			
		}
		
		for(String w: set){
			if( Util.idf.containsKey(w) && Util.tf.containsKey(w) )
				res += Util.idf.get(w) * Util.tf.get(w);
		}
		
		//System.err.println( res );
		
		if(res > 0.0)
			res = res / sentList.length;
		
		return res;		
	}

	
	public List<Sentence> doProcess(){
		List<Sentence> res = new ArrayList<Sentence>();
		
		Double prevScore = 0.0;
		Double allScore = 0.0;
		
		allScore = getAllCScore() * tradeOffB;
		
		while(true){
			Integer sentID = -1;
			Double score, maxScore = -1.0, maxPrevScore = 0.0, scoreDiff = 0.0;
			
			for(int sentId = 0; sentId < sentList.length; sentId++){
				Sentence sent = sentList[sentId];
				
				if (sent.isSummSentence == true)
					continue;
				
				if(bucketSize != -1 && (currBucketSize + sent.wordList.size() > bucketSize) )
					continue;
				
				sent.isSummSentence = true;
				summSentences.add(sentId); // mark examined
				summClusters.add( cluster.getClusterID(sentId) ); // for penalty
				
				Double csore = getCScore();
				Double dscore = getDDScore();
				System.err.println( "::" + csore + "\t" + allScore+ "\t" + dscore );
				score = Math.min(csore, allScore)  + tradeOff * dscore;
				scoreDiff = score - prevScore;
				scoreDiff = scoreDiff / Math.pow( sent.wordList.size() , tradeR );
				if(scoreDiff > maxScore){
					sentID = sentId;
					maxScore = scoreDiff;
					maxPrevScore = score;
				}
				
				sent.isSummSentence = false;
				summSentences.remove( summSentences.size()-1 ); // mark examined
				summClusters.remove( summClusters.size()-1 ); // for penalty
				
			}
			if(sentID < 0 ){
				break;
			}
			System.err.println("===>" + maxScore + " " + prevScore);
			Sentence sent = sentList[sentID];
			res.add( sent ); // as a result	
			summSentences.add(sentID); // mark examined
			sent.isSummSentence = true;	
			summClusters.add( cluster.getClusterID(sentID) ); // for penalty
			currBucketSize = currBucketSize + sent.wordList.size();
			availableSize = availableSize - sent.wordList.size();
			prevScore = maxPrevScore;
			System.err.println("res = " + sentID + " = " + sentList[sentID].getSentString());
		}
		return res;
	}
	
	public List<Sentence> doSummarizeFromText(String fileContent){
		
		HttpGet.mssd.clear();
		
		List<Sentence> res = new ArrayList<Sentence>();
		
		//String sentArr[] = fileContent.split("(?<=[a-zA-Z])\\.\\s+");
		String sentArr[] = Util.SentenceDetector(fileContent);
		
		/// 
		sentList = new Sentence[ sentArr.length ];
		///
		
		noClusters = Math.min(noClusters, sentList.length);
		
		/**** Sentence Construction */
		
		int iSentID = 0;
		for(String sent : sentArr){
			System.err.println(sent);
			//if(sent.length()<5)
				//continue;
			Sentence s =  new Sentence( sent );
			s.setSentID(iSentID);
			sentList[iSentID] = s ;
			iSentID ++;
		}
		System.err.println("Sim Computation Started---------------------------------------");
		
		/*** Similarity Computation ***/
		
		simw = new simWrapper(sentList, this);

		System.err.println("Sim Computation Ended---------------------------------------");

		System.err.println("clustering started---------------------------------------" + clusterName);

		/* Clustering */
		
		if( clusterName == null || clusterName.equals("km") ){
			cluster = new KMCluster(noClusters, sentList);
			cluster.doCluster();
			cluster.print();
		}else{
			cluster = new SLCluster(noClusters, sentList);
			cluster.doCluster();
			cluster.print();			
		}
		System.err.println("clustering done-------------------------------------------");
		
		/* Run algo */
		
		return doProcess();
		
	}
	
	public static void main(String args[]){
		
		Util.loadIDF("/home/zerone/Desktop/duc/duc2007/corpus");
		runTest ();
		
		
		/*
		String fileName = null;
		if(args.length == 0)
			fileName = "/home/zerone/code/SubModular/test/test01.txt";
		else
			fileName = args[0];
		
		System.err.println( "Wordnet Stem Test:" + Util.getWordNetStem("walking") );
		//System.exit(0);
		
		Summarize sum = new Summarize();
		
		//sum.setreqSize(5);
		sum.setnoClusters(150);
		sum.setSimilarity("sim");
		
		
		sum.settradeOff(6.0);
		sum.settradeR(1.5);
		sum.settradeOffB(0.01);
		
		sum.setclusterName("km");
		sum.setbucketSize(200);
		
		List<Sentence> ls =  sum.doSummarizeFromFile(fileName);
		int i=0;
		for(Sentence s:ls){
			System.out.println(  s.getSentString() );
		}
		*/
	}
	
	public static void runTest(){
		String  input[] = {"/home/zerone/Desktop/duc/duc2007/corpus/test/D0703.txt",
								"/home/zerone/Desktop/duc/duc2007/corpus/test/D0707.txt",
								"/home/zerone/Desktop/duc/duc2007/corpus/test/D0704.txt",
								"/home/zerone/Desktop/duc/duc2007/corpus/test/D0716.txt",
								"/home/zerone/Desktop/duc/duc2007/corpus/test/D0710.txt",
								"/home/zerone/Desktop/duc/duc2007/corpus/test/D0705.txt",
								"/home/zerone/Desktop/duc/duc2007/corpus/test/D0720.txt",
								"/home/zerone/Desktop/duc/duc2007/corpus/test/D0740.txt"};
		
		String  input1[] = 
			   {"D0703",
				"D0707",
				"D0704",
				"D0716",
				"D0710",
				"D0705",
				"D0720",
				"D0740" 
				
				};
		
		String dir1 = "/home/zerone/Desktop/duc/duc2007/corpus/test/";
		
		
		for(Double B = 0.1; B <= 0.5; B += 0.1){
			for(Double off = 5.0; off <= 10.0; off += 2.0){
				for(Double R = 1.0; R <= 5.0; R += 2.0){
					for(int sim =0;sim<3;sim++){
						String dir = dir1+"res1"+"_R"+R+"_B"+B+"_off"+off+"_sim"+sim+"/";
						new File(dir).mkdirs();
						for(String fname:input1){							
							
							
							Summarize sum = new Summarize();
							
							String outFile = dir+fname+".M.250.A.1";
							sum.inFile = dir1+fname+".txt";
							
							sum.settradeOff(off);
							sum.settradeR(R);
							sum.settradeOffB(B);
						
							sum.setclusterName("km");
							sum.setbucketSize(200);
							sum.setnoClusters(100);
							
							sum.setSimilarity(sim);
							
							try{
								Util.loadTF( sum.inFile );
								
								sum.runSummarizeFromFile(sum.inFile, outFile);
								//System.exit(1);
							}catch (Exception e) {
								e.printStackTrace();
							}
							
						}
					}
				}
			}
			
		}

	}
	
	
	public void runSummarizeFromFile(String fileName, String outFileName) throws IOException{
		
		String fileContent = Util.getFileContents(fileName);
	
		List<Sentence> res = new ArrayList<Sentence>();
		String sentArr[] = Util.SentenceDetector(fileContent);
		
		/// 
		sentList = new Sentence[ sentArr.length ];
		///
		
		
		/**** Sentence Construction */
		
		int iSentID = 0;
		for(String sent : sentArr){
			System.err.println(sent);
			Sentence s =  new Sentence( sent );
			s.setSentID(iSentID);
			sentList[iSentID] = s ;
			iSentID ++;
		}
		System.err.println("Sim Computation Started---------------------------------------");
		
		noClusters = Math.min(noClusters, sentList.length/6);
		noClusters = sentList.length/6;

		/*** Similarity Computation ***/
		
		simw = new simWrapper(sentList, this);

		System.err.println("Sim Computation Ended---------------------------------------");

		System.err.println("clustering started---------------------------------------" + clusterName);

		/* Clustering */
		
		if( clusterName == null || clusterName.equals("km") ){
			cluster = new KMCluster(noClusters, sentList);
			cluster.doCluster();
			cluster.print();
		}else{
			cluster = new SLCluster(noClusters, sentList);
			cluster.doCluster();
			cluster.print();			
		}
		System.err.println("clustering done-------------------------------------------");
		
		/* Run algo */
		
		res =  doProcess();
		FileWriter outWriter = new FileWriter(outFileName);
		for(Sentence s:res){
			System.err.println(s.getSentString());
			outWriter.write(s.getSentString());
		}
		outWriter.close();
	}

}
