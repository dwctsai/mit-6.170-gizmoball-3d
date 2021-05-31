/*
 * Created on Apr 12, 2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package engine.backend;

import javax.media.j3d.Transform3D;

/**
 *	BallPhysics - represents a 3-dimensional ball in space to be used in the
 *  Gizmoball game - MAKE SURE EVERYTHING THAT IS JAVA3D returns new objects
 *
 *  @author Ragu Vijaykumar
 */
public interface BallPhysics extends PiecePhysics {
	
	// Mutators
	
	/**
	 * Changes the mass of this ball to the new mass
	 * @param mass - new mass of the ball
	 */
	public void setMass(double mass);
	
	/**
	 * Changes the velocity of the ball to have a new velocity in 3-dimensional space
	 * @param velocity - new velocity of the ball
	 */
	public void setVelocity(Transform3D velocity);

	// Observers

	/**
	 * Returns the mass of the ball
	 * @return mass of the ball
	 */
	public double getMass();
	
	/**
	 * Returns the radius of the ball
	 * @return radius of ball
	 */
	public double getRadius();

	/**
	 * Returns the velocity of the ball in 3-dimensional space
	 * @return velocity of the ball in 3-dimensional space
	 */
	public Transform3D getVelocity();
}
