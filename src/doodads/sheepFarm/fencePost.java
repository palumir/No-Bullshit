package doodads.sheepFarm;

import modes.mode;
import terrain.chunk;
import terrain.generalChunkType;

public class fencePost extends chunk {
	
	////////////////
	/// DEFAULTS ///
	////////////////
	
	// Default name.
	private static String DEFAULT_CHUNK_NAME = "fencePost";
	
	// Sprite stuff.
	private static String DEFAULT_CHUNK_SPRITESHEET = "images/doodads/farmLand/sheepFarm/"+ DEFAULT_CHUNK_NAME + ".png";
	
	// Dimensions
	public static int DEFAULT_CHUNK_WIDTH = 5;
	public static int DEFAULT_CHUNK_HEIGHT = 26;
	
	// The actual type.
	private static generalChunkType typeReference = new generalChunkType(DEFAULT_CHUNK_NAME, DEFAULT_CHUNK_SPRITESHEET, DEFAULT_CHUNK_WIDTH, DEFAULT_CHUNK_HEIGHT);  

	///////////////
	/// METHODS ///
	///////////////
	// Constructor
	public fencePost(int newX, int newY, int i) {
		super(typeReference, newX, newY, i, 0);
		if(mode.getCurrentMode().equals("topDown")) {
			setHitBoxAdjustmentY(8);
			setHeight(6);
		}
		else {
			setHitBoxAdjustmentY(0);
			setHeight(DEFAULT_CHUNK_HEIGHT);
			setWidth(DEFAULT_CHUNK_WIDTH);
		}
		setPassable(false);
	}
}
