package doodads.sheepFarm;

import interactions.event;
import interactions.interactBox;
import interactions.textSeries;
import modes.mode;
import terrain.chunk;
import terrain.generalChunkType;

public class haystack extends chunk {
	
	////////////////
	/// DEFAULTS ///
	////////////////
	
	// Default name.
	private static String DEFAULT_CHUNK_NAME = "haystack";
	
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
	
	// Sequence for haystacks.
	private interactBox interactSequence;
	
	// For the needle interaction.
	private int timesSearched = 0;
	private boolean setNextSearch = false;
	private boolean strange = false;
	private static event needleJoke;
	
	///////////////
	/// METHODS ///
	///////////////
	
	// Create interact sequence
	public interactBox makeNormalInteractSequence() {
		
		// Placeholder for each individual textSeries.
		textSeries s;
					
		// Start of conversation.
		textSeries startOfConversation = null;
		
		// Hay not changed.
		if(needleJoke != null && !needleJoke.isCompleted()) {
			
			// Start of conversation.
			startOfConversation = new textSeries("StartWithButtons", "StartWithButtons");
			s = startOfConversation.addChild("Search", "You search through the haystack ...");
			s = s.addChild(null, "... you find nothing.");
			s.setEnd();
		}
		
		// If hay changed to needle before.
		else {
			
			// Start of conversation.
			startOfConversation = new textSeries("StartWithButtons", "StartWithButtons");
			
			s = startOfConversation.addChild("Brush off hay", "You brush off the hay ...");
			s = s.addChild(null, "... it's more hay under the hay you brushed off.");
			s.setEnd();
		}
		
		return new interactBox(startOfConversation, this);
	}
	
	// Create interact sequence
	public interactBox makeStrangeInteractSequence() {
		
		// Load the gag.
		if(needleJoke == null) needleJoke = event.createEvent("needleJoke"); // Needle joke experienced.
		
		// Placeholder for each individual textSeries.
		textSeries s;
		
		// Start of conversation.
		textSeries startOfConversation = new textSeries("StartWithButtons", "StartWithButtons");
		
		s = startOfConversation.addChild("Search", "You search through the haystack ...");
		s = s.addChild(null, "... you find a needle.");
		s = s.addChild("Search Again", "You search the haystack ...");
		s = s.addChild(null, "You find two needles.");
		s = s.addChild("Search Again", "You shove your fist into the haystack ...");
		s = s.addChild(null, "You pull out a handful of needles.");
		s = s.addChild(null, "A needle pricks you in the hand.");
		s = s.addChild(null, "It draws blood.");
		s = s.addChild(null, "You have hemophilia and lack proper blood-clotting proteins.");
		s = s.addChild(null, "You feel light headed and your legs begin to tremble.");
		s = s.addChild(null, "Just kidding, you're fine.");
		s = s.addChild(null, "It's just a game.");
		s = s.addChild("Search Again", "You search the haystack ...");
		s = s.addChild(null, "You can't actually find any hay. Only needles.");
		s = s.addChild("Brush off hay", "You brush off the hay ...");
		s = s.addChild(null, "It's a stack of needles under some hay.");

		// Set each to the end.
		s.setEnd();
		
		return new interactBox(startOfConversation, this);
	}
	
	// Interact stuff.
	public void doInteractStuff() {


		// If we are the strange haystack and have searched 6 times, destroy it.
		if(strange && (needleJoke.isCompleted() || interactSequence.getTextSeries().isEnd())) {
			
			// Spawn a needleStack.
			needlestack n = new needlestack(getIntX(), getIntY(), 0);
			needleJoke.setCompleted(true);
			
			// Destroy this.
			this.destroy();
		}
	}
	
	// Update
	@Override
	public void update() {
		doInteractStuff();
	}
	
	// Set strange.
	public void setStrange() {
		interactSequence = makeStrangeInteractSequence();
		strange = true;
	}
	
	// Interact with object. Should be over-ridden.
	public void interactWith() { 
		if(!strange) interactSequence = makeNormalInteractSequence();
		if(strange) interactSequence = makeStrangeInteractSequence();
		setNextSearch = false;
		interactSequence.toggleDisplay();
	}
	
	// Constructor
	public haystack(int newX, int newY, int i) {
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
