/* @author Eric Tung
 * @created on May 11, 2003
 */
package plugins.pieces;

import gui.PieceProperties;

import javax.media.j3d.Appearance;
import javax.media.j3d.Transform3D;
import javax.vecmath.Point3d;

import backend.Piece;
import backend.PieceFactory;


/**
 * TriangularBumperBFactory - creates a pyramidal bumper gizmo for the gizmoball game.
 */
public class TriangularBumperBFactory implements PieceFactory {
	
	/**
	 * @return a triangular bumper with default parameters.
	 */
	public Piece makePiece(Point3d location) {
		return (
			new TriangularBumperB3D(
				null,
				location,
				PieceProperties.defaultTriangularBumperB3DXLength,
				PieceProperties.defaultTriangularBumperB3DYLength,
				PieceProperties.defaultTriangularBumperB3DZLength,
				PieceProperties.defaultTriangularBumperB3DCOR,
				PieceProperties.appTriangularBumperB3DDefault,
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

		return (new TriangularBumperB3D(name, location, xLength, yLength, zLength, COR, app, isVisible));
	}	

}
