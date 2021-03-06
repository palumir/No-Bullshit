package doodads.sheepFarm;

import interactions.interactBox;
import interactions.textSeries;
import modes.mode;
import terrain.chunk;
import terrain.generalChunkType;

public class needlestack extends chunk {
	
	////////////////
	/// DEFAULTS ///
	////////////////
	
	// Default name.
	private static String DEFAULT_CHUNK_NAME = "needlestack";
	
	// Sprite stuff.
	private static String DEFAULT_CHUNK_SPRITESHEET = "images/doodads/farmLand/sheepFarm/"+ DEFAULT_CHUNK_NAME + ".png";
	
	// Dimensions
	private static int DEFAULT_CHUNK_WIDTH = 59;
	private static int DEFAULT_CHUNK_HEIGHT = 41;
	
	// The actual type.
	private static generalChunkType typeReference = new generalChunkType(DEFAULT_CHUNK_NAME, DEFAULT_CHUNK_SPRITESHEET, DEFAULT_CHUNK_WIDTH, DEFAULT_CHUNK_HEIGHT);  
	
	////////////////
	/// FIELDS /////
	//////////////// 
	private interactBox interactSequence;
	
	///////////////
	/// METHODS ///
	///////////////
	
	// Create interact sequence
	public interactBox makeNormalInteractSequence() {
		
		// Placeholder for each individual textSeries.
		textSeries s;
		
		// Start of conversation.
		textSeries startOfConversation = new textSeries("StartWithButtons", "StartWithButtons");
		s = startOfConversation.addChild("Search", "You search through the needlestack ...");
		s = s.addChild(null, "... you find a piece of hay.");
		s.setEnd();
		
		return new interactBox(startOfConversation, this);
	}
	
	// Interact with object. Should be over-ridden.
	public void interactWith() { 
		interactSequence = makeNormalInteractSequence();
		interactSequence.toggleDisplay();
	}
	
	// Constructor
	public needlestack(int newX, int newY, int i) {
		super(typeReference, newX, newY, i, 0);
		if(mode.getCurrentMode().equals("topDown")) {
			setHitBoxAdjustmentY(10);
			setWidth(44);
			setHeight(20);
		}
		else {
			setHitBoxAdjustmentY(0);
			setHeight(DEFAULT_CHUNK_HEIGHT);
			setWidth(DEFAULT_CHUNK_WIDTH);
		}
		
		// Interactable.
		setInteractable(true);
		interactSequence = makeNormalInteractSequence();
		
		// Passable.
		setPassable(false);
	}
}
