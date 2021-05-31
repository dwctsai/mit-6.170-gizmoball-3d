/* @author David Tsai
 * @created on May 10, 2003
 */
package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.media.j3d.Appearance;
import javax.media.j3d.Material;
import javax.media.j3d.TextureAttributes;
import javax.media.j3d.TransparencyAttributes;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.vecmath.Color3f;

import backend.Piece;
import backend.stream.TextureFileFilter;
import backend.stream.TextureFilePreview;

import com.sun.j3d.utils.image.TextureLoader;

/**
 * AppearanceDialog - The user can use this dialog to modify the appearance of
 * a gizmoball game piece.
 */
public class AppearanceDialog extends JDialog {
	// FIELDS
	private Piece thePiece;
	private Appearance thePieceApp;
	
	public ColorPropertiesPanel cpp;
	public MiscAppPropertiesPanel mpp;
	public TexturePropertiesPanel tpp;
	public CommandButtonsPanel cbp;
	
	private JFileChooser fc;
	
	// CONSTRUCTOR
	/**
	 * @effects  
	 */
	public AppearanceDialog(JFrame jf, String title, boolean modality, Piece thePiece) {
		super(jf, title, modality);

		this.thePiece = thePiece;
		this.thePieceApp = thePiece.getAppearance();

		this.getContentPane().setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
		((JPanel) this.getContentPane()).setBorder(BorderFactory.createEmptyBorder(4,4,4,4));		

		cpp = new ColorPropertiesPanel();
		mpp = new MiscAppPropertiesPanel();
		tpp = new TexturePropertiesPanel();
		cbp = new CommandButtonsPanel();
		
		this.getContentPane().add(cpp);
		this.getContentPane().add(mpp);
		this.getContentPane().add(tpp);
		this.getContentPane().add(cbp);

		this.setResizable(false);
		Point p = Gizmoball.theMainFrame.getLocationOnScreen();
		this.setLocation(p.x + 200, p.y + 200);  // starting location of the dialog box
		this.pack();
		this.setVisible(true);
	}

	// METHODS
	/**
	 * @effects Commits the new settings.
	 */
	public void setCommit() {
		Appearance newApp = new Appearance();
		newApp.setCapability(Appearance.ALLOW_MATERIAL_READ);
		newApp.setCapability(Appearance.ALLOW_MATERIAL_WRITE);
		newApp.setCapability(Appearance.ALLOW_TRANSPARENCY_ATTRIBUTES_READ);
		newApp.setCapability(Appearance.ALLOW_TRANSPARENCY_ATTRIBUTES_WRITE);		
		newApp.setCapability(Appearance.ALLOW_POLYGON_ATTRIBUTES_READ);
		newApp.setCapability(Appearance.ALLOW_POLYGON_ATTRIBUTES_WRITE);
		newApp.setCapability(Appearance.ALLOW_RENDERING_ATTRIBUTES_READ);
		newApp.setCapability(Appearance.ALLOW_RENDERING_ATTRIBUTES_WRITE);	
		
		
		if (!tpp.textureString.equals("")) {
			TextureLoader tex = new TextureLoader(tpp.textureString, null);
			newApp.setTexture(tex.getTexture());
			TextureAttributes texAttr = new TextureAttributes();
			texAttr.setTextureMode(TextureAttributes.MODULATE);
			newApp.setTextureAttributes(texAttr);
		}
		else { 
			Color3f newColor = new Color3f(cpp.selectedColor);
		
			Material m;
			if (mpp.specLightCB.isSelected()) {
				m = new Material(Arena3D.black, Arena3D.black, newColor, Arena3D.white, 80.0f);			
			}
			else {
				m = new Material(newColor, Arena3D.black, newColor, Arena3D.white, 80.0f);			
			}
			m.setCapability(Material.ALLOW_COMPONENT_READ);
			m.setCapability(Material.ALLOW_COMPONENT_WRITE);		
			m.setLightingEnable(true);
			newApp.setMaterial(m);

			if (mpp.transparencyValue != 0.0) {

				TransparencyAttributes ta = new TransparencyAttributes();
				ta.setCapability(TransparencyAttributes.ALLOW_VALUE_READ);
				ta.setCapability(TransparencyAttributes.ALLOW_VALUE_WRITE);
				ta.setTransparencyMode(TransparencyAttributes.BLENDED);
				ta.setTransparency((float) (mpp.transparencyValue / 100.0f));
				newApp.setTransparencyAttributes(ta);
			}
		}
		thePiece.setAppearance(newApp);
	}
	
	
	/**
	 * @return A slider from the given parameters. 
	 */
	public static JSlider createSlider(int min, int max, double init, 
								int majorTick, int minorTick,
								String toolTipString) {
		JSlider js = new JSlider(JSlider.HORIZONTAL, min, max, (int) init);
		js.setToolTipText(toolTipString);
			
		js.setMajorTickSpacing(majorTick);
		js.setMinorTickSpacing(minorTick);
		js.setPaintTicks(true);
		js.setPaintLabels(true);
			
		return js;
	}	
		
	
	
	// PANELS
	
	public class ColorPropertiesPanel extends JPanel implements ChangeListener {
		// FIELDS
		public int WIDTH = 450;
		public int HEIGHT = 380;
		
		public JColorChooser tcc;
		public Color selectedColor;
		

		/**
		 * @effects Sets up the panel.
		 */
		public ColorPropertiesPanel() {
			// Set the size.		
			this.setPreferredSize(new Dimension(WIDTH, HEIGHT));

			this.setBorder(new TitledBorder("Color Settings"));
			

			Material tempM = thePieceApp.getMaterial();
			Color3f tempColor3f = new Color3f();
			tempM.getDiffuseColor(tempColor3f);

			selectedColor = tempColor3f.get();
			
			// Set up the color chooser.
			tcc = new JColorChooser();
			tcc.setColor(selectedColor);
			tcc.getSelectionModel().addChangeListener(this);
			add(tcc);
		}

		
		/**
		 * @effects Handles color chooser state changes.
		 */
		public void stateChanged(ChangeEvent e) {
			selectedColor = tcc.getColor();
		}

	}

	
	public class MiscAppPropertiesPanel extends JPanel implements ChangeListener {
		// FIELDS
		public int WIDTH = 450;
		public int HEIGHT = 110;
		
		public JCheckBox specLightCB;
		public JSlider transparencySlider;
		public JLabel transparencyLB2;
		public double transparencyValue;

		/**
		 * @effects Sets up the panel.
		 */
		public MiscAppPropertiesPanel() {
			// Set the size.		
			this.setPreferredSize(new Dimension(WIDTH, HEIGHT));

			this.setLayout(null);
			this.setBorder(new TitledBorder("Miscellaneous Settings"));

			if (thePieceApp.getTransparencyAttributes() == null) {
				TransparencyAttributes ta = new TransparencyAttributes();
				ta.setCapability(TransparencyAttributes.ALLOW_VALUE_READ);
				ta.setCapability(TransparencyAttributes.ALLOW_VALUE_WRITE);				
				thePieceApp.setTransparencyAttributes(ta);
				transparencyValue = 0.0;
			}
			else if (thePieceApp.getTransparencyAttributes() != null) {
				transparencyValue = 100.0 * thePieceApp.getTransparencyAttributes().getTransparency();
			}


			JLabel specLightLB = new JLabel("Spectral Lighting:");
			specLightLB.setBounds(20, 20, 100, 20);
			add(specLightLB);
			
			specLightCB = new JCheckBox();
			specLightCB.setToolTipText("Toggle Spectral Lighting");
			specLightCB.setSelected(false);
			specLightCB.setBounds(130, 20, 100, 20);
			add(specLightCB);	
			
			JLabel transparencyLB = new JLabel("Transparency %:");
			transparencyLB.setBounds(20, 50, 100, 20);
			add(transparencyLB);

			transparencySlider = createSlider(0, 100, (int) transparencyValue, 20, 1, "Set Transparency");
			transparencySlider.setBounds(130, 50, 200, 50);
			transparencySlider.addChangeListener(this);
			add(transparencySlider);
			
			transparencyLB2 = new JLabel("" + transparencyValue);
			transparencyLB2.setBounds(350, 50, 50, 20);
			transparencyLB2.setBorder(new TitledBorder(""));
			add(transparencyLB2);
			
		}
		
		/**
		 * @effects Handles transparency slider changes.
		 */
		public void stateChanged(ChangeEvent e) {
			transparencyValue = transparencySlider.getValue();
			transparencyLB2.setText("" + transparencyValue);
		}


	}
	
	public class TexturePropertiesPanel extends JPanel implements ActionListener {
		// FIELDS
		public int WIDTH = 450;
		public int HEIGHT = 90;
		
		public JLabel loadedTextureLB;
		public String textureString = "";

		/**
		 * @effects Sets up the panel.
		 */
		public TexturePropertiesPanel() {
			// Set the size.		
			this.setPreferredSize(new Dimension(WIDTH, HEIGHT));

			this.setLayout(null);
			this.setBorder(new TitledBorder("Texture Settings"));


			JLabel specLightLB = new JLabel("Loaded Texture:");
			specLightLB.setBounds(20, 20, 100, 20);
			add(specLightLB);
			
			loadedTextureLB = new JLabel("None");
			loadedTextureLB.setBounds(130, 20, 225, 20);
			loadedTextureLB.setBorder(new TitledBorder(""));
			add(loadedTextureLB);
			
			
			JButton loadTextureButton = new JButton("Load...");
			loadTextureButton.setBounds(130, 50, 100, 25);
			loadTextureButton.addActionListener(this);
			add(loadTextureButton);

			
			JButton clearTextureButton = new JButton("Clear");
			clearTextureButton.setBounds(250, 50, 100, 25);
			clearTextureButton.addActionListener(this);
			add(clearTextureButton);

			
			
			
			// Setup the load/save file chooser and make it such that
			// the default directory when you open the save/load dialog is "Gizmoball/saved".
			fc = new JFileChooser(new File("gui/textures"));

			// add the custom file filter and disable the default "Accept All" file filter.
			fc.addChoosableFileFilter(new TextureFileFilter());
			fc.setAcceptAllFileFilterUsed(false);
			
			// Add preview pane.
			fc.setAccessory(new TextureFilePreview(fc));			
			
		}
		
		/**
		 * @effects Handles buttons actions. 
		 */
		public void actionPerformed(ActionEvent evt) {
			
			
			if (evt.getActionCommand().equals("Load...")) {

				int returnVal = fc.showDialog(this, "Load Gizmo Texture");
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fc.getSelectedFile();
					textureString = file.getPath();
					loadedTextureLB.setText(file.getName());
				}
				fc.setSelectedFile(null);

			}
			
			if (evt.getActionCommand().equals("Clear")) {
				loadedTextureLB.setText("None");
				textureString = "";
			}			
			
		}

	}
	
	
	public class CommandButtonsPanel extends JPanel implements ActionListener {
		// FIELDS
		private int WIDTH = 450;
		private int HEIGHT = 50;
		

		/**
		 * @effets Sets up the panel.
		 */		
		public CommandButtonsPanel() {
			this.setPreferredSize(new Dimension(WIDTH, HEIGHT));


			this.setLayout(null);

			JButton doneButton = new JButton("Done");
			doneButton.addActionListener(this);
			add(doneButton);
			doneButton.setBounds(100, 10, 100, 35);			
			
			JButton cancelButton = new JButton("Cancel");
			cancelButton.addActionListener(this);
			add(cancelButton);
			cancelButton.setBounds(250, 10, 100, 35);			
			
		}
		
		/**
		 * @effects Handles all user-interactions with this.
		 */
		public void actionPerformed(ActionEvent evt) {
			if (evt.getActionCommand().equals("Done")) {
				setCommit();
				MainFrame.ppPanel.appDiag.dispose();
			}
			
			if (evt.getActionCommand().equals("Cancel")) {
				MainFrame.ppPanel.appDiag.dispose();				
			}

		}		
		
	}	
	
	
	
		

}
