/*
 * Created on Apr 12, 2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package engine.backend;

import physics3d.Cylinder;

/**
 *	RotatableGizmoPhysics - represents all gizmos that are rotable about a pivot point
 *
 *  @author Ragu Vijaykumar
 */
public interface RotatableGizmoPhysics extends GizmoPhysics {
	
	// Mutators
	
	/**
	 * Sets the angular velocity of the rotatable gizmo
	 * @param angular velocity - the new angular velocity of the gizmo
	 * @modifies this
	 */
	public void setAngularVelocity(double velocity);
	

	// Mutators
	
	/**
	 * Sets the axis of rotation to axis
	 * @param axis - a cylinder of radius 0 that represents the new axis of rotation
	 * @modifies this
	 */
	public void setAxisOfRotation(Cylinder axis);
	
	// Observers
	
	/**
	 * Returns the angular velocity
	 * @return angular velocity
	 */
	public double getAngularVelocity();
	
	/**
	 * Gets the axis of rotation that this gizmo rotates about
	 * @return cylinder of radius 0 that this gizmo rotates about
	 */
	public Cylinder getAxisOfRotation();
	
}
