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
 * SphericalBumperFactory - creates a spherical bumper gizmo for the gizmoball game.
 */
public class SphericalBumperFactory implements PieceFactory {
	/**
	 * @return a spherical bumper with default parameters.
	 */
	public Piece makePiece(Point3d location) {
		return (
			new SphericalBumper3D(
				null,
				location,
				PieceProperties.defaultSphericalBumper3DRadius,
				PieceProperties.defaultSphericalBumper3DCOR,
				PieceProperties.appSphericalBumper3DDefault,
				true)
		);
	}
	
	
	/**
	 * @return a spherical bumper with customized parameters.
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

		return (new SphericalBumper3D(name, location, radius, COR, app, isVisible));
	}	

}
