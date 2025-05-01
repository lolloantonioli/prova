package it.unibo.view.Shop;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.JPanel;

import it.unibo.model.Shop.Skin;
import it.unibo.model.Shop.SkinManager;
import it.unibo.view.ScaleManager;

public class ShopView extends JPanel {
    private final SkinManager skinManager;
    private final ScaleManager scaleManager;
    private int coins;
    private Runnable onBackToMainMenu;
    
    public ShopView(SkinManager skinManager, ScaleManager scaleManager) {
        this.skinManager = skinManager;
        this.scaleManager = scaleManager;
        this.coins = 0;
        
        setPreferredSize(new Dimension(scaleManager.getCurrentWidth(), scaleManager.getCurrentHeight()));
        setBackground(Color.BLACK);
        
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleMouseClick(e.getX(), e.getY());
            }
        });
    }
    
    public void setCoins(int coins) {
        this.coins = coins;
    }
    
    public void setOnBackToMainMenu(Runnable onBackToMainMenu) {
        this.onBackToMainMenu = onBackToMainMenu;
    }
    
    private void handleMouseClick(int x, int y) {
        int unscaledX = scaleManager.unscaleX(x);
        int unscaledY = scaleManager.unscaleY(y);
        
        // Controlla se è stato cliccato il pulsante "Back"
        if (unscaledY >= 500 && unscaledY <= 550) {
            if (onBackToMainMenu != null) {
                onBackToMainMenu.run();
            }
            return;
        }
        
        List<Skin> skins = skinManager.getAvailableSkins();
        int skinWidth = 150;
        int spacing = 20;
        int totalWidth = skins.size() * (skinWidth + spacing) - spacing;
        int startX = (800 - totalWidth) / 2;
        
        for (int i = 0; i < skins.size(); i++) {
            Skin skin = skins.get(i);
            int skinX = startX + i * (skinWidth + spacing);
            
            if (unscaledX >= skinX && unscaledX <= skinX + skinWidth && 
                unscaledY >= 200 && unscaledY <= 350) {
                
                if (skin.isUnlocked()) {
                    skinManager.selectSkin(skin.getId());
                } else if (coins >= skin.getPrice()) {
                    if (skinManager.buySkin(skin.getId(), coins)) {
                        // Notifica il controller che una skin è stata acquistata
                        // e che il saldo delle monete deve essere aggiornato
                    }
                }
                repaint();
                break;
            }
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Scala le operazioni di disegno
        Graphics2D g2d = (Graphics2D) g;
        g2d.scale(scaleManager.getScaleX(), scaleManager.getScaleY());
        // Scala le operazioni di disegno
        //g.scale(scaleManager.getScaleX(), scaleManager.getScaleY());
        
        // Titolo
        g.setColor(Color.WHITE);
        Font titleFont = new Font("Arial", Font.BOLD, 36);
        g.setFont(titleFont);
        g.drawString("Skin Shop", 300, 80);
        
        // Monete
        Font coinFont = new Font("Arial", Font.BOLD, 24);
        g.setFont(coinFont);
        g.drawString("Coins: " + coins, 350, 130);
        
        // Disegna le skin disponibili
        List<Skin> skins = skinManager.getAvailableSkins();
        int skinWidth = 150;
        int spacing = 20;
        int totalWidth = skins.size() * (skinWidth + spacing) - spacing;
        int startX = (800 - totalWidth) / 2;
        
        Font skinFont = new Font("Arial", Font.PLAIN, 18);
        g.setFont(skinFont);
        
        for (int i = 0; i < skins.size(); i++) {
            Skin skin = skins.get(i);
            int skinX = startX + i * (skinWidth + spacing);
            
            // Sfondo della skin
            if (skin == skinManager.getCurrentSkin()) {
                g.setColor(new Color(0, 150, 0));
            } else if (skin.isUnlocked()) {
                g.setColor(new Color(0, 100, 0));
            } else {
                g.setColor(new Color(100, 0, 0));
            }
            g.fillRect(skinX, 200, skinWidth, 150);
            
            // Immagine della skin
            g.drawImage(skin.getImage(), skinX + 25, 220, 100, 100, null);
            
            // Nome della skin
            g.setColor(Color.WHITE);
            g.drawString(skin.getName(), skinX + 50, 340);
            
            // Prezzo o stato
            if (skin.isUnlocked()) {
                if (skin == skinManager.getCurrentSkin()) {
                    g.drawString("Selected", skinX + 45, 370);
                } else {
                    g.drawString("Available", skinX + 45, 370);
                }
            } else {
                g.drawString(skin.getPrice() + " coins", skinX + 40, 370);
            }
        }
        
        // Pulsante Back
        g.setColor(new Color(50, 50, 150));
        g.fillRect(350, 500, 100, 50);
        g.setColor(Color.WHITE);
        g.drawString("Back", 380, 530);
    }
}