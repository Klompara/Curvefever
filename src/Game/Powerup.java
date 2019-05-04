package Game;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

public class Powerup {
	
	public enum POWERUPTYPE {
		speed,
		fat,
		border
	}
	
	private POWERUPTYPE type;
	private int x, y;
	private final int size = 50;
	
	private Color[] colors =  {Color.green, Color.red, Color.blue};
	
	public Powerup(int x, int y, POWERUPTYPE type) {
		this.type = type;
		this.x = x;
		this.y = y;
	}
	
	public boolean colliding(Player p) {
		double diffX = x - p.getX();
		double diffY = y - p.getY();
		double distance = Math.sqrt(Math.pow(diffX, 2) + Math.pow(diffY, 2));
		
		return (distance < size/2+p.getSize());
	}
	
	public void render(Graphics2D g) {
		g.setColor(colors[type.ordinal()]);
		g.fillOval(x-size/2, y-size/2, size, size);
		g.setColor(colors[type.ordinal()].darker());
		g.setStroke(new BasicStroke(3));
		g.drawOval(x-size/2, y-size/2, size, size);
		
		g.setFont(Main.font(20));
		Main.drawTextWithShadow(type.toString(), g, Color.white, x-Main.getFWidth(type.toString(), g)/2, y+5, 2);
	}

	public POWERUPTYPE getType() {
		return type;
	}
	
}
