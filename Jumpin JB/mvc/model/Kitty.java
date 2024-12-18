package edu.uchicago.gerber._08final.mvc.model;

import edu.uchicago.gerber._08final.mvc.controller.CommandCenter;
import edu.uchicago.gerber._08final.mvc.controller.Game;
import edu.uchicago.gerber._08final.mvc.controller.ImageLoader;
import edu.uchicago.gerber._08final.mvc.controller.SoundLoader;
import lombok.Data;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;

@Data
public class Kitty extends Sprite {

	// ==============================================================
	// FIELDS
	// ==============================================================

	//static fields
	//number of frames that the falcon will be protected after a spawn
	public static final int INITIAL_SPAWN_TIME = 48;
	//number of frames falcon will be protected after consuming a NewShieldFloater
	public static final int MAX_SHIELD = 200;
	public static final int RADIUS = 100;
	private static final int KITTY_SIZE = 127;
	private static final double GRAVITY_PULL = 0.5;

	private Map<BalloonState, Map<ImageState, BufferedImage>> ImageMap;

	public enum ImageState {
		KITTY, // normal
		LEFT_KITTY, //left arrow
	}

	//instance fields (getters/setters provided by Lombok @Data above)
	private int shield;
	private int invisible;
	private int showLevel;
	private int lastBalloonWarning;

	// more enums added:
	// FaceState - the way the kitty is facing
	public enum FaceState {LEFT, RIGHT}
	private FaceState faceState = FaceState.RIGHT;

	// TurnState - the way kitty is actually moving
	public enum TurnState {IDLE, LEFT, RIGHT}
	private TurnState turnState = TurnState.IDLE;

	// BalloonState - the amount of balloons they have
	public enum BalloonState {THREE, TWO, ONE}
	private BalloonState balloonState = BalloonState.THREE;
	private boolean jumping;
	private int ground = Game.DIM.height - 127;

	// ==============================================================
	// CONSTRUCTOR
	// ==============================================================

	public Kitty() {
		super();
		ImageMap = new HashMap<>();
		setTeam(Team.FRIEND);
		setRadius(RADIUS);
		setCenter(new Point(750, 623));

		// 3 balloons
		Map<ImageState, BufferedImage> threeBalloonState = new HashMap<>();
		// loading
		BufferedImage kittyImage = ImageLoader.getImage("/imgs/jb/kitty.png");
		BufferedImage lKittyImage = ImageLoader.getImage("/imgs/jb/leftkitty.png");
		// resizing
		kittyImage = ImageLoader.resize(kittyImage);
		lKittyImage = ImageLoader.resize(lKittyImage);
		// inputting
		threeBalloonState.put(ImageState.KITTY, kittyImage);
		threeBalloonState.put(ImageState.LEFT_KITTY, lKittyImage);
		// finishing map
		ImageMap.put(BalloonState.THREE, threeBalloonState);

		// two balloons
		Map<ImageState, BufferedImage> twoBalloonState = new HashMap<>();
		BufferedImage r2Kitty = ImageLoader.getImage("/imgs/jb/R2.png");
		BufferedImage l2Kitty = ImageLoader.getImage("/imgs/jb/L2.png");
		r2Kitty = ImageLoader.resize(r2Kitty);
		l2Kitty = ImageLoader.resize(l2Kitty);
		twoBalloonState.put(ImageState.KITTY, r2Kitty);
		twoBalloonState.put(ImageState.LEFT_KITTY, l2Kitty);
		ImageMap.put(BalloonState.TWO, twoBalloonState);

		// one balloons
		Map<ImageState, BufferedImage> oneBalloonState = new HashMap<>();
		BufferedImage r1Kitty = ImageLoader.getImage("/imgs/jb/R1.png");
		BufferedImage l1Kitty = ImageLoader.getImage("/imgs/jb/L1.png");
		r1Kitty = ImageLoader.resize(r1Kitty);
		l1Kitty = ImageLoader.resize(l1Kitty);
		oneBalloonState.put(ImageState.KITTY, r1Kitty);
		oneBalloonState.put(ImageState.LEFT_KITTY, l1Kitty);
		ImageMap.put(BalloonState.ONE, oneBalloonState);
	}

	// ==============================================================
	// METHODS
	// ==============================================================
	@Override
	public void move() {
		super.move();
		int maxHeight = 127;

		int centerX = getCenter().x;
		int centerY = getCenter().y;

		if (turnState == TurnState.LEFT) {
			centerX -= 4;
		}
		if (turnState == TurnState.RIGHT) {
			centerX += 4;
		}

		// kitty is bounded to the dimensions - shouldn't wrap around
		if (centerX >= Game.DIM.width - KITTY_SIZE) {
			centerX = Game.DIM.width - KITTY_SIZE;
		} else if (centerX <= KITTY_SIZE) {
			centerX = KITTY_SIZE;
		}

		if (jumping) {
			setDeltaY(getDeltaY() + GRAVITY_PULL);
			centerY = (int) (getCenter().y + getDeltaY());

			// dont go too far above or below the screen
			if (centerY <= maxHeight) {
				centerY = maxHeight;
				if (getDeltaY() < 0) setDeltaY(0);
			}

			if (centerY >= ground) {
				setDeltaY(0);
				centerY = ground;
				jumping = false;
			}
		}
		setCenter(new Point(centerX, centerY));

		if (invisible > 0) invisible--;
		if (shield > 0) shield--;
		//The falcon is a convenient place to decrement the showLevel variable as the falcon
		//move() method is being called every frame (~40ms); and the falcon reference is never null.
		if (showLevel > 0) showLevel--;
		if (lastBalloonWarning > 0) lastBalloonWarning--;
	}

	public void jump() {
		double jumpHeight = -10;
		setDeltaY(jumpHeight);
		jumping = true;
	}

	@Override
	public void draw(Graphics g) {
		// gets current balloonState
		Map<ImageState, BufferedImage> currentMap = ImageMap.get(balloonState);

		if (shield > 0) bubbleUp(g);

		// kitty is default image
		ImageState imageState = ImageState.KITTY;
		if (faceState == FaceState.LEFT){
			imageState = ImageState.LEFT_KITTY;
		}

		//down-cast (widen the aperture of) the graphics object to gain access to methods of Graphics2D
		//and render the raster image according to the image-state
		renderRaster((Graphics2D) g, currentMap.get(imageState));
	}

	// bubble up edited from the GPT generated ShieldFloater
	private void bubbleUp(Graphics g){
		{
			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			Point kittyCenter = this.getCenter();
			int bubbleWidth = (this.getRadius() * 2);

			RadialGradientPaint gradient = new RadialGradientPaint(
					kittyCenter,
					bubbleWidth,
					new float[]{0f, 0.6f, 0.85f, 1f}, // Gradient stops for the bubble
					new Color[]{
							new Color(255, 182, 193, 200), // Soft pink
							new Color(200, 200, 255, 50), // Hot pink
							new Color(255, 255, 255, 100), // Highlight
							new Color(200, 200, 255, 50)   // Outer edge
					}

			);
			g2.setPaint(gradient);
			g2.fillOval(kittyCenter.x - bubbleWidth, kittyCenter.y - bubbleWidth, bubbleWidth * 2, bubbleWidth * 2);
		}
	}

	@Override
	public void removeFromGame(LinkedList<Movable> list) {
		if (CommandCenter.getInstance().isGameOver()) return;

		else if (shield == 0 && invisible == 0)
		{
			if (balloonState == BalloonState.THREE) {
				balloonState = BalloonState.TWO;
			}

			else if (balloonState == BalloonState.TWO) {
				balloonState = BalloonState.ONE;
				setLastBalloonWarning(50);
			}

			decrementBalloonsAndSpawn();
			SoundLoader.playSound("balloonPop.wav");
		}
	}


	public void decrementBalloonsAndSpawn(){

		CommandCenter.getInstance().setNumBalloons(CommandCenter.getInstance().getNumBalloons() -1);

		if (CommandCenter.getInstance().isGameOver()) return;

		setInvisible(Kitty.INITIAL_SPAWN_TIME/2);
		setDeltaX(0);
		setDeltaY(0);
		setRadius(Kitty.RADIUS);
	}
} //end class