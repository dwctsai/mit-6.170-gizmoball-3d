/* @author Eric Tung
 * @created on May 11, 2003
 */
package gui.geometries;

import javax.media.j3d.Shape3D;
import javax.media.j3d.TriangleArray;


/**
 * TriangularPrismC - A "Type C" triangular prism.  It is used for TriangularBumperC3D.
 */
public class TriangularPrismC extends Shape3D {
	double scale;
	
	private static final float[] verts = {		
		// z=0 part 1
		0.0f, 0.0f, 0.0f,
                0.0f, 1.0f, 0.0f,
                1.0f, 0.0f, 0.0f,

		// z=0 part 2
                1.0f, 1.0f, 0.0f,
                1.0f, 0.0f, 0.0f,
                0.0f, 1.0f, 0.0f,

                // x=0 part 1
                0.0f, 0.0f, 0.0f,
                0.0f, 0.0f, 1.0f,
                0.0f, 1.0f, 0.0f,

                // x=0 part 2
                0.0f, 1.0f, 1.0f,
                0.0f, 1.0f, 0.0f,
                0.0f, 0.0f, 1.0f,

                // y=0 part 1
                0.0f, 0.0f, 0.0f,
                1.0f, 0.0f, 0.0f,
                0.0f, 0.0f, 1.0f,

                // y=0 part 2
                1.0f, 0.0f, 1.0f,
                0.0f, 0.0f, 1.0f,
                1.0f, 0.0f, 0.0f,

                // z=1
                0.0f, 0.0f, 1.0f,
                0.0f, 1.0f, 1.0f,
                1.0f, 0.0f, 1.0f,

                // x=1
                1.0f, 0.0f, 0.0f,
                1.0f, 0.0f, 1.0f,
                1.0f, 1.0f, 0.0f,

                // y=1
                0.0f, 1.0f, 0.0f,
                1.0f, 1.0f, 0.0f,
                0.0f, 1.0f, 1.0f,

                // angled
                1.0f, 1.0f, 0.0f,
                0.0f, 1.0f, 1.0f,
                1.0f, 0.0f, 1.0f,
            };

	private static final float[] colors = {
		// z=0 part 1
		1.0f, 0.0f, 1.0f,
		1.0f, 0.0f, 1.0f,
		1.0f, 0.0f, 1.0f,		

		// z=0 part 2
		1.0f, 0.0f, 1.0f,
		1.0f, 0.0f, 1.0f,
		1.0f, 0.0f, 1.0f,		

		// x=0 part 1
		1.0f, 0.0f, 1.0f,
		1.0f, 0.0f, 1.0f,
		1.0f, 0.0f, 1.0f,		

		// x=0 part 2
		1.0f, 0.0f, 1.0f,
		1.0f, 0.0f, 1.0f,
		1.0f, 0.0f, 1.0f,		

		// y=0 part 1
		1.0f, 0.0f, 1.0f,
		1.0f, 0.0f, 1.0f,
		1.0f, 0.0f, 1.0f,		

		// y=0 part 2
		1.0f, 0.0f, 1.0f,
		1.0f, 0.0f, 1.0f,
		1.0f, 0.0f, 1.0f,		
		
		// z=1
		1.0f, 0.0f, 1.0f,
		1.0f, 0.0f, 1.0f,
		1.0f, 0.0f, 1.0f,		
		
		// x=1
		1.0f, 0.0f, 1.0f,
		1.0f, 0.0f, 1.0f,
		1.0f, 0.0f, 1.0f,		

		// y=1
		1.0f, 0.0f, 1.0f,
		1.0f, 0.0f, 1.0f,
		1.0f, 0.0f, 1.0f,

		// angled
		1.0f, 0.0f, 1.0f,
		1.0f, 0.0f, 1.0f,
		1.0f, 0.0f, 1.0f,
	};
	
	
	/**
	 * @effects Constructs a TriangularPrismB with unit scale.
	 */
	public TriangularPrismC() {
		TriangleArray triangle = new TriangleArray(30, TriangleArray.COORDINATES
													| TriangleArray.COLOR_3);
		triangle.setCoordinates(0, verts);
		triangle.setColors(0, colors);		
		
		this.setGeometry(triangle);
		scale = 1.0;
	}


	/**
	 * @effects Constructs a TriangularPrismC with the specified scale.
	 * @param scale the scale of the cube
	 */
	
	public TriangularPrismC(double scale) {
		TriangleArray triangle = new TriangleArray(30, TriangleArray.COORDINATES
													| TriangleArray.COLOR_3);
		float scaledVerts[] = new float[verts.length];		
		for (int i = 0; i < verts.length; i++) {
			scaledVerts[i] = verts[i] * (float)scale;
		}
		
		triangle.setCoordinates(0, scaledVerts);
		triangle.setColors(0, colors);				
		this.setGeometry(triangle);
		this.scale = scale;		
	}


	/**
	 * @return The scale of this.
	 */
	public double getScale() {
		return scale;
	}
	
	
		

}
