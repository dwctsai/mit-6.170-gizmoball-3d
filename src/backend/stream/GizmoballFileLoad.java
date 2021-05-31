/* @author David Tsai, Eric Tung
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
import plugins.pieces.FlipperFactory;
import plugins.pieces.Jezzmo3D;
import plugins.pieces.JezzmoFactory;
import plugins.pieces.SphericalBumperFactory;
import plugins.pieces.TriangularBumperAFactory;
import backend.Gizmo;
import backend.Piece;
import backend.PieceFactory;
import backend.event.GizmoEvent;
import backend.event.GizmoListener;
import backend.events.exceptions.EventException;

/**
 * GizmoballFileLoad - Translates from the 6.170 file format to
 * the *.g3d file format and then loads the game.
 */
public class GizmoballFileLoad {
	private PrintWriter output;
	private BufferedReader input;
	private static final int ysize = 19;

	private static final int zplane = 19;

	private PieceFactory pf;
	private Piece p;
	private HashMap nameToPiece = new HashMap();

	public static void main(String args[]) {
		try {
			GizmoballFileLoad td;

			if (args.length == 0) {
				td =
					new GizmoballFileLoad(
						new InputStreamReader(System.in),
						new OutputStreamWriter(System.out));
			} else {

				String fileName = args[0];
				File savefile = new File(fileName);

				if (savefile.exists() || savefile.canRead()) {
					td =
						new GizmoballFileLoad(
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
	 * @effects Creates a new GizmoballFileLoad from a File f.
	 */
	public GizmoballFileLoad(File f) {
		try {
			GizmoballFileLoad td;

			if (f.exists() || f.canRead()) {
				td =
					new GizmoballFileLoad(
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
	 * @requies r != null && w != null
	 *
	 * @effects Creates a new GizmoballFileLoad which reads the format from
	 * <tt>r</tt> and writes results to <tt>w</tt>.
	 **/
	public GizmoballFileLoad(Reader r, Writer w) {
		input = new BufferedReader(r);
		output = new PrintWriter(w);
	}

	/**
	 * @effects Translates from the input and writes results to the output
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

	private void executeCommand(String opcode, List arguments) {
		if (opcode.equalsIgnoreCase("Square")) {
			doCreateSquare(arguments);
		} else if (opcode.equalsIgnoreCase("Circle")) {
			doCreateCircle(arguments);
		} else if (opcode.equalsIgnoreCase("Triangle")) {
			doCreateTriangle(arguments);
		} else if (opcode.equalsIgnoreCase("RightFlipper")) {
			doCreateRightFlipper(arguments);
		} else if (opcode.equalsIgnoreCase("LeftFlipper")) {
			doCreateLeftFlipper(arguments);
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
		} else if (opcode.equalsIgnoreCase("Jezzmo")) {
			doCreateJezzmo(arguments);
		} else if (opcode.equalsIgnoreCase("MouseConnect")) {
			doMouseConnect(arguments);
		} else {
			throw new ParseException("Unrecognized opcode: " + opcode);
		}
	}

	private void doCreateSquare(List arguments) {
		if (arguments.size() != 3) {
			throw new ParseException(
				"Wrong number of arguments to Square: " + arguments);
		}

		// Create the gizmo.
		int xLocation = Integer.parseInt((String) arguments.get(1));
		int yLocation = ysize - Integer.parseInt((String) arguments.get(2));
		int zLocation = zplane;
		pf = new CubicalBumperFactory();
//		p = pf.makePiece(new Point3d(xLocation, yLocation, zLocation));

		String name = (String) arguments.get(0);
		
		p = pf.makePiece(name,
						 new Point3d(xLocation, yLocation, zLocation),
						 -1.0,
						 PieceProperties.defaultCubicalBumper3DXLength,
						 PieceProperties.defaultCubicalBumper3DYLength,
						 PieceProperties.defaultCubicalBumper3DZLength,
						 -1.0,
						 null,
						 PieceProperties.defaultCubicalBumper3DCOR,
						 PieceProperties.appCubicalBumper3DDefault,
						 true,
						 null);
						
						 
						 
		                

		p.addPiece();

		nameToPiece.put((String) arguments.get(0), p);
	}

	private void doCreateCircle(List arguments) {
		if (arguments.size() != 3) {
			throw new ParseException(
				"Wrong number of arguments to Circle: " + arguments);
		}

		// Create the gizmo.
		int xLocation = Integer.parseInt((String) arguments.get(1));
		int yLocation = ysize - Integer.parseInt((String) arguments.get(2));
		int zLocation = zplane;
		pf = new SphericalBumperFactory();
//		p = pf.makePiece(new Point3d(xLocation, yLocation, zLocation));

		String name = (String) arguments.get(0);
		
		p = pf.makePiece(name,
						 new Point3d(xLocation, yLocation, zLocation),
						 PieceProperties.defaultSphericalBumper3DRadius,
						 -1.0,
						 -1.0,
						 -1.0,
						 -1.0,
						 null,
						 PieceProperties.defaultSphericalBumper3DCOR,
						 PieceProperties.appSphericalBumper3DDefault,
						 true,
						 null);


		p.addPiece();

		nameToPiece.put((String) arguments.get(0), p);
	}

	private void doCreateTriangle(List arguments) {
		if (arguments.size() != 3) {
			throw new ParseException(
				"Wrong number of arguments to Triangle: " + arguments);
		}

		// Create the gizmo.
		int xLocation = Integer.parseInt((String) arguments.get(1));
		int yLocation = ysize - Integer.parseInt((String) arguments.get(2));
		int zLocation = zplane;
		pf = new TriangularBumperAFactory();
//		p = pf.makePiece(new Point3d(xLocation, yLocation, zLocation));

		String name = (String) arguments.get(0);
		
		p = pf.makePiece(name,
						 new Point3d(xLocation, yLocation, zLocation),
						 -1.0,
						 PieceProperties.defaultTriangularBumperA3DXLength,
						 PieceProperties.defaultTriangularBumperA3DYLength,
						 PieceProperties.defaultTriangularBumperA3DZLength,
						 -1.0,
						 null,
						 PieceProperties.defaultTriangularBumperA3DCOR,
						 PieceProperties.appTriangularBumperA3DDefault,
						 true,
						 null);


		p.addPiece();

		p.rotateYX90Graphics();

		nameToPiece.put((String) arguments.get(0), p);
	}

	private void doCreateRightFlipper(List arguments) {
		if (arguments.size() != 3) {
			throw new ParseException(
				"Wrong number of arguments to RightFlipper: " + arguments);
		}

		// Create the gizmo.
		int xLocation = Integer.parseInt((String) arguments.get(1));
		int yLocation = ysize - 1 - Integer.parseInt((String) arguments.get(2));
		int zLocation = zplane;
		pf = new FlipperFactory();
//		p = pf.makePiece(new Point3d(xLocation, yLocation, zLocation));

		String name = (String) arguments.get(0);
		
		p = pf.makePiece(name,
						 new Point3d(xLocation, yLocation, zLocation),
						 -1.0,
						 -1.0,
						 -1.0,
						 -1.0,
						 -1.0,
						 null,
						 PieceProperties.defaultFlipper3DCOR,
						 PieceProperties.appFlipper3DDefault,
						 true,
						 PieceProperties.defaultFlipper3DOrientation);



		p.addPiece();

	    p.rotateXZ90Graphics();
		p.rotateXZ90Graphics();

		nameToPiece.put((String) arguments.get(0), p);
	}

	private void doCreateLeftFlipper(List arguments) {
		if (arguments.size() != 3) {
			throw new ParseException(
				"Wrong number of arguments to RightFlipper: " + arguments);
		}

		// Create the gizmo.
		int xLocation = Integer.parseInt((String) arguments.get(1));
		int yLocation = ysize - 1 - Integer.parseInt((String) arguments.get(2));
		int zLocation = zplane;
		pf = new FlipperFactory();
//		p = pf.makePiece(new Point3d(xLocation, yLocation, zLocation));

		String name = (String) arguments.get(0);
		
		p = pf.makePiece(name,
						 new Point3d(xLocation, yLocation, zLocation),
						 -1.0,
						 -1.0,
						 -1.0,
						 -1.0,
						 -1.0,
						 null,
						 PieceProperties.defaultFlipper3DCOR,
						 PieceProperties.appFlipper3DDefault,
						 true,
						 PieceProperties.defaultFlipper3DOrientation);

		p.addPiece();

		nameToPiece.put((String) arguments.get(0), p);
	}

	private void doCreateAbsorber(List arguments) {
		if (arguments.size() != 5) {
			throw new ParseException(
				"Wrong number of arguments to Absorber: " + arguments);
		}

		// Create the gizmo.
		double xLocation = Double.parseDouble((String) arguments.get(1));
		double yLocation = ysize - Double.parseDouble((String) arguments.get(2));
		double zLocation = zplane;
		double xLocation2 = Double.parseDouble((String) arguments.get(3));
		double yLocation2 = ysize - Double.parseDouble((String) arguments.get(4));
		double xLength = xLocation2 - xLocation;
		double yLength = yLocation - yLocation2;
		double zLength = 1.0;

		pf = new AbsorberFactory();
		
		String name = (String) arguments.get(0);		
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
					1.0,
	PieceProperties.appAbsorber3DDefault, true, null);
		p.addPiece();

		nameToPiece.put((String) arguments.get(0), p);
	}

	private void doCreateBall(List arguments) {
		if (arguments.size() != 5) {
			throw new ParseException(
				"Wrong number of arguments to Ball: " + arguments);
		}

		// Create the ball.
		double xLocation = Double.parseDouble((String) arguments.get(1)) - 0.5;
		double yLocation = ysize - Double.parseDouble((String) arguments.get(2)) + 0.5;
		double zLocation = zplane;
		double xVelocity = Double.parseDouble((String) arguments.get(3));
		double yVelocity = Double.parseDouble((String) arguments.get(4));
		Vector3d v = new Vector3d(xVelocity, -yVelocity, 0.0);
		Transform3D velocityT3D = new Transform3D();
		velocityT3D.set(v);

		pf = new BallFactory();
		
		String name = (String) arguments.get(0);

		p =
			pf.makePiece(
				name,
				new Point3d(xLocation, yLocation, zLocation),
				0.25,
				-1.0,
				-1.0,
				-1.0,
				1.0,
				velocityT3D,
				-1.0,
				PieceProperties.appBall3DDefault,
				true,
				null);
				
		p.addPiece();
	}

	private void doRotate(List arguments) {
		if (arguments.size() != 1) {
			throw new ParseException(
				"Wrong number of arguments to Rotate: " + arguments);
		}

		// Do the action.
		p = (Piece) nameToPiece.get((String) arguments.get(0));
		p.rotateYX90Graphics();
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
		if (arguments.size() != 3) {
			throw new ParseException(
				"Wrong number of arguments to Move: " + arguments);
		}

		// Do the action.
		p = (Piece) nameToPiece.get((String) arguments.get(0));
		Point3d newLocation = new Point3d();
		newLocation.x = Integer.parseInt((String) arguments.get(1));
		newLocation.y = Integer.parseInt((String) arguments.get(2));
		newLocation.z = zplane;
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
		if (arguments.size() != 1) {
			throw new ParseException(
				"Wrong number of arguments to Gravity: " + arguments);
		}

		// Do the action.
		double gravity = Double.parseDouble((String) arguments.get(0));
		engine.Xeon.GRAVITY = new Vector3d(0.0, -gravity, 0.0);
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

	private void doCreateJezzmo(List arguments) {
		if (arguments.size() != 5) {
			throw new ParseException(
				"Wrong number of arguments to Jezzmo: " + arguments);
		}

		// Create the gizmo.
		int xLocation = Integer.parseInt((String) arguments.get(1));
		int yLocation = ysize - Integer.parseInt((String) arguments.get(2));
		int zLocation = zplane;
		double velocity = Double.parseDouble((String) arguments.get(3));

		Transform3D velocityT3D = new Transform3D();
		Vector3d tempV = new Vector3d(velocity, velocity, velocity);
		velocityT3D.set(tempV);

		String orientation = (String) arguments.get(4);
		if (orientation.equalsIgnoreCase("vertical")) {
			orientation = Jezzmo3D.Y_GROWTH;
		}
		else if (orientation.equalsIgnoreCase("horizontal")) {
			orientation = Jezzmo3D.X_GROWTH;
		}

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
				1.0,
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
		if (!button.equalsIgnoreCase("left-click") && !button.equalsIgnoreCase("right-click"))
			throw new ParseException(
				"Expected \"left-click\" or \"right-click\" as the first argument to MouseConnect, but found "
					+ button);

		String second = (String) arguments.get(1);
		if (!second.equalsIgnoreCase("press") && !second.equalsIgnoreCase("release"))
			throw new ParseException(
				"Expected \"press\" or \"release\" as the second argument to MouseConnect, but found "
					+ second);

		int direction, theButton;
		if (button.equalsIgnoreCase("left-click"))
			theButton = MouseEvent.BUTTON1;
		else
			theButton = MouseEvent.BUTTON3;
		if (second.equalsIgnoreCase("press"))
			direction = MouseEvent.MOUSE_PRESSED;
		else
			direction = MouseEvent.MOUSE_RELEASED;

		Gizmo g = (Gizmo) nameToPiece.get((String) arguments.get(2));

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
}
