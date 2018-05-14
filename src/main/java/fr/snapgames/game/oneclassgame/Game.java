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
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

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

	private static int WIDTH = 640;
	private static int HEIGHT = 400;
	private static float SCALE = 2.0f;

	private float FPS = 30.0f;
	private float fpsDelay = 1000.0f / FPS;

	private JFrame frame;
	// Windows Dimension (Scale factor applied)
	private Dimension dim;
	// Play zone dimension
	private Dimension playZone;

	private boolean exit = false;
	private boolean pause = false;
	private int debug = 2;

	private BufferedImage buffer;
	private List<GameObject> objects = new ArrayList<>();

	KeyInputListener kil = null;

	GameObject player;
	private ResourceManager resourceMgr;

	/**
	 * the ResourceUnknownException class is thrown when a resource is not found.
	 * 
	 * @author Frédéric Delorme <frederic.delorme@snapgames.fr>
	 *
	 */
	public class ResourceUnknownException extends Exception {

		/**
		 * @param message
		 */
		public ResourceUnknownException(String message) {
			super(message);
		}

	}

	/**
	 * the ResourceManager class is ....
	 * 
	 * @author Frédéric Delorme <frederic.delorme@snapgames.fr>
	 *
	 */
	public class ResourceManager {
		private Map<String, Object> objects = new HashMap<>();

		public void addResource(String name, String path) {
			if (path.contains(".png")) {
				try {
					BufferedImage image = ImageIO.read(this.getClass().getResourceAsStream(name));
					objects.put(name, image);
				} catch (Exception e) {
					System.err.println(String.format("Unable to find %s and store resource as %s.", path, name));
					System.exit(-1);
				}
			}

		}

		public BufferedImage getImage(String name) throws ResourceUnknownException {
			if (objects.containsKey(name)) {
				return (BufferedImage) objects.get(name);
			} else {
				throw new ResourceUnknownException(String.format("Unknown resource named %s", name));
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
		float x = 0.0f, y = 0.0f;

		float width = 16.0f, height = 16.0f;

		float gravity = 0.981f;

		float moveVelocity = 4.0f;
		float mass = 0.89f;
		float friction = 0.92f;
		float elasticity = 0.60f;

		BufferedImage image = null;

		int priority = 0;

		Color debugColor = Color.GREEN;

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
		}

		public void update(float elapsed) {
			this.dx += this.ax * friction * elapsed;
			this.dy += (((this.ay * friction) + this.gravity) / mass) * elapsed;
			this.x += this.dx * elapsed;
			this.y += this.dy * elapsed;

			if (Math.abs(dx) < 0.01f) {
				dx = 0.0f;
			}
			if (Math.abs(dy) < 0.01f) {
				dy = 0.0f;
			}

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

		public void setAcceleration(float ax, float ay) {
			this.ax = ax;
			this.ay = ay;
		}

		public void setVelocity(float dx, float dy) {
			this.dx = dx;
			this.dy = dy;
		}

		public void setPosition(float x, float y) {
			this.x = x;
			this.y = y;
		}

		public void setMass(float factor) {
			this.mass = factor;
		}

		public void setFriction(float factor) {
			this.friction = factor;
		}

		public void setElasticity(float factor) {
			this.elasticity = factor;
		}

		public void setDebugColor(Color debugColor) {
			this.debugColor = debugColor;
		}

		public void setPriority(int priority) {
			this.priority = priority;
		}

		public void setImage(BufferedImage image) {
			this.image = image;
			this.width = image.getWidth();
			this.height = image.getHeight();
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
		boolean movePlayer = false;

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
				movePlayer = true;
				player.ay = -player.moveVelocity;
			}
			if (kil.keys[KeyEvent.VK_DOWN]) {
				movePlayer = true;
				player.ay = player.moveVelocity;
			}
			if (kil.keys[KeyEvent.VK_LEFT]) {
				movePlayer = true;
				player.ax = -player.moveVelocity;
			}

			if (kil.keys[KeyEvent.VK_RIGHT]) {
				movePlayer = true;
				player.ax = player.moveVelocity;
			}

			if (kil.keys[KeyEvent.VK_SPACE]) {
				movePlayer = true;
				player.ax = 0.0f;
				player.ay = 0.0f;
			}

			if (!movePlayer) {
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
			movePlayer = false;

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
		dim = new Dimension((int) (WIDTH * SCALE), (int) (HEIGHT * SCALE));
		playZone = new Dimension(WIDTH, HEIGHT);
		buffer = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_4BYTE_ABGR);
		kil = new KeyInputListener();
		resourceMgr = new ResourceManager();

		frame = new JFrame("Hello Game !");

		frame.setMaximumSize(dim);
		frame.setMinimumSize(dim);
		frame.setPreferredSize(dim);
		frame.setResizable(false);

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
		resourceMgr.addResource("playerBall", "res/images/blue-bouncing-ball-64x64.png");
		
		player = new GameObject("player", 50, 50);
		try {
			player.setImage(resourceMgr.getImage("playerBall"));
		} catch (ResourceUnknownException e) {
			System.err.println("Unable to retrieve the playerBall resource");
			System.exit(-1);
		}
		player.moveVelocity = 2.0f;
		player.setMass(0.100f);
		player.setFriction(0.92f);
		player.setElasticity(0.42f);

		player.setPriority(1);
		player.setDebugColor(Color.RED);
		add(player);

		// add specific game player key listener
		kil.add(new PlayerKeyInput(player, kil));

		for (int i = 0; i < 10; i++) {
			float posX = (float) (Math.random() * WIDTH / 2);
			float posY = (float) (Math.random() * HEIGHT / 2);
			GameObject enemy = new GameObject("enemy_" + i, posX, posY);
			enemy.setPriority(2 + i);
			enemy.setMass(10f);
			enemy.setFriction(0.01f);
			enemy.setElasticity(0.0f);
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
			if (previousTime > 0.0f) {
				elapsed = (currentTime - previousTime) / 10000000.0f;
				if (elapsed < 0.0f) {
					elapsed = 1.0f;
				}
				update(elapsed);
			}
			render(String.format("c:%02d t:%04d fps:%03d", framesCount, timeFrames, realFPS));
			framesCount += 1;
			timeFrames += elapsed;
			if (timeFrames > 1000) {
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
				o.update((int) elapsed);
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

		if (object.x < 0) {
			object.x = 0;
		}
		if (object.y < 0) {
			object.y = 0;
		}
		if (object.x > constrainedZone.width - object.width) {
			object.x = constrainedZone.width - object.width;
		}
		if (object.y > constrainedZone.height - object.height) {
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
		gbuff.drawImage(buffer, 0, 0, (int) (WIDTH * SCALE), (int) (HEIGHT * SCALE), 0, 0, WIDTH, HEIGHT, null);
		gbuff.dispose();
	}

	/**
	 * release all resources before quitting.
	 */
	private void dispose() {
		dim = null;
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
