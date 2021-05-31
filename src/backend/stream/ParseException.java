/* @author Eric Tung, David Tsai
 * @created on Apr 26, 2003
 */
package backend.stream;

/**
 * This exception results when the input file cannot be parsed properly
 */
class ParseException extends RuntimeException {

	public ParseException() {
		super();
	}
	public ParseException(String s) {
		super(s);
	}

}

