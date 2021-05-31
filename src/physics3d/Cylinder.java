//
//  Cylinder.java
//  
//
//  Created by Eric Tung on Fri Apr 11 2003.
//

// File history
// 2003/04/11 EGT added basic methods to match that of physics.LineSegment
// 2003/04/11 EGT changed internal rep to Vector3d
// 2003/04/12 EGT changed LineSegment3D to Cylinder, because it's not going to be incredibly harder for Geometry
// 2003/04/14 EGT fixed bug which caused p1 and p2 to point to the same object in constructor that used Transform3D
// 2003/04/20 EGT fixed getVector so it actually returns p2 - p1 instead of p1 - p2
// 2003/04/29 EGT clarified AF/RI a bit
// 2003/05/10 EGT added name parameter

package physics3d;

// from physics package
import javax.media.j3d.Transform3D;
import javax.vecmath.Vector3d;

/**
 *  Cylinder is an immutable ADT which
 *    represents a cylinder in 3-space
 *
 **/

public final class Cylinder implements Physics3dObject {
	private final Vector3d p1;
	private final Vector3d p2;
	private final double radius;
	private final String name;

	// RepInvariant: p1 != null && p2 != null && p1 != p2 && radius >= 0.0
	// AbsFunction: The cylinder starting at p1 and going to p2 with radius radius.

	// Constructors -------------------------------------------------

	/**
	 * @requires <code>t1</code> and <code>t2</code> are not null and r >= 0.0
	 * @effects constructs a new Cylinder between the two transforms
	 * <code>t1</code> and <code>t2</code> with radius r.
	 */
	public Cylinder(Transform3D t1, Transform3D t2, double r, String n) {
		Vector3d v = new Vector3d();
		t1.get(v);
		p1 = new Vector3d(v);
		t2.get(v);
		p2 = new Vector3d(v);
		radius = r;
		name = n;
	}

	/**
	 * @requires <code>v1</code> and <code>v2</code> are not null and r >= 0.0
	 * @effects constructs a new LineSegment3D between the two vectors
	 * <code>v1</code> and <code>v2</code> with radius r.
	 */
	public Cylinder(Vector3d v1, Vector3d v2, double r, String n) {
		p1 = new Vector3d(v1);
		p2 = new Vector3d(v2);
		radius = r;
		name = n;
	}

	/**
	 * @requires r >= 0.0
	 * @effects constructs a new Cylinder between
	 * <code>(x1,y1,z1)</code> and <code>(x2,y2,z2)</code> with radius r.
	 */
	public Cylinder(
		double x1,
		double y1,
		double z1,
		double x2,
		double y2,
		double z2,
		double r,
		String n) {
		p1 = new Vector3d(x1, y1, z1);
		p2 = new Vector3d(x2, y2, z2);
		radius = r;
		name = n;
	}

	// Observers -------------------------------------------------

	/**
	 * @return the first point of <code>this</code> Cylinder.
	 */
	public Vector3d p1() {
		return new Vector3d(p1);
	}

	/**
	 * @return the second point of <code>this</code> Cylinder.
	 */
	public Vector3d p2() {
		return new Vector3d(p2);
	}

	/**
	 * @return the radius of <code>this</code> Cylinder.
	 */
	public double getRadius() {
		return radius;
	}

	public String name() {
		return name;
	}

	// Note physics3d does not have a direct analog for Angle.

	/**
	 * @return the vector equal to p2 - p1.
	 */
	public Vector3d getVector() {
		Vector3d v3d = new Vector3d();
		v3d.sub(p2, p1);
		return v3d;
	}

	/**
	 * @return the length of <code>this</code> line segment.
	 */
	public double length() {
		Vector3d v3d = new Vector3d();
		v3d.sub(p1, p2);
		return v3d.length();
	}

	public String toString() {
		return "Cylinder(" + p1 + "-" + p2 + " r:" + radius + ")";
	}

	public boolean equals(Cylinder c) {
		if (c == null)
			return false;
		return (p1.equals(c.p1) && p2.equals(c.p2) && (radius == c.radius));
	}

	public boolean equals(Object o) {
		if (o instanceof Cylinder)
			return equals((Cylinder) o);
		else
			return false;
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
