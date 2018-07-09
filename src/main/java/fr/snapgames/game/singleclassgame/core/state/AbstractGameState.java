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
		// TODO Auto-generated method stub

	}

	@Override
	public void activate(Game game) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deactivate(Game game) {
		// TODO Auto-generated method stub

	}

	@Override
	public void input(Game game) {
		// TODO Auto-generated method stub

	}

	@Override
	public void update(Game game, float elapsed) {
		// TODO Auto-generated method stub

	}

	@Override
	public void render(Game game, Graphics2D g) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getName() {
		return name;
	}

}
