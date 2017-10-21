package test;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.JFrame;

public class GameLauncher extends Canvas implements Runnable {
	//to keep it quiet:
	private static final long serialVersionUID = 1L;
	
	public static final int xDimension=380;
	public static final int yDimension=380;//screen dimensions
	
	private boolean running=false;
	private Thread thread;
	private Maze maze;
	
	//Images used:
	private BufferedImage background;
	private BufferedImage hWall;
	private BufferedImage vWall;
	
	private boolean[] instructionsArray = new boolean[10]; //W,A,S,D,Q,UP,Left,Down,Right,Enter
	
	public GameLauncher(Maze maze) {
		this.maze = maze;
	}
	private synchronized void start(){
		if (running){
			return;
		}
		running=true;
		thread=new Thread(this);
		thread.start();
	}
	
	private void init(){
		//Loads images
		BufferedImageLoader loader = new BufferedImageLoader();
		try{
			background = loader.loadImage("/background.png");
			hWall = loader.loadImage("/hWall.png");
			vWall = loader.loadImage("/vWall.png");
			
		}catch(IOException e){
			e.printStackTrace();
		}
		
		addKeyListener(new KeyboardInput(this));
		
	}
	
	private synchronized void stop(){
		if (!running){
			return;
		}
		
		running=false;
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.exit(1);
	}
	
	
	public void run(){
		init();
		long lastTime = System.nanoTime();
		final double amountOfTicks=60.0;
		double ns = 1000000000/amountOfTicks;
		double delta = 0;
		while(running){
			long now = System.nanoTime();
			delta+=(now-lastTime)/ns;
			lastTime=now;
			if(delta>=1){
				render();
				delta--;
			}
		}
		stop();
	}
	private void render(){
		BufferStrategy bs = this.getBufferStrategy();
		if(bs==null){
			createBufferStrategy(3);
			return;
		}
		Graphics g = bs.getDrawGraphics();
		//Draw the objects{
		
		//Draw the background:
		g.drawImage(background, 0, 0, this);
		
		//Draw the walls:
		for (int x = 0; x<7;x++){
			for (int y = 0; y<7; y++){
				if (maze.isWallBelow(x, y)){
					g.drawImage(hWall,(50+5)*x,(50+5)*(y+1),this);
				}
				if (maze.isWallRight(x, y)){
					g.drawImage(vWall,(50+5)*(x+1),(50+5)*y,this);
				}
			}
		}
		//Draw the objects}
		g.dispose();
		bs.show();
	}
	
	public void keyPressed(KeyEvent e){
		int key = e.getKeyCode();
		if (key==KeyEvent.VK_W){
			instructionsArray[0]=true;
		}else if (key==KeyEvent.VK_A){
			instructionsArray[1]=true;
		}else if (key==KeyEvent.VK_S){
			instructionsArray[2]=true;
		}else if (key==KeyEvent.VK_D){
			instructionsArray[3]=true;
		}else if (key==KeyEvent.VK_Q){
			instructionsArray[4]=true;
		}else if (key==KeyEvent.VK_UP){
			instructionsArray[5]=true;
		}else if (key==KeyEvent.VK_LEFT){
			instructionsArray[6]=true;
		}else if (key==KeyEvent.VK_DOWN){
			instructionsArray[7]=true;
		}else if (key==KeyEvent.VK_RIGHT){
			instructionsArray[8]=true;
		}else if (key==KeyEvent.VK_ENTER){
			instructionsArray[9]=true;
		}else if (key==KeyEvent.VK_SPACE){
			
		}
	}
	public void keyReleased(KeyEvent e){
		int key = e.getKeyCode();
		if (key==KeyEvent.VK_W){
			instructionsArray[0]=false;
		}else if (key==KeyEvent.VK_A){
			instructionsArray[1]=false;
		}else if (key==KeyEvent.VK_S){
			instructionsArray[2]=false;
		}else if (key==KeyEvent.VK_D){
			instructionsArray[3]=false;
		}else if (key==KeyEvent.VK_Q){
			instructionsArray[4]=false;
		}else if (key==KeyEvent.VK_UP){
			instructionsArray[5]=false;
		}else if (key==KeyEvent.VK_LEFT){
			instructionsArray[6]=false;
		}else if (key==KeyEvent.VK_DOWN){
			instructionsArray[7]=false;
		}else if (key==KeyEvent.VK_RIGHT){
			instructionsArray[8]=false;
		}else if (key==KeyEvent.VK_ENTER){
			instructionsArray[9]=false;
		}
	}

	public static void main(String args[]){
		Test test = null;
		try {
			test = new Test();
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		GameLauncher game = new GameLauncher(new Maze(test.maze));

		//set window size:
		game.setPreferredSize(new Dimension(GameLauncher.xDimension,GameLauncher.yDimension));
		game.setMaximumSize(new Dimension(GameLauncher.xDimension,GameLauncher.yDimension));
		game.setMinimumSize(new Dimension(GameLauncher.xDimension,GameLauncher.yDimension));

		JFrame frame = new JFrame("Tank Trouble");
		frame.add(game);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		
		game.start();

	}
}
