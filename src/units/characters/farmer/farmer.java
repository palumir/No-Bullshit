package units.characters.farmer;

import java.awt.Color;
import java.awt.image.BufferedImage;

import drawing.spriteSheet;
import effects.interfaceEffects.tooltipString;
import interactions.interactBox;
import interactions.quest;
import interactions.textSeries;
import items.keys.farmGateKey;
import items.keys.farmKey;
import modes.mode;
import units.humanType;
import units.player;
import units.unit;
import units.unitType;
import units.characters.farlsworth.cinematics.farmFenceCinematic;
import units.characters.farlsworth.cinematics.farmIntroCinematic;
import units.characters.farmer.cinematics.farmerIntroCinematic;
import units.unitTypes.sheepFarm.sheep;
import zones.zone;

public class farmer extends unit {
	
	// Default dimensions.
	private static int DEFAULT_PLATFORMER_HEIGHT = 46;
	private static int DEFAULT_PLATFORMER_WIDTH = humanType.DEFAULT_UNIT_WIDTH;
	private static int DEFAULT_TOPDOWN_HEIGHT = 20;
	private static int DEFAULT_TOPDOWN_WIDTH = humanType.DEFAULT_UNIT_WIDTH;
	
	// Platformer and topdown default adjustment
	private static int DEFAULT_PLATFORMER_ADJUSTMENT_Y = 6;
	private static int DEFAULT_TOPDOWN_ADJUSTMENT_Y = 20;
	
	////////////////
	/// DEFAULTS ///
	////////////////
	
	// Default name.
	private static String unitName = "Farmer Farmer";
	
	// Farmer
	public static farmer farmer;
	
	// Default movespeed.
	private static int DEFAULT_FARMER_MOVESPEED = 1;
	
	// Default name color
	private static Color DEFAULT_NAME_COLOR = new Color(103,65,44);
	
	// Default jump speed
	private static int DEFAULT_FARMER_JUMPSPEED = 10;
	
	// farmer sprite stuff.
	private static String DEFAULT_FARMER_SPRITESHEET = "images/units/humanoid/farmer.png";
	
	// Farmer
	private static BufferedImage DEFAULT_DIALOGUE_BOX = spriteSheet.getSpriteFromFilePath("images/units/dialogueBoxes/farmerBox.png");
	
	// The actual type.
	private static unitType farmerType =
			new humanType( "Farmer Farmer",  // Name of unitType 
						 DEFAULT_FARMER_SPRITESHEET,
					     DEFAULT_FARMER_MOVESPEED, // Movespeed
					     DEFAULT_FARMER_JUMPSPEED // Jump speed
						);
	
	//////////////
	/// FIELDS ///
	//////////////
	
	// Interacting with farmer.
	public quest farlsworthQuest;
	
	// Tooltip help
	private boolean tooltipShown = false;
	
	// Interact sequence
	private interactBox interactSequence;

	
	///////////////
	/// METHODS ///
	///////////////
	// Constructor
	public farmer(int newX, int newY) {
		super(farmerType, newX, newY);
		
		farmer = this;
		setDialogueBox(DEFAULT_DIALOGUE_BOX);
		
		// Interactable.
		setInteractable(true);
		
		// Name color
		setNameColor(DEFAULT_NAME_COLOR);
		
		// Make adjustments on hitbox if we're in topDown.
		// Set dimensions
		setHeight(getDefaultHeight());
		setWidth(getDefaultWidth());
		platformerHeight = DEFAULT_PLATFORMER_HEIGHT;
		platformerWidth = DEFAULT_PLATFORMER_WIDTH;
		topDownHeight = DEFAULT_TOPDOWN_HEIGHT;
		topDownWidth = DEFAULT_TOPDOWN_WIDTH;
		setHitBoxAdjustmentY(getDefaultHitBoxAdjustmentY());
		farmer.setFacingDirection("Up");
		
		// Create quest.
		//farlsworthQuest = makeQuest();
		//if(!farlsworthQuest.isStarted()) hasQuest();
		relocate();
	}
	
	// Create interact sequence
	public interactBox makeNormalInteractSequence() {
		
		// Placeholder for each individual textSeries.
		textSeries s;
					
		// Start of conversation.
		textSeries startOfConversation = null;
		
		// If the person said no a bunch and the farmer was in Farlsworth's interaction.
		if(farmerIntroCinematic.playerPressedNoABunch.isCompleted() && farmIntroCinematic.isCompleted.isCompleted()) {
			if(farmFenceCinematic.fenceJokeExperienced.isCompleted()) {
				startOfConversation = new textSeries(null, "I can't remember how to get back to my spot from here.");
				s = startOfConversation.addChild(null, "I usually walk along the fence.");
				s = s.addChild(null, "I guess this is my spot now.");
				s.setEnd();
			}
			else {
				startOfConversation = new textSeries(null, "You'd better go with him.");
				s = startOfConversation.addChild(null, "But he did call you a dumbo.");
				s = s.addChild(null, "You should beat him up for that.");
				s = s.addChild(null, "He's a sheep, you'd probably win.");
				s = s.addChild(null, "Punch him right in the wool.");
				s.setEnd();
			}
			
		}
		
		else if(farmerIntroCinematic.isCompleted.isCompleted()) {
			
			if(sheep.sheepHitABunchJoke.isCompleted()) {
				startOfConversation = new textSeries(null, "Why are you beating the turd out of my sheep?");
				s = startOfConversation.addChild(null, "They're immortal. It's actually pretty annoying.");
				s = s.addChild(null, "I can never have lamb for dinner.");
				s = s.addChild(null, "But there's plenty of wool to go around.");
				s = s.addChild(null, "So I guess that's pretty dope.");
				s.setEnd();
			}
			else  {
				if(farmIntroCinematic.isCompleted.isCompleted() && !farmFenceCinematic.isCompleted.isCompleted()) {
					startOfConversation = new textSeries(null, "I think Frangurns needs to go to the bathroom.");
					s = startOfConversation.addChild(null, "He's standing at the fence waiting for you.");
					s = s.addChild(null, "Can you let him out for his potty break?");
					s = s.addChild(null, "I can't do it because my pizza's almost ready.");
					s.setEnd();
				}	
				
				else if(farmIntroCinematic.isCompleted.isCompleted() && farmFenceCinematic.isCompleted.isCompleted()) {
					startOfConversation = new textSeries(null, "Farnsbirth darted off to the North.");
					s = startOfConversation.addChild(null, "There's wolves in that direction!");
					s = s.addChild(null, "You better go make sure he's okay.");
					s = s.addChild(null, "I can't do it because I have a delicious pie baking.");
					s.setEnd();
				}	
				
				else {
					startOfConversation = new textSeries(null, "Are you confused?");
					s = startOfConversation.addChild(null, "Farnsgirth is in the pen to the East.");
					s = s.addChild(null, "Please go get his wool for me.");
					s = s.addChild(null, "I can't get it because I already asked you to.");
					s = s.addChild(null, "So it would therefore be rude of me.");
					s = s.addChild(null, "And I'm not rude. I'm chill.");
					s.setEnd();
				}
			}	
		}
			
		return new interactBox(startOfConversation, this);
	}
	
	// Relocate
	public void relocate() {
		if(zone.getCurrentZone().getName().equals("sheepFarm")) {
			if(farmerIntroCinematic.playerPressedNoABunch.isCompleted()) {
				setDoubleX(260);
				setDoubleY(-394);
				setFacingDirection("Right");
				
				if(farmIntroCinematic.isCompleted.isCompleted()) {
					setFacingDirection("Down");
				}
			}
			else if(farmerIntroCinematic.isCompleted.isCompleted()) {
				setFacingDirection("Down");
			}
		}
	}
	
	// Create conversation
	/*public quest makeQuest() {
		
		// Description
		String DEFAULT_QUEST_DESC = "Retrieve Farlsworth's wool";
		
		// Placeholder for each individual textSeries.
		textSeries s;
		
		// Start of conversation.
		textSeries startOfConversation = new textSeries(null, "Holy heck, where did you come from!?");
		textSeries why = startOfConversation.addChild("'Why are you standing there?'", "I was trying to figure out which building is my house.");
		s = why.addChild(null, "I know one thing for sure.");
		s = why.addChild(null, "I know one thing for sure.");
		s = s.addChild(null, "I'm pretty sure that's a squirrel house.");
		
		
		
		textSeries bridge = startOfConversation.addChild("'Over the bridge'", "There's a bridge in my farm?");
		
		
		
		s = why.addChild(null, "<insert friggin pizza joke or something>");
		s = s.addChild(null, "Farlsworth should be on his own in the Eastern pen.");
		textSeries question = s.addChild(null, "Can you grab his wool for me?");
		
		// Saying yes.
		textSeries yes = question.addChild("\'Yes\'","Alright, here's the key to his pen.");
		s = yes.addChild(null, "It also doubles as the key to the front gate.");
		s = s.addChild(null, "And the barn.");
		s = s.addChild(null, "And my house.");
		s = s.addChild(null, "And my fridge.");
		s = s.addChild(null, "Actually, give me that back. I need that one.");
		s = s.addChild(null, "Otherwise I can't heat up my toaster strudels.");
		s = s.addChild(null, "Take this one instead.");
		s = s.addChild(null, "Good luck.");
		s.setEnd();
		
		// Saying no to path 1.
		textSeries noHelp = question.addChild("\'No\'","Please?");
		
		// Keep saying no like an asshole.
		noHelp.addChild(yes);
		s = noHelp.addChild("\"No\"","I'll make it worth your while.");
		s.addChild(yes);
		s = s.addChild("\"No\"","Come on ... ");
		s.addChild(yes);
		s = s.addChild("\"No\"","Well, why not?");
		s.addChild(yes);
		s = s.addChild("\"No\"","That doesn't even make sense.");
		s.addChild(yes);
		s = s.addChild("\"No\"","Well, you can always just sheep on it.");
		s.addChild(yes);
		s = s.addChild("\"No\"","Come on, that was a good one.");
		s.addChild(yes);
		s = s.addChild("\"No\"","Why won't you take the quest?");
		s.addChild(yes);
		s = s.addChild("\"No\"","What's your issue?");
		s.addChild(yes);
		s = s.addChild("\"No\"","Madam, you are harassing me.");
		s.addChild(yes);
		s = s.addChild("\"No\"","Leave me alone.");
		s.addChild(yes);
		s = s.addChild("No.","Can you please just take the quest?");
		s.addChild(yes);
		s = s.addChild("No.","Can you please just take the quest?");
		s.addChild(yes);
		s = s.addChild("No.","Can you please just take the quest?");
		s.addChild(yes);
		s = s.addChild("No.","Can you please just take the quest?");
		s.addChild(yes);
		s = s.addChild("No.","Can you please just take the quest?");
		s.addChild(yes);
		s = s.addChild("No.","Oh, COME ON. How can you STILL possibly be saying no?");
		s.addChild(yes);
		s = s.addChild("No.","You're relentless, really.");
		s.addChild(yes);
		s = s.addChild("\"No\"","That's not a compliment, by the way.");
		s.addChild(yes);
		s = s.addChild("\"No\"","You need counselling.");
		s.addChild(yes);
		s = s.addChild("\"No\"","You can't say no forever.");
		s.addChild(yes);
		s = s.addChild("\"No\"","Actually, you can.");
		s.addChild(yes);
		s.addChild(s);
		
		// Create the whole quest and add dialogue.
		interactSequence = new interactBox(startOfConversation, this);
		quest q = new quest(DEFAULT_QUEST_DESC, this, interactSequence);
		
		// If the quest is started, don't allow the person to do the whole dialogue.
		if(q.isStarted()) {
			if(sheep.sheepHitABunchJoke.isCompleted()) {
				startOfConversation = new textSeries(null, "Why are you beating the turd out of my sheep?");
				s = startOfConversation.addChild(null, "They're immortal. It's actually pretty annoying.");
				s = s.addChild(null, "I can never have lamb for dinner.");
				s = s.addChild(null, "But there's plenty of wool to go around.");
				s = s.addChild(null, "So I guess that's pretty dope.");
				s.setEnd();
				q.getDialogue().setTheText(startOfConversation);
			}
			else {
				startOfConversation = new textSeries(null, "Are you confused?");
				s = startOfConversation.addChild(null, "Farnsgirth is in the pen to the East.");
				s = s.addChild(null, "Please go get his wool for me.");
				s = s.addChild(null, "I can't get it because I already asked you to.");
				s = s.addChild(null, "So it would therefore be rude of me.");
				s = s.addChild(null, "And I'm not rude. I'm chill.");
				s.setEnd();
				q.getDialogue().setTheText(startOfConversation);
			}	
		}
		
		interactSequence.getTextSeries().setWhoIsTalking(this);
		
		return q;
	}*/
	
	// Sequence number
	int sequenceNumber = 0;
	
	// Quest stuff.
	public void doQuestStuff() {

		// If we have reached the end of our quest conversation (and they clicked yes, of course, since it's all they can do.)
		if(sequenceNumber == 0 && (interactSequence != null && !interactSequence.isButtonMode() && interactSequence.getTextSeries().getTextOnPress()!=null) &&
				(interactSequence.getTextSeries().getTextOnPress().contains("key to his pen") && interactSequence.textScrollingFinished())) {
			farmKey f = new farmKey(0,0);
			f.pickUp();
			sequenceNumber++;
		}
		if(sequenceNumber == 1 && (interactSequence != null && interactSequence.getTextSeries().getTextOnPress()!=null) &&
				(interactSequence.getTextSeries().getTextOnPress().contains("give me that back") && interactSequence.textScrollingFinished())) {
			player.getPlayer().getPlayerInventory().get("Farm Key").drop();
			sequenceNumber++;
		}
		if(sequenceNumber == 2 && (interactSequence != null && interactSequence.getTextSeries().getTextOnPress()!=null) &&
				(interactSequence.getTextSeries().getTextOnPress().contains("ake this") && interactSequence.textScrollingFinished())) {
			farmGateKey f = new farmGateKey(0,0);
			f.pickUp();
			sequenceNumber++;
		}
		if(sequenceNumber == 3 && interactSequence != null && interactSequence.getTextSeries()!=null && interactSequence.getTextSeries().isEnd()) {
			farlsworthQuest.startQuest();
			sequenceNumber++;
		}
	}
	
	// Farmer intro cinematic
	farmerIntroCinematic intro;
	
	// Interact with object. Should be over-ridden.
	public void interactWith() { 
		// Tomb patch cinematic
		if(!intro.isCompleted.isCompleted()
				&& (intro == null || !intro.isInProgress())
				&& !farmerIntroCinematic.isCompleted.isCompleted()) {
			intro = new farmerIntroCinematic();
			intro.start();
		}
		else {
			faceTowardPlayer();
			interactSequence = makeNormalInteractSequence();
			interactSequence.toggleDisplay();
		}
	}
	
	// React to pain.
	public void reactToPain() {
	}
	
	// Tutorial stuff.
	public void doTutorialStuff() {
		player currPlayer = player.getPlayer();
		if(!tooltipShown && farlsworthQuest != null && !farlsworthQuest.isStarted() && currPlayer != null && currPlayer.isWithin(-1017,-283,-450,25)) {
			tooltipShown = true;
			tooltipString t = new tooltipString("Press 'e' to interact with something.");
		}
	}
	
	// Does nothing yet.
	public void updateUnit() {
		doTutorialStuff();
		doQuestStuff();
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
