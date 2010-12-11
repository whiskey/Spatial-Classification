package de.staticline;

import java.util.StringTokenizer;

public class SD_Tests {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String data = "      501       119      0.73      0.14       100        45       650        59        36        41 ";
		StringTokenizer st = new StringTokenizer(data, " ");
		while(st.hasMoreTokens()){
			System.out.println(st.nextToken());
		}
	}

}
