/* @author David Tsai
 * @created on May 10, 2003
 */
package gui;

import engine.Xeon;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * ArenaPropertiesDialog - Allows the user to change the arena board settings.
 */
public class ArenaPropertiesDialog extends JDialog {
	// FIELDS
	public final int UPPER_PANELS_WIDTH = 450;
	public static int UPPER_PANELS_HEIGHT = 200;
	private JTabbedPane tabbedPane = new JTabbedPane();
	
	private GravityPropertiesPanel gpp;
	private MuPropertiesPanel mpp;
	private FPSPropertiesPanel fpp;
	private GizmoSpawnPropertiesPanel gspp;
	
	// CONSTRUCTOR
	/**
	 * @effects Sets up the dialog.
	 */
	public ArenaPropertiesDialog(JFrame jf, String title, boolean modality) {
		super(jf, title, modality);
		
		this.getContentPane().setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
		((JPanel) this.getContentPane()).setBorder(BorderFactory.createEmptyBorder(4,4,4,4));

		gpp = new GravityPropertiesPanel();
		mpp = new MuPropertiesPanel();
		fpp = new FPSPropertiesPanel();
		gspp = new GizmoSpawnPropertiesPanel();

		tabbedPane.add("Gravity", gpp);
		tabbedPane.add("Friction", mpp);
		tabbedPane.add("FPS", fpp);
		tabbedPane.add("Gizmo Creation", gspp);		
		
		this.getContentPane().add(tabbedPane);
		this.getContentPane().add(new CommandButtonsPanel());

		
		this.setResizable(false);
		Point p = Gizmoball.theMainFrame.getLocationOnScreen();
		this.setLocation(p.x + 200, p.y + 200);   // starting location of the dialog box
		this.pack();
		this.setVisible(true);
		
		
	
	}
	
	// METHODS
	/**
	 * @effects Sets all arena properties to default values.
	 */
	public void setDefault() {
		// Value changes.
		Xeon.GRAVITY.x = 0.0;
		Xeon.GRAVITY.y = -25.0;
		Xeon.GRAVITY.z = 0.0;

		Xeon.MU = 0.025;
		Xeon.MU_2 = 0.025;
		
		Arena3DScene.TIME_PER_UPDATE = 30;
		
		PieceListPanel.pieceSpawnLocation.x = 0.0;
		PieceListPanel.pieceSpawnLocation.y = 0.0;
		PieceListPanel.pieceSpawnLocation.z = 0.0;
		
		
		// GUI changes.
		gpp.gravityXTF.setText("" + Xeon.GRAVITY.x);
		gpp.gravityYTF.setText("" + Xeon.GRAVITY.y);
		gpp.gravityZTF.setText("" + Xeon.GRAVITY.z);
		
		mpp.mu1TF.setText("" + Xeon.MU);
		mpp.mu2TF.setText("" + Xeon.MU_2);
		
		fpp.fpsLB2.setText("" + (1000 / Arena3DScene.TIME_PER_UPDATE));
		fpp.fpsSlider.setValue(1000 / Arena3DScene.TIME_PER_UPDATE);
		
		gspp.spawnXLB2.setText("" + PieceListPanel.pieceSpawnLocation.x);
		gspp.spawnYLB2.setText("" + PieceListPanel.pieceSpawnLocation.y);
		gspp.spawnZLB2.setText("" + PieceListPanel.pieceSpawnLocation.z);
		gspp.spawnXSlider.setValue((int) PieceListPanel.pieceSpawnLocation.x);
		gspp.spawnYSlider.setValue((int) PieceListPanel.pieceSpawnLocation.y);
		gspp.spawnZSlider.setValue((int) PieceListPanel.pieceSpawnLocation.z);
		
	}
	
	/**
	 * @effects Commits the new settings.
	 */
	public void setCommit() {
		try {
			Xeon.GRAVITY.x = Double.parseDouble(gpp.gravityXTF.getText());
			Xeon.GRAVITY.y = Double.parseDouble(gpp.gravityYTF.getText());
			Xeon.GRAVITY.z = Double.parseDouble(gpp.gravityZTF.getText());
			
			Xeon.MU = Double.parseDouble(mpp.mu1TF.getText());
			Xeon.MU_2 = Double.parseDouble(mpp.mu2TF.getText());			
			
			
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this,
				"Please enter a number.",
				"You didn't enter a number for one of the text fields.",
				JOptionPane.ERROR_MESSAGE);
		}
		
		
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

	public class GravityPropertiesPanel extends JPanel {
		// FIELDS
		public JTextFieldFocusable gravityXTF;
		public JTextFieldFocusable gravityYTF;
		public JTextFieldFocusable gravityZTF;
		

		/**
		 * @effects Sets up the panel.
		 */
		public GravityPropertiesPanel() {
			// Set the size.		
			this.setPreferredSize(new Dimension(UPPER_PANELS_WIDTH, UPPER_PANELS_HEIGHT));

			this.setLayout(null);
			this.setBorder(new TitledBorder("Gravity Settings"));
			
			
			JLabel gravityXLB = new JLabel("X:");
			gravityXLB.setBounds(30, 30, 100, 20);
			add(gravityXLB);
			
			JLabel gravityYLB = new JLabel("Y:");
			gravityYLB.setBounds(30, 60, 100, 20);
			add(gravityYLB);
			
			JLabel gravityZLB = new JLabel("Z:");
			gravityZLB.setBounds(30, 90, 100, 20);
			add(gravityZLB);
			
			
			gravityXTF = new JTextFieldFocusable("Gravity X", "" + Xeon.GRAVITY.x);
			gravityXTF.setBounds(55, 30, 50, 20);
			gravityXTF.setBorder(new TitledBorder(""));
			add(gravityXTF);
			
			gravityYTF = new JTextFieldFocusable("Gravity Y", "" + Xeon.GRAVITY.y);
			gravityYTF.setBounds(55, 60, 50, 20);
			gravityYTF.setBorder(new TitledBorder(""));			
			add(gravityYTF);
			
			gravityZTF = new JTextFieldFocusable("Gravity Z", "" + Xeon.GRAVITY.z);
			gravityZTF.setBounds(55, 90, 50, 20);
			gravityZTF.setBorder(new TitledBorder(""));			
			add(gravityZTF);		
			
		}
		

	}

	public class MuPropertiesPanel extends JPanel {
		// FIELDS
		public JTextFieldFocusable mu1TF;
		public JTextFieldFocusable mu2TF;
		
		/**
		 * @effects Sets up the panel.
		 */
		public MuPropertiesPanel() {
			// Set the size.
			this.setPreferredSize(new Dimension(UPPER_PANELS_WIDTH, UPPER_PANELS_HEIGHT));

			this.setLayout(null);
			this.setBorder(new TitledBorder("Friction Settings"));

			JLabel mu1LB = new JLabel("Mu 1:");
			mu1LB.setBounds(30, 30, 100, 20);
			add(mu1LB);

			JLabel mu2LB = new JLabel("Mu 2:");
			mu2LB.setBounds(30, 60, 100, 20);
			add(mu2LB);
			
			mu1TF = new JTextFieldFocusable("MU1", "" + Xeon.MU);
			mu1TF.setBounds(70, 30, 50, 20);
			mu1TF.setBorder(new TitledBorder(""));
			add(mu1TF);
			
			mu2TF = new JTextFieldFocusable("MU2", "" + Xeon.MU_2);
			mu2TF.setBounds(70, 60, 50, 20);
			mu2TF.setBorder(new TitledBorder(""));
			add(mu2TF);			
						
		}
			

	}
	
	
	public class FPSPropertiesPanel extends JPanel implements ChangeListener {
		// FIELDS
		public JSlider fpsSlider;
		public JLabel fpsLB2;
		
		private double currentFPS = 1000 / Arena3DScene.TIME_PER_UPDATE;
		
		/**
		 * @effects Sets up the panel.
		 */
		public FPSPropertiesPanel() {
			// Set the size.
			this.setPreferredSize(new Dimension(UPPER_PANELS_WIDTH, UPPER_PANELS_HEIGHT));
			
			this.setLayout(null);
			this.setBorder(new TitledBorder("Frames Per Second Settings"));			
			
			JLabel timePerUpdateLB = new JLabel("FPS:");
			timePerUpdateLB.setBounds(30, 30, 100, 20);
			add(timePerUpdateLB);
						
			fpsSlider = createSlider(1, 61, currentFPS, 10, 1, "FPS Setting");
			fpsSlider.setBounds(70, 30, 300, 50);
			fpsSlider.addChangeListener(this);
			add(fpsSlider);
			
			fpsLB2 = new JLabel("" + currentFPS);
			fpsLB2.setBounds(370, 30, 50, 20);
			fpsLB2.setBorder(new TitledBorder(""));
			add(fpsLB2);
		}
		
		
		
		/**
		 * @effects Handles all slider changes.
		 */
		public void stateChanged(ChangeEvent e) {
			JSlider source = (JSlider)e.getSource();
			
			if (source.getToolTipText().equals("FPS Setting")) {
				int fps = (int) source.getValue();
				Arena3DScene.TIME_PER_UPDATE = 1000 / fps;
				fpsLB2.setText("" + fps);
			}
			
		}		
	
	}
	
	
	
	

	public class GizmoSpawnPropertiesPanel extends JPanel implements ChangeListener {
		// FIELDS
		public JSlider spawnXSlider;
		public JSlider spawnYSlider;
		public JSlider spawnZSlider;
		
		public JLabel spawnXLB2;
		public JLabel spawnYLB2;
		public JLabel spawnZLB2;
		

		/**
		 * @effects Sets up the panel.
		 */
		public GizmoSpawnPropertiesPanel() {
			// Set the size.		
			this.setPreferredSize(new Dimension(UPPER_PANELS_WIDTH, UPPER_PANELS_HEIGHT));

			this.setLayout(null);
			this.setBorder(new TitledBorder("Set Default Gizmo Creation Location"));
			
			
			JLabel spawnXLB = new JLabel("X:");
			spawnXLB.setBounds(30, 30, 100, 20);
			add(spawnXLB);
			
			JLabel spawnYLB = new JLabel("Y:");
			spawnYLB.setBounds(30, 80, 100, 20);
			add(spawnYLB);
			
			JLabel spawnZLB = new JLabel("Z:");
			spawnZLB.setBounds(30, 130, 100, 20);
			add(spawnZLB);
			
			
			spawnXSlider = createSlider(0, 19, PieceListPanel.pieceSpawnLocation.x, 5, 1, "X Location");
			spawnXSlider.setBounds(55, 30, 300, 50);
			spawnXSlider.addChangeListener(this);
			add(spawnXSlider);
			
			spawnYSlider = createSlider(0, 19, PieceListPanel.pieceSpawnLocation.y, 5, 1, "Y Location");
			spawnYSlider.setBounds(55, 80, 300, 50);
			spawnYSlider.addChangeListener(this);			
			add(spawnYSlider);
			
			spawnZSlider = createSlider(0, 19, PieceListPanel.pieceSpawnLocation.z, 5, 1, "Z Location");
			spawnZSlider.setBounds(55, 130, 300, 50);
			spawnZSlider.addChangeListener(this);			
			add(spawnZSlider);
			
			
			spawnXLB2 = new JLabel("" + PieceListPanel.pieceSpawnLocation.x);
			spawnXLB2.setBounds(370, 30, 50, 20);
			spawnXLB2.setBorder(new TitledBorder(""));
			add(spawnXLB2);
			
			spawnYLB2 = new JLabel("" + PieceListPanel.pieceSpawnLocation.y);
			spawnYLB2.setBounds(370, 80, 50, 20);
			spawnYLB2.setBorder(new TitledBorder(""));			
			add(spawnYLB2);
			
			spawnZLB2 = new JLabel("" + PieceListPanel.pieceSpawnLocation.z);
			spawnZLB2.setBounds(370, 130, 50, 20);
			spawnZLB2.setBorder(new TitledBorder(""));			
			add(spawnZLB2);			
			
		}
		
		
		/**
		 * @effects Handles all slider changes.
		 */
		public void stateChanged(ChangeEvent e) {
			JSlider source = (JSlider)e.getSource();
			
			if (source.getToolTipText().equals("X Location")) {
				PieceListPanel.pieceSpawnLocation.x = (double) source.getValue();
				spawnXLB2.setText("" + PieceListPanel.pieceSpawnLocation.x);
			}
			
			if (source.getToolTipText().equals("Y Location")) {
				PieceListPanel.pieceSpawnLocation.y = (double) source.getValue();
				spawnYLB2.setText("" + PieceListPanel.pieceSpawnLocation.y);
			}
			
			if (source.getToolTipText().equals("Z Location")) {
				PieceListPanel.pieceSpawnLocation.z = (double) source.getValue();
				spawnZLB2.setText("" + PieceListPanel.pieceSpawnLocation.z);
			}
			
		}		

	}
	
	
	
	
	
	
	public class CommandButtonsPanel extends JPanel implements ActionListener {
		// FIELDS
		private int WIDTH = 500;
		private int HEIGHT = 50;
		

		/**
		 * @effets Sets up the panel.
		 */		
		public CommandButtonsPanel() {
			this.setPreferredSize(new Dimension(WIDTH, HEIGHT));


			this.setLayout(null);

			JButton defaultButton = new JButton("Default All");
			defaultButton.addActionListener(this);
			add(defaultButton);
			defaultButton.setBounds(50, 10, 100, 35);
			
			JButton doneButton = new JButton("Done");
			doneButton.addActionListener(this);
			add(doneButton);
			doneButton.setBounds(200, 10, 100, 35);			
			
			JButton cancelButton = new JButton("Cancel");
			cancelButton.addActionListener(this);
			add(cancelButton);
			cancelButton.setBounds(350, 10, 100, 35);			
			
		}
		
		/**
		 * @effects Handles all user-interactions with this.
		 */
		public void actionPerformed(ActionEvent evt) {
			if (evt.getActionCommand().equals("Default All")) {
				setDefault();
			}
			
			if (evt.getActionCommand().equals("Done")) {
				setCommit();
				MainFrame.arenaPropertiesDiag.dispose();
			}
			
			if (evt.getActionCommand().equals("Cancel")) {
				MainFrame.arenaPropertiesDiag.dispose();
			}

		}		
		
	}
	

}




