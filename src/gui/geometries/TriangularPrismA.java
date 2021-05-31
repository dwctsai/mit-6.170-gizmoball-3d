/* @author David Tsai
 * @created on Apr 26, 2003
 */
package gui.geometries;

import javax.media.j3d.Shape3D;
import javax.media.j3d.TriangleArray;


/**
 * TriangularPrismA - A "Type A" triangular prism.  It is used for TriangularBumperA3D.
 */
public class TriangularPrismA extends Shape3D {
	double scale;
	
	private static final float[] verts = {
		// front triangle
		0.0f, 0.0f, 1.0f,
		1.0f, 0.0f, 1.0f,
		0.0f, 1.0f, 1.0f,
		
		// back triangle
		0.0f, 0.0f, 0.0f,
		0.0f, 1.0f, 0.0f,
		1.0f, 0.0f, 0.0f,
		
		// xy side 1
		0.0f, 0.0f, 0.0f,
		0.0f, 1.0f, 1.0f,
		0.0f, 1.0f, 0.0f,
		
		// xy side 2
		0.0f, 0.0f, 0.0f,
		0.0f, 0.0f, 1.0f,
		0.0f, 1.0f, 1.0f,	

		// yz side 1
		0.0f, 0.0f, 0.0f,
		1.0f, 0.0f, 0.0f,
		1.0f, 0.0f, 1.0f,
		
		// yz side 2		
		0.0f, 0.0f, 0.0f,
		1.0f, 0.0f, 1.0f,
		0.0f, 0.0f, 1.0f,
		
		// hypotenuse side 1
		0.0f, 1.0f, 0.0f,
		1.0f, 0.0f, 1.0f,
		1.0f, 0.0f, 0.0f,
		
		// hypotenuse side 2	
		0.0f, 1.0f, 0.0f,
		0.0f, 1.0f, 1.0f,
		1.0f, 0.0f, 1.0f,

	};

	private static final float[] colors = {
		// front triangle
		1.0f, 0.0f, 1.0f,
		1.0f, 0.0f, 1.0f,
		1.0f, 0.0f, 1.0f,		
				
		// back triangle
		1.0f, 0.0f, 1.0f,
		1.0f, 0.0f, 1.0f,
		1.0f, 0.0f, 1.0f,		
		
		// xy side 1
		1.0f, 0.0f, 1.0f,
		1.0f, 0.0f, 1.0f,
		1.0f, 0.0f, 1.0f,		
		
		// xy side 2
		1.0f, 0.0f, 1.0f,
		1.0f, 0.0f, 1.0f,
		1.0f, 0.0f, 1.0f,		

		// yz side 1
		1.0f, 0.0f, 1.0f,
		1.0f, 0.0f, 1.0f,
		1.0f, 0.0f, 1.0f,		
		
		// yz side 2		
		1.0f, 0.0f, 1.0f,
		1.0f, 0.0f, 1.0f,
		1.0f, 0.0f, 1.0f,		
		
		// hypotenuse side 1
		1.0f, 0.0f, 1.0f,
		1.0f, 0.0f, 1.0f,
		1.0f, 0.0f, 1.0f,		
		
		// hypotenuse side 2	
		1.0f, 0.0f, 1.0f,
		1.0f, 0.0f, 1.0f,
		1.0f, 0.0f, 1.0f,		
	
	};
	
	
	/**
	 * @effects Constructs a TriangularPrismA with unit scale.
	 */
	public TriangularPrismA() {
		TriangleArray triangle = new TriangleArray(24, TriangleArray.COORDINATES
													| TriangleArray.COLOR_3);
		triangle.setCoordinates(0, verts);
		triangle.setColors(0, colors);		
		
		this.setGeometry(triangle);
		scale = 1.0;


	}


	/**
	 * @effects Constructs a TriangularPrismA with the specified scale.
	 * @param scale the scale of the cube
	 */
	
	public TriangularPrismA(double scale) {
		TriangleArray triangle = new TriangleArray(24, TriangleArray.COORDINATES
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
