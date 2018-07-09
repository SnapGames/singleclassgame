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
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import fr.snapgames.game.singleclassgame.Game;
import fr.snapgames.game.singleclassgame.core.collision.BoundingBox;
import fr.snapgames.game.singleclassgame.core.collision.BoundingBoxType;
import fr.snapgames.game.singleclassgame.core.collision.Collidable;
import fr.snapgames.game.singleclassgame.core.math.Vector2D;

/**
 * <p>
 * The class {@link GameObject} depicts any object managed by the game. All the
 * displayed objects are <code>GameObject</code>.
 * <p>
 * This entity intends to propose all the metadata to compute object behavior
 * and graphical rendering, like:
 * <ul>
 * <li><code>position</code> current position of the object,
 * <li><code>velocity</code> the object's current velocity,
 * <li><code>acceleration</code> the object's current acceleration.
 * </ul>
 * <p>
 * Some attributes to compute more realistic physical things:
 * <ul>
 * <li><code>forces</code> used to compute physic behavior,
 * </ul>
 * <p>
 * But also some material characteristics as:
 * <ul>
 * <li><code>mass</code> the mass of the object,
 * <li><code>gravity</code> its affected gravity,
 * <li><code>friction</code> the friction factor is case of contact,
 * <li><code>elasticity</code> the elasticity characteristic of the object 's
 * material.
 * </ul>
 * <p>
 * And soon we will add real gravity force computation between objects.
 *
 * @author Frédéric Delorme<frederic.delorme@snapgames.fr>
 */
public class GameObject implements Collidable {

	public String name = "";

	/**
	 * Next generation GameObject ---- Start here ---->
	 */
	public Vector2D acceleration = new Vector2D("acceleration");
	public Vector2D velocity = new Vector2D("velocity");
	public Vector2D position = new Vector2D("position");

	public Vector2D offset = new Vector2D("offset");
	public Vector2D size = new Vector2D("size");

	public List<Vector2D> forces = new ArrayList<>();

	public float scale = 1.0f;

	public float width = 16.0f, height = 16.0f;

	public float moveFactor = 0.5f;

	public float mass = 0.89f;
	public float friction = 0.92f;
	public float elasticity = 0.60f;

	public int lifeDuration = 0;

	public BufferedImage image = null;

	public int priority = 0;

	public Color debugColor = Color.ORANGE;

	public Color color = Color.GREEN;

	public BoundingBox bBox;

	public List<Collidable> colliders = new CopyOnWriteArrayList<>();

	public boolean collidingResponseProcessed;

	public Game game;

	/**
	 * Create a new basic Object entity with a <code>name</code>.
	 *
	 * @param name the name of this object.
	 */
	public GameObject(Game game, String name) {
		this.game = game;
		this.name = name;
		bBox = new BoundingBox();
		bBox.setType(BoundingBoxType.RECTANGLE);
	}

	/**
	 * Create a new Object entity with a <code>name</code> and a position
	 * <code>(x,y)</code>.
	 *
	 * @param name the name of this object.
	 * @param x    the X position of this object.
	 * @param y    the Y position of this object.
	 */
	public GameObject(Game game, String name, float x, float y) {
		this(game, name);
		setPosition(x, y);
		bBox.update(this);
	}

	/**
	 * Update all physic according the <code>elapsed</code> time since previous
	 * call.
	 *
	 * @param elapsed time elapsed since previous call.
	 */
	public void updatePhysic(float dt) {

		float t = dt * 1f;
		// -- update the life of this object (in 1/60 sec.)
		lifeDuration--;

		// -- Update Physics (System)
		// if (forces != null && world != null) {
		forces.addAll(game.world.forces);
		for (Vector2D v : forces) {
			acceleration = acceleration.add(v);
		}
		// }
		acceleration = acceleration.multiply(friction).multiply(1.0f / mass).multiply(t);
		// compute velocity
		velocity.x += (acceleration.x * t * t);
		velocity.y += (acceleration.y * t * t);

		// -- update Position (System)
		position.x += 0.5f * (velocity.x * t);
		position.y += 0.5f * (velocity.y * t);

		if (Math.abs(velocity.x) < 0.01f) {
			velocity.x = 0.0f;
		}
		if (Math.abs(velocity.y) < 0.01f) {
			velocity.y = 0.0f;
		}
		bBox.update(this);

	}

	/**
	 * Render the object. Draw an <code>image</code> if this attribute is not null,
	 * or anyway, if <code>debug</code> level>1, draw a simple rectangle.
	 *
	 * @param g
	 */
	public void render(Graphics2D g) {
		// if image exists in object, draw image.
		if (image != null) {
			g.drawImage(image, (int) position.x, (int) position.y, (int) width, (int) height, null);
		}
		// if debug mode level >0, draw debug info

		if (game.debug > 1) {
			g.setColor(debugColor);
			g.drawRect((int) position.x, (int) position.y, (int) width, (int) height);
			g.drawString(String.format("n:%s", name), (int) (position.x + width + 4), (int) position.y + 10);
			if (game.debug > 2) {
				g.setColor(Color.CYAN);
				g.drawLine((int) position.x, (int) position.y, (int) (position.x + velocity.x),
						(int) (position.y + velocity.y));
				if (game.debug > 3) {
					g.setColor(Color.RED);
					g.drawOval((int) (position.x + offset.x), (int) (position.y + offset.y), 2, 2);
					g.setColor(Color.CYAN);
					g.drawLine((int) (position.x + (offset.x)), (int) (position.y + (offset.y)),
							(int) (position.x + (offset.x) + (velocity.x * 4)),
							(int) (position.y + (offset.y) + (velocity.y * 4)));
					g.drawString(String.format("v:(%4.2f,%4.2f)", velocity.x, velocity.y),
							(int) (position.x + width + 4), (int) position.y + 20);
					g.setColor(Color.GREEN);
					g.drawLine((int) (position.x + (offset.x)), (int) (position.y + (offset.y)),
							(int) (position.x + (offset.x) + (acceleration.x * 10)),
							(int) (position.y + (offset.y) + (acceleration.y * 10)));
					g.drawString(String.format("a:(%4.2f,%4.2f)", acceleration.x, acceleration.y),
							(int) (position.x + width + 4), (int) position.y + 30);
					g.drawString(String.format("ld:(%06d)", lifeDuration), (int) (position.x + width + 4),
							(int) position.y + 50);
				}
			}
		}
	}

	/**
	 * Set the acceleration for this object.
	 *
	 * @param ax X component for the acceleration
	 * @param ay Y component for the acceleration
	 */
	public GameObject setAcceleration(float ax, float ay) {
		this.acceleration.x = ax;
		this.acceleration.y = ay;
		return this;
	}

	/**
	 * Set the velocity for this object.
	 *
	 * @param dx
	 * @param dy
	 */
	public GameObject setVelocity(float dx, float dy) {
		this.velocity.x = dx;
		this.velocity.y = dy;
		return this;
	}

	/**
	 * Set position for this object.
	 *
	 * @param x
	 * @param y
	 */
	public GameObject setPosition(float x, float y) {
		this.position.x = x;
		this.position.y = y;
		bBox.update(this);
		return this;
	}

	/**
	 * Set the object's scale.
	 *
	 * @param scale
	 */
	public GameObject setScale(float scale) {
		this.scale = scale;
		bBox.update(this);
		return this;
	}

	/**
	 * Set the object Mass.
	 *
	 * @param factor
	 */
	public GameObject setMass(float factor) {
		this.mass = factor;
		return this;
	}

	/**
	 * Set the object friction factor.
	 *
	 * @param factor
	 */
	public GameObject setFriction(float factor) {
		this.friction = factor;
		return this;
	}

	/**
	 * Set the elasticity factor for the object.
	 *
	 * @param factor
	 */
	public GameObject setElasticity(float factor) {
		this.elasticity = factor;
		return this;
	}

	/**
	 * Set the default debug information rendering color.
	 *
	 * @param debugColor
	 */
	public GameObject setDebugColor(Color debugColor) {
		this.debugColor = debugColor;
		return this;
	}

	/**
	 * Set the rendering order priority for this object.
	 *
	 * @param priority
	 */
	public GameObject setPriority(int priority) {
		this.priority = priority;
		return this;
	}

	/**
	 * Set the image to be rendered for this object.
	 *
	 * @param image
	 */
	public GameObject setImage(BufferedImage image) {
		this.image = image;
		this.width = image.getWidth();
		this.height = image.getHeight();
		bBox.update(this);
		return this;
	}

	/**
	 * Set the move factor for the player to <code>factor</code>.
	 *
	 * @param f
	 */
	public GameObject setMoveFactor(float factor) {
		this.moveFactor = factor;
		return this;

	}

	/**
	 * Set the offset factor to set object real center.
	 *
	 * @param offsetX
	 * @param offsetY
	 */
	public GameObject setOffset(float offsetX, float offsetY) {
		if (offset == null) {
			offset = new Vector2D(this.name, offsetX, offsetY);
		}
		this.offset.x = offsetX;
		this.offset.y = offsetY;
		return this;
	}

	/**
	 * Set Object size by setting width and height.
	 *
	 * @param width  the width of the object
	 * @param height the height of the object.
	 */
	public GameObject setSize(float width, float height) {
		this.width = (int) width;
		this.height = (int) height;
		bBox.update(this);
		return this;
	}

	/**
	 * Set the life duration for this object.
	 * 
	 * @param ld
	 * @return
	 */
	public GameObject setLifeDuration(int ld) {
		this.lifeDuration = ld;
		return this;
	}

	public GameObject offsetAtCenter() {
		offset.x = this.width / 2;
		offset.y = this.height / 2;
		return this;
	}

	@Override
	public BoundingBox getBoundingBox() {
		return bBox;
	}

	@Override
	public void addCollider(Collidable c) {
		colliders.add(c);
		// this.velocity.multiply(-this.elasticity*this.friction);
	}

	@Override
	public void setCollidingResponseProcessed(boolean f) {
		collidingResponseProcessed = f;

	}
}
