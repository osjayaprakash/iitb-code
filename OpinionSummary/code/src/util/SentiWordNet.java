package util; 

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SentiWordNet {
	
	public static SentiWordNet sentiwordnet = null;
	
	private Map<String, Double[]> dictionary;

	public SentiWordNet() throws IOException {
		// This is our main dictionary representation
		String pathToSWN = Constants.SENTI_FILE;
		dictionary = new HashMap<String, Double[]>();

		// From String to list of doubles.
		HashMap<String, HashMap<Integer, Double[]>> tempDictionary = new HashMap<String, HashMap<Integer, Double[]>>();

		BufferedReader csv = null;
		try {
			csv = new BufferedReader(new FileReader(pathToSWN));
			int lineNumber = 0;

			String line;
			while ((line = csv.readLine()) != null) {
				lineNumber++;

				// If it's a comment, skip this line.
				if (!line.trim().startsWith("#")) {
					// We use tab separation
					String[] data = line.split("\t");
					String wordTypeMarker = data[0];

					// Example line:
					// POS ID PosS NegS SynsetTerm#sensenumber Desc
					// a 00009618 0.5 0.25 spartan#4 austere#3 ascetical#2
					// ascetic#2 practicing great self-denial;...etc

					// Is it a valid line? Otherwise, through exception.
					if (data.length != 6) {
						throw new IllegalArgumentException(
								"Incorrect tabulation format in file, line: "
										+ lineNumber);
					}
					
					Double synsetScore[] = new Double[3];
					synsetScore[0] = Double.parseDouble(data[2]);
					synsetScore[1] = Double.parseDouble(data[3]);
					synsetScore[2] = 1 - (synsetScore[0]+synsetScore[1]);

					// Get all Synset terms
					String[] synTermsSplit = data[4].split(" ");

					// Go through all terms of current synset.
					for (String synTermSplit : synTermsSplit) {
						// Get synterm and synterm rank
						String[] synTermAndRank = synTermSplit.split("#");
						String synTerm = synTermAndRank[0] + "#"
								+ wordTypeMarker;

						int synTermRank = Integer.parseInt(synTermAndRank[1]);
						// What we get here is a map of the type:
						// term -> {score of synset#1, score of synset#2...}

						// Add map to term if it doesn't have one
						if (!tempDictionary.containsKey(synTerm)) {
							tempDictionary.put(synTerm,
									new HashMap<Integer, Double[]>());
						}

						// Add synset link to synterm
						tempDictionary.get(synTerm).put(synTermRank,
								synsetScore);
					}
				}
			}

			// Go through all the terms.
			for (Map.Entry<String, HashMap<Integer, Double[]>> entry : tempDictionary
					.entrySet()) {
				String word = entry.getKey();
				Map<Integer, Double[]> synSetScoreMap = entry.getValue();

				// Calculate weighted average. Weigh the synsets according to
				// their rank.
				// Score= 1/2*first + 1/3*second + 1/4*third ..... etc.
				// Sum = 1/1 + 1/2 + 1/3 ...
				Double score[] = {0.0, 0.0, 0.0};
				Double sum = 0.0;
				for (Map.Entry<Integer, Double[]> setScore : synSetScoreMap
						.entrySet()) {
					score[0] += setScore.getValue()[0] / (double) setScore.getKey();
					score[1] += setScore.getValue()[1] / (double) setScore.getKey();
					score[2] += setScore.getValue()[2] / (double) setScore.getKey();
					sum += 1.0 / (double) setScore.getKey();
				}
				score[0] /= sum;
				score[1] /= sum;
				score[2] /= sum;

				dictionary.put(word, score);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (csv != null) {
				csv.close();
			}
		}
	}

	public Double[] extract(String word, String pos) {
		String entry = word + "#" + pos;
		if(dictionary.containsKey(entry))
			return dictionary.get(word + "#" + pos);
		else{
			Double defaultScore[] = {0.0, 0.0, 1.0};
			return defaultScore;
		}
	}
	
	public static void main(String [] args) throws IOException {

		
		SentiWordNet sentiwordnet = new SentiWordNet();
		
		Double a[] = sentiwordnet.extract("good", "r");
		System.out.println(" ,"+a[0]+" ,"+a[1]+" ,"+a[2]);
		System.out.println("good#a "+sentiwordnet.extract("good", "a"));

		System.out.println("bad#a "+sentiwordnet.extract("bad", "a"));
		System.out.println("blue#a "+sentiwordnet.extract("blue", "a"));
		System.out.println("blue#n "+sentiwordnet.extract("blue", "n"));
		
		Double res = SubjSentiScore("i will win in a good way.");
		System.out.println(res);
	}

	public static Double SubjSentiScore(String str) {
		try{
			if( sentiwordnet == null){
				sentiwordnet = new SentiWordNet();
			}
			
			Double subS		= 0.0;
			String[] tokens	=	Tokenize.runME(str);
			String[] tags = PosTag.tag(tokens);
			
			int i=0;
			
			for(String tag: tags){
				Double score[] = {0.0, 0.0, 0.0};
				if(tag.startsWith("JJ")){
					score = sentiwordnet.extract(tokens[i], "a");
				}else if(tag.startsWith("V")){
					score = sentiwordnet.extract(tokens[i], "v");
				}else if(tag.startsWith("N")){
					score = sentiwordnet.extract(tokens[i], "n");
				}else if(tag.startsWith("R")){
					score = sentiwordnet.extract(tokens[i], "r");
				}
				subS += (score[0] + score[1]); 
				i++;
			}
			
			return subS;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return 0.0;
	}
}