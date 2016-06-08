package units.unitTypes.farmLand;

import java.util.Random;

import drawing.camera;
import modes.mode;
import sounds.sound;
import units.animalType;
import units.humanType;
import units.unit;
import units.unitType;
import utilities.time;
import utilities.utility;
import zones.zone;

public class sheep extends unit {
	
	// Default dimensions.
	private static int DEFAULT_PLATFORMER_HEIGHT = 32;
	private static int DEFAULT_WIDTH = 32;
	private static int DEFAULT_TOPDOWN_HEIGHT = 7;
	
	// Platformer and topdown default adjustment
	private static int DEFAULT_PLATFORMER_ADJUSTMENT_Y = 0;
	private static int DEFAULT_TOPDOWN_ADJUSTMENT_Y = 8;
	
	// How far do the sheep patrol
	private static int DEFAULT_PATROL_RADIUS = 100;
	
	////////////////
	/// DEFAULTS ///
	////////////////
	
	// Default name.
	private static String DEFAULT_SHEEP_NAME = "sheep";
	
	// Default movespeed.
	private static int DEFAULT_SHEEP_MOVESPEED = 1;
	
	// Default jump speed
	private static int DEFAULT_SHEEP_JUMPSPEED = 10;
	
	// SHEEP sprite stuff.
	private static String DEFAULT_SHEEP_SPRITESHEET = "images/units/animals/sheep.png";
	
	// The actual type.
	private static unitType sheepType =
			new animalType( "sheep",  // Name of unitType 
						 DEFAULT_SHEEP_SPRITESHEET,
					     DEFAULT_SHEEP_MOVESPEED, // Movespeed
					     DEFAULT_SHEEP_JUMPSPEED // Jump speed
						);	
	
	// Sounds
	private sound bleet1 = new sound("C:/Users/Andrew Jenkins/Desktop/Games/No Bullshit/sounds/effects/animals/sheep1.wav");
	private sound bleet2 = new sound("C:/Users/Andrew Jenkins/Desktop/Games/No Bullshit/sounds/effects/animals/sheep2.wav");
	
	//////////////
	/// FIELDS ///
	//////////////
	
	// AI movement.
	private Long AILastCheck = 0l; // milliseconds
	private Float randomMove = 1f; // seconds
	private Float randomStop = 0.5f;
	private int startX = 0;
	private int startY = 0;
	private int patrolRadius = DEFAULT_PATROL_RADIUS;
	
	// AI sounds.
	private Float randomBleet = 0f;
	private Float lastBleet = 0f;
	
	///////////////
	/// METHODS ///
	///////////////
	// Constructor
	public sheep(int newX, int newY) {
		super(sheepType, newX, newY);
		
		// Set AI start X and Y
		startX = newX;
		startY = newY;
		
		// Make adjustments on hitbox if we're in topDown.
		if(mode.getCurrentMode().equals("topDown")) {
			height = DEFAULT_TOPDOWN_HEIGHT;
			width = DEFAULT_WIDTH;
			setHitBoxAdjustmentY(DEFAULT_TOPDOWN_ADJUSTMENT_Y);
		}
		else {
			height = DEFAULT_PLATFORMER_HEIGHT;
			width = DEFAULT_WIDTH;
			setHitBoxAdjustmentY(DEFAULT_PLATFORMER_ADJUSTMENT_Y);
		}
	}
	
	// Make sure the movement is within a certain radius.
	public void checkMovement(String direction) {
			if(getX() < startX - patrolRadius) moveUnit("right");
			else if(getX() + width > startX + patrolRadius)  moveUnit("left");
			else if(getY() < startY - patrolRadius) moveUnit("down");
			else if(getY() + height > startY + patrolRadius) moveUnit("up");
			else moveUnit(direction);
	}
	
	// SHEEP AI moves SHEEP around for now.
	public void AI() {
		
		// Sheep make sounds
		if(randomBleet == 0f) randomBleet = 0.5f + utility.RNG.nextInt(8)*0.25f;
		if(time.getTime() - lastBleet > randomBleet) {
			bleet1.playSound();
		}
		
		// Move SHEEP in a random direction every interval.
		if(time.getTime() - AILastCheck > randomMove*1000) {
			AILastCheck = time.getTime();
			int random = utility.RNG.nextInt(4);
			if(random==0) checkMovement("left");
			if(random==1) checkMovement("right");
			if(random==2) checkMovement("down");
			if(random==3) checkMovement("up");
			randomStop = 0.5f + utility.RNG.nextInt(8)*0.25f;
		}
		
		// Stop sheep after a fraction of a second
		if(isMoving() && time.getTime() - AILastCheck > randomStop*1000) {
			randomMove = 2f + utility.RNG.nextInt(9)*0.5f;
			AILastCheck = time.getTime();
			stopMove("all");
		}
	}
}
