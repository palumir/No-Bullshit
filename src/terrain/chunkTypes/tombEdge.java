package terrain.chunkTypes;

import terrain.chunk;
import terrain.generalChunkType;
import terrain.groundTile;
import zones.zone;

public class tombEdge extends groundTile {
	
	////////////////
	/// DEFAULTS ///
	////////////////
	
	// Default name.
	private static String DEFAULT_CHUNK_NAME = "tombDirt";
	
	// KNIGHT sprite stuff.
	private static String DEFAULT_CHUNK_SPRITESHEET = "images/terrain/tombDirt.png";
	
	// Dimensions
	public static int DEFAULT_CHUNK_WIDTH = 32;
	public static int DEFAULT_CHUNK_HEIGHT = 32;
	
	// The actual type.
	private static generalChunkType typeReference = new generalChunkType(DEFAULT_CHUNK_NAME, DEFAULT_CHUNK_SPRITESHEET, DEFAULT_CHUNK_WIDTH, DEFAULT_CHUNK_HEIGHT);  
	
	// Create function
	public static chunk createChunk(int newX, int newY, int i) {
		if(!zone.loadedOnce) {
			chunk t = new tombEdge(newX,newY,i);
			t.setReloadObject(false);
			return t;
		}
		else {
			return null;
		}
	}

	
	///////////////
	/// METHODS ///
	///////////////
	// Constructor
	public tombEdge(int newX, int newY, int i) {
		super(typeReference, newX, newY, i);
		setPassable(false);
	}
}
