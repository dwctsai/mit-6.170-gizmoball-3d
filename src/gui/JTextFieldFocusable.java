/* @author David Tsai
 * @created on May 10, 2003
 */
package gui;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JOptionPane;
import javax.swing.JTextField;

/**
 * 
 * 
 * 
 */
public class JTextFieldFocusable extends JTextField implements FocusListener {
	// FIELDS
	private String theName;
	private String theText;

	// CONSTRUCTOR
	public JTextFieldFocusable(String theName, String theText) {
		super(theText);
		
		this.theName = theName;
		this.theText = theText;
		
		this.addFocusListener(this);
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
		
		if (theName.equals("Gravity X") ||
			theName.equals("Gravity Y") ||
			theName.equals("Gravity Z")) {
				try {
					Double.parseDouble(this.getText());
				} catch (Exception e) {
					JOptionPane.showMessageDialog(this,
						"Please enter a number.",
						"You didn't enter a number for this field.",
						JOptionPane.ERROR_MESSAGE);
					this.setFocusable(true);
					this.requestFocus();
				}
		}

		if (theName.equals("MU1") ||
			theName.equals("MU2")) {
				try {
					Double.parseDouble(this.getText());
				} catch (Exception e) {
					JOptionPane.showMessageDialog(this,
						"Please enter a number.",
						"You didn't enter a number for this field.",
						JOptionPane.ERROR_MESSAGE);
					this.setFocusable(true);
					this.requestFocus();					
				}
		}
		
		
		
	}
	
	

}
