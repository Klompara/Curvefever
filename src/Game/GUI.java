package Game;

import java.awt.Graphics2D;

public class GUI {

	private Main main;
	
	public GUI (Main main) {
		this.main = main;
	}
	
	public void tick() {
		
	}

	public void render(Graphics2D g) {
		g.setFont(Main.font(20));
		String text;
		for(int i = 0; i < main.getPlayerList().size(); i++) {
			text = "Player " + i + ": " + main.getPlayerList().get(i).getScore();
			Main.drawTextWithShadow(text, g, main.getPlayerList().get(i).getColor(), 20, 20+i*25, 2);	
		}
	}
	
}