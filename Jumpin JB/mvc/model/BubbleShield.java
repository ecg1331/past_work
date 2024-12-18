package edu.uchicago.gerber._08final.mvc.model;

import edu.uchicago.gerber._08final.mvc.controller.CommandCenter;
import edu.uchicago.gerber._08final.mvc.controller.Game;
import edu.uchicago.gerber._08final.mvc.controller.SoundLoader;

import java.awt.*;
import java.util.LinkedList;

public class BubbleShield extends Floater {
	//spawn every 25 seconds
	public static final int RADIUS = 30;
	public static final int SPAWN_BUBBLE_SHIELD = Game.FRAMES_PER_SECOND * 25; /// edited for debug - og 25

	public BubbleShield()
	{
		super();
		setRadius(RADIUS);
	}

	// GPT generated
	public void draw(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		RadialGradientPaint gradient = new RadialGradientPaint(
				getCenter(),
				RADIUS,
				new float[]{0f, 0.6f, 0.85f, 1f}, // Adjusted color stops for a shiny effect
				new Color[]{
						new Color(255, 182, 193, 200), // Soft pink center (semi-transparent)
						new Color(255, 105, 180, 150), // Hot pink mid-layer
						new Color(255, 255, 255, 100), // Highlight layer for shine
						new Color(200, 200, 255, 50)   // Faint bluish outer edge for realism
				}
		);
		g2.setPaint(gradient);
		g2.fillOval(getCenter().x - RADIUS, getCenter().y - RADIUS, RADIUS * 2, RADIUS * 2);

	// Optional: Add a white reflection to enhance the "bubble" effect
		g2.setColor(new Color(255, 255, 255, 180)); // Semi-transparent white
		int reflectionRadius = RADIUS / 3; // Reflection size relative to the bubble size
		g2.fillOval(
				getCenter().x - reflectionRadius,
				getCenter().y - (RADIUS / 2), // Slightly offset reflection
				reflectionRadius,
				reflectionRadius
		);
	}

	@Override
	public void removeFromGame(LinkedList<Movable> list) {
		super.removeFromGame(list);
		//if getExpiry() > 0, then this remove was the result of a collision, rather than natural mortality
		if (getExpiry() > 0) {
			SoundLoader.playSound("bubbleshield.wav");
		    CommandCenter.getInstance().getKitty().setShield(Kitty.MAX_SHIELD);
	   }
	}
}
