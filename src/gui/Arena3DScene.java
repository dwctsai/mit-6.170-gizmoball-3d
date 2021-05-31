/* @author David Tsai
 * @created on Apr 12, 2003
 */
package gui;

import gui.backend.PieceGraphics;
import gui.picking.behaviors.PickRotateBehavior;
import gui.picking.behaviors.PickTranslateBehavior;
import gui.picking.behaviors.PickZoomBehavior;

import java.util.ArrayList;
import java.util.HashMap;

import javax.media.j3d.Background;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import plugins.pieces.BallFactory;
import plugins.pieces.OuterWall3D;
import plugins.pieces.SphericalBumperFactory;
import backend.Gizmo;
import backend.Piece;
import backend.PieceFactory;

/**
 * This class handles the main scene for Arena3D.
 */
public class Arena3DScene extends BranchGroup  {
	// FIELDS
	public static Background backGround;
	public static BranchGroup piecesBranch;
	private BranchGroup pickBranch;
	private BranchGroup miscBranch;
	public static ArrayList allGizmos = new ArrayList();
	public static ArrayList allBalls = new ArrayList();
	public static ArrayList allOuterWalls = new ArrayList();
	public static HashMap TGtoPiece = new HashMap();
	public static TransformGroup currentlyPickedTG;
	public static DrawGridWalls dgw;
	private boolean gridWallsOn;
	public static final PieceProperties pieceProperties = new PieceProperties();
	
	// BEHAVIOR FIELDS
	public static PickTranslateBehavior pickTranslate = null;
	public static PickZoomBehavior pickZoom = null;
	public static PlayBehavior m_PlayBehavior = null;
	public static int TIME_PER_UPDATE = 30;
	public static int collisionCount = 0;

	public static int numCollisionsUponPick;
	public static Vector3d locationUponPick = new Vector3d();
	public static double xMouseIntegration = 0.0;
	public static double yMouseIntegration = 0.0;
	public static double zMouseIntegration = 0.0;
	

	// CONSTRUCTOR
	/**
	 * @effects Creates an Arena3DScene.
	 */
	public Arena3DScene() {
		super();

		// Give this some capabilities.
		setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
		setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
		setCapability(BranchGroup.ALLOW_LOCAL_TO_VWORLD_READ);

		// Setup the background.
		backGround = new Background(Arena3D.gray);
		backGround.setCapability(Background.ALLOW_COLOR_WRITE);
		backGround.setApplicationBounds(Arena3D.bounds);
		addChild(backGround);

		// Draw the outer grid walls.
		gridWallsOn = false;  // off at this point
		setGridWalls(true);

		// Make a BranchGroup to store all Gizmos.
		piecesBranch = new BranchGroup();
		piecesBranch.setPickable(true);
		piecesBranch.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
		piecesBranch.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
		piecesBranch.compile();
		addChild(piecesBranch);

		// Make a BranchGroup for picking abilities.
		setupPickBranch();

		// Setup the other wall gizmo bouncers.
		setupOuterWallGizmos();

		// Have Java 3D perform optimizations on this branch.
		// Note: this branch must be setPickable(true), which is the default
		compile();
	}
	
	/**
	 * TODO: TO BE REMOVED (TEST METHOD)
	 */
	public void createArena3DScene() {
		//***SOME HOT LIGHTGREEN BALL ACTION***
		PieceFactory pf;
		Piece p;
		
		pf = new SphericalBumperFactory();
/*
		for (int x = 1; x < 19; x++)
			for (int z = 1; z < 19; z++) {
				p = pf.makePiece(new Point3d(x, 1, z));
				p.addPiece();
			}
*/		

		pf = new BallFactory();
/*
		for (int x = 5; x < 15; x++)
			for (int z = 5; z < 15; z++) {
				p = pf.makePiece(new Point3d(x+.5, z+.5, 10 ));
				p.addPiece();
			}		
*/

	}


	/**
	 * @effects Creates the pickBranch and adds different picking behaviors to it, as
	 *          well as any other miscellaneous behaviors.
	 *          These behaviors are global, so all children under Arena3DScene will
	 *          have these behaviors.
	 */
	private void setupPickBranch() {
		pickBranch = new BranchGroup();

		// Provide translational picking abilities.
		pickTranslate =
			new PickTranslateBehavior(
				this,
				CanvasPanel.canvas3D,
				Arena3D.bounds);
		pickBranch.addChild(pickTranslate);

		// Provide zooming picking abilities.
		pickZoom =
			new PickZoomBehavior(this, CanvasPanel.canvas3D, Arena3D.bounds);
		pickBranch.addChild(pickZoom);

		// Provide rotational picking abilities.
		PickRotateBehavior pickRotate =
			new PickRotateBehavior(this, CanvasPanel.canvas3D, Arena3D.bounds);
//		pickBranch.addChild(pickRotate);

		// Provide "play" mode behavior.
		m_PlayBehavior = new PlayBehavior();
		m_PlayBehavior.setSchedulingBounds(Arena3D.bounds);
		m_PlayBehavior.setEnable(false);
		pickBranch.addChild(m_PlayBehavior);


		// Make optimizations on pickBranch and add it to this. 
		pickBranch.setPickable(false);
		pickBranch.compile();
		addChild(pickBranch);
	}

	/**
	 * @effects Enables mouse pick behaviors.
	 */
	public void enablePickBranchBehaviors() {
		if (pickTranslate != null && pickZoom != null) {
			pickTranslate.setEnable(true);
			pickZoom.setEnable(true);
			
			pickTranslate.setSchedulingBounds(Arena3D.bounds);
			pickZoom.setSchedulingBounds(Arena3D.bounds);
		}
	}

	/**
	 * @effects Disables mouse pick behaviors.
	 */
	public void disablePickBranchBehaviors() {
		if (pickTranslate != null && pickZoom != null) {
			pickTranslate.setEnable(false);
			pickZoom.setEnable(false);
			
			pickTranslate.setSchedulingBounds(Arena3D.boundsDisable);
			pickZoom.setSchedulingBounds(Arena3D.boundsDisable);			
		}
	}

	/**
	 * @effects Removes all previously added Pieces,
	 *          then adds back the OuterWall3D gizmos, which are
	 *          not supposed to be erased from the gizmoball game.
	 */
	public void removeAllPieces() {
		for (int i = 0; i < allBalls.size();) {
			PieceGraphics g = (PieceGraphics) allBalls.get(i);
			g.removePiece();
		}
		for (int j = 0; j < allGizmos.size();) {
			PieceGraphics g = (PieceGraphics) allGizmos.get(j);
			g.removePiece();
		}

		// Add back the outer walls.
		setupOuterWallGizmos();
		MainFrame.drawOuterWallGizmosToggleItem.setSelected(false);

		// Reset collision counter.
		collisionCount = 0;
		
		//currentlyPickedTG = null;
	}

	/**
	 * @effects Toggles the grid walls on and off.
	 */
	public void setGridWalls(boolean setOn) {
		if ((setOn == true) && (gridWallsOn == false)) {
			// Create miscellaneous branch to store grid walls.
			miscBranch = new BranchGroup();
			miscBranch.setCapability(BranchGroup.ALLOW_DETACH);
			miscBranch.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
			miscBranch.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
			miscBranch.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
			miscBranch.setPickable(false);

			// Draw the grid walls.
			dgw = new DrawGridWalls(miscBranch);
			dgw.createXYGridWall(); // "left" wall
			dgw.createYZGridWall(); // "right" wall
			dgw.createXZGridWall(); // "bottom" wall

			miscBranch.compile();
			addChild(miscBranch);
			gridWallsOn = true;
		} else if (setOn == false) {
			removeChild(miscBranch);
			gridWallsOn = false;
		}
	}

	/**
	 * @effects Toggles wireframe drawing mode on and off.
	 */
	public void setWireFrame(boolean setOn) {
		if (setOn == true) {
			for (int i = 0; i < allGizmos.size(); i++) {
				PieceGraphics g = (PieceGraphics) allGizmos.get(i);
				g.drawWireFrameMode();
			}
			for (int j = 0; j < allBalls.size(); j++) {
				PieceGraphics g = (PieceGraphics) allBalls.get(j);
				g.drawWireFrameMode();
			}
		} else {
			for (int i = 0; i < allGizmos.size(); i++) {
				PieceGraphics g = (PieceGraphics) allGizmos.get(i);
				g.drawFillMode();
			}
			for (int j = 0; j < allBalls.size(); j++) {
				PieceGraphics g = (PieceGraphics) allBalls.get(j);
				g.drawFillMode();
			}

		}
	}

	/**
	 * @effects Sets up the outer wall bouncer gizmos.
	 */
	public void setupOuterWallGizmos() {
		boolean drawWall = false;

		// BACK
		Point3d location = new Point3d(0, 0, -1);
		OuterWall3D ow =
			new OuterWall3D(
				"W1",
				location,
				20.0,
				20.0,
				1.0,
				1.0,
				PieceProperties.appCubicalBumper3DOuterWall,
				drawWall);
		ow.addPiece();

		// BOTTOM
		location = new Point3d(0, -1, 0);
		ow =
			new OuterWall3D(
				"W2",
				location,
				20.0,
				1.0,
				20.0,
				1.0,
				PieceProperties.appCubicalBumper3DOuterWall,
				drawWall);
		ow.addPiece();

		// FRONT
		location = new Point3d(0, 0, 20);
		ow =
			new OuterWall3D(
				"W3",
				location,
				20.0,
				20.0,
				1.0,
				1.0,
				PieceProperties.appCubicalBumper3DOuterWall,
				drawWall);
		ow.addPiece();

		// LEFT
		location = new Point3d(-1, 0, 0);
		ow =
			new OuterWall3D(
				"W4",
				location,
				1.0,
				20.0,
				20.0,
				1.0,
				PieceProperties.appCubicalBumper3DOuterWall,
				drawWall);
		ow.addPiece();

		// RIGHT
		location = new Point3d(20, 0, 0);
		ow =
			new OuterWall3D(
				"W5",
				location,
				1.0,
				20.0,
				20.0,
				1.0,
				PieceProperties.appCubicalBumper3DOuterWall,
				drawWall);
		ow.addPiece();

		// TOP
		location = new Point3d(0, 20, 0);
		ow =
			new OuterWall3D(
				"W6",
				location,
				20.0,
				1.0,
				20.0,
				1.0,
				PieceProperties.appCubicalBumper3DOuterWall,
				drawWall);
		ow.addPiece();
	}

	/**
	 * @effects Draws the outlines of the OuterWall3D gizmos.
	 */
	public void drawOuterWallGizmos() {
		for (int i = 0; i < allOuterWalls.size(); i++) {
			PieceGraphics g = (PieceGraphics) allOuterWalls.get(i);
			g.enablePieceBehaviors();
		}
	}

	/**
	 * @effects Hides the outlines of the OuterWall3D gizmos.
	 */
	public void hideOuterWallGizmos() {
		for (int i = 0; i < allOuterWalls.size(); i++) {
			PieceGraphics g = (PieceGraphics) allOuterWalls.get(i);
			g.disablePieceBehaviors();
		}
	}

	/**
	 * @effects Clears the current gizmoball arena.
	 *          Note that removeAllPieces() will add back the OuterWall3D gizmos.
	 */
	public void clearArena() {
		removeAllPieces();
		for (int i = 0; i < allGizmos.size(); i++) {
			Gizmo g = (Gizmo) allGizmos.get(i);
			g.clearConnections();
		}
		
		// Update the arena's stats panel.
		MainFrame.bottomPropertiesCardLayout.show(MainFrame.bottomPropertiesPanels, "ArenaStatsPanel");
		
		// More housekeeping.
		PieceProperties.getEventHandler().clearBindings();
	}
	
}
