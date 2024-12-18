package edu.uchicago.gerber._08final.mvc.model;
import edu.uchicago.gerber._08final.mvc.controller.CommandCenter;
import edu.uchicago.gerber._08final.mvc.controller.Game;
import lombok.Data;

import java.awt.*;
import java.util.LinkedList;

@Data
public class Cloud implements Movable {

    private int x;
    private int y;
    private Point center;
    int upperLimit;
    int lowerLimit;
    private boolean up;
    private static Cloud[] clouds;

    public Cloud(int x, int y) {
        this.x = x;
        this.y = y;
        this.upperLimit = y - 4;
        this.lowerLimit = y + 4;
        this.center = new Point(x, y);
    }

    public static void createClouds() {
        if (clouds == null) {
            clouds = new Cloud[5];
            clouds[0] = new Cloud(0, 20);
            clouds[1] = new Cloud(400, 60);
            clouds[2] = new Cloud(800, 100);
            clouds[3] = new Cloud(1200, 50);
            clouds[4] = new Cloud(-400, 75);
        }
    }

    static { // this static method was GPTs idea
        createClouds();
    }

    // GPT helped generate code (the correct placement of x and y) and I edited the width and heights to get correct look
    public void drawCloud(Graphics g){
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setColor(Color.WHITE);
        g2d.fillOval(center.x, center.y, 100, 60);
        g2d.fillOval(center.x + 50, center.y - 20, 80, 50);
        g2d.fillOval(center.x + 100, center.y, 120, 70);
        g2d.fillOval(center.x + 150, center.y - 10, 90, 60);
        g2d.fillOval(center.x + 200, center.y, 70, 40);
    }

    @Override
    public void draw(Graphics g) {
        for (Cloud cloud : clouds) {
            cloud.drawCloud(g);
        }
    }

    @Override
    public Point getCenter() {
        return center;
    }

    @Override
    public int getRadius() {return 1;}

    @Override
    public Team getTeam() {return Team.DEBRIS;}

    @Override
    public void move() {
        int level = CommandCenter.getInstance().getLevel();
        int speed = (int) Math.sqrt(level);
        for (Cloud cloud : clouds) {
        cloud.center.x += speed;

        if (cloud.up) {
            cloud.center.y -= speed;
            if (cloud.center.y <= cloud.upperLimit) {
                cloud.up = false;
            }
        } else {
            cloud.center.y += speed;
            if (cloud.center.y >= cloud.lowerLimit) {
                cloud.up = true;
            }

            if (cloud.center.x > Game.DIM.width) {
                cloud.center.x = -400;
            }
        }
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