/* @author Ragu Vijaykumar
 * @created on Apr 20, 2003
 */
package engine.testing;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.media.j3d.BoundingBox;
import javax.media.j3d.Transform3D;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import physics3d.Cylinder;
import physics3d.Physics3dObject;
import backend.event.GizmoEvent;
import engine.backend.BallPhysics;

/**
 *	Ball3D - a piece that represents a 3D ball.
 */
public class Ball implements BallPhysics {

	// GENERAL FIELDS
	private static int ID = 0;
	private final int myID;

	// PHYSICS FIELDS
	private Point3d location;
	private double radius;
	private Transform3D velocity;
	private double mass;
	private Set physics = new HashSet();
	private BoundingBox container;

	// CONSTRUCTOR 
	/**
	 * @effects Creates a Ball3D from the given parameters.
	 */
	public Ball(
		Point3d location,
		double radius,
		double mass,
		Transform3D velocity) {

		// Initialize fields.
		this.location = location;
		this.radius = radius;
		this.velocity = velocity;
		this.mass = mass;
		this.myID = Ball.ID++;
		
		Point3d lower =
			new Point3d(
				location.x - radius * 2.0,
				location.y - radius * 2.0,
				location.z - radius * 2.0);
		Point3d upper =
			new Point3d(
				location.x + radius * 2.0,
				location.y + radius * 2.0,
				location.z + radius * 2.0);

		this.container = new BoundingBox(lower, upper);
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

		physics3d.Sphere pSphere =
			new physics3d.Sphere(new Vector3d(location), radius, "pSphere");

		physics = new HashSet();
		physics.add(pSphere);

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
		// Update all physical references
		Point3d location = new Point3d(this.location);

		Point3d lower =
			new Point3d(
				location.x - radius * 2.0,
				location.y - radius * 2.0,
				location.z - radius * 2.0);
		Point3d upper =
			new Point3d(
				location.x + radius * 2.0,
				location.y + radius * 2.0,
				location.z + radius * 2.0);

		this.container = new BoundingBox(lower, upper);

		physics3d.Sphere pSphere =
			new physics3d.Sphere(new Vector3d(location), radius, "pSphere");
		physics = new HashSet();
		physics.add(pSphere);
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
	 * @see engine.backend.BallPhysics
	 */
	public double getRadius() {
		return this.radius;
	}

	/*
	 * @see engine.backend.BallPhysics
	 */
	public Transform3D getVelocity() {
		return new Transform3D(this.velocity);
	}

	/*
	 * @see engine.backend.BallPhysics
	 */
	public void setVelocity(Transform3D velocity) {
		this.velocity = new Transform3D(velocity);
	}

	/*
	 * @see engine.backend.BallPhysics
	 */
	public double getMass() {
		return this.mass;
	}

	/*
	 * @see engine.backend.BallPhysics
	 */
	public void setMass(double mass) {
		this.mass = mass;
	}

	// OTHER METHODS

	/**
	 * Compares the specified object with this for equality.
	 */
	public boolean equals(Object o) {
		if (o != null && o instanceof Ball) {
			Ball ball = (Ball) o;
			return ball.myID == this.myID;
		}

		return false;
	}

	/**
	 * @return A valid hashcode for this.
	 */
	public int hashCode() {
		return 37 * this.myID;
	}

	/**
	 * @return A string representation of this.
	 */
	public String toString() {
		return new String("Ball: " + this.myID);
	}

	/* (non-Javadoc)
	 * @see engine.backend.PiecePhysics#isPhysical()
	 */
	public boolean isPhysical() {
		return true;
	}
	
	// Other methods
	
	public Physics3dObject getShape() {
		Iterator temp = this.physics.iterator();
		return (Physics3dObject) temp.next();
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
