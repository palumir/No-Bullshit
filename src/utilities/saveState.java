package utilities;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import effects.interfaceEffects.tooltipString;
import interactions.event;
import interactions.quest;
import items.bottle;
import items.inventory;
import items.item;
import units.player;
import units.unit;
import units.unitTypes.sheepFarm.blackWolf;

public class saveState implements Serializable {
	
	/////////////////////////
	////// DEFAULTS /////////
	/////////////////////////
	
	// Game saved text.
	public static String DEFAULT_GAME_SAVED_TEXT = "Game saved.";
	
	// Default saveFile
	private static String DEFAULT_SAVE_FILENAME = "save/gameData.save";

	///////////////////////
	////// FIELDS /////////
	///////////////////////
	
	/////////////////////////////////
	////// GLOBAL SAVE FIELDS////////
	/////////////////////////////////
	
	// Current zone name
	private String zoneName;
	
	// Position in current zone.
	private int playerX;
	private int playerY;
	
	// Choices
	private int chaosChoices;
	private int orderChoices;
	
	// Facing position
	private String facingDirection;
	
	// Player inventory
	private inventory playerInventory;
	private bottle equippedBottle;
	
	// Important units
	private ArrayList<HashMap<Object, Object>> importantUnits = new ArrayList<HashMap<Object, Object>>();
	
	// Last well coordinates
	public Point lastWell;
	
	// Last save bottle coordinates
	public ArrayList<Point> lastSaveBottles;
	
	// Save quietly?
	private static boolean quiet = false;
	
	// Events
	private CopyOnWriteArrayList<event> allEvents;
	
	// Quests
	private ArrayList<String> currentQuests;

	////////////////////////
	////// METHODS /////////
	////////////////////////
	
	// Constructor
	public saveState() {
		// Does nothing.
	}
	
	// Save important units
	public static ArrayList<HashMap<Object, Object>> loadImportantUnits(ObjectInputStream objectStream) throws IOException, ClassNotFoundException {
		
		ArrayList<HashMap<Object, Object>> importantUnits = new ArrayList<HashMap<Object, Object>>();
		
		// Read how many important units we have saved.
		int howMany = (int)objectStream.readObject();
		
		for(int i = 0; i < howMany; i++) {
			
			// Write the name of the thing.
			String unitName = (String)objectStream.readObject();
			
			// Write where it is
			String zoneName = (String)objectStream.readObject();
			int spawnedAtX = (int)objectStream.readObject();
			int spawnedAtY = (int)objectStream.readObject();
			
			// Add these things to the hashmap.
			importantUnits.add(new HashMap<Object,Object>());
			importantUnits.get(i).put("unitName", unitName);
			importantUnits.get(i).put("zoneName", zoneName);
			importantUnits.get(i).put("spawnedAtX", spawnedAtX);
			importantUnits.get(i).put("spawnedAtY", spawnedAtY);
			
			if(unitName.contains("blackWolf")) {
				
				// Write happiness to file.
				boolean isHappy = (boolean)objectStream.readObject();
				importantUnits.get(i).put("happy", isHappy);
				
				// Where are we sleeping?
				int x = (int)objectStream.readObject();
				int y = (int)objectStream.readObject();
				importantUnits.get(i).put("x", x);
				importantUnits.get(i).put("y", y);
			}
		}
		
		return importantUnits;
	}
	
	// Save important units
	public static void saveImportantUnits(ObjectOutputStream objectStream) throws IOException {
		
		ArrayList<unit> importantUnits = new ArrayList<unit>();
		
		if(unit.getAllUnits()!=null) {
			for(int i = 0; i < unit.getAllUnits().size(); i++) {
				
				unit currentUnit = unit.getAllUnits().get(i);
				
				// Important cases
				if(currentUnit.isSaveFields()) {
					importantUnits.add(currentUnit);
				}
			}
		}
		
		// Write how many important units we have saved.
		objectStream.writeObject(importantUnits.size());
		
		for(int i = 0; i < importantUnits.size(); i++) {
			
			unit currentUnit = importantUnits.get(i);
			
			// Write the name of the thing.
			objectStream.writeObject(currentUnit.getClass().getName());
			
			// Write where it is
			objectStream.writeObject(currentUnit.getZoneName());
			objectStream.writeObject(currentUnit.getSpawnedAtX());
			objectStream.writeObject(currentUnit.getSpawnedAtY());
			
			if(currentUnit instanceof blackWolf) {
				blackWolf b = (blackWolf)currentUnit;
				
				// Write happiness to file.
				objectStream.writeObject(b.isHappy());
				
				// Where are we sleeping?
				objectStream.writeObject(currentUnit.getIntX());
				objectStream.writeObject(currentUnit.getIntY());
			}
		}
	}
	
	// Save the game.
	public static void createSaveState() {
		try {
			if(player.playerLoaded && player.getPlayer()!= null) {
				
				// Display that we made a new savestate.
				if(!quiet) {
					tooltipString t = new tooltipString(DEFAULT_GAME_SAVED_TEXT);
				}
				
				// Create new saveState.
				saveState s = new saveState();
				
				// Load our player. 
				player currPlayer = player.getPlayer();
				s.setZoneName(currPlayer.getCurrentZone().getName());
				s.setPlayerX(currPlayer.getIntX());
				s.setPlayerY(currPlayer.getIntY());
				s.setFacingDirection(currPlayer.getFacingDirection());
				s.setPlayerInventory(currPlayer.getPlayerInventory());
				s.setEquippedBottle(currPlayer.getEquippedBottle());
				s.setCurrentQuests(quest.getCurrentQuests());
				
				// Save jokes.
				s.setAllEvents(event.loadedEvents);
				
				// Open the streams.
				FileOutputStream fileStream = new FileOutputStream(DEFAULT_SAVE_FILENAME);   
				ObjectOutputStream objectStream = new ObjectOutputStream(fileStream);  
				
				// Save all units
				saveImportantUnits(objectStream);
				
				// Write the save state to the file.
				objectStream.writeObject(s.getZoneName());
				objectStream.writeObject(s.getPlayerX());
				objectStream.writeObject(s.getPlayerY());
				objectStream.writeObject(s.getFacingDirection());
				
				// Choices	
				objectStream.writeObject(player.getPlayer().chaosChoices);
				objectStream.writeObject(player.getPlayer().orderChoices);
				
				// Last well position
				Point lastWell = player.getPlayer().lastWell;
				
				if(lastWell == null) {
					objectStream.writeObject(false);
					objectStream.writeObject(0);
					objectStream.writeObject(0);
				}
				else {
					objectStream.writeObject(true);
					objectStream.writeObject((int)lastWell.getX());
					objectStream.writeObject((int)lastWell.getY());
				}
				
				// Last save bottle
				ArrayList<Point> lastSaveBottles = player.getPlayer().lastSaveBottles;
				if(lastSaveBottles == null) objectStream.writeObject(0);
				else { 
					objectStream.writeObject(lastSaveBottles.size());
					
					for(int i = 0; i < lastSaveBottles.size(); i++) {
						objectStream.writeObject((int)lastSaveBottles.get(i).getX());
						objectStream.writeObject((int)lastSaveBottles.get(i).getY());
					}
				}
				
				
				////////////////
				/// EVENTS   ///
				////////////////
				// Write the length of the coming array.
				int gagsSize = 0;
				if(s.getAllEvents() != null) gagsSize = s.getAllEvents().size();
				objectStream.writeObject(gagsSize); 
				
				// Write the inventory (names of items) to save file.
				for(int i = 0; i < gagsSize; i++) {
					objectStream.writeObject(s.getAllEvents().get(i).getName());
					objectStream.writeObject(s.getAllEvents().get(i).isCompleted());
				}
				
				/////////////////
				/// INVENTORY ///
				/////////////////
				// Write the length of the coming array.
				int inventorySize = 0;
				if(s.getPlayerInventory() != null) inventorySize = s.getPlayerInventory().getPickedUpItems().size();
				objectStream.writeObject(inventorySize); 
				
				// Write the inventory (names of items) to save file.
				for(int i = 0; i < inventorySize; i++) {
					item currItem = s.getPlayerInventory().getPickedUpItems().get(i);
					
					// Write the item name.
					objectStream.writeObject(currItem.getClass().getName());
					
					// Write object's upgrade leve.
					objectStream.writeObject(currItem.upgradeLevel);
					
					// Write object's quality
					objectStream.writeObject(currItem.quality);
					
					// Write if it's in inventory or not
					objectStream.writeObject(currItem.inInventory);
					
					// Write if it's picked up or not.
					objectStream.writeObject(currItem.pickedUpItem);
					
					// Save zone
					objectStream.writeObject(currItem.discoverZone);
					
					// Save position.
					objectStream.writeObject(currItem.getIntX());
					objectStream.writeObject(currItem.getIntY());
					
					// Save the slot.
					objectStream.writeObject(currItem.slot);
					
					// For bottles, save the charges.
					if(currItem instanceof bottle) {
						objectStream.writeObject(((bottle)currItem).getChargesLeft());
						objectStream.writeObject(((bottle)currItem).getMaxCharges());
					}
					
				}
				
				//////////////////////
				/// CURRENT QUESTS ///
				//////////////////////
				// Write the length of the coming array.
				int questsSize = 0;
				if(s.getCurrentQuests() != null) questsSize = s.getCurrentQuests().size();
				objectStream.writeObject(questsSize); 
				
				// Write the inventory (names of items) to save file.
				for(int i = 0; i < questsSize; i++) {
					objectStream.writeObject(s.getCurrentQuests().get(i));
				}
				
				// Close the streams.
			    objectStream.close();   
			    fileStream.close(); 
			}
		}
		catch(Exception e) {
			e.printStackTrace();
			// Failed to save state.
		}
	}
	
	// Load the game
	public static saveState loadSaveState() {
		try {
			// Open the streams.
			FileInputStream fileStream = new FileInputStream(DEFAULT_SAVE_FILENAME);   
			ObjectInputStream objectStream = new ObjectInputStream(fileStream);
			
			// Create a new saveState
			saveState s = new saveState();
			
			// Okay buddy, let's set some fucking guidelines here!
			unit.setSavedUnits(loadImportantUnits(objectStream));
			
			// Write the objects to our fields.
			s.setZoneName((String) objectStream.readObject());
			s.setPlayerX((int) objectStream.readObject());
			s.setPlayerY((int) objectStream.readObject());
			s.setFacingDirection((String)objectStream.readObject());
			s.setChaosChoices(((int)objectStream.readObject()));
			s.setOrderChoices(((int)objectStream.readObject()));
			
			// Get the last well.
			boolean isThereAWell = (boolean) objectStream.readObject();
			if(isThereAWell) {
				s.lastWell = new Point((int)objectStream.readObject(),(int)objectStream.readObject());
			}
			else {
				objectStream.readObject();
				objectStream.readObject();
			}
			
			// Get the last bottle charge
			int howManySaveBottles = (int) objectStream.readObject();
			s.lastSaveBottles = new ArrayList<Point>();
			for(int i = 0; i < howManySaveBottles; i++) {
				s.lastSaveBottles.add(new Point((int)objectStream.readObject(),(int)objectStream.readObject()));
			}
			
			//////////////
			/// EVENTS ///
			//////////////
			// Write the length of the coming array.
			int eventsSize = (int)objectStream.readObject(); 
			
			// Create new array.
			ArrayList<event> newEvents = new ArrayList<event>();
			
			// Read the gags from save file.
			for(int i = 0; i < eventsSize; i++) {
				String theName = (String)objectStream.readObject();
				boolean completed = (boolean)objectStream.readObject();
				event g = event.createEvent(theName);
				g.setCompleted(completed);
				newEvents.add(g);
			}
			
			// Initiate items
			item.initiate();
			
			//////////////////
			/// INVENTORY ///
			//////////////////
			// Read the length of the coming array.
			int j = (int)objectStream.readObject();
			
			// Get the item pertaining to each name and add it to an array list.
			inventory newInventory = new inventory();
			for(int i = 0; i < j; i++) {
				
				// Write the item name.
				String itemName = (String)objectStream.readObject();
				
				// Update level
				int upgradeLevel = (int)objectStream.readObject();
				
				// Quality
				String quality = (String)objectStream.readObject();
				
				// Get whether it's in inventory or not.
				boolean inInventory = (boolean)objectStream.readObject();
				
				// Get whether it's picked up or not.
				boolean pickedUpItem = (boolean)objectStream.readObject();
				
				// Save zone
				String discoverZone = (String)objectStream.readObject();
				
				// x and y
				int x = (int)objectStream.readObject();
				int y = (int)objectStream.readObject();
			
				Class<?> clazz = Class.forName(itemName);
				Constructor<?> ctor = clazz.getConstructor(int.class, int.class);
				Object object = ctor.newInstance(new Object[] { x,
						y});
				
				// Save the slot.
				int slot = (int)objectStream.readObject();
				
				// Add the slot and discover zone to the item.
				item newItem = (item)object;
				newItem.setDrawObject(false); // Don't draw objects on the floor if they're in our inventory.
				newItem.inInventory = true; // Of course, we're loading it from the inventory.
				newItem.slot = slot;
				newItem.discoverZone = discoverZone;
				newItem.pickedUpItem = pickedUpItem;
				newItem.inInventory = inInventory;
				newItem.quality = quality;
				newItem.upgradeLevel = upgradeLevel;
				
				// Equip equipped items.
				if(newItem.slot != null && slot != KeyEvent.VK_WINDOWS) newInventory.equipItemSilent(newItem, slot);
				
				// For bottles, save the charges.
				if(newItem instanceof bottle) {
					((bottle) newItem).setChargesLeft((int)(objectStream.readObject()));
					((bottle) newItem).setMaxCharges((int)(objectStream.readObject()));
				}
				
				if(inInventory) {
					newInventory.add(newItem);
				}
				
				if(pickedUpItem) {
					newInventory.getPickedUpItems().add(newItem);
				}
				
			}
			s.setPlayerInventory(newInventory);
			
			//////////////
			/// QUESTS ///
			//////////////
			// Write the length of the coming array.
			int questsSize = (int)objectStream.readObject(); 
			
			// Create new array.
			ArrayList<String> newQuests = new ArrayList<String>();
			
			// Read the quests from save file.
			for(int i = 0; i < questsSize; i++) {
				String theName = (String)objectStream.readObject();
				newQuests.add(theName);
			}
			
			// Load the quests
			s.setCurrentQuests(newQuests);
			quest.setCurrentQuests(s.getCurrentQuests());
			
			// Close the streams.
		    objectStream.close();   
		    fileStream.close();   
			
		    // Return the state.
		    return s;
		}
		catch(Exception e) {
			// Initiate items
			item.initiate();
			
			e.printStackTrace();
			// Failed to load game.
			return null;
		}
	}
	
	public String getZoneName() {
		return zoneName;
	}

	public void setZoneName(String zoneName) {
		this.zoneName = zoneName;
	}

	public int getPlayerX() {
		return playerX;
	}

	public void setPlayerX(int playerX) {
		this.playerX = playerX;
	}

	public int getPlayerY() {
		return playerY;
	}

	public void setPlayerY(int playerY) {
		this.playerY = playerY;
	}

	public String getFacingDirection() {
		return facingDirection;
	}

	public void setFacingDirection(String facingDirection) {
		this.facingDirection = facingDirection;
	}

	public inventory getPlayerInventory() {
		return playerInventory;
	}

	public void setPlayerInventory(inventory playerInventory) {
		this.playerInventory = playerInventory;
	}

	public bottle getEquippedBottle() {
		return equippedBottle;
	}

	public void setEquippedBottle(bottle equippedBottle) {
		this.equippedBottle = equippedBottle;
	}

	public CopyOnWriteArrayList<event> getAllEvents() {
		return allEvents;
	}

	public void setAllEvents(CopyOnWriteArrayList<event> allEvents) {
		this.allEvents = allEvents;
	}

	public static boolean isQuiet() {
		return quiet;
	}

	public static void setQuiet(boolean quiet) {
		saveState.quiet = quiet;
	}

	public ArrayList<String> getCurrentQuests() {
		return currentQuests;
	}

	public void setCurrentQuests(ArrayList<String> currentQuests) {
		this.currentQuests = currentQuests;
	}

	public int getChaosChoices() {
		return chaosChoices;
	}

	public void setChaosChoices(int chaosChoices) {
		this.chaosChoices = chaosChoices;
	}

	public int getOrderChoices() {
		return orderChoices;
	}

	public void setOrderChoices(int orderChoices) {
		this.orderChoices = orderChoices;
	}
	
}