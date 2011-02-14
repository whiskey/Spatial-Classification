package de.staticline.analyze;

import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import weka.classifiers.Classifier;
import weka.classifiers.lazy.IBk;
import weka.core.Instances;
import weka.core.UnsupportedAttributeTypeException;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.NumericToNominal;

/**
 * Data analyzer class for handling classification and hyper-parameter-search tasks.
 * 
 * @author Carsten Witzke
 */
public class DataAnalyzer implements Runnable {
	private Classifier 		classifier = new IBk(); //default classifier
	private final String			options = "";
	private Instances 		data;
	private static Logger	logger;

	private DataSource 		source;
	//private Loader 			loader; //used for incremental training
	private String			dataURL;

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
	public DataAnalyzer(final String dataFileURL){
		this(dataFileURL, DataAnalyzer.DEFAULT_CLASSINDEX);
	}

	/**
	 * 
	 * Manually sets the class-index.
	 * @param dataFileURL String to a Weka data-source. Could be 'arff', 'csv', ...
	 * @param classIndex the index of the class attribute
	 */
	public DataAnalyzer(final String dataFileURL, final int classIndex){
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
			int cIndex = classIndex;
			if(classIndex == DataAnalyzer.DEFAULT_CLASSINDEX){
				cIndex = data.numAttributes()-1;
				data.setClassIndex(cIndex);
			}else{
				data.setClassIndex(classIndex);
			}
			
			//transform numeric to nominal if attribute at class index is
			//in inappropriate format
			if(data.firstInstance().attribute(cIndex).isNumeric()){
				final NumericToNominal filter = new NumericToNominal();
				//IMPORTANT cIndex+1 ! See method documentation.
				filter.setAttributeIndices(""+(cIndex+1));
				filter.setInputFormat(data);
				data = Filter.useFilter(data, filter);
			}

			//setup logger
			final Handler fh = new FileHandler("log/logfile.txt");
			fh.setFormatter(new SimpleFormatter());
			DataAnalyzer.logger = Logger.getLogger("de.staticline.spatial");
			DataAnalyzer.logger.addHandler(fh);
			DataAnalyzer.logger.setLevel(Level.ALL);
		}catch(final Exception exception){
			exception.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		//TODO: used for hpo
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
	public void trainClassifiers(final EClassifiers classificationEngine, final boolean hpo){
		try{
			classifier = classificationEngine.getInstance();
			
			//hpo on/off
			if(hpo){
				//TODO: hyper parameter optimization
				//classifier.setOptions(Utils.splitOptions(options));
			}
			
			//log
			String log = "Running "+classifier.getClass().toString()+" with options: ";
			for(final String option : classifier.getOptions()){
				log += option+" ";
			}
			DataAnalyzer.logger.config(log);
			System.out.println(log);
			
			//build data
			classifier.buildClassifier(data);
		}catch(final UnsupportedAttributeTypeException exception){
			//exception.printStackTrace();
			DataAnalyzer.logger.warning(classifier.getClass()+" can't handle numeric class attributes");
		}catch(final Exception exception){
			exception.printStackTrace();
		}
	}

}
