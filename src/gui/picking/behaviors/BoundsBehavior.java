/* @author David Tsai
 *
 *
 * This class is a modification of:
 * 
 *	@BoundsBehavior.java
 *
 * Copyright (C) 2001 	Daniel Selman
 *
 * First distributed with the book "Java 3D Programming"
 * by Daniel Selman and published by Manning Publications.
 * http://manning.com/selman
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation, version 2.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * The license can be found on the WWW at:
 * http://www.fsf.org/copyleft/gpl.html
 *
 * Or by writing to:
 * Free Software Foundation, Inc.,
 * 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 * Authors can be contacted at:
 * Daniel Selman: daniel@selman.org
 *
 * If you make changes you think others would like, please 
 * contact one of the authors or someone at the 
 * www.j3d.org web site.
 */

package gui.picking.behaviors;

import gui.Arena3D;
import gui.geometries.ColorlessCube;

import javax.media.j3d.Appearance;
import javax.media.j3d.Behavior;
import javax.media.j3d.BoundingBox;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.Group;
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Switch;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.WakeupCondition;
import javax.media.j3d.WakeupCriterion;
import javax.media.j3d.WakeupOnElapsedFrames;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import com.sun.j3d.utils.geometry.Sphere;

/**
 * This class implements a simple behavior that displays a 
 * graphical representation of the bounds of a node.
 * 
 * This class is modified specifically for Gizmoball.
 */
public class BoundsBehavior extends Behavior {
	// FIELDS
	// the wake up condition for the behavior
	protected WakeupCondition m_WakeupCondition = null;

	// the Node that we are tracking
	protected BoundingBox bounds = null;
	protected TransformGroup m_TransformGroup = null;
	protected Switch m_BoundsSwitch = null;

	protected Transform3D m_Transform3D = null;
	protected Vector3d m_Scale = null;
	protected Vector3d m_Vector3d = null;
	protected Point3d m_Point3d1 = null;
	protected Point3d m_Point3d2 = null;

	private final int m_kBoxBounds = 1;
	
	public CollisionDetector cd = null;

	// CONSTRUCTOR
	/**
	 * @effects constructs a BoundsBehavior from the given parameters.
	 */
	public BoundsBehavior(BoundingBox bb) {
		bounds = bb;
		
		m_Transform3D = new Transform3D();
		m_Scale = new Vector3d();
		m_Vector3d = new Vector3d();
		m_Point3d1 = new Point3d();
		m_Point3d2 = new Point3d();

		// save the WakeupCriterion for the behavior
		m_WakeupCondition = new WakeupOnElapsedFrames(35);
	}

	// METHODS
	/**
	 * @effects adds this to a parent group.  
	 */
	public void addBehaviorToParentGroup(Group nodeParentGroup) {
		nodeParentGroup.addChild(this);

		m_TransformGroup = new TransformGroup();
		m_TransformGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

		m_BoundsSwitch = new Switch();
		m_BoundsSwitch.setCapability(Switch.ALLOW_SWITCH_WRITE);

		Appearance app = new Appearance();

		PolygonAttributes polyAttrbutes = new PolygonAttributes();
		polyAttrbutes.setPolygonMode(PolygonAttributes.POLYGON_LINE);
		polyAttrbutes.setCullFace(PolygonAttributes.CULL_NONE);
		app.setPolygonAttributes(polyAttrbutes);

		m_BoundsSwitch.addChild(new Sphere(1, app));

		// Setup the graphical representation of the bounding box.
		ColorlessCube cube = new ColorlessCube();
		ColoringAttributes ca = new ColoringAttributes();
		ca.setColor(Arena3D.cyan);
		app.setCapability(Appearance.ALLOW_COLORING_ATTRIBUTES_WRITE);
		app.setColoringAttributes(ca);
		app.setCapability(Appearance.ALLOW_POLYGON_ATTRIBUTES_WRITE);
		app.setCapability(Appearance.ALLOW_TRANSPARENCY_ATTRIBUTES_WRITE);
		cube.setAppearance(app);

		// Enable appearance of the cube to change.
		cube.setCapability(Shape3D.ALLOW_APPEARANCE_READ);
		cube.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);

		// Create a new Behavior object that will perform the collision
		// detection on the specified object.  It is off by default.
		cd = new CollisionDetector(cube);
		cd.setSchedulingBounds(Arena3D.boundsDisable);
		cd.setEnable(false);

		Group g = new Group();
		g.addChild(cube);
		g.addChild(cd);

		m_BoundsSwitch.addChild(g);

		m_BoundsSwitch.setWhichChild(Switch.CHILD_NONE);

		m_TransformGroup.addChild(m_BoundsSwitch);
		nodeParentGroup.addChild(m_TransformGroup);
	}

	/**
	 * @effects enables this behavior.  
	 */
	
	public void setEnable(boolean bEnable) {
		super.setEnable(bEnable);

		if (m_BoundsSwitch != null) {
			if (bEnable == false)
				m_BoundsSwitch.setWhichChild(Switch.CHILD_NONE);
		}
	}
	

	/**
	 * @effects initializes this behavior  
	 */
	public void initialize() {
		// apply the initial WakeupCriterion
		wakeupOn(m_WakeupCondition);
	}

	/**
	 * @effects initializes this behavior  
	 */
	public void processStimulus(java.util.Enumeration criteria) {
		while (criteria.hasMoreElements()) {
			WakeupCriterion wakeUp = (WakeupCriterion) criteria.nextElement();

			// every N frames, update position of the graphic
			if (wakeUp instanceof WakeupOnElapsedFrames) {
				if (m_TransformGroup != null) {

					int nBoundsType = m_kBoxBounds;
					m_Transform3D.setIdentity();

					nBoundsType = m_kBoxBounds;

					bounds.getLower(m_Point3d1);
					bounds.getUpper(m_Point3d2);

					m_Vector3d.x = (m_Point3d1.x + m_Point3d2.x) / 2;
					m_Vector3d.y = (m_Point3d1.y + m_Point3d2.y) / 2;
					m_Vector3d.z = (m_Point3d1.z + m_Point3d2.z) / 2;

					m_Scale.x = Math.abs(m_Point3d1.x - m_Point3d2.x) / 2;
					m_Scale.y = Math.abs(m_Point3d1.y - m_Point3d2.y) / 2;
					m_Scale.z = Math.abs(m_Point3d1.z - m_Point3d2.z) / 2;

					m_Transform3D.setScale(m_Scale);
					m_Transform3D.setTranslation(m_Vector3d);

					m_TransformGroup.setTransform(m_Transform3D);

					m_BoundsSwitch.setWhichChild(nBoundsType);
				} else {
					System.err.println(
						"Call addBehaviorToParentGroup for BoundsBehavior.");
				}
			}
		}

		// assign the next WakeUpCondition, so we are notified again
		wakeupOn(m_WakeupCondition);
	}
}
