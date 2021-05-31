/* @author David Tsai
 * @created on Apr 22, 2003
 */
package backend;

import javax.media.j3d.Appearance;
import javax.media.j3d.Transform3D;
import javax.vecmath.Point3d;

/**
 *	PieceFactory - creates a piece for the gizmoball game.
 */
public interface PieceFactory {
	
	/**
	 * Creates a default piece.
	 * 
	 * @return a Piece at the given location.
	 */
	public Piece makePiece(Point3d location);
	

	/**
	 * Creates a custom piece.
	 * 
	 * @parm name - the name of the piece
	 * @param location - location of the piece's bounding box's lower-left corner
	 * @param radius - radius of the piece
	 * @param xLength - x-dimension of the piece
	 * @param yLength - y-dimension of the piece
	 * @param zLength - z-dimension of the piece
	 * @param mass    - mass of the piece
	 * @param velocity - velocity of the piece
	 * @param COR - coefficient of reflection of the piece
	 * @param app - appearance of the piece
	 * @param isVisible - whether this piece is visible or not
	 * @param orientation - denotes orientation of pieces if relevant (l/r for Flipper; x/y/z for Jezzmo)
	 * @return a Piece from the given parameters.
	 */	
	public Piece makePiece(String name,
						   Point3d location,
						   double radius,
						   double xLength,
						   double yLength,
						   double zLength,
						   double mass,
						   Transform3D velocity,
						   double COR,
						   Appearance app,
						   boolean isVisible,
						   String orientation);


}
