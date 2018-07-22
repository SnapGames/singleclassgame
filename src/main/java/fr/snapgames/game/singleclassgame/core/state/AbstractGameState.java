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

import fr.snapgames.game.singleclassgame.Game;
import fr.snapgames.game.singleclassgame.core.entity.GameObject;

/**
 * 
 * @author Frédéric Delorme
 *
 */
public class AbstractGameState implements GameState {

	private String name;

	public AbstractGameState(Game game, String name) {
		this.name = name;
	}

	@Override
	public void initialize(Game game) {

	}

	@Override
	public void activate(Game game) {

	}

	@Override
	public void deactivate(Game game) {

	}

	@Override
	public void input(Game game) {

	}

	@Override
	public void update(Game game, float elapsed) {
		for (GameObject o : game.objects) {
			o.updatePhysic(elapsed);
		}
	}

	@Override
	public void render(Game game, Graphics2D g) {

		// if objects in the list, draw all those things
		if (game.objects != null && game.objects.size() > 0) {
			for (GameObject o : game.objects) {
				o.render(g);
			}
		}

	}

	@Override
	public String getName() {
		return name;
	}

}
