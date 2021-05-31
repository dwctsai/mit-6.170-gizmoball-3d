/* @author David Tsai
 * @created on Apr 20, 2003
 */
package gui.backend;

import javax.media.j3d.Appearance;
import javax.media.j3d.BoundingBox;
import javax.vecmath.Point3d;

/**
 * PieceGraphics represents the graphical interface side of a 
 * piece of a Gizmoball game.
 * 
 */
public interface PieceGraphics {
	/**
	 * @effects Sets up the graphics for this piece. 
	 * @param lowerCornerLocation - this piece's bounding box's lower corner
	 */
	public void setupGraphics(Point3d lowerCornerLocation);

	
	/**
	 * @effects Updates the graphics of this piece.
	 */
	public void updateGraphics();
	
	/**
	 * @effects Moves the graphics of this piece to the specified location.
	 */
	public void moveGraphics(Point3d p);

	/**
	 * @effects Adds the piece to the playing arena.
	 */
	public void addPiece();
	
	
	/**
	 * @effects Removes this piece from the playing arena. 
	 */
	public void removePiece();

	
	/**
	 * @return The location of this piece's bounding box's "small"-valued corner.
	 */
	public Point3d getContainerLowerLocation();

	
	/**
	 * @return The location of this piece's bounding box's "large"-valued corner.
	 */
	public Point3d getContainerUpperLocation();
	
	/**
	 * @return The container of this piece in absolute, virtual-world coordinates.
	 */
	public BoundingBox getContainerAbsoluteCoord();
	

	/**
	 * @effects Rotates this piece 90 degrees along the Z axis.
	 *          The piece's bounding box is rotated along with it.
	 */
	public void rotateXY90Graphics();
	
	
	/**
	 * @effects Rotates this piece 90 degrees along the Y axis.
	 *          The piece's bounding box is rotated along with it.
	 */
	public void rotateXZ90Graphics();
	
	
	/**
	 * @effects Rotates this piece 90 degrees along the X axis.
	 *          The piece's bounding box is rotated along with it.
	 */
	public void rotateYZ90Graphics();
	
	
	/**
	 * @effects Rotates this piece -90 degrees along the Z axis.
	 *          The piece's bounding box is rotated along with it.
	 */
	public void rotateYX90Graphics();
	
	
	/**
	 * @effects Rotates this piece -90 degrees along the Y axis.
	 *          The piece's bounding box is rotated along with it.
	 */
	public void rotateZX90Graphics();
	
	
	/**
	 * @effects Rotates this piece -90 degrees along the X axis.
	 *          The piece's bounding box is rotated along with it.
	 */
	public void rotateZY90Graphics();	


	/**
	 * @effects Gets the Appearance of this piece.
	 */
	public Appearance getAppearance();

	
	/**
	 * @effects Sets the Appearance of this piece.
	 */
	public void setAppearance(Appearance app);

	
	/**
	 * @effects Draws this piece as a wireframe model.
	 */
	public void drawWireFrameMode();
	
	
	/**
	 * @effects Draws this piece as a filled polygon model.
	 */
	public void drawFillMode();

	
	/**
	 * @effects Enables all the behaviors of this piece. 
	 */
	public void enablePieceBehaviors();
	
	
	/**
	 * @effects Diables all the behaviors of this piece.
	 */
	public void disablePieceBehaviors();
}
