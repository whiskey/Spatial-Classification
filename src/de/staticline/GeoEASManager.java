package de.staticline;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

/**
 * Handles GeoEAS files for the needed tasks.
 * 
 * @author Carsten Witzke
 */
public class GeoEASManager{
	
	/**
	 * Converts GeoEAS files into weka's arff format. 
	 * @param dataFile the GeoEAS file to be converted into arff
	 */
	public void convertToARFF(File dataFile){
		try{
			//reading the file
			FileInputStream fis = new FileInputStream(dataFile);
			InputStreamReader in = new InputStreamReader(fis);
			BufferedReader br = new BufferedReader(in);
			//writing the new arff
			File arff = new File(dataFile.getAbsolutePath()+".arff");
			FileWriter fw = new FileWriter(arff, false);
			BufferedWriter bwriter = new BufferedWriter(fw);
			
			//some statistics
			int numAttributes = 0;
			int numDataRows = 0;
			//start the work
			String fileLine = br.readLine();
			if(fileLine != null){
				//first line = arff-relation
				bwriter.write("@relation \'"+fileLine+"\'\n\n");
				//FIXME:ignored 2nd line (# attributes); use this number!
				br.readLine();
				//find attributes and data
				StringTokenizer tokenizer;
				while((fileLine = br.readLine()) != null){
					//fitted for given files - no general optimization here!
					tokenizer = new StringTokenizer(fileLine, " ");
					switch (tokenizer.countTokens()) {
						case 0://found an empty line
							//line break to keep a similar file structure
							bwriter.write("\n");
							break;
						case 1://found an attribute
							bwriter.write("@attribute \'"+fileLine+"\' numeric\n");
							numAttributes++;
							break;
						case 2://here: found attribute with measurement (which is dropped)
							bwriter.write("@attribute \'"+tokenizer.nextToken()+"\' numeric\n");
							numAttributes++;
							break;
						default://data line
							if(numDataRows == 0){
								//first data found!
								bwriter.write("\n@data\n");
							}
							while(tokenizer.hasMoreTokens()){
								bwriter.write(tokenizer.nextToken());
								if(tokenizer.hasMoreTokens()){ 
									bwriter.write(","); 
								}
							}
							bwriter.write("\n");
							numDataRows++;
							break;
					}
				}
			}
			bwriter.close();
			//stats:
			System.out.println("attributes: "+numAttributes);
			System.out.println("data rows:  "+numDataRows);
		}catch(Exception exception){
			exception.printStackTrace();
		}
	}
}
