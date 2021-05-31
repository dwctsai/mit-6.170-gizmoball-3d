//
//  GizmoPhysicsTest.java
//  
//  Created by Eric Tung on Sun Apr 27 2003.
//

package plugins.testing;

import javax.media.j3d.Transform3D;
import javax.vecmath.Point3d;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import plugins.pieces.Ball3D;
import plugins.pieces.BallFactory;
import backend.PieceFactory;
import engine.backend.BallPhysics;
import engine.backend.PiecePhysics;

public class GizmoPhysicsTest extends TestCase {

    private static final double tolerance = 0.0001;

    public GizmoPhysicsTest(String name) { super(name); }

    protected void setUp() {}
    
//    public void testAbsorber3D() { testStaticGizmoPhysicsInterface(new AbsorberFactory()); }
    public void testBall3D()
    {
        Point3d theLocation = new Point3d(3.4, 5.1, 1.7);
        PieceFactory bf = new BallFactory();
        Ball3D b3d = (Ball3D)bf.makePiece(theLocation);
        testBallPhysicsInterface(b3d);
    }
//    public void testCubicalBumper3D() { testStaticGizmoPhysicsInterface(new CubicalBumperFactory()); }
//    public void testFlipper3D() { testRotatableGizmoPhysicsInterface(new FlipperFactory()); }
//    public void testOuterWall3D() { testStaticGizmoPhysicsInterface(new OuterWallFactory()); }
//    public void testSphericalBumper3D() { testStaticGizmoPhysicsInterface(new SphericalBumperFactory()); }
//    public void testTriangularBumperA3D() { testStaticGizmoPhysicsInterface(new TriangularBumperAFactory()); }

    private void testBallPhysicsInterface(BallPhysics bp)
    {
        bp.setMass(17.0);
        assertEquals("Piece did not honor setMass", 17.0, bp.getMass(), tolerance);
        bp.setMass(2.0);
        assertEquals("Piece did not honor setMass", 2.0, bp.getMass(), tolerance);
        Transform3D testTransform = new Transform3D();
        bp.setVelocity(testTransform);
        assertTrue("Piece did not honor setTransform", testTransform.epsilonEquals(bp.getVelocity(), tolerance));        
        testPiecePhysicsInterface(bp);
    }

//    private void testGizmoPhysicsInterface(PieceFactory pf)
//    {
        // ADD TESTS
//        testPiecePhysicsInterface(pf);
//    }

//    private void testMobileGizmoPhysicsInterface(PieceFactory pf)
//    {
        // ADD TESTS
//        testGizmoPhysicsInterface(pf);
//    }

    private void testPiecePhysicsInterface(PiecePhysics pp)
    {
        // this only tests get/set Location and Visible
        pp.setPhysical(true);
        assertTrue("Piece did not honor setVisible(true)", pp.isPhysical());
        pp.setPhysical(false);
        assertTrue("Piece did not honor setVisible(false)", !pp.isPhysical());
    }

//    private void testRotatableGizmoPhysicsInterface(PieceFactory pf)
//    {
        // ADD TESTS
//        testGizmoPhysicsInterface(pf);
//    }

//    private void testStaticGizmoPhysicsInterface(PieceFactory pf) { testGizmoPhysicsInterface(pf); }


    public static Test suite()
    {
        TestSuite suite = new TestSuite();
//        suite.addTest(new GizmoPhysicsTest("testAbsorber3D"));
        suite.addTest(new GizmoPhysicsTest("testBall3D"));
//        suite.addTest(new GizmoPhysicsTest("testCubicalBumper3D"));
//        suite.addTest(new GizmoPhysicsTest("testFlipper3D"));
//        suite.addTest(new GizmoPhysicsTest("testOuterWall3D"));
//        suite.addTest(new GizmoPhysicsTest("testSphericalBumper3D"));
//        suite.addTest(new GizmoPhysicsTest("testTriangluarBumperA3D"));
        return suite;
    }
    
}