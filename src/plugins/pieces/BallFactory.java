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
 * BallFactory - creates a ball for the gizmoball game.
 */
public class BallFactory implements PieceFactory {
	
	/**
	 * @return a ball with default parameters.
	 */
	public Piece makePiece(Point3d location) {
		return (
			new Ball3D(
				null,
				location,
				PieceProperties.defaultBall3DRadius,
				PieceProperties.defaultBall3DVelocity,
				PieceProperties.defaultBall3DMass,
				PieceProperties.appBall3DDefault)
//				PieceProperties.appBall3DTextureEarth)
		);
	}
	
	
	/**
	 * @return a ball with customized parameters.
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

		return (new Ball3D(name, location, radius, velocity, mass, app));
	}

	


}
