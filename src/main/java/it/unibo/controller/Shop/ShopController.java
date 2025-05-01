package it.unibo.controller.Shop;

import it.unibo.model.Shop.SkinManager;
import it.unibo.view.ScaleManager;
import it.unibo.view.Shop.ShopView;

public class ShopController {
    private final SkinManager skinManager;
    private final ShopView shopView;
    private int playerCoins;
    
    public ShopController(SkinManager skinManager, ScaleManager scaleManager) {
        this.skinManager = skinManager;
        this.shopView = new ShopView(skinManager, scaleManager);
        this.playerCoins = 0;
    }
    
    public void setPlayerCoins(int coins) {
        this.playerCoins = coins;
        shopView.setCoins(coins);
    }
    
    public int getPlayerCoins() {
        return playerCoins;
    }
    
    public void buySkin(String skinId) {
        if (skinManager.buySkin(skinId, playerCoins)) {
            // Aggiorna il saldo dopo l'acquisto
            playerCoins -= skinManager.getAvailableSkins().stream()
                .filter(s -> s.getId().equals(skinId))
                .findFirst()
                .orElseThrow()
                .getPrice();
            shopView.setCoins(playerCoins);
        }
    }
    
    public void selectSkin(String skinId) {
        skinManager.selectSkin(skinId);
    }
    
    public ShopView getView() {
        return shopView;
    }
}