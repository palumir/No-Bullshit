package units.unitCommands;

import units.unitCommand;

public abstract class positionedCommand extends unitCommand {
	
	// Where do we move to?
	private double x;
	private double y;
	
	// Constructor
	public positionedCommand(String s, double x, double y) {
		super(s);
		this.setX(x);
		this.setY(y);
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

}

