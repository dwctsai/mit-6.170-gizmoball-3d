/*
 * Created on Apr 27, 2003
 *
 * @author Ragu Vijaykumar
 */
package gui;

import java.awt.GraphicsConfiguration;

import javax.media.j3d.Canvas3D;

/**
 *	GraphicArena - represents the playing arena for the 3D gizmoball
 *  Allows for key presses to still occur
 *
 *  @author Ragu Vijaykumar
 */
public class GraphicArena
	extends Canvas3D {

	/**
	 * Creates a new Graphic Arena
	 * @param configuration
	 * @param offScreen
	 */
	public GraphicArena(
		GraphicsConfiguration configuration,
		boolean offScreen) {
		super(configuration, offScreen);
	}

	/**
	 * Creates a new Graphic Arena
	 * @param configuration
	 */
	public GraphicArena(
		GraphicsConfiguration configuration) {
		super(configuration);
	}
}
