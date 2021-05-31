/* @author David Tsai
 * @created on Apr 12, 2003
 */
package gui;

import javax.media.j3d.BranchGroup;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.View;
import javax.media.j3d.ViewPlatform;
import javax.vecmath.Vector3f;

import com.sun.j3d.utils.behaviors.keyboard.KeyNavigatorBehavior;
import com.sun.j3d.utils.universe.SimpleUniverse;

/**
 * This class handles the main camera for Arena3D. 
 */
public class Arena3DCamera extends BranchGroup {
	// FIELDS
	public static final String viewPerspective = "Perspective View";
	public static final String viewFront = "Front View";
	public static final String viewSide = "Side View";
	public static final String viewTop = "Top View";
	public static String currentView;
	
	private View universeView;
	private TransformGroup vpTG;
	
	public static KeyNavigatorBehavior keyNavBeh;

 	// CONSTRUCTOR
	/**
	 * @effects Creates an Arena3DCamera.
	 */
	public Arena3DCamera(SimpleUniverse universe) {
		super();

		// Set the activation radius of the univere's ViewPlatform to 1000.0.
		ViewPlatform universeViewPlatform = universe.getViewingPlatform().getViewPlatform();
		//universeViewPlatform.setActivationRadius(1000.0f);
		vpTG = universe.getViewingPlatform().getViewPlatformTransform();

		// Set the universe's view's background clipping distance to 100.0.
		// The default is 10.0, and if you don't change it your object will sink into
		// the background and "disappear."
		universeView = universe.getViewer().getView();
		universeView.setBackClipDistance(100.0);
			
		// Keyboard camera control.	
		keyNavBeh = new KeyNavigatorBehavior(vpTG);
		keyNavBeh.setSchedulingBounds(Arena3D.bounds);
		this.addChild(keyNavBeh);
		
		
		// Mouse camera control.
/*
		MouseRotate mr = new MouseRotate(vpTG);
		mr.setSchedulingBounds(Arena3D.bounds);
		this.addChild(mr);
		
		MouseTranslate mt = new MouseTranslate(vpTG);
		mt.setSchedulingBounds(Arena3D.bounds);
		this.addChild(mt);
		
		MouseZoom mz = new MouseZoom(vpTG);
		mz.setSchedulingBounds(Arena3D.bounds);
		this.addChild(mz);
*/		
		
		
		// Set the camera to "Perspective View"
		cameraSetPerspectiveView();		

		// Position the viewer so that they are looking at the object.
		//universe.getViewingPlatform().setNominalViewingTransform();
			
		// Have Java 3D perform optimizations on this branch.
		setPickable(false);		// picking behaviors will ignore this branch
		compile();				// compile this branch
	}
	
	
	// MUTATORS
	/**
	 * @effects Sets the camera to the perspective view.
	 */
	public void cameraSetPerspectiveView() {
		CanvasPanel.canvas3D.getView().setProjectionPolicy(View.PERSPECTIVE_PROJECTION);
		Transform3D T3D = new Transform3D();
		Vector3f translate = new Vector3f();
		T3D.rotY(Math.PI/4);
		translate.set(50.0f, 10.0f, 50.0f);
		T3D.setTranslation(translate);
		vpTG.setTransform(T3D);
		
		currentView = viewPerspective;
		/*
		CanvasPanel.canvas3D.getView().setProjectionPolicy(View.PERSPECTIVE_PROJECTION);
		Transform3D T3D = new Transform3D();
		Point3d eye = new Point3d(45.0, 15.0, 45.0);
		Point3d target = new Point3d(0.0, 0.0, 0.0);
		Vector3d upDirection = new Vector3d(0.0, 1.0, 0.0);
		T3D.lookAt(eye, target, upDirection);
		T3D.invert();
		vpTG.setTransform(T3D);
		*/
		}

	/**
	 * @effects Sets the camera to the front view, facing -z.
	 */	
	public void cameraSetFrontView(boolean lockedView) {
		CanvasPanel.canvas3D.getView().setProjectionPolicy(View.PERSPECTIVE_PROJECTION);
		Transform3D T3D = new Transform3D();
		Vector3f translate = new Vector3f();
		translate.set(10.0f, 10.0f, 60.0f);
		T3D.setTranslation(translate);
		vpTG.setTransform(T3D);

		if (lockedView == true) {
			CanvasPanel.canvas3D.getView().setProjectionPolicy(View.PARALLEL_PROJECTION);
			CanvasPanel.canvas3D.getView().setScreenScalePolicy(View.SCALE_EXPLICIT);
			CanvasPanel.canvas3D.getView().setScreenScale(0.011);
		}
		
		currentView = viewFront;		
	}
	
	/**
	 * @effects Sets the camera to the side view, facing -x.
	 */	
	public void cameraSetSideView(boolean lockedView) {
		CanvasPanel.canvas3D.getView().setProjectionPolicy(View.PERSPECTIVE_PROJECTION);		
		Transform3D T3D = new Transform3D();
		Vector3f translate = new Vector3f();
		T3D.rotY(Math.PI / 2.0);
		translate.set(60.0f, 10.0f, 10.0f);
		T3D.setTranslation(translate);
		vpTG.setTransform(T3D);

		if (lockedView == true) {
			CanvasPanel.canvas3D.getView().setProjectionPolicy(View.PARALLEL_PROJECTION);
			CanvasPanel.canvas3D.getView().setScreenScalePolicy(View.SCALE_EXPLICIT);
			CanvasPanel.canvas3D.getView().setScreenScale(0.011);
		}
		
		currentView = viewSide;		
	}

	/**
	 * @effects Sets the camera to the top view, facing -y.
	 */	
	public void cameraSetTopView(boolean lockedView) {
		CanvasPanel.canvas3D.getView().setProjectionPolicy(View.PERSPECTIVE_PROJECTION);
		Transform3D T3D = new Transform3D();
		Vector3f translate = new Vector3f();
		T3D.rotX(-Math.PI / 2.0);
		translate.set(10.0f, 60.0f, 10.0f);
		T3D.setTranslation(translate);
		vpTG.setTransform(T3D);
		
		if (lockedView == true) {
			CanvasPanel.canvas3D.getView().setProjectionPolicy(View.PARALLEL_PROJECTION);
			CanvasPanel.canvas3D.getView().setScreenScalePolicy(View.SCALE_EXPLICIT);
			CanvasPanel.canvas3D.getView().setScreenScale(0.011);
		}
		
		currentView = viewTop;		
	}	
	

}
