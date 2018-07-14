/**
 * SnapGames
 * 
 * Project : SingleClassGame
 * 
 * <p>This project intends to propose a basement for **2D game development** with the `Java` language. 
 * 
 * @year 2018
 * @see http://snapgames.fr/
 */
package fr.snapgames.game.singleclassgame.audio;

import java.util.Map;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is intend to manage and control Sound play and output.
 * 
 * @author Frédéric Delorme.
 *
 */
public class SoundControl {
	private static final Logger logger = LoggerFactory.getLogger(SoundControl.class);
	/**
	 * Internal instance for the SoundControl system.
	 */
	private final static SoundControl instance = new SoundControl();

	private static final int MAX_SOUNDS_IN_STACK = 40;

	/**
	 * Internal play Stack
	 */
	Stack<String> soundsStack = new Stack<String>();
	/**
	 * Internal SoundBank.
	 */
	Map<String, SoundClip> soundBank = new ConcurrentHashMap<String, SoundClip>();

	private SoundControl() {
		soundsStack.setSize(MAX_SOUNDS_IN_STACK);
		logger.debug("Initialize SoundControl with {} stack places", MAX_SOUNDS_IN_STACK);
	}

	/**
	 * Load a Sound from <code>filename</code> to the sound bank.
	 * 
	 * @param filename
	 *            file name of the sound to be loaded to the
	 *            <code>soundBank</code>.
	 * @return filename if file has been loaded into the sound bank or null.
	 */
	public String load(String code, String filename) {
		if (!soundBank.containsKey(code)) {
			SoundClip sc = new SoundClip(filename);
			if (sc != null) {
				soundBank.put(code, sc);
				logger.debug("Load sound {} to sound bank with code {}", filename, code);
			}
			return filename;
		} else {
			return null;
		}
	}

	public void play(String code) {
		if (soundBank.containsKey(code)) {
			SoundClip sc = soundBank.get(code);
			sc.play();
			logger.debug("Play sound {}", code);
		} else {
			logger.error("unable to find the sound {} in the SoundBank !", code);
		}
	}

	public void play(String code, float volume) {
		if (soundBank.containsKey(code)) {
			SoundClip sc = soundBank.get(code);
			sc.play(0.5f, volume);
			logger.debug("Play sound {} with volume {}", code, volume);
		} else {
			logger.error("unable to find the sound {} in the SoundBank !", code);
		}
	}

	public void play(String code, float volume, float pan) {
		if (soundBank.containsKey(code)) {
			SoundClip sc = soundBank.get(code);
			sc.play(0.5f, volume);
			logger.debug("Play sound {} with volume {} and pan {}", code, volume, pan);
		} else {
			logger.error("unable to find the sound {} in the SoundBank !", code);
		}
	}
	public static SoundControl getInstance(){
		return instance;
	}
}