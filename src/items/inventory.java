package items;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import drawing.gameCanvas;
import drawing.spriteSheet;
import sounds.sound;
import units.player;
import userInterface.interfaceObject;
import utilities.stringUtils;

public class inventory extends interfaceObject {
	////////////////
	/// DEFAULTS ///
	////////////////
	
	// Inventory background.
	public static BufferedImage inventoryBackground = spriteSheet.getSpriteFromFilePath("images/interface/inventory.png");
	
	// Draw position
	public static int DEFAULT_INVENTORY_START_X = 20;
	public static int DEFAULT_INVENTORY_START_Y = gameCanvas.getDefaultHeight()-320;
	
	// Inventory size.
	public static int DEFAULT_INVENTORY_SIZE = 16; // total number of slots. must be a perfect root of 2
	public static int DEFAULT_SLOT_SIZE = 30; // size in terms of the actual physical size on the screen.
	
	// Strings.
	public static String DEFAULT_EMPTY_SLOT = "Empty";
	public static String DEFAULT_BOTTOM_TEXT = "Press \'e\' to equip";
	
	// Colors
	public static Color DEFAULT_SLOT_COLOR = new Color(85,58,30);
	public static Color DEFAULT_SLOT_BACKGROUND_COLOR = new Color(133,94,46);
	public static Color DEFAULT_TEXT_COLOR = Color.white;
	public static Color DEFAULT_SELECTED_SLOT_COLOR = new Color(73,58,7);
	
	//////////////
	/// FIELDS ///
	//////////////
	
	// The actual items
	private ArrayList<item> items;
	
	// Selected slot
	private int selectedSlot = 0;
	
	// Display inventory on screen?
	private boolean displayOn = false;
	
	// Sounds for inventory.
	private sound openInventory;
	private sound closeInventory;
	private sound equipWeapon;
	private sound unequipWeapon;
	private sound UIMove;
	
	///////////////
	/// METHODS ///
	///////////////
	public inventory() {
		super(null, DEFAULT_INVENTORY_START_X, DEFAULT_INVENTORY_START_Y, 0, 0);
		items = new ArrayList<item>();
		
		// Set sounds.
		openInventory = new sound("sounds/effects/player/UI/openInventory.wav");
		closeInventory = new sound("sounds/effects/player/UI/closeInventory.wav");
		equipWeapon = new sound("sounds/effects/player/UI/equipItem.wav");
		unequipWeapon = new sound("sounds/effects/player/UI/unequipItem.wav");
		UIMove = new sound("sounds/effects/player/UI/UIMove.wav");
	}
	
	// Toggle inventory display.
	public void toggleDisplay() {
		setDisplayOn(!isDisplayOn());
		if(displayOn) openInventory.playSound(0.7f);
		else { closeInventory.playSound(0.7f); }
	}
	
	// Pickup an item into inventory.
	public void pickUp(item i) {
		if(!hasItem(i)) {
			items.add(i);
		}
	}
	
	// Check if inventory has item with the same name.
	public boolean hasItem(item i) {
		for(int j = 0; j < items.size(); j++) {
			if(items.get(j).name.equals(i.name)) return true;
		}
		return false;
	}
	
	// Interact with the current selected item.
	public void equipSelectedItem() {
		
		// We aren't trying to equip nothing.
		if(selectedSlot < items.size()) {
			
			// Make sure it's equippable.
			if(items.get(selectedSlot).equippable) {
				
				// Get the item.
				item i = items.get(selectedSlot);
				player currPlayer = player.getCurrentPlayer();
				
				// Deal with weapons.
				if(i instanceof weapon) {
					
					// If the weapon is currently equipped, unequip it.
					if(currPlayer.getEquippedWeapon() != null && currPlayer.getEquippedWeapon().name.equals(i.name)) {
						// Unequip item
						currPlayer.unequipWeapon();
						
						// Play equip sound.
						unequipWeapon.playSound(0.7f);
					}
					else {
						// Equip item
						i.equip();
						
						// Play equip sound.
						equipWeapon.playSound(0.7f);
					}
				}
				
			}
			else {
				// TODO: Play unequippable sound?
			}
		}
	}
	
	// Move the select around.
	public void moveSelect(String direction) {
		
		// Left
		if(direction=="left") {
			if(selectedSlot==0 ||
			(selectedSlot)/Math.sqrt(DEFAULT_INVENTORY_SIZE)==(int)((selectedSlot+1)/Math.sqrt(DEFAULT_INVENTORY_SIZE)));
			else {
				UIMove.playSound(0.7f);
				selectedSlot--;
			}
		}
		
		// Right
		if(direction=="right") {
			if((selectedSlot+1)/Math.sqrt(DEFAULT_INVENTORY_SIZE)==(int)((selectedSlot+1)/Math.sqrt(DEFAULT_INVENTORY_SIZE)));
			else {
				UIMove.playSound(0.7f);
				selectedSlot++;
			}
		}
		
		// Up
		if(direction=="up") {
			if(selectedSlot-Math.sqrt(DEFAULT_INVENTORY_SIZE) < 0);
			else {
				UIMove.playSound(0.7f);
				selectedSlot -= Math.sqrt(DEFAULT_INVENTORY_SIZE);
			}
		}
		
		// Down
		if(direction=="down") {
			if(selectedSlot + Math.sqrt(DEFAULT_INVENTORY_SIZE) >= DEFAULT_INVENTORY_SIZE);
			else {
				UIMove.playSound(0.7f);
				selectedSlot += Math.sqrt(DEFAULT_INVENTORY_SIZE);
			}
		}
	}

	// Draw the inventory.
	@Override
	public void drawObject(Graphics g) {
		if(isDisplayOn()) {
			
			// Draw the inventory background.
			g.drawImage(inventoryBackground, 
					getX(), 
					getY(), 
					inventoryBackground.getWidth(), 
					inventoryBackground.getHeight(), 
					null);
			
			// Adjustment for inventory background.
			int adjustX = 36;
			int adjustY = 50;
			
			// Draw each slot and an item in it if we have one.
			int x = 0;
			for(int i = 0; i < Math.sqrt(DEFAULT_INVENTORY_SIZE); i++) {
				for(int j = 0; j < Math.sqrt(DEFAULT_INVENTORY_SIZE); j++) {
					
					// Draw the slot.
					g.setColor(DEFAULT_SLOT_COLOR);
					g.drawRect(getX() + i*DEFAULT_SLOT_SIZE + adjustX, 
							getY() + j*DEFAULT_SLOT_SIZE + adjustY, 
							   DEFAULT_SLOT_SIZE, 
							   DEFAULT_SLOT_SIZE);
					
					// Draw the slot background.
					g.setColor(DEFAULT_SLOT_BACKGROUND_COLOR);
					g.fillRect(getX() + j*DEFAULT_SLOT_SIZE+1 + adjustX, 
							   getY() + i*DEFAULT_SLOT_SIZE+1 + adjustY, 
							   DEFAULT_SLOT_SIZE-1, 
							   DEFAULT_SLOT_SIZE-1);
					
					// If the slot is selected, then mark it.
					if(selectedSlot == i*Math.sqrt(DEFAULT_INVENTORY_SIZE) + j) {
						
						// Draw the yellow background for the selected slot.
						g.setColor(DEFAULT_SELECTED_SLOT_COLOR);
						g.fillRect(getX() + j*DEFAULT_SLOT_SIZE+1 + adjustX, 
								   getY() + i*DEFAULT_SLOT_SIZE+1 + adjustY, 
								   DEFAULT_SLOT_SIZE-1, 
								   DEFAULT_SLOT_SIZE-1);
						
					}
					
					// Draw the item, if it exists.
					if(x < items.size()) {
						
						// Draw the item, if it exists.
						item currentItem = items.get(x);
						g.setColor(DEFAULT_TEXT_COLOR);
						g.drawImage(currentItem.getImage(), 
								getX() + j*DEFAULT_SLOT_SIZE + DEFAULT_SLOT_SIZE/2 - currentItem.getImage().getWidth()/2 + adjustX, 
								getY() + i*DEFAULT_SLOT_SIZE + DEFAULT_SLOT_SIZE/2 - currentItem.getImage().getHeight()/2 + adjustY, 
								currentItem.getImage().getWidth(), 
								currentItem.getImage().getHeight(), 
								null);
						
						// Set color
						g.setColor(DEFAULT_SELECTED_SLOT_COLOR);
						
						// Get parameters.
						
						
						if(selectedSlot == i*Math.sqrt(DEFAULT_INVENTORY_SIZE) + j) {

							// Draw the item information on the right
							g.drawString(stringUtils.toTitleCase(currentItem.name), getX() + (int) (Math.sqrt(DEFAULT_INVENTORY_SIZE)*DEFAULT_SLOT_SIZE) + 10 + adjustX, getY()+ 34 + adjustY);
							
							// Draw weapon information.
							if(currentItem instanceof weapon) {
								weapon currentWeapon = (weapon)currentItem;
								g.drawString("Damage: " + currentWeapon.attackDamage, getX() + (int) (Math.sqrt(DEFAULT_INVENTORY_SIZE)*DEFAULT_SLOT_SIZE) + 10 + adjustX, getY()+ 34 + adjustY + 20);
								g.drawString("Speed: " + currentWeapon.speed, getX() + (int) (Math.sqrt(DEFAULT_INVENTORY_SIZE)*DEFAULT_SLOT_SIZE) + 10 + adjustX, getY()+ 34 + adjustY + 34);
								g.drawString("Range: " + currentWeapon.range, getX() + (int) (Math.sqrt(DEFAULT_INVENTORY_SIZE)*DEFAULT_SLOT_SIZE) + 10 + adjustX, getY()+ 34 + adjustY + 48);
								
								// Press e to equip.
								g.drawString(DEFAULT_BOTTOM_TEXT, getX() - 7 + (int) (Math.sqrt(DEFAULT_INVENTORY_SIZE)*DEFAULT_SLOT_SIZE) + adjustX, getY()+ 34 + adjustY + 140);
							}
						
						}
						else {
							
							// Draw empty slot.
							g.drawString(stringUtils.toTitleCase(DEFAULT_EMPTY_SLOT), getX() + (int) (Math.sqrt(DEFAULT_INVENTORY_SIZE)*DEFAULT_SLOT_SIZE) + 10 + adjustX, getY()+ 34  + adjustY);
						}
						x++;
					}
				}
			}
			
			// Draw the equipped weapon.
			g.setColor(DEFAULT_TEXT_COLOR);
			g.drawString("Weapon",
					   getX() + adjustX,
					   getY()  + adjustY + (int) (DEFAULT_SLOT_SIZE*Math.sqrt(DEFAULT_INVENTORY_SIZE)+23));
			
			// Draw the slot.
			g.setColor(DEFAULT_SLOT_COLOR);
			g.drawRect(getX() + DEFAULT_SLOT_SIZE/2 + 35 + adjustX, 
					   getY() + (int) (DEFAULT_SLOT_SIZE*Math.sqrt(DEFAULT_INVENTORY_SIZE)+19 - DEFAULT_SLOT_SIZE/2) + adjustY, 
					   DEFAULT_SLOT_SIZE, 
					   DEFAULT_SLOT_SIZE);
			
			// Draw slot background.
			g.setColor(DEFAULT_SLOT_BACKGROUND_COLOR);
			g.fillRect(getX() + DEFAULT_SLOT_SIZE/2 + 35 + adjustX+1, 
						getY() + (int) (DEFAULT_SLOT_SIZE*Math.sqrt(DEFAULT_INVENTORY_SIZE)+19 - DEFAULT_SLOT_SIZE/2) + adjustY + 1, 
					   DEFAULT_SLOT_SIZE-1, 
					   DEFAULT_SLOT_SIZE-1);
			
			// Draw the weapon.
			if(player.getCurrentPlayer().getEquippedWeapon() != null) {
				g.drawImage(player.getCurrentPlayer().getEquippedWeapon().getImage(), 
						getX() + DEFAULT_SLOT_SIZE + 35 - player.getCurrentPlayer().getEquippedWeapon().getImage().getWidth()/2 + adjustX, 
						getY() + (int) (DEFAULT_SLOT_SIZE*Math.sqrt(DEFAULT_INVENTORY_SIZE)+19) - player.getCurrentPlayer().getEquippedWeapon().getImage().getHeight()/2 + adjustY, 
						player.getCurrentPlayer().getEquippedWeapon().getImage().getWidth(), 
						player.getCurrentPlayer().getEquippedWeapon().getImage().getHeight(), 
						null);
			}
			
			// Draw the equipped potion.
			g.setColor(DEFAULT_TEXT_COLOR);
			g.drawString("Potion",
					   getX() + adjustX,
					   getY() + (int) (DEFAULT_SLOT_SIZE*Math.sqrt(DEFAULT_INVENTORY_SIZE)+56) + adjustY);
			
			// Draw the slot.
			g.setColor(DEFAULT_SLOT_COLOR);
			g.drawRect(getX() + DEFAULT_SLOT_SIZE/2 + 35 + adjustX, 
					   getY() + (int) (DEFAULT_SLOT_SIZE*Math.sqrt(DEFAULT_INVENTORY_SIZE)+53 - DEFAULT_SLOT_SIZE/2) + adjustY, 
					   DEFAULT_SLOT_SIZE, 
					   DEFAULT_SLOT_SIZE);
			
			// Draw slot background.
			g.setColor(DEFAULT_SLOT_BACKGROUND_COLOR);
			g.fillRect(getX() + DEFAULT_SLOT_SIZE/2 + 35 + adjustX+1, 
						getY() + (int) (DEFAULT_SLOT_SIZE*Math.sqrt(DEFAULT_INVENTORY_SIZE)+53 - DEFAULT_SLOT_SIZE/2) + adjustY + 1, 
					   DEFAULT_SLOT_SIZE-1, 
					   DEFAULT_SLOT_SIZE-1);
			
			// Draw the potion
			if(player.getCurrentPlayer().getEquippedPotion() != null) {
				g.drawImage(player.getCurrentPlayer().getEquippedWeapon().getImage(), 
						getX() + DEFAULT_SLOT_SIZE + 35 - player.getCurrentPlayer().getEquippedWeapon().getImage().getWidth()/2 + adjustX, 
						getY() + (int) (DEFAULT_SLOT_SIZE*Math.sqrt(DEFAULT_INVENTORY_SIZE)+53) - player.getCurrentPlayer().getEquippedWeapon().getImage().getHeight()/2 + adjustY, 
						player.getCurrentPlayer().getEquippedWeapon().getImage().getWidth(), 
						player.getCurrentPlayer().getEquippedWeapon().getImage().getHeight(), 
						null);
			}
		}
	}
	
	////////////////////////////
	/// GETTERS AND SETTERS ////
	////////////////////////////

	public boolean isDisplayOn() {
		return displayOn;
	}

	public void setDisplayOn(boolean displayOn) {
		this.displayOn = displayOn;
	}
	
	public item get(int i) {
		return items.get(i);
	}
	
	public int size() {
		return items.size();
	}
	
	public void add(item i) {
		items.add(i);
	}
}