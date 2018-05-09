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
import java.util.List;

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
 */
public class Game extends JPanel implements KeyListener {

	private static int WIDTH = 640;
	private static int HEIGHT = 400;
	private static float SCALE = 2.0f;

	private float FPS = 30.0f;
	private float fpsDelay = 1000.0f / FPS;

	private JFrame frame;
	private Dimension dim;

	private boolean exit = false;
	private boolean pause = false;
	private int debug = 2;

	private BufferedImage buffer;
	GameObject player;

	private List<GameObject> objects = new ArrayList<>();

	class KeyInputListner implements KeyListener {
		boolean[] keys = new boolean[256];
		List<KeyCallBack> objectsCallBack = new ArrayList<>();

		@Override
		public void keyPressed(KeyEvent e) {
			keys[e.getKeyCode()] = true;
			for (KeyCallBack kcb : objectsCallBack) {
				kcb.KeyPressed((GameObject) kcb, e);
			}
		}

		@Override
		public void keyReleased(KeyEvent e) {
			keys[e.getKeyCode()] = true;
			for (KeyCallBack kcb : objectsCallBack) {
				kcb.KeyReleased((GameObject) kcb, e);

			}
		}

		@Override
		public void keyTyped(KeyEvent e) {
			for (KeyCallBack kcb : objectsCallBack) {
				kcb.KeyPressed((GameObject) kcb, e);

			}

		}

		public void add(KeyCallBack kcb) {
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

		float x = 0, y = 0, width = 16, height = 16;
		float dx = 0, dy = 0;
		float friction = 0.89f;

		BufferedImage image = null;

		int priority = 0;

		Color debugColor = Color.GREEN;

		public float moveVelocity = 4.0f;

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

		public void setVelocity(float dx, float dy) {
			this.dx = dx;
			this.dy = dy;
		}

		public void setDebugColor(Color debugColor) {
			this.debugColor = debugColor;
		}

		public void update(float elapsed) {
			this.x += this.dx * elapsed;
			this.y += this.dy * elapsed;
			dx *= friction;
			dy *= friction;
			if (dx < 0.1) {
				dx = 0.0f;
			}
			if (dy < 0.1) {
				dy = 0.0f;
			}
		}

		public void setPosition(float x, float y) {
			this.x = x;
			this.y = y;
		}

		public void setPriority(int priority) {
			this.priority = priority;
		}

		public void setImage(BufferedImage image) {
			this.image = image;
			this.width = image.getWidth();
			this.height = image.getHeight();
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

	}

	class PlayerKeyInput implements KeyCallBack {

		GameObject player = null;
		
		public PlayerKeyInput(GameObject o) {
			player = o;
		}

		@Override
		public void KeyPressed(GameObject go, KeyEvent e) {
			

		}

		@Override
		public void KeyReleased(GameObject go, KeyEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void KeyTyped(GameObject go, KeyEvent e) {
			// TODO Auto-generated method stub

		}

	}

	/**
	 * Initialize all things about game !
	 */
	public Game() {
		dim = new Dimension((int) (WIDTH * SCALE), (int) (HEIGHT * SCALE));
		buffer = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_4BYTE_ABGR);

		frame = new JFrame("Hello Game !");

		frame.setMaximumSize(dim);
		frame.setMinimumSize(dim);
		frame.setPreferredSize(dim);
		frame.setResizable(false);

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.addKeyListener(this);

		frame.setContentPane(this);

		frame.setVisible(true);
		initialize();
	}

	/**
	 * initialize some GameObject's to play with.
	 */
	public void initialize() {
		player = new GameObject("player", 50, 50);
		player.setPriority(1);
		player.setDebugColor(Color.RED);
		add(player);
		for (int i = 0; i < 10; i++) {
			float posX = (float) (Math.random() * WIDTH / 2);
			float posY = (float) (Math.random() * HEIGHT / 2);
			GameObject enemy = new GameObject("enemy_" + i, posX, posY);
			player.setPriority(2 + i);
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
			render(String.format("c:%2d t:%4d fps:%3d", framesCount, timeFrames, realFPS));
			framesCount += 1;
			timeFrames += elapsed;
			if (timeFrames > 1000) {
				realFPS = framesCount;
				framesCount = 0;
				timeFrames = 0;
			}
			wait(elapsed);
			previousTime = currentTime;
		}
		System.exit(0);
	}

	private void wait(float elapsed) {
		if (elapsed < 0) {
			elapsed = 1;
		}
		try {
			Thread.sleep((int) (fpsDelay - elapsed));
		} catch (InterruptedException e) {
			System.err.println("unable to wait !!");
			System.exit(-1);
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
				if (o.x < 0) {
					o.x = 0;
				}
				if (o.y < 0) {
					o.y = 0;
				}
				if (o.x > HEIGHT - o.height) {
					o.x = HEIGHT - o.height;
				}
				if (o.y > WIDTH - o.width) {
					o.y = WIDTH - o.width;
				}
			}
		}
	}

	/**
	 * render all the game objects to the buffer.
	 */
	public void render(String fps) {

		Graphics2D g = (Graphics2D) buffer.getGraphics();

		g.setColor(Color.BLACK);
		g.fillRect(0, 0, WIDTH, HEIGHT);

		if (objects != null && objects.size() > 0) {
			for (GameObject o : objects) {
				o.render(g);
			}
		}
		if (debug > 0) {
			g.setColor(Color.ORANGE);
			g.drawString(fps, 10, HEIGHT - 20);
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
	 */
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_UP:
			player.dy = -player.moveVelocity;
			break;
		case KeyEvent.VK_DOWN:
			player.dy = player.moveVelocity;
			break;
		case KeyEvent.VK_LEFT:
			player.dx = -player.moveVelocity;
			break;
		case KeyEvent.VK_RIGHT:
			player.dx = player.moveVelocity;
			break;
		case KeyEvent.VK_SPACE:
			player.dx = 0.0f;
			player.dy = 0.0f;
			break;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
	 */
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
	public void keyTyped(KeyEvent arg0) {

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Game game = new Game();
		game.run();
	}

}
