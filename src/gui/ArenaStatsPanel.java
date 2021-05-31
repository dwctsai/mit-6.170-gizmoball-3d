/* @author David Tsai
 * @created on May 6, 2003
 */
package gui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * ArenaStatsPanel is displayed whenever a gizmoball game piece is not selected. 
 */
public class ArenaStatsPanel extends JPanel {
	// FIELDS
	private Graphics g;
	static final int WIDTH = 700;
	static final int HEIGHT = 250;
	private ImageIcon bgImage;	
	
	private JLabel activeBalls;
	private JLabel activeGizmos;

	// CONSTRUCTOR	
	/**
	* @effects Sets up the panel.
	*/
	public ArenaStatsPanel() {
		// Set the size.
		this.setPreferredSize(new Dimension(WIDTH, HEIGHT));

		// Set the opacity.
		this.setOpaque(true);

		// Set the layout and border.	
		this.setLayout(null);	

		// Display the arena's stats.
		activeBalls = new JLabel();
		activeBalls.setBounds(215, 144, 64, 20);
		add(activeBalls);		
		
		activeGizmos = new JLabel();
		activeGizmos.setBounds(215, 166, 64, 20);
		add(activeGizmos);
		
		// Load the background image for paintComponent().
		URL bgImageURL = ClassLoader.getSystemResource("gui/images/bg_ArenaStatsPanel.png");
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
		
		activeBalls.setText("" + Arena3DScene.allBalls.size());
		activeGizmos.setText("" + Arena3DScene.allGizmos.size());		
	}	
	
	

}
