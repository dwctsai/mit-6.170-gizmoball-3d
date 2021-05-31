/* @author David Tsai
 * @created on Apr 30, 2003
 */

package gui;

import engine.Xeon;

import javax.media.j3d.Behavior;
import javax.media.j3d.WakeupCondition;
import javax.media.j3d.WakeupCriterion;
import javax.media.j3d.WakeupOnElapsedTime;

/**
 * PlayBehavior - used for a gizmoball game's Play mode.  It starts the play action.
 */
public class PlayBehavior extends Behavior {
	// FIELDS
	protected WakeupCondition m_WakeupCondition = null;

	// CONSTRUCTOR
	/**
	 * @effects constructs a PlayBehavior from the given parameters.
	 */
	public PlayBehavior() {
		m_WakeupCondition = new WakeupOnElapsedTime(Arena3DScene.TIME_PER_UPDATE);
	}

	// METHODS
	/**
	 * @effects initializes this behavior  
	 */
	public void initialize() {
		// apply the initial WakeupCriterion
		wakeupOn(m_WakeupCondition);
	}

	/**
	 * @effects initializes this behavior  
	 */
	public void processStimulus(java.util.Enumeration criteria) {
		while (criteria.hasMoreElements()) {
			WakeupCriterion wakeUp = (WakeupCriterion) criteria.nextElement();

			if (wakeUp instanceof WakeupOnElapsedTime) {
				Xeon.update(Arena3DScene.TIME_PER_UPDATE/1000.0);
			}
			
			// assign the next WakeUpCondition, so we are notified again
			wakeupOn(m_WakeupCondition);
		}
	}

}
