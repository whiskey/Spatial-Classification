package de.staticline;

import java.io.File;

import de.staticline.data.GeoEASManager;



/**
 * Quick and dirty testing and deveopent class
 * 
 * @author Carsten Witzke
 */
public class Workbench {

	public static void main(String[] args) {
		String projectRootPath = System.getProperty("user.dir");
		File lake = new File(projectRootPath+"/data/Lake.dat");//FIXME: DEBUG
		//data converter
		GeoEASManager geom = new GeoEASManager();
		geom.convertToARFF(lake);

		//		System.out.println(lake.getAbsolutePath());
		//		if(lake.exists()){
		//			DataAnalyzer da = new DataAnalyzer(lake.getAbsolutePath());
		//			for(int c=0; c<8;c++){
		//				da.trainClassifiers(c, false);
		//			}
		//		}
	}

}
