package de.staticline.analyze;

import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.UnsupportedAttributeTypeException;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.NumericToNominal;
import weka.filters.unsupervised.attribute.Remove;

/**
 * Data analyzer class for handling classification and 
 * hyper-parameter-search tasks.
 * 
 * @author Carsten Witzke
 */
public class DataAnalyzer {
    private DataSource     source;
    private String		   dataURL;
	private Instances 	   data;
	private EDataSets      dataSet;
	private Classifier     classifier;
	private boolean        hpoEnabled = false;
	private static Logger  logger = Logger.getLogger("de.staticline.spatial");

	/**
	 * Which is the class index of the data source?
	 * Default (<code>-1</code>) is the last column of the data set.
	 */
	public static final int DEFAULT_CLASSINDEX = -1;

	
	
	/**
	 * Fetches data at the given url and prepares it for the given tasks.
     * @param dataFileURL String to a Weka data-source. 
     * Could be 'arff', 'csv', ...
     * @param set the used data set. According to the task data sets are
     * handled different (split, remove columns).
	 */
	public DataAnalyzer(final String dataFileURL, final EDataSets set){
		try{
			dataURL = dataFileURL;
			dataSet = set;
			
			source = new DataSource(dataURL);
			data = source.getDataSet();
			data.setClassIndex(data.numAttributes()-1);
			
			filterDataSet();
		}catch(final Exception exception){
			exception.printStackTrace();
		}
	}

	public void start() {
		trainClassifier();
		evaluateClassifier();
	}
	
	/**
	 * Set classification algorithm.
	 * @param c
	 */
	public void setClassifier(final EClassifiers c){
	    classifier = c.getInstance();
	}
	
	/**
	 * Set hyper-parameter optimization enabled or disabled.
	 * @param value
	 */
	public void setHPO(final boolean value){
	    hpoEnabled = value;
	}

	/**
	 * Pre-filters the current data sets for the given tasks.
	 * @param set the used data set
	 */
	private void filterDataSet(){
		//filter unused columns
	    try {
    		switch (dataSet) {
    		case POROSITY:
    		    final Remove remove = new Remove();
    		    remove.setInputFormat(data);
    			remove.setAttributeIndices("3,4");
    			data = Filter.useFilter(data, remove);
    			break;
    		default:
    			break;
    		}
		} catch (final Exception exception) {
			logger.log(Level.WARNING,
					"Error during column removal! ", exception);
		}
		
		//handle pollution data
		switch (dataSet) {
		case POLLUTION_1:
			defineClass(382);
			break;
		case POLLUTION_2:
			defineClass(1200);
			break;
		default:
			break;
		}
		
		//convert attributes if needed
		convertClassIndexToNominal();
	}
	
	/**
	 * Transforms numeric to nominal if attribute at class index is
	 * in inappropriate format.
	 */
	private void convertClassIndexToNominal(){
	    final int cIndex = data.classIndex();
        if(data.firstInstance().attribute(cIndex).isNumeric()){
            final NumericToNominal filter = new NumericToNominal();
            filter.setAttributeIndices(""+(cIndex+1));
            try {
                filter.setInputFormat(data);
                data = Filter.useFilter(data, filter);
            } catch (final Exception exception) {
                exception.printStackTrace();
            }
        }
	}
	
	/**
	 * Helper method. Splits data set into two classes (-1 and +1) where
	 * attributes with values below <code>threshold</code> are in class -1
	 * and above in +1.
	 * @param threshold the threshold for the split
	 */
	private void defineClass(final double threshold){
	    Attribute att = data.attribute("cs_class");
	    //create attribute if needed
	    if(att == null){
	    	final FastVector nominal_values = new FastVector(2);
    		nominal_values.addElement("-1");
    		nominal_values.addElement("+1");
    		att = new Attribute("cs_class", nominal_values, data.numAttributes());
    		data.insertAttributeAt(att, data.numAttributes());
    		data.setClassIndex(data.numAttributes()-1);
	    }
		//each instance...
		for(int i=0; i<data.numInstances(); i++){
			final Instance instance = data.instance(i);
			if(instance.value(2) < threshold){
			    instance.setValue(att,"-1");
			}else{
			    instance.setValue(att,"+1");
			}
		}
	}
	
	/**
     * Trains a classifier with a previously loaded data set.
     * 
     * @param hpo - hyper parameter optimization enabled? false: use Weka's
     * default parameters for each classification engine; true: currently
     * not implemented
     * @see EClassifiers
     */
    private void trainClassifier(){
        try{
            //log
            String log = "Running "+classifier.getClass() +
                " with options: ";
            for(final String option : classifier.getOptions()){
                log += option+" ";
            }
            log += "on data set " + dataSet;
            logger.log(Level.FINER,log);
            
            //build model
            //TODO: hyper parameter optimization
            classifier.buildClassifier(data);
            log = "Done with "+classifier.getClass() +
                " on " + dataSet;
            logger.log(Level.FINER, log);
        }catch(final UnsupportedAttributeTypeException exception){
            logger.log(Level.WARNING, classifier.getClass() +
                    " can't handle numeric class attributes");
        }catch(final Exception exception){
            final String log = "Error during classifier training!\n" +
                "  Data set: " + dataSet + "\n  Classifier: " + 
                classifier.getClass();
            logger.log(Level.WARNING,log, exception);
        }
    }
    
    /**
     * Evaluate current model with 4-fold validation.
     */
    private void evaluateClassifier(){
        try {
            final Evaluation eval = new Evaluation(data);
            eval.crossValidateModel(classifier, data, 4, new Random(System.currentTimeMillis()));
            System.out.println(eval.toSummaryString());
        } catch (final Exception exception) {
            final String log = "Error during classifier evaluation!\n" +
            "  Data set: " + dataSet + "\n  Classifier: " + 
            classifier.getClass();
        logger.log(Level.WARNING,log, exception);
        }
    }
}
