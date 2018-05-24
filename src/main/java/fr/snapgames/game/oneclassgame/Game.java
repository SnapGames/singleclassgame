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
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * This main Single class is a tutorial on how to develop a game with Java.
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
	 * internal Game logger.
	 */
	private static final Logger logger = LoggerFactory.getLogger(Game.class);

	/**
	 * Default path for screenshot image files.
	 */
	private static final String DEFAULT_SCREENSHOT_PATH = "screenshots";

	/**
	 * Display scaling factor.
	 */
	float scale = 1.0f;
	/**
	 * Frames per seconds targeted.
	 */
	private float FPS = 60.0f;
	private float fpsDelay = 1000.0f / FPS;

	/**
	 * Frame where to render game image.
	 */
	private JFrame frame;
	// Windows Dimension (Scale factor applied)
	private Dimension screenDim;
	// Play zone dimension
	private Dimension playZone;

	/**
	 * internal Exit request flag.
	 */
	private boolean exit = false;
	/**
	 * internal Pause request flag.
	 */
	private boolean pause = false;

	/**
	 * internal Screenshot request flag. This flag must be set to true to activate
	 * screen shot save action.
	 */
	private boolean requestScreenshot = false;

	/**
	 * Debug Level. - 0 = no debug information - 5 = maximum visual debug
	 * information.
	 */
	private int debug = 2;

	/**
	 * Graphical Buffer to render game objects.
	 */
	private BufferedImage buffer;

	/**
	 * Internal list of objects to be managed by Game.
	 */
	private List<GameObject> objects = new ArrayList<>();

	/**
	 * Main Key Input Listener to manage keyboard (and more !)
	 */
	KeyInputListener kil = null;

	/**
	 * The player object.
	 */
	GameObject player;

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

		/**
		 * request a configuration reload from file.
		 */
		public static void reload() {
			Configuration.instance.load();
		}

	}

	/**
	 * Some utilities to assist rendering tasks.
	 * 
	 * @author Frederic Delorme
	 *
	 */
	static class RenderingUtils {
		/**
		 * internal instance (if needed)
		 */
		public static RenderingUtils instance = new RenderingUtils();

		/**
		 * default path for screenshots.
		 */
		public static final String SCREENSHOT_PATH = File.pathSeparator + "screenshots";
		/**
		 * default path to store image captures.
		 */
		private static String path;
		/**
		 * internal screenshot counter
		 */
		private static int index;

		/**
		 * A default constructor.
		 */
		private RenderingUtils() {
			index = 0;
			path = System.getProperty("user.home");
		}

		/**
		 * Take a screenshot from the image to the default `user.dir`.
		 *
		 * @param image
		 *            image to be saved to disk.
		 */
		public static void screenshot(BufferedImage image, String userPath, String format) {
			if (userPath == null) {
				userPath = path + File.pathSeparator + SCREENSHOT_PATH;
			}
			userPath = String.format("%s/screenshot_%s_%010d.%s", userPath, ("" + System.nanoTime()), index++, format);
			try {
				java.io.File out = new java.io.File(userPath);
				ImageIO.write(image, format.toUpperCase(), out);
			} catch (Exception e) {
				System.err.println("Unable to write screenshot to: " + path);
			}
		}

	}

	/**
	 * Main KeyListener at GameInstance level. It will manage multiple KeyListeners
	 * on the window.
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

		public void add(KeyListener kcb) {
			this.objectsCallBack.add(kcb);
		}

	}

	/**
	 * The class to manage an object managed by the game.
	 * 
	 * @author Frédéric Delorme<frederic.delorme@snapgames.fr>
	 *
	 */
	class GameObject {
		String name = "";

		float ax = 0.0f, ay = 0.0f;
		float dx = 0.0f, dy = 0.0f;
		float x = 0.0f, y = 0.0f, width = 16.0f, height = 16.0f;
		float friction = 0.89f;
		float elasticity = 0.025f;
		float mass = 0.0f;
		float gravity = 0.981f;

		BufferedImage image = null;

		int priority = 0;

		Color debugColor = Color.GREEN;

		float moveAccel = 4.0f;

		Rectangle boundingBox = new Rectangle();

		private List<GameObject> colliders = new ArrayList<>();

		/**
		 * CRete a new Object entity with a <code>name</code> and a position
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
			this.x = x;
			this.y = y;
			updateBB();
		}

		private void updateBB() {
			this.boundingBox.x = (int) x;
			this.boundingBox.y = (int) y;
			this.boundingBox.width = (int) width;
			this.boundingBox.height = (int) height;

		}

		public void setDebugColor(Color debugColor) {
			this.debugColor = debugColor;
		}

		public void update(float elapsed) {
			// compute velocity
			this.dx = (this.ax / this.mass) * elapsed;
			this.dy = ((this.ay / this.mass) + this.mass * (this.gravity)) * elapsed;

			// fix velocity min.
			if (Math.abs(dx) < 0.01) {
				dx = 0.0f;
			}
			if (Math.abs(dy) < 0.01) {
				dy = 0.0f;
			}

			// Apply friction factor (will be changed when collision detection will work).
			this.dx *= this.friction;
			this.dy *= this.friction;

			// compute position
			this.x += this.dx * elapsed;
			this.y += this.dy * elapsed;
			updateBB();

		}

		public void render(Graphics2D g) {
			if (image != null) {
				g.drawImage(image, (int) x, (int) y, null);
			}
			if (debug > 1) {
				g.setColor(debugColor);
				g.drawRect((int) x, (int) y, (int) width, (int) height);
				if (debug > 2) {
					g.setColor(Color.CYAN);
					g.drawLine((int) x, (int) y, (int) (x + dx), (int) (y + dy));
				}
			}
		}

		public void setVelocity(float dx, float dy) {
			this.dx = dx;
			this.dy = dy;
		}

		public void setAcceleration(float ax, float ay) {
			this.ax = ax;
			this.ay = ay;
		}

		public void setMoveStep(float step) {
			this.moveAccel = step;
		}

		public void setPosition(float x, float y) {
			this.x = x;
			this.y = y;
			updateBB();
		}

		public void setFriction(float friction) {
			this.friction = friction;
		}

		public void setMass(float mass) {
			this.mass = mass;
		}

		public void setPriority(int priority) {
			this.priority = priority;
		}

		public void setImage(BufferedImage image) {
			this.image = image;
			this.width = image.getWidth();
			this.height = image.getHeight();
			updateBB();
		}

		public void setSize(int width, int height) {
			this.width = width;
			this.height = height;
			updateBB();
		}

		public void addCollider(GameObject o) {
			this.colliders.add(o);

		}

		public void clearColliderList() {
			this.colliders.clear();
			
		}

	}

	/**
	 * Game Level key listener
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
				break;
			case KeyEvent.VK_F3:
			case KeyEvent.VK_S:
				requestScreenshot = true;
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
	 * A specific Key Listener for the Player object.
	 * 
	 * @author Frédéric Delorme<frederic.delorme@snapgames.fr>
	 *
	 */
	class PlayerKeyInput implements KeyListener {

		GameObject player = null;
		KeyInputListener kil = null;

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
			boolean playerMove = false;

			if (kil.keys[KeyEvent.VK_UP]) {
				player.ay = -player.moveAccel;
				playerMove = true;
			}
			if (kil.keys[KeyEvent.VK_DOWN]) {
				player.ay = player.moveAccel;
				playerMove = true;
			}
			if (kil.keys[KeyEvent.VK_LEFT]) {
				player.ax = -player.moveAccel;
				playerMove = true;
			}

			if (kil.keys[KeyEvent.VK_RIGHT]) {
				player.ax = player.moveAccel;
				playerMove = true;
			}
			if (kil.keys[KeyEvent.VK_SPACE]) {
				player.ax = 0.0f;
				player.ay = 0.0f;
				player.dx = 0.0f;
				player.dy = 0.0f;
			}
			if (!playerMove) {
				player.ax = 0.0f;
				player.ay = 0.0f;
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
		 */
		@Override
		public void keyReleased(KeyEvent e) {
			player.ax = 0.0f;
			player.ay = 0.0f;
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
	 * Initialize all things about game !
	 */
	public Game() {
		GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()[0];

		int width = Configuration.getInteger("screen.width", 320);
		int height = Configuration.getInteger("screen.height", 200);
		scale = Configuration.getFloat("screen.scale", 3);
		debug = Configuration.getInteger("debug.mode", 2);

		screenDim = new Dimension((int) (width * scale), (int) (height * scale));
		playZone = new Dimension(width, height);
		buffer = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
		kil = new KeyInputListener();

		frame = new JFrame(Configuration.get("window.title", "Game !"));

		frame.setMaximumSize(screenDim);
		frame.setMinimumSize(screenDim);
		frame.setPreferredSize(screenDim);
		frame.setFocusable(true);
		frame.setAlwaysOnTop(Configuration.getBoolean("window.onTop", false));
		frame.setUndecorated(!Configuration.getBoolean("window.border", false));

		if (device.isFullScreenSupported() && Configuration.getBoolean("window.fullscreen", false)
				&& !Configuration.getBoolean("window.border", false)) {
			device.setFullScreenWindow(frame);
		} else {
			frame.setResizable(false);
		}

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.addKeyListener(kil);

		frame.setContentPane(this);

		frame.setVisible(true);
		initialize();
	}

	/**
	 * initialize some GameObject's to play with.
	 */
	public void initialize() {
		// add Game key listener
		kil.add(new GameKeyInput());

		player = new GameObject("player", 50, 50);
		player.setFriction(0.25f);
		player.setMoveStep(2.0f);
		player.setPriority(1);
		player.setDebugColor(Color.RED);
		player.setMass(0.5f);
		add(player);

		// add specific game player key listener
		kil.add(new PlayerKeyInput(player, kil));

		for (int i = 0; i < 10; i++) {
			float posX = (float) (Math.random() * screenDim.width / 2);
			float posY = (float) (Math.random() * screenDim.height / 2);
			GameObject enemy = new GameObject("enemy_" + i, posX, posY);
			enemy.setMass(0.20f);
			enemy.setPriority(2 + i);
			enemy.setSize(16, 16);
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
			if (previousTime > 0) {
				elapsed = (currentTime - previousTime) / 10000000.0f;
				if (elapsed < 0) {
					elapsed = 1;
				}
				update(elapsed);
			}
			render(String.format("c:%02d t:%04d fps:%03d", framesCount, timeFrames, realFPS));
			framesCount += 1;
			timeFrames += fpsDelay;
			if (timeFrames >= 1000) {
				realFPS = framesCount;
				framesCount = 0;
				timeFrames = 0;
			}
			wait(fpsDelay - elapsed);
			previousTime = currentTime;
		}
		dispose();
		System.exit(0);
	}

	private void wait(float sleepDuration) {
		if (sleepDuration > fpsDelay) {
			sleepDuration = 1;
		}
		if (sleepDuration > 0) {
			try {
				Thread.sleep((int) (sleepDuration));
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
				o.update((int) elapsed);
				collisionDetection(playZone, o);
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
	private void collisionDetection(Dimension constrainedZone, GameObject object) {
		object.clearColliderList();
		for (GameObject o : objects) {
			//object.debugColor=Color.GREEN;
			if (object.boundingBox.intersects(o.boundingBox) && !o.equals(object)) {
				if (o.name.equals("player")) {
					o.debugColor = Color.RED;
				} else {
					o.debugColor = Color.ORANGE;
				}
				object.addCollider(o);
			}
		}

		if (object.x <= 0) {
			object.ax = 0;
			object.dx = 0;
			object.x = 0;
		}
		if (object.x >= constrainedZone.width - object.width) {
			object.ax = 0;
			object.dx = 0;
			object.x = constrainedZone.width - object.width;
		}
		if (object.y <= 0) {
			object.ay = 0;
			object.dy = 0;
			object.y = 0;
		}
		if (object.y >= constrainedZone.height - object.height) {
			object.ax = 0;
			object.dy = 0;
			object.y = constrainedZone.height - object.height;
		}
	}

	/**
	 * render all the game objects to the buffer.
	 */
	public void render(String fps) {

		Graphics2D g = (Graphics2D) buffer.getGraphics();

		g.setColor(Color.BLACK);
		g.fillRect(0, 0, buffer.getWidth(), buffer.getHeight());

		if (objects != null && objects.size() > 0) {
			for (GameObject o : objects) {
				o.render(g);
			}
		}
		if (debug > 0) {
			g.setColor(Color.ORANGE);
			g.drawString(fps, 10, buffer.getHeight() - 20);
		}

		g.dispose();
		drawToScreen();
	}

	/**
	 * Draw buffer to screen.
	 */
	private void drawToScreen() {
		Graphics2D gbuff = (Graphics2D) frame.getGraphics();
		gbuff.drawImage(buffer, 0, 0, (int) (screenDim.width * scale), (int) (screenDim.height * scale), 0, 0,
				screenDim.width, screenDim.height, null);
		if (buffer != null && requestScreenshot) {
			saveScreenShot(buffer);
			requestScreenshot = false;
		}
		gbuff.dispose();

	}

	/**
	 * save screen to default screenshot path.
	 * 
	 * @param screen
	 *            the BufferedIage to be saved as screenshot.
	 */
	private void saveScreenShot(BufferedImage screen) {
		String scrpath = Game.class.getResource("/").getPath()
				+ Configuration.get("screenshot.path", DEFAULT_SCREENSHOT_PATH);
		File dir = new File(scrpath);
		if (!dir.exists()) {
			dir.mkdir();
			logger.info(String.format("Create the '%s' path", scrpath));
		}
		RenderingUtils.screenshot(screen, scrpath, Configuration.get("screenshot.format", "JPG").toUpperCase());
		logger.info("Write a new screenshot to {} path", scrpath);
	}

	/**
	 * release all resources before quitting.
	 */
	private void dispose() {
		screenDim = null;
		frame = null;
		kil = null;
		player = null;
		buffer = null;
	}

	/**
	 * Add a GameObject to the list of object managed by the Game.
	 * 
	 * @param o
	 *            the GameObject to add to the list.
	 */
	public void add(GameObject o) {
		objects.add(o);
		objects.sort(new Comparator<GameObject>() {
			public int compare(GameObject o1, GameObject o2) {
				return (o1.priority < o2.priority ? -1 : 1);
			};
		});
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Game game = new Game();
		game.run();
	}

}
