package sm.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;



public class HttpGet {
	private String responseBody = null;
	public static Map<String, Map<String, Double>> mssd = new HashMap<String, Map<String,Double>>();
	public HttpGet() {
		new Settings();
	}
	
	public Double getscore(String a, String b)throws IOException{
		Double res = 0.0;
		@SuppressWarnings("deprecation")
		String url = "http://swoogle.umbc.edu/StsService/GetStsSim?operation=api&phrase1="+URLEncoder.encode(b)+"&phrase2="+
				URLEncoder.encode(a);
		if( a.compareTo(b) >= 0 ){
			String t = a;
			a = b;
			b  = t;
		}
		if( mssd.containsKey(a) ){
			Map<String, Double> msd = mssd.get(a);
			if( msd.containsKey(b)) {
				return msd.get(b);
			}else{
				int i=0;
				while(sendGetRequest(url)!=200){
					System.err.println("retry #"+i);
					i++;
				}
				res = Double.parseDouble( responseBody );				
				msd.put(b, res);
			}
		}else{
			int i=0;
			while(sendGetRequest(url)!=200){
				System.err.println("retry #"+i);
				i++;
			}
			res = Double.parseDouble( responseBody );
			Map<String, Double> msd = new HashMap<String, Double>();
			msd.put(b, res);
			mssd.put(a, msd);
		}
		//System.err.println("getscore==>" + res);
		return res;
	}

	public HttpURLConnection getConnection(String url) throws IOException{
		try {
			URL u = new URL(url);
			HttpURLConnection uc = (HttpURLConnection) u.openConnection();
			uc.connect();
			return uc;
		} catch (IOException e) {
			throw e;
		}
	}

	public int sendGetRequest(String url) throws IOException
			 {

		int responseCode = 500;
		try {
			HttpURLConnection uc = getConnection(url);

			responseCode = uc.getResponseCode();

			if (200 == responseCode || 401 == responseCode
					|| 404 == responseCode || responseCode == 503) {
				BufferedReader rd = new BufferedReader(new InputStreamReader(
						responseCode == 200 ? uc.getInputStream()
								: uc.getErrorStream()));
				StringBuffer sb = new StringBuffer();
				String line;
				while ((line = rd.readLine()) != null) {
					sb.append(line);
				}
				rd.close();
				setResponseBody(sb.toString());
			}
		} catch (MalformedURLException ex) {
			throw new IOException(url + " is not valid");
		} catch (IOException ie) {
			throw new IOException("IO Exception " + ie.getMessage());
		}
		return responseCode;
	}


	public String getResponseBody() {
		return responseBody;
	}


	public void setResponseBody(String responseBody) {
		if (null != responseBody) {
			this.responseBody = responseBody;
		}
	}

}
