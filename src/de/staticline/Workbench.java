package de.staticline;

import java.io.File;

import de.staticline.analyze.DataAnalyzer;
import de.staticline.analyze.EClassifiers;
import de.staticline.data.DataFileFilter;

/**
 * Quick and dirty testing and deveopent class
 * 
 * @author Carsten Witzke
 */
public class Workbench {
	private static final String ROOT_PATH = System.getProperty("user.dir");
	private static final String DATA_PATH = ROOT_PATH + "/data/raw";
	private static final String ARFF_PATH = ROOT_PATH + "/data/arff"; 
	
	public static void main(final String[] args) {
		//--- convert data files to arff
		//final GeoEASManager geom = new GeoEASManager();
		
		//FIXME: remove next 2 lines after debug
		//final File file = new File(ROOT_PATH+"/data/raw/Lake.dat");
		//geom.convertToARFF(file);
		
		
		//--- anylyze data
		for(final File f : new File(ARFF_PATH).listFiles(new DataFileFilter("arff"))){
			System.out.println(f.getAbsolutePath());
			final DataAnalyzer da = new DataAnalyzer(f.getAbsolutePath());
			for(final EClassifiers c : EClassifiers.values()){
				da.trainClassifiers(c, false);
			}
		}
	}
}
