package opsum;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class HardClustering {
	Summarize summ = null;
	
	public HardClustering(Summarize sum){
		summ	=	sum; 
	}
	
	public void run(){
		List<Aspect> lasp = new ArrayList<Aspect>();
		Stack<Aspect> stack = new Stack<Aspect>();
		stack.push(summ.aspectRoot);
		while(stack.empty() == false){
			Aspect node = stack.peek();
			stack.pop();
			lasp.add(node);
			for( Aspect asp: node.getChildren() )
				stack.push(asp);
		}
		
		/*
		for(Aspect asp:lasp){
			Main.print( asp.getName() +"-->");
				for( String w: asp.getKeywordList() )
					Main.print( w+"," );
			Main.println("");
		}
		*/
		
		for(int i=0;i<summ.sentList.length;i++)
			clusterSentenceRandom(i, lasp);
	}

	public void clusterSentenceRandom(int sentID, List<Aspect> lasp){
		Sentence sent = summ.sentList[sentID]; // sent
		sent.aspect = summ.aspectRoot;
		Double maxScore = 0.0;
		for( Aspect asp : lasp ){
			Double score = getMatch( sent, asp.getKeywordList() );
			if(score >= maxScore){
				sent.aspect = asp;
				maxScore = score;
			}
		}
		/*
			Main.println( sent.getSentString() +"==>\t{" + sent.aspect.getName() +"}={"+ maxScore  +"}"  );
		*/
	}
	
	public Double getMatch( Sentence sent , List<String> keywordList ){
		Double res = 0.0;
		for(String w: keywordList){
			res += sent.getTF(w);
		}
		return res;
	}
}
