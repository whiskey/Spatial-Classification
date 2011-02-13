package de.staticline.analyze;

import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.Logistic;
import weka.classifiers.functions.RBFNetwork;
import weka.classifiers.functions.SMO;
import weka.classifiers.lazy.IBk;
import weka.classifiers.meta.AdaBoostM1;
import weka.classifiers.meta.Bagging;
import weka.classifiers.trees.J48;

/**
 * All supported classifiers.
 * 
 * <ul>
 *  <li>Naive Bayes</li>
 *  <li>Logistic</li>
 *  <li>SMO (RBF)</li>
 *  <li>IBk</li>
 *  <li>AdaBoost M1</li>
 *  <li>Bagging</li>
 *  <li>J48</li>
 * </ul>
 * 
 * @author Carsten Witzke
 */
public enum EClassifiers {
	NAIVE_BAYES(new NaiveBayes()),
	LOGISTIC(new Logistic()),
	RBF(new RBFNetwork()),
	SMO(new SMO()),
	IBK(new IBk()),
	ADA_BOOST(new AdaBoostM1()),
	BAGGING(new Bagging()),
	J48(new J48());


	private Classifier prototype;

	private EClassifiers(Classifier c){
		prototype = c;
	}

	public Classifier getInstance(){
		return prototype;
	}
}
