//
//  Sphere.java
//  
//
//  Created by Eric Tung on Sun Apr 06 2003.
//

// File history
// 2003/04/11 EGT added basic methods to match that of physics.Circle
// 2003/04/21 EGT added method to get the center as a Transform3D instead of a Vector3d
// 2003/04/29 EGT clarified AF/RI a bit
// 2003/05/10 EGT added name parameter

package physics3d;

// from physics package
import javax.media.j3d.Transform3D;
import javax.vecmath.Vector3d;

/**
 *  Sphere is an immutable ADT which represents a sphere in 3-space
 *
 **/

public final class Sphere implements Physics3dObject {
	private final double radius;
	private final Transform3D trans;
	private final String name;

	// RepInvariant: radius >= 0.0, trans != null
	// AbsFunction: The sphere with the transform trans and with radius radius.

	// Constructors -------------------------------------------------

	/**
	 * @requires <code>r</code> >= 0 and <code>t</code> != null
	 *
	 * @effects Creates a new Sphere with the specified size and location.
	 *
	 * @param t the transform of the center point of the circle
	 * @param r the radius of the circle
	 */
	public Sphere(Transform3D t, double r, String n) {
		radius = r;
		trans = new Transform3D(t);
		// copy so that Sphere can't be changed outside
		name = n;
	}

	/**
	 * @requires <code>r</code> >= 0 and <code>v</code> != null
	 *
	 * @effects Creates a new Sphere with the specified size and location.
	 *
	 * @param v the vector to the center point of the circle
	 * @param r the radius of the circle
	 */
	public Sphere(Vector3d v, double r, String n) {
		radius = r;
		Transform3D temp = new Transform3D();
		temp.set(v);
		trans = new Transform3D(temp);
		name = n;
	}

	/**
	 * @requires <code>r</code> >= 0
	 *
	 * @effects Creates a new Sphere with the specified size and location.
	 *
	 * @param x the x coordinate of the center of the circle
	 * @param y the y coordinate of the center of the circle
	 * @param z the z coordinate of the center of the circle
	 * @param r the radius of the circle
	 */
	public Sphere(double x, double y, double z, double r, String n) {
		radius = r;
		Transform3D temp = new Transform3D();
		temp.set(new Vector3d(x, y, z));
		trans = new Transform3D(temp);
		name = n;
	}

	// Observers --------------------------------------------

	/**
	 * @return the center point of this sphere as a Vector3d
	 */
	public Vector3d getCenter() {
		Vector3d v3d = new Vector3d();
		trans.get(v3d);
		return new Vector3d(v3d);
		// return a new object to avoid mutability problems
	}

	/**
	 * @return the center point of this sphere as a Transform3D
	 */
	public Transform3D getCenterAsTransform() {
		return new Transform3D(trans);
		// return a new object to avoid mutability problems
	}

	/**
	 * @return the radius of this sphere
	 */
	public double getRadius() {
		return radius;
	}

	public String name() {
		return name;
	}

	// Methods
	public boolean equals(Sphere s) {
		if (s == null)
			return false;
		return (radius == s.radius) && trans.equals(s.trans);
	}

	public boolean equals(Object o) {
		if (o instanceof Sphere)
			return equals((Sphere) o);
		else
			return false;
	}

	public String toString() {
		return "[Sphere center=" + trans + " radius=" + radius + "]";
	}

	public int hashCode() {
		return this.name.hashCode();
	}

	/* (non-Javadoc)
	 * @see physics3d.Physics3dObject#getName()
	 */
	public String getName() {
		return this.name;
	}
}
