package Game;

import Game.Powerup.POWERUPTYPE;

public class Debuff {
	
	private long debuffStart;
	private POWERUPTYPE p;
	private boolean activated = false;
	
	public Debuff(POWERUPTYPE p) {
		debuffStart = System.currentTimeMillis();
		this.p = p;
	}
	
	public long getDebuffStart() {
		return debuffStart;
	}

	public POWERUPTYPE getP() {
		return p;
	}

	public boolean isActivated() {
		return activated;
	}

	public void setActivated(boolean activated) {
		this.activated = activated;
	}
	
}
