package sm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import sm.api.HttpGet;
import sm.api.SentSim;
import sm.api.Util;
import sm.collection.MongeElkanMatch;

import sts.*;

public class simWrapper {
	Double sim[][] = { null };
	Sentence[] sentList = { null };
	int simMethod = 0;
	Summarize sum;

	public simWrapper(Sentence[] sl, Summarize sum) {
		sentList = sl;
		sim = new Double[sl.length][sl.length];
		simMethod = sum.isim;
		this.sum = sum;
		try{
			calc();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public void calc() throws Exception {

		if (simMethod == 0) {
			for (int i = 0; i < sentList.length; i++) {
				Sentence a = sentList[i];
				for (int j = 0; j < sentList.length; j++) {
					Sentence b = sentList[j];
					sim[i][j] = a.scoreCosine(b);
				}
			}
		} else if (simMethod == 1) {
			Scanner scan = new Scanner(new FileInputStream(sum.inFile+".sim.1"));

			for (int i = 0; i < sentList.length; i++) {
				Sentence a = sentList[i];
				for (int j = 0; j < sentList.length; j++) {
					Sentence b = sentList[j];
					int ai = scan.nextInt();
					int bj = scan.nextInt();
					Double score = scan.nextDouble();
					sim[ai][bj] = score;
				}
			}
			
			scan.close();
		} else if (simMethod == 2) {
			Scanner scan = new Scanner(new FileInputStream(sum.inFile+".sim.2"));

			for (int i = 0; i < sentList.length; i++) {
				Sentence a = sentList[i];
				for (int j = 0; j < sentList.length; j++) {
					Sentence b = sentList[j];
					int ai = scan.nextInt();
					int bj = scan.nextInt();
					Double score = scan.nextDouble();
					sim[ai][bj] = score;
				}
			}
			
			scan.close();
		} else if (simMethod == 4) {
			try{
			MongeElkanMatch match=new MongeElkanMatch();
			for (int i = 0; i < sentList.length; i++) {
				Sentence a = sentList[i];
				for (int j = 0; j < sentList.length; j++) {
					Sentence b = sentList[j];
					Float x = 0.0f;		
					x=match.getSimilarity(b.getSentString(), a.getSentString());
					sim[i][j] = x.doubleValue();
					System.err.println("++"+j+" " + sentList.length+" " + x);
				}
				Runtime.getRuntime().gc();
				System.err.println(""+i+" " + sentList.length);
			}
			}catch(Exception e){
				e.printStackTrace();
			}
		} else if (simMethod == 5) {
			try{
				for (int i = 0; i < sentList.length; i++) {
					Sentence a = sentList[i];
					for (int j = 0; j < sentList.length; j++) {
						Sentence b = sentList[j];
						SentSim ss = new SentSim();
						sim[i][j] = ss.getScore(a.wordList, b.wordList);
						//System.err.println(""+i+" " + j + " " + sim[i][j]);

					}
					Runtime.getRuntime().gc();
					System.err.println(""+i+" " + sentList.length);
				}				
			}catch(Exception e){
				e.printStackTrace();
			}
		}


	}

	public Double getScore(int i, int j) {
		return sim[i][j];
	}
	
	public static void c1(Sentence sentList [], FileWriter w){
		try{
		MongeElkanMatch match=new MongeElkanMatch();
		for (int i = 0; i < sentList.length; i++) {
			Sentence a = sentList[i];
			for (int j = 0; j < sentList.length; j++) {
				Sentence b = sentList[j];
				Float score=match.getSimilarity(b.getSentString(), a.getSentString());
				w.write(""+i+"\t"+j+"\t"+score+"\n");
			}
			Runtime.getRuntime().gc();
			System.err.println(""+i+" " + sentList.length);
		}
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}

	public static void c2(Sentence sentList [], FileWriter w){
		try{
			for (int i = 0; i < sentList.length; i++) {
				Sentence a = sentList[i];
				for (int j = 0; j < sentList.length; j++) {
					Sentence b = sentList[j];
					SentSim ss = new SentSim();
					Double score = ss.getScore(a.wordList, b.wordList);
					//System.err.println(""+i+" " + j + " " + sim[i][j]);
					w.write(""+i+"\t"+j+"\t"+score+"\n");
				}
				Runtime.getRuntime().gc();
				System.err.println(""+i+" " + sentList.length);
			}				
		}catch(Exception e){
			e.printStackTrace();
		}		
	}

	public static void c3(Sentence sentList [], FileWriter w){
		try{
		for (int i = 0; i < sentList.length; i++) {
			Sentence a = sentList[i];
			for (int j = 0; j < sentList.length; j++) {
				Sentence b = sentList[j];
				HttpGet hg = new HttpGet();
				Double score= hg.getscore(a.getSentString(), b.getSentString());
				w.write(""+i+"\t"+j+"\t"+score+"\n");
				if(j%100 == 0)
					System.err.println(""+i+" " + j + " " + score);
			}
			System.err.println(""+i+" " + sentList.length);
		}
		}catch(Exception e){
			e.printStackTrace();
		}		
	}

	public static void c4(Sentence sentList [], FileWriter w){
		SemanticTextualSimilarity sts = new SemanticTextualSimilarity();
		try{
		for (int i = 0; i < sentList.length; i++) {
			String arr [] = new String[2];
			arr[0] = sentList[i].getSentString();
			for (int j = 0; j < sentList.length; j++) {
				arr[1] = sentList[j].getSentString();
				Double score = sts.generateSimilarityScore(arr); 
				w.write(""+i+"\t"+j+"\t"+score+"\n");
				if(j%100 == 0)
					System.err.println(""+i+" " + j + " " + score);
			}
			System.err.println(""+i+" " + sentList.length);
		}
		}catch(Exception e){
			e.printStackTrace();
		}		
	}

	
	public static void main(String a[]){
		
		String fileContent = Util.getFileContents(a[1]);
		String sentArr[] = Util.SentenceDetector(fileContent);
		
		/// 
		Sentence sentList [] = new Sentence[ sentArr.length ];
		///
		
		
		/**** Sentence Construction */
		
		int iSentID = 0;
		for(String sent : sentArr){
			//System.err.println(sent);
			//if(sent.length()<5)
				//continue;
			Sentence s =  new Sentence( sent );
			s.setSentID(iSentID);
			sentList[iSentID] = s ;
			iSentID ++;
		}
		   File file = new File(a[1]+".sim"+"."+a[0]);
		   try {
		       FileWriter writer = new FileWriter(file);
				if( a[0].equals("1") ){
					c1( sentList, writer);
				}else if( a[0].equals("2") ){
					c2( sentList, writer);					
				}else if( a[0].equals("3") ){
					c3( sentList, writer);					
				}else if( a[0].equals("4") ){
					c4( sentList, writer);					
				}
		       writer.close();
		   } catch (IOException e) {
		       e.printStackTrace();
		   }
		
	}
}
