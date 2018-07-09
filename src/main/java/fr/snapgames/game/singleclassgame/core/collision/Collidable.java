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

/**
 * <p>
 * Interface to managed Collision with CollisionManager and QuadTree.
 * <p>
 * An object implementing the Collidable interface will be managed by the
 * CollisionManager to detect any collision with other object of the scene.
 * <p>
 * All the objects in the scene are dispatch into a Quadtree to optimze
 * Collision Detection process.
 * 
 * @author Frédéric Delorme
 * 
 * @see QuadTree
 * @see CollisionManager
 */
public interface Collidable {
	/**
	 * return the bounding box of the Collidable object.
	 * 
	 * @return a BoundingBox object.
	 */
	BoundingBox getBoundingBox();

	/**
	 * Add a <code>collider</code>. When a collision is detected, the colliding
	 * object is adding to this <code>collidable</code>.
	 * 
	 * @param co the colliding object to be added to the list of already colliding
	 *           objects.
	 */
	void addCollider(Collidable co);

	void setCollidingResponseProcessed(boolean f);
}