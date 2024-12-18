package edu.uchicago.gerber._08final.mvc.model;

import lombok.Data;
import java.awt.*;

@Data
public class Tack extends Sprite {

    private final int RADIUS = 20;
    private static Tack previousTack = null;
    private final int SPACING = 200;

    public Tack() {
        super();
        setTeam(Team.FOE);
        setColor(Color.LIGHT_GRAY);
        setSpin(somePosNegValue(10));
        setDeltaX(-10);
        setRadius(RADIUS);
        setExpiry(300);
        previousTack = this; // static, so it knows the previous tack
    }

    public static Point getPreviousTack() {
        return previousTack != null ? previousTack.getCenter() : null;
    }

    // GPT
    @Override
    public void draw(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Point center = getCenter();
        int size = RADIUS; // Use RADIUS for proportional size

        // Define the triangle pointing left
        Polygon triangle = new Polygon(
                new int[]{center.x, center.x + size, center.x + size}, // X points
                new int[]{center.y, center.y - size / 2, center.y + size / 2}, // Y points
                3 // Number of points
        );

        // Create a gradient to simulate metallic shine
        GradientPaint metallicGradient = new GradientPaint(
                center.x, center.y - size / 2, new Color(192, 192, 192), // Start color (light silver)
                center.x + size, center.y + size / 2, new Color(96, 96, 96), // End color (dark silver)
                true // Cyclic gradient
        );
        g2.setPaint(metallicGradient);
        g2.fill(triangle);

        // Optional: Add a subtle highlight for extra shine
        g2.setColor(new Color(255, 255, 255, 120)); // Semi-transparent white
        g2.drawLine(center.x + size / 2, center.y - size / 4, center.x + size, center.y);

        // Optional: Add a border for clarity
        g2.setColor(Color.BLACK);
        g2.draw(triangle);
    }
}


