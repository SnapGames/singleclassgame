package fr.snapgames.game.singleclassgame.core.state;

import java.awt.Graphics2D;

import fr.snapgames.game.singleclassgame.Game;

/**
 * This interface will describe a phase of the game, which can be instantiated,
 * activated and deleted on demand
 * 
 * @author Frédéric Delorme
 *
 */
public interface GameState {

	/**
	 * Initialize this state.
	 * 
	 * @param game
	 */
	public void initialize(Game game);

	/**
	 * Activate this state just after been initialized
	 * 
	 * @param game
	 */
	public void activate(Game game);

	/**
	 * Deactivate this state just after been initialized
	 * 
	 * @param game
	 */
	public void deactivate(Game game);

	/**
	 * manage input for this state.
	 * 
	 * @param game
	 */
	public void input(Game game);

	/**
	 * Update this state.
	 * 
	 * @param game
	 * @param elapsed
	 */
	public void update(Game game, float elapsed);

	/**
	 * render this state.
	 * 
	 * @param game
	 * @param g
	 */
	public void render(Game game, Graphics2D g);

	/**
	 * retrieve Name of this state.
	 * 
	 * @return
	 */
	public String getName();

}
