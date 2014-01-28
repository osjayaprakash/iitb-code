package sm.api;

import java.io.IOException;
import java.io.StringReader;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;


import java.io.File;


public class Alchemy {
	public static List<String> getKeywords(String textContent) throws IOException{
		String baseURL= "http://access.alchemyapi.com/calls/text/TextGetRankedKeywords?apikey=1cdd37aeecd01aaf728d80fb9d47da75c1f0fdbe&text=";
		@SuppressWarnings("deprecation")
		String url = baseURL + URLEncoder.encode(textContent) + 
					"&maxRetrieve="+5;
		HttpGet hg = new HttpGet();
		hg.sendGetRequest(url);
		String responseXML = hg.getResponseBody();
		return parseResponse(responseXML);
	}
	
	 public static Document loadXMLFromString(String xml) throws Exception
	    {
	        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	        DocumentBuilder builder = factory.newDocumentBuilder();
	        InputSource is = new InputSource(new StringReader(xml));
	        return builder.parse(is);
	    }
	
	public static List<String> parseResponse(String responseXML){
		List<String> res = new ArrayList<String>();
		//res.add(responseXML);
	    try {
	    	DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	    	DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	    	Document doc = loadXMLFromString(responseXML);
	 
	    	//optional, but recommended
	    	//read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
	    	doc.getDocumentElement().normalize();
	    	
	    	NodeList nList = doc.getElementsByTagName("keyword");	 
	    	for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;	 
					String text = eElement.getElementsByTagName("text").item(0).getTextContent();
					res.add( text );
				}
	    	}
	    } catch (Exception e) {
	    	e.printStackTrace();
	   }
	   return res;
	}
}
