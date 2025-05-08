package it.unibo.view.Menu.impl;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import it.unibo.controller.Menu.api.MenuController;
import it.unibo.view.Menu.api.MenuView;
import it.unibo.view.ScaleManager;

/**
 * Implementazione della vista del menu principale.
 */
public class MenuViewImpl implements MenuView {
    
    private MenuController controller;
    private final ScaleManager scaleManager;
    private BufferedImage backgroundImage;
    private BufferedImage logoImage;
    
    private List<MenuButton> buttons;
    private static final Color BUTTON_COLOR = new Color(76, 175, 80);
    private static final Color HOVER_COLOR = new Color(129, 199, 132);
    private static final Color TEXT_COLOR = Color.WHITE;
    private static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 48);
    private static final Font BUTTON_FONT = new Font("Arial", Font.BOLD, 24);
    
    private int currentWidth;
    private int currentHeight;
    
    /**
     * Costruttore per la vista del menu.
     * 
     * @param scaleManager Il gestore dello scaling
     */
    public MenuViewImpl(ScaleManager scaleManager) {
        this.scaleManager = scaleManager;
        this.currentWidth = scaleManager.getCurrentWidth();
        this.currentHeight = scaleManager.getCurrentHeight();
        
        loadImages();
        initButtons();
    }
    
    private void loadImages() {
        try {
            backgroundImage = ImageIO.read(getClass().getResourceAsStream("/images/menu_background.png"));
            logoImage = ImageIO.read(getClass().getResourceAsStream("/images/logo.png"));
        } catch (IOException | NullPointerException e) {
            // Se non riusciamo a caricare l'immagine, utilizziamo un colore di sfondo
            System.err.println("Impossibile caricare le immagini del menu: " + e.getMessage());
            backgroundImage = null;
            logoImage = null;
        }
    }
    
    private void initButtons() {
        buttons = new ArrayList<>();
        
        int buttonWidth = 200;
        int buttonHeight = 60;
        int spacing = 20;
        int startY = currentHeight / 2;
        
        // Bottone "Gioca"
        MenuButton playButton = new MenuButton(
            (currentWidth - buttonWidth) / 2,
            startY,
            buttonWidth,
            buttonHeight,
            "Gioca",
            e -> controller.startGame()
        );
        buttons.add(playButton);
        
        // Bottone "Negozio"
        MenuButton shopButton = new MenuButton(
            (currentWidth - buttonWidth) / 2,
            startY + buttonHeight + spacing,
            buttonWidth,
            buttonHeight,
            "Negozio",
            e -> controller.openShop()
        );
        buttons.add(shopButton);
        
        // Bottone "Esci"
        MenuButton exitButton = new MenuButton(
            (currentWidth - buttonWidth) / 2,
            startY + 2 * (buttonHeight + spacing),
            buttonWidth,
            buttonHeight,
            "Esci",
            e -> controller.exitGame()
        );
        buttons.add(exitButton);
    }
    
    @Override
    public void render(Graphics g) {
        // Disegna lo sfondo
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, currentWidth, currentHeight, null);
        } else {
            g.setColor(new Color(30, 30, 30));
            g.fillRect(0, 0, currentWidth, currentHeight);
        }
        
        // Disegna il logo o il titolo
        if (logoImage != null) {
            int logoWidth = currentWidth / 2;
            int logoHeight = logoWidth / 3; // Proporzione approssimativa per un logo
            g.drawImage(
                logoImage,
                (currentWidth - logoWidth) / 2,
                currentHeight / 6,
                logoWidth,
                logoHeight,
                null
            );
        } else {
            g.setFont(scaleFont(TITLE_FONT));
            g.setColor(Color.WHITE);
            String title = "ROAD HOP";
            FontMetrics fm = g.getFontMetrics();
            int titleWidth = fm.stringWidth(title);
            g.drawString(title, (currentWidth - titleWidth) / 2, currentHeight / 4);
        }
        
        // Disegna i bottoni
        for (MenuButton button : buttons) {
            button.render(g, this);
        }
    }
    
    @Override
    public void updateDimensions(int width, int height) {
        this.currentWidth = width;
        this.currentHeight = height;
        
        // Aggiorniamo anche la scala nel ScaleManager
        scaleManager.updateScale(width, height);
        
        // Ridimensiona i bottoni
        initButtons();
    }
    
    @Override
    public boolean handleClick(int x, int y) {
        int unscaledX = scaleManager.unscaleX(x);
        int unscaledY = scaleManager.unscaleY(y);
        
        for (MenuButton button : buttons) {
            if (button.contains(unscaledX, unscaledY)) {
                button.onClick();
                return true;
            }
        }
        
        return false;
    }
    
    @Override
    public void setController(MenuController controller) {
        this.controller = controller;
    }
    
    @Override
    public Font scaleFont(Font font) {
        // Utilizziamo il ScaleManager per scalare il font
        return scaleManager.scaleFont(font);
    }
    
    /**
     * Classe interna per rappresentare un bottone nel menu.
     */
    private static class MenuButton {
        private final Rectangle bounds;
        private final String text;
        private final ButtonClickListener listener;
        
        /**
         * Costruttore per un bottone del menu.
         * 
         * @param x Coordinata X
         * @param y Coordinata Y
         * @param width Larghezza
         * @param height Altezza
         * @param text Testo del bottone
         * @param listener Listener per il click
         */
        public MenuButton(int x, int y, int width, int height, String text, ButtonClickListener listener) {
            this.bounds = new Rectangle(x, y, width, height);
            this.text = text;
            this.listener = listener;
        }
        
        /**
         * Renderizza il bottone.
         * 
         * @param g Il contesto grafico
         * @param menuView Riferimento alla vista del menu per lo scaling del font
         */
        public void render(Graphics g, MenuView menuView) {
            ScaleManager scaleManager = ((MenuViewImpl) menuView).scaleManager;
            int scaledX = scaleManager.scaleX(bounds.x);
            int scaledY = scaleManager.scaleY(bounds.y);
            int scaledWidth = scaleManager.scaleWidth(bounds.width);
            int scaledHeight = scaleManager.scaleHeight(bounds.height);
            
            // Sfondo del bottone
            g.setColor(BUTTON_COLOR);
            g.fillRoundRect(scaledX, scaledY, scaledWidth, scaledHeight, 15, 15);
            
            // Bordo del bottone
            g.setColor(Color.BLACK);
            g.drawRoundRect(scaledX, scaledY, scaledWidth, scaledHeight, 15, 15);
            
            // Testo del bottone
            Font scaledFont = menuView.scaleFont(BUTTON_FONT);
            g.setFont(scaledFont);
            g.setColor(TEXT_COLOR);
            
            FontMetrics fm = g.getFontMetrics();
            int textWidth = fm.stringWidth(text);
            int textHeight = fm.getHeight();
            
            g.drawString(
                text,
                scaledX + (scaledWidth - textWidth) / 2,
                scaledY + (scaledHeight + textHeight / 2) / 2
            );
        }
        
        /**
         * Verifica se il punto è contenuto nel bottone.
         * 
         * @param x Coordinata X
         * @param y Coordinata Y
         * @return true se il punto è contenuto nel bottone, false altrimenti
         */
        public boolean contains(int x, int y) {
            return bounds.contains(x, y);
        }
        
        /**
         * Esegue l'azione del bottone.
         */
        public void onClick() {
            listener.onClick(this);
        }
    }
    
    /**
     * Interfaccia per il listener del click su un bottone.
     */
    @FunctionalInterface
    interface ButtonClickListener {
        void onClick(MenuButton button);
    }
}