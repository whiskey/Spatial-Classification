package de.staticline.analyze;

import java.util.logging.Logger;

import weka.classifiers.Classifier;
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
public class DataAnalyzer implements Runnable {
	private Instances 		data;
	private DataSource 		source;
	private String			dataURL;
	private static Logger logger = Logger.getLogger("de.staticline.spatial");

	/**
	 * Which is the class index of the data source?
	 * Default (<code>-1</code>) is the last column of the data set.
	 */
	public static final int DEFAULT_CLASSINDEX = -1;

	
	
	/**
	 * Uses the default class-index of an arff: the last attribute.
     * @param dataFileURL String to a Weka data-source. 
     * Could be 'arff', 'csv', ...
	 */
	public DataAnalyzer(final String dataFileURL){
		try{
			dataURL = dataFileURL;
			source = new DataSource(dataURL);
			data = source.getDataSet();
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

	/**
	 * Trains a classifier with a previously loaded data set.
	 * @param cl the classification algorithm
	 * @param hpo - hyper parameter optimization enabled? false: use Weka's
	 * default parameters for each classification engine; true: currently
	 * not implemented
	 * 
	 * @see EClassifiers
	 */
	public void trainClassifiers(final EClassifiers cl, final boolean hpo){
	    final Classifier classifier = cl.getInstance();
		try{
			//TODO: hyper parameter optimization
			
			//log
			String log = "Running "+classifier.getClass().toString() +
				" with options: ";
			for(final String option : classifier.getOptions()){
				log += option+" ";
			}
			DataAnalyzer.logger.config(log);
			System.out.println(log);
			
			//build data
			classifier.buildClassifier(data);
		}catch(final UnsupportedAttributeTypeException exception){
			//exception.printStackTrace();
			DataAnalyzer.logger.warning(classifier.getClass() +
					" can't handle numeric class attributes");
		}catch(final Exception exception){
			exception.printStackTrace();
		}
	}
	
	/**
	 * Filters the complete data sets for the given tasks.
	 * @param set
	 */
	public void filterDataSet(final EDataSets set){
		//filter unused columns
	    try {
    		switch (set) {
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
			DataAnalyzer.logger.warning(
					"Error during column removal! " + exception.toString());
			exception.printStackTrace();
		}
		
		//handle pollution data
		switch (set) {
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
}
