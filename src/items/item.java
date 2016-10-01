package items;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import UI.items.itemDiscover;
import drawing.drawnObject;
import drawing.gameCanvas;
import drawing.spriteSheet;
import effects.effect;
import effects.interfaceEffects.floatingString;
import sounds.sound;
import units.player;
import utilities.saveBooleanList;
import utilities.stringUtils;
import zones.zone;

public abstract class item extends drawnObject {
	////////////////
	/// DEFAULTS ///
	////////////////
	
	// All items.
	public static ArrayList<item> allItems = new ArrayList<item>();
	
	// Display
	public static Color DEFAULT_PICKUP_COLOR = new Color(103,238,245);
	public static Color DEFAULT_DROP_COLOR = Color.red;
	
	// Volume and sound
	public static String pickUpSound = "sounds/effects/player/items/itemPickup.wav";
	
	//////////////
	/// FIELDS ///
	//////////////
	
	// List of booleans we will save for the item.
	private saveBooleanList saveBooleans = new saveBooleanList();

	// Properties
	public ArrayList<String> properties;
	
	// Description
	public String description;
	
	// Does the player actually own the item?
	public boolean inInventory = false;
	
	// Is the item picked up before?
	public boolean pickedUpItem = false;
	
	// Is it equippable?
	public boolean equippable = false;
	
	// Consumable
	public boolean usedOnItems = false;
	
	// Item rarity.
	public String quality = "Alright";
	
	// The zone the item was discovered in.
	public String discoverZone = "None"; // Defaults to not a zone.
	
	// Slot the item is equipped in (if possible)
	public Integer slot = KeyEvent.VK_WINDOWS; // Defaults to something that's not a key. Hahahaha.
	
	///////////////
	/// METHODS ///
	///////////////
	public item(String newName, spriteSheet newSpriteSheet, int newX, int newY, int newWidth, int newHeight) {
		super(newSpriteSheet,newName, newX,newY,newWidth,newHeight);
		
		// Set discover zone.
		if(zone.getCurrentZone() != null) discoverZone = zone.getCurrentZone().getName();
		
		// Break up the spriteSheet. Assumed to be regular human character size, for now.
		if(player.getPlayer()!=null) {
			if(player.getPlayer().getPlayerInventory().hasItem(this)) {
				setDrawObject(false);
			}
		}
		else setDrawObject(true);
		
		allItems.add(this);
	}
	
	// Get the item's image.
	public abstract BufferedImage getImage();
	
	// Pickup the item.
	public void drop() {
		if(player.getPlayer() != null) {
		
			// Display text. 
			player currPlayer = player.getPlayer();
			effect e = new floatingString("-" + stringUtils.toTitleCase(getName()), DEFAULT_DROP_COLOR, currPlayer.getIntX() + currPlayer.getWidth()/2, currPlayer.getIntY() + currPlayer.getHeight()/2, 1.3f);
			e.setAnimationDuration(3f);
			
			// At least add the item to the player's inventory.
			player.getPlayer().getPlayerInventory().drop(this);
			
		}
		
		// Play sound.
		sound s = new sound(pickUpSound);
		s.start();
		
		// Stop drawing the weapon on the ground.
		inInventory = false;
		destroy();
	}
	
	// Item discover.
	itemDiscover discoverAnimation;
	
	// Pickup the item.
	public void pickUp() {
		if(player.getPlayer() != null) {
		
			// Display text. 
			player currPlayer = player.getPlayer();

			discoverAnimation = new itemDiscover(this,
					gameCanvas.getDefaultWidth()/2 - itemDiscover.getDefaultWidth()/2,
					gameCanvas.getDefaultHeight()/2 - itemDiscover.getDefaultHeight()/2 + 80);
			
			// At least add the item to the player's inventory.
			player.getPlayer().getPlayerInventory().pickUp(this);
			
		}
		
		// Play sound.
		sound s = new sound(pickUpSound);
		s.start();
		
		// React to pick-up
		reactToPickup();
		
		// Stop drawing the weapon on the ground.
		setDrawObject(false);
		inInventory = true;
		pickedUpItem = true;
		destroy();
	}
	
	// React to pickup
	public void reactToPickup() {
	}
	
	// Use item
	public void use() {
		
	}
	
	// Update. Only allow pick-up for drawn items.
	@Override
	public void update() {
		
		// Only allow pick-up for drawn items.
		if(this.isDrawObject() && this.collides(this.getIntX(), this.getIntY(), player.getPlayer())) {
			pickUp();
		}
	}
	
	// Draw the item
	@Override
	public void drawObject(Graphics g) {
		g.drawImage(getImage(), 
				getDrawX(), 
				getDrawY(), 
				(int)(gameCanvas.getScaleX()*getImage().getWidth()), 
				(int)(gameCanvas.getScaleY()*getImage().getHeight()), 
				null);
	}
	
	// Initiate events.
	public static void initiateEvents() {
	}
	
	// Initiate so we actually have a list of items.
	public static void initiate() {
		
		// Load the item pick-up animation so it doesn't lag on item pick-up.
		itemDiscover.DEFAULT_EFFECT_NAME = itemDiscover.DEFAULT_EFFECT_NAME; // Why does this work? No fucking clue.
		
	}

	public saveBooleanList getSaveBooleans() {
		return saveBooleans;
	}

	public void setSaveBooleans(saveBooleanList saveBooleans) {
		this.saveBooleans = saveBooleans;
	}

	public void setUpItem() {
	}
}