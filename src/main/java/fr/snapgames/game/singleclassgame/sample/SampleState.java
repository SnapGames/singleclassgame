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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.snapgames.game.singleclassgame.Game;
import fr.snapgames.game.singleclassgame.core.collision.BoundingBoxType;
import fr.snapgames.game.singleclassgame.core.entity.Camera;
import fr.snapgames.game.singleclassgame.core.entity.GameObject;
import fr.snapgames.game.singleclassgame.core.entity.ObjectFactory;
import fr.snapgames.game.singleclassgame.core.math.Vector2D;
import fr.snapgames.game.singleclassgame.core.resources.ResourceUnknownException;
import fr.snapgames.game.singleclassgame.core.state.AbstractGameState;
import fr.snapgames.game.singleclassgame.core.state.GameState;

/**
 * This implementation of a Game State is a sample demonstrating the pro's and
 * con's of the GameStateManager.
 * 
 * @author Frédéric Delorme
 *
 */
public class SampleState extends AbstractGameState implements GameState {

	private static final Logger logger = LoggerFactory.getLogger(SampleState.class);

	GameObject player;
	ObjectFactory factory;
	
	

	/**
	 * A flag to request a randomization of the enemies moves.
	 */
	public boolean randomizeEnemies;

	/**
	 * initialize this State.
	 */
	public SampleState(Game game) {
		super(game, "SampleState");
	}

	@Override
	public void initialize(Game game) {
		super.initialize(game);

		factory = new ObjectFactory();
		
		
		// register the collision response processor for our demo.
		game.collisionMgr.registerProcessor(new ColliderResponse());


		

		// read image resources
		game.resourceMgr.addResource("playerBall", "res/images/blue-bouncing-ball-64x64.png");
		game.resourceMgr.addResource("enemyBall", "res/images/red-bouncing-ball-64x64.png");

		// read Sounds
		game.soundControl.load("boing", "res/audio/sounds/boing.wav");

		// Add objects to world.
		// ---------------------------------------------------------------------
		// ---- Add the main player object
		try {
			player = ((GameObject) factory.create(game, GameObject.class, "player")).setPosition(50, 50)
					.setImage(game.resourceMgr.getImage("playerBall")).setMoveFactor(0.50f).setMass(100f)
					.setFriction(0.30f).setElasticity(0.32f).offsetAtCenter().setPriority(1).setDebugColor(Color.RED)
					.setLifeDuration(100000);
			player.bBox.type = BoundingBoxType.CIRCLE;
			game.add(player);
		} catch (ResourceUnknownException e) {
			System.err.println("Unable to retrieve the playerBall resource");
			System.exit(-1);
		}

		// add specific game player key listener
		game.kil.register(new PlayerKeyInput(player, game.kil));

		// ---- Add a bunch of enemies !
		for (int i = 0; i < 50; i++) {
			float posX = (float) (Math.random() * game.playZone.width);
			float posY = (float) (Math.random() * game.playZone.height);
			try {
				GameObject enemy = ((GameObject) factory.create(game, GameObject.class, "enemy_" + i))
						.setPosition(posX, posY).setImage(game.resourceMgr.getImage("enemyBall")).setSize(24.0f, 24.0f)
						.setAcceleration((float) Math.random() * 0.005f, (float) Math.random() * 0.005f)
						.setPriority(2 + i).setMass(50.0f).setFriction(0.95f).setElasticity(0.890f)
						.setOffset(12.0f, 12.0f).setLifeDuration(300);
				enemy.bBox.type = BoundingBoxType.CIRCLE;
				game.add(enemy);
			} catch (ResourceUnknownException e) {
				System.err.println("Unable to retrieve the enemyBall resource");
				System.exit(-1);
			}
		}

		// Other elements to the scene.
		// ---------------------------------------------------------------------
		// Add a Camera to the world.
		Camera cam = (Camera) factory.createCamera(game, "camera").setTrackedObject(player).setTweenFactor(0.22f)
				.setView(game.dim).setPosition(0.0f, 0.0f);

		game.world.addCamera(cam);

		// Add a piece of wind to our world !
		// world.addForce(new Vector2D("some-wind", 0.5f, 0.0f));

	}

	@Override
	public void activate(Game game) {
		super.activate(game);
	}

	@Override
	public void input(Game game) {
		super.input(game);

		// move up (with extra speed !)
		if (game.kil.getKey(KeyEvent.VK_UP)) {
			player.forces.add(new Vector2D("move-up", 0.0f, -player.moveFactor * 20.0f));
			logger.debug("player move up y+={}", -player.moveFactor);
		}
		// move down
		if (game.kil.getKey(KeyEvent.VK_DOWN)) {
			player.forces.add(new Vector2D("move-down", 0.0f, player.moveFactor));
			logger.debug("player move down y+={}", player.moveFactor);
		}
		// move left
		if (game.kil.getKey(KeyEvent.VK_LEFT)) {
			player.forces.add(new Vector2D("move-left", -player.moveFactor, 0.0f));
			logger.debug("player move left x+={}", player.moveFactor);
		}

		// move right
		if (game.kil.getKey(KeyEvent.VK_RIGHT)) {
			player.forces.add(new Vector2D("move-right", player.moveFactor, 0.0f));
			logger.debug("player move left x+={}", -player.moveFactor);
		}

		// stop any action !
		if (game.kil.getKey(KeyEvent.VK_SPACE)) {
			player.forces.clear();
			player.velocity.x = 0.0f;
			player.velocity.y = 0.0f;
			player.acceleration.x = 0.0f;
			player.acceleration.y = 0.0f;
		}

	}

	@Override
	public void update(Game game, float elapsed) {
		super.update(game, elapsed);

		if (randomizeEnemies) {
			randomizeEnemies(game);
			randomizeEnemies = false;
		}
		for (GameObject o : game.objects) {
			o.updatePhysic(elapsed);
			constrainsObjectToPlayZone(game.playZone, o);
		}

	}

	@Override
	public void render(Game game, Graphics2D g) {
		super.render(game, g);
		
		
		// if objects in the list, draw all those things
		if (game.objects != null && game.objects.size() > 0) {
			for (GameObject o : game.objects) {
				o.render(g);
			}
		}
		
	}

	@Override
	public void deactivate(Game game) {
		// TODO Auto-generated method stub
		super.deactivate(game);
	}

	

	/**
	 * Generate randomly enemies position.
	 */
	private void randomizeEnemies(Game game) {
		for (GameObject o : game.objects) {
			if (o.name.startsWith("enemy_")) {

				o.forces.clear();
				o.setVelocity(0, 0);
				o.velocity.x = 0;
				o.velocity.y = 0;
				o.setAcceleration((float) ((Math.random() * 50f) - 25f), (float) ((Math.random() * 50f) - 25f));
				// o.gravity = new Vector2D("gravity", 0.0f, -9.81f);
				logger.info("add a new acceleration to {}:{}", o.name, o.acceleration);
			}
		}

	}

	/**
	 * Constrained {@link GameObject} <code>object</code> to the Play Zone
	 * <code>constrainedZone</code>.
	 *
	 * @param constrainedZone the zone where to constrains game object.
	 * @param object          the object to be constrained to the play zone.
	 */
	private void constrainsObjectToPlayZone(Dimension constrainedZone, GameObject object) {

		if (object.position.x < 0) {
			object.position.x = 0;
			object.velocity.x *= -1 * object.elasticity;

		}
		if (object.position.y < 0) {
			object.position.y = 0;
			object.velocity.y *= -1 * object.elasticity;
		}
		if (object.position.x > constrainedZone.width - object.width) {
			object.position.x = constrainedZone.width - object.width;
			object.velocity.x *= -1 * object.elasticity;
		}
		if (object.position.y > constrainedZone.height - object.height) {
			object.position.y = constrainedZone.height - object.height;
			object.velocity.y *= -1 * object.elasticity;

		}
	}
	
}