package de.staticline.data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Handles GeoEAS files for the needed tasks.
 * 
 * @author Carsten Witzke
 */
public class GeoEASManager{
	private static Logger logger;
	private static final String ROOT_PATH = System.getProperty("user.dir");
	private static final String DATA_PATH = ROOT_PATH + "/data/raw";
	private static final File dataFolder = new File(DATA_PATH);
	
	public GeoEASManager(){
		//setup logger
		try {
			final Handler fh = new FileHandler("logfile.txt");
			fh.setFormatter(new SimpleFormatter());
			GeoEASManager.logger = Logger.getLogger("de.staticline.spatial");
			GeoEASManager.logger.addHandler(fh);
			GeoEASManager.logger.setLevel(Level.ALL);
		} catch (final SecurityException exception) {
			exception.printStackTrace();
		} catch (final IOException exception) {
			exception.printStackTrace();
		}
	}
	
	/**
	 * Converts all files with the default extension (<code>.dat</code>)
	 * to their ARFF equivalents.
	 */
	public final void convertAllFilesToARFF(){
		if(dataFolder.isDirectory()){
			for(final File file : dataFolder.listFiles(new DataFileFilter())){
				convertToARFF(file);
			}
		}
	}

	/**
	 * Converts GeoEAS files into weka's arff format.
	 * @param dataFile the GeoEAS file to be converted into arff
	 */
	public void convertToARFF(final File dataFile){
		try{
			//reading the file
			final FileInputStream fis = new FileInputStream(dataFile);
			final InputStreamReader in = new InputStreamReader(fis);
			final BufferedReader br = new BufferedReader(in);
			//writing the new arff
			final File arff = new File(ROOT_PATH+"/data/arff/"+dataFile.getName()+".arff");
			final FileWriter fw = new FileWriter(arff, false);
			final BufferedWriter bwriter = new BufferedWriter(fw);

			//some statistics
			int numDataRows = 0;
			//first line = name of relation
			String fileLine = br.readLine();
			if(fileLine != null){
				//first line = arff-relation
				bwriter.write("@relation \'"+fileLine+"\'\n\n");
				//get the number of attributes from the 2nd line
				final Integer numAttributes = Integer.valueOf(br.readLine());

				//write attributes
				for(int i=0; i<numAttributes.intValue(); i++){
					fileLine = br.readLine();
					bwriter.write("@attribute \'"+fileLine+"\' numeric\n");
				}
				bwriter.write("\n@data\n");

				//write data
				StringTokenizer tokenizer = new StringTokenizer(fileLine, " ");
				while((fileLine = br.readLine()) != null){
					tokenizer = new StringTokenizer(fileLine, " ");
					//pre-test structure
					final int tokens = tokenizer.countTokens();
					if(tokens != numAttributes.intValue()){
						System.out.println(	"Invalid data format? I have "+numAttributes+
								" but got "+tokens+" tokens. Skipping file!");
						GeoEASManager.logger.log(Level.SEVERE, 
								"Invalid file structure in file "+dataFile.getAbsolutePath());
						return;
					}
					//handle data
					final StringBuilder line = new StringBuilder();
					while(tokenizer.hasMoreTokens()){
						line.append(tokenizer.nextToken());
						if(tokenizer.hasMoreTokens()){
							line.append(",");
						}
					}
					line.append("\n");
					bwriter.write(line.toString());
					numDataRows++;
				}

				//finish
				bwriter.close();
				//stats
				System.out.println(dataFile.getName()+" data rows:  "+numDataRows);
			}
		}catch(final Exception exception){
			exception.printStackTrace();
		}
	}
}
