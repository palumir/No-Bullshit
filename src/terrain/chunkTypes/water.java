package terrain.chunkTypes;

import java.util.Random;

import doodads.sheepFarm.needlestack;
import drawing.camera;
import drawing.userInterface.tooltipString;
import interactions.interactBox;
import interactions.textSeries;
import main.main;
import modes.mode;
import sounds.sound;
import terrain.chunk;
import terrain.chunkType;
import terrain.generalChunkType;
import terrain.groundTile;
import units.humanType;
import units.player;
import units.unit;
import units.unitType;
import utilities.saveState;
import utilities.stringUtils;
import utilities.time;
import zones.zone;

public class water extends chunk {
	
	////////////////
	/// DEFAULTS ///
	////////////////
	
	// Default name.
	private static String DEFAULT_CHUNK_NAME = "Water";
	
	// Tile sprite stuff
	private static String DEFAULT_CHUNK_SPRITESHEET = "images/terrain/water.png";
	
	// Sound
	public static String waterSplash = "sounds/effects/doodads/waterSplash.wav";
	
	// Dimensions
	public static int DEFAULT_CHUNK_WIDTH = 32;
	public static int DEFAULT_CHUNK_HEIGHT = 32;
	
	// The actual type.
	private static generalChunkType typeReference = new generalChunkType(DEFAULT_CHUNK_NAME, DEFAULT_CHUNK_SPRITESHEET, DEFAULT_CHUNK_WIDTH, DEFAULT_CHUNK_HEIGHT); 
	
	// Interact sequence.
	private static interactBox interactSequence;
	
	// Have saved?
	private boolean haveSaved = true;
	
	///////////////
	/// METHODS ///
	///////////////
	// Constructor
	public water(int newX, int newY) {
		super(typeReference, newX, newY);
		setInteractable(true);
		this.setPassable(false);
		backgroundDoodad = true;
	}
	
	public water(generalChunkType typeReference, int newX, int newY) {
		super(typeReference, newX, newY);
		setInteractable(true);
		this.setPassable(false);
	}

	// Create interact sequence
	public static interactBox makeInteractSequence() {
		
		// Placeholder for each individual textSeries.
		textSeries s;
		
		// Start of conversation.
		textSeries startOfConversation = new textSeries("StartWithButtons", "StartWithButtons");
		
		// Save and reset.
		textSeries saveGame = startOfConversation.addChild("Save game", "Saving the game will reset mobs. Are you sure?");
		
		// Cancel
		textSeries cancel = startOfConversation.addChild("Cancel", "The game was not saved.");
		cancel.setEnd();
		
		// Warning
		s = saveGame.addChild("Yes", "Game saved and mobs reset.");
		s.setEnd();
		
		s = saveGame.addChild("No", "The game was not saved.");
		s.setEnd();

		return new interactBox(startOfConversation, stringUtils.toTitleCase(DEFAULT_CHUNK_NAME));
	}
	
	// Interact stuff.
	public void doInteractStuff() {
		
		if(interactSequence != null) {
			// Save
			if(!haveSaved && interactSequence.getTheText().getButtonText().equals("Yes")) {
				if(player.getCurrentPlayer().getEquippedBottle()!=null) player.getCurrentPlayer().getEquippedBottle().refill();
				saveState.createSaveState();
				haveSaved = true;
				interactSequence.toggleDisplay();
				main.restartGame("Save");
			}
			
			// Don't save.
			if(interactSequence.getTheText().getButtonText().equals("No")) {
				
			}
		}
	}
	
	// Update
	@Override
	public void update() {
		doInteractStuff();
	}
	
	// Interacting with heals you and saves.
	@Override
	public void interactWith() {
		
		// Not in combat.
		if(player.getCurrentPlayer().getInCombatWith().size() <= 0) {
			// Play sound
			sound s = new sound(waterSplash);
			s.start();
			
			// Restart sequence.
			interactSequence = makeInteractSequence();
			
			// Reset booleans
			haveSaved = false;
			
			// Toggle display.
			interactSequence.toggleDisplay();
		}
		
		// In combat
		else {
			tooltipString t = new tooltipString("You cannot save while in combat.");
		}
	}
}
