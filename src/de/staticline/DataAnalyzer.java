package de.staticline;

import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.Logistic;
import weka.classifiers.functions.RBFNetwork;
import weka.classifiers.functions.SMO;
import weka.classifiers.lazy.IBk;
import weka.classifiers.meta.AdaBoostM1;
import weka.classifiers.meta.Bagging;
import weka.classifiers.trees.J48;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

/**
 * Data analyzer class for handling classification and hyper-parameter-search tasks.
 * 
 * @author Carsten Witzke
 */
public class DataAnalyzer implements Runnable {
	private DataSource 			source;
	//private Loader 			loader; //used for incremental training
	private String				dataURL;
	
	protected Instances 		data;
	protected Classifier 		classifier = new IBk(); //default classifier
	protected String			options = "";
	protected static Logger		logger;
	
	
	public static final int DEFAULT_CLASSINDEX = -1;
	
	/**
	 * 
	 * Uses the default class-index of an arff: the last attribute.
	 * @param dataFileURL String to a Weka data-source. Could be 'arff', 'csv', ...
	 */
	public DataAnalyzer(String dataFileURL){
		this(dataFileURL, DataAnalyzer.DEFAULT_CLASSINDEX);
	}
	
	/**
	 * 
	 * Manually sets the class-index.
	 * @param dataFileURL String to a Weka data-source. Could be 'arff', 'csv', ...
	 * @param classIndex the index of the class attribute
	 */
	public DataAnalyzer(String dataFileURL, int classIndex){
		try{
			dataURL = dataFileURL;
			source = new DataSource(dataURL);
			//TODO: load only the structure to keep the memory usage low
			//loader = source.getLoader();
			//data = source.getStructure();
			//incremental training not implemented for needed classifiers :(
			//see: http://weka.wikispaces.com/Use+WEKA+in+your+Java+code#toc13
			data = source.getDataSet();
			//set class index
			if(classIndex == DataAnalyzer.DEFAULT_CLASSINDEX){
				data.setClassIndex(data.numAttributes()-1);
			}else{
				data.setClassIndex(classIndex);
			}
			
			//setup logger
			Handler fh = new FileHandler("logfile.txt");
			fh.setFormatter(new SimpleFormatter());
			logger = Logger.getLogger("de.staticline.spatial");
			logger.addHandler(fh);
			logger.setLevel(Level.ALL);
		}catch(Exception exception){
			exception.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		
	}
	
	/*
	private void trainUpdateableClassifiers(){
		try{
			classifier.buildClassifier(data);
			//current (data) instance
			Instance instance;
			
			while((instance = loader.getNextInstance(data)) != null){
				//All Known Implementing Classes:
				//AODE, IB1, IBk, KStar, LWL, NaiveBayesUpdateable, 
				//NNge, RacedIncrementalLogitBoost, Winnow
				
			}
		}catch (Exception exception){
			exception.printStackTrace();
		}
	}
	*/
	
	public void trainClassifiers(int classificationEngine){
		try{
			switch(classificationEngine){
			case 0:
				classifier = new NaiveBayes();
				break;
			case 1:
				classifier = new Logistic();
				break;
			case 2:
				classifier = new RBFNetwork();
				break;
			case 3:
				classifier = new SMO();
				break;
			case 4:
				classifier = new IBk();
				break;
			case 5:
				classifier = new AdaBoostM1();
				break;
			case 6:
				classifier = new Bagging();
				break;
			case 7:
				classifier = new J48();
				break;
			default:
				System.err.println("Unsupported classification engine choosen.");
				logger.warning("Unsupported classification engine choosen.");
				break;
			}
			//TODO: hyper parameter optimization
			//classifier.setOptions(Utils.splitOptions(options));
			
			//some output
			String log = "Running "+classifier.getClass().toString()+"with default options: ";
			for(String option : classifier.getOptions()){
				log += option+" ";
			}
			logger.config(log);
			
			classifier.buildClassifier(data);
		}catch(Exception exception){
			exception.printStackTrace();
		}
	}

}
