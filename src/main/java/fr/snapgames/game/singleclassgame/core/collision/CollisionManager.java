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

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.snapgames.game.singleclassgame.Game;
import fr.snapgames.game.singleclassgame.core.entity.GameObject;

/**
 * THe Collision Manager help game to detect and manage collision between
 * <code>Collidable</code> object.
 * 
 * @author Frédéric Delorme
 * @see Collidable
 */
public class CollisionManager {

	private static final Logger logger = LoggerFactory.getLogger(CollisionManager.class);

	private List<Collidable> colliders = new ArrayList<>();

	private List<CollisionResponseProcessor> processors = new ArrayList<>();

	/**
	 * The QuadTree to manage objects collision and visibility.
	 */
	private QuadTree quadTree;

	public CollisionManager() {
	}

	/**
	 * Set Colliding System dimension.
	 * 
	 * @param dim
	 */
	public void setDimension(Dimension dim) {
		quadTree = new QuadTree(dim.width, dim.height);
		quadTree.MAX_LEVELS = 16;
		quadTree.MAX_OBJECTS = 2;
	}

	/**
	 * @param game
	 * @param dt
	 */
	public void cullingProcess(Game game, float dt) {
		quadTree.clear();
		for (Collidable e : colliders) {
			quadTree.insert(e);
		}
	}

	/**
	 * Add an object to the Scene for the game, to be sorted and collision tested.
	 * 
	 * @param o The Game object to be added to the scene.
	 */
	public void add(GameObject o) {
		colliders.add((Collidable) o);
		logger.debug("Add {} to CollisionSystem", o.name);
	}

	/**
	 * Remove the object o from the scene.
	 * 
	 * @param o The Game object to be removed from the scene.
	 */
	public void remove(GameObject o) {
		colliders.remove(o);

		logger.debug("Remove {} from CollisionSystem", o.name);
	}

	/**
	 * Another Remove function , searching by the <code>name</code> to be removed by
	 * its name from the <code>colliders</code> list.
	 * 
	 * @param name the name of the Gameobject ({@link Collidable} implementaiton) to
	 *             be removed from the scene.
	 */
	public void remove(String name) {
		List<Collidable> toBeRemoved = new ArrayList<>();
		for (Collidable c : colliders) {
			GameObject e = (GameObject) c;
			if (e.name.equals(name)) {
				toBeRemoved.add((Collidable) e);

				logger.debug("object {} marked as remove into CollisionSystem", e.name);
			}
		}
		colliders.removeAll(toBeRemoved);
	}

	/**
	 * Manage collision from Player to other objects.
	 */
	public void update(Game game, float dt) {
		cullingProcess(game, dt);

		List<Collidable> collisionList = new CopyOnWriteArrayList<>();
		GameObject o = game.objects.get(0);
		
		quadTree.retrieve(collisionList, (Collidable) o);
		if (collisionList != null && !collisionList.isEmpty()) {
			for (Collidable s : collisionList) {
				GameObject ago = (GameObject) s;
				if (!o.name.equals(ago.name) && ago.bBox.intersect(o.bBox) == 1) {
					o.addCollider((Collidable) ago);
					ago.addCollider((Collidable) o);
					if (processors != null && !processors.isEmpty()) {
						for (CollisionResponseProcessor crp : processors) {
							crp.onCollide(game, o, ago);
						}
					}
					logger.info("object {} collide object {}", o.name, ago.name);
				}
			}
		}

	}

	public void draw(Game game, Graphics2D g, float fps) {
		quadTree.draw(g);
	}

	/**
	 * Add a collision response processor to the processors list.
	 * 
	 * @param crp the CollisionResponseProcessor implementation to be added to the
	 *            called stack.
	 */
	public void registerProcessor(CollisionResponseProcessor crp) {
		if (crp != null) {
			processors.add(crp);
		} else {
			logger.error("Unable to add a null CollisionResponseProcessor");
		}
	}
}
