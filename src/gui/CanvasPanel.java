/* @author David Tsai
 * @created on Apr 10, 2003
 */

package gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.Toolkit;

import javax.swing.JPanel;

import com.sun.j3d.utils.universe.SimpleUniverse;

/**
 * CanvasPanel displays the 3D canvas. 
 */
public class CanvasPanel extends JPanel {
	// FIELDS
	private Graphics g;

	static final int WIDTH = 800;
	static final int HEIGHT = 500;

	public static Arena3D a3D;
	public static GraphicArena canvas3D;

	// CONSTRUCTOR
	/**
	 * @effects Sets up the panel.
	 */
	public CanvasPanel(Component glassPane) {
		// Set the size.
		this.setPreferredSize(new Dimension(WIDTH, HEIGHT));

		// Set the opacity.
		this.setOpaque(false);

		// Set the layout.
		this.setLayout(new BorderLayout());

		// Setup the 3D canvas and add the Arena3D to the canvas.
		GraphicsConfiguration config =
			SimpleUniverse.getPreferredConfiguration();
		canvas3D = new GraphicArena(config);
		PieceProperties.getEventHandler().addEventNotifier(canvas3D);
		PieceProperties.getEventHandler().setTopLevelComponent(canvas3D);
		a3D = new Arena3D(canvas3D);
		this.add(canvas3D);

		// Draw this panel.
		this.setVisible(true);
	}

	
	/**
	 * @effects Overrides the default paint method so that the Canvas3D will
	 *          never disappear ("turn gray") when another window overlaps it
	 *          or if somehow gets resized.
	 */
	public void paint(Graphics g) {
	   super.paint(g);
	   Toolkit.getDefaultToolkit().sync();
	}
	
}
