/*
 * Created on Apr 23, 2003
 *
 * @author Ragu Vijaykumar
 */
package engine;

import javax.vecmath.Tuple3d;
import javax.vecmath.Tuple3f;

/**
 *	Dimension3d - represents dimensional cooridinates
 *
 *  @author Ragu Vijaykumar
 */
public class Dimension3d extends Tuple3d {

	/**
	 * Constructs and initializes a Dimension3d from the specified xyz dimensions.
	 * @param x - z dimension
	 * @param y - y dimension
	 * @param z - z dimension
	 */
	public Dimension3d(double x, double y, double z) {
		super(x, y, z);
	}

	/**
	 * Constructs and initializes a Dimension3d from the array of length 3.
	 * @param d - array of three doubles that represent the dimensions
	 */
	public Dimension3d(double[] d) {
		super(d);
	}

	/**
	 * Constructs and initializes a Dimension3d from the specified Tuple3d.
	 * @param t1 - Tuple3d containin dimensions
	 */
	public Dimension3d(Tuple3d t1) {
		super(t1);
	}

	/**
	 * Constructs and initializes a Tuple3d from the specified Tuple3f
	 * @param t1 - Tuple3f containin dimensions
	 */
	public Dimension3d(Tuple3f t1) {
		super(t1);
	}

	/**
	 * Creates a default Dimension3d object with dimesions {0,0,0}
	 */
	public Dimension3d() {
		super();
	}

}
