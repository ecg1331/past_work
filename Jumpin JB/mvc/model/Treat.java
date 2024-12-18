package edu.uchicago.gerber._08final.mvc.model;

import edu.uchicago.gerber._08final.mvc.controller.CommandCenter;
import edu.uchicago.gerber._08final.mvc.controller.SoundLoader;
import java.awt.*;
import java.util.LinkedList;

public class Treat extends Sprite {

    private final int RADIUS = 15;

    public Treat() {
        super();
        setTeam(Team.FLOATER);
        setSpin(somePosNegValue(10));
        setDeltaX(-10);
        setRadius(RADIUS);
        setExpiry(300);
    }

    @Override
    public void removeFromGame(LinkedList<Movable> list) {
        super.removeFromGame(list);
        if (getExpiry() > 0) {
            SoundLoader.playSound("bite.wav");
            CommandCenter.getInstance().setScore(CommandCenter.getInstance().getScore() + 1);
            CommandCenter.getInstance().setPointSinceLast(CommandCenter.getInstance().getPointSinceLast() + 1);
            CommandCenter.getInstance().levelUp();
        }
    }


    // GPT generated (almost all drawings)
    @Override
    public void draw(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Set the color for the treat
        g2.setColor(new Color(189,154,122));

        // Get the treat's position
        int x = getCenter().x;
        int y = getCenter().y;

        // Draw the tail first (so it's behind the body)
        int tailOverlap = RADIUS / 2; // Amount of overlap between tail and body
        int[] xPoints = {x + RADIUS * 2 - tailOverlap, x + RADIUS * 2 - tailOverlap, x + RADIUS};
        int[] yPoints = {y - RADIUS / 2, y + RADIUS / 2, y};
        g2.fillPolygon(xPoints, yPoints, 3);

        // Draw the body (oval) over the tail
        g2.fillOval(x - RADIUS, y - RADIUS / 2, RADIUS * 2, RADIUS);
    }
}
