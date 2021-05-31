/*
 * Created on Apr 17, 2003
 *
 * @author Ragu Vijaykumar
 */
package engine.backend;

import java.util.Set;

import javax.media.j3d.BoundingBox;
import javax.vecmath.Point3d;

import physics3d.Cylinder;

/**
 *	PiecePhysics - a piece of the gizmoball game - do not implement this class
 *  Use subinterfaces to get access to these methods
 *
 *  @author Ragu Vijaykumar
 */
public abstract interface PiecePhysics {
	
	// Mutators
	
	/**
	 * Sets the location of the piece in 3d space, must be the center of the piece
	 * @param location - location of center of this piece in 3d space.
	 */
	public void setLocation(Point3d location);
	
	/**
	 * How this piece should change dynamically as the game is played
	 */
	public void updatePhysics(double timeSlice);
	
	/**
	 * How this piece should change dynamically as the game is played
	 */
	public void update(double timeSlice);
	
	/**
	 * Sets up this piece to be run in the run mode
	 */
	public void setupPhysics();
	
	/**
	 * Determines whether the engine should take into account the physics of this piece
	 * @effects The piece's isPhysical property.
	 */
	public void setPhysical(boolean isPhysical);
		
	/**
	 * Creates correct axis of rotation
	 * @param xbar - center of the gizmo
	 * @param ybar - center of the gizmo
	 * @param zbar - center of the gizmo
	 */
	public void setupPhysicsRotations(double xbar, double ybar, double zbar);
	
	/**
	 * Setups up this gizmo to be rotated
	 * @param axisOfRotation - axis to rotate about
	 */
	public void rotate90Physics(Cylinder axisOfRotation);
	

	// Observers
	
	/**
	 * Should the engine take into account the physics of this piece
	 * @return true iff engine should take into account piece physics
	 */
	public boolean isPhysical();
	
	/**
	 * Gets the bounding box that surrounds the entire 3-dimensional
	 * piece in space. Bounding Box is in terms of absolute 3-dimensional
	 * space 
	 * @return bounding box container of the 3-dimensional object
	 */
	public BoundingBox getContainer();
	
	/**
	 * Returns the center of the piece in 3 dimensional space, must be the center
	 * of the piece
	 * @return a new 3 dimensional point of the absolute center position of this piece
	 */
	public Point3d getLocation();

	/**
	 * Returnst the set of components built from the physics 3D package that 
	 * account for how this piece should react to a physically-simulated environment
	 * @return read-only set of physics-3D components that make up the physical nature
	 * of this piece
	 */
	public Set getPhysicalComponents();

}
