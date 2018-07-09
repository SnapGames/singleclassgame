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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.snapgames.game.singleclassgame.Game;

/**
 * The factory to create any object for this sample game.
 * <p>
 * - Factory#createGameObject(String name) create a GameObject, -
 * Factory#createCamera(String name), create a Camera (sic).
 *
 * @author Frédéric Delorme
 */
public class ObjectFactory {

	private static final Logger logger = LoggerFactory.getLogger(ObjectFactory.class);

	/**
	 * Create a new GameObject.
	 *
	 * @param name the name of the new object.
	 * @return
	 */
	public GameObject createGameObject(Game game, String name) {
		return new GameObject(game, name);
	}

	/**
	 * Create a new Camera.
	 *
	 * @param name the name of the new camera.
	 * @return
	 */
	public Camera createCamera(Game game, String name) {
		return new Camera(game, name);
	}

	/**
	 * Generic dynamic factory to create any object inheriting from
	 * {@link GameObject} and having a <code>Constructor(String)</code>.
	 *
	 * @param clazz class, inheriting from {@link GameObject}, to be instantiated
	 * @param name  name for this object.
	 * @return a new instance of the request <code>clazz</code> object.
	 */
	public Object create(Game game, Class<? extends GameObject> clazz, String name) {
		Constructor<?> constructor;
		Object obj = null;
		try {
			constructor = clazz.getDeclaredConstructor(Game.class, String.class);
			obj = constructor.newInstance(game, name);
		} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException e) {
			logger.error("Unable to create object {} based on class {}", name, clazz, e);
		}
		return obj;
	}

}
