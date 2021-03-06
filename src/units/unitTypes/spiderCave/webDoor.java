package units.unitTypes.spiderCave;

import doodads.cave.webDoorDoodad;
import drawing.spriteSheet;
import drawing.spriteSheet.spriteSheetInfo;
import drawing.animation.animationPack;
import modes.mode;
import units.unit;
import units.unitType;

public class webDoor extends unit {
	
	////////////////
	/// DEFAULTS ///
	////////////////
	
	// Unit name
	public static String DEFAULT_UNIT_NAME = "webDoor";
	
	// Default exp given.
	private int DEFAULT_EXP_GIVEN = 10;
	
	// Health.
	private int DEFAULT_HP = 5000;
	
	// Default movespeed.
	private static int DEFAULT_UNIT_MOVESPEED = 1;
	
	// Default jump speed
	private static int DEFAULT_UNIT_JUMPSPEED = 10;
	
	// wolf sprite stuff.
	private static String DEFAULT_UNIT_SPRITESHEET = "images/doodads/farmLand/spiderCave/webDoor.png";
	
	// The actual type.
	private static unitType typeRef =
			new unitType(DEFAULT_UNIT_NAME,  // Name of unitType 
					new spriteSheet(new spriteSheetInfo(
							DEFAULT_UNIT_SPRITESHEET, 
							webDoorDoodad.DEFAULT_SPRITE_WIDTH, 
							webDoorDoodad.DEFAULT_SPRITE_HEIGHT,
							0,
							0
							)),
				     null,
				     webDoorDoodad.DEFAULT_TOPDOWN_WIDTH,
				     webDoorDoodad.DEFAULT_TOPDOWN_HEIGHT,
				     DEFAULT_UNIT_MOVESPEED, // Movespeed
				     DEFAULT_UNIT_JUMPSPEED // Jump speed
					);
	
	//////////////
	/// FIELDS ///
	//////////////
	// Webdoor doodad.
	private webDoorDoodad doodad;

	///////////////
	/// METHODS ///
	///////////////
	// Constructor
	public webDoor(int newX, int newY) {
		super(typeRef, newX, newY);
	
		// Set combat stuff.
		setCombatStuff();
		
		// Deal with animations
		animationPack unitTypeAnimations = new animationPack();

		// Set animations.
		setAnimations(unitTypeAnimations);
		
		// Set webDoor Doodad
		doodad = new webDoorDoodad(newX,newY,0);
		
		// Set unkillable.
		setKillable(false);

		// Set dimensions
		setHeight(getDefaultHeight());
		setWidth(getDefaultWidth());
		platformerHeight = webDoorDoodad.DEFAULT_PLATFORMER_HEIGHT;
		platformerWidth = webDoorDoodad.DEFAULT_PLATFORMER_WIDTH;
		topDownHeight = webDoorDoodad.DEFAULT_TOPDOWN_HEIGHT;
		topDownWidth = webDoorDoodad.DEFAULT_TOPDOWN_WIDTH;
		setHitBoxAdjustmentY(getDefaultHitBoxAdjustmentY());
	}
	
	// Combat defaults.
	public void setCombatStuff() {
	
		setHealthPoints(DEFAULT_HP);
		
	}
	
	// React to death.
	@Override
	public void reactToDeath() {
		doodad.destroy();
	}
	
	// React to pain.
	public void reactToPain() {
		//sound s = new sound(spiderSound1);
		//s.setPosition(getX(), getY(), soundRadius);
		//s.start();
	}
	
	public void updateUnit() {
	}
	
	///////////////////////////
	/// GETTERS AND SETTERS ///
	///////////////////////////
	
	// Get default width.
	public static int getDefaultWidth() {
		if(mode.getCurrentMode().equals("topDown")) {
			return webDoorDoodad.DEFAULT_TOPDOWN_WIDTH;
		}
		else {
			return webDoorDoodad.DEFAULT_PLATFORMER_WIDTH;
		}
	}
	
	// Get default height.
	public static int getDefaultHeight() {
		if(mode.getCurrentMode().equals("topDown")) {
			return webDoorDoodad.DEFAULT_TOPDOWN_HEIGHT;
		}
		else {
			return webDoorDoodad.DEFAULT_PLATFORMER_HEIGHT;
		}
	}
	
	// Get default hitbox adjustment Y.
	public static int getDefaultHitBoxAdjustmentY() {
		if(mode.getCurrentMode().equals("topDown")) {
			return webDoorDoodad.DEFAULT_TOPDOWN_ADJUSTMENT_Y;
		}
		else {
			return webDoorDoodad.DEFAULT_PLATFORMER_ADJUSTMENT_Y;
		}
	}
}
