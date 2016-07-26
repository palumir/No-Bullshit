package units.bosses;

import java.util.ArrayList;

import doodads.sheepFarm.fireLog;
import doodads.sheepFarm.woolPiece;
import drawing.spriteSheet;
import drawing.animation.animation;
import drawing.animation.animationPack;
import effects.effectTypes.lightningStrike;
import effects.effectTypes.poisonBall;
import effects.effectTypes.spinningFireLog;
import drawing.spriteSheet.spriteSheetInfo;
import interactions.event;
import interactions.interactBox;
import interactions.textSeries;
import modes.mode;
import sounds.music;
import sounds.sound;
import terrain.chunk;
import units.boss;
import units.player;
import units.unitType;
import units.unitTypes.farmLand.sheepFarm.sheep;
import utilities.intTuple;
import utilities.saveState;
import utilities.stringUtils;
import utilities.time;
import zones.farmLand.sheepFarm;
import zones.farmLand.tombZone;

public class playerOne extends boss {
	
	// Platformer real dimensions
	public static int DEFAULT_PLATFORMER_HEIGHT = 18;
	public static int DEFAULT_PLATFORMER_WIDTH = 20;
	public static int DEFAULT_PLATFORMER_ADJUSTMENT_Y = 0;
	
	// TopDown real dimensions
	public static int DEFAULT_TOPDOWN_HEIGHT = 18;
	public static int DEFAULT_TOPDOWN_WIDTH = 20;
	public static int DEFAULT_TOPDOWN_ADJUSTMENT_Y = 4;
	
	////////////////
	/// DEFAULTS ///
	////////////////
	
	// Default name.
	private static String DEFAULT_UNIT_NAME = "??";
	
	// Default movespeed.
	private static float DEFAULT_UNIT_MOVESPEED = 4.5f;
	
	// Default jump speed
	private static int DEFAULT_UNIT_JUMPSPEED = 10;
	
	// FARLSWORTH sprite stuff.
	private static String DEFAULT_FARLSWORTH_SPRITESHEET = "images/units/animals/sheep.png";
	
	// Sounds
	private static String bellToll = "sounds/effects/horror/bellToll.wav";
	
	// The actual type.
	private static unitType sheepType  =
			new unitType(DEFAULT_UNIT_NAME,  // Name of unitType 
					  new spriteSheet(new spriteSheetInfo(
							"images/units/animals/sheep.png", 
							90, 
							90,
							0,
							DEFAULT_TOPDOWN_ADJUSTMENT_Y
							)),
					     null,
					     DEFAULT_TOPDOWN_WIDTH,
					     DEFAULT_TOPDOWN_HEIGHT,
					     DEFAULT_UNIT_MOVESPEED, // Movespeed
					     DEFAULT_UNIT_JUMPSPEED // Jump speed
						);	
	
	//////////////
	/// FIELDS ///
	//////////////
	
	// Interaction
	private interactBox interactSequence;
	
	// Path to follow on interaction.
	private ArrayList<intTuple> p;
	
	// Are we in boss fight mode?
	private boolean bossFight = false;
	
	// What part of the sequence are we at?
	private int sequencePart = 0;
	
	// Current player one.
	public static playerOne currentPlayerOne;
	
	// Scenes
	private boolean shadowElevatorSceneInProgress = false;
	
	// Events
	private event pastShadowElevator;
	
	// Have we revealed who this character is?
	private event playerOneHasBeenRevealed; // Player one is the bad player (in regards to what the hero decides) 
	private event playerOneIsTurner; // Who is player one? Is it Brady or Turner? TODO: placeholder names. Brady's a faggot.
	
	///////////////
	/// METHODS ///
	///////////////
	
	// Create interact sequence
	public interactBox makeNormalInteractSequence() {
	
		// Placeholder for each individual textSeries.
		textSeries s = null;
		
		// Start.
		textSeries startOfConversation = null;
	
		if(shadowElevatorSceneInProgress) {
			
			// Start of conversation.
			startOfConversation = new textSeries(null, "Can you feel the shadows slinking around you?");
			s = startOfConversation.addChild(null, "They are getting closer and closer.");
			s = s.addChild(null, "Smell their aroma.");
			s = s.addChild(null, "Hearken their call.");
			s = s.addChild(null, "Let them embrace you.");
			s = s.addChild(null, "But do not get consumed by them.");
			s = s.addChild(null, "View them as an opportunity.");
			s = s.addChild(null, "An opportunity to test your power of will.");
			s = s.addChild(null, "He wants to know if you are strong ...");
			s = s.addChild(null, "What interests me is far more important than that.");
			s = s.addChild(null, "I want you to know if you understand.");
			s.setEnd();
		}
			
			
		return new interactBox(startOfConversation, stringUtils.toTitleCase(DEFAULT_UNIT_NAME), true);
	}
	
	// Booleans
	private long waitStart = 0;
	private float waitFor = 0;
	private boolean waiting = false;
	
	// Do interact stuff.
	public void doInteractStuff() {
		
		// Load player.
		player currPlayer = player.getCurrentPlayer();
		
		// Scenes.
		if(shadowElevatorSceneInProgress) {
			
			// Hold the player for a bit.
			if(sequencePart == 0) {
				waitStart = time.getTime();
				waitFor = 5f;
				sequencePart++;
			}
			
			// Initiate the dialogue
			if(sequencePart == 1  && time.getTime() - waitStart > waitFor*1000) {
				currentPlayerOne.interactSequence = currentPlayerOne.makeNormalInteractSequence();
				if(interactBox.getCurrentDisplay() != null) {
					interactBox.getCurrentDisplay().toggleDisplay();
				}
				currentPlayerOne.interactSequence.toggleDisplay();
				currentPlayerOne.interactSequence.setUnescapable(true);
				player.getCurrentPlayer().stopMove("all");
				sequencePart++;
			}
			
			// Fade slowly.
			if(sequencePart == 2 && interactSequence!=null && interactSequence.getTheText()!=null && interactSequence.getTheText().getTextOnPress()!=null &&
					interactSequence.getTheText().getTextOnPress().contains("Can you feel")) {
				tombZone.zoneFog.fadeTo(.4f, 1);
				sequencePart++;
			}
			if(sequencePart == 3 && interactSequence!=null && interactSequence.getTheText()!=null && interactSequence.getTheText().getTextOnPress()!=null &&
					interactSequence.getTheText().getTextOnPress().contains("closer and closer")) {
				tombZone.zoneFog.fadeTo(.5f, 1);
				sequencePart++;
			}
			if(sequencePart == 4 && interactSequence!=null && interactSequence.getTheText()!=null && interactSequence.getTheText().getTextOnPress()!=null &&
					interactSequence.getTheText().getTextOnPress().contains("Smell")) {
				tombZone.zoneFog.fadeTo(.6f, 1);
				sequencePart++;
			}
			if(sequencePart == 5 && interactSequence!=null && interactSequence.getTheText()!=null && interactSequence.getTheText().getTextOnPress()!=null &&
					interactSequence.getTheText().getTextOnPress().contains("Hearken")) {
				tombZone.zoneFog.fadeTo(.7f, 1);
				sequencePart++;
			}
			if(sequencePart == 6 && interactSequence!=null && interactSequence.getTheText()!=null && interactSequence.getTheText().getTextOnPress()!=null &&
					interactSequence.getTheText().getTextOnPress().contains("embrace")) {
				tombZone.zoneFog.fadeTo(.8f, 1);
				sequencePart++;
			}
			if(sequencePart == 7 && interactSequence!=null && interactSequence.getTheText()!=null && interactSequence.getTheText().getTextOnPress()!=null &&
					interactSequence.getTheText().getTextOnPress().contains("consumed")) {
				tombZone.zoneFog.fadeTo(.9f, 1);
				sequencePart++;
			}
			if(sequencePart == 8 && interactSequence!=null && interactSequence.getTheText()!=null && interactSequence.getTheText().getTextOnPress()!=null &&
					interactSequence.getTheText().getTextOnPress().contains("opportun")) {
				tombZone.zoneFog.fadeTo(1f, 1);
				sequencePart++;
			}
			
			// We are done our talk.
			if(sequencePart == 9 && interactSequence!=null && interactSequence.getTheText()!=null && interactSequence.getTheText().getTextOnPress()!=null &&
					interactSequence.getTheText().getTextOnPress().contains("understand")) {
				
				// Wait a bit.
				waitFor = 3f;
				waitStart = time.getTime();
				sequencePart++;
			}
			
			// Reveal elevator.
			if(sequencePart == 10 && time.getTime() - waitStart > waitFor*1000) {
				
				// Create elevator and set fog.
				tombZone.zoneFog.setTo(1);
				if(interactSequence != null) {
					interactSequence.setUnescapable(false);
					interactSequence.toggleDisplay();
				}
				tombZone.createShadowElevatorAroundPlayer(true);
				tombZone.zoneFog.fadeTo(.3f, .2f);
				
				// Chime
				sound s = new sound(bellToll);
				s.start();
				
				// Wait for next chime.
				waitFor = 3f;
				waitStart = time.getTime();
				sequencePart++;
			}
			
			// Show eyes
			if(sequencePart == 11 && time.getTime() - waitStart > waitFor*1000) {
				
				// Show eyes of elevator.
				tombZone.giveElevatorEyes();

				// Wait for next chime.
				waitFor = 3.16f;
				waitStart = time.getTime();
				sequencePart++;
			}
			
			// Move elevator up.
			if(sequencePart == 12 && time.getTime() - waitStart > waitFor*1000) {
				
				// Show eyes of elevator.
				tombZone.moveElevatorUp();
				sequencePart++;
			}
		}
		
	}
	
	// Set sequence to
	public static void setSequenceTo(int n) {
		currentPlayerOne.sequencePart = n;
	}
	
	// Interact with object. 
	public void interactWith() { 
		if(interactSequence == null || (interactSequence != null && !interactSequence.isUnescapable())) {
			this.facingDirection = stringUtils.oppositeDir(player.getCurrentPlayer().getFacingDirection());
			interactSequence = makeNormalInteractSequence();
			interactSequence.toggleDisplay();
		}
	}

	///////////////
	/// METHODS ///
	///////////////
	// Constructor
	public playerOne(int newX, int newY) {
		super(sheepType, "Farlsworth", newX, newY);
		
		// Facing direction.
		facingDirection = "Up";
		
		// He has no collision
		collisionOn = false;
		
		// Set interactable.
		setInteractable(true);
		
		// Deal with animations
		animationPack unitTypeAnimations = new animationPack();
		
		// Standing left animation.
		animation standingLeft = new animation("standingLeft", getObjectSpriteSheet().getAnimation(1), 3, 3, 1);
		unitTypeAnimations.addAnimation(standingLeft);
		
		// Standing right animation.
		animation standingRight = new animation("standingRight", getObjectSpriteSheet().getAnimation(3), 3, 3, 1);
		unitTypeAnimations.addAnimation(standingRight);
		
		// Running left animation.
		animation runningLeft = new animation("runningLeft", getObjectSpriteSheet().getAnimation(1), 0, 3, 0.75f/5);
		unitTypeAnimations.addAnimation(runningLeft);		
		
		// Running right animation.
		animation runningRight = new animation("runningRight", getObjectSpriteSheet().getAnimation(3), 0, 3, 0.75f/5);
		unitTypeAnimations.addAnimation(runningRight);
		
		// Standing up animation.
		animation standingUp = new animation("standingUp", getObjectSpriteSheet().getAnimation(0), 3, 3, 1);
		unitTypeAnimations.addAnimation(standingUp);
		
		// Standing down animation.
		animation standingDown = new animation("standingDown", getObjectSpriteSheet().getAnimation(2), 3, 3, 1);
		unitTypeAnimations.addAnimation(standingDown);
		
		// Running up animation.
		animation runningUp = new animation("runningUp", getObjectSpriteSheet().getAnimation(0), 0, 3, 0.75f/5);
		unitTypeAnimations.addAnimation(runningUp);
		
		// Running down animation.
		animation runningDown = new animation("runningDown", getObjectSpriteSheet().getAnimation(2), 0, 3, 0.75f/5);
		unitTypeAnimations.addAnimation(runningDown);
		
		// Set animations.
		setAnimations(unitTypeAnimations);
		
		// Load events
		loadEvents();
		
		// Set current player one.
		currentPlayerOne = this;
		
		// Set dimensions
		setHeight(getDefaultHeight());
		setWidth(getDefaultWidth());
		platformerHeight = DEFAULT_PLATFORMER_HEIGHT;
		platformerWidth = DEFAULT_PLATFORMER_WIDTH;
		topDownHeight = DEFAULT_TOPDOWN_HEIGHT;
		topDownWidth = DEFAULT_TOPDOWN_WIDTH;
		setHitBoxAdjustmentY(getDefaultHitBoxAdjustmentY());
	}
	
	// Load events
	public void loadEvents() {
		
		// Load events that dictate where the player is in the game.
		pastShadowElevator = new event("playerOnePastShadowElevator");
	}
	
	// Initiate shadow elevator scene.
	public static void initiateShadowElevatorScene() {
		currentPlayerOne.shadowElevatorSceneInProgress = true;
	}
	
	// React to pain.
	public void reactToPain() {
	}
	
	// Farlsworth AI
	public void updateUnit() {
		//printFarlsworthEvents();
		
		// Stuff to do in non-boss fight mode.
		if(!bossFight) {
			doInteractStuff();
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
