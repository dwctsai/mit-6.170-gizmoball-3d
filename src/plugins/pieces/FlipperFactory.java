/* @author David Tsai
 * @created on Apr 27, 2003
 */
package plugins.pieces;

import gui.PieceProperties;

import javax.media.j3d.Appearance;
import javax.media.j3d.Transform3D;
import javax.vecmath.Point3d;

import backend.Piece;
import backend.PieceFactory;


/**
 * FlipperFactory - creates a flipper gizmo for the gizmoball game.
 */
public class FlipperFactory implements PieceFactory {
	
	/**
	 * @return a flipper with default parameters.
	 */
	public Piece makePiece(Point3d location) {
		return (
			new Flipper3D(
				null,
				location,
				PieceProperties.defaultFlipper3DCOR,
				PieceProperties.appFlipper3DDefault,
				true,
				PieceProperties.defaultFlipper3DOrientation)
		);
	}
	
	
	/**
	 * @return a flipper with customized parameters.
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

		return (new Flipper3D(name, location, COR, app, isVisible, orientation));
	}	

}

