/**
 * SnapGames
 * 
 * Game Development Java
 * 
 * singleclassgame
 * 
 * @year 2018
 */
package fr.snapgames.game.singleclassgame.sample;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.snapgames.game.singleclassgame.core.entity.GameObject;
import fr.snapgames.game.singleclassgame.core.input.KeyInputListener;
import fr.snapgames.game.singleclassgame.core.math.Vector2D;

/**
 * <p>
 * A specific Key Listener for the Player object.
 * <p>
 * This key listener manage the directional keys:
 * <ul>
 * <li><code>UP</code> to move up,
 * <li><code>DOWN</code> to move down,
 * <li><code>LEFT</code> to move left,
 * <li><code>RIGHT</code> to move right,
 * <li><code>SPACE</code> to stop all.
 * </ul>
 *
 * @author Frédéric Delorme<frederic.delorme@snapgames.fr>
 */
public class PlayerKeyInput implements KeyListener {

	private static final Logger logger = LoggerFactory.getLogger(PlayerKeyInput.class);

	GameObject player = null;
	KeyInputListener kil = null;
	boolean move = false;

	public PlayerKeyInput(GameObject o, KeyInputListener kil) {
		player = o;
		this.kil = kil;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
	 */
	@Override
	public void keyPressed(KeyEvent e) {
		if (kil.getKey(KeyEvent.VK_UP)) {
			move = true;
			player.forces.add(new Vector2D("move-up", 0.0f, -player.moveFactor * 10.0f));
			logger.debug("player move up y+={}", -player.moveFactor);
		}
		if (kil.getKey(KeyEvent.VK_DOWN)) {
			move = true;
			player.forces.add(new Vector2D("move-down", 0.0f, player.moveFactor));
			logger.debug("player move down y+={}", player.moveFactor);
		}
		if (kil.getKey(KeyEvent.VK_LEFT)) {
			move = true;
			player.forces.add(new Vector2D("move-left", -player.moveFactor, 0.0f));
			logger.debug("player move left x+={}", player.moveFactor);
		}

		if (kil.getKey(KeyEvent.VK_RIGHT)) {
			move = true;
			player.forces.add(new Vector2D("move-right", player.moveFactor, 0.0f));
			logger.debug("player move left x+={}", -player.moveFactor);
		}

		if (kil.getKey(KeyEvent.VK_SPACE)) {
			move = true;
			player.acceleration.x = 0.0f;
			player.acceleration.y = 0.0f;
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
	 */
	@Override
	public void keyReleased(KeyEvent e) {
		move = false;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
	 */
	@Override
	public void keyTyped(KeyEvent e) {
		// Nothing to do here.
	}

}
