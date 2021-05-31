/*
 * Created on May 10, 2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package backend.event;

import java.util.Collection;
import java.util.Set;

import backend.events.exceptions.EventException;

/**
 * @author Ragu Vijaykumar
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public interface GizmoListener {
	
	/**
	 * The representation of a Defualt action of a gizmo
	 */
	public static final String DEFAULT_ACTION = "";
	
	// Mutators
	
	/**
	 * Adds a connection between this gizmo and another gizmo
	 * The connection always results with any action performed on this
	 * gizmo triggering the action on all connected gizmos.
	 * this --> toBeTriggered, NOT toBeTriggered --> this
	 * @param toBeTriggered - the gizmo to be be triggered
	 * @throws EventException - if toBeTriggered already exists
	 */
	public void addConnection(GizmoEvent toBeTriggered) throws EventException;
	
	/**
	 * Deletes the connection between this gizmo and another gizmo
	 * @param toBeTriggered - gizmo that was to be triggered
	 * @throws EventException - if toBeTriggered is not a conncetion
	 */
	public void delConnection(GizmoEvent toBeTriggered) throws EventException;

	/**
	 * Clears all connections this gizmo has with other gizmos
	 */
	public void clearConnections();

	/**
	* Triggers this piece to do a specific activity
	*/
	public void action(GizmoEvent event);
	
	// Observers
		
	/**
	 * A set of all gizmos that this gizmo is connected to
	 * @return read-only set of gizmos that this gizmo is connected to
	 */
	public Set getConnections();
	
	/**
	 * A list of all possible actions that a gizmo can do
	 * @return a collection of strings that oorrespond to the actions a gizmo can do
	 */
	public Collection getActions();
}
