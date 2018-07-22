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
package fr.snapgames.game.singleclassgame.core.state;

import java.awt.Graphics2D;

import fr.snapgames.game.singleclassgame.Game;

/**
 * <p>
 * The {@link GameState} interface will describe a phase of the game, which can
 * be instantiated, activated and deleted on demand.
 * 
 * <p>
 * This interface is managed by the {@link GameStateManager}, to switch smoothly
 * from one game state to another.
 * 
 * The game loop delegated methods are:
 * <ul>
 * <li>{@link GameState#input(Game)} to manage user input on keyboard or mouse,
 * or anything input commands,
 * <li>{@link GameState#update(Game, float)} will delegate the State objects
 * update processing,
 * <li>{@link GameState#render(Game, Graphics2D)} to render this particular
 * state to the screen.
 * </ul>
 * 
 * <p>
 * The other methods will be called by the GSM to manage creation, instantiation
 * activation and de-activation of this state by the {@link GameStateManager}.
 * 
 * <ul>
 * <li>{@link GameState#initialize(Game)} to initialize all necessary resources
 * for this state,
 * <li>{@link GameState#activate(Game)} to activate this state,
 * <li>{@link GameState#deactivate(Game)} to disable this state.
 * </ul>
 * 
 * @author Frédéric Delorme
 * 
 * @see AbstractGameState
 * @see GameStateManager
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
	 * manage input for this state. This will be called from the main game loop
	 * through a delegated method by <code>GSM</code>.
	 * 
	 * @param game
	 */
	public void input(Game game);

	/**
	 * Update this state. this will be called from the main game loop through a
	 * delegated method by <code>GSM</code>.
	 * 
	 * @param game
	 * @param elapsed
	 */
	public void update(Game game, float elapsed);

	/**
	 * render this state. This will be called from the main game loop through a
	 * delegated method by <code>GSM</code>.
	 * 
	 * @param game
	 * @param g
	 */
	public void render(Game game, Graphics2D g);

	/**
	 * retrieve the internal Name of this state. This is mainly called by the
	 * <code>GSM</code> at finding a particular state in the states stack.
	 * 
	 * @return
	 */
	public String getName();

}
