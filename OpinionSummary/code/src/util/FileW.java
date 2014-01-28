package util;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class FileW {
	public static String getFileContents(String fileName){
		   String content = null;
		   File file = new File(fileName);
		   try {
		       FileReader reader = new FileReader(file);
		       char[] chars = new char[(int) file.length()];
		       reader.read(chars);
		       content = new String(chars);
		       reader.close();
		   } catch (IOException e) {
		       e.printStackTrace();
		   }
		   return content;
		}
}
