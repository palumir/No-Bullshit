package sounds;

import java.util.ArrayList;

import utilities.time;

public class music extends sound {
	
	// Defaults.
	private static float DEFAULT_MUSIC_VOLUME = 0.9f;
	
	// List of all music.
	private static ArrayList<music> allMusic = new ArrayList<music>();
	
	// Last song and volume
	private static music lastMusic;
	
	// Current music.
	public static music currMusic;

	// Music constructor.
	public music(String soundFile) {
		super(soundFile);
		musicConstruct();
	}
	
	// Copy constructor.
	public music(music music) {
		super(music);
		musicConstruct();
	}
	
	// Called in both constructors
	public void musicConstruct() {
		
		// Set volume.
		this.setVolume(DEFAULT_MUSIC_VOLUME);
		
		// Last music.
		lastMusic = currMusic;
		
		// Replace current music.
		currMusic = this;
		
		// Loop all music.
		this.setLoop(true);
		
		// Start music.
		this.start();
		
		// Add to all music.
		allMusic.add(this);
	}
	
	// Fadeout
	public void fadeOut(float f) {
		if(currMusic.getFileName().equals(this.getFileName()))  currMusic = null;
    	setLoop(false);
    	setFadeOver(f);
    	setFadeOutStart(time.getTime());
	}
	
	// Factory constructor
	public static music startMusic(String s) {
		if(currMusic != null && currMusic.getFileName().equals(s)) {
			// Don't play the same music twice, dumby!
			return null;
		}
		else {
			// Play the music
			music m = new music(s);
			return m;
		}
	}
	
	// Init.
	public static void endAll() {
		if(allMusic != null) {
			for(int i = 0; i < allMusic.size(); i++) {
				allMusic.get(i).stopRequested = true;
			}
			currMusic = null;
		}
	}
	
	// Player died
	public static void playerDied() {
		if(currMusic != null && currMusic.stopOnDeath) {
			currMusic.stopRequested = true;
			currMusic = null;
		}
	}
	
	// Play last song played before current.
	public static void playLast() {
		if(lastMusic != null) {
			music s = new music(lastMusic);
		}
	}
}