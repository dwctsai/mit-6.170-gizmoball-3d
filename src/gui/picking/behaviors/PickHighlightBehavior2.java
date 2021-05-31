/* @author David Tsai
 *
 *
 * This class is a modification of:
 * 
 *      @(#)PickHighlight.java 1.3 99/09/01 16:01:15
 *
 * Copyright (c) 1996-1999 Sun Microsystems, Inc. All Rights Reserved.
 *
 * Sun grants you ("Licensee") a non-exclusive, royalty free, license to use,
 * modify and redistribute this software in source and binary code form,
 * provided that i) this copyright notice and license appear on all copies of
 * the software; and ii) Licensee does not utilize the software in a manner
 * which is disparaging to Sun.
 *
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING ANY
 * IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR
 * NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN AND ITS LICENSORS SHALL NOT BE
 * LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING
 * OR DISTRIBUTING THE SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL SUN OR ITS
 * LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT,
 * INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER
 * CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF
 * OR INABILITY TO USE SOFTWARE, EVEN IF SUN HAS BEEN ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES.
 *
 * This software is not designed or intended for use in on-line control of
 * aircraft, air traffic, aircraft navigation or aircraft communications; or in
 * the design, construction, operation or maintenance of any nuclear
 * facility. Licensee represents and warrants that it will not use or
 * redistribute the Software for such purposes.
 */



package gui.picking.behaviors;

import java.awt.AWTEvent;
import java.awt.Event;
import java.awt.event.MouseEvent;
import java.util.Enumeration;

import javax.media.j3d.Appearance;
import javax.media.j3d.Bounds;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.Material;
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.Shape3D;
import javax.media.j3d.WakeupCriterion;
import javax.media.j3d.WakeupOnAWTEvent;
import javax.media.j3d.WakeupOr;
import javax.vecmath.Color3f;

import com.sun.j3d.utils.picking.PickResult;
import com.sun.j3d.utils.picking.PickTool;
import com.sun.j3d.utils.picking.behaviors.PickMouseBehavior;

/**
 * PickHighlightBehavior2 provides a picking behavior such that when a mouse
 * hovers over a picked object, it will be highlighted.
 * This is different from PickHilightBehavior, where an object is higlighted
 * only if you click on it.
 * 
 * This class is modified specifically for Gizmoball.
 */
public class PickHighlightBehavior2 extends PickMouseBehavior {
  Appearance savedAppearance = null;
  Shape3D oldShape = null;
  Appearance highlightAppearance;

  public PickHighlightBehavior2(BranchGroup root, Canvas3D canvas, Bounds bounds, boolean filled) {
	  super(canvas, root, bounds);
	  this.setSchedulingBounds(bounds);
	  Color3f white = new Color3f(1.0f, 1.0f, 1.0f);
	  Color3f black = new Color3f(0.0f, 0.0f, 0.0f);
	  Color3f highlightColor = new Color3f(1.0f, 1.0f, 1.0f);



  	  highlightAppearance = new Appearance();
	  Material highlightMaterial = new Material(highlightColor, black,
						highlightColor, white,
						80.0f);
	  highlightAppearance.setMaterial(highlightMaterial);
	  
	  if (filled == false) {
	  	PolygonAttributes pa = new PolygonAttributes();
		pa.setPolygonMode(PolygonAttributes.POLYGON_LINE);
		highlightAppearance.setPolygonAttributes(pa);	
	  }
	  
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

		if (oldShape != null){
			oldShape.setAppearance(savedAppearance);
		}
		if (shape != null) {
			savedAppearance = shape.getAppearance();
			oldShape = shape;
			shape.setAppearance(highlightAppearance);
		}
	}
	

	public void initialize() {

	  conditions = new WakeupCriterion[1];
	  conditions[0] = new WakeupOnAWTEvent(Event.MOUSE_MOVE);
	  wakeupCondition = new WakeupOr(conditions);

	  wakeupOn(wakeupCondition);
	}
	
	private void processMouseEvent(MouseEvent evt) {
	  buttonPress = false;

	 if (evt.getID() == MouseEvent.MOUSE_MOVED) {
		// Process mouse move event
		buttonPress = true;
	  }
	}	
	
	public void processStimulus (Enumeration criteria) {
	  WakeupCriterion wakeup;
	  AWTEvent[] evt = null;
	  int xpos = 0, ypos = 0;

	  while(criteria.hasMoreElements()) {
		wakeup = (WakeupCriterion)criteria.nextElement();
		if (wakeup instanceof WakeupOnAWTEvent)
	  evt = ((WakeupOnAWTEvent)wakeup).getAWTEvent();
	  }
    
	  if (evt[0] instanceof MouseEvent){
		mevent = (MouseEvent) evt[0];

		if (debug)
	  System.out.println("got mouse event");
		processMouseEvent((MouseEvent)evt[0]);
		xpos = mevent.getPoint().x;
		ypos = mevent.getPoint().y;
	  }
    
	  if (debug)
		System.out.println("mouse position " + xpos + " " + ypos);
    
	  if (buttonPress){
		updateScene(xpos, ypos);
	  }
	  wakeupOn (wakeupCondition);
	}
}
