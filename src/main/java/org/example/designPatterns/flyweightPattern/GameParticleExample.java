package org.example.designPatterns.flyweightPattern;

import java.util.*;

/**
 * Game Particle System Example demonstrating Flyweight Pattern
 * 
 * This example shows how particle effects in games can share
 * common properties while maintaining individual positions and velocities.
 */

// Flyweight interface
interface ParticleFlyweight {
    void render(double x, double y, double velocity, String context);
    void update(double x, double y, double velocity, double deltaTime);
}

// Concrete Flyweight - stores intrinsic state (color, size, texture)
class ParticleType implements ParticleFlyweight {
    private final String color;
    private final double size;
    private final String texture;
    private final String effect;
    
    public ParticleType(String color, double size, String texture, String effect) {
        this.color = color;
        this.size = size;
        this.texture = texture;
        this.effect = effect;
    }
    
    @Override
    public void render(double x, double y, double velocity, String context) {
        System.out.println(String.format(
            "Rendering %s particle [%s] at (%.1f, %.1f) with velocity %.1f in %s",
            color, effect, x, y, velocity, context
        ));
    }
    
    @Override
    public void update(double x, double y, double velocity, double deltaTime) {
        // Simulate particle physics update
        double newX = x + velocity * deltaTime * Math.cos(Math.random() * 2 * Math.PI);
        double newY = y + velocity * deltaTime * Math.sin(Math.random() * 2 * Math.PI);
        // In real implementation, this would update the particle's position
    }
    
    public String getTypeInfo() {
        return String.format("Type[color=%s, size=%.1f, texture=%s, effect=%s]", 
                           color, size, texture, effect);
    }
}

// Flyweight Factory
class ParticleTypeFactory {
    private static final Map<String, ParticleType> particleTypes = new HashMap<>();
    
    public static ParticleType getParticleType(String color, double size, String texture, String effect) {
        String key = color + "_" + size + "_" + texture + "_" + effect;
        
        if (!particleTypes.containsKey(key)) {
            particleTypes.put(key, new ParticleType(color, size, texture, effect));
            System.out.println("Created new particle type: " + key);
        }
        
        return particleTypes.get(key);
    }
    
    public static void printTypeStatistics() {
        System.out.println("\n=== Particle Type Statistics ===");
        System.out.println("Total particle types created: " + particleTypes.size());
        for (Map.Entry<String, ParticleType> entry : particleTypes.entrySet()) {
            System.out.println(entry.getKey() + " -> " + entry.getValue().getTypeInfo());
        }
    }
}

// Context class - stores extrinsic state
class Particle {
    private double x, y;
    private double velocity;
    private double lifeTime;
    private ParticleType type;
    
    public Particle(double x, double y, double velocity, double lifeTime, ParticleType type) {
        this.x = x;
        this.y = y;
        this.velocity = velocity;
        this.lifeTime = lifeTime;
        this.type = type;
    }
    
    public void render(String context) {
        type.render(x, y, velocity, context);
    }
    
    public void update(double deltaTime) {
        type.update(x, y, velocity, deltaTime);
        lifeTime -= deltaTime;
        
        // Update position (simplified physics)
        x += velocity * deltaTime * 0.1;
        y += velocity * deltaTime * 0.1;
        velocity *= 0.99; // Friction
    }
    
    public boolean isAlive() {
        return lifeTime > 0;
    }
    
    public double getX() { return x; }
    public double getY() { return y; }
    public double getVelocity() { return velocity; }
}

// Particle System
class ParticleSystem {
    private List<Particle> particles;
    private String systemName;
    
    public ParticleSystem(String systemName) {
        this.systemName = systemName;
        this.particles = new ArrayList<>();
    }
    
    public void createExplosion(double x, double y, int count) {
        System.out.println("\nCreating explosion at (" + x + ", " + y + ") with " + count + " particles");
        
        for (int i = 0; i < count; i++) {
            double velocity = 50 + Math.random() * 100;
            double lifeTime = 2 + Math.random() * 3;
            
            // Create different types of particles for explosion
            ParticleType type;
            if (i % 3 == 0) {
                type = ParticleTypeFactory.getParticleType("Red", 2.0, "fire", "explosion");
            } else if (i % 3 == 1) {
                type = ParticleTypeFactory.getParticleType("Orange", 1.5, "spark", "explosion");
            } else {
                type = ParticleTypeFactory.getParticleType("Yellow", 1.0, "glow", "explosion");
            }
            
            particles.add(new Particle(x, y, velocity, lifeTime, type));
        }
    }
    
    public void createMagicEffect(double x, double y, int count) {
        System.out.println("\nCreating magic effect at (" + x + ", " + y + ") with " + count + " particles");
        
        for (int i = 0; i < count; i++) {
            double velocity = 20 + Math.random() * 40;
            double lifeTime = 3 + Math.random() * 2;
            
            // Create magical particles
            ParticleType type;
            if (i % 2 == 0) {
                type = ParticleTypeFactory.getParticleType("Blue", 1.5, "magic", "sparkle");
            } else {
                type = ParticleTypeFactory.getParticleType("Purple", 1.2, "magic", "sparkle");
            }
            
            particles.add(new Particle(x, y, velocity, lifeTime, type));
        }
    }
    
    public void update(double deltaTime) {
        // Update all particles
        particles.forEach(particle -> particle.update(deltaTime));
        
        // Remove dead particles
        particles.removeIf(particle -> !particle.isAlive());
    }
    
    public void render() {
        System.out.println("\n=== Rendering " + systemName + " ===");
        for (Particle particle : particles) {
            particle.render(systemName);
        }
        if (particles.isEmpty()) {
            System.out.println("No active particles");
        }
    }
    
    public int getParticleCount() {
        return particles.size();
    }
}

// Client code
public class GameParticleExample {
    public static void main(String[] args) {
        System.out.println("=== Flyweight Pattern: Game Particle System ===\n");
        
        // Create particle systems
        ParticleSystem explosionSystem = new ParticleSystem("Explosion System");
        ParticleSystem magicSystem = new ParticleSystem("Magic System");
        
        // Create explosions
        explosionSystem.createExplosion(100, 50, 15);
        explosionSystem.createExplosion(200, 100, 12);
        
        // Create magic effects
        magicSystem.createMagicEffect(150, 75, 10);
        magicSystem.createMagicEffect(250, 125, 8);
        
        // Render initial state
        explosionSystem.render();
        magicSystem.render();
        
        // Show statistics
        System.out.println("\n=== System Statistics ===");
        System.out.println("Explosion system particles: " + explosionSystem.getParticleCount());
        System.out.println("Magic system particles: " + magicSystem.getParticleCount());
        System.out.println("Total particles: " + 
            (explosionSystem.getParticleCount() + magicSystem.getParticleCount()));
        
        ParticleTypeFactory.printTypeStatistics();
        
        // Simulate game loop
        System.out.println("\n=== Simulating Game Updates ===");
        for (int frame = 1; frame <= 3; frame++) {
            System.out.println("\n--- Frame " + frame + " ---");
            
            double deltaTime = 0.5; // Half second per frame for demo
            explosionSystem.update(deltaTime);
            magicSystem.update(deltaTime);
            
            System.out.println("Active particles: Explosion=" + explosionSystem.getParticleCount() + 
                             ", Magic=" + magicSystem.getParticleCount());
        }
        
        // Create more effects to show flyweight reuse
        System.out.println("\n=== Creating More Effects (Reusing Types) ===");
        explosionSystem.createExplosion(300, 150, 20);
        magicSystem.createMagicEffect(350, 175, 15);
        
        ParticleTypeFactory.printTypeStatistics();
        System.out.println("\nNotice: No new particle types created - existing types were reused!");
        
        System.out.println("\n=== Memory Efficiency Benefits ===");
        System.out.println("Without Flyweight: Each particle stores color, size, texture, effect");
        System.out.println("With Flyweight: Particles share type objects, only store position/velocity");
        System.out.println("For " + (explosionSystem.getParticleCount() + magicSystem.getParticleCount()) + 
                         " particles, only " + ParticleTypeFactory.particleTypes.size() + " type objects needed!");
    }
}