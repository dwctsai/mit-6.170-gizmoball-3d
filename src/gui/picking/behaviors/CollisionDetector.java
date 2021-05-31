/* @author David Tsai
 *
 *
 * This class is a modification of:
 * 
 *	@(#)CollisionDetector.java 1.10 02/04/01 15:03:22
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

import gui.Arena3DScene;

import java.util.Enumeration;

import javax.media.j3d.Appearance;
import javax.media.j3d.Behavior;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.Shape3D;
import javax.media.j3d.TransparencyAttributes;
import javax.media.j3d.WakeupOnCollisionEntry;
import javax.media.j3d.WakeupOnCollisionExit;
import javax.vecmath.Color3f;

/**
 * CollisionDetector is a behavior that detects when a collision
 * between Shape3Ds has occurred.  For Gizmoball, this class
 * is specifically used for detecting collisions between GizmoPhysics bounding boxes
 * while in Editor mode.  The bounding box will light up red when it
 * collides/overlaps with another bounding box, indicating that the user has
 * placed the gizmo in an invalid or occupied region.
 */
public class CollisionDetector extends Behavior {
	// FIELDS
    private static final Color3f highlightColor =
	new Color3f(1.0f, 0.0f, 0.0f);
    private static final ColoringAttributes highlight =
	new ColoringAttributes(highlightColor,
			       ColoringAttributes.SHADE_GOURAUD);

    private boolean inCollision = false;
    private Shape3D shape;
    private ColoringAttributes shapeColoring;
    private Appearance shapeAppearance;

    private WakeupOnCollisionEntry wEnter;
    private WakeupOnCollisionExit wExit;

	// CONSTRUCTOR
	/**
	 * @effects Constructs a CollisionDetector from the given parameters.
	 */
    public CollisionDetector(Shape3D s) {
		shape = s;
		shapeAppearance = shape.getAppearance();
		shapeColoring = shapeAppearance.getColoringAttributes();
		inCollision = false;
    }

	// METHODS
	/**
	 * @effects Initializes this behavior.
	 */

    public void initialize() {
		wEnter = new WakeupOnCollisionEntry(shape);
		wExit = new WakeupOnCollisionExit(shape);
		wakeupOn(wEnter);
    }

	/**
	 * @effects Swaps the collided object's appearance depending on
	 *          whether or not it is currently colliding with an object.
	 */
    public void processStimulus(Enumeration criteria) {
		inCollision = !inCollision;

		if (inCollision) {
			// Update the collision count.
			Arena3DScene.collisionCount++;
			
			// Draw the collided object in Polygon Fill mode, and paint it transparent red.
		    shapeAppearance.setColoringAttributes(highlight);
			PolygonAttributes pa = new PolygonAttributes();
			pa.setPolygonMode(PolygonAttributes.POLYGON_FILL);	    
		    shapeAppearance.setPolygonAttributes(pa);
		    TransparencyAttributes ta = new TransparencyAttributes();
			ta.setTransparencyMode(TransparencyAttributes.BLENDED);
			ta.setTransparency(0.6f);
		    shapeAppearance.setTransparencyAttributes(ta);
	    	wakeupOn(wExit);
		}
		else {
			// Update the collision count.
			Arena3DScene.collisionCount--;
			
			// Object is no longer in collision, so restore its former appearance.
		    shapeAppearance.setColoringAttributes(shapeColoring);
			PolygonAttributes pa = new PolygonAttributes();
			pa.setPolygonMode(PolygonAttributes.POLYGON_LINE);	    
			shapeAppearance.setPolygonAttributes(pa);	    
	    	wakeupOn(wEnter);
		}
    }
}
