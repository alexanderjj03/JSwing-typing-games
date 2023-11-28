package BossFight;

import TypeGameModel.TypePassage;

import java.awt.*;
import java.util.Iterator;

import javax.swing.Timer;
import java.util.Arrays;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/*
 * Represents an instance of a boss fight game (specification provided in constructor).
 */
public class BossFightGame {
    private BossFightPanel panel;

    private static final Random rand = new Random();
    private Timer clock;
    private int ticksElapsed; // Used to synchronize boss' attacks

    private boolean running; // to ensure that charTyped executes to completion before it is called again
    private int correctChars; // Typing stats
    private int charsTyped;
    private int accuracy;

    private boolean introSkip; // See BossFightPanel, when this is marked true the dialogue phase gets skipped.

    private double livesLeft; // starts at 4
    private int mouseX;
    private int mouseY;
    private int playerX; // center of player's position
    private int playerY;
    private int playerWidth; // same as player height

    private int bossHP; // starts at 100
    private int bossX; // center of boss' position
    private int bossY;
    private int bossWidth; // same as boss height
    private int phase; // starts at 1 (beginning dialogue), is 2 from 100-50 hp, 3 from 49-25 hp, 4 from 24-0 hp.
    private int shieldStatus; // 0 if inactive, 1-2 if weak (breakable), 3 if strong (unbreakable).
    private int specialAttackVersion;
    private boolean specialAttackIncoming; // Changed by timer. True when a special attack will happen in 2 seconds.

    private String dialogue; // What that boss is saying (will appear as text on screen)
    private boolean dialogueActive; // true when there is dialogue on screen

    private boolean gameWon;
    private boolean gameLost;

    private boolean wordSelected; // true if a word on screen is in the process of being typed.
    private TypePassage curPassage; // the word that is being typed

    private List<TypePassage> wordPassages; // list of single-word typePassages on screen
    private List<String> allWords; // every word in wordlist.10000

    // Constructs an instance of a boss fight game. This fight occurs in a 2D arena within a JFrame. The fight has 3
    // total phases. Across all phases, the boss can attack the player by spewing type-able words around the screen.
    // The only way of damaging the boss is by typing the words it shoots out. Once they are fully typed, they will be
    // sent directly away from the player. If they hit the boss on their way out, it will take damage. Incompletely
    // typed words damage the player on contact. To avoid attacks, the player can reposition themself by clicking
    // on a point in the arena, and the player icon will move towards that point.
    // REQUIRES: None
    public BossFightGame() {
        wordPassages = new ArrayList<>(); // initiating all instance variables
        allWords = new ArrayList<>();
        running = false;
        ticksElapsed = 0;
        correctChars = 0;
        charsTyped = 0;
        accuracy = 100;
        livesLeft = 4;

        mouseX = 400;
        mouseY = 750;
        playerX = 400; // player and boss info.
        playerY = 750;
        playerWidth = 60;
        bossHP = 100;
        bossX = 400;
        bossY = 140;
        bossWidth = 120;
        phase = 1;
        shieldStatus = 0;
        specialAttackIncoming = false;
        dialogue = "";
        dialogueActive = false;
        gameWon = false;
        gameLost = false;
        wordSelected = false;

        try {
            BufferedReader bf = new BufferedReader(new FileReader("wordlist.10000.txt"));
            String line = bf.readLine();
            while (line != null) {
                allWords.add(line);
                line = bf.readLine();
            }
            bf.close();
        } catch (Exception e) {
            System.out.println("Error");
        } // reading the list of 10,000 words and storing all of them onto allWords
        addTimer();
    }

    // Getters and setters
    public void setBossFightPanel(BossFightPanel bossPanel) {
        panel = bossPanel;
    }

    public int getTicks() {
        return ticksElapsed;
    }

    public int getAccuracy() {
        return accuracy;
    }

    public void skipIntro() {
        introSkip = true;
    }

    public double getLivesLeft() {
        return livesLeft;
    }

    public int getPlayerX() {
        return playerX;
    }

    public int getPlayerY() {
        return playerY;
    }

    public int getBossX() {
        return bossX;
    }

    public int getBossY() {
        return bossY;
    }

    public void setMouseX(int num) {
        mouseX = num;
    }

    public void setMouseY(int num) {
        mouseY = num;
    }

    public int getPlayerWidth() {
        return playerWidth;
    }

    public int getBossWidth() {
        return bossWidth;
    }

    public int getShieldStatus() {
        return shieldStatus;
    }

    public int getBossHP() {
        return bossHP;
    }

    public int getPhase() {
        return phase;
    }

    public int getSpecialAttackVersion() {
        return specialAttackVersion;
    }

    public boolean isIncoming() {
        return specialAttackIncoming;
    }

    public String getDialogue() {
        return dialogue;
    }

    public boolean isDialogueActive() {
        return dialogueActive;
    }

    public boolean isGameWon() {
        return gameWon;
    }

    public boolean isGameLost() {
        return gameLost;
    }

    public List<TypePassage> getWordPassages() {
        return wordPassages;
    }


    // Boss' standard attack. Can be called at any phase. Spawns 4-5 TypePassages in randomized locations.
    public void standardAttack() {
        addNewWord(150, 200 + rand.nextInt(200),
                rand.nextInt(3) + 1, rand.nextInt(3) + 3);
        addNewWord(275, 200 + rand.nextInt(200),
                rand.nextInt(3), rand.nextInt(3) + 3);
        addNewWord(525, 200 + rand.nextInt(200),
                rand.nextInt(3) - 2, rand.nextInt(3) + 3);
        addNewWord(630, 200 + rand.nextInt(200),
                rand.nextInt(3) - 3, rand.nextInt(3) + 3);
        if (phase == 4) {
            addNewWord(400, 200 + rand.nextInt(200),
                    rand.nextInt(3) - 1,rand.nextInt(3) + 3);
        }
    }

    // Only called in phase >= 3. Takes 2 off the player's health if the player is within certain sections of the
    // screen. This attack has a few variations that affect which sections the player will get damaged in.
    public void specialAttack() {
        boolean playerHit = false;
        if (specialAttackVersion == 0) {
            // damages player if the player has an x position between 225 and 575
            playerHit = (playerX + playerWidth/2 > 225 && playerX - playerWidth/2 < 575);
        } else if (specialAttackVersion == 1) {
            // damages player if the player has an x position between 100 and 325 or between 475 and 700.
            playerHit = ((playerX + playerWidth/2 > 100 && playerX - playerWidth/2 < 325)
                    || (playerX + playerWidth/2 > 475 && playerX - playerWidth/2 < 700));
        } else {
            // damages player if the player has a y position > 600
            playerHit = (playerY + playerWidth/2 > 550);
        }

        if (playerHit) {
            livesLeft -= 2;
            setDialogue(3);
        } else {
            setDialogue(0);
        }
        if (livesLeft <= 0) { // Game's over if lives hit zero
            gameLost = true;
            panel.setLoseScreen();
            clock.stop();
        }
    }

    // Called when the boss attacks (or possibly other reasons). Adds a random word from allWords whose
    // first letter is unique (no 2 words on screen will have the same first letter). There can be a maximum of
    // 10 words on screen. Adds the word so it has center at x, y with velocity vx, vy
    public void addNewWord(int x, int y, int vx, int vy) {
        if (wordPassages.size() > 8) { // More than 8 words on screen at a time makes the fight borderline impossible.
            return;
        } else {
            String chosenWord = " "; // placeholder
            boolean wordFound = false; // true when a suitable radom word has been found.
            while (!wordFound) { // continually find random words until there's one with a unique first letter.
                boolean dupeFirstLetter = false;
                chosenWord = allWords.get(rand.nextInt(allWords.size()));

                for (TypePassage t : wordPassages) {
                    if (t.getPassageWords().substring(0, 1).equalsIgnoreCase(chosenWord.substring(0, 1))) {
                        dupeFirstLetter = true;
                    }
                }
                if (!dupeFirstLetter && chosenWord.length() > 5 && chosenWord.length() < 13) {
                    // Only words that are 6-12 characters long can be selected
                    wordFound = true;
                }
            }
            TypePassage passage = new TypePassage(chosenWord, x, y, vx, vy);
            passage.setXPos(x - passage.getWidth()/2);
            passage.setYPos(y - passage.getHeight()/2);
            wordPassages.add(passage);
        }
    }

    // Sets up a timer that is initialized when the game starts. Used to control the progress of the fight, and
    // synchronize checks for collisions. Also used to trigger attacks at random intervals.
    // MODIFIES: none
    // EFFECTS: initializes a timer that ticks every 25 milliseconds
    private void addTimer() {
        // Implement shield (not now)
        clock = new Timer(25, new ActionListener() {
            int normalAttackCounter = 0;
            int specialAttackCounter = 0;
            int dialogueCounter = 0;
            @Override
            public void actionPerformed(ActionEvent ae) {
                if (phase >= 1) {
                    ticksElapsed++; // boss fight elapsed time
                    normalAttackCounter++; // used in phase 1 to time dialogue switches.
                }

                if (phase >= 3) {
                    specialAttackCounter++;
                }

                if (phase == 1) { // Introduction/dialogue phase (skippable via a button in BossFightPanel)
                    if (normalAttackCounter >= 680 || introSkip == true) {
                        phase = 2;
                        panel.setMainFight();
                        normalAttackCounter = 0;
                        shieldStatus = 2;
                    }
                }

                if (phase >= 2) {
                    updatePositions();
                    if (phase == 2 && bossHP <= 70) { // Go to phase 3 when bossHP is 70
                        phase = 3;
                        normalAttackCounter = 0;
                    }
                    if (phase == 3 && bossHP <= 25) { // Go to phase 4 when bossHP is 25
                        phase = 4;
                        normalAttackCounter = 0;
                        shieldStatus = 2;
                    }
                    if (bossHP < 1) {
                        gameWon = true;
                        panel.setWinScreen();
                        clock.stop();
                    }

                    if (!specialAttackIncoming) {
                        // Even if setDialogue is called from another method, this condition block ensures that
                        // dialogue lines don't stay on screen for too long.
                        dialogueCounter++;
                        if (dialogueActive && dialogueCounter >= 180) {
                            // reset dialogue back to default after 4.5 secs of having a dialogue line active
                            setDialogue(0);
                            dialogueCounter = 0;
                        } else if (!dialogueActive && dialogueCounter >= 400){
                            setDialogue(1);
                            dialogueCounter = 0;
                        }
                    }

                    if (normalAttackCounter > 160 && !specialAttackIncoming) {
                        // 1 in 80 chance to trigger a normal attack per tick beyond 4 seconds.
                        // NormalAttackCounter Resets on attack
                        if (rand.nextInt(80) == 23) {
                            standardAttack();
                            normalAttackCounter = 0;
                        }
                    }

                    if ((specialAttackCounter > 600 && !specialAttackIncoming) && !dialogueActive) {
                        // 1 in 200 chance to trigger a special attack per tick beyond 15 seconds. Once it's triggered,
                        // there is a 2-second delay until the attack actually happens (see next if statement)
                        if (rand.nextInt(200) == 23) {
                            specialAttackIncoming = true;
                            specialAttackVersion = rand.nextInt(3);
                            shieldStatus = 3;
                            setDialogue(2);
                            specialAttackCounter = 0;
                        }
                    }

                    if (specialAttackIncoming) {
                        if (specialAttackCounter > 80) {
                            specialAttack();
                            shieldStatus = 2;
                            specialAttackIncoming = false;
                            specialAttackCounter = 0;
                            normalAttackCounter = 0;
                        }
                    }
                }
                panel.repaint(); // repaints the corresponding panel with the new updated information
            }
        });
        clock.start();
    }

    public void setDialogue(int preset) { // Have this appear at the TOP of the screen (by boss)
        // (I intend to expand the boss' dialogue options, but this is not my priority at the moment)
        dialogueActive = true;
        switch (preset) {
            case 0:
                dialogue = "";
                dialogueActive = false;
                break;
            case 1:
                dialogue = "*yawn* You slow humans bore me";
                break;
            case 2:
                dialogue = "Better watch out, Better not cry! ;)";
                break;
            case 3:
                dialogue = "Wow, can't believe you got hit by that! Dummy";
                break;
        }
    }

    // Called on every tick. Updates the position of the player and all words on screen.
    // Calls checkCollisions for each typePassage
    public void updatePositions() {
        // Move player towards the position of the last mouse click
        double playerMouseDist = Math.sqrt(Math.pow(mouseX - playerX, 2.0) +
                Math.pow(mouseY - playerY, 2.0)); // distance between player and mouse
        if (playerMouseDist > 10) {
            int playerXVel = (int) ((mouseX - playerX)*8.0/playerMouseDist);
            int playerYVel = (int) ((mouseY - playerY)*8.0/playerMouseDist);
            checkPlayerCollisions(playerXVel, playerYVel); // This updates the player's position in the process
        }

        for (TypePassage t : wordPassages) {
            t.setXPos(t.getXPos() + t.getXVel()); // player movement has not been implemented yet
            t.setYPos(t.getYPos() + t.getYVel());
        }
        checkPassageCollisions();
    }

    // Checks for collisons between the player and the boss and/or walls (collisions with TypePassages are covered
    // in checkPassageCollisions). Updates the player's position as appropriate
    public void checkPlayerCollisions(int xVel, int yVel) {
        int newXPos = playerX + xVel;
        int newYPos = playerY + yVel;

        // Wall collisions
        if (newXPos - playerWidth/2 < 5) {
            newXPos = playerWidth/2 + 5;
        } else if (newXPos + playerWidth/2 > 780) {
            newXPos = 780 - playerWidth/2;
        }
        if (newYPos - playerWidth/2 < 50) {
            newYPos = playerWidth/2 + 50;
        } else if (newYPos + playerWidth/2 > 850) {
            newYPos = 850 - playerWidth/2;
        }

        // Collision with boss
        if ((newXPos + playerWidth/2 > bossX - bossWidth/2 - 10
                && newXPos - playerWidth/2 < bossX + bossWidth/2 + 10)
                && (newYPos + playerWidth/2 > bossY - bossWidth/2 - 10
                && newYPos - playerWidth/2 < bossY + bossWidth/2 + 10)) {
            int shortestDist = findMin(new int[]
                    {playerX + playerWidth / 2 - (bossX - bossWidth / 2), // shortestDist=0
                    bossX + bossWidth/2 - (playerX - playerWidth / 2), // 1
                    playerY + playerWidth / 2 - (bossY - bossWidth / 2), // 2
                    bossY + bossWidth/2 - (playerY - playerWidth / 2)});  // 3
            // used to determine which side of the boss the player collided with.
            switch (shortestDist) {
                case 0:
                    newXPos = bossX - bossWidth / 2 - playerWidth / 2 - 10;
                    break;
                case 1:
                    newXPos = bossX + bossWidth/2 + playerWidth / 2 + 10;
                    break;
                case 2:
                    newYPos = bossY - bossWidth / 2 - playerWidth / 2 - 10;
                    break;
                case 3:
                    newYPos = bossY + bossWidth/2 + playerWidth / 2 + 10;
                    break;
            }
        }

        playerX = newXPos;
        playerY = newYPos;
    }

    // Returns the index of the smallest integer in an array. Helper function for checkPlayerCollisions
    public int findMin(int[] nums) {
        int min = 0;
        for (int i=1; i < nums.length; i++) {
            if (nums[i] < nums[min]) {
                min = i;
            }
        }
        return min;
    }

    // Checks for collisions between a TypePassage on screen and the player, boss (and walls in phase >= 3).
    // TypePassages bounce off the bottom wall regardless of phase.
    public void checkPassageCollisions() {
        Iterator<TypePassage> itr = wordPassages.iterator();
        while (itr.hasNext()) {
            // Objects will be removed as typePassages gets iterated through, hence the need for an iterator.
            TypePassage current = itr.next();
            if (current.getYPos() < 50 || current.getYPos() + current.getHeight() > 850) {
                // Collision with top or bottom wall
                if (phase >= 3 || current.getYPos() + current.getHeight() > 850) {
                    current.setYVel(current.getYVel() * -1); // flip the sign of y velocity
                } else {
                    if (wordSelected && (current == curPassage)) { // Remove the word from the screen
                        wordSelected = false;
                        curPassage = null;
                    }
                    itr.remove();
                    continue;
                }
            }

            if (current.getXPos() < 5 || current.getXPos() + current.getWidth() > 780) {
                // if there's a collision with the side walls
                if (phase >= 3) {
                    current.setXVel(current.getXVel() * -1); // flip the sign of x velocity
                } else {
                    if (wordSelected && (current == curPassage)) {
                        wordSelected = false;
                        curPassage = null;
                    }
                    itr.remove();
                    continue;
                }
            }
            if (current.checkComplete()) {
                // completed TypePassages are the only ones capable of damaging the boss. They can bounce on side walls
                // but not top or bottom walls.
                if ((current.getXPos() + current.getWidth() > bossX - bossWidth/2
                        && current.getXPos() < bossX + bossWidth/2)
                        && (current.getYPos() > bossY - bossWidth/2
                        && current.getYPos() - current.getHeight() < bossY + bossWidth/2)) {
                    // if there's a collision with boss
                    switch (shieldStatus) {
                        // boss can only be damaged when shield is inactive. An active weak shield can take 2 hits
                        case 0:
                            bossHP -= 5;
                            if (livesLeft + 0.2 < 4) {
                                livesLeft += 0.2;
                            } else {
                                livesLeft = 4;
                            }
                            break;
                        case 1:
                            shieldStatus = 0;
                            break;
                        case 2:
                            shieldStatus = 1;
                            break;
                    }
                    itr.remove();
                    continue;
                }
            } else { // Incomplete TypePassages damage the player
                if ((current.getXPos() + current.getWidth() > bossX - bossWidth/2 // If there's a collision with boss
                        && current.getXPos() < bossX + bossWidth/2)
                        && (current.getYPos() > bossY - bossWidth/2
                        && current.getYPos() - current.getHeight() < bossY + bossWidth/2)) {

                    current.setXVel(current.getXVel() * -1); // reverse the direction the word's travelling
                    current.setYVel(current.getYVel() * -1);
                }

                if ((current.getXPos() + current.getWidth() > playerX - playerWidth/2
                        && current.getXPos() < playerX + playerWidth/2)
                        && (current.getYPos() > playerY - playerWidth/2
                        && current.getYPos() - current.getHeight() < playerY + playerWidth/2)) {
                        // If there's a collision with player

                    if (wordSelected && (current == curPassage)) {
                        wordSelected = false;
                        curPassage = null;
                    }
                    itr.remove();
                    livesLeft--; // deduct one life
                    if (livesLeft <= 0) { // Game's over if lives hit zero
                        gameLost = true;
                        panel.setLoseScreen();
                        clock.stop();
                    }
                }
            }
        }
    }

    // Calculates the current typing game accuracy (correct chars/chars typed)
    public void calcAccuracy() {
        if (charsTyped == 0) {
            accuracy = 100;
        } else {
            accuracy = (int) Math.round((100.0 * correctChars) / charsTyped);
        }
    }

    // Handler for a key press.
    public void charTyped(char c) {
        if (wordPassages.size() == 0) { // this function has no purpose if wordpassages has 0 elements.
            running = false;
            return;
        }

        while (running) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {}
        } // to prevent synchrony issues.
        running = true;

        if (wordSelected) {
            // If a word is currently being typed, the user is only able to continue typing that word.
            if (c == curPassage.getCharList().get(curPassage.getCursorPos()).getChar()) {
                curPassage.correctInput();
                correctChars++;
            } else {
                curPassage.incorrectInput();
            }

            // If curpassage is done being typed, make it move in the vector direction from the player to the word.
            if (curPassage.checkComplete()) {
                double wordCenterX = curPassage.getXPos() + curPassage.getWidth()/2.0;
                double wordCenterY = curPassage.getYPos() + curPassage.getHeight()/2.0;
                double playerWordDist = Math.sqrt(Math.pow(wordCenterX - playerX, 2.0) +
                        Math.pow(wordCenterY - playerY, 2.0)); // distance between player and word

                curPassage.setXVel((int) ((wordCenterX - playerX)*15.0/playerWordDist)); // vector between player & word
                curPassage.setYVel((int) ((wordCenterY - playerY)*15.0/playerWordDist));

                wordSelected = false;
                curPassage = null;
            }
        } else { // If no word is currently being typed, the user can start typing any of the words on screen
            for (int i=0; i<wordPassages.size(); i++) {
                if (c == wordPassages.get(i).getPassageWords().charAt(0)) {
                    curPassage = wordPassages.get(i);
                    wordSelected = true;
                    curPassage.correctInput();
                    correctChars++;
                }
            }
        }
        charsTyped++;
        calcAccuracy();
        running = false;
    }
}
