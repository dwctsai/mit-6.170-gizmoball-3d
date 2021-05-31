/* @author David Tsai
 * @created on Apr 10, 2003
 */

package gui;

import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.Locale;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;

import com.sun.j3d.utils.universe.SimpleUniverse;

/**
 * Arena3D sets up the 3D universe. 
 */
public class Arena3D {
	// FIELDS
	SimpleUniverse universe;
	public static Locale locale;
	BranchGroup sceneBranch;	// this branch is the main scene branch
	BranchGroup lightsBranch;	// this branch stores the main scene lights
	BranchGroup cameraBranch;	// this branch stores all camera and view things
	BranchGroup gizmosBranch;	// this branch stores all the gizmos

	public static final BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 1000.0);
	public static final BoundingSphere boundsDisable = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 0.0);
	public static final Color3f black = new Color3f(0.0f, 0.0f, 0.0f);
	public static final Color3f white = new Color3f(1.0f, 1.0f, 1.0f);
	public static final Color3f red   = new Color3f(1.0f, 0.0f, 0.0f);
	public static final Color3f green = new Color3f(0.0f, 1.0f, 0.0f);
	public static final Color3f blue  = new Color3f(0.0f, 0.0f, 1.0f);
	public static final Color3f yellow = new Color3f(1.0f, 1.0f, 0.0f);
	public static final Color3f lightgreen = new Color3f(0.0f, 0.8f, 0.0f);
	public static final Color3f turquoise = new Color3f(0.0f, 0.8f, 0.8f);
	public static final Color3f navyblue = new Color3f(0.05f, 0.05f, 0.2f);
	public static final Color3f cyan = new Color3f(0.0f, 1.0f, 1.0f);
	public static final Color3f magenta = new Color3f(1.0f, 0.0f, 1.0f);
	public static final Color3f orange = new Color3f(1.0f, 0.6f, 0.2f);
	public static final Color3f gray = new Color3f(0.6f, 0.6f, 0.6f);
	
	// CONSTRUCTOR
	/**
	 * @effects Sets up the 3D universe. 
	 *
	 */
	public Arena3D(Canvas3D canvas3D) {
		// Create a virtual universe to contain the scene.
		universe = new SimpleUniverse(canvas3D);
		
		// Set the locale.
		locale = new Locale(universe);

		// Setup the lights.
		lightsBranch = new Arena3DLights();
		locale.addBranchGraph(lightsBranch);

		// Setup the camera.
		cameraBranch = new Arena3DCamera(universe);
		locale.addBranchGraph(cameraBranch);
		
		// Setup the scene;
		sceneBranch = new Arena3DScene();
		locale.addBranchGraph(sceneBranch);
		((Arena3DScene) sceneBranch).createArena3DScene();
	}


} // end of Arena3D
