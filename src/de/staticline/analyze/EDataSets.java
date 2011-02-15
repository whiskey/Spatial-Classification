package de.staticline.analyze;

/**
 * Used data sets for this task.
 * 
 * @author Carsten Witzke
 */
public enum EDataSets {
	POROSITY("Porosity"),
	SOILS("Soils"),
	POLLUTION_1("Pollution 1"),
	POLLUTION_2("Pollution 2");
	
	private String name;
	
	private EDataSets(final String n){
	    this.name = n;
	}
	
	@Override
    public String toString(){
	    return name;
	}
}
