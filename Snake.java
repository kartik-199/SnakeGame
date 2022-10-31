import java.awt.Color;
import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import doodlepad.Oval;
import doodlepad.Pad;
import doodlepad.Text;

public class Snake extends Pad{
	private ArrayList<Oval> body;
	private Color c;
	private Oval head,apple;
	private int numApples;
	private static final int WIDTH = 30;
	private boolean headRight, headLeft, headUp, headDown;
	
	public Snake(Color c) {
		super(500,500);
		setBackground(0,200,0);
		head = new Oval(235,235,30,30);
		head.setFillColor(Color.YELLOW);
		headRight=true;
		int x = 205;
		body = new ArrayList<Oval>();
		this.c=c;
		for(int i = 0; i<3; i++) {
			body.add(new Oval(x,235,WIDTH,WIDTH));
			body.get(i).setFillColor(this.c);
			x-=WIDTH;
		}
		makeApple();
	}
	public void makeApple() {
		int x = (int)(Math.random()*441+WIDTH), y = (int)(Math.random()*441+WIDTH);
		apple = new Oval(x,y,WIDTH,WIDTH);
		apple.setFillColor(Color.RED);
		while(intersectApple()) {
			x = (int)(Math.random()*471+WIDTH/2);
			y = (int)(Math.random()*471+WIDTH/2);
			apple.setCenter(x,y);
			apple.setFillColor(Color.RED);
		}
		
		

	}
	public boolean intersectApple() {
		if(head.intersects(apple))
			return true;
		for(int i = 0; i<body.size(); i++)
			if(body.get(i).intersects(apple))
				return true;
		return false;
	}
	public boolean ateApple() {
		if(head.intersects(apple)) {
			numApples++;
			removeShape(apple);
			makeApple();
			return true;
		}
		return false;
	}
	public void moveSnake(double x, double y) {
		double originalHeadX = head.getX();
		double originalHeadY = head.getY();
		head.move(x, y);
		if(ateApple()) {
			body.add(0,new Oval(originalHeadX,originalHeadY,WIDTH,WIDTH));
			body.get(0).setFillColor(c);
		}
		else {
			body.add(0,body.get(body.size()-1));
			body.remove(body.size()-1);
			body.get(0).setX(originalHeadX);
			body.get(0).setY(originalHeadY);
			body.get(0).setFillColor(c);
		}
	}
	public void moveRight() {
		if(!headLeft) {
			headUp=false;
			headDown = false;
			headRight = true;
			
			moveSnake(WIDTH,0);
		}
	}
	public void moveLeft() {
		if(!headRight) {
			headUp=false;
			headDown = false;
			headLeft=true;
			
			moveSnake(-WIDTH,0);
		}
	}
	
	public void moveUp() {
		if(!headDown) {
			headRight = false;
			headLeft = false;
			headUp = true;
			
			moveSnake(0,-WIDTH);
		}
	}
	
	public void moveDown() {
		if(!headUp) {
			headRight = false;
			headLeft = false;
			headDown = true;
			
			moveSnake(0,WIDTH);
		}
	}
	public boolean gameOver() {
		if(head.getX()<=0 || head.getY()<=0 || head.getX()>=470 || head.getY()>=470)
			return true;
		for(int i = 0; i<body.size(); i++)
			if(head.intersects(body.get(i)))
				return true;
		return false;
	}
	public void onKeyPressed(String keyText, String e) {
		if(gameOver())
			setEventsEnabled(false);
		else {
			if(keyText.equals("D"))
				moveRight();
			if(keyText.equals("W"))
				moveUp();
			if(keyText.equals("A"))
				moveLeft();
			if(keyText.equals("S"))
				moveDown();
		}
	}
	public void move(Clip clip) throws InterruptedException, UnsupportedAudioFileException, IOException, LineUnavailableException{

        boolean u, d, l, r;
        while(!gameOver()) {
            if(headRight) moveRight();
            else if(headLeft) moveLeft();
            else if(headUp) moveUp();
            else if(headDown) moveDown();
            TimeUnit.MILLISECONDS.sleep(250);
        }
      //ends theme music and plays game over sound after snake dies
        clip.stop();
        clip.close();
        File gameOverPath = new File("gameover.wav");
        AudioInputStream s2 = AudioSystem.getAudioInputStream(gameOverPath);
        clip.open(s2);
        clip.start();

        //waits 6 seconds after playing game over sound, then ends all audio objects
        TimeUnit.SECONDS.sleep(2);
        clip.stop();
        clip.close();
        setEventsEnabled(false);
        Text t;
        if (numApples == 1)
            t = new Text(numApples +" APPLE EATEN!",30,50,50);
        else
            t = new Text(numApples +" APPLES EATEN!",30,50,50);
        TimeUnit.SECONDS.sleep(5);
        Clip clip1 = AudioSystem.getClip();
        File themePath = new File("theme.wav");
        AudioInputStream s1 = AudioSystem.getAudioInputStream(themePath);
        clip1.open(s1);
        clip1.start();

        //starts the game
        Color bodyC = new Color(93, 200, 253);
        Snake game = new Snake(bodyC);
       game.move(clip1);
       game.moveSnake(285, 285);
    }



    public static void main(String[] args) throws InterruptedException, IOException, UnsupportedAudioFileException, LineUnavailableException {
        //theme music setup
        Clip clip = AudioSystem.getClip();
        File themePath = new File("theme.wav");
        AudioInputStream s1 = AudioSystem.getAudioInputStream(themePath);
        clip.open(s1);
        clip.start();

        //starts the game
        Color bodyC = new Color(93, 200, 253);
        Snake game = new Snake(bodyC);
       game.move(clip);
       game.moveSnake(285, 285);
        
    }

}
