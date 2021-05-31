/*
 * Created on Apr 24, 2003
 *
 * @author Ragu Vijaykumar
 */
package engine;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.media.j3d.BoundingBox;
import javax.media.j3d.Transform3D;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import physics3d.Cylinder;
import physics3d.Geometry;
import physics3d.Physics3dObject;
import physics3d.PlaneSegment;
import physics3d.Sphere;
import physics3d.Trans3DPair;
import engine.Collisions.CollisionInfo;
import engine.Collisions.CollisionIterator;
import engine.backend.BallPhysics;
import engine.backend.GizmoPhysics;
import engine.backend.MobileGizmoPhysics;
import engine.backend.PiecePhysics;
import engine.backend.RotatableGizmoPhysics;
import engine.backend.StaticGizmoPhysics;

/**
 *	Xeon - new performance efficient engine
 *
 *  @specfield gravity - Vector3d // Gravity vector
 *  @specfield MU - double // Coefficient of Friction based on L
 *  @specfield MU_2 - double // Coeffiecient of Frication based on time
 *
 *  @author Ragu Vijaykumar
 */
public final class Xeon {

	private static Xeon engine = null;

	/**
	 * Gravity Vector
	 */
	public static Vector3d GRAVITY = new Vector3d(0, -25, 0);
	
	/**
	 * Coefficient of Friction based on time
	 */
	public static double MU = 0.025;
	
	/**
	 * Coefficient of Friction based on L
	 */
	public static double MU_2 = 0.025;

	private static boolean setup = false;

	private static Set ballSet;
	private static Set ballSpace = new HashSet();

	private static Set gizmoSet;
	private static Set gizmoSpace = new HashSet();
	private static Map gizmoBlocks;

	private static Map playingField;

	private static Partition partition = null;
	private static int PARTITION_X = 22;
	private static int PARTITION_Y = 22;
	private static int PARTITION_Z = 22;

	private static final Collisions collisions =
		Collisions.initialize(20, 0.8f);
	private static Physics3dObject ballShape, pieceShape;
	private static final double TIME_TOLERANCE = 1E-4;
	private static final double VELOCITY_TOLERANCE = 200;
	private static final double ZERO_VELOCITY_TOLERANCE = 4; // 3 L / sec
	private static final Vector3d ZERO_VELOCITY = new Vector3d(0, 0, 0);
	private static final Double ZERO = new Double(0.0);
	private static final Vector3d bVect1 = new Vector3d();
	private static final Vector3d bVect2 = new Vector3d();

	private static int count = 0;
	private static double total = 0;

	/**
	 * No one should instantiate this - empty constuctor
	 */
	private Xeon() {
		// DO NOTHING
	}

	/**
	 * Returns the running instance of the engine
	 * @param balls - collection of balls
	 * @param gizmos - collection of gizmos
	 * @param origin - origin of the playing field in absolute coordinates
	 * @param upperCorner - upper corner of the playing field in absolute coordinates
	 * @return instance of the running engine
	 */
	public static final Xeon buildEngine(
		Set balls,
		Set gizmos,
		Point3d origin,
		Point3d upperCorner) {

		if (balls == null
			|| gizmos == null
			|| origin == null
			|| upperCorner == null)
			throw new NullPointerException("Parameters are null!");

		// System.err.println("Origin: " + origin);
		// System.err.println("Upper: " + upperCorner);

		if (Xeon.engine == null) {

			// First make sure the gizmo set can handle all gizmos
			if (gizmos.size() != 0)
				Xeon.gizmoSet = new HashSet((int) (gizmos.size() * 1.4));
			else
				Xeon.gizmoSet = new HashSet();

			// Make sure the ball set can handle all balls
			if (balls.size() != 0)
				Xeon.ballSet = new HashSet((int) (balls.size() * 1.4));
			else
				Xeon.ballSet = new HashSet();

			Xeon.playingField = new HashMap();
			Xeon.gizmoBlocks = new HashMap(gizmos.size());
			Xeon.ballShape = null;
			Xeon.pieceShape = null;
			Xeon.partition =
				Partition.initialize(
					origin,
					upperCorner,
					Xeon.PARTITION_X,
					Xeon.PARTITION_Y,
					Xeon.PARTITION_Z);

			Xeon.setup(balls, gizmos);

			Xeon.engine = new Xeon();
		}

		return Xeon.engine;
	}

	/**
	 * Setups up the engine to be run
	 * @param balls - set of balls
	 * @param gizmos - set of gizmos
	 */
	private static final void setup(Set balls, Set gizmos) {

		// System.out.println("Setup called!");

		// Add all balls to the game
		Iterator ballIter = balls.iterator();
		while (ballIter.hasNext()) {
			boolean status = Xeon.addPiece((BallPhysics) ballIter.next());
			if (!status)
				throw new RuntimeException("Setup failed in adding balls!");
		}

		// Add all gizmos to the game
		Iterator gizmoIter = gizmos.iterator();
		while (gizmoIter.hasNext()) {
			boolean status = Xeon.addPiece((GizmoPhysics) gizmoIter.next());
			if (!status)
				throw new RuntimeException("Setup failed in adding gizmos!");
		}

		Xeon.setup = true;
		// System.out.println("Setup finished!");

	}

	/**
	 * Resets this engine to be reused for a new simulation
	 * @modifies this
	 */
	public static final void reset() {
		Xeon.engine = null;
		Xeon.setup = false;
	}

	/**
	 * Checks to see if the gizmo is implements a concrete interface
	 * of the physics package
	 * @param gizmo - gizmo to be checked
	 * @return true iff gizmo implements a conccrete sub interface of GizmoPhysics
	 */
	private static final boolean checkConcreteNature(GizmoPhysics gizmo) {

		boolean status = false;

		if (gizmo instanceof StaticGizmoPhysics
			|| gizmo instanceof RotatableGizmoPhysics
			|| gizmo instanceof MobileGizmoPhysics)
			status = true;

		return status;
	}

	/**
	 * Adds a gizmo to the board
	 * @param piece to be added
	 * @return true iff piece was added
	 */
	public static final boolean addPiece(PiecePhysics piece) {
		if (piece == null)
			throw new IllegalArgumentException(
				"Cannot add a null piece: " + piece);

		// Sets up the physics of this piece
		piece.setupPhysics();

		if (piece instanceof GizmoPhysics) {
			GizmoPhysics gizmo = (GizmoPhysics) piece;

			if (!Xeon.checkConcreteNature(gizmo))
				throw new IllegalArgumentException("GizmoPhysics is an abstract interface! Please implement a concrete sub-interface!");

			if (Xeon.gizmoBlocks.containsKey(gizmo))
				return false;

			// Determine where the gizmo is in space
			BoundingBox container = gizmo.getContainer();
			Set blocks = Xeon.partition.getBlocks(container, new HashSet());
			// gizmo.setBlocks(Collections.unmodifiableSet(blocks));

			Xeon.gizmoBlocks.put(gizmo, blocks);

			Iterator blockIter = blocks.iterator();
			while (blockIter.hasNext()) {

				Object key = blockIter.next();

				if (!Xeon.playingField.containsKey(key)) {
					Set gizmos = new HashSet();
					gizmos.add(gizmo);
					playingField.put(key, gizmos);
				} else {
					Set gizmos = (Set) Xeon.playingField.get(key);
					gizmos.add(gizmo);
				}
			}

			return Xeon.gizmoSet.add(gizmo);

		} else if (piece instanceof BallPhysics) {
			return Xeon.ballSet.add(piece);
		} else
			throw new IllegalArgumentException("PiecePhysics is an abstract interface! Please implement a concrete sub-interface!");

	}

	/**
	 * Removes a piece from the playing space's physical simulation
	 * @param piece - piece to be removed
	 * @return true iff piece was removed
	 * @throw IllegalArgumentException - if piece is null or does not implement a concrete interface
	 */
	public static final boolean removePiece(PiecePhysics piece) {
		if (piece == null)
			throw new IllegalArgumentException(
				"Cannot delete a null piece: " + piece);

		if (piece instanceof GizmoPhysics) {
			GizmoPhysics gizmo = (GizmoPhysics) piece;

			if (!Xeon.checkConcreteNature(gizmo))
				throw new IllegalArgumentException("GizmoPhysics is an abstract interface! Please implement a concrete sub-interface!");

			if (!Xeon.gizmoBlocks.containsKey(gizmo))
				return false;

			// Set blocks = gizmo.getBlocks();

			Set blocks = (Set) Xeon.gizmoBlocks.get(gizmo);

			Iterator blockIter = blocks.iterator();
			while (blockIter.hasNext()) {

				Object key = blockIter.next();
				Object value = Xeon.playingField.get(key);

				if (value != null) {
					Set gizmos = (Set) value;
					gizmos.remove(gizmo);

					if (gizmos.size() == 0)
						Xeon.playingField.remove(key);
				}
			}

			return Xeon.gizmoSet.remove(gizmo);

		} else if (piece instanceof BallPhysics)
			return Xeon.ballSet.remove(piece);
		else
			throw new IllegalArgumentException("PiecePhysics is an abstract interface! Please implement a concrete sub-interface!");

	}

	/**
	 * Updates the simulation by the time
	 * @param timeSlice - amount of time to animate for between frames
	 */
	public static void update(double timeSlice) {

		// System.exit(0);

		if (!Xeon.setup)
			throw new RuntimeException("Setup has not been successfully completed on Xeon engine!");

		if (timeSlice <= 0) {
			throw new IllegalArgumentException("Time Slice is less than or equal to 0!");
		}

		// System.err.println("-------RUNNING UPDATE------");

		double timeLeft = timeSlice;

		while (timeLeft > 0) {
			// While there is still time to check for collisions

			double minTime = timeLeft;
			Iterator ballIter = Xeon.ballSet.iterator();

			while (ballIter.hasNext()) {
				// For every ball, check for potential collisions
				// Find minimum time until a collision

				BallPhysics ball = (BallPhysics) ballIter.next();
				if (ball.isPhysical()) {

					// Get all regions this ball could traverse 
					Set ballSpace =
						Xeon.ballSpace(ball, minTime, Xeon.ballSpace);
					// System.err.println(ballSpace);
					//					System.err.println(
					//						"Ball block: "
					//							+ Xeon.partition.getBlock(ball.getLocation()));
					// Get all gizmos this ball could potentially collide with
					Set gizmoSpace =
						Xeon.getGizmoSpace(ballSpace, Xeon.gizmoSpace);
					// System.err.println("Gizmo Space: " + gizmoSpace);
					// Check for collisions with gizmos
					Iterator gizmoIter = gizmoSpace.iterator();

					while (gizmoIter.hasNext()) {

						GizmoPhysics gizmo = (GizmoPhysics) gizmoIter.next();

						if (gizmo.isPhysical()) {

							double updateTime = Double.POSITIVE_INFINITY;

							if (gizmo instanceof StaticGizmoPhysics)
								updateTime =
									Xeon.timeUntilCollision(
										ball,
										(StaticGizmoPhysics) gizmo);
							else if (gizmo instanceof RotatableGizmoPhysics)
								updateTime =
									Xeon.timeUntilCollision(
										ball,
										(RotatableGizmoPhysics) gizmo);
							else if (gizmo instanceof MobileGizmoPhysics)
								updateTime =
									Xeon.timeUntilCollision(
										ball,
										(MobileGizmoPhysics) gizmo);
							else
								throw new IllegalArgumentException("Gizmo is not a valid type!");

							if (updateTime < minTime) {
								minTime = updateTime;
								Xeon.collisions.setMinCollision(
									ball,
									Xeon.ballShape,
									gizmo,
									Xeon.pieceShape);
							}
						}
					}

					// Check for collisions with other balls
					Iterator balls = Xeon.ballSet.iterator();

					while (balls.hasNext()) {
						BallPhysics oBall = (BallPhysics) balls.next();
						double updateTime = Double.POSITIVE_INFINITY;

						if (oBall.isPhysical()
							&& !oBall.equals(ball)
							&& !Xeon.collisions.isPresent(oBall, ball)) {
							// if these two balls are not the same ball
							// OPTIMIZE - This method can be optimized	
							updateTime = Xeon.timeUntilCollision(ball, oBall);
						}

						if (updateTime < minTime) {
							minTime = updateTime;
						}
					}
				}
			}

			// System.err.println("Fast Forwarded: " + (minTime * 1000) + " ms");

			if (minTime > 0.0) {
				Xeon.fastForward(ballSet, gizmoSet, minTime);
			}

			if (minTime < timeLeft) {
				// There are collisions to be processed

				// Find all collisions that will happen
				// System.err.println("Finding collisions!");
				Xeon.findCollisions(Xeon.TIME_TOLERANCE);

				// Process all those collisions
				// System.err.println("Processing collisions!");
				Xeon.processCollisions();

				timeLeft -= minTime;
				// System.err.println(
				//	"Done processing collisions - Time Left in slice: " + (timeLeft * 1000) + " ms");
			} else
				timeLeft = 0;

		}

		// Update all balls and gizmos				
		Xeon.updateBalls(Xeon.ballSet, timeSlice);
		Xeon.updateGizmos(Xeon.gizmoSet, timeSlice);

		// System.err.println("-------COMPLETED UPDATE------");
	}

	/**
	 * Computes whether the ball has 0 velocity
	 * @return true iff the ball is virtually a zero velocity ball
	 * @deprecated - ball animation is smooth enough to not have to hard
	 * code check this
	 */
	private static boolean zeroVelocity(BallPhysics ball) {

		Transform3D vel = ball.getVelocity();
		Vector3d velocity = new Vector3d();
		vel.get(velocity);

		if (velocity.length() < 1E-6)
			return true;
		else
			return false;
	}

	/**
	 *
	 * Returns all the partition blocks that the ball could potentially travel in
	 * @param ball - ball that is to be projected
	 * @param timeSlice - time the ball has to travel in
	 * @param ballSpace - where to put all blocks that have been found
	 * @return - set of all blocks that could be travelled in that amount of time
	 */
	private static Set ballSpace(
		BallPhysics ball,
		double timeSlice,
		Set ballSpace) {

		if (ball == null)
			throw new NullPointerException("Piece is null!");

		ballSpace.clear();

		// Get the ball's location
		Point3d ballOldLocation = ball.getLocation();

		// Get the velocity of the ball
		Transform3D ballVelocity = ball.getVelocity();
		Vector3d velocity = new Vector3d();
		ballVelocity.get(velocity);

		// Add gravitational component
		Vector3d gravity = new Vector3d(Xeon.GRAVITY);
		gravity.scale(0.5);
		gravity.scale(timeSlice);
		gravity.scale(timeSlice);

		Point3d ballNewLocation = new Point3d(ballOldLocation);
		velocity.scale(timeSlice); // where is this going in the time slice
		ballNewLocation.add(velocity);

		//		System.err.println("Ball Space: ");
		//		System.err.println(
		//			"Old Location: " + Xeon.partition.getBlock(ball.getLocation()));
		//		System.err.println(
		//			"New Location: " + Xeon.partition.getBlock(ballNewLocation));

		// Need to find the absolute lower bound where the ball is to go

		double x, y, z;
		double radius = ball.getRadius();

		if (ballOldLocation.x < ballNewLocation.x)
			x = ballOldLocation.x - radius;
		else
			x = ballNewLocation.x - radius;

		if (ballOldLocation.y < ballNewLocation.y)
			y = ballOldLocation.y - radius;
		else
			y = ballNewLocation.y - radius;

		if (ballOldLocation.z < ballNewLocation.z)
			z = ballOldLocation.z - radius;
		else
			z = ballNewLocation.z - radius;

		Point3d lower = new Point3d(x, y, z);

		// Need to find absolute upper bound where ball is to go

		if (ballOldLocation.x > ballNewLocation.x)
			x = ballOldLocation.x + radius;
		else
			x = ballNewLocation.x + radius;

		if (ballOldLocation.y > ballNewLocation.y)
			y = ballOldLocation.y + radius;
		else
			y = ballNewLocation.y + radius;

		if (ballOldLocation.z > ballNewLocation.z)
			z = ballOldLocation.z + radius;
		else
			z = ballNewLocation.z + radius;

		Point3d upper = new Point3d(x, y, z);

		//		System.err.println("After calc: ");
		//		System.err.println(
		//			"Old Location: " + Xeon.partition.getBlock(lower));
		//		System.err.println(
		//			"New Location: " + Xeon.partition.getBlock(upper));

		BoundingBox newSpace = new BoundingBox(lower, upper);

		// Get all partition blocks in that new space
		ballSpace = Xeon.partition.getBlocks(newSpace, ballSpace);
		// System.out.println(ball);
		// System.out.println(ballSpace);

		return ballSpace;
	}

	/**
	 * Obtains all gizmos that are in the ball space
	 * @param pieceSpace - partition blocks that the piece could potentially fall into
	 * @return - set of all gizmos in this space
	 */
	private static Set getGizmoSpace(Set pieceSpace, Set gizmoSpace) {

		Iterator spaceIter = pieceSpace.iterator();
		gizmoSpace.clear();

		while (spaceIter.hasNext()) {
			Partition block = (Partition) spaceIter.next();

			Object gizmos = playingField.get(block);
			if (gizmos != null)
				gizmoSpace.addAll((Set) gizmos);
		}

		return gizmoSpace;
	}

	/**
	 * Calculates the time until collision between two balls and stores the 
	 * physical objects of the collision in the two shape parameters
	 * @param ball 
	 * @param ballShape
	 * @param ball2
	 * @param ball2Shape
	 * @return time until collision
	 */
	private static double timeUntilCollision(
		BallPhysics ball,
		BallPhysics ball2) {

		if (ball == null)
			throw new NullPointerException("Ball is null!");
		else if (ball2 == null)
			throw new NullPointerException("Ball2 is null!");

		Set physics = ball.getPhysicalComponents();
		Iterator physicsIter = physics.iterator();
		Xeon.ballShape = (Sphere) physicsIter.next();

		physics = ball2.getPhysicalComponents();
		physicsIter = physics.iterator();
		Xeon.pieceShape = (Sphere) physicsIter.next();

		double time =
			Geometry.timeUntilBallBallCollision(
				(Sphere) Xeon.ballShape,
				ball.getVelocity(),
				(Sphere) Xeon.pieceShape,
				ball2.getVelocity());

		time = Xeon.cleanCollisionTime(time);
		return time;
	}

	/**
	 * Determines the amount of time before the ball collides with a gizmo
	 * @param ball - ball that could collide with gizmo
	 * @param gizmo - gizmo that could be collided with
	 * @return - time until the collision
	 */
	private static double timeUntilCollision(
		BallPhysics ball,
		StaticGizmoPhysics gizmo) {

		// System.out.println("Time until Static Gizmo!");

		if (ball == null)
			throw new NullPointerException("Ball is null!");
		else if (gizmo == null)
			throw new NullPointerException("Gizmo is null!");

		double time = Double.POSITIVE_INFINITY;

		Set physics = ball.getPhysicalComponents();
		Iterator physicsIter = physics.iterator();
		Xeon.ballShape = (Sphere) physicsIter.next();

		// Need to check all physical components that comprise the gizmo
		physics = gizmo.getPhysicalComponents();
		physicsIter = physics.iterator();

		//				if (gizmo instanceof TriangularBumperA3D) {
		//					System.err.println("Time Until Collision: " + gizmo);
		//					System.err.println("Ball Location: " + ball.getLocation());
		//					System.err.println("Ball Velocity:\n " + ball.getVelocity());
		//				}

		while (physicsIter.hasNext()) {
			Object shape = physicsIter.next();

			double collisionTime = Double.POSITIVE_INFINITY;

			if (shape instanceof Cylinder)
				collisionTime =
					Geometry.timeUntilBallCylinderCollision(
						(Sphere) Xeon.ballShape,
						(Cylinder) shape,
						ball.getVelocity());
			else if (shape instanceof PlaneSegment)
				collisionTime =
					Geometry.timeUntilBallPlaneCollision(
						(Sphere) Xeon.ballShape,
						(PlaneSegment) shape,
						ball.getVelocity());
			else if (shape instanceof Sphere)
				collisionTime =
					Geometry.timeUntilBallSphereCollision(
						(Sphere) Xeon.ballShape,
						(Sphere) shape,
						ball.getVelocity());
			else
				throw new IllegalArgumentException("Physics Object not recognized!");

			collisionTime = Xeon.cleanCollisionTime(collisionTime);

			if (collisionTime < time) {
				time = collisionTime;
				Xeon.pieceShape = (Physics3dObject) shape;
			}

			if (collisionTime == 0) {
				// Value cannot be smaller than 0.0

				Xeon.pieceShape = (Physics3dObject) shape;

				//				System.err.println(
				//					"Time Until Collision Ball-Sg:\n"
				//						+ ball
				//						+ "\nVelocity\n"
				//						+ ball.getVelocity()
				//						+ "Location: "
				//						+ ball.getLocation()
				//						+ "\nTime: "
				//						+ (collisionTime * 1000)
				//						+ " ms with shape\n"
				//						+ shape
				//						+ "\non gizmo "
				//						+ gizmo);

				return collisionTime;
			}
		}

		return time;
	}

	/**
	 * Returns the time until collision with a ball and a rotating gizmo
	 * @param ball
	 * @param gizmo
	 * @return
	 */
	private static double timeUntilCollision(
		BallPhysics ball,
		RotatableGizmoPhysics gizmo) {

		// System.out.println("There is a rotating gizmo!");

		if (ball == null)
			throw new NullPointerException("Ball is null!");
		else if (gizmo == null)
			throw new NullPointerException("Gizmo is null!");

		double time = Double.POSITIVE_INFINITY;

		Set physics = ball.getPhysicalComponents();
		Iterator physicsIter = physics.iterator();
		Xeon.ballShape = (Sphere) physicsIter.next();

		// Need to check all physical components that comprise the gizmo
		physics = gizmo.getPhysicalComponents();
		physicsIter = physics.iterator();

		while (physicsIter.hasNext()) {
			Object shape = physicsIter.next();

			double collisionTime = Double.POSITIVE_INFINITY;

			if (shape instanceof Cylinder)
				collisionTime =
					Geometry.timeUntilBallRotatingCylinderCollision(
						(Sphere) Xeon.ballShape,
						ball.getVelocity(),
						(Cylinder) shape,
						gizmo.getAxisOfRotation(),
						gizmo.getAngularVelocity());
			else if (shape instanceof PlaneSegment)
				collisionTime =
					Geometry.timeUntilBallRotatingPlaneSegmentCollision(
						(Sphere) Xeon.ballShape,
						ball.getVelocity(),
						(PlaneSegment) shape,
						gizmo.getAxisOfRotation(),
						gizmo.getAngularVelocity());

			else if (shape instanceof Sphere)
				collisionTime =
					Geometry.timeUntilBallRotatingSphereCollision(
						(Sphere) Xeon.ballShape,
						ball.getVelocity(),
						(Sphere) shape,
						gizmo.getAxisOfRotation(),
						gizmo.getAngularVelocity());
			else
				throw new IllegalArgumentException("Physics Object not recognized!");

			collisionTime = Xeon.cleanCollisionTime(collisionTime);

			if (collisionTime < time) {
				time = collisionTime;
				Xeon.pieceShape = (Physics3dObject) shape;

			}

			if (collisionTime == 0) {
				// Value cannot be smaller than 0.0

				//				System.err.println(
				//					"Time Until Collision Ball-Sg:\n"
				//						+ ball
				//						+ "\nVelocity\n"
				//						+ ball.getVelocity()
				//						+ "Location: "
				//						+ ball.getLocation()
				//						+ "\nTime: "
				//						+ (collisionTime * 1000)
				//						+ " ms with shape\n"
				//						+ shape
				//						+ "\non gizmo "
				//						+ gizmo);
				// System.err.println("Time Until Collision: " + collisionTime);
				return collisionTime;
			}
		}

		//		System.err.println("Time Until Collision: " + gizmo);
		//		System.err.println("Ball Location: " + ball.getLocation());
		//		System.err.println("Ball Velocity: " + ball.getVelocity());
		//		System.err.println(
		//			"Time Until Collision Ball-Sg: "
		//				+ (time * 1000)
		//				+ " ms with shape "
		//				+ Xeon.pieceShape
		//				+ " on gizmo "
		//				+ gizmo
		//				+ "\n");

		//		if (time < Double.POSITIVE_INFINITY) {
		//
		//			System.err.println("Time Until Collision: " + time);
		//			System.exit(0);
		//		}
		return time;
	}

	/**
	 * @param ball
	 * @param gizmo
	 * @return
	 */
	private static double timeUntilCollision(
		BallPhysics ball,
		MobileGizmoPhysics gizmo) {

		if (ball == null)
			throw new NullPointerException("Ball is null!");
		else if (gizmo == null)
			throw new NullPointerException("Gizmo is null!");

		// Effective Velocity
		Vector3d eVel = new Vector3d();
		Transform3D eTrans = new Transform3D();

		// Ball Information
		Vector3d bVel = new Vector3d();
		ball.getVelocity().get(bVel);
		Set physics = ball.getPhysicalComponents();
		Iterator physicsIter = physics.iterator();
		Xeon.ballShape = (Sphere) physicsIter.next();

		// Get mapping from physical components to their velocities
		Map cVels = gizmo.getComponentVelocities();
		Vector3d cVel = new Vector3d();
		Set components = gizmo.getPhysicalComponents();

		double time = Double.POSITIVE_INFINITY;

		Iterator cIter = components.iterator();
		while (cIter.hasNext()) {
			Physics3dObject shape = (Physics3dObject) cIter.next();
			((Transform3D) cVels.get(shape.getName())).get(cVel);
			
			eVel.sub(bVel, cVel);
			eTrans.set(eVel);

			double collisionTime = Double.POSITIVE_INFINITY;

			if (shape instanceof Cylinder)
				collisionTime =
					Geometry.timeUntilBallCylinderCollision(
						(Sphere) Xeon.ballShape,
						(Cylinder) shape,
						eTrans);
			else if (shape instanceof PlaneSegment)
				collisionTime =
					Geometry.timeUntilBallPlaneCollision(
						(Sphere) Xeon.ballShape,
						(PlaneSegment) shape,
						eTrans);
			else if (shape instanceof Sphere)
				collisionTime =
					Geometry.timeUntilBallSphereCollision(
						(Sphere) Xeon.ballShape,
						(Sphere) shape,
						eTrans);
			else
				throw new IllegalArgumentException("Physics Object not recognized!");

			collisionTime = Xeon.cleanCollisionTime(collisionTime);

			if (collisionTime < time) {
				time = collisionTime;
				Xeon.pieceShape = (Physics3dObject) shape;
			}

			if (collisionTime == 0)
				return collisionTime;
		}

		// System.out.println("Time Until Mobile Collision: " + time);
		// System.exit(0);
		return time;
	}

	/**
	 * @param ballSet
	 * @param gizmoSet
	 * @param timeSlice
	 */
	private static void fastForward(
		Set ballSet,
		Set gizmoSet,
		double timeSlice) {

		if (ballSet == null || gizmoSet == null)
			throw new NullPointerException("Ball Collection / Gizmo Collection is null!");
		else if (timeSlice < 0)
			throw new IllegalArgumentException(
				"Time left is less than 0: " + timeSlice);

		// System.err.println("Fast Forwarding: " + (timeSlice*1000) + " ms");
		
		// Fast forward all gizmos
		Iterator gizmoIter = gizmoSet.iterator();
		while (gizmoIter.hasNext()) {
			GizmoPhysics gizmo = (GizmoPhysics) gizmoIter.next();
			if (gizmo.isPhysical()) {
				if (gizmo instanceof StaticGizmoPhysics)
					gizmo.updatePhysics(timeSlice);
				else if (gizmo instanceof RotatableGizmoPhysics)
					gizmo.updatePhysics(timeSlice);
				else if (gizmo instanceof MobileGizmoPhysics) {
					Xeon.updateMobileGizmoPhysics(
						(MobileGizmoPhysics) gizmo,
						timeSlice);
				}
			}
		}

		Iterator ballIter = ballSet.iterator();
		while (ballIter.hasNext()) {
			
			// Update all balls

			BallPhysics ball = (BallPhysics) ballIter.next();

			if (ball.isPhysical()) {

				// Obtain the translational component of the ball's velocity
				Transform3D ballVelocity = ball.getVelocity();
				Vector3d velocity = new Vector3d();
				ballVelocity.get(velocity);
				// System.err.println("Ball Old Loc: " + ball.getLocation());
				Point3d ballNewLocation = new Point3d(ball.getLocation());
				velocity.scale(timeSlice);

				// Gravitational Velocity
				Vector3d gravity = new Vector3d(Xeon.GRAVITY);
				gravity.scale(0.5);
				gravity.scale(timeSlice);
				gravity.scale(timeSlice);

				// where is this going in the time slice
				ballNewLocation.add(velocity);
				ballNewLocation.add(gravity);

				ball.setLocation(new Point3d(ballNewLocation));

				//				System.err.println(
				//					"Fast Forwarded "
				//						+ (timeSlice * 1000)
				//						+ ": "
				//						+ Xeon.partition.getBlock(ball.getLocation()));
				//				// System.err.println("Velocity: \n" + ball.getVelocity());
				//				System.err.println();

				ball.updatePhysics(timeSlice);
			}
		}
	}

	/**
	 * Updates the physics of mobile gizmos
	 * @param gizmo - mobile gizmo
	 * @param timeSlice - time that has elapsed
	 */
	private static void updateMobileGizmoPhysics(
		MobileGizmoPhysics mobileGizmo,
		double timeSlice) {

		// System.out.println("Updating Mobile Gizmo!");
		
		// System.out.println("Mobile Gizmo Bounding Box: " + mobileGizmo.getContainer());
		
		// System.out.println(mobileGizmo);
		// System.out.println(mobileGizmo.getContainer());

		if (mobileGizmo.isMoving()) {
			// Only if the mobile gizmo is still moving
			// System.out.println("Mobile Gizmo is Moving!");
			BoundingBox oldContainer = mobileGizmo.getContainer();
			BoundingBox newContainer = mobileGizmo.getNextContainer(timeSlice);

			// Get all blocks that this gizmo is currently in
			Set currentSpace =
				Xeon.partition.getBlocks(mobileGizmo.getContainer());

			// Remove it from the playingSpace
			Iterator spaceIter = currentSpace.iterator();
			while (spaceIter.hasNext()) {
				Object value = Xeon.playingField.get(spaceIter.next());
				if (value != null) {
					Set gizmos = (Set) value;
					gizmos.remove(mobileGizmo);
				}
			}

			// Get all partition blocks in that new space
			Set mobileSpace = Xeon.partition.getBlocks(newContainer);

			Set pieceSpace = Xeon.getGizmoSpace(mobileSpace, new HashSet());
			pieceSpace.addAll(Xeon.ballSet);
			
			Iterator pieceIter = pieceSpace.iterator();
			// System.out.println("Gizmo Space: " + gizmoSpace); 
			while (pieceIter.hasNext()) {
				PiecePhysics piece = (PiecePhysics) pieceIter.next();

				if (!piece.equals(mobileGizmo)) {

					BoundingBox container = piece.getContainer();

					if (newContainer.intersect(container) && !oldContainer.intersect(container)) {
						container = mobileGizmo.collision(container);
						mobileGizmo.setContainer(container);
					} else {
						mobileGizmo.setContainer(newContainer);
					}
					newContainer = mobileGizmo.getContainer();
				}

				mobileGizmo.updatePhysics(timeSlice);
			}

			// Get all blocks that this gizmo is currently in
			currentSpace = Xeon.partition.getBlocks(mobileGizmo.getContainer());
			
			Xeon.gizmoBlocks.put(mobileGizmo, currentSpace);
			// System.out.println(currentSpace);

			// Add it to the playingSpace
			spaceIter = currentSpace.iterator();
			while (spaceIter.hasNext()) {
				
				Partition space = (Partition) spaceIter.next();
				Object value = Xeon.playingField.get(space);
				if (value != null) {
					Set gizmos = (Set) value;
					gizmos.add(mobileGizmo);
				} else {
					Set gizmos = new HashSet();
					gizmos.add(mobileGizmo);
					Xeon.playingField.put(space, gizmos);
				}
			}
		}
	}

	/**
	 * Finds all collisions that occur within a tolerance value
	 * specified by the engine
	 * @param timeSlice - tolerance to find collisions within
	 */
	private static void findCollisions(double timeSlice) {

		// First need to recheck the minimum collision
		if (Xeon.collisions.getMinCollision().piece instanceof BallPhysics) {
			// Ball-ball collisions are always added as collisions
			Xeon.collisions.addCollision(Xeon.collisions.getMinCollision());
		} else if (
			Xeon.collisions.getMinCollision().piece
				instanceof StaticGizmoPhysics) {
			// Static gizmos are always added as collisions
			Xeon.collisions.addCollision(Xeon.collisions.getMinCollision());

		} else if (
			Xeon.collisions.getMinCollision().piece
				instanceof RotatableGizmoPhysics) {
			// Need to recheck the time since rotatable gizmos can stop

			double updateTime = Double.POSITIVE_INFINITY;
			updateTime =
				Xeon.timeUntilCollision(
					Xeon.collisions.getMinCollision().ball,
					(RotatableGizmoPhysics) Xeon
						.collisions
						.getMinCollision()
						.piece);

			if (updateTime < Xeon.TIME_TOLERANCE) {
				Xeon.collisions.addCollision(Xeon.collisions.getMinCollision());
			} else {
				Xeon.collisions.setMinCollision(null, null, null, null);
			}

		} else if (
			Xeon.collisions.getMinCollision().piece
				instanceof MobileGizmoPhysics) {
			// Need to recheck the time since mobile gizmos can stop

			// MobileGizmoPhysics mobileGizmo = (MobileGizmoPhysics) Xeon.collisions.getMinCollision().piece;

			double updateTime = Double.POSITIVE_INFINITY;
			updateTime =
				Xeon.timeUntilCollision(
					Xeon.collisions.getMinCollision().ball,
					(MobileGizmoPhysics) Xeon
						.collisions
						.getMinCollision()
						.piece);

			if (updateTime < Xeon.TIME_TOLERANCE) {
				Xeon.collisions.addCollision(
					Xeon.collisions.getMinCollision().ball,
					Xeon.ballShape,
					Xeon.collisions.getMinCollision().piece,
					Xeon.pieceShape);
			} else {
				Xeon.collisions.setMinCollision(null, null, null, null);
			}
		} else {
			throw new IllegalArgumentException(
				"Type of piece unrecognized: "
					+ Xeon.collisions.getMinCollision().piece);
		}

		Iterator ballIter = Xeon.ballSet.iterator();

		while (ballIter.hasNext()) {
			// For every ball, check for potential collisions
			// Find minimum time until a collision

			BallPhysics ball = (BallPhysics) ballIter.next();

			if (ball.isPhysical()
				&& !ball.equals(Xeon.collisions.getMinCollision().ball)) {

				// Get all regions this ball could traverse 
				Set ballSpace = Xeon.ballSpace(ball, timeSlice, Xeon.ballSpace);

				// Get all gizmos this ball could potentially collide with
				Set gizmoSpace = Xeon.getGizmoSpace(ballSpace, Xeon.gizmoSpace);

				// Check for collisions with gizmos
				Iterator gizmoIter = gizmoSpace.iterator();

				while (gizmoIter.hasNext()) {

					GizmoPhysics gizmo = (GizmoPhysics) gizmoIter.next();
					if (gizmo.isPhysical()) {

						double updateTime = Double.POSITIVE_INFINITY;

						if (gizmo instanceof StaticGizmoPhysics)
							updateTime =
								Xeon.timeUntilCollision(
									ball,
									(StaticGizmoPhysics) gizmo);
						else if (gizmo instanceof RotatableGizmoPhysics)
							updateTime =
								Xeon.timeUntilCollision(
									ball,
									(RotatableGizmoPhysics) gizmo);
						else if (gizmo instanceof MobileGizmoPhysics)
							updateTime =
								Xeon.timeUntilCollision(
									ball,
									(MobileGizmoPhysics) gizmo);
						else
							throw new IllegalArgumentException("Gizmo is not a valid type!");

						if (updateTime <= timeSlice) {
							Xeon.collisions.addCollision(
								ball,
								Xeon.ballShape,
								gizmo,
								Xeon.pieceShape);
						}
					}
				}

				// Check for collisions with other balls
				Iterator balls = Xeon.ballSet.iterator();

				while (balls.hasNext()) {
					BallPhysics oBall = (BallPhysics) balls.next();
					double updateTime = Double.POSITIVE_INFINITY;

					if (ball.isPhysical()
						&& !oBall.equals(ball)
						&& !Xeon.collisions.isPresent(oBall, ball)) {
						// if these two balls are not the same ball
						// OPTIMIZE - This method can be optimized	
						updateTime = Xeon.timeUntilCollision(ball, oBall);
					}

					if (updateTime <= timeSlice) {
						Xeon.collisions.addCollision(
							ball,
							Xeon.ballShape,
							oBall,
							Xeon.pieceShape);
					}
				}
			}
		}
	}

	private static void processCollisions() {

		// Trigger collision that had the minimum time
		// Xeon.triggerCollision(Xeon.collisions.getMinCollsion());

		// System.err.println(
		// 	"Number of collisions: " + (Xeon.collisions.size() + 1));

		// Trigger all other collisions that were close to this collision
		CollisionIterator cIter = Xeon.collisions.getCollisions();
		while (cIter.hasNext()) {
			CollisionInfo info = cIter.next();
			Xeon.triggerCollision(info);
		}

		Xeon.collisions.clear();
	}

	private static void triggerCollision(CollisionInfo info) {

		if (info.piece instanceof BallPhysics)
			Xeon.triggerBallCollision(info);
		else if (info.piece instanceof StaticGizmoPhysics)
			Xeon.triggerStaticCollision(info);
		else if (info.piece instanceof RotatableGizmoPhysics)
			Xeon.triggerRotateCollision(info);
		else if (info.piece instanceof MobileGizmoPhysics)
			Xeon.triggerMobileCollision(info);
	}

	/**
	 * @param info - collision information
	 */
	private static void triggerBallCollision(CollisionInfo info) {

		if (info == null)
			throw new NullPointerException("Collision Information is null!");

		//		System.err.println("Triggering ball-ball collision");

		// Ball One Information
		BallPhysics ball = info.ball;
		Sphere ballSphere = (Sphere) info.ballShape;

		// Ball Two Information
		BallPhysics ball2 = (BallPhysics) info.piece;
		Sphere ball2Sphere = (Sphere) info.pieceShape;

		//		System.err.println(
		//			ball.toString()
		//				+ "\n"
		//				+ ball.getVelocity()
		//				+ " Location: "
		//				+ ball.getLocation()
		//				+ " Radius: "
		//				+ ball.getRadius());
		//		System.err.println(
		//			ball2.toString()
		//				+ "\n"
		//				+ ball2.getVelocity()
		//				+ " Location: "
		//				+ ball2.getLocation()
		//				+ " Radius: "
		//				+ ball2.getRadius());
		//
		// Trigger this collision
		Trans3DPair velocityPair =
			Geometry.reflectBalls(
				ballSphere,
				ball.getMass(),
				ball.getVelocity(),
				ball2Sphere,
				ball2.getMass(),
				ball2.getVelocity());

		Transform3D vel1 = velocityPair.t1();
		Transform3D vel2 = velocityPair.t2();

		//		// Normalize
		//		vel1.get(Xeon.bVect1);
		//		vel2.get(Xeon.bVect2);
		//
		//		if (bVect1.length() > Xeon.VELOCITY_TOLERANCE) {
		//			System.err.println("Before normalization ball velocity: " + bVect1);
		//			bVect1.normalize();
		//			bVect1.scale(Xeon.VELOCITY_TOLERANCE);
		//			System.err.println("After normalization ball velocity: " + bVect1);
		//			vel1.set(bVect1);
		//		}
		//
		//		if (bVect2.length() > Xeon.VELOCITY_TOLERANCE) {
		//			bVect2.normalize();
		//			bVect2.scale(Xeon.VELOCITY_TOLERANCE);
		//			vel2.set(bVect2);
		//		}

		// Update the balls with the information after the collision
		ball.setVelocity(vel1);
		ball2.setVelocity(vel2);

		//		System.err.println("New " + ball + " velocity: \n" + velocityPair.t1());
		//		System.err.println(
		//			"New " + ball2 + " velocity: \n" + velocityPair.t2());

		/*
			Point3d b1loc = new Point3d(ball.getLocation());
			Point3d b2loc = new Point3d(ball2.getLocation());
			b1loc.sub(b2loc);
			b1loc.absolute();
			Point3d dist = new Point3d(0.5, 0.49, 0.5);
			dist.sub(b1loc);
			if (dist.y > 0.0)
				throw new RuntimeException(
					"Balls are in the same place: "
						+ ball.getLocation()
						+ " "
						+ ball2.getLocation());
						*/

	}

	/**
	 * @param info
	 */
	private static void triggerStaticCollision(CollisionInfo info) {
		if (info == null)
			throw new NullPointerException("Collision Information is null!");

		// Ball information
		BallPhysics ball = info.ball;
		Sphere ballSphere = (Sphere) info.ballShape;

		// Static gizmo information
		GizmoPhysics gizmo = (GizmoPhysics) info.piece;
		Physics3dObject gizmoShape = info.pieceShape;

		// System.err.println("Triggering ball-SG Collision: " + gizmo);

		// System.err.println("Ball Collision: " + ball.getLocation());

		Transform3D newVelocity;

		// System.err.println(ball.toString() + "\nOld Velocity: ");
		// System.err.println(ball.getVelocity());

		if (gizmoShape instanceof Sphere)
			newVelocity =
				Geometry.reflectBallFromSphere(
					ballSphere,
					(Sphere) gizmoShape,
					ball.getVelocity(),
					gizmo.getReflectCoeff());
		else if (gizmoShape instanceof Cylinder)
			newVelocity =
				Geometry.reflectBallFromCylinder(
					ballSphere,
					(Cylinder) gizmoShape,
					ball.getVelocity(),
					gizmo.getReflectCoeff());
		else if (gizmoShape instanceof PlaneSegment)
			newVelocity =
				Geometry.reflectBallFromPlane(
					(PlaneSegment) gizmoShape,
					ball.getVelocity(),
					gizmo.getReflectCoeff());
		else
			throw new IllegalArgumentException(
				"Physics Object is null or not valid: " + gizmoShape);

		// 		Normalization:
		//
		//		newVelocity.get(bVect1);
		//		if (bVect1.length() > Xeon.VELOCITY_TOLERANCE) {
		//			System.err.println("Before normalization ball velocity: " + bVect1);
		//			bVect1.normalize();
		//			bVect1.scale(Xeon.VELOCITY_TOLERANCE);
		//			System.err.println("After normalization ball velocity: " + bVect1);
		//			newVelocity.set(bVect1);
		//		}

		// Make 0 L/sec balls

		//		if (gizmoShape instanceof PlaneSegment) {
		//			// Ball can rest on a plane
		//
		//			Vector3d velocity = new Vector3d();
		//			newVelocity.get(velocity);
		//
		//			System.err.println(velocity);
		//		}
		//
		//			if (false && velocity.length() < Xeon.ZERO_VELOCITY_TOLERANCE) {
		//				// Consider that ball sits on this plane
		//
		//				// System.err.println("Possible zero velocity");
		//
		//				velocity.normalize();
		//
		//				Vector3d gravity = new Vector3d(Xeon.GRAVITY);
		//				gravity.normalize();
		//
		//				double dot = velocity.dot(gravity);
		//				// Velocity after collision should be in opposite 
		//				// direction than gravity
		//				// Value should be -1
		//
		//				dot = dot + 1;
		//
		//				if (dot < 1E-8 && dot > -1E-8) {
		//					// velocity vector points in the same direction as gravity
		//
		//					PlaneSegment ps = (PlaneSegment) gizmoShape;
		//					Vector3d normal = ps.normal();
		//					normal.normalize();
		//
		//					newVelocity.get(velocity);
		//					velocity.normalize();
		//
		//					// Can return +/- 1
		//					dot = velocity.dot(normal);
		//					// System.err.println(
		//					// 	"Points in same direction as gravity: " + dot);
		//
		//					dot = dot - 1;
		//
		//					if (dot < 1E-8 && dot > -1E-8) {
		//						// Velocity is in same direction as normal
		//						// System.err.println("Setting zero velocity");
		//						newVelocity.set(Xeon.ZERO_VELOCITY);
		//
		//					} else {
		//						dot = velocity.dot(normal);
		//						dot = dot + 1;
		//
		//						if (dot < 1E-8 && dot > -1E-8) {
		//							// Velocity is in opposite direction of normal
		//							// System.err.println("Setting zero velocity");
		//							newVelocity.set(Xeon.ZERO_VELOCITY);
		//						}
		//					}
		//				}
		//			}
		//		}

		// Update the ball and trigger all necessary connections
		ball.setVelocity(newVelocity);
		gizmo.ballCollision(ball);
		gizmo.triggerConnections();

		//		System.err.println("New Velocity: ");
		//		System.err.println(newVelocity);
		//		System.err.println("Physical Object: " + gizmoShape);
		//		System.err.println("Sphere: " + ballSphere);
		//		System.err.println("Bounced: " + ball.getLocation());

		//		if (gizmo.toString().trim().equals("TA12"))
		//			System.exit(0);
	}

	/**
	 * Triggers a collision with a rotating gizmo
	 * @param info - information related to the rotating gizmo collision
	 */
	private static void triggerRotateCollision(CollisionInfo info) {

		if (info == null)
			throw new NullPointerException("Collision Information is null!");

		// Ball information
		BallPhysics ball = info.ball;
		Sphere ballSphere = (Sphere) info.ballShape;

		// Rotatable gizmo information
		RotatableGizmoPhysics gizmo = (RotatableGizmoPhysics) info.piece;
		Physics3dObject shape = info.pieceShape;

		// New Velocity
		Transform3D newVelocity;

		if (shape instanceof Cylinder) {

			newVelocity =
				Geometry.reflectBallFromRotatingCylinder(
					ballSphere,
					ball.getVelocity(),
					(Cylinder) shape,
					gizmo.getAxisOfRotation(),
					gizmo.getAngularVelocity(),
					gizmo.getReflectCoeff());

		} else if (shape instanceof PlaneSegment) {

			newVelocity =
				Geometry.reflectBallFromRotatingPlaneSegment(
					ballSphere,
					ball.getVelocity(),
					(PlaneSegment) shape,
					gizmo.getAxisOfRotation(),
					gizmo.getAngularVelocity(),
					gizmo.getReflectCoeff());

		} else if (shape instanceof Sphere) {

			newVelocity =
				Geometry.reflectBallFromRotatingSphere(
					ballSphere,
					ball.getVelocity(),
					(Sphere) shape,
					gizmo.getAxisOfRotation(),
					gizmo.getAngularVelocity(),
					gizmo.getReflectCoeff());

		} else
			throw new IllegalArgumentException("Physics Object not recognized!");

		// Update the ball and trigger all necessary connections

		ball.setVelocity(newVelocity);
		gizmo.ballCollision(ball);
		gizmo.triggerConnections();
	}

	/**
	 * Triggers a collision with a mobile gizmo
	 * @param info - information related to the mobile gizmo collision
	 */
	private static void triggerMobileCollision(CollisionInfo info) {
		if (info == null)
			throw new NullPointerException("Collision Information is null!");

		// Ball information
		BallPhysics ball = info.ball;
		Sphere ballSphere = (Sphere) info.ballShape;

		// Static gizmo information
		GizmoPhysics gizmo = (GizmoPhysics) info.piece;
		Physics3dObject gizmoShape = info.pieceShape;

		// System.err.println("Triggering ball-SG Collision: " + gizmo);

		// System.err.println("Ball Collision: " + ball.getLocation());

		Transform3D newVelocity;

		// System.err.println(ball.toString() + "\nOld Velocity: ");
		// System.err.println(ball.getVelocity());

		if (gizmoShape instanceof Sphere)
			newVelocity =
				Geometry.reflectBallFromSphere(
					ballSphere,
					(Sphere) gizmoShape,
					ball.getVelocity(),
					gizmo.getReflectCoeff());
		else if (gizmoShape instanceof Cylinder)
			newVelocity =
				Geometry.reflectBallFromCylinder(
					ballSphere,
					(Cylinder) gizmoShape,
					ball.getVelocity(),
					gizmo.getReflectCoeff());
		else if (gizmoShape instanceof PlaneSegment)
			newVelocity =
				Geometry.reflectBallFromPlane(
					(PlaneSegment) gizmoShape,
					ball.getVelocity(),
					gizmo.getReflectCoeff());
		else
			throw new IllegalArgumentException(
				"Physics Object is null or not valid: " + gizmoShape);

		// 		Normalization:
		//
		//		newVelocity.get(bVect1);
		//		if (bVect1.length() > Xeon.VELOCITY_TOLERANCE) {
		//			System.err.println("Before normalization ball velocity: " + bVect1);
		//			bVect1.normalize();
		//			bVect1.scale(Xeon.VELOCITY_TOLERANCE);
		//			System.err.println("After normalization ball velocity: " + bVect1);
		//			newVelocity.set(bVect1);
		//		}

		// Make 0 L/sec balls

		//		if (gizmoShape instanceof PlaneSegment) {
		//			// Ball can rest on a plane
		//
		//			Vector3d velocity = new Vector3d();
		//			newVelocity.get(velocity);
		//
		//			System.err.println(velocity);
		//		}
		//
		//			if (false && velocity.length() < Xeon.ZERO_VELOCITY_TOLERANCE) {
		//				// Consider that ball sits on this plane
		//
		//				// System.err.println("Possible zero velocity");
		//
		//				velocity.normalize();
		//
		//				Vector3d gravity = new Vector3d(Xeon.GRAVITY);
		//				gravity.normalize();
		//
		//				double dot = velocity.dot(gravity);
		//				// Velocity after collision should be in opposite 
		//				// direction than gravity
		//				// Value should be -1
		//
		//				dot = dot + 1;
		//
		//				if (dot < 1E-8 && dot > -1E-8) {
		//					// velocity vector points in the same direction as gravity
		//
		//					PlaneSegment ps = (PlaneSegment) gizmoShape;
		//					Vector3d normal = ps.normal();
		//					normal.normalize();
		//
		//					newVelocity.get(velocity);
		//					velocity.normalize();
		//
		//					// Can return +/- 1
		//					dot = velocity.dot(normal);
		//					// System.err.println(
		//					// 	"Points in same direction as gravity: " + dot);
		//
		//					dot = dot - 1;
		//
		//					if (dot < 1E-8 && dot > -1E-8) {
		//						// Velocity is in same direction as normal
		//						// System.err.println("Setting zero velocity");
		//						newVelocity.set(Xeon.ZERO_VELOCITY);
		//
		//					} else {
		//						dot = velocity.dot(normal);
		//						dot = dot + 1;
		//
		//						if (dot < 1E-8 && dot > -1E-8) {
		//							// Velocity is in opposite direction of normal
		//							// System.err.println("Setting zero velocity");
		//							newVelocity.set(Xeon.ZERO_VELOCITY);
		//						}
		//					}
		//				}
		//			}
		//		}

		// Update the ball and trigger all necessary connections
		ball.setVelocity(newVelocity);
		gizmo.ballCollision(ball);
		gizmo.triggerConnections();

		//		System.err.println("New Velocity: ");
		//		System.err.println(newVelocity);
		//		System.err.println("Physical Object: " + gizmoShape);
		//		System.err.println("Sphere: " + ballSphere);
		//		System.err.println("Bounced: " + ball.getLocation());

		//		if (gizmo.toString().trim().equals("TA12"))
		//			System.exit(0);
	}

	/**
	 * Updates all of the balls with new 
	 * @param ballList
	 * @param timeSlice
	 */
	private static void updateBalls(Set ballSet, double timeSlice) {

		if (ballSet == null)
			throw new NullPointerException("Ball Collection is null!");
		else if (timeSlice < 0)
			throw new IllegalArgumentException(
				"Time left is less than 0: " + timeSlice);

		Iterator ballIter = ballSet.iterator();
		while (ballIter.hasNext()) {

			BallPhysics ball = (BallPhysics) ballIter.next();

			if (ball.isPhysical()) {

				// Get translational component of the ball velocity
				Transform3D ballVelocity = ball.getVelocity();
				Vector3d velocity = new Vector3d();
				ballVelocity.get(velocity);
				//
				//				System.err.println(
				//					"Before gravity - Ball Loc: "
				//						+ ball.getLocation()
				//						+ " Velocity: "
				//						+ velocity);

				// Account for gravitational effects
				Vector3d gVelocity = new Vector3d(Xeon.GRAVITY);
				gVelocity.scale(timeSlice);
				velocity.add(gVelocity);

				// Account for frictional effects
				double velX =
					velocity.x
						* (1
							- Xeon.MU * timeSlice
							- Xeon.MU_2 * Math.abs(velocity.x) * timeSlice);
				double velY =
					velocity.y
						* (1
							- Xeon.MU * timeSlice
							- Xeon.MU_2 * Math.abs(velocity.y) * timeSlice);
				double velZ =
					velocity.z
						* (1
							- Xeon.MU * timeSlice
							- Xeon.MU_2 * Math.abs(velocity.z) * timeSlice);
				Vector3d fVelocity = new Vector3d(velX, velY, velZ);

				ballVelocity.set(fVelocity);
				ball.setVelocity(ballVelocity);

				//				System.err.println(
				//					"After gravity - Ball Loc: "
				//						+ ball.getLocation()
				//						+ " Velocity: "
				//						+ fVelocity
				//						+ " in timeSlice (sec): "
				//						+ timeSlice);

				// Update the ball
				ball.update(timeSlice);

				/*
				// Update ball position in the timeslice
				// Use physics equation x(new) = x(old)+v(x)t+0.5*a(x)*t^2
				
				// x,y,z coordinates
				Vector3d ballPosition = new Vector3d(ball.getLocation());
				
				// v*t
				ballVelocity = ball.getVelocity();
				velocity = new Vector3d();
				ballVelocity.get(velocity);
				velocity.scale(timeLeft);
				
				// 0.5*a*t*t
				gVelocity = new Vector3d(Xeon.GRAVITY);
				gVelocity.scale(0.5);
				gVelocity.scale(timLeft);
				gVelocity.scale(timeLeft);
				
				//Sum all three up
				ballPosition.add(velocity);
				ballPosition.add(gVelocity);
				
				// Set ball Position
				ball.setLocation(new Point3d(ballPosition));		
				*/

				// System.err.println(ball.getVelocity());
				// System.err.println("Updated ball: " + ball.getLocation());
			}
		}
	}

	/**
	 * Updates all the gizmos
	 * @param gizmos
	 * @param timeSlice
	 */
	private static void updateGizmos(Set gizmos, double timeSlice) {

		if (gizmos == null)
			throw new NullPointerException("Gizmo collection is null!");
		else if (timeSlice < 0)
			throw new IllegalArgumentException(
				"Amount of time left is less than 0: " + timeSlice);

		Iterator gizmoIter = gizmos.iterator();
		while (gizmoIter.hasNext()) {
			GizmoPhysics gizmo = (GizmoPhysics) gizmoIter.next();

			gizmo.update(timeSlice);
		}
	}

	private static double cleanCollisionTime(double collisionTime) {

		// First check for NaN
		if (!(collisionTime <= Double.POSITIVE_INFINITY
			&& collisionTime >= Double.NEGATIVE_INFINITY))
			collisionTime = Double.POSITIVE_INFINITY;

		// Then check for negative times, including -0.0
		if (!(collisionTime > 0)) {
			if (collisionTime < 0)
				collisionTime = Double.POSITIVE_INFINITY;
			else if (!(new Double(collisionTime)).equals(Xeon.ZERO))
				// Checks for -0.0
				collisionTime = Double.POSITIVE_INFINITY;
		}

		return collisionTime;
	}
}
