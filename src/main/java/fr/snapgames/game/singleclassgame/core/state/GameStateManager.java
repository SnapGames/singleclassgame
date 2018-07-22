/**
 * SnapGames
 * 
 * Game Development Java
 * 
 * singleclassgame
 * 
 * @year 2018
 */
package fr.snapgames.game.singleclassgame.core.state;

import java.awt.Graphics2D;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.snapgames.game.singleclassgame.Game;

/**
 * 
 * @author Frédéric Delorme
 *
 */
public class GameStateManager {

	private static final Logger logger = LoggerFactory.getLogger(GameStateManager.class);

	private Map<String, GameState> states = new HashMap<>();
	GameState current = null;

	public void add(GameState state) {
		if (!states.containsKey(state.getName())) {
			states.put(state.getName(), state);
		} else {
			logger.error("Unablet to add the state {}: it already exists in the state stack.", state.getName());
		}
	}

	public void load() {
		// TODO load states implementation from the configuration file.
	}

	public void start(Game game, String name) {
		if (states.containsKey(name)) {
			if (current != null) {
				current.deactivate(game);
			}
			current = states.get(name);
			current.activate(game);
		} else {
			logger.error("Unable to start {} because this state name does not exists !", name);

		}
	}

	public void initialize(Game game) {
		if (current != null) {
			current.initialize(game);
		}
	}

	public void update(Game game, float elapsed) {
		if (current != null) {
			current.update(game, elapsed);
		}
	}

	public void input(Game game) {
		if (current != null) {
			current.input(game);
		}
	}

	public void render(Game game, Graphics2D g) {
		if (current != null) {
			current.render(game, g);
		}
	}

}
