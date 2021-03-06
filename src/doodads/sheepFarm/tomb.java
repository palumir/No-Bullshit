package doodads.sheepFarm;

import modes.mode;
import terrain.chunk;
import terrain.generalChunkType;
import units.player;
import zones.zone;

public class tomb extends chunk {
	
	////////////////
	/// DEFAULTS ///
	////////////////
	
	// Default name.
	private static String DEFAULT_CHUNK_NAME = "tomb";
	
	// Sprite stuff.
	private static String DEFAULT_CHUNK_SPRITESHEET = "images/doodads/farmLand/sheepFarm/"+ DEFAULT_CHUNK_NAME + ".png";
	
	// Dimensions
	private static int DEFAULT_CHUNK_WIDTH = 107;
	private static int DEFAULT_CHUNK_HEIGHT = 142;
	
	// The actual type.
	private static generalChunkType typeReference = new generalChunkType(DEFAULT_CHUNK_NAME, DEFAULT_CHUNK_SPRITESHEET, DEFAULT_CHUNK_WIDTH, DEFAULT_CHUNK_HEIGHT);  
	
	//////////////
	/// FIELDS ///
	//////////////
	private zone toZone;
	private int spawnX;
	private int spawnY;
	private String spawnDirection;
	
	///////////////
	/// METHODS ///
	///////////////
	
	// Constructor
	public tomb(int newX, int newY, int i, zone newZone, int newSpawnX, int newSpawnY, String direction) {
		super(typeReference, newX, newY, i, 0);
		toZone = newZone;
		spawnDirection = direction;
		spawnX = newSpawnX;
		spawnY = newSpawnY;
		if(mode.getCurrentMode().equals("topDown")) {
			setHitBoxAdjustmentY(12);
			setHeight(100);
			setWidth(60);
		}
		else {
			setHitBoxAdjustmentY(12);
			setHeight(55);
			setWidth(20);
		}
		setPassable(true);
	}
	
	// Update.
	@Override
	public void update() {
		if(this.collides(this.getIntX(), this.getIntY(), player.getPlayer())) {
			enter();
		}
	}
	
	// Enter the cave
	public void enter() {
		zone.switchZones(player.getPlayer(), player.getPlayer().getCurrentZone(), toZone, spawnX, spawnY, spawnDirection);
	}
}
