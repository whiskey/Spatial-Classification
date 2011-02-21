package de.staticline.analyze;

import java.io.File;
import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.staticline.data.Exporter;

/**
 * Singleton manager for the given tasks:
 * <ol>
 * <li>Classification model for 'porosity', 'soils' and 
 * 'pollution1' data set</li>
 * <li>Classification model for 'pollution2'</li>
 * <li>Regression model for 'lake'</li>
 * </ol>
 * Runs and handles all jobs/threads.
 *
 * @author Carsten Witzke
 */
public class TaskManager {
	private static TaskManager instance = null;
    //paths
    private static final String ROOT_PATH = System.getProperty("user.dir");
    private static final String ARFF_PATH = ROOT_PATH + "/data/arff";
    //log + output
    private static Logger logger = Logger.getLogger("de.staticline.spatial");
    private Exporter evalExport;
    //queue
    private static Queue<DataAnalyzer> jobs = new LinkedList<DataAnalyzer>();
    

    /**
     * Get the TaskManager.
     * @return the only instance of TaskManager
     */
    public static TaskManager getInstance(){
        if(instance == null){
            instance = new TaskManager();
        }
        return instance;
    }
    
    /**
     * Multiple data sets and classifiers.
     */
    public void doTask1(String outputURL){
        final EDataSets[] SET_1 = 
            {EDataSets.POROSITY, EDataSets.SOILS, EDataSets.POLLUTION_1};
        for(final EDataSets set : SET_1){
            for(final EClassifiers c : EClassifiers.values()){
                final DataAnalyzer da = setupJob(set, c, false);
                getJobs().add(da);
            }
        }
        logger.log(Level.FINER, "============== "+
                "Starting task 1: "+getJobs().size()+" jobs in queue"+
                " ==============");
        setExporter(outputURL, getJobs().size());
        startJobs();
    }
    
    /**
     * Subset of all classifiers on 'pollution2' data set.
     */
    public void doTask2(String outputURL){
        final EClassifiers[] cSet = {EClassifiers.LOGISTIC, EClassifiers.RBF, 
                EClassifiers.IBK, EClassifiers.J48};
        for(final EClassifiers c : cSet){
            final DataAnalyzer da = 
                setupJob(EDataSets.POLLUTION_2, c, false);
            getJobs().add(da);
        }
        logger.log(Level.FINER, "============== "+
                "Starting task 2: "+getJobs().size()+" jobs in queue"+
                " ==============");
        setExporter(outputURL, getJobs().size());
        startJobs();
    }
    
    /**
     * Regression analysis of the 'lake' data set.
     */
    public void doTask3(String outputURL){
//    	final EClassifiers[] cSet = {EClassifiers.};
//        for(final EClassifiers c : cSet){
//            final DataAnalyzer da = 
//                setupJob(EDataSets.LAKE, c, false);
//            getJobs().add(da);
//        }
    	
        logger.log(Level.FINER, "============== "+
                "Starting task 3: "+getJobs().size()+" jobs in queue"+
                " ==============");
        setExporter(outputURL, getJobs().size());
        startJobs();
    }
    
    /**
     * Initializes a DataAnalyzer with all data needed to prepare the data
     * of a given data set.
     * @param set the data set to be used
     * @param c the classification algorithm used in this job
     * @param hpo hyper-parameter optimization enabled?
     * @return the DataAnalyzer to be used in a thread
     * 
     * @see DataAnalyzer
     * @see EDataSets
     * @see EClassifiers
     */
    private DataAnalyzer setupJob(
            final EDataSets set, 
            final EClassifiers c, 
            final boolean hpo){
        String fileName = "";
        switch (set) {
            case POROSITY:
                fileName = "Porosity.arff";
                break;
            case SOILS:
                fileName = "Soils.arff";
                break;
            case POLLUTION_1:
                fileName = "Pollution.arff";
                break;
            case POLLUTION_2:
                fileName = "Pollution.arff";
                break;
            default:
                System.err.println("unsupported data set");
                break;
        }
        final DataAnalyzer da = new DataAnalyzer(ARFF_PATH+"/"+fileName, set);
        da.setClassifier(c);
        da.setHPO(hpo);
        return da;
    }
    
    /**
     * Currently only quick and dirty start of all jobs in queue.
     */
    private void startJobs(){
        //consider this:
        //http://stackoverflow.com/questions/790710/how-should-i-handle-multi-threading-in-java
        final int cores = Runtime.getRuntime().availableProcessors();
        for(int i=0; i<cores; i++){
            final Worker w = new Worker();
            w.run();
        }
    }
    
    /**
     * @return the jobs
     */
    public static Queue<DataAnalyzer> getJobs() {
        return jobs;
    }

    /**
     * Creates a new Exporter whose output will be written to the given URL.
     * Missing folders will be created on the fly. Currently the given file
     * extension has no effect on the export function. It is possible to give
     * an extension <code>.xml</code> and call afterwards 
     * <code>Exporter.exportCSV()</code>.
     *  
     * @param out the URL-String to the output file, e.g. 
     * <code>./output/eval.csv</code>
     * @see Exporter
     */
	private void setExporter(String out, int rows) {
		File output = new File(out);
		if(!output.getParentFile().exists()){
			output.getParentFile().mkdirs();
		}
		evalExport = new Exporter(output, rows);
		evalExport.start();
	}

	/**
	 * @return the Exporter
	 */
	public Exporter getExporter() {
		return evalExport;
	}


	private class Worker extends Thread{
        public Worker() {}

        @Override
        public void run(){
            while(getJobs().peek() != null){
                DataAnalyzer da = getJobs().poll();
                da.start();
                getExporter().addJob(da.getEval());
            }
        }
    }
}
