package edu.uchicago.gerber._08final.mvc.controller;

import edu.uchicago.gerber._08final.mvc.model.*;
import edu.uchicago.gerber._08final.mvc.view.GamePanel;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.LinkedList;
import java.util.Random;

// ===============================================
// == This Game class is the CONTROLLER
// ===============================================

public class Game implements Runnable, KeyListener {

    // ===============================================
    // FIELDS
    // ===============================================

    public static final Dimension DIM = new Dimension(1500, 750); //the dimension of the game-screen. //changed to 550 for debug
    private final GamePanel gamePanel;
    public static final Random R = new Random();
    public final static int ANIMATION_DELAY = 40; // milliseconds between frames
    public final static int FRAMES_PER_SECOND = 1000 / ANIMATION_DELAY;
    private final Thread animationThread;
    public final int MAX_SPAWN_INTERVAL = 20;

    private final static int x =  Game.DIM.width - 100;
    private final static int minY = 50;
    private final static int maxY = Game.DIM.height - 50;

    //key-codes
    private static final int
            PAUSE = 80,     // p key
            QUIT = 81,      // q key
            LEFT = 37,      // move left
            RIGHT = 39,     // move right
            MEOW = 32,      // space key
            UP = 38,        // jump
            START = 83,     // s key
            MUTE = 77;     // m-key mute



    // ===============================================
    // ==CONSTRUCTOR
    // ===============================================

    public Game() {

        gamePanel = new GamePanel(DIM);
        gamePanel.addKeyListener(this); //Game object implements KeyListener
        //fire up the animation thread
        animationThread = new Thread(this); // pass the animation thread a runnable object, the Game object
        //set as daemon so as not to block the main thread from exiting
        animationThread.setDaemon(true);
        animationThread.start();
    }


    // ===============================================
    // ==METHODS
    // ===============================================

    public static void main(String[] args) {
        //typical Swing application start; we pass EventQueue a Runnable object.
        EventQueue.invokeLater(Game::new);
    }

    // Game implements runnable, and must have run method
    @Override
    public void run() {
        // lower animation thread's priority, thereby yielding to the 'Event Dispatch Thread' or EDT
        // thread which listens to keystrokes
        animationThread.setPriority(Thread.MIN_PRIORITY);

        // and get the current time
        long startTime = System.currentTimeMillis();

        // this thread animates the scene
        while (Thread.currentThread() == animationThread) {

            //this call will cause all movables to move() and draw() themselves every ~40ms
            // see GamePanel class for details
            gamePanel.update(gamePanel.getGraphics());
            checkCollisions();
            checkFloaters();
            //this method will execute addToGame() and removeFromGame() callbacks on Movable objects
            processGameOpsQueue();
            //keep track of the frame for development purposes
            CommandCenter.getInstance().incrementFrame();

            // surround the sleep() in a try/catch block
            // this simply controls delay time between
            // the frames of the animation
            try {
                // The total amount of time is guaranteed to be at least ANIMATION_DELAY long.  If processing (update)
                // between frames takes longer than ANIMATION_DELAY, then the difference between startTime -
                // System.currentTimeMillis() will be negative, then zero will be the sleep time
                startTime += ANIMATION_DELAY;

                Thread.sleep(Math.max(0,
                        startTime - System.currentTimeMillis()));
            } catch (InterruptedException e) {
                // do nothing (bury the exception), and just continue, e.g. skip this frame -- no big deal
            }
        } // end while
    } // end run

    private void checkFloaters() {
        if (CommandCenter.getInstance().isPaused()) return;
        spawnTacks(CommandCenter.getInstance().getLevel());
        spawnTreats(CommandCenter.getInstance().getLevel());
        spawnBubbleShield();
        spawnBalloon();
    }

    /*
    TODO The following two methods are an example of the Command design pattern. This approach involves deferring
    mutations to collections (linked lists of Movables) while iterating over them, and then processing the mutations
    later (in the processGameOpsQueue() method below). The Command design pattern decouples the request for an
    operation from the  execution of the operation itself. We do this because mutating a data structure while iterating it
    is dangerous and may lead to null-pointer or array-index-out-of-bounds exceptions, or other erroneous behavior.
     */

    private void checkCollisions() {
        //This has order-of-growth of O(FOES * FRIENDS)
        Point pntFriendCenter, pntFoeCenter;
        int radFriend, radFoe;
        for (Movable movFriend : CommandCenter.getInstance().getMovFriends()) {
            for (Movable movFoe : CommandCenter.getInstance().getMovFoes()) {

                pntFriendCenter = movFriend.getCenter();
                pntFoeCenter = movFoe.getCenter();
                radFriend = movFriend.getRadius();
                radFoe = movFoe.getRadius();

                //detect collision
                if (pntFriendCenter.distance(pntFoeCenter) < (radFriend + radFoe)) {
                    //enqueue the friend
                    CommandCenter.getInstance().getOpsQueue().enqueue(movFriend, GameOp.Action.REMOVE);
                    //enqueue the foe
                    CommandCenter.getInstance().getOpsQueue().enqueue(movFoe, GameOp.Action.REMOVE);
                }
            }//end inner for
        }//end outer for

        //check for collisions between falcon and floaters. Order of growth of O(FLOATERS)
        Point pntFalCenter = CommandCenter.getInstance().getKitty().getCenter();
        int radFalcon = CommandCenter.getInstance().getKitty().getRadius();

        Point pntFloaterCenter;
        int radFloater;
        for (Movable movFloater : CommandCenter.getInstance().getMovFloaters()) {
            pntFloaterCenter = movFloater.getCenter();
            radFloater = movFloater.getRadius();
            //detect collision
            if (pntFalCenter.distance(pntFloaterCenter) < (radFalcon + radFloater)) {
                //enqueue the floater
                CommandCenter.getInstance().getOpsQueue().enqueue(movFloater, GameOp.Action.REMOVE);
            }//end if
        }//end for

    }//end meth


    //This method adds and removes movables to/from their respective linked-lists.
    private void processGameOpsQueue() {

        //deferred mutation: these operations are done AFTER we have completed our collision detection to avoid
        // mutating the movable linkedlists while iterating them above.
        while (!CommandCenter.getInstance().getOpsQueue().isEmpty()) {

            GameOp gameOp = CommandCenter.getInstance().getOpsQueue().dequeue();
            //given team, determine which linked-list this object will be added-to or removed-from
            LinkedList<Movable> list;
            Movable mov = gameOp.getMovable();
            switch (mov.getTeam()) {
                case FOE:
                    list = CommandCenter.getInstance().getMovFoes();
                    break;
                case FRIEND:
                    list = CommandCenter.getInstance().getMovFriends();
                    break;
                case FLOATER:
                    list = CommandCenter.getInstance().getMovFloaters();
                    break;
                case DEBRIS:
                default:
                    list = CommandCenter.getInstance().getMovDebris();
            }

            //pass the appropriate linked-list from above
            //this block will execute the addToGame() or removeFromGame() callbacks in the Movable models.
            GameOp.Action action = gameOp.getAction();
            if (action == GameOp.Action.ADD)
                mov.addToGame(list);
            else //REMOVE
                mov.removeFromGame(list);

        }//end while
    }

    /**
     * Spawns bubble shield
     */
    private void spawnBubbleShield() {
        if (CommandCenter.getInstance().getFrame() % BubbleShield.SPAWN_BUBBLE_SHIELD  == 0) {
            BubbleShield bs = new BubbleShield();
            int possibleY = Game.R.nextInt(minY, maxY);
            bs.setCenter(new Point(x, possibleY));
            CommandCenter.getInstance().getOpsQueue().enqueue(bs, GameOp.Action.ADD);
        }
    }

    /**
     * Spawns properly spaced single tack
     */
    private void spawnTack() {
        Tack tack = new Tack();
        Point previousPoint = Tack.getPreviousTack(); // getting last spawn loc
        int previousY = previousPoint != null ? previousPoint.y : 0;

        int possibleY = Game.R.nextInt(minY, maxY);

        // while the y isnt properly spaced, generate a new one
        while (Math.abs(possibleY - previousY) < tack.getSPACING())
        {
            possibleY = Game.R.nextInt(minY, maxY);}

        tack.setCenter(new Point(x, possibleY));
        CommandCenter.getInstance().getOpsQueue().enqueue(tack, GameOp.Action.ADD);
    }

    /**
     * Continuously spawns tacks based on level
     * @param level
     */
    private void spawnTacks(int level) {
        long frame = CommandCenter.getInstance().getFrame();
        int spawnInterval = Math.max(100 - (level * 10), MAX_SPAWN_INTERVAL);

        if (frame % spawnInterval == 0){
            spawnTack();
        }
    }

    /**
     * Spawns treats based on level
     * @param level
     */
    private void spawnTreats(int level) {
        long frame = CommandCenter.getInstance().getFrame();
        int spawnInterval = Math.max(150 / level, MAX_SPAWN_INTERVAL);
        Treat treat = new Treat();

        if (frame % spawnInterval == 0) {
            int possibleY = Game.R.nextInt(minY, maxY);
            treat.setCenter(new Point(x, possibleY));
            CommandCenter.getInstance().getOpsQueue().enqueue(treat, GameOp.Action.ADD);
        }
    }

    /**
     * Spawns balloon (lives)
     */
    private void spawnBalloon() {
        if (CommandCenter.getInstance().getFrame() % Balloon.SPAWN_BALLOON == 0) {
            Balloon b = new Balloon();
            int possibleY = Game.R.nextInt(minY, maxY);
            b.setCenter(new Point(x, possibleY));
            CommandCenter.getInstance().getOpsQueue().enqueue(b, GameOp.Action.ADD);
        }
    }

    // ===============================================
    // KEYLISTENER METHODS
    // ===============================================

    @Override
    public void keyPressed(KeyEvent e) {
        Kitty kitty = CommandCenter.getInstance().getKitty();
        int keyCode = e.getKeyCode();
        switch (keyCode) {
            case UP:
                kitty.jump();
                SoundLoader.playSound("jump.wav");
                break;
            case LEFT:
                kitty.setTurnState(Kitty.TurnState.LEFT);
                kitty.setFaceState(Kitty.FaceState.LEFT);
                break;
            case RIGHT:
                kitty.setTurnState(Kitty.TurnState.RIGHT);
                kitty.setFaceState(Kitty.FaceState.RIGHT);
                break;
            default:
                break;
        }

    }

    @Override
    public void keyReleased(KeyEvent e) {
        Kitty kitty = CommandCenter.getInstance().getKitty();
        int keyCode = e.getKeyCode();
        //show the key-code in the console
//        System.out.println(keyCode);

        if (keyCode == START && CommandCenter.getInstance().isGameOver()) {
            CommandCenter.getInstance().initGame();
            return;
        }

        switch (keyCode) {
            //releasing either the LEFT or RIGHT arrow key will set the TurnState to IDLE
            case LEFT:
                kitty.setTurnState(Kitty.TurnState.IDLE);
                break;
            case RIGHT:
                kitty.setTurnState(Kitty.TurnState.IDLE);
                break;
            case MEOW:
                SoundLoader.playSound("jb_meow_vol.wav");
                break;
            case PAUSE:
                CommandCenter.getInstance().setPaused(!CommandCenter.getInstance().isPaused());
                break;
            case QUIT:
                System.exit(0);
                break;
            case MUTE:
                //if music is currently playing, then stop
                if (CommandCenter.getInstance().isThemeMusic()) {
                    SoundLoader.stopSound("theme_loop.wav");

                } else { //else not playing, then play
                    SoundLoader.playSound("theme_loop.wav");
                    System.out.println(CommandCenter.getInstance().isThemeMusic());
                }
                //toggle the boolean switch
                CommandCenter.getInstance().setThemeMusic(!CommandCenter.getInstance().isThemeMusic());
                break;
            default:
                break;

        }
    }

    @Override
    // does nothing, but we need it b/c of KeyListener contract
    public void keyTyped(KeyEvent e) {
    }
}

