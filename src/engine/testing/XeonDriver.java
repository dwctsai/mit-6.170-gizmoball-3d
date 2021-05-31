/*
 * Created on Apr 24, 2003
 *
 * @author Ragu Vijaykumar
 */
package engine.testing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Set;

import javax.media.j3d.Transform3D;
import javax.swing.JFrame;
import javax.vecmath.Point3d;

import testing.TimeProfiler;
import engine.Xeon;

/**
 *	XeonDriver - drives the Xeon engine for testing
 *
 *  @author Ragu Vijaykumar
 */
public class XeonDriver extends JFrame implements ActionListener {

	private Set ballSet;
	private Set gizmoSet;
	private javax.swing.Timer timer;
	private static int count = 0;
	private TimeProfiler tp = new TimeProfiler("Xeon Engine");
	private PrintStream p = testing.FileGenerator.createFile("XeonOutput.txt");

	/**
	 * Creates an instance of the driver
	 */
	public XeonDriver() {
		super();

		ballSet = new HashSet();
		gizmoSet = new HashSet();
		gizmoSet.add(new Wall(new Point3d(-1, -1, -1), 20, 1, 20, 1));

		this.createStressTest();

		Xeon.buildEngine(
			ballSet,
			gizmoSet,
			new Point3d(-1, -1, -1),
			new Point3d(21, 21, 21));

		Xeon.update(0.03);

		timer = new javax.swing.Timer(30, this);

		timer.start();

	}

	private void createStressTest() {

		for (int x = 5; x < 6; x++)
			for (int y = 5; y < 7; y++) {
				Point3d location = new Point3d(x, y, 10);
				Ball b3d = new Ball(location, 0.25f, 64, new Transform3D());
				ballSet.add(b3d);
			}
	}

	private void createTopLayer() {

		for (int x = 5; x < 15; x++)
			for (int z = 5; z < 15; z++) {
				Point3d location = new Point3d(x, 19, z);
				Ball b3d = new Ball(location, 0.25, 64, new Transform3D());
				ballSet.add(b3d);
			}
	}

	public static void main(String[] args) {
		JFrame xd = new XeonDriver();
		xd.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		xd.setSize(100, 100);
		xd.setVisible(true);
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent arg0) {
		tp.start();
		Xeon.update(0.030);
		tp.stop();
		count++;
		if (count > 300) {
			p.println(tp.stats());
			System.exit(0);
		}
	}
}
