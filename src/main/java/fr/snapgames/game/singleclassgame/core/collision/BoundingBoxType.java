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
 * Bounding Box type can have 3 values. * `NONE` No bounding box, * `RECTANGLE`
 * a simple rectangle as a bounding box, * `CIRCLE` a circle, * `CAPSULE` a
 * capsule composed of 2 circles and a distance between its axes, * `POINTS` a
 * list of points defining an object's frontier.
 * 
 * @author Frédéric Delorme
 */
public enum BoundingBoxType {
	NONE,
	/** No boundigbox */
	RECTANGLE, // a simple rectangle as a bounding box
	CIRCLE, // a circle
	CAPSULE, // a capsule composed of 2 circles and a distance between its axes.
	POINTS; // a list of points defining a perimeter.
}