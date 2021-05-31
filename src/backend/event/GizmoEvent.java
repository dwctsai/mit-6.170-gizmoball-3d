package backend.event;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

/**
 * GizmoEvent will associate a gizmo with a particular event
 * @author Ragu Vijaykumar
 */
public class GizmoEvent {

	private final GizmoListener gizmo;
	private final String command;
	private final String notifierType;
	private final int notifierCode;
	private final int notifierAction;

	public static final String Gizmo_Event = new String("NONE");

	public static final String Key_Event = new String("KEY");

	public static final String Mouse_Event = new String("MOUSE");

	/**
	 * Creates a new Gizmo Event
	 * @param notifierType - mouse or key; use EventHandler static variables
	 * @param notifierCode - actual input button pressed
	 * @param notifierAction - what type of action the input button recieves
	 * @param gizmo - gizmo to be notified
	 * @param command - command to pass to gizmo
	 */
	public GizmoEvent(
		String notifierType,
		int notifierCode,
		int notifierAction,
		GizmoListener gizmo,
		String command) {
		this.gizmo = gizmo;
		this.command = command;
		this.notifierType = notifierType;
		this.notifierCode = notifierCode;
		this.notifierAction = notifierAction;
	}

	/**
	 * Constructor for gizmo-gizmo connections
	 * @param gizmo - gizmo to trigger
	 * @param command - command to be sent to gizmo
	 */
	public GizmoEvent(GizmoListener gizmo, String command) {
		this.notifierType = GizmoEvent.Gizmo_Event;
		this.notifierCode = Integer.MIN_VALUE;
		this.notifierAction = Integer.MIN_VALUE;
		this.gizmo = gizmo;
		this.command = command;
	}

	// Observers

	/**
	 * @return the gizmo that is involved with in the gizmo event
	 */
	public GizmoListener getGizmo() {
		return gizmo;
	}

	/**
	 * Get the event associated with this gizmo
	 * @return event
	 */
	public String getCommand() {
		return command;
	}

	/**
	 * Returns the type of input that this event is linked to
	 * @return
	 */
	public String getEventType() {
		return this.notifierType;
	}

	/**
	 * Returns the code corresponding to the event
	 * @return
	 */
	public int getEventCode() {
		return this.notifierCode;
	}

	/**
	 * Returns the action of the specific button pressed
	 * @return
	 */
	public int getEventAction() {
		return this.notifierAction;
	}

	/**
	 * Returns a string of the event action
	 * @return string of the event action; returns "unknown" if the event action is unknown
	 */
	public String getEventActionString() {

		if (this.notifierAction == KeyEvent.KEY_PRESSED) {
			return "down";
		}

		if (this.notifierAction == MouseEvent.MOUSE_PRESSED)
			return "press";

		if (this.notifierAction == KeyEvent.KEY_RELEASED) {
			return "up";
		}
		
		if(this.notifierAction == MouseEvent.MOUSE_RELEASED) {
			return "release";
		}

		return "unknown";
	}

	/**
	 * 
	 * @return
	 */
	public String getInfo() {

		String action = "UNKNOWN_ACTION";
		String type = "UNKNOWN_TYPE";
		String code = "UNKNOWN_CODE";
		String command = this.getCommand();

		if (notifierType.equals(GizmoEvent.Key_Event)) {
			type = "Key";
			code = KeyEvent.getKeyText(this.notifierCode);
			if (this.notifierAction == KeyEvent.KEY_PRESSED)
				action = "Pressed";
			else if (this.notifierAction == KeyEvent.KEY_RELEASED)
				action = "Released";
			else {
				action = "Unknown Key Action";
			}
		} else if (notifierType.equals(GizmoEvent.Mouse_Event)) {
			type = "Mouse";

			if (this.notifierCode == MouseEvent.BUTTON1) {
				code = "Button One";
			} else if (this.notifierCode == MouseEvent.BUTTON2) {
				code = "Button Two";
			} else if (this.notifierCode == MouseEvent.BUTTON3) {
				code = "Button Three";
			} else if (this.notifierCode == MouseEvent.MOUSE_WHEEL) {
				code = "Mouse Wheel";
			} else {
				code = "Unknown Mouse Code";
			}

			if (this.notifierAction == MouseEvent.MOUSE_PRESSED)
				action = "Pressed";
			else if (this.notifierAction == MouseEvent.MOUSE_RELEASED)
				action = "Released";
			else {
				action = "Unknown_Action";
			}
		}

		if (command.equals(GizmoListener.DEFAULT_ACTION))
			command = "Default Action";

		return new String(type + " " + code + " " + action + " --> " + command);
	}

	/**
	 * Whether this gizmo event is used for gizmo-gizmo triggering
	 * @return
	 */
	public boolean isGizmoTrigger() {
		return (this.notifierType.equals(GizmoEvent.Gizmo_Event));
	}

	/**
	 * @return unique sum of member variables hashcodes
	 */
	public int hashCode() {
		return this.gizmo.hashCode()
			+ this.command.hashCode()
			+ this.notifierAction * 17
			+ this.notifierCode * 19
			+ this.notifierType.hashCode();
	}

	/**
	 * @return true iff o is not null and o is an instanceof a GizmoEvent and the
	 * specfields of each match this
	 */
	public boolean equals(Object o) {

		if (o != null && o instanceof GizmoEvent) {

			GizmoEvent ge = (GizmoEvent) o;

			return (
				ge.gizmo.equals(gizmo)
					&& ge.command.equals(this.command)
					&& this.notifierAction == ge.notifierAction
					&& this.notifierCode == ge.notifierCode
					&& this.notifierType.equals(ge.notifierType));
		}

		return false;
	}

	/**
	 * @return string representation of this GizmoEvent
	 */
	public String toString() {
		return new String(
			"GizmoEvent: " + gizmo + " with command: " + this.command);
	}

}
