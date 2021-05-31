/*
 * Created on Apr 12, 2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package backend.events.exceptions;

/**
 *	EventException - a checked exception that is thrown anytime some
 *  parameter of a GizmoPhysics is not valid.
 * 
 *  @author Ragu Vijaykumar
 */
public class EventException extends Exception {

	/**
	 * Returns a new instance of a EventException
	 */
	public EventException() {
		super();
	}

	/**
	 * @param arg0
	 */
	public EventException(String arg0) {
		super(arg0);
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public EventException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	/**
	 * @param arg0
	 */
	public EventException(Throwable arg0) {
		super(arg0);
	}

}
