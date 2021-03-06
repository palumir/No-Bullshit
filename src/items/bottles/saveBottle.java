package items.bottles;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import drawing.spriteSheet;
import drawing.spriteSheet.spriteSheetInfo;
import effects.effectTypes.items.savePoint;
import effects.interfaceEffects.tooltipString;
import interactions.interactBox;
import interactions.textSeries;
import items.bottle;
import sounds.sound;
import units.player;
import utilities.saveState;

public class saveBottle extends bottle {
	////////////////
	/// DEFAULTS ///
	////////////////
	// Bottle name
	public static String DEFAULT_BOTTLE_NAME = "Save Bottle";
	
	// Bottle stats.
	public static int DEFAULT_MAX_CHARGES = 3;
	
	// Tutorial stuff
	public static tooltipString enterToUse;
	
	//////////////
	/// FIELDS ///
	//////////////
	public static spriteSheet bottleSpriteSheetRef = new spriteSheet(new spriteSheetInfo(
			"images/doodads/items/saveBottle.png", 
			bottle.DEFAULT_SPRITE_WIDTH, 
			bottle.DEFAULT_SPRITE_HEIGHT,
			bottle.DEFAULT_SPRITE_ADJUSTMENT_X,
			bottle.DEFAULT_SPRITE_ADJUSTMENT_Y
			));
	
	///////////////
	/// METHODS ///
	///////////////
	
	// On floor.
	public saveBottle(int x, int y) {
		super(DEFAULT_BOTTLE_NAME,x,y);
		
		// Weapon stats.
		setStats();
	}
	
	// Set stats
	public void setStats() {
		
		// Rarity
		quality = "Good";
		description = "Saves game.";
		
		// Set item's stats
		// Bottle charges.
		setChargesLeft(3);
		setMaxCharges(DEFAULT_MAX_CHARGES);
	}
	
	// Use charge.
	@Override
	public void useCharge() {
		if(getChargesLeft() > 0) {
			
			sound s = new sound(bottle.bottleDrink);
			s.start();
			setChargesLeft(getChargesLeft() - 1);
			
			// Set position to be last bottle charge.
			if(player.getPlayer().lastSaveBottles == null) player.getPlayer().lastSaveBottles = new ArrayList<Point>();
			player.getPlayer().lastSaveBottles.add(new Point(player.getPlayer().getIntX(), player.getPlayer().getIntY()));
			
			// Put down indicator and destroy old one.
			if(player.getPlayer().lastSaveBottleChargeIndicator != null) player.getPlayer().lastSaveBottleChargeIndicator.destroy();
			savePoint.createSavePoint();
			
			// Save.
			saveState.createSaveState();
			
			// Destroy tutorial text.
			if(enterToUse!=null && !enterToUse.fadingOut) {
				enterToUse.fadeOut();
				new tooltipString("2 charges of Save Bottle remaining. Use them wisely!");
			}
		}
	}
	
	// Note attached
	private boolean noteAttached = false;
	
	// Attach note.
	public void attachFarlsworthNote() {
		noteAttached = true;
	}
	
	// React to being picked up.
	@Override
	public void reactToPickup() {
		player currPlayer = player.getPlayer();
		if(currPlayer != null) {
			
			if(noteAttached) {
				currPlayer.getPlayerInventory().equipItem(this, KeyEvent.VK_ENTER);
				
				textSeries s = new textSeries(null, "There is a note attached to the bottle.");
			    interactBox interactSequence = new interactBox(s, this);
				interactSequence.toggleDisplay();
				
				textSeries fart = s.addChild(new textSeries("Read", "\"Pick your boogers.\""));
				textSeries read = fart.addChild(new textSeries("Wait a second, I read that wrong the first time", "\"Pick your poison\" - with love, Farlsworth."));
				read.setChoiceType("Order");
				read.setEnd();
				
				textSeries dontRead = s.addChild(new textSeries("Don't read", "It's probably poisonous anyway."));
				dontRead.setChoiceType("Chaos");
				dontRead.setEnd();
			}
			else {
				currPlayer.getPlayerInventory().equipItem(this, KeyEvent.VK_ENTER);
				enterToUse = new tooltipString("Press 'Enter' to create a save point with the Save Bottle.");
				enterToUse.setHasATimer(false);
			}
		}
	}

	// Get the item ground image.
	public BufferedImage getImage() {
		if(getChargesLeft() == 0) {
			return bottleSpriteSheetRef.getSprite(0, 0); // Empty bottle.
		}
		else if (getChargesLeft() == getMaxCharges()) {
			return bottleSpriteSheetRef.getSprite(1, 0); // Full bottle.
		}
		else if (getChargesLeft() == getMaxCharges() - 1) {
			return bottleSpriteSheetRef.getSprite(2, 0); // Full bottle.
		}
		else {
			return bottleSpriteSheetRef.getSprite(3, 0); // Full bottle.
		}
	}
}