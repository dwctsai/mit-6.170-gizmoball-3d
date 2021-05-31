/*
 * Created on Apr 23, 2003
 *
 * @author Ragu Vijaykumar
 */
package engine.testing;

import java.util.ArrayList;
import java.util.List;

import javax.media.j3d.Transform3D;
import javax.vecmath.Point3d;

import testing.MemoryProfiler;
import testing.TimeProfiler;
import engine.Collisions;

/**
 *	CollisionsProfiler.java
 *
 *  @author Ragu Vijaykumar
 */
public class CollisionsProfiler {

	private int numBalls = 100;
	private int sims = 200;
	private Collisions collisions = null;
	private int name = 0;

	/**
	 * 
	 */
	public CollisionsProfiler() {

		System.out.println("Profiling for " + numBalls + " collisions....");

		TimeProfiler tp;
		MemoryProfiler mp;

		List ballList = new ArrayList();

		for (int i = 0; i < numBalls; i++) {
			Point3d location = new Point3d(0, 19, 0);
			Ball b3d = new Ball(location, 0.25, 64, new Transform3D());
			ballList.add(b3d);
		}

		mp =
			new MemoryProfiler("Collisions Memory Usage -- Creating Collisions class");
		mp.start();
		collisions = Collisions.initialize(numBalls, 0.8f);
		mp.stop();
		System.out.println(mp.stats());
		System.out.println();

		mp =
			new MemoryProfiler("Collisions Memory Usage -- Initial creation of collisions");
		tp =
			new TimeProfiler("Collisions Time Usage -- Initial creation of collisions");
		mp.start();
		tp.start();
		for (int i = 0; i < numBalls; i++)
			for (int j = 0; j < numBalls; j++) {
				Ball ball1 = (Ball) ballList.get(i);
				Ball ball2 = (Ball) ballList.get(j);
				collisions.addCollision(
					ball1,
					ball1.getShape(),
					ball2,
					ball2.getShape());
			}
		tp.stop();
		mp.stop();
		System.out.println("Number of collisions: " + collisions.size());
		System.out.println(mp.stats());
		System.out.println();
		System.out.println(tp.stats());
		System.out.println();

		collisions.clear();

		tp =
			new TimeProfiler("Collisions Time Usage -- Constant creation/clearing of collisions");
		mp =
			new MemoryProfiler("Collisions Memory Usage -- Constant creation/clearing of collisions");

		for (int count = 0; count < sims; count++) {
			mp.start();
			tp.start();
			for (int i = 0; i < numBalls; i++)
				for (int j = 0; j < numBalls; j++) {
					Ball ball1 = (Ball) ballList.get(i);
					Ball ball2 = (Ball) ballList.get(j);
					collisions.addCollision(
						ball1,
						ball1.getShape(),
						ball2,
						ball2.getShape());
				}

			collisions.clear();
			tp.stop();
			mp.stop();
		}

		System.out.println(mp.stats());
		System.out.println();
		System.out.println(tp.stats());
		System.out.println();
	}

	public static void main(String[] args) {
		CollisionsProfiler cp = new CollisionsProfiler();
	}
}
