/*
 * Created on Apr 12, 2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package engine.backend;

import java.util.Map;

import javax.media.j3d.BoundingBox;

/**
 *	MobileGizmoPhysics - represents a gizmo that is free to move around the playing board
 *  but only within a specfied space
 *  @author Ragu Vijaykumar
 */
public interface MobileGizmoPhysics extends GizmoPhysics {

	// Mutators
	/**
	 * Signals the mobile gizmo that there has been a collision in the timeSlice
	 * @param component - component that has been involved in the collision
	 * @param problemBox - maximum box that this gizmo can take up
	 * @return the maximum allowed bounding box
	 */
	public BoundingBox collision(BoundingBox problemBox);
	
	/**
	 * Sets the bounding box of this mobile gizmo
	 * @param container - new container of this mobile gizmo
	 */
	public void setContainer(BoundingBox container);

	// Observers
	
	/**
	 * Returns a mapping from all the physical components making up this
	 * gizmo to the velocities of those components
	 * @return
	 */
	public Map getComponentVelocities();
	
	/**
	 * Return the next bounding box that this mobile gizmo will occupy in the timeSlice
	 * @param timeSlice - amount of time elapsed
	 * @return new Bounding Box
	 */
	public BoundingBox getNextContainer(double timeSlice);
	
	/**
	 * Is this gizmo moving?
	 * @return true iff this gizmo is moving
	 */
	public boolean isMoving();
}
