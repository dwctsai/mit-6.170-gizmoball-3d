/*
 * Created on Apr 27, 2003
 *
 * @author Ragu Vijaykumar
 */
package backend.event;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import backend.events.exceptions.InvalidInputException;

/**
 *	EventHandler - handles all key and mouse connections for gizmos
 *
 *  @author Ragu Vijaykumar
 */
public class EventHandler implements KeyListener, MouseListener {

	private final Map inputMap;
	private final Map gizmoMap;
	private final Set notifierSet;
	private final KeyListener wrapperListener;
	private final Set gizmoEvents;
	private Component topLevel;

	// Rep Invariant:
	// wrapperListener != null
	// notifierSet is not null and contains no duplicate elements
	// inpupMap is not null

	/**
	 * Creates a new Event Handling system 
	 */
	public EventHandler() {
		this.inputMap = new HashMap();
		this.gizmoMap = new HashMap();
		this.notifierSet = new HashSet();
		this.gizmoEvents = new HashSet();
		this.wrapperListener = new MagicKeyListener(this);
	}

	// Mutators

	/**
	 * Adds an input binding to this gizmo
	 * @param event - gizmo event
	 * @modifies this
	 */
	public void bindInputToGizmo(GizmoEvent event) {

		if (event == null)
			throw new NullPointerException("Gizmo Event is null!");

		if (!event.getEventType().equals(GizmoEvent.Key_Event)
			&& !event.getEventType().equals(GizmoEvent.Mouse_Event))
			throw new InvalidInputException(
				"Gizmo Event is not allowed for this type: "
					+ event.getEventType());

		Object value = this.inputMap.get(event.getEventType());
		Map inputTypeMap;

		if (value == null) {
			// This is a new type of input

			inputTypeMap = new HashMap();
			this.inputMap.put(event.getEventType(), inputTypeMap);

		} else {
			// There exists this type of input

			inputTypeMap = (Map) value;
		}

		Integer button = new Integer(event.getEventCode());
		Integer action = new Integer(event.getEventAction());

		value = inputTypeMap.get(button);

		if (value == null) {
			// This is the first type of mouse button to be added

			Map actionMap = new HashMap(10);
			inputTypeMap.put(button, actionMap);

			Set geSet = new HashSet();
			geSet.add(event);

			actionMap.put(action, geSet);

		} else {
			// There exists a mapping to this mouse button

			Map actionMap = (Map) value;
			value = actionMap.get(action);

			if (value == null) {
				// New mouse action

				Set geSet = new HashSet();
				geSet.add(event);
				actionMap.put(action, geSet);
			} else {
				// There is a mapping to this key
				Set geSet = (Set) value;
				geSet.add(event);
			}
		}

		// Append to the map of gizmos -> gizmo events

		value = this.gizmoMap.get(event.getGizmo());

		if (value == null) {
			// This is the first mapping to this gizmo
			Set events = new HashSet();
			events.add(event);
			this.gizmoMap.put(event.getGizmo(), events);
		} else {
			// There are already mappings to this gizmo
			Set events = (Set) value;
			events.add(event);
			this.gizmoMap.put(event.getGizmo(), events);
		}

		this.gizmoEvents.add(event);

	}

	/**
	 * Removes the input binding from the gizmo
	 * @param event - gizmo event to remove
	 * @throws InvaliInputException - if there is no binding to this gizmo
	 * @modifies this
	 */
	public void removeInputFromGizmo(GizmoEvent event) {

		if (event == null)
			throw new NullPointerException("Cannot remove Mouse Binding from a null gizmo!");

		if (!event.getEventType().equals(GizmoEvent.Key_Event)
			&& !event.getEventType().equals(GizmoEvent.Mouse_Event))
			throw new InvalidInputException(
				"Gizmo Event is not allowed for this type: "
					+ event.getEventType());

		if (!this.inputMap.containsKey(event.getEventType())) {
			// There is no mapping to this type of input

			throw new InvalidInputException(
				"Input mapping doesn't exist to gizmo event"
					+ event.getEventType());
		} else {
			// There is a mapping to this type of input

			Map inputTypeMap = (Map) this.inputMap.get(event.getEventType());

			Integer button = new Integer(event.getEventCode());

			if (!inputTypeMap.containsKey(button)) {
				// Mouse Button does not map to any gizmos

				throw new InvalidInputException(
					"Input mapping doesn't exist to gizmo event"
						+ event.getEventCode());

			} else {
				// This is an existing mouse button binding

				Map actionMap = (Map) inputTypeMap.get(button);
				Integer action = new Integer(event.getEventAction());
				if (!actionMap.containsKey(action)) {
					// Mouse action doesn't map to any gizmos			
					throw new InvalidInputException(
						"Input mapping doesn't exist to input action: "
							+ event.getEventAction());
				} else {
					// Mouse mapping exists

					Set geSet = (Set) actionMap.get(action);

					boolean found = geSet.remove(event);

					if (!found)
						throw new InvalidInputException("Failed to remove gizmo: No mapping present to gizmo");

					this.gizmoEvents.remove(event);

					Set events = (Set) this.gizmoMap.get(event.getGizmo());
					events.remove(event);
				}
			}
		}
	}

	/**
	 * Returns a set of all the inputs for the particular gizmo
	 * @param gizmo - gizmo to search for inputs
	 * @return - inputs tied to this gizmo; input is in the form of a GizmoEvent
	 */
	public Set getInputsForGizmo(GizmoListener gizmo) {
		Object value = this.gizmoMap.get(gizmo);

		if (value == null)
			return Collections.unmodifiableSet(new HashSet(1));
		else
			return Collections.unmodifiableSet((Set) value);
	}

	/**
	 * Removes all input bindings to gizmos
	 * @modifies this
	 */
	public void clearBindings() {
		this.inputMap.clear();
		this.gizmoMap.clear();
		this.gizmoEvents.clear();
	}

	/**
	 * Returns a read-only set of all the events
	 * @return event set
	 */
	public Set events() {
		return Collections.unmodifiableSet(this.gizmoEvents);
	}

	// ---------------------------------- Handles Notifiers -------------------------------------------

	/**
	 * Adds a component that should 
	 * @param notifier - component that notifies of event presses; 
	 * 				it is not activated till enableEventNotifiers is called
	 * @return true iff notifier was added
	 */
	public boolean addEventNotifier(Component notifier) {
		if (notifier == null)
			throw new NullPointerException("Cannot have a null notifier!");

		return this.notifierSet.add(notifier);
	}

	/**
	 * Remove one notifier from the notifying gizmos of key presses
	 * @param notifier - notifier is immediately removed from listening
	 * @return true iff notifier was removed
	 */
	public boolean removeEventNotifier(Component notifier) {

		if (notifier == null)
			throw new NullPointerException("Cannot have a null notifier!");

		if (!this.notifierSet.contains(notifier))
			return false;
		else {
			notifier.removeKeyListener(this.wrapperListener);
			notifier.removeMouseListener(this);
			return this.notifierSet.remove(notifier);
		}
	}

	/**
	 * Disables all notifiers from notifying gizmos about events
	 * @modifies this
	 */
	public void disableEventNotifiers() {
		Iterator notifierIter = this.notifierSet.iterator();
		while (notifierIter.hasNext()) {
			Component notifier = (Component) notifierIter.next();
			notifier.removeKeyListener(this.wrapperListener);
			notifier.removeMouseListener(this);
		}
	}

	/**
	 * Enables all notifiers to notify gizmos of events
	 * @modifies this
	 */
	public void enableEventNotifiers() {
		Iterator notifierIter = this.notifierSet.iterator();
		while (notifierIter.hasNext()) {
			Component notifier = (Component) notifierIter.next();
			notifier.addKeyListener(this.wrapperListener);
			notifier.addMouseListener(this);
		}
	}

	//------------------------------------ Handles Key Listening ----------------------------------------

	/* (non-Javadoc)
	 * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
	 */
	public void keyTyped(KeyEvent event) {
		this.handleInputEvent(
			GizmoEvent.Key_Event,
			event.getKeyCode(),
			KeyEvent.KEY_TYPED);
	}

	/* (non-Javadoc)
	 * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
	 */
	public void keyPressed(KeyEvent event) {
		this.handleInputEvent(
			GizmoEvent.Key_Event,
			event.getKeyCode(),
			KeyEvent.KEY_PRESSED);
	}

	/* (non-Javadoc)
	 * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
	 */
	public void keyReleased(KeyEvent event) {
		this.handleInputEvent(
			GizmoEvent.Key_Event,
			event.getKeyCode(),
			KeyEvent.KEY_RELEASED);
	}

	// ----------------------------- Handles Mouse Listening ----------------------------------------

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	public void mouseClicked(MouseEvent event) {
		this.handleInputEvent(
			GizmoEvent.Mouse_Event,
			event.getButton(),
			MouseEvent.MOUSE_CLICKED);
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	public void mousePressed(MouseEvent event) {
		this.handleInputEvent(
			GizmoEvent.Mouse_Event,
			event.getButton(),
			MouseEvent.MOUSE_PRESSED);
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	public void mouseReleased(MouseEvent event) {
		this.handleInputEvent(
			GizmoEvent.Mouse_Event,
			event.getButton(),
			MouseEvent.MOUSE_RELEASED);
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	public void mouseEntered(MouseEvent event) {
		this.handleInputEvent(
			GizmoEvent.Mouse_Event,
			event.getButton(),
			MouseEvent.MOUSE_ENTERED);
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	public void mouseExited(MouseEvent event) {
		this.handleInputEvent(
			GizmoEvent.Mouse_Event,
			event.getButton(),
			MouseEvent.MOUSE_EXITED);
	}

	// -------------------------------- Handles Events ------------------------------------

	/**
	 * Handles a key event that occurs
	 * @param keyButton
	 * @param keyAction
	 */
	private void handleInputEvent(
		String eventType,
		int eventButton,
		int eventAction) {

		if (this.inputMap.containsKey(eventType)) {
			Map inputTypeMap = (Map) this.inputMap.get(eventType);

			Integer button = new Integer(eventButton);
			if (inputTypeMap.containsKey(button)) {
				// There is a mapping to this key

				Integer action = new Integer(eventAction);
				Map actionMap = (Map) inputTypeMap.get(button);

				if (actionMap.containsKey(action)) {
					// There exists a mapping to this particular action of the key

					Set geSet = (Set) actionMap.get(action);
					Iterator geIter = geSet.iterator();
					while (geIter.hasNext()) {
						GizmoEvent ge = (GizmoEvent) geIter.next();
						ge.getGizmo().action(ge);
					}
				}
			}
		}
	}

	// -------------------- Handles Top Level Notifier Component -------------------------------

	/**
	 * The top level component is who recieves focus any time focus needs
	 * to be switched
	 * @param topLevel
	 */
	public void setTopLevelComponent(Component topLevel) {
		this.topLevel = topLevel;
	}

	/**
	 * Returns the top level component
	 * @return top level component
	 */
	public Component getTopLevelComponent() {
		return this.topLevel;
	}

	/**
	 * Tells the topLevel component to recieve focus to recieve input events
	 */
	public void requestFocus(boolean isVisible) {
		topLevel.setVisible(isVisible);
		topLevel.setFocusable(true);
		topLevel.requestFocus();
	}
}
