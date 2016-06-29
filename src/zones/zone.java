package zones;

import java.util.ArrayList;

import drawing.userInterface.tooltipString;
import units.player;
import utilities.intTuple;
import utilities.saveState;
import utilities.utility;
import zones.farmLand.sheepFarm;
import zones.farmLand.spiderCave;

public abstract class zone {
	
	// List of all zones.
	private static ArrayList<zone> allZones = new ArrayList<zone>();
	
	// Fields
	private String parentName;
	private String name;
	
	// Zone loaded?
	private boolean zoneLoaded = false;
	
	// Constructor
	public zone(String newName, String parentName) {
		
		// Set the name.
		setName(newName);
		
		// Add it to our list.
		allZones.add(this);
	}
	
	// Return the start zone.
	public static zone getStartZone() {
		return sheepFarm.getZone();
	}
	
	// Load the zone.
	public abstract void loadZone();
	
	// Switch zones.
	public static void switchZones(player p, zone a, zone b, int x, int y, String direction) {	
		// Save the player's progress before switching zones..
		saveState.createSaveState();
		
		// Re-create the player in the new zone.
		player.loadPlayer(player.getCurrentPlayer(), b, x, y, direction);
		
		// If we are going into the cave for the first time, display tooltip.
		if(spiderCave.enteredSpiderCaveBefore != null && !spiderCave.enteredSpiderCaveBefore.isCompleted()) {
			tooltipString t = new tooltipString("In platformer mode, press 'w' or 'up' to jump.");
			spiderCave.enteredSpiderCaveBefore.setCompleted(true);
			saveState.setQuiet(true);
			saveState.createSaveState();
			saveState.setQuiet(false);
		}
		else {
			// Save the player in the new zone.
			saveState.createSaveState();
		}
	}
	
	// Get zone by name.
	public static zone getZoneByName(String s) {
		for(int i=0; i < allZones.size(); i++) {
			zone currentZone = allZones.get(i);
			if(currentZone.getName().equals(s)) return currentZone; // Found the zone!
		}
		return null; // Didn't find the zone.
	}
	
	// TODO: Figure out a better way to initiate these variables so we
	// don't have to keep coming back and adding them here.
	// Initiate zones.
	public static void initiate() {
		
		// Sheep Farms
		sheepFarm.setZone(new sheepFarm());
		spiderCave.setZone(new spiderCave());
	}
	
	// Get the player location in the zone.
	public abstract intTuple getDefaultLocation();
	
	// Update.
	public void update() {
		
	}

	/////////////////////////
	// Getters and setters //
	/////////////////////////
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isZoneLoaded() {
		return zoneLoaded;
	}

	public void setZoneLoaded(boolean zoneLoaded) {
		this.zoneLoaded = zoneLoaded;
	}
}