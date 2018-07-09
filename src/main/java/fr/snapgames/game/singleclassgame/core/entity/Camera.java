/**
 * SnapGames
 * 
 * Game Development Java
 * 
 * singleclassgame
 * 
 * @year 2018
 */
package fr.snapgames.game.singleclassgame.core.entity;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;

import fr.snapgames.game.singleclassgame.Game;

/**
 * The camera object defines how to manage the game view, by tracking an object.
 *
 * @author Frédéric Delorme
 */
public class Camera extends GameObject {

	private GameObject trackedObject = null;
	private float tween = 1.0f;
	private Dimension view = new Dimension(0, 0);
	public double angle = 0.0f;

	public Camera(Game game, String name) {
		super(game, name);
	}

	public Camera(Game game, String name, float x, float y) {
		super(game, name, x, y);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see fr.snapgames.game.singleclassgame.Game.GameObject#updatePhysic(float)
	 */
	@Override
	public void updatePhysic(float dt) {
		if (trackedObject != null) {
			this.position.x += (trackedObject.position.x - (view.width / 2) - this.position.x) * tween * dt;
			this.position.y += (trackedObject.position.y - (view.height / 2) - this.position.y) * tween * dt;
		} else {
			this.updatePhysic(dt);
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * fr.snapgames.game.singleclassgame.Game.GameObject#render(java.awt.Graphics2D)
	 */
	@Override
	public void render(Graphics2D g) {
		if (game.debug > 1) {
			g.setColor(Color.ORANGE);
			g.drawRect(16, 16, view.width - 32, view.height - 32);
			g.drawString(name, 16, 16);
		}
	}

	/**
	 * set the game view for camera position computation.
	 *
	 * @param view
	 * @return
	 */
	public Camera setView(Dimension view) {
		this.view = view;
		return this;
	}

	/**
	 * Add a target to the camera.
	 *
	 * @param target
	 * @return
	 */
	public Camera setTrackedObject(GameObject target) {
		this.trackedObject = target;
		return this;
	}

	/**
	 * Define the Tween tracker factor to <code>tween</code>.
	 *
	 * @param tween
	 * @return
	 */
	public Camera setTweenFactor(float tween) {
		this.tween = tween;
		return this;
	}

	/**
	 * Define the camera rotation angle.
	 *
	 * @param angle
	 * @return
	 */
	public Camera setRotation(float angle) {
		this.angle = angle;
		return this;
	}

}
