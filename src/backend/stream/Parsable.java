/*
 * Created on Apr 20, 2003
 *
 * @author Ragu Vijaykumar
 */
package backend.stream;

/**
 *	Parsable - allows an object to be unparsed into a string
 *
 *  @author Ragu Vijaykumar
 */
public interface Parsable {
	
	/**
	 * Creates a command line that represents the state of this gizmo
	 * @return command line string
	 */
	public String unparse();
}
