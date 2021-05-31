/* @author David Tsai
 * @created on May 7, 2003
 */
package gui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

/**
 * PieceListPlayPanel replaces PieceListPanel when the gizmoball game is in "play" mode.
 */
public class PieceListPlayPanel extends JPanel {
	// FIELDS
	private Graphics g;
	static final int WIDTH = 200;
	static final int HEIGHT = 500;
	private ImageIcon bgImage;


	// CONSTRUCTOR
	/**
	 * @effects Sets up the panel.
	 */
	public PieceListPlayPanel() {
		// Set the size.
		this.setPreferredSize(new Dimension(WIDTH, HEIGHT));

		// Set the opacity.
		this.setOpaque(true);


		// Load the background image for paintComponent().
		URL bgImageURL = ClassLoader.getSystemResource("gui/images/bg_PieceListPanel.png");
		bgImage = new ImageIcon(bgImageURL); 		

		// Draw this panel.
		this.setVisible(true);
	
	}
	
	
	// METHODS
	/**
	 * @effects This method is called everytime the display is updated.
	 */
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		// paint the background		
		bgImage.paintIcon(this, g, 0,0);
		paintChildren(g);
	}	
}
