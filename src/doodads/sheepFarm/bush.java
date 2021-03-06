package doodads.sheepFarm;

import interactions.interactBox;
import interactions.textSeries;
import modes.mode;
import sounds.sound;
import terrain.chunk;
import terrain.generalChunkType;
import units.player;
import utilities.utility;

public class bush extends chunk {
	
	////////////////
	/// DEFAULTS ///
	////////////////
	
	// Default name.
	private static String DEFAULT_CHUNK_NAME = "bush";
	
	// Sprite stuff.
	private static String DEFAULT_CHUNK_SPRITESHEET = "images/doodads/farmLand/sheepFarm/"+ DEFAULT_CHUNK_NAME + ".png";
	
	// Sound
	public static String eating = "sounds/effects/doodads/eating.wav";
	public static String clearBush = "sounds/effects/doodads/bush.wav";
	
	// Bushtype
	private int bushType = 0;
	
	// Dimensions
	private static int DEFAULT_CHUNK_WIDTH = 54;
	private static int DEFAULT_CHUNK_HEIGHT = 32;
	
	// The actual type.
	private static generalChunkType typeReference = new generalChunkType(DEFAULT_CHUNK_NAME, DEFAULT_CHUNK_SPRITESHEET, DEFAULT_CHUNK_WIDTH, DEFAULT_CHUNK_HEIGHT);
	
	////////////
	// FIELDS //
	////////////
	private interactBox interactSequence;
	
	// Has eaten berry?
	private boolean hasEatenBerry = false;
	
	///////////////
	/// METHODS ///
	///////////////
	// Constructor
	public bush(int newX, int newY, int i) {
		super(typeReference, newX, newY, i, 0);
		bushType = i;
		if(mode.getCurrentMode().equals("topDown")) {
			setHitBoxAdjustmentY(5);
			setWidth(50);
			setHeight(15);
		}
		else {
			setHitBoxAdjustmentY(0);
			setHeight(DEFAULT_CHUNK_HEIGHT);
			setWidth(DEFAULT_CHUNK_WIDTH);
		}
		
		// Set interactable.
		setInteractable(true);
		
		// Set not passable.
		setPassable(false);
	}
	
	// Create interact sequence
	public interactBox makeNormalInteractSequence() {
		
		// Placeholder for each individual textSeries.
		textSeries s;
					
		// Start of conversation.
		textSeries startOfConversation = null;
		if(bushType == 1) {
			// Start of conversation.
			startOfConversation = new textSeries(null, "It's a bush filled with some unknown berries.");
				
			s = startOfConversation.addChild("Eat a berry", "Delicious.");
			s.setEnd();
			
			s = startOfConversation.addChild("Do not eat a berry", "They're probably poisonous anyway.");
			s.setEnd();
		}
		else {
			// Start of conversation.
			startOfConversation = new textSeries(null, "It's just a regular bush.");
			s = startOfConversation.addChild(null, "Except in a video game.");
			s = s.addChild(null, "I don't really like games.");
			s.setEnd();
		}
			
		return new interactBox(startOfConversation, this);
	}
	
	// Interact stuff.
	public void doInteractStuff() {
		
		// If they choose to eat a berry.
		if(!hasEatenBerry && interactSequence != null 
				&& interactSequence.getTextSeries().isEnd() 
				&& interactSequence.getTextSeries().getButtonText() != null
				&& interactSequence.getTextSeries().getButtonText().equals("Eat a berry")) {
			int randomBetween = 20 - utility.RNG.nextInt(41);
			
			// Play sound
			sound s = new sound(eating);
			s.start();
		
			if(randomBetween < 0) {
				player.getPlayer().hurt(randomBetween*(-1), 1);
			}
			else {
			}
			hasEatenBerry = true;
		}
	}
	
	// Update
	@Override
	public void update() {
		doInteractStuff();
	}
	
	// Interact with object. Should be over-ridden.
	public void interactWith() { 
		hasEatenBerry = false;
		interactSequence = makeNormalInteractSequence();
		interactSequence.toggleDisplay();
	}
}
