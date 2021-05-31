/* @author Eric Tung
 * @created on May 11, 2003
 */
package plugins.pieces;

import engine.backend.BallPhysics;
import gui.Arena3D;
import gui.Arena3DScene;
import gui.CanvasPanel;
import gui.geometries.TriangularPrismC;
import gui.picking.behaviors.BoundsBehavior;
import gui.picking.behaviors.PickHighlightBehavior;
import gui.picking.behaviors.PickHighlightBehavior2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import javax.media.j3d.Appearance;
import javax.media.j3d.BoundingBox;
import javax.media.j3d.BoundingLeaf;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Node;
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.RenderingAttributes;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import backend.StaticGizmo;
import backend.event.GizmoEvent;
import backend.events.exceptions.EventException;

import com.sun.j3d.utils.geometry.Primitive;
//import com.sun.rsasign.u;

/**
 * TriangularBumperC3D - a gizmo that represents a "Type C" triangular bumper.
 */
// AF:
//   An ADT representing a triangular bumper in Cartesian space.
//   TriangularBumperC3D has a location, dimensions, coefficient of reflection, and appearance.
// RI:
//   lowerCornerLocation != null &&
//   xLength >= 0 &&
//   yLength >= 0 &&
//   && COR >= 0 &&
//   app != null

public class TriangularBumperC3D implements StaticGizmo {

	private static final String[] COMMANDS = {
	};

	// GENERAL FIELDS
	private static int ID = 0;
	private final int myID;
	private int cachedHashCode = 0;
	private String name;	

	// GRAPHICS FIELDS
	private BranchGroup pieceBranch;
	private TransformGroup pieceTG;
	private TransformGroup pieceTGR;
	private Transform3D pieceT3D;
	private BranchGroup graphicsSubBranch;
	private BranchGroup containerSubBranch;
	private BoundingBox container;
	private BoundingLeaf containerLeaf;
	private BoundsBehavior m_BoundsBehavior = null;
	private PickHighlightBehavior pickHighlight = null;
	private PickHighlightBehavior2 pickHighlight2 = null;
	private Appearance app;
	private boolean isVisible;
	private ArrayList allGeometries = new ArrayList();
	private LinkedList rotationQueue = new LinkedList();

	// PHYSICS FIELDS
	private Point3d location;
	private double xLength;
	private double yLength;
	private double zLength;
	private double COR;
	private Set connections = new HashSet();
	private Set physics = new HashSet();
	private boolean isPhysical;

	// CONSTRUCTOR 
	/**
	 * @effects Creates a TriangularBumperC3D from the given parameters.
	 *          The length of the legs (not counting the hypotenuse) of the bumper
	 *          is the length of the parameter xLength passed into the constructor.
	 */
	public TriangularBumperC3D(
	    String name,	
		Point3d lowerCornerLocation,
		double xLength,
		double yLength,
		double zLength,
		double COR,
		Appearance app,
		boolean isVisible) {

		// Check RI.
		if (lowerCornerLocation == null
			|| xLength < 0
			|| yLength < 0
			|| zLength < 0
			|| COR < 0
			|| app == null) {
			throw new RuntimeException("Invalid attributes passed in!");
		}

		// Initialize fields.
		this.xLength = xLength;
		this.yLength = yLength;
		this.zLength = zLength;
		this.COR = COR;
		this.app = app;
		this.isVisible = isVisible;
		this.isPhysical = true;
		this.myID = TriangularBumperC3D.ID++;
		
		if (name == null) {
			this.name = "TC-" + this.myID;
		}
		else {
			this.name = name;
		}		

		// Setup the bounding box.
		container = new BoundingBox();
		container.setLower(-xLength / 2.0, -yLength / 2.0, -zLength / 2.0);
		container.setUpper(xLength / 2.0, yLength / 2.0, zLength / 2.0);
		containerLeaf = new BoundingLeaf(container);
		containerLeaf.setCapability(Node.ALLOW_LOCAL_TO_VWORLD_READ);

		// Setup graphics.
		setupGraphics(lowerCornerLocation);

	}

	// GRAPHICAL METHODS

	/*
	 * @see gui.backend.PieceGraphics
	 */
	public void setupGraphics(Point3d lowerCornerLocation) {
		// Initialize the transforms for the graphic.
		pieceTG = new TransformGroup();
		pieceTGR = new TransformGroup();
		pieceTG.addChild(pieceTGR);
		if (isVisible) {
			pieceTG.setCapability(TransformGroup.ENABLE_PICK_REPORTING);
			pieceTG.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
			pieceTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
			pieceTG.setPickable(true);
			pieceTG.setCollidable(true);

			pieceTGR.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
			pieceTGR.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

		} else {
			pieceTG.setPickable(false);
		}

		pieceT3D = new Transform3D();

		// Setup the appearance of this piece.
		if (!isVisible) {
			RenderingAttributes ra = new RenderingAttributes();
			ra.setVisible(false);
			app.setRenderingAttributes(ra);
		}

		// Create the shape that will make up this piece.
		TriangularPrismC gTriangle = new TriangularPrismC(xLength);
		//		gTriangle.setBoundsAutoCompute(false);
		gTriangle.setPickable(true);
		gTriangle.setCollidable(false);
		gTriangle.setCapability(Shape3D.ALLOW_APPEARANCE_READ);
		gTriangle.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);

		allGeometries.add(gTriangle);
		gTriangle.setAppearance(app);

		// Arrange the primitive shapes to create the geometry of the piece.
		graphicsSubBranch = new BranchGroup();

		Transform3D translate = new Transform3D();

		// --add the triangle shape
		Vector3d transVec = new Vector3d(-0.5, -0.5, -0.5);
		translate.set(transVec);
		TransformGroup tempTG1 = new TransformGroup(translate);
		tempTG1.addChild(gTriangle);
		graphicsSubBranch.addChild(tempTG1);

		// Move this piece to its starting location.
		translate = new Transform3D();
		transVec =
			new Vector3d(
				lowerCornerLocation.x + xLength / 2.0,
				lowerCornerLocation.y + yLength / 2.0,
				lowerCornerLocation.z + zLength / 2.0);
		translate.set(transVec);
		pieceT3D.set(transVec);
		pieceTG.setTransform(pieceT3D);
	}

	/*
	 * @see gui.backend.PieceGraphics
	 */
	public void updateGraphics() {
		//DO NOTHING
	}

	/*
	 * @see gui.backend.PieceGraphics
	 */
	public void moveGraphics(Point3d p) {
		Point3d lower = getContainerLowerLocation();
		Point3d upper = getContainerUpperLocation();
		double xLength = upper.x - lower.x;
		double yLength = upper.y - lower.y;
		double zLength = upper.z - lower.z;

		p.x += xLength / 2.0;
		p.y += yLength / 2.0;
		p.z += zLength / 2.0;

		Transform3D translate = new Transform3D();
		pieceTG.getTransform(translate);
		Vector3d transVec = new Vector3d(p);
		translate.setTranslation(transVec);
		pieceTG.setTransform(translate);
	}

	/*
	 * @see gui.backend.PieceGraphics
	 */
	public void addPiece() {
		// Add this to the list of gizmos.
		Arena3DScene.allGizmos.add(this);
		Arena3DScene.TGtoPiece.put(pieceTG, this);

		// Create the main pieceBranch.
		pieceBranch = new BranchGroup();
		pieceBranch.setCapability(BranchGroup.ALLOW_DETACH);

		// Transform pieceTG to the starting position.
		pieceTG.setTransform(pieceT3D);
		pieceBranch.addChild(pieceTG);

		// Complete the graphics sub-branch.
		if (isVisible) {
			pickHighlight =
				new PickHighlightBehavior(
					graphicsSubBranch,
					CanvasPanel.canvas3D,
					Arena3D.boundsDisable);
			pickHighlight.setEnable(false);
			//			graphicsSubBranch.addChild(pickHighlight);
		}
		graphicsSubBranch.compile();
		pieceTGR.addChild(graphicsSubBranch);

		// Create the container sub-branch. 
		containerSubBranch = new BranchGroup();
		if (isVisible) {
			m_BoundsBehavior = new BoundsBehavior(container);
			m_BoundsBehavior.setSchedulingBounds(Arena3D.boundsDisable);
			m_BoundsBehavior.addBehaviorToParentGroup(containerSubBranch);
			m_BoundsBehavior.setEnable(false);
			pickHighlight2 =
				new PickHighlightBehavior2(
					containerSubBranch,
					CanvasPanel.canvas3D,
					Arena3D.boundsDisable,
					false);
			pickHighlight2.setEnable(false);
			containerSubBranch.addChild(pickHighlight2);
		}
		containerSubBranch.addChild(containerLeaf);
		containerSubBranch.compile();
		pieceTG.addChild(containerSubBranch);

		// Add pieceBranch branch to Arena3DScene's piecesBranch.	
		pieceBranch.compile();
		Arena3DScene.piecesBranch.addChild(pieceBranch);
	}

	/*
	 * @see gui.backend.PieceGraphics
	 */
	public void removePiece() {
		// Remove this from the list of gizmos.
		Arena3DScene.allGizmos.remove(this);
		Arena3DScene.TGtoPiece.remove(pieceTG);

		// Remove this piece's parent BranchGroup from the scene.
		pieceBranch.detach();
		Arena3DScene.piecesBranch.removeChild(pieceBranch);
	}

	/*
	 * @see gui.backend.PieceGraphics
	 */
	public Point3d getContainerLowerLocation() {
		Point3d lp = new Point3d();
		container.getLower(lp);
		containerLeaf.getLocalToVworld(pieceT3D);
		pieceT3D.transform(lp);

		Point3d up = new Point3d();
		container.getUpper(up);
		containerLeaf.getLocalToVworld(pieceT3D);
		pieceT3D.transform(up);

		lp.x = (lp.x < up.x) ? lp.x : up.x;
		lp.y = (lp.y < up.y) ? lp.y : up.y;
		lp.z = (lp.z < up.z) ? lp.z : up.z;

		return lp;
	}

	/*
	 * @see gui.backend.PieceGraphics
	 */
	public Point3d getContainerUpperLocation() {
		Point3d lp = new Point3d();
		container.getLower(lp);
		containerLeaf.getLocalToVworld(pieceT3D);
		pieceT3D.transform(lp);

		Point3d up = new Point3d();
		container.getUpper(up);
		containerLeaf.getLocalToVworld(pieceT3D);
		pieceT3D.transform(up);

		up.x = (lp.x > up.x) ? lp.x : up.x;
		up.y = (lp.y > up.y) ? lp.y : up.y;
		up.z = (lp.z > up.z) ? lp.z : up.z;

		return up;
	}

	/*
	 *  @see gui.backend.PieceGraphics
	 */
	public BoundingBox getContainerAbsoluteCoord() {
		BoundingBox result = new BoundingBox();
		result.setLower(getContainerLowerLocation());
		result.setUpper(getContainerUpperLocation());

		return result;
	}

	/*
	 * @see gui.backend.PieceGraphics
	 */
	public void rotateXY90Graphics() {
		Transform3D temp = new Transform3D();
		temp.rotZ(Math.PI / 2.0);
		Vector3d currentTransVec = new Vector3d();
		pieceT3D.get(currentTransVec);
		pieceT3D.setTranslation(new Vector3d());
		pieceT3D.mul(temp, pieceT3D);
		pieceT3D.setTranslation(currentTransVec);
		pieceTG.setTransform(pieceT3D);

		rotationQueue.add("XY");
	}

	/*
	 * @see gui.backend.PieceGraphics
	 */
	public void rotateXZ90Graphics() {
		Transform3D temp = new Transform3D();
		temp.rotY(-Math.PI / 2.0);
		Vector3d currentTransVec = new Vector3d();
		pieceT3D.get(currentTransVec);
		pieceT3D.setTranslation(new Vector3d());
		pieceT3D.mul(temp, pieceT3D);
		pieceT3D.setTranslation(currentTransVec);
		pieceTG.setTransform(pieceT3D);

		rotationQueue.add("XZ");
	}

	/*
	 * @see gui.backend.PieceGraphics
	 */
	public void rotateYZ90Graphics() {
		Transform3D temp = new Transform3D();
		temp.rotX(Math.PI / 2.0);
		Vector3d currentTransVec = new Vector3d();
		pieceT3D.get(currentTransVec);
		pieceT3D.setTranslation(new Vector3d());
		pieceT3D.mul(temp, pieceT3D);
		pieceT3D.setTranslation(currentTransVec);
		pieceTG.setTransform(pieceT3D);

		rotationQueue.add("YZ");
	}

	/*
	 * @see gui.backend.PieceGraphics
	 */
	public void rotateYX90Graphics() {
		Transform3D temp = new Transform3D();
		temp.rotZ(-Math.PI / 2.0);
		Vector3d currentTransVec = new Vector3d();
		pieceT3D.get(currentTransVec);
		pieceT3D.setTranslation(new Vector3d());
		pieceT3D.mul(temp, pieceT3D);
		pieceT3D.setTranslation(currentTransVec);
		pieceTG.setTransform(pieceT3D);

		rotationQueue.add("YX");
	}

	/*
	 * @see gui.backend.PieceGraphics
	 */
	public void rotateZX90Graphics() {
		Transform3D temp = new Transform3D();
		temp.rotY(Math.PI / 2.0);
		Vector3d currentTransVec = new Vector3d();
		pieceT3D.get(currentTransVec);
		pieceT3D.setTranslation(new Vector3d());
		pieceT3D.mul(temp, pieceT3D);
		pieceT3D.setTranslation(currentTransVec);
		pieceTG.setTransform(pieceT3D);

		rotationQueue.add("ZX");
	}

	/*
	 * @see gui.backend.PieceGraphics
	 */
	public void rotateZY90Graphics() {
		Transform3D temp = new Transform3D();
		temp.rotX(-Math.PI / 2.0);
		Vector3d currentTransVec = new Vector3d();
		pieceT3D.get(currentTransVec);
		pieceT3D.setTranslation(new Vector3d());
		pieceT3D.mul(temp, pieceT3D);
		pieceT3D.setTranslation(currentTransVec);
		pieceTG.setTransform(pieceT3D);

		rotationQueue.add("ZY");
	}

	/*
	 * @see gui.backend.PieceGraphics
	 */
	public Appearance getAppearance() {
		return this.app;
	}

	/*
	 * @see gui.backend.PieceGraphics
	 */
	public void setAppearance(Appearance app) {
		this.app = app;

		for (int i = 0; i < allGeometries.size(); i++) {
			Object currentGeom = allGeometries.get(i);
			if (currentGeom instanceof Primitive) {
				((Primitive) allGeometries.get(i)).setAppearance(app);
			} else if (currentGeom instanceof Shape3D) {
				((Shape3D) allGeometries.get(i)).setAppearance(app);
			}
		}
	}

	/*
	 * @see gui.backend.PieceGraphics
	 */
	public void drawWireFrameMode() {
		PolygonAttributes pa = new PolygonAttributes();
		pa.setPolygonMode(PolygonAttributes.POLYGON_LINE);
		app.setPolygonAttributes(pa);

		for (int i = 0; i < allGeometries.size(); i++) {
			Object currentGeom = allGeometries.get(i);
			if (currentGeom instanceof Primitive) {
				((Primitive) allGeometries.get(i)).setAppearance(app);
			} else if (currentGeom instanceof Shape3D) {
				((Shape3D) allGeometries.get(i)).setAppearance(app);
			}
		}
	}

	/*
	 * @see gui.backend.PieceGraphics
	 */
	public void drawFillMode() {
		PolygonAttributes pa = new PolygonAttributes();
		pa.setPolygonMode(PolygonAttributes.POLYGON_FILL);
		app.setPolygonAttributes(pa);

		for (int i = 0; i < allGeometries.size(); i++) {
			Object currentGeom = allGeometries.get(i);
			if (currentGeom instanceof Primitive) {
				((Primitive) allGeometries.get(i)).setAppearance(app);
			} else if (currentGeom instanceof Shape3D) {
				((Shape3D) allGeometries.get(i)).setAppearance(app);
			}
		}
	}

	/*
	 * @see gui.backend.PieceGraphics
	 */
	public void enablePieceBehaviors() {
		if (pickHighlight != null
			&& pickHighlight2 != null
			&& m_BoundsBehavior != null
			&& m_BoundsBehavior.cd != null) {

			pickHighlight.setEnable(true);
			pickHighlight2.setEnable(true);
			m_BoundsBehavior.setEnable(true);
			m_BoundsBehavior.cd.setEnable(true);

			pickHighlight.setSchedulingBounds(Arena3D.bounds);
			pickHighlight2.setSchedulingBounds(Arena3D.bounds);
			m_BoundsBehavior.setSchedulingBounds(Arena3D.bounds);
			m_BoundsBehavior.cd.setSchedulingBounds(Arena3D.bounds);

		}
	}

	/*
	 * @see gui.backend.PieceGraphics
	 */
	public void disablePieceBehaviors() {
		if (pickHighlight != null
			&& pickHighlight2 != null
			&& m_BoundsBehavior != null
			&& m_BoundsBehavior.cd != null) {

			pickHighlight.setEnable(false);
			pickHighlight2.setEnable(false);
			m_BoundsBehavior.setEnable(false);
			m_BoundsBehavior.cd.setEnable(false);

			pickHighlight.setSchedulingBounds(Arena3D.boundsDisable);
			pickHighlight2.setSchedulingBounds(Arena3D.boundsDisable);
			m_BoundsBehavior.setSchedulingBounds(Arena3D.boundsDisable);
			m_BoundsBehavior.cd.setSchedulingBounds(Arena3D.boundsDisable);
		}
	}

	// PHYSICS METHODS

	/*
	 * @see engine.backend.PiecePhysics
	 */
	public Point3d getLocation() {
		return new Point3d(this.location);
	}

	/*
	 * @see engine.backend.PiecePhysics
	 */
	public void setLocation(Point3d location) {
		this.location = new Point3d(location);
	}

	/*
	 * @see engine.backend.PiecePhysics
	 */
	public void setupPhysics() {
		// Take connected BoundingBox and get new Boundingbox in absolute coordinates
		Point3d lower = new Point3d(this.getContainerLowerLocation());
		Point3d upper = new Point3d(this.getContainerUpperLocation());
		this.location = new Point3d(lower);
		this.container = new BoundingBox(lower, upper);

		double x1 = lower.x;
		double y1 = lower.y;
		double z1 = lower.z;
		double x2 = upper.x;
		double y2 = upper.y;
		double z2 = upper.z;

		physics = new HashSet();
		physics3d.Sphere p = new physics3d.Sphere(x1, y1, z1, 0.0, "p1");
		physics.add(p);
		p = new physics3d.Sphere(x2, y1, z1, 0.0, "p1");
		physics.add(p);
		p = new physics3d.Sphere(x1, y2, z1, 0.0, "p2");
		physics.add(p);
		p = new physics3d.Sphere(x1, y1, z2, 0.0, "p3");
		physics.add(p);
		p = new physics3d.Sphere(x2, y2, z1, 0.0, "p4");
		physics.add(p);
		p = new physics3d.Sphere(x1, y2, z2, 0.0, "p5");
		physics.add(p);
		p = new physics3d.Sphere(x2, y1, z2, 0.0, "p6");
		physics.add(p);

		physics3d.Cylinder c =
			new physics3d.Cylinder(x1, y1, z1, x2, y1, z1, 0.0, "c1");
		physics.add(c);
		c = new physics3d.Cylinder(x1, y1, z1, x1, y2, z1, 0.0, "c2");
		physics.add(c);
		c = new physics3d.Cylinder(x1, y1, z1, x1, y1, z2, 0.0, "c3");
		physics.add(c);

		c = new physics3d.Cylinder(x2, y1, z1, x2, y2, z1, 0.0, "c4");
		physics.add(c);
		c = new physics3d.Cylinder(x1, y2, z1, x2, y2, z1, 0.0, "c5");
		physics.add(c);
		c = new physics3d.Cylinder(x1, y2, z1, x1, y2, z2, 0.0, "c6");
		physics.add(c);
		c = new physics3d.Cylinder(x1, y1, z2, x1, y2, z2, 0.0, "c7");
		physics.add(c);
		c = new physics3d.Cylinder(x2, y1, z1, x2, y1, z2, 0.0, "c8");
		physics.add(c);
		c = new physics3d.Cylinder(x1, y1, z2, x2, y1, z2, 0.0, "c9");
		physics.add(c);

		c = new physics3d.Cylinder(x2, y1, z2, x2, y2, z1, 0.0, "c10");
		physics.add(c);
		c = new physics3d.Cylinder(x2, y2, z1, x1, y2, z2, 0.0, "c11");
		physics.add(c);
		c = new physics3d.Cylinder(x1, y2, z2, x2, y1, z2, 0.0, "c12");
		physics.add(c);

		physics3d.PlaneSegment ps =
			new physics3d.PlaneSegment(
				x1,
				y1,
				z1,
				x1,
				y2,
				z1,
				x2,
				y1,
				z1,
				"ps1");
		physics.add(ps);
		ps =
			new physics3d.PlaneSegment(
				x2,
				y2,
				z1,
				x2,
				y1,
				z1,
				x1,
				y2,
				z1,
				"ps2");
		physics.add(ps);

		ps =
			new physics3d.PlaneSegment(
				x1,
				y1,
				z1,
				x1,
				y1,
				z2,
				x1,
				y2,
				z1,
				"ps3");
		physics.add(ps);
		ps =
			new physics3d.PlaneSegment(
				x1,
				y2,
				z2,
				x1,
				y2,
				z1,
				x1,
				y1,
				z2,
				"ps4");
		physics.add(ps);

		ps =
			new physics3d.PlaneSegment(
				x1,
				y1,
				z1,
				x2,
				y1,
				z1,
				x1,
				y1,
				z2,
				"ps5");
		physics.add(ps);
		ps =
			new physics3d.PlaneSegment(
				x2,
				y1,
				z2,
				x1,
				y1,
				z2,
				x2,
				y1,
				z1,
				"ps6");
		physics.add(ps);

		ps =
			new physics3d.PlaneSegment(
				x1,
				y1,
				z2,
				x1,
				y2,
				z2,
				x2,
				y1,
				z2,
				"ps7");
		physics.add(ps);
		ps =
			new physics3d.PlaneSegment(
				x2,
				y1,
				z1,
				x2,
				y1,
				z2,
				x2,
				y2,
				z1,
				"ps8");
		physics.add(ps);
		ps =
			new physics3d.PlaneSegment(
				x1,
				y2,
				z1,
				x2,
				y2,
				z1,
				x1,
				y2,
				z2,
				"ps9");
		physics.add(ps);

		ps =
			new physics3d.PlaneSegment(
				x2,
				y2,
				z1,
				x1,
				y2,
				z2,
				x2,
				y1,
				z2,
				"ps10");
		physics.add(ps);

		setupPhysicsRotations(
			(x1 + x2) / 2.0,
			(y1 + y2) / 2.0,
			(z1 + z2) / 2.0);
		physics = Collections.unmodifiableSet(physics);

	}

	public void setupPhysicsRotations(double xbar, double ybar, double zbar) {
		for (int i = 0; i < rotationQueue.size(); i++) {
			if (rotationQueue.get(i).equals("XY")) {
				rotate90Physics(
					new physics3d.Cylinder(
						xbar,
						ybar,
						zbar,
						xbar,
						ybar,
						zbar + 1.0,
						0.0,
						"zhat"));
			} else if (rotationQueue.get(i).equals("XZ")) {
				rotate90Physics(
					new physics3d.Cylinder(
						xbar,
						ybar,
						zbar,
						xbar,
						ybar - 1.0,
						zbar,
						0.0,
						"-yhat"));
			} else if (rotationQueue.get(i).equals("YZ")) {
				rotate90Physics(
					new physics3d.Cylinder(
						xbar,
						ybar,
						zbar,
						xbar + 1.0,
						ybar,
						zbar,
						0.0,
						"xhat"));
			} else if (rotationQueue.get(i).equals("YX")) {
				rotate90Physics(
					new physics3d.Cylinder(
						xbar,
						ybar,
						zbar,
						xbar,
						ybar,
						zbar - 1.0,
						0.0,
						"-zhat"));
			} else if (rotationQueue.get(i).equals("ZX")) {
				rotate90Physics(
					new physics3d.Cylinder(
						xbar,
						ybar,
						zbar,
						xbar,
						ybar + 1.0,
						zbar,
						0.0,
						"yhat"));
			} else if (rotationQueue.get(i).equals("ZY")) {
				rotate90Physics(
					new physics3d.Cylinder(
						xbar,
						ybar,
						zbar,
						xbar - 1.0,
						ybar,
						zbar,
						0.0,
						"-xhat"));
			}
		}
	}

	public void rotate90Physics(physics3d.Cylinder tempAxisOfRotation) {
		Set newPhysics = new HashSet();
		Object nO;
		physics3d.Sphere newS;
		physics3d.Cylinder newC;
		physics3d.PlaneSegment newPS;
		for (Iterator i = this.physics.iterator(); i.hasNext();) {
			nO = i.next();
			if (nO instanceof physics3d.Sphere) {
				newS =
					physics3d.Geometry.rotateSphereAboutLine(
						(physics3d.Sphere) nO,
						tempAxisOfRotation,
						Math.PI / 2.0);
				newPhysics.add(newS);
			}

			if (nO instanceof physics3d.Cylinder) {
				newC =
					physics3d.Geometry.rotateCylinderAboutLine(
						(physics3d.Cylinder) nO,
						tempAxisOfRotation,
						Math.PI / 2.0);
				newPhysics.add(newC);
			}

			if (nO instanceof physics3d.PlaneSegment) {
				newPS =
					physics3d.Geometry.rotatePlaneSegmentAboutLine(
						(physics3d.PlaneSegment) nO,
						tempAxisOfRotation,
						Math.PI / 2.0);
				newPhysics.add(newPS);
			}
		}
		physics = newPhysics;
	}

	/* (non-Javadoc)
	 * @see engine.backend.PiecePhysics#update(double)
	 */
	public void update(double timeSlice) {
		// DOES NOTHING

	}

	/*
	 * @see engine.backend.PiecePhysics
	 */
	public void updatePhysics(double timeSlice) {
		//DO NOTHING
	}

	/* (non-Javadoc)
	 * @see engine.backend.PiecePhysics#isPhysical()
	 */
	public boolean isPhysical() {
		return this.isPhysical;
	}

	/* (non-Javadoc)
	 * @see engine.backend.PiecePhysics#setPhysical(boolean)
	 */
	public void setPhysical(boolean isPhysical) {
		this.isPhysical = isPhysical;
	}

	/*
	 * @see engine.backend.PiecePhysics
	 */
	public Set getPhysicalComponents() {
		return Collections.unmodifiableSet(physics);
	}

	/*
	 * @see engine.backend.PiecePhysics
	 */
	public BoundingBox getContainer() {
		return new BoundingBox(this.container);
	}

	/*
	 * @see engine.backend.GizmoPhysics
	 */
	public double getReflectCoeff() {
		return COR;
	}

	/*
	 * @see engine.backend.GizmoPhysics
	 */
	public void setReflectCoeff(double reflectCoeff) {
		if (reflectCoeff < 0.0)
			throw new IllegalArgumentException("Coefficient of Restitution cannot be negative!");
		else
			COR = reflectCoeff;
	}

	/*
	 * @see engine.backend.GizmoPhysics
	 */
	public void triggerConnections() {
		Iterator cIter = this.connections.iterator();
		while (cIter.hasNext()) {
			GizmoEvent event = (GizmoEvent) cIter.next();
			event.getGizmo().action(event);
		}
	}

	/*
	 * @see engine.backend.GizmoPhysics
	 */
	public void ballCollision(BallPhysics ball) {
		// triggerConnections();
	}

	/*
	 * @see engine.backend.GizmoPhysics
	 */
	public Set getConnections() {
		return Collections.unmodifiableSet(connections);
	}

	/*
	 * @see engine.backend.GizmoPhysics
	 */
	public void clearConnections() {
		connections = new HashSet();
	}

	// OTHER METHODS
	/**
	 * Creates a command line that represents the state of this gizmo
	 * @return command line string
	 */
	public String unparse() {
		String s = "";
		s =
			"TriangleC"
				+ " "
				+ toString()
				+ " "
				+ getContainerLowerLocation().x
				+ " "
				+ getContainerLowerLocation().y
				+ " "
				+ getContainerLowerLocation().z
				+ " "
				+ getReflectCoeff();

		if (this.rotationQueue.size() > 0) {
			s += "\nRotate " + toString();
			for (int i = 0; i < this.rotationQueue.size(); i++) {
				s = s + " " + rotationQueue.get(i);
			}
		}

		s += "\n";

		return s;
	}

	/**
	 * Compares the specified object with this for equality.
	 */
	public boolean equals(Object o) {
		if (o != null && o instanceof TriangularBumperC3D) {
			TriangularBumperC3D tb = (TriangularBumperC3D) o;
			return tb.name.equals(this.name);
		}
		return false;
	}

	/**
	 * @return A valid hashcode for this.
	 */
	public int hashCode() {
		if (cachedHashCode != 0) {
			return cachedHashCode;
		}
		int result = 17;
		result = 37 * result + "TriangularBumperC3D".hashCode();
		result = 37 * result + this.name.hashCode();

		cachedHashCode = result;

		return result;
	}

	/**
	 * @return A string representation of this.
	 */
	public String toString() {
		return this.name;
	}

	/* (non-Javadoc)
	 * @see backend.event.GizmoListener#addConnection(backend.event.GizmoEvent)
	 */
	public void addConnection(GizmoEvent toBeTriggered) throws EventException {
		if (toBeTriggered == null)
		throw new NullPointerException("Cannot pass in a null trigger!");

	boolean added = this.connections.add(toBeTriggered);
	if (!added)
		throw new EventException(
			"There is already a connection to this gizmo: "
				+ toBeTriggered.getGizmo());
	}

	/* (non-Javadoc)
	 * @see backend.event.GizmoListener#delConnection(backend.event.GizmoEvent)
	 */
	public void delConnection(GizmoEvent toBeTriggered) throws EventException {
		if (toBeTriggered == null)
			throw new NullPointerException("Cannot pass in a null trigger!");

		boolean deleted = this.connections.remove(toBeTriggered);
		if (!deleted)
			throw new EventException(
				"There is no connection to this gizmo: "
					+ toBeTriggered.getGizmo());
	}

	/* (non-Javadoc)
	 * @see backend.event.GizmoListener#getActions()
	 */
	public Collection getActions() {

		LinkedList l = new LinkedList();

		for (int i = 0; i < TriangularBumperC3D.COMMANDS.length; i++) {
			l.add(TriangularBumperC3D.COMMANDS[i]);
		}

		return Collections.unmodifiableCollection(l);
	}

	/* (non-Javadoc)
	 * @see backend.event.GizmoListener#action(backend.event.GizmoEvent)
	 */
	public void action(GizmoEvent event) {
		if (event.getCommand().equals(TriangularBumperC3D.DEFAULT_ACTION)) {
			// DO NOTHING
		}
	}

}
