package doodads.cave;

import java.util.ArrayList;
import java.util.Random;

import drawing.camera;
import interactions.event;
import interactions.interactBox;
import interactions.textSeries;
import modes.mode;
import terrain.chunk;
import terrain.chunkType;
import terrain.generalChunkType;
import units.humanType;
import units.unit;
import units.unitType;
import utilities.stringUtils;
import utilities.time;
import zones.zone;

public class webSmall extends chunk {
	
	////////////////
	/// DEFAULTS ///
	////////////////
	
	// Default name.
	private static String DEFAULT_CHUNK_NAME = "Web Small";
	
	// Sprite stuff.
	private static String DEFAULT_CHUNK_SPRITESHEET = "images/doodads/farmLand/spiderCave/webSmall.png";
	
	// Dimensions
	private static int DEFAULT_SPRITE_WIDTH = 118;
	private static int DEFAULT_SPRITE_HEIGHT = 118;

	// Topdown
	private static int DEFAULT_TOPDOWN_ADJUSTMENT_Y = 0;
	private static int DEFAULT_TOPDOWN_HEIGHT = 118;
	private static int DEFAULT_TOPDOWN_WIDTH = 118;

	// Platformer.
	private static int DEFAULT_PLATFORMER_ADJUSTMENT_Y = 0;
	private static int DEFAULT_PLATFORMER_HEIGHT = 105;
	private static int DEFAULT_PLATFORMER_WIDTH = 105;
	
	// Default Z
	private static int DEFAULT_Z = -50;
	
	// The actual type.
	private static generalChunkType typeReference = new generalChunkType(DEFAULT_CHUNK_NAME, DEFAULT_CHUNK_SPRITESHEET, DEFAULT_SPRITE_WIDTH, DEFAULT_SPRITE_HEIGHT);  
	
	// Currently stuck units
	private ArrayList<unit> currentlyStuckUnits = new ArrayList<unit>();
	private ArrayList<unit> recentlyUnstuckUnits = new ArrayList<unit>();
	private ArrayList<Long> whenUnitsWereUnstuck = new ArrayList<Long>();
	private float howLongBeforeUnstuck = 0.05f;
	
	////////////////
	/// FIELDS /////
	////////////////

	// Make the web actually sticky.
	public void beSticky() {
		
		// Add units walking into the web into the stuck units array.
		ArrayList<unit> stuckUnits = unit.getUnitsInBox(getX(),getY(), getX() + getWidth(), getY() + getHeight());
		
		// Stick each unit
		if(stuckUnits != null) {
			for(int i = 0; i < stuckUnits.size(); i++) {
				stuckUnits.get(i).setStuck(true);
				if(!currentlyStuckUnits.contains(stuckUnits.get(i))) {
					currentlyStuckUnits.add(stuckUnits.get(i));
				}
			}
		}
		
		// Remove units who aren't stuck anymore.
		for(int i = 0; i < currentlyStuckUnits.size();) {
			if(!currentlyStuckUnits.get(i).isWithin(getX(),getY(), getX() + getWidth(), getY() + getHeight())) {
				currentlyStuckUnits.get(i).setStuck(false);
				recentlyUnstuckUnits.add(currentlyStuckUnits.get(i));
				whenUnitsWereUnstuck.add(time.getTime());
				currentlyStuckUnits.remove(i);
			}
			else {
				i++;
			}
		}
		
		// Unstuck the units.
		for(int i = 0; i < recentlyUnstuckUnits.size(); i++) {
			if(time.getTime() - whenUnitsWereUnstuck.get(i) > howLongBeforeUnstuck*1000) {
				whenUnitsWereUnstuck.remove(i);
				recentlyUnstuckUnits.get(i).setStuck(false);
				recentlyUnstuckUnits.remove(i);
			}
			else {
				recentlyUnstuckUnits.get(i).touchDown();
				i++;
			}
		}
	}
	
	// Update
	@Override
	public void update() {
		// Do web stuff.
		beSticky();
	}
	
	// Constructor
	public webSmall(int newX, int newY, int i) {
		super(typeReference, newX, newY, i, 0);
		if(mode.getCurrentMode().equals("topDown")) {
			setHitBoxAdjustmentY(DEFAULT_TOPDOWN_ADJUSTMENT_Y);
			setWidth(DEFAULT_TOPDOWN_WIDTH);
			setHeight(DEFAULT_TOPDOWN_HEIGHT);
		}
		else {
			setHitBoxAdjustmentY(DEFAULT_PLATFORMER_ADJUSTMENT_Y);
			setHeight(DEFAULT_PLATFORMER_HEIGHT);
			setWidth(DEFAULT_PLATFORMER_WIDTH);
		}
		
		// Set z.
		setZ(DEFAULT_Z);
		
		// Passable.
		setPassable(true);
	}
}