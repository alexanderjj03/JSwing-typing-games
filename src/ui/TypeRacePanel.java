package ui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Line2D;
import java.util.Collections;
import java.util.List;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import TypeGameModel.TypeChar;
import TypeGameModel.TypeRaceGame;

/*
 * Represents the panel on which a type race game is played.
 */
public class TypeRacePanel extends JPanel implements ActionListener, ChangeListener {
    List<Double> wpmList;

    private boolean beginScreen;
    private boolean raceActive;
    private boolean endScreen;

    private TypeRaceGame game;

    private JSlider slider;
    private JButton startButton;
    private JLabel label;
    private JButton replayButton;

    // creates the panel
    public TypeRacePanel() {
        beginScreen = true;
        raceActive = false;
        endScreen = false;

        // initializing all the JComponents that will be needed
        startButton = new JButton(); // brings the user from the begin screen to the game screen.
        startButton.setBounds(255, 500, 250, 100);
        startButton.setText("Proceed to game");
        startButton.setFocusable(false);
        startButton.addActionListener(this);

        replayButton = new JButton();
        replayButton.setBounds(400, 720, 150, 80);
        replayButton.setText("Replay"); // gives the user the option to replay the race
        replayButton.setFocusable(false);
        replayButton.addActionListener(this);

        slider = new JSlider(JSlider.VERTICAL, 20, 260, 140);
        slider.setBackground(Color.GRAY); // slider to select the number of words in the passage
        slider.setBounds(200, 40, 400, 400);
        slider.setFont(new Font("Helvetica", Font.PLAIN, 15));
        slider.setMinorTickSpacing(10);
        slider.setMajorTickSpacing(40);
        slider.setPaintTicks(true);
        slider.setPaintTrack(true);
        slider.setPaintLabels(true);
        slider.setFocusable(false);
        slider.addChangeListener(this);

        label = new JLabel(); // shows the slider's current value
        label.setFont(new Font("Helvetica", Font.PLAIN, 20));
        label.setText("How many words would you like to type? " + slider.getValue() + " words.");
        label.setBounds(150, 420, 500, 100);

        setBackground(Color.GRAY);
        setLayout(null);
        setBounds(0, 0, TypeFrame.DIMENSION1, TypeFrame.DIMENSION2);
    }

    // simple getter
    public TypeRaceGame getGame() {
        return game;
    }

    // Paints the component based on which stage the game is at
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (beginScreen) { // race begin screen
            paintBeginScreen(g);
        }
        else if (raceActive) { // actual race screen (displays passage)
            paintPassage(g);
            paintAccuracyWpm(g);
        }
        else if (endScreen) { // race end screen
            paintMessages(g);
            paintGraph(g);
        }
    }

    // BEGIN SCREEN

    // Plays the begin screen of the Type Game. Includes a slider which determines the number of words that are to
    // typed in the game.
    // REQUIRES: None
    // MODIFIES: this, g
    private void paintBeginScreen(Graphics g) {
        add(slider);
        add(startButton);
        add(label);
    }

    // Handler to be called by startButton or replayButton on the begin or end screens, respectively.
    @Override
    public void actionPerformed(ActionEvent e) {
        if (beginScreen) {
            game = new TypeRaceGame(slider.getValue()); // sets up the type race based on the slider's input
            remove(slider);
            remove(startButton);
            remove(label);
            beginScreen = false;
            raceActive = true;
            repaint(); // displays the type race
        } else if (endScreen) {
            remove(replayButton);
            endScreen = false;
            beginScreen = true;
            repaint(); // displays the begin screen
        }
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        if (beginScreen) {
            label.setText("How many words would you like to type? " + slider.getValue() + " words.");
            // updates label's text to reflect the current slider value.
        }
    }

    // TYPE RACE

    // Draws the entire passage, character by character, onto the screen. Ensures the text doesn't overflow, and that
    // individual words aren't split between lines.
    // MODIFIES: g
    // EFFECTS:  draws passage onto the screen.
    private void paintPassage(Graphics g) {
        Color saved = g.getColor();
        g.setFont(new Font("Helvetica", 20, 20));
        int x = 50;
        int y = 60;
        int maxWidth = 0;
        int wordWidth = 0;

        for (TypeChar c : game.getPassage().getCharList()) { // finding the word in the passage with the greatest width
            String str = Character.toString(c.getChar());
            FontMetrics fm = g.getFontMetrics();
            int width = fm.stringWidth(str);
            wordWidth += width;
            if (wordWidth > maxWidth) {
                maxWidth = wordWidth;
            }

            if (str.equals(" ")) { // uses spaces to identify words within the passage (an array of TypeChars)
                wordWidth = 0;
            }
        }

        for (TypeChar c : game.getPassage().getCharList()) {
            // iterates through the passage character by character and prints each one on screen
            String str = Character.toString(c.getChar());
            FontMetrics fm = g.getFontMetrics();
            int width = fm.stringWidth(str);

            g.setColor(c.getStatus());
            g.drawString(str, x, y);
            x += width + 1;
            if (x >= TypeFrame.DIMENSION1 - maxWidth - 20 && str.equals(" ")) {
                // start a new line if a space is close to the right side of the screen
                x = 50;
                y = y + 22;
            }
        }
        g.setColor(saved);
    }

    // Draws the user's accuracy and wpm at the top of the screen
    // MODIFIES: g
    // EFFECTS:  draws accuracy % and wpm onto g
    private void paintAccuracyWpm(Graphics g) {
        Color saved = g.getColor();
        g.setColor(new Color(0, 0, 0));
        g.setFont(new Font("Comic Sans MS", 20, 16));
        g.drawString("Accuracy: " + game.getAccuracy() + "%", 150, 20);
        g.drawString("Wpm: " + game.getCumulativeWpm(), 500, 20);
        g.setColor(saved);
    }

    // Will be called when the keyhandler in TypeFrame is called. Passes on the input to TypeGame for processing
    // (i.e. determining whether the character is correct or not). Updates the screen as needed.
    public void charPressed(char c) {
        if (raceActive) {
            game.charTyped(c);
            if (game.isGameOver()) {
                raceActive = false;
                endScreen = true;
            }
            repaint(); // will display the end screen if the game's over.
        }
    }

    // END SCREEN

    // Simply prints out the congratulations message for finishing the passage, avg wpm and accuracy. Also asks
    // the user if they want to play again, and gives them a button to do so
    // MODIFIES: g
    private void paintMessages(Graphics g) {
        Color saved = g.getColor();
        add(replayButton);
        g.setColor(new Color(0, 0, 0));
        g.setFont(new Font("Comic Sans MS", 20, 32));
        g.drawString("Congratulations! You finished the race with", 50, 50);
        g.drawString("a wpm of " + game.getCumulativeWpm() + ", and an accuracy of "
                + game.getAccuracy() + "%!", 70, 85);
        g.drawString("Play this race again?", 70, 770);
        g.setColor(saved);
    }

    // Graphs the user's wpm across the passage (vs characters typed thusfar).
    // MODIFIES: g
    private void paintGraph(Graphics g) {
        wpmList = game.getWpmList();
        Color saved = g.getColor();
        g.setColor(new Color(0, 0, 0));

        // maximum and minimum bounds on the x and y axes
        int xMin = 0;
        int xMax = wpmList.size() * 50 + 20;
        double yMin = Collections.min(wpmList) - 10;
        double yMax = Collections.max(wpmList) + 10;

        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(4));
        g2.draw(new Line2D.Float(150, 150, 150, 650));
        g2.draw(new Line2D.Float(150, 650, 650, 650)); // axes
        g.setFont(new Font("Arial", 20, 16));
        g.drawString("Local WPM", 50, 400);
        g.drawString("Characters typed thusfar", 350, 700); // axis labels

        g.setFont(new Font("Arial", 20, 12));
        for (int i = 0; i<=5; i++) {
            // Tick marks
            g.drawLine(140, 150 + i*100, 150, 150 + i*100);
            g.drawLine(150 + i*100, 650, 150 + i*100, 660);

            // Tick labels
            String str = Double.toString(Math.round(100.0*(yMin + (yMax - yMin) * i/5.0))/100.0);
            FontMetrics fm = g.getFontMetrics();
            int width = fm.stringWidth(str);
            g.drawString(Double.toString(xMin + (xMax - xMin) * i/5.0), 136 + i*100, 675); // tick labels
            g.drawString(str, 135-width, 655 - i*100);
        }
        g2.setStroke(new BasicStroke(2));
        for (int i=0; i<wpmList.size(); i++) {
            g.drawOval((int) (147.0 + 500.0*(i+1)*50.0/xMax),
                    (int) (647 - 500.0*(wpmList.get(i) - yMin)/(yMax - yMin)), 6, 6);
            // drawing the graph's data points
            if (i==0) {
                g.drawLine(150, (int) (650 - 500.0*(wpmList.get(i) - yMin)/(yMax - yMin)),
                        (int) (150.0 + 500.0*(i+1)*50.0/xMax),
                        (int) (650 - 500.0*(wpmList.get(i) - yMin)/(yMax - yMin))); // horizontal line to first point
            } else {
                g.drawLine((int) (150.0 + 500.0*i*50.0/xMax),
                        (int) (650 - 500.0*(wpmList.get(i-1) - yMin)/(yMax - yMin)),
                        (int) (150.0 + 500.0*(i+1)*50.0/xMax),
                        (int) (650 - 500.0*(wpmList.get(i) - yMin)/(yMax - yMin))); // lines between consecutive points
            }
        }
        g.setColor(saved);
    }
}
