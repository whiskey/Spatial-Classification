package de.staticline;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

public class GeoEASManager{
	public void convertToARFF(File dataFile){
		try{
			//reading the file
			FileInputStream fis = new FileInputStream(dataFile);
			InputStreamReader in = new InputStreamReader(fis);
			BufferedReader br = new BufferedReader(in);
			//writing the new arff
			File arff = new File(dataFile.getAbsolutePath()+".arff");
			FileWriter fw = new FileWriter(arff, false); //overwrites existing files!
			BufferedWriter bwriter = new BufferedWriter(fw);
			
			//some statistics
			int numAttributes = 0;
			int numDataRows = 0;
			//start the work
			String fileLine = br.readLine();
			if(fileLine != null){
				//first line = arff-relation
				bwriter.write("@relation \'"+fileLine+"\'\n");
				
				//find attributes and data
				StringTokenizer tokenizer;
				while((fileLine = br.readLine()) != null){
					//fitted for given files - no general optimization here!
					tokenizer = new StringTokenizer(fileLine, " ");
					switch (tokenizer.countTokens()) {
						case 0://found an empty line
							bwriter.write("\n");
							break;
						case 1://found an attribute
							bwriter.write("@attribute \'"+fileLine+"\' numeric\n");
							numAttributes++;
							break;
						default://data line
							
							numDataRows++;
							break;
					}
				}
			}
			bwriter.close();
		}catch(Exception exception){
			exception.printStackTrace();
		}
	}
}
