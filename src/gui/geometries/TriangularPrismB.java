/* @author Eric Tung
 * @created on May 11, 2003
 */
package gui.geometries;

import javax.media.j3d.Shape3D;
import javax.media.j3d.TriangleArray;


/**
 * TriangularPrismB - A "Type B" triangular prism.  It is used for TriangularBumperB3D.
 */
public class TriangularPrismB extends Shape3D {
	double scale;
	
	private static final float[] verts = {
		// angled triangle
		1.0f, 0.0f, 0.0f,
		0.0f, 1.0f, 0.0f,
		0.0f, 0.0f, 1.0f,
		
		// xy
		0.0f, 0.0f, 0.0f,
		0.0f, 1.0f, 0.0f,
		1.0f, 0.0f, 0.0f,
		
		// yz
		0.0f, 0.0f, 0.0f,
		0.0f, 0.0f, 1.0f,
		0.0f, 1.0f, 0.0f,	

		// xz
		0.0f, 0.0f, 0.0f,
		1.0f, 0.0f, 0.0f,
		0.0f, 0.0f, 1.0f,
            };

	private static final float[] colors = {
		// angled triangle
		1.0f, 0.0f, 1.0f,
		1.0f, 0.0f, 1.0f,
		1.0f, 0.0f, 1.0f,		
		
		// xy
		1.0f, 0.0f, 1.0f,
		1.0f, 0.0f, 1.0f,
		1.0f, 0.0f, 1.0f,		
		
		// yz
		1.0f, 0.0f, 1.0f,
		1.0f, 0.0f, 1.0f,
		1.0f, 0.0f, 1.0f,		

		// xz
		1.0f, 0.0f, 1.0f,
		1.0f, 0.0f, 1.0f,
		1.0f, 0.0f, 1.0f,
	};
	
	
	/**
	 * @effects Constructs a TriangularPrismB with unit scale.
	 */
	public TriangularPrismB() {
		TriangleArray triangle = new TriangleArray(12, TriangleArray.COORDINATES
													| TriangleArray.COLOR_3);
		triangle.setCoordinates(0, verts);
		triangle.setColors(0, colors);		
		
		this.setGeometry(triangle);
		scale = 1.0;
	}


	/**
	 * @effects Constructs a TriangularPrismB with the specified scale.
	 * @param scale the scale of the cube
	 */
	
	public TriangularPrismB(double scale) {
		TriangleArray triangle = new TriangleArray(12, TriangleArray.COORDINATES
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
