package edu.uchicago.gerber._08final.mvc.model;

import edu.uchicago.gerber._08final.mvc.controller.CommandCenter;
import edu.uchicago.gerber._08final.mvc.controller.Game;
import edu.uchicago.gerber._08final.mvc.controller.SoundLoader;

import java.awt.*;
import java.awt.geom.QuadCurve2D;
import java.util.LinkedList;

public class Balloon extends Floater {
    public static final int RADIUS = 30;
    public static final int SPAWN_BALLOON = Game.FRAMES_PER_SECOND * 45;

    public Balloon() {
        super();
        setRadius(RADIUS);
    }

    @Override
    public void removeFromGame(LinkedList<Movable> list) {
        super.removeFromGame(list);
        if (getExpiry() > 0) {
            // check balloon state
            // nothing happens here you dont get extras
            if (CommandCenter.getInstance().getKitty().getBalloonState() == Kitty.BalloonState.THREE) return;

            if (CommandCenter.getInstance().getKitty().getBalloonState() == Kitty.BalloonState.TWO) {
                CommandCenter.getInstance().getKitty().setBalloonState(Kitty.BalloonState.THREE);
            }

            else if (CommandCenter.getInstance().getKitty().getBalloonState() == Kitty.BalloonState.ONE){
                CommandCenter.getInstance().getKitty().setBalloonState(Kitty.BalloonState.TWO);
            }
                CommandCenter.getInstance().setNumBalloons(CommandCenter.getInstance().getNumBalloons() + 1);
                SoundLoader.playSound("balloonInflate.wav");
        }
    }

    // Chat GPT Generated drawing
    public void draw(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Balloon center and radius
        int x = getCenter().x;
        int y = getCenter().y;
        int radius = RADIUS;

        // Draw the balloon gradient
        RadialGradientPaint gradient = new RadialGradientPaint(
                getCenter(),
                radius,
                new float[]{0f, 0.6f, 0.85f, 1f}, // Adjusted color stops for a shiny effect
                new Color[]{
                        new Color(255, 255, 0, 200),  // Bright yellow center
                        new Color(255, 223, 0, 180), // Golden yellow mid-layer
                        new Color(255, 240, 128, 120), // Light yellow highlight
                        new Color(255, 250, 210, 80)  // Faint yellow glow
                }
        );

        g2.setPaint(gradient);
        g2.fillOval(x - radius, y - radius, radius * 2, radius * 2);

        // Optional: Add a white reflection to enhance the "bubble" effect
        g2.setColor(new Color(255, 255, 255, 180)); // Semi-transparent white
        int reflectionRadius = radius / 3; // Reflection size relative to the balloon size
        g2.fillOval(
                x - (reflectionRadius / 2), // Center the reflection horizontally
                y - (radius / 2), // Position slightly above the balloon center
                reflectionRadius,
                reflectionRadius
        );

    // Draw the balloon tie (triangle) below the balloon
        g2.setColor(new Color(255, 223, 0, 200)); // Slightly darker yellow for the tie
        int tieWidth = radius / 3; // Tie width relative to the balloon size
        int tieHeight = radius / 2; // Tie height

        // Define the triangle's points (flipped to point upward)
        int offset = radius / 8; // Move the triangle upward slightly
        int[] xPoints = {
                x - tieWidth / 2, // Left base point
                x + tieWidth / 2, // Right base point
                x                 // Tip of the triangle
        };
        int[] yPoints = {
                y + radius + tieHeight - offset,    // Left and right base points
                y + radius + tieHeight - offset,    // Left and right base points
                y + radius - offset                 // Tip of the triangle moved up
        };

        // Draw the triangle
        g2.fillPolygon(xPoints, yPoints, 3);

        // Draw a slightly curved string
        g2.setColor(new Color(150, 150, 150, 200)); // Light gray for the string
        g2.setStroke(new BasicStroke(2)); // Thin stroke for the string

    // Define the curve points
        // Start point (center of the tie)
        int stringStartY = y + radius + tieHeight - offset; // Bottom of the tie
        // End point directly below the start
        int stringEndY = stringStartY + radius; // Shorter length

        // Control point for a slight curve
        int controlX = x - 10; // Offset slightly to the left
        int controlY = stringStartY + (stringEndY - stringStartY) / 2; // Midway down the string

        // Create and draw the curve
        QuadCurve2D.Double curve = new QuadCurve2D.Double(
                x, stringStartY, // Start point
                controlX, controlY,         // Control point for the curve
                x, stringEndY      // End point
        );
        g2.draw(curve);

    }
}