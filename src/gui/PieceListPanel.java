/* @author David Tsai
 * @created on Apr 10, 2003
 */

package gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.Iterator;
import java.util.LinkedList;

import javax.media.j3d.Bounds;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.vecmath.Point3d;

import plugins.pieces.AbsorberFactory;
import plugins.pieces.BallFactory;
import plugins.pieces.CubicalBumperFactory;
import plugins.pieces.FlipperFactory;
import plugins.pieces.JezzmoFactory;
import plugins.pieces.OuterWall3D;
import plugins.pieces.SphericalBumperFactory;
import plugins.pieces.TriangularBumperAFactory;
import plugins.pieces.TriangularBumperBFactory;
import plugins.pieces.TriangularBumperCFactory;
import backend.Piece;
import backend.PieceFactory;

/**
 * PieceListPanel displays the list of available pieces for the gizmoball game. 
 */
public class PieceListPanel extends JPanel implements ActionListener {
	// FIELDS
	private Graphics g;
	static final int WIDTH = 200;
	static final int HEIGHT = 500;
	private ImageIcon bgImage;

	public static Point3d pieceSpawnLocation = new Point3d(0.0, 0.0, 0.0);
	private PieceFactory pf;
	private Piece p;


	// CONSTRUCTOR
	/**
	 * @effects Sets up the panel.
	 */
	public PieceListPanel() {
		// Set the size.
		this.setPreferredSize(new Dimension(WIDTH, HEIGHT));

		// Set the opacity.
		this.setOpaque(true);
		
		// Set the layout and border.
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.setBorder(null);
		
		
		add(new JLabel("    "));
		
		// Add the buttons:
		// Ball3D		
		addImageButton("Ball", "Add Ball 3D");
		
		// CubicalBumper3D
		addImageButton("Cube", "Add Cubical Bumper 3D");
		
		// SphericalBumper3D
		addImageButton("Sphere", "Add Spherical Bumper 3D");
		
		// TriangularBumperA3D
		addImageButton("TriangleA", "Add Triangular Bumper 3D Type A");
		
		// TriangularBumperB3D
		addImageButton("TriangleB", "Add Triangular Bumper 3D Type B");
		
		// TriangularBumperC3D
		addImageButton("TriangleC", "Add Triangular Bumper 3D Type C");
		
		// Flipper3D		
		addImageButton("Flipper", "Add Flipper 3D");
		
		// Absorber3D
		addImageButton("Absorber", "Add Absorber 3D");

		// Jezzmo3D
		addImageButton("Jezzmo", "Add Jezzmo 3D");


		// Load the background image for paintComponent().
		URL bgImageURL = ClassLoader.getSystemResource("gui/images/bg_PieceListPanel.png");
		bgImage = new ImageIcon(bgImageURL);

		// Draw this panel.
		this.setVisible(true);
	}

	// METHODS
	/**
	 * @effects creates a new image button from the given parameters and
	 *          adds it to this.
	 */
	private void addImageButton(String urlFragment, String toolTipString) {
		JButton b = new JButton();
		urlFragment = "gui/images/piece_" + urlFragment;
		URL imageURL = ClassLoader.getSystemResource(urlFragment + ".png");
		b.setIcon(new ImageIcon(imageURL));
		imageURL = ClassLoader.getSystemResource(urlFragment + "_pressed.png");
		b.setRolloverIcon(new ImageIcon(imageURL));
		imageURL = ClassLoader.getSystemResource(urlFragment + "_pressed.png");
		b.setPressedIcon(new ImageIcon(imageURL));		
		
		b.setFocusPainted(true);
		b.setToolTipText(toolTipString);
		b.setMargin(new Insets(0, 0, 0, 0));	
		b.setAlignmentX(Component.CENTER_ALIGNMENT);
		b.setBorderPainted(false);
		b.addActionListener(this);
		b.setContentAreaFilled(false);

		this.add(b);
	}
	
	
	
	/**
	 * @effects Handles all user-interactions with the GUI.
	 */
	public void actionPerformed(ActionEvent evt) {
		int oldCollisionCount = Arena3DScene.collisionCount;
		boolean newPieceAdded = false;
		
		// Button Actions
		JButton bSource = (JButton) evt.getSource();

		if (bSource.getToolTipText().equals("Add Ball 3D")) {
			pf = new BallFactory();
			p = pf.makePiece(pieceSpawnLocation);									
			p.addPiece();
			p.enablePieceBehaviors();
			newPieceAdded = true;			
		}
		
		if (bSource.getToolTipText().equals("Add Cubical Bumper 3D")) {
			pf = new CubicalBumperFactory();
			p = pf.makePiece(pieceSpawnLocation);									
			p.addPiece();
			p.enablePieceBehaviors();
			newPieceAdded = true;
		}

		if (bSource.getToolTipText().equals("Add Spherical Bumper 3D")) {
			pf = new SphericalBumperFactory();
			p = pf.makePiece(pieceSpawnLocation);									
			p.addPiece();
			p.enablePieceBehaviors();
			newPieceAdded = true;
		}
		
		if (bSource.getToolTipText().equals("Add Triangular Bumper 3D Type A")) {
			pf = new TriangularBumperAFactory();
			p = pf.makePiece(pieceSpawnLocation);							
			p.addPiece();
			p.enablePieceBehaviors();
			newPieceAdded = true;
		}
		
		if (bSource.getToolTipText().equals("Add Triangular Bumper 3D Type B")) {
			pf = new TriangularBumperBFactory();
			p = pf.makePiece(pieceSpawnLocation);							
			p.addPiece();
			p.enablePieceBehaviors();
			newPieceAdded = true;
		}
		
		if (bSource.getToolTipText().equals("Add Triangular Bumper 3D Type C")) {
			pf = new TriangularBumperCFactory();
			p = pf.makePiece(pieceSpawnLocation);							
			p.addPiece();
			p.enablePieceBehaviors();
			newPieceAdded = true;
		}
		
		if (bSource.getToolTipText().equals("Add Flipper 3D")) {
			pf = new FlipperFactory();
			p = pf.makePiece(pieceSpawnLocation);									
			p.addPiece();
			p.enablePieceBehaviors();
			newPieceAdded = true;
		}
		
		if (bSource.getToolTipText().equals("Add Absorber 3D")) {
			pf = new AbsorberFactory();
			p = pf.makePiece(pieceSpawnLocation);									
			p.addPiece();
			p.enablePieceBehaviors();
			newPieceAdded = true;
		}
		
		if (bSource.getToolTipText().equals("Add Jezzmo 3D")) {
			pf = new JezzmoFactory();
			p = pf.makePiece(pieceSpawnLocation);									
			p.addPiece();
			p.enablePieceBehaviors();
			newPieceAdded = true;
		}

			try {

				boolean doesIntersect = false;

				if ((newPieceAdded == true) && p != null) {

					LinkedList boundingBoxes = new LinkedList();
					for (int i = 0; i < Arena3DScene.allBalls.size(); i++) {
						Piece g = (Piece) Arena3DScene.allBalls.get(i);
						if (g.equals(p)) {
							continue;
						}
						boundingBoxes.add(g.getContainerAbsoluteCoord());
					}
					for (int j = 0; j < Arena3DScene.allGizmos.size(); j++) {
						Piece g = (Piece) Arena3DScene.allGizmos.get(j);
						if ((g instanceof OuterWall3D) || g.equals(p)) {
							continue;
						}
						boundingBoxes.add(g.getContainerAbsoluteCoord());
					}

					Iterator boundingBoxesIter = boundingBoxes.iterator();
					while (boundingBoxesIter.hasNext() && !(doesIntersect)) {
						doesIntersect =
							p.getContainerAbsoluteCoord().intersect(
								(Bounds) boundingBoxesIter.next());
					}

				}

				if ((newPieceAdded == true)
					&& (p != null)
					&& (doesIntersect == true)) {
					p.removePiece();
					JOptionPane.showMessageDialog(
						this,
						"Sorry, you can't add a new gizmo at that location since another gizmo is already there.\nPlease move the existing gizmo away if you want to add a new gizmo there.",
						"You Can't Put a Gizmo There",
						JOptionPane.ERROR_MESSAGE);
				}
				if ((newPieceAdded == true)
					&& (p != null)
					&& (doesIntersect == false)) {
					// Show the properties panel of the Piece you just added.
					MainFrame.ppPanel.inputPiece(p);
					MainFrame.bottomPropertiesCardLayout.show(
						MainFrame.bottomPropertiesPanels,
						"PiecePropertiesPanel");
				}
			} catch (Exception e) {

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
