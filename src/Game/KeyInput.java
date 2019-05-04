package Game;


import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

public class KeyInput extends KeyAdapter implements KeyListener{

	private Main main;
	
	public KeyInput(Main main) {
		this.main = main;
	}
	
	public static List<Integer> pressedKeys = new ArrayList<Integer>();
	
	public void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();
		if(main.getCurrentState() == Main.GAMESATES.MAINMENU) {
			if(key == KeyEvent.VK_ENTER) {
				main.setCurrentState(Main.GAMESATES.PLAY);
			}
		} else if(main.getCurrentState() == Main.GAMESATES.PLAY) {
			if(!pressedKeys.contains(key)) {
				pressedKeys.add(key);
			}
		}
	}

	public void keyReleased(KeyEvent e) {
		int key = e.getKeyCode();
		if(main.getCurrentState() == Main.GAMESATES.PLAY) {
			if(pressedKeys.contains(key)) {
				for(int i = 0; i < pressedKeys.size(); i++) {
					if(pressedKeys.get(i) == key) {
						pressedKeys.remove(i);
					}
				}
			}
		}
	}
}
