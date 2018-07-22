/**
 * SnapGames
 * 
 * Game Development Java
 * 
 * singleclassgame
 * 
 * @year 2018
 */
package fr.snapgames.game.singleclassgame.core.collision;

import fr.snapgames.game.singleclassgame.Game;
import fr.snapgames.game.singleclassgame.core.entity.GameObject;

/**
 * Interface explaining how to implement a Collision response process.
 * 
 * @author Frédéric Delorme
 *
 */
public interface CollisionResponseProcessor {
	/**
	 * When a Collision occurred, this Collision Response Processor method is called
	 * where `o1` and `o2` are the collision players.
	 * 
	 * @param o1 First GameObject participating in collision.
	 * @param o2 Second GameObject participating in collision.
	 */
	public void onCollide(Game game, GameObject o1, GameObject o2);
}