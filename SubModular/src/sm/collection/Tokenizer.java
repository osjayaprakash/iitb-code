/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sm.collection;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.Span;

/**
 *
 * @author biplab
 */
public class Tokenizer 
{
    opennlp.tools.tokenize.Tokenizer _tokenizer=null;
    InputStream _inputStream=null;
    TokenizerModel _tokenizerModel=null;
    public Tokenizer(String modelName) throws FileNotFoundException, IOException
    {
        _inputStream=new FileInputStream(modelName);
        _tokenizerModel=new TokenizerModel(_inputStream);
        _inputStream.close();
        _tokenizer=new TokenizerME(_tokenizerModel);
        
    }
    public String[] getTokens(String content)
    {
        return _tokenizer.tokenize(content);
    }
    public Span[] getTokensSpan(String content)
    {
        return _tokenizer.tokenizePos(content);
    }
    
    
}
