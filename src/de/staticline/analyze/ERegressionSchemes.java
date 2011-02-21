package de.staticline.analyze;

import weka.classifiers.Classifier;
import weka.classifiers.functions.LinearRegression;
import weka.classifiers.functions.SMOreg;

/**
 * All supported regression schemes.
 * <ul>
 *  <li>LinearRegression</li>
 *  <li>SMOreg</li>
 * </ul>
 * 
 * @author Carsten Witzke
 */
public enum ERegressionSchemes {
	LINEAR(new LinearRegression()),
	SMO_REG(new SMOreg());

	private Classifier prototype;

	private ERegressionSchemes(Classifier c){
		prototype = c;
	}

	public Classifier getInstance(){
		return prototype;
	}
}
