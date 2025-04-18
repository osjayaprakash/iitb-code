package opsum;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Aspect {
	private Double dWeight;
	private Double dBudget;	
	private List<Aspect> Children = new ArrayList<Aspect>();
	private Aspect parent = null;
	private String nodeName = null;
	private Integer nodeNo = -1;
	private List<String> Keywords = new ArrayList<String>();
	private Integer level = -1;
	
	public Double d_Budget = 0.0, d_Score = 0.0, d_MXScore=0.0;

	public Aspect() {
	}

	/**getter and setter **/
	
	public void setLevel(Integer l){
		level = l;
	}
	
	public Integer getLevel(){
		return level ;
	}

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
	

	public void setName(String name) {
		nodeName = name;
	}

	public void setNumber(Integer no) {
		nodeNo = no;
	}

	public void setParent(Aspect p) {
		parent = p;
	}

	public Aspect getParent() {
		return parent;
	}

	public List<Aspect> getChildren() {
		return Children;
	}

	public List<String> getKeywordList() {
		return Keywords;
	}

	public void addKeyword(String keyword) {
		Keywords.add(keyword);
	}

	public void addChild(Aspect c) {
		Children.add(c);
	}

	public String getName() {
		return nodeName;
	}	
	
	public String toString() {
		String res = "";
		res += ", Name:" + getName();
		return res;
	}
	
	public void print(String prefix){
		Main.print( prefix );
		Main.print( nodeName + ", w=" + getWeight() + ", b=" + getBudget());
		Main.print("\n");
		
		prefix = prefix + "\t";
		for(Aspect asp: getChildren()){
			asp.print( prefix);
		}
	}
	
	public void printOk(String prefix){
		System.out.print( prefix );
		System.out.print( nodeName + ", w=" + getWeight() + ", b=" + d_Budget+", bud=" + d_Budget+", mscore=" + d_MXScore +", score=" + d_Score);
		System.out.print("\n");
		
		prefix = prefix + "\t";
		for(Aspect asp: getChildren()){
			asp.printOk( prefix);
		}
	}
	
}
