package terrain;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import drawing.drawnObject;
import drawing.gameCanvas;
import drawing.spriteSheet;
import drawing.userInterface.interfaceObject;
import effects.effect;
import effects.effectTypes.floatingString;
import modes.mode;
import units.player;
import units.unit;
import utilities.intTuple;

public class chunk extends drawnObject {
	////////////////
	/// DEFAULTS ///
	////////////////

	private static Comparator<chunk> chunkComparator = new Comparator<chunk>() {
	       public int compare(chunk c1, chunk c2) {
	         int result = Double.compare(c1.getIntX(), c2.getIntX());
	         if ( result == 0 ) {
	           // both X are equal -> compare Y too
	           result = Double.compare(c1.getIntY(), c2.getIntY());
	         } 
	         return result;
	      }
	};

	// All chunks.
	public static ArrayList<chunk> allChunks;
	public static CopyOnWriteArrayList<chunk> impassableChunks;
	
	// Largest chunk size.
	private static int largestChunkWidth = 0;
	private static int largestChunkHeight = 0;
	
	// Default passable boolean
	private boolean DEFAULT_PASSABLE = true;
	
	//////////////
	/// FIELDS ///
	//////////////
	// The image of the chunk.
	protected BufferedImage chunkImage;
	
	// Is the chunk passable or impassable?
	private boolean passable;

	//////////////
	/// MEHODS ///
	//////////////
	// Constructor for choosing a random variation of the chunk.
	public chunk(chunkType c, int newX, int newY) {
		super(c.getChunkTypeSpriteSheet(), newX, newY, c.getWidth(), c.getHeight());
		
		// Set our image field.
		chunkImage = c.getChunkImage();
		
		// Set default passable
		passable = DEFAULT_PASSABLE;
		
		// Remember our chunks.
		allChunks.add(this);
		if(!passable) impassableChunks.add(this);
		
		// Set largest.
		if(c.getWidth() > largestChunkWidth) largestChunkWidth = c.getWidth();
		if(c.getHeight() > largestChunkHeight) largestChunkHeight = c.getHeight();
	}
	
	// Constructor for choosing a given variation of the chunk.
	public chunk(chunkType c, int newX, int newY, int i, int j) {
		super(c.getChunkTypeSpriteSheet(), newX, newY, c.getWidth(), c.getHeight());
		
		// Set our image field.
		if(c.getChunkTypeSpriteSheet() != null) chunkImage = c.getChunkImage(i, j);
		
		// Set default passable
		passable = DEFAULT_PASSABLE;
		
		// Remember our chunks.
		allChunks.add(this);
		
		// Set largest.
		if(c.getWidth() > largestChunkWidth) largestChunkWidth = c.getWidth();
		if(c.getHeight() > largestChunkHeight) largestChunkHeight = c.getHeight();
	}
	
	// Check if a unit collides with any chunk. Returns by how much.
	public static intTuple collidesWith(drawnObject u, int newX, int newY) {
		
		// Check if it collides in x or y position.
		boolean tX = false;
		boolean tY = false;
		
		// Phase 1 TODO: make it binary search? This is faster, but binary search is very fast.
		boolean phaseOneOver = false;
		
		if(allChunks != null && impassableChunks.size() > 0) {
			int i = 0;
			int jump = (int) (impassableChunks.size()*0.10f); // Jump by 15%
			while(i < impassableChunks.size()) {
				chunk currChunk = impassableChunks.get(i);
				if(phaseOneOver) {
					// If we collide with one, return the tuple containing by how much.
					if(u.collides(newX, u.getIntY(),currChunk)) tX = true;
					if(u.collides(u.getIntX(), newY,currChunk)) tY = true;
					i++;
				}
				else {
					if(currChunk.getIntX() + largestChunkWidth >= u.getIntX()) {
						phaseOneOver = true;
						if(i - jump < 0) i = 0;
						else i -= jump;
					}
					else {
						i += jump;
					}
				}
			}
		}
		
		// Make an intTuple for the return.
		int txInt = 0;
		int tyInt = 0;
		if(tX) txInt = 1;
		if(tY) tyInt = 1;
		
		if(tX || tY) return new intTuple(txInt, tyInt);
		else return intTuple.emptyTuple;
	}

	// Draw the chunk. 
	@Override
	public void drawObject(Graphics g) {
		//showHitBox();
		
		// Draw it. 
		if(getChunkImage() != null) {
			int changeFactor = 0;
			if(gameCanvas.getScaleX() != 1f || gameCanvas.getScaleY() != 1f) changeFactor = 1;
			if(isDrawSprite()) g.drawImage(getChunkImage(), 
					getDrawX(), 
					getDrawY(), 
					(int)(gameCanvas.getScaleX()*getChunkImage().getWidth() + changeFactor), 
					(int)(gameCanvas.getScaleY()*getChunkImage().getHeight() + changeFactor), 
					null);
			
			// Draw the outskirts of the sprite.
			if(showSpriteBox) {
				g.setColor(Color.red);
				g.drawRect(getDrawX(),
						   getDrawY(), 
						   (int)(gameCanvas.getScaleX()*getObjectSpriteSheet().getSpriteWidth()), 
						   (int)(gameCanvas.getScaleY()*getObjectSpriteSheet().getSpriteHeight()));
			}
			
			// Draw the hitbox of the image in green.
			if(showHitBox) {
				g.setColor(Color.green);
				g.drawRect(getDrawX() - (int)(gameCanvas.getScaleX()*(- (getObjectSpriteSheet().getSpriteWidth()/2 - getWidth()/2) - getHitBoxAdjustmentX())),
						   getDrawY() - (int)(gameCanvas.getScaleY()*(- (getObjectSpriteSheet().getSpriteHeight()/2 - getHeight()/2) - getHitBoxAdjustmentY())), 
						   (int)(gameCanvas.getScaleX()*getWidth()), 
						   (int)(gameCanvas.getScaleY()*getHeight()));
			}
			
			// Draw the x,y coordinates of the unit.
			if(showUnitPosition) {
				g.setColor(Color.white);
				g.drawString((int)(gameCanvas.getScaleX()*getIntX()) + "," + (int)(gameCanvas.getScaleX()*getIntY()),
						   getDrawX(),
						   getDrawY());
			}
		}
	}
	
	// Get units in box.
	public static ArrayList<chunk> getImpassableChunksInBox(int x1, int y1, int x2, int y2) {
		ArrayList<chunk> returnList = new ArrayList<chunk>();
		for(int i = 0; i < impassableChunks.size(); i++) {
			chunk c = impassableChunks.get(i);
			if(c.isWithin(x1, y1, x2, y2)) {
				returnList.add(c);
			}
		}
		if(returnList.size()==0) return null;
		return returnList;
	}
	
	// Check if a chunk is within 
	public boolean isWithin(int x1, int y1, int x2, int y2) {
		return getIntX() < x2 && 
		 getIntX() + getWidth() > x1 && 
		 getIntY() < y2 && 
		 getIntY() + getHeight() > y1;
	}
	
	// Initiate chunks
	public static void initiate() {
		allChunks = new ArrayList<chunk>();
		impassableChunks = new CopyOnWriteArrayList<chunk>();
	}
	
	// Sort chunks.
	public static void sortChunks() {
		Collections.sort(impassableChunks, chunkComparator);
	}
	
	////////////////////////////////////
	////// GETTERS AND SETTERS /////////
	////////////////////////////////////
	// Get chunk image.
	public BufferedImage getChunkImage() {
		return chunkImage;
	}
	
	public boolean isPassable() {
		return passable;
	}
	
	public void setPassable(boolean b) {
		
		// Add to unpassable blocks.
		if(b == false) {
			impassableChunks.add(this);
		}
		if(b == true && impassableChunks.contains(this)) {
			impassableChunks.remove(this);
		}
		passable = b;
	}
	
}