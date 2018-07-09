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

import fr.snapgames.game.singleclassgame.Game;

/**
 * The {@link GameKeyInput} is a key input handler to manage the Game Level key
 * listener.
 * <ul>
 * <li><key>ESCAPE</key> to quit the game,</li>
 * <li><key>PAUSE</key> to set game in the pause state.</li>
 * </ul>
 *
 * @author Frédéric Delorme<frederic.delorme@snapgames.fr>
 */
public class GameKeyInput implements KeyListener {

	private static final Logger logger = LoggerFactory.getLogger(GameKeyInput.class);

	Game game;

	public GameKeyInput(Game game) {
		this.game = game;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
	 */
	@Override
	public void keyPressed(KeyEvent e) {
		// Nothing to do here.
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
	 */
	@Override
	public void keyReleased(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_ESCAPE:
		case KeyEvent.VK_Q:
			game.exit = true;
			break;
		case KeyEvent.VK_PAUSE:
		case KeyEvent.VK_P:
			game.pause = !game.pause;
			logger.info(String.format("Pause mode set to %s", (game.pause ? "ON" : "OFF")));
			break;
		case KeyEvent.VK_F3:
		case KeyEvent.VK_D:
			game.debug = Math.floorMod(game.debug + 1, 5);
			logger.info(String.format("Debug level set to %d", game.debug));
			break;
		case KeyEvent.VK_S:
		case KeyEvent.VK_F12:
			logger.info("take ascreenshot");
			break;

		case KeyEvent.VK_R:
			logger.info("randomize enemies forces");
			game.randomizeEnemies = true;
			break;
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
	 */
	@Override
	public void keyTyped(KeyEvent arg0) {
		// Nothing to do here.

	}
}