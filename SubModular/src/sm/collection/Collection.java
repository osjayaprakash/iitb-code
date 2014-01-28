/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sm.collection;

import java.io.FileNotFoundException;
import java.io.IOException;



/**
 *
 * @author biplab
 */
public class Collection {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException, IOException 
    {
        // TODO code application logic here
         //AbstractStringMetric metric = new Levenshtein();
         //AbstractStringMetric mematch=new MongeElkanMatch(metric);
         //System.out.println(mematch.getSimilarity("",""));
         MongeElkanMatch match=new MongeElkanMatch();
         float x=match.getSimilarity("I am runnig away","i got far from there");
         System.out.println(x);
        
    }
}
