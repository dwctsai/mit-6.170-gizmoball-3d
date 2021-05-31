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
 * AbsorberFactory - creates an absorber gizmo for the gizmoball game.
 */
public class AbsorberFactory implements PieceFactory {
	
	/**
	 * @return an absorber bumper with default parameters.
	 */
	public Piece makePiece(Point3d location) {
		return (
			new Absorber3D(
				null,
				location,
				PieceProperties.defaultAbsorber3DXLength,
				PieceProperties.defaultAbsorber3DYLength,
				PieceProperties.defaultAbsorber3DZLength,
				PieceProperties.defaultAbsorber3DCOR,
				PieceProperties.appAbsorber3DDefault,
				true)
		);
	}
	
	
	/**
	 * @return an absorber bumper with customized parameters.
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

		return (new Absorber3D(name, location, xLength, yLength, zLength, COR, app, isVisible));
	}	

}
