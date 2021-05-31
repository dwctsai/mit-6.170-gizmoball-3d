/*
 * Created on May 11, 2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package plugins.pieces;

import gui.PieceProperties;

import javax.media.j3d.Appearance;
import javax.media.j3d.Transform3D;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import backend.Piece;
import backend.PieceFactory;

/**
 * JezzmoFactory - creates a cubical bumper gizmo for the gizmoball game.
 */
public class JezzmoFactory implements PieceFactory {

	/**
	 * @return a cubical bumper with default parameters.
	 */
	public Piece makePiece(Point3d location) {
		Transform3D velocityT3D = new Transform3D();
		Vector3d tempV = new Vector3d(5.0, 5.0, 5.0);
		velocityT3D.set(tempV);

		//		public static final Transform3D defaultJezzmo3DVelocity = new Transform3D(tempT3D);

		return (
			new Jezzmo3D(
				null,
				location,
				PieceProperties.defaultJezzmo3DXLength,
				PieceProperties.defaultJezzmo3DYLength,
				PieceProperties.defaultJezzmo3DZLength,
				PieceProperties.defaultJezzmo3DCOR,
				velocityT3D,
				PieceProperties.appJezzmo3DDefault,
				true,
				PieceProperties.defaultJezzmo3DOrientation));
	}

	/**
	 * @return a cubical bumper with customized parameters.
	 */
	public Piece makePiece(
		String name,
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

		return (
			new Jezzmo3D(
				name,
				location,
				xLength,
				yLength,
				zLength,
				COR,
				velocity,
				app,
				isVisible,
				orientation));
	}

}
