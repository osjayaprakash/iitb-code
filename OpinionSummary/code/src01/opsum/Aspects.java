package opsum;

import java.util.Map;

public class Aspects {
	public String sText;
	public String sDesc;
	public Map<String, Double> coOccWords;
	public Double dWeight;
	public Double dBudget;
	

	public Aspects(){
		System.err.println("Aspect created");
	}
	
	/**getter and setter **/
	
	public void setWeight(Double w){
		dWeight = w;
	}

	public void setBudget(Double b){
		dBudget = b;
	}
	
	public Double getWeight(){
		return dWeight;
	}

	public Double getBudget(){
		return dBudget;
	}
	
}
