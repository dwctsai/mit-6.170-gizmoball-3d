/* @author David Tsai
 * @created on Apr 20, 2003
 */
package engine.testing;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.media.j3d.BoundingBox;
import javax.vecmath.Point3d;

import physics3d.Cylinder;
import physics3d.Physics3dObject;
import backend.event.GizmoEvent;
import backend.events.exceptions.EventException;
import engine.backend.BallPhysics;
import engine.backend.StaticGizmoPhysics;

/**
 * CubicalBumper3D - a gizmo that represents a cubical bumper.
 */
public class Wall implements StaticGizmoPhysics {

	// GENERAL FIELDS
	private static int ID = 0;
	private final int myID;

	// PHYSICS FIELDS
	private Point3d location;
	private double xLength;
	private double yLength;
	private double zLength;
	private double COR;
	private Set blocks = new HashSet();
	private Set connections = new HashSet();
	private Set physics = new HashSet();
	private Physics3dObject cShape;
	private BoundingBox container;

	// CONSTRUCTOR 
	/**
	 * @effects Creates a CubicalBumper3D from the given parameters.
	 */
	public Wall(
		Point3d lowerCornerLocation,
		double xLength,
		double yLength,
		double zLength,
		double COR) {

		// Initialize fields.
		this.location = lowerCornerLocation;

		this.xLength = xLength;
		this.yLength = yLength;
		this.zLength = zLength;
		this.COR = COR;
		this.cShape = null;
		this.myID = Wall.ID++;
		
		Point3d upper =
			new Point3d(
				this.location.x + xLength,
				this.location.y + yLength,
				this.location.z + zLength);

		// Setup the bounding box.
		this.container = new BoundingBox(this.location, upper);

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

		Point3d lower = new Point3d(this.getLocation());
		Point3d upper =
			new Point3d(
				this.location.x + this.xLength,
				this.location.y + this.yLength,
				this.location.z + this.zLength);

		double x1 = lower.x;
		double y1 = lower.y;
		double z1 = lower.z;
		double x2 = upper.x;
		double y2 = upper.y;
		double z2 = upper.z;

		physics = new HashSet();
		physics3d.Sphere p = new physics3d.Sphere(x1, y1, z1, 0.0, "p1");
		physics.add(p);
		p = new physics3d.Sphere(x2, y1, z1, 0.0, "p2");
		physics.add(p);
		p = new physics3d.Sphere(x1, y2, z1, 0.0, "p3");
		physics.add(p);
		p = new physics3d.Sphere(x2, y2, z1, 0.0, "p4");
		physics.add(p);
		p = new physics3d.Sphere(x1, y1, z2, 0.0, "p5");
		physics.add(p);
		p = new physics3d.Sphere(x2, y1, z2, 0.0, "p6");
		physics.add(p);
		p = new physics3d.Sphere(x1, y2, z2, 0.0, "p7");
		physics.add(p);
		p = new physics3d.Sphere(x2, y2, z2, 0.0, "p8");
		physics.add(p);

		physics3d.Cylinder c =
			new physics3d.Cylinder(x1, y1, z1, x2, y1, z1, 0.0, "c1");
		physics.add(c);
		c = new physics3d.Cylinder(x1, y1, z1, x1, y2, z1, 0.0, "c2");
		physics.add(c);
		c = new physics3d.Cylinder(x2, y1, z1, x2, y2, z1, 0.0, "c3");
		physics.add(c);
		c = new physics3d.Cylinder(x1, y2, z1, x2, y2, z1, 0.0, "c4");
		physics.add(c);
		c = new physics3d.Cylinder(x1, y1, z1, x1, y1, z2, 0.0, "c5");
		physics.add(c);
		c = new physics3d.Cylinder(x2, y1, z1, x2, y1, z2, 0.0, "c6");
		physics.add(c);
		c = new physics3d.Cylinder(x1, y2, z1, x1, y2, z2, 0.0, "c7");
		physics.add(c);
		c = new physics3d.Cylinder(x2, y2, z1, x2, y2, z2, 0.0, "c8");
		physics.add(c);
		c = new physics3d.Cylinder(x1, y1, z2, x2, y1, z2, 0.0, "c9");
		physics.add(c);
		c = new physics3d.Cylinder(x1, y1, z2, x1, y2, z2, 0.0, "c10");
		physics.add(c);
		c = new physics3d.Cylinder(x2, y1, z2, x2, y2, z2, 0.0, "c11");
		physics.add(c);
		c = new physics3d.Cylinder(x1, y2, z2, x2, y2, z2, 0.0, "c12");
		physics.add(c);

		physics3d.PlaneSegment ps =
			new physics3d.PlaneSegment(x1, y1, z1, x1, y2, z1, x2, y1, z1, "ps1");
		physics.add(ps);
		ps = new physics3d.PlaneSegment(x2, y2, z1, x2, y1, z1, x1, y2, z1, "ps2");
		physics.add(ps);

		ps = new physics3d.PlaneSegment(x1, y1, z1, x1, y1, z2, x1, y2, z1, "ps3");
		physics.add(ps);
		ps = new physics3d.PlaneSegment(x1, y2, z2, x1, y2, z1, x1, y1, z2, "ps4");
		physics.add(ps);

		ps = new physics3d.PlaneSegment(x1, y2, z1, x1, y2, z2, x2, y2, z1, "ps5");
		physics.add(ps);
		ps = new physics3d.PlaneSegment(x2, y2, z2, x2, y2, z1, x1, y2, z2, "ps6");
		physics.add(ps);

		ps = new physics3d.PlaneSegment(x2, y2, z1, x2, y2, z2, x2, y1, z1, "ps7");
		physics.add(ps);
		ps = new physics3d.PlaneSegment(x2, y1, z2, x2, y1, z1, x2, y2, z2, "ps8");
		physics.add(ps);

		ps = new physics3d.PlaneSegment(x2, y1, z1, x2, y1, z2, x1, y1, z1, "ps9");
		physics.add(ps);
		ps = new physics3d.PlaneSegment(x1, y1, z2, x1, y1, z1, x2, y1, z2, "ps10");
		physics.add(ps);

		ps = new physics3d.PlaneSegment(x2, y1, z2, x2, y2, z2, x1, y1, z2, "ps11");
		physics.add(ps);
		ps = new physics3d.PlaneSegment(x1, y2, z2, x1, y1, z2, x2, y2, z2, "ps12");
		physics.add(ps);

		physics = Collections.unmodifiableSet(physics);
	}

	/* (non-Javadoc)
	 * @see engine.backend.PiecePhysics#update(double)
	 */
	public void update(double timeSlice) {
		// TODO Auto-generated method stub

	}

	/*
	 * @see engine.backend.PiecePhysics
	 */
	public void updatePhysics(double timeSlice) {
		//DO NOTHING
	}

	/*
	 * @see engine.backend.PiecePhysics
	 */
	public void resetPhysics() {
		// TODO Auto-generated method stub
	}

	/* 
	 * @see engine.backend.PiecePhysics
	 */
	public boolean isPhysical() {
		// TODO Auto-generated method stub
		return true;
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
	public Set getBlocks() {
		return new HashSet(blocks);
	}

	/*
	 * @see engine.backend.GizmoPhysics
	 */
	public void setBlocks(Set blocks) {
		this.blocks = new HashSet(blocks);
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
		while(cIter.hasNext()) {
			GizmoEvent event = (GizmoEvent) cIter.next();
			event.getGizmo().action(event);
		}
	}

	/*
	 * @see engine.backend.GizmoPhysics
	 */
	public void action() {
		//setupLooks(new Color3f(random.nextFloat(), random.nextFloat(), random.nextFloat()));
	}

	/*
	 * @see engine.backend.GizmoPhysics
	 */
	public void ballCollision(BallPhysics ball) {
		triggerConnections();
	}

	/*
	 * @see engine.backend.GizmoPhysics
	 */
	public Set getConnections() {
		return Collections.unmodifiableSet(this.connections);
	}

	/*
	 * @see engine.backend.GizmoPhysics
	 */
	public void addConnection(GizmoEvent toBeTriggered)
		throws EventException {
		if (null != toBeTriggered)
			throw new NullPointerException("Null argument to addConnection");
		if (connections.contains(toBeTriggered))
			throw new EventException("Already triggering " + toBeTriggered);
		else
			connections.add(toBeTriggered);
	}

	/*
	 * @see engine.backend.GizmoPhysics
	 */
	public void delConnection(GizmoEvent toBeTriggered)
		throws EventException {
		if (null != toBeTriggered)
			throw new EventException("Null argument to deleteConnection");
		if (connections.contains(toBeTriggered))
			connections.remove(toBeTriggered);
		else
			throw new EventException("No such trigger " + toBeTriggered);
	}

	/*
	 * @see engine.backend.GizmoPhysics
	 */
	public void clearConnections() {
		connections = new HashSet();
	}

	// OTHER METHODS

	/**
	 * Compares the specified object with this for equality.
	 */
	public boolean equals(Object o) {
		if (o != null && o instanceof Wall) {
			Wall cb = (Wall) o;
			return cb.myID == this.myID;
		}
		return false;
	}

	/**
	 * @return A valid hashcode for this.
	 */
	public int hashCode() {
		return 17 * this.myID;
	}

	/**
	 * @return A string representation of this.
	 */
	public String toString() {
		return new String("CubicalBumper3D: " + this.myID);
	}

	/* (non-Javadoc)
	 * @see engine.backend.PiecePhysics#action(backend.Event)
	 */
	public void action(GizmoEvent event) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see engine.backend.PiecePhysics#setPhysical(boolean)
	 */
	public void setPhysical(boolean isPhysical) {
		// TODO Auto-generated method stub
		
	}

  	/* (non-Javadoc)
	 * @see engine.backend.PiecePhysics#setupPhysicsRotations(double, double, double)
	 */
	public void setupPhysicsRotations(double xbar, double ybar, double zbar) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see engine.backend.PiecePhysics#rotate90Physics(physics3d.Cylinder)
	 */
	public void rotate90Physics(Cylinder axisOfRotation) {
		// TODO Auto-generated method stub
		
	}

}
