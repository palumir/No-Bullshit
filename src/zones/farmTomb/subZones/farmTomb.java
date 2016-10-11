package zones.farmTomb.subZones;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import doodads.tomb.stairsUp;
import drawing.background;
import drawing.spriteSheet;
import interactions.event;
import items.bottleShards.jumpBottleShard;
import items.bottles.pushBottle;
import items.other.bottleExpander;
import sounds.music;
import terrain.chunk;
import terrain.atmosphericEffects.fog;
import terrain.chunkTypes.tomb;
import terrain.chunkTypes.tombEdge;
import units.player;
import units.unit;
import units.bosses.fernando.fernando;
import units.bosses.graveKeeper.cinematics.wolflessFightCinematic;
import units.bosses.rodriguez.rodriguez;
import units.bosses.rodriguez.cinematics.farmTombCinematic;
import units.bosses.wolfless.wolfless;
import units.unitCommands.commandList;
import units.unitCommands.commands.moveCommand;
import units.unitTypes.tomb.lightDude;
import units.unitTypes.tomb.shadowDude;
import utilities.intTuple;
import utilities.levelSave;
import utilities.saveState;
import zones.zone;
import zones.sheepFarm.subZones.sheepFarm;

public class farmTomb extends zone {
	
	//////////////////////////////////
	// FIELDS, GLOBALS, CONSTRUCTOR //
	//////////////////////////////////
	
	// Static caller of the zone.
	private static zone zoneReference;
	
	// Default background.
	private static BufferedImage DEFAULT_ZONE_BACKGROUND = spriteSheet.getSpriteFromFilePath("images/terrain/backgrounds/tombBackground.png");
	
	// Zone music.
	public static String zoneMusic = "sounds/music/farmLand/tomb/tomb.wav";
	public static String zoneMusicFrantic = "sounds/music/farmLand/tomb/tombBossFight.wav";
	
	// Default zone mode
	private static String DEFAULT_ZONE_MODE = "platformer";
	
	// References we will use throughout.
	static unit u;
	static chunk c;
	static commandList commands;
	
	// Some defaults.
	public static int BACKGROUND_Z = -100;
	
	// Zone events.
	public static event enteredtombZoneBefore;
	public static event shadowBossFightStarted;
	public static boolean shadowBossFightFirstTime;
	public static event fernandoRodriguezInteraction;
	
	// Initiated?
	public boolean shadowBossFightInitiated = false;
	
	// Defaults
	public static intTuple DEFAULT_SPAWN_TUPLE = new intTuple(0,-50);
	
	 // BossFight loaded?
	static boolean bossFightLoaded = false;
	
	// Zone fog
	public static fog zoneFog;
	
	// Constructor
	public farmTomb() {
		super("farmTombEasy", "farmLand");
		zoneMusics.add(zoneMusic);
		zoneMusics.add(zoneMusicFrantic);
	}
		
	///////////////////////////////
	// SPAWN PATTERNS/GENERATORS //
	///////////////////////////////
	
	// Spawn grass dirt x to y.
	public static void spawnReloadableTombRect(int x1, int y1, int x2, int y2, String type) {
			
			int numX = (x2 - x1)/tomb.DEFAULT_CHUNK_WIDTH;
			int numY = (y2 - y1)/tomb.DEFAULT_CHUNK_HEIGHT;
			for(int i = 0; i < numX; i++) {
				for(int j = 0; j < numY; j++) {
					if((i == numX-1 || i == 0 || j == 0 || j == numY-1)) {
						if(j==0 && type.equals("ground")) {
							c = new tombEdge(i*tomb.DEFAULT_CHUNK_WIDTH + x1, j*tomb.DEFAULT_CHUNK_HEIGHT + y1, 0);
						}
						else if(i == numX - 1 && type.equals("leftWall")) {
							c = new tombEdge(i*tomb.DEFAULT_CHUNK_WIDTH + x1, j*tomb.DEFAULT_CHUNK_HEIGHT + y1, 1);
						}
						else if(i==0 && type.equals("rightWall")) {
							c = new tombEdge(i*tomb.DEFAULT_CHUNK_WIDTH + x1, j*tomb.DEFAULT_CHUNK_HEIGHT + y1, 3);
						}
						else if(j==numY - 1 && type.equals("roof")) {
							c = new tombEdge(i*tomb.DEFAULT_CHUNK_WIDTH + x1, j*tomb.DEFAULT_CHUNK_HEIGHT + y1, 2);
						}
						else {
							c = new tomb(i*tomb.DEFAULT_CHUNK_WIDTH + x1, j*tomb.DEFAULT_CHUNK_HEIGHT + y1);
						}
					}
					else { 
						c = new tomb(i*tomb.DEFAULT_CHUNK_WIDTH + x1, j*tomb.DEFAULT_CHUNK_HEIGHT + y1);
						 if(c!=null) c.setPassable(true);
					}
				}
			}
	}
	
	// Spawn grass dirt x to y.
	public static void spawnTombRect(int x1, int y1, int x2, int y2, String type) {
			
			int numX = (x2 - x1)/tomb.DEFAULT_CHUNK_WIDTH;
			int numY = (y2 - y1)/tomb.DEFAULT_CHUNK_HEIGHT;
			for(int i = 0; i < numX; i++) {
				for(int j = 0; j < numY; j++) {
					if((i == numX-1 || i == 0 || j == 0 || j == numY-1)) {
						if(j==0 && type.equals("ground")) {
							tombEdge.createChunk(i*tomb.DEFAULT_CHUNK_WIDTH + x1, j*tomb.DEFAULT_CHUNK_HEIGHT + y1, 0);
						}
						else if(i == numX - 1 && type.equals("leftWall")) {
							tombEdge.createChunk(i*tomb.DEFAULT_CHUNK_WIDTH + x1, j*tomb.DEFAULT_CHUNK_HEIGHT + y1, 1);
						}
						else if(i==0 && type.equals("rightWall")) {
							tombEdge.createChunk(i*tomb.DEFAULT_CHUNK_WIDTH + x1, j*tomb.DEFAULT_CHUNK_HEIGHT + y1, 3);
						}
						else if(j==numY - 1 && type.equals("roof")) {
							tombEdge.createChunk(i*tomb.DEFAULT_CHUNK_WIDTH + x1, j*tomb.DEFAULT_CHUNK_HEIGHT + y1, 2);
						}
						else {
							tomb.createChunk(i*tomb.DEFAULT_CHUNK_WIDTH + x1, j*tomb.DEFAULT_CHUNK_HEIGHT + y1);
						}
					}
					else { 
						 c = tomb.createChunk(i*tomb.DEFAULT_CHUNK_WIDTH + x1, j*tomb.DEFAULT_CHUNK_HEIGHT + y1);
						 if(c!=null) c.setPassable(true);
					}
				}
			}
	}
	
	
	/////////////////
	// ZONE LOADER //
	/////////////////
	// Load the zone.
	public void loadSpecificZoneStuff() {
		
		// Shadow bossFight not loaded
		bossFightLoaded = false;
		
		// Set the mode of the zone of course.
		setMode(DEFAULT_ZONE_MODE);
		
		// Set the darkness.
		zoneFog = new fog();
		zoneFog.setTo(0.3f);
		
		// BossFight not initiated
		shadowBossFightInitiated = false;
		
		// Load stuff so the zone doesn't lag
		preLoadStuff();
		
		// Load zone events.
		loadZoneEvents();
		
		//levelSave.loadSaveState("actualTombLevel.save");
		
		// Load the level save.
		//farmTombZoneLoader loader = new farmTombZoneLoader();
		//loader.loadSegments();

		// Load zone items
		loadItems();
		
		// Load units
		loadUnits();
				
		// Background
		background.setGameBackground(DEFAULT_ZONE_BACKGROUND);
		
		// Spawn area.
		createNonEditorChunks();
		
		// Play zone music.
		if(!shadowBossFightStarted.isCompleted()) { music.startMusic(zoneMusic);  }
		else {
			if(player.getPlayer().isWithin((int)player.getPlayer().lastWell.getX()-200, 
					(int)player.getPlayer().lastWell.getY()-200, 
					(int)player.getPlayer().lastWell.getX()+250, 
					(int)player.getPlayer().lastWell.getY()+250)) {
				shadowBossFightStarted.setCompleted(false);
				shadowBossFightFirstTime = true;
				music.startMusic(zoneMusic); 
				saveState.setQuiet(true);
				saveState.createSaveState();
				saveState.setQuiet(false);
			}
			else {
				// Load bossFight
				createShadowBossFightAroundPlayer();
			}
		}
		
	}
	
	// PreloadStuff
	public void preLoadStuff() {
		int holder = fernando.getDefaultHeight();
		holder = rodriguez.getDefaultHeight();
		holder = wolfless.leniency;
	}
	
	// Load items
	public void loadItems() {
		pushBottle b = new pushBottle(2867, 1398+32);
		bottleExpander exp = new bottleExpander(11498,2333);
		exp.quality = "Alright";
	}
	
	// Load units
	public void loadUnits() {

	}
	
	// Load zone events.
	public void loadZoneEvents() {
		
		// Have we entered the dirt before?
		enteredtombZoneBefore = new event("enteredtombZoneBefore");
		
		// Has the BossFight started?
		shadowBossFightStarted = new event("tombZoneShadowBossFightStarted");
		
		// Have we interacted with these two yet?
		fernandoRodriguezInteraction = new event("tombZoneFernandoRodriguezInteraction");
	}
	
	
	public static void makeShadowRectangle(int topLeftDudePosX, int topLeftDudePosY, int spreadX, int spreadY, int numDudesWidth, int numDudesHeight, float speed, boolean clockwise) {
		
		commandList squareCommands = new commandList();
		for(int i = 0; i < numDudesWidth; i++) {
			squareCommands.add(new moveCommand(i,0));
		}
		for(int i = 0; i < numDudesHeight; i++) {
			squareCommands.add(new moveCommand(numDudesWidth-1,i));
		}
		for(int i = numDudesWidth-1; i > 0; i--) {
			squareCommands.add(new moveCommand(i,numDudesHeight-1));
		}
		for(int i = numDudesHeight-1; i > 0; i--) {
			squareCommands.add(new moveCommand(0,i));
		}
		
		for(int i = 0; i < numDudesWidth; i++) {
		      for(int j = 0; j < numDudesHeight; j++) {
		       
		    	  if(i != 0 && j != 0 && j != numDudesHeight - 1 && i != numDudesWidth - 1) {
		    		  // Do nothing.
		    	  }
		    	  
		    	  else {
		    		  
		    		  // How far they are spread
		    		  int spreadOutX = spreadX;
		    		  int spreadOutY = spreadY;
		    		    
		    		  // Spawn the shadow dude
		    		  u = new shadowDude(topLeftDudePosX + i*spreadOutX,topLeftDudePosY + spreadOutY*j);
		    		  u.setMoveSpeed(speed);
		    		  commands = new commandList();
		    		  
		    		  // Searches squareCommands for our shadowDude's start position.
		    		  int n = 0;
		    		  for(; n < squareCommands.size(); n++) {
		    			  moveCommand currentTuple = (moveCommand)squareCommands.get(n);
		    			  if(currentTuple.getX() == i && currentTuple.getY() == j) break;
		    		  }
		    		  
		    		  if(clockwise) n++;
		    		  if(!clockwise) n--;
		    		  
		    		  // Walk him on the path.
		    		  for(int m = 0; m < squareCommands.size()-1; m++) {
		    			  if(n >= squareCommands.size()) n = 0;
		    			  if(n < 0) n = squareCommands.size() - 1;
		    			  moveCommand currentTuple = (moveCommand)squareCommands.get(n);
		    			  commands.add(new moveCommand(topLeftDudePosX + currentTuple.getX()*spreadOutX,topLeftDudePosY + currentTuple.getY()*spreadOutY));
		    			  if(clockwise) n++;
		    			  if(!clockwise) n--;
		    		  }
		    		  
		    		  commands.add(new moveCommand(u.getIntX(),u.getIntY()));
		    		  u.repeatCommands(commands);
		    	  }
		      }
			}
	}
	
	public void makeShadowSquarePosMissing(int topLeftDudePosX, int topLeftDudePosY, int spreadX, int spreadY, float speed, boolean clockwise, int missingPosX, int missingPosY) {
		
		commandList squareCommands = new commandList();
		squareCommands.add(new moveCommand(0,0));
		squareCommands.add(new moveCommand(1,0));
		squareCommands.add(new moveCommand(2,0));
		squareCommands.add(new moveCommand(2,1));
		squareCommands.add(new moveCommand(2,2));
		squareCommands.add(new moveCommand(1,2));
		squareCommands.add(new moveCommand(0,2));
		squareCommands.add(new moveCommand(0,1));
		
		for(int i = 0; i < 3; i++) {
		      for(int j = 0; j < 3; j++) {
		       
		    	  if(i == 1 && j == 1) {
		    		  // Do nothing.
		    	  }
		    	  else if(i == missingPosX && j==missingPosY) {
		    		  // Do nothing.
		    	  }
		    	  else {
		    		  
		    		  // How far they are spread
		    		  int spreadOutX = spreadX;
		    		  int spreadOutY = spreadY;
		    		    
		    		  // Spawn the shadow dude
		    		  u = new shadowDude(topLeftDudePosX + i*spreadOutX,topLeftDudePosY + spreadOutY*j);
		    		  u.setMoveSpeed(speed);
		    		  commands = new commandList();
		    		  
		    		  // Searches squareCommands for our shadowDude's start position.
		    		  int n = 0;
		    		  for(; n < squareCommands.size(); n++) {
		    			  moveCommand currentTuple = (moveCommand)squareCommands.get(n);
		    			  if(currentTuple.getX() == i && currentTuple.getY() == j) break;
		    		  }
		    		  
		    		  if(clockwise) n++;
		    		  if(!clockwise) n--;
		    		  
		    		  // Walk him on the path.
		    		  for(int m = 0; m < squareCommands.size()-1; m++) {
		    			  if(n >= squareCommands.size()) n = 0;
		    			  if(n < 0) n = squareCommands.size() - 1;
		    			  moveCommand currentTuple = (moveCommand)squareCommands.get(n);
		    			  commands.add(new moveCommand(topLeftDudePosX + currentTuple.getX()*spreadOutX,topLeftDudePosY + currentTuple.getY()*spreadOutY));
		    			  if(clockwise) n++;
		    			  if(!clockwise) n--;
		    		  }
		    		  
		    		  commands.add(new moveCommand(u.getIntX(),u.getIntY()));
		    		  u.repeatCommands(commands);
		    	  }
		      }
			}
	}
	
	
	public void shadowDudePatrolPath(int shadowDudeX, int shadowDudeY, commandList commands2, float moveSpeed) {
		u = new shadowDude(shadowDudeX,shadowDudeY);
		u.repeatCommands(commands2);
		u.setMoveSpeed(moveSpeed);
	}
	
	public static void shadowDudePatrol(int shadowDudeX, int shadowDudeY, int patrolToX, int patrolToY, float moveSpeed) {
		u = new shadowDude(shadowDudeX,shadowDudeY);
		commands = new commandList();
		commands.add(new moveCommand(patrolToX, patrolToY));
		commands.add(new moveCommand(shadowDudeX, shadowDudeY));
		u.repeatCommands(commands);
		u.setMoveSpeed(moveSpeed);
	}
	
	public static void lightDudePatrol(int lightDudeX, int lightDudeY, int patrolToX, int patrolToY, float moveSpeed) {
		u = new lightDude (lightDudeX,lightDudeY);
		commands = new commandList();
		commands.add(new moveCommand(patrolToX, patrolToY));
		commands.add(new moveCommand(lightDudeX, lightDudeY));
		u.repeatCommands(commands);
		u.setMoveSpeed(moveSpeed);
	}
	
	//////////////////////
	// INDIVIDUAL AREAS //
	//////////////////////
	public void createTerrain() {
		
	}
	
	public void createNonEditorChunks() {
		
		/////////////////////////
		//////// ENTRANCE ///////
		/////////////////////////
		
		// Entrance
		stairsUp tombZoneEnterance = new stairsUp(30,-8,sheepFarm.getZone(),-3442, -5682,"Down");
		tombZoneEnterance.setZ(BACKGROUND_Z);
		
		// Secret chest stairs up to well.
		stairsUp secretStairs = new stairsUp(11615,2314,farmTomb.getZone(),11629,1670,"Right");
		secretStairs.setZ(BACKGROUND_Z);
		
		// Secret stairs to start
		stairsUp secretStairs2 = new stairsUp(12052,2314,farmTomb.getZone(),70,-6,"Right");
		secretStairs.setZ(BACKGROUND_Z);
	}
	
	// Spawn a rectangle of shadow dudes
	public static ArrayList<shadowDude> createRectangleOfShadows(int x1, int y1, int x2, int y2, boolean eyeless) {
		
		ArrayList<shadowDude> retDudes = new ArrayList<shadowDude>();
		for(int i = x1; i < x2; i += shadowDude.getDefaultWidth()) {
			for(int j = y1; j < y2; j += shadowDude.getDefaultHeight()) {
				u = new shadowDude(i,j);
				((shadowDude)u).setEyeless(true);
				retDudes.add((shadowDude) u);
			}
		}
		return retDudes;
	}
	
/////////////////////////////
//////SHADOW ELEVATOR //////
///////////////////////////
	
	// Shadow bossFight
	public static ArrayList<shadowDude> shadowBossFight;
	
	// Give bossFight eyes
	public static void giveBossFightEyes() {
		if(shadowBossFight!=null) {
			for(int i = 0; i < shadowBossFight.size(); i++) {
				shadowBossFight.get(i).setEyeless(false);
				shadowBossFight.get(i).setIgnoreIllumination(true);
				shadowBossFight.get(i).setMoveSpeed(0.70f);
			}
		}
	}
	
	// Move bossFight up
	public static void startBossFight() {
	
		// Play frantic music.
		music m = music.startMusic(zoneMusicFrantic);
		if(m!=null) m.stopOnDeath = true;
		
		bossFight.startFight();
	}
	
	static wolfless bossFight;
	
	// Create shadow dude bossFight
	public static void createShadowBossFightAroundPlayer() {
		
		if(!bossFightLoaded) {
			bossFightLoaded = true;
			bossFight = new wolfless();
		}
	}
	
	// Deal with the first well we encounters.
	public void dealWithRegionStuff() {
		player currPlayer = player.getPlayer();
		if(currPlayer != null && currPlayer.isWithin(13232,1000,13232+1500,1800) && shadowBossFightStarted!=null && !shadowBossFightStarted.isCompleted()) {
			shadowBossFightStarted.setCompleted(true);
			shadowBossFightFirstTime = true;
		}
		if(currPlayer != null && currPlayer.isWithin(7040,1551,7101,1798)) {
			if(tombMiddleCinematic==null) startFernandoRodriguezInteraction();
		}
	}
	
	// The tomb cinematic in the middle
	farmTombCinematic tombMiddleCinematic;
	
	public void startFernandoRodriguezInteraction() {
		
		// Start cinematic in the middle of the tomb.
		tombMiddleCinematic = new farmTombCinematic();
		tombMiddleCinematic.start();
	}
	
	// Deal with shadow bossFight stuff
	public void dealWithShadowBossFightStuff() {
		
		// It's game time, bro!! turner!! wooo!
		if(shadowBossFightStarted!=null && shadowBossFightStarted.isCompleted()) {

			if(!shadowBossFightInitiated && zoneLoaded) {
				
				// If it's the first time.
				if(shadowBossFightFirstTime) {
					music.currMusic.fadeOut(5f);
					shadowBossFightFirstTime = false;
					wolflessFightCinematic w = new wolflessFightCinematic();
					w.start();
					shadowBossFightInitiated = true;
				}
				else {
					wolflessFightCinematic w = new wolflessFightCinematic();
					w.startBossImmediately = true;
					w.start();
					
					shadowBossFightInitiated = true;
				}
			}
		}
		
	}
	
	// Do zone specific tasks that aren't monitored by
	// zone specific units. 
	@Override
	public void update() {
		dealWithRegionStuff();
		dealWithShadowBossFightStuff();
	}

	// Get the player location in the zone.
	public intTuple getDefaultLocation() {
		return DEFAULT_SPAWN_TUPLE;
	}

	/////////////////////////
	// Getters and setters //
	/////////////////////////
	public static zone getZone() {
		return zoneReference;
	}

	public static void setZone(zone z) {
		zoneReference = z;
	}
	
	public String getMode() {
		return DEFAULT_ZONE_MODE;
	}
	
}