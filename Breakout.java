/*
 * File: Breakout.java
 * -------------------
 * Name:
 * Section Leader:
 * 
 * This file will eventually implement the game of Breakout.
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class Breakout extends GraphicsProgram {

	/** Width and height of application window in pixels */
	public static final int APPLICATION_WIDTH = 400;
	public static final int APPLICATION_HEIGHT = 600;

	/** Dimensions of game board (usually the same) */
	private static final int WIDTH = APPLICATION_WIDTH;
	private static final int HEIGHT = APPLICATION_HEIGHT;

	/** Dimensions of the paddle */
	private static final int PADDLE_WIDTH = 60;
	private static final int PADDLE_HEIGHT = 10;

	/** Offset of the paddle up from the bottom */
	private static final int PADDLE_Y_OFFSET = 30;

	/** Number of bricks per row */
	private static final int NBRICKS_PER_ROW = 10;

	/** Number of rows of bricks */
	private static final int NBRICK_ROWS = 10;

	/** Separation between bricks */
	private static final int BRICK_SEP = 4;

	/** Width of a brick */
	private static final int BRICK_WIDTH =
	  (WIDTH - (NBRICKS_PER_ROW - 1) * BRICK_SEP) / NBRICKS_PER_ROW;

	/** Height of a brick */
	private static final int BRICK_HEIGHT = 8;

	/** Radius of the ball in pixels */
	private static final int BALL_RADIUS = 10;

	/** Offset of the top brick row from the top */
	private static final int BRICK_Y_OFFSET = 70;

	/** Number of turns */
	private static final int NTURNS = 3;

	/* Method: init() */
	/** Sets up the Breakout program. */
	public void init() {
		createBall();
		setupBricksRows();
		createLabels();
		createPaddle();
		addMouseListeners();
		ballStartAngle();
		vy = 4.0;
	}

	/* Method: run() */
	/** Runs the Breakout program. */
	public void run() {	
		waitForClick();
		while (brickCount != 0) {
			moveBall();
			checkForCollision();
			pause(20);
		}
	}
	
	//moves the ball in the game world
	private void moveBall(){
		ball.move(vx, vy);
	}
	
	//method for collision detection as the ball moves
	//called eachtime the ball moves. checks for points on the ball
	//also detects walls
	private void checkForCollision(){
		wallDetection();
		objectDetection();
	}
	
	// Responsible for moving the paddle in accordance with the mouse
	// location on screen. X axis only!
	public void mouseMoved(MouseEvent e){
		if (e.getX() > PADDLE_WIDTH/2 && e.getX() < WIDTH - PADDLE_WIDTH/2)
		paddle.setLocation(e.getX() - PADDLE_WIDTH/2 , HEIGHT - PADDLE_Y_OFFSET - PADDLE_HEIGHT);
	}
	
	//responsible for building out the rows of bricks. feed y position
	//location to both setupColumns() and brickColorGetter()
	private void setupBricksRows(){
		int y = BRICK_Y_OFFSET;
		for (int i=1; i<NBRICK_ROWS + 1; i++){
			buildBrickColumns(y);
			y += BRICK_HEIGHT + BRICK_SEP;
		}
	}
	
	//adds each to a row to the appropriate column length
	//takes a int y to determine y position of brick
	private void buildBrickColumns(int y){
		int x = (WIDTH - (BRICK_WIDTH)*NBRICKS_PER_ROW)/2 - 19 ;
		for (int i=0; i<NBRICKS_PER_ROW; i++){
			GRect brick = new GRect(x,y,BRICK_WIDTH,BRICK_HEIGHT);
			brick.setFilled(true);
			brick.setColor(brickColorGetter(y));
			add(brick);
			brick.sendToFront();
			x += BRICK_WIDTH + BRICK_SEP;
			brickCount += 1;
		}
	}
	
	//creates the game paddle and places it in the world
	private void createPaddle(){
		paddle = new GRect(WIDTH/2 - PADDLE_WIDTH/2 - 8  , HEIGHT - PADDLE_Y_OFFSET - PADDLE_HEIGHT, PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		paddle.sendToFront();
		add(paddle);
	}
	
	//takes y position data from setupBricksRows() to determine row color
	private Color brickColorGetter(int y){
		if (y==70 || y==82){
			return Color.RED;
		} else if (y==94 || y==106){
			return Color.ORANGE;
		} else if(y==118 || y==130){
		return Color.BLUE;
		} else if (y==142 || y==154){
			return Color.GREEN;
		} else {
			return Color.CYAN;
		}
	}
	
	//create and adds ball to canvas
	private void createBall(){
		ball = new GOval(BALL_RADIUS,BALL_RADIUS);
		ball.setFilled(true);
		add(ball, WIDTH/2 - 8, HEIGHT/2);
	}
	
	//returns object found if getElementAt(x,y) !=null
	private GObject getCollidingObject(double x, double y){
		GObject collider = getElementAt(x,y);
		return collider;
	}
	
	//holds the logic for wall detection in the checkForCollisions() method
	private void wallDetection(){
		if (ball.getY() < 0 + 4 ){
			vy = -vy;
			println("y: " + ball.getY());
		} else if (ball.getX() > APPLICATION_WIDTH - BALL_RADIUS){
			vx = -vx;
			ball.setLocation(WIDTH - BALL_RADIUS, ball.getY());
		} else if (ball.getX() < 0) {
			vx = -vx;
			ball.setLocation(0, ball.getY());
		} else if (ball.getY() > HEIGHT - 30) {
			loseLife();
		}
	}
	
	//creates the score and lives labels
	private void createLabels(){
		liveTracker = new GLabel("Lives: " + lives);
		add(liveTracker, 10, 20);
		scoreBoard = new GLabel("Score: " + score);
		add(scoreBoard, WIDTH - 80, 20);
		add(victoryLabel);
		victoryLabel.setVisible(false);
	}
	
	//hold the object detection logic for checkForCollisions() method
	private void objectDetection(){
		if (getElementAt(ball.getX(),ball.getY()) != null){		
			collisionLogic(ball.getX(),ball.getY());
		} else if (getElementAt(ball.getX() + BALL_RADIUS ,ball.getY()) != null) {
			collisionLogic(ball.getX() + BALL_RADIUS,ball.getY());
		} else if (getElementAt(ball.getX(),ball.getY() + BALL_RADIUS) != null) {
			collisionLogic(ball.getX(),ball.getY() + BALL_RADIUS);
		} else if (getElementAt(ball.getX() + BALL_RADIUS,ball.getY() + BALL_RADIUS) != null) {
			collisionLogic(ball.getX() + BALL_RADIUS,ball.getY() + BALL_RADIUS);
		}
	}
	
	//is called whenever a detection of a object is detected. if paddle it bounces. if brick it removes(currently)
	private void collisionLogic(double x, double y){
		GObject object = getCollidingObject(x,y);
		println(object);
		
		if (object == paddle) {
			vy = -vy;
			ball.setLocation(ball.getX(), HEIGHT - PADDLE_Y_OFFSET - PADDLE_HEIGHT + BALL_RADIUS);
		} else if (object != scoreBoard || object != liveTracker){
			vy = -vy;
			score += 10;
			updateScore();
			remove(object);
			brickCount -= 1;
		}
		if (brickCount == 0){
			victory();
		}
	}
	
	//called when the ball hits the bottom wall of the game space
	private void loseLife(){
		lives -= 1;
		if (lives == 0) {
			removeAll();
			init();
			lives = NTURNS;
			score = 0;
			updateScore();
		} else {
			ball.setLocation(WIDTH/2, HEIGHT/2);
			ballStartAngle();	
		}
		liveTracker.setLabel("Lives: " + lives);
		waitForClick();
		
	}
	
	private void victory(){
		victoryLabel.setVisible(true);
		waitForClick();
		removeAll();
		init();	
	}
	
	//uses rGen to set a random vx at the start of the game
	private void ballStartAngle(){
		vx = rGen.nextDouble(1.0,3.0);
		if (rGen.nextBoolean(0.5)) vx = -vx;
	}
	
	//INSTANCE VARIABLES
	//keeps track of paddle for mouseListener to reposition
	private GRect paddle;
	
	//keeps track of ball for rebounding and collision
	private GOval ball;
	//ball velocity tracker
	private double vx, vy;
	
	//updates the score text on the scoreBoard
	private void updateScore(){
		scoreBoard.setLabel("Score: " + score);
	}
	//tracks players lives
	int lives = NTURNS;
	
	//label for lives
	GLabel liveTracker;
	
	//score tracker
	int score = 0;
	
	//label for score
	GLabel scoreBoard;
	
	//brick count
	int brickCount = 0;
	
	//RandomGenerator for ball initial arc
	RandomGenerator rGen = RandomGenerator.getInstance();
	
	GLabel victoryLabel = new GLabel("Victory", WIDTH/2, HEIGHT/2);
	
}
