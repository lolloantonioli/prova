package it.unibo.model.Shop;

import java.awt.image.BufferedImage;

public class Skin {
    private final String id;
    private final String name;
    private final int price;
    private final BufferedImage image;
    private boolean unlocked;

    public Skin(String id, String name, int price, BufferedImage image, boolean unlocked) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.image = image;
        this.unlocked = unlocked;
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public int getPrice() { return price; }
    public BufferedImage getImage() { return image; }
    public boolean isUnlocked() { return unlocked; }
    
    // Setter
    public void setUnlocked(boolean unlocked) { this.unlocked = unlocked; }
}