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

import fr.snapgames.game.singleclassgame.Game;
import fr.snapgames.game.singleclassgame.core.collision.CollisionResponseProcessor;
import fr.snapgames.game.singleclassgame.core.entity.GameObject;
import fr.snapgames.game.singleclassgame.core.math.Vector2D;

/**
 * This is a simple response processor
 * 
 * @author Frédéric Delorme
 *
 */
public class ColliderResponse implements CollisionResponseProcessor {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * fr.snapgames.game.singleclassgame.Game.CollisionResponseProcessor#onCollide(
	 * fr.snapgames.game.singleclassgame.Game.GameObject,
	 * fr.snapgames.game.singleclassgame.Game.GameObject)
	 */
	@Override
	public void onCollide(Game game, GameObject o1, GameObject o2) {
		if (o1.name.startsWith("player")) {
			if (o2.lifeDuration > 0) {
				o2.lifeDuration = 0;
			}
			Vector2D vR = o2.velocity.add(o1.velocity);
			vR.multiply(-1 * o2.elasticity * o2.friction * o1.elasticity * o1.friction);

			//o2.velocity = vR;
			o2.acceleration.multiply(-1 * o2.elasticity * o2.friction * o1.elasticity * o1.friction);

//			computePosition(o1, o2);
//			o1.velocity.x = 0;
//			o1.velocity.y = 0;
//			o1.acceleration.x = 0;
//			o1.acceleration.y = 0;

			game.soundControl.play("boing");

		}
	}

	/***
	 * Compute the new position of object o2 according to which side of the o1
	 * object is colliding.
	 * 
	 * @param o1 the GameObject colliding with o2
	 * @param o2 the GameObject to be moved, according to the a1 colliding sides.
	 */
	private void computePosition(GameObject o1, GameObject o2) {
		boolean left, right, top, bottom;
		if (!o2.collidingResponseProcessed) {
			Vector2D o1offset = o1.position;
			o1offset.x += o1.width / 2;
			o1offset.y += o1.height / 2;

			Vector2D o2offset = o2.position;
			o2offset.x += o2.width / 2;
			o2offset.y += o2.height / 2;

			left = o2offset.x < o1offset.x;
			right = o2offset.x > o1offset.x;
			top = o2offset.y < o1offset.y;
			bottom = o2offset.y > o1offset.y;

			/**
			 * Move on horizontal axis.
			 */
			if (left) {
				o2.position.x = o1.position.y - (o2.width / 2);
			} else if (right) {
				o2.position.x = o1.position.y + (o1.width / 2);
			}
			/**
			 * Move on vertical axis
			 */
			if (top) {
				o2.position.y = o1.position.y + (o2.height / 2);
			} else if (bottom) {
				o2.position.y = o1.position.y - (o1.height / 2);
			}
			o2.setCollidingResponseProcessed(true);
		}

	}

}