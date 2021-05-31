/* @author David Tsai
 * @created on Apr 24, 2003
 */
package gui;

import java.net.URL;

import javax.media.j3d.Appearance;
import javax.media.j3d.Material;
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.TextureAttributes;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransparencyAttributes;

import plugins.pieces.Flipper3D;
import plugins.pieces.Jezzmo3D;
import plugins.pieces.OuterWall3D;
import backend.Piece;
import backend.event.EventHandler;

import com.sun.j3d.utils.image.TextureLoader;

/**
 * PieceProperties - helps manage the properties of the gizmoball game pieces.   
 */
public class PieceProperties {
	
	// General Fields
	private static final EventHandler eventHandler = new EventHandler();
	
	// Ball3D Fields
	public static Appearance appBall3DDefault;
	public static Appearance appBall3DSpecular;
	public static Appearance appBall3DTextureEarth;
	public static final double defaultBall3DRadius = 0.25;
	public static final Transform3D defaultBall3DVelocity = new Transform3D();
	public static final double defaultBall3DMass = 1.0;
	
	// CubicalBumper3D Fields
	public static Appearance appCubicalBumper3DDefault;
	public static Appearance appCubicalBumper3DOuterWall;
	public static final double defaultCubicalBumper3DXLength = 1.0;
	public static final double defaultCubicalBumper3DYLength = 1.0;
	public static final double defaultCubicalBumper3DZLength = 1.0;
	public static final double defaultCubicalBumper3DCOR = 1.0;
	
	// SphericalBumper3D Fields
	public static Appearance appSphericalBumper3DDefault;
	public static final double defaultSphericalBumper3DRadius = 0.5;
	public static final double defaultSphericalBumper3DCOR = 1.0;
	
	// TriangularBumperA3D Fields
	public static Appearance appTriangularBumperA3DDefault;
	public static final double defaultTriangularBumperA3DXLength = 1.0;
	public static final double defaultTriangularBumperA3DYLength = 1.0;
	public static final double defaultTriangularBumperA3DZLength = 1.0;
	public static final double defaultTriangularBumperA3DCOR = 1.0;	

	// TriangularBumperB3D Fields
	public static Appearance appTriangularBumperB3DDefault;
	public static final double defaultTriangularBumperB3DXLength = 1.0;
	public static final double defaultTriangularBumperB3DYLength = 1.0;
	public static final double defaultTriangularBumperB3DZLength = 1.0;
	public static final double defaultTriangularBumperB3DCOR = 1.0;	
	
	// TriangularBumperC3D Fields
	public static Appearance appTriangularBumperC3DDefault;
	public static final double defaultTriangularBumperC3DXLength = 1.0;
	public static final double defaultTriangularBumperC3DYLength = 1.0;
	public static final double defaultTriangularBumperC3DZLength = 1.0;
	public static final double defaultTriangularBumperC3DCOR = 1.0;	

	// Flipper Fields
	public static Appearance appFlipper3DDefault;
	public static final double defaultFlipper3DCOR = 0.95;
	public static final String defaultFlipper3DOrientation = Flipper3D.LEFT_ORIENTATION;
	
	// Absorber Fields
	public static Appearance appAbsorber3DDefault;
	public static final double defaultAbsorber3DXLength = 20.0;
	public static final double defaultAbsorber3DYLength = 1.0;
	public static final double defaultAbsorber3DZLength = 1.0;
	public static final double defaultAbsorber3DCOR = 1.0;
	
	// Jezzmo Fields
	public static Appearance appJezzmo3DDefault;
	public static final double defaultJezzmo3DXLength = 1.0;
	public static final double defaultJezzmo3DYLength = 1.0;
	public static final double defaultJezzmo3DZLength = 1.0;
	public static final double defaultJezzmo3DCOR = 1.0;
	public static final String defaultJezzmo3DOrientation = Jezzmo3D.X_GROWTH;

	// CONSTRUCTOR
	
	/**
	 * @effects Creates a PieceProperties.
	 */	
	public PieceProperties() {
		setupAppearances();
	}
	
	
	
	
	// METHODS

	/**
	 * @effects Sets up the pre-customized appearances of the gizmoball game pieces.
	 */		
	public void setupAppearances() {
		// Ball3D: Default
		appBall3DDefault = new Appearance();
		appBall3DDefault.setCapability(Appearance.ALLOW_MATERIAL_READ);
		appBall3DDefault.setCapability(Appearance.ALLOW_MATERIAL_WRITE);
		appBall3DDefault.setCapability(Appearance.ALLOW_TRANSPARENCY_ATTRIBUTES_READ);
		appBall3DDefault.setCapability(Appearance.ALLOW_TRANSPARENCY_ATTRIBUTES_WRITE);		
		appBall3DDefault.setCapability(Appearance.ALLOW_POLYGON_ATTRIBUTES_READ);
		appBall3DDefault.setCapability(Appearance.ALLOW_POLYGON_ATTRIBUTES_WRITE);

		Material m = new Material(Arena3D.lightgreen, Arena3D.black, Arena3D.lightgreen, Arena3D.white, 80.0f);
		m.setCapability(Material.ALLOW_COMPONENT_READ);
		m.setCapability(Material.ALLOW_COMPONENT_WRITE);		
		m.setLightingEnable(true);
		appBall3DDefault.setMaterial(m);

		
		// Ball3D: Specular
		appBall3DSpecular = new Appearance();
		appBall3DSpecular.setCapability(Appearance.ALLOW_MATERIAL_READ);
		appBall3DSpecular.setCapability(Appearance.ALLOW_MATERIAL_WRITE);
		appBall3DSpecular.setCapability(Appearance.ALLOW_TRANSPARENCY_ATTRIBUTES_READ);
		appBall3DSpecular.setCapability(Appearance.ALLOW_TRANSPARENCY_ATTRIBUTES_WRITE);		
		appBall3DSpecular.setCapability(Appearance.ALLOW_POLYGON_ATTRIBUTES_READ);
		appBall3DSpecular.setCapability(Appearance.ALLOW_POLYGON_ATTRIBUTES_WRITE);

		m = new Material(Arena3D.black, Arena3D.black, Arena3D.lightgreen, Arena3D.white, 80.0f);
		m.setCapability(Material.ALLOW_COMPONENT_READ);
		m.setCapability(Material.ALLOW_COMPONENT_WRITE);		
		m.setLightingEnable(true);
		appBall3DSpecular.setMaterial(m);

		
		// Ball3D: Earth Textured
		appBall3DTextureEarth = new Appearance();
		appBall3DTextureEarth.setCapability(Appearance.ALLOW_MATERIAL_READ);
		appBall3DTextureEarth.setCapability(Appearance.ALLOW_MATERIAL_WRITE);
		appBall3DTextureEarth.setCapability(Appearance.ALLOW_TRANSPARENCY_ATTRIBUTES_READ);
		appBall3DTextureEarth.setCapability(Appearance.ALLOW_TRANSPARENCY_ATTRIBUTES_WRITE);		
		appBall3DTextureEarth.setCapability(Appearance.ALLOW_POLYGON_ATTRIBUTES_READ);
		appBall3DTextureEarth.setCapability(Appearance.ALLOW_POLYGON_ATTRIBUTES_WRITE);

		m = new Material(Arena3D.white, Arena3D.black, Arena3D.white, Arena3D.black, 1.0f);
		m.setCapability(Material.ALLOW_COMPONENT_READ);
		m.setCapability(Material.ALLOW_COMPONENT_WRITE);		
		m.setLightingEnable(true);
		appBall3DTextureEarth.setMaterial(m);

		URL earthURL = ClassLoader.getSystemResource("gui/textures/earth.jpg");
		TextureLoader tex = new TextureLoader(earthURL, null);
		appBall3DTextureEarth.setTexture(tex.getTexture());
		TextureAttributes texAttr = new TextureAttributes();
		texAttr.setTextureMode(TextureAttributes.MODULATE);
		appBall3DTextureEarth.setTextureAttributes(texAttr);


		// CubicalBumper3D: Default
		appCubicalBumper3DDefault = new Appearance();
		appCubicalBumper3DDefault.setCapability(Appearance.ALLOW_MATERIAL_READ);
		appCubicalBumper3DDefault.setCapability(Appearance.ALLOW_MATERIAL_WRITE);
		appCubicalBumper3DDefault.setCapability(Appearance.ALLOW_TRANSPARENCY_ATTRIBUTES_READ);
		appCubicalBumper3DDefault.setCapability(Appearance.ALLOW_TRANSPARENCY_ATTRIBUTES_WRITE);		
		appCubicalBumper3DDefault.setCapability(Appearance.ALLOW_POLYGON_ATTRIBUTES_READ);
		appCubicalBumper3DDefault.setCapability(Appearance.ALLOW_POLYGON_ATTRIBUTES_WRITE);
		appCubicalBumper3DDefault.setCapability(Appearance.ALLOW_RENDERING_ATTRIBUTES_READ);
		appCubicalBumper3DDefault.setCapability(Appearance.ALLOW_RENDERING_ATTRIBUTES_WRITE);		

		m =	new Material(Arena3D.red, Arena3D.black, Arena3D.red, Arena3D.white, 80.0f);
		m.setCapability(Material.ALLOW_COMPONENT_READ);
		m.setCapability(Material.ALLOW_COMPONENT_WRITE);
		m.setLightingEnable(true);
		appCubicalBumper3DDefault.setMaterial(m);

		
		// CubicalBumper3D: Outer Wall
		appCubicalBumper3DOuterWall = new Appearance();
		appCubicalBumper3DOuterWall.setCapability(Appearance.ALLOW_MATERIAL_READ);
		appCubicalBumper3DOuterWall.setCapability(Appearance.ALLOW_MATERIAL_WRITE);
		appCubicalBumper3DOuterWall.setCapability(Appearance.ALLOW_TRANSPARENCY_ATTRIBUTES_READ);
		appCubicalBumper3DOuterWall.setCapability(Appearance.ALLOW_TRANSPARENCY_ATTRIBUTES_WRITE);		
		appCubicalBumper3DOuterWall.setCapability(Appearance.ALLOW_POLYGON_ATTRIBUTES_READ);
		appCubicalBumper3DOuterWall.setCapability(Appearance.ALLOW_POLYGON_ATTRIBUTES_WRITE);
		appCubicalBumper3DOuterWall.setCapability(Appearance.ALLOW_RENDERING_ATTRIBUTES_READ);
		appCubicalBumper3DOuterWall.setCapability(Appearance.ALLOW_RENDERING_ATTRIBUTES_WRITE);		

		m =	new Material(Arena3D.red, Arena3D.black, Arena3D.red, Arena3D.white, 80.0f);
		m.setCapability(Material.ALLOW_COMPONENT_READ);
		m.setCapability(Material.ALLOW_COMPONENT_WRITE);
		m.setLightingEnable(true);
		appCubicalBumper3DOuterWall.setMaterial(m);

	
		// SphericalBumper3D: Default
		appSphericalBumper3DDefault = new Appearance();
		appSphericalBumper3DDefault.setCapability(Appearance.ALLOW_MATERIAL_READ);
		appSphericalBumper3DDefault.setCapability(Appearance.ALLOW_MATERIAL_WRITE);
		appSphericalBumper3DDefault.setCapability(Appearance.ALLOW_TRANSPARENCY_ATTRIBUTES_READ);
		appSphericalBumper3DDefault.setCapability(Appearance.ALLOW_TRANSPARENCY_ATTRIBUTES_WRITE);		
		appSphericalBumper3DDefault.setCapability(Appearance.ALLOW_POLYGON_ATTRIBUTES_READ);
		appSphericalBumper3DDefault.setCapability(Appearance.ALLOW_POLYGON_ATTRIBUTES_WRITE);

		m = new Material(Arena3D.yellow, Arena3D.black, Arena3D.yellow, Arena3D.white, 80.0f);
		m.setCapability(Material.ALLOW_COMPONENT_READ);
		m.setCapability(Material.ALLOW_COMPONENT_WRITE);		
		m.setLightingEnable(true);
		appSphericalBumper3DDefault.setMaterial(m);

		
		// TriangularBumperA3D: Default
		
		appTriangularBumperA3DDefault = new Appearance();
		appTriangularBumperA3DDefault.setCapability(Appearance.ALLOW_MATERIAL_READ);
		appTriangularBumperA3DDefault.setCapability(Appearance.ALLOW_MATERIAL_WRITE);
		appTriangularBumperA3DDefault.setCapability(Appearance.ALLOW_TRANSPARENCY_ATTRIBUTES_READ);
		appTriangularBumperA3DDefault.setCapability(Appearance.ALLOW_TRANSPARENCY_ATTRIBUTES_WRITE);		
		appTriangularBumperA3DDefault.setCapability(Appearance.ALLOW_POLYGON_ATTRIBUTES_READ);
		appTriangularBumperA3DDefault.setCapability(Appearance.ALLOW_POLYGON_ATTRIBUTES_WRITE);
		appTriangularBumperA3DDefault.setCapability(Appearance.ALLOW_RENDERING_ATTRIBUTES_READ);
		appTriangularBumperA3DDefault.setCapability(Appearance.ALLOW_RENDERING_ATTRIBUTES_WRITE);		

		m =	new Material(Arena3D.yellow, Arena3D.black, Arena3D.yellow, Arena3D.white, 80.0f);
		m.setCapability(Material.ALLOW_COMPONENT_READ);
		m.setCapability(Material.ALLOW_COMPONENT_WRITE);		
		appTriangularBumperA3DDefault.setMaterial(m);
		m.setLightingEnable(false);


		// TriangularBumperB3D: Default
		
		appTriangularBumperB3DDefault = new Appearance();
		appTriangularBumperB3DDefault.setCapability(Appearance.ALLOW_MATERIAL_READ);
		appTriangularBumperB3DDefault.setCapability(Appearance.ALLOW_MATERIAL_WRITE);
		appTriangularBumperB3DDefault.setCapability(Appearance.ALLOW_TRANSPARENCY_ATTRIBUTES_READ);
		appTriangularBumperB3DDefault.setCapability(Appearance.ALLOW_TRANSPARENCY_ATTRIBUTES_WRITE);		
		appTriangularBumperB3DDefault.setCapability(Appearance.ALLOW_POLYGON_ATTRIBUTES_READ);
		appTriangularBumperB3DDefault.setCapability(Appearance.ALLOW_POLYGON_ATTRIBUTES_WRITE);
		appTriangularBumperB3DDefault.setCapability(Appearance.ALLOW_RENDERING_ATTRIBUTES_READ);
		appTriangularBumperB3DDefault.setCapability(Appearance.ALLOW_RENDERING_ATTRIBUTES_WRITE);		

		m =	new Material(Arena3D.yellow, Arena3D.black, Arena3D.yellow, Arena3D.white, 80.0f);
		m.setCapability(Material.ALLOW_COMPONENT_READ);
		m.setCapability(Material.ALLOW_COMPONENT_WRITE);		
		appTriangularBumperB3DDefault.setMaterial(m);
		m.setLightingEnable(false);
		
		PolygonAttributes pa = new PolygonAttributes();
		pa.setCullFace(PolygonAttributes.CULL_NONE);
		appTriangularBumperB3DDefault.setPolygonAttributes(pa);		

		// TriangularBumperB3D: Default
		
		appTriangularBumperC3DDefault = new Appearance();
		appTriangularBumperC3DDefault.setCapability(Appearance.ALLOW_MATERIAL_READ);
		appTriangularBumperC3DDefault.setCapability(Appearance.ALLOW_MATERIAL_WRITE);
		appTriangularBumperC3DDefault.setCapability(Appearance.ALLOW_TRANSPARENCY_ATTRIBUTES_READ);
		appTriangularBumperC3DDefault.setCapability(Appearance.ALLOW_TRANSPARENCY_ATTRIBUTES_WRITE);		
		appTriangularBumperC3DDefault.setCapability(Appearance.ALLOW_POLYGON_ATTRIBUTES_READ);
		appTriangularBumperC3DDefault.setCapability(Appearance.ALLOW_POLYGON_ATTRIBUTES_WRITE);
		appTriangularBumperC3DDefault.setCapability(Appearance.ALLOW_RENDERING_ATTRIBUTES_READ);
		appTriangularBumperC3DDefault.setCapability(Appearance.ALLOW_RENDERING_ATTRIBUTES_WRITE);
            
		m =	new Material(Arena3D.yellow, Arena3D.black, Arena3D.yellow, Arena3D.white, 80.0f);
		m.setCapability(Material.ALLOW_COMPONENT_READ);
		m.setCapability(Material.ALLOW_COMPONENT_WRITE);		
		appTriangularBumperC3DDefault.setMaterial(m);
		m.setLightingEnable(false);
		
		pa = new PolygonAttributes();
		pa.setCullFace(PolygonAttributes.CULL_NONE);
		appTriangularBumperC3DDefault.setPolygonAttributes(pa);		
			
		// Flipper: Default
		appFlipper3DDefault = new Appearance();
		appFlipper3DDefault.setCapability(Appearance.ALLOW_MATERIAL_READ);
		appFlipper3DDefault.setCapability(Appearance.ALLOW_MATERIAL_WRITE);
		appFlipper3DDefault.setCapability(Appearance.ALLOW_TRANSPARENCY_ATTRIBUTES_READ);
		appFlipper3DDefault.setCapability(Appearance.ALLOW_TRANSPARENCY_ATTRIBUTES_WRITE);
		appFlipper3DDefault.setCapability(Appearance.ALLOW_POLYGON_ATTRIBUTES_READ);
		appFlipper3DDefault.setCapability(Appearance.ALLOW_POLYGON_ATTRIBUTES_WRITE);
		appFlipper3DDefault.setCapability(Appearance.ALLOW_RENDERING_ATTRIBUTES_READ);
		appFlipper3DDefault.setCapability(Appearance.ALLOW_RENDERING_ATTRIBUTES_WRITE);		

		m =	new Material(Arena3D.orange, Arena3D.black, Arena3D.orange, Arena3D.white, 80.0f);
		m.setCapability(Material.ALLOW_COMPONENT_READ);
		m.setCapability(Material.ALLOW_COMPONENT_WRITE);
		m.setLightingEnable(true);
		appFlipper3DDefault.setMaterial(m);

	
		// Absorber: Default	
		appAbsorber3DDefault = new Appearance();
		appAbsorber3DDefault.setCapability(Appearance.ALLOW_MATERIAL_READ);
		appAbsorber3DDefault.setCapability(Appearance.ALLOW_MATERIAL_WRITE);
		appAbsorber3DDefault.setCapability(Appearance.ALLOW_TRANSPARENCY_ATTRIBUTES_READ);
		appAbsorber3DDefault.setCapability(Appearance.ALLOW_TRANSPARENCY_ATTRIBUTES_WRITE);		
		appAbsorber3DDefault.setCapability(Appearance.ALLOW_POLYGON_ATTRIBUTES_READ);
		appAbsorber3DDefault.setCapability(Appearance.ALLOW_POLYGON_ATTRIBUTES_WRITE);
		appAbsorber3DDefault.setCapability(Appearance.ALLOW_RENDERING_ATTRIBUTES_READ);
		appAbsorber3DDefault.setCapability(Appearance.ALLOW_RENDERING_ATTRIBUTES_WRITE);		

		m =	new Material(Arena3D.blue, Arena3D.black, Arena3D.blue, Arena3D.white, 80.0f);
		m.setCapability(Material.ALLOW_COMPONENT_READ);
		m.setCapability(Material.ALLOW_COMPONENT_WRITE);		
		m.setLightingEnable(true);
		appAbsorber3DDefault.setMaterial(m);
		
		TransparencyAttributes ta = new TransparencyAttributes();
		ta.setTransparencyMode(TransparencyAttributes.BLENDED);
		ta.setTransparency(0.6f);
//		appAbsorber3DDefault.setTransparencyAttributes(ta);

		// Jezzmo: Default
		// CubicalBumper3D: Default
		appJezzmo3DDefault = new Appearance();
		appJezzmo3DDefault.setCapability(Appearance.ALLOW_MATERIAL_READ);
		appJezzmo3DDefault.setCapability(Appearance.ALLOW_MATERIAL_WRITE);
		appJezzmo3DDefault.setCapability(Appearance.ALLOW_TRANSPARENCY_ATTRIBUTES_READ);
		appJezzmo3DDefault.setCapability(Appearance.ALLOW_TRANSPARENCY_ATTRIBUTES_WRITE);		
		appJezzmo3DDefault.setCapability(Appearance.ALLOW_POLYGON_ATTRIBUTES_READ);
		appJezzmo3DDefault.setCapability(Appearance.ALLOW_POLYGON_ATTRIBUTES_WRITE);
		appJezzmo3DDefault.setCapability(Appearance.ALLOW_RENDERING_ATTRIBUTES_READ);
		appJezzmo3DDefault.setCapability(Appearance.ALLOW_RENDERING_ATTRIBUTES_WRITE);		

		m =	new Material(Arena3D.turquoise, Arena3D.black, Arena3D.turquoise, Arena3D.white, 80.0f);
		m.setCapability(Material.ALLOW_COMPONENT_READ);
		m.setCapability(Material.ALLOW_COMPONENT_WRITE);
		m.setLightingEnable(true);
		appJezzmo3DDefault.setMaterial(m);		

	}
	
	
	
	/**
	 * @effects Enables all behaviors of all the active gizmoball game pieces.
	 *          Does not change OuterWall3D gizmos behaviors.
	 */
	public static void enableAllPieceBehaviors() {
		for (int i = 0; i < Arena3DScene.allBalls.size(); i++) {
			Piece g = (Piece) Arena3DScene.allBalls.get(i);
			g.enablePieceBehaviors();
		}
		for (int j = 0; j < Arena3DScene.allGizmos.size(); j++) {
			Piece g = (Piece) Arena3DScene.allGizmos.get(j);
			if (g instanceof OuterWall3D) {
				continue;
			}
			g.enablePieceBehaviors();
		}		
			
	}
	
	
	/**
	 * @effects Disables all behaviors of all the active gizmoball game pieces.
	 *          Does not change OuterWall3D gizmos behaviors.
	 */	
	public static void disableAllPieceBehaviors() {
		for (int i = 0; i < Arena3DScene.allBalls.size(); i++) {
			Piece g = (Piece) Arena3DScene.allBalls.get(i);
			g.disablePieceBehaviors();
		}
		for (int j = 0; j < Arena3DScene.allGizmos.size(); j++) {
			Piece g = (Piece) Arena3DScene.allGizmos.get(j);
			if (g instanceof OuterWall3D) {
				continue;
			}			
			g.disablePieceBehaviors();
		}				
	}
	
	/**
	 * Returns the key handler that manages key triggering to gizmos
	 * @return key handler
	 */
	public static EventHandler getEventHandler() {
		return PieceProperties.eventHandler;
	}

}
