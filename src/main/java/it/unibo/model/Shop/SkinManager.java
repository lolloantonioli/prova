package it.unibo.model.Shop;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.prefs.Preferences;

import javax.imageio.ImageIO;

public class SkinManager {
    private final List<Skin> availableSkins = new ArrayList<>();
    private final Map<String, Skin> skinMap = new HashMap<>();
    private Skin currentSkin;
    private final Preferences prefs;
    
    public SkinManager() {
        prefs = Preferences.userNodeForPackage(SkinManager.class);
        loadSkins();
        loadUnlockedStatus();
    }
    
    private void loadSkins() {
        try {
            // Carica la skin predefinita (sempre sbloccata)
            BufferedImage defaultImage = ImageIO.read(new File("path/to/default_skin.png"));
            Skin defaultSkin = new Skin("default", "Default", 0, defaultImage, true);
            availableSkins.add(defaultSkin);
            skinMap.put(defaultSkin.getId(), defaultSkin);
            currentSkin = defaultSkin;
            
            // Carica altre skin
            BufferedImage frogImage = ImageIO.read(new File("path/to/frog_skin.png"));
            Skin frogSkin = new Skin("frog", "Frog", 100, frogImage, false);
            availableSkins.add(frogSkin);
            skinMap.put(frogSkin.getId(), frogSkin);
            
            // Aggiungi altre skin qui...
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void loadUnlockedStatus() {
        for (Skin skin : availableSkins) {
            if (!skin.getId().equals("default")) { // La skin predefinita è sempre sbloccata
                boolean unlocked = prefs.getBoolean("skin_" + skin.getId(), false);
                skin.setUnlocked(unlocked);
            }
        }
        
        // Carica la skin corrente
        String currentSkinId = prefs.get("current_skin", "default");
        currentSkin = skinMap.getOrDefault(currentSkinId, skinMap.get("default"));
    }
    
    public boolean buySkin(String skinId, int coins) {
        Skin skin = skinMap.get(skinId);
        if (skin == null || skin.isUnlocked()) {
            return false;
        }
        
        if (coins >= skin.getPrice()) {
            skin.setUnlocked(true);
            prefs.putBoolean("skin_" + skin.getId(), true);
            return true;
        }
        
        return false;
    }
    
    public void selectSkin(String skinId) {
        Skin skin = skinMap.get(skinId);
        if (skin != null && skin.isUnlocked()) {
            currentSkin = skin;
            prefs.put("current_skin", skinId);
        }
    }
    
    public List<Skin> getAvailableSkins() {
        return new ArrayList<>(availableSkins);
    }
    
    public Skin getCurrentSkin() {
        return currentSkin;
    }
}