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
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.swing.JPanel;

import fr.snapgames.game.singleclassgame.core.audio.SoundControl;
import fr.snapgames.game.singleclassgame.core.collision.CollisionManager;
import fr.snapgames.game.singleclassgame.core.config.Configuration;
import fr.snapgames.game.singleclassgame.core.config.VersionTracker;
import fr.snapgames.game.singleclassgame.core.entity.GameObject;
import fr.snapgames.game.singleclassgame.core.entity.World;
import fr.snapgames.game.singleclassgame.core.graphics.Window;
import fr.snapgames.game.singleclassgame.core.input.KeyInputListener;
import fr.snapgames.game.singleclassgame.core.math.Vector2D;
import fr.snapgames.game.singleclassgame.core.resources.ResourceManager;
import fr.snapgames.game.singleclassgame.core.state.GameStateManager;
import fr.snapgames.game.singleclassgame.sample.GameKeyInput;
import fr.snapgames.game.singleclassgame.sample.SampleState;

import fr.snapgames.game.singleclassgame.audio.SoundControl;

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

	// private static final Logger logger = LoggerFactory.getLogger(Game.class);

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
	public Dimension dim;
	// Play zone dimension
	public Dimension playZone;

	/**
	 * Internal flag to request EXIT.
	 */
	public boolean exit = false;
	/**
	 * internal debug level to track things id <code>debug</code>>0.
	 */
	public int debug = 2;
	/**
	 * rendering buffer
	 */
	private BufferedImage buffer;

	/**
	 * Game objects to be managed.
	 */
	public List<GameObject> objects = new ArrayList<>();

	/**
	 * The main input key listener.
	 */
	public KeyInputListener kil = null;

	/**
	 * the main resource manager to try and share things.
	 */
	public ResourceManager resourceMgr;

	/**
	 * The world object contains physic constrains for the physic engine system.
	 */
	public World world;

	/**
	 * The window where to draw the game view.
	 */
	private Window window;

	/**
	 * This is the collision manager.
	 */
	public CollisionManager collisionMgr;

	/**
	 * This is the sound controller.
	 */
	public SoundControl soundControl;

	public GameStateManager gsm;

	/**
	 * A flag to request a randomization of the enemies moves.
	 */
	public boolean randomizeEnemies;

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

		Configuration.getInstance();

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

		// Set the default World parameters.
		world = new World(new Vector2D("gravity", 0.0f, -0.981f));
		// Initialize ResourceManager
		resourceMgr = new ResourceManager();

		collisionMgr = new CollisionManager();

		soundControl = SoundControl.getInstance();

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
		kil.register(new GameKeyInput(this));

		// Define Collision manage playground.
		collisionMgr.setDimension(playZone);
		gsm = new GameStateManager();

		SampleState samp = new SampleState(this);
		samp.initialize(this);
		gsm.add(samp);

		gsm.start(this, "SampleState");

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
				gsm.input(this);
				elapsed = (currentTime - previousTime) / 10000000.0f;
				if (elapsed < 0.0f) {
					elapsed = 1.0f;
				}
				gsm.update(this, elapsed);
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
			// TODO call gsm.current.update()
		}
		collisionMgr.update(this, elapsed);
		if (world != null && world.activeCam != null) {
			world.activeCam.updatePhysic(elapsed);
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

		gsm.render(this, g);

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
