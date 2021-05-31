/*
 * Created on Apr 12, 2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package engine.backend;


/**
 *	GizmoPhysics - defines all methods that gizmos must implement
 *  in order to be succesfully processed by the engine
 *
 *  @author Ragu Vijaykumar
 */
public abstract interface GizmoPhysics extends PiecePhysics {
	
	// Mutators
	
	/**
	 * Sets this gizmo to have a new reflection coefficient
	 * @param reflectCoeff - new reflection coefficient
	 */
	public void setReflectCoeff(double reflectCoeff);
	
	/**
	 * Triggers all connections that this gizmo has to other pieces
	 */
	public void triggerConnections();
	
	/**
	 * Triggers actions that occur when a ball colides with this gizmo
	 * @param ball - the ball that collided with the gizmo
	 */
	public void ballCollision(BallPhysics ball);
	
	// Observers
	
	/**
	 * Returns the reflection coefficient of the gizmo
	 * @return the reflection coefficient of the gizmo
	 */
	public double getReflectCoeff();
		
}
