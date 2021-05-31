//
//  Geometry.java
//  
//
//  Created by Eric Tung on Sun Apr 06 2003.
//

// Search for DO OPTIMIZATION for places where code can be improved

// File history
// 2003/04/11 EGT added Ball/nonmoving Gizmo interaction
// 2003/04/12 EGT added Ball/nonmoving PlaneSegment interaction
// 2003/04/12 EGT caught and fixed bugs in reflectBallFromPlane and timeUntilBallSphereCollision
// 2003/04/13 EGT added Ball/nonmoving Cylinder interaction, fixed bugs in timeUntilBallPlaneCollision
// 2003/04/13 EGT fixed bugs in timeUntilBallCylinderCollision
// 2003/04/13 EGT added Ball/nonmoving Sphere reflection, probably fixed bugs
// 2003/04/14 EGT added Ball/Ball reflection, probably fixed bugs in timeUntilBallBallCollision
// 2003/04/21 EGT fixed bug where a ball in contact with a sphere would return 0.0 sec to collision
// 2003/04/21 EGT corrected ball-* methods to return +Inf when the ball is moving away from the object
// 2003/04/21 EGT added Ball/rotating Sphere interaction
// 2003/04/25 EGT fixed bug where a ball overlapping with a static object and with decreasing distance would return a positive time to collision
// 2003/04/26 EGT added Ball/rotating Cylinder interaction
// 2003/04/26 EGT added Ball/rotating Plane Segment interaction
// 2003/04/27 EGT fixed bug where a ball overlapping with a plane but not with a plane segment would incorrectly return 0.0 to collision.

package physics3d;

// from physics package
import javax.media.j3d.Transform3D;
import javax.vecmath.Tuple3d;
import javax.vecmath.Vector3d;

/****************************************************************************
 * Documentation from physics.Geometry is modified from documentation:
 *
 * Copyright (C) 1999-2001 by the Massachusetts Institute of Technology,
 *                       Cambridge, Massachusetts.
 *
 ***************************************************************************/

// The following documentation is based on the documentation for 
//   physics.Gemoetry, which is Copyright (C) 1999-2001 by the Massachusetts 
//   Institute of Technology, Cambridge, Massachusetts.
// The documentation has been changed to account for 3D geometry.

/**
 * The Geometry library contains procedural abstractions which are useful
 * in modeling the physical interactions between objects.
 *
 * <p>The library is described in terms of these concepts:
 * <br><ul>
 * <li> object  - a ball or a bouncer
 * <li> ball    - a sphere representing a engine.MobileGizmo
 * <li> bouncer - a plane segment, line segment 3D or sphere with
 *      position and angular velocity
 * </ul>
 *
 * <p>
 * The intended use of the Geometry library is as follows:
 *
 * <p><ol><li>
 * The client calls the timeUntilCollision() methods to calculate the
 * times at which the ball(s) will collide with each of the bouncers
 * or with another ball.
 * The minimum of all these times (call it "mintime") is the
 * time of the next collision.
 *
 * <li>
 * The client updates the position of the ball(s) and the bouncers to
 * account for mintime passing.  At this point, the ball and the object
 * it is about to hit are exactly adjacent to one another.
 *
 * <li>
 * The client calls the appropriate reflect() method to calculate the
 * change in the ball's velocity.
 *
 * <li>The client updates the ball's velocity and repeats back to step 1.
 *
 * </ol>
 *
 * <p><a name="constant_velocity"></a>
 *
 * <p>The timeUntilCollision() methods assume constant ball velocity.
 * That is, no force will be acting on the ball, so it will follow a
 * straight-line path.  Therefore, if external forces (such as gravity
 * or friction) need to be accounted for, the client must do so before
 * or after the of the "time until / update position / reflect" series
 * of steps - never inbetween those three steps.
 * 
 * <p><a name="endpoint_effects"></a>
 *
 * <b>Important note</b>:
 * The methods which deal with plane segment bouncers do NOT deal with the edges of 
 * the plane segment, and line segment bouncers do NOT deal with the end-points of 
 * the line segment.  To ensure realistic behavior, shapes should be constructed
 * from a combination of planes with line segments (zero-radius cylinders)
 * at edges and with zero-radius spheres at the end points of line segments.
 *
 * <p>
 * For example: A ball is located at (0,0,0) and is moving in the
 * (1,1,0) direction towards two line segments; one segments spans the
 * points (1,1,0),(1,2,0) and the other spans (1,1,0),(2,1,0).
 * The ball will hit the ends of both line segments at a 45 degree angle and
 * something REALLY WEIRD will happen.  However, if a circle with zero radius
 * is placed at (1,1,0) then the ball will bounce off the circle in the
 * expected manner.
 **/

// end documentation from physics.Geometry

/**
 * In physics3d, the timeUntil* methods are not well-definied for the case 
 *   where a ball is intersecting an object. Please don't do this.
 *
 * Unlike 6.170 physics, physics3d has the weaker spec that it is allowed to
 *   return negative collision times instead of Double.POSITIVE_INFINITY. This
 *   may be changed in the future depending on time constraints.
 *
 * Unlike 6.170 physics, physics3d defaults to maximumForesight = 0.1
 **/

// Spin is not currently accounted for; however, the use of Transform3D allows
//   it to be added to physics without changing any other class.
// This will not be done by me during 6.170.

// Geometry is a singleton.

public class Geometry {

    protected static final double dt = 0.000000001;	// dt to use for the Newton.Functions
    private static final double pipTolerance = 0.0001;	// point-in-Polygon tolerance
    private static final double collinearTolerance = 0.0000001;	// tolerance for collinear points
    protected static boolean showDebugInfo = false;
    private static double maximumForesight = 0.1;	// must be at least 0.0
    private static int searchSlices = 15;		// must be at least 1
    public static void resetTuningParameters()
    {
        maximumForesight = 0.1;
        searchSlices = 15;
    }

    public static void setForesight(double mF) { if(mF >= 0.0) maximumForesight = mF; }
    public static void setTuningParameters(double mF, int nOS)
    {
        if(mF >= 0.0 && nOS > 0)
        {
            maximumForesight = mF;
            searchSlices = nOS;
        }
    }

  /**
   * Accounts for the effects of inelastic collisions given the initial transform
   * and change in transform of the collision assuming elasticity.
   *
   * NOTE: This is DIFFERENT from the 6.170 physics.Geometry.applyReflectionCoeff
   *  which uses initial and resultant transforms, not initial and delta transforms.
   *  Although the 6.170 method is slightly faster in the perfectly elastic case, since it
   *  skips applyReflectionCoefficient, it's easier to calculate the delta than the result,
   *  so I think this is a net win.
   *
   * NOTE: This will MUTATE both input transforms. Because this should normally be the last
   *  thing called, this mutation shouldn't cause problems; if for some reason it's not the
   *  last calculation, be sure to save a copy first!
   *
   * @requires <code>rCoeff</code> >= 0
   *
   * @effects given an initial velocity, <code>incidentVect</code>,
   * and the change in velocity resulting from an elastic collision,
   * <code>deltaTransform</code>, and a reflection coefficient,
   * <code>rCoeff</code>, returns the resulting transform of the
   * collision had it been inelastic with the given reflection
   * coefficient.  If the reflection coefficient is 1.0, the resulting
   * velocity will be equal to <code>incidentTransform + deltaTransform</code>.  A
   * reflection coefficient of 0 implies that the collision will
   * absorb any energy that was reflected in the elastic case.
   *
   * @param incidentTransform the intial velocity of the ball
   * @param deltaTransform the change in velocity after the collision
   * assuming elasticity.
   * @param rCoeff the reflection coefficent.
   *
   * @return the resulting velocity after an inelastic collision.
   **/
    private static Transform3D applyReflectionCoeff(Transform3D inOutTransform, Transform3D deltaTransform, double rCoeff)
    {
        deltaTransform.mul(0.5 + 0.5*rCoeff);
        inOutTransform.add(deltaTransform);
	inOutTransform.setScale(1.0);
        return inOutTransform;
    }


	// reflect methods assume perfectly elastic (COR == 1), non-spinning balls

  /**  
   * Computes the new velocity of a ball after bouncing (reflecting)
   * off of a Sphere.
   *
   * @requires <code>reflectionCoeff</code> >= 0 and the radii of 
   * <code>ball</code> and <code>sph</code> are not both 0
   *
   * @effects computes the new velocity of a ball reflecting off of a
   * Sphere.  The velocity resulting from this method corresponds to a
   * collision against a surface with the given reflection
   * coefficient.  A reflection coefficient of 1 indicates a perfectly
   * elastic collision.  This method assumes that the ball is at the
   * point of impact.
   *
   * @param ball the Sphere representing the ball
   *
   * @param sph the Sphere which is being hit by the ball
   *
   * @param velocity the velocity of the ball before impact
   *
   * @param reflectionCoeff the reflection coefficient
   *
   * @return the velocity of the ball after impacting the given Sphere 
   **/
    public static Transform3D reflectBallFromSphere(Sphere ball, Sphere sph, Transform3D ballVelocity, double sphCOR)
    {
        Vector3d vOut = new Vector3d();
        ballVelocity.get(vOut);
        //if(showDebugInfo) System.err.println("\nBall: " + ball.getCenter() + "\nSphere: " + sph.getCenter() + "\nVel: " + vOut);
        Vector3d deltaV = new Vector3d();
        deltaV.sub(sph.getCenter(),ball.getCenter());
        //if(showDebugInfo) System.err.println("Ball1 center - Ball2 center: " + deltaV);
        
        //if(showDebugInfo) System.err.println("Closest approach: " + deltaV);
        deltaV.normalize();
        deltaV.scale(-2.0*vOut.dot(deltaV));
        //if(showDebugInfo) System.err.println("DeltaV: " + deltaV);
	Transform3D deltaT = new Transform3D();
        deltaT.set(deltaV);
        return applyReflectionCoeff(new Transform3D(ballVelocity), deltaT, sphCOR);
    }

  /**  
   * Computes the new velocity of a ball after bouncing (reflecting)
   * off of a Sphere.
   *
   * @requires the radii of <code>ball</code> and <code>sph</code> are not both 0
   * 
   *
   * @effects computes the new velocity of a ball reflecting off of a
   * Sphere.  The velocity resulting from this method corresponds to a
   * perfectly elastic collision.  This method assumes that the ball
   * is at the point of impact.
   *
   * @param ball the Sphere representing the ball
   *
   * @param sph the Sphere which is being hit by the ball
   *
   * @param velocity the velocity of the ball before impact
   *
   * @return the velocity of the ball after impacting the given Sphere 
   **/
    public static Transform3D reflectBallFromSphere(Sphere ball, Sphere sph, Transform3D ballVelocity)
    {
    	return reflectBallFromSphere(ball, sph, ballVelocity, 1.0);
    }




  /**
   * Computes the new velocity of a ball after bouncing (reflecting)
   * off a Cylinder.
   *
   * @requires <code>cyl</code> has non-zero length and the radii of
   * <code>ball</code> and <code>cyl</code> are not both 0 and
   * <code>reflectionCoeff</code> >= 0
   *
   * @effects computes the new velocity of a ball reflecting off of a
   * Cylinder.  A reflection coefficient of 1 indiciates a
   * perfectly elastic collision.  This method assumes that the ball
   * is at the point of impact.
   *
   * @param Cylinder the Cylinder representing the cylinder which is being hit
   *
   * @param ball the Sphere representing the ball at the point of impact
   *
   * @param velocity the velocity of the ball before impact
   *
   * @param planeCOR the reflection coefficent of the plane.
   *
   * @return the velocity of the ball after impacting the given wall
   **/
    public static Transform3D reflectBallFromCylinder(Sphere ball, Cylinder cyl, Transform3D ballVelocity, double cylCOR)
    {
        Vector3d vOut = new Vector3d();
        ballVelocity.get(vOut);
        //if(showDebugInfo) System.err.println("\nBall: " + ball.getCenter() + "\nCyl: " + cyl + "\nVel: " + vOut);
        Vector3d deltaV = closestApproach(cyl.p1(),cyl.getVector(),ball.getCenter());
        //if(showDebugInfo) System.err.println("Closest approach: " + deltaV);
        deltaV.normalize();
        //if(showDebugInfo) System.err.println("v dot -closestApproach: " + vOut.dot(deltaV));
        deltaV.scale(-2.0*vOut.dot(deltaV));
        //if(showDebugInfo) System.err.println("DeltaV: " + deltaV);
	Transform3D deltaT = new Transform3D();
        deltaT.set(deltaV);
        return applyReflectionCoeff(new Transform3D(ballVelocity), deltaT, cylCOR);
    }

  /**
   * Computes the new velocity of a ball after bouncing (reflecting)
   * off a Cylinder.
   *
   * @requires <code>cyl</code> has non-zero length and the radii of
   * <code>ball</code> and <code>cyl</code> are not both 0
   *
   * @effects computes the new velocity of a ball reflecting off of a
   * Cylinder.  The velocity resulting from this method corresponds to
   * a perfectly elastic collision against a surface.  This method 
   * assumes that the ball is at the point of impact.
   *
   * @param Cylinder the Cylinder representing the cylinder which is being hit
   *
   * @param ball the Sphere representing the ball at the point of impact
   *
   * @param velocity the velocity of the ball before impact
   *
   * @return the velocity of the ball after impacting the given wall
   **/
    public static Transform3D reflectBallFromCylinder(Sphere ball, Cylinder cyl, Transform3D ballVelocity)
    {
        return reflectBallFromCylinder(ball, cyl, ballVelocity, 1.0);
    }

  /**
   * Computes the new velocity of a ball after bouncing (reflecting)
   * off a wall.
   *
   * @requires <code>PlaneSegment</code> has non-zero area,
   * <code>reflectionCoeff</code> >= 0
   *
   * @effects computes the new velocity of a ball reflecting off of a
   * plane segment.  The velocity resulting from this method corresponds to
   * collision against a surface with the given reflection
   * coefficient.  A reflection coefficient of 1 indiciates a
   * perfectly elastic collision.  This method assumes that the ball
   * is at the point of impact and is on the side of the plane that the normal is on.
   *
   * @param pseg the PlaneSegment representing the wall which is being hit
   *
   * @param velocity the velocity of the ball before impact
   *
   * @param planeCOR the reflection coefficent of the plane.
   *
   * @return the velocity of the ball after impacting the given wall
   **/
    public static Transform3D reflectBallFromPlane(PlaneSegment pseg, Transform3D ballVelocity, double planeCOR)
    {
        Vector3d vOut = new Vector3d();
        Vector3d deltaV = pseg.normal();
        ballVelocity.get(vOut);
        //if(showDebugInfo) System.err.println("V: " + vOut);
        if(vOut.dot(deltaV) < 0)
            deltaV.negate();
        deltaV.scale(-2.0*vOut.dot(deltaV));
        //if(showDebugInfo) System.err.println("Delta V: " + deltaV);
	Transform3D deltaT = new Transform3D();
        deltaT.set(deltaV);
        return applyReflectionCoeff(new Transform3D(ballVelocity), deltaT, planeCOR);  
    }

  /**
   * Computes the new velocity of a ball after bouncing (reflecting)
   * off a wall.
   *
   * @requires <code>PlaneSegment</code> has non-zero area
   *
   * @effects computes the new velocity of a ball reflecting off of a
   * plane segment.  The velocity resulting from this method corresponds to
   * a perfectly elastic collision against a surface.  This method assumes 
   * that the ball is at the point of impact and is on the side of the plane 
   * that the normal is on.
   *
   * @param pseg the PlaneSegment representing the wall which is being hit
   *
   * @param velocity the velocity of the ball before impact
   *
   * @return the velocity of the ball after impacting the given wall
   **/
    public static Transform3D reflectBallFromPlane(PlaneSegment pseg, Transform3D velocity)
    {
        return reflectBallFromPlane(pseg, velocity, 1.0);
    }
    
  /**
   * Computes the resulting velocities of two balls which collide, assuming a perfectly elastic collision.
   *
   * @requires mass1 > 0 && mass2 > 0 && the distance between the two
   * balls is approximately equal to the sum of their radii; that is,
   * the balls are positioned at the point of impact.
   *
   * @effects computes the resulting velocities of two balls which
   * collide, assuming a perfectly elastic collision.
   *
   * @param ball1 the Sphere representing the first ball
   *
   * @param mass1 the mass of the first ball
   *
   * @param trans1 the Transform of the first ball before impact
   *
   * @param ball2 the Sphere representing the second ball
   *
   * @param mass2 the mass of the second ball
   *
   * @param trans2 the Transform of the second ball before impact
   *
   * @return a <code>Trans3DPair</code>, where the first <code>Transform3D</code> is
   * the velocity of the first ball after the collision and the second
   * <code>Transform3D</code> is the velocity of the second ball after the collision.
   **/
    public static Trans3DPair reflectBalls(Sphere ball1, double mass1, Transform3D trans1, Sphere ball2, double mass2, Transform3D trans2)
    {
        /* Strategy: Change to the frame of reference of ball1; we can then trivially calculate the resulting relative velocity.
         * 	We also know momentum is conserved, so
         *		m1*v1i + m2*v2i = m1*v1f + m2*v2f
         *	and vRelFinal = v1f - v2f
         *
         *	Then
         *		m1*v1i + m2*v2i = m1*vRelFinal + m1*v2f + m2*v2f
         *	So
         *		(m1*v1i + m2*v2i - m1*vRelFinal)/(m1+m2) = v2f
         *	and
         *		(m1*v1i + m2*v2i - m2*v2f)/m1 = v1f
         *
         *	The only thing I'm not sure about is how spin is handled, but I'm not handling it anyway so it doesn't matter.
         */

        Transform3D rTI = new Transform3D(trans1);	// Relative Transform Initial
        rTI.sub(trans2);
        //if(showDebugInfo) System.err.println("Relative translation: " + rTI);
        Transform3D rTF = reflectBallFromSphere(ball2, ball1, rTI); // Relative Transform Final
        Transform3D t2f = new Transform3D(rTF);
        t2f.mul(-mass1);				// we no longer need rTF, so reuse it for scratch variables.
        rTF = new Transform3D(trans2);
        rTF.mul(mass2);
        t2f.add(rTF);
        rTF = new Transform3D(trans1);
        rTF.mul(mass1);
        t2f.add(rTF);
        t2f.mul(1.0/(mass1+mass2));
        Transform3D t1f = new Transform3D(t2f);
        t1f.mul(-mass2);
        t1f.add(rTF);					// still trans1*mass1 from last time
        rTF = new Transform3D(trans2);
        rTF.mul(mass2);
        t1f.add(rTF);
        t1f.mul(1.0/(mass1));
        t1f.setScale(1.0);				// not sure if these are important but they might be
        t2f.setScale(1.0);
        return new Trans3DPair(t1f, t2f);
    }
    


  /**
   * Computes the new velocity of a ball reflected off of a rotating
   * Sphere.
   *
   * @requires the ball is at the point of impact
   *
   * @effects computes the new velocity of a ball reflected off of a
   * Sphere which is rotating with constant angular velocity around an
   * axis.  The velocity resulting from this method corresponds to a
   * collision against a surface with the given reflection
   * coefficient.  A reflection coefficient of 1.0 indicates a
   * perfectly elastic collision.
   *
   * @param ball the Sphere representing the size and position of the ball before impact
   *
   * @param trans the Transform3D of the ball before impact
   *
   * @param sph the rotating Sphere
   *
   * @param axisOfRotation the axis about which <code>sph</code> is
   * rotating
   *
   * @param angularVelocity the angular velocity with which
   * <code>sph</code> is rotating about <code>axisOfRotation</code>, in
   * radians per second.  A positive angular velocity denotes a
   * CCW rotation, looking at <code>axisOfRotation</code> with p2 in front of p1.
   *
   * @param sphCOR the COR of the sphere
   *
   * @return the Transform3D of the ball after impacting the rotating
   * Sphere
   **/
    public static Transform3D reflectBallFromRotatingSphere(Sphere ball, Transform3D trans, Sphere sph, Cylinder axisOfRotation, double angularVelocity, double sphCOR)
    {
        //if(showDebugInfo) System.out.println("Initial Transform:\n" + trans);
        Vector3d ballCenter = ball.getCenter();
        Vector3d sphCenter = sph.getCenter();
        Vector3d normal = new Vector3d(ballCenter);
        normal.sub(sphCenter);
        normal.normalize();
        
        Vector3d pointOfContact = new Vector3d(normal);
        pointOfContact.scale(sph.getRadius());
        pointOfContact.add(sphCenter);
        //if(showDebugInfo) System.out.println("Point of Contact is: " + pointOfContact);
        Vector3d l2p = closestApproach(axisOfRotation.p1(), axisOfRotation.getVector(), pointOfContact);
        l2p.negate();
        //if(showDebugInfo) System.out.println("Point of Contact is relatively: " + l2p);

        Vector3d pOCLinearVelocity = new Vector3d();
        pOCLinearVelocity.cross(axisOfRotation.getVector(), l2p);
        Transform3D sT = new Transform3D();				// Sphere's Transform
        Transform3D rT = new Transform3D(trans);			// relative Transform, won't be accurate until if statement finishes.
        if(0 != pOCLinearVelocity.length())
        {
            pOCLinearVelocity.normalize();
            pOCLinearVelocity.scale(l2p.length()*angularVelocity);
            sT.set(pOCLinearVelocity);
            sT.sub(new Transform3D());
            rT.sub(sT);
        }
        else
        {
            sT.sub(new Transform3D());        
        }

        //if(showDebugInfo) System.out.println("Point of Contact is moving at: " + pOCLinearVelocity);
        
        //if(showDebugInfo) System.out.println("Relative Transform:\n" + rT);
        Transform3D rTF = reflectBallFromSphere(ball, sph, rT, sphCOR); // Relative Transform Final
        //if(showDebugInfo) System.out.println("Reflected Relative Transform:\n" + rTF);
        rTF.add(sT);
        //if(showDebugInfo) System.out.println("Reflected Transform:\n" + rTF);
        return rTF;
    }
    
  /**
   * Computes the new velocity of a ball reflected off of a rotating
   * Sphere.
   *
   * @requires the ball is at the point of impact
   *
   * @effects computes the new velocity of a ball reflected off of a
   * Sphere which is rotating with constant angular velocity around an
   * axis.  The velocity resulting from this method corresponds to a
   * perfectly elastic collision.
   *
   * @param ball the Sphere representing the size and position of the ball before impact
   *
   * @param trans the Transform3D of the ball before impact
   *
   * @param sph the rotating Sphere
   *
   * @param axisOfRotation the axis about which <code>sph</code> is
   * rotating
   *
   * @param angularVelocity the angular velocity with which
   * <code>sph</code> is rotating about <code>axisOfRotation</code>, in
   * radians per second.  A positive angular velocity denotes a
   * CCW rotation, looking at <code>axisOfRotation</code> with p2 in front of p1.
   *
   * @return the Transform3D of the ball after impacting the rotating
   * Sphere
   **/
    public static Transform3D reflectBallFromRotatingSphere(Sphere ball, Transform3D trans, Sphere sph, Cylinder axisOfRotation, double angularVelocity)
    {
        return reflectBallFromRotatingSphere(ball, trans, sph, axisOfRotation, angularVelocity, 1.0);
    }


  /**
   * Computes the new velocity of a ball reflecting off of a
   * cylinder which is rotating about a point with constant angular
   * velocity.
   *
   * @requires <code>cyl</code> has non-zero length
   *           && the ball is at the point of impact
   *           && <code>reflectionCoeff</code> >= 0
   *
   * @effects computes the new velocity of a ball reflecting off of a
   * cylinder which is rotating about a point with constant angular
   * velocity.  The velocity resulting from this method corresponds to
   * a collision against a surface of the given reflection
   * coefficient.  A reflection coefficient of 1 indicates a perfectly
   * elastic collision.  This method assumes that the ball is at the
   * point of impact.
   *
   * NOTE: This is DIFFERENT from 6.170 physics, which says "If the ball
   * does not hit in between the endpoints of <code>cyl</code>, <code>velocity</code> is
   * returned." - In physics3d, this requirement is relaxed: the ball 
   * will act as if the cylinder extends to the point of impact.
   *
   * @param ball the Sphere representing the size and position of the ball before impact
   *
   * @param trans the Transform3D of the ball before impact
   *
   * @param cyl the rotating Cylinder
   *
   * @param axisOfRotation the point about which <code>cyl</code> rotates
   *
   * @param angularVelocity the angular velocity with which
   * <code>sph</code> is rotating about <code>axisOfRotation</code>, in
   * radians per second.  A positive angular velocity denotes a
   * CCW rotation, looking at <code>axisOfRotation</code> with p2 in front of p1.
   *
   * @param cylCOR the reflection coefficient of the cylinder
   *
   * @return the velocity of the ball after impacting the Cylinder
   **/
    public static Transform3D reflectBallFromRotatingCylinder(Sphere ball, Transform3D trans, Cylinder cyl, Cylinder axisOfRotation, double angularVelocity, double cylCOR)
    {
        //if(showDebugInfo) System.out.println("Initial Transform:\n" + trans);
        Vector3d ballCenter = ball.getCenter();
        Vector3d normal = closestApproach(cyl.p1(), cyl.getVector(), ballCenter);
        Vector3d pointOfContact = new Vector3d(normal);
        pointOfContact.add(ballCenter);
        normal.negate();
        normal.normalize();
        //if(showDebugInfo) System.out.println("Point of Contact is: " + pointOfContact);
        Vector3d l2p = closestApproach(axisOfRotation.p1(), axisOfRotation.getVector(), pointOfContact);
        l2p.negate();
        //if(showDebugInfo) System.out.println("Point of Contact is relatively: " + l2p);
        Vector3d pOCLinearVelocity = new Vector3d();
        pOCLinearVelocity.cross(axisOfRotation.getVector(), l2p);
        Transform3D cT = new Transform3D();				// Cylinder's Transform
        Transform3D rT = new Transform3D(trans);			// relative Transform, won't be accurate until if statement finishes.
        if(0 != pOCLinearVelocity.length())
        {
            pOCLinearVelocity.normalize();
            pOCLinearVelocity.scale(l2p.length()*angularVelocity);
            cT.set(pOCLinearVelocity);
            cT.sub(new Transform3D());
            rT.sub(cT);
        }
        else
        {
            cT.sub(new Transform3D());        
        }

        //if(showDebugInfo) System.out.println("Point of Contact is moving at: " + pOCLinearVelocity);
        //if(showDebugInfo) System.out.println("Relative Transform:\n" + rT);
        Transform3D rTF = reflectBallFromCylinder(ball, cyl, rT, cylCOR); // Relative Transform Final
        //if(showDebugInfo) System.out.println("Reflected Relative Transform:\n" + rTF);
        rTF.add(cT);
        //if(showDebugInfo) System.out.println("Reflected Transform:\n" + rTF);
        return rTF;
    }

  /**
   * Computes the new velocity of a ball reflecting off of a
   * cylinder which is rotating about a point with constant angular
   * velocity.
   *
   * @requires <code>cyl</code> has non-zero length
   *           && the ball is at the point of impact
   *           && <code>reflectionCoeff</code> >= 0
   *
   * @effects computes the new velocity of a ball reflecting off of a
   * cylinder which is rotating about a point with constant angular
   * velocity.  The velocity resulting from this method corresponds to a
   * perfectly elastic collision.  This method assumes that the ball is at the
   * point of impact.
   *
   * NOTE: This is DIFFERENT from 6.170 physics, which says "If the ball
   * does not hit in between the endpoints of <code>cyl</code>, <code>velocity</code> is
   * returned." - In physics3d, this requirement is relaxed: the ball 
   * will act as if the cylinder extends to the point of impact.
   *
   * @param ball the Sphere representing the size and position of the ball before impact
   *
   * @param trans the Transform3D of the ball before impact
   *
   * @param cyl the rotating Cylinder
   *
   * @param axisOfRotation the point about which <code>cyl</code> rotates
   *
   * @param angularVelocity the angular velocity with which
   * <code>sph</code> is rotating about <code>axisOfRotation</code>, in
   * radians per second.  A positive angular velocity denotes a
   * CCW rotation, looking at <code>axisOfRotation</code> with p2 in front of p1.
   *
   * @return the velocity of the ball after impacting the Cylinder
   **/
    public static Transform3D reflectBallFromRotatingCylinder(Sphere ball, Transform3D trans, Cylinder cyl, Cylinder axisOfRotation, double angularVelocity)
    {
        return reflectBallFromRotatingCylinder(ball, trans, cyl, axisOfRotation, angularVelocity, 1.0);
    }
    
    
  /**
   * Computes the new velocity of a ball reflecting off of a
   * plane segment which is rotating about a point with constant angular
   * velocity.
   *
   * @requires <code>cyl</code> has non-zero length
   *           && the ball is at the point of impact
   *           && <code>reflectionCoeff</code> >= 0
   *
   * @effects computes the new velocity of a ball reflecting off of a
   * plane segment which is rotating about a point with constant angular
   * velocity.  The velocity resulting from this method corresponds to
   * a collision against a surface of the given reflection
   * coefficient.  A reflection coefficient of 1 indicates a perfectly
   * elastic collision.  This method assumes that the ball is at the
   * point of impact.
   *
   * NOTE: This is similar to reflectBallFromRotatingCylinder in that the ball 
   * will act as if the plane extends to the point of impact.
   *
   * @param ball the Sphere representing the size and position of the ball before impact
   *
   * @param trans the Transform3D of the ball before impact
   *
   * @param ps the rotating PlaneSegment
   *
   * @param axisOfRotation the point about which <code>ps</code> rotates
   *
   * @param angularVelocity the angular velocity with which
   * <code>ps</code> is rotating about <code>axisOfRotation</code>, in
   * radians per second.  A positive angular velocity denotes a
   * CCW rotation, looking at <code>axisOfRotation</code> with p2 in front of p1.
   *
   * @param psCOR the reflection coefficient of the PlaneSegment
   *
   * @return the velocity of the ball after impacting the PlaneSegment
   **/
    public static Transform3D reflectBallFromRotatingPlaneSegment(Sphere ball, Transform3D trans, PlaneSegment ps, Cylinder axisOfRotation, double angularVelocity, double psCOR)
    {
        Vector3d vball = ball.getCenter();
        vball.sub(ps.p1());
        Vector3d pointOfContact = ps.normal();
        pointOfContact.scale(vball.dot(pointOfContact));
        pointOfContact.add(ball.getCenter());
        Vector3d l2p = closestApproach(axisOfRotation.p1(), axisOfRotation.getVector(), pointOfContact);
        l2p.negate();
        //if(showDebugInfo) System.out.println("Point of Contact is relatively: " + l2p);
        Vector3d pOCLinearVelocity = new Vector3d();
        pOCLinearVelocity.cross(axisOfRotation.getVector(), l2p);
        Transform3D psT = new Transform3D();				// PlaneSegment's Transform
        Transform3D rT = new Transform3D(trans);			// relative Transform, won't be accurate until if statement finishes.
        if(0 != pOCLinearVelocity.length())
        {
            pOCLinearVelocity.normalize();
            pOCLinearVelocity.scale(l2p.length()*angularVelocity);
            psT.set(pOCLinearVelocity);
            psT.sub(new Transform3D());
            rT.sub(psT);
        }
        else
        {
            psT.sub(new Transform3D());        
        }

        //if(showDebugInfo) System.out.println("Point of Contact is moving at: " + pOCLinearVelocity);
        //if(showDebugInfo) System.out.println("Relative Transform:\n" + rT);
        Transform3D rTF = reflectBallFromPlane(ps, rT, psCOR); // Relative Transform Final
        //if(showDebugInfo) System.out.println("Reflected Relative Transform:\n" + rTF);
        rTF.add(psT);
        //if(showDebugInfo) System.out.println("Reflected Transform:\n" + rTF);
        return rTF;
    }
    
  /**
   * Computes the new velocity of a ball reflecting off of a
   * plane segment which is rotating about a point with constant angular
   * velocity.
   *
   * @requires <code>cyl</code> has non-zero length
   *           && the ball is at the point of impact
   *           && <code>reflectionCoeff</code> >= 0
   *
   * @effects computes the new velocity of a ball reflecting off of a
   * plane segment which is rotating about a point with constant angular
   * velocity.  The velocity resulting from this method corresponds to a
   * perfectly elastic collision.  This method assumes that the ball is at the
   * point of impact.
   *
   * NOTE: This is similar to reflectBallFromRotatingCylinder in that the ball 
   * will act as if the plane extends to the point of impact.
   *
   * @param ball the Sphere representing the size and position of the ball before impact
   *
   * @param trans the Transform3D of the ball before impact
   *
   * @param ps the rotating PlaneSegment
   *
   * @param axisOfRotation the point about which <code>ps</code> rotates
   *
   * @param angularVelocity the angular velocity with which
   * <code>ps</code> is rotating about <code>axisOfRotation</code>, in
   * radians per second.  A positive angular velocity denotes a
   * CCW rotation, looking at <code>axisOfRotation</code> with p2 in front of p1.
   *
   * @param psCOR the reflection coefficient of the PlaneSegment
   *
   * @return the velocity of the ball after impacting the PlaneSegment
   **/
    public static Transform3D reflectBallFromRotatingPlaneSegment(Sphere ball, Transform3D trans, PlaneSegment ps, Cylinder axisOfRotation, double angularVelocity)
    {
        return reflectBallFromRotatingPlaneSegment(ball, trans, ps, axisOfRotation, angularVelocity, 1.0);
    }
















    // timeUntil Methods ========================================================








    // does not modify any input
    // returns the vector from point to the closest approach of direction
  private static Vector3d closestApproach(Vector3d start, Vector3d direction, Vector3d point)
  {
    Vector3d vbg = new Vector3d(point);
    vbg.sub(start);
    Vector3d vbgProjected = new Vector3d(direction);
    vbgProjected.normalize();
    vbgProjected.scale(vbg.dot(vbgProjected));
    Vector3d closestApproach = new Vector3d(start);
    closestApproach.add(vbgProjected);
    closestApproach.sub(point);
    if(Math.abs(closestApproach.length()) > 0.000001)
        return closestApproach;
    else
        return new Vector3d(0.0, 0.0, 0.0);
  }

  /**
   * Computes the time until a ball represented by a Sphere,
   * travelling at a specified velocity collides with a specified
   * (non-moving) Sphere.
   *
   * @requires ball.radius >= 0 && gizmo.radius >= 0 && arguments non-null
   * 
   * @effects computes the time until a ball represented by a Sphere,
   * travelling at a specified velocity collides with a specified
   * non-moving Sphere.  If no collision will occur <tt>POSITIVE_INFINITY</tt>
   * is returned.  This method assumes the ball travels with constant
   * velocity until impact.
   *
   * @param gizmo a Sphere representing the gizmo with which the
   * ball may collide
   *
   * @param ball a Sphere representing the size and initial location
   * of the ball
   *
   * @param velocity the velocity of the ball before impact
   *
   * @return the time until collision or <tt>POSITIVE_INFINITY</tt> if
   * the collision will not occur
   *
   * @see Double#POSITIVE_INFINITY
   **/
  public static double timeUntilBallSphereCollision(Sphere ball, Sphere sph, Transform3D velocity)
  {
    Vector3d v = new Vector3d();
    velocity.get(v);
    if(0 == v.length())	// shouldn't ever happen, but just in case.
        return Double.POSITIVE_INFINITY;
    Vector3d vbgProjected = sph.getCenter();
    vbgProjected.sub(ball.getCenter());
    if(v.dot(vbgProjected) < 0.0)		// ball is moving away from the sphere
        return Double.POSITIVE_INFINITY;
    final double collisionDistance = ball.getRadius() + sph.getRadius();
    if(vbgProjected.length() < collisionDistance)	// we already know the ball is moving towards the sphere
        return 0.0;
    Vector3d vClosest = closestApproach(ball.getCenter(), v, sph.getCenter());
    if(collisionDistance <= vClosest.length())
        return Double.POSITIVE_INFINITY;
    vbgProjected.add(vClosest);
    //if(showDebugInfo) System.err.println("vbgProjected " + vbgProjected);
    //if(showDebugInfo) System.err.println("Difference in lengths " + (collisionDistance - vClosest.length()));
    //if(showDebugInfo) System.err.println("Distance Back Squared " + (collisionDistance*collisionDistance - vClosest.length()*vClosest.length()));
    //if(showDebugInfo) System.err.println("Distance " + (vbgProjected.length() - Math.sqrt(collisionDistance*collisionDistance - vClosest.length()*vClosest.length())));
    return (vbgProjected.length() - Math.sqrt(Math.abs(collisionDistance*collisionDistance - vClosest.length()*vClosest.length())))/v.length();
  }
  // overloaded case
  public static double timeUntilCollision(Sphere ball, Sphere gizmo, Transform3D velocity)
  {
    return timeUntilBallSphereCollision(ball, gizmo, velocity);
  }



  /**
   * Computes the time until a ball, represented by a Sphere,
   * travelling at a specified velocity collides with a specified 
   * Cylinder's rounded perimiter. Note endcaps and edges are *not* checked.
   *
   * @requires <code>cyl</code> has non-zero length
   *
   * @effects computes the time until a spherical ball
   * travelling at a specified velocity collides with a specified cylinder.
   * If no collision will occur, <tt>POSITIVE_INFINITY</tt> is
   * returned.  This method assumes that the ball will travel with
   * constant velocity until impact.
   *
   * @param cyl the Cylinder representing an edge that might be collided 
   * with.
   *
   * @param ball a Sphere indicate the size and location of a ball
   * which might collide with the given line segment
   *
   * @param velocity the velocity of the ball before impact
   *
   * @return the time until collision, or <tt>POSITIVE_INFINITY</tt> if
   * the collision will not occur
   *
   * @see Double#POSITIVE_INFINITY
   * @see <a href="#endpoint_effects">endpoint effects</a>
   **/
  public static double timeUntilBallCylinderCollision(Sphere ball, Cylinder cyl, Transform3D velocity)
  {
  
    /* Strategy: reduce the problem to a ray-plane intersection problem.
     *	The minimum distance between the lines occurs in the plane containing cyl.getVector() and
     *  velocity x cyl.getVector(). Then extending cyl.getVector() into a rectangle of length cyl.getVector()
     *  and width 2*(ball.getRadius() + cyl.getRadius()) (centered on cyl) is the plane that a point moving at
     *  velocity from ball.getCenter() has to pass through in order to collide. If it does, compensate for where the
     *  ball collided.
     */
    final double collisionDistance = ball.getRadius() + cyl.getRadius(); // we can now treat the ball as a point and cyl as a plane (see below)

    Vector3d side = new Vector3d();
    Vector3d vel = new Vector3d();
    Vector3d line = cyl.getVector();
    velocity.get(vel);
    if(0 == vel.length())	// then the ball isn't moving
        return Double.POSITIVE_INFINITY;    
    side.cross(vel, line);
    if(0 == side.length())	// then the vectors are parallel
        return Double.POSITIVE_INFINITY;
    Vector3d normal = new Vector3d();
    normal.cross(line, side);	// we know line and side are at a right angle because side came from a cross of line, so we don't need to check for 0 length
    if(vel.dot(normal) > 0)
        normal.negate();
    normal.normalize();
    Vector3d vball = new Vector3d();
    vball.sub(ball.getCenter(), cyl.p1());
    if(vball.dot(normal) < 0.0)
        return Double.POSITIVE_INFINITY;

    //if(showDebugInfo) System.err.println("\nPassed degenerate checks with\n\tball "+ ball.getCenter() + "\n\tvel " + vel + "\n\tcyl " + cyl + "\n\tcD " + collisionDistance);

    // DO OPTIMIZATION
    final double simplifiedCollisionTime = vball.dot(normal)/-vel.dot(normal);
    Vector3d delta = new Vector3d(vel);
    delta.scale(simplifiedCollisionTime);
    Vector3d simplifiedCollisionPoint = ball.getCenter();
    simplifiedCollisionPoint.add(delta);		// the point on the plane where the velocity vector + ball center vector hits
    Vector3d minDistToLine = closestApproach(cyl.p1(), cyl.getVector(), simplifiedCollisionPoint);

    //if(showDebugInfo) System.err.println("Simplified Collision Point "+ simplifiedCollisionPoint);

    final double distanceBack = Math.sqrt(collisionDistance*collisionDistance - minDistToLine.length()*minDistToLine.length());
    final double actualCollisionTime = (vball.dot(normal)-distanceBack)/-vel.dot(normal);

    //if(showDebugInfo) System.err.println("Actual Collision Time "+ actualCollisionTime);

    Vector3d ballToCollision = new Vector3d(vel);
    ballToCollision.scale(actualCollisionTime);

    Vector3d projectedImpactPoint = new Vector3d(normal);
    Vector3d shortestLineBallCollision = new Vector3d(simplifiedCollisionPoint);
    shortestLineBallCollision.add(minDistToLine);
    shortestLineBallCollision.sub(ball.getCenter());
    shortestLineBallCollision.sub(ballToCollision);
    
    projectedImpactPoint.scale(shortestLineBallCollision.dot(normal));		// negativeR * normal

    Vector3d actualCollisionPoint = ball.getCenter();
    actualCollisionPoint.add(ballToCollision);
    //if(showDebugInfo) System.err.println("Ball at Collision " + actualCollisionPoint);
    //if(showDebugInfo) System.err.println("Shortest Line / Ball at Collision " + shortestLineBallCollision);
    //if(showDebugInfo) System.err.println("Projected Impact Point " + projectedImpactPoint);
    actualCollisionPoint.add(projectedImpactPoint);			// the point where the ball contacts the cylinder

    //if(showDebugInfo) System.err.println("Actual Collision Point " + actualCollisionPoint);

    side.normalize();
    side.scale(collisionDistance);
    
    // DO OPTIMIZATION
    // This uses the point-in-polygon method (http://www.cs.montana.edu/~charon/thesis/tutorials/collision.php)
    //                                        (http://www.flipcode.com/tutorials/tut_collision.shtml)
    //  However, more efficient algorithms probably exist.
    Vector3d cp1 = new Vector3d(cyl.p1());
    cp1.add(side);
    cp1.sub(actualCollisionPoint);
    cp1.normalize();
    Vector3d cp2 = new Vector3d(cyl.p1());
    cp2.sub(side);
    cp2.sub(actualCollisionPoint);
    cp2.normalize();
    Vector3d cp3 = new Vector3d(cyl.p2());
    cp3.sub(side);
    cp3.sub(actualCollisionPoint);
    cp3.normalize();
    Vector3d cp4 = new Vector3d(cyl.p2());
    cp4.add(side);
    cp4.sub(actualCollisionPoint);
    cp4.normalize();

/*
    double d12 = cp1.dot(cp2);
    double d23 = cp2.dot(cp3);
    double d34 = cp3.dot(cp4);
    double d41 = cp4.dot(cp1);

    double a12 = (0 < d12 ? Math.acos(d12) : Math.PI - Math.acos(-d12));
    double a23 = (0 < d23 ? Math.acos(d23) : Math.PI - Math.acos(-d23));
    double a34 = (0 < d34 ? Math.acos(d34) : Math.PI - Math.acos(-d34));
    double a41 = (0 < d41 ? Math.acos(d41) : Math.PI - Math.acos(-d41));
    if(Math.abs(a12 + a23 + a34 + a41 - 2*Math.PI) > pipTolerance)	// if the ball doesn't collide
        return Double.POSITIVE_INFINITY;
*/

    final double a12 = Math.acos(cp1.dot(cp2));
    final double a23 = Math.acos(cp2.dot(cp3));
    final double a34 = Math.acos(cp3.dot(cp4));
    final double a41 = Math.acos(cp4.dot(cp1));
    if(Math.abs(a12 + a23 + a34 + a41 - 2*Math.PI) > pipTolerance)	// if the ball doesn't collide
        return Double.POSITIVE_INFINITY;

    //if(showDebugInfo) System.err.println("Passed plane segment test");
    if(actualCollisionTime <= 0.0) return 0.0;
        return ballToCollision.length()/vel.length();
  }
  // overloaded case
  public static double timeUntilCollision(Sphere ball, Cylinder cyl, Transform3D velocity)
  {
    return timeUntilBallCylinderCollision(ball, cyl, velocity);
  }



  /**
   * Computes the time until a ball represented by a Sphere,
   * travelling at a specified velocity collides with a specified
   * (non-moving) plane segment.
   *
   * @requires ball.radius >= 0 && arguments non-null
   * 
   * @effects computes the time until a ball represented by a Sphere,
   * travelling at a specified velocity collides with a specified
   * non-moving PlaneSegment.  If no collision will occur <tt>POSITIVE_INFINITY</tt>
   * is returned.  This method assumes the ball travels with constant
   * velocity until impact.
   *
   * @param pseg a PlaneSegment representing the triangle with which the
   * ball may collide
   *
   * @param ball a Sphere representing the size and initial location
   * of the ball
   *
   * @param velocity the velocity of the ball before impact
   *
   * @return the time until collision or <tt>POSITIVE_INFINITY</tt> if
   * the collision will not occur
   *
   * @see Double#POSITIVE_INFINITY
   **/
  public static double timeUntilBallPlaneCollision(Sphere ball, PlaneSegment pseg, Transform3D velocity)
  {
    // DO OPTIMIZATION
    // There's a *lot* of Vector3d creation for one-shot stuff, a lot of them could probably
    //  be combined to reduce overhead.

    Vector3d v = new Vector3d();
    velocity.get(v);
    Vector3d normal = pseg.normal();	// right now it's just n
    if(0 == v.dot(normal))
        return Double.POSITIVE_INFINITY;
    else if(v.dot(normal) > 0)
        normal.negate();
                                        // now normal definitely points against v
                                        
    Vector3d vball = new Vector3d();
    vball.sub(ball.getCenter(), pseg.p1());

    if(vball.dot(normal) < 0.0)
        return Double.POSITIVE_INFINITY;	// ball is moving away from plane

    Vector3d contactPoint = ball.getCenter();
    //if(showDebugInfo) System.err.println("Passed degenerate test with\n\tball: " + ball.getCenter() + "\n\tvel: " + v + "\n\tnorm: " + normal);

    Vector3d distance = new Vector3d(normal);
    distance.scale(vball.dot(normal));
    final double distToCollision = distance.length();
    //if(showDebugInfo) System.err.println("Distance to extrapolated point collision: " + distToCollision);
    if(distToCollision < ball.getRadius())
    {
        v.scale(distToCollision/-v.dot(normal));
        contactPoint.add(v);
        //if(showDebugInfo) System.err.println("Contact Point: " + contactPoint);
        if(pointIsInPlaneSegment(contactPoint, pseg))
            return 0.0;
        else
            return Double.POSITIVE_INFINITY;
    }
    final double tcollision = (distToCollision-ball.getRadius())/-v.dot(normal);

    //if(showDebugInfo) System.err.println("Time to Collision: " + tcollision);

    v.scale(tcollision);
    contactPoint.add(v);
    Vector3d nrn = new Vector3d(normal);
    nrn.scale(-ball.getRadius());
    contactPoint.add(nrn);

    //if(showDebugInfo) System.err.println("Contact Point: " + contactPoint);

    if(pointIsInPlaneSegment(contactPoint, pseg))
        return tcollision;
    else
        return Double.POSITIVE_INFINITY;
  }
  // overloaded case
  public static double timeUntilCollision(Sphere ball, PlaneSegment pseg, Transform3D velocity)
  {
    return timeUntilBallPlaneCollision(ball, pseg, velocity);
  }

  /**
   * Computes the time until two balls collide.
   *
   * @effects computes the time until two balls, represented by two
   * Spheres, travelling at specified constant velocities, collide.
   * If no collision will occur <tt>POSITIVE_INFINITY</tt> is returned.
   * This method assumes that both balls will travel at constant
   * velocity until impact.
   *
   * @param ball1 a Sphere representing the size and initial position
   * of the first ball.
   *
   * @param trans1 the Transform3D of the first ball before impact
   *
   * @param ball2 a Sphere representing the size and initial position
   * of the second ball.
   *
   * @param trans2 the Transform3D of the second ball before impact
   *
   * @return the time until collision or <tt>POSITIVE_INFINITY</tt> if the
   * collision will not occur
   *
   * @see Double#POSITIVE_INFINITY
   **/
  public static double timeUntilBallBallCollision(Sphere ball1, Transform3D trans1, Sphere ball2, Transform3D trans2)
  {
  
    /*	Strategy: the positions of the balls are given by:
     *		b1x(t) = v1x*t + b1x(0)
     *		b1y(t) = v1y*t + b1y(0)
     *		b1z(t) = v1z*t + b1z(0)
     *
     *		b2x(t) = v2x*t + b2x(0)
     *		b2y(t) = v2y*t + b2y(0)
     *		b2z(t) = v2z*t + b2z(0)
     *
     *	At time t, the balls are distance d(t) apart.
     *		d(t)^2 = (b1x(t) - b2x(t))^2 + (b1y(t) - b2y(t))^2 + (b1z(t) - b2z(t))^2
     *
     *	Substituting, expanding, and collecting t terms, we get
     *		d(t)^2 =    ((v1x-v2x)^2 + (v1y-v2y)^2 + (v1y-v2y)^2)*t^2
     *                      + 2*(((v1x-v2x)(b1x(0)-b2x(0))) + ((v1y-v2y)(b1y(0)-b2y(0))) + ((v1z-v2z)(b1z(0)-b2z(0))))*t
     *                      + (b1x(0)-b2x(0))^2 + (b1y(0)-b2y(0))^2 + (b1z(0)-b2z(0))^2
     *
     *	Then set d(t) = ball1.radius + ball2.radius and solve the quadratic equation.
     */
    Vector3d b1 = ball1.getCenter();
    Vector3d b2 = ball2.getCenter();
    Vector3d v1 = new Vector3d();
    trans1.get(v1);
    Vector3d v2 = new Vector3d();
    trans2.get(v2);
    
    //if(showDebugInfo) System.err.println("\nball1: " + b1);
    //if(showDebugInfo) System.err.println("vel1: " + v1);
    //if(showDebugInfo) System.err.println("ball2: " + b2);
    //if(showDebugInfo) System.err.println("vel2: " + v2);
    final double c = (b1.x-b2.x)*(b1.x-b2.x) + (b1.y-b2.y)*(b1.y-b2.y) + (b1.z-b2.z)*(b1.z-b2.z) - (ball1.getRadius()+ball2.getRadius())*(ball1.getRadius()+ball2.getRadius());

    //if(showDebugInfo) System.err.println("c: "+ c);

    if(c < 0.0)		// we somehow managed to collide without knowing it
    {			// at this point, we can reuse v1 v2 b1 b2 since we don't need them anymore
        //if(showDebugInfo) System.err.println("Already collided, checking direction...");
        v2.sub(v1);
        b2.sub(b1);
        if(v2.dot(b2) < 0.0)	// if the balls are moving towards each other, collide now, otherwise they're moving apart
            return 0.0;
        else
            return Double.POSITIVE_INFINITY;
    }
    final double b = 2.0 * ((v1.x-v2.x)*(b1.x-b2.x) + (v1.y-v2.y)*(b1.y-b2.y) + (v1.z-v2.z)*(b1.z-b2.z)); 
    final double a = (v1.x-v2.x)*(v1.x-v2.x) + (v1.y-v2.y)*(v1.y-v2.y) + (v1.z-v2.z)*(v1.z-v2.z);
    //if(showDebugInfo) System.err.println("b: "+ b);
    //if(showDebugInfo) System.err.println("a: "+ a);
    if(a != 0.0)
        return minPositiveQuadraticSolution(a, b, c);
    if(b == 0.0)
        return Double.POSITIVE_INFINITY;
    return -c/b;
  }

  private static double minPositiveQuadraticSolution(double a, double b, double c)
  {
    final double discriminant = b*b-4*a*c;
    double theAnswer;
    if(discriminant < 0.0)	// roots are imaginary
        return Double.POSITIVE_INFINITY;
    else if(0.0 == discriminant)
    {
        theAnswer = -b / (2*a);
        //if(showDebugInfo) System.err.println("Singleton solution: " + theAnswer);
        if(theAnswer >= 0.0)
            return theAnswer;
        else
            return Double.POSITIVE_INFINITY;
    }
    final double d = Math.sqrt(discriminant);
    theAnswer = -(b+d)/(2*a);

/*
    //if(showDebugInfo) System.err.println("1st root: " + theAnswer);
    double theAnswer2 = -(b-d)/(2*a);
    //if(showDebugInfo) System.err.println("2nd root: " + theAnswer2);
    if(theAnswer >= 0.0)
        return theAnswer;
    else if(theAnswer2 >= 0.0)
        return theAnswer2;
    else
        return Double.POSITIVE_INFINITY;    
*/
    if(theAnswer >= 0.0)
        return theAnswer;
    theAnswer = -(b-d)/(2*a);    
    if(theAnswer >= 0.0)
        return theAnswer;
    return Double.POSITIVE_INFINITY;    
  }


  /**
   * Rotates the point represented by <code>point</code> by
   * <code>radsToRotate</code> around the center of rotation, <code>axisOfRotation</code>,
   * and returns the result.
   *
   * @effects rotates the point represented by <code>point</code> by
   * <code>radstoRotate</code> around the center of rotation, <code>axisOfRotation</code>,
   * and returns the result.
   *
   * @param point the initial location of the point to be rotated
   *
   * @param axisOfRotation a Cylinder representing indicating the axis of rotation; 
   * radius is ignored. Positive rotation is right-handed (ie looking at axisOfRotation 
   * head-on, with p2 closer to you, positive is CCW)
   *
   * @param radsToRotate the amount (in radians) by which to rotate <code>point</code>
   *
   * @return point <code>point</code> rotated around <code>axisOfRotation</code>
   * by <code>radsToRotate</code>
   **/
  public static Transform3D rotateTransformAboutLine(Transform3D point, Cylinder axisOfRotation, double radsToRotate)
  {
    // Strategy: See http://graphics.cs.ucdavis.edu/GraphicsNotes/General-Rotation/General-Rotation.html
    // Essentailly, reduce the problem until it's a trivial axis-aligned rotation, do it, then undo reductions.
    
    // Note: rotateCylinderAboutLine and rotatePlaneAboutLine duplicate this code for efficiency,
    // so if a bug is found here, they need to be changed as well

    Transform3D theTransformResult = new Transform3D(point);
//    System.out.println("point to rotate\n" + theTransformResult);
    Transform3D theRotationTransform = new Transform3D();	// translate so p1 is at the origin
    Transform3D nextRotationTransform = new Transform3D();
    Vector3d nextVector = new Vector3d(axisOfRotation.p1());
    theRotationTransform.set(nextVector);
//    System.out.println("zeroed p1\n" + theRotationTransform);
    theTransformResult.sub(theRotationTransform);
    theTransformResult.add(new Transform3D());    
//    System.out.println("p-p1\n" + theTransformResult);
    nextVector = axisOfRotation.getVector();


/*
    // this is in the order presented in the link, but it doesn't do the right thing
    double theta = Math.atan2(nextVector.x, nextVector.z);	// rotation about y needed to get the axis in the yz plane
    System.out.println("Theta rotation: " + theta + " based on x=" + nextVector.x + " and z=" + nextVector.z);
    theRotationTransform.rotY(-theta);
    double phi = Math.atan2(nextVector.y, Math.sqrt(nextVector.x * nextVector.x + nextVector.z * nextVector.z));	// rotation about x needed to get the axis aligned with z
    System.out.println("Phi rotation: " + phi);
    nextRotationTransform.rotX(phi);
    theRotationTransform.mul(nextRotationTransform);
    nextRotationTransform.rotZ(radsToRotate);		// rotation about z
    theRotationTransform.mul(nextRotationTransform);
    nextRotationTransform.rotX(-phi);			// now undo everything!
    theRotationTransform.mul(nextRotationTransform);
    nextRotationTransform.rotY(theta);
    theRotationTransform.mul(nextRotationTransform);
*/


    // reverse the order of rotations so we get something that does the right thing, I think    
    double theta = Math.atan2(nextVector.x, nextVector.z);
//    System.out.println("Theta rotation: " + theta + " based on x=" + nextVector.x + " and z=" + nextVector.z);
    theRotationTransform.rotY(theta);
    double phi = Math.atan2(nextVector.y, Math.sqrt(nextVector.x * nextVector.x + nextVector.z * nextVector.z));
//    System.out.println("Phi rotation: " + phi);
    nextRotationTransform.rotX(-phi);
    theRotationTransform.mul(nextRotationTransform);
    nextRotationTransform.rotZ(radsToRotate);
    theRotationTransform.mul(nextRotationTransform);
    nextRotationTransform.rotX(phi);
    theRotationTransform.mul(nextRotationTransform);
    nextRotationTransform.rotY(-theta);
    theRotationTransform.mul(nextRotationTransform);


/*
    // for debugging
    System.out.println("Transfom normalized for p1:\n" + theTransformResult);
    double theta = Math.atan2(nextVector.x, nextVector.z);
    System.out.println("Theta rotation: " + theta + " based on x=" + nextVector.x + " and z=" + nextVector.z);
    nextRotationTransform.rotY(-theta);
    theTransformResult.mul(nextRotationTransform, theTransformResult);
    System.out.println("Transfom after -theta about Y:\n" + theTransformResult);
    double phi = Math.atan2(nextVector.y, Math.sqrt(nextVector.x * nextVector.x + nextVector.z * nextVector.z));
    System.out.println("Phi rotation: " + phi);
    nextRotationTransform.rotX(phi);
    theTransformResult.mul(nextRotationTransform, theTransformResult);
    System.out.println("Transfom after phi about X:\n" + theTransformResult);
    nextRotationTransform.rotZ(radsToRotate);
    theTransformResult.mul(nextRotationTransform, theTransformResult);
    System.out.println("Transfom after rotation about Z:\n" + theTransformResult);
    nextRotationTransform.rotX(-phi);
    theTransformResult.mul(nextRotationTransform, theTransformResult);
    System.out.println("Transfom after -phi about X:\n" + theTransformResult);
    nextRotationTransform.rotY(theta);
    theTransformResult.mul(nextRotationTransform, theTransformResult);
    System.out.println("Transfom after theta about Y:\n" + theTransformResult);
*/

    theTransformResult.mul(theRotationTransform, theTransformResult);
//    System.out.println("R*(p-p1)\n" + theTransformResult + "\n");
//    nextRotationTransform = new Transform3D();
    nextRotationTransform.set(axisOfRotation.p1());
    nextRotationTransform.sub(new Transform3D());
    theTransformResult.add(nextRotationTransform);
    return theTransformResult;
  }

  /**
   * Rotates the point represented by <code>point</code> by
   * <code>radsToRotate</code> around the center of rotation, <code>axisOfRotation</code>,
   * and returns the result.
   *
   * @effects rotates the point represented by <code>point</code> by
   * <code>radstoRotate</code> around the center of rotation, <code>axisOfRotation</code>,
   * and returns the result.
   *
   * @param point the initial location of the point to be rotated
   *
   * @param axisOfRotation a Cylinder representing indicating the axis of rotation; 
   * radius is ignored. Positive rotation is right-handed (ie looking at axisOfRotation 
   * head-on, with p2 closer to you, positive is CCW)
   *
   * @param radsToRotate the amount (in radians) by which to rotate <code>point</code>
   *
   * @return point <code>point</code> rotated around <code>axisOfRotation</code>
   * by <code>radsToRotate</code>
   **/
  public static Vector3d rotatePointAboutLine(Vector3d point, Cylinder axisOfRotation, double radsToRotate)
  {
    Transform3D tio = new Transform3D();
    tio.set(point);
    Vector3d theResult = new Vector3d();
    tio = rotateTransformAboutLine(tio, axisOfRotation, radsToRotate);
    tio.get(theResult);
    return theResult;
  }

  /**
   * Rotates the Sphere represented by <code>s</code> by
   * <code>radsToRotate</code> around the center of rotation, <code>axisOfRotation</code>,
   * and returns the result.
   *
   * @effects rotates the Sphere represented by <code>s</code> by
   * <code>radstoRotate</code> around the center of rotation, <code>axisOfRotation</code>,
   * and returns the result.
   *
   * @param s the Sphere to be rotated
   *
   * @param axisOfRotation a Cylinder representing indicating the axis of rotation; 
   * radius is ignored. Positive rotation is right-handed (ie looking at axisOfRotation 
   * head-on, with p2 closer to you, positive is CCW)
   *
   * @param radsToRotate the amount (in radians) by which to rotate <code>point</code>
   *
   * @return point <code>point</code> rotated around <code>axisOfRotation</code>
   * by <code>radsToRotate</code>
   **/
  public static Sphere rotateSphereAboutLine(Sphere s, Cylinder axisOfRotation, double radsToRotate)
  {
    return new Sphere(rotateTransformAboutLine(s.getCenterAsTransform(), axisOfRotation, radsToRotate), s.getRadius(), s.name());
  }
  
  /**
   * Rotates the Cylinder represented by <code>c</code> by
   * <code>radsToRotate</code> around the center of rotation, <code>axisOfRotation</code>,
   * and returns the result.
   *
   * @effects rotates the Cylinder represented by <code>c</code> by
   * <code>radstoRotate</code> around the center of rotation, <code>axisOfRotation</code>,
   * and returns the result.
   *
   * @param c the Cylinder to be rotated
   *
   * @param axisOfRotation a Cylinder representing indicating the axis of rotation; 
   * radius is ignored. Positive rotation is right-handed (ie looking at axisOfRotation 
   * head-on, with p2 closer to you, positive is CCW)
   *
   * @param radsToRotate the amount (in radians) by which to rotate <code>point</code>
   *
   * @return point <code>point</code> rotated around <code>axisOfRotation</code>
   * by <code>radsToRotate</code>
   **/
  public static Cylinder rotateCylinderAboutLine(Cylinder c, Cylinder axisOfRotation, double radsToRotate)
  {
    // Strategy: See http://graphics.cs.ucdavis.edu/GraphicsNotes/General-Rotation/General-Rotation.html
    // Essentailly, reduce the problem until it's a trivial axis-aligned rotation, do it, then undo reductions.

    // Note: rotateTransformAboutLine and rotatePlaneAboutLine duplicate this code for efficiency,
    // so if a bug is found here, they need to be changed as well

    Transform3D transformedP1 = new Transform3D();
    transformedP1.set(c.p1());
    Transform3D transformedP2 = new Transform3D();
    transformedP2.set(c.p2());
    Transform3D theRotationTransform = new Transform3D();	// translate so p1 is at the origin
    Transform3D nextRotationTransform = new Transform3D();
    Vector3d nextVector = new Vector3d(axisOfRotation.p1());
    theRotationTransform.set(nextVector);
    transformedP1.sub(theRotationTransform);
    transformedP2.sub(theRotationTransform);
    transformedP1.add(new Transform3D());
    transformedP2.add(new Transform3D());

    nextVector = axisOfRotation.getVector();
    double theta = Math.atan2(nextVector.x, nextVector.z);
    theRotationTransform.rotY(theta);
    double phi = Math.atan2(nextVector.y, Math.sqrt(nextVector.x * nextVector.x + nextVector.z * nextVector.z));
    nextRotationTransform.rotX(-phi);
    theRotationTransform.mul(nextRotationTransform);
    nextRotationTransform.rotZ(radsToRotate);
    theRotationTransform.mul(nextRotationTransform);
    nextRotationTransform.rotX(phi);
    theRotationTransform.mul(nextRotationTransform);
    nextRotationTransform.rotY(-theta);
    theRotationTransform.mul(nextRotationTransform);

    transformedP1.mul(theRotationTransform, transformedP1);
    transformedP2.mul(theRotationTransform, transformedP2);
    nextRotationTransform.set(axisOfRotation.p1());
    nextRotationTransform.sub(new Transform3D());
    transformedP1.add(nextRotationTransform);
    transformedP2.add(nextRotationTransform);    
    Vector3d vp1 = new Vector3d();
    Vector3d vp2 = new Vector3d();
    transformedP1.get(vp1);
    transformedP2.get(vp2); 
    return new Cylinder(vp1, vp2, c.getRadius(), c.name());
  }

  /**
   * Rotates the PlaneSegment represented by <code>ps</code> by
   * <code>radsToRotate</code> around the center of rotation, <code>axisOfRotation</code>,
   * and returns the result.
   *
   * @effects rotates the PlaneSegment represented by <code>ps</code> by
   * <code>radstoRotate</code> around the center of rotation, <code>axisOfRotation</code>,
   * and returns the result.
   *
   * @param ps the PlaneSegment to be rotated
   *
   * @param axisOfRotation a Cylinder representing indicating the axis of rotation; 
   * radius is ignored. Positive rotation is right-handed (ie looking at axisOfRotation 
   * head-on, with p2 closer to you, positive is CCW)
   *
   * @param radsToRotate the amount (in radians) by which to rotate <code>point</code>
   *
   * @return point <code>point</code> rotated around <code>axisOfRotation</code>
   * by <code>radsToRotate</code>
   **/
  public static PlaneSegment rotatePlaneSegmentAboutLine(PlaneSegment ps, Cylinder axisOfRotation, double radsToRotate)
  {
    // Strategy: See http://graphics.cs.ucdavis.edu/GraphicsNotes/General-Rotation/General-Rotation.html
    // Essentailly, reduce the problem until it's a trivial axis-aligned rotation, do it, then undo reductions.

    // Note: rotateTransformAboutLine and rotatePlaneAboutLine duplicate this code for efficiency,
    // so if a bug is found here, they need to be changed as well

    Transform3D transformedP1 = new Transform3D();
    transformedP1.set(ps.p1());
    Transform3D transformedP2 = new Transform3D();
    transformedP2.set(ps.p2());
    Transform3D transformedP3 = new Transform3D();
    transformedP3.set(ps.p3());
    Transform3D theRotationTransform = new Transform3D();	// translate so p1 is at the origin
    Transform3D nextRotationTransform = new Transform3D();
    Vector3d nextVector = new Vector3d(axisOfRotation.p1());
    theRotationTransform.set(nextVector);
    transformedP1.sub(theRotationTransform);
    transformedP2.sub(theRotationTransform);
    transformedP3.sub(theRotationTransform);
    transformedP1.add(new Transform3D());
    transformedP2.add(new Transform3D());
    transformedP3.add(new Transform3D());

    nextVector = axisOfRotation.getVector();
    double theta = Math.atan2(nextVector.x, nextVector.z);
    theRotationTransform.rotY(theta);
    double phi = Math.atan2(nextVector.y, Math.sqrt(nextVector.x * nextVector.x + nextVector.z * nextVector.z));
    nextRotationTransform.rotX(-phi);
    theRotationTransform.mul(nextRotationTransform);
    nextRotationTransform.rotZ(radsToRotate);
    theRotationTransform.mul(nextRotationTransform);
    nextRotationTransform.rotX(phi);
    theRotationTransform.mul(nextRotationTransform);
    nextRotationTransform.rotY(-theta);
    theRotationTransform.mul(nextRotationTransform);

    transformedP1.mul(theRotationTransform, transformedP1);
    transformedP2.mul(theRotationTransform, transformedP2);
    transformedP3.mul(theRotationTransform, transformedP3);
    nextRotationTransform.set(axisOfRotation.p1());
    nextRotationTransform.sub(new Transform3D());
    transformedP1.add(nextRotationTransform);
    transformedP2.add(nextRotationTransform);    
    transformedP3.add(nextRotationTransform);    
    Vector3d vp1 = new Vector3d();
    Vector3d vp2 = new Vector3d();
    Vector3d vp3 = new Vector3d();
    transformedP1.get(vp1);
    transformedP2.get(vp2);
    transformedP3.get(vp3); 
    return new PlaneSegment(vp1, vp2, vp3, ps.name());
  }

    // true iff p1, p2, p3 are collinear
  public static boolean pointsAreCollinear(Tuple3d p1, Tuple3d p2, Tuple3d p3)
  {
    if(p1.equals(p3) || p1.equals(p2) || p2.equals(p3)) return true;
    Vector3d v13 = new Vector3d(p3);
    v13.sub(p1);
    Vector3d v12 = new Vector3d(p2);
    v12.sub(p1);
    v13.normalize();
    v12.normalize();
    if(v13.epsilonEquals(v12, collinearTolerance)) return true;
    v12.negate();
    if(v13.epsilonEquals(v12, collinearTolerance)) return true;
    return false;
  }

    // true iff p1, p2, p3 are collinear AND p2 is between p1 and p3, inclusive
  public static boolean pointsAreInOrder(Tuple3d p1, Tuple3d p2, Tuple3d p3)
  {
    if(p1.equals(p3) || p1.equals(p2) || p2.equals(p3)) return true;
    Vector3d v13 = new Vector3d(p3);
    v13.sub(p2);
    v13.normalize();
    Vector3d v12 = new Vector3d(p1);
    v12.sub(p2);
    v12.normalize();
    v12.negate();
    if(v13.epsilonEquals(v12, collinearTolerance)) return true;
    return false;
  }

    // true iff p1 is inside ps
    //@requres p1 and ps are co-planar
  public static boolean pointIsInPlaneSegment(Tuple3d p1, PlaneSegment ps)
  {
    // DO OPTIMIZATION
    // This uses the point-in-triangle method (http://www.cs.montana.edu/~charon/thesis/tutorials/collision.php)
    //                                        (http://www.flipcode.com/tutorials/tut_collision.shtml)
    //  However, more efficient algorithms exist (example: http://www.clarus.se/People/tompa/)
    Vector3d v1 = new Vector3d();
    Vector3d v2 = new Vector3d();
    Vector3d v3 = new Vector3d();
    v1.sub(ps.p1(), p1);
    v1.normalize();
    v2.sub(ps.p2(), p1);
    v2.normalize();
    v3.sub(ps.p3(), p1);
    v3.normalize();

    //if(showDebugInfo) System.err.println("Point-in-Triangle Angles: " + Math.acos(v1.dot(v2)) + ", " + Math.acos(v2.dot(v3)) + ", " + Math.acos(v3.dot(v1)));

    if(Math.abs(Math.acos(v1.dot(v2)) + Math.acos(v2.dot(v3)) + Math.acos(v3.dot(v1)) - 2*Math.PI) < pipTolerance)
        return true;
    return false;
  }


  /**
   * Computes the time until a ball travelling at a specified
   * velocity collides with a rotating Sphere.
   *
   * @effects computes the time until a Spherical ball
   * travelling at a specified velocity collides with a specified Sphere
   * that is rotating about a given axis of rotation at a given
   * angular velocity.  If no collision will occurr <tt>POSITIVE_INFINITY</tt>
   * is returned. This method assumes the
   * ball will travel with constant velocity until impact.
   *
   * @param ball a Sphere representing the size and initial position
   * of the ball
   *
   * @param trans the Transform3D of the ball before impact
   *
   * @param sph a Sphere representing the initial location and size
   * of the rotating sphere
   *
   * @param axisOfRotation the axis around which the Sphere is rotating
   *
   * @param radsPerSec the angular velocity with which
   * <code>sph</code> is rotating about <code>axisOfRotation</code>, in
   * radians per second.  A positive angular velocity denotes a
   * rotation in the CCW direction with p2 of <code>axisOfRotation</code> being in front of p1.
   *
   * @return the time until collision or <tt>POSITIVE_INFINITY</tt> if no
   * collision was detected.
   *
   * @see Double#POSITIVE_INFINITY
   **/
  public static double timeUntilBallRotatingSphereCollision(Sphere ball, Transform3D trans, Sphere sph, Cylinder axisOfRotation, double radsPerSec)
  {
    if(0.0 == radsPerSec || pointsAreCollinear(sph.getCenter(), axisOfRotation.p1(), axisOfRotation.p2()))
        return timeUntilBallSphereCollision(ball, sph, trans);

    Vector3d v = new Vector3d();
    trans.get(v);
    final double vel_x = v.x;
    final double vel_y = v.y;
    final double vel_z = v.z;
    
    v = ball.getCenter();
    final double ball_x = v.x;
    final double ball_y = v.y;
    final double ball_z = v.z;

    final double omega = radsPerSec;

    final Cylinder theAOR = new Cylinder(axisOfRotation.p1(), axisOfRotation.p2(), 0.0, axisOfRotation.name());
    final Vector3d theSphere = sph.getCenter();

    final double distanceToCollision = ball.getRadius() + sph.getRadius();
    final double d2c_2 = distanceToCollision * distanceToCollision;

    class RotatingSphereDistance implements Newton.Function
    {
        public Newton.Result evaluate(double t)
        {
            double ballXOfT = ball_x + t * vel_x;
            double ballYOfT = ball_y + t * vel_y;
            double ballZOfT = ball_z + t * vel_z;
            Vector3d rotatedPoint = rotatePointAboutLine(theSphere, theAOR, t*omega);
            double SphereXOfT = rotatedPoint.x;
            double SphereYOfT = rotatedPoint.y;
            double SphereZOfT = rotatedPoint.z;
            double dist_2 = (ballXOfT - SphereXOfT) * (ballXOfT - SphereXOfT) + (ballYOfT - SphereYOfT) * (ballYOfT - SphereYOfT) + (ballZOfT - SphereZOfT) * (ballZOfT - SphereZOfT);
            double f = dist_2 - d2c_2;
            // now fake the derivative
            ballXOfT = ball_x + (t+dt) * vel_x;
            ballYOfT = ball_y + (t+dt) * vel_y;
            ballZOfT = ball_z + (t+dt) * vel_z;
            rotatedPoint = rotatePointAboutLine(theSphere, theAOR, (t+dt)*omega);
            SphereXOfT = rotatedPoint.x;
            SphereYOfT = rotatedPoint.y;
            SphereZOfT = rotatedPoint.z;
            dist_2 = (ballXOfT - SphereXOfT) * (ballXOfT - SphereXOfT) + (ballYOfT - SphereYOfT) * (ballYOfT - SphereYOfT) + (ballZOfT - SphereZOfT) * (ballZOfT - SphereZOfT);
            double f_prime = dist_2 - d2c_2;
            f_prime = (f_prime - f)/dt;
            return new Newton.Result(f, f_prime);
        }
    }
    Newton.Function function = new RotatingSphereDistance();
    Newton.Result initialDistance = function.evaluate(0);
    if(initialDistance.f <= 0)
    {
        if(initialDistance.f_prime >= 0)		// if they're already in contact but moving apart
            return Double.POSITIVE_INFINITY;
        else
            return 0.0;
    }
    return searchForCollision(function);
  }

  /**
   * @effects: attempts to return the root of
   * <code>distanceFunction</code> of the least value which occurs
   * before <code>maximumForesight</code>.  If no such root exists, or no
   * such root is found, returns +Inf. 
   **/
  private static double searchForCollision(Newton.Function distanceFunction)
  {
    double collisionTime = Newton.findRoot(distanceFunction, 0.0, maximumForesight, maximumForesight/searchSlices);
    if (collisionTime >= 0)
    {
        Newton.Result result = distanceFunction.evaluate(collisionTime);
    	if (result.f_prime < 0) {
	  return collisionTime;
	} else
        {
          // Eric's note: I'm not sure I agree with this; how do we know there won't be further collisions with result.f_prime > 0?
          //        However, 6.170 physics uses it, so I will too.
	  return Double.POSITIVE_INFINITY;
        }
    }
    return Double.POSITIVE_INFINITY;
  }


  /**
   * Computes the time until a ball travelling at a specified
   * velocity collides with a rotating cylinder.
   *
   * @requires <code>cylinder</code> has non-zero length
   *
   * @effects computes the time until a Spherical ball
   * travelling at a specified velocity collides with a specified Cylinder
   * which is rotating at a fixed angular velocity about a
   * fixed axis of rotation.
   *
   * NOTE: Because I fake the derivative instead of actually computing it, f_prime 
   *   may sometimes by Double.NaN; this may cause problems.
   *
   * @param ball a Sphere representing the size and initial location of the ball
   *
   * @param trans the initial Transform3D of the ball before impact.  The ball is
   * assumed to travel at a constant velocity until impact.
   *
   * @param cyl the initial position of the rotating cylinder
   *
   * @param axisOfRotation the axis of rotation for <code>cyl</code>
   *
   * @param radsPerSec the angular velocity of the rotation of
   * <code>cyl</code> in radians per second.  A positive angular
   * velocity denotes a rotation in the CCW direction with p2 of
   * <code>axisOfRotation</code> being in front of p1.
   *
   * @return the time until collision or <tt>POSITIVE_INFINITY</tt> if no
   * collision was detected.
   *
   * @see Double#POSITIVE_INFINITY
   * @see <a href="#endpoint_effects">endpoint effects</a>
   **/
  public static double timeUntilBallRotatingCylinderCollision(Sphere ball, Transform3D trans, Cylinder cyl, Cylinder axisOfRotation, double radsPerSec)
  {
    if(0.0 == radsPerSec || (pointsAreCollinear(cyl.p1(), axisOfRotation.p1(), axisOfRotation.p2()) && pointsAreCollinear(cyl.p2(), axisOfRotation.p1(), axisOfRotation.p2())))
        return timeUntilBallCylinderCollision(ball, cyl, trans);

    Vector3d v = new Vector3d();
    trans.get(v);
    final double vel_x = v.x;
    final double vel_y = v.y;
    final double vel_z = v.z;
    
    v = ball.getCenter();
    final double ball_x = v.x;
    final double ball_y = v.y;
    final double ball_z = v.z;

    final double omega = radsPerSec;

    final Cylinder theAOR = new Cylinder(axisOfRotation.p1(), axisOfRotation.p2(), 0.0, axisOfRotation.name());
    final Cylinder theCyl = new Cylinder(cyl.p1(), cyl.p2(), cyl.getRadius(), cyl.name());

    final double distanceToCollision = ball.getRadius() + cyl.getRadius();

    class RotatingCylinderDistance implements Newton.Function
    {
        public Newton.Result evaluate(double t)
        {
            double ballXOfT = ball_x + t * vel_x;
            double ballYOfT = ball_y + t * vel_y;
            double ballZOfT = ball_z + t * vel_z;
            Vector3d ballPos = new Vector3d(ballXOfT, ballYOfT, ballZOfT);
            Cylinder rotatedCylinder = rotateCylinderAboutLine(theCyl, theAOR, t*omega);
            Vector3d b2l = closestApproach(rotatedCylinder.p1(), rotatedCylinder.getVector(), ballPos);
            ballPos.add(b2l); // now the point on the line where the point is closest
            if(!pointsAreInOrder(rotatedCylinder.p1(), ballPos, rotatedCylinder.p2()))
                return new Newton.Result(Double.NaN, Double.NaN);

            double dist = b2l.length();
            double f = dist - distanceToCollision;
            // now fake the derivative
            ballXOfT = ball_x + (t+dt) * vel_x;
            ballYOfT = ball_y + (t+dt) * vel_y;
            ballZOfT = ball_z + (t+dt) * vel_z;
            ballPos = new Vector3d(ballXOfT, ballYOfT, ballZOfT);
            rotatedCylinder = rotateCylinderAboutLine(theCyl, theAOR, (t+dt)*omega);
            b2l = closestApproach(rotatedCylinder.p1(), rotatedCylinder.getVector(), ballPos);
            ballPos.add(b2l); // now the point on the line where the point is closest
            double f_prime;
            if(!pointsAreInOrder(rotatedCylinder.p1(), ballPos, rotatedCylinder.p2()))
                f_prime = Double.NaN;
            else
            {
                dist = b2l.length();
                f_prime = dist - distanceToCollision;
                f_prime = (f_prime - f)/dt;
            }
            return new Newton.Result(f, f_prime);
        }
    }
    Newton.Function function = new RotatingCylinderDistance();
    Newton.Result initialDistance = function.evaluate(0);
    if(initialDistance.f <= 0)
    {
        if(initialDistance.f_prime >= 0)		// if they're already in contact but moving apart
            return Double.POSITIVE_INFINITY;
        else
            return 0.0;
    }
    return searchForCollision(function);
  }
  
  /**
   * Computes the time until a ball travelling at a specified
   * velocity collides with a rotating PlaneSegment.
   *
   * @effects computes the time until a Spherical ball
   * travelling at a specified velocity collides with a specified PlaneSegment
   * which is rotating at a fixed angular velocity about a
   * fixed axis of rotation.
   *
   * NOTE: Because I fake the derivative instead of actually computing it, f_prime 
   *   may sometimes by Double.NaN; this may cause problems.
   *
   * @param ps the initial position of the rotating PlaneSegment
   *
   * @param axisOfRotation the axis of rotation for <code>ps</code>
   *
   * @param radsPerSec the angular velocity of the rotation of
   * <code>ps</code> in radians per second.  A positive angular
   * velocity denotes a rotation in the CCW direction with p2 of
   * <code>axisOfRotation</code> being in front of p1.
   *
   * @param ball a Sphere representing the size and initial location of the ball
   *
   * @param trans the initial Transform3D of the ball before impact.  The ball is
   * assumed to travel at a constant velocity until impact.
   *
   * @return the time until collision or <tt>POSITIVE_INFINITY</tt> if no
   * collision was detected.
   *
   * @see Double#POSITIVE_INFINITY
   * @see <a href="#endpoint_effects">endpoint effects</a>
   **/
  public static double timeUntilBallRotatingPlaneSegmentCollision(Sphere ball, Transform3D trans, PlaneSegment ps, Cylinder axisOfRotation, double radsPerSec)
  {
    if(0.0 == radsPerSec)
        return timeUntilBallPlaneCollision(ball, ps, trans);

    Vector3d v = new Vector3d();
    trans.get(v);
    final double vel_x = v.x;
    final double vel_y = v.y;
    final double vel_z = v.z;
    
    v = ball.getCenter();
    final double ball_x = v.x;
    final double ball_y = v.y;
    final double ball_z = v.z;

    final double omega = radsPerSec;

    final Cylinder theAOR = new Cylinder(axisOfRotation.p1(), axisOfRotation.p2(), 0.0, axisOfRotation.name());
    final PlaneSegment thePS = new PlaneSegment(ps.p1(), ps.p2(), ps.p3(), ps.name());

    final double distanceToCollision = ball.getRadius();

    class RotatingPlaneSegmentDistance implements Newton.Function
    {
        public Newton.Result evaluate(double t)
        {
            double ballXOfT = ball_x + t * vel_x;
            double ballYOfT = ball_y + t * vel_y;
            double ballZOfT = ball_z + t * vel_z;
            Vector3d ballPos = new Vector3d(ballXOfT, ballYOfT, ballZOfT);
            PlaneSegment rotatedPS = rotatePlaneSegmentAboutLine(thePS, theAOR, t*omega);
            Vector3d normal = rotatedPS.normal();
            Vector3d vball = new Vector3d();
            vball.sub(ballPos, rotatedPS.p1());
            Vector3d b2p = new Vector3d(normal);
            b2p.scale(vball.dot(normal));
            ballPos.sub(b2p); // now the point on the line where the point is closest
            if(!pointIsInPlaneSegment(ballPos, rotatedPS))
                return new Newton.Result(Double.NaN, Double.NaN);
            double dist = b2p.length();
            double f = dist - distanceToCollision;
            // now fake the derivative
            ballXOfT = ball_x + (t+dt) * vel_x;
            ballYOfT = ball_y + (t+dt) * vel_y;
            ballZOfT = ball_z + (t+dt) * vel_z;
            ballPos = new Vector3d(ballXOfT, ballYOfT, ballZOfT);
            rotatedPS = rotatePlaneSegmentAboutLine(thePS, theAOR, (t+dt)*omega);
            normal = rotatedPS.normal();
            vball = new Vector3d();
            vball.sub(ballPos, rotatedPS.p1());
            b2p = new Vector3d(normal);
            b2p.scale(vball.dot(normal));
            ballPos.sub(b2p); // now the point on the line where the point is closest
            double f_prime;
            if(!pointIsInPlaneSegment(ballPos, rotatedPS))
                f_prime = Double.NaN;
            else
            {
                dist = b2p.length();
                f_prime = dist - distanceToCollision;
                f_prime = (f_prime - f)/dt;
            }
            return new Newton.Result(f, f_prime);
        }
    }
    Newton.Function function = new RotatingPlaneSegmentDistance();
    Newton.Result initialDistance = function.evaluate(0);
    if(initialDistance.f <= 0)
    {
        if(initialDistance.f_prime >= 0)		// if they're already in contact but moving apart
            return Double.POSITIVE_INFINITY;
        else
            return 0.0;
    }
    return searchForCollision(function);
  }
}
