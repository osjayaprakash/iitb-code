package opsum;

import java.util.ArrayList;
import java.util.List;

import util.*;

public class Summarize {
	
	public static Double tradeOffB 	= 0.1;
	public static Double tradeOff  	= 0.5;
	public static Double tradeR		= 0.4;
	public static Double tradeCBudget	= 0.1;
	public static Aspects aspects[] = null;
	public static OntCreate ontCreate = null;
	public static Integer iAspectCnt = 5;
	public static Boolean isSingleDone = false;
	
	
	public Integer bucketSize		= 200;
	public Integer currBucketSize	= 0;
	
	public Sentence sentList[];
	public SimWrapper simWrapper;
	public List<Integer> summSentences;
	
	public ScoreL scoreL = null;
	public ScoreA scoreA = null;
	public Clustering clustering = null;
	HardClustering hClustering = null;
	
	public Summarize(){
		summSentences = new ArrayList<Integer>();
		bucketSize = 200;
		currBucketSize = 0;
		clustering = new Clustering(this);
		hClustering = new HardClustering(this);
		
		if( isSingleDone == false)
			singletonInit();
	}
	
	public static void singletonInit(){
		ontCreate = new OntCreate();
		aspects = ontCreate.run();
		IDF.loadIDF(Constants.IDF_DIR);
		isSingleDone = true;
	}

	
	public Double getDScore(){
		Double res = 0.0;
		return res;
	}
	
	public List<Sentence> doProcess() {
		List<Sentence> res = new ArrayList<Sentence>();

		Double prevScore = 0.0;
		
		scoreL = new ScoreL( this );
		scoreA = new ScoreA2( this );
		
		while (true) {
			Integer sentID = -1;
			Double score, maxScore = -1.0, maxPrevScore = 0.0, scoreDiff = 0.0;

			for (int sentId = 0; sentId < sentList.length; sentId++) {
				Sentence sent = sentList[sentId];

				if (sent.isSummSentence == true)
					continue;

				if (bucketSize != -1
						&& (currBucketSize + sent.wordList.length > bucketSize))
					continue;
				
				if( sent.wordList.length <= 0 )
					continue;

				sent.isSummSentence = true;
				summSentences.add(sentId); // mark examined

				Double csore = scoreL.getCScore();
				Double dscore = scoreA.getAScore();
				score = csore + tradeOff * dscore;
				scoreDiff = score - prevScore;
				scoreDiff = scoreDiff / Math.pow(sent.wordList.length, tradeR);
				System.err.println("::" + csore +  "\t" + dscore);
				System.err.println("=>"+sent.getSentString());
				System.err.println("**" + "f(S)=" + prevScore +  "\tf(S+"+sentId+")=" + score + "\tdiff="+scoreDiff);
				
				if (scoreDiff > maxScore) {
					sentID = sentId;
					maxScore = scoreDiff;
					maxPrevScore = score;
				}

				sent.isSummSentence = false;
				summSentences.remove(summSentences.size() - 1); // mark examined
			}
			
			if (sentID < 0) {
				break;
			}
			
			System.err.println("===>" + maxScore + " " + prevScore);
			Sentence sent = sentList[sentID];
			res.add(sent); // as a result
			summSentences.add(sentID); // mark examined
			sent.isSummSentence = true;
			currBucketSize = currBucketSize + sent.wordList.length;
			prevScore = maxPrevScore;
			
			System.err.println("res = " + sentID + " = "+ sentList[sentID].getSentString());
		}
		return res;
	}

	public List<Sentence> doSummarizeFromFile(String fileName){
		String fileContent = FileW.getFileContents(fileName);
		return doSummarizeFromText(fileContent);
	}
	
	public List<Sentence> doSummarizeFromText(String fileContent) {

		List<Sentence> res = new ArrayList<Sentence>();
		String sentArr[] = SentenceDetector.run(fileContent);

		// /
		sentList = new Sentence[sentArr.length];
		
		System.err.println("Sentence Construction-----");
		/**** Sentence Construction */
		int iSentID = 0;
		for (String sent : sentArr) {
			Sentence s = new Sentence(sent);
			s.setSentID(iSentID);
			sentList[iSentID] = s;
			iSentID++;
		}
		System.err.println("Sim Computation Started-----");

		/*** Similarity Computation ***/
		simWrapper = new SimWrapper(sentList, this);
		System.err.println("Sim Computation Ended-----");

		clustering.run();
		hClustering.run();
		
		/*** Run algorithm ***/
		return doProcess();
	}

}
