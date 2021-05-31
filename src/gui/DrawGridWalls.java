/* @author David Tsai
 * @created on Apr 10, 2003
 */

package gui;

import java.util.ArrayList;

import javax.media.j3d.Appearance;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.LineArray;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3d;

import plugins.pieces.OuterWall3D;
import backend.Piece;

import com.sun.j3d.utils.geometry.Box;

/**
 * Draws Arena3D's outer grid walls.
 */
public class DrawGridWalls {
	// FIELDS
	private BranchGroup miscBranch;
	
	// CONSTRUCTOR
	/**
	 * @effects Creates a new DrawGridWalls from the given parameters.
	 */
	public DrawGridWalls(BranchGroup mb) {
		this.miscBranch = mb;
	}
	
	/**
	 * @effects Draws the xy-plane's grid wall.
	 */	
	public void createXYGridWall() {
		// vertical lines
		LineArray xyVerticalGridLines = new LineArray(42, 
				LineArray.COORDINATES | LineArray.COLOR_3 );
		Shape3D xyVglS3D = new Shape3D(xyVerticalGridLines);
		xyVglS3D.setCollidable(false);
		miscBranch.addChild(xyVglS3D);
		float j = 0.0f;		
		for (int i = 0; i < 42; i += 2, j += 1.0f) {
			xyVerticalGridLines.setCoordinate(i, new Point3f(j, 0.0f, 0.0f ));
			xyVerticalGridLines.setCoordinate(i + 1, new Point3f(j, 20.0f, 0.0f ));
			xyVerticalGridLines.setColor(i, Arena3D.blue);
		}
		
		// horizontal lines
		LineArray xyHorizontalGridLines = new LineArray(42, 
				LineArray.COORDINATES | LineArray.COLOR_3 );
		Shape3D xyHglS3D = new Shape3D(xyHorizontalGridLines);
		xyHglS3D.setCollidable(false);
		miscBranch.addChild(xyHglS3D);
		j = 0.0f;		
		for (int i = 0; i < 42; i += 2, j += 1.0f) {
			xyHorizontalGridLines.setCoordinate(i, new Point3f(0.0f, j, 0.0f ));
			xyHorizontalGridLines.setCoordinate(i + 1, new Point3f(20.0f, j, 0.0f ));
			xyHorizontalGridLines.setColor(i, Arena3D.blue);
		}		
	}
	
	/**
	 * @effects Draws the yz-plane's grid wall.
	 */	
	public void createYZGridWall() {
		// vertical lines
		LineArray yzVerticalGridLines = new LineArray(42, 
				LineArray.COORDINATES | LineArray.COLOR_3 );
		Shape3D yzVglS3D = new Shape3D(yzVerticalGridLines);
		yzVglS3D.setCollidable(false);
		miscBranch.addChild(yzVglS3D);
		float j = 0.0f;		
		for (int i = 0; i < 42; i += 2, j += 1.0f) {
			yzVerticalGridLines.setCoordinate(i, new Point3f(0.0f, 0.0f, j ));
			yzVerticalGridLines.setCoordinate(i + 1, new Point3f(0.0f, 20.0f, j ));
			yzVerticalGridLines.setColor(i, Arena3D.red);
		}
		
		// horizontal lines
		LineArray yzHorizontalGridLines = new LineArray(42, 
				LineArray.COORDINATES | LineArray.COLOR_3 );
		Shape3D yzHglS3D = new Shape3D(yzHorizontalGridLines);
		yzHglS3D.setCollidable(false);
		miscBranch.addChild(yzHglS3D);
		j = 0.0f;		
		for (int i = 0; i < 42; i += 2, j += 1.0f) {
			yzHorizontalGridLines.setCoordinate(i, new Point3f(0.0f, j, 0.0f ));
			yzHorizontalGridLines.setCoordinate(i + 1, new Point3f(0.0f, j, 20.0f ));
			yzHorizontalGridLines.setColor(i, Arena3D.red);
		}		
	}
	
	
	/**
	 * @effects Draws the xz-plane's grid wall.
	 */	
	public void createXZGridWall() {
		// vertical lines
		LineArray xzVerticalGridLines = new LineArray(42, 
				LineArray.COORDINATES | LineArray.COLOR_3 );
		Shape3D xzVglS3D = new Shape3D(xzVerticalGridLines);
		xzVglS3D.setCollidable(false);
		miscBranch.addChild(xzVglS3D);
		float j = 0.0f;		
		for (int i = 0; i < 42; i += 2, j += 1.0f) {
			xzVerticalGridLines.setCoordinate(i, new Point3f(j, 0.0f, 0.0f ));
			xzVerticalGridLines.setCoordinate(i + 1, new Point3f(j, 0.0f, 20.0f ));
			xzVerticalGridLines.setColor(i, Arena3D.yellow);
		}
		
		// horizontal lines
		LineArray xzHorizontalGridLines = new LineArray(42, 
				LineArray.COORDINATES | LineArray.COLOR_3 );
		Shape3D xzHglS3D = new Shape3D(xzHorizontalGridLines);
		xzHglS3D.setCollidable(false);
		miscBranch.addChild(xzHglS3D);
		j = 0.0f;		
		for (int i = 0; i < 42; i += 2, j += 1.0f) {
			xzHorizontalGridLines.setCoordinate(i, new Point3f(0.0f, 0.0f, j ));
			xzHorizontalGridLines.setCoordinate(i + 1, new Point3f(20.0f, 0.0f, j ));
			xzHorizontalGridLines.setColor(i, Arena3D.yellow);
		}			
	}
	
	/**
	 * @effects Draws the shadow projections of a given BoundingBox onto 
	 *          the xz, xy, and yz grid walls.
	 */
	public void drawGridShadows() {
		// Create a new sub-branchgroup for all the shadows projections.
		BranchGroup shadowSubBranch = new BranchGroup();

		// Create an appearance of the shadow projections.
		Appearance app = new Appearance();
		ColoringAttributes ca = new ColoringAttributes();
		ca.setColor(Arena3D.cyan);
		app.setColoringAttributes(ca);
		//		TransparencyAttributes ta = new TransparencyAttributes();
		//		ta.setTransparencyMode(TransparencyAttributes.BLENDED);
		//		ta.setTransparency(0.6f);
		//		app.setTransparencyAttributes(ta);

		// Iterate through all the Gizmos and draw their shadow projections.
		ArrayList gizmos = Arena3DScene.allGizmos;
		ArrayList balls = Arena3DScene.allBalls;
		boolean combo = gizmos.addAll(balls);
		Point3d lp = null;
		Point3d up = null;
		TransformGroup tempTG = null;
		Transform3D tempT3D = new Transform3D();
		Vector3d tempVec = null;
		Box tempBox = null; 		
		for (int i = 0; i < gizmos.size(); i++) {
			Piece p = (Piece) gizmos.get(i);
			if (p instanceof OuterWall3D) {		// don't paint OuterWalls' shadows
				continue;
			}
			lp = new Point3d(p.getContainerLowerLocation());
			up = new Point3d(p.getContainerUpperLocation());

			// Draw on xy wall.
			tempTG = new TransformGroup();			
			tempBox = new Box((float) (up.x-lp.x)/2.0f, 
						      (float) (up.y-lp.y)/2.0f, 
						      0.0f,
						      app);
			tempVec = new Vector3d((up.x+lp.x)/2.0, (up.y+lp.y)/2.0, 0.0);
			tempT3D.set(tempVec);
			tempTG.setTransform(tempT3D);
			tempTG.addChild(tempBox);
			shadowSubBranch.addChild(tempTG);
			
			// Draw on yz wall.
			tempTG = new TransformGroup();			
			tempBox = new Box(0.0f,
							  (float) (up.y-lp.y)/2.0f, 
							  (float) (up.z-lp.z)/2.0f,
							  app);
			tempVec = new Vector3d(0.0, (up.y+lp.y)/2.0, (up.z+lp.z)/2.0);
			tempT3D.set(tempVec);
			tempTG.setTransform(tempT3D);
			tempTG.addChild(tempBox);
			shadowSubBranch.addChild(tempTG);
			
			// Draw on xz wall.
			tempTG = new TransformGroup();			
			tempBox = new Box((float) (up.x-lp.x)/2.0f,
							  0.0f, 
							  (float) (up.z-lp.z)/2.0f,
							  app);
			tempVec = new Vector3d((up.x+lp.x)/2.0, 0.0, (up.z+lp.z)/2.0);
			tempT3D.set(tempVec);
			tempTG.setTransform(tempT3D);
			tempTG.addChild(tempBox);
			shadowSubBranch.addChild(tempTG);		
		}

		// Add the sub-branch to miscBranch.
		shadowSubBranch.compile();
		miscBranch.addChild(shadowSubBranch);
	}
	
	
}
