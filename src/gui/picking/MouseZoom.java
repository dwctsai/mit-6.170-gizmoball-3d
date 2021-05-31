/* @author David Tsai
 *
 *
 * This class is a modification of:
 * 
 *	@(#)MouseZoom.java 1.33 02/04/01 15:01:07
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

package gui.picking;

import gui.Arena3DScene;
import gui.MainFrame;
import gui.PiecePropertiesPanel;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.event.MouseEvent;
import java.util.Enumeration;

import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.WakeupCriterion;
import javax.media.j3d.WakeupOnAWTEvent;
import javax.media.j3d.WakeupOnBehaviorPost;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import com.sun.j3d.utils.behaviors.mouse.MouseBehavior;
import com.sun.j3d.utils.behaviors.mouse.MouseBehaviorCallback;

/**
 * MouseZoom is a Java3D behavior object that lets users control the 
 * Z axis translation of an object via a mouse drag motion with the second
 * mouse button. See MouseRotate for similar usage info.
 * 
 * This class is modified specifically for Gizmoball.
 */

public class MouseZoom extends MouseBehavior {

	double z_factor = .04;
	Vector3d translation = new Vector3d();

	private MouseBehaviorCallback callback = null;

	/**
	 * Creates a zoom behavior given the transform group.
	 * @param transformGroup The transformGroup to operate on.
	 */
	public MouseZoom(TransformGroup transformGroup) {
		super(transformGroup);
	}

	/**
	 * Creates a default mouse zoom behavior.
	 **/
	public MouseZoom() {
		super(0);
	}

	/**
	 * Creates a zoom behavior.
	 * Note that this behavior still needs a transform
	 * group to work on (use setTransformGroup(tg)) and
	 * the transform group must add this behavior.
	 * @param flags
	 */
	public MouseZoom(int flags) {
		super(flags);
	}

	/**
	 * Creates a zoom behavior that uses AWT listeners and behavior
	 * posts rather than WakeupOnAWTEvent.  The behavior is added to the
	 * specified Component.  A null component can be passed to specify
	 * the behavior should use listeners.  Components can then be added
	 * to the behavior with the addListener(Component c) method.
	 * @param c The Component to add the MouseListener
	 * and MouseMotionListener to.
	 * @since Java 3D 1.2.1
	 */
	public MouseZoom(Component c) {
		super(c, 0);
	}

	/**
	 * Creates a zoom behavior that uses AWT listeners and behavior
	 * posts rather than WakeupOnAWTEvent.  The behaviors is added to
	 * the specified Component and works on the given TransformGroup.
	 * @param c The Component to add the MouseListener and
	 * MouseMotionListener to.  A null component can be passed to specify
	 * the behavior should use listeners.  Components can then be added
	 * to the behavior with the addListener(Component c) method.
	 * @param transformGroup The TransformGroup to operate on.
	 * @since Java 3D 1.2.1
	 */
	public MouseZoom(Component c, TransformGroup transformGroup) {
		super(c, transformGroup);
	}

	/**
	 * Creates a zoom behavior that uses AWT listeners and behavior
	 * posts rather than WakeupOnAWTEvent.  The behavior is added to the
	 * specified Component.  A null component can be passed to specify
	 * the behavior should use listeners.  Components can then be added
	 * to the behavior with the addListener(Component c) method.
	 * Note that this behavior still needs a transform
	 * group to work on (use setTransformGroup(tg)) and the transform
	 * group must add this behavior.
	 * @param flags interesting flags (wakeup conditions).
	 * @since Java 3D 1.2.1
	 */
	public MouseZoom(Component c, int flags) {
		super(c, flags);
	}

	public void initialize() {
		super.initialize();
		if ((flags & INVERT_INPUT) == INVERT_INPUT) {
			z_factor *= -1;
			invert = true;
		}
	}

	/**
	 * Return the y-axis movement multipler.
	 **/
	public double getFactor() {
		return z_factor;
	}

	/**
	 * Set the y-axis movement multipler with factor.
	 **/
	public void setFactor(double factor) {
		z_factor = factor;
	}

	public void processStimulus(Enumeration criteria) {
		WakeupCriterion wakeup;
		AWTEvent[] events;
		MouseEvent evt;
		// 	int id;
		// 	int dx, dy;

		while (criteria.hasMoreElements()) {
			wakeup = (WakeupCriterion) criteria.nextElement();
			if (wakeup instanceof WakeupOnAWTEvent) {
				events = ((WakeupOnAWTEvent) wakeup).getAWTEvent();
				if (events.length > 0) {
					evt = (MouseEvent) events[events.length - 1];
					doProcess(evt);
				}
			} else if (wakeup instanceof WakeupOnBehaviorPost) {
				while (true) {
					synchronized (mouseq) {
						if (mouseq.isEmpty())
							break;
						evt = (MouseEvent) mouseq.remove(0);
						// consolodate MOUSE_DRAG events
						while ((evt.getID() == MouseEvent.MOUSE_DRAGGED)
							&& !mouseq.isEmpty()
							&& (((MouseEvent) mouseq.get(0)).getID()
								== MouseEvent.MOUSE_DRAGGED)) {
							evt = (MouseEvent) mouseq.remove(0);
						}
					}
					doProcess(evt);
				}
			}

		}
		wakeupOn(mouseCriterion);
	}

	void doProcess(MouseEvent evt) {
		int id;
		int dx, dy;

		processMouseEvent(evt);
		id = evt.getID();


		//	if (((buttonPress)&&((flags & MANUAL_WAKEUP) == 0)) ||
		if (((buttonPress) && ((flags & MANUAL_WAKEUP) == 0))
			|| (id == MouseEvent.MOUSE_RELEASED)
			|| ((wakeUp) && ((flags & MANUAL_WAKEUP) != 0)) && (PiecePropertiesPanel.p != null)) {
				
			if ((id == MouseEvent.MOUSE_DRAGGED)
				&& //		evt.isAltDown() && !evt.isMetaDown()){
			!evt.isAltDown()
				&& evt.isMetaDown()) {

				x = evt.getX();
				y = evt.getY();

				dx = x - x_last;
				dy = y - y_last;

				if (!reset) {
					transformGroup.getTransform(currXform);

					translation.z = dy * z_factor;

					transformX.set(translation);

					if (invert) {
						currXform.mul(currXform, transformX);
					} else {
						currXform.mul(transformX, currXform);
					}
// DW's code
					Vector3d tempVec = new Vector3d();
					currXform.get(tempVec);

					// Check for Snap-To-Grid.
					if (MainFrame.snapToGridToggleOn == false) {
						currXform.setTranslation(tempVec);

					} else if (MainFrame.snapToGridToggleOn == true) {
						Arena3DScene.zMouseIntegration += translation.z;

						Point3d lower = PiecePropertiesPanel.p.getContainerLowerLocation();
						Point3d upper = PiecePropertiesPanel.p.getContainerUpperLocation();
						double midZ = (lower.z + upper.z) / 2.0;

						if (Arena3DScene.zMouseIntegration >= 0.5) {
							tempVec.z = midZ + 1.0;
							Arena3DScene.zMouseIntegration = 0.0;
						} else if (Arena3DScene.zMouseIntegration <= -0.5) {
							tempVec.z = midZ - 1.0;
							Arena3DScene.zMouseIntegration = 0.0;
						} else {
							tempVec.z = midZ;
						}

						currXform.setTranslation(tempVec);
					}
// end of DW
					transformGroup.setTransform(currXform);

					transformChanged(currXform);

					if (callback != null)
						callback.transformChanged(
							MouseBehaviorCallback.ZOOM,
							currXform);

				} else {
					reset = false;
				}

				x_last = x;
				y_last = y;
			} else if (id == MouseEvent.MOUSE_PRESSED) {
				x_last = evt.getX();
				y_last = evt.getY();
			}
			
			if (id == MouseEvent.MOUSE_RELEASED && (PiecePropertiesPanel.p != null)) {
				try {
					Point3d lower = PiecePropertiesPanel.p.getContainerLowerLocation();
					Point3d upper = PiecePropertiesPanel.p.getContainerUpperLocation();
				
					// Check to see that the Piece is not placed outside of the arena.
					if (lower.x < 0.0 || lower.y < 0.0 || lower.z < 0.0 ||
						upper.x > 20.0 || upper.y > 20.0 || upper.z > 20.0) {

							currXform.setTranslation(Arena3DScene.locationUponPick);
							transformGroup.setTransform(currXform);
						}
				
					// Check for collisions with other Pieces.
					if (Arena3DScene.collisionCount > Arena3DScene.numCollisionsUponPick) {

						currXform.setTranslation(Arena3DScene.locationUponPick);
						transformGroup.setTransform(currXform);
					}

					// Update the properties of the currently picked Piece.
//					MainFrame.bottomPropertiesCardLayout.show(MainFrame.bottomPropertiesPanels, "PiecePropertiesPanel");					
				} catch (Exception e) {
					// DO NOTHING
				}				
			}

			
		}
	}

	/**
	  * Users can overload this method  which is called every time
	  * the Behavior updates the transform
	  *
	  * Default implementation does nothing
	  */
	public void transformChanged(Transform3D transform) {
	}

	/**
	  * The transformChanged method in the callback class will
	  * be called every time the transform is updated
	  */
	public void setupCallback(MouseBehaviorCallback callback) {
		this.callback = callback;
	}
}
