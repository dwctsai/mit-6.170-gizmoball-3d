/* @author David Tsai
 * @created on May 1, 2003
 */
package gui;
import javax.media.j3d.BackgroundSound;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.PointSound;
import javax.vecmath.Point3d;
/**
 * This class handles all sounds for Arena3D.
 */
public class Arena3DSounds extends BranchGroup {

	// CONSTRUCTOR
	/**
	 * @effects Creates an Arena3DLights.
	 */	
	public Arena3DSounds() {
		super();
		
		setupSoundsNode();
//		setupSoundsBehaviors();
		
	}
	
	
	
	private void setupSoundsNode() {
		
		BackgroundSound sound1 = new BackgroundSound();
		//		PointSound sound2 = new PointSound();
		//		PointSound sound3 = new PointSound();
		sound1.setCapability(PointSound.ALLOW_ENABLE_WRITE);
		sound1.setCapability(PointSound.ALLOW_INITIAL_GAIN_WRITE);
		sound1.setCapability(PointSound.ALLOW_SOUND_DATA_WRITE);
		sound1.setCapability(PointSound.ALLOW_SCHEDULING_BOUNDS_WRITE);
		sound1.setCapability(PointSound.ALLOW_CONT_PLAY_WRITE);
		sound1.setCapability(PointSound.ALLOW_RELEASE_WRITE);
		sound1.setCapability(PointSound.ALLOW_DURATION_READ);
		sound1.setCapability(PointSound.ALLOW_IS_PLAYING_READ);
		sound1.setCapability(PointSound.ALLOW_LOOP_WRITE);
		/*
		sound2.setCapability(PointSound.ALLOW_ENABLE_WRITE);
		sound2.setCapability(PointSound.ALLOW_INITIAL_GAIN_WRITE);
		sound2.setCapability(PointSound.ALLOW_SOUND_DATA_WRITE);
		sound2.setCapability(PointSound.ALLOW_SCHEDULING_BOUNDS_WRITE);
		sound2.setCapability(PointSound.ALLOW_CONT_PLAY_WRITE);
		sound2.setCapability(PointSound.ALLOW_RELEASE_WRITE);
		sound2.setCapability(PointSound.ALLOW_DURATION_READ);
		sound2.setCapability(PointSound.ALLOW_IS_PLAYING_READ);
		sound2.setCapability(PointSound.ALLOW_POSITION_WRITE);
		sound2.setCapability(PointSound.ALLOW_LOOP_WRITE);
		sound3.setCapability(PointSound.ALLOW_ENABLE_WRITE);
		sound3.setCapability(PointSound.ALLOW_INITIAL_GAIN_WRITE);
		sound3.setCapability(PointSound.ALLOW_SOUND_DATA_WRITE);
		sound3.setCapability(PointSound.ALLOW_SCHEDULING_BOUNDS_WRITE);
		sound3.setCapability(PointSound.ALLOW_CONT_PLAY_WRITE);
		sound3.setCapability(PointSound.ALLOW_RELEASE_WRITE);
		sound3.setCapability(PointSound.ALLOW_DURATION_READ);
		sound3.setCapability(PointSound.ALLOW_IS_PLAYING_READ);
		sound3.setCapability(PointSound.ALLOW_POSITION_WRITE);
		*/

		BoundingSphere soundBounds =
			new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0);
		sound1.setSchedulingBounds(soundBounds);
//		sound2.setSchedulingBounds(soundBounds);
//		sound3.setSchedulingBounds(soundBounds);
//		objTrans.addChild(sound1);
//		objTrans.addChild(sound2);
//		objTrans.addChild(sound3);	
		
	}
	
	/**
	* Create a new Behavior object that will play the sound
	*/
	/**
	 * @effects Creates a new Behavior object that will play the sound.
	 */	
	/* 64X
	private void setupSoundsBehaviors() {

		SimpleSoundsBehavior player =
			new SimpleSoundsBehavior(
				sound1,
				sound2,
				sound3,
				url[0],
				url[1],
				url[2],
				soundBounds);
		player.setSchedulingBounds(soundBounds);
		objTrans.addChild(player);
	}
	*/

}
