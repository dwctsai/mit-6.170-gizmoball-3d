/* @author David Tsai
 * @created on May 10, 2003
 */
package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * AboutDialog - Displays the About dialog box.
 */
public class AboutDialog extends JDialog {
	
	// CONSTRUCTOR
	/**
	 * @effects Sets up the dialog.
	 */
	public AboutDialog(JFrame jf, String title, boolean modality) {
		super(jf, title, modality);

		JPanel aboutPanel = new AboutDialog.AboutPanel();
		this.getContentPane().add(aboutPanel);

		this.setResizable(false);
		Point p = Gizmoball.theMainFrame.getLocationOnScreen();
		this.setLocation(p.x + 200, p.y + 200);
		// starting location of the dialog box
		this.pack();
		this.setVisible(true);
	}
	
	
	// PANELS
	
	/**
	 * AboutPanel displays the "About" dialog box.
	 */
	public class AboutPanel extends JPanel implements ActionListener {
		// Fields
		private int WIDTH = 600;
		private int HEIGHT = 405;  
		private ImageIcon bgImage;	
	
		/**
		 * @effects Sets up the panel.
		 */
		public AboutPanel() {
			// Set the size.		
			this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
		
			// Add the "OK" button.
			this.setLayout(new BorderLayout());
			JButton okButton = new JButton("OK");
			okButton.addActionListener(this);
			this.add(okButton, BorderLayout.SOUTH);
		
			// Load the background image for paintComponent().
			URL bgImageURL = ClassLoader.getSystemResource("gui/images/bg_AboutPanel.png");
			bgImage = new ImageIcon(bgImageURL); 
		}

	
		/**
		 * @effects Handles all user-interactions with this.
		 */
		public void actionPerformed(ActionEvent evt) {
			// if aboutDiag's "OK" button is pressed
			if (evt.getActionCommand().equals("OK")) {
			  MainFrame.aboutDiag.setVisible(false);
			}	
		
		}	

	
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

}
