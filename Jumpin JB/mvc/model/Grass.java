package edu.uchicago.gerber._08final.mvc.model;

import edu.uchicago.gerber._08final.mvc.controller.CommandCenter;
import edu.uchicago.gerber._08final.mvc.controller.Game;
import lombok.Data;

import java.awt.*;
import java.util.LinkedList;


@Data
public class Grass implements Movable {
    int y = Game.DIM.height - 75;
    int x = 750;
    private static final int GRASS_SPACING = 400;

    public void drawGrass(Graphics g, int x, Color color) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(color);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.fillOval(x, y, 600, 350);
    }

    @Override
    public void draw(Graphics g) {
        Color grass = (new Color(95, 195, 20));
        drawGrass(g, this.x, grass);
        drawGrass(g, this.x - 4 * GRASS_SPACING, grass);
        drawGrass(g, this.x - 3 * GRASS_SPACING, grass);
        drawGrass(g, this.x - 2 * GRASS_SPACING, grass);
        drawGrass(g, this.x - 1 * GRASS_SPACING, grass);
        drawGrass(g, this.x + 1 * GRASS_SPACING, grass);
        drawGrass(g, this.x + 2 * GRASS_SPACING, grass);


    }

    @Override
    public Point getCenter() {
        return new Point(this.x, this.y);
    }


    @Override
    public int getRadius() {
        return 1;
    }

    @Override
    public Team getTeam() {
        return Team.DEBRIS;
    }

    @Override
    public void move() {
        int level = CommandCenter.getInstance().getLevel();
        int speed = (int) Math.sqrt(level);
        this.x += speed;
        if (this.x > 1150) {
            this.x = 750;
        }
        }

    @Override
    public void addToGame(LinkedList<Movable> list) {
        list.add(this);
    }

    @Override
    public void removeFromGame(LinkedList<Movable> list) {
        list.remove(this);
    }
}


