package ui;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

/*
 * Represents the frame and components on which the typing games are initialized and played.
 */
public class TypeFrame implements KeyListener, ActionListener {
    public static final int DIMENSION1 = 800;
    public static final int DIMENSION2 = 1000;

    private JFrame contentFrame;
    private JPanel mainMenu;
    private JLabel mainMenuText;
    private JButton option1;
    private JButton option2;
    private JButton backToMainMenu;

    private int gameMode; // 0 for none, 1 for type race. 2 for falling words.
    private TypeRacePanel racePanel;
    private FallingWordsPanel fallingPanel;

    public TypeFrame() {
        gameMode = 0;
        contentFrame = new JFrame("The Typing games"); // everything will be displayed on this frame.
        contentFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        contentFrame.setBackground(Color.GRAY);
        contentFrame.setLayout(null);
        contentFrame.setResizable(false);
        contentFrame.setSize(DIMENSION1, DIMENSION2);
        contentFrame.addKeyListener(this);
        centreOnScreen();

        mainMenu = new JPanel();
        // will prompt the user to choose between playing the type race game or falling words game
        mainMenu.setBackground(Color.GRAY);
        mainMenu.setLayout(null);
        mainMenu.setBounds(0, 0, TypeFrame.DIMENSION1, TypeFrame.DIMENSION2);

        mainMenuText = new JLabel();
        mainMenuText.setFont(new Font("Helvetica", Font.PLAIN, 30));
        mainMenuText.setText("Welcome! Please select game mode");
        mainMenuText.setBounds(150, 300, 500, 100);

        option1 = new JButton(); // "Type race" option
        option1.setBounds(150, 450, 150, 100);
        option1.setText("Typing race");
        option1.setFocusable(false);
        option1.addActionListener(this);

        option2 = new JButton(); // "Falling words" option
        option2.setBounds(450, 450, 200, 100);
        option2.setText("Falling words survival");
        option2.setFocusable(false);
        option2.addActionListener(this);

        backToMainMenu = new JButton(); // To navigate back to the main menu at any time.
        backToMainMenu.setBounds(200, 830, 400, 100);
        backToMainMenu.setText("Back to main menu");
        backToMainMenu.setFocusable(false);
        backToMainMenu.addActionListener(this);

        mainMenu.add(mainMenuText);
        mainMenu.add(option1);
        mainMenu.add(option2);
        contentFrame.add(mainMenu);
        contentFrame.setVisible(true);
    }

    // Handler for any button presses that may happen.
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == option1) { // if the type race button is clicked
            contentFrame.setVisible(false);
            mainMenu.remove(mainMenuText);
            mainMenu.remove(option1);
            mainMenu.remove(option2);
            contentFrame.remove(mainMenu); // remove the main menu

            gameMode = 1;
            racePanel = new TypeRacePanel();
            racePanel.add(backToMainMenu);
            contentFrame.add(racePanel);
            contentFrame.setVisible(true); // start the race
        } else if (e.getSource() == option2) { // if the falling words button is clicked.
            contentFrame.setVisible(false);
            mainMenu.remove(mainMenuText);
            mainMenu.remove(option1);
            mainMenu.remove(option2);
            contentFrame.remove(mainMenu); // remove the main menu

            gameMode = 2;
            fallingPanel = new FallingWordsPanel();
            fallingPanel.add(backToMainMenu);
            contentFrame.add(fallingPanel);
            contentFrame.setVisible(true); // start the falling words game
        } else if (e.getSource() == backToMainMenu) { // if the "back to main menu" button is clicked
            contentFrame.setVisible(false);
            if (gameMode == 1) {
                racePanel.remove(backToMainMenu);
                contentFrame.remove(racePanel);
            } else if (gameMode == 2) {
                fallingPanel.remove(backToMainMenu);
                contentFrame.remove(fallingPanel);
            } // remove the race panel or falling words panel (and the back to main menu button)
            mainMenu.add(mainMenuText);
            mainMenu.add(option1);
            mainMenu.add(option2);
            contentFrame.add(mainMenu);
            contentFrame.setVisible(true); // return to the main menu
        }
    }

    // handler for when a key is typed. Trigger the charPressed method in whichever panel is active.
    @Override
    public void keyTyped(KeyEvent e) {
        if (gameMode == 1) {
            racePanel.charPressed(e.getKeyChar());
        } else if (gameMode == 2) {
            fallingPanel.charPressed(e.getKeyChar());
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        return;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        return;
    }

    // Centres frame on desktop
    // MODIFIES: this
    // EFFECTS: location of frame is set so frame is centred on desktop
    private void centreOnScreen() {
        Dimension scrn = Toolkit.getDefaultToolkit().getScreenSize();
        contentFrame.setLocation((scrn.width - contentFrame.getWidth()) / 2,
                (scrn.height - contentFrame.getHeight()) / 2);
    }
}
