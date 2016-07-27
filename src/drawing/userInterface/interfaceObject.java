package drawing.userInterface;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import drawing.drawnObject;
import utilities.time;

public abstract class interfaceObject extends drawnObject  {
	
	/////////////////////////
	////// DEFAULTS /////////
	/////////////////////////
	
	public static ArrayList<interfaceObject> interfaceObjects;
	
	///////////////
	/// METHODS ///
	///////////////

	// Constructor
	public interfaceObject(BufferedImage newImage, int newX, int newY, int newWidth, int newHeight) {
		super(null, newX, newY, newWidth, newHeight);	
		setObjectImage(newImage);
		interfaceObjects.add(this);
	}
	
	// Respond to destroy
	@Override
	public void respondToDestroy() {
		if(interfaceObjects.contains(this)) interfaceObjects.remove(this);
	}
	
	// Update unit
	@Override
	public void update() {
	}
	
	// Initiate
	public static void initiate() {
		interfaceObjects = new ArrayList<interfaceObject>();
	}
	
}