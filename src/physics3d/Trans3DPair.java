//
//  Trans3DPair.java
//  
//
//  Created by Eric Tung on Mon Apr 14 2003.
//

package physics3d;

import javax.media.j3d.Transform3D;

public class Trans3DPair {
  /**
   * <code>Trans3DPair</code> is a simple immutable record type representing
   * a pair of <code>Transform3D</code>s.
   **/
    private final Transform3D trans1;
    private final Transform3D trans2;

    /**
     * Creates a Trans3DPair with <code>t1</code> and
     * <code>t2</code> as given
     **/
    public Trans3DPair(Transform3D t1, Transform3D t2) {
      trans1 = new Transform3D(t1);
      trans2 = new Transform3D(t2);
    }

    public Transform3D t1() { return new Transform3D(trans1); }
    public Transform3D t2() { return new Transform3D(trans2); }

    public String toString() {
      return "[" + trans1 + "," + trans2 + "]";
    }

    public boolean equals(Object o) {
      return (o instanceof Trans3DPair) && equals((Trans3DPair) o);
    }

    public boolean equals(Trans3DPair p) {
      if (p == null) return false;
      return
	((trans1 == null) ? (p.trans1 == null) : trans1.equals(p.trans1)) &&
	((trans2 == null) ? (p.trans2 == null) : trans2.equals(p.trans2));
    }

    public int hashCode() {
      return
	((trans1 == null) ? 0 : (3 * trans1.hashCode())) +
	((trans2 == null) ? 0 : (7 * trans2.hashCode()));
    }
}
