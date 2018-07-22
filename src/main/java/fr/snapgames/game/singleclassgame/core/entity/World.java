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

import java.util.ArrayList;
import java.util.List;

import fr.snapgames.game.singleclassgame.core.math.Vector2D;

/**
 * @author Frédéric Delorme<frederic.delorme@snapgames.fr>
 */
public class World {

	/**
	 * the default gravity value for this world (can be overridden at world
	 * construction).
	 */
	public Vector2D gravity = new Vector2D("gravity", 0.0f, -0.981f);

	/**
	 * List of forces to be applied to all objects. (would be changed in a next
	 * version to introduce space gravity and attraction factor).
	 */
	public List<Vector2D> forces = new ArrayList<>();

	/**
	 * List of available camera in this world.
	 */
	public List<Camera> cameras = new ArrayList<>();
	/**
	 * the current active camera.
	 */
	public Camera activeCam = null;

	/**
	 * Default world constructor.
	 */
	public World() {
		super();
	}

	/**
	 * Initialize the world with a gravity.
	 *
	 * @param gravity
	 */
	public World(Vector2D gravity) {
		this();
		this.gravity = gravity;
		forces.add(gravity.multiply(-1.0f));
	}

	/**
	 * Add a camera to the world. if no default camera is set, add this as the
	 * default one.
	 *
	 * @param cam
	 * @return
	 */
	public World addCamera(Camera cam) {
		this.cameras.add(cam);
		if (activeCam == null) {
			this.activeCam = cam;
		}
		return this;
	}

	/**
	 * Set the active camera for this world.
	 *
	 * @param cam
	 * @return
	 */
	World setActiveCamera(Camera cam) {
		if (!cameras.contains(cam)) {
			cameras.add(cam);
		}
		activeCam = cam;
		return this;
	}

	/**
	 * add a new force to this world.
	 *
	 * @param force
	 * @return
	 */
	public World addForce(Vector2D force) {
		this.forces.add(force);
		return this;
	}

}
