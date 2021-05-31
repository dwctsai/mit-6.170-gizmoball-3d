/* @author David Tsai
 *
 *
 * This class is a modification of:
 * 
 *	@(#)PickZoomBehavior.java 1.7 02/04/01 15:01:47
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
import gui.MainFrame;
import gui.picking.MouseZoom;

import javax.media.j3d.Bounds;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Vector3d;

import backend.Piece;

import com.sun.j3d.utils.behaviors.mouse.MouseBehavior;
import com.sun.j3d.utils.behaviors.mouse.MouseBehaviorCallback;
import com.sun.j3d.utils.picking.PickResult;

/**
 * A mouse behavior that allows user to pick and zoom scene graph objects.
 * Common usage: 1. Create your scene graph. 2. Create this behavior with
 * the root and canvas. See PickRotateBehavior for more details.
 * 
 * This class is modified specifically for Gizmoball.
 */

public class PickZoomBehavior
	extends PickMouseBehavior
	implements MouseBehaviorCallback {
	MouseZoom zoom;
	public static PickResult pr = null;	
	private PickingCallback callback = null;
	private TransformGroup currentTG;

	/**
	 * Creates a pick/zoom behavior that waits for user mouse events for
	 * the scene graph. 
	 * @param root   Root of your scene graph.
	 * @param canvas Java 3D drawing canvas.
	 * @param bounds Bounds of your scene.
	 **/

	public PickZoomBehavior(BranchGroup root, Canvas3D canvas, Bounds bounds) {
		super(canvas, root, bounds);
		zoom = new MouseZoom(MouseBehavior.MANUAL_WAKEUP);
		zoom.setTransformGroup(currGrp);
		currGrp.addChild(zoom);
		zoom.setSchedulingBounds(bounds);
		this.setSchedulingBounds(bounds);
	}

	/**
	 * Creates a pick/zoom behavior that waits for user mouse events for
	 * the scene graph. 
	 * @param root   Root of your scene graph.
	 * @param canvas Java 3D drawing canvas.
	 * @param bounds Bounds of your scene.
	 * @param pickMode specifys PickTool.BOUNDS, PickTool.GEOMETRY or
	 * PickTool.GEOMETRY_INTERSECT_INFO.  
	 * @see PickTool#setMode
	 */
	public PickZoomBehavior(
		BranchGroup root,
		Canvas3D canvas,
		Bounds bounds,
		int pickMode) {
		super(canvas, root, bounds);
		zoom = new MouseZoom(MouseBehavior.MANUAL_WAKEUP);
		zoom.setTransformGroup(currGrp);
		currGrp.addChild(zoom);
		zoom.setSchedulingBounds(bounds);
		this.setSchedulingBounds(bounds);
		this.setMode(pickMode);
	}

	/**
	 * Update the scene to manipulate any nodes. This is not meant to be 
	 * called by users. Behavior automatically calls this. You can call 
	 * this only if you know what you are doing.
	 * 
	 * @param xpos Current mouse X pos.
	 * @param ypos Current mouse Y pos.
	 **/

	public void updateScene(int xpos, int ypos) {
		TransformGroup tg = null;

		//    if (mevent.isAltDown() && !mevent.isMetaDown()){
		if (!mevent.isAltDown() && mevent.isMetaDown()) {

			pickCanvas.setShapeLocation(xpos, ypos);
			pr = pickCanvas.pickClosest();
			
			if (pr == null) {
				// Show the arena's stats panel, since no Piece is picked.
				MainFrame.bottomPropertiesCardLayout.show(MainFrame.bottomPropertiesPanels, "ArenaStatsPanel");					
			}			
			
			if ((pr != null)
				&& ((tg = (TransformGroup) pr.getNode(PickResult.TRANSFORM_GROUP))
					!= null)
				&& (tg.getCapability(TransformGroup.ALLOW_TRANSFORM_READ))
				&& (tg.getCapability(TransformGroup.ALLOW_TRANSFORM_WRITE))) {
				zoom.setTransformGroup(tg);
				zoom.wakeup();
				currentTG = tg;
				
				// Set this pick result as the currently picked Piece.
				Arena3DScene.currentlyPickedTG = currentTG;
				Piece p = (Piece) Arena3DScene.TGtoPiece.get(currentTG);
				MainFrame.ppPanel.inputPiece(p);

				// Save the collision count upon picking.				
				Arena3DScene.numCollisionsUponPick = Arena3DScene.collisionCount;

				// Save the location of the piece upon picking.
				Transform3D tempT3D = new Transform3D();
				tg.getTransform(tempT3D);
				Vector3d tempP = new Vector3d();
				tempT3D.get(Arena3DScene.locationUponPick);

				
				pr = null;

				// Show the properties of the currently picked Piece.
				MainFrame.bottomPropertiesCardLayout.show(MainFrame.bottomPropertiesPanels, "PiecePropertiesPanel");			
				
				
				//See design/000_Updates_2008.txt
				//freePickResult(pr);
			} else if (callback != null)
				callback.transformChanged(PickingCallback.NO_PICK, null);
		}
	}

	/**
	  * Callback method from MouseZoom
	  * This is used when the Picking callback is enabled
	  */
	public void transformChanged(int type, Transform3D transform) {
		callback.transformChanged(PickingCallback.ZOOM, currentTG);
	}

	/**
	  * Register the class @param callback to be called each
	  * time the picked object moves
	  */
	public void setupCallback(PickingCallback callback) {
		this.callback = callback;
		if (callback == null)
			zoom.setupCallback(null);
		else
			zoom.setupCallback(this);
	}
}
