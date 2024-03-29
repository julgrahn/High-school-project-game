package spelet.game;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import spelet.game.entitites.Ghoul;
import spelet.game.entitites.Player;
import spelet.game.gfx.Screen;
import spelet.game.gfx.SpriteSheet;
import spelet.game.gfx.level.Level;
//import spelet.game.gfx.Colours;
//import spelet.game.gfx.Font;


public class Game extends Canvas implements Runnable {

	private static final long serialVersionUID = 1L;

	public static final int WIDTH = 160;
	public static final int HEIGHT = WIDTH / 12 * 9;
	public static final int SCALE = 7;
	public static final String NAME = "Flowerpower";

	
	private JFrame frame; 
	
	public boolean running = false;
	public int tickCount = 0;
	
	private BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
	private int[] pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
	private int [] colours = new int[6*6*6];	
	
	private Screen screen;
	public InputHandler input;
	public Level level;
	public Player player;
	
	
	public Game(){
		setMinimumSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
		setMaximumSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
		setPreferredSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
		
		frame = new JFrame(NAME);
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());
		
		frame.add(this, BorderLayout.CENTER);
		frame.pack();
		
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		
	}
	
	public void init(){
		int index = 0;
		for (int r = 0; r < 6;r++){
			for (int g = 0; g < 6;g++){
				for (int b = 0; b < 6;b++){
					int rr = (r*255/5);
					int gg = (g*255/5);
					int bb = (b*255/5);
					
					colours[index++] = rr <<16 | gg << 8 | bb;
				}					
			}					
		}
		
		screen = new Screen(WIDTH, HEIGHT, new SpriteSheet("/sprite_sheet5.png"));
		input = new InputHandler(this);
		level = new Level("/levels/water_test_level.png");
		player = new Player(level, 0, 0, input, JOptionPane.showInputDialog(this, "Anv�ndarnamn?"));
		level.addEntity(player);
		Ghoul ghoul = new Ghoul(level, 300, 300, player);
	 	level.addEntity(ghoul);
	}
	
	public synchronized void start() {
		running = true;
		new Thread(this).start();
		
	}
	public synchronized void stop() {
		running = false;
	}
	
	public void run() {
		long lastTime = System.nanoTime();
		double nsPerTick = 1000000000D/60D;
		
		int frames = 0;
		int ticks = 0;
		
		long lastTimer = System.currentTimeMillis();
		double delta = 0;
		
		init();
		
		while (running){
			if (level.done()) {
				JOptionPane.showMessageDialog(this, "Du vann! Din po�ng blev " + Math.round(player.score));
				System.exit(0);
				}
				if (player.isDead()) {
				JOptionPane.showMessageDialog(this, "Du dog :(");
				System.exit(0); 
				}
			long now = System.nanoTime();
			delta +=(now - lastTime) / nsPerTick;
			lastTime = now;
			boolean shouldRender = true;
			
			
			while (delta >= 1) {
				ticks++;
				tick();
				delta -= 1;
				shouldRender = true;
			}
			
			try{
			Thread.sleep(2);
			}catch(InterruptedException e){				
			}
			if (shouldRender){
			frames++;
			render();
			
			if (System.currentTimeMillis() - lastTimer >= 1000){
				lastTimer += 1000;
				System.out.println(ticks + " ticks, " + frames + " frames" );
				frames = 0;
				ticks = 0;
			}
			}
		}
	}
	
	public void tick(){
		tickCount++;
//		screen.xOffset++;
//		screen.yOffset++;
		level.tick();
			}	
	public void render(){
		BufferStrategy bs = getBufferStrategy();
		if (bs == null){
			createBufferStrategy(3);
			return;
		}
		
		int xOffset = (int)Math.round(player.x - (screen.width/2));
		int yOffset = (int)Math.round(player.y - (screen.height/2));
		
		level.renderTiles(screen, xOffset, yOffset);
		for (int x = 0; x <level.width;x++){
			//int colour = Colours.get(-1, -1, -1, 000);
			if (x %10 == 0 && x != 0){
				//colour = Colours.get(-1, -1, -1, 500);
			}
			//Font.render((x%10)+"", screen, 0+(x*8), 0, colour);
		}
		
		level.renderEntities(screen);
		
		for (int y = 0; y<screen.height; y++){
			for(int x = 0; x <screen.width; x++){
				int colourCode = screen.pixels[x+y*screen.width];
				if (colourCode < 255) pixels[x+y*WIDTH] = colours[colourCode];
			}
		}
		Graphics g = bs.getDrawGraphics();
		
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, getWidth(), getHeight());
		
		g.drawImage(image, 0, 0,getWidth(), getHeight(), null);
		
		g.setColor(Color.BLACK);
		g.drawRect(0, 0, getWidth(), getHeight());
		g.dispose();
		bs.show();
	}
	public static void main(String[] args){
		new Game().start();
		
	}
	
		
		
}

