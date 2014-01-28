package sm;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import sm.api.PorterStemmer;
import sm.api.Util;

public class Sentence {
	private int sentID;
	private String sent;
	public  List<String> wordList;
	public  Map<String, Double> wordMap;
	private Double covScore, divScore;
	private int clusterID;
	private int summID;
	public boolean isSummSentence = false;
	
	
	public Sentence(){
		sentID = -1;
		sent = null;
		wordList = null;
		wordMap = null;
		covScore = divScore = null;
		clusterID = summID = -1;
	}

	public Sentence(String str){
		sentID = -1;
		sent = null;
		wordList = null;
		wordMap = null;
		covScore = divScore = null;
		clusterID = summID = -1;
		setSentence(str);
	}
	
	public void setSentID(int id){ sentID = id; }
	public int getSentID(){ return sentID; }
	public void setSummID(int id){ summID = id; }
	public int getSummID(){ return summID; }
	public void setClusterID(int id){ clusterID = id; }
	public int getClusterID(){ return clusterID; }
	public void setDiversityScore(Double score){ divScore = score; }
	public Double getDiversityScore(){ return divScore; }
	public void setCoverageScore(Double score){ covScore = score; }
	public Double getCoverageScore(){ return covScore; }
	
	public String getSentString(){
		return sent;
	}

	public void setSentence(String str){
		this.sent = str;
		this.wordList = tokenize(this.sent);
		wordMap = new HashMap<String, Double>();
		for(String s: this.wordList){
			if (wordMap.containsKey(s) == false){
				wordMap.put(s, 0.0);
			}
			Double frq = wordMap.get(s);
			wordMap.put(s, frq+1.0);
		}
	}
	
	
	private List<String> tokenize(String sent) {
		List<String> res = new ArrayList<String>();
		String wordArr[ ] = sent.split(" ");
		for(int i=0;i<wordArr.length;i++){
			String w = wordArr[i].toLowerCase();
			if( Util.usePorterStem == true ){
				PorterStemmer st = new PorterStemmer();
				char cw[] = w.toCharArray();
				st.add(cw, cw.length);
				st.stem();
				res.add( st.toString() );
			}else if( Util.useWordnetStem == true ){
				res.add( Util.getWordNetStem(w) );
			}else
				res.add( w );
		}
		Collections.sort(res);
		return res;
	}
	
	public List<String> analyze(List<String> wordList) {
		return null;
	}
	
	public Double score(Sentence sent){
		//return scoreTFIDF(sent);
		return scoreCosine(sent);
	}

	public Double scoreCosine(Sentence sent){
		Double res  = 0.0;
		Double res1 = 0.0, res2 = 0.0;
		Set<String> ks = new TreeSet<String>();
		ks.addAll(this.wordMap.keySet());
		ks.addAll(sent.wordMap.keySet());
		for(String word : ks){
			res += this.getTF(word) * sent.getTF(word) * this.getIDF(word) * this.getIDF(word);
			res1 += this.getTF(word) *  this.getIDF(word) ;
			res2 += sent.getTF(word) * this.getIDF(word) ;
		}
		Double result = res / (Math.sqrt(res1) + Math.sqrt(res2));
		return result;
	}

	private Double scoreTFIDF(Sentence sent){
		Double res = 0.0;
		res = findIntersection( this.wordList, sent.wordList );
		return res / Math.max(this.wordList.size(), sent.wordList.size()); //TODO
	}

	private Double findIntersection(List<String> wordList1, List<String> wordList2  ){
		Double res = 0.0;		

		for(int i=0, j=0; i<wordList1.size() && j<wordList2.size();){
			String a = wordList1.get(i).toLowerCase();
			String b = wordList2.get(j).toLowerCase();
			if( a.equals(b) ){
				res += getIDF(b)*getIDF(a)*getTF(a)*getTF(b);
				i++; 
				j++;
			}else if( a.compareTo( b ) < 0 ){
				i++;
			}else{
				j++;
			}
		}
		return res;
	}
	
	public Double getTF(String word){
		if( wordMap.containsKey(word) )
			return wordMap.get(word);
		else
			return 0.0;
	}
 
	public Double getIDF(String word){
		return 1.0;
	}
	
	public String printDetails(){
		String res = "[ ";
		res += "ClusterID : " + getClusterID() +", ";
		res += "Coverage : " + getCoverageScore() +", ";
		res += "Diversity : " + getDiversityScore() +", ";
		res += "Total : " + (getDiversityScore() + getCoverageScore()) +", ";
		res += "] ";
		return res;
	}
}
