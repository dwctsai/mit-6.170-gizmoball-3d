/*
 * Created on Apr 23, 2003
 *
 * @author Ragu Vijaykumar
 */
package engine;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import physics3d.Physics3dObject;
import engine.backend.BallPhysics;
import engine.backend.PiecePhysics;

/**
 *	Collisions - Handles the processing of collisions - cannot be subclasses because
 *  it is written in a very C like structure as to maximize on Java performance
 *  Everything is static for memory efficiency and time efficiency. Only one instance
 *  of the Collisions class can exist at any given runtime.
 * 
 *  It is assumed that a ball can only collide with one object at any given time.
 *
 *  @author Ragu Vijaykumar
 */
public final class Collisions {

	private static Collisions collisions = null;
	private static Map BallPieceMap;
	private static Map BallShapeMap;
	private static Map PieceShapeMap;
	private static CollisionIterator cIterator;
	private static CollisionInfo minCollision;
	private static Set ballCollisions;

	private Collisions() {
		// DO NOTHING
	}

	/**
	 * Initializes this with the following information. Only one instance can exist at
	 * any given time
	 * @param initialCapacity - number of collisions expected
	 * @param loadFactor - what percentage of the capacity should the size increase
	 */
	public static Collisions initialize(
		int initialCapacity,
		float loadFactor) {

		if (Collisions.collisions == null) {

			Collisions.BallPieceMap = new HashMap(initialCapacity, loadFactor);
			Collisions.BallShapeMap = new HashMap(initialCapacity, loadFactor);
			Collisions.PieceShapeMap = new HashMap(initialCapacity, loadFactor);
			Collisions.ballCollisions =
				new HashSet(initialCapacity, loadFactor);

			Collisions.minCollision = new CollisionInfo();
			Collisions.cIterator =
				new CollisionIterator(Collisions.minCollision);

			Collisions.collisions = new Collisions();
			CollisionIterator.collisions = Collisions.collisions;
		}

		return Collisions.collisions;
	}

	/**
	 * Is there a collision that this ball caused
	 * @param ball - first ball involved in a collision
	 * @return true iff ball has been involved in a collision
	 */
	public boolean isPresent(BallPhysics ball) {
		return Collisions.ballCollisions.contains(ball);
	}

	/**
	 * Checks to see if there is already a collision between this ball and piece
	 * @param ball - ball involved in collision
	 * @param piece - piece involved in collision
	 * @return true iff there is a collision between the ball and the piece
	 */
	public boolean isPresent(BallPhysics ball, PiecePhysics piece) {
		Object value = BallPieceMap.get(ball);
		return piece.equals(value);
	}

	/**
	 * Sets the collision that is going to happen in the fastest amount of time
	 * This method should be expected to take O(1) time.
	 * 
	 * @param ball - ball involved in collision
	 * @param ballShape - physics3d object that is part of the ball that is involved in collision
	 * @param piece - piece involved in collision
	 * @param pieceShape - physics3d object that is part of the piece that is involved in collision
	 */
	public void setMinCollision(
		BallPhysics ball,
		Physics3dObject ballShape,
		PiecePhysics piece,
		Physics3dObject pieceShape) {

		Collisions.minCollision.ball = ball;
		Collisions.minCollision.ballShape = ballShape;
		Collisions.minCollision.piece = piece;
		Collisions.minCollision.pieceShape = pieceShape;

	}

	/**
	 * Retrieve the minimum collision for this 
	 * @return
	 */
	public CollisionInfo getMinCollision() {
		return minCollision;
	}

	/**
	 * Adds the following collision information to the collection of collisions
	 * @param info - collision information to be added to the collection
	 */
	public void addCollision(CollisionInfo info) {
		this.addCollision(
			info.ball,
			info.ballShape,
			info.piece,
			info.pieceShape);
	}

	/**
	 * Adds a collision only if the collision is unique
	 * @param ball - ball involved in the collision
	 * @param piece - piece involved in the collision
	 */
	public void addCollision(
		BallPhysics ball,
		Physics3dObject ballShape,
		PiecePhysics piece,
		Physics3dObject pieceShape) {

		if (ball == null
			|| piece == null
			|| ballShape == null
			|| pieceShape == null)
			throw new NullPointerException(
				"Collision pair is null: "
					+ ball
					+ " "
					+ ballShape
					+ " - "
					+ piece
					+ " "
					+ pieceShape);

		// Add all this collision information
		Collisions.ballCollisions.add(ball);
		Collisions.BallPieceMap.put(ball, piece);
		Collisions.BallShapeMap.put(ball, ballShape);
		Collisions.PieceShapeMap.put(ball, pieceShape);
	}

	/**
	 * Iterates over all collisions - cannot call more than once per
	 * update
	 * @return iterator over all collisions
	 */
	public CollisionIterator getCollisions() {
		CollisionIterator.balls = Collisions.ballCollisions.iterator();
		return Collisions.cIterator;
	}

	/**
	 * Clears all the collisions stored in this collection of collisions
	 * @effects clears all collisions
	 */
	public void clear() {
		Collisions.BallPieceMap.clear();
		Collisions.BallShapeMap.clear();
		Collisions.PieceShapeMap.clear();
		Collisions.ballCollisions.clear();
		//		Collisions.BallPieceMap = new HashMap();
		//		Collisions.BallShapeMap = new HashMap();
		//		Collisions.PieceShapeMap = new HashMap();
		//		Collisions.ballCollisions = new HashSet();
	}

	/**
	 * The number of collisions currently held
	 * @return cardinatlity of this
	 */
	public int size() {
		return Collisions.BallPieceMap.size();
	}

	// Inner classes

	/**
	 *	CollisionIterator - operates in much the same manner as an iterator, without
	 *  a remove method however. Also, calling next() on the iterator will return
	 *  the specific type of CollisionInfo, which holds the information of the collision
	 *  
	 *  @author Ragu Vijaykumar
	 */
	public static class CollisionIterator {

		private static Collisions collisions;
		private static CollisionInfo info;
		private static Iterator balls;

		private CollisionIterator(CollisionInfo info) {
			CollisionIterator.info = info;
		}

		/**
		 * @return true iff there are still more collisions left
		 */
		public boolean hasNext() {
			return balls.hasNext();
		}

		/**
		 * This will return a CollisionInfo class that contains the information
		 * about the next collision. This operation can be expected to take O(1) time.
		 *
		 * @return next collision if there is one
		 */
		public CollisionInfo next() {

			CollisionIterator.info.ball = (BallPhysics) balls.next();
			CollisionIterator.info.piece =
				(PiecePhysics) Collisions.BallPieceMap.get(
					CollisionIterator.info.ball);
			CollisionIterator.info.ballShape =
				(Physics3dObject) Collisions.BallShapeMap.get(
					CollisionIterator.info.ball);
			CollisionIterator.info.pieceShape =
				(Physics3dObject) Collisions.PieceShapeMap.get(
					CollisionIterator.info.ball);

			return info;
		}

	}

	/**
	 *	CollisionInfo - Holds all information related to a collision that needs
	 *  to be processed by the engine - all information regarding collision
	 *  should be accessed in a static way, with no worry as to manipulation of data
	 *
	 *  @author Ragu Vijaykumar
	 */
	public static class CollisionInfo {

		/**
		 * The ball involved in the collision
		 */
		public BallPhysics ball = null;

		/**
		 * The ball physical object involved in the collision
		 */
		public Physics3dObject ballShape = null;
		
		/**
		 * The piece involved in the collision
		 */
		public PiecePhysics piece = null;
		
		/**
		 * The piece physical shape involved in the collision
		 */
		public Physics3dObject pieceShape = null;

		private CollisionInfo() {
			// DO NOTHING
		}

		/**
		 * String representation of the collision information
		 * @return collision information
		 */
		public String toString() {

			return new String(
				"**************\n"
					+ "Ball:\n"
					+ ball
					+ "\nBall Shape:\n"
					+ ballShape
					+ "\nPiece:\n"
					+ piece
					+ "\nPiece Shape:\n"
					+ pieceShape
					+ "\n**************\n");
		}
	}
}
