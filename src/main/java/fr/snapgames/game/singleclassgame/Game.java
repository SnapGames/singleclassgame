/**
 * SnapGames
 * 
 * Project : SingleClassGame
 * 
 * <p>This project intends to propose a basement for **2D game development** with the `Java` language. 
 * 
 * @year 2018
 * @see http://snapgames.fr/
 */
package fr.snapgames.game.singleclassgame;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.CopyOnWriteArrayList;

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
 * <li>the {@link Game#render(float,string)} will compute and draw to screen all
 * those objects.
 * </ul>
 *
 * @author Frédéric Delorme<frederic.delorme@snapgames.fr>
 * 
 * @see http://github.com/SnapGames/singleclassgame
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

	private List<CollisionResponseProcessor> processors = new ArrayList<>();

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
	 * the object Factory.
	 */
	Factory factory = null;

	/**
	 * the main resource manager to try and share things.
	 */
	private ResourceManager resourceMgr;

	/**
	 * The world object contains physic constrains for the physic engine system.
	 */
	private World world;

	/**
	 * The window where to draw the game view.
	 */
	private Window window;

	/**
	 * THE player for this game.
	 */
	GameObject player;

	private CollisionManager collisionMgr;

	public boolean randomizeEnemies;

	/**
	 * This integrated class parse Maven model to expose a resulting list of
	 * dependencies.
	 *
	 * @author Frédéric Delorme
	 */
	public class VersionTracker {
		private final Logger logger = LoggerFactory.getLogger(VersionTracker.class);
		Model model = null;

		public VersionTracker() {
			try {
				MavenXpp3Reader reader = new MavenXpp3Reader();
				if (reader != null) {
					if ((new File("pom.xml")).exists()) {
						model = reader.read(new FileReader("pom.xml"));
					} else {
						model = reader.read(new InputStreamReader(Game.class
								.getResourceAsStream("/META-INF/maven/fr.snapgames.game/singleclassgame/pom.xml")));
					}
				}
			} catch (IOException | XmlPullParserException e) {
				logger.error("Unable to retrieve data from maven `pom.xml` file", e);
			}
			if (model == null) {
				logger.info("unable to read dependency data from maven");
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
	 * The Window object will create layer between the game and the OS windowing
	 * system. The window can have a size at `scale` regarding game viewport size.
	 * 
	 * 
	 * @author Frédéric Delorme<frederic.delorme@snapgames.fr>
	 */
	public class Window {
		/**
		 * rendering frame.
		 */
		private JFrame frame;

		/**
		 * CReate a window containing the Game with a title.
		 *
		 * @param game  the game to display in the window.
		 * @param title the title of the window.
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
		 * @param title the new window title.
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
	 */
	public class World {

		/**
		 * the default gravity value for this world (can be overridden at world
		 * construction).
		 */
		Vector2D gravity = new Vector2D("gravity", 0.0f, -0.981f);

		/**
		 * List of forces to be applied to all objects. (would be changed in a next
		 * version to introduce space gravity and attraction factor).
		 */
		List<Vector2D> forces = new ArrayList<>();

		/**
		 * List of available camera in this world.
		 */
		List<Camera> cameras = new ArrayList<>();
		/**
		 * the current active camera.
		 */
		Camera activeCam = null;

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

	/**
	 * the ResourceManager class intends to load and cache some objects like image,
	 * sounds, font, etc... any resources.
	 *
	 * @author Frédéric Delorme <frederic.delorme@snapgames.fr>
	 */
	public class ResourceManager {
		private Map<String, Object> objects = new HashMap<>();

		/**
		 * Add a resource to the set.
		 *
		 * @param name name for this resource
		 * @param path path to the rsource.
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
		 * @param name the name of the resource to retrieve.
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
	 */
	public class KeyInputListener implements KeyListener {
		/**
		 * current state of the key
		 */
		private boolean[] keys = new boolean[65235];
		/**
		 * Previous state of the key.
		 */
		private boolean[] previous = new boolean[65235];

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
			if (!this.objectsCallBack.contains(kcb)) {
				this.objectsCallBack.add(kcb);
			} else {
				logger.error("The KeyInputListener already contains this {} key listener", kcb.getClass().getName());
			}
		}

		/**
		 * This is to remove a key listener from the system.
		 *
		 * @param kcb
		 */
		public void remove(KeyListener kcb) {
			this.objectsCallBack.remove(kcb);
		}

		/**
		 * Return current state of the key for <code>keyCode</code>.
		 * 
		 * @param keyCode Key code to be verified.
		 * @return true if pushed, else false.
		 */
		public boolean getKey(int keyCode) {
			return keys[keyCode];
		}

		/**
		 * Return the previous state of the key for <code>KeyCode</code>.
		 * 
		 * @param keyCode the Key code of the key to be verified.
		 * @return true if previously pushed, else false.
		 */
		public boolean getPrevious(int keyCode) {
			return previous[keyCode];
		}
	}

	/**
	 * A 2D Vector class to compute next gen things..
	 *
	 * @author Frédéric Delorme<frederic.delorme@snapgames.fr>
	 */
	public class Vector2D {
		private String name;

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
		Vector2D(String name) {
			this.x = 0.0f;
			this.y = 0.0f;
			name = "v_noname";
		}

		/**
		 * Set the default gravity.
		 *
		 * @param x
		 * @param y
		 */
		Vector2D(String name, float x, float y) {
			this.name = name;
			this.x = x;
			this.y = y;
		}

		/**
		 * add the v vector.
		 *
		 * @param v
		 */
		Vector2D add(Vector2D v) {
			if (v != null) {
				this.x += v.x;
				this.y += v.y;
			}
			return this;
		}

		/**
		 * substract the v vector.
		 *
		 * @param v
		 */
		public Vector2D sub(Vector2D v) {
			return new Vector2D(this.name, x - v.x, y - v.y);
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
		 * @param v the vector to compute distance with.
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

			return new Vector2D(this.name, x, y);
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

		public String toString() {
			return String.format("(%03.4f,%03.4f)", x, y);
		}

	}

	/**
	 * Interface to managed Collision with CollisionManager and QuadTree.
	 * 
	 * @author Frédéric Delorme
	 * 
	 * @see QuadTree
	 * @see CollisionManager
	 */
	public interface Collidable {

		BoundingBox getBoundingBox();

		void addCollider(Collidable c);
	}

	/**
	 * This is a simple response processor
	 * 
	 * @author Frédéric Delorme
	 *
	 */
	public class ColliderResponse implements CollisionResponseProcessor {

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * fr.snapgames.game.singleclassgame.Game.CollisionResponseProcessor#onCollide(
		 * fr.snapgames.game.singleclassgame.Game.GameObject,
		 * fr.snapgames.game.singleclassgame.Game.GameObject)
		 */
		@Override
		public void onCollide(GameObject o1, GameObject o2) {
			if (o1.name.startsWith("player")) {
				if (o2.lifeDuration > 0) {
					o2.lifeDuration = 0;
				}
				Vector2D vR = o2.velocity.add(o1.velocity);
				vR.multiply(-1 * o2.elasticity * o2.friction * o1.elasticity * o1.friction);
				o2.velocity = vR;

				o1.velocity.x = 0;
				o1.velocity.y = 0;
				o1.acceleration.x = 0;
				o1.acceleration.y = 0;
			}
		}

	}

	/**
	 * Interface explaining how to implement a Collision response process.
	 * 
	 * @author Frédéric Delorme
	 *
	 */
	public interface CollisionResponseProcessor {
		/**
		 * When a Collision occurred, this Collision Response Processor method is called
		 * where `o1` and `o2` are the collision players.
		 * 
		 * @param o1 First GameObject participating in collision.
		 * @param o2 Second GameObject participating in collision.
		 */
		public void onCollide(GameObject o1, GameObject o2);
	}

	/**
	 * THe Collision Manager help game to detect and manage collision between
	 * <code>Collidable</code> object.
	 * 
	 * @author Frédéric Delorme
	 * @see Collidable
	 */
	public class CollisionManager {

		private List<Collidable> colliders = new ArrayList<>();

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

		public void add(GameObject e) {
			colliders.add(e);
			logger.debug("Add {} to CollisionSystem", e.name);
		}

		public void remove(GameObject e) {
			colliders.remove(e);

			logger.debug("Remove {} from CollisionSystem", e.name);
		}

		public void remove(String name) {
			List<Collidable> toBeRemoved = new ArrayList<>();
			for (Collidable c : colliders) {
				GameObject e = (GameObject) c;
				if (e.name.equals(name)) {
					toBeRemoved.add(e);

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
			GameObject o = game.player;
			quadTree.retrieve(collisionList, o);
			if (collisionList != null && !collisionList.isEmpty()) {
				for (Collidable s : collisionList) {
					GameObject ago = (GameObject) s;
					if (!o.name.equals(ago.name) && ago.bBox.intersect(o.bBox) == 1) {
						o.addCollider(ago);
						ago.addCollider(o);
						if (processors != null && !processors.isEmpty()) {
							for (CollisionResponseProcessor crp : processors) {
								crp.onCollide(o, ago);
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

	/**
	 * A lot of this code was stolen from this article: <br>
	 * http://gamedevelopment.tutsplus.com/tutorials/quick-tip-use-quadtrees-to-detect-likely-collisions-in-2d-space--gamedev-374
	 * <br>
	 * <br>
	 * created by chrislo27
	 *
	 */
	public class QuadTree {

		private int MAX_OBJECTS = 4;
		private int MAX_LEVELS = 12;

		private int level;
		private List<Collidable> objects;
		private float posX, posY, width, height;
		private QuadTree[] nodes;
		private int identifiedIndex;

		/**
		 * ideal constructor for making a quadtree that's empty <br>
		 * simply calls the normal constructor with <code>
		 * this(0, 0, 0, width, height)
		 * </code>
		 * 
		 * @param width  your game world width in units
		 * @param height your game world height in units
		 */
		public QuadTree(float width, float height) {
			this(0, 0, 0, width, height);
		}

		/**
		 * 
		 * @param pLevel start at level 0 if you're creating an empty quadtree
		 * @param x
		 * @param y
		 * @param width
		 * @param height
		 */
		public QuadTree(int pLevel, float x, float y, float width, float height) {
			level = pLevel;
			objects = new ArrayList<>();
			posX = x;
			posY = y;
			this.width = width;
			this.height = height;
			nodes = new QuadTree[4];
		}

		public QuadTree setMaxObjects(int o) {
			MAX_OBJECTS = o;
			return this;
		}

		public QuadTree setMaxLevels(int l) {
			MAX_LEVELS = l;
			return this;
		}

		public int getChecks() {
			int num = 0;

			num += (objects.size() * objects.size());

			for (int i = 0; i < nodes.length; i++) {
				if (nodes[i] != null) {
					num += nodes[i].getChecks();
				}
			}

			return num;
		}

		/*
		 * Clears the quadtree
		 */
		public void clear() {
			objects.clear();

			for (int i = 0; i < nodes.length; i++) {
				if (nodes[i] != null) {
					nodes[i].clear();
					nodes[i] = null;
				}
			}
		}

		/*
		 * Splits the node into 4 subnodes
		 */
		private void split() {
			float subWidth = (width / 2);
			float subHeight = (height / 2);
			float x = posX;
			float y = posY;

			nodes[0] = new QuadTree(level + 1, x + subWidth, y, subWidth, subHeight);
			nodes[1] = new QuadTree(level + 1, x, y, subWidth, subHeight);
			nodes[2] = new QuadTree(level + 1, x, y + subHeight, subWidth, subHeight);
			nodes[3] = new QuadTree(level + 1, x + subWidth, y + subHeight, subWidth, subHeight);
		}

		/*
		 * Determine which node the object belongs to. -1 means object cannot completely
		 * fit within a child node and is part of the parent node
		 */
		private int getIndex(Collidable col) {
			BoundingBox bb = col.getBoundingBox();
			int index = -1;
			double verticalMidpoint = posX + (width / 2);
			double horizontalMidpoint = posY + (width / 2);

			// Object can completely fit within the top quadrants
			boolean topQuadrant = (bb.rect.getX() < horizontalMidpoint
					&& bb.rect.getY() + bb.rect.getHeight() < horizontalMidpoint);
			// Object can completely fit within the bottom quadrants
			boolean bottomQuadrant = (bb.rect.getY() > horizontalMidpoint);

			// Object can completely fit within the left quadrants
			if (bb.rect.getX() < verticalMidpoint && bb.rect.getX() + bb.rect.getWidth() < verticalMidpoint) {
				if (topQuadrant) {
					index = 1;
				} else if (bottomQuadrant) {
					index = 2;
				}
			}
			// Object can completely fit within the right quadrants
			else if (bb.rect.getX() > verticalMidpoint) {
				if (topQuadrant) {
					index = 0;
				} else if (bottomQuadrant) {
					index = 3;
				}
			}
			identifiedIndex = index;

			return index;
		}

		/*
		 * Insert the object into the quadtree. If the node exceeds the capacity, it
		 * will split and add all objects to their corresponding nodes.
		 */
		public void insert(Collidable pRect) {
			if (nodes[0] != null) {
				int index = getIndex(pRect);

				if (index != -1) {
					nodes[index].insert(pRect);

					return;
				}
			}

			objects.add(pRect);

			if (objects.size() > MAX_OBJECTS && level < MAX_LEVELS) {
				if (nodes[0] == null) {
					split();
				}

				int i = 0;
				while (i < objects.size()) {
					int index = getIndex(objects.get(i));
					if (index != -1) {
						nodes[index].insert(objects.remove(i));
					} else {
						i++;
					}
				}
			}
		}

		/*
		 * Return all objects that could collide with the given object
		 */
		public List<Collidable> retrieve(List<Collidable> returnObjects, Collidable pRect) {
			int index = getIndex(pRect);
			if (index != -1 && nodes[0] != null) {
				nodes[index].retrieve(returnObjects, pRect);
			}

			returnObjects.addAll(objects);

			return returnObjects;
		}

		public void draw(Graphics2D g) {

			for (int i = 0; i < nodes.length; i++) {
				QuadTree n = nodes[i];
				if (n != null) {
					if (i == n.identifiedIndex) {
						g.setColor(Color.ORANGE);
					} else {
						g.setColor(Color.BLUE);
					}
					g.drawRect((int) n.posX, (int) n.posY, (int) n.width, (int) n.height);
					n.draw(g);
				}
			}
		}

	}

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

	/**
	 * Bounding Box for any object managed by the system.
	 *
	 * @author Frédéric Delorme
	 */
	public class BoundingBox {

		/**
		 * position for the boundingbox.
		 */
		Rectangle2D rect;
		/**
		 * diam1 is used for elipse1/circle/capsule BoundingBox type
		 */
		Ellipse2D elipse1;
		Ellipse2D elipse2;
		float e1e2Distance;
		/**
		 * list of specific points for POINT mode.
		 */
		Point[] points;

		BoundingBoxType type;

		public int intersect(BoundingBox b) {
			switch (type) {
			case NONE:
				return 0;
			case RECTANGLE:
				return (b.rect.intersects(rect) ? 1 : 0);
			case CIRCLE:
				return (b.elipse1.intersects(rect) ? 1 : 0);
			case CAPSULE:
				// TODO to be implemented later.
				return 0;
			case POINTS:
				// TODO to be implemented later.
				return 0;
			default:
				return 0;
			}
		}

		/**
		 * Update bounding box according to GameObject size and position.
		 *
		 * @param go
		 */
		public void update(GameObject go) {
			this.rect = new Rectangle2D.Float(go.position.x, go.position.y, go.width, go.height);
			this.elipse1 = new Ellipse2D.Float(go.position.x, go.position.y, go.width, go.height);
			// TODO compute distance for CAPSULE.
			// this.elipse2 = new Ellipse2D.Float(go.position.x, go.position.y, go.width,
			// go.height);
		}

		/**
		 * Create a new Bounding Box.
		 * 
		 * @return
		 */
		public BoundingBox builder() {
			return new BoundingBox();
		}

		/**
		 * Define the BoundingBoxType for this BoundingBox.
		 * 
		 * @param type type of the bounding box
		 * @return this object.
		 * @see BoundingBoxType
		 */
		public BoundingBox setType(BoundingBoxType type) {
			this.type = type;
			return this;
		}
	}

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

		String name = "";

		/**
		 * Next generation GameObject ---- Start here ---->
		 */
		Vector2D acceleration = new Vector2D("acceleration");
		Vector2D velocity = new Vector2D("velocity");
		Vector2D position = new Vector2D("position");

		Vector2D offset = new Vector2D("offset");
		Vector2D size = new Vector2D("size");

		List<Vector2D> forces = new ArrayList<>();
		Vector2D gravity = new Vector2D("gravity", 0.0f, 0.981f);

		float scale = 1.0f;

		float width = 16.0f, height = 16.0f;

		float moveFactor = 0.5f;

		float mass = 0.89f;
		float friction = 0.92f;
		float elasticity = 0.60f;

		int lifeDuration = 0;

		BufferedImage image = null;

		int priority = 0;

		Color debugColor = Color.ORANGE;

		Color color = Color.GREEN;

		BoundingBox bBox;

		private List<Collidable> colliders = new CopyOnWriteArrayList<>();

		/**
		 * Create a new basic Object entity with a <code>name</code>.
		 *
		 * @param name the name of this object.
		 */
		private GameObject(String name) {
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
		private GameObject(String name, float x, float y) {
			this(name);
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
	}

	/**
	 * The factory to create any object for this sample game.
	 * <p>
	 * - Factory#createGameObject(String name) create a GameObject, -
	 * Factory#createCamera(String name), create a Camera (sic).
	 *
	 * @author Frédéric Delorme
	 */
	public class Factory {
		/**
		 * Create a new GameObject.
		 *
		 * @param name the name of the new object.
		 * @return
		 */
		public GameObject createGameObject(String name) {
			return new GameObject(name);
		}

		/**
		 * Create a new Camera.
		 *
		 * @param name the name of the new camera.
		 * @return
		 */
		public Camera createCamera(String name) {
			return new Camera(name);
		}

		/**
		 * Dynamic Factory create.
		 *
		 * @param clazz class to be instantiated
		 * @param name  name for this object.
		 * @return
		 */
		public Object create(Class<? extends GameObject> clazz, String name) {
			Constructor<?> constructor;
			Object obj = null;
			try {
				constructor = clazz.getDeclaredConstructor(String.class);
				obj = constructor.newInstance(name);
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return obj;
		}

	}

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

		Camera(String name) {
			super(name);
		}

		Camera(String name, float x, float y) {
			super(name, x, y);
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
			if (debug > 1) {
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
		Camera setView(Dimension view) {
			this.view = view;
			return this;
		}

		/**
		 * Add a target to the camera.
		 *
		 * @param target
		 * @return
		 */
		Camera setTrackedObject(GameObject target) {
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

	/**
	 * The {@link GameKeyInput} is a key input handler to manage the Game Level key
	 * listener.
	 * <ul>
	 * <li><key>ESCAPE</key> to quit the game,</li>
	 * <li><key>PAUSE</key> to set game in the pause state.</li>
	 * </ul>
	 *
	 * @author Frédéric Delorme<frederic.delorme@snapgames.fr>
	 */
	public class GameKeyInput implements KeyListener {
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
				logger.info(String.format("Pause mode set to %s", (pause ? "ON" : "OFF")));
				break;
			case KeyEvent.VK_F3:
			case KeyEvent.VK_D:
				debug = Math.floorMod(debug + 1, 5);
				logger.info(String.format("Debug level set to %d", debug));
				break;
			case KeyEvent.VK_S:
			case KeyEvent.VK_F12:
				logger.info("take ascreenshot");
				break;

			case KeyEvent.VK_R:
				logger.info("randomize enemies forces");
				randomizeEnemies = true;
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
	 */
	public class PlayerKeyInput implements KeyListener {

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
				player.forces.add(new Vector2D("move-up", 0.0f, -player.moveFactor * 10.0f));
				logger.debug("player move up y+={}", -player.moveFactor);
			}
			if (kil.keys[KeyEvent.VK_DOWN]) {
				move = true;
				player.forces.add(new Vector2D("move-down", 0.0f, player.moveFactor));
				logger.debug("player move down y+={}", player.moveFactor);
			}
			if (kil.keys[KeyEvent.VK_LEFT]) {
				move = true;
				player.forces.add(new Vector2D("move-left", -player.moveFactor, 0.0f));
				logger.debug("player move left x+={}", player.moveFactor);
			}

			if (kil.keys[KeyEvent.VK_RIGHT]) {
				move = true;
				player.forces.add(new Vector2D("move-right", player.moveFactor, 0.0f));
				logger.debug("player move left x+={}", -player.moveFactor);
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

	/**
	 * This configuration object intends to manage persisting configuration
	 * properties to a specific file.
	 *
	 * @author Frédéric Delorme
	 */
	public static class Configuration {

		private static final Logger logger = LoggerFactory.getLogger(Configuration.class);

		private static final String UNKNOWN_CONFIG_KEY = "UNKNOWN_CONFIG_KEY";
		public static Configuration instance = new Configuration();
		Properties props;

		private Configuration() {
			props = new Properties();
			load();
		}

		/**
		 * Load configuration properties from file. By default, the configuration file
		 * is located in "/res/configuration.properties". All matching properties are
		 * loaded.
		 */
		private void load() {
			try {
				if (props == null) {
					props = new Properties();
				}
				if (new File(Game.class.getResource("/").getPath() + "configuration.properties").exists()) {
					props.load(Game.class.getResourceAsStream("/configuration.properties"));

				} else {
					props.load(Game.class.getResourceAsStream("/res/configuration.properties"));
				}
				for (Entry<Object, Object> prop : props.entrySet()) {
					logger.info(String.format("config %s : %s", prop.getKey(), prop.getValue()));
				}

			} catch (IOException e) {
				logger.error("Unable to read configuration file", e);
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
				logger.error("Unable to store configuration file", e);
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
		 * @param key key configuration to be retrieved.
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
	 * Create the Game window based on configuration and/or args.
	 *
	 * @param args list of command line argument send to java program.
	 */
	private void createWindow(String[] args) {

		// retrieve configuration things
		int width = Configuration.getInteger("window.width", 320);
		int height = Configuration.getInteger("window.height", 320);
		String title = Configuration.get("window.title", "SingleClassGame");
		scale = Configuration.getFloat("window.scale", 2.0f);
		debug = Configuration.getInteger("debug.level", 1);

		// Window dimension
		dim = new Dimension((int) (width * scale), (int) (height * scale));

		// Playing zone where things happened
		playZone = new Dimension(dim.width * 4, dim.height * 4);

		// buffer where to render things
		buffer = new BufferedImage(dim.width, dim.height, BufferedImage.TYPE_INT_ARGB);

		// add the default key listener
		kil = new KeyInputListener();

		// Initialize object factory
		factory = new Factory();

		// Set hte default World parameters.
		world = new World(new Vector2D("gravity", 0.0f, -0.981f));
		// Initialize ResourceManager
		resourceMgr = new ResourceManager();

		collisionMgr = new CollisionManager();

		// create window and attach needed things
		window = new Window(this, title);
		window.setKeyInputListener(kil);
		// show me the window !
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

		// Define Collision manage playground.
		collisionMgr.setDimension(playZone);
		// register the collision response processor for our demo.
		collisionMgr.registerProcessor(new ColliderResponse());

		// read image resources
		resourceMgr.addResource("playerBall", "res/images/blue-bouncing-ball-64x64.png");
		resourceMgr.addResource("enemyBall", "res/images/red-bouncing-ball-64x64.png");

		// Add objects to world.
		try {

			player = factory.createGameObject("player").setPosition(50, 50).setImage(resourceMgr.getImage("playerBall"))
					.setMoveFactor(0.50f).setMass(100f).setFriction(0.30f).setElasticity(0.32f).offsetAtCenter()
					.setPriority(1).setDebugColor(Color.RED).setLifeDuration(100000);
			player.bBox.type = BoundingBoxType.CIRCLE;
			add(player);
		} catch (ResourceUnknownException e) {
			System.err.println("Unable to retrieve the playerBall resource");
			System.exit(-1);
		}

		// add specific game player key listener
		kil.register(new PlayerKeyInput(player, kil));

		for (int i = 0; i < 50; i++) {
			float posX = (float) (Math.random() * playZone.width);
			float posY = (float) (Math.random() * playZone.height);
			try {
				GameObject enemy = factory.createGameObject("enemy_" + i).setPosition(posX, posY)
						.setImage(resourceMgr.getImage("enemyBall")).setSize(24.0f, 24.0f)
						.setAcceleration((float) Math.random() * 0.005f, (float) Math.random() * 0.005f)
						.setPriority(2 + i).setMass(50.0f).setFriction(0.95f).setElasticity(0.890f)
						.setOffset(12.0f, 12.0f).setLifeDuration(300);
				enemy.bBox.type = BoundingBoxType.CIRCLE;
				add(enemy);
			} catch (ResourceUnknownException e) {
				System.err.println("Unable to retrieve the enemyBall resource");
				System.exit(-1);
			}
		}

		// Add a Camera to the world.
		Camera cam = (Camera) factory.createCamera("camera").setTrackedObject(player).setTweenFactor(0.22f).setView(dim)
				.setPosition(0.0f, 0.0f);

		world.addCamera(cam);

		// Add a piece of wind to our world !
		// world.addForce(new Vector2D("some-wind", 0.5f, 0.0f));

	}

	private void randomizeEnemies() {
		for (GameObject o : objects) {
			if (o.name.startsWith("enemy_")) {

				o.forces.clear();
				o.setVelocity(0, 0);
				o.velocity.x = 0;
				o.velocity.y = 0;
				o.setAcceleration((float) ((Math.random() * 50f) - 25f), (float) ((Math.random() * 50f) - 25f));
				o.gravity = new Vector2D("gravity", 0.0f, -9.81f);
				logger.info("add a new acceleration to {}:{}", o.name, o.acceleration);
			}
		}

	}

	/**
	 * Run the main loop for the game.
	 */
	public void run() {

		float previousTime = System.nanoTime();
		float elapsed = 0;
		float currentTime = 0;
		int realFPS = 0;
		int framesCount = 0, timeFrames = 0;

		while (!exit) {
			currentTime = System.nanoTime();
			if (previousTime > 0.0f && !pause) {
				asynchorneInput();
				elapsed = (currentTime - previousTime) / 10000000.0f;
				if (elapsed < 0.0f) {
					elapsed = 1.0f;
				}
				update(elapsed);
			}
			if (elapsed <= fpsDelay) {
				render(realFPS, String.format("debug:%d c:%02d t:%04d fps:%03d pause:%s", debug, framesCount,
						timeFrames, realFPS, (pause ? "on" : "off")));
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

	private void asynchorneInput() {

		// move up (with extra speed !)
		if (kil.keys[KeyEvent.VK_UP]) {
			player.forces.add(new Vector2D("move-up", 0.0f, -player.moveFactor * 20.0f));
			logger.debug("player move up y+={}", -player.moveFactor);
		}
		// move down
		if (kil.keys[KeyEvent.VK_DOWN]) {
			player.forces.add(new Vector2D("move-down", 0.0f, player.moveFactor));
			logger.debug("player move down y+={}", player.moveFactor);
		}
		// move left
		if (kil.keys[KeyEvent.VK_LEFT]) {
			player.forces.add(new Vector2D("move-left", -player.moveFactor, 0.0f));
			logger.debug("player move left x+={}", player.moveFactor);
		}

		// move right
		if (kil.keys[KeyEvent.VK_RIGHT]) {
			player.forces.add(new Vector2D("move-right", player.moveFactor, 0.0f));
			logger.debug("player move left x+={}", -player.moveFactor);
		}

		// stop any action !
		if (kil.keys[KeyEvent.VK_SPACE]) {
			player.forces.clear();
			player.velocity.x = 0.0f;
			player.velocity.y = 0.0f;
			player.acceleration.x = 0.0f;
			player.acceleration.y = 0.0f;
		}

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
	 * @param elapsed time elapsed since previous call.
	 */
	public void update(float elapsed) {
		if (objects != null && objects.size() > 0) {
			for (GameObject o : objects) {
				o.updatePhysic(elapsed);
				constrainsObjectToPlayZone(playZone, o);
			}
		}
		collisionMgr.update(this, elapsed);
		if (world != null && world.activeCam != null) {
			world.activeCam.updatePhysic(elapsed);
		}
		if (randomizeEnemies) {
			randomizeEnemies();
			randomizeEnemies = false;
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

	/**
	 * Render all the game objects to the buffer.
	 */
	public void render(float realFPS, String fps) {

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

		if (world.activeCam != null) {
			g.translate(-world.activeCam.position.x, -world.activeCam.position.y);
			g.rotate(-world.activeCam.angle);
		}

		// if objects in the list, draw all those things
		if (objects != null && objects.size() > 0) {
			for (GameObject o : objects) {
				o.render(g);
			}
		}

		if (debug > 2) {
			g.setColor(Color.LIGHT_GRAY);
			Stroke bckValue = g.getStroke();
			g.setStroke(new BasicStroke(0.56f));
			for (int ix = 0; ix < playZone.width; ix += (playZone.width / 20)) {
				for (int iy = 0; iy < playZone.height; iy += (playZone.height / 20)) {
					g.drawRect(ix, iy, (playZone.width / 20), (playZone.height / 20));
				}
			}
			g.setStroke(bckValue);
		}
		if (debug > 3) {
			collisionMgr.draw(this, g, realFPS);

		}

		if (world.activeCam != null) {
			g.rotate(world.activeCam.angle);
			g.translate(world.activeCam.position.x, world.activeCam.position.y);
			world.activeCam.render(g);
		}

		// add some debug information
		if (debug > 0) {
			g.setColor(Color.ORANGE);
			g.drawString(fps, 10, 40);
			if (debug > 1) {
				g.setColor(Color.GRAY);
				g.drawRect(0, 0, dim.width, dim.height);
			}
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
	 * @param o the GameObject to add to the list.
	 */
	public void add(GameObject o) {
		o.forces.addAll(world.forces);
		o.forces.add(world.gravity);
		objects.add(o);
		collisionMgr.add(o);
		objects.sort(new Comparator<GameObject>() {
			public int compare(GameObject o1, GameObject o2) {
				return (o1.priority < o2.priority ? -1 : 1);
			}

			;
		});
	}

	/**
	 * Arguments to be analyzed and
	 *
	 * @param args
	 */
	private void parseArgs(String[] args) {
		if (dim == null) {
			dim = new Dimension(320, 200);
		}
		for (String arg : args) {
			String[] parts = arg.split("=");
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
