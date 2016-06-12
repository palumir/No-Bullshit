package drawing;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.CopyOnWriteArrayList;

import effects.effect;
import units.player;
import units.unit;
import userInterface.interfaceObject;
import modes.mode;
import terrain.chunk;
import terrain.groundTile;
import terrain.doodads.general.questMark;
import utilities.intTuple;

// A class for any object that is drawn in the
// canvas.
public abstract class drawnObject {
	
	////////////////
	/// DEFAULTS ///
	////////////////
	private static Comparator<drawnObject> yComparator = new Comparator<drawnObject>() {
	    @Override
	    public int compare(drawnObject d1, drawnObject d2) {
	    	
	    	// Draw interface objects over anything.
	    	if(d1 instanceof interfaceObject) return 1;
	    	if(d2 instanceof interfaceObject) return -1;
	    	
	    	// Different comparator for drawing effects over things.
	    	if(d1 instanceof effect && (d1.y + d1.height > d2.y)) return 1;
	    	if(d2 instanceof effect && (d1.y < d2.y + d2.height)) return -1;
	    	
	    	// Prioritize units walking over chunks
	    	// and units walking in front of other units.
	    	if(!(d1 instanceof groundTile) && d2 instanceof groundTile) return 1;
	    	if(d1 instanceof groundTile && !(d2 instanceof groundTile)) return -1;
	        	
	        // Draw units closer to the camera first.
	    	if(d1.y + d1.height > d2.y + d2.height) return 1;
	    	if(d1.y + d1.height < d2.y + d2.height) return -1;
	    	return 0;
	    }
	};
	
	//////////////
	/// FIELDS ///
	//////////////
	private int x;
	private int y;
	protected int drawX;
	protected int drawY;
	protected int width;
	protected int height;
	
	// Do we actually draw the object?
	private boolean drawObject = true;
	
	// Can we interact with the object?
	protected boolean interactable = false;
	
	// Developer stuff
	protected boolean showHitBox = false;
	protected boolean showSpriteBox = false;
	protected boolean showUnitPosition = false;
	
	// Sprite stuff.
	private spriteSheet objectSpriteSheet;
	private BufferedImage objectImage;
	private int hitBoxAdjustmentX;
	private int hitBoxAdjustmentY;
	
	// Camera attached to the object,
	protected camera attachedCamera = null;
	
	// A list of things that we need to draw in general.
	public static CopyOnWriteArrayList<drawnObject> objects;
	
	///////////////
	/// METHODS ///
	///////////////
	// drawnObject constructor
	public drawnObject(spriteSheet newSpriteSheet, int newX, int newY, int newWidth, int newHeight) {
		objectSpriteSheet = newSpriteSheet;
		if(objectSpriteSheet!=null) {
			setHitBoxAdjustmentX(objectSpriteSheet.getHitBoxAdjustmentX());
			setHitBoxAdjustmentY(objectSpriteSheet.getHitBoxAdjustmentY());
		}
		setX(newX);
		setY(newY);
		width = newWidth;
		height = newHeight;
		addObject(this);
	}
	
	// Get units in box.
	public static ArrayList<drawnObject> getObjectsInBox(int x1, int y1, int x2, int y2) {
		ArrayList<drawnObject> returnList = new ArrayList<drawnObject>();
		for(int i = 0; i < objects.size(); i++) {
			drawnObject o = objects.get(i);
			if(o.getX() < x2 && 
					 o.getX() + o.width > x1 && 
					 o.getY() < y2 && 
					 o.getY() + o.height > y1) {
				returnList.add(o);
			}
		}
		if(returnList.size()==0) return null;
		return returnList;
	}
	
	// Every thing needs to update itself in some way.
	public void update() {
		// Do nothing for basic objects.
	}
	
	// Draw all objects.
	public static void drawObjects(Graphics g) {
		if(objects != null) {
			sortObjects();
			for(int i = 0; i < objects.size(); i++) {
				drawnObject d = objects.get(i);
				
				if(d.isDrawObject()) {
					// If there's a camera, adjust units drawn to the camera pos.
					if(camera.getCurrent() != null) {
						// TODO: possible issues with the screen being resized.
						d.drawX = d.getX() - camera.getCurrent().getX() - camera.getCurrent().getAttachedUnit().width/2 + gameCanvas.getDefaultWidth()/2;
						d.drawY = d.getY() - camera.getCurrent().getY() - camera.getCurrent().getAttachedUnit().height/2 + gameCanvas.getDefaultHeight()/2;
					}
					else {
						d.drawX = d.getX();
						d.drawY = d.getY();
					}
					
					// Get the correct sprite width and height.
					int spriteWidth = 0;
					int spriteHeight = 0;
					if(d.getObjectSpriteSheet() != null) {
						spriteWidth = d.getObjectSpriteSheet().getSpriteWidth();
						spriteHeight = d.getObjectSpriteSheet().getSpriteHeight();
					}
					else if(d.getObjectImage() != null) {
						spriteWidth = d.getObjectImage().getWidth();
						spriteHeight = d.getObjectImage().getHeight();
					}
					
					// Adjust for hitboxes.
					 d.drawX += - (spriteWidth/2 - d.width/2) - d.getHitBoxAdjustmentX();
					 d.drawY += - (spriteHeight/2 - d.height/2) - d.getHitBoxAdjustmentY();
					
					// Draw the object if it's on the screen.
					if(d instanceof interfaceObject ||
					   (d.drawX + spriteWidth > 0 && 
					   d.drawY + spriteHeight > 0 && 
					   d.drawX < gameCanvas.getDefaultWidth() && 
					   d.drawY < gameCanvas.getDefaultHeight()))
					d.drawObject(g);
				}
			}
		}
	}
	
	// Returns a tuple containing pertaining to how much the object would
	// be inside checkObject if moved to newX and newY. 0,0 otherwise.
	public boolean collides(int newX, int newY, drawnObject checkObject) {
		// Check each side.
		boolean intercepts = newX < checkObject.getX() + checkObject.width && 
							 newX + width > checkObject.getX() && 
							 newY < checkObject.getY() + checkObject.height && 
							 newY + height > checkObject.getY();
		return intercepts;
	}
	
	// Initiate drawnObjects
	public static void initiate() {
		objects = new CopyOnWriteArrayList<drawnObject>();
	}
	
	// Draw object.
	public abstract void drawObject(Graphics g);
	
	// Destroy an object.
	public void destroy() {
		drawnObject.removeObject(this);
	}
	
	// Interact with object. Should be over-ridden.
	public void interactWith() {
		
	}
	
	///////////////////////////
	/// Getters and Setters ///
	///////////////////////////
	public boolean canInteract() {
		return interactable;
	}
	
	public void showHitBox() {
		showHitBox = true;
	}
	
	public void showSpriteBox() {
		showSpriteBox = true;
	}
	
	public void showUnitPosition() {
		showUnitPosition = true;
	}
	
	public static void addObject(drawnObject d) {
		objects.add(d);
	}
	
	public static void removeObject(drawnObject d) {
		objects.remove(d);
	}
	
	public static void sortObjects() {
		if(mode.getCurrentMode() == "topDown") Collections.sort(objects, yComparator);
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public spriteSheet getObjectSpriteSheet() {
		return objectSpriteSheet;
	}

	public void setObjectSpriteSheet(spriteSheet objectSpriteSheet) {
		this.objectSpriteSheet = objectSpriteSheet;
	}

	public int getHitBoxAdjustmentY() {
		return hitBoxAdjustmentY;
	}

	public void setHitBoxAdjustmentY(int hitBoxAdjustmentY) {
		this.hitBoxAdjustmentY = hitBoxAdjustmentY;
	}

	public int getHitBoxAdjustmentX() {
		return hitBoxAdjustmentX;
	}

	public void setHitBoxAdjustmentX(int hitBoxAdjustmentX) {
		this.hitBoxAdjustmentX = hitBoxAdjustmentX;
	}

	public boolean isDrawObject() {
		return drawObject;
	}

	public void setDrawObject(boolean drawObject) {
		this.drawObject = drawObject;
	}

	public BufferedImage getObjectImage() {
		return objectImage;
	}

	public void setObjectImage(BufferedImage objectImage) {
		this.objectImage = objectImage;
	}
	
}