package units.unitCommands.commands;

import units.unitCommand;
import units.unitCommands.positionedCommand;

public class moveCommand extends positionedCommand {
	
	// Constructor
	public moveCommand(double x, double y) {
		super("move",x,y);
	}

	public moveCommand(moveCommand unitCommand) {
		super("move",unitCommand.getX(),unitCommand.getY());
	}
}

