package opsum;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;


class Tree {
	private List<Tree> Children = new ArrayList<Tree>();
	private Tree parent = null;
	private String nodeName = null;
	private Integer nodeNo = -1;
	private List<String> Keywords = new ArrayList<String>();

	public Tree() {
	}

	public void setName(String name) {
		nodeName = name;
	}

	public void setNumber(Integer no) {
		nodeNo = no;
	}

	public void setParent(Tree p) {
		parent = p;
	}

	public Tree getParent() {
		return parent;
	}

	public List<Tree> getChildren() {
		return Children;
	}

	public List<String> getKeywordList() {
		return Keywords;
	}

	public void addKeyword(String keyword) {
		Keywords.add(keyword);
	}

	public void addChild(Tree c) {
		Children.add(c);
	}

}

public class OntCreate {

	public Aspects[] run() {
		return runRandom();
	}

	public Aspects[] runRandom() {
		Aspects asp[] = new Aspects[Summarize.iAspectCnt];
		Double sumW = 0.0;
		Double sumB = 0.0;
		for (int i = 0; i < asp.length; i++) {
			asp[i] = new Aspects();
			Double b = Math.random();
			Double w = Math.random();
			asp[i].setBudget(b);
			asp[i].setWeight(w);
			sumW += w;
			sumB += b;
		}
		for (int i = 0; i < asp.length; i++) {
			asp[i].setBudget(asp[i].getBudget() / sumB);
			asp[i].setWeight(asp[i].getWeight() / sumW);
		}
		return asp;
	}

	public Tree loadOntologyTree() {
		BufferedReader br;
		String line = null;
		Map<Integer, Tree> map = new HashMap<Integer, Tree>();
		try {
			br = new BufferedReader(new FileReader( util.Constants.ONTOLOGY_FILE ));
			while ((line = br.readLine()) != null) {
				String arr[] = line.split("\t");
				Integer no = Integer.parseInt( arr[0] );
				String keywords[] = arr[1].split(",");
				
				Tree node = new Tree();
				node.setName( keywords[0] );
				node.setNumber( no );
				for(String kw:keywords){
					node.addKeyword(kw);
				}
				Tree parent = map.get( no%10 );
				node.setParent(parent);
				if(parent != null)
					parent.addChild(node);
				map.put(no, node); // add current node to hash
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map.get(0); // root
	}
}
