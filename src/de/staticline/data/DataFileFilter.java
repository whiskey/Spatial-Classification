package de.staticline.data;

import java.io.File;
import java.io.FilenameFilter;

/**
 * 
 *
 * @author Carsten Witzke
 */
public class DataFileFilter implements FilenameFilter {
	private final String allowedExtension;
	
	public DataFileFilter(){
		this("dat");
	}
	
	public DataFileFilter(final String ending){
		allowedExtension = ending;
	}

	
	/* (non-Javadoc)
	 * @see java.io.FilenameFilter#accept(java.io.File, java.lang.String)
	 */
	@Override
	public boolean accept(final File dir, final String name) {
		if(name.endsWith("."+allowedExtension))
			return true;
		else
			return false;
	}

}
