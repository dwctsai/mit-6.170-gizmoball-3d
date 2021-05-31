/* @author David Tsai
 * @created on Apr 10, 2003
 */

package gui;

import engine.Xeon;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.vecmath.Point3d;

import backend.Piece;
import backend.stream.GizmoballFileLoadG3d;
import backend.stream.GizmoballFileSaveG3d;

/**
 * CameraControlPanel is used for manipulating the view of the 3D canvas. 
 */
public class CameraControlPanel extends JPanel implements ActionListener {
	// FIELDS
	private Graphics g;
	private final Component glassPane;
	static final int WIDTH = 300;
	static final int HEIGHT = 250;
	private ImageIcon bgImage;	

	private JButton pButton;
	private JButton xyButton;
	private JButton yzButton;
	private JButton xzButton;
	private JButton playButton;
	private JButton pauseButton;
	private JButton stopButton;
	private JButton editorButton;
	private JCheckBox viewLockCheckBox;
	private JCheckBox controlCheckBox;

	private boolean lockedView = true;
	private Piece p;
	public boolean hasPaused = false;

	private GizmoballFileSaveG3d gbFileSaveG3d = null;
	private GizmoballFileLoadG3d gbFileLoadG3d = null;
	public static String savedEditorState = ""; 

	// CONSTRUCTOR
	/**
	* @effects Sets up the panel.
	*/
	public CameraControlPanel(Component glassPane) {
		// Set the size.
		this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
		this.glassPane = glassPane;

		// Set the opacity.
		this.setOpaque(true);
		
		// Set the layout.
		this.setLayout(null);
		
		// Add buttons.
		pButton = createImageButton("P", "Perspective View", 17, 24, 60, 60);

		xyButton = createImageButton("XY", "Front View", 81, 24, 60, 60);
		
		yzButton = createImageButton("YZ", "Side View", 146, 24, 60, 60);
		
		xzButton = createImageButton("XZ", "Top View", 210, 24, 60, 60);
		
		playButton = createImageButton("Play", "Play", 12, 109, 70, 70);
		
		pauseButton = createImageButton("Pause", "Pause", 67, 157, 75, 70);
		pauseButton.setEnabled(false);
		
		stopButton = createImageButton("Stop", "Stop", 147, 155, 75, 70);
		stopButton.setEnabled(false);
		
		editorButton = createImageButton("Editor", "Editor", 205, 108, 70, 70);
		editorButton.setEnabled(false);
		
		
		// Add Lock/Unlock checkbox.
		viewLockCheckBox = new JCheckBox();
		viewLockCheckBox.setToolTipText("Toggle View Lock");		
		URL imageURL = ClassLoader.getSystemResource("gui/images/camera_Unlock.png");
		viewLockCheckBox.setIcon(new ImageIcon(imageURL));
		imageURL = ClassLoader.getSystemResource("gui/images/camera_Lock.png");
		viewLockCheckBox.setSelectedIcon(new ImageIcon(imageURL));
		imageURL = ClassLoader.getSystemResource("gui/images/camera_Unlock_pressed.png");
		viewLockCheckBox.setRolloverIcon(new ImageIcon(imageURL));
		imageURL = ClassLoader.getSystemResource("gui/images/camera_Lock_pressed.png");
		viewLockCheckBox.setRolloverSelectedIcon(new ImageIcon(imageURL));
		imageURL = ClassLoader.getSystemResource("gui/images/camera_Unlock_disabled.png");
		viewLockCheckBox.setDisabledIcon(new ImageIcon(imageURL));
		imageURL = ClassLoader.getSystemResource("gui/images/camera_Lock_disabled.png");
		viewLockCheckBox.setDisabledSelectedIcon(new ImageIcon(imageURL));
		
		viewLockCheckBox.setEnabled(false);
		viewLockCheckBox.setSelected(true);
		viewLockCheckBox.setMargin(new Insets(0, 0, 0, 0));	
		viewLockCheckBox.setAlignmentX(Component.CENTER_ALIGNMENT);
		viewLockCheckBox.setBorderPainted(false);
		viewLockCheckBox.addActionListener(this);
		viewLockCheckBox.setContentAreaFilled(false);		
		
		viewLockCheckBox.setBounds(147, 87, 60, 60);
		this.add(viewLockCheckBox);
		
		
		// Add Camera Control checkbox.
		controlCheckBox = new JCheckBox();
		controlCheckBox.setToolTipText("Toggle Keyboard Camera Control");		
		imageURL = ClassLoader.getSystemResource("gui/images/camera_ControlOff.png");
		controlCheckBox.setIcon(new ImageIcon(imageURL));
		imageURL = ClassLoader.getSystemResource("gui/images/camera_ControlOn.png");
		controlCheckBox.setSelectedIcon(new ImageIcon(imageURL));
		imageURL = ClassLoader.getSystemResource("gui/images/camera_ControlOff_pressed.png");
		controlCheckBox.setRolloverIcon(new ImageIcon(imageURL));
		imageURL = ClassLoader.getSystemResource("gui/images/camera_ControlOn_pressed.png");
		controlCheckBox.setRolloverSelectedIcon(new ImageIcon(imageURL));
		imageURL = ClassLoader.getSystemResource("gui/images/camera_ControlOff_disabled.png");
		controlCheckBox.setDisabledIcon(new ImageIcon(imageURL));
		imageURL = ClassLoader.getSystemResource("gui/images/camera_ControlOn_disabled.png");
		controlCheckBox.setDisabledSelectedIcon(new ImageIcon(imageURL));
		
		controlCheckBox.setEnabled(true);
		controlCheckBox.setSelected(true);
		controlCheckBox.setMargin(new Insets(0, 0, 0, 0));	
		controlCheckBox.setAlignmentX(Component.CENTER_ALIGNMENT);
		controlCheckBox.setBorderPainted(false);
		controlCheckBox.addActionListener(this);
		controlCheckBox.setContentAreaFilled(false);		
		
		controlCheckBox.setBounds(83, 87, 60, 60);
		this.add(controlCheckBox);


		// Load the background image for paintComponent().
		URL bgImageURL = ClassLoader.getSystemResource("gui/images/bg_CameraControlPanel.png");
		bgImage = new ImageIcon(bgImageURL); 
		
		// Draw this panel.
		this.setVisible(true);

	}

	// METHODS
	/**
	 * @effects Starts gizmoball gameplay.
	 */
	private void modePlay() {
		

		// Setup everything else.
		if (hasPaused == false) {

			if (Arena3DScene.collisionCount > 0) {
				int n = JOptionPane.showConfirmDialog(
						this, 
						"" + Arena3DScene.collisionCount + " gizmo collisions were detected.\nPress Cancel to resume Editor mode and fix the issue (recommended).\nPress OK only if you are absolutely sure you want to proceed with Play.",
						"Gizmo Collisions Detected",
						JOptionPane.WARNING_MESSAGE,
						JOptionPane.YES_NO_OPTION);
				if (n == JOptionPane.YES_OPTION) {
					// DO NOTHING: CONTINUE ON
				} else if (n == JOptionPane.NO_OPTION) {
					return;
				} else {
					return;
				}						
												
			}
			
			// Save current Editor state.
			try {
				gbFileSaveG3d = new GizmoballFileSaveG3d();
				savedEditorState = gbFileSaveG3d.GizmoballFileSaveG3dToString();
			} catch (Exception e) {
				//TODO: THERE IS A GOOD CHANCE OF A PROBLEM HERE
			}

			// Reset the Engine.
			Xeon.reset();
			
			((Arena3DScene) (CanvasPanel.a3D.sceneBranch)).setGridWalls(false);
			((Arena3DScene) (CanvasPanel.a3D.sceneBranch)).hideOuterWallGizmos();
			MainFrame.drawGridWallToggleItem.setSelected(false);
			MainFrame.drawOuterWallGizmosToggleItem.setSelected(false);
			((Arena3DScene) (CanvasPanel.a3D.sceneBranch)).disablePickBranchBehaviors();
			PieceProperties.disableAllPieceBehaviors();

			// Setup the Engine.
			Set ballSet = new HashSet(Arena3DScene.allBalls);
			Set gizmoSet = new HashSet(Arena3DScene.allGizmos);

			Xeon.buildEngine(
				ballSet,
				gizmoSet,
				new Point3d(-1.0, -1.0, -1.0),
				new Point3d(21.0, 21.0, 21.0));
				
			hasPaused = true;						
			

		}

		// Enable keyboard/mouse events handling.
		PieceProperties.getEventHandler().enableEventNotifiers();	
		
		// Change the GUI accordingly.
		Arena3DScene.backGround.setColor(Arena3D.black);
		playButton.setEnabled(true);		// TODO: THIS WILL CAUSE EVENTHANDLER BUG
		pauseButton.setEnabled(true);
		stopButton.setEnabled(true);
		editorButton.setEnabled(true);
		MainFrame.leftPiecesCardLayout.show(MainFrame.leftPiecesPanels, "PieceListPlayPanel");
		MainFrame.bottomPropertiesCardLayout.show(MainFrame.bottomPropertiesPanels, "ArenaStatsPanel");
		
		// Enable the Play Behavior.
		Arena3DScene.m_PlayBehavior.setSchedulingBounds(Arena3D.bounds);
		Arena3DScene.m_PlayBehavior.setEnable(true);		
	}
	
	/**
	 * @effects Pauses gizmoball gameplay.
	 */
	private void modePause() {
		// Change the GUI accordingly.
		playButton.setEnabled(true);
		pauseButton.setEnabled(false);
		stopButton.setEnabled(true);
		editorButton.setEnabled(true);		
		
		// Do the pause.
		Arena3DScene.m_PlayBehavior.setEnable(false);
		hasPaused = true;
		PieceProperties.getEventHandler().disableEventNotifiers();
	}
	
	
	/**
	 * @effects Stops gizmoball gameplay.
	 */
	private void modeStop() {
		// Change the GUI accordingly.
		playButton.setEnabled(true);
		pauseButton.setEnabled(false);
		stopButton.setEnabled(false);
		editorButton.setEnabled(true);		
		
		// Turn off Play Behavior.
		Arena3DScene.m_PlayBehavior.setEnable(false);
		Arena3DScene.m_PlayBehavior.setSchedulingBounds(Arena3D.boundsDisable);
		
		// Clear the arena and load the cached Editor state.
		((Arena3DScene) (CanvasPanel.a3D.sceneBranch)).clearArena();
		try {
			gbFileLoadG3d = new GizmoballFileLoadG3d(savedEditorState);			
		} catch (Exception e) {
			// e.printStackTrace();
			System.out.println("CameraControlPanel: Loading of prior Editor save string failed!");
		}

		// Turn off all Piece behaviors.
		PieceProperties.disableAllPieceBehaviors();
		
		// Reset flags.
		hasPaused = false;
		
		// Disable keyboard/mouse events handling.
		PieceProperties.getEventHandler().disableEventNotifiers();
	}
	
	/**
	 * @effects Exits gizmoball gameplay mode and goes back to Editor mode.
	 */
	public void modeEditor() {
		// Change the GUI accordingly.
		playButton.setEnabled(true);
		pauseButton.setEnabled(false);
		stopButton.setEnabled(false);
		editorButton.setEnabled(false);		
		Arena3DScene.backGround.setColor(Arena3D.gray);
		MainFrame.leftPiecesCardLayout.show(MainFrame.leftPiecesPanels, "PieceListPanel");
		
		// Turn off Play Behavior.
		Arena3DScene.m_PlayBehavior.setEnable(false);
		Arena3DScene.m_PlayBehavior.setSchedulingBounds(Arena3D.boundsDisable);
		
		// Clear the arena and load the cached Editor state.
		((Arena3DScene) (CanvasPanel.a3D.sceneBranch)).clearArena();
		try {
			gbFileLoadG3d = new GizmoballFileLoadG3d(savedEditorState);			
		} catch (Exception e) {
//			e.printStackTrace();
			System.out.println("CameraControlPanel: Loading of prior Editor save string failed!");
		}

		// House keeping!
		((Arena3DScene) (CanvasPanel.a3D.sceneBranch)).setGridWalls(true);
		MainFrame.drawGridWallToggleItem.setSelected(true);
		PieceProperties.enableAllPieceBehaviors();
		((Arena3DScene) (CanvasPanel.a3D.sceneBranch)).enablePickBranchBehaviors();		
		
		// Reset flags.
		hasPaused = false;
		
		// Disable keyboard/mouse events handling.
		PieceProperties.getEventHandler().disableEventNotifiers();
	}
	
	/**
	 * This method is used when the user loads a new gizmoball game file in the middle
	 * while the current game is still playing.
	 * 
	 * @effects Brings the gizmoball game to Editor mode.
	 *
	 */
	public void modeUponNewFileLoad() {
		// Change the GUI accordingly.
		playButton.setEnabled(true);
		pauseButton.setEnabled(false);
		stopButton.setEnabled(false);
		editorButton.setEnabled(false);		
		Arena3DScene.backGround.setColor(Arena3D.gray);
		MainFrame.leftPiecesCardLayout.show(MainFrame.leftPiecesPanels, "PieceListPanel");
		
		// Turn off Play Behavior.
		Arena3DScene.m_PlayBehavior.setEnable(false);
		Arena3DScene.m_PlayBehavior.setSchedulingBounds(Arena3D.boundsDisable);
		
		// House keeping!
		((Arena3DScene) (CanvasPanel.a3D.sceneBranch)).setGridWalls(true);
		MainFrame.drawGridWallToggleItem.setSelected(true);
		PieceProperties.enableAllPieceBehaviors();
		((Arena3DScene) (CanvasPanel.a3D.sceneBranch)).enablePickBranchBehaviors();		
		
		// Reset flags.
		hasPaused = false;			
	}
	
	
	
	
	/**
	 * @effects Creates a new image button from the given parameters and
	 *          adds it to this.
	 * @return The created button.
	 */
	private JButton createImageButton(String urlFragment, String toolTipString,
								int x, int y, int width, int height) {
		JButton b = new JButton();
		urlFragment = "gui/images/camera_" + urlFragment;
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
	 * @effects Handles all user-interactions with the GUI.
	 **/
	public void actionPerformed(ActionEvent evt) {
		
		// Allow Key Presses to be Active.
		PieceProperties.getEventHandler().requestFocus(true);

		// Button Actions
		if (evt.getSource() instanceof JButton) {
			JButton buttonSource = (JButton) evt.getSource();

			if (buttonSource.getToolTipText().equals("Perspective View")) {
				viewLockCheckBox.setEnabled(false);				
				((Arena3DCamera) (CanvasPanel.a3D.cameraBranch)).cameraSetPerspectiveView();
			}
			
			if (buttonSource.getToolTipText().equals("Front View")) {
				viewLockCheckBox.setEnabled(true);					
				((Arena3DCamera) (CanvasPanel.a3D.cameraBranch)).cameraSetFrontView(lockedView);
			}	
			
			if (buttonSource.getToolTipText().equals("Side View")) {
				viewLockCheckBox.setEnabled(true);					
				((Arena3DCamera) (CanvasPanel.a3D.cameraBranch)).cameraSetSideView(lockedView);
			}	

			if (buttonSource.getToolTipText().equals("Top View")) {
				viewLockCheckBox.setEnabled(true);					
				((Arena3DCamera) (CanvasPanel.a3D.cameraBranch)).cameraSetTopView(lockedView);
			}
			
			if (buttonSource.getToolTipText().equals("Play")) {
				modePlay();
			}
			
			if (buttonSource.getToolTipText().equals("Pause")) {
				modePause();
			}
			
			if (buttonSource.getToolTipText().equals("Stop")) {
				modeStop();
			}
			
			if (buttonSource.getToolTipText().equals("Editor")) {
				modeEditor();
			}
		
		}


		// CheckBox Actions
		else if (evt.getSource() instanceof JCheckBox) {
			JCheckBox cbSource = (JCheckBox) evt.getSource();
			if (cbSource.getToolTipText().equals("Toggle View Lock")) {
				if (cbSource.isSelected()) {
					lockedView = true;
				} else {
					lockedView = false;
				}
				
			
				if (Arena3DCamera.currentView.equals(Arena3DCamera.viewFront)) {
					((Arena3DCamera) (CanvasPanel.a3D.cameraBranch)).cameraSetFrontView(lockedView);						
				}
				else if (Arena3DCamera.currentView.equals(Arena3DCamera.viewSide)) {
					viewLockCheckBox.setEnabled(true);					
					((Arena3DCamera) (CanvasPanel.a3D.cameraBranch)).cameraSetSideView(lockedView);
				}
				else if (Arena3DCamera.currentView.equals(Arena3DCamera.viewTop)) {
					viewLockCheckBox.setEnabled(true);					
					((Arena3DCamera) (CanvasPanel.a3D.cameraBranch)).cameraSetTopView(lockedView);						
				}		
				
			}
			
			if (cbSource.getToolTipText().equals("Toggle Keyboard Camera Control")) {
				if (cbSource.isSelected()) {
					Arena3DCamera.keyNavBeh.setEnable(true);
				} else {
					Arena3DCamera.keyNavBeh.setEnable(false);
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
