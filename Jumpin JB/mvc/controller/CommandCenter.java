package edu.uchicago.gerber._08final.mvc.controller;

import edu.uchicago.gerber._08final.mvc.model.*;
import lombok.Data;

import java.util.LinkedList;

//The CommandCenter is a singleton that manages the state of the game.
//the lombok @Data gives us automatic getters and setters on all members
@Data
public class CommandCenter {

	private int numBalloons;
	private int level = 1;
	private int score;
	private int pointSinceLast;
	private boolean paused;
	private boolean themeMusic;
	//this value is used to count the number of frames (full animation cycles) in the game
	private long frame;
	private final Kitty kitty  = new Kitty();

	/*
	 TODO The following LinkedList<Movable> are examples of the Composite design pattern which is used to allow
	 compositions of objects to be treated uniformly. Here are the elements of the Composite design pattern:

     Component: Movable serves as the component interface. It defines common methods (move(), draw(Graphics g), etc.)
     that all concrete implementing classes must provide.

     Leaf: Concrete classes that implement Movable (e.g., Bullet, Asteroid) are the leaf nodes. They implement the
     Movable interface and provide specific behavior.

     Composite: The LinkedLists below that aggregate Movable objects (e.g., movFriends, movFoes) act as
     composites. They manage collections of Movable objects and provide a way to iterate over and operate on them as a
     group.

	 */
	private final LinkedList<Movable> movDebris = new LinkedList<>();
	private final LinkedList<Movable> movFriends = new LinkedList<>();
	private final LinkedList<Movable> movFoes = new LinkedList<>();
	private final LinkedList<Movable> movFloaters = new LinkedList<>();

	private final GameOpsQueue opsQueue = new GameOpsQueue();

	private static CommandCenter instance = null;

	// Constructor made private
	private CommandCenter() {
		System.out.print("level:" + level);
	}

	//this class maintains game state - make this a singleton.
	public static CommandCenter getInstance(){
		if (instance == null){
			instance = new CommandCenter();
		}
		return instance;
	}


	/**
	 * In charge of leveling up based on points since previous level
	 */
	public void levelUp(){
		if (this.pointSinceLast == 5) {
			this.level++;
			System.out.println("Level up! Current level: " + this.level);
			this.pointSinceLast = 0;
			getKitty().setShowLevel(50);
		}
	}


	public void initGame(){
		clearAll();
		levelUp();
		setScore(0);
		setPaused(false);
		//set to one greater than number of falcons lives in your game as decrementFalconNumAndSpawn() also decrements
		setNumBalloons(4);
		setPointSinceLast(0);
		setLevel(1);
		setFrame(1);
		kitty.decrementBalloonsAndSpawn();
		opsQueue.enqueue(kitty, GameOp.Action.ADD);
		Grass grass = new Grass();
		opsQueue.enqueue(grass, GameOp.Action.ADD);
		Cloud clouds = new Cloud(0, 20);
		Cloud.createClouds();
		opsQueue.enqueue(clouds, GameOp.Action.ADD);
		kitty.setBalloonState(Kitty.BalloonState.THREE);
	}


	public void incrementFrame(){
		frame = frame < Long.MAX_VALUE ? frame + 1 : 0;
	}

	private void clearAll(){
		movDebris.clear();
		movFriends.clear();
		movFoes.clear();
		movFloaters.clear();
	}

	public boolean isGameOver() {
		return numBalloons < 1;
	}

}