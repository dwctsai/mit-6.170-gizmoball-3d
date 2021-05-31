//
//  PlaneSegment.java
//  
//
//  Created by Eric Tung on Fri Apr 11 2003.
//

// File history
// 2003/04/11 EGT added basic methods that might be what is needed
// 2003/04/29 EGT clarified AF/RI a bit
// 2003/05/10 EGT added name parameter
package physics3d;

// from physics package
import javax.media.j3d.Transform3D;
import javax.vecmath.Vector3d;

/**
 *  PlaneSegment is an immutable ADT which
 *    represents a plane in 3-space.
 *    The plane is defined by three non-collinear points
 *    and the normal is right-handed (p1->p2->p3, thumb points to normal)
 *    A PlaneSegment is only a part of a plane.
 *
 **/

public final class PlaneSegment implements Physics3dObject {
	private final Vector3d p1;
	private final Vector3d p2;
	private final Vector3d p3;
	private final String name;

	// RepInvariant: p1 != null && p2 != null && p3 != null
	//                 && p1,p2,p3 are not collinear
	// AbsFunction: The plane containg p1, p2, p3 and with normal
	//              in the direction (p2-p1)x(p3-p2)
	//              The rotational components of p1, p2, p3 are ignored.

	/**
	 * @requires <code>t1</code>, <code>t2</code>, <code>t3</code>
	 *     are not null and are not collinear
	 * @effects constructs a new PlaneSegment containing
	 * <code>t1</code>, <code>t2</code>, <code>t3</code>.
	 */
	public PlaneSegment(
		Transform3D t1,
		Transform3D t2,
		Transform3D t3,
		String n) {
		Vector3d v = new Vector3d();
		t1.get(v);
		p1 = new Vector3d(v);
		t2.get(v);
		p2 = new Vector3d(v);
		t3.get(v);
		p3 = new Vector3d(v);
		name = n;
	}

	/**
	 * @requires <code>v1</code>, <code>v2</code>, <code>v3</code>
	 *     are not null and are not collinear.
	 * @effects constructs a new PlaneSegment containing
	 * <code>v1</code>, <code>v2</code>, <code>t3</code>.
	 */
	public PlaneSegment(Vector3d v1, Vector3d v2, Vector3d v3, String n) {
		p1 = new Vector3d(v1);
		p2 = new Vector3d(v2);
		p3 = new Vector3d(v3);
		name = n;
	}

	/**
	 * @requires <code>(x1,y1,z1)</code>, <code>(x2,y2,z2)</code>, <code>(x3,x3,z3)</code>
	 *     are not collinear.
	 * @effects constructs a new PlaneSegment containing
	 * <code>(x1,y1,z1)</code>, <code>(x2,y2,z2)</code>, <code>(x3,y3,z3)</code>.
	 */
	public PlaneSegment(
		double x1,
		double y1,
		double z1,
		double x2,
		double y2,
		double z2,
		double x3,
		double y3,
		double z3,
		String n) {
		p1 = new Vector3d(x1, y1, z1);
		p2 = new Vector3d(x2, y2, z2);
		p3 = new Vector3d(x3, y3, z3);
		name = n;
	}

	// Observers --------------------------------------------------------------

	public Vector3d p1() {
		return new Vector3d(p1);
	}
	public Vector3d p2() {
		return new Vector3d(p2);
	}
	public Vector3d p3() {
		return new Vector3d(p3);
	}
	public String name() {
		return name;
	}

	public Vector3d normal() {
		Vector3d v21 = new Vector3d();
		Vector3d v32 = new Vector3d();
		Vector3d vn = new Vector3d();
		v21.sub(p2, p1);
		v32.sub(p3, p2);
		vn.cross(v21, v32);
		vn.normalize();
		return vn;
	}

	public String toString() {
		return "PlaneSegment(" + p1 + ":" + p2 + ":" + p3 + ")";
	}

	public boolean equals(PlaneSegment ps) {
		if (ps == null)
			return false;
		return (p1.equals(ps.p1) && p2.equals(ps.p2) && p3.equals(ps.p3));
	}

	public boolean equals(Object o) {
		if (o instanceof PlaneSegment)
			return equals((PlaneSegment) o);
		else
			return false;
	}

	/**
	 * The hashcode is based solely on the name of the object
	 */
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
