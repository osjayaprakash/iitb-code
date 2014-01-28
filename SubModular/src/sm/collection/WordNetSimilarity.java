/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sm.collection;

import edu.cmu.lti.lexical_db.ILexicalDatabase;
import edu.cmu.lti.lexical_db.NictWordNet;
import edu.cmu.lti.ws4j.RelatednessCalculator;
import edu.cmu.lti.ws4j.impl.HirstStOnge;
import edu.cmu.lti.ws4j.impl.LeacockChodorow;
import edu.cmu.lti.ws4j.impl.Lin;
import edu.cmu.lti.ws4j.impl.Resnik;
import edu.cmu.lti.ws4j.impl.WuPalmer;
import uk.ac.shef.wit.simmetrics.similaritymetrics.AbstractStringMetric;
import uk.ac.shef.wit.simmetrics.similaritymetrics.SmithWaterman;

/**
 *
 * @author biplab
 */
public class WordNetSimilarity extends AbstractStringMetric
{

    ILexicalDatabase db=null;
    RelatednessCalculator rcs=null;
    AbstractStringMetric backupMetric=null;
    public WordNetSimilarity()
    {
      db = new NictWordNet();
      rcs=new WuPalmer(db);
      backupMetric=new SmithWaterman();
      
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
    public float getSimilarity(String string, String string1) {
        float x=0;
        if(string1.equalsIgnoreCase(string))
        {
            return 1.0f;
        }
        else
        {
            x=(float)rcs.calcRelatednessOfWords(string1, string);
            x+=backupMetric.getSimilarity(string, string1);
        }
        return x/2.0f;
    }

    @Override
    public float getUnNormalisedSimilarity(String string, String string1) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
