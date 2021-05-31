/* @author David Tsai
 * @created on Apr 26, 2003
 */
package plugins.pieces;

import gui.PieceProperties;

import javax.media.j3d.Appearance;
import javax.media.j3d.Transform3D;
import javax.vecmath.Point3d;

import backend.Piece;
import backend.PieceFactory;


/**
 * TriangularBumperAFactory - creates a cubical bumper gizmo for the gizmoball game.
 */
public class TriangularBumperAFactory implements PieceFactory {
	
	/**
	 * @return a triangular bumper with default parameters.
	 */
	public Piece makePiece(Point3d location) {
		return (
			new TriangularBumperA3D(
				null,
				location,
				PieceProperties.defaultTriangularBumperA3DXLength,
				PieceProperties.defaultTriangularBumperA3DYLength,
				PieceProperties.defaultTriangularBumperA3DZLength,
				PieceProperties.defaultTriangularBumperA3DCOR,
				PieceProperties.appTriangularBumperA3DDefault,
				true)
		);
	}
	
	
	/**
	 * @return a triangular bumper with customized parameters.
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
						   String orientation) {

		return (new TriangularBumperA3D(name, location, xLength, yLength, zLength, COR, app, isVisible));
	}	

}
