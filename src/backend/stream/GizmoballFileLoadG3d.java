/* @author David Tsai
 * @created on Apr 26, 2003
 */
package backend.stream;

import gui.PieceProperties;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

import javax.media.j3d.Transform3D;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import plugins.pieces.AbsorberFactory;
import plugins.pieces.BallFactory;
import plugins.pieces.CubicalBumperFactory;
import plugins.pieces.Flipper3D;
import plugins.pieces.FlipperFactory;
import plugins.pieces.Jezzmo3D;
import plugins.pieces.JezzmoFactory;
import plugins.pieces.SphericalBumperFactory;
import plugins.pieces.TriangularBumperAFactory;
import plugins.pieces.TriangularBumperBFactory;
import plugins.pieces.TriangularBumperCFactory;
import backend.Gizmo;
import backend.Piece;
import backend.PieceFactory;
import backend.event.GizmoEvent;
import backend.event.GizmoListener;
import backend.events.exceptions.EventException;

/**
 * GizmoballFileLoadG3d - Loads a gizmoball game in the *.g3d file format.
 */
public class GizmoballFileLoadG3d {
	private PrintWriter output;
	private BufferedReader input;

	private PieceFactory pf;
	private Piece p;
	private HashMap nameToPiece = new HashMap();

	public static void main(String args[]) {
		try {

			GizmoballFileLoadG3d td;

			if (args.length == 0) {
				td =
					new GizmoballFileLoadG3d(
						new InputStreamReader(System.in),
						new OutputStreamWriter(System.out));
			} else {

				String fileName = args[0];
				File savefile = new File(fileName);

				if (savefile.exists() || savefile.canRead()) {
					td =
						new GizmoballFileLoadG3d(
							new FileReader(savefile),
							new OutputStreamWriter(System.out));
				} else {
					System.err.println(
						"Cannot read from " + savefile.toString());
					return;
				}
			}

			td.translate();

		} catch (IOException e) {
			System.err.println(e.toString());
			e.printStackTrace(System.err);
		}
	}

	/**
	 * @effects Creates a new GizmoballFileLoadG3d from a File f.
	 */
	public GizmoballFileLoadG3d(File f) {
		try {
			GizmoballFileLoadG3d td;

			if (f.exists() || f.canRead()) {
				td =
					new GizmoballFileLoadG3d(
						new FileReader(f),
						new OutputStreamWriter(System.out));
			} else {
				System.err.println("Cannot read from " + f.getName());
				return;
			}

			td.translate();

			// Enable all piece behaviors.
			PieceProperties.enableAllPieceBehaviors();

		} catch (IOException e) {
			System.err.println(e.toString());
			e.printStackTrace(System.err);
		}
	}

	/**
	 * @effects Creates a new GizmoballFileLoadG3d from a File f.
	 */
	public GizmoballFileLoadG3d(String s) {
		translateString(s);

		// Enable all piece behaviors.
		PieceProperties.enableAllPieceBehaviors();
	}

	/**
	 * @requies r != null && w != null
	 *
	 * @effects Creates a new GizmoballFileLoadG3d which reads the format from
	 * <tt>r</tt> and writes results to <tt>w</tt>.
	 **/
	public GizmoballFileLoadG3d(Reader r, Writer w) {
		input = new BufferedReader(r);
		output = new PrintWriter(w);
	}

	/**
	 * @effects Translates from the input and executes any commands accordingly.
	 * @throws IOException if the input or output sources encounter an IOException
	 **/
	public void translate() throws IOException {
		String inputLine;
		while ((inputLine = input.readLine()) != null) {

			// separate the input line on white space
			StringTokenizer st = new StringTokenizer(inputLine);
			if (st.hasMoreTokens()) {
				String opcode = st.nextToken();

				List arguments = new ArrayList();
				while (st.hasMoreTokens()) {
					arguments.add(st.nextToken());
				}

				executeCommand(opcode, arguments);
			}
		}
	}

	/**
	 * @effects Translates from an inputted String and executes any commands accordingly.
	 */
	public void translateString(String s) {
		String inputString = s;

		StringTokenizer stString = new StringTokenizer(s, "\n");
		StringTokenizer stLine;
		while (stString.hasMoreTokens()) {
			String currentLine = stString.nextToken();

			stLine = new StringTokenizer(currentLine);
			String opcode = stLine.nextToken();
			List arguments = new ArrayList();
			while (stLine.hasMoreTokens()) {
				arguments.add(stLine.nextToken());
			}
			executeCommand(opcode, arguments);

		}

	}

	private void executeCommand(String opcode, List arguments) {
		if (opcode.equalsIgnoreCase("Cube")) {
			doCreateCube(arguments);
		} else if (opcode.equalsIgnoreCase("Sphere")) {
			doCreateSphere(arguments);
		} else if (opcode.equalsIgnoreCase("TriangleA")) {
			doCreateTriangleA(arguments);
		} else if (opcode.equalsIgnoreCase("TriangleB")) {
			doCreateTriangleB(arguments);
		} else if (opcode.equalsIgnoreCase("TriangleC")) {
			doCreateTriangleC(arguments);
		} else if (opcode.equalsIgnoreCase("Flipper")) {
			doCreateFlipper(arguments);
		} else if (opcode.equalsIgnoreCase("Absorber")) {
			doCreateAbsorber(arguments);
		} else if (opcode.equalsIgnoreCase("Ball")) {
			doCreateBall(arguments);
		} else if (opcode.equalsIgnoreCase("Rotate")) {
			doRotate(arguments);
		} else if (opcode.equalsIgnoreCase("Delete")) {
			doDelete(arguments);
		} else if (opcode.equalsIgnoreCase("Move")) {
			doMove(arguments);
		} else if (opcode.equalsIgnoreCase("Connect")) {
			doConnect(arguments);
		} else if (opcode.equalsIgnoreCase("KeyConnect")) {
			doKeyConnect(arguments);
		} else if (opcode.equalsIgnoreCase("Gravity")) {
			doSetGravity(arguments);
		} else if (opcode.equalsIgnoreCase("Friction")) {
			doSetFriction(arguments);
		} else if (opcode.equalsIgnoreCase("TimePerUpdate")) {
			doSetTPU(arguments);
		} else if (opcode.equalsIgnoreCase("Jezzmo")) {
			doCreateJezzmo(arguments);
		} else if (opcode.equalsIgnoreCase("MouseConnect")) {
			doMouseConnect(arguments);
		} else if (opcode.equalsIgnoreCase("Wall")) {
			doCreateWall(arguments);
		} else {
			throw new ParseException("Unrecognized opcode: " + opcode);
		}
	}

	private void doCreateCube(List arguments) {
		if (arguments.size() != 5) {
			throw new ParseException(
				"Wrong number of arguments to Cube: " + arguments);
		}
		try {
			// Create the gizmo.
			double xLocation = Double.parseDouble((String) arguments.get(1));
			double yLocation = Double.parseDouble((String) arguments.get(2));
			double zLocation = Double.parseDouble((String) arguments.get(3));
			double cor = Double.parseDouble((String) arguments.get(4));
			
			pf = new CubicalBumperFactory();
//			p = pf.makePiece(new Point3d(xLocation, yLocation, zLocation));
			
			String name = (String) arguments.get(0);
						
			p = pf.makePiece(name,
							 new Point3d(xLocation, yLocation, zLocation),
							 -1.0,
							 PieceProperties.defaultCubicalBumper3DXLength,
							 PieceProperties.defaultCubicalBumper3DYLength,
							 PieceProperties.defaultCubicalBumper3DZLength,
							 -1.0,
							 null,
							 cor,
							 PieceProperties.appCubicalBumper3DDefault,
							 true,
							 null);
			
			
			p.addPiece();
	
			nameToPiece.put((String) arguments.get(0), p);

		} catch (NumberFormatException nfe) {
			throw new ParseException("Cube: Invalid Input");
		}

	}

	private void doCreateSphere(List arguments) {
		if (arguments.size() != 5) {
			throw new ParseException(
				"Wrong number of arguments to Sphere: " + arguments);
		}
		try {
			// Create the gizmo.
			double xLocation = Double.parseDouble((String) arguments.get(1));
			double yLocation = Double.parseDouble((String) arguments.get(2));
			double zLocation = Double.parseDouble((String) arguments.get(3));
			double cor = Double.parseDouble((String) arguments.get(4));			
			
			pf = new SphericalBumperFactory();
//			p = pf.makePiece(new Point3d(xLocation, yLocation, zLocation));

			String name = (String) arguments.get(0);
						
			p = pf.makePiece(name,
							 new Point3d(xLocation, yLocation, zLocation),
							 PieceProperties.defaultSphericalBumper3DRadius,
							 -1.0,
							 -1.0,
							 -1.0,
							 -1.0,
							 null,
							 cor,
							 PieceProperties.appSphericalBumper3DDefault,
							 true,
							 null);


			p.addPiece();


			nameToPiece.put((String) arguments.get(0), p);

		} catch (NumberFormatException nfe) {
			throw new ParseException("Sphere: Invalid Input");
		}

	}

	private void doCreateTriangleA(List arguments) {
		if (arguments.size() != 5) {
			throw new ParseException(
				"Wrong number of arguments to TriangleA: " + arguments);
		}
		try {
			// Create the gizmo.
			double xLocation = Double.parseDouble((String) arguments.get(1));
			double yLocation = Double.parseDouble((String) arguments.get(2));
			double zLocation = Double.parseDouble((String) arguments.get(3));
			double cor = Double.parseDouble((String) arguments.get(4));				
			
			pf = new TriangularBumperAFactory();
//			p = pf.makePiece(new Point3d(xLocation, yLocation, zLocation));

			String name = (String) arguments.get(0);
						
			p = pf.makePiece(name,
							 new Point3d(xLocation, yLocation, zLocation),
							 -1.0,
							 PieceProperties.defaultTriangularBumperA3DXLength,
							 PieceProperties.defaultTriangularBumperA3DYLength,
							 PieceProperties.defaultTriangularBumperA3DZLength,
							 -1.0,
							 null,
							 cor,
							 PieceProperties.appTriangularBumperA3DDefault,
							 true,
							 null);


			p.addPiece();
			

			nameToPiece.put((String) arguments.get(0), p);

		} catch (NumberFormatException nfe) {
			throw new ParseException("TriangleA: Invalid Input");
		}
	}

	private void doCreateTriangleB(List arguments) {
		if (arguments.size() != 5) {
			throw new ParseException(
				"Wrong number of arguments to TriangleB: " + arguments);
		}
		try {
			// Create the gizmo.
			double xLocation = Double.parseDouble((String) arguments.get(1));
			double yLocation = Double.parseDouble((String) arguments.get(2));
			double zLocation = Double.parseDouble((String) arguments.get(3));
			double cor = Double.parseDouble((String) arguments.get(4));				
			
			pf = new TriangularBumperBFactory();
//			p = pf.makePiece(new Point3d(xLocation, yLocation, zLocation));

			String name = (String) arguments.get(0);
						
			p = pf.makePiece(name,
							 new Point3d(xLocation, yLocation, zLocation),
							 -1.0,
							 PieceProperties.defaultTriangularBumperB3DXLength,
							 PieceProperties.defaultTriangularBumperB3DYLength,
							 PieceProperties.defaultTriangularBumperB3DZLength,
							 -1.0,
							 null,
							 cor,
							 PieceProperties.appTriangularBumperB3DDefault,
							 true,
							 null);

			p.addPiece();


			nameToPiece.put((String) arguments.get(0), p);
		} catch (NumberFormatException nfe) {
			throw new ParseException("TriangleB: Invalid Input");
		}
	}

	private void doCreateTriangleC(List arguments) {
		if (arguments.size() != 5) {
			throw new ParseException(
				"Wrong number of arguments to TriangleC: " + arguments);
		}
		try {
			// Create the gizmo.
			double xLocation = Double.parseDouble((String) arguments.get(1));
			double yLocation = Double.parseDouble((String) arguments.get(2));
			double zLocation = Double.parseDouble((String) arguments.get(3));
			double cor = Double.parseDouble((String) arguments.get(4));				
			
			pf = new TriangularBumperCFactory();
//			p = pf.makePiece(new Point3d(xLocation, yLocation, zLocation));

			String name = (String) arguments.get(0);
						
			p = pf.makePiece(name,
							 new Point3d(xLocation, yLocation, zLocation),
							 -1.0,
							 PieceProperties.defaultTriangularBumperC3DXLength,
							 PieceProperties.defaultTriangularBumperC3DYLength,
							 PieceProperties.defaultTriangularBumperC3DZLength,
							 -1.0,
							 null,
							 cor,
							 PieceProperties.appTriangularBumperC3DDefault,
							 true,
							 null);


			p.addPiece();
			
				

			nameToPiece.put((String) arguments.get(0), p);
		} catch (NumberFormatException nfe) {
			throw new ParseException("TriangleC: Invalid Input");
		}
	}

	private void doCreateFlipper(List arguments) {
		if (arguments.size() != 6) {
			throw new ParseException(
				"Wrong number of arguments to RightFlipper: " + arguments);
		}
		try {
			// Create the gizmo.
			double xLocation = Double.parseDouble((String) arguments.get(1));
			double yLocation = Double.parseDouble((String) arguments.get(2));
			double zLocation = Double.parseDouble((String) arguments.get(3));
			double cor = Double.parseDouble((String) arguments.get(4));		

			String orientation = (String) arguments.get(5);
			if (orientation.equalsIgnoreCase("left")) {
				orientation = Flipper3D.LEFT_ORIENTATION;
			} else if (orientation.equalsIgnoreCase("right")) {
				orientation = Flipper3D.RIGHT_ORIENTATION;
			}

			String name = (String) arguments.get(0);
			
			pf = new FlipperFactory();
			
			p =
				pf.makePiece(
					name,
					new Point3d(xLocation, yLocation, zLocation),
					-1.0,
					-1.0,
					-1.0,
					-1.0,
					-1.0,
					null,
					cor,
					PieceProperties.appFlipper3DDefault,
					true,
					orientation);

			p.addPiece();

			nameToPiece.put((String) arguments.get(0), p);

		} catch (NumberFormatException nfe) {
			throw new ParseException("Flipper: Invalid Input");
		}
	}

	private void doCreateAbsorber(List arguments) {
		if (arguments.size() != 7) {
			throw new ParseException(
				"Wrong number of arguments to Absorber: " + arguments);
		}
		try {
			// Create the gizmo.
			double xLocation = Double.parseDouble((String) arguments.get(1));
			double yLocation = Double.parseDouble((String) arguments.get(2));
			double zLocation = Double.parseDouble((String) arguments.get(3));
			double xLocation2 = Double.parseDouble((String) arguments.get(4));
			double yLocation2 = Double.parseDouble((String) arguments.get(5));
			double zLocation2 = Double.parseDouble((String) arguments.get(6));
			double xLength = xLocation2 - xLocation;
			double yLength = yLocation2 - yLocation;
			double zLength = zLocation2 - zLocation;

			String name = (String) arguments.get(0);

			pf = new AbsorberFactory();
			p =
				pf.makePiece(
					name,
					new Point3d(xLocation, yLocation, zLocation),
					-1.0,
					xLength,
					yLength,
					zLength,
					-1.0,
					null,
					PieceProperties.defaultAbsorber3DCOR,
					PieceProperties.appAbsorber3DDefault,
					true,
					null);

			p.addPiece();

			nameToPiece.put((String) arguments.get(0), p);

		} catch (NumberFormatException nfe) {
			throw new ParseException("Absorber: Invalid Input");
		}
	}

	private void doCreateBall(List arguments) {
		if (arguments.size() != 8) {
			throw new ParseException(
				"Wrong number of arguments to Ball: " + arguments);
		}
		try {
			// Create the ball.
			double xLocation = Double.parseDouble((String) arguments.get(1));
			double yLocation = Double.parseDouble((String) arguments.get(2));
			double zLocation = Double.parseDouble((String) arguments.get(3));
			double xVelocity = Double.parseDouble((String) arguments.get(4));
			double yVelocity = Double.parseDouble((String) arguments.get(5));
			double zVelocity = Double.parseDouble((String) arguments.get(6));
			Vector3d v = new Vector3d(xVelocity, yVelocity, zVelocity);

			Transform3D velocityT3D = new Transform3D();
			velocityT3D.set(v);
			
			double mass = Double.parseDouble((String) arguments.get(7));
			String name = (String) arguments.get(0);

			pf = new BallFactory();
			p =
				pf.makePiece(
					name,
					new Point3d(xLocation, yLocation, zLocation),
					PieceProperties.defaultBall3DRadius,
					-1.0,
					-1.0,
					-1.0,
					PieceProperties.defaultBall3DMass,
					velocityT3D,
					-1.0,
					PieceProperties.appBall3DDefault,
					true,
					null);
			p.addPiece();

			nameToPiece.put((String) arguments.get(0), p);

		} catch (NumberFormatException nfe) {
			throw new ParseException("Ball: Invalid Input");
		}

	}

	private void doRotate(List arguments) {
		if (arguments.size() < 2) {
			throw new ParseException(
				"Wrong number of arguments to Rotate: " + arguments);
		}

		// Do the action.
		p = (Piece) nameToPiece.get((String) arguments.get(0));

		for (int i = 1; i < arguments.size(); i++) {
			if (((String) arguments.get(i)).equalsIgnoreCase("XY")) {
				p.rotateXY90Graphics();
			} else if (((String) arguments.get(i)).equalsIgnoreCase("XZ")) {
				p.rotateXZ90Graphics();
			} else if (((String) arguments.get(i)).equalsIgnoreCase("YZ")) {
				p.rotateYZ90Graphics();
			} else if (((String) arguments.get(i)).equalsIgnoreCase("YX")) {
				p.rotateYX90Graphics();
			} else if (((String) arguments.get(i)).equalsIgnoreCase("ZX")) {
				p.rotateZX90Graphics();
			} else if (((String) arguments.get(i)).equalsIgnoreCase("ZY")) {
				p.rotateZY90Graphics();
			}

		}

	}

	private void doDelete(List arguments) {
		if (arguments.size() != 1) {
			throw new ParseException(
				"Wrong number of arguments to Delete: " + arguments);
		}

		// Do the action.
		p = (Piece) nameToPiece.get((String) arguments.get(0));
		p.removePiece();
	}

	private void doMove(List arguments) {
		if (arguments.size() != 4) {
			throw new ParseException(
				"Wrong number of arguments to Move: " + arguments);
		}

		// Do the action.
		p = (Piece) nameToPiece.get((String) arguments.get(0));
		Point3d newLocation = new Point3d();
		newLocation.x = Double.parseDouble((String) arguments.get(1));
		newLocation.y = Double.parseDouble((String) arguments.get(2));
		newLocation.z = Double.parseDouble((String) arguments.get(3));
		p.moveGraphics(newLocation);

	}

	private void doConnect(List arguments) {
		if (arguments.size() != 2 && arguments.size() != 3) {
			throw new ParseException(
				"Wrong number of arguments to Connect: " + arguments);
		}

		// Do the action.
		Gizmo g = (Gizmo) nameToPiece.get((String) arguments.get(0));
		Gizmo g2 = (Gizmo) nameToPiece.get((String) arguments.get(1));
		try {

			if (2 == arguments.size())
				g.addConnection(
					new GizmoEvent(g2, GizmoListener.DEFAULT_ACTION));
			else
				g.addConnection(new GizmoEvent(g2, (String) arguments.get(2)));
		} catch (EventException e) {
			throw new ParseException(e.getMessage());
		}
	}

	private void doKeyConnect(List arguments) {
		if (arguments.size() != 4 && arguments.size() != 5) {
			throw new ParseException(
				"Wrong number of arguments to KeyConnect: " + arguments);
		}

		// Do the action.
		String first = (String) arguments.get(0);

		if (!first.equalsIgnoreCase("key"))
			throw new ParseException(
				"Expected \"key\" as first argument to KeyConnect, but found "
					+ first);
		int keynum = Integer.parseInt((String) arguments.get(1));
		String third = (String) arguments.get(2);
		if (!third.equalsIgnoreCase("down") && !third.equalsIgnoreCase("up"))
			throw new ParseException(
				"Expected \"down\" or \"up\" as the third argument to KeyConnect, but found "
					+ third);
		int direction;

		if (third.equalsIgnoreCase("down"))
			direction = KeyEvent.KEY_PRESSED;
		else
			direction = KeyEvent.KEY_RELEASED;

		Gizmo g = (Gizmo) nameToPiece.get((String) arguments.get(3));

		if (4 == arguments.size()) {
			GizmoEvent ge =
				new GizmoEvent(
					GizmoEvent.Key_Event,
					keynum,
					direction,
					g,
					GizmoListener.DEFAULT_ACTION);
			PieceProperties.getEventHandler().bindInputToGizmo(ge);
		} else {
			GizmoEvent ge =
				new GizmoEvent(
					GizmoEvent.Key_Event,
					keynum,
					direction,
					g,
					(String) arguments.get(4));
			PieceProperties.getEventHandler().bindInputToGizmo(ge);
		}
	}

	private void doSetGravity(List arguments) {
		if (arguments.size() != 3) {
			throw new ParseException(
				"Wrong number of arguments to Gravity: " + arguments);
		}

		// Do the action.
		double gravity_x = Double.parseDouble((String) arguments.get(0));
		double gravity_y = Double.parseDouble((String) arguments.get(1));
		double gravity_z = Double.parseDouble((String) arguments.get(2));
		engine.Xeon.GRAVITY = new Vector3d(gravity_x, gravity_y, gravity_z);
	}

	private void doSetFriction(List arguments) {
		if (arguments.size() != 2) {
			throw new ParseException(
				"Wrong number of arguments to Friction: " + arguments);
		}
		// Do the action.
		double mu1 = Double.parseDouble((String) arguments.get(0));
		double mu2 = Double.parseDouble((String) arguments.get(1));
		engine.Xeon.MU = mu1;
		engine.Xeon.MU_2 = mu2;

	}

	private void doSetTPU(List arguments) {
		if (arguments.size() != 1) {
			throw new ParseException(
				"Wrong number of arguments to TimePerUpdate: " + arguments);
		}
		// Do the action.
                int tpu = Integer.parseInt((String) arguments.get(0));
                if(tpu <= 0)
                    throw new ParseException("Time per update must be positive (was " + tpu + ")");
		gui.Arena3DScene.TIME_PER_UPDATE = tpu;
	}

	private void doCreateJezzmo(List arguments) {
		if (arguments.size() != 7) {
			throw new ParseException(
				"Wrong number of arguments to Jezzmo: " + arguments);
		}

		// Create the gizmo.
		double xLocation = Double.parseDouble((String) arguments.get(1));
		double yLocation = Double.parseDouble((String) arguments.get(2));
		double zLocation = Double.parseDouble((String) arguments.get(3));
		double velocity = Double.parseDouble((String) arguments.get(4));

		Transform3D velocityT3D = new Transform3D();
		Vector3d tempV = new Vector3d(velocity, velocity, velocity);
		velocityT3D.set(tempV);

		String orientation = (String) arguments.get(6);
		if (!orientation.equalsIgnoreCase(Jezzmo3D.X_GROWTH)
			&& !orientation.equalsIgnoreCase(Jezzmo3D.Y_GROWTH)
			&& !orientation.equalsIgnoreCase(Jezzmo3D.Z_GROWTH)) {

			throw new ParseException(
				"Jezmo3D growth direction is invalid: " + orientation);
		}

		double cor = Double.parseDouble((String) arguments.get(5));
		String name = (String) arguments.get(0);

		pf = new JezzmoFactory();
		p =
			pf.makePiece(
				name,
				new Point3d(xLocation, yLocation, zLocation),
				-1.0,
				PieceProperties.defaultJezzmo3DXLength,
				PieceProperties.defaultJezzmo3DYLength,
				PieceProperties.defaultJezzmo3DZLength,
				-1.0,
				velocityT3D,
				cor,
				PieceProperties.appJezzmo3DDefault,
				true,
				orientation);

		p.addPiece();

		nameToPiece.put((String) arguments.get(0), p);
	}

	private void doMouseConnect(List arguments) {
		if (arguments.size() != 3 && arguments.size() != 4) {
			throw new ParseException(
				"Wrong number of arguments to MouseConnect: " + arguments);
		}

		// Do the action.
		String button = (String) arguments.get(0);

		String second = (String) arguments.get(1);
		if (!second.equalsIgnoreCase("press") && !second.equalsIgnoreCase("release"))
			throw new ParseException(
				"Expected \"press\" or \"release\" as the second argument to MouseConnect, but found "
					+ second);

		int theButton = Integer.parseInt(button);

		int direction;
		if (second.equalsIgnoreCase("press"))
			direction = MouseEvent.MOUSE_PRESSED;
		else
			direction = MouseEvent.MOUSE_RELEASED;

		Gizmo g = (Gizmo) nameToPiece.get((String) arguments.get(2));

		//System.out.println("Loading Gizmo: " + g + " Argument: " + arguments.get(2));

		if (3 == arguments.size()) {
			GizmoEvent ge =
				new GizmoEvent(
					GizmoEvent.Mouse_Event,
					theButton,
					direction,
					g,
					GizmoListener.DEFAULT_ACTION);
			PieceProperties.getEventHandler().bindInputToGizmo(ge);
		} else {
			GizmoEvent ge =
				new GizmoEvent(
					GizmoEvent.Mouse_Event,
					theButton,
					direction,
					g,
					(String) arguments.get(3));
			PieceProperties.getEventHandler().bindInputToGizmo(ge);
		}
	}

	private void doCreateWall(List arguments) {
		// DO NOTHING: Walls are pre-made already.	
	}
}
