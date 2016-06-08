package terrain.chunkTypes;

import java.util.Random;

import drawing.camera;
import modes.mode;
import terrain.chunk;
import terrain.chunkType;
import terrain.generalChunkType;
import terrain.groundTile;
import units.humanType;
import units.unit;
import units.unitType;
import utilities.time;
import zones.zone;

public class wood extends groundTile {
	
	////////////////
	/// DEFAULTS ///
	////////////////
	
	// Default name.
	private static String DEFAULT_CHUNK_NAME = "wood";
	
	// Tile sprite stuff
	private static String DEFAULT_CHUNK_SPRITESHEET = "images/terrain/" + DEFAULT_CHUNK_NAME + ".png";
	
	// Dimensions
	public static int DEFAULT_CHUNK_WIDTH = 32;
	public static int DEFAULT_CHUNK_HEIGHT = 32;
	
	// The actual type.
	private static generalChunkType typeReference = new generalChunkType(DEFAULT_CHUNK_NAME, DEFAULT_CHUNK_SPRITESHEET, DEFAULT_CHUNK_WIDTH, DEFAULT_CHUNK_HEIGHT);  
	
	///////////////
	/// METHODS ///
	///////////////
	// Constructor
	public wood(int newX, int newY) {
		super(typeReference, newX, newY);
		this.setPassable(true);
	}
}
