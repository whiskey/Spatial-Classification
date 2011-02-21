package de.staticline.data;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

import weka.classifiers.Evaluation;
import de.staticline.analyze.Eval;

/**
 * Data-exporter for Weka evaluations.
 * 
 * @author Carsten Witzke
 */
public class Exporter extends Thread{
	private static Logger logger = Logger.getLogger("de.staticline.spatial");
	private Queue<Eval> evalQueue = new LinkedList<Eval>();
	private final File outputFile;
	private FileWriter fw;
	private BufferedWriter bw;
	private int rows;
	private int written;
	
	public Exporter(File out, int rws){
		outputFile = out;
		rows = rws;
		try {
			//create writer
			fw = new FileWriter(outputFile,false);
			bw = new BufferedWriter(fw);
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		logger.log(Level.FINEST, "Starting Exporter for file "+
				outputFile.getAbsolutePath());
		while(written < rows){
			try {
				if(evalQueue.peek() != null)
					exportCSV(evalQueue.poll());
				sleep(500);
			} catch (InterruptedException exception) {
				exception.printStackTrace();
			}
        }
		//let the writer do his job
		try {
			bw.flush();
			bw.close();
		} catch (IOException exception) {
			exception.printStackTrace();
		}
	}
	
	public void addJob(Eval eval){
		evalQueue.add(eval);
	}
	
	/**
	 * Exports evaluation-data to csv file. If file already exists all data 
	 * will be overwritten. Format is:
	 * <ol>
	 * <li>class name classifier</li>
	 * <li>% correct</li>
	 * <li>% incorrect</li>
	 * <li>relative absolute error</li>
	 * <li>confusion matrix: ((x1,y1)(x2,y2))</li>
	 * <li>classifier options</li>
	 * </ol>
	 * 
	 * @param eval the Eval to be exported
	 * @see Eval
	 * @see Evaluation
	 */
	public void exportCSV(Eval eval){
		try {
			StringBuilder result = new StringBuilder();
			result.append(eval.classifier).append(",")
			.append(eval.evaluation.pctCorrect()).append(",")
			.append(eval.evaluation.pctIncorrect()).append(",");
			//for regression
			result.append(eval.evaluation.relativeAbsoluteError()).append(",");
			try{
				double[][] matrix = eval.evaluation.confusionMatrix();
				result.append("(");
				for(int i=0; i<matrix.length; i++){
					result.append("(");
					for(int j=0; j<matrix[i].length; j++){
						result.append(matrix[i][j]).append(" ");
					}
					result.append(")");
				}
				result.append("),");
			}catch(Exception exception){
				result.append(",");
				//Hey Weka-guys, it's possible to define OWN exceptions!
			}
			for(String opt : eval.options){
				result.append(opt).append(" ");
			}
			result.append("\n");
			
			//System.out.println(result);
			bw.write(result.toString());
			written++;
			//System.out.println(written+"/"+rows);
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}
}
