/*
 * Created on Apr 24, 2003
 *
 * @author Ragu Vijaykumar
 */
package testing;

import java.io.FileOutputStream;
import java.io.PrintStream;

/**
 *	FileGenerator.java
 *
 *  @author Ragu Vijaykumar
 */
public class FileGenerator {

	/**
	 * 
	 */
	private FileGenerator() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * Opens up a stream to file with the given param name
	 * @param filename - name of file to create
	 * @return new PrintStream to output information to; if no file can be written to, it will print to shell
	 */
	public static PrintStream createFile(String filename) {

		try {
			FileOutputStream outFile = new FileOutputStream(filename);
			PrintStream p = new PrintStream(outFile);
			return p;
		} catch (Exception e) {
			return System.out;
		}
	}

}
