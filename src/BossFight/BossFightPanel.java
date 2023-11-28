package BossFight;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Timer;
import javax.swing.*;

import TypeGameModel.*;
import ui.TypeFrame;

public class BossFightPanel extends JPanel implements ActionListener {
    private Timer t;
    private boolean phase1;
    private boolean mainFight;
    private boolean winScreen;
    private boolean loseScreen;

    private String introText;
    private String introText2;
    private int timesWarningCalled; // counts the number of times the special attack warning has been called
    private int timesRepainted;

    private JButton skipIntro;
    private BossFightGame game;
    private Image bossImage;
    private Image playerImage;

    // Constructor, creates the panel where the boss fight occurs.
    public BossFightPanel() {
        game = new BossFightGame();
        game.setBossFightPanel(this);
        phase1 = true;
        mainFight = false;
        winScreen = false;
        loseScreen = false;
        timesRepainted = 0;
        timesWarningCalled = 0;

        bossImage = new ImageIcon("boss.png").getImage();
        bossImage = bossImage.getScaledInstance(120, 120,  java.awt.Image.SCALE_SMOOTH); // rescaling image

        playerImage = new ImageIcon("player.png").getImage();

        skipIntro = new JButton(); // Option to skip intro dialogue (otherwise takes 17 seconds)
        skipIntro.setBounds(600, 880, 150, 60);
        skipIntro.setText("Skip intro:");
        skipIntro.setFocusable(false);
        skipIntro.addActionListener(this);

        setBackground(Color.BLACK);
        setLayout(null);
        setBounds(0, 0, TypeFrame.DIMENSION1, TypeFrame.DIMENSION2);
        add(skipIntro);
    }

    public BossFightGame getGame() {
        return game;
    }

    // called from bossFightGame. Causes the main boss fight arena to be shown on screen (after phase 1).
    public void setMainFight() {
        setBackground(Color.GRAY);
        remove(skipIntro);
        timesRepainted = 0;
        phase1 = false;
        mainFight = true;
        winScreen = false;
        loseScreen = false;
    }

    // called from bossFightGame. Causes the win screen to be shown on screen (after main fight). Calls repaint.
    public void setWinScreen() {
        phase1 = false;
        mainFight = false;
        winScreen = true;
        loseScreen = false;
        repaint();
    }

    // called from bossFightGame. Causes the lose screen to be shown on screen (after main fight). Calls repaint.
    public void setLoseScreen() {
        phase1 = false;
        mainFight = false;
        winScreen = false;
        loseScreen = true;
        repaint();
    }

    // Paints the component based on which stage the game is at
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (phase1) { // fight introduction dialogue
            paintIntro(g);
        } else if (mainFight) { // actual boss fight
            if (game.isIncoming()) {
                paintWarning(g);
            } else if (timesWarningCalled != 0) {
                timesWarningCalled = 0; // reset this counter when the attack has occurred
            }
            paintArenaWalls(g);
            paintPlayerBossStats(g);
            paintPlayerBoss(g);
            if (game.getWordPassages().size() > 0) {
                paintPassages(g);
            }
        } else if (winScreen) { // win screen (do this)
            paintWinScreen(g);
        } else if (loseScreen) { // lose screen (do this)
            paintLostScreen(g);
        }
        timesRepainted++;
    }

    // Shows on a blank screen the lines the boss says before the fight actually starts. FIX
    private void paintIntro(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        Color saved = g.getColor();
        g.setFont(new Font("Chiller", 20, 33));
        g.setColor(new Color(127, 36, 39));
        FontMetrics fm = g.getFontMetrics();
        if (timesRepainted == 0) {
            introText = "So I hear you're good at typing, eh?";
            introText2 = " ";
        } else if (timesRepainted == 130) {
            introText = "Allow me to introduce myself. I am the";
            introText2 = "Wizard of Words, and I am here to Test you.";
        } else if (timesRepainted == 260) {
            introText = "I must warn you, the Test is incredibly";
            introText2 = "dangerous, if you wish to leave then do so now.";
        } else if (timesRepainted == 420) {
            introText = "Very well then. Before we begin, we";
            introText2 = "have one rule around these parts:";
        } else if (timesRepainted == 560) {
            introText = "Don't die.";
            introText2 = " ";
        }
        int width = fm.stringWidth(introText);
        g.drawString(introText, 400 - width/2, 440);
        width = fm.stringWidth(introText2);
        g.drawString(introText2, 400 - width/2, 475);

        g.setFont(new Font("Helvetica", 20, 30));
        g.setColor(new Color(199, 175, 154));
        fm = g.getFontMetrics();

        String header;
        if (timesRepainted < 260) {
            header = "Mysterious voice:";
        } else {
            header = "The Wizard of Words:";
            g2.drawImage(bossImage, 400 - game.getBossWidth()/2, // draw boss image too once his name is revealed.
                    300 - game.getBossWidth()/2,null);
        }
        width = fm.stringWidth(header);
        g.drawString(header, 400 - width/2, 400);
        g.setColor(saved);
    }

    // Handler for button presses (the only one needed so far is to skip the intro dialogue phase)
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == skipIntro) {
            game.skipIntro();
        }
    }

    // Before a special attack occurs, the areas in which the player will be damaged will flash red.
    private void paintWarning(Graphics g) {
        if (timesWarningCalled % 32 < 16) {
            Color saved = g.getColor();
            g.setColor(new Color(182, 37, 37, 157));
            switch (game.getSpecialAttackVersion()) {
                case 0:
                    g.fillRect(225, 50, 350, 800);
                    break;
                case 1:
                    g.fillRect(100, 50, 225, 800);
                    g.fillRect(475, 50, 225, 800);
                    break;
                case 2:
                    g.fillRect(0, 550, 800, 300);
                    break;
            }
            g.setColor(saved);
        }
        timesWarningCalled++;
    }

    // Paints the "arena" in which the boss fight occurs. Also paints the "bouncy" walls are present in phase 3+;
    private void paintArenaWalls(Graphics g) {
        Color saved = g.getColor();
        g.setColor(new Color(0, 0, 0));
        g.drawLine(0, 50, 800, 50);
        g.drawLine(0, 850, 800, 850);
        if (game.getPhase() >= 3) {
            g.setColor(new Color(75, 206, 151));
            g.fillRect(0, 50, 10, 800); // reflects the bounds of the arena (5<=x<=780, 50<=y<=850)
            g.fillRect(775, 50, 10, 800);
            g.fillRect(0, 50, 800, 10);
            g.fillRect(0, 840, 800, 10);
        }
        g.setColor(saved);
    }

    // Paints the user's accuracy, lives left, and the boss' remaining health.
    private void paintPlayerBossStats(Graphics g) {
        Color saved = g.getColor();
        g.setColor(new Color(0, 0, 0));
        g.setFont(new Font("Comic Sans MS", 20, 24));
        g.drawString("Accuracy: " + game.getAccuracy() + "%", 50, 890);
        g.drawString("Health: " + ((int) (game.getLivesLeft() * 10))/10.0 + "/4", 50, 930);
        g.drawString("Boss health: " + game.getBossHP() + "/100", 530, 910);
        g.setColor(saved);
    }

    // Adds the boss and player images to the screen as soon as mainFight is set to true. Updates their positions
    // Also displays boss' shield and dialogue. FIX, USE IMAGES
    private void paintPlayerBoss(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        Color saved = g.getColor();

        // Update player and boss positions
        g2.drawImage(bossImage, game.getBossX() - game.getBossWidth()/2,
                        game.getBossY() - game.getBossWidth()/2,null);
        g2.drawImage(playerImage, game.getPlayerX() - game.getPlayerWidth()/2,
                game.getPlayerY() - game.getPlayerWidth()/2,null);

        // Draw boss shield. Represented as a hollow rectangle with custom colors corresponding to its status.
        if (game.getShieldStatus() != 0) {
            switch (game.getShieldStatus()) {
                case 1:
                    g.setColor(new Color(190, 26, 26));
                    break;
                case 2:
                    g.setColor(new Color(57, 180, 68));
                    break;
                case 3:
                    g.setColor(new Color(27, 136, 225));
                    break;
            }
            g.fillRect(game.getBossX() - game.getBossWidth()/2 - 10,
                    game.getBossY() - game.getBossWidth()/2 - 10, 5, game.getBossWidth() + 20);
            g.fillRect(game.getBossX() - game.getBossWidth()/2 - 10,
                    game.getBossY() - game.getBossWidth()/2 - 10, game.getBossWidth() + 20, 5);
            g.fillRect(game.getBossX() + game.getBossWidth()/2 + 5,
                    game.getBossY() - game.getBossWidth()/2 - 10, 5, game.getBossWidth() + 20);
            g.fillRect(game.getBossX() - game.getBossWidth()/2 - 10,
                    game.getBossY() + game.getBossWidth()/2 + 5, game.getBossWidth() + 20, 5);
        }

        // Boss dialogue
        if (game.isDialogueActive()) {
            g.setColor(new Color(0, 0, 0));
            g.setFont(new Font("Helvetica", 20, 25));
            FontMetrics fm = g.getFontMetrics();
            int width = fm.stringWidth(game.getDialogue());
            g.drawString("Boss: '" + game.getDialogue() + "'", 400 - width/2, 40);
        }
        g.setColor(saved);
    }

    // Prints every single TypePassage that is on screen
    private void paintPassages(Graphics g) {
        Color saved = g.getColor();
        g.setFont(new Font("Helvetica", 20, 20));
        g.setColor(new Color(0, 0, 0, 105));
        FontMetrics fm = g.getFontMetrics();
        int xPos;

        for (int i=0; i<game.getWordPassages().size(); i++) {
            // to ensure that no words spill off the side of the panel
            xPos = game.getWordPassages().get(i).getXPos();

            for (TypeChar c : game.getWordPassages().get(i).getCharList()) {
                // displays each word, character by character
                String str = Character.toString(c.getChar());
                int charWidth = fm.stringWidth(str);
                g.setColor(c.getStatus());
                g.drawString(str, xPos, game.getWordPassages().get(i).getYPos());
                xPos += charWidth + 1;
            }
        }
        g.setColor(saved);
    }

    // Paints the win screen if the player defeats the boss. Work in progress
    private void paintWinScreen(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        Color saved = g.getColor();
        setBackground(new Color(140, 192, 106, 186));
        g2.drawImage(bossImage, 400 - game.getBossWidth()/2,
                300 - game.getBossWidth()/2,null);

        g.setFont(new Font("Chiller", 20, 35));
        g.setColor(new Color(239, 238, 238, 211));
        FontMetrics fm = g.getFontMetrics();
        String winText = "I stand humbled. Congratulations.";
        int width = fm.stringWidth(winText);
        g.drawString(winText, 400 - width/2, 400);

        g.setFont(new Font("Helvetica", 20, 25));
        g.setColor(new Color(199, 175, 154));
        fm = g.getFontMetrics();

        String accuracy = "Your accuracy was an astounding " + game.getAccuracy() + "%!";
        width = fm.stringWidth(accuracy);
        g.drawString(accuracy, 400 - width/2, 700);
        g.setColor(saved);
    }

    // Paints the lose screen if the player dies. Work in progress
    private void paintLostScreen(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        Color saved = g.getColor();
        setBackground(new Color(91, 83, 199, 255));
        g2.drawImage(bossImage, 400 - game.getBossWidth()/2,
                300 - game.getBossWidth()/2,null);

        g.setFont(new Font("Chiller", 20, 35));
        g.setColor(new Color(255, 255, 255, 255));
        FontMetrics fm = g.getFontMetrics();
        String loseText = "Not good enough, I see. A true pity.";
        int width = fm.stringWidth(loseText);
        g.drawString(loseText, 400 - width/2, 400);

        g.setFont(new Font("Helvetica", 20, 25));
        g.setColor(new Color(57, 51, 46));
        fm = g.getFontMetrics();

        String accuracy = "For what it's worth, your accuracy was " + game.getAccuracy() + "%.";
        width = fm.stringWidth(accuracy);
        g.drawString(accuracy, 400 - width/2, 700);
        g.setColor(saved);
    }

    // Will be called when the keyhandler in TypeFrame is called. Passes the input to FallingWordsGame for processing
    // (i.e. determining whether the character is correct or not). Updates the screen as needed.
    // MODIFIES: game
    public void charPressed(char c) {
        if (mainFight) {
            game.charTyped(c);
        }
    }
}
