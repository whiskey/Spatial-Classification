package de.staticline;

import java.io.File;



/**
 * Quick and dirty testing and deveopent class
 * 
 * @author Carsten Witzke
 */
public class Workbench {

	public static void main(String[] args) {
		String projectRootPath = System.getProperty("user.dir");
		/*
		//data converter
		GeoEASManager geom = new GeoEASManager();
		try{
			geom.convertToARFF(lake);
		}catch(Exception exception){
			exception.printStackTrace();
		}
		*/
		File lake = new File(projectRootPath+"/data/Soils.dat.arff");
		System.out.println(lake.getAbsolutePath());
		if(lake.exists()){
			DataAnalyzer da = new DataAnalyzer(lake.getAbsolutePath());
			for(int c=0; c<9;c++){
				da.trainClassifiers(c);
			}
		}
	}

}
