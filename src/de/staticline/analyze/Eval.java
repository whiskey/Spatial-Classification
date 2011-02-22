package de.staticline.analyze;

import weka.classifiers.Evaluation;

/**
 * Quick and dirty container for an Evaluation and some
 * meta data. 
 * 
 * @author Carsten Witzke
 */
public class Eval{
	public String classifier;
	public EDataSets dataSet;
	public Evaluation evaluation;
	public String[] options;
	
	/**
	 * Create and fill a new Eval-container
	 * @param cName classifier Name
	 * @param dSet the used data set
	 * @param eval the Evaluation 
	 * @param opt (Weka) options
	 */
	public Eval(String cName, EDataSets dSet, Evaluation eval, String[] opt){
		this.classifier = cName;
		this.dataSet = dSet;
		this.evaluation = eval;
		this.options = opt;
	}
}
