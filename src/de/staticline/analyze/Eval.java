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
	public Evaluation evaluation;
	public String[] options;
	
	public Eval(String n, Evaluation e, String[] o){
		this.classifier = n;
		this.evaluation = e;
		this.options = o;
	}
}
