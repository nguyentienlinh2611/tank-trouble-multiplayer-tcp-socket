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
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JFrame;

import model.GameEngine;

public class GameLauncher extends Canvas implements Runnable {
	//to keep it quiet:
	private static final long serialVersionUID = 1L;
	
	public static final int xDimension=380;
	public static final int yDimension=380;//screen dimensions
	public static final int tankWidth = 26;
	public static final int bulletWidth = 6;
	public static final int squareWidth = 50;
	public static final int wallWidth = 5;
	
	private boolean running=false;
	private Thread thread;
	
	//Images used:
	private BufferedImage background;
	private BufferedImage tank1;
	private BufferedImage tank2;
	private BufferedImage bullet;
	private BufferedImage hWall;
	private BufferedImage vWall;
	private Socket socket;
	private ObjectOutputStream writer;
	private ObjectInputStream reader;
	private GameEngine engine;
	
	private boolean[] instructionsArray = new boolean[5]; //UP,Left,Down,Right,SPACE
	
	public GameLauncher() throws UnknownHostException, IOException, ClassNotFoundException {
		socket = new Socket("localhost",9000);
		writer = new ObjectOutputStream(socket.getOutputStream());
		reader = new ObjectInputStream(socket.getInputStream());
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
			tank1 = loader.loadImage("/tank1.png");
			tank2 = loader.loadImage("/tank2.png");
			bullet = loader.loadImage("/bullet.png");
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
	
	private void tick(){
		try {
			engine = (GameEngine) reader.readObject();
			writer.writeObject(instructionsArray);
			writer.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
				tick();
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
				if (engine.maze.isWallBelow(x, y)){
					g.drawImage(hWall,(50+5)*x,(50+5)*(y+1),this);
				}
				if (engine.maze.isWallRight(x, y)){
					g.drawImage(vWall,(50+5)*(x+1),(50+5)*y,this);
				}
			}
		}
		//Draw the rotated tanks:
				double rotationRequired = Math.toRadians(engine.player1.getDirection());
				double locationX = tank1.getWidth() / 2;
				double locationY = tank1.getHeight() / 2;
				AffineTransform tx = AffineTransform.getRotateInstance(rotationRequired, locationX, locationY);
				AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
				g.drawImage(op.filter(tank1, null), (int)(engine.player1.getCoordinates().getxCoord()-GameEngine.tankWidth/2), (int)(engine.player1.getCoordinates().getyCoord()-GameEngine.tankWidth/2), this);
				
				AffineTransform tx2 = AffineTransform.getRotateInstance(Math.toRadians(engine.player2.getDirection()), tank2.getWidth() / 2, tank2.getHeight() / 2);
				AffineTransformOp op2 = new AffineTransformOp(tx2, AffineTransformOp.TYPE_BILINEAR);
				g.drawImage(op2.filter(tank2, null), (int)(engine.player2.getCoordinates().getxCoord()-GameEngine.tankWidth/2), (int)(engine.player2.getCoordinates().getyCoord()-GameEngine.tankWidth/2), this);
				//Draw the bullets:
				for (int i = 0 ;i<engine.bulletList.size();i++){
					g.drawImage(bullet,(int)(engine.bulletList.get(i).getPosition().getxCoord()-GameEngine.bulletWidth/2),(int)(engine.bulletList.get(i).getPosition().getyCoord()-GameEngine.bulletWidth/2),this);
				}
		//Draw the objects}
		g.dispose();
		bs.show();
	}
	
	public void keyPressed(KeyEvent e){
		int key = e.getKeyCode();
		if (key==KeyEvent.VK_UP){
			instructionsArray[0]=true;
		}else if (key==KeyEvent.VK_LEFT){
			instructionsArray[1]=true;
		}else if (key==KeyEvent.VK_DOWN){
			instructionsArray[2]=true;
		}else if (key==KeyEvent.VK_RIGHT){
			instructionsArray[3]=true;
		}else if (key==KeyEvent.VK_ENTER){
			instructionsArray[4]=true;
		}
	}
	public void keyReleased(KeyEvent e){
		int key = e.getKeyCode();
		if (key==KeyEvent.VK_UP){
			instructionsArray[0]=false;
		}else if (key==KeyEvent.VK_LEFT){
			instructionsArray[1]=false;
		}else if (key==KeyEvent.VK_DOWN){
			instructionsArray[2]=false;
		}else if (key==KeyEvent.VK_RIGHT){
			instructionsArray[3]=false;
		}else if (key==KeyEvent.VK_SPACE){
			instructionsArray[4]=false;
		}
	}

	public static void main(String args[]){
		GameLauncher game=null;
		try {
			game = new GameLauncher();
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
