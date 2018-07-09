/**
 * SnapGames
 * 
 * Game Development Java
 * 
 * singleclassgame
 * 
 * @year 2018
 */
package fr.snapgames.game.singleclassgame.core.input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * Main {@link KeyInputListener} at GameInstance level. It will manage multiple
 * Key Listeners on the window.
 * <p>
 * You can :
 * <ul>
 * <li>add a `KeyListener` with {@link KeyInputListener#register(KeyListener)},
 * <li>and remove at anytime a keyListener with
 * {@link KeyInputListener#remove(KeyListener)}
 * </ul>
 *
 * @author Frédéric Delorme<frederic.delorme@snapgames.fr>
 */
public class KeyInputListener implements KeyListener {
	
	private static final Logger logger = LoggerFactory.getLogger(KeyInputListener.class);
	
	
	/**
	 * current state of the key
	 */
	private boolean[] keys = new boolean[65235];
	/**
	 * Previous state of the key.
	 */
	private boolean[] previous = new boolean[65235];

	/**
	 * List of KeyListener to be called on key events.
	 */
	List<KeyListener> objectsCallBack = new ArrayList<>();

	/*
	 * (non-Javadoc)
	 *
	 * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
	 */
	@Override
	public void keyPressed(KeyEvent e) {
		previous[e.getKeyCode()] = keys[e.getKeyCode()];
		keys[e.getKeyCode()] = true;
		for (KeyListener kcb : objectsCallBack) {
			kcb.keyPressed(e);
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
	 */
	@Override
	public void keyReleased(KeyEvent e) {
		previous[e.getKeyCode()] = keys[e.getKeyCode()];
		keys[e.getKeyCode()] = false;
		for (KeyListener kcb : objectsCallBack) {
			kcb.keyReleased(e);
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
	 */
	@Override
	public void keyTyped(KeyEvent e) {
		for (KeyListener kcb : objectsCallBack) {
			kcb.keyPressed(e);
		}

	}

	/**
	 * This method will register a new key manager for a specific need.
	 *
	 * @param kcb
	 */
	public void register(KeyListener kcb) {
		if (!this.objectsCallBack.contains(kcb)) {
			this.objectsCallBack.add(kcb);
		} else {
			logger.error("The KeyInputListener already contains this {} key listener", kcb.getClass().getName());
		}
	}

	/**
	 * This is to remove a key listener from the system.
	 *
	 * @param kcb
	 */
	public void remove(KeyListener kcb) {
		this.objectsCallBack.remove(kcb);
	}

	/**
	 * Return current state of the key for <code>keyCode</code>.
	 * 
	 * @param keyCode Key code to be verified.
	 * @return true if pushed, else false.
	 */
	public boolean getKey(int keyCode) {
		return keys[keyCode];
	}

	/**
	 * Return the previous state of the key for <code>KeyCode</code>.
	 * 
	 * @param keyCode the Key code of the key to be verified.
	 * @return true if previously pushed, else false.
	 */
	public boolean getPrevious(int keyCode) {
		return previous[keyCode];
	}
}
