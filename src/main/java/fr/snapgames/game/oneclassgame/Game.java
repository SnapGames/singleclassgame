/**
 * SnapGames
 *
 * @see http://snapgames.fr/
 * @year 2018
 */
package fr.snapgames.game.oneclassgame;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Developer;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * This small class is a tutorial on how to develop a simple game with Java.
 * <p>
 * Based on a basic Loop with update and render operations, this main class
 * manage a bunch of {@link GameObject} to be displayed.
 * <ul>
 * <li>The {@link Game#update(float)} is will update all the object according to
 * very a simplistic physic computation.
 * <li>the {@link Game#render()} will compute and draw to screen all those
 * objects.
 * </ul>
 * 
 * @author Frédéric Delorme<frederic.delorme@snapgames.fr>
 * 
 * @see http://github.com/SnapGames/singleclassgame
 *
 */
public class Game extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final Logger logger = LoggerFactory.getLogger(Game.class);

	/**
	 * Window and Rendering size
	 */
	private static float scale = 1.5f;

	/**
	 * Rendering pace
	 */
	private float FPS = 30.0f;
	private float fpsDelay = 1000.0f / FPS;
	/**
	 * Computation pace
	 */
	private float UPS = 60.0f;
	private float upsDelay = 1000.0f / UPS;

	// Windows Dimension (Scale factor applied)
	private Dimension dim;
	// Play zone dimension
	private Dimension playZone;

	/**
	 * Internal flag to request EXIT.
	 */
	private boolean exit = false;
	/**
	 * internal flag to request pause mode.
	 */
	private boolean pause = false;
	/**
	 * internal debug level to track things id <code>debug</code>>0.
	 */
	private int debug = 2;
	/**
	 * rendering buffer
	 */
	private BufferedImage buffer;

	/**
	 * Game objects to be managed.
	 */
	private List<GameObject> objects = new ArrayList<>();

	/**
	 * The main input key listener.
	 */
	KeyInputListener kil = null;

	/**
	 * THE player for this game.
	 */
	GameObject player;

	/**
	 * the main resource manager to try and share things.
	 */
	private ResourceManager resourceMgr;

	/**
	 * The world object contains physic constrains for the physic engine system.
	 */
	private World world;
	private Window window;

	/**
	 * This integrated class parse Maven model to expose a resulting list of
	 * dependencies.
	 * 
	 * @author Frédéric Delorme
	 *
	 */
	class VersionTracker {

		Model model = null;

		public VersionTracker() {
			try {
				MavenXpp3Reader reader = new MavenXpp3Reader();
				if ((new File("pom.xml")).exists())
					model = reader.read(new FileReader("pom.xml"));

				else
					model = reader.read(new InputStreamReader(Game.class.getResourceAsStream(
							"/META-INF/maven/de.scrum-master.stackoverflow/aspectj-introduce-method/pom.xml")));
			} catch (IOException | XmlPullParserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		void extractProjectInformation() {
			List<Dependency> deps = getDependencyList();
			logger.info("project name: " + getName());
			logger.info("project description: " + getDescription());
			logger.info("project version: " + getVersion());
			logger.info("dependency list:");
			int i = 0;
			for (Dependency dep : deps) {
				logger.info((i++) + " - " + dep.getType() + "://" + dep.getGroupId() + ":" + dep.getArtifactId() + ":"
						+ dep.getVersion());
			}
		}

		/**
		 * Parse pom.xml file to extract Maven dependencies for the project.
		 * 
		 * @return List of corresponding dependencies.
		 * @see https://stackoverflow.com/questions/3697449/retrieve-version-from-maven-pom-xml-in-code
		 */
		List<Dependency> getDependencyList() {
			return (model != null ? model.getDependencies() : new ArrayList<Dependency>());
		}

		/**
		 * Retrive list of developers
		 * 
		 * @return
		 */
		List<Developer> getDevelopers() {
			return (model != null ? model.getDevelopers() : new ArrayList<Developer>());
		}

		/**
		 * Retrieve description
		 * 
		 * @return
		 */
		String getDescription() {
			return (model != null ? model.getDescription() : "");
		}

		/**
		 * Retrieve project name.
		 * 
		 * @return
		 */
		String getName() {
			return (model != null ? model.getName() : "");
		}

		/**
		 * Retrieve project version.
		 * 
		 * @return
		 */
		String getVersion() {
			return (model != null ? model.getVersion() : "");
		}

		/**
		 * Retrieve inception year
		 * 
		 * @return
		 */
		String getInceptionYear() {
			return (model != null ? model.getInceptionYear() : "");
		}

	}

	/**
	 * @author Frédéric Delorme<frederic.delorme@snapgames.fr>
	 *
	 */
	public class Window {
		/**
		 * rendering frame.
		 */
		private JFrame frame;

		/**
		 * CReate a window containing the Game with a title.
		 * 
		 * @param game
		 *            the game to display in the window.
		 * @param title
		 *            the title of the window.
		 */
		Window(Game game, String title) {
			frame = new JFrame(title);

			frame.setMaximumSize(dim);
			frame.setMinimumSize(dim);
			frame.setPreferredSize(dim);
			frame.setResizable(false);

			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.addKeyListener(kil);

			frame.setContentPane(game);

		}

		/**
		 * Show the window.
		 */
		public void show() {
			frame.setVisible(true);
		}

		/**
		 * Hide the window.
		 */
		public void hide() {
			frame.setVisible(false);
		}

		/**
		 * add a key listener to the window.
		 * 
		 * @param kil
		 */
		public void setKeyInputListener(KeyInputListener kil) {
			frame.addKeyListener(kil);
		}

		/**
		 * Set the window title.
		 * 
		 * @param title
		 *            the new window title.
		 */
		public void setTitle(String title) {
			frame.setTitle(title);
		}

		/**
		 * retrieve the Graphics API for this window.
		 * 
		 * @return
		 */
		public Graphics2D getGraphics() {
			return (Graphics2D) frame.getGraphics();
		}
	}

	/**
	 * @author Frédéric Delorme<frederic.delorme@snapgames.fr>
	 *
	 */
	public class World {

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

		Vector2D gravity = new Vector2D(0.0f, -0.981f);
		List<Vector2D> forces = new ArrayList<>();
	}

	/**
	 * the ResourceUnknownException class is thrown when a resource is not found.
	 * 
	 * @author Frédéric Delorme <frederic.delorme@snapgames.fr>
	 *
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

	/**
	 * the ResourceManager class intends to load and cache some objects like image,
	 * sounds, font, etc... any resources.
	 * 
	 * @author Frédéric Delorme <frederic.delorme@snapgames.fr>
	 *
	 */
	public class ResourceManager {
		private Map<String, Object> objects = new HashMap<>();

		/**
		 * Add a resource to the set.
		 * 
		 * @param name
		 *            name for this resource
		 * @param path
		 *            path to the rsource.
		 */
		public void addResource(String name, String path) {
			// Manage image (PNG or JPG)
			if (path.toLowerCase().endsWith(".png") || path.toLowerCase().endsWith(".png")) {
				try {
					BufferedImage image = ImageIO.read(this.getClass().getResourceAsStream("/" + path));
					objects.put(name, image);
				} catch (Exception e) {
					System.err.println(String.format("Unable to find %s and store resource as %s.", path, name));
					System.exit(-1);
				}
			}
		}

		/**
		 * retrieve an image from the resource set.
		 * 
		 * @param name
		 *            the name of the resource to retrieve.
		 * @return the BufferedImage extracted from the resource set.
		 * @throws ResourceUnknownException
		 */
		public BufferedImage getImage(String name) throws ResourceUnknownException {
			if (objects.containsKey(name)) {
				return (BufferedImage) objects.get(name);
			} else {
				throw new ResourceUnknownException(String.format("Unknown resource named %s", name));
			}
		}
	}

	/**
	 * <p>
	 * Main {@link KeyInputListener} at GameInstance level. It will manage multiple
	 * Key Listeners on the window.
	 * <p>
	 * You can :
	 * <ul>
	 * <li>add a `KeyListener` with {@link KeyInputListener#register(KeyListener)},
	 * <li>and remove at anytime a keyListener with
	 * {@link KeyInputListener#remove(KeyListener)}
	 * </ul>
	 * 
	 * @author Frédéric Delorme<frederic.delorme@snapgames.fr>
	 * 
	 */
	class KeyInputListener implements KeyListener {
		/**
		 * current state of the key
		 */
		boolean[] keys = new boolean[65235];
		/**
		 * Previous state of the key.
		 */
		boolean[] previous = new boolean[65235];

		/**
		 * List of KeyListener to be called on key events.
		 */
		List<KeyListener> objectsCallBack = new ArrayList<>();

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
		 */
		@Override
		public void keyPressed(KeyEvent e) {
			previous[e.getKeyCode()] = keys[e.getKeyCode()];
			keys[e.getKeyCode()] = true;
			for (KeyListener kcb : objectsCallBack) {
				kcb.keyPressed(e);
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
		 */
		@Override
		public void keyReleased(KeyEvent e) {
			previous[e.getKeyCode()] = keys[e.getKeyCode()];
			keys[e.getKeyCode()] = false;
			for (KeyListener kcb : objectsCallBack) {
				kcb.keyReleased(e);
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
		 */
		@Override
		public void keyTyped(KeyEvent e) {
			for (KeyListener kcb : objectsCallBack) {
				kcb.keyPressed(e);
			}

		}

		/**
		 * This method will register a new key manager for a specific need.
		 * 
		 * @param kcb
		 */
		public void register(KeyListener kcb) {
			this.objectsCallBack.add(kcb);
		}

		/**
		 * This is to remove a key listener from the system.
		 * 
		 * @param kcb
		 */
		public void remove(KeyListener kcb) {
			this.objectsCallBack.remove(kcb);
		}

	}

	/**
	 * A 2D Vector class to compute next gen things..
	 * 
	 * @author Frédéric Delorme<frederic.delorme@snapgames.fr>
	 *
	 */
	class Vector2D {
		/**
		 * X axe factor.
		 */
		public float x = 0.0f;
		/**
		 * Y axe factor.
		 */
		public float y = 0.0f;

		/**
		 * Create a Vector2D
		 */
		Vector2D() {
			this.x = 0.0f;
			this.y = 0.0f;
		}

		/**
		 * Set the default gravity.
		 * 
		 * @param x
		 * @param y
		 */
		Vector2D(float x, float y) {
			this.x = x;
			this.y = y;
		}

		/**
		 * add the v vector.
		 * 
		 * @param v
		 */
		Vector2D add(Vector2D v) {
			this.x += v.x;
			this.y += v.y;
			return this;
		}

		/**
		 * substract the v vector.
		 * 
		 * @param v
		 */
		public Vector2D sub(Vector2D v) {
			return new Vector2D(x - v.x, y - v.y);
		}

		/**
		 * multiply the vector with f.
		 * 
		 * @param f
		 */
		public Vector2D multiply(float f) {
			this.x *= f;
			this.y *= f;
			return this;
		}

		/**
		 * Compute distance between this vector and the vector <code>v</code>.
		 * 
		 * @param v
		 *            the vector to compute distance with.
		 * @return
		 */
		public float distance(Vector2D v) {
			float v0 = x - v.x;
			float v1 = y - v.y;
			return (float) Math.sqrt(v0 * v0 + v1 * v1);
		}

		/**
		 * Normalization of this vector.
		 */
		public Vector2D normalize() {
			// sets length to 1
			//
			double length = Math.sqrt(x * x + y * y);

			if (length != 0.0) {
				float s = 1.0f / (float) length;
				x = x * s;
				y = y * s;
			}

			return new Vector2D(x, y);
		}

		/**
		 * Dot product for current instance {@link Vector2D} and the <code>v1</code>
		 * vector.
		 * 
		 * @param v1
		 * @return
		 */
		public double dot(Vector2D v1) {
			return this.x * v1.x + this.y * v1.y;
		}

	}

	/**
	 * <p>
	 * The class {@link GameObject} depicts any object managed by the game. All the
	 * displayed objects are <code>GameObject</code>.
	 * <p>
	 * This entity intends to propose all the metadata to compute object behavior
	 * and graphical rendering, like
	 * <ul>
	 * <li><code>position</code>,
	 * <li><code>velocity</code>,
	 * <li><code>acceleration</code>,
	 * </ul>
	 * <p>
	 * but also some physic information as
	 * <ul>
	 * <li><code>mass</code>,
	 * <li><code>gravity</code>,
	 * <li><code>material</code>,
	 * <li><code>friction</code>,
	 * <li><code>elasticity</code>.
	 * </ul>
	 * 
	 * @author Frédéric Delorme<frederic.delorme@snapgames.fr>
	 *
	 */
	class GameObject {

		String name = "";

		/**
		 * Next generation GameObject ---- Start here ---->
		 */
		Vector2D acceleration = new Vector2D();
		Vector2D velocity = new Vector2D();
		Vector2D position = new Vector2D();

		Vector2D offset = new Vector2D();
		Vector2D size = new Vector2D();

		List<Vector2D> forces = new ArrayList<>();
		Vector2D gravity = new Vector2D(0.0f, 0.981f);

		float scale = 1.0f;

		float width = 16.0f, height = 16.0f;

		float moveFactor = 0.5f;

		float mass = 0.89f;
		float friction = 0.92f;
		float elasticity = 0.60f;

		BufferedImage image = null;

		int priority = 0;

		Color debugColor = Color.ORANGE;

		Color color = Color.GREEN;

		/**
		 * Create a new Object entity with a <code>name</code> and a position
		 * <code>(x,y)</code>.
		 * 
		 * @param name
		 *            the name of this object.
		 * @param x
		 *            the X position of this object.
		 * @param y
		 *            the Y position of this object.
		 */
		GameObject(String name, float x, float y) {
			this.name = name;
			setPosition(x, y);
		}

		/**
		 * Update all physic according the <code>elapsed</code> time since previous
		 * call.
		 * 
		 * @param elapsed
		 *            time elapsed since previous call.
		 */
		public void updatePhysic(float dt) {

			float t = dt * 1f;

			// -- Update Physics (System)
			forces.addAll(world.forces);
			for (Vector2D v : forces) {
				acceleration = acceleration.add(v);
			}
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
			if (debug > 1) {
				g.setColor(debugColor);
				g.drawRect((int) position.x, (int) position.y, (int) width, (int) height);
				g.drawString(String.format("n:%s", name), (int) (position.x + width + 4), (int) position.y + 10);
				if (debug > 2) {
					g.setColor(Color.CYAN);
					g.drawLine((int) position.x, (int) position.y, (int) (position.x + velocity.x),
							(int) (position.y + velocity.y));
					if (debug > 3) {
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
								(int) (position.x + (offset.x) + (acceleration.x * 4)),
								(int) (position.y + (offset.y) + (acceleration.y * 4)));
						g.drawString(String.format("a:(%4.2f,%4.2f)", acceleration.x, acceleration.y),
								(int) (position.x + width + 4), (int) position.y + 30);
					}
				}
			}
		}

		/**
		 * Set the acceleration for this object.
		 * 
		 * @param ax
		 * @param ay
		 */
		public void setAcceleration(float ax, float ay) {
			this.acceleration.x = ax;
			this.acceleration.y = ay;
		}

		/**
		 * Set the velocity for this object.
		 * 
		 * @param dx
		 * @param dy
		 */
		public void setVelocity(float dx, float dy) {
			this.velocity.x = dx;
			this.velocity.y = dy;
		}

		/**
		 * Set position for this object.
		 * 
		 * @param x
		 * @param y
		 */
		public void setPosition(float x, float y) {
			this.position.x = x;
			this.position.y = y;
		}

		/**
		 * Set the object's scale.
		 * 
		 * @param scale
		 */
		public void setScale(float scale) {
			this.scale = scale;
		}

		/**
		 * Set the object Mass.
		 * 
		 * @param factor
		 */
		public void setMass(float factor) {
			this.mass = factor;
		}

		/**
		 * Set the object friction factor.
		 * 
		 * @param factor
		 */
		public void setFriction(float factor) {
			this.friction = factor;
		}

		/**
		 * Set the elasticity factor for the object.
		 * 
		 * @param factor
		 */
		public void setElasticity(float factor) {
			this.elasticity = factor;
		}

		/**
		 * Set the default debug information rendering color.
		 * 
		 * @param debugColor
		 */
		public void setDebugColor(Color debugColor) {
			this.debugColor = debugColor;
		}

		/**
		 * Set the rendering order priority for this object.
		 * 
		 * @param priority
		 */
		public void setPriority(int priority) {
			this.priority = priority;
		}

		/**
		 * Set the image to be rendered for this object.
		 * 
		 * @param image
		 */
		public void setImage(BufferedImage image) {
			this.image = image;
			this.width = image.getWidth();
			this.height = image.getHeight();
		}

		/**
		 * Set the move factor for the player to <code>factor</code>.
		 * 
		 * @param f
		 */
		public void setMoveFactor(float factor) {
			this.moveFactor = factor;

		}

		/**
		 * Set the offset factor to set object real center.
		 * 
		 * @param offsetX
		 * @param offsetY
		 */
		public void setOffset(float offsetX, float offsetY) {
			this.offset.x = offsetX;
			this.offset.y = offsetY;
		}

		/**
		 * Set Object size by setting width and height.
		 * 
		 * @param width
		 *            the width of the object
		 * @param height
		 *            the height of the object.
		 */
		public void setSize(float width, float height) {
			this.width = (int) width;
			this.height = (int) height;
		}

	}

	/**
	 * The {@link GameKeyInput} is a key input handler to manage the Game Level key
	 * listener.
	 * <ul>
	 * <li><key>ESCAPE</key> to quit the game,</li>
	 * <li><key>PAUSE</key> to set game in the pause state.</li>
	 * </ul>
	 * 
	 * @author Frédéric Delorme<frederic.delorme@snapgames.fr>
	 *
	 */
	class GameKeyInput implements KeyListener {
		/*
		 * (non-Javadoc)
		 * 
		 * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
		 */
		@Override
		public void keyPressed(KeyEvent e) {
			// Nothing to do here.
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
		 */
		@Override
		public void keyReleased(KeyEvent e) {
			switch (e.getKeyCode()) {
			case KeyEvent.VK_ESCAPE:
			case KeyEvent.VK_Q:
				exit = true;
				break;
			case KeyEvent.VK_PAUSE:
			case KeyEvent.VK_P:
				pause = !pause;
				System.out.println(String.format("Pause mode set to %s", (pause ? "ON" : "OFF")));
				break;
			case KeyEvent.VK_F3:
			case KeyEvent.VK_D:
				debug = Math.floorMod(debug + 1, 5);
				System.out.println(String.format("Debug level set to %d", debug));
				break;
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
		 */
		@Override
		public void keyTyped(KeyEvent arg0) {
			// Nothing to do here.

		}
	}

	/**
	 * <p>
	 * A specific Key Listener for the Player object.
	 * <p>
	 * This key listener manage the directional keys:
	 * <ul>
	 * <li><code>UP</code> to move up,
	 * <li><code>DOWN</code> to move down,
	 * <li><code>LEFT</code> to move left,
	 * <li><code>RIGHT</code> to move right,
	 * <li><code>SPACE</code> to stop all.
	 * </ul>
	 * 
	 * @author Frédéric Delorme<frederic.delorme@snapgames.fr>
	 *
	 */
	class PlayerKeyInput implements KeyListener {

		GameObject player = null;
		KeyInputListener kil = null;
		boolean move = false;

		public PlayerKeyInput(GameObject o, KeyInputListener kil) {
			player = o;
			this.kil = kil;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
		 */
		@Override
		public void keyPressed(KeyEvent e) {
			if (kil.keys[KeyEvent.VK_UP]) {
				move = true;
				player.forces.add(new Vector2D(0.0f, -player.moveFactor));
			}
			if (kil.keys[KeyEvent.VK_DOWN]) {
				move = true;
				player.forces.add(new Vector2D(0.0f, player.moveFactor));
			}
			if (kil.keys[KeyEvent.VK_LEFT]) {
				move = true;
				player.forces.add(new Vector2D(-player.moveFactor, 0.0f));
			}

			if (kil.keys[KeyEvent.VK_RIGHT]) {
				move = true;
				player.forces.add(new Vector2D(player.moveFactor, 0.0f));
			}

			if (kil.keys[KeyEvent.VK_SPACE]) {
				move = true;
				player.acceleration.x = 0.0f;
				player.acceleration.y = 0.0f;
			}

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
		 */
		@Override
		public void keyReleased(KeyEvent e) {
			move = false;

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
		 */
		@Override
		public void keyTyped(KeyEvent e) {
			// Nothing to do here.

		}

	}

	static class Configuration {

		private static final Logger logger = LoggerFactory.getLogger(Configuration.class);

		private static final String UNKNOWN_CONFIG_KEY = "UNKNOWN_CONFIG_KEY";
		public static Configuration instance = new Configuration();
		Properties props;

		private Configuration() {
			props = new Properties();
			load();
		}

		/**
		 * 
		 */
		private void load() {
			try {
				if (props == null) {
					props = new Properties();
				}
				props.load(Game.class.getResourceAsStream("/res/configuration.properties"));
				for (Entry<Object, Object> prop : props.entrySet()) {
					logger.info(String.format("config %s : %s", prop.getKey(), prop.getValue()));
				}

			} catch (IOException e) {
				System.err.println("Unable to read configuration file");
				System.exit(-1);
			}
		}

		/**
		 * request a configuration reload from file.
		 */
		public static void reload() {
			Configuration.instance.load();
		}

		/**
		 * Save configuration to configuration.properties file.
		 */
		private void store() {

			try {
				File f = new File(Game.class.getResource("/").getPath() + "/configuration.properties");
				OutputStream out = new FileOutputStream(f);
				props.store(out, "Update configuration");
			} catch (IOException e) {
				System.err.println("Unable to store configuration file");
				System.exit(-1);
			}
		}

		/**
		 * request a configuration reload from file.
		 */
		public static void save() {
			Configuration.instance.store();
		}

		/**
		 * retrieve a value from configuraiton.properties file.
		 * 
		 * @param key
		 *            key configuration to be retrieved.
		 * 
		 * @return String value
		 */
		private String getConfig(String key) {
			if (props.containsKey(key)) {
				return props.getProperty(key);
			} else {
				return UNKNOWN_CONFIG_KEY;
			}
		}

		/**
		 * retrieve a <code>int</code> value for configuration <code>key</code>.
		 * 
		 * @param key
		 * @return
		 */
		public static int getInteger(String key, int defaultValue) {
			String value = Configuration.instance.getConfig(key);
			if (value.equals(UNKNOWN_CONFIG_KEY)) {
				return defaultValue;
			}
			return Integer.parseInt(value);
		}

		/**
		 * retrieve a <code>float</code> value for configuration <code>key</code>.
		 * 
		 * @param key
		 * @return
		 */
		public static float getFloat(String key, float defaultValue) {
			String value = Configuration.instance.getConfig(key);
			if (value.equals(UNKNOWN_CONFIG_KEY)) {
				return defaultValue;
			}
			return Float.parseFloat(value);
		}

		/**
		 * retrieve a <code>boolean</code> value for configuration <code>key</code>.
		 * 
		 * @param key
		 * @return
		 */
		public static boolean getBoolean(String key, boolean defaultValue) {
			String value = Configuration.instance.getConfig(key);
			if (value.equals(UNKNOWN_CONFIG_KEY)) {
				return defaultValue;
			}
			return Boolean.parseBoolean(value);
		}

		/**
		 * retrieve a <code>String</code> value for configuration <code>key</code>.
		 * 
		 * @param key
		 * @return
		 */
		public static String get(String key, String defaultValue) {
			String value = Configuration.instance.getConfig(key);
			if (value.equals(UNKNOWN_CONFIG_KEY)) {
				return defaultValue;
			}
			return value;
		}

		private void setPropertyInt(String key, int value) {
			props.setProperty(key, "" + value);

		}

		private void setPropertyFloat(String key, float value) {
			props.setProperty(key, "" + value + "f");

		}

		private void setPropertyBoolean(String key, boolean value) {
			props.setProperty(key, "" + value);

		}

		private void setPropertyString(String key, String value) {
			props.setProperty(key, value);

		}

		public static void setInteger(String key, int value) {
			Configuration.instance.setPropertyInt(key, value);
		}

		public static void setFloat(String key, float value) {
			Configuration.instance.setPropertyFloat(key, value);
		}

		public static void setBoolean(String key, boolean value) {
			Configuration.instance.setPropertyBoolean(key, value);
		}

		public static void setString(String key, String value) {
			Configuration.instance.setPropertyString(key, value);
		}

	}

	/**
	 * Initialize all things about game !
	 */
	public Game() {
		createWindow(null);
		initialize(null);
	}

	/**
	 * 
	 */
	private void createWindow(String[] args) {
		int width = Configuration.getInteger("window.width", 320);
		int height = Configuration.getInteger("window.height", 320);
		String title = Configuration.get("window.title", "SingleClassGame");
		scale = Configuration.getFloat("window.scale", 2.0f);
		debug = Configuration.getInteger("debug.level", 1);

		dim = new Dimension((int) (width * scale), (int) (height * scale));

		playZone = new Dimension(dim.width, dim.height);

		buffer = new BufferedImage(dim.width, dim.height, BufferedImage.TYPE_INT_ARGB);

		kil = new KeyInputListener();

		world = new World(new Vector2D(0.0f, -0.981f));

		resourceMgr = new ResourceManager();

		window = new Window(this, title);
		window.setKeyInputListener(kil);
		window.show();

	}

	/**
	 * Get attributes from Java command line.
	 * 
	 * @param args
	 */
	public Game(String[] args) {
		retrieveDependencies();
		parseArgs(args);
		createWindow(args);
		initialize(args);
	}

	/**
	 * Retrieve Maven dependency list.
	 */
	private void retrieveDependencies() {
		VersionTracker vt = new VersionTracker();
		vt.extractProjectInformation();

	}

	/**
	 * initialize some GameObject's to play with.
	 */
	public void initialize(String[] args) {
		// add Game key listener
		kil.register(new GameKeyInput());
		resourceMgr.addResource("playerBall", "res/images/blue-bouncing-ball-64x64.png");
		resourceMgr.addResource("enemyBall", "res/images/red-bouncing-ball-64x64.png");

		player = new GameObject("player", 50, 50);
		try {
			player.setImage(resourceMgr.getImage("playerBall"));
		} catch (ResourceUnknownException e) {
			System.err.println("Unable to retrieve the playerBall resource");
			System.exit(-1);
		}
		player.setMoveFactor(0.5f);
		player.setMass(20.0f);
		player.setFriction(0.56f);
		player.setElasticity(0.32f);
		player.setOffset(player.width / 2, player.height / 2);

		player.setPriority(1);
		player.setDebugColor(Color.RED);
		add(player);

		// add specific game player key listener
		kil.register(new PlayerKeyInput(player, kil));

		for (int i = 0; i < 10; i++) {
			float posX = (float) (Math.random() * dim.width / 2);
			float posY = (float) (Math.random() * dim.height / 2);
			GameObject enemy = new GameObject("enemy_" + i, posX, posY);
			try {
				enemy.setImage(resourceMgr.getImage("enemyBall"));
			} catch (ResourceUnknownException e) {
				System.err.println("Unable to retrieve the enemyBall resource");
				System.exit(-1);
			}
			enemy.setSize(16.0f, 16.0f);
			enemy.setAcceleration((float) Math.random() * 0.010f, (float) Math.random() * 0.010f);
			enemy.setPriority(2 + i);
			enemy.setMass(100.0f);
			enemy.setFriction(0.10f);
			enemy.setElasticity(0.80f);
			enemy.setOffset(enemy.width / 2, enemy.height / 2);
			add(enemy);
		}
	}

	/**
	 * Run the main loop for the game.
	 */
	public void run() {
		float previousTime = 0;
		float elapsed = 0;
		float currentTime = 0;
		int realFPS = 0;
		int framesCount = 0, timeFrames = 0;
		while (!exit) {
			currentTime = System.nanoTime();
			if (previousTime > 0.0f && !pause) {
				elapsed = (currentTime - previousTime) / 10000000.0f;
				if (elapsed < 0.0f) {
					elapsed = 1.0f;
				}
				update(elapsed);
			}
			if (elapsed <= fpsDelay) {
				render(String.format("debug:%d c:%02d t:%04d fps:%03d pause:%s", debug, framesCount, timeFrames,
						realFPS, (pause ? "on" : "off")));
			}
			postOperation();
			framesCount += 1;
			timeFrames += elapsed;
			if (timeFrames > 1000) {
				realFPS = framesCount;
				framesCount = 0;
				timeFrames = 0;
			}
			wait(upsDelay - elapsed);
			previousTime = currentTime;
		}
		dispose();
		System.exit(0);
	}

	private void postOperation() {
		for (GameObject go : objects) {
			go.forces.clear();
		}

	}

	/**
	 * Wait for a delay corresponding to the elapsed time modulus the fpsDelay.
	 * 
	 * @param elapsed
	 */
	private void wait(float elapsed) {
		if (elapsed > fpsDelay) {
			elapsed = 1;
		}
		if (elapsed > 0) {
			try {
				Thread.sleep((int) (elapsed));
			} catch (InterruptedException e) {
				System.err.println("unable to wait !!");
				System.exit(-1);
			}

		}
	}

	/**
	 * Update all the objects of the game.
	 * 
	 * @param elapsed
	 *            time elapsed since previous call.
	 */
	public void update(float elapsed) {
		if (objects != null && objects.size() > 0) {
			for (GameObject o : objects) {
				// o.update(elapsed);
				o.updatePhysic(elapsed);
				constrainsObjectToPlayZone(playZone, o);
			}
		}
	}

	/**
	 * Constrained {@link GameObject} <code>object</code> to the Play Zone
	 * <code>constrainedZone</code>.
	 * 
	 * @param constrainedZone
	 *            the zone where to constrains game object.
	 * @param object
	 *            the object to be constrained to the play zone.
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

	/**
	 * Render all the game objects to the buffer.
	 */
	public void render(String fps) {

		// retrieve graphic API
		Graphics2D g = (Graphics2D) buffer.getGraphics();

		// set rendering parameters
		// pixel anti-aliasing
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		// text anti-aliasing.
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		// clear view before redraw things
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, buffer.getWidth(), buffer.getHeight());

		// if objects in the list, draw all those things
		if (objects != null && objects.size() > 0) {
			for (GameObject o : objects) {
				o.render(g);
			}
		}
		// add some debug information
		if (debug > 0) {
			g.setColor(Color.ORANGE);
			g.drawString(fps, 10, buffer.getHeight() - 20);
		}

		// release API
		g.dispose();

		// copy rendering buffer to window.
		drawToScreen();
	}

	/**
	 * Draw buffer to screen.
	 */
	private void drawToScreen() {
		Graphics2D gbuff = window.getGraphics();
		gbuff.drawImage(buffer, 0, 0, (int) (dim.width * scale), (int) (dim.height * scale), 0, 0, dim.width,
				dim.height, null);
		gbuff.dispose();
	}

	/**
	 * release all resources before quitting.
	 */
	private void dispose() {
		dim = null;
		window = null;
		kil = null;
		player = null;
		resourceMgr = null;
		buffer = null;
	}

	/**
	 * Add a GameObject to the list of object managed by the Game.
	 * 
	 * @param o
	 *            the GameObject to add to the list.
	 */
	public void add(GameObject o) {
		o.forces.addAll(world.forces);
		o.forces.add(world.gravity);
		objects.add(o);
		objects.sort(new Comparator<GameObject>() {
			public int compare(GameObject o1, GameObject o2) {
				return (o1.priority < o2.priority ? -1 : 1);
			};
		});
	}

	/**
	 * Arguments to be analyzed and
	 * 
	 * @param args
	 */
	private void parseArgs(String[] args) {
		for (String arg : args) {
			String[] parts = arg.split(":");
			switch (parts[0]) {
			case "width":
			case "w":
				int valueWidth = Integer.parseInt(parts[1]);
				if (valueWidth > 0 && valueWidth < 2048) {
					dim.width = valueWidth;
					System.out.println(String.format("Window width set to %d", dim.width));
					Configuration.setInteger("window.width", dim.width);
				} else {
					System.err.println(String.format("Unable to set height to %d (min=1,max=2048)", valueWidth));
				}
				break;
			case "height":
			case "h":
				int valueHeight = Integer.parseInt(parts[1]);
				if (valueHeight > 0 && valueHeight < 2048) {
					dim.height = valueHeight;
					System.out.println(String.format("Window height set to %d", dim.height));
					Configuration.setInteger("window.height", dim.height);
				} else {
					System.err.println(String.format("Unable to set height to %d (min=1,max=2048)", valueHeight));
				}
				break;
			case "scale":
			case "s":
				float valueScale = Float.parseFloat(parts[1]);
				if (valueScale >= 1.0f && valueScale <= 4.0f) {
					scale = valueScale;
					System.out.println(String.format("Window scale set to %f", valueScale));
					Configuration.setFloat("window.scale", scale);
				} else {
					System.err.println(String.format("Unable to set scale value to %f (min=1,max=4)", valueScale));
				}
				break;
			case "debug":
			case "d":
				int value = Integer.parseInt(parts[1]);
				if (value >= 0 && value <= 9) {
					debug = value;
					System.out.println(String.format("debug mode set to %d", debug));
					Configuration.setInteger("debug.level", debug);
				} else {
					System.err.println(String.format("Unable to set value to %d, (min=0,max=9)", value));
				}
				break;
			default:
				break;
			}
		}
		Configuration.save();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Game game = new Game(args);
		game.run();
	}

}
