package units;

import drawing.spriteSheet;
import drawing.spriteSheet.spriteSheetInfo;
import drawing.animation.animation;
import drawing.animation.animationPack;

public class humanType extends unitType {
	
	/////////////////////////
	////// DEFAULTS /////////
	/////////////////////////
	
	// Default dimensions.
	public static int DEFAULT_UNIT_WIDTH = 20;
	public static int DEFAULT_UNIT_HEIGHT = 20;
	
	// Default sprite stuff
	public static int DEFAULT_SPRITE_WIDTH = 64;
	public static int DEFAULT_SPRITE_HEIGHT = 64;
	private static int DEFAULT_SPRITE_ADJUSTMENT_X = 0;
	private static int DEFAULT_SPRITE_ADJUSTMENT_Y = 6;
	
	// Default movespeed.
	static float DEFAULT_HUMAN_MOVESPEED = 3f;
	
	///////////////
	/// METHODS ///
	///////////////
	
	public humanType(String newName, String spriteSheetLocation, float newMoveSpeed, int newJumpSpeed) {
		super(newName, 
				new spriteSheet(new spriteSheetInfo(
				spriteSheetLocation, 
				DEFAULT_SPRITE_WIDTH, 
				DEFAULT_SPRITE_HEIGHT,
				DEFAULT_SPRITE_ADJUSTMENT_X,
				DEFAULT_SPRITE_ADJUSTMENT_Y
				)),
				null,
				DEFAULT_UNIT_WIDTH,	   // Width
				DEFAULT_UNIT_HEIGHT,	   // Height
			    newMoveSpeed, // Movespeed
			    newJumpSpeed // Jump speed
			    );
		
		// Deal with animations
		animationPack unitTypeAnimations = new animationPack();
		
		// Jumping left animation.
		animation jumpingLeft = new animation("jumpingLeft", getUnitTypeSpriteSheet().getAnimation(1), 5, 5, 1){{
			setRepeats(true);
		}};
		unitTypeAnimations.addAnimation(jumpingLeft);
		
		// Jumping up animation.
		animation jumpingUp = new animation("jumpingUp", getUnitTypeSpriteSheet().getAnimation(0), 5, 5, 1){{
			setRepeats(true);
		}};
		unitTypeAnimations.addAnimation(jumpingUp);
		
		// Jumping down animation.
		animation jumpingDown = new animation("jumpingDown", getUnitTypeSpriteSheet().getAnimation(2), 5, 5, 1){{
			setRepeats(true);
		}};
		unitTypeAnimations.addAnimation(jumpingDown);
		
		// Jumping right animation.
		animation jumpingRight = new animation("jumpingRight", getUnitTypeSpriteSheet().getAnimation(3), 5, 5, 1){{
			setRepeats(true);
		}};
		unitTypeAnimations.addAnimation(jumpingRight);
		
		// Standing left animation.
		animation standingLeft = new animation("standingLeft", getUnitTypeSpriteSheet().getAnimation(9), 0, 0, 1){{
			setRepeats(true);
		}};
		unitTypeAnimations.addAnimation(standingLeft);
		
		// Standing up animation.
		animation standingUp = new animation("standingUp", getUnitTypeSpriteSheet().getAnimation(8), 0, 0, 1){{
			setRepeats(true);
		}};
		unitTypeAnimations.addAnimation(standingUp);
		
		// Standing right animation.
		animation standingRight = new animation("standingRight", getUnitTypeSpriteSheet().getAnimation(11), 0, 0, 1){{
			setRepeats(true);
		}};
		unitTypeAnimations.addAnimation(standingRight);
		
		// Standing down animation.
		animation standingDown = new animation("standingDown", getUnitTypeSpriteSheet().getAnimation(10), 0, 0, 1){{
			setRepeats(true);
		}};
		unitTypeAnimations.addAnimation(standingDown);
		
		// Running left animation.
		animation runningLeft = new animation("runningLeft", getUnitTypeSpriteSheet().getAnimation(9), 1, 8, 0.75f){{
			setRepeats(true);
		}};
		unitTypeAnimations.addAnimation(runningLeft);		
		
		// Running up animation.
		animation runningUp = new animation("runningUp", getUnitTypeSpriteSheet().getAnimation(8), 1, 8, 0.75f){{
			setRepeats(true);
		}};
		unitTypeAnimations.addAnimation(runningUp);
		
		// Running right animation.
		animation runningRight = new animation("runningRight", getUnitTypeSpriteSheet().getAnimation(11), 1, 8, 0.75f){{
			setRepeats(true);
		}};
		unitTypeAnimations.addAnimation(runningRight);
		
		// Running down animation.
		animation runningDown = new animation("runningDown", getUnitTypeSpriteSheet().getAnimation(10), 1, 8, 0.75f){{
			setRepeats(true);
		}};
		unitTypeAnimations.addAnimation(runningDown);
		
		// Set animations.
		setAnimations(unitTypeAnimations);
	}
}