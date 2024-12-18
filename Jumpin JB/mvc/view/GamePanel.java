package edu.uchicago.gerber._08final.mvc.view;

import edu.uchicago.gerber._08final.mvc.controller.CommandCenter;
import edu.uchicago.gerber._08final.mvc.controller.Game;
import edu.uchicago.gerber._08final.mvc.model.*;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


public class GamePanel extends Panel {

    // ==============================================================
    // FIELDS
    // ==============================================================
    final Font fontNormal = new Font("SansSerif", Font.BOLD, 12);
    private final Font fontBig = new Font("SansSerif", Font.BOLD + Font.ITALIC, 36);
    private final DecimalFormat decimalFormat = new DecimalFormat("#,###");
    private FontMetrics fontMetrics;
    private int fontHeight;

    //used for double-buffering
    private Image imgOff;
    private Graphics grpOff;

    // ==============================================================
    // CONSTRUCTOR
    // ==============================================================

    public GamePanel(Dimension dim) {

        GameFrame gameFrame = new GameFrame();

        gameFrame.getContentPane().add(this);
        gameFrame.pack();
        initFontInfo();
        gameFrame.setSize(dim);
        gameFrame.setTitle("Jumpin' JB");
        gameFrame.setResizable(false);
        gameFrame.setVisible(true);
        setFocusable(true);
    }


    // ==============================================================
    // METHODS
    // ==============================================================

    private void drawFalconStatus(final Graphics graphics) {

        graphics.setColor(Color.black);
        graphics.setFont(new Font("SansSerif", Font.BOLD, 20));
        final int OFFSET_LEFT = 220;


//        draw the level upper-right corner
        String levelText = "Level " + CommandCenter.getInstance().getLevel();
        graphics.drawString(levelText, Game.DIM.width - OFFSET_LEFT, fontHeight + 10); //upper-right corner
        graphics.drawString("Score : " + decimalFormat.format(CommandCenter.getInstance().getScore()),
                Game.DIM.width - OFFSET_LEFT,
                fontHeight + 10 * 3);

        //build the status string array with possible messages in middle of screen
        List<String> statusArray = new ArrayList<>();
        if (CommandCenter.getInstance().getKitty().getShowLevel() > 0)
            statusArray.add("Level Up! " + levelText);
        if (CommandCenter.getInstance().getKitty().getLastBalloonWarning() > 0) statusArray.add("Careful! Last Balloon!");
        // draw the statusArray strings to middle of screen
        if (!statusArray.isEmpty())
            displayTextOnScreen(graphics, statusArray.toArray(new String[0]));

    }

    @Override
    public void update(Graphics g) {

        // The following "off" vars are used for the off-screen double-buffered image.
        imgOff = createImage(Game.DIM.width, Game.DIM.height);
        //get its graphics context
        grpOff = imgOff.getGraphics();

        Color skyBlue = new Color(135, 206, 235);
        grpOff.setColor(skyBlue);
        grpOff.fillRect(0, 0, Game.DIM.width, Game.DIM.height);

        grpOff.setColor(Color.WHITE);
        grpOff.setFont(fontNormal);
        if (CommandCenter.getInstance().isGameOver()) {
            displayTextOnScreen(grpOff,
                    "GAME OVER",
                    "catch the cat treats, avoid the thumbtacks,",
                    "catch the bubbles to get a shield",
                    "if you pop a balloon, try to catch another!",
                    "use the arrow keys to move",
                    "hit the space bar to meow",
                    "'S' to Start",
                    "'P' to Pause",
                    "'Q' to Quit",
                    "'M' to toggle music"

            );
        } else if (CommandCenter.getInstance().isPaused()) {

            displayTextOnScreen(grpOff, "Game Paused");

        }

        //playing and not paused!
        else {
            moveDrawMovables(grpOff,
                    CommandCenter.getInstance().getMovDebris(),
                    CommandCenter.getInstance().getMovFloaters(),
                    CommandCenter.getInstance().getMovFoes(),
                    CommandCenter.getInstance().getMovFriends());


            drawFalconStatus(grpOff);

        }

        //after drawing all the movables or text on the offscreen-image, copy it in one fell-swoop to graphics context
        // of the game panel, and show it for ~40ms. If you attempt to draw sprites directly on the gamePanel, e.g.
        // without the use of a double-buffered off-screen image, you will see flickering.
        g.drawImage(imgOff, 0, 0, this);
    }


    //this method causes all sprites to move and draw themselves. This method takes a variable number of teams.
    @SafeVarargs
    private final void moveDrawMovables(final Graphics g, List<Movable>... teams) {
        for (List<Movable> team : teams) {
            for (Movable mov : team) {
                mov.move();
                mov.draw(g);
            }
        }
    }

    private void initFontInfo() {
        Graphics g = getGraphics();            // get the graphics context for the panel
        g.setFont(fontNormal);                        // take care of some simple font stuff
        fontMetrics = g.getFontMetrics();
        fontHeight = fontMetrics.getHeight();
        g.setFont(fontBig);                    // set font info
    }


    // This method draws some text to the middle of the screen
    private void displayTextOnScreen(final Graphics graphics, String... lines) {

        //AtomicInteger is safe to pass into a stream
        final AtomicInteger spacer = new AtomicInteger(0);
        Arrays.stream(lines)
                .forEach(str ->
                            graphics.drawString(str, (Game.DIM.width - fontMetrics.stringWidth(str)) / 2,
                                    Game.DIM.height / 4 + fontHeight + spacer.getAndAdd(40))

                );

    }
}
