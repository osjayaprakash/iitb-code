package util; 

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SentiWordNet {
	
	public static SentiWordNet sentiwordnet = null;
	
	private Map<String, Double> dictionary;

	public SentiWordNet() throws IOException {
		// This is our main dictionary representation
		String pathToSWN = Constants.SENTI_FILE;
		dictionary = new HashMap<String, Double>();

		// From String to list of doubles.
		HashMap<String, HashMap<Integer, Double>> tempDictionary = new HashMap<String, HashMap<Integer, Double>>();

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

					// Calculate synset score as score = PosS - NegS
					Double synsetScore = Double.parseDouble(data[2])
							- Double.parseDouble(data[3]);

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
									new HashMap<Integer, Double>());
						}

						// Add synset link to synterm
						tempDictionary.get(synTerm).put(synTermRank,
								synsetScore);
					}
				}
			}

			// Go through all the terms.
			for (Map.Entry<String, HashMap<Integer, Double>> entry : tempDictionary
					.entrySet()) {
				String word = entry.getKey();
				Map<Integer, Double> synSetScoreMap = entry.getValue();

				// Calculate weighted average. Weigh the synsets according to
				// their rank.
				// Score= 1/2*first + 1/3*second + 1/4*third ..... etc.
				// Sum = 1/1 + 1/2 + 1/3 ...
				double score = 0.0;
				double sum = 0.0;
				for (Map.Entry<Integer, Double> setScore : synSetScoreMap
						.entrySet()) {
					score += setScore.getValue() / (double) setScore.getKey();
					sum += 1.0 / (double) setScore.getKey();
				}
				score /= sum;

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

	public Double extract(String word, String pos) {
		String entry = word + "#" + pos;
		if(dictionary.containsKey(entry))
			return dictionary.get(word + "#" + pos);
		return 0.0;
	}
	
	public static void main(String [] args) throws IOException {

		
		SentiWordNet sentiwordnet = new SentiWordNet();
		
		System.out.println("good#a "+sentiwordnet.extract("good", "a"));
		System.out.println("bad#a "+sentiwordnet.extract("bad", "a"));
		System.out.println("blue#a "+sentiwordnet.extract("blue", "a"));
		System.out.println("blue#n "+sentiwordnet.extract("blue", "n"));
	}

	public static Double SentiScore(String str) {
		try{
			if( sentiwordnet == null){
				sentiwordnet = new SentiWordNet();
			}
			
			Double subj		= 0.0;
			Double score	= 0.0;
			String[] tokens	=	Tokenize.runME(str);
			for(String tok: tokens){
				Double s = sentiwordnet.extract(tok, "a");
				s = Math.max(s, sentiwordnet.extract(tok, "n"));
				s = Math.max(s, sentiwordnet.extract(tok, "v"));
				s = Math.max(s, sentiwordnet.extract(tok, "r"));
				
				score += s;
				if(s != 0.0){
					subj += 1.0;
				}
			}
			Double res = 0.0;
			if( subj > 0.0 )
				res =  score / subj;
			return res;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return 0.0;
	}
}