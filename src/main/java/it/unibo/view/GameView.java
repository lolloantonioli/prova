package it.unibo.view;

import javax.swing.*;

import it.unibo.view.Map.api.MapView;
import it.unibo.view.Map.impl.MapViewImpl;

import java.awt.*;

public class GameView extends JFrame {
    private JPanel mainPanel;
    private JPanel scorePanel;
    private JLabel scoreLabel;
    private JLabel highScoreLabel;
    private JButton pauseButton;
    
    private MapView mapView;
    private CardLayout cardLayout;
    
    public GameView() {
        // Get screen dimensions
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = (int) screenSize.getWidth();
        int screenHeight = (int) screenSize.getHeight();
        
        // Set up the main window
        setTitle("Road Hop");
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Fullscreen
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Initialize main layout
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        
        // Create screens with adaptive sizing
        JPanel startScreen = createAdaptiveStartScreen(screenWidth, screenHeight);
        JPanel gameScreen = createAdaptiveGameScreen(screenWidth, screenHeight);
        JPanel gameOverScreen = createAdaptiveGameOverScreen(screenWidth, screenHeight);
        
        // Add screens to card layout
        mainPanel.add(startScreen, "START");
        mainPanel.add(gameScreen, "GAME");
        mainPanel.add(gameOverScreen, "GAMEOVER");
        
        // Set content
        add(mainPanel);
        
        // Remove window decorations for true fullscreen (optional)
        // setUndecorated(true);
    }
    
    private JPanel createAdaptiveStartScreen(int screenWidth, int screenHeight) {
        JPanel startPanel = new JPanel(new BorderLayout());
        startPanel.setBackground(Color.BLACK);
        
        // Adaptive font sizing
        int titleFontSize = screenHeight / 10;
        int buttonFontSize = screenHeight / 30;
        
        JLabel titleLabel = new JLabel("ROAD HOP", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, titleFontSize));
        titleLabel.setForeground(Color.WHITE);
        
        JButton startButton = createAdaptiveButton("Start Game", buttonFontSize);
        startButton.addActionListener(e -> showGameScreen());
        
        JButton instructionsButton = createAdaptiveButton("How to Play", buttonFontSize);
        instructionsButton.addActionListener(e -> showInstructions());
        
        // Adaptive button panel
        JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 
            screenWidth / 20,  // horizontal gap
            screenHeight / 20  // vertical gap
        ));
        buttonPanel.setBackground(Color.BLACK);
        buttonPanel.add(startButton);
        buttonPanel.add(instructionsButton);
        
        // Add padding
        JPanel paddedButtonPanel = new JPanel(new GridBagLayout());
        paddedButtonPanel.setBackground(Color.BLACK);
        paddedButtonPanel.add(buttonPanel);
        
        startPanel.add(titleLabel, BorderLayout.NORTH);
        startPanel.add(paddedButtonPanel, BorderLayout.CENTER);
        
        return startPanel;
    }
    
    private JPanel createAdaptiveGameScreen(int screenWidth, int screenHeight) {
        JPanel gameScreen = new JPanel(new BorderLayout());
        
        // Adaptive score panel
        int scoreFontSize = screenHeight / 40;
        scorePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        scoreLabel = new JLabel("Score: 0");
        scoreLabel.setFont(new Font("Arial", Font.BOLD, scoreFontSize));
        
        highScoreLabel = new JLabel("High Score: 0");
        highScoreLabel.setFont(new Font("Arial", Font.BOLD, scoreFontSize));
        
        pauseButton = createAdaptiveButton("Pause", scoreFontSize);
        pauseButton.addActionListener(e -> togglePause());
        
        scorePanel.add(scoreLabel);
        scorePanel.add(highScoreLabel);
        scorePanel.add(pauseButton);
        
        // Create map view with screen-adaptive size
        mapView = new MapViewImpl();
        
        gameScreen.add(scorePanel, BorderLayout.NORTH);
        gameScreen.add((Component) mapView, BorderLayout.CENTER);
        
        return gameScreen;
    }
    
    private JPanel createAdaptiveGameOverScreen(int screenWidth, int screenHeight) {
        JPanel gameOverPanel = new JPanel(new BorderLayout());
        gameOverPanel.setBackground(Color.BLACK);
        
        // Adaptive font sizing
        int titleFontSize = screenHeight / 10;
        int scoreFontSize = screenHeight / 20;
        int buttonFontSize = screenHeight / 30;
        
        JLabel gameOverLabel = new JLabel("GAME OVER", SwingConstants.CENTER);
        gameOverLabel.setFont(new Font("Arial", Font.BOLD, titleFontSize));
        gameOverLabel.setForeground(Color.WHITE);
        
        JLabel finalScoreLabel = new JLabel("Your Score: 0", SwingConstants.CENTER);
        finalScoreLabel.setFont(new Font("Arial", Font.BOLD, scoreFontSize));
        finalScoreLabel.setForeground(Color.WHITE);
        
        JButton restartButton = createAdaptiveButton("Restart", buttonFontSize);
        restartButton.addActionListener(e -> restartGame());
        
        JButton mainMenuButton = createAdaptiveButton("Main Menu", buttonFontSize);
        mainMenuButton.addActionListener(e -> showStartScreen());
        
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 
            screenWidth / 20,  // horizontal gap
            screenHeight / 20  // vertical gap
        ));
        buttonPanel.setBackground(Color.BLACK);
        buttonPanel.add(restartButton);
        buttonPanel.add(mainMenuButton);
        
        // Add padding
        JPanel paddedButtonPanel = new JPanel(new GridBagLayout());
        paddedButtonPanel.setBackground(Color.BLACK);
        paddedButtonPanel.add(buttonPanel);
        
        gameOverPanel.add(gameOverLabel, BorderLayout.NORTH);
        gameOverPanel.add(finalScoreLabel, BorderLayout.CENTER);
        gameOverPanel.add(paddedButtonPanel, BorderLayout.SOUTH);
        
        return gameOverPanel;
    }
    
    // Create adaptive buttons with dynamic sizing
    private JButton createAdaptiveButton(String text, int fontSize) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, fontSize));
        button.setPreferredSize(new Dimension(
            fontSize * text.length() * 2, 
            fontSize * 3
        ));
        button.setBackground(Color.DARK_GRAY);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        return button;
    }
    
    // Public methods for game state management
    public void showStartScreen() {
        cardLayout.show(mainPanel, "START");
    }
    
    public void showGameScreen() {
        cardLayout.show(mainPanel, "GAME");
    }
    
    public void showGameOverScreen(int finalScore) {
        cardLayout.show(mainPanel, "GAMEOVER");
    }
    
    public void updateScore(int score) {
        scoreLabel.setText("Score: " + score);
    }
    
    public void updateHighScore(int highScore) {
        highScoreLabel.setText("High Score: " + highScore);
    }
    
    public void setPauseState(boolean isPaused) {
        pauseButton.setText(isPaused ? "Resume" : "Pause");
    }
    
    private void togglePause() {
        // This method would be connected to the game controller
        // Placeholder for pause functionality
        System.out.println("Pause/Resume game");
    }
    
    private void restartGame() {
        // This method would be connected to the game controller
        // Placeholder for restart functionality
        System.out.println("Restart game");
    }
    
    private void showInstructions() {
        JOptionPane.showMessageDialog(this, 
            "Road Hop Instructions:\n" +
            "- Use arrow keys to move\n" +
            "- Avoid obstacles\n" +
            "- Collect coins\n" +
            "- Travel as far as possible!", 
            "How to Play", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    // Main method for testing
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GameView gameView = new GameView();
            gameView.setVisible(true);
        });
    }
}