package loanprediction;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import weka.classifiers.Classifier;
import weka.classifiers.functions.LinearRegression;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.M5P;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

public class PredictDefault {

	/**
	 * @param args
	 * @throws Exception
	 */
	
	public static String dir = "/home/zerone/kaggle/";
	public static String ver = "02";
	public static String testfile = dir + "mytest"+ver+".csv";
	public static String trainfile = dir + "mytrain"+ver+".csv";
	public static String outputfile = dir + "myoutput"+ver+".csv";
	public static String modelfile = dir + "myM5P.model.trained"+ver;
	
	public static int VectorSize = 28;
	public static int trainingSize = 105471;
	public static int classIndex = 27;
	
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

		FastVector fvWekaAttributes = new FastVector(VectorSize);
		// fvWekaAttributes.addElement(ClassAttribute);
		for (int j = 0; j < VectorSize; j++) {
			fvWekaAttributes.addElement(new Attribute("" + (j + 1)));
		}
		
		Instances isTrainingSet = new Instances("Rel", fvWekaAttributes, trainingSize);
		isTrainingSet.setClassIndex(classIndex);

		BufferedReader br = null;
		String line = "";
		Classifier sl = null;

		if (!new File(modelfile).exists()) {
			br = new BufferedReader(new FileReader(new File(trainfile)));
			br.readLine();
			while ((line = br.readLine()) != null) {
				String[] attr = line.split(",");
				Instance iExample = new Instance(VectorSize);
				for (int j = 1; j < attr.length-1; j++) {
					double d = 0.0;
					if (!attr[j].equals("NA")) {
						d = Double.parseDouble(attr[j]);
					}
					iExample.setValue(
							(Attribute) fvWekaAttributes.elementAt(j-1), d);
				}
				Double d = Double.parseDouble( attr[attr.length-1] );
				d = Math.min(10.0, d);
				iExample.setValue( (Attribute) fvWekaAttributes.elementAt(classIndex), d);				
				
				isTrainingSet.add(iExample);
			}
			br.close();
			//sl = (Classifier) new LinearRegression();
			//sl = (Classifier) new J48();
			sl = (Classifier) new M5P();
			
			sl.buildClassifier(isTrainingSet);
			FileOutputStream fout = new FileOutputStream(modelfile);
			ObjectOutputStream oos = new ObjectOutputStream(fout);
			oos.writeObject(sl);
			oos.close();
		} else {
			FileInputStream fout = new FileInputStream(modelfile);
			ObjectInputStream oos = new ObjectInputStream(fout);
			sl = (Classifier) oos.readObject();
			oos.close();
		}

		br = new BufferedReader(new FileReader(new File(testfile)));
		line = "";
		// int count = 0;
		br.readLine();
		PrintStream ps = new PrintStream( new FileOutputStream(outputfile) ); 
		System.setOut( ps );
		System.out.println("id,loss");
		while ((line = br.readLine()) != null) {
			String[] attr = line.split(",");
			Instance iExample = new Instance(VectorSize);
			iExample.setDataset(isTrainingSet);
			for (int j = 1; j < attr.length; j++) {
				double d = 0.0;
				if (!attr[j].equals("NA")) {
					d = Double.parseDouble(attr[j]);
				}
				iExample.setValue(
						(Attribute) fvWekaAttributes.elementAt(j-1), d);
			}
			String id = attr[0];
			Double res = sl.classifyInstance(iExample);
			res = Math.min(res, 10.0);
			res = Math.max(res, 0.0);
			System.out.println(id + "," + res.intValue() );
		}
		br.close();
	}

}