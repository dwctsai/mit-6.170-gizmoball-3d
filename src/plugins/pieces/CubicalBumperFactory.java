/* @author David Tsai
 * @created on Apr 22, 2003
 */
package plugins.pieces;

import gui.PieceProperties;

import javax.media.j3d.Appearance;
import javax.media.j3d.Transform3D;
import javax.vecmath.Point3d;

import backend.Piece;
import backend.PieceFactory;


/**
 * CubicalBumperFactory - creates a cubical bumper gizmo for the gizmoball game.
 */
public class CubicalBumperFactory implements PieceFactory {
	
	/**
	 * @return a cubical bumper with default parameters.
	 */
	public Piece makePiece(Point3d location) {
		return (
			new CubicalBumper3D(
				null,
				location,
				PieceProperties.defaultCubicalBumper3DXLength,
				PieceProperties.defaultCubicalBumper3DYLength,
				PieceProperties.defaultCubicalBumper3DZLength,
				PieceProperties.defaultCubicalBumper3DCOR,
				PieceProperties.appCubicalBumper3DDefault,
				true)
		);
	}
	
	
	/**
	 * @return a cubical bumper with customized parameters.
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

		return (new CubicalBumper3D(name, location, xLength, yLength, zLength, COR, app, isVisible));
	}	

}
