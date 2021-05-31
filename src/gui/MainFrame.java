/* @author David Tsai
 * @created on Apr 10, 2003
 */
package gui;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;

import javax.media.j3d.BoundingBox;
import javax.media.j3d.Bounds;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.ToolTipManager;
import javax.swing.event.MouseInputAdapter;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import engine.Xeon;

import plugins.pieces.OuterWall3D;
import backend.Piece;
import backend.stream.GizmoballFileFilter;
import backend.stream.GizmoballFileLoad;
import backend.stream.GizmoballFileLoadG3d;
import backend.stream.GizmoballFileSaveG3d;

/**
 * MainFrame is the main outer frame of the Gizmoball GUI.
 */
public class MainFrame extends JFrame implements ActionListener {
	// FIELDS

	private Container contentPane = this.getContentPane();
	private JMenuBar menuBar;
	public static JDialog arenaPropertiesDiag;
	public static JDialog aboutDiag;

	public static CanvasPanel cPanel;
	public static CameraControlPanel ccPanel;

	public static JPanel bottomPropertiesPanels;
	public static PiecePropertiesPanel ppPanel;
	public static CardLayout bottomPropertiesCardLayout = new CardLayout();

	public static JPanel leftPiecesPanels;
	public static PieceListPanel plPanel;
	public static CardLayout leftPiecesCardLayout = new CardLayout();

	public static JMenuItem drawGridWallToggleItem;
	public static JMenuItem drawWireframeToggleItem;
	public static JMenuItem drawOuterWallGizmosToggleItem;
	public static boolean snapToGridToggleOn = true;

	private JFileChooser fc;
	private GizmoballFileLoad gbFileLoad;
	private GizmoballFileLoadG3d gbFileLoadG3d;
	private GizmoballFileSaveG3d gbFileSaveG3d;

	public static final Color shadow_blue = new Color(130, 160, 215);
	public static final Color shadow_darkblue = new Color(121, 143, 210);
	public static final Color shadow_verydarkblue = new Color(80, 96, 128);
	public static final Color shadow_lightpurple = new Color(160, 176, 225);
	public static final Color shadow_lightblue = new Color(192, 225, 255);

	/**
	* @effects Sets up the window.
	*/
	public MainFrame() {
		super("Gizmoball3D");

		// Add a window listener so that if the x in the corner is clicked	
		// the program closes.
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		}); // addWindowListener

		// Set the size of the window at (0,0) and set the dimensions
		this.setBounds(0, 0, 1005, 805);

		// Make this window un-resizable.
		this.setResizable(false);

		// Make the menu bar heavy-weight to avoid conflict with 3d canvas.
		JPopupMenu.setDefaultLightWeightPopupEnabled(false);

		// Create the menu bar.
		this.setJMenuBar(createMenuBar());

		// Make Mouse Tips heavy-weight.
		ToolTipManager.sharedInstance().setLightWeightPopupEnabled(false);

		// Setup the load/save file chooser and make it such that
		// the default directory when you open the save/load dialog is "Gizmoball/saved".
		fc = new JFileChooser(new File("saved"));

		// add the custom file filter and disable the default "Accept All" file filter.
		fc.addChoosableFileFilter(new GizmoballFileFilter());
		//		fc.setAcceptAllFileFilterUsed(false);	

		// Create the various panels.
		plPanel = new PieceListPanel();
		cPanel = new CanvasPanel(this.getGlassPane());
		ppPanel = new PiecePropertiesPanel();
		ccPanel = new CameraControlPanel(this.getGlassPane());

		ArenaStatsPanel asPanel = new ArenaStatsPanel();

		bottomPropertiesPanels = new JPanel();
		bottomPropertiesPanels.setLayout(bottomPropertiesCardLayout);
		bottomPropertiesPanels.add(asPanel, "ArenaStatsPanel");
		bottomPropertiesPanels.add(ppPanel, "PiecePropertiesPanel");

		PieceListPlayPanel plpp = new PieceListPlayPanel();

		leftPiecesPanels = new JPanel();
		leftPiecesPanels.setLayout(leftPiecesCardLayout);
		leftPiecesPanels.add(plPanel, "PieceListPanel");
		leftPiecesPanels.add(plpp, "PieceListPlayPanel");

		// Add all the panels into the window.
		contentPane.setLayout(null);
		leftPiecesPanels.setBounds(
			0,
			0,
			PieceListPanel.WIDTH,
			PieceListPanel.HEIGHT);
		cPanel.setBounds(200, 0, CanvasPanel.WIDTH, CanvasPanel.HEIGHT);
		bottomPropertiesPanels.setBounds(
			0,
			500,
			PiecePropertiesPanel.WIDTH,
			PiecePropertiesPanel.HEIGHT);

		ccPanel.setBounds(
			700,
			500,
			CameraControlPanel.WIDTH,
			CameraControlPanel.HEIGHT);

		contentPane.add(leftPiecesPanels);
		contentPane.add(cPanel);
		contentPane.add(bottomPropertiesPanels);
		contentPane.add(ccPanel);

		// Set up to listen for keys
		// this.addFocusListener(this);
		// contentPane.addFocusListener(this);
		//		GlassPane gp = new GlassPane(this.getJMenuBar(), this.getContentPane());
		//		gp.setVisible(true);
		//		System.out.println(gp.getSize());
		//		this.setGlassPane(gp);

		// Allow components of this frame to listen for key events
		// PieceProperties.getEventHandler().addEventNotifier(this.getGlassPane());

		// Draw this frame.
		this.setVisible(true);

	} //  FrameTest

	/**
	 * @effects Creates the GUI's menu bar.
	 **/
	public JMenuBar createMenuBar() {
		menuBar = new JMenuBar();
		JMenu fileMenu;
		JMenu arenaMenu;
		JMenu helpMenu;

		JMenuItem newItem;
		JMenuItem loadItem;
		JMenuItem loadG3DItem;
		JMenuItem saveAsG3DItem;
		JMenuItem exitItem;

		JMenuItem arenaPropertiesDiagItem;
		JMenuItem snapToGridToggleItem;

		JMenuItem mouseTipToggleItem;
		JMenuItem aboutItem;

		// Add fileMenu to the menu bar.
		fileMenu = new JMenu("File");
		fileMenu.setMnemonic(KeyEvent.VK_F);
		menuBar.add(fileMenu);

		// Populate fileMenu:
		// "New Arena"
		newItem = new JMenuItem("New Arena");
		newItem.setMnemonic(KeyEvent.VK_N);
		newItem.addActionListener(this);
		fileMenu.add(newItem);

		fileMenu.addSeparator();

		// "Load ..."
		loadItem = new JMenuItem("Load...");
		loadItem.setMnemonic(KeyEvent.VK_L);
		loadItem.addActionListener(this);
		fileMenu.add(loadItem);

		// "Save As .g3d ..."
		saveAsG3DItem = new JMenuItem("Save As .g3d...");
		saveAsG3DItem.setMnemonic(KeyEvent.VK_D);
		saveAsG3DItem.addActionListener(this);
		fileMenu.add(saveAsG3DItem);

		fileMenu.addSeparator();

		// "Exit"
		exitItem = new JMenuItem("Exit");
		exitItem.setMnemonic(KeyEvent.VK_X);
		exitItem.addActionListener(this);
		fileMenu.add(exitItem);

		// Add arenaMenu to the menu bar.
		arenaMenu = new JMenu("Arena");
		arenaMenu.setMnemonic(KeyEvent.VK_A);
		menuBar.add(arenaMenu);

		// Populate arenaMenu:
		// "Arena Properties..."
		arenaPropertiesDiagItem = new JMenuItem("Arena Properties...");
		arenaPropertiesDiagItem.setMnemonic(KeyEvent.VK_P);
		arenaPropertiesDiagItem.addActionListener(this);
		arenaMenu.add(arenaPropertiesDiagItem);

		arenaMenu.addSeparator();

		// "Grid Walls" checkbox
		drawGridWallToggleItem = new JCheckBoxMenuItem("Draw Grid Walls");
		drawGridWallToggleItem.setMnemonic(KeyEvent.VK_G);
		drawGridWallToggleItem.setSelected(true);
		drawGridWallToggleItem.addActionListener(this);
		arenaMenu.add(drawGridWallToggleItem);

		// "Draw Outer Wall Gizmos" checkbox
		drawOuterWallGizmosToggleItem =
			new JCheckBoxMenuItem("Draw Outer Wall Gizmos");
		drawOuterWallGizmosToggleItem.setMnemonic(KeyEvent.VK_O);
		drawOuterWallGizmosToggleItem.setSelected(true);
		drawOuterWallGizmosToggleItem.addActionListener(this);
		drawOuterWallGizmosToggleItem.setSelected(false);
		arenaMenu.add(drawOuterWallGizmosToggleItem);

		// "Wireframe Drawing" checkbox
		drawWireframeToggleItem = new JCheckBoxMenuItem("Draw as Wireframes");
		drawWireframeToggleItem.setMnemonic(KeyEvent.VK_W);
		drawWireframeToggleItem.setSelected(false);
		drawWireframeToggleItem.addActionListener(this);
		arenaMenu.add(drawWireframeToggleItem);

		// "Snap to Grid" checkbox
		snapToGridToggleItem = new JCheckBoxMenuItem("Snap to Grid");
		snapToGridToggleItem.setMnemonic(KeyEvent.VK_S);
		snapToGridToggleItem.setSelected(true);
		snapToGridToggleItem.addActionListener(this);
		arenaMenu.add(snapToGridToggleItem);

		// Add helpMenu to the menu bar.
		helpMenu = new JMenu("Help");
		helpMenu.setMnemonic(KeyEvent.VK_H);
		menuBar.add(helpMenu);

		// Populate helpMenu:
		// "Mouse Tips" checkbox
		mouseTipToggleItem = new JCheckBoxMenuItem("Mouse Tips");
		mouseTipToggleItem.setMnemonic(KeyEvent.VK_T);
		mouseTipToggleItem.setSelected(true);
		mouseTipToggleItem.addActionListener(this);
		helpMenu.add(mouseTipToggleItem);

		helpMenu.addSeparator();

		// about item
		ImageIcon aboutIcon = new ImageIcon("gui/images/64X.gif");
		aboutItem = new JMenuItem("About", aboutIcon);
		aboutItem.setMnemonic(KeyEvent.VK_A);
		aboutItem.addActionListener(this);
		helpMenu.add(aboutItem);

		// return the menuBar
		return menuBar;
	}

	/**
	 * @effects Pops up a dialog asking if the user is sure he/she wants to exit Gizmoball.
	 */
	public void askExit() {
		int n =
			JOptionPane.showConfirmDialog(
				this,
				"Are you sure you want to exit Gizmoball3D?",
				"Exit Gizmoball3D?",
				JOptionPane.YES_NO_OPTION);
		if (n == JOptionPane.YES_OPTION) {
			System.exit(0);
		} else if (n == JOptionPane.NO_OPTION) {
			// DO NOTHING
		} else {
			// DO NOTHING
		}
	}

	/**
	 * @effects Handles all user-interactions with this.
	 */
	public void actionPerformed(ActionEvent evt) {
		// Menu Actions
		if (evt.getSource() instanceof JMenuItem) {
			JMenuItem menuSource = (JMenuItem) evt.getSource();
			// "New Arena"
			if (menuSource.getText().equals("New Arena")) {
				// Reset gravity/coefficent settings.
				Xeon.GRAVITY = new Vector3d(0, -25, 0);
				Xeon.MU = 0.025;
				Xeon.MU_2 = 0.025;					
				
				((Arena3DScene) (CanvasPanel.a3D.sceneBranch)).clearArena();
				CameraControlPanel.savedEditorState = "";
				MainFrame.ccPanel.modeEditor();

			}

			// "Load..."
			if (menuSource.getText().equals("Load...")) {
				// Back up gravity/coefficient settings in case user hits "cancel".
				Vector3d backupGravity = new Vector3d(Xeon.GRAVITY);
				double backupMU = Xeon.MU;
				double backupMU_2 = Xeon.MU_2;				
				
				// Reset gravity/coefficent settings.
				Xeon.GRAVITY = new Vector3d(0, -25, 0);
				Xeon.MU = 0.025;
				Xeon.MU_2 = 0.025;				
				
				int returnVal = fc.showDialog(this, "Load Gizmoball File");
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					// Clear the arena.
					((Arena3DScene) (CanvasPanel.a3D.sceneBranch)).clearArena();

					// Try to load the file.
					File file = fc.getSelectedFile();
					boolean successTrue = false;
					try {
						gbFileLoad = new GizmoballFileLoad(file);
						successTrue = true;
					} catch (Exception e) {
//						e.printStackTrace();
						successTrue = false;
					}
					if (successTrue == false) {
						try {
							gbFileLoadG3d = new GizmoballFileLoadG3d(file);
							successTrue = true;
						} catch (Exception e) {
//							e.printStackTrace();
							successTrue = false;
						}
					}
					if (successTrue == true) {
						CameraControlPanel.savedEditorState = "";
						ccPanel.modeUponNewFileLoad();

						// Check collisions via bounding box intersections and
						// out-of-arena placement locations.
						boolean doesIntersect = false;
						boolean isOutofBounds = false;
						
						LinkedList boundingBoxes = new LinkedList();
						for (int i = 0;
							i < Arena3DScene.allBalls.size();
							i++) {
							Piece g = (Piece) Arena3DScene.allBalls.get(i);
							
							boundingBoxes.add(g.getContainerAbsoluteCoord());
							
							Point3d lower = g.getContainerLowerLocation();
							Point3d upper = g.getContainerUpperLocation();
							
							if (lower.x < 0.0 || lower.y < 0.0 || lower.z < 0.0 ||
							    lower.x > 20.0 || lower.y > 20.0 || lower.z > 20.0 ||
							    upper.x < 0.0 || upper.y < 0.0 || upper.z < 0.0 ||
							    upper.x > 20.0 || upper.y > 20.0 || upper.z > 20.0) {
							    	isOutofBounds = true;
							    }
							
						}
						for (int j = 0;
							j < Arena3DScene.allGizmos.size();
							j++) {
							Piece g = (Piece) Arena3DScene.allGizmos.get(j);
							
							if (g instanceof OuterWall3D) {
								continue;
							}							
							
							boundingBoxes.add(g.getContainerAbsoluteCoord());
							
							Point3d lower = g.getContainerLowerLocation();
							Point3d upper = g.getContainerUpperLocation();
							
							// TODO: NOTE: DOUBLE ERRORS IN ROTATIONS ACCOUNT FOR +/-0.01 CHECK
							//       INSTEAD OF 0.0
							if (lower.x < -0.01 || lower.y < -0.01 || lower.z < -0.01 ||
								lower.x > 20.0 || lower.y > 20.0 || lower.z > 20.0 ||
								upper.x < 0.0 || upper.y < 0.0 || upper.z < 0.0 ||
								upper.x > 20.01 || upper.y > 20.01 || upper.z > 20.01) {
									isOutofBounds = true;
								}						
						}

						int identical = 0;
						LinkedList boundingBoxesCopy =
							new LinkedList(boundingBoxes);
						Iterator boundingBoxesIter = boundingBoxes.iterator();

						while (boundingBoxesIter.hasNext()
							&& !(doesIntersect)) {
							BoundingBox tempBB = (BoundingBox) boundingBoxesIter.next();
							Iterator boundingBoxesCopyIter =
								boundingBoxesCopy.iterator();

							while (boundingBoxesCopyIter.hasNext()
								&& !(doesIntersect)) {
									
								doesIntersect =
									tempBB.intersect((Bounds) boundingBoxesCopyIter.next());
								if(doesIntersect)
								identical++;
								
								if(identical > 1) {
									// Too many identical bounding boxes
									doesIntersect = true;
								} else {
									doesIntersect = false;
								}

							}
							
							identical = 0;
						}
						
						if (doesIntersect || isOutofBounds) {
							// Load file is bad
							((Arena3DScene) (CanvasPanel.a3D.sceneBranch)).clearArena();							
							CameraControlPanel.savedEditorState = "";
							ccPanel.modeUponNewFileLoad();
							
							JOptionPane.showMessageDialog(
								this,
							"The file \""
								+ file.getName()
								+ "\" places gizmos in invalid locations.\nPlease correct the errors in the file.",
								"Gizmoball File Loading Error",
								JOptionPane.ERROR_MESSAGE);

						}

					}
					if (successTrue == false) {
						// Load file is bad
						((Arena3DScene) (CanvasPanel.a3D.sceneBranch)).clearArena();							
						CameraControlPanel.savedEditorState = "";
						ccPanel.modeUponNewFileLoad();				
						
						JOptionPane.showMessageDialog(
							this,
							"The file \""
								+ file.getName()
								+ "\" couldn't load.\nPlease check to see that it is a proper Gizmoball file.",
							"Gizmoball File Loading Error",
							JOptionPane.ERROR_MESSAGE);
					}

				}
				else if (returnVal == JFileChooser.CANCEL_OPTION) {
					// Restore backed up settings.
					Xeon.GRAVITY = backupGravity;
					Xeon.MU = backupMU;
					Xeon.MU_2 = backupMU_2;
				}
				

				//Reset the file chooser for the next time it's shown.
				fc.setSelectedFile(null);
			}
			// "Save As .g3d..."
			if (menuSource.getText().equals("Save As .g3d...")) {
				int returnVal = fc.showDialog(this, "Save Gizmoball Game");
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fc.getSelectedFile();
					System.out.println("Saving: " + file.getName());
					try {
						gbFileSaveG3d = new GizmoballFileSaveG3d(file);
					} catch (IOException e) {
						System.out.println("Unable to Save File");
					}

				}
				//Reset the file chooser for the next time it's shown.
				fc.setSelectedFile(null);
			}
			// "Exit"
			if (menuSource.getText().equals("Exit")) {
				askExit();
			}

			// "Arena Properties..."
			if (menuSource.getText().equals("Arena Properties...")) {
				arenaPropertiesDiag =
					new ArenaPropertiesDialog(
						new JFrame(),
						"Arena Properties",
						false);
			}

			// "Draw Grid Walls"
			if (menuSource.getText().equals("Draw Grid Walls")) {
				if (menuSource.isSelected()) {
					(
						(Arena3DScene)
							(CanvasPanel.a3D.sceneBranch)).setGridWalls(
						true);
				} else {
					(
						(Arena3DScene)
							(CanvasPanel.a3D.sceneBranch)).setGridWalls(
						false);
				}
			}
			// "Draw Outer Wall Gizmos"	
			if (menuSource.getText().equals("Draw Outer Wall Gizmos")) {
				if (menuSource.isSelected()) {
					((Arena3DScene) (CanvasPanel.a3D.sceneBranch))
						.drawOuterWallGizmos();
				} else {
					((Arena3DScene) (CanvasPanel.a3D.sceneBranch))
						.hideOuterWallGizmos();
				}
			}
			// "Draw as Wireframes"
			if (menuSource.getText().equals("Draw as Wireframes")) {
				if (menuSource.isSelected()) {
					(
						(Arena3DScene)
							(CanvasPanel.a3D.sceneBranch)).setWireFrame(
						true);
				} else {
					(
						(Arena3DScene)
							(CanvasPanel.a3D.sceneBranch)).setWireFrame(
						false);
				}
			}
			// "Snap to Grid"
			if (menuSource.getText().equals("Snap to Grid")) {
				if (menuSource.isSelected()) {
					snapToGridToggleOn = true;
				} else {
					snapToGridToggleOn = false;
				}
			}

			// "Mouse Tips"
			if (menuSource.getText().equals("Mouse Tips")) {
				if (menuSource.isSelected()) {
					ToolTipManager.sharedInstance().setEnabled(true);
				} else {
					ToolTipManager.sharedInstance().setEnabled(false);
				}
			}
			// "About"
			if (menuSource.getText().equals("About")) {
				aboutDiag =
					new AboutDialog(new JFrame(), "About Gizmoball3D", false);
			}

		} // menu actions

	}

} // MainFrame

class GlassPane extends JComponent {

	public GlassPane(JMenuBar menuBar, Container contentPane) {

		// this.addKeyListener(new MagicKeyListener(PieceProperties.getKeyHandler()));

		FrameListener listener = new FrameListener(menuBar, this, contentPane);
		this.addMouseMotionListener(listener);
		this.addMouseListener(listener);
	}

}

class FrameListener extends MouseInputAdapter {

	public FrameListener(
		JMenuBar menuBar,
		GlassPane glassPane,
		Container contentPane) {

	}

}
