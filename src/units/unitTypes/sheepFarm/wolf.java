package units.unitTypes.sheepFarm;

import java.util.ArrayList;

import drawing.spriteSheet;
import drawing.spriteSheet.spriteSheetInfo;
import drawing.animation.animation;
import drawing.animation.animationPack;
import modes.mode;
import sounds.sound;
import terrain.chunk;
import units.player;
import units.unit;
import units.unitType;
import units.unitCommands.commands.slashCommand;
import utilities.time;
import utilities.utility;

public abstract class wolf extends unit {
		
		////////////////
		/// DEFAULTS ///
		////////////////
	
		// Platformer real dimensions
		public static int DEFAULT_PLATFORMER_HEIGHT = 25;
		public static int DEFAULT_PLATFORMER_WIDTH = 25;
		public static int DEFAULT_PLATFORMER_ADJUSTMENT_Y = 0;
		public static int DEFAULT_SPRITE_ADJUSTMENT_X = 0;
		
		// TopDown real dimensions
		public static int DEFAULT_TOPDOWN_WIDTH = 19;
		public static int DEFAULT_TOPDOWN_HEIGHT = 19;
		public static int DEFAULT_TOPDOWN_ADJUSTMENT_Y = 2;
		
		// Default don't attack for
		public static float DEFAULT_DONT_ATTACK_FOR = 0f;
		
		// Sounds
		protected static String howl = "sounds/effects/animals/wolfHowl.wav";
		protected static String growl = "sounds/effects/animals/wolfGrowl.wav";
		protected static String bark1 = "sounds/effects/animals/wolfBark1.wav";
		protected static String bark2 = "sounds/effects/animals/wolfBark2.wav";
		protected long lastHowl = 0;
		protected float randomHowl = 0;
		protected float baseRandomHowl = 10f;
		
		//////////////
		/// FIELDS ///
		//////////////
		
		// Unit sprite stuff.
		private static spriteSheet DEFAULT_UPDOWN_SPRITESHEET = new spriteSheet(new spriteSheetInfo(
				"images/units/animals/wolfUpDown.png", 
				32, 
				64,
				0,
				0
				));
		private static spriteSheet DEFAULT_LEFTRIGHT_SPRITESHEET = new spriteSheet(new spriteSheetInfo(
				"images/units/animals/wolfLeftRight.png",
				64, 
				32,
				0,
				0
				));
		
		// Spritesheets
		protected spriteSheet upDownSpriteSheet;
		protected spriteSheet leftRightSpriteSheet;
		
		// Follow until range
		protected int followUntilRange = 10 + utility.RNG.nextInt(15);
		
		// Is the wolf the alpha of the group?
		private boolean alpha = false;
		
		// Is the wolf dosile?
		protected boolean dosile = false;
		
		// Is the wolf aggrod?
		protected boolean aggrod = false;
		protected long aggrodTime = 0;
		
		// Don't attack for
		protected float dontAttackFor = DEFAULT_DONT_ATTACK_FOR;
		
		// Claw attacking?
		protected boolean clawAttacking = false;
		protected boolean hasClawSpawned = false;
		protected boolean slashing = false;
		protected long startOfClawAttack = 0;
		private float spawnClawPhaseTime = .5f;
		protected float clawAttackEveryBase = 3f;
		private float clawAttackEvery = 0f;
		protected long lastClawAttack = 0;
		
		// Claw
		protected chunk currClaw = null;
		protected boolean hasStartedJumping = false;
		
		// Start rise and run
		protected double startX = 0;
		protected double startY = 0;
		
		// Claw attacking x and y
		protected int clawAttackingX = 0;
		protected int clawAttackingY = 0;
		
		// Jumping stuff
		protected int jumpingToX = 0;
		protected int jumpingToY = 0;
		protected boolean hasSlashed = false;
		protected boolean riseRunSet = false;
		protected double rise = 0;
		protected double run = 0;
		
		// Wolves we are in combat with
		int numWolves = 0;
		int numRedWolves = 0;
		int numBlackWolves = 0;
		int numYellowWolves = 0;
		
		///////////////
		/// METHODS ///
		///////////////
		// Constructor
		public wolf(unitType wolfType, int newX, int newY) {
			super(wolfType, newX, newY);
			
			// If topDown, take off collision
			if(mode.getCurrentMode().equals("topDown")) collisionOn = false;
			
			upDownSpriteSheet = getUpDownSpriteSheet();
			
			leftRightSpriteSheet = getLeftRightSpriteSheet();
			
			// Set wolf combat stuff.
			setCombatStuff();
			killsPlayer = true;
			
			// Set animations
			addAnimations();
			
			// Set dimensions
			setHeight(getDefaultHeight());
			setWidth(getDefaultWidth());
			platformerHeight = DEFAULT_PLATFORMER_HEIGHT;
			platformerWidth = DEFAULT_PLATFORMER_WIDTH;
			topDownHeight = DEFAULT_TOPDOWN_WIDTH;
			topDownWidth = DEFAULT_TOPDOWN_WIDTH;
		}
		
		// Add animations.
		public void addAnimations() {
			
			// Deal with animations
			animationPack unitTypeAnimations = new animationPack();
			
			// Jumping left animation.
			animation jumpingLeft = new animation("jumpingLeft", leftRightSpriteSheet.getAnimation(6), 4, 4, 1){{
				setRepeats(true);
			}};
			unitTypeAnimations.addAnimation(jumpingLeft);
			
			// Jumping down animation.
			animation jumpingDown = new animation("jumpingDown", upDownSpriteSheet.getAnimation(3), 2, 2, 1){{
				setRepeats(true);
			}};
			unitTypeAnimations.addAnimation(jumpingDown);
			
			// Jumping up animation.
			animation jumpingUp = new animation("jumpingUp", upDownSpriteSheet.getAnimation(3), 7, 7, 1){{
				setRepeats(true);
			}};
			unitTypeAnimations.addAnimation(jumpingUp);
			
			// Jumping right animation.
			animation jumpingRight = new animation("jumpingRight", leftRightSpriteSheet.getAnimation(2), 4, 4, 1){{
				setRepeats(true);
			}};
			unitTypeAnimations.addAnimation(jumpingRight);
			
			// Standing left animation.
			animation standingLeft = new animation("standingLeft", leftRightSpriteSheet.getAnimation(5), 0, 0, 1){{
				setRepeats(true);
			}};
			unitTypeAnimations.addAnimation(standingLeft);
			
			// Standing right animation.
			animation standingRight = new animation("standingRight", leftRightSpriteSheet.getAnimation(3), 0, 0, 1){{
				setRepeats(true);
			}};
			unitTypeAnimations.addAnimation(standingRight);
			
			// Running left animation.
			animation runningLeft = new animation("runningLeft", leftRightSpriteSheet.getAnimation(5), 0, 4, 1f){{
				setRepeats(true);
			}};
			unitTypeAnimations.addAnimation(runningLeft);		
			
			// Running right animation.
			animation runningRight = new animation("runningRight", leftRightSpriteSheet.getAnimation(1), 0, 4, 1f){{
				setRepeats(true);
			}};
			unitTypeAnimations.addAnimation(runningRight);
			
			// Standing up animation.
			animation standingUp = new animation("standingUp", upDownSpriteSheet.getAnimation(4), 5, 5, 1){{
				setRepeats(true);
			}};
			unitTypeAnimations.addAnimation(standingUp);
			
			// Standing down animation.
			animation standingDown = new animation("standingDown", upDownSpriteSheet.getAnimation(0), 0, 0, 1){{
				setRepeats(true);
			}};
			unitTypeAnimations.addAnimation(standingDown);
			
			// Running up animation.
			animation runningUp = new animation("runningUp", upDownSpriteSheet.getAnimation(2), 5, 8, 1f){{
				setRepeats(true);
			}};
			unitTypeAnimations.addAnimation(runningUp);
			
			// Running down animation.
			animation runningDown = new animation("runningDown", upDownSpriteSheet.getAnimation(2), 0, 3, 1f){{
				setRepeats(true);
			}};
			unitTypeAnimations.addAnimation(runningDown);
			
			// Sleeping animation
			animation sleepingStartLeft = new animation("sleepingStartLeft", leftRightSpriteSheet.getAnimation(4), 0, 2, 0.3f){{
				setRepeats(false);
			}};
			unitTypeAnimations.addAnimation(sleepingStartLeft);
			
			// Sleeping animation
			animation sleepingLoopLeft = new animation("sleepingLoopLeft", leftRightSpriteSheet.getAnimation(4), 3,3, 0.5f){{
				setRepeats(true);
			}};
			unitTypeAnimations.addAnimation(sleepingLoopLeft);
			
			// Sleeping animation
			animation sleepingStartRight = new animation("sleepingStartRight", leftRightSpriteSheet.getAnimation(0), 0, 2, 0.3f){{
				setRepeats(false);
			}};
			unitTypeAnimations.addAnimation(sleepingStartRight);
			
			// Sleeping animation
			animation sleepingLoopRight = new animation("sleepingLoopRight", leftRightSpriteSheet.getAnimation(0), 3,3, 0.5f){{
				setRepeats(true);
			}};
			unitTypeAnimations.addAnimation(sleepingLoopRight);
			
			// Set animations.
			setAnimations(unitTypeAnimations);
		
		}
		
		// Combat defaults.
		public abstract void setCombatStuff();
		
		// Assign a random alpha
		public void assignRandomAlpha(ArrayList<unit> units) {
			
			// Assign alpha after we all start attacking
			if(aggrod && time.getTime() - aggrodTime > dontAttackFor*1000) {
				
				// Get wolves from unit list
				ArrayList<wolf> wolves = new ArrayList<wolf>();
				for(int i = 0; i < units.size(); i++) {
					if(units.get(i) instanceof wolf) wolves.add((wolf)units.get(i));
				}
				
				// Make a random wolf alpha
				int random = utility.RNG.nextInt(wolves.size());
				boolean alphaAssignedAlready = false;
				for(int i = 0; i < wolves.size(); i++) {
					if((wolves.get(i)).isAlpha()) alphaAssignedAlready = true;
				}
				if(!alphaAssignedAlready) {
					
					if(aggrodTime != 0) {
						for(int i = 0; i < wolves.size(); i++) {
							wolves.get(i).aggrodTime = time.getTime();
						}
					}
					
					// Make alpha
					sound s = new sound(growl);
					s.setPosition(getIntX(), getIntY(), sound.DEFAULT_SOUND_RADIUS);
					s.start();
					wolves.get(random).setAlpha(true);
					wolves.get(random).setAlphaAnimations();
				}
			}
		}
		
		// React to pain.
		public void reactToPain() {
			// Play a bark on pain.
			sound s = new sound(bark2);
			s.setPosition(getIntX(), getIntY(), sound.DEFAULT_SOUND_RADIUS);
			s.start();
		}
		
		// Wolf random noises
		public void makeSounds() {
			
				// Create a new random growl interval
				/*float newRandomHowlInterval = baseRandomHowl + utility.RNG.nextInt(10);
				
				// Make the wolf howl
				if(randomHowl == 0f) {
					randomHowl = newRandomHowlInterval;
				}
				if(!dosile && !aggrod && time.getTime() - lastHowl > randomHowl*1000) {
					
					// Set the last time they howled
					lastHowl = time.getTime();
					randomHowl = newRandomHowlInterval;
					
					// Will it howl? 10% chance.
					int willItHowl = utility.RNG.nextInt(10);
					if(willItHowl == 1) {
						sound s = new sound(howl);
						s.setPosition(getIntX(), getIntY(), sound.DEFAULT_SOUND_RADIUS);
						s.start();
					}
				}*/
		}

		// Claw attack.
		public void clawAttack(int x, int y) {
			clawAttacking = true;
			hasClawSpawned = false;
			slashing = false;
			clawAttackingX = x;
			clawAttackingY = y;
			
			// Start of claw attack.
			startOfClawAttack = time.getTime();
		}
		
		// Charge units?
		public abstract void chargeUnits();
		
		// Spawn a trail?
		public abstract void spawnTrail();
		
		// Jumping finished
		public abstract void jumpingFinished();
		
		// Deal with jumping.
		public void dealWithJumping() {
			if(clawAttacking) {
				
				// Get current player.
				player currPlayer = player.getPlayer();
				
				// Jump to the location.
				if(isJumping()) {
					
					// Set rise/run
					if(!riseRunSet) {
						riseRunSet = true;
						float yDistance = (jumpingToY - getIntY());
						float xDistance = (jumpingToX - getIntX());
						float distanceXY = (float) Math.sqrt(yDistance * yDistance
								+ xDistance * xDistance);
						
						rise = 0;
						run = 0;
						
						if(distanceXY != 0) {
							rise = ((yDistance/distanceXY)*getJumpSpeed());
							run = ((xDistance/distanceXY)*getJumpSpeed());
						}
						startX = getDoubleX();
						startY = getDoubleY();
					}
					
					// Charge units to position.
					chargeUnits();
					
					// Spawn rocks
					spawnTrail();
					
					setDoubleX(getDoubleX() + run);
					setDoubleY(getDoubleY() + rise);
					
					// Don't let him not move at all or leave region.
					if((run == 0 && rise == 0) || ((Math.abs(jumpingToX - getIntX()) < getJumpSpeed() && Math.abs(jumpingToY - getIntY()) < getJumpSpeed()))) {
						if(currClaw != null) {
							setDoubleX(jumpingToX);
							setDoubleY(jumpingToY);
							clawDestroy();
						}
						if(getAllCommands() != null && getAllCommands().size() > 0 && getAllCommands().get(0) instanceof slashCommand) setCurrentCommandComplete(true);
						setJumping(false);
						clawAttacking = false;
						hasStartedJumping = false;
					}
					
					// If slashing, hurt the player.
					if(slashing && !hasSlashed && currPlayer.isWithin(getIntX(), getIntY(), getIntX() + getWidth(), getIntY() + getHeight())) {
						hasSlashed = true;
						slashing = false;
					}
				}
				else {
					if(mode.getCurrentMode().equals("platformer")) collisionOn = true;
					setStuck(false);
					jumpingFinished();
				}
			}
			else {
				if(mode.getCurrentMode().equals("platformer")) collisionOn = true;
				setStuck(false);
				jumpingFinished();
			}
		}
		
		// Claw destroy
		public void clawDestroy() {
			currClaw.destroy();
		}

		// Remove claw.
		@Override
		public void reactToDeath() {
			if(currClaw != null) currClaw.destroy();
		}
		
		// Jump
		public void slashTo(chunk c) {
			stopMove("all");
			collisionOn = false;
			setStuck(true);
			sound s = new sound(bark1);
			s.setPosition(getIntX(), getIntY(), sound.DEFAULT_SOUND_RADIUS);
			s.start();
			
			// Jump there
			jumpingToX = c.getIntX() + c.getWidth()/2 - getWidth()/2;
			jumpingToY = c.getIntY() + c.getHeight()/2 - getHeight()/2;
			
			setJumping(true);
			slashing = true;
			hasSlashed = false;
			riseRunSet = false;
		}
		
		// Slash to a point (issued by the command function)
		public void slashTo(int x, int y) {
			clawAttack(x,y);
		}
		
		// Spawn claw
		public abstract void spawnClaw(int x, int y);
		
		// Deal with claw attacks.
		public void dealWithClawAttacks() {
			if(clawAttacking) {
				
				// Spawn claw phase.
				if(!hasClawSpawned && time.getTime() - startOfClawAttack < getSpawnClawPhaseTime()*1000) {
					hasClawSpawned = true;
					spawnClaw(clawAttackingX, clawAttackingY);
				}
				
				// Slashing phase.
				else if(hasClawSpawned && time.getTime() - startOfClawAttack > getSpawnClawPhaseTime()*1000 && !hasStartedJumping) {
					hasStartedJumping = true;
					slashTo((chunk)currClaw);
				}
			}
		}
		
		// Change combat based on whose alpha
		public abstract void changeCombat();
		
		// Deal with animations.
		@Override
		public void dealWithAnimations(int moveX, int moveY) {
				
			// No hitboxadjustment.
			setHitBoxAdjustmentY(DEFAULT_PLATFORMER_ADJUSTMENT_Y);
			setHitBoxAdjustmentX(0);
			if(isJumping() || clawAttacking) {
				if(clawAttacking && !isJumping()) {
					animate("standing" + getFacingDirection());
				}
				else {
					animate("jumping" + getFacingDirection());
				}
			}
			else if(isMoving()) {
				animate("running" + getFacingDirection());
			}
			else {
				animate("standing" + getFacingDirection());
			}
		}
		
		// Get updown spritesheet
		public spriteSheet getUpDownSpriteSheet() {
			return DEFAULT_UPDOWN_SPRITESHEET;
		}
		
		// Get leftRight spritesheet
		public spriteSheet getLeftRightSpriteSheet() {
			return DEFAULT_LEFTRIGHT_SPRITESHEET;
		}	
		
		// wolf AI moves wolf around for now.
		public void updateUnit() {
			
			// If player is in radius, follow player, attacking.
			player currPlayer = player.getPlayer();
			
			// Claw attack
			dealWithClawAttacks();
			dealWithJumping();
			
			/* Claw attack
			clawAttackEvery = clawAttackEveryBase + 0.1f*(float)utility.RNG.nextInt(5);
			lastClawAttack = time.getTime();
			unfollow();
			clawAttack(currPlayer);
			*/
			
		}
		
		///////////////////////////
		/// GETTERS AND SETTERS ///
		///////////////////////////
		
		// Wolf kill width and height changes based on how they're facing
		@Override
		public int getKillWidth() {
			if(getFacingDirection().equals("Left") || getFacingDirection().equals("Right")) {
				return getWidth() + 36;
			}
			else {
				return getWidth()+4;
			}
		}
		
		// Wolf kill width and height changes based on how they're facing
		@Override
		public int getKillAdjustY() {
			if(getFacingDirection().equals("Left") || getFacingDirection().equals("Right")) {
				return 5;
			}
			else {
				return 0;
			}
		}
		
		
		@Override
		public int getKillHeight() {
			if(getFacingDirection().equals("Up") || getFacingDirection().equals("Down")) {
				return getHeight() + 26;
			}
			else {
				return getHeight() - 2;
			}
		}
		
		
		// Get default width.
		public static int getDefaultWidth() {
			if(mode.getCurrentMode().equals("topDown")) {
				return DEFAULT_TOPDOWN_WIDTH;
			}
			else {
				return DEFAULT_PLATFORMER_WIDTH;
			}
		}
		
		// Get hitboxadjustment
		@Override
		public int getHitBoxAdjustmentY() {
			if(mode.getCurrentMode().equals("topDown")) {
				return DEFAULT_TOPDOWN_ADJUSTMENT_Y;
				
			}
			else {
				return 0;
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
		
		
		public abstract void setAlphaAnimations();
		
		public boolean isDosile() {
			return dosile;
		}

		public void setDosile(boolean dosile) {
			this.dosile = dosile;
		}

		public boolean isAlpha() {
			return alpha;
		}

		public void setAlpha(boolean alpha) {
			this.alpha = alpha;
			this.changeCombat();
			this.setAlphaAnimations();
		}

		public float getSpawnClawPhaseTime() {
			return spawnClawPhaseTime;
		}

		public void setSpawnClawPhaseTime(float spawnClawPhaseTime) {
			this.spawnClawPhaseTime = spawnClawPhaseTime;
		}

		public float getClawAttackEvery() {
			return clawAttackEvery;
		}

		public void setClawAttackEvery(float clawAttackEvery) {
			this.clawAttackEvery = clawAttackEvery;
		}

}