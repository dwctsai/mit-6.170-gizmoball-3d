/*
 * Created on May 05, 2003
 *
 * @author Ragu Vijaykumar
 */

package engine.testing;

import java.util.HashSet;

import javax.vecmath.Point3d;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import engine.Xeon;

public class XeonTest extends TestCase {

	private static final double tolerance = 0.0001;

	public XeonTest(String name) {
		super(name);
	}

	protected void setUp() {
	}

	public void testXeon() {
		// Glass Box Test

		try {
			Xeon.update(30);
		} catch (RuntimeException e) {
			assertTrue(
				"Update didn't complete because engine was not built",
				true);
		}
		try {
			Xeon.buildEngine(
				new HashSet(),
				new HashSet(),
				new Point3d(),
				new Point3d());
			Xeon.update(-1);
		} catch (IllegalArgumentException e) {
			assertTrue(
				"Update didn't complete because update time was negative",
				true);
		}

		try {
			Xeon.buildEngine(null, null, null, null);
		} catch (NullPointerException e) {
			assertTrue(
				"Building of the engine did not complete because parameters were null",
				true);
		}

		try {
			Xeon.buildEngine(
				new HashSet(),
				new HashSet(),
				new Point3d(),
				new Point3d());
			Xeon.update(20);
		} catch (Exception e) {
			fail("Engine should run properly! Valid arguments passed in!");
		}
	}

	public static Test suite() {
		TestSuite suite = new TestSuite();
		suite.addTest(new XeonTest("testXeon"));

		return suite;
	}

}
