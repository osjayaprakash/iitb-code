package loanprediction;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;


public class Correlation {

	public static String dir = "/home/zerone/kaggle/";
	public static String testfile = dir + "test.csv";
	public static String trainfile = dir + "train.csv";
	public static String outputfile = dir + "output.csv";
	public static String modelfile = dir + "M5P.model.trained";

	public void run() throws Exception {
		BufferedReader br = null;
		String line = null;
		br = new BufferedReader(new FileReader(new File(trainfile)));
		line = br.readLine();
		String[] attr1 = line.split(",");
		Double sX[] = new Double[attr1.length];
		Double sXY[] = new Double[attr1.length];
		Double sX2[] = new Double[attr1.length];
		Double sY = 0.0;
		Double sY2 = 0.0;
		
		for(int i=0;i< sX.length;i++){
			sX[i] = sXY[i] = sX2[i] = 0.0;
		}
		
		int NR = 0;
		while ((line = br.readLine()) != null) {
			String[] attr = line.split(",");
			Double Y = Double.parseDouble(attr[attr.length-1]);
			sY += Y;
			sY2 += Y*Y;
			for (int j = 0; j < attr.length; j++) {
				double d = 0.0;
				if (!attr[j].equals("NA")) {
					d = Double.parseDouble(attr[j]);					
				}else{
					attr[j] = "0.0";
				}
				Double X = Double.parseDouble(attr[j]);
				sX[j] += X;
				sX2[j] += X*X;
				sXY[j] += X*Y;
			}	
			NR ++;
		}
		br.close();
		
		
		for(int j=0;j<attr1.length;j++){
			Double ssx=sX2[j]-((sX[j]*sX[j])/NR); 
			Double ssy=sY2-((sY*sY)/NR); 
			Double ssxy = sXY[j] - ((sX[j]*sY)/NR); 
			Double r=ssxy/Math.sqrt(ssx*ssy); 
			System.out.println(j+","+r);
		}
	}
	
	public static void main(String a[]){
		Correlation c = new Correlation();
		try {
			c.run();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
