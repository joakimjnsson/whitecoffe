package com.ziphiaz.whitecoffe;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import javax.swing.JFrame;

import com.ziphiaz.whitecoffe.graphics.Screen;

public class Game extends Canvas implements Runnable {
	private static final long serialVersionUID = 1L;

	public static int width = 300; //width of the game
	public static int height = width / 16 * 9; //height in 16-9 aspect ratio
	public static int scale = 3;//scale the game to 3x the size, but same pixels

	private Thread thread;//to start a subprocess of java
	private JFrame frame;
	private boolean running = false;

	private Screen screen;

	private BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
	//create image
	private int[] pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();

	//create a image we can change
	//raster is a data structure, rectangular array of pixel

	public Game() {
		Dimension size = new Dimension(width * scale, height * scale);
		setPreferredSize(size);//setPre extended from Canvas

		screen = new Screen(width, height);

		frame = new JFrame();

	}

	public synchronized void start() {
		/*preventing thread interference and memory incosistency errors
		gonna have multiple threads, so sync makes sure there are 
		no overlapps of threads*/
		running = true;
		thread = new Thread(this, "Display");//this - contain this game class
		thread.start();
	}

	public synchronized void stop() {
		/*måste kunna stänga av det, typ om du
		 * använder en applet(internet) och bara stänger tabben
		 * stängs dte inte på riktigt*/
		running = false;
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void run() {//game loop running
		/*//must seperate logic and graphics, 
		 * because of people using different computers and this will make the
		 * game run at different speeds because of cpu and loop speeds */
		while (running) {//b.c. it impliments runnable, when we start a thread it will automaticlt call run
			update();//60 ticks per sec
			render();//unlimited, (FPS)
		}
	}

	public void update() {

	}
	
	public void render() {
		/*a buffer is a temp storage, put it on ready list and then get it
		 * render before it is needed, because render draws form left to right
		 * up to down - this is called a "buffer strategy"*/
		BufferStrategy bs = getBufferStrategy();

		if (bs == null) {//dont want to create BS everytime render is called
			createBufferStrategy(3);//tripple buffeering (two frames are rendered&stores) i.e a midpoint 
			return;
		}
		screen.render();

		for (int i = 0; i < pixels.length; i++) {
			pixels[i] = screen.pixels[i];
		}
		//give data to the buffer- a link between Graphics and bs
		Graphics g = bs.getDrawGraphics();
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, getWidth(), getHeight());//graphis inbetween here---
		g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
		g.dispose();
		//buffer spwapping or blitting(?)
		bs.show();//show next available buffer
	}

	/*we figure out which pixel at what point should be what color
	 * 
	 * */

	public static void main(String[] args) {
		Game game = new Game();
		game.frame.setResizable(false);
		game.frame.setTitle("whiteCoffe");
		game.frame.add(game);//can add game cus Game extends Canvas
		game.frame.pack();
		game.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		game.frame.setLocationRelativeTo(null);
		game.frame.setVisible(true);

		game.start();
	}

}
