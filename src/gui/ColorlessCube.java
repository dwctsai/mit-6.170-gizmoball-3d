/* @author David Tsai
 *
 *
 * This class is a modification of:
 *
 *	@(#)ColorCube.java 1.17 02/04/01 15:01:21
 *
 * Copyright (c) 1996-2002 Sun Microsystems, Inc. All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * - Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * - Redistribution in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in
 *   the documentation and/or other materials provided with the
 *   distribution.
 *
 * Neither the name of Sun Microsystems, Inc. or the names of
 * contributors may be used to endorse or promote products derived
 * from this software without specific prior written permission.
 *
 * This software is provided "AS IS," without a warranty of any
 * kind. ALL EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND
 * WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY
 * EXCLUDED. SUN AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
 * DISTRIBUTING THE SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL SUN
 * OR ITS LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR
 * FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR
 * PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY OF
 * LIABILITY, ARISING OUT OF THE USE OF OR INABILITY TO USE SOFTWARE,
 * EVEN IF SUN HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 *
 * You acknowledge that Software is not designed,licensed or intended
 * for use in the design, construction, operation or maintenance of
 * any nuclear facility.
 */
package gui;

import javax.media.j3d.QuadArray;
import javax.media.j3d.Shape3D;

/**
 * A ColorlessCube is basically a ColorCube primitive, except with only one color.
 */
public class ColorlessCube extends Shape3D {
	double scale;
	private static final float[] verts = {
	// front face
	 1.0f, -1.0f,  1.0f,
	 1.0f,  1.0f,  1.0f,
	-1.0f,  1.0f,  1.0f,
	-1.0f, -1.0f,  1.0f,
	// back face
	-1.0f, -1.0f, -1.0f,
	-1.0f,  1.0f, -1.0f,
	 1.0f,  1.0f, -1.0f,
	 1.0f, -1.0f, -1.0f,
	// right face
	 1.0f, -1.0f, -1.0f,
	 1.0f,  1.0f, -1.0f,
	 1.0f,  1.0f,  1.0f,
	 1.0f, -1.0f,  1.0f,
	// left face
	-1.0f, -1.0f,  1.0f,
	-1.0f,  1.0f,  1.0f,
	-1.0f,  1.0f, -1.0f,
	-1.0f, -1.0f, -1.0f,
	// top face
	 1.0f,  1.0f,  1.0f,
	 1.0f,  1.0f, -1.0f,
	-1.0f,  1.0f, -1.0f,
	-1.0f,  1.0f,  1.0f,
	// bottom face
	-1.0f, -1.0f,  1.0f,
	-1.0f, -1.0f, -1.0f,
	 1.0f, -1.0f, -1.0f,
	 1.0f, -1.0f,  1.0f,
	};

	
	/**
	 * @effects Constructs a one-color cube with unit scale.  The corners of the
	 * color cube are [-1,-1,-1] and [1,1,1].
	 */
	public ColorlessCube() {
	QuadArray cube = new QuadArray(24, QuadArray.COORDINATES);
	cube.setCoordinates(0, verts);

	this.setGeometry(cube);

	scale = 1.0;
	}


	/**
	 * @effects Constructs a color cube with the specified scale.  The corners of the
	 * color cube are [-scale,-scale,-scale] and [scale,scale,scale].
	 * @param scale the scale of the cube
	 */
	public ColorlessCube(double scale) {
	QuadArray cube = new QuadArray(24, QuadArray.COORDINATES);

	float scaledVerts[] = new float[verts.length];
	for (int i = 0; i < verts.length; i++)
		scaledVerts[i] = verts[i] * (float)scale;

	cube.setCoordinates(0, scaledVerts);

	this.setGeometry(cube);

	this.scale = scale;
	}


	/**
	 * Returns the scale of the Cube
	 *
	 * @since Java 3D 1.2.1
	 */
	public double getScale() {
	return scale;
	}
}
