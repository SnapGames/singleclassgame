/**
 * SnapGames
 * 
 * Game Development Java
 * 
 * singleclassgame
 * 
 * @year 2018
 */
package fr.snapgames.game.singleclassgame.core.resources;

/**
 * the ResourceUnknownException class is thrown when a resource is not found.
 *
 * @author Frédéric Delorme <frederic.delorme@snapgames.fr>
 */
public class ResourceUnknownException extends Exception {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @param message
	 */
	public ResourceUnknownException(String message) {
		super(message);
	}

}