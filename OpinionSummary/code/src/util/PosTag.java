package util;

import java.io.FileInputStream;
import java.io.InputStream;

import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import sun.security.jca.GetInstance;

public class PosTag {

	private static POSModel model = null;

	private static POSModel getInstance() {
		if (model == null) {
			InputStream modelIn = null;
			try {
				modelIn = new FileInputStream(util.Constants.POSTAG_FILE);
				model = new POSModel(modelIn);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (modelIn != null) {
					try {
						modelIn.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		return model;

	}

	public static String[] tag(String in[]) {
		POSModel model = PosTag.getInstance();
		POSTaggerME tagger = new POSTaggerME(model);
		String tags[] = tagger.tag(in);
		return tags;
	}

}
