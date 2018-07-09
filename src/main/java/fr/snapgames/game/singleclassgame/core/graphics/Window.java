/**
 * SnapGames
 * 
 * Game Development Java
 * 
 * singleclassgame
 * 
 * @year 2018
 */
package fr.snapgames.game.singleclassgame.core.graphics;

import java.awt.Graphics2D;

import javax.swing.JFrame;

import fr.snapgames.game.singleclassgame.Game;
import fr.snapgames.game.singleclassgame.core.input.KeyInputListener;

/**
 * The Window object will create layer between the game and the OS windowing
 * system. The window can have a size at `scale` regarding game viewport size.
 * 
 * 
 * @author Frédéric Delorme<frederic.delorme@snapgames.fr>
 */
public class Window {
	/**
	 * rendering frame.
	 */
	private JFrame frame;

	/**
	 * CReate a window containing the Game with a title.
	 *
	 * @param game  the game to display in the window.
	 * @param title the title of the window.
	 */
	public Window(Game game, String title) {
		frame = new JFrame(title);

		frame.setMaximumSize(game.dim);
		frame.setMinimumSize(game.dim);
		frame.setPreferredSize(game.dim);
		frame.setResizable(false);

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.setContentPane(game);

	}

	/**
	 * Show the window.
	 */
	public void show() {
		frame.setVisible(true);
	}

	/**
	 * Hide the window.
	 */
	public void hide() {
		frame.setVisible(false);
	}

	/**
	 * add a key listener to the window.
	 *
	 * @param kil
	 */
	public void setKeyInputListener(KeyInputListener kil) {
		frame.addKeyListener(kil);
	}

	/**
	 * Set the window title.
	 *
	 * @param title the new window title.
	 */
	public void setTitle(String title) {
		frame.setTitle(title);
	}

	/**
	 * retrieve the Graphics API for this window.
	 *
	 * @return
	 */
	public Graphics2D getGraphics() {
		return (Graphics2D) frame.getGraphics();
	}
}