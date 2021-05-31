/* @author David Tsai
 *
 *
 * This class is a modification of:
 * 
 *	@(#)PickHighlightBehavior.java 1.15 02/04/01 15:03:24
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
package gui.picking.behaviors;

import javax.media.j3d.Appearance;
import javax.media.j3d.Bounds;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.Material;
import javax.media.j3d.Shape3D;
import javax.vecmath.Color3f;

import com.sun.j3d.utils.picking.PickResult;
import com.sun.j3d.utils.picking.PickTool;
import com.sun.j3d.utils.picking.behaviors.PickMouseBehavior;

/**
 * PickHighlightBehavior provides a picking behavior such that when a mouse
 * such that when you click an object, it will become highlighted.
 * 
 * This class is modified specifically for Gizmoball.
 */
public class PickHighlightBehavior extends PickMouseBehavior {
  Appearance savedAppearance = null;
  Shape3D oldShape = null;
  Appearance highlightAppearance;

  public PickHighlightBehavior(BranchGroup root, Canvas3D canvas, Bounds bounds) {
      super(canvas, root, bounds);
      this.setSchedulingBounds(bounds);
      Color3f white = new Color3f(1.0f, 1.0f, 1.0f);
      Color3f black = new Color3f(0.0f, 0.0f, 0.0f);
	  Color3f highlightColor = new Color3f(0.0f, 1.0f, 1.0f);

	  highlightAppearance = new Appearance();
      Material highlightMaterial = new Material(highlightColor, black,
						highlightColor, white,
						80.0f);
      highlightAppearance.setMaterial(highlightMaterial);

      pickCanvas.setMode(PickTool.BOUNDS);
  }

	public void updateScene(int xpos, int ypos) {
		PickResult pickResult = null;
		Shape3D shape = null;

		pickCanvas.setShapeLocation(xpos, ypos);

		pickResult = pickCanvas.pickClosest();
		if (pickResult != null) {
			shape = (Shape3D) pickResult.getNode(PickResult.SHAPE3D);
		}

		if (oldShape != null) {
			oldShape.setAppearance(savedAppearance);
		}
		if (shape != null) {
			savedAppearance = shape.getAppearance();
			oldShape = shape;
			shape.setAppearance(highlightAppearance);
		}
	}
}
