/* @author David Tsai
 * @created on Apr 26, 2003
 */
package backend.stream;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

import backend.Gizmo;
import backend.Piece;
import backend.event.GizmoEvent;
import backend.event.GizmoListener;
import engine.Xeon;
import gui.Arena3DScene;
import gui.PieceProperties;

/**
 * GizmoballFileSaveG3d - saves a gizmoball file in the *.g3d format.
 */
public class GizmoballFileSaveG3d {

	// CONSTRUCTORS

	/**
	 * Creates a GizmoballFileSaveG3d from the given parameters.
	 *  
	 * @effects Writes the current gizmoball game settings to outputFile.
	 * @param outputFile
	 * @throws IOException
	 */
	public GizmoballFileSaveG3d(File outputFile) throws IOException {
		FileWriter out = new FileWriter(outputFile);

        out.write("Gravity " + Xeon.GRAVITY.x + " " + Xeon.GRAVITY.y + " " + Xeon.GRAVITY.z + "\n");
        out.write("Friction " + Xeon.MU + " " + Xeon.MU_2 + "\n");
        out.write("TimePerUpdate " + gui.Arena3DScene.TIME_PER_UPDATE + "\n");

		for (int i = 0; i < Arena3DScene.allBalls.size(); i++) {
			Piece g = (Piece) Arena3DScene.allBalls.get(i);
			out.write(g.unparse() + "\n");
		}

		for (int j = 0; j < Arena3DScene.allGizmos.size(); j++) {
			Piece g = (Piece) Arena3DScene.allGizmos.get(j);
			out.write(g.unparse() + "\n");
		}
		
		for (int j = 0; j < Arena3DScene.allGizmos.size(); j++) {
			Gizmo g = (Gizmo) Arena3DScene.allGizmos.get(j);
			
			Set connections = g.getConnections();
			Iterator connIter = connections.iterator();
			String output = "";
			
			while (connIter.hasNext()) {
				GizmoEvent ge = (GizmoEvent) connIter.next();

				String command = ge.getCommand();
				if (command.equalsIgnoreCase(GizmoListener.DEFAULT_ACTION))
					command = "";

				output += "Connect "
					+ g.toString()
					+ " "
					+ ge.getGizmo().toString()
					+ " "
					+ command
					+ "\n";
			}
			
			out.write(output.trim() + "\n");
		}

		Set events = PieceProperties.getEventHandler().events();
		Iterator eventIter = events.iterator();
		while (eventIter.hasNext()) {

			GizmoEvent gizmoEvent = (GizmoEvent) eventIter.next();
			String command = gizmoEvent.getCommand();
			if (command.equalsIgnoreCase(GizmoListener.DEFAULT_ACTION)) {
				command = "";
			}

			String output = "";

			if (gizmoEvent.getEventType().equalsIgnoreCase(GizmoEvent.Key_Event)) {
				// Key Event

				output =
					"KeyConnect key "
						+ gizmoEvent.getEventCode()
						+ " "
						+ gizmoEvent.getEventActionString()
						+ " "
						+ gizmoEvent.getGizmo().toString()
						+ " "
						+ command
						+ "\n";
			}

			if (gizmoEvent.getEventType().equalsIgnoreCase(GizmoEvent.Mouse_Event)) {
				// Mouse Event

				output =
					"MouseConnect "
						+ gizmoEvent.getEventCode()
						+ " "
						+ gizmoEvent.getEventActionString()
						+ " "
						+ gizmoEvent.getGizmo().toString()
						+ " "
						+ command
						+ "\n";
			}

			out.write(output);

		}

		out.close();

	}

	/**
	 * Empty constructor.
	 * 
	 * @effect Creates a GizmoballFileSaveG3d.
	 */
	public GizmoballFileSaveG3d() {

	}

	// METHODS

	/**
	 * @return A String of the current gizmoball game settings.
	 */
	public String GizmoballFileSaveG3dToString() {
		String result = "";

		for (int i = 0; i < Arena3DScene.allBalls.size(); i++) {
			Piece g = (Piece) Arena3DScene.allBalls.get(i);
			result += g.unparse() + "\n";
		}

		for (int j = 0; j < Arena3DScene.allGizmos.size(); j++) {
			Piece g = (Piece) Arena3DScene.allGizmos.get(j);
			result += g.unparse() + "\n";
		}
		
		for (int j = 0; j < Arena3DScene.allGizmos.size(); j++) {
			Gizmo g = (Gizmo) Arena3DScene.allGizmos.get(j);
			
			Set connections = g.getConnections();
			Iterator connIter = connections.iterator();
			String output = "";
			
			while (connIter.hasNext()) {
				GizmoEvent ge = (GizmoEvent) connIter.next();

				String command = ge.getCommand();
				if (command.equalsIgnoreCase(GizmoListener.DEFAULT_ACTION))
					command = "";

				output += "Connect "
					+ g.toString()
					+ " "
					+ ge.getGizmo().toString()
					+ " "
					+ command
					+ "\n";
			}
			
			result += output.trim() + "\n";
		}

		Set events = PieceProperties.getEventHandler().events();
		Iterator eventIter = events.iterator();
		while (eventIter.hasNext()) {

			GizmoEvent gizmoEvent = (GizmoEvent) eventIter.next();
			String command = gizmoEvent.getCommand();
			if (command.equalsIgnoreCase(GizmoListener.DEFAULT_ACTION)) {
				command = "";
			}

			String output = "";

			if (gizmoEvent.getEventType().equalsIgnoreCase(GizmoEvent.Key_Event)) {
				// Key Event

				output =
					"KeyConnect key "
						+ gizmoEvent.getEventCode()
						+ " "
						+ gizmoEvent.getEventActionString()
						+ " "
						+ gizmoEvent.getGizmo().toString()
						+ " "
						+ command
						+ "\n";
			}

			if (gizmoEvent.getEventType().equalsIgnoreCase(GizmoEvent.Mouse_Event)) {
				// Mouse Event

				output =
					"MouseConnect "
						+ gizmoEvent.getEventCode()
						+ " "
						+ gizmoEvent.getEventActionString()
						+ " "
						+ gizmoEvent.getGizmo().toString()
						+ " "
						+ command
						+ "\n";
			} 
			else {
//				System.err.println("Event Type: " + gizmoEvent.getEventType());
			}

			result += output;
		}

		return result;
	}

}
