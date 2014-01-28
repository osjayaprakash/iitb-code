package opsum;

import util.IDF;


public class SimWrapper {
	Double simScore[][] = { null };
	Sentence[] sentList = { null };
	int simMethod = 0;

	public SimWrapper(Sentence[] sl, Summarize sum) {
		
		sentList = sl;
		simScore = new Double[sl.length][sl.length];
		try{
			calc();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public void calc() throws Exception {
		
		//recomputeIDF();
		
		if (simMethod == 0) {
			for (int i = 0; i < sentList.length; i++) {
				Sentence a = sentList[i];
				System.err.println( "\t>"+a.getSentString() );
				for (int j = 0; j < sentList.length; j++) {
					Sentence b = sentList[j];
					simScore[i][j] = a.scoreCosine(b);
					System.err.println( "\t\t+" + b.getSentString() );
					System.err.println( "\t\t+" + simScore[i][j]);
				}
			}
		} 
				
		allBudgetScores();
	}

	private void recomputeIDF() {
		IDF.recompute(sentList);
	}

	private void allBudgetScores() {
		for (int i = 0; i < sentList.length; i++) {
			Sentence a = sentList[i];
			a.budgetScore = 0.0;
			for (int j = 0; j < sentList.length; j++) {
				a.budgetScore += simScore[i][j];
			}
		}		
	}

	public Double getScore(int i, int j) {
		return simScore[i][j];
	}	
}