//
//  GeometryTest.java
//  
//
//  Created by Eric Tung on Sat Apr 12 2003.
//

// File history
// 2003/04/12 EGT added Ball/nonmoving PlaneSegment test
// 2003/04/13 EGT added Ball/nonmoving Cylinder test, expanded Ball/nonmoving PlaneSegment test
// 2003/04/13 EGT expanded Ball/nonmoving Cylinder test a bunch, most cases are now tested
// 2003/04/13 EGT added Ball/nonmoving Sphere test, expanded Ball/nonmoving PlaneSegment test slightly
// 2003/04/14 EGT added Ball/Ball test
// 2003/04/21 EGT added Ball/Rotating Sphere test
// 2003/04/25 EGT added more thorough ball/nonmoving * tests
// 2003/04/26 EGT added Ball/Rotating Cylinder tests
// 2003/04/26 EGT added Ball/Rotating PlaneSegment test; this will probably have to be expanded.
// 2003/04/27 EGT added test for a ball overlapping with a plane but not with a plane segment

package physics3d;

import javax.media.j3d.Transform3D;
import javax.vecmath.Matrix4d;
import javax.vecmath.Vector3d;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class GeometryTest extends TestCase {

    private static final double tolerance = 0.0001;

    private Sphere aBall = null;
    private Cylinder aCylinder = null;
    private PlaneSegment aPlaneSegment = null;
    private PlaneSegment bPlaneSegment = null;

    public GeometryTest(String name) { super(name); }

    protected void setUp()
    {
        aBall = new Sphere(0.0, 0.0, 2.5, 0.5, "aBall");
        aCylinder = new Cylinder(1.0, 3.5, 0.0, -1.0, 3.5, 2.0, 0.0, "aCylinder");
        aPlaneSegment = new PlaneSegment(-1.0, -1.0, 1.0, 1.0, -1.0, 1.0, 0.0, 1.0, 1.0, "aPlaneCylinder");
        bPlaneSegment = new PlaneSegment(1.0, 1.0, 1.0, 1.0, -1.0, 1.0, 0.0, 1.0, 1.0, "bPlaneSegment");
    }

  public void testTransform()
  { // http://www.makegames.com/3drotation/
    Matrix4d dmat = new Matrix4d(1.0, 0.0, 0.0, 1.0,
				 0.0, 1.0, 0.0, 0.0,
				 0.0, 0.0, 1.0, 0.0,
				 0.0, 0.0, 0.0, 1.0);
    Transform3D dmt = new Transform3D(dmat);
    Vector3d xhv = new Vector3d(1.0, 0.0, 0.0);
    Transform3D xhat = new Transform3D();
    xhat.set(xhv);
    assertTrue("Transform from matrix and Transform from vector do not match", xhat.epsilonEquals(dmt, tolerance));
    Vector3d yhv = new Vector3d(0.0, 1.0, 0.0);
    Transform3D yhat = new Transform3D();
    yhat.set(yhv);
    
    Matrix4d r90xym = new Matrix4d(0.0, -1.0, 0.0, 0.0,
				   1.0, 0.0, 0.0, 0.0,
				   0.0, 0.0, 1.0, 0.0,
				   0.0, 0.0, 0.0, 1.0);
    Transform3D r90xy = new Transform3D(r90xym);
    Transform3D r90z = new Transform3D();
    r90z.rotZ(Math.PI/2);
//    System.out.println("Matrix:\n" + r90xy);
//    System.out.println("\nPrimitive:\n" + r90z);
    Transform3D temp = new Transform3D(r90xy);
    temp.sub(r90z);
//    System.out.println("\nTemp:\n" + temp);
    Vector3d v1 = new Vector3d();
    Vector3d v2 = new Vector3d();
    r90xy.get(v1);
    r90z.get(v2);
    assertTrue("Vector from matrix and Vector from primitive do not match", v1.epsilonEquals(v2, tolerance));

    Transform3D testTrans = new Transform3D(r90xy);
    testTrans.mul(yhat);
//    System.out.println("Y-hat:\n" + yhat);
//    System.out.println("\n90-XY * Y-hat:\n" + testTrans);
//    assertTrue("Y-hat and X-hat*90-XYdo not match", yhat.epsilonEquals(testTrans, tolerance));


    Transform3D testTrans2 = Geometry.rotateTransformAboutLine(xhat, new Cylinder(0.0, 0.0, 0.0, 0.0, 2.0, 0.0, 0.0, "xhat"), -Math.PI/2);
    
//    System.out.println("1,0,0 -90 degrees about Y axis\n" + testTrans2); // 0, 0, 1
    v1 = new Vector3d(0.0, 0.0, 1.0);
    testTrans2.get(v2);
    assertTrue("1,0,0 -90 degrees about Y axis is incorrect", v1.epsilonEquals(v2, tolerance));
//    Transform3D testTransA = new Transform3D(xhat);
//    Transform3D testTransB = new Transform3D();
//    testTransB.rotY(-Math.PI/2);
//    testTransA.mul(testTransB, testTransA);
//    System.out.println("1,0,0 -90 degrees about Y axis\n" + testTransA); // 0, 0, 1

    Transform3D testTrans3 = Geometry.rotateTransformAboutLine(xhat, new Cylinder(2.0, 0.0, 0.0, 2.0, 2.0, 0.0, 0.0, "xhat"), -Math.PI/2);
    v1 = new Vector3d(2.0, 0.0, -1.0);
    testTrans3.get(v2);
    assertTrue("1,0,0 -90 degrees about 2,0,0:2,2,0 is incorrect", v1.epsilonEquals(v2, tolerance));
//    System.out.println("1,0,0 -90 degrees about 2,0,0:2,2,0\n" + testTrans3); //2, 0, -1

    Transform3D testTransC = Geometry.rotateTransformAboutLine(xhat, new Cylinder(2.0, 2.0, 0.0, 2.0, 0.0, 0.0, 0.0, "xhat"), -Math.PI/2);
    v1 = new Vector3d(2.0, 0.0, 1.0);
    testTransC.get(v2);
    assertTrue("1,0,0 -90 degrees about 2,2,0:2,0,0 is incorrect", v1.epsilonEquals(v2, tolerance));
//    System.out.println("1,0,0 -90 degrees about 2,2,0:2,0,0\n" + testTransC); //2, 0, 1

    Transform3D testTrans4 = Geometry.rotateTransformAboutLine(xhat, new Cylinder(0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, "xhat"), -Math.PI/2);
    v1 = new Vector3d(0.0, -1.0, 0.0);
    testTrans4.get(v2);
    assertTrue("1,0,0 -90 degrees about 0,0,0:0,0,1 is incorrect", v1.epsilonEquals(v2, tolerance));
//    System.out.println("1,0,0 -90 degrees about 0,0,0:0,0,1\n" + testTrans4); // 0, -1, 0

    Transform3D testTrans5 = Geometry.rotateTransformAboutLine(xhat, new Cylinder(-1.0, 0.0, 0.0, -1.0, 0.0, 1.0, 0.0, "xhat"), -Math.PI/2);
    v1 = new Vector3d(-1.0, -2.0, 0.0);
    testTrans5.get(v2);
    assertTrue("1,0,0 -90 degrees about -1,0,0:-1,0,1 is incorrect", v1.epsilonEquals(v2, tolerance));
//    System.out.println("1,0,0 -90 degrees about -1,0,0:-1,0,1\n" + testTrans5); // -1, -2, 0

    Transform3D testTrans6 = Geometry.rotateTransformAboutLine(xhat, new Cylinder(0.0, 1.0, 0.0, 1.0, 1.0, 0.0, 0.0, "xhat"), -Math.PI/2);
    v1 = new Vector3d(1.0, 1.0, 1.0);
    testTrans6.get(v2);
    assertTrue("1,0,0 -90 degrees about 0,1,0:1,1,0 is incorrect", v1.epsilonEquals(v2, tolerance));
//    System.out.println("1,0,0 -90 degrees about 0,1,0:1,1,0\n" + testTrans6); // 1, 1, 1

    Transform3D testTrans7 = new Transform3D();
    testTrans7.set(new Vector3d(1.0, 2.0, 3.0));
    Transform3D testTrans8 = Geometry.rotateTransformAboutLine(testTrans7, new Cylinder(-1.0, 0.0, 0.0, 0.0, 1.0, 1.0, 0.0, "testTrans7"), 4*Math.PI/3);
    v1 = new Vector3d(1.0, 3.0, 2.0);
    testTrans8.get(v2);
    assertTrue("1,2,3 240 degrees about -1,0,0:0,1,1 is incorrect", v1.epsilonEquals(v2, tolerance));
//    System.out.println("1,2,3 240 degrees about 1,2,2:2,3,3\n" + testTrans8);	//1, 3, 2

  }
  
    public void testConstructors()
    {
        Geometry.showDebugInfo = false;
        Transform3D t1 = new Transform3D();
        Transform3D t2 = new Transform3D();
        Vector3d p1 = new Vector3d(1.0, 3.5, 0.0);
        Vector3d p2 = new Vector3d(-1.0, 3.5, 2.0);
        t1.set(p1);
        t2.set(p2);
        Cylinder tcCylinder = new Cylinder(t1,t2,0.0, "tCylinder");
        assertTrue("Cylinder point 1 is incorrect", tcCylinder.p1().equals(p1));
        assertTrue("Cylinder point 2 is incorrect", tcCylinder.p2().equals(p2));
        assertEquals("Cylinder radius is incorrect", 0.0, tcCylinder.getRadius(), tolerance);
    }

    public void testEquals()
    {
        Geometry.showDebugInfo = false;
        Sphere teBall = new Sphere(0.0, 0.0, 1.5, 0.5, "teBall");
        Cylinder teCylinder = new Cylinder(1.0, 3.0, 0.0, -1.0, 3.0, 2.0, 0.0, "teCylinder");
        PlaneSegment tePlaneSegment = new PlaneSegment(-1.0, -1.0, 0.0, 1.0, -1.0, 0.0, 0.0, 1.0, 0.0, "tePlaneSegment");
        assertTrue("Sphere equals is not reflexive!", teBall.equals(new Sphere(0.0, 0.0, 1.5, 0.5, "testBall")));
        assertTrue("Sphere equals radius it shouldn't!", !teBall.equals(new Sphere(0.0, 0.0, 1.5, 0.4, "testBall")));
        assertTrue("Sphere equals z it shouldn't!", !teBall.equals(new Sphere(0.0, 0.0, 1.2, 0.5, "testBall")));
        assertTrue("Sphere equals y it shouldn't!", !teBall.equals(new Sphere(0.0, -0.3, 1.5, 0.5, "testBall")));
        assertTrue("Sphere equals x it shouldn't!", !teBall.equals(new Sphere(0.17, 0.0, 1.5, 0.5, "testBall")));
        assertTrue("Cylinder equals is not reflexive!", teCylinder.equals(new Cylinder(1.0, 3.0, 0.0, -1.0, 3.0, 2.0, 0.0, "Test Cylinder")));
        assertTrue("Cylinder equals radius it shouldn't!", !teCylinder.equals(new Cylinder(1.0, 3.0, 0.0, -1.0, 3.0, 2.0, 0.7, "Test Cylinder")));
        assertTrue("Cylinder equals reverse!", !teCylinder.equals(new Cylinder(-1.0, 3.0, 0.0, 1.0, 3.0, 2.0, 0.0, "Test Cylinder")));
        assertTrue("PlaneSegment equals is not reflexive!", tePlaneSegment.equals(new PlaneSegment(-1.0, -1.0, 0.0, 1.0, -1.0, 0.0, 0.0, 1.0, 0.0, "Test PlaneSegment")));
        assertTrue("PlaneSegment equals something it shouldn't!", !tePlaneSegment.equals(new PlaneSegment(1.0, -1.0, 0.0, -1.0, -1.0, 0.0, 0.0, 1.0, 0.0, "Test PlaneSegment")));
    }

    public void testSimpleBallSphereCollision()
    {
        Geometry.showDebugInfo = false;

        Transform3D ballVelocity = new Transform3D();
        Vector3d ballVelocityV = new Vector3d(0.0, 0.8, 0.0);
        ballVelocity.set(ballVelocityV);
        Sphere aGizmo = new Sphere (0.0, 3.0, 2.5, 0.5, "aGizmo");
        Sphere bGizmo = new Sphere (1.0, 3.0, 2.5, 0.5, "bGizmo");
        double t = Geometry.timeUntilBallSphereCollision(aBall, bGizmo, ballVelocity);
        assertEquals("Ball-Gizmo collision time is wrong!", Double.POSITIVE_INFINITY, t, tolerance);	// should just miss bGizmo
        t = Geometry.timeUntilBallSphereCollision(aBall, aGizmo, ballVelocity); 
        assertEquals("Ball-Gizmo collision time is wrong!", 2.5, t, tolerance);				// head-on collision
        Transform3D deltaT3D = new Transform3D(ballVelocity);
        deltaT3D.mul(t);
        Vector3d deltaV3d = new Vector3d();
        deltaT3D.get(deltaV3d);
        Sphere tsbscBall = new Sphere(aBall.getCenter().x + deltaV3d.x, aBall.getCenter().y + deltaV3d.y, aBall.getCenter().z + deltaV3d.z, aBall.getRadius(), "tsbscBall");
        Transform3D newVelocityT3D = Geometry.reflectBallFromSphere(tsbscBall, aGizmo, ballVelocity);
        Vector3d newVelocityV3d = new Vector3d();
        newVelocityT3D.get(newVelocityV3d);
        assertEquals("Ball-Gizmo reflection velocity error (COR = 1)",  ballVelocityV.length(), newVelocityV3d.length(), tolerance);
        assertEquals("Ball-Gizmo reflection x velocity error (COR = 1)", ballVelocityV.x, newVelocityV3d.x ,  tolerance);
        assertEquals("Ball-Gizmo reflection y velocity error (COR = 1)", -ballVelocityV.y, newVelocityV3d.y, tolerance);
        assertEquals("Ball-Gizmo reflection z velocity error (COR = 1)", ballVelocityV.z, newVelocityV3d.z, tolerance);

        Sphere tsbscBall2 = new Sphere(1 - 0.5*Math.sqrt(2), -0.5*Math.sqrt(2), 2.5, 0.5, "tsbscBall2");
        t = Geometry.timeUntilBallSphereCollision(tsbscBall2, bGizmo, ballVelocity); 
        assertEquals("Offcenter Ball-Gizmo collision time is wrong!", 3.75, t, tolerance);		// center of Gizmo is not on ballVelocity line
        deltaT3D = new Transform3D(ballVelocity);
        deltaT3D.mul(t);
        deltaT3D.get(deltaV3d);
        Sphere tsbscBall3 = new Sphere(tsbscBall2.getCenter().x + deltaV3d.x, tsbscBall2.getCenter().y + deltaV3d.y, tsbscBall2.getCenter().z + deltaV3d.z, tsbscBall2.getRadius(), "tsbscBall3");
        newVelocityT3D = Geometry.reflectBallFromSphere(tsbscBall3, bGizmo, ballVelocity);
        newVelocityT3D.get(newVelocityV3d);
        assertEquals("Offcenter Ball-Gizmo reflection velocity error (COR = 1)",  ballVelocityV.length(), newVelocityV3d.length(), tolerance);
        assertEquals("Offcenter Ball-Gizmo reflection x velocity error (COR = 1)", -ballVelocityV.y, newVelocityV3d.x ,  tolerance);
        assertEquals("Offcenter Ball-Gizmo reflection y velocity error (COR = 1)", 0.0, newVelocityV3d.y, tolerance);
        assertEquals("Offcenter Ball-Gizmo reflection z velocity error (COR = 1)", ballVelocityV.z, newVelocityV3d.z, tolerance);

        Sphere tsbscBall4 = new Sphere(1.0, 3.1, 2.5, 0.5, "tsbscBall4");
        t = Geometry.timeUntilBallSphereCollision(tsbscBall4, bGizmo, ballVelocity); 
        assertEquals("Overlapping Ball-Gizmo collision time is wrong!", Double.POSITIVE_INFINITY, t, tolerance);	// overlapping, moving apart
        Transform3D ballVelocity2 = new Transform3D();
        Vector3d ballVelocityV2 = new Vector3d(0.0, -0.8, 0.0);
        ballVelocity2.set(ballVelocityV2);
        t = Geometry.timeUntilBallSphereCollision(tsbscBall4, bGizmo, ballVelocity2); 
        assertEquals("Overlapping Ball-Gizmo collision time is wrong!", 0.0, t, tolerance);	// overlapping, moving together
    }


    public void testSimpleBallPlaneCollision()
    {
        Geometry.showDebugInfo = false;

        Transform3D ballVelocity = new Transform3D();
        Vector3d ballVelocityV = new Vector3d(0.0, 0.0, -2.0);
        ballVelocity.set(ballVelocityV);
        double t = Geometry.timeUntilBallPlaneCollision(aBall, bPlaneSegment, ballVelocity); 
        assertEquals("Ball-bPlaneSegment collision time is wrong!", Double.POSITIVE_INFINITY, t, tolerance);
        t = Geometry.timeUntilBallPlaneCollision(aBall, aPlaneSegment, ballVelocity); 
        assertEquals("Ball-aPlaneSegment collision time is wrong!", 0.5, t, tolerance);
        Transform3D deltaT3D = new Transform3D(ballVelocity);
        deltaT3D.mul(t);
        Vector3d deltaV3d = new Vector3d();
        deltaT3D.get(deltaV3d);
        Transform3D newVelocityT3D = Geometry.reflectBallFromPlane(aPlaneSegment, ballVelocity);
        Vector3d newVelocityV3d = new Vector3d();
        newVelocityT3D.get(newVelocityV3d);
        assertTrue("Ball-PlaneSegment reflection velocity error (COR = 1)", Math.abs(newVelocityV3d.length() - ballVelocityV.length()) < tolerance);
        assertTrue("Ball-PlaneSegment reflection x velocity error (COR = 1)", Math.abs(newVelocityV3d.x - ballVelocityV.x) < tolerance);
        assertTrue("Ball-PlaneSegment reflection y velocity error (COR = 1)", Math.abs(newVelocityV3d.y - ballVelocityV.y) < tolerance);
        assertTrue("Ball-PlaneSegment reflection z velocity error (COR = 1)", Math.abs(newVelocityV3d.z + ballVelocityV.z) < tolerance);

        Sphere tsbpcBall = new Sphere(0.5, 0.5, 2.5, 0.5, "tsbpcBall");
        Transform3D ballVelocity2 = new Transform3D();
        Vector3d ballVelocityV2 = new Vector3d(-1.0, -1.0, -2.0);
        ballVelocity2.set(ballVelocityV2);
        t = Geometry.timeUntilBallPlaneCollision(tsbpcBall, aPlaneSegment, ballVelocity2); 
        assertEquals("Angled Ball-aPlaneSegment collision time is wrong!", 0.5, t, tolerance);
        deltaT3D = new Transform3D(ballVelocity2);
        deltaT3D.mul(t);
        deltaT3D.get(deltaV3d);
        newVelocityT3D = Geometry.reflectBallFromPlane(aPlaneSegment, ballVelocity2);
        newVelocityT3D.get(newVelocityV3d);
        assertTrue("Angled Ball-PlaneSegment reflection velocity error (COR = 1)", Math.abs(newVelocityV3d.length() - ballVelocityV2.length()) < tolerance);
        assertTrue("Angled Ball-PlaneSegment reflection x velocity error (COR = 1)", Math.abs(newVelocityV3d.x - ballVelocityV2.x) < tolerance);
        assertTrue("Angled Ball-PlaneSegment reflection y velocity error (COR = 1)", Math.abs(newVelocityV3d.y - ballVelocityV2.y) < tolerance);
        assertTrue("Angled Ball-PlaneSegment reflection z velocity error (COR = 1)", Math.abs(newVelocityV3d.z + ballVelocityV2.z) < tolerance);

        Sphere tsbpcBall2 = new Sphere(-2.0+(0.5/Math.sqrt(3.0)), -0.5/Math.sqrt(3.0), 2.0+(0.5/Math.sqrt(3.0)), 0.5, "tsbpcBall2");
        Transform3D ballVelocity3 = new Transform3D();
        Vector3d ballVelocityV3 = new Vector3d(0.0, 0.4, 0.0);
        ballVelocity3.set(ballVelocityV3);
        PlaneSegment tsbpcPlaneSegment = new PlaneSegment(-2.05, 1.9, 1.95, -1.90, 2.05, 1.95, -2.05, 2.05, 2.10, "tsbpcPlaneSegment");        
        t = Geometry.timeUntilBallPlaneCollision(tsbpcBall2, tsbpcPlaneSegment, ballVelocity3); 		// check for collision accuracy
        assertEquals("Angled Ball-tsbpcPlaneSegment collision time is wrong!", 5.0, t, tolerance);
        deltaT3D = new Transform3D(ballVelocity3);
        deltaT3D.mul(t);
        deltaT3D.get(deltaV3d);
        newVelocityT3D = Geometry.reflectBallFromPlane(tsbpcPlaneSegment, ballVelocity3);
        newVelocityT3D.get(newVelocityV3d);
        assertEquals("Angled Ball-SmallPlaneSegment reflection velocity error (COR = 1)", ballVelocityV3.length(), newVelocityV3d.length(), tolerance);
        /*
        // I'm not sure what the answer should be. These seem reasonable, but because I didn't calculate them by hand, they're commented out
        assertEquals("Angled Ball-SmallPlaneSegment reflection x velocity error (COR = 1)", 8.0/30.0, newVelocityV3d.x, tolerance);
        assertEquals("Angled Ball-SmallPlaneSegment reflection y velocity error (COR = 1)", 4.0/30.0, newVelocityV3d.y, tolerance);
        assertEquals("Angled Ball-SmallPlaneSegment reflection z velocity error (COR = 1)", 8.0/30.0, newVelocityV3d.z, tolerance);
        */
        
        Sphere tsbpcBall3 = new Sphere(1.0, 1.6, 1.0, 0.5, "tsbpcBall3");
        Transform3D ballVelocity4 = new Transform3D();
        Vector3d ballVelocityV4 = new Vector3d(0.0, 0.4, 0.0);
        ballVelocity4.set(ballVelocityV4);
        PlaneSegment tsbpcPlaneSegment2 = new PlaneSegment(1.5, 1.5, 1.5, 1.5, 1.5, 0.25, 0.25, 1.5, 1.5, "tsbpcPlaneSegment2");        
        t = Geometry.timeUntilBallPlaneCollision(tsbpcBall3, tsbpcPlaneSegment2, ballVelocity4); 	// overlapping, moving apart
        assertEquals("Overlapping tsbpcBall3-tsbpcPlaneSegment2 collision time is wrong!", Double.POSITIVE_INFINITY, t, tolerance);
        Transform3D ballVelocity5 = new Transform3D();
        Vector3d ballVelocityV5 = new Vector3d(0.0, -0.4, 0.0);
        ballVelocity5.set(ballVelocityV5);

        t = Geometry.timeUntilBallPlaneCollision(tsbpcBall3, tsbpcPlaneSegment2, ballVelocity5); 	// overlapping, moving together
        assertEquals("Overlapping tsbpcBall3-tsbpcPlaneSegment2 collision time is wrong!", 0.0, t, tolerance);

        Sphere tsbpcBall4 = new Sphere(19.5, 16.930703122625633, 19.5, 0.25, "tsbpcBall4");
        Transform3D ballVelocity6 = new Transform3D();
        Vector3d ballVelocityV6 = new Vector3d(0.0, 24.96025135130202, 0.0);
        ballVelocity6.set(ballVelocityV6);
        PlaneSegment tsbpcPlaneSegment3 = new PlaneSegment(19.0, 17.0, 19.0, 19.0, 17.0, 20.0, 18.0, 17.0, 20.0, "tsbpcPlaneSegment");        
        t = Geometry.timeUntilBallPlaneCollision(tsbpcBall4, tsbpcPlaneSegment3, ballVelocity6); 	// test for the "triangle bumper" bug
        assertEquals("tsbpcBall4-tsbpcPlaneSegment3 collision time is wrong!", Double.POSITIVE_INFINITY, t, tolerance);
    }
    
    public void testSimpleBallCylinderCollision()
    {
//        Geometry.showDebugInfo = true;
        Geometry.showDebugInfo = false;

        Transform3D ballVelocity = new Transform3D();
        Vector3d ballVelocityV = new Vector3d(0.0, 0.5, 0.0);
        ballVelocity.set(ballVelocityV);
        Sphere tsbccBall = new Sphere(0.0, 0.0, 1.0, 0.5, "tsbccBall");
        Sphere tsbccBall2 = new Sphere(-2.0, 0.0, 3.0, 0.5, "tsbccBall2");
        Cylinder tsbccCylinder = new Cylinder(2.0, -1.0, 0.5, 2.0, 1.0, 0.5, 0.0, "tsbccCylinder");
        double t = Geometry.timeUntilBallCylinderCollision(tsbccBall, tsbccCylinder, ballVelocity); // ball parallel to line
        assertEquals("tsbccBall-tsbccCylinder collision time is wrong!", Double.POSITIVE_INFINITY, t, tolerance);
        t = Geometry.timeUntilBallCylinderCollision(tsbccBall, aCylinder, ballVelocity); // ball right angle to line
        assertEquals("tsbccBall-aCylinder collision time is wrong!", 6.0, t, tolerance);

        Transform3D deltaT3D = new Transform3D(ballVelocity);
        deltaT3D.mul(t);
        Vector3d deltaV3d = new Vector3d();
        deltaT3D.get(deltaV3d);
        Sphere tsbccBall4 = new Sphere(tsbccBall.getCenter().x + deltaV3d.x, tsbccBall.getCenter().y + deltaV3d.y, tsbccBall.getCenter().z + deltaV3d.z, tsbccBall.getRadius(), "tsbccBall4");
        Transform3D newVelocityT3D = Geometry.reflectBallFromCylinder(tsbccBall4, aCylinder, ballVelocity);
        Vector3d newVelocityV3d = new Vector3d();
        newVelocityT3D.get(newVelocityV3d);
        double vel = newVelocityV3d.length();
        assertEquals("Ball-Line reflection velocity error (COR = 1)", ballVelocityV.length(), newVelocityV3d.length(), tolerance);
        assertTrue("Ball-Line reflection x velocity error (COR = 1)", Math.abs(newVelocityV3d.x - ballVelocityV.x) < tolerance);
        assertTrue("Ball-Line reflection y velocity error (COR = 1)", Math.abs(newVelocityV3d.y + ballVelocityV.y) < tolerance);
        assertTrue("Ball-Line reflection z velocity error (COR = 1)", Math.abs(newVelocityV3d.z - ballVelocityV.z) < tolerance);

        t = Geometry.timeUntilBallCylinderCollision(tsbccBall2, aCylinder, ballVelocity); // ball would collide if not segment
        assertEquals("tsbccBall2-aCylinder collision time is wrong!", Double.POSITIVE_INFINITY, t, tolerance);
        Cylinder tsbccCylinder2 = new Cylinder(1.0, 3.5, 0.0, -1.0, 3.5, 2.0, 1.0, "tsbccCylinder2");
        t = Geometry.timeUntilBallCylinderCollision(tsbccBall, tsbccCylinder2, ballVelocity); // ball right angle to cylinder
        assertEquals("tsbccBall-aCylinder collision time is wrong!", 4.0, t, tolerance);

        deltaT3D = new Transform3D(ballVelocity);
        deltaT3D.mul(t);
        deltaT3D.get(deltaV3d);
        Sphere tsbccBall5 = new Sphere(tsbccBall.getCenter().x + deltaV3d.x, tsbccBall.getCenter().y + deltaV3d.y, tsbccBall.getCenter().z + deltaV3d.z, tsbccBall.getRadius(), "tsbccBall5");
        newVelocityT3D = Geometry.reflectBallFromCylinder(tsbccBall5, aCylinder, ballVelocity);
        newVelocityT3D.get(newVelocityV3d);
        vel = newVelocityV3d.length();
        assertTrue("Ball-Cylinder reflection velocity error (COR = 1)", Math.abs(newVelocityV3d.length() - ballVelocityV.length()) < tolerance);
        assertTrue("Ball-Cylinder reflection x velocity error (COR = 1)", Math.abs(newVelocityV3d.x - ballVelocityV.x) < tolerance);
        assertTrue("Ball-Cylinder reflection y velocity error (COR = 1)", Math.abs(newVelocityV3d.y + ballVelocityV.y) < tolerance);
        assertTrue("Ball-Cylinder reflection z velocity error (COR = 1)", Math.abs(newVelocityV3d.z - ballVelocityV.z) < tolerance);

        Sphere tsbccBall3 = new Sphere(3.0, 0.0, -2.0, 0.5, "tsbccBall3");
        Transform3D ballVelocity2 = new Transform3D();
        Vector3d ballVelocityV2 = new Vector3d(-0.5, 0.5, 0.5);
        ballVelocity2.set(ballVelocityV2);
        t = Geometry.timeUntilBallCylinderCollision(tsbccBall3, aCylinder, ballVelocity2); // ball at angle
        assertEquals("tsbccBall3-aCylinder collision time is wrong!", 6.0, t, tolerance);

        deltaT3D = new Transform3D(ballVelocity2);
        deltaT3D.mul(t);
        deltaT3D.get(deltaV3d);
        Sphere tsbccBall6 = new Sphere(tsbccBall3.getCenter().x + deltaV3d.x, tsbccBall3.getCenter().y + deltaV3d.y, tsbccBall3.getCenter().z + deltaV3d.z, tsbccBall3.getRadius(), "tsbccBall6");
        newVelocityT3D = Geometry.reflectBallFromCylinder(tsbccBall6, aCylinder, ballVelocity2);
        newVelocityT3D.get(newVelocityV3d);
        vel = newVelocityV3d.length();
        assertEquals("Angled Ball-Cylinder reflection velocity error (COR = 1)", ballVelocityV2.length(), newVelocityV3d.length(), tolerance);
        assertEquals("Angled Ball-Cylinder reflection x velocity error (COR = 1)", ballVelocityV2.x, newVelocityV3d.x, tolerance);
        assertEquals("Angled Ball-Cylinder reflection y velocity error (COR = 1)", -ballVelocityV2.y, newVelocityV3d.y, tolerance);
        assertEquals("Angled Ball-Cylinder reflection z velocity error (COR = 1)", ballVelocityV2.z, newVelocityV3d.z, tolerance);
        
        
        Sphere tsbccBall7 = new Sphere(3.0, -1.0, -5.0, 0.5, "tsbccBall7");
        Transform3D ballVelocity3 = new Transform3D();
        Vector3d ballVelocityV3 = new Vector3d(1.5, 0.0, 0.0);
        ballVelocity3.set(ballVelocityV3);
        Cylinder tsbccCylinder3 = new Cylinder(7.0, -1.1, -4.0, 7.0, -0.9, -4.0, (Math.sqrt(2.0)-0.5), "tsbccCylinder3");

        t = Geometry.timeUntilBallCylinderCollision(tsbccBall7, tsbccCylinder3, ballVelocity3); // ball glancing
        assertEquals("tsbccBall7-tsbccCylinder3 collision time is wrong!", 2.0, t, tolerance);

        deltaT3D = new Transform3D(ballVelocity3);
        deltaT3D.mul(t);
        deltaT3D.get(deltaV3d);
        Sphere tsbccBall8 = new Sphere(tsbccBall7.getCenter().x + deltaV3d.x, tsbccBall7.getCenter().y + deltaV3d.y, tsbccBall7.getCenter().z + deltaV3d.z, tsbccBall7.getRadius(), "tsbccBall8");
        newVelocityT3D = Geometry.reflectBallFromCylinder(tsbccBall8, tsbccCylinder3, ballVelocity3);
        newVelocityT3D.get(newVelocityV3d);
        vel = newVelocityV3d.length();
        assertEquals("Glancing Ball-Cylinder reflection velocity error (COR = 1)", ballVelocityV3.length(), newVelocityV3d.length(), tolerance);
        assertEquals("Glancing Ball-Cylinder reflection x velocity error (COR = 1)", 0.0, newVelocityV3d.x, tolerance);
        assertEquals("Glancing Ball-Cylinder reflection y velocity error (COR = 1)", ballVelocityV3.y, newVelocityV3d.y, tolerance);
        assertEquals("Glancing Ball-Cylinder reflection z velocity error (COR = 1)", -ballVelocityV3.x, newVelocityV3d.z, tolerance);  


        Sphere tsbccBall9 = new Sphere(3.0, -2.0, -5.0, 0.5, "tsbccBall9");
        Transform3D ballVelocity4 = new Transform3D();
        Vector3d ballVelocityV4 = new Vector3d(1.5, 0.5, 0.0);
        ballVelocity4.set(ballVelocityV4);

        t = Geometry.timeUntilBallCylinderCollision(tsbccBall9, tsbccCylinder3, ballVelocity4); // ball angled and glancing (whew!)
        assertEquals("tsbccBall9-tsbccCylinder3 collision time is wrong!", 2.0, t, tolerance);

        deltaT3D = new Transform3D(ballVelocity4);
        deltaT3D.mul(t);
        deltaT3D.get(deltaV3d);
        Sphere tsbccBall10 = new Sphere(tsbccBall9.getCenter().x + deltaV3d.x, tsbccBall9.getCenter().y + deltaV3d.y, tsbccBall9.getCenter().z + deltaV3d.z, tsbccBall8.getRadius(), "tsbccBall10");
        newVelocityT3D = Geometry.reflectBallFromCylinder(tsbccBall10, tsbccCylinder3, ballVelocity4);
        newVelocityT3D.get(newVelocityV3d);
        vel = newVelocityV3d.length();
        assertEquals("Angled Glancing Ball-Cylinder reflection velocity error (COR = 1)", ballVelocityV4.length(), newVelocityV3d.length(), tolerance);
        assertEquals("Angled Glancing Ball-Cylinder reflection x velocity error (COR = 1)", 0.0, newVelocityV3d.x, tolerance);
        assertEquals("Angled Glancing Ball-Cylinder reflection y velocity error (COR = 1)", ballVelocityV4.y, newVelocityV3d.y, tolerance);
        assertEquals("Angled Glancing Ball-Cylinder reflection z velocity error (COR = 1)", -ballVelocityV4.x, newVelocityV3d.z, tolerance);  

        Sphere tsbccBall11 = new Sphere(0.5, 1.5, 0.5, 0.25, "tsbccBall11");
        Transform3D ballVelocity5 = new Transform3D();
        Vector3d ballVelocityV5 = new Vector3d(0, -12.207, 0.0);
        ballVelocity5.set(ballVelocityV5);
        Cylinder tsbccCylinder4 = new Cylinder(1.0, 0.0, 1.0, 1.0, 1.0, 1.0, 0.0, "tsbccCylinder4");

        t = Geometry.timeUntilBallCylinderCollision(tsbccBall11, tsbccCylinder4, ballVelocity5); // ball down to cube edge
        assertEquals("tsbccBall11-tsbccCylinder4 collision time is wrong!", Double.POSITIVE_INFINITY, t, tolerance);
        
        Sphere tsbccBall12 = new Sphere(0.5, 0.25, 1.0, 0.25, "tsbccBall12");
        Transform3D ballVelocity6 = new Transform3D();
        Vector3d ballVelocityV6 = new Vector3d(0.0, 0.3, 0.0);
        ballVelocity6.set(ballVelocityV6);
        Cylinder tsbccCylinder5 = new Cylinder(0.0, 0.0, 1.0, 1.0, 0.0, 1.0, 0.0, "tsbccCylinder5");

        t = Geometry.timeUntilBallCylinderCollision(tsbccBall12, tsbccCylinder5, ballVelocity6); // ball just touching, heading away
        assertEquals("tsbccBall12-tsbccCylinder5 collision time is wrong!", Double.POSITIVE_INFINITY, t, tolerance);

        Sphere tsbccBall13 = new Sphere(0.5, 0.2, 1.0, 0.25, "tsbccBall13");
        t = Geometry.timeUntilBallCylinderCollision(tsbccBall13, tsbccCylinder5, ballVelocity6); // ball overlapping, heading away
        assertEquals("tsbccBall13-tsbccCylinder5 collision time is wrong!", Double.POSITIVE_INFINITY, t, tolerance);

        Transform3D ballVelocity7 = new Transform3D();
        Vector3d ballVelocityV7 = new Vector3d(0.0, -0.3, 0.0);
        ballVelocity7.set(ballVelocityV7);

        t = Geometry.timeUntilBallCylinderCollision(tsbccBall12, tsbccCylinder5, ballVelocity7); // ball just touching, heading in
        assertEquals("tsbccBall12-tsbccCylinder5 collision time is wrong!", 0.0, t, tolerance);

        t = Geometry.timeUntilBallCylinderCollision(tsbccBall13, tsbccCylinder5, ballVelocity7); // ball overlapping, heading in
        assertEquals("tsbccBall13-tsbccCylinder5 collision time is wrong!", 0.0, t, tolerance);
    }

    public void testSimpleBallBallCollision()
    {
        Geometry.showDebugInfo = false;

        Sphere tsbbcBallA = new Sphere (1.0, 3.0, 2.0, 0.5, "tsbbcBallA");
        Sphere tsbbcBallB = new Sphere (1.0, 3.0, 5.0, 0.5, "tsbbcBallB");
        Transform3D aBallVelocity = new Transform3D();
        Vector3d aBallVelocityV = new Vector3d(0.0, 0.0, 0.6);
        aBallVelocity.set(aBallVelocityV);
        Transform3D bBallVelocity = new Transform3D();
        Vector3d bBallVelocityV = new Vector3d(0.0, 0.0, -1.4);
        bBallVelocity.set(bBallVelocityV);
        double t = Geometry.timeUntilBallBallCollision(tsbbcBallA, bBallVelocity, tsbbcBallB, aBallVelocity);
        assertEquals("Ball-Ball collision time is wrong!", Double.POSITIVE_INFINITY, t, tolerance);	// should miss
        t = Geometry.timeUntilBallBallCollision(tsbbcBallA, aBallVelocity, tsbbcBallB, bBallVelocity);
        assertEquals("Ball-Ball collision time is wrong!", 1.0, t, tolerance);				// head-on
        Transform3D deltaT3DA = new Transform3D(aBallVelocity);
        deltaT3DA.mul(t);
        Vector3d deltaV3dA = new Vector3d();
        deltaT3DA.get(deltaV3dA);
        Transform3D deltaT3DB = new Transform3D(bBallVelocity);
        deltaT3DB.mul(t);
        Vector3d deltaV3dB = new Vector3d();
        deltaT3DB.get(deltaV3dB);
        Sphere tsbbcBallA2 = new Sphere(tsbbcBallA.getCenter().x + deltaV3dA.x, tsbbcBallA.getCenter().y + deltaV3dA.y, tsbbcBallA.getCenter().z + deltaV3dA.z, tsbbcBallA.getRadius(), "tsbbcBallA2");
        Sphere tsbbcBallB2 = new Sphere(tsbbcBallB.getCenter().x + deltaV3dB.x, tsbbcBallB.getCenter().y + deltaV3dB.y, tsbbcBallB.getCenter().z + deltaV3dB.z, tsbbcBallB.getRadius(), "tsbbcBallB2");
        Trans3DPair t3dp = Geometry.reflectBalls(tsbbcBallA2, 1.0, aBallVelocity, tsbbcBallB2, 1.0, bBallVelocity);
        Transform3D newVelocityT3DA = t3dp.t1();
        Vector3d newVelocityV3dA = new Vector3d();
        newVelocityT3DA.get(newVelocityV3dA);
        Transform3D newVelocityT3DB = t3dp.t2();
        Vector3d newVelocityV3dB = new Vector3d();
        newVelocityT3DB.get(newVelocityV3dB);

        Vector3d p1i = new Vector3d(aBallVelocityV);
        Vector3d p2i = new Vector3d(bBallVelocityV);
        Vector3d p1f = new Vector3d(newVelocityV3dA);
        Vector3d p2f = new Vector3d(newVelocityV3dB);
        Vector3d pi = new Vector3d();
        Vector3d pf = new Vector3d();
        pi.add(p1i,p2i);
        pf.add(p1f,p2f);

        assertEquals("BallAB momentum not conserved",  pi.length(), pf.length(), tolerance);
        assertEquals("Ball 1 reflection x velocity error", bBallVelocityV.x, newVelocityV3dA.x ,  tolerance);
        assertEquals("Ball 1 reflection y velocity error", bBallVelocityV.y, newVelocityV3dA.y, tolerance);
        assertEquals("Ball 1 reflection z velocity error", bBallVelocityV.z, newVelocityV3dA.z, tolerance);
        assertEquals("Ball 2 reflection x velocity error", aBallVelocityV.x, newVelocityV3dB.x ,  tolerance);
        assertEquals("Ball 2 reflection y velocity error", aBallVelocityV.y, newVelocityV3dB.y, tolerance);
        assertEquals("Ball 2 reflection z velocity error", aBallVelocityV.z, newVelocityV3dB.z, tolerance);

        Sphere tsbbcBallC = new Sphere (1.0 + 1.0/Math.sqrt(2), 3.0, 4.0 + 1.0/Math.sqrt(2), 0.5, "tsbbcBallC");
        t = Geometry.timeUntilBallBallCollision(tsbbcBallA, aBallVelocity, tsbbcBallC, bBallVelocity);
        assertEquals("Ball-Ball collision time is wrong!", 1.0, t, tolerance);				// head-on
        deltaT3DA = new Transform3D(aBallVelocity);
        deltaT3DA.mul(t);
        deltaV3dA = new Vector3d();
        deltaT3DA.get(deltaV3dA);
        Transform3D deltaT3DC = new Transform3D(bBallVelocity);
        deltaT3DC.mul(t);
        Vector3d deltaV3dC = new Vector3d();
        deltaT3DC.get(deltaV3dC);
        Sphere tsbbcBallA3 = new Sphere(tsbbcBallA.getCenter().x + deltaV3dA.x, tsbbcBallA.getCenter().y + deltaV3dA.y, tsbbcBallA.getCenter().z + deltaV3dA.z, tsbbcBallA.getRadius(), "tsbbcBallA3");
        Sphere tsbbcBallC2 = new Sphere(tsbbcBallC.getCenter().x + deltaV3dC.x, tsbbcBallC.getCenter().y + deltaV3dC.y, tsbbcBallC.getCenter().z + deltaV3dC.z, tsbbcBallC.getRadius(), "tsbbcBallC2");
        t3dp = Geometry.reflectBalls(tsbbcBallA3, 1.0, aBallVelocity, tsbbcBallC2, 1.0, bBallVelocity);
        newVelocityT3DA = t3dp.t1();
        newVelocityV3dA = new Vector3d();
        newVelocityT3DA.get(newVelocityV3dA);
        Transform3D newVelocityT3DC = t3dp.t2();
        Vector3d newVelocityV3dC = new Vector3d();
        newVelocityT3DC.get(newVelocityV3dC);

        p1i = new Vector3d(aBallVelocityV);
        p2i = new Vector3d(bBallVelocityV);
        p1f = new Vector3d(newVelocityV3dA);
        p2f = new Vector3d(newVelocityV3dC);
        pi.add(p1i,p2i);
        pf.add(p1f,p2f);

        assertEquals("BallAC momentum not conserved",  pi.length(), pf.length(), tolerance);
        /*
        // I'm not sure what the answer should be. These seem reasonable, but because I didn't calculate them by hand, they're commented out. This can eventually be checked against 6.170 physics
        // My code claims the resulting vectors are -1,0,-.4 and 1,0,-.4.
        assertEquals("2D Glancing Ball 1 reflection x velocity error", -bBallVelocityV.z, newVelocityV3dA.x ,  tolerance);
        assertEquals("2D Glancing Ball 1 reflection y velocity error", bBallVelocityV.y, newVelocityV3dA.y, tolerance);
        assertEquals("2D Glancing Ball 1 reflection z velocity error", bBallVelocityV.x, newVelocityV3dA.z, tolerance);
        assertEquals("2D Glancing Ball 2 reflection x velocity error", -aBallVelocityV.z, newVelocityV3dC.x ,  tolerance);
        assertEquals("2D Glancing Ball 2 reflection y velocity error", aBallVelocityV.y, newVelocityV3dC.y, tolerance);
        assertEquals("2D Glancing Ball 2 reflection z velocity error", aBallVelocityV.x, newVelocityV3dC.z, tolerance);
        */

        Sphere tsbbcBallD = new Sphere (1.0 + 1.0/Math.sqrt(3), 3.0 + 1.0/Math.sqrt(3), 4.0 + 1.0/Math.sqrt(3), 0.5, "tsbbcBallD");
        t = Geometry.timeUntilBallBallCollision(tsbbcBallA, aBallVelocity, tsbbcBallD, bBallVelocity);
        assertEquals("Ball-Ball collision time is wrong!", 1.0, t, tolerance);				// head-on
        deltaT3DA = new Transform3D(aBallVelocity);
        deltaT3DA.mul(t);
        deltaV3dA = new Vector3d();
        deltaT3DA.get(deltaV3dA);
        Transform3D deltaT3DD = new Transform3D(bBallVelocity);
        deltaT3DD.mul(t);
        Vector3d deltaV3dD = new Vector3d();
        deltaT3DD.get(deltaV3dD);
        Sphere tsbbcBallA4 = new Sphere(tsbbcBallA.getCenter().x + deltaV3dA.x, tsbbcBallA.getCenter().y + deltaV3dA.y, tsbbcBallA.getCenter().z + deltaV3dA.z, tsbbcBallA.getRadius(), "tsbbcBallA4");
        Sphere tsbbcBallD2 = new Sphere(tsbbcBallD.getCenter().x + deltaV3dD.x, tsbbcBallD.getCenter().y + deltaV3dD.y, tsbbcBallD.getCenter().z + deltaV3dD.z, tsbbcBallD.getRadius(), "tsbbcBallD2");
        t3dp = Geometry.reflectBalls(tsbbcBallA4, 2.0, aBallVelocity, tsbbcBallD2, 1.0, bBallVelocity);
        newVelocityT3DA = t3dp.t1();
        newVelocityV3dA = new Vector3d();
        newVelocityT3DA.get(newVelocityV3dA);
        Transform3D newVelocityT3DD = t3dp.t2();
        Vector3d newVelocityV3dD = new Vector3d();
        newVelocityT3DD.get(newVelocityV3dD);

        p1i = new Vector3d(aBallVelocityV);
        p2i = new Vector3d(bBallVelocityV);
        p1f = new Vector3d(newVelocityV3dA);
        p2f = new Vector3d(newVelocityV3dD);
        p1i.scale(2.0);
        p1f.scale(2.0);
        pi.add(p1i,p2i);
        pf.add(p1f,p2f);

        assertEquals("BallAD momentum not conserved,",  pi.length(), pf.length(), tolerance);
        /*
        // I'm not sure what the answer should be. These seem reasonable, but because I didn't calculate them by hand, they're commented out
        // My code claims the resultant vectors are -2/3, -2/3, -2/30 and 2/3, 2/3, 22/30 for the equal mass case
        // My code claims the resultant vectors are -4/9, -4/9, -7/45 and 8/9, 8/9, 23/45 for the case where mass1 = 2 * mass2
        assertEquals("3D Glancing Ball 1 reflection x velocity error", -bBallVelocityV.z, newVelocityV3dA.x ,  tolerance);
        assertEquals("3D Glancing Ball 1 reflection y velocity error", bBallVelocityV.y, newVelocityV3dA.y, tolerance);
        assertEquals("3D Glancing Ball 1 reflection z velocity error", bBallVelocityV.x, newVelocityV3dA.z, tolerance);
        assertEquals("3D Glancing Ball 2 reflection x velocity error", -aBallVelocityV.z, newVelocityV3dC.x ,  tolerance);
        assertEquals("3D Glancing Ball 2 reflection y velocity error", aBallVelocityV.y, newVelocityV3dC.y, tolerance);
        assertEquals("3D Glancing Ball 2 reflection z velocity error", aBallVelocityV.x, newVelocityV3dC.z, tolerance);
        */

        Sphere tsbbcBallE = new Sphere(1.0, 3.0, 1.95, 0.5, "tsbbcBallE");
        t = Geometry.timeUntilBallBallCollision(tsbbcBallA, aBallVelocity, tsbbcBallE, bBallVelocity);
        assertEquals("Ball-Ball overlapping collision time is wrong!", Double.POSITIVE_INFINITY, t, tolerance);				// overlapping, moving apart
        t = Geometry.timeUntilBallBallCollision(tsbbcBallA, bBallVelocity, tsbbcBallE, aBallVelocity);
        assertEquals("Ball-Ball overlapping collision time is wrong!", 0.0, t, tolerance);	// overlapping, moving together
    }

    public void testBallRotatingSphereCollision()
    {
        Geometry.showDebugInfo = false;
        Geometry.setTuningParameters(6.0, 900);
        Sphere tbrscBall1 = new Sphere(1.0, 1.5, 1.25, 0.25, "tbrscBall1");
        Sphere tbrscBall2 = new Sphere(1.0, -1.75, 2.0, 0.25, "tbrscBall2");
        Transform3D ballVelocity1 = new Transform3D();
        Vector3d ballVelocityV1 = new Vector3d(0.0, -0.5, 0.0);
        ballVelocity1.set(ballVelocityV1);
        Cylinder aor1 = new Cylinder(0.0, -1.0, 2.0, 1.0, -1.0, 2.0, 0.0, "aor1");
        double t = Geometry.timeUntilBallRotatingSphereCollision(tbrscBall1, ballVelocity1, tbrscBall2, aor1, Math.PI/8.0);
        assertEquals("Ball-Rotating Sphere collision time is wrong!", 4.0, t, tolerance);

        Vector3d deltaV3d1 = new Vector3d();
        ballVelocity1.get(deltaV3d1);
        deltaV3d1.scale(t);
        Sphere tbrscBall3 = new Sphere(tbrscBall1.getCenter().x + deltaV3d1.x, tbrscBall1.getCenter().y + deltaV3d1.y, tbrscBall1.getCenter().z + deltaV3d1.z, tbrscBall1.getRadius(), "tbrscBall3");
        Sphere tbrscBall4 = Geometry.rotateSphereAboutLine(tbrscBall2, aor1, Math.PI/2.0);
        
        Transform3D ballVelocity1R = Geometry.reflectBallFromRotatingSphere(tbrscBall3, ballVelocity1, tbrscBall4, aor1, Math.PI/8.0);
        Vector3d newVelocityV31 = new Vector3d();
        ballVelocity1R.get(newVelocityV31);
//        System.out.println("Resultant velocity: " + newVelocityV31);
            // Geometry claims the resultant velocity is (0.0, 1.0890486225480862, 0.0)
// I'm not sure what the answer should be. These seem reasonable, but because I didn't calculate them by hand, they're commented out
//        assertEquals("Ball-Rotating Sphere reflection velocity is wrong!", ???, newVelocityV31.length(), tolerance);
        assertEquals("Ball-Rotating Sphere reflection x velocity is wrong!", ballVelocityV1.x, newVelocityV31.x, tolerance);
//        assertEquals("Ball-Rotating Sphere reflection y velocity is wrong!", ???, newVelocityV31.y, tolerance);
//        assertEquals("Ball-Rotating Sphere reflection z velocity is wrong!", ???, newVelocityV31.z, tolerance);

        Sphere tbrscBall5 = new Sphere(1.0, 1.5, 2.0, 0.25, "tbrscBall5");
        t = Geometry.timeUntilBallRotatingSphereCollision(tbrscBall5, ballVelocity1, tbrscBall2, aor1, Math.PI/8.0);
        assertEquals("Ball-Rotating Sphere collision time is wrong!", Double.POSITIVE_INFINITY, t, tolerance);

        Sphere tbrscBall6 = new Sphere(1.0, -1.0, -3.25, 0.25, "tbrscBall6");
        Transform3D ballVelocity2 = new Transform3D();
        Vector3d ballVelocityV2 = new Vector3d(0.0, 0.0, 1.0);
        ballVelocity2.set(ballVelocityV2);
        t = Geometry.timeUntilBallRotatingSphereCollision(tbrscBall6, ballVelocity2, tbrscBall2, aor1, Math.PI/8.0);
        assertEquals("Ball-Rotating Sphere collision time is wrong!", 4.0, t, tolerance);

        Vector3d deltaV3d2 = new Vector3d();
        ballVelocity2.get(deltaV3d2);
        deltaV3d2.scale(t);
        Sphere tbrscBall7 = new Sphere(tbrscBall6.getCenter().x + deltaV3d2.x, tbrscBall6.getCenter().y + deltaV3d2.y, tbrscBall6.getCenter().z + deltaV3d2.z, tbrscBall6.getRadius(), "tbrscBall7");
        Sphere tbrscBall8 = Geometry.rotateSphereAboutLine(tbrscBall2, aor1, Math.PI/2.0);
       
        Transform3D ballVelocity2R = Geometry.reflectBallFromRotatingSphere(tbrscBall6, ballVelocity2, tbrscBall8, aor1, Math.PI/8.0);
        Vector3d newVelocityV32 = new Vector3d();
        ballVelocity2R.get(newVelocityV32);
//        System.out.println("Resultant velocity: " + newVelocityV32);

        assertEquals("Ball-Rotating Sphere perpendicular reflection velocity is wrong!", ballVelocityV2.length(), newVelocityV32.length(), tolerance);
        assertEquals("Ball-Rotating Sphere perpendicular reflection x velocity is wrong!", ballVelocityV2.x, newVelocityV32.x, tolerance);
        assertEquals("Ball-Rotating Sphere perpendicular reflection y velocity is wrong!", ballVelocityV2.y, newVelocityV32.y, tolerance);
        assertEquals("Ball-Rotating Sphere perpendicular reflection z velocity is wrong!", -ballVelocityV2.z, newVelocityV32.z, tolerance);

        Sphere tbrscBall9 = new Sphere(-2.0, -0.5, 1.25, 0.25, "tbrscBall9");
        Transform3D ballVelocity3 = new Transform3D();
        Vector3d ballVelocityV3 = new Vector3d(0.75, 0.0, 0.0);
        ballVelocity3.set(ballVelocityV3);
        Sphere tbrscBall10 = new Sphere(1.0, -0.25, 2.0, 0.25, "tbrscBall10");
        t = Geometry.timeUntilBallRotatingSphereCollision(tbrscBall9, ballVelocity3, tbrscBall10, aor1, 3*Math.PI/8.0);
        assertEquals("Ball-Rotating Sphere collision time is wrong!", 4.0, t, tolerance);

        Vector3d deltaV3d3 = new Vector3d();
        ballVelocity3.get(deltaV3d3);
        deltaV3d3.scale(t);
        Sphere tbrscBall11 = new Sphere(tbrscBall9.getCenter().x + deltaV3d3.x, tbrscBall9.getCenter().y + deltaV3d3.y, tbrscBall9.getCenter().z + deltaV3d3.z, tbrscBall9.getRadius(), "tbrscBall11");
        Sphere tbrscBall12 = Geometry.rotateSphereAboutLine(tbrscBall10, aor1, 3*Math.PI/2.0);

        Transform3D ballVelocity3R = Geometry.reflectBallFromRotatingSphere(tbrscBall11, ballVelocity3, tbrscBall12, aor1, 3*Math.PI/8.0);
        Vector3d newVelocityV33 = new Vector3d();
        ballVelocity3R.get(newVelocityV33);
//        System.out.println("Resultant velocity: " + newVelocityV33);
        // Geometry claims the resultant velocity is (0.75, 1.7671458676442584, 0.0)
// I'm not sure what the answer should be. These seem reasonable, but because I didn't calculate them by hand, they're commented out        
//        assertEquals("Ball-Rotating Sphere perpendicular reflection velocity is wrong!", ???, newVelocityV33.length(), tolerance);
        assertEquals("Ball-Rotating Sphere perpendicular reflection x velocity is wrong!", ballVelocityV3.x, newVelocityV33.x, tolerance);
//        assertEquals("Ball-Rotating Sphere perpendicular reflection y velocity is wrong!", ballVelocityV3.y, newVelocityV33.y, tolerance);
//        assertEquals("Ball-Rotating Sphere perpendicular reflection z velocity is wrong!", -ballVelocityV3.z, newVelocityV33.z, tolerance);

        Geometry.resetTuningParameters();
    }

    public void testBallRotatingCylinderCollision()
    {
        Geometry.showDebugInfo = false;
        Geometry.setTuningParameters(6.0, 900);
        
        Sphere tbrccBall1 = new Sphere(0.5, 1.5, 2.0, 0.25, "tbrccBall1");
        Cylinder aor1 = new Cylinder(0.0, -1.0, 2.0, 1.0, -1.0, 2.0, 0.0, "aor1");
        Cylinder tbrccCyl1 = new Cylinder(0.0, -1.0, 2.0, 1.0, -1.0, 2.0, 0.0, "tbrccCyl1");
        Transform3D ballVelocity1 = new Transform3D();
        Vector3d ballVelocityV1 = new Vector3d(0.0, -0.5, 0.0);
        ballVelocity1.set(ballVelocityV1);
        double t = Geometry.timeUntilBallRotatingCylinderCollision(tbrccBall1, ballVelocity1, tbrccCyl1, aor1, Math.PI/8.0); // rotating about own axis
        assertEquals("Ball-Rotating Cylinder collision time is wrong!", 4.5, t, tolerance);

        Sphere tbrccBall2 = new Sphere(0.25, 1.0, 1.0, 0.25, "tbrccBall2");
        Cylinder tbrccCyl2 = new Cylinder(0.0, -1.0, 2.0, 0.0, -3.0, 2.0, 0.0, "tbrccCyl2");
        Transform3D ballVelocity2 = new Transform3D();
        Vector3d ballVelocityV2 = new Vector3d(0.0, -0.5, 0.0);
        ballVelocity2.set(ballVelocityV2);
        t = Geometry.timeUntilBallRotatingCylinderCollision(tbrccBall2, ballVelocity2, tbrccCyl2, aor1, Math.PI/7.0); // head-on collision
        assertEquals("Ball-Rotating Cylinder collision time is wrong!", Double.POSITIVE_INFINITY, t, tolerance);
        Sphere tbrccBallA = new Sphere(0.0, 1.0, -.5, 0.25, "tbrccBallA");
        t = Geometry.timeUntilBallRotatingCylinderCollision(tbrccBallA, ballVelocity2, tbrccCyl2, aor1, Math.PI/7.0); // whiff on the end
        assertEquals("Ball-Rotating Cylinder collision time is wrong!", Double.POSITIVE_INFINITY, t, tolerance);
        Sphere tbrccBall3 = new Sphere(0.0, 1.0, 1.0, 0.25, "tbrccBall3");
        t = Geometry.timeUntilBallRotatingCylinderCollision(tbrccBall3, ballVelocity2, tbrccCyl2, aor1, Math.PI/7.0); // whiff on the side
        assertEquals("Ball-Rotating Cylinder collision time is wrong!", 3.5, t, tolerance);

        Vector3d deltaV3d1 = new Vector3d();
        ballVelocity2.get(deltaV3d1);
        deltaV3d1.scale(t);
        Sphere tbrccBall4 = new Sphere(tbrccBall3.getCenter().x + deltaV3d1.x, tbrccBall3.getCenter().y + deltaV3d1.y, tbrccBall3.getCenter().z + deltaV3d1.z, tbrccBall3.getRadius(), "tbrccBall4");
        Cylinder tbrccCyl3 = Geometry.rotateCylinderAboutLine(tbrccCyl2, aor1, Math.PI/7.0*t);
        
        Transform3D ballVelocity1R = Geometry.reflectBallFromRotatingCylinder(tbrccBall4, ballVelocity2, tbrccCyl3, aor1, Math.PI/7.0);
        Vector3d newVelocityV1 = new Vector3d();
        ballVelocity1R.get(newVelocityV1);
//        System.out.println("Resultant velocity: " + newVelocityV1);
            // Geometry claims the resultant velocity is (-4.647774391119609E-16, 1.3975979010256552, 0.0)
// I'm not sure what the answer should be. These seem reasonable, but because I didn't calculate them by hand, they're commented out
//        assertEquals("Ball-Rotating Cylinder reflection velocity is wrong!", ???, newVelocityV1.length(), tolerance);
        assertEquals("Ball-Rotating Cylinder reflection x velocity is wrong!", ballVelocityV2.x, newVelocityV1.x, tolerance);
//        assertEquals("Ball-Rotating Cylinder reflection y velocity is wrong!", ???, newVelocityV1.y, tolerance);
        assertEquals("Ball-Rotating Cylinder reflection z velocity is wrong!", ballVelocityV2.z, newVelocityV1.z, tolerance);

        Sphere tbrccBall5 = new Sphere(2.0, -0.75, 1.0, 0.25, "tbrccBall5");
        Transform3D ballVelocity3 = new Transform3D();
        Vector3d ballVelocityV3 = new Vector3d(-0.5, 0.0, 0.0);
        ballVelocity3.set(ballVelocityV3);
        t = Geometry.timeUntilBallRotatingCylinderCollision(tbrccBall5, ballVelocity3, tbrccCyl2, aor1, Math.PI/8.0); // head-on collision
        assertEquals("Ball-Rotating Cylinder collision time is wrong!", 4.0, t, tolerance);

        deltaV3d1 = new Vector3d();
        ballVelocity3.get(deltaV3d1);
        deltaV3d1.scale(t);
        Sphere tbrccBall6 = new Sphere(tbrccBall5.getCenter().x + deltaV3d1.x, tbrccBall5.getCenter().y + deltaV3d1.y, tbrccBall5.getCenter().z + deltaV3d1.z, tbrccBall5.getRadius(), "tbrccBall6");
        Cylinder tbrccCyl4 = Geometry.rotateCylinderAboutLine(tbrccCyl2, aor1, Math.PI/8.0*t);
        
        Transform3D ballVelocity2R = Geometry.reflectBallFromRotatingCylinder(tbrccBall6, ballVelocity3, tbrccCyl4, aor1, Math.PI/8.0);
        Vector3d newVelocityV2 = new Vector3d();
        ballVelocity2R.get(newVelocityV2);
//        System.out.println("Resultant velocity: " + newVelocityV2);
            // Geometry claims the resultant velocity is (-0.4999999999999995, 0.785398163397449, 0.0)
// I'm not sure what the answer should be. These seem reasonable, but because I didn't calculate them by hand, they're commented out
//        assertEquals("Ball-Rotating Cylinder reflection velocity is wrong!", ???, newVelocityV2.length(), tolerance);
        assertEquals("Ball-Rotating Cylinder reflection x velocity is wrong!", ballVelocityV3.x, newVelocityV2.x, tolerance);
//        assertEquals("Ball-Rotating Cylinder reflection y velocity is wrong!", ???, newVelocityV2.y, tolerance);
        assertEquals("Ball-Rotating Cylinder reflection z velocity is wrong!", ballVelocityV3.z, newVelocityV2.z, tolerance);

        Geometry.resetTuningParameters();
    }

    public void testBallRotatingPlaneSegmentCollision()
    {
        Geometry.showDebugInfo = false;
        Geometry.setTuningParameters(4.0, 600);
        
        Sphere tbrpscBall1 = new Sphere(0.5, 1.0, 1.0, 0.25, "tbrpscBall1");
        Cylinder aor1 = new Cylinder(0.0, -1.0, 2.0, 1.0, -1.0, 2.0, 0.0, "aor1");
        PlaneSegment tbrpscPS1 = new PlaneSegment(0.0, -1.0, 2.0, 1.0, -1.0, 2.0, 0.5, -3.0, 2.0, "tbrpscPS1");
        Transform3D ballVelocity1 = new Transform3D();
        Vector3d ballVelocityV1 = new Vector3d(0.0, -0.5, 0.0);
        ballVelocity1.set(ballVelocityV1);
        double t = Geometry.timeUntilBallRotatingPlaneSegmentCollision(tbrpscBall1, ballVelocity1, tbrpscPS1, aor1, Math.PI/7.0);
        assertEquals("Ball-Rotating Plane Segment collision time is wrong!", 3.5, t, tolerance);

        Vector3d deltaV3d1 = new Vector3d();
        ballVelocity1.get(deltaV3d1);
        deltaV3d1.scale(t);
        Sphere tbrpscBall2 = new Sphere(tbrpscBall1.getCenter().x + deltaV3d1.x, tbrpscBall1.getCenter().y + deltaV3d1.y, tbrpscBall1.getCenter().z + deltaV3d1.z, tbrpscBall1.getRadius(), "tbrpscBall2");
        PlaneSegment tbrpscPS2 = Geometry.rotatePlaneSegmentAboutLine(tbrpscPS1, aor1, Math.PI/7.0*t);
        
        Transform3D ballVelocity1R = Geometry.reflectBallFromRotatingPlaneSegment(tbrpscBall2, ballVelocity1, tbrpscPS2, aor1, Math.PI/7.0);
        Vector3d newVelocityV1 = new Vector3d();
        ballVelocity1R.get(newVelocityV1);
//        System.out.println("Resultant velocity: " + newVelocityV1);
            // Geometry claims the resultant velocity is (-2.1067568811990513E-16, 1.3975979010256552, -5.551115123125783E-17)
// I'm not sure what the answer should be. These seem reasonable, but because I didn't calculate them by hand, they're commented out
//        assertEquals("Ball-Rotating Cylinder reflection velocity is wrong!", ???, newVelocityV1.length(), tolerance);
        assertEquals("Ball-Rotating Cylinder reflection x velocity is wrong!", ballVelocityV1.x, newVelocityV1.x, tolerance);
//        assertEquals("Ball-Rotating Cylinder reflection y velocity is wrong!", ???, newVelocityV1.y, tolerance);
        assertEquals("Ball-Rotating Cylinder reflection z velocity is wrong!", ballVelocityV1.z, newVelocityV1.z, tolerance);

        Geometry.resetTuningParameters();
    }


    public static Test suite()
    {
        TestSuite suite = new TestSuite();
        suite.addTest(new GeometryTest("testTransform"));
        suite.addTest(new GeometryTest("testEquals"));
        suite.addTest(new GeometryTest("testSimpleBallPlaneCollision"));
        suite.addTest(new GeometryTest("testSimpleBallCylinderCollision"));
        suite.addTest(new GeometryTest("testSimpleBallSphereCollision"));
        suite.addTest(new GeometryTest("testSimpleBallBallCollision"));
        suite.addTest(new GeometryTest("testBallRotatingSphereCollision"));
        suite.addTest(new GeometryTest("testBallRotatingCylinderCollision"));
        suite.addTest(new GeometryTest("testBallRotatingPlaneSegmentCollision"));
        return suite;
    }
}
