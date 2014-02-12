import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


public class PMI {
	//12,32,635
	Set<String> setString = new HashSet<String>();
	StringIntBijection sib = null;

	public void updateLine(String line){
		String[] arr =  line.split("[ \t]+");
		
//		for(String t:arr){
//			System.out.println(t);
//		}
		
		if( arr.length != 3 ){
			System.err.println(line);
			return;
		}
		setString.add(arr[0]);
		setString.add(arr[1]);
		//System.exit(-1);
	}
	
	public void readDoc(String fileName) {
		String text = "";
		int read, N = 1024 * 1024 * 30;
		char[] buffer = new char[N];

		try {
		    FileReader fr = new FileReader( new File(fileName));
		    BufferedReader br = new BufferedReader(fr);

		    while(true) {
		        read = br.read(buffer, 0, N);
		        text += new String(buffer, 0, read);
		        String[] lines = text.split("[\r\n]");
		        int noLines = lines.length;
		        for(int i=0;i<noLines-1;i++){
		        	updateLine(lines[i]);
		        }
		        text = lines[noLines-1];
		        if(read < N) {
		            break;
		        }
		    }
		} catch(Exception ex) {
		    ex.printStackTrace();
		}

		//sib.verify();
		return;
	}
	
	public static void  main(String args[]){
		PMI pmi = new PMI();
		Long startTime = System.currentTimeMillis();
		pmi.readDoc("/home/zerone/2gramstuff.final.sorted");
		//pmi.sib = new StringIntBijection(new Array)
		Long endTime = System.currentTimeMillis();
		System.out.println((endTime-startTime)/1000);
		System.out.println( pmi.setString.size() );
	}
}
