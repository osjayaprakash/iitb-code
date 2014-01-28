package sm.api;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;



public class GImage {
	public static List<String> getImageList(String textContent) throws IOException, ParseException{
		String baseURL= "http://www.google.com/uds/GimageSearch?rsz=small&hl=en&gss=.19&sig=cf717ce13f86fb2ebeed8f0046aba6ef&context=0&key=notsupplied&v=1.0&q=";
		String url = baseURL + URLEncoder.encode(textContent);
		HttpGet hg = new HttpGet();
		hg.sendGetRequest(url);
		String responseJSON = hg.getResponseBody();
		List<String> res = parseResponse(responseJSON);
		//res.add(0, url);
		return res;
	}

	private static List<String> parseResponse(String responseJSON) throws ParseException {
		List<String> res = new ArrayList<String>();
		JSONParser parser = new JSONParser();
		JSONObject jsonObject = (JSONObject) parser.parse( responseJSON );
		JSONObject responseData = (JSONObject) jsonObject.get("responseData");
		JSONArray results = (JSONArray) responseData.get("results");
		for(Object result: results){
			JSONObject jobj = (JSONObject) result;
			String url = (String) jobj.get("url");
			res.add(url);
		}
		return res;
	}
}
