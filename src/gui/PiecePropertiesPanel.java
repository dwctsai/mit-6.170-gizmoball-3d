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

import javax.media.j3d.Transform3D;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import plugins.pieces.Absorber3D;
import plugins.pieces.Ball3D;
import plugins.pieces.CubicalBumper3D;
import plugins.pieces.Flipper3D;
import plugins.pieces.Jezzmo3D;
import plugins.pieces.SphericalBumper3D;
import plugins.pieces.TriangularBumperA3D;
import plugins.pieces.TriangularBumperB3D;
import plugins.pieces.TriangularBumperC3D;
import backend.Piece;
import backend.event.GizmoListener;

/**
 * PiecePropertiesPanel displays the properties of the currently selected gizmo. 
 */
public class PiecePropertiesPanel extends JPanel implements ActionListener {
	// FIELDS
	private Graphics g;
	static final int WIDTH = 700;
	static final int HEIGHT = 250;
	private ImageIcon bgImage;	
	
	public JTextField xLocationTF;
	public JTextField yLocationTF;
	public JTextField zLocationTF;
	public JTextField xLengthTF;
	public JTextField yLengthTF;
	public JTextField zLengthTF;
	public JTextField xVelocityTF;
	public JTextField yVelocityTF;
	public JTextField zVelocityTF;
	public JTextField radiusTF;
	public JTextField massTF;
	public JTextField corTF;
	public JButton xyCWRotate;
	public JButton xzCWRotate;
	public JButton yzCWRotate;
	public JButton xyCCWRotate;
	public JButton xzCCWRotate;
	public JButton yzCCWRotate;
	public JCheckBox indestructableCheckBox;
	public JButton orientationButton;
	public JButton appButton;
	public JButton triggersButton;
	public JButton removeButton;
	public JButton updateButton;
	public JLabel pieceIconLB;
	public URL pieceIconURL;
	
	public ImageIcon iconBall;
	public ImageIcon iconCube;
	public ImageIcon iconSphere;
	public ImageIcon iconTriangleA;
	public ImageIcon iconTriangleB;
	public ImageIcon iconTriangleC;
	public ImageIcon iconAbsorber;
	public ImageIcon iconFlipper;
	public ImageIcon iconJezzmo;

	 
	public static Piece p;
	private Transform3D tempVelocityT3D;
	private Vector3d tempVelocityVec = new Vector3d();
	public AppearanceDialog appDiag;
    public static TriggersConnectionsDialog connDiag;
		
	// CONSTRUCTOR	
	/**
	* @effects Sets up the panel.
	*/
	public PiecePropertiesPanel() {
		// Set the size.
		this.setPreferredSize(new Dimension(WIDTH, HEIGHT));

		// Set the opacity.
		this.setOpaque(true);

		// Set the layout and border.	
		this.setLayout(null);		

		// Add the content components.
		JLabel xLocationLB = new JLabel("X:");
		xLocationLB.setBounds(243, 21, 30, 20);
		add(xLocationLB);
		
		JLabel yLocationLB = new JLabel("Y:");
		yLocationLB.setBounds(243, 45, 30, 20);
		add(yLocationLB);
		
		JLabel zLocationLB = new JLabel("Z:");
		zLocationLB.setBounds(243, 70, 30, 20);
		add(zLocationLB);		
				
		JLabel corLB = new JLabel("COR:");
		corLB.setBounds(243, 93, 64, 20);
		add(corLB);
				
		xLocationTF = new JTextField("    ");
		xLocationTF.setBounds(276, 21, 40, 20);
		add(xLocationTF);

		yLocationTF = new JTextField("    ");
		yLocationTF.setBounds(276, 45, 40, 20);
		add(yLocationTF);	
		
		zLocationTF = new JTextField("    ");
		zLocationTF.setBounds(276, 70, 40, 20);
		add(zLocationTF);
		
		corTF = new JTextField("    ");
		corTF.setBounds(276, 95, 40, 20);
		add(corTF);
				
		
		
		JLabel xLengthLB = new JLabel("Length:");
		xLengthLB.setBounds(330, 21, 70, 20);
		add(xLengthLB);
		
		JLabel yLengthLB = new JLabel("Length:");
		yLengthLB.setBounds(330, 45, 70, 20);
		add(yLengthLB);
		
		JLabel zLengthLB = new JLabel("Length:");
		zLengthLB.setBounds(330, 70, 70, 20);
		add(zLengthLB);			
		
		JLabel radiusLB = new JLabel("Radius:");
		radiusLB.setBounds(330, 93, 70, 20);
		add(radiusLB);	
		
		xLengthTF = new JTextField("    ");
		xLengthTF.setBounds(375, 21, 40, 20);
		add(xLengthTF);

		yLengthTF = new JTextField("    ");
		yLengthTF.setBounds(375, 45, 40, 20);
		add(yLengthTF);	
		
		zLengthTF = new JTextField("    ");
		zLengthTF.setBounds(375, 70, 40, 20);
		add(zLengthTF);		
		
		radiusTF = new JTextField("    ");
		radiusTF.setBounds(375, 95, 40, 20);
		add(radiusTF);



		JLabel xVelocityLB = new JLabel("Velocity:");
		xVelocityLB.setBounds(430, 21, 70, 20);
		add(xVelocityLB);

		JLabel yVelocityLB = new JLabel("Velocity:");
		yVelocityLB.setBounds(430, 45, 70, 20);
		add(yVelocityLB);
		
		JLabel zVelocityLB = new JLabel("Velocity:");
		zVelocityLB.setBounds(430, 70, 70, 20);
		add(zVelocityLB);		

	
		JLabel massLB = new JLabel("Mass:");
		massLB.setBounds(430, 93, 70, 20);
		add(massLB);
		
		xVelocityTF = new JTextField("    ");
		xVelocityTF.setBounds(485, 21, 40, 20);
		add(xVelocityTF);
		
		yVelocityTF = new JTextField("    ");
		yVelocityTF.setBounds(485, 45, 40, 20);
		add(yVelocityTF);
		
		zVelocityTF = new JTextField("    ");
		zVelocityTF.setBounds(485, 70, 40, 20);
		add(zVelocityTF);

		massTF = new JTextField("    ");
		massTF.setBounds(485, 95, 40, 20);
		add(massTF);	
		

		xyCWRotate = createImageButton("CWXY", "Rotate 90 Degrees Clockwise XY", 240, 116, 60, 60);
		yzCWRotate = createImageButton("CWYZ", "Rotate 90 Degrees Clockwise YZ", 290, 116, 60, 60);
		xzCWRotate = createImageButton("CWXZ", "Rotate 90 Degrees Clockwise XZ", 340, 116, 60, 60);
				
		xyCCWRotate = createImageButton("CCWXY", "Rotate 90 Degrees Counterclockwise XY", 240, 166, 60, 60);
		yzCCWRotate = createImageButton("CCWYZ", "Rotate 90 Degrees Counterclockwise YZ", 290, 166, 60, 60);
		xzCCWRotate = createImageButton("CCWXZ", "Rotate 90 Degrees Counterclockwise XZ", 340, 166, 60, 60);
		
		indestructableCheckBox = createImageCheckBox("Indestructible", "Toggle Indestructibility", 540, 24, 60, 60);
		orientationButton = createImageButton("Orientation", "Change this Gizmo's Orientation", 596, 24, 60, 60);
		

		appButton = createImageButton("Appearance", "Change this Gizmo's Appearance", 540, 77, 60, 60);
		triggersButton = createImageButton("Triggers", "Set this Gizmo's Event Triggers", 596, 77, 60, 60);		
		
		removeButton = createImageButton("Remove", "Remove this Gizmo", 438, 166, 60, 60);
		updateButton = createImageButton("Update", "Update your changes!", 536, 169, 130, 60);	


		// Wireframe icon.		
		pieceIconURL = ClassLoader.getSystemResource("gui/images/wireframe-ball.gif");
		iconBall = new ImageIcon(pieceIconURL);
		pieceIconURL = ClassLoader.getSystemResource("gui/images/wireframe-cube.gif");
		iconCube = new ImageIcon(pieceIconURL);
		pieceIconURL = ClassLoader.getSystemResource("gui/images/wireframe-sphere.gif");
		iconSphere = new ImageIcon(pieceIconURL);		
		pieceIconURL = ClassLoader.getSystemResource("gui/images/wireframe-triangle-a.gif");
		iconTriangleA = new ImageIcon(pieceIconURL);		
		pieceIconURL = ClassLoader.getSystemResource("gui/images/wireframe-triangle-b.gif");
		iconTriangleB = new ImageIcon(pieceIconURL);
		pieceIconURL = ClassLoader.getSystemResource("gui/images/wireframe-triangle-b.gif");
		iconTriangleC = new ImageIcon(pieceIconURL);
		pieceIconURL = ClassLoader.getSystemResource("gui/images/wireframe-absorber.gif");
		iconAbsorber = new ImageIcon(pieceIconURL);		
		pieceIconURL = ClassLoader.getSystemResource("gui/images/wireframe-flipper.gif");
		iconFlipper = new ImageIcon(pieceIconURL);		
		pieceIconURL = ClassLoader.getSystemResource("gui/images/wireframe-jezzmo.gif");
		iconJezzmo = new ImageIcon(pieceIconURL);				
		
		
		pieceIconLB = new JLabel();
		pieceIconLB.setIcon(iconBall);		
		pieceIconLB.setBounds(50, 50, iconBall.getIconWidth(), iconBall.getIconHeight());		
		add(pieceIconLB);
		
		


		// Load the background image for paintComponent().
		URL bgImageURL = ClassLoader.getSystemResource("gui/images/bg_PiecePropertiesPanel.png");
		bgImage = new ImageIcon(bgImageURL);
		
		// Draw this panel.
		this.setVisible(true);
		
	}

	// GIZMO MANIPULATION METHODS
	/**
	 * @effects Parses and processes a give gizmoball game piece and updates
	 *          its information in 'this' accordingly.
	 */
	public void inputPiece(Piece p) {
		this.p = p;
		
		Point3d pLPos = p.getContainerLowerLocation();
		Point3d pUPos = p.getContainerUpperLocation();		
		
		xLocationTF.setText("" + pLPos.x);
		yLocationTF.setText("" + pLPos.y);
		zLocationTF.setText("" + pLPos.z);
		
		xLocationTF.setEnabled(false);
		yLocationTF.setEnabled(false);		
		zLocationTF.setEnabled(false);		
		
		xLengthTF.setText("" + (pUPos.x - pLPos.x));
		yLengthTF.setText("" + (pUPos.y - pLPos.y));
		zLengthTF.setText("" + (pUPos.z - pLPos.z));
		
		xLengthTF.setEnabled(false);
		yLengthTF.setEnabled(false);
		zLengthTF.setEnabled(false);
		
		
		if (p instanceof Ball3D) {
			tempVelocityT3D = ((Ball3D) p).getVelocity();
			tempVelocityT3D.get(tempVelocityVec);

			xVelocityTF.setEnabled(true);
			xVelocityTF.setText("" + tempVelocityVec.x);
			yVelocityTF.setEnabled(true);			
			yVelocityTF.setText("" + tempVelocityVec.y);
			zVelocityTF.setEnabled(true);
			zVelocityTF.setText("" + tempVelocityVec.z);
			
			corTF.setEnabled(false);
			corTF.setText("");
			radiusTF.setEnabled(false);			
			radiusTF.setText("" + ((Ball3D) p).getRadius());
			massTF.setEnabled(true);
			massTF.setText("" + ((Ball3D) p).getMass());

			indestructableCheckBox.setEnabled(false);
			orientationButton.setEnabled(false);			
			triggersButton.setEnabled(false);
			
			xyCWRotate.setEnabled(false);
			yzCWRotate.setEnabled(false);
			xzCWRotate.setEnabled(false);
			xyCCWRotate.setEnabled(false);
			yzCCWRotate.setEnabled(false);
			xzCCWRotate.setEnabled(false);
			
			
			pieceIconLB.setIcon(iconBall);				
		}
		
		if (p instanceof CubicalBumper3D) {
			xVelocityTF.setEnabled(false);
			xVelocityTF.setText("");
			yVelocityTF.setEnabled(false);			
			yVelocityTF.setText("");
			zVelocityTF.setEnabled(false);			
			zVelocityTF.setText("");
			
			corTF.setEnabled(true);
			corTF.setText("" + ((CubicalBumper3D) p).getReflectCoeff());
			radiusTF.setEnabled(false);
			radiusTF.setText("");
			massTF.setEnabled(false);
			massTF.setText("");
			
			indestructableCheckBox.setEnabled(false);
			orientationButton.setEnabled(false);
			triggersButton.setEnabled(true);			
			
			xyCWRotate.setEnabled(false);
			yzCWRotate.setEnabled(false);
			xzCWRotate.setEnabled(false);
			xyCCWRotate.setEnabled(false);
			yzCCWRotate.setEnabled(false);
			xzCCWRotate.setEnabled(false);
			
			
			pieceIconLB.setIcon(iconCube);
		}
		
		if (p instanceof SphericalBumper3D) {
			xVelocityTF.setEnabled(false);
			xVelocityTF.setText("");
			yVelocityTF.setEnabled(false);			
			yVelocityTF.setText("");
			zVelocityTF.setEnabled(false);			
			zVelocityTF.setText("");
			
			corTF.setEnabled(true);
			corTF.setText("" + ((SphericalBumper3D) p).getReflectCoeff());
			radiusTF.setEnabled(false);
			radiusTF.setText("" + ((SphericalBumper3D) p).getRadius());
			massTF.setEnabled(false);
			massTF.setText("");
			
			indestructableCheckBox.setEnabled(false);
			orientationButton.setEnabled(false);
			triggersButton.setEnabled(true);			
			
			xyCWRotate.setEnabled(false);
			yzCWRotate.setEnabled(false);
			xzCWRotate.setEnabled(false);
			xyCCWRotate.setEnabled(false);
			yzCCWRotate.setEnabled(false);
			xzCCWRotate.setEnabled(false);
			
			
			pieceIconLB.setIcon(iconSphere);			
		}
		
		
		if (p instanceof TriangularBumperA3D) {
			xVelocityTF.setEnabled(false);
			xVelocityTF.setText("");
			yVelocityTF.setEnabled(false);			
			yVelocityTF.setText("");
			zVelocityTF.setEnabled(false);			
			zVelocityTF.setText("");
			
			corTF.setEnabled(true);
			corTF.setText("" + ((TriangularBumperA3D) p).getReflectCoeff());
			radiusTF.setEnabled(false);
			radiusTF.setText("");
			massTF.setEnabled(false);
			massTF.setText("");
			
			indestructableCheckBox.setEnabled(false);
			orientationButton.setEnabled(false);
			triggersButton.setEnabled(true);			
			
			xyCWRotate.setEnabled(true);
			yzCWRotate.setEnabled(true);
			xzCWRotate.setEnabled(true);
			xyCCWRotate.setEnabled(true);
			yzCCWRotate.setEnabled(true);
			xzCCWRotate.setEnabled(true);
			
			
			pieceIconLB.setIcon(iconTriangleA);			
		}
		
		if (p instanceof TriangularBumperB3D) {
			xVelocityTF.setEnabled(false);
			xVelocityTF.setText("");
			yVelocityTF.setEnabled(false);			
			yVelocityTF.setText("");
			zVelocityTF.setEnabled(false);			
			zVelocityTF.setText("");
			
			corTF.setEnabled(true);
			corTF.setText("" + ((TriangularBumperB3D) p).getReflectCoeff());
			radiusTF.setEnabled(false);
			radiusTF.setText("");
			massTF.setEnabled(false);
			massTF.setText("");
			
			indestructableCheckBox.setEnabled(false);
			orientationButton.setEnabled(false);
			triggersButton.setEnabled(true);			
			
			xyCWRotate.setEnabled(true);
			yzCWRotate.setEnabled(true);
			xzCWRotate.setEnabled(true);
			xyCCWRotate.setEnabled(true);
			yzCCWRotate.setEnabled(true);
			xzCCWRotate.setEnabled(true);
			
			
			pieceIconLB.setIcon(iconTriangleB);					
		}
		
		if (p instanceof TriangularBumperC3D) {
			xVelocityTF.setEnabled(false);
			xVelocityTF.setText("");
			yVelocityTF.setEnabled(false);			
			yVelocityTF.setText("");
			zVelocityTF.setEnabled(false);			
			zVelocityTF.setText("");
			
			corTF.setEnabled(true);
			corTF.setText("" + ((TriangularBumperC3D) p).getReflectCoeff());
			radiusTF.setEnabled(false);
			radiusTF.setText("");
			massTF.setEnabled(false);
			massTF.setText("");
			
			indestructableCheckBox.setEnabled(false);
			orientationButton.setEnabled(false);
			triggersButton.setEnabled(true);			
			
			xyCWRotate.setEnabled(true);
			yzCWRotate.setEnabled(true);
			xzCWRotate.setEnabled(true);
			xyCCWRotate.setEnabled(true);
			yzCCWRotate.setEnabled(true);
			xzCCWRotate.setEnabled(true);
			

			pieceIconLB.setIcon(iconTriangleC);					
		}
		
		
		if (p instanceof Absorber3D) {
			xVelocityTF.setEnabled(false);
			xVelocityTF.setText("");
			yVelocityTF.setEnabled(false);			
			yVelocityTF.setText("");
			zVelocityTF.setEnabled(false);			
			zVelocityTF.setText("");
			
			corTF.setEnabled(false);
			corTF.setText("" + ((Absorber3D) p).getReflectCoeff());
			radiusTF.setEnabled(false);
			radiusTF.setText("");
			massTF.setEnabled(false);
			massTF.setText("");
			
			indestructableCheckBox.setEnabled(false);
			orientationButton.setEnabled(false);
			triggersButton.setEnabled(true);			
			
			xyCWRotate.setEnabled(false);
			yzCWRotate.setEnabled(false);
			xzCWRotate.setEnabled(false);
			xyCCWRotate.setEnabled(false);
			yzCCWRotate.setEnabled(false);
			xzCCWRotate.setEnabled(false);
			
			
			pieceIconLB.setIcon(iconAbsorber);					
		}		
		
		if (p instanceof Flipper3D) {
			xVelocityTF.setEnabled(false);
			xVelocityTF.setText("");
			yVelocityTF.setEnabled(false);			
			yVelocityTF.setText("");
			zVelocityTF.setEnabled(false);			
			zVelocityTF.setText("");
			
			corTF.setEnabled(true);
			corTF.setText("" + ((Flipper3D) p).getReflectCoeff());
			radiusTF.setEnabled(false);
			radiusTF.setText("");
			massTF.setEnabled(false);
			massTF.setText("");
			
			indestructableCheckBox.setEnabled(false);
			orientationButton.setEnabled(false);
			triggersButton.setEnabled(true);			
			
			xyCWRotate.setEnabled(true);
			yzCWRotate.setEnabled(true);
			xzCWRotate.setEnabled(true);
			xyCCWRotate.setEnabled(true);
			yzCCWRotate.setEnabled(true);
			xzCCWRotate.setEnabled(true);
			
			
			pieceIconLB.setIcon(iconFlipper);					
		}
		
		if (p instanceof Jezzmo3D) {
			tempVelocityT3D = ((Jezzmo3D) p).getVelocity();
			tempVelocityT3D.get(tempVelocityVec);

			xVelocityTF.setEnabled(true);
			xVelocityTF.setText("" + tempVelocityVec.x);
			yVelocityTF.setEnabled(true);			
			yVelocityTF.setText("" + tempVelocityVec.y);
			zVelocityTF.setEnabled(true);
			zVelocityTF.setText("" + tempVelocityVec.z);			
			
			corTF.setEnabled(true);
			corTF.setText("" + ((Jezzmo3D) p).getReflectCoeff());
			radiusTF.setEnabled(false);
			radiusTF.setText("");
			massTF.setEnabled(false);
			massTF.setText("");
			
			indestructableCheckBox.setEnabled(false);
			orientationButton.setEnabled(true);
			triggersButton.setEnabled(true);			
			
			xyCWRotate.setEnabled(false);
			yzCWRotate.setEnabled(false);
			xzCWRotate.setEnabled(false);
			xyCCWRotate.setEnabled(false);
			yzCCWRotate.setEnabled(false);
			xzCCWRotate.setEnabled(false);
			
			
			pieceIconLB.setIcon(iconJezzmo);					
		}
		
	}
	
	
	/**
	 * @effects Updates user changes to the gizmoball game piece.
	 */
	public void updatePiece() {
		try {
		
		if (p instanceof Ball3D) {
			double xVelocity = Double.parseDouble(xVelocityTF.getText());
			double yVelocity = Double.parseDouble(yVelocityTF.getText());
			double zVelocity = Double.parseDouble(zVelocityTF.getText());
			
			Transform3D tempVelocityT3D = new Transform3D();
			tempVelocityT3D.set(new Vector3d(xVelocity, yVelocity, zVelocity));
			((Ball3D) p).setVelocity(tempVelocityT3D);
			
			double mass = Double.parseDouble(massTF.getText());
			((Ball3D) p).setMass(mass);
		
		}
		
		if (p instanceof CubicalBumper3D) {
			double cor = Double.parseDouble(corTF.getText());
			((CubicalBumper3D) p).setReflectCoeff(cor);

		}
		
		if (p instanceof SphericalBumper3D) {
			double cor = Double.parseDouble(corTF.getText());
			((SphericalBumper3D) p).setReflectCoeff(cor);			
		}
		
		
		if (p instanceof TriangularBumperA3D) {
			double cor = Double.parseDouble(corTF.getText());
			((TriangularBumperA3D) p).setReflectCoeff(cor);
		}
		
		if (p instanceof TriangularBumperB3D) {
			double cor = Double.parseDouble(corTF.getText());
			((TriangularBumperB3D) p).setReflectCoeff(cor);
		}
		
		if (p instanceof TriangularBumperC3D) {
			double cor = Double.parseDouble(corTF.getText());
			((TriangularBumperC3D) p).setReflectCoeff(cor);
		}
		
		
		if (p instanceof Absorber3D) {

		}		
		
		if (p instanceof Flipper3D) {
			double cor = Double.parseDouble(corTF.getText());
			((Flipper3D) p).setReflectCoeff(cor);			

		}
		
		if (p instanceof Jezzmo3D) {
			double cor = Double.parseDouble(corTF.getText());
			((Jezzmo3D) p).setReflectCoeff(cor);
		}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(
					this,
					"Please input in a number for the text fields.",
					"Bad Input",
					JOptionPane.ERROR_MESSAGE);			
		}
			
	}	




	// UTILITY/SYSTEM METHODS
	/**
	 * @effects Creates a new image button from the given parameters and
	 *          adds it to this.
	 * @return The created button.
	 */
	private JButton createImageButton(String urlFragment, String toolTipString,
								int x, int y, int width, int height) {
		JButton b = new JButton();
		urlFragment = "gui/images/properties_" + urlFragment;
		URL imageURL = ClassLoader.getSystemResource(urlFragment + ".png");
		b.setIcon(new ImageIcon(imageURL));
		imageURL = ClassLoader.getSystemResource(urlFragment + "_pressed.png");
		b.setRolloverIcon(new ImageIcon(imageURL));
		imageURL = ClassLoader.getSystemResource(urlFragment + "_pressed.png");
		b.setPressedIcon(new ImageIcon(imageURL));
		imageURL = ClassLoader.getSystemResource(urlFragment + "_disabled.png");
		b.setDisabledIcon(new ImageIcon(imageURL));
		
		b.setFocusPainted(false);
		b.setToolTipText(toolTipString);
		b.setMargin(new Insets(0, 0, 0, 0));	
		b.setAlignmentX(Component.CENTER_ALIGNMENT);
		b.setBorderPainted(false);
		b.addActionListener(this);
		b.setContentAreaFilled(false);

		b.setBounds(x, y, width, height);
		this.add(b);
		
		return b;
	}
	
	
	/**
	 * @effects Creates a new image checkbox from the given parameters and
	 *          adds it to this.
	 * @return The created checkbox.
	 */
	private JCheckBox createImageCheckBox(String urlFragment, String toolTipString,
								int x, int y, int width, int height) {

		JCheckBox cb = new JCheckBox();
		urlFragment = "gui/images/properties_" + urlFragment;		
		URL imageURL = ClassLoader.getSystemResource(urlFragment + "Off.png");
		cb.setIcon(new ImageIcon(imageURL));
		imageURL = ClassLoader.getSystemResource(urlFragment + "Off_pressed.png");
		cb.setRolloverIcon(new ImageIcon(imageURL));
		imageURL = ClassLoader.getSystemResource(urlFragment + "On.png");
		cb.setSelectedIcon(new ImageIcon(imageURL));
		imageURL = ClassLoader.getSystemResource(urlFragment + "On_pressed.png");
		cb.setRolloverSelectedIcon(new ImageIcon(imageURL));
		imageURL = ClassLoader.getSystemResource(urlFragment + "Off_disabled.png");
		cb.setDisabledIcon(new ImageIcon(imageURL));
		imageURL = ClassLoader.getSystemResource(urlFragment + "On_disabled.png");
		cb.setDisabledSelectedIcon(new ImageIcon(imageURL));

		cb.setSelected(true);
		cb.setToolTipText(toolTipString);
		cb.setMargin(new Insets(0, 0, 0, 0));	
		cb.setAlignmentX(Component.CENTER_ALIGNMENT);		
		cb.setBorderPainted(false);
		cb.addActionListener(this);
		cb.setContentAreaFilled(false);		
		
		cb.setBounds(x, y, width, height);
		this.add(cb);		
		
		return cb;
	}
	
	
	
	/**
	 * @effects Handles all user-interactions with the GUI.
	 **/
	public void actionPerformed(ActionEvent evt) {
		
		// Allow Key Presses to be active
		PieceProperties.getEventHandler().requestFocus(true);


		// Button Actions
		if (evt.getSource() instanceof JButton) {
			JButton buttonSource = (JButton) evt.getSource();
			
			if (buttonSource.getToolTipText().equals("Rotate 90 Degrees Clockwise XY")) {
				if ((Arena3DScene.currentlyPickedTG != null) && (p != null)) {
					p.rotateXY90Graphics();
				}
			}
			
			if (buttonSource.getToolTipText().equals("Rotate 90 Degrees Clockwise YZ")) {
				if ((Arena3DScene.currentlyPickedTG != null) && (p != null)) {
					p.rotateYZ90Graphics();				
				}
			}
			
			if (buttonSource.getToolTipText().equals("Rotate 90 Degrees Clockwise XZ")) {
				if ((Arena3DScene.currentlyPickedTG != null) && (p != null)) {
					p.rotateXZ90Graphics();				
				}
			}
			
			if (buttonSource.getToolTipText().equals("Rotate 90 Degrees Counterclockwise XY")) {
				if ((Arena3DScene.currentlyPickedTG != null) && (p != null)) {
					p.rotateYX90Graphics();				
				}
			}
			
			if (buttonSource.getToolTipText().equals("Rotate 90 Degrees Counterclockwise YZ")) {
				if ((Arena3DScene.currentlyPickedTG != null) && (p != null)) {
					p.rotateZY90Graphics();				
				}
			}
			
			if (buttonSource.getToolTipText().equals("Rotate 90 Degrees Counterclockwise XZ")) {
				if ((Arena3DScene.currentlyPickedTG != null) && (p != null)) {
					p.rotateZX90Graphics();		
				}
			}
			
			if (buttonSource.getToolTipText().equals("Toggle Indestructibility")) {
				if ((Arena3DScene.currentlyPickedTG != null) && (p != null)) {
					//TODO: FILL-IN				
				}
			}
			
			if (buttonSource.getToolTipText().equals("Change this Gizmo's Orientation")) {
				if ((Arena3DScene.currentlyPickedTG != null) && (p != null)) {
					if (p instanceof Jezzmo3D) {

								
						Object[] options = {"x-direction",
											"y-direction",
											"z-direction"};
						int n = JOptionPane.showOptionDialog(this,
							"Change this gizmo's orientation.",
							"Gizmo Orientation",
							JOptionPane.YES_NO_CANCEL_OPTION,
							JOptionPane.QUESTION_MESSAGE,
							null,
							options,
							options[2]);
								
						if (n == JOptionPane.YES_OPTION) {
							((Jezzmo3D)p).setGrowthDirection(Jezzmo3D.X_GROWTH);
						} else if (n == JOptionPane.NO_OPTION) {
							((Jezzmo3D)p).setGrowthDirection(Jezzmo3D.Y_GROWTH);
						} else if (n == JOptionPane.CANCEL_OPTION) {
							((Jezzmo3D)p).setGrowthDirection(Jezzmo3D.Z_GROWTH);
						} else {
							// DO NOTHING
						}
					}
								
				}
			}
			
			if (buttonSource.getToolTipText().equals("Change this Gizmo's Appearance")) {
				if ((Arena3DScene.currentlyPickedTG != null) && (p != null)) {
					appDiag = new AppearanceDialog(new JFrame(), "Gizmo Appearance Settings", false, p);
				}
			}
			
			if (buttonSource.getToolTipText().equals("Set this Gizmo's Event Triggers")) {
				if ((Arena3DScene.currentlyPickedTG != null) && (p != null)) {
					connDiag = new TriggersConnectionsDialog(new JFrame(), "Gizmo Connections Settings", false, (GizmoListener) p);
				}
			}			

			if (buttonSource.getToolTipText().equals("Remove this Gizmo")) {
				if ((Arena3DScene.currentlyPickedTG != null) && (p != null)) {
					System.out.println("p:" + p);
					p.removePiece();
					p = null;
				}
			}

			if (buttonSource.getToolTipText().equals("Update your changes!")) {
				if ((Arena3DScene.currentlyPickedTG != null) && (p != null)) {
					updatePiece();
				}
			}				
			
		}


	

	} // actionPerformed		
	
	
	
	
	 
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
