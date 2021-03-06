package effects.interfaceEffects;

import java.awt.Graphics;

import drawing.gameCanvas;
import drawing.spriteSheet;
import drawing.spriteSheet.spriteSheetInfo;
import drawing.animation.animation;
import drawing.animation.animationPack;
import effects.effect;
import effects.effectType;
import modes.mode;
import utilities.time;

public class textBlurb extends effect {
	
	// Default dimensions.
	public static int DEFAULT_SPRITE_WIDTH = 15;
	public static int DEFAULT_SPRITE_HEIGHT = 12;
	
	// Platformer real dimensions
	public static int DEFAULT_PLATFORMER_HEIGHT = DEFAULT_SPRITE_WIDTH;
	public static int DEFAULT_PLATFORMER_WIDTH = DEFAULT_SPRITE_HEIGHT;
	public static int DEFAULT_PLATFORMER_ADJUSTMENT_Y = 0;
	
	// TopDown real dimensions
	public static int DEFAULT_TOPDOWN_HEIGHT = DEFAULT_SPRITE_WIDTH;
	public static int DEFAULT_TOPDOWN_WIDTH = DEFAULT_SPRITE_HEIGHT;
	public static int DEFAULT_TOPDOWN_ADJUSTMENT_Y = 0;
	
	////////////////
	/// DEFAULTS ///
	////////////////
	
	// Default name.
	private static String DEFAULT_EFFECT_NAME = "textBlurb";
	
	// Effect sprite stuff.
	private static String DEFAULT_EFFECT_SPRITESHEET = "images/effects/interface/" + DEFAULT_EFFECT_NAME + ".png";
	
	// Duration
	private static float DEFAULT_ANIMATION_DURATION = 3f; // multiple of 0.25f
	
	// The actual type.
	private static effectType theEffectType =
			new effectType(DEFAULT_EFFECT_NAME,
					new spriteSheet(new spriteSheetInfo(
							DEFAULT_EFFECT_SPRITESHEET, 
							DEFAULT_SPRITE_WIDTH, 
							DEFAULT_SPRITE_HEIGHT,
							0,
							0
							)),
							DEFAULT_ANIMATION_DURATION);	
	
	//////////////
	/// FIELDS ///
	//////////////
	private boolean playedOnce = false;
	
	///////////////
	/// METHODS ///
	///////////////
	// Constructor
	public textBlurb(int newX, int newY) {
		super(theEffectType, newX, newY);
		
		// Duration
		this.setHasATimer(false);
		
		// Draw in front
		this.forceInFront = true;
		
		// Set background
		setBackgroundDoodad(true);
		
		// Deal with animations.
		// Set-up animations.
		animationPack newAnimationPack =  new animationPack();
		animation startAnimation = new animation("startAnimation", 
					theEffectType.getEffectTypeSpriteSheet().getAnimation(0), 
					0, 
					3, 
					0.17f); 
		
		animation pulseAnimation = new animation("pulseAnimation", 
				theEffectType.getEffectTypeSpriteSheet().getAnimation(0), 
				4, 
				7, 
				1.25f){{
					setRepeats(true);
				}}; 
		animation endAnimation = new animation("endAnimation", 
				theEffectType.getEffectTypeSpriteSheet().getAnimation(0), 
				8, 
				11, 
				0.17f); 
		newAnimationPack.addAnimation(startAnimation);
		newAnimationPack.addAnimation(pulseAnimation);
		newAnimationPack.addAnimation(endAnimation);
		animations = newAnimationPack;
		
		// Set the animation.
		setCurrentAnimation(startAnimation);
		
		// Make adjustments on hitbox if we're in topDown.
		setHeight(getDefaultHeight());
		setWidth(getDefaultWidth());
		setHitBoxAdjustmentY(getDefaultHitBoxAdjustmentY());

	}
	
	// Respond to ending
	@Override
	public void respondToFrame(int j) {
		// We've played the start already.
		if(time.getTime() - getCurrentAnimation().getStartTime() > getCurrentAnimation().getTimeToComplete()*1000
				&& getCurrentAnimation().getName().equals("startAnimation")) {
			setCurrentAnimation(animations.getAnimation("pulseAnimation"));
		}
		
		// If we are done the end animation, die.
		if(time.getTime() - getCurrentAnimation().getStartTime() > getCurrentAnimation().getTimeToComplete()*1000
				&& getCurrentAnimation().getName().equals("endAnimation")) {
			if(getAttachedObject() != null) getAttachedObject().unnattachFromObject(this);
			this.destroy();
		}
	}
	
	// End
	public void end() {
		setCurrentAnimation(animations.getAnimation("endAnimation"));
	}
	
	// Respond to destroy
	@Override
	public void respondToDestroy() {
	}
	
	// Draw the effect
	@Override
	public void drawObject(Graphics g) {
		
		// Of course only draw if the animation is not null.
		if(getCurrentAnimation() != null) {
			g.drawImage(getCurrentAnimation().getCurrentFrame(), 
					getDrawX(), 
					getDrawY(), 
					(int)(gameCanvas.getScaleX()*getObjectSpriteSheet().getSpriteWidth()), 
					(int)(gameCanvas.getScaleY()*getObjectSpriteSheet().getSpriteHeight()), 
					null);
		}
	}
	
	
	///////////////////////////
	/// GETTERS AND SETTERS ///
	///////////////////////////
	
	// Get default width.
	public static int getDefaultWidth() {
		if(mode.getCurrentMode().equals("topDown")) {
			return DEFAULT_TOPDOWN_WIDTH;
		}
		else {
			return DEFAULT_PLATFORMER_WIDTH;
		}
	}
	
	// Get default height.
	public static int getDefaultHeight() {
		if(mode.getCurrentMode().equals("topDown")) {
			return DEFAULT_TOPDOWN_HEIGHT;
		}
		else {
			return DEFAULT_PLATFORMER_HEIGHT;
		}
	}
	
	// Get default hitbox adjustment Y.
	public static int getDefaultHitBoxAdjustmentY() {
		if(mode.getCurrentMode().equals("topDown")) {
			return DEFAULT_TOPDOWN_ADJUSTMENT_Y;
		}
		else {
			return DEFAULT_PLATFORMER_ADJUSTMENT_Y;
		}
	}

}
