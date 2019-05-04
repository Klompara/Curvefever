package Game;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JOptionPane;

import Game.Powerup.POWERUPTYPE;

public class Player {

	public List<Debuff> debuffs = new ArrayList<Debuff>();
	
	private double x, y;
	private Color color;
	
	private boolean makeHole = false;
	private long timeHoleMake = 0;
	
	private double dx;
	private double dy;
	private double rad;
	private double speed;
	private double angle = 0;
	public boolean left;
	public boolean right;
	private double angleMove;
	private double normalSpeed;
	private double size = 10;
	private Main main;
	
	private int score = 0;
	
	private boolean dead;
	
	private int controlLeft;
	private int controlRight;
	
	private List<Color> colors;
	
	public Player(Main main, int number) {
		colors = new ArrayList<Color>();
		colors.add(new Color(0, 0, 255));
		colors.add(new Color(255, 0 , 0));
		colors.add(new Color(0, 255, 0));
		colors.add(new Color(255, 127, 0));
		colors.add(new Color(148, 0, 211));
		colors.add(new Color(255, 255, 0));		
		color = colors.get(number);
		this.main = main;
		String left = JOptionPane.showInputDialog("Left Key: ");
		String right = JOptionPane.showInputDialog("Right Key: ");
		if(left.toLowerCase().equals("left")) {
			this.controlLeft = KeyEvent.VK_LEFT;
			
		} else {
			this.controlLeft = KeyEvent.getExtendedKeyCodeForChar(left.toCharArray()[0]);
		}
		if(right.toLowerCase().equals("right")) {
			this.controlRight = KeyEvent.VK_RIGHT;
		} else {
			this.controlRight = KeyEvent.getExtendedKeyCodeForChar(right.toCharArray()[0]);
		}
		
		reset();
	}
	
	public void reset() {
		Random r = new Random();
		dead = false;
		this.x = r.nextInt(Main.WIDTH-300) + 150;
		this.y = r.nextInt(Main.HEIGHT-300) + 150;
		normalSpeed = 2.5;
		angleMove = 2;
		angle = r.nextDouble() * 360;
	}
	
	public void tick() {
		if(KeyInput.pressedKeys.contains(controlLeft)) angle-=angleMove+normalSpeed/3;
		if(KeyInput.pressedKeys.contains(controlRight)) angle+=angleMove+normalSpeed/3;
		
		speed = normalSpeed;
		
		rad = Math.toRadians(angle);
		dx = Math.cos(rad) * speed;
		dy = Math.sin(rad) * speed;
		
		x += dx;
		y += dy;
		
		if(new Random().nextInt(100) == 1 && !makeHole && main.isStartedDrawing()) {
			makeHole = true;
			timeHoleMake = System.currentTimeMillis();
		}
		
		if(makeHole) {
			if(System.currentTimeMillis()-timeHoleMake > 100 + (size*10)) {
				makeHole = false;
			}
		}
		if(colliding()) {
			dead = true;
		}
		
		for(int i = 0; i < debuffs.size(); i++) {
			Debuff d = debuffs.get(i);
			if(!d.isActivated()) {
				if(d.getP() == POWERUPTYPE.fat) {
					size = size*2;
				}else if(d.getP() == POWERUPTYPE.speed) {
					normalSpeed = normalSpeed * 2;
				}
				d.setActivated(true);
			}else if(System.currentTimeMillis()-4000 > d.getDebuffStart()) {
				if(d.getP() == POWERUPTYPE.fat) {
					size = size/2;
				}else if(d.getP() == POWERUPTYPE.speed) {
					normalSpeed = normalSpeed / 2;
				}
				debuffs.remove(d);
			}
		}
	}
	
	private boolean colliding() {
		boolean rgw = false;
		if((int)(x+dx*(size/4)) > main.getBiMap().getWidth()-1 || (int)(x+dx*(size/4)) < 0 || (int)(y+dy*(size/4)) > main.getBiMap().getHeight()-1 || (int)(y+dy*(size/4)) < 0) {
			//System.out.println("Colliding out of bounds");
			return true;
		}
		
		Color pixelColor = new Color(main.getBiMap().getRGB((int)(x+dx*(size/4)), (int)(y+dy*(size/4))));
		if(pixelColor.getRed() != 0 || pixelColor.getGreen() != 0 || pixelColor.getBlue() != 0) {
			rgw = true;
		}
		
		double tempAngle = angle;
		tempAngle-=(angleMove+normalSpeed/2) * 20;
		double temprad = Math.toRadians(tempAngle);
		double tempdx = Math.cos(temprad) * speed;
		double tempdy = Math.sin(temprad) * speed;
		
		if((int)(x+tempdx*(size/4)) > main.getBiMap().getWidth()-1 || (int)(x+tempdx*(size/4)) < 0 || (int)(y+tempdy*(size/4)) > main.getBiMap().getHeight() || (int)(y+tempdy*(size/4)) < 0) {
			return true;
		}
		
		pixelColor = new Color(main.getBiMap().getRGB((int)(x+tempdx*(size/4)), (int)(y+tempdy*(size/4))));
		if(pixelColor.getRed() != 0 || pixelColor.getGreen() != 0 || pixelColor.getBlue() != 0) {
			rgw = true;
			//System.out.println(pixelColor.getRed() + " " + pixelColor.getGreen() + " " +pixelColor.getBlue());
		}
		
		tempAngle = angle;
		tempAngle+=(angleMove+normalSpeed/2) * 20;
		temprad = Math.toRadians(tempAngle);
		tempdx = Math.cos(temprad) * speed;
		tempdy = Math.sin(temprad) * speed;
		
		if((int)(x+tempdx*(size/4)) > main.getBiMap().getWidth()-1 || (int)(x+tempdx*(size/4)) < 0 || (int)(y+tempdy*(size/4)) > main.getBiMap().getHeight() || (int)(y+tempdy*(size/4)) < 0) {
			return true;
		}
		
		pixelColor = new Color(main.getBiMap().getRGB((int)(x+tempdx*(size/4)), (int)(y+tempdy*(size/4))));
		if(pixelColor.getRed() != 0 || pixelColor.getGreen() != 0 || pixelColor.getBlue() != 0) {
			rgw = true;
			//System.out.println(pixelColor.getRed() + " " + pixelColor.getGreen() + " " +pixelColor.getBlue());
		}
		return rgw;
	}
	
	public void render(Graphics2D g) {
		if(main.isStartedDrawing() && !makeHole) {
			g = main.getBiMap().createGraphics();
		} else if(!main.isStartedDrawing()) {
			g.setFont(Main.font(20));
			g.setColor(color);
			String text = KeyEvent.getKeyText(controlLeft) + " - " + KeyEvent.getKeyText(controlRight);
			Main.drawTextWithShadow(text, g, color, (int)x - (Main.getFWidth(text, g)/2), (int)y-10, 1);
		}
		g.setColor(color);
		g.fillOval((int)(x-size/2), (int)(y-size/2), (int)size, (int)size);
		
		
	}

	public boolean isDead() {
		return dead;
	}

	public void setDead(boolean dead) {
		this.dead = dead;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}
	
	public double getSize() {
		return size;
	}
	
	public void setSize(double size) {
		this.size = size;
	}
}
