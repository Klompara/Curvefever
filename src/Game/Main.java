package Game;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import Game.Powerup.POWERUPTYPE;

public class Main extends Canvas implements Runnable{
	private static final long serialVersionUID = 1L;
	
	public static final int WIDTH = 1280, HEIGHT = 768;
	
	private JFrame frame;
	public static boolean running = false;
	private Thread thread;
	
	private List<Player> playerList = new ArrayList<Player>();
	private List<Powerup> powerupList = new ArrayList<Powerup>();
	private GUI gui;
	
	private boolean roundEnd = false;
	private long roundEndTime = 0;
	private boolean addScoreToPlayer = false;
	private Player playerToAddScore = null;
	
	private BufferedImage biMap = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_BYTE_INDEXED);
	
	public static enum GAMESATES {
		PLAY,
		MAINMENU
	}
	
	private GAMESATES currentState = GAMESATES.MAINMENU;
	
	private boolean startedDrawing;
	private long startedDrawingStartTime = 0;
	
	public Main() {		
		loadFrame("Curve Fever!");
		this.addKeyListener(new KeyInput(this));
		running = true;
		thread = new Thread(this, "Game");
		thread.start();
		
		int anzahl = Integer.parseInt(JOptionPane.showInputDialog("Anzahl der Spieler: "));
		
		for(int i = 0; i < anzahl; i++) {
			playerList.add(new Player(this, i));
		}
		
		gui = new GUI(this);
	}
	
	private void tick() {
		if(currentState == GAMESATES.MAINMENU) {
			
		}else if(currentState == GAMESATES.PLAY) {
			
			if(startedDrawingStartTime == 0)
				startedDrawingStartTime = System.currentTimeMillis();
			if(System.currentTimeMillis()-startedDrawingStartTime > 2000 && !startedDrawing) {
				startedDrawing = true;
			}
						
			checkIfRoundOver();

			addRandomPowerup();
			
			for(int i = 0; i < powerupList.size(); i++) {
				Powerup powerup = powerupList.get(i);
				for(Player player : playerList) {
					if(powerup.colliding(player)) {
						if(powerup.getType() == POWERUPTYPE.fat) {
							for(int j = 0; j < playerList.size(); j++) {
								Player p = playerList.get(j);
								if(p != player)
									p.debuffs.add(new Debuff(POWERUPTYPE.fat));
							}
						}else if(powerup.getType() == POWERUPTYPE.speed) {
							player.debuffs.add(new Debuff(POWERUPTYPE.speed));
						}else if(powerup.getType() == POWERUPTYPE.border) {
							// TODO Map border collision
						}
						powerupList.remove(powerup);
					}
				}
			}
			
			gui.tick();
		}
	}
	
	private void render() {
		BufferStrategy bs = this.getBufferStrategy();
		if(bs == null) {
			this.createBufferStrategy(2);
			return;
		}
		
		Graphics2D g = (Graphics2D) bs.getDrawGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		if(currentState == GAMESATES.MAINMENU) {
			g.setColor(Color.black);
			g.fillRect(0, 0, WIDTH, HEIGHT);
			g.setFont(font(70));
			drawTextWithShadow("Play", g, Color.white, (WIDTH/2)-(getFWidth("Play", g)/2), HEIGHT/2, 5);
			g.setFont(font(20));
			drawTextWithShadow("(press Enter)", g, Color.white, (WIDTH/2)-(getFWidth("(press Enter)", g)/2), HEIGHT/2 + 40, 3);
			
			
		}else if(currentState == GAMESATES.PLAY) {
			g.setColor(Color.black);
			g.fillRect(0, 0, WIDTH, HEIGHT);
			
			g.drawImage(biMap, 0, 0, WIDTH, HEIGHT, null);
			for(Player p : playerList) {
				if(!p.isDead())
					p.render(g);
			}
			gui.render(g);
			
			for(Powerup p : powerupList) {
				p.render(g);
			}
		}
		
		g.dispose();
		bs.show();
	}
	
	private void checkIfRoundOver() {
		int deadPlayers = 0;
		
		for(Player p : playerList) {
			if(!p.isDead())
				p.tick();
			else
				deadPlayers++;
		}
		
		if(deadPlayers == playerList.size()-1 && playerList.size() != 1) {
			roundEnd = true;
			addScoreToPlayer = true;
			
			for(Player p : playerList) {
				if(!p.isDead())
					playerToAddScore = p;
			}
		}else if(deadPlayers == playerList.size()) {
			roundEnd = true;
		}
		
		if(roundEnd && roundEndTime == 0)
			roundEndTime = System.currentTimeMillis();
		if(roundEnd && roundEndTime != 0 && System.currentTimeMillis()-3000 > roundEndTime) {
			if(addScoreToPlayer) {
				playerToAddScore.setScore(playerToAddScore.getScore()+1);
			}
			startedDrawingStartTime = 0;
			startedDrawing = false;
			
			for(Player p : playerList) {
				p.reset();
				biMap = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_BYTE_INDEXED);
			}
			
			addScoreToPlayer = false;
			roundEnd = false;
			roundEndTime = 0;
		}
	}
	
	private void addRandomPowerup() {
		Random r = new Random();
		if(r.nextInt(300) == 1) {
			powerupList.add(new Powerup(r.nextInt(WIDTH), r.nextInt(HEIGHT), POWERUPTYPE.values()[r.nextInt(3)]));
		}
	}
	
	public void run() {
		long lastTime = System.nanoTime();
		double amountOfTicks = 60.0;
		double ns = 1000000000 / amountOfTicks;
		double delta = 0;
		long timer = System.currentTimeMillis();
		
		int ticks = 0;
		int frames = 0;
		while(running){
			long now = System.nanoTime();
			delta += (now - lastTime) / ns;
			lastTime = now;
			while(delta >= 1) {
				tick();
				ticks++;
				delta--;
			}
			if(running) {
				render();
				frames++;
			}
			
			if(System.currentTimeMillis() - timer > 1000){
				timer += 1000;
				frame.setTitle("Game - fps: "+frames+", ticks: "+ticks);
				frames = 0;
				ticks = 0;
			}
		}
		stop();
	}
	
	private void stop() {
		running = false;
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private void loadFrame(String title) {
		frame = new JFrame(title);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(WIDTH, HEIGHT);
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.add(this);
		frame.setVisible(true);
	}
	
	public static Font font(double size){
		return new Font("Purisa", Font.BOLD, (int) size);
	}
	public static int getFWidth(String s, Graphics2D g) {
		return (int) g.getFontMetrics().getStringBounds(s, g).getWidth();
	}
	public static int getFHeight(String s, Graphics2D g) {
		return (int) g.getFontMetrics().getStringBounds(s, g).getHeight();
	}
	
	public GAMESATES getCurrentState() {
		return currentState;
	}

	public void setCurrentState(GAMESATES currentState) {
		this.currentState = currentState;
	}

	public boolean isStartedDrawing() {
		return startedDrawing;
	}

	public void setStartedDrawing(boolean startedDrawing) {
		this.startedDrawing = startedDrawing;
	}
	
	public BufferedImage getBiMap() {
		return biMap;
	}

	public void setBiMap(BufferedImage biMap) {
		this.biMap = biMap;
	}
	
	public List<Player> getPlayerList() {
		return playerList;
	}

	public void setPlayerList(List<Player> playerList) {
		this.playerList = playerList;
	}

	public static void drawTextWithShadow(String text, Graphics2D g, Color c, int x, int y, int offset) {
		g.setColor(Color.gray.darker().darker());
		g.drawString(text, x+offset, y+offset);
		g.setColor(c);
		g.drawString(text, x, y);
	}
	
	public static void main(String[] args) { new Main(); }
}
