/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sm.collection;

import java.io.FileNotFoundException;
import java.io.IOException;

import sm.api.Util;
import uk.ac.shef.wit.simmetrics.similaritymetrics.AbstractStringMetric;

/**
 *
 * @author biplab
 */
public class MongeElkanMatch extends AbstractStringMetric
{
     Tokenizer tokenizer=null;
     AbstractStringMetric internalStringMetric=null;
     public MongeElkanMatch() throws FileNotFoundException, IOException
     {
         tokenizer=new Tokenizer(Util.modelToken);
         WordNetSimilarity wns=new WordNetSimilarity();
         this.internalStringMetric=wns;
     }
     public MongeElkanMatch(AbstractStringMetric internalStringMetric) throws FileNotFoundException, IOException
     {
         tokenizer=new Tokenizer(Util.modelToken);
         this.internalStringMetric=internalStringMetric;
     }
     public final float getSimilarity(final String string1, final String string2) {
        //split the strings into tokens for comparison
        String[] str1Tokens = tokenizer.getTokens(string1);
        String[] str2Tokens = tokenizer.getTokens(string2);
        
        float sumMatches = 0.0f;
        float maxFound;
        for (Object str1Token : str1Tokens) {
            maxFound = 0.0f;
            for (Object str2Token : str2Tokens) {
                final float found = internalStringMetric.getSimilarity((String) str1Token, (String) str2Token);
                if (found > maxFound) {
                    maxFound = found;
                }
            }
            sumMatches += maxFound;
        }
        return sumMatches / (float) str1Tokens.length;
    }

    @Override
    public String getShortDescriptionString() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getLongDescriptionString() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getSimilarityExplained(String string, String string1) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public float getSimilarityTimingEstimated(String string, String string1) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public float getUnNormalisedSimilarity(String string, String string1) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
