package ui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import TypeGameModel.FallingWordsGame;
import TypeGameModel.TypeChar;

/*
 * Represents the panel on which a falling words game is played.
 */
public class FallingWordsPanel extends JPanel implements ActionListener {
    private Timer t;
    private boolean beginScreen;
    private boolean gameScreen;
    private boolean endScreen;

    private FallingWordsGame game; // the corresponding fallingwordsgame

    private JButton easyDifficulty;
    private JButton mediumDifficulty;
    private JButton hardDifficulty;

    private JLabel label;
    private JButton replayButton;

    // Constructor, creates the panel
    public FallingWordsPanel() {
        beginScreen = true;
        gameScreen = false;
        endScreen = false;

        // initializing all the JComponents that will be needed
        easyDifficulty = new JButton();
        easyDifficulty.setBounds(200, 450, 100, 100);
        easyDifficulty.setText("Easy"); // "easy difficulty" button
        easyDifficulty.setFocusable(false);
        easyDifficulty.addActionListener(this);

        mediumDifficulty = new JButton();
        mediumDifficulty.setBounds(350, 450, 100, 100);
        mediumDifficulty.setText("Medium"); // "medium difficulty" button
        mediumDifficulty.setFocusable(false);
        mediumDifficulty.addActionListener(this);

        hardDifficulty = new JButton();
        hardDifficulty.setBounds(500, 450, 100, 100);
        hardDifficulty.setText("Hard"); // "hard difficulty" button
        hardDifficulty.setFocusable(false);
        hardDifficulty.addActionListener(this);

        replayButton = new JButton();
        replayButton.setBounds(400, 400, 150, 80);
        replayButton.setText("Replay"); // allows the user to play the game again without closing the application.
        replayButton.setFocusable(false);
        replayButton.addActionListener(this);

        label = new JLabel(); // instructions that are shown before the game starts
        label.setFont(new Font("Helvetica", Font.PLAIN, 20));
        label.setText("<html>Type the words before they fall to the ground! <br/><br/>" +
                " Please select the difficulty you wish to play</html>");
        label.setBounds(210, 300, 500, 150);

        setBackground(Color.GRAY);
        setLayout(null);
        setBounds(0, 0, TypeFrame.DIMENSION1, TypeFrame.DIMENSION2);
    }

    // Simple getter
    public FallingWordsGame getGame() {
        return game;
    }

    // Paints the component based on which stage the game is at
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (beginScreen) { // game begin screen
            paintBeginScreen(g);
        }
        else if (gameScreen) { // actual game screen
            paintPassage(g);
            paintStats(g);
        }
        else if (endScreen) { // game end screen
            paintMessages(g);
        }
    }

    // BEGIN SCREEN

    // Plays the begin screen of this gamemode. Prompts the user to select the game's difficulty via buttons
    // MODIFIES: this, g
    private void paintBeginScreen(Graphics g) {
        add(easyDifficulty);
        add(mediumDifficulty);
        add(hardDifficulty);
        add(label);
    }

    // Handler for button presses (the difficulty buttons and the replay button)
    @Override
    public void actionPerformed(ActionEvent e) {
        if (beginScreen) {
            if (e.getSource() == easyDifficulty) {
                game = new FallingWordsGame(1);
            }
            else if (e.getSource() == mediumDifficulty) {
                game = new FallingWordsGame(2);
            }
            else if (e.getSource() == hardDifficulty) {
                game = new FallingWordsGame(3);
            } // sets up the game, based on which button is pressed
            remove(easyDifficulty);
            remove(mediumDifficulty);
            remove(hardDifficulty);
            remove(label);
            beginScreen = false;
            gameScreen = true;
            addTimer(); // starts the timer (see below).
            repaint(); // displays the actual game
        } else if (endScreen) { // if the replay button is pressed at the end of the game
            remove(replayButton);
            endScreen = false;
            beginScreen = true;
            repaint(); // displays the begin screen
        }
    }

    // GAME SCREEN

    // Prints all the words onto the screen at their respective positions
    // MODIFIES: g
    private void paintPassage(Graphics g) {
        Color saved = g.getColor();
        g.setFont(new Font("Helvetica", 20, 20));
        FontMetrics fm = g.getFontMetrics();
        int width, xPos;

        for (int i=0; i<game.getWordPassages().size(); i++) {
            // to ensure that no words spill off the side of the panel
            width = fm.stringWidth(game.getSelectedWords().get(i));
            if (game.getXPosList().get(i) + width > 760) {
                xPos = 760 - width;
            } else {
                xPos = game.getXPosList().get(i);
            }

            for (TypeChar c : game.getWordPassages().get(i).getCharList()) {
                // displays each word, character by character
                String str = Character.toString(c.getChar());
                int charWidth = fm.stringWidth(str);
                g.setColor(c.getStatus());
                g.drawString(str, xPos, game.getYPosList().get(i));
                xPos += charWidth + 1;
            }
        }
        g.setColor(saved);
    }

    // Prints the user's accuracy, lives left, and words left at the top of the screen.
    // MODIFIES: g
    private void paintStats(Graphics g) {
        Color saved = g.getColor();
        g.setColor(new Color(0, 0, 0));
        g.setFont(new Font("Comic Sans MS", 20, 16));
        g.drawString("Accuracy: " + game.getAccuracy() + "%", 50, 20);
        g.drawString("Lives left: " + game.getLivesLeft(), 300, 20);
        g.drawString("Words left: " + game.getWordsLeft(), 550, 20);
        g.drawLine(0, 800, 800, 800); // just to denote where the words will disappear
        g.setColor(saved);
    }

    // Will be called when the keyhandler in TypeFrame is called. Passes the input to FallingWordsGame for processing
    // (i.e. determining whether the character is correct or not). Updates the screen as needed.
    // MODIFIES: game
    public void charPressed(char c) {
        if (gameScreen) {
            game.charTyped(c);
            if (game.getGameWon()) {
                gameScreen = false;
                endScreen = true;
                t.stop(); // stops the timer when the game is over.
            }
            repaint(); // will display the end screen if the game's over.
        }
    }

    // Sets up a timer that is initialized when the game starts. Updates game periodically.
    // MODIFIES: none
    // EFFECTS: initializes a timer that updates game each
    // 25 milliseconds
    private void addTimer() {
        t = new Timer(25, new ActionListener() {
            int counter = 0;

            @Override
            public void actionPerformed(ActionEvent ae) {
                counter++;
                game.shiftWordsDown(); // shifts the words down every tick.
                if (counter % (120/game.getDifficulty()) == 0) { // add a new random word every 3 seconds for easy,
                    // 1.5 seconds for medium. and 1 seconds for hard.
                    game.addNewWord();
                    counter = 0;
                }

                if (game.getGameLost()) {
                    gameScreen = false;
                    endScreen = true;
                    t.stop(); // stops the timer if the game has been lost.
                }
                repaint();
            }
        });
        t.start();
    }

    // END SCREEN

    // Displays the congratulations/fail messages at the end of the game and gives the user the option to play again
    // MODIFIES: g
    private void paintMessages(Graphics g) {
        Color saved = g.getColor();
        add(replayButton);
        g.setColor(new Color(0, 0, 0));
        g.setFont(new Font("Comic Sans MS", 20, 30));
        if (game.getGameLost()) {
            g.drawString("Oh no! You lost! Maybe try getting good", 100, 150);
            g.drawString( "You had " + game.getWordsLeft() + " words left, and an accuracy of "
                    + game.getAccuracy() + "%.", 50, 300);
            g.drawString("Play this race again?", 110, 450);
        } else if (game.getGameWon()) {
            g.drawString("Congratulations! You won the game with", 100, 250);
            g.drawString( game.getLivesLeft() + " lives left, and an accuracy of "
                    + game.getAccuracy() + "%!", 120, 300);
            g.drawString("Play this race again?", 110, 450);
        }
        g.setColor(saved);
    }
}
