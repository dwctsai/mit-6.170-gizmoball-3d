/* @author Ragu Vijaykumar
 * @created on May 10, 2003
 */
package gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import backend.Piece;
import backend.event.GizmoEvent;
import backend.event.GizmoListener;
import backend.event.MagicKeyListener;
import backend.events.exceptions.EventException;

/**
 * TriggersConnectionsDialog - The user can use this dialog to modify the
 * triggers and connections of a gizmoball game piece.
 */
public final class TriggersConnectionsDialog
	extends JDialog
	implements
		ActionListener,
		KeyListener,
		MouseListener,
		ListSelectionListener,
		FocusListener {

	public static boolean SECOND_PIECE = false;
	private final GizmoListener listener;
	private Vector actions;
	private JButton mdButton = new JButton("On Mouse Down");
	private JButton muButton = new JButton("On Mouse Up");
	private JButton kdButton = new JButton("On Key Down");
	private JButton kuButton = new JButton("On Key Up");
	private JButton connButton = new JButton("On Gizmo Trigger");
	private JButton delButton = new JButton("Delete Selected Connection");
	private JButton okButton = new JButton("Done");
	private Vector theConnections;
	private PromptFrame pf;
	private int button_action = -1;
	private String button_type = "";
	private InputEvent event;
	private JList connectionList;
	private GizmoEvent gizmoEvent;
	private GizmoListener trigger;
	private boolean isActionList, triggerGizmo;
	private Map eventMap = new HashMap();
	private String eventMapKey = "";

	public TriggersConnectionsDialog(
		JFrame jf,
		String title,
		boolean modality,
		GizmoListener listener) {
		super(jf, title, modality);
		this.listener = listener;
		this.theConnections = new Vector();

		Container contentPane = getContentPane();
		GridBagLayout gbl = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(4, 4, 4, 4);
		contentPane.setLayout(gbl);
		this.refreshConnections();
		connectionList = new JList(theConnections);
		connectionList.addListSelectionListener(this);
		this.refreshConnectionList();
		JScrollPane scrollPane = new JScrollPane(connectionList);

		grid_add(scrollPane, gbl, gbc, 50, 100, 0, 0, 2, 5, getContentPane());

		grid_add(mdButton, gbl, gbc, 50, 20, 2, 0, 2, 1, getContentPane());
		grid_add(muButton, gbl, gbc, 50, 20, 2, 1, 2, 1, getContentPane());
		grid_add(kdButton, gbl, gbc, 50, 20, 2, 2, 2, 1, getContentPane());
		grid_add(kuButton, gbl, gbc, 50, 20, 2, 3, 2, 1, getContentPane());
		grid_add(connButton, gbl, gbc, 50, 20, 2, 4, 2, 1, getContentPane());

		mdButton.addActionListener(this);
		muButton.addActionListener(this);
		kdButton.addActionListener(this);
		kuButton.addActionListener(this);
		connButton.addActionListener(this);

		JLabel spacer = new JLabel("   ");
		JLabel spacer2 = new JLabel("   ");
		grid_add(delButton, gbl, gbc, 0, 0, 0, 5, 1, 1, getContentPane());
		grid_add(spacer, gbl, gbc, 50, 0, 1, 5, 1, 1, getContentPane());
		grid_add(spacer2, gbl, gbc, 50, 0, 2, 5, 1, 1, getContentPane());
		grid_add(okButton, gbl, gbc, 0, 0, 3, 5, 1, 1, getContentPane());

		delButton.addActionListener(this);
		okButton.addActionListener(this);

		// this.addFocusListener(this);
		this.triggerGizmo = false;

		super.setResizable(false);
		Point p = Gizmoball.theMainFrame.getLocationOnScreen();
		super.setLocation(p.x + 200, p.y + 200);

		super.pack();
		super.setVisible(true);
	}

	private void grid_add(
		Component c,
		GridBagLayout gbl,
		GridBagConstraints gbc,
		int weightx,
		int weighty,
		int x,
		int y,
		int w,
		int h,
		Container container) {
		// sets the constraints in gbl for component c based on the 
		// base constraings passed in as gbc, augmented with the other
		// constraints passed in (size, location, and weights).
		gbc.weightx = weightx;
		gbc.weighty = weighty;
		gbc.gridx = x;
		gbc.gridy = y;
		gbc.gridwidth = w;
		gbc.gridheight = h;
		gbl.setConstraints(c, gbc);
		container.add(c);
	}

	/**
	 * @effects Handles buttons actions. 
	 */
	public void actionPerformed(ActionEvent evt) {
		if (evt.getSource().equals(kdButton)) {
			handleNewKeyDown();
		}
		if (evt.getSource().equals(kuButton)) {
			handleNewKeyUp();
		}
		if (evt.getSource().equals(mdButton)) {
			handleNewMousePress();
		}
		if (evt.getSource().equals(muButton)) {
			handleNewMouseRelease();
		}
		if (evt.getSource().equals(connButton)) {
			handleNewGizmoConnection();
		}
		if (evt.getSource().equals(delButton)) {
			handleDeleteConnection();
		}
		if (evt.getSource().equals(okButton)) {
			handleDoneDialog();
		}
		if (evt.getActionCommand().equals("Add Action")) {
			isActionList = false;
			// System.out.println("Add Action: " + this.gizmoEvent);
			if (this.gizmoEvent != null) {

				if (this.triggerGizmo) {
					// System.out.println("Adding trigger");

					try {
						listener.addConnection(this.gizmoEvent);
					} catch (EventException e) {
						// DO NOTHING
					}

				} else {

					PieceProperties.getEventHandler().bindInputToGizmo(
						this.gizmoEvent);
				}
				this.refreshConnections();
				this.refreshConnectionList();
				this.gizmoEvent = null;
				this.setModal(false);
				this.setVisible(true);
			}
			this.triggerGizmo = false;
		}
	}

	private void handleNewKeyDown() {
		// System.out.println("Key down pressed");
		this.button_action = KeyEvent.KEY_PRESSED;
		this.button_type = "KEY";
		this.setModal(false);
		pf = new PromptFrame("Input", "Please press the key now");
		pf.addKeyListener(new MagicKeyListener(this));
		pf.addMouseListener(this);
		pf.setFocusable(true);
		pf.requestFocus();
	}

	private void handleNewKeyUp() {
		this.button_action = KeyEvent.KEY_RELEASED;
		this.button_type = "KEY";
		this.setModal(false);
		pf = new PromptFrame("Input", "Please press the key now");
		pf.addKeyListener(new MagicKeyListener(this));
		pf.addMouseListener(this);
		// this.removeFocusListener(this);
		pf.setFocusable(true);
		pf.requestFocus();

	}

	private void handleNewMousePress() {
		this.button_action = MouseEvent.MOUSE_PRESSED;
		this.button_type = "MOUSE";
		this.setModal(false);
		pf = new PromptFrame("Input", "Please press the key now");
		pf.addKeyListener(new MagicKeyListener(this));
		pf.addMouseListener(this);
		// this.removeFocusListener(this);
		pf.setFocusable(true);
		pf.requestFocus();

	}

	private void handleNewMouseRelease() {
		this.button_action = MouseEvent.MOUSE_RELEASED;
		this.button_type = "MOUSE";
		this.setModal(false);
		pf = new PromptFrame("Input", "Please press the key now");
		pf.addKeyListener(new MagicKeyListener(this));
		pf.addMouseListener(this);
		// this.removeFocusListener(this);
		pf.setFocusable(true);
		pf.requestFocus();
	}

	private void handleNewGizmoConnection() {
		// System.out.println("Gizmo connection button pressed");
		TriggersConnectionsDialog.SECOND_PIECE = true;
		this.setVisible(false);
		this.setModal(false);
		this.triggerGizmo = true;
		// this.removeFocusListener(this);
	}

	private void handleDeleteConnection() {
		// System.out.println("Delete button pressed");
		try {

			if (this.gizmoEvent != null) {

//				System.out.println(
//					"Is this a gizmo Trigger: "
//						+ this.gizmoEvent.isGizmoTrigger());
				// System.out.println("Gizmo Event: " + gizmoEvent.getInfo());

				this.eventMap.remove(this.eventMapKey);
				this.theConnections.remove(this.gizmoEvent);
				if (this.gizmoEvent.isGizmoTrigger()) {
					// Gizmo - gizmo trigger

					try {
						this.listener.delConnection(this.gizmoEvent);
					} catch (EventException e) {
						// DO NOTHING
					}

				} else {
					// Input-gizmo trigger

					PieceProperties.getEventHandler().removeInputFromGizmo(
						this.gizmoEvent);
				}

				this.refreshConnections();
				this.refreshConnectionList();
				this.gizmoEvent = null;
			}
		} catch (Exception e) {
			// DO NOTHING
		}
	}

	private void handleDoneDialog() {
		this.dispose();
	}

	/* (non-Javadoc)
	 * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
	 */
	public void keyTyped(KeyEvent event) {
		// DO NOTHING
	}

	/* (non-Javadoc)
	 * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
	 */
	public void keyPressed(KeyEvent event) {
		// DO NOTHING
	}

	/* (non-Javadoc)
	 * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
	 */
	public void keyReleased(KeyEvent event) {
		// System.out.println("Key Released!");
		if (this.button_type.equals("KEY")) {
			pf.dispose();
			this.event = event;
			this.promptForAction(this.listener);
		}
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	public void mouseClicked(MouseEvent event) {
		// DO NOTHING

	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	public void mousePressed(MouseEvent event) {
		// DO NOTHING

	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	public void mouseReleased(MouseEvent event) {

		// System.out.println("Mouse Released!");
		if (this.button_type.equals("MOUSE")) {
			pf.dispose();
			this.event = event;
			this.promptForAction(this.listener);
		}
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	public void mouseEntered(MouseEvent event) {
		// DO NOTHING
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	public void mouseExited(MouseEvent event) {
		// DO NOTHING

	}

	private void refreshConnections() {
		Set events =
			PieceProperties.getEventHandler().getInputsForGizmo(this.listener);
		Set triggers = listener.getConnections();
		Iterator eventIter = events.iterator();
		this.theConnections = new Vector();
		this.eventMap = new HashMap();
		while (eventIter.hasNext()) {
			GizmoEvent ge = (GizmoEvent) eventIter.next();
			String output = ge.getInfo();
			theConnections.add(output);
			this.eventMap.put(output, ge);
		}
		Iterator triggerIter = triggers.iterator();
		while (triggerIter.hasNext()) {
			GizmoEvent ge = (GizmoEvent) triggerIter.next();
			String output =
				"Triggering "
					+ ge.getCommand()
					+ " on "
					+ ge.getGizmo().toString();
			theConnections.add(output);
			this.eventMap.put(output, ge);
			// System.out.println("Trigger: " + output);
		}
	}

	private void refreshConnectionList() {
		this.connectionList.setListData(this.theConnections);
	}

	public void processSecondPiece(Piece p) {

		TriggersConnectionsDialog.SECOND_PIECE = false;

		if (p == null) {
			throw new IllegalArgumentException("Piece is null!");
		}

		if (p instanceof GizmoListener) {
			// The second piece is a valid piece
			// Need to process the second piece

			// System.out.println("Picked: " + p);
			this.promptForAction((GizmoListener) p);

			// this.addFocusListener(this);

		} else {
			// Not a valid piece
			JFrame errorPane = new JFrame();
			JOptionPane.showMessageDialog(
				errorPane,
				"You must select a gizmo. Balls are not allowed to be selected",
				"Gizmo Error",
				JOptionPane.ERROR_MESSAGE);
			this.setVisible(true);
			this.triggerGizmo = false;
		}
	}

	private void promptForAction(GizmoListener listener) {

		this.trigger = listener;
		this.isActionList = true;

		final JFrame actionFrame = new JFrame();

		actionFrame.getContentPane().setLayout(new BorderLayout());

		actionFrame.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent arg0) {
				// DO NOTHING
			}

			public void focusLost(FocusEvent arg0) {
				actionFrame.setFocusable(true);
				actionFrame.requestFocus();
			}
		});

		actions = new Vector(listener.getActions());
		actions.add("Default Action");

		JList actionList = new JList(actions);
		actionList.addListSelectionListener(this);
		actionList.setSelectedIndex(actions.size() - 1);

		JButton okButton = new JButton("Add Action");
		okButton.addActionListener(this);
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				actionFrame.setVisible(false);
				actionFrame.dispose();
				// System.out.println("Disposing");
			}
		});

		actionFrame.getContentPane().add(actionList, BorderLayout.CENTER);
		actionFrame.getContentPane().add(okButton, BorderLayout.SOUTH);

		actionFrame.setResizable(false);
		Point p = Gizmoball.theMainFrame.getLocationOnScreen();
		actionFrame.setLocation(p.x + 250, p.y + 250);
		actionFrame.setVisible(true);
		actionFrame.pack();
	}

	/* (non-Javadoc)
	 * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
	 */
	public void valueChanged(ListSelectionEvent event) {

		JList actionList = (JList) event.getSource();
		Object value = actionList.getSelectedValue();
//
//		System.out.println(
//			"Value: " + value + " Action List?: " + isActionList);

		if (value == null) {
			// DO NOTHING
		} else if (isActionList) {
			// Came from the Jlist of options

			String action = (String) value;
			String notifierType = "";
			int notifierCode = -1;
			int notifierAction = -1;
			if (action.equals("Default Action"))
				action = GizmoListener.DEFAULT_ACTION;

			if (this.event instanceof KeyEvent) {
				notifierType = GizmoEvent.Key_Event;
				notifierCode = ((KeyEvent) this.event).getKeyCode();
				notifierAction = this.button_action;
				this.gizmoEvent =
					new GizmoEvent(
						notifierType,
						notifierCode,
						notifierAction,
						this.trigger,
						action);
			} else if (this.event instanceof MouseEvent) {
				notifierType = GizmoEvent.Mouse_Event;
				notifierCode = ((MouseEvent) this.event).getButton();
				notifierAction = this.button_action;
				this.gizmoEvent =
					new GizmoEvent(
						notifierType,
						notifierCode,
						notifierAction,
						this.trigger,
						action);
			} else {
				this.gizmoEvent = new GizmoEvent(this.trigger, action);
			}

		} else {
			// Need to remove the gizmo event
			String action = (String) value;
			this.eventMapKey = action;
			this.gizmoEvent = (GizmoEvent) this.eventMap.get(action);

		}
	}

	public static void main(String[] args) {
		// System.out.println("Starting");
		TriggersConnectionsDialog connDiag =
			new TriggersConnectionsDialog(
				new JFrame(),
				"Gizmo Connections Settings",
				false,
				null);
		// System.out.println("Done");

	}

	// Inner classes

	/**
	 * Gets the input from the user
	 * 
	 * @author Ragu Vijaykumar
	 */
	private static class PromptFrame extends JFrame implements FocusListener {

		private InputEvent event;

		/**
		 * Creates a new PromptFrame
		 * @param name
		 */
		private PromptFrame(String name, String text) {
			super(name);

			JPanel textPanel = new JPanel();
			JLabel messageLabel = new JLabel(text);
			textPanel.add(messageLabel);
			this.getContentPane().add(textPanel);
			this.addFocusListener(this);
			this.setResizable(false);
			Point p = Gizmoball.theMainFrame.getLocationOnScreen();
			this.setLocation(p.x + 250, p.y + 250);
			this.setVisible(true);
			this.setFocusable(true);
			this.requestFocus();
			this.pack();
		}

		public InputEvent getInputEvent() {
			return event;
		}

		public boolean isFocusable() {
			return true;
		}

		/* (non-Javadoc)
		 * @see java.awt.event.FocusListener#focusGained(java.awt.event.FocusEvent)
		 */
		public void focusGained(FocusEvent event) {
			// System.out.println("Gained Focus!");
		}

		/* (non-Javadoc)
		 * @see java.awt.event.FocusListener#focusLost(java.awt.event.FocusEvent)
		 */
		public void focusLost(FocusEvent event) {
			// System.out.println("Lost Focus!");
			this.setFocusable(true);
			this.requestFocus();
		}
	}
	/* (non-Javadoc)
	 * @see java.awt.event.FocusListener#focusGained(java.awt.event.FocusEvent)
	 */
	public void focusGained(FocusEvent arg0) {
		// DO NOTHING

	}

	/* (non-Javadoc)
	 * @see java.awt.event.FocusListener#focusLost(java.awt.event.FocusEvent)
	 */
	public void focusLost(FocusEvent arg0) {
		// this.setFocusable(true);
		// this.requestFocus();
	}

}
