/* @author David Tsai
 * @created on Apr 12, 2003
 */
package gui;

import javax.media.j3d.AmbientLight;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Color3f;
import javax.vecmath.Vector3f;

/**
 * This class handles all main lights for Arena3D.
 */
public class Arena3DLights extends BranchGroup {
	// CONSTRUCTOR
	/**
	 * @effects Creates an Arena3DLights.
	 */
	public Arena3DLights() {
		super();
		addChild(createAmbientLight());
		addChild(createDirectionalLight());
		
		// Have Java 3D perform optimizations on this branch.
		setPickable(false);		// picking behaviors will ignore this branch
		compile();				// compile this branch
	}
	
	
	// PRODUCERS
	/**
	 * @return a ambient light.
	 */
	private AmbientLight createAmbientLight() {
		Color3f ambientLightColor = new Color3f(0.2f, 0.2f, 0.2f);
		AmbientLight ambientLightNode = new AmbientLight(ambientLightColor);
		ambientLightNode.setEnable(true);
		ambientLightNode.setInfluencingBounds(Arena3D.bounds);
		return ambientLightNode;
	}
	

	/**
	 * @return a TransformGroup with a directional light in it. 
	 */	
	private TransformGroup createDirectionalLight() {
		// Create a directional light.
		Color3f dirLightColor = new Color3f(0.7f, 0.7f, 0.7f);
		Vector3f dirLightVector  = new Vector3f(-1.0f, -1.0f, -1.0f);
		DirectionalLight dirLight = new DirectionalLight(dirLightColor, dirLightVector);
		dirLight.setInfluencingBounds(Arena3D.bounds);

		// Add the directional light to a TransformGroup in case I 
		// want to be able to move it around later.
		TransformGroup dirLightTG = new TransformGroup();
		dirLightTG.addChild(dirLight);
		
		return dirLightTG;
	}
	

}
