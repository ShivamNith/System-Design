package org.example.designPatterns.adapterPattern;

/**
 * Demonstration of Adapter Pattern with Media Player System
 * 
 * This example shows how to use the Adapter pattern to integrate
 * different media player libraries with incompatible interfaces
 * into a unified system.
 */

// Target interface - what the client expects
interface MediaPlayer {
    void play(String audioType, String fileName);
}

// Adaptee interfaces - external libraries with different interfaces
interface AdvancedMediaPlayer {
    void playVlc(String fileName);
    void playMp4(String fileName);
}

// Concrete Adaptee implementations - third-party libraries
class VlcPlayer implements AdvancedMediaPlayer {
    @Override
    public void playVlc(String fileName) {
        System.out.println("Playing VLC file. Name: " + fileName);
    }

    @Override
    public void playMp4(String fileName) {
        // VLC player doesn't support MP4 in this implementation
        System.out.println("VLC player: MP4 format not supported");
    }
}

class Mp4Player implements AdvancedMediaPlayer {
    @Override
    public void playVlc(String fileName) {
        // MP4 player doesn't support VLC in this implementation
        System.out.println("MP4 player: VLC format not supported");
    }

    @Override
    public void playMp4(String fileName) {
        System.out.println("Playing MP4 file. Name: " + fileName);
    }
}

// Additional media players to demonstrate extensibility
interface StreamingPlayer {
    void startStream(String url, String quality);
    void stopStream();
}

class YouTubePlayer implements StreamingPlayer {
    @Override
    public void startStream(String url, String quality) {
        System.out.println("Starting YouTube stream: " + url + " at " + quality + " quality");
    }

    @Override
    public void stopStream() {
        System.out.println("Stopping YouTube stream");
    }
}

class SpotifyPlayer {
    public void playTrack(String trackId, boolean premiumQuality) {
        System.out.println("Playing Spotify track: " + trackId + 
                          (premiumQuality ? " (Premium Quality)" : " (Standard Quality)"));
    }
    
    public void pauseTrack() {
        System.out.println("Pausing Spotify track");
    }
}

// Adapter - converts different media player interfaces to common interface
class MediaAdapter implements MediaPlayer {
    private AdvancedMediaPlayer advancedMusicPlayer;
    private StreamingPlayer streamingPlayer;
    private SpotifyPlayer spotifyPlayer;
    private String currentType;
    
    public MediaAdapter(String audioType) {
        this.currentType = audioType.toLowerCase();
        
        switch (this.currentType) {
            case "vlc":
                advancedMusicPlayer = new VlcPlayer();
                break;
            case "mp4":
                advancedMusicPlayer = new Mp4Player();
                break;
            case "youtube":
                streamingPlayer = new YouTubePlayer();
                break;
            case "spotify":
                spotifyPlayer = new SpotifyPlayer();
                break;
            default:
                throw new IllegalArgumentException("Unsupported audio type: " + audioType);
        }
    }
    
    @Override
    public void play(String audioType, String fileName) {
        String type = audioType.toLowerCase();
        
        switch (type) {
            case "vlc":
                advancedMusicPlayer.playVlc(fileName);
                break;
            case "mp4":
                advancedMusicPlayer.playMp4(fileName);
                break;
            case "youtube":
                // Adapt the streaming interface to the play interface
                streamingPlayer.startStream(fileName, "1080p");
                break;
            case "spotify":
                // Adapt Spotify's different interface
                spotifyPlayer.playTrack(fileName, true);
                break;
            default:
                System.out.println("Invalid media. " + audioType + " format not supported");
        }
    }
    
    // Additional method to stop streaming
    public void stop() {
        if ("youtube".equals(currentType) && streamingPlayer != null) {
            streamingPlayer.stopStream();
        } else if ("spotify".equals(currentType) && spotifyPlayer != null) {
            spotifyPlayer.pauseTrack();
        }
    }
}

// Enhanced adapter with caching and error handling
class EnhancedMediaAdapter implements MediaPlayer {
    private static final String[] SUPPORTED_FORMATS = {"vlc", "mp4", "youtube", "spotify"};
    
    private AdvancedMediaPlayer advancedMusicPlayer;
    private StreamingPlayer streamingPlayer;
    private SpotifyPlayer spotifyPlayer;
    private String currentType;
    private boolean isInitialized = false;
    
    public EnhancedMediaAdapter() {
        // Lazy initialization - don't create players until needed
    }
    
    @Override
    public void play(String audioType, String fileName) {
        String type = audioType.toLowerCase();
        
        if (!isSupported(type)) {
            System.out.println("Error: " + audioType + " format not supported. Supported formats: " + 
                             String.join(", ", SUPPORTED_FORMATS));
            return;
        }
        
        try {
            initializePlayer(type);
            playMedia(type, fileName);
        } catch (Exception e) {
            System.out.println("Error playing media: " + e.getMessage());
        }
    }
    
    private boolean isSupported(String audioType) {
        for (String format : SUPPORTED_FORMATS) {
            if (format.equals(audioType)) {
                return true;
            }
        }
        return false;
    }
    
    private void initializePlayer(String audioType) {
        if (currentType != null && currentType.equals(audioType) && isInitialized) {
            return; // Already initialized for this type
        }
        
        // Clean up previous player if switching types
        cleanup();
        
        this.currentType = audioType;
        
        switch (audioType) {
            case "vlc":
                advancedMusicPlayer = new VlcPlayer();
                break;
            case "mp4":
                advancedMusicPlayer = new Mp4Player();
                break;
            case "youtube":
                streamingPlayer = new YouTubePlayer();
                break;
            case "spotify":
                spotifyPlayer = new SpotifyPlayer();
                break;
        }
        
        isInitialized = true;
        System.out.println("Initialized " + audioType.toUpperCase() + " player");
    }
    
    private void playMedia(String audioType, String fileName) {
        switch (audioType) {
            case "vlc":
                advancedMusicPlayer.playVlc(fileName);
                break;
            case "mp4":
                advancedMusicPlayer.playMp4(fileName);
                break;
            case "youtube":
                streamingPlayer.startStream(fileName, "1080p");
                break;
            case "spotify":
                spotifyPlayer.playTrack(fileName, true);
                break;
        }
    }
    
    private void cleanup() {
        if (streamingPlayer != null) {
            streamingPlayer.stopStream();
        }
        if (spotifyPlayer != null) {
            spotifyPlayer.pauseTrack();
        }
        
        advancedMusicPlayer = null;
        streamingPlayer = null;
        spotifyPlayer = null;
        isInitialized = false;
    }
    
    public void stop() {
        cleanup();
        System.out.println("Stopped all media players");
    }
}

// Client class - uses the Target interface
class AudioPlayer implements MediaPlayer {
    private MediaAdapter mediaAdapter;
    
    @Override
    public void play(String audioType, String fileName) {
        String type = audioType.toLowerCase();
        
        // Built-in support for mp3 format
        if ("mp3".equals(type)) {
            System.out.println("Playing MP3 file (built-in): " + fileName);
        }
        // Use adapter for other formats
        else {
            try {
                mediaAdapter = new MediaAdapter(audioType);
                mediaAdapter.play(audioType, fileName);
            } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }
    
    public void stopMedia() {
        if (mediaAdapter != null) {
            mediaAdapter.stop();
        }
    }
}

// Enhanced audio player with better adapter management
class EnhancedAudioPlayer implements MediaPlayer {
    private EnhancedMediaAdapter mediaAdapter;
    
    public EnhancedAudioPlayer() {
        this.mediaAdapter = new EnhancedMediaAdapter();
    }
    
    @Override
    public void play(String audioType, String fileName) {
        String type = audioType.toLowerCase();
        
        if (fileName == null || fileName.trim().isEmpty()) {
            System.out.println("Error: File name cannot be empty");
            return;
        }
        
        System.out.println("\n=== Playing Media ===");
        System.out.println("Format: " + audioType.toUpperCase());
        System.out.println("File: " + fileName);
        System.out.println("---");
        
        // Built-in support for mp3 format
        if ("mp3".equals(type)) {
            System.out.println("Playing MP3 file (built-in): " + fileName);
        } else {
            mediaAdapter.play(audioType, fileName);
        }
    }
    
    public void stopAll() {
        mediaAdapter.stop();
    }
    
    public void playPlaylist(String[][] playlist) {
        System.out.println("\n=== Playing Playlist ===");
        for (String[] track : playlist) {
            if (track.length >= 2) {
                play(track[0], track[1]);
                try {
                    Thread.sleep(1000); // Simulate playing time
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
        System.out.println("=== Playlist Complete ===\n");
    }
}

// Factory for creating media adapters
class MediaAdapterFactory {
    public static MediaPlayer createMediaPlayer(String playerType) {
        switch (playerType.toLowerCase()) {
            case "basic":
                return new AudioPlayer();
            case "enhanced":
                return new EnhancedAudioPlayer();
            default:
                throw new IllegalArgumentException("Unknown player type: " + playerType);
        }
    }
    
    public static boolean isFormatSupported(String format) {
        String[] supportedFormats = {"mp3", "vlc", "mp4", "youtube", "spotify"};
        for (String supportedFormat : supportedFormats) {
            if (supportedFormat.equalsIgnoreCase(format)) {
                return true;
            }
        }
        return false;
    }
}

// Demonstration class
public class MediaPlayerAdapterExample {
    
    public static void main(String[] args) {
        System.out.println("=== Adapter Pattern Demonstration - Media Player System ===\n");
        
        // Example 1: Basic media player usage
        System.out.println("1. Basic Media Player:");
        AudioPlayer audioPlayer = new AudioPlayer();
        
        audioPlayer.play("mp3", "beyond_the_horizon.mp3");
        audioPlayer.play("mp4", "alone.mp4");
        audioPlayer.play("vlc", "far_far_away.vlc");
        audioPlayer.play("avi", "mind_me.avi"); // Unsupported format
        
        audioPlayer.stopMedia();
        System.out.println();
        
        // Example 2: Enhanced media player with better error handling
        System.out.println("2. Enhanced Media Player:");
        EnhancedAudioPlayer enhancedPlayer = new EnhancedAudioPlayer();
        
        enhancedPlayer.play("mp3", "classic_song.mp3");
        enhancedPlayer.play("youtube", "https://youtube.com/watch?v=dQw4w9WgXcQ");
        enhancedPlayer.play("spotify", "track:4iV5W9uYEdYUVa79Axb7Rh");
        enhancedPlayer.play("vlc", "movie_trailer.vlc");
        enhancedPlayer.play("unsupported", "test.xyz"); // Unsupported format
        enhancedPlayer.play("mp4", ""); // Empty filename
        
        enhancedPlayer.stopAll();
        System.out.println();
        
        // Example 3: Playing a playlist
        System.out.println("3. Playlist Example:");
        String[][] playlist = {
            {"mp3", "song1.mp3"},
            {"spotify", "track:1A2B3C4D5E6F"},
            {"youtube", "https://youtube.com/watch?v=abc123"},
            {"vlc", "video.vlc"},
            {"mp4", "music_video.mp4"}
        };
        
        enhancedPlayer.playPlaylist(playlist);
        
        // Example 4: Using factory to create players
        System.out.println("4. Using Media Player Factory:");
        MediaPlayer basicPlayer = MediaAdapterFactory.createMediaPlayer("basic");
        MediaPlayer advancedPlayer = MediaAdapterFactory.createMediaPlayer("enhanced");
        
        System.out.println("Basic Player:");
        basicPlayer.play("mp3", "factory_test.mp3");
        basicPlayer.play("vlc", "factory_test.vlc");
        
        System.out.println("\nAdvanced Player:");
        advancedPlayer.play("spotify", "track:factory_test");
        System.out.println();
        
        // Example 5: Format support checking
        System.out.println("5. Format Support Checking:");
        String[] testFormats = {"mp3", "mp4", "vlc", "youtube", "spotify", "wav", "flac"};
        
        for (String format : testFormats) {
            boolean supported = MediaAdapterFactory.isFormatSupported(format);
            System.out.println(format.toUpperCase() + ": " + (supported ? "Supported" : "Not Supported"));
        }
        System.out.println();
        
        // Example 6: Error handling demonstration
        System.out.println("6. Error Handling:");
        try {
            MediaPlayer unknownPlayer = MediaAdapterFactory.createMediaPlayer("unknown");
        } catch (IllegalArgumentException e) {
            System.out.println("Factory error: " + e.getMessage());
        }
        
        // Test adapter with null input
        EnhancedMediaAdapter adapter = new EnhancedMediaAdapter();
        adapter.play("mp4", null);
        System.out.println();
        
        // Example 7: Adapter reuse and switching
        System.out.println("7. Adapter Type Switching:");
        EnhancedMediaAdapter reusableAdapter = new EnhancedMediaAdapter();
        
        System.out.println("Playing different formats with same adapter:");
        reusableAdapter.play("vlc", "test1.vlc");
        reusableAdapter.play("mp4", "test2.mp4");  // Should switch adapters
        reusableAdapter.play("vlc", "test3.vlc");  // Should switch back
        reusableAdapter.play("youtube", "https://youtube.com/test"); // Switch to streaming
        
        reusableAdapter.stop();
        System.out.println();
        
        System.out.println("=== Demonstration Complete ===");
        
        // Clean up
        if (enhancedPlayer instanceof EnhancedAudioPlayer) {
            ((EnhancedAudioPlayer) enhancedPlayer).stopAll();
        }
        if (audioPlayer instanceof AudioPlayer) {
            ((AudioPlayer) audioPlayer).stopMedia();
        }
    }
}