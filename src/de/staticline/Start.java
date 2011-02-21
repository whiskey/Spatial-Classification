package de.staticline;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import de.staticline.analyze.TaskManager;

/**
 * Start the work...
 * 
 * @author Carsten Witzke
 */
public class Start {
    private static final String ROOT_PATH = System.getProperty("user.dir");
    private static final String OUTPUT_PATH = ROOT_PATH + "/output";
    private static final TaskManager TM = TaskManager.getInstance();
    
	public static void main(final String[] args) {
	    setupLogger();
	    
		//--- convert data files to arff
//		final GeoEASManager geom = new GeoEASManager();
//		geom.convertAllFilesToARFF();
		
		//--- anylyze data
	    TM.doTask1(OUTPUT_PATH+"/eval_1.csv");
	    TM.doTask2(OUTPUT_PATH+"/eval_2.csv");
	    TM.doTask3(OUTPUT_PATH+"/eval_3.csv");
	}
	
	/**
	 * Initializes general logger instance for this project.
	 * Level is set to 'ALL'.
	 * 
	 * @see Logger
	 * @see Level  
	 */
	private static void setupLogger(){
        try {
            //create log folder
            final File logFolder = new File("log");
            if(!logFolder.exists()){
                logFolder.mkdir();
            }
            //setup logger
            final Handler fh = new FileHandler("log/logfile.txt");
            fh.setFormatter(new SimpleFormatter());
            final Logger log = Logger.getLogger("de.staticline.spatial");
            log.addHandler(fh);
            log.setLevel(Level.ALL);
        } catch (final SecurityException exception) {
            exception.printStackTrace();
        } catch (final IOException exception) {
            exception.printStackTrace();
        }
	}
	
}
