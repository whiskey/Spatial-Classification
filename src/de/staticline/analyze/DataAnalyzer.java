package de.staticline.analyze;

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
import weka.core.UnsupportedAttributeTypeException;
import weka.core.converters.ConverterUtils.DataSource;

/**
 * Data analyzer class for handling classification and hyper-parameter-search tasks.
 * 
 * @author Carsten Witzke
 */
public class DataAnalyzer implements Runnable {
	protected Instances 		data;
	protected Classifier 		classifier = new IBk(); //default classifier
	protected String			options = "";
	private static Logger		logger;

	private DataSource 			source;
	//private Loader 			loader; //used for incremental training
	private String				dataURL;

	/**
	 * Which is the class index of the data source?
	 * Default (<code>-1</code>) is the last column of the data set.
	 */
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
			DataAnalyzer.logger = Logger.getLogger("de.staticline.spatial");
			DataAnalyzer.logger.addHandler(fh);
			DataAnalyzer.logger.setLevel(Level.ALL);
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

	/**
	 * Trains a classification engine with a previously loaded data set.
	 * @param classificationEngine the classification engine (=algorithm)
	 * <ol>
	 *  <li>Naive Bayes</li>
	 *  <li>Logistic</li>
	 *  <li>SMO (RBF)</li>
	 *  <li>IBk</li>
	 *  <li>AdaBoost M1</li>
	 *  <li>Bagging</li>
	 *  <li>J48</li>
	 * </ol>
	 * @param hpo - hyper parameter optimization enabled? false: use Weka's
	 * default parameters for each classification engine; true: currently
	 * not implemented
	 */
	public void trainClassifiers(int classificationEngine, boolean hpo){
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
				DataAnalyzer.logger.warning("Unsupported classification engine choosen.");
				break;
			}
			//trigger hpo
			if(hpo){
				//TODO: hyper parameter optimization
				//classifier.setOptions(Utils.splitOptions(options));
			}

			//some output
			String log = "Running "+classifier.getClass().toString()+"with default options: ";
			for(String option : classifier.getOptions()){
				log += option+" ";
			}
			DataAnalyzer.logger.config(log);

			classifier.buildClassifier(data);
		}catch(UnsupportedAttributeTypeException exception){
			//exception.printStackTrace();
			DataAnalyzer.logger.warning(classifier.getClass()+" can't handle numeric class attributes");
		}catch(Exception exception){
			exception.printStackTrace();
		}
	}

}
