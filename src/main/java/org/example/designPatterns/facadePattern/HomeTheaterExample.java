package org.example.designPatterns.facadePattern;

/**
 * Home Theater Example - Facade Pattern Implementation
 * 
 * This example demonstrates the Facade Pattern by providing a unified
 * interface to control a complex home theater system with multiple
 * devices like DVD player, amplifier, projector, lights, and screen.
 */

// Subsystem components
class DVDPlayer {
    private String movie;
    private boolean playing;
    
    public void on() {
        System.out.println("DVD Player: Powering on");
    }
    
    public void off() {
        System.out.println("DVD Player: Powering off");
        playing = false;
    }
    
    public void play(String movie) {
        this.movie = movie;
        this.playing = true;
        System.out.println("DVD Player: Playing '" + movie + "'");
    }
    
    public void pause() {
        if (playing) {
            System.out.println("DVD Player: Pausing '" + movie + "'");
        }
    }
    
    public void stop() {
        if (playing) {
            System.out.println("DVD Player: Stopping '" + movie + "'");
            playing = false;
        }
    }
    
    public void eject() {
        stop();
        System.out.println("DVD Player: Ejecting disc");
        movie = null;
    }
}

class Amplifier {
    private int volume;
    private DVDPlayer dvdPlayer;
    private boolean on;
    
    public void on() {
        on = true;
        System.out.println("Amplifier: Powering on");
    }
    
    public void off() {
        on = false;
        System.out.println("Amplifier: Powering off");
    }
    
    public void setDvd(DVDPlayer dvdPlayer) {
        this.dvdPlayer = dvdPlayer;
        System.out.println("Amplifier: Setting DVD player as input source");
    }
    
    public void setSurroundSound() {
        System.out.println("Amplifier: Setting surround sound mode (5.1)");
    }
    
    public void setStereoSound() {
        System.out.println("Amplifier: Setting stereo sound mode (2.0)");
    }
    
    public void setVolume(int volume) {
        this.volume = Math.max(0, Math.min(100, volume));
        System.out.println("Amplifier: Setting volume to " + this.volume);
    }
    
    public int getVolume() {
        return volume;
    }
}

class Tuner {
    private String station;
    
    public void on() {
        System.out.println("Tuner: Powering on");
    }
    
    public void off() {
        System.out.println("Tuner: Powering off");
    }
    
    public void setFrequency(String station) {
        this.station = station;
        System.out.println("Tuner: Setting frequency to " + station);
    }
    
    public void setAM() {
        System.out.println("Tuner: Switching to AM mode");
    }
    
    public void setFM() {
        System.out.println("Tuner: Switching to FM mode");
    }
}

class Projector {
    private boolean on;
    private String input;
    
    public void on() {
        on = true;
        System.out.println("Projector: Powering on");
    }
    
    public void off() {
        on = false;
        System.out.println("Projector: Powering off");
    }
    
    public void setInput(String input) {
        this.input = input;
        System.out.println("Projector: Setting input to " + input);
    }
    
    public void wideScreenMode() {
        System.out.println("Projector: Setting wide screen mode (16:9)");
    }
    
    public void standardMode() {
        System.out.println("Projector: Setting standard mode (4:3)");
    }
}

class TheaterLights {
    private int brightness; // 0-100
    
    public void on() {
        brightness = 100;
        System.out.println("Theater Lights: Turning on (brightness: " + brightness + "%)");
    }
    
    public void off() {
        brightness = 0;
        System.out.println("Theater Lights: Turning off");
    }
    
    public void dim(int brightness) {
        this.brightness = Math.max(0, Math.min(100, brightness));
        System.out.println("Theater Lights: Dimming to " + this.brightness + "%");
    }
    
    public int getBrightness() {
        return brightness;
    }
}

class Screen {
    public void up() {
        System.out.println("Screen: Raising screen");
    }
    
    public void down() {
        System.out.println("Screen: Lowering screen");
    }
}

class PopcornPopper {
    private boolean popping;
    
    public void on() {
        System.out.println("Popcorn Popper: Powering on");
    }
    
    public void off() {
        System.out.println("Popcorn Popper: Powering off");
        popping = false;
    }
    
    public void pop() {
        popping = true;
        System.out.println("Popcorn Popper: Popping corn!");
    }
    
    public boolean isPopping() {
        return popping;
    }
}

// Facade - Simplifies home theater operations
class HomeTheaterFacade {
    private Amplifier amplifier;
    private Tuner tuner;
    private DVDPlayer dvdPlayer;
    private Projector projector;
    private TheaterLights lights;
    private Screen screen;
    private PopcornPopper popper;
    
    public HomeTheaterFacade(Amplifier amplifier, Tuner tuner, DVDPlayer dvdPlayer,
                           Projector projector, TheaterLights lights, Screen screen,
                           PopcornPopper popper) {
        this.amplifier = amplifier;
        this.tuner = tuner;
        this.dvdPlayer = dvdPlayer;
        this.projector = projector;
        this.lights = lights;
        this.screen = screen;
        this.popper = popper;
    }
    
    /**
     * Simplified movie watching experience
     * Sets up all equipment for optimal movie viewing
     */
    public void watchMovie(String movie) {
        System.out.println("=== STARTING MOVIE MODE ===");
        System.out.println("Get ready to watch a movie...\n");
        
        // Step 1: Prepare the environment
        System.out.println("--- Setting up environment ---");
        lights.dim(10); // Dim lights for movie experience
        screen.down(); // Lower the projection screen
        
        // Step 2: Setup audio/video equipment
        System.out.println("\n--- Setting up audio/video ---");
        projector.on();
        projector.wideScreenMode();
        projector.setInput("DVD");
        
        amplifier.on();
        amplifier.setDvd(dvdPlayer);
        amplifier.setSurroundSound();
        amplifier.setVolume(25); // Comfortable movie volume
        
        // Step 3: Start the movie
        System.out.println("\n--- Starting playback ---");
        dvdPlayer.on();
        dvdPlayer.play(movie);
        
        System.out.println("\nMovie '" + movie + "' is now playing! Enjoy!");
    }
    
    /**
     * End movie mode and restore normal environment
     */
    public void endMovie() {
        System.out.println("\n=== ENDING MOVIE MODE ===");
        
        System.out.println("--- Stopping playback ---");
        dvdPlayer.stop();
        dvdPlayer.eject();
        dvdPlayer.off();
        
        System.out.println("\n--- Shutting down equipment ---");
        amplifier.off();
        projector.off();
        
        System.out.println("\n--- Restoring environment ---");
        lights.on(); // Turn lights back on
        screen.up(); // Raise the screen
        
        System.out.println("Home theater system shut down. Hope you enjoyed the movie!");
    }
    
    /**
     * Radio listening mode
     */
    public void listenToRadio(String station) {
        System.out.println("=== STARTING RADIO MODE ===");
        
        System.out.println("--- Setting up for radio ---");
        lights.on(); // Full lights for radio mode
        screen.up(); // Screen not needed for radio
        
        amplifier.on();
        amplifier.setStereoSound(); // Stereo is fine for radio
        amplifier.setVolume(15); // Lower volume for background music
        
        tuner.on();
        tuner.setFM();
        tuner.setFrequency(station);
        
        System.out.println("Now playing radio station " + station);
    }
    
    /**
     * Stop radio mode
     */
    public void stopRadio() {
        System.out.println("\n=== STOPPING RADIO MODE ===");
        
        tuner.off();
        amplifier.off();
        
        System.out.println("Radio stopped.");
    }
    
    /**
     * Make popcorn for movie time
     */
    public void makePopcorn() {
        System.out.println("=== MAKING POPCORN ===");
        
        popper.on();
        popper.pop();
        
        System.out.println("Popcorn is ready! Perfect for movie time!");
    }
    
    /**
     * Party mode - bright lights and loud music
     */
    public void partyMode() {
        System.out.println("=== STARTING PARTY MODE ===");
        
        // Bright lights for a party
        lights.on();
        screen.up(); // Screen up and out of the way
        
        // Loud music setup
        amplifier.on();
        amplifier.setStereoSound();
        amplifier.setVolume(50); // Party volume!
        
        tuner.on();
        tuner.setFM();
        tuner.setFrequency("101.5 FM"); // Popular music station
        
        System.out.println("Party mode activated! Let's dance!");
    }
    
    /**
     * Sleep mode - turn everything off with minimal lighting
     */
    public void sleepMode() {
        System.out.println("=== ACTIVATING SLEEP MODE ===");
        
        // Turn everything off
        dvdPlayer.off();
        amplifier.off();
        tuner.off();
        projector.off();
        popper.off();
        
        // Very dim lights for night time
        lights.dim(2);
        screen.up();
        
        System.out.println("Sleep mode activated. Good night!");
    }
    
    /**
     * Emergency shutdown for safety
     */
    public void emergencyShutdown() {
        System.out.println("EMERGENCY SHUTDOWN - Turning off all equipment");
        
        dvdPlayer.off();
        amplifier.off();
        tuner.off();
        projector.off();
        popper.off();
        lights.off();
        
        System.out.println("All equipment safely shut down.");
    }
    
    /**
     * Get current system status
     */
    public void getSystemStatus() {
        System.out.println("=== SYSTEM STATUS ===");
        System.out.println("Amplifier volume: " + amplifier.getVolume());
        System.out.println("Light brightness: " + lights.getBrightness() + "%");
        System.out.println("Popcorn maker: " + (popper.isPopping() ? "Popping" : "Idle"));
        System.out.println("===================");
    }
}

/**
 * Client code demonstration
 * Shows how the facade pattern simplifies home theater operations
 */
public class HomeTheaterExample {
    public static void main(String[] args) {
        System.out.println("Home Theater Facade Pattern Example");
        System.out.println("===================================\n");
        
        // Create all subsystem components
        Amplifier amplifier = new Amplifier();
        Tuner tuner = new Tuner();
        DVDPlayer dvdPlayer = new DVDPlayer();
        Projector projector = new Projector();
        TheaterLights lights = new TheaterLights();
        Screen screen = new Screen();
        PopcornPopper popper = new PopcornPopper();
        
        // Create facade
        HomeTheaterFacade homeTheater = new HomeTheaterFacade(
            amplifier, tuner, dvdPlayer, projector, lights, screen, popper);
        
        // Without facade, user would need to:
        // 1. Know how to operate each component individually
        // 2. Remember the correct sequence of operations
        // 3. Coordinate settings between multiple components
        // 4. Handle error conditions and cleanup manually
        
        // With facade, user gets simple, high-level operations:
        
        // Make some popcorn first
        homeTheater.makePopcorn();
        
        System.out.println("\n" + "=".repeat(50) + "\n");
        
        // Watch a movie
        homeTheater.watchMovie("The Avengers");
        
        System.out.println("\n" + "=".repeat(50) + "\n");
        
        // Check system status during movie
        homeTheater.getSystemStatus();
        
        System.out.println("\n" + "=".repeat(50) + "\n");
        
        // End movie
        homeTheater.endMovie();
        
        System.out.println("\n" + "=".repeat(50) + "\n");
        
        // Switch to radio
        homeTheater.listenToRadio("105.7 FM");
        
        System.out.println("\n" + "=".repeat(50) + "\n");
        
        // Switch to party mode
        homeTheater.partyMode();
        
        System.out.println("\n" + "=".repeat(50) + "\n");
        
        // Finally, activate sleep mode
        homeTheater.sleepMode();
        
        System.out.println("\nExample completed!");
    }
}