package org.usfirst.frc1388;

public enum Position {
	LEFT(-1),
	RIGHT(1),
	CENTER(0);
	
	private int dir;
	
	private Position(int dir) {
		this.dir = dir;
	}
	
	// determine direction to turn based on starting position
	public int turnSame(int deg) { return deg * dir; }
	
	public int turnOpposite(int deg) { return deg * dir; }

}
